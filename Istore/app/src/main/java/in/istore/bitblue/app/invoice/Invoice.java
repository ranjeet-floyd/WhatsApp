package in.istore.bitblue.app.invoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.InvoiceAdapter;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.cloudprint.CloudPrint;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.Store;


public class Invoice extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvinvoicenumber, tvtodayDate, tvstoreid, tvstaffid, tvcustmobile, tvTotalBillPay;
    private ListView lvProductList;
    private long invoiceNumber, StaffId, AdminId, Mobile;
    private int StoreId;
    private Date date;
    private String todayDate, StoreName;
    private SharedPreferences prefCustMobile;
    public static String CUST_MOBILE = "custmobile";
    private ArrayList<CartItem> invoiceArrayList;
    private GlobalVariables globalVariable;
    private InvoiceAdapter invoiceAdapter;
    private DbCartAdapter dbCartAdapter;
    private DbLoginCredAdapter dbLoginCredAdapter;
    private BaseFont bfBold;

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
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Invoice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        prefCustMobile = getSharedPreferences(CUST_MOBILE, MODE_PRIVATE);
        globalVariable = (GlobalVariables) getApplicationContext();
        dbCartAdapter = new DbCartAdapter(this);
        dbLoginCredAdapter = new DbLoginCredAdapter(this);
        tvinvoicenumber = (TextView) findViewById(R.id.tv_invoice_number);
        tvtodayDate = (TextView) findViewById(R.id.tv_invoice_todayDate);
        tvstoreid = (TextView) findViewById(R.id.tv_invoice_storeid);
        tvstaffid = (TextView) findViewById(R.id.tv_invoice_staffid);
        tvcustmobile = (TextView) findViewById(R.id.tv_invoice_custMobile);
        lvProductList = (ListView) findViewById(R.id.lv_invoice_products);
        tvTotalBillPay = (TextView) findViewById(R.id.tv_invoicefooter_totalbill);
        invoiceNumber = Store.generateInVoiceNumber();
        StaffId = globalVariable.getStaffId();
        StoreId = globalVariable.getStoreId();
        AdminId = globalVariable.getAdminId();
        StoreName = "Bit Store";
        Mobile = prefCustMobile.getLong("custMobile", 0);
        date = new Date();
        todayDate = DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(DateUtil.convertToStringDateOnly(date));
        tvinvoicenumber.setText(String.valueOf(invoiceNumber));
        tvtodayDate.setText(todayDate);
        tvstoreid.setText(String.valueOf(StoreId));
        if (StaffId > 0)
            tvstaffid.setText(String.valueOf(StaffId));
        else
            tvstaffid.setText(" Shop Owner ");
        tvcustmobile.setText(String.valueOf(Mobile));

        invoiceArrayList = dbCartAdapter.getAllCartItems();
        if (invoiceArrayList != null) {
            invoiceAdapter = new InvoiceAdapter(this, invoiceArrayList);
            lvProductList.setAdapter(invoiceAdapter);
            tvTotalBillPay.setText(String.valueOf(dbCartAdapter.getTotalPayAmount()));
            //dbCartAdapter.emptyCart();
            // dbCartAdapter.clearAllPurchases();
        } else {
            startActivity(new Intent(this, Cart.class));
        }

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
            createHeadings(cb, 400, 740, "Staff Id: " + StaffId, 10);

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
            createHeadings(cb, 200, 710, "Total Pay Amount: Rs."  + dbCartAdapter.getTotalPayAmount(), 12);
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

}
