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
import in.istore.bitblue.app.adapters.FilterByProdNameAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.DateUtil;

public class FilterByProdName extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvprodTotRev;
    private AutoCompleteTextView actvProdName;
    private Button bSubmit;
    private ListView lvfilterproname;
    private String fromdate, todate, prodName;
    private float totrevforrange;
    private final static String FROM_TO = "fromto";

    private ArrayList<String> prodNameList;
    private ArrayList<SoldProduct> soldProductArrayList;
    private SharedPreferences prefFromTo;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private FilterByProdNameAdapter filtProNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_by_prod_name);
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
        dbProSubCatAdapter = new DbProSubCatAdapter(this);
        dbCustPurHistAdapter = new DbCustPurHistAdapter(this);

        tvprodTotRev = (TextView) findViewById(R.id.tv_filterbyprodname_prodtotrev);
        lvfilterproname = (ListView) findViewById(R.id.lv_filterbyname);
        actvProdName = (AutoCompleteTextView) findViewById(R.id.actv_filterbyprodname_prodname);
        actvProdName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                prodNameList = dbProSubCatAdapter.getAllProductNames();
                ArrayAdapter subcatadapter = new ArrayAdapter
                        (getApplicationContext(), R.layout.dropdownlist, prodNameList);
                actvProdName.setThreshold(0);
                actvProdName.setAdapter(subcatadapter);
                actvProdName.showDropDown();
                return false;
            }
        });

        bSubmit = (Button) findViewById(R.id.b_filterbyprodname_submit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prodName = actvProdName.getText().toString();
                tvprodTotRev.setText(String.valueOf(getTotalRevenueByProduct()));
                soldProductArrayList = dbCustPurHistAdapter.getPurchaseHistoryFor(prodName);
                if (soldProductArrayList != null) {
                    filtProNameAdapter = new FilterByProdNameAdapter(getApplicationContext(), soldProductArrayList);
                    lvfilterproname.setAdapter(filtProNameAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private float getTotalRevenueByProduct() {
        String formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        String formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);
        return dbCustPurHistAdapter.getTotalSalesForProduct(prodName, formattedFrom, formattedTo);
    }
}
