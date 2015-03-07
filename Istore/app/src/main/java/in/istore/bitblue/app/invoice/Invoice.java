package in.istore.bitblue.app.invoice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.InvoiceAdapter;
import in.istore.bitblue.app.cloudprint.PrintDialogActivity;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;


public class Invoice extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvinvoicenumber, tvtodayDate, tvstoreid, tvstaffid, tvcustmobile, tvdeliveryAddress, tvTotalBillPay;
    private ListView lvProductList;
    private LinearLayout lldeliverAddress;
    private long invoiceNumber, Mobile;
    private int StoreId, prodQuantity;
    private float prodSellPrice, prodTotalPrice, totalPayAmount;
    private Date date;
    private String todayDate, StoreName, UserType, Key, prodId, prodName, PersonId, DeliverAddress;
    private SharedPreferences prefCustMobile;
    public static String CUST_MOBILE = "custmobile";
    private ArrayList<CartItem> invoiceArrayList = new ArrayList<>();
    private GlobalVariables globalVariable;

    private TinyDB tinyDB;
    private InvoiceAdapter invoiceAdapter;
    private DbCartAdapter dbCartAdapter;
    private DbLoginCredAdapter dbLoginCredAdapter;
    private BaseFont bfBold;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private CartItem cartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("");
    }

    private void initViews() {
        prefCustMobile = getSharedPreferences(CUST_MOBILE, MODE_PRIVATE);
        globalVariable = (GlobalVariables) getApplicationContext();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
            PersonId = "Owner";

        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
            PersonId = String.valueOf(globalVariable.getStaffId());
        }
        StoreId = globalVariable.getStoreId();
        DeliverAddress = globalVariable.getDeliveryAddress();
        dbCartAdapter = new DbCartAdapter(this);
        dbLoginCredAdapter = new DbLoginCredAdapter(this);
        tvinvoicenumber = (TextView) findViewById(R.id.tv_invoice_number);
        tvtodayDate = (TextView) findViewById(R.id.tv_invoice_todayDate);
        tvstoreid = (TextView) findViewById(R.id.tv_invoice_storeid);
        tvstaffid = (TextView) findViewById(R.id.tv_invoice_staffid);
        tvcustmobile = (TextView) findViewById(R.id.tv_invoice_custMobile);
        lvProductList = (ListView) findViewById(R.id.lv_invoice_products);
        tvTotalBillPay = (TextView) findViewById(R.id.tv_invoicefooter_totalbill);
        tvdeliveryAddress = (TextView) findViewById(R.id.tv_invoice_deliveryAddress);
        lldeliverAddress = (LinearLayout) findViewById(R.id.ll_invoice_deliverAddress);

        findViewById(R.id.ll_invoice).setVisibility(View.GONE);
        findViewById(R.id.ll_invoice_one).setVisibility(View.GONE);
        findViewById(R.id.footer_layout).setVisibility(View.GONE);
        StoreId = globalVariable.getStoreId();
        tinyDB = new TinyDB(this);
        StoreName = tinyDB.getString("StoreName");
        invoiceNumber = getIntent().getLongExtra("InVoiceNumber", 0);
        Mobile = getIntent().getLongExtra("Mobile", 0);

        date = new Date();
        todayDate = DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(DateUtil.convertToStringDateOnly(date));
        tvinvoicenumber.setText(String.valueOf(invoiceNumber));

        tvtodayDate.setText(todayDate);
        tvstoreid.setText(String.valueOf(StoreId));
        tvstaffid.setText(String.valueOf(PersonId));
        tvcustmobile.setText(String.valueOf(Mobile));
        if (DeliverAddress != null && !(DeliverAddress.equals("")))
            tvdeliveryAddress.setText(DeliverAddress);
        else lldeliverAddress.setVisibility(View.GONE);
        getInVoiceItems();
       /* invoiceArrayList = dbCartAdapter.getAllCartItems();
        if (invoiceArrayList != null) {
            invoiceAdapter = new InvoiceAdapter(this, invoiceArrayList);
            lvProductList.setAdapter(invoiceAdapter);
            tvTotalBillPay.setText(String.valueOf(dbCartAdapter.getTotalPayAmount()));
            //dbCartAdapter.emptyCart();
            // dbCartAdapter.clearAllPurchases();
        } else {
            startActivity(new Intent(this, Cart.class));
        }*/

    }

    private void getInVoiceItems() {
        new AsyncTask<String, String, String>() {

            ProgressDialog dialog = new ProgressDialog(Invoice.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_CART_ITEMS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getApplicationContext(), "No Items in Cart", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            prodId = jsonObject.getString("Cid");
                            prodName = jsonObject.getString("Name");
                            prodQuantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            prodSellPrice = Float.parseFloat(jsonObject.getString("Sellingprice"));
                            prodTotalPrice = Float.parseFloat(jsonObject.getString("Totalprice"));
                            if (prodId == null || prodId.equals("null")) {
                                break;
                            }
                            cartItem = new CartItem();
                            cartItem.setItemId(prodId);
                            cartItem.setItemName(prodName);
                            cartItem.setItemSoldQuantity(prodQuantity);
                            cartItem.setItemSellPrice(prodSellPrice);
                            cartItem.setItemTotalAmnt(prodTotalPrice);
                            totalPayAmount += cartItem.getItemTotalAmnt();
                            invoiceArrayList.add(cartItem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (invoiceArrayList != null && invoiceArrayList.size() > 0) {
                        findViewById(R.id.ll_invoice).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll_invoice_one).setVisibility(View.VISIBLE);
                        findViewById(R.id.footer_layout).setVisibility(View.VISIBLE);
                        tvTotalBillPay.setText(String.valueOf(totalPayAmount));
                        invoiceAdapter = new InvoiceAdapter(getApplicationContext(), invoiceArrayList);
                        lvProductList.setAdapter(invoiceAdapter);
                    } else
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invoice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_print:
                if (invoiceArrayList == null || invoiceArrayList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Nothing to print", Toast.LENGTH_SHORT).show();
                } else {
                    clearAllPurchases();
                    showCloudPrintDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllPurchases() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Invoice.this);
            String Response
                    ,
                    Status;

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                for (CartItem cartItem : invoiceArrayList) {
                    nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("key", Key));
                    nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                    nameValuePairs.add(new BasicNameValuePair("CartId", cartItem.getItemId()));
                    Response = jsonParser.makeHttpPostRequest(API.BITSTORE_CLEAR_CART_ITEMS, nameValuePairs);
                }
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("Status");
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void printInVoice() {
        Document invoice = new Document();
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "/Istore");
            if (dir.mkdir()) {
                Log.e("App", "created directory, Istore");
            } else {
                Log.e("App", "Already created directory, Istore");
            }
            File invoiceInPdfFormat = new File(dir, "InVoice.pdf");
            invoiceInPdfFormat.createNewFile();
            PdfWriter invoicewriter = PdfWriter.getInstance(invoice, new FileOutputStream(invoiceInPdfFormat));
            invoice.open();
            PdfContentByte cb = invoicewriter.getDirectContent();
            initializeFonts();

            //creating a sample invoice with some customer data
            createHeadings(cb, 250, 800, "INVOICE", 20);
            createHeadings(cb, 100, 780, "InVoice Number: " + invoiceNumber, 10);
            createHeadings(cb, 400, 780, "Store Id: " + StoreId, 10);
            createHeadings(cb, 100, 760, "Date: " + todayDate, 10);
            // createHeadings(cb, 400, 760, "Store Name: " + StoreName, 10);
            createHeadings(cb, 100, 740, "Customer Number: " + Mobile, 10);
            if (DeliverAddress != null && !(DeliverAddress.equals("")))
                createHeadings(cb, 100, 720, "Delivery Address: " + DeliverAddress, 10);
            createHeadings(cb, 400, 740, "Staff Id: " + PersonId, 10);
            //list all the products sold to the customer
            float[] columnWidths = {1.5f, 3f, 3f, 2f, 2f, 2f};
            //create PDF table with the given widths
            PdfPTable table = new PdfPTable(columnWidths);
            // set table width a percentage of the page width
            table.setTotalWidth(500f);

            PdfPCell cell = new PdfPCell(new Phrase("No."));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Id"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Item Name"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Quantity"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Total Price"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);            //the number of the top rows that constitute the header

            int i = 1;
            for (CartItem cartItem : invoiceArrayList) {
                table.addCell(String.valueOf(i));
                table.addCell(cartItem.getItemId());
                table.addCell(cartItem.getItemName());
                table.addCell(String.valueOf(cartItem.getItemSoldQuantity()));
                table.addCell(" Rs. " + String.valueOf(cartItem.getItemSellPrice()));
                table.addCell(" Rs. " + String.valueOf(cartItem.getItemTotalAmnt()));
                i++;
            }

            //absolute location to print the PDF table from
            table.writeSelectedRows(0, -1, invoice.leftMargin(), 700, invoicewriter.getDirectContent());
            // createHeadings(cb, 200, 710, "Total Pay Amount: Rs." + totalPayAmount, 12);
            createHeadings(cb, 400, 400, "Total Pay Amount: " + "Rs. " + totalPayAmount, 10);
            invoice.close();

            Intent printIntent = new Intent(Invoice.this, PrintDialogActivity.class);
            printIntent.setDataAndType(Uri.fromFile(invoiceInPdfFormat), "application/pdf");
            printIntent.putExtra("title", "InVoice");
            startActivity(printIntent);
        } catch (Exception e) {
        }
    }

    private void initializeFonts() {
        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text, float fontSize) {

        cb.beginText();
        cb.setFontAndSize(bfBold, fontSize);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomePage.class));
    }

    private void showCloudPrintDialog() {
        // Linkify the message
        final String cloudPrint = "https://www.google.com/cloudprint/learn/howitworks.html";
        final SpannableString cloudprintLink = new SpannableString(cloudPrint);
        Linkify.addLinks(cloudprintLink, Linkify.ALL);
        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent opencloudPrintTutorial = new Intent(Intent.ACTION_VIEW);
                        opencloudPrintTutorial.setData(Uri.parse(cloudPrint));
                        startActivity(opencloudPrintTutorial);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        printInVoice();
                    }
                })
                .setIcon(R.drawable.ic_drawer)
                .setMessage("Google Cloud Service must be enabled to print invoice.\n" +
                        "For setting up Google Cloud Print go to:\n" + cloudprintLink)
                .setCancelable(false)
                .create();
        d.show();

        // Make the textview clickable. Must be called after show()
        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
