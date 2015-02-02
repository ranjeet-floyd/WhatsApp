package in.istore.bitblue.app.adminMenu.transactions.outofstock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.OutOfStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.pojo.Outofstock;

public class OutOfStock extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvOutOfStock;

    private int outofStock;

    private ArrayList<Outofstock> outofstockArrayList;
    private DbOutOfStockAdapter dbOutOfStockAdapter;
    private OutOfStockAdapter outOfStockAdapter;
    private final static String TRANSACTION = "transaction";
    private SharedPreferences preftransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_stock);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        preftransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE);
        outofStock = preftransaction.getInt("OutOfStockItem", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Out Of Stock Items: " + outofStock);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        lvOutOfStock = (ListView) findViewById(R.id.lv_outofstock_list);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        outofstockArrayList = dbOutOfStockAdapter.getAllOutOfStockItems();
        if (outofstockArrayList != null) {
            outOfStockAdapter = new OutOfStockAdapter(this, outofstockArrayList);
            lvOutOfStock.setAdapter(outOfStockAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
