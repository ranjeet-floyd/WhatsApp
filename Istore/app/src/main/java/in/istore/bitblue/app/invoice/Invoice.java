package in.istore.bitblue.app.invoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.InvoiceAdapter;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.Store;


public class Invoice extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvinvoicenumber, tvtodayDate, tvstoreid, tvstaffid, tvcustmobile, tvTotalBillPay;
    private ListView lvProductList;
    private long invoiceNumber, StaffId, Mobile;
    private int StoreId;
    private Date date;
    private String todayDate;

    private ArrayList<CartItem> invoiceArrayList;
    private GlobalVariables globalVariable;
    private InvoiceAdapter invoiceAdapter;
    private DbCartAdapter dbCartAdapter;

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
        globalVariable = (GlobalVariables) getApplicationContext();
        dbCartAdapter = new DbCartAdapter(this);
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
        Mobile = getIntent().getLongExtra("Mobile", 0);
        date = new Date();
        todayDate = DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(DateUtil.convertToStringDateOnly(date));
        tvinvoicenumber.setText(String.valueOf(invoiceNumber));
        tvtodayDate.setText(todayDate);
        tvstoreid.setText(String.valueOf(StoreId));
        if (StaffId > 0)
            tvstaffid.setText(String.valueOf(StaffId));
        else
            tvstaffid.setText("Shop Owner");
        tvcustmobile.setText(String.valueOf(Mobile));

        invoiceArrayList = dbCartAdapter.getAllCartItems();
        if (invoiceArrayList != null) {
            invoiceAdapter = new InvoiceAdapter(this, invoiceArrayList);
            lvProductList.setAdapter(invoiceAdapter);
            tvTotalBillPay.setText(String.valueOf(dbCartAdapter.getTotalPayAmount()));
            dbCartAdapter.emptyCart();
            dbCartAdapter.clearAllPurchases();
        } else {
            startActivity(new Intent(this, Cart.class));
        }

    }

}
