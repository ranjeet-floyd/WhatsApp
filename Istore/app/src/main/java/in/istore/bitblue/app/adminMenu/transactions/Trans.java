package in.istore.bitblue.app.adminMenu.transactions;

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
import in.istore.bitblue.app.adminMenu.transactions.todaysales.TodaySales;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.TotalRevSelectRange;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbTotSaleAmtByDateAdapter;

public class Trans extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvTotalSales, tvTodaySales, tvOutOfStock;
    private TextView bTotalSales, bTodaySales, bOutOfStock;

    private DbTotSaleAmtByDateAdapter dbTotSaleAmtByDateAdapter;
    private DbOutOfStockAdapter dbOutOfStockAdapter;

    private float TotalRevenue, TodaySales;
    private int OutofStock;

    private final static String TRANSACTION = "transaction";
    private SharedPreferences.Editor preftransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
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
        preftransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE).edit();

        dbTotSaleAmtByDateAdapter = new DbTotSaleAmtByDateAdapter(this);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        TotalRevenue = dbTotSaleAmtByDateAdapter.getTotalRevenue();
        TodaySales = dbTotSaleAmtByDateAdapter.getTodaySales();
        OutofStock = getOutOfStockItems();

        preftransaction.putFloat("TotalRevenue", TotalRevenue);
        preftransaction.putFloat("TodaySales", TodaySales);
        preftransaction.putInt("OutOfStockItem", OutofStock);
        preftransaction.commit();
        tvTotalSales = (TextView) findViewById(R.id.tv_transaction_totalrevenue);
        tvTotalSales.setText(String.valueOf(TotalRevenue));

        tvTodaySales = (TextView) findViewById(R.id.tv_transaction_todaysales);
        tvTodaySales.setText(String.valueOf(TodaySales));

        tvOutOfStock = (TextView) findViewById(R.id.tv_transaction_outofstockitems);
        tvOutOfStock.setText(String.valueOf(OutofStock));

        bTotalSales = (Button) findViewById(R.id.b_transaction_totalrevenue);
        bTotalSales.setOnClickListener(this);
        bTodaySales = (Button) findViewById(R.id.b_transaction_todaySales);
        bTodaySales.setOnClickListener(this);
        bOutOfStock = (Button) findViewById(R.id.b_transaction_outofstock);
        bOutOfStock.setOnClickListener(this);

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_transaction_totalrevenue:
                Intent totalRevenue = new Intent(this, TotalRevSelectRange.class);
                startActivity(totalRevenue);
                break;
            case R.id.b_transaction_todaySales:
                Intent todaySales = new Intent(this, TodaySales.class);
                startActivity(todaySales);
                break;
            case R.id.b_transaction_outofstock:
                Intent outOfStock = new Intent(this, OutOfStock.class);
                startActivity(outOfStock);
                break;

        }
    }

    public int getOutOfStockItems() {
        return dbOutOfStockAdapter.getOutOfStockItems();
    }
}
