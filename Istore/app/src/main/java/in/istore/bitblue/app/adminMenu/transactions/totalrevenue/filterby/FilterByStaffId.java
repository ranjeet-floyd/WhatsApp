package in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.FilterByStaffIdAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.DateUtil;

public class FilterByStaffId extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvstaffidTotRev;
    private AutoCompleteTextView actvStaffId;
    private Button bSubmit;
    private ListView lvfilterproname;
    private String fromdate, todate;
    private int StaffId;
    private float totrevforrange;
    private final static String FROM_TO = "fromto";

    private ArrayList<Integer> staffIdList;
    private ArrayList<SoldProduct> soldProductArrayList;
    private SharedPreferences prefFromTo;

    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private FilterByStaffIdAdapter filtstaffidAdapter;
    private DbStaffAdapter dbStaffAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_by_staff_id);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        prefFromTo = getSharedPreferences(FROM_TO, MODE_PRIVATE);
        fromdate = prefFromTo.getString("fromdate", "");
        todate = prefFromTo.getString("todate", "");
        totrevforrange = prefFromTo.getFloat("totrevforrange", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);

        toolTitle.setText(fromdate + " to " + todate + " Rs: " + totrevforrange);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);
        staffIdList = dbStaffAdapter.getAllStaffIds();
        tvstaffidTotRev = (TextView) findViewById(R.id.tv_filterbystaffid_stafftotrev);
        lvfilterproname = (ListView) findViewById(R.id.lv_filterbystaffid);
        actvStaffId = (AutoCompleteTextView) findViewById(R.id.actv_filterbystaffid_staffid);
        actvStaffId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                ArrayAdapter staffAdapter = new ArrayAdapter
                        (getApplicationContext(), R.layout.dropdownlist, staffIdList);
                actvStaffId.setThreshold(0);
                actvStaffId.setAdapter(staffAdapter);
                actvStaffId.showDropDown();
                return false;
            }
        });

        bSubmit = (Button) findViewById(R.id.b_filterbystaffid_submit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaffId = Integer.parseInt(actvStaffId.getText().toString());
                tvstaffidTotRev.setText(String.valueOf(getTotalRevenueByStaff()));
                soldProductArrayList = dbCustPurHistAdapter.getSoldHistoryForStaffId(StaffId);
                if (soldProductArrayList != null) {
                    filtstaffidAdapter = new FilterByStaffIdAdapter(getApplicationContext(), soldProductArrayList);
                    lvfilterproname.setAdapter(filtstaffidAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public float getTotalRevenueByStaff() {
        String formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        String formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);
        return dbCustPurHistAdapter.getTotalSalesForStaffId(StaffId, formattedFrom, formattedTo);
    }
}
