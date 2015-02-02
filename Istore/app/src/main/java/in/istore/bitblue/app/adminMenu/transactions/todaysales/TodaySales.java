package in.istore.bitblue.app.adminMenu.transactions.todaysales;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TodaySaleAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TodaysSale;

public class TodaySales extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvtodaysales;

    private float TodaySales;

    private ArrayList<TodaysSale> todaysSaleArrayList;
    private TodaySaleAdapter todaySaleAdapter;
    private DbCustPurHistAdapter custPurHistAdapter;
    private final static String TRANSACTION = "transaction";
    private SharedPreferences preftransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_sales);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        preftransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE);
        TodaySales = preftransaction.getFloat("TodaySales", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Today's Total Sales: Rs " + TodaySales);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        custPurHistAdapter = new DbCustPurHistAdapter(this);
        lvtodaysales = (ListView) findViewById(R.id.lv_todaysales_list);
        todaysSaleArrayList = custPurHistAdapter.getTodaysSale();
        if (todaysSaleArrayList != null) {
            todaySaleAdapter = new TodaySaleAdapter(this, todaysSaleArrayList);
            lvtodaysales.setAdapter(todaySaleAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
