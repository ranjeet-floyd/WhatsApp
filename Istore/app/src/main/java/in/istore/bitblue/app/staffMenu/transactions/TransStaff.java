package in.istore.bitblue.app.staffMenu.transactions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adminMenu.transactions.outofstock.OutOfStock;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.staffMenu.transactions.todaysales.TodaySalesStaff;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class TransStaff extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvTodaySalesStaff, tvOutOfStock;
    private Button bTodaySales, bOutofStock;

    private float TodaySales;
    private int OutOfStockItems;
    private long StaffId;
    private final static String TRANSACTION_STAFF = "transactionstaff";
    private SharedPreferences.Editor preftransactionstaff;
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private DbOutOfStockAdapter dbOutOfStockAdapter;
    private GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_staff);
        globalVariables = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Transaction Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        globalVariables = (GlobalVariables) getApplicationContext();
        StaffId = globalVariables.getStaffId();
        TodaySales = dbCustPurHistAdapter.getTodaySalesForStaffId(StaffId);  //Change this
        OutOfStockItems = dbOutOfStockAdapter.getOutOfStockItems();

        preftransactionstaff = getSharedPreferences(TRANSACTION_STAFF, MODE_PRIVATE).edit();
        preftransactionstaff.putFloat("TodaySales", TodaySales);
        preftransactionstaff.putInt("OutOfStockItem", OutOfStockItems);
        preftransactionstaff.commit();

        tvTodaySalesStaff = (TextView) findViewById(R.id.tv_transaction_todaysalesstaff);
        tvTodaySalesStaff.setText(String.valueOf(TodaySales));
        tvOutOfStock = (TextView) findViewById(R.id.tv_transaction_outofstockitemsstaff);
        tvOutOfStock.setText(String.valueOf(OutOfStockItems));

        bTodaySales = (Button) findViewById(R.id.b_transaction_todaySalesstaff);
        bOutofStock = (Button) findViewById(R.id.b_transaction_outofstockstaff);
        bTodaySales.setOnClickListener(this);
        bOutofStock.setOnClickListener(this);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_transaction_todaySalesstaff:
                Intent todaySalesStaff = new Intent(this, TodaySalesStaff.class);
                startActivity(todaySalesStaff);
                break;
            case R.id.b_transaction_outofstockstaff:
                Intent outOfStock = new Intent(this, OutOfStock.class);
                startActivity(outOfStock);
                break;

        }
    }
}
