package in.istore.bitblue.app.adminMenu.transactions.totalrevenue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TotRevDetailsAdapter;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby.FilterByProdName;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby.FilterByStaffId;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TotRevDetails;
import in.istore.bitblue.app.utilities.DateUtil;

public class TotalRevViewDetails extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvFrom, tvTo, tvRangeRevenue;
    private ListView lvtotrevdetails;

    private FloatingActionsMenu itemMenu;
    private FloatingActionButton filterbystaffid, filterbyproductname;

    private String formattedfrom, formattedto, fromdate, todate;
    private float TotalRevenue, totrevforrange;
    private final static String TOTAL_REVENUE_FOR_RANGE = "TotalRevenueForRange";
    private final static String TRANSACTION = "transaction";
    private final static String FROM_TO = "fromto";

    private SharedPreferences.Editor prefFromTo;
    private SharedPreferences prefTotalRevenueForRange;
    private SharedPreferences prefTransaction;

    private ArrayList<TotRevDetails> totRevDetailsArrayList;
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private TotRevDetailsAdapter totRevDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_rev_view_details);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        prefTransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE);
        prefTotalRevenueForRange = getSharedPreferences(TOTAL_REVENUE_FOR_RANGE, MODE_PRIVATE);
        prefFromTo = getSharedPreferences(FROM_TO, MODE_PRIVATE).edit();

        TotalRevenue = prefTransaction.getFloat("TotalRevenue", 0);
        formattedfrom = prefTotalRevenueForRange.getString("formattedfromdate", "");
        formattedto = prefTotalRevenueForRange.getString("formattedtodate", "");
        totrevforrange = prefTotalRevenueForRange.getFloat("totalrevforrange", 0);

        fromdate = DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(formattedfrom);
        todate = DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(formattedto);
        prefFromTo.putString("fromdate", fromdate);
        prefFromTo.putString("todate", todate);
        prefFromTo.putFloat("totrevforrange", totrevforrange);
        prefFromTo.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);

        toolTitle.setText("Total Revenue: Rs " + TotalRevenue);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_listmystock_menu);

        filterbystaffid = (FloatingActionButton) findViewById(R.id.fab_totrevdetails_staffid);
        filterbystaffid.setOnClickListener(this);

        filterbyproductname = (FloatingActionButton) findViewById(R.id.fab_totrevdetails_product_name);
        filterbyproductname.setOnClickListener(this);

        tvFrom = (TextView) findViewById(R.id.tv_totrevdetails_from);
        tvFrom.setText(fromdate);
        tvTo = (TextView) findViewById(R.id.tv_totrevdetails_to);
        tvTo.setText(todate);
        tvRangeRevenue = (TextView) findViewById(R.id.tv_totrevdetails_rangeRevnue);
        tvRangeRevenue.setText(String.valueOf(totrevforrange));

        dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        totRevDetailsArrayList = dbCustPurHistAdapter.getAllSoldProductsHistoryBetween(formattedfrom, formattedto);
        if (totRevDetailsArrayList != null) {
            lvtotrevdetails = (ListView) findViewById(R.id.lv_totrevdetails_list);
            totRevDetailsAdapter = new TotRevDetailsAdapter(this, totRevDetailsArrayList);
            lvtotrevdetails.setAdapter(totRevDetailsAdapter);
        } else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_totrevdetails_staffid:
                filterByStaffId();
                break;
            case R.id.fab_totrevdetails_product_name:
                filterByProductName();
                break;
        }
    }

    private void filterByProductName() {
        Intent prodname = new Intent(this, FilterByProdName.class);
        startActivity(prodname);
    }

    private void filterByStaffId() {
        Intent staffId = new Intent(this, FilterByStaffId.class);
        startActivity(staffId);
    }
}
