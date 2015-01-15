package in.istore.bitblue.app.soldItems;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.adapters.SoldItemAdapter;
import in.istore.bitblue.app.listMyStock.Product;

public class SoldItems extends ActionBarActivity {
    private TextView tvnodata;
    private Toolbar toolbar;
    private DbProductAdapter dbAdapter;
    private ListView lvproductList;
    private SoldItemAdapter listAdapter;
    private ArrayList<Product> soldproductArrayList;
    private static final String STATUS = "sold";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SOLD ITEMS");
    }

    private void initViews() {
        dbAdapter = new DbProductAdapter(this);
        soldproductArrayList = dbAdapter.getAllSoldProducts(STATUS);

        tvnodata = (TextView) findViewById(R.id.tv_soldItem_nodata);

        if (soldproductArrayList == null || soldproductArrayList.size() == 0) {
            tvnodata.setVisibility(View.VISIBLE);
        } else {
            tvnodata.setVisibility(View.GONE);
            listAdapter = new SoldItemAdapter(this, soldproductArrayList);
            lvproductList = (ListView) findViewById(R.id.lv_soldItem_itemlist);
            lvproductList.setAdapter(listAdapter);
        }
    }
}
