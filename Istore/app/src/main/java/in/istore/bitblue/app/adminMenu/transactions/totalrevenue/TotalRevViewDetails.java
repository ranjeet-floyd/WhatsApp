package in.istore.bitblue.app.adminMenu.transactions.totalrevenue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TotRevDetailsAdapter;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby.FilterByProdName;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby.FilterByStaffId;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TotRevDetails;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class TotalRevViewDetails extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvFrom, tvTo, tvRangeRevenue;
    private ListView lvtotrevdetails;

    private FloatingActionsMenu itemMenu;
    private FloatingActionButton filterbystaffid, filterbyproductname;

    private int StoreId;
    private long AdminId;
    private String AdminKey, formattedfrom, formattedto, fromdate, todate;
    private float TotalRevenue, totrevforrange;
    private final static String TOTAL_REVENUE_FOR_RANGE = "TotalRevenueForRange";
    private final static String TRANSACTION = "transaction";
    private final static String FROM_TO = "fromto";

    private SharedPreferences.Editor prefFromTo;
    private SharedPreferences prefTotalRevenueForRange;
    private SharedPreferences prefTransaction;

    private ArrayList<TotRevDetails> totRevDetailsArrayList = new ArrayList<TotRevDetails>();
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private TotRevDetailsAdapter totRevDetailsAdapter;
    private GlobalVariables globalVariable;

    private TinyDB tinyDB;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private TotRevDetails totRevDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_rev_view_details);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        tinyDB = new TinyDB(this);
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

        toolTitle.setText("Total Revenue: Rs " + tinyDB.getString("TotalRevenue"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        globalVariable = (GlobalVariables) getApplicationContext();
        StoreId = globalVariable.getStoreId();
        AdminKey = globalVariable.getAdminKey();
        AdminId = globalVariable.getAdminId();
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
        tvRangeRevenue.setText(tinyDB.getString("totrevforrange"));
        lvtotrevdetails = (ListView) findViewById(R.id.lv_totrevdetails_list);
        getTransactionDetailsBetween(formattedfrom, formattedto);
       /* dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        totRevDetailsArrayList = dbCustPurHistAdapter.getAllSoldProductsHistoryBetween(formattedfrom, formattedto);
        if (totRevDetailsArrayList != null) {
            totRevDetailsAdapter = new TotRevDetailsAdapter(this, totRevDetailsArrayList);
            lvtotrevdetails.setAdapter(totRevDetailsAdapter);
        } else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();*/
    }

    private void getTransactionDetailsBetween(final String from, final String to) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(TotalRevViewDetails.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Revenue Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("FromDate", from));
                nameValuePairs.add(new BasicNameValuePair("todate", to));
                nameValuePairs.add(new BasicNameValuePair("StaffId", ""));
                nameValuePairs.add(new BasicNameValuePair("AdminId", ""));
                nameValuePairs.add(new BasicNameValuePair("ProductName", ""));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TOTAL_REVENUE_FOR_ALL_STAFFANDPRODUCTS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getApplicationContext(), "No Details to show", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String Id = jsonObject.getString("StaffId");
                            String prodName = jsonObject.getString("ProductName");
                            int soldquantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            float custPurAmnt = Float.parseFloat(jsonObject.getString("TotalAmount"));
                            long custMobile = Long.parseLong(jsonObject.getString("CusMobile"));
                            String soldDate = jsonObject.getString("SoldDate");
                            if (custMobile == 0) {
                                break;
                            }
                            totRevDetails = new TotRevDetails();
                            if (Id.equals("-1"))
                                totRevDetails.setId("admin");
                            else totRevDetails.setId(Id);
                            totRevDetails.setProdName(prodName);
                            totRevDetails.setQuantity(soldquantity);
                            totRevDetails.setPurchaseAmnt(custPurAmnt);
                            totRevDetails.setMobile(custMobile);
                            totRevDetails.setDate(soldDate);
                            totRevDetailsArrayList.add(totRevDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (totRevDetailsArrayList != null && totRevDetailsArrayList.size() > 0) {
                        totRevDetailsAdapter = new TotRevDetailsAdapter(getApplicationContext(), totRevDetailsArrayList);
                        lvtotrevdetails.setAdapter(totRevDetailsAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
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
