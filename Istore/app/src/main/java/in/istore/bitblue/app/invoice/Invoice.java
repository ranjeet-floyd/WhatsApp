package in.istore.bitblue.app.invoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import in.istore.bitblue.app.cloudprint.CloudPrint;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.api.API;


public class Invoice extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvinvoicenumber, tvtodayDate, tvstoreid, tvstaffid, tvcustmobile, tvTotalBillPay;
    private ListView lvProductList;
    private long invoiceNumber, Mobile;
    private int StoreId, prodQuantity;
    private float prodSellPrice, prodTotalPrice, totalPayAmount;
    private Date date;
    private String todayDate, StoreName, UserType, Key, prodId, prodName, PersonId;
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
        toolTitle.setText("Invoice");
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

        dbCartAdapter = new DbCartAdapter(this);
        dbLoginCredAdapter = new DbLoginCredAdapter(this);
        tvinvoicenumber = (TextView) findViewById(R.id.tv_invoice_number);
        tvtodayDate = (TextView) findViewById(R.id.tv_invoice_todayDate);
        tvstoreid = (TextView) findViewById(R.id.tv_invoice_storeid);
        tvstaffid = (TextView) findViewById(R.id.tv_invoice_staffid);
        tvcustmobile = (TextView) findViewById(R.id.tv_invoice_custMobile);
        lvProductList = (ListView) findViewById(R.id.lv_invoice_products);
        tvTotalBillPay = (TextView) findViewById(R.id.tv_invoicefooter_totalbill);
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
                        tvTotalBillPay.setText(String.valueOf(totalPayAmount));
                        invoiceAdapter = new InvoiceAdapter(getApplicationContext(), invoiceArrayList);
                        lvProductList.setAdapter(invoiceAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Product Available", Toast.LENGTH_LONG).show();
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
                printInVoice();
                dbCartAdapter.emptyCart();
                dbCartAdapter.clearAllPurchases();
                break;
        }
        return super.onOptionsItemSelected(item);
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
            createHeadings(cb, 400, 760, "Store Name: " + StoreName, 10);
            createHeadings(cb, 100, 740, "Customer Number: " + Mobile, 10);
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
            cell = new PdfPCell(new Phrase("Price Rs."));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Total Price Rs."));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);            //the number of the top rows that constitute the header

            int i = 1;
            for (CartItem cartItem : invoiceArrayList) {
                table.addCell(String.valueOf(i));
                table.addCell(cartItem.getItemId());
                table.addCell(cartItem.getItemName());
                table.addCell(String.valueOf(cartItem.getItemSoldQuantity()));
                table.addCell(String.valueOf(cartItem.getItemSellPrice()));
                table.addCell(String.valueOf(cartItem.getItemTotalAmnt()));
                i++;
            }

            //absolute location to print the PDF table from
            table.writeSelectedRows(0, -1, invoice.leftMargin(), 700, invoicewriter.getDirectContent());
            createHeadings(cb, 200, 710, "Total Pay Amount: Rs." + totalPayAmount, 12);
            createHeadings(cb, 500, 200, "Signature", 10);
            invoice.close();

            startActivity(new Intent(this, CloudPrint.class));
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
    }
}
