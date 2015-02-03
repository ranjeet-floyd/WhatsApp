package in.istore.bitblue.app.staffMenu.transactions.todaysales;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TodaySaleStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TodaysSale;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class TodaySalesStaff extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvtodaysalesstaff;

    private float TodaySalesStaff;
    private long StaffId;
    private ArrayList<TodaysSale> todaysSaleArrayList;
    private TodaySaleStaffAdapter todaySaleAdapter;
    private DbCustPurHistAdapter custPurHistAdapter;
    private final static String TRANSACTION_STAFF = "transactionstaff";
    private SharedPreferences preftransactionstaff;
    private GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_sales_staff);
        globalVariables = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        preftransactionstaff = getSharedPreferences(TRANSACTION_STAFF, MODE_PRIVATE);
        TodaySalesStaff = preftransactionstaff.getFloat("TodaySales", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Today's Total Sales: Rs " + TodaySalesStaff);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        StaffId = globalVariables.getStaffId();
        custPurHistAdapter = new DbCustPurHistAdapter(this);
        lvtodaysalesstaff = (ListView) findViewById(R.id.lv_todaysalesstaff_list);
        todaysSaleArrayList = custPurHistAdapter.getTodaysSaleForStaff(StaffId);  //Change this
        if (todaysSaleArrayList != null) {
            todaySaleAdapter = new TodaySaleStaffAdapter(this, todaysSaleArrayList);
            lvtodaysalesstaff.setAdapter(todaySaleAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
