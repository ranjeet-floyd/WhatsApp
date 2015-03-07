package in.istore.bitblue.app.staffMenu.transactions.todaysales;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TodaySaleStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TodaysSale;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class TodaySalesStaff extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvtodaysalesstaff;
    private ProgressBar progressBar;

    private float TodaySalesStaff;
    private long StaffId;
    private int StoreId;
    private String TodaySaleForStaff, StaffKey, TodayDate;
    private ArrayList<TodaysSale> todaysSaleArrayList = new ArrayList<TodaysSale>();
    private TodaySaleStaffAdapter todaySaleAdapter;
    private DbCustPurHistAdapter custPurHistAdapter;
    private final static String TRANSACTION_STAFF = "transactionstaff";
    private SharedPreferences preftransactionstaff;
    private GlobalVariables globalVariables;
    private TinyDB tinyDB;

    private TodaysSale todaysSaleStaff;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_sales_staff);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        tinyDB = new TinyDB(this);
        TodaySaleForStaff = tinyDB.getString("TodaySalesStaff");
        // preftransactionstaff = getSharedPreferences(TRANSACTION_STAFF, MODE_PRIVATE);
        // TodaySalesStaff = preftransactionstaff.getFloat("TodaySales", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("Today's Total Sales: Rs " + TodaySaleForStaff);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariables = (GlobalVariables) getApplicationContext();
        StaffKey = globalVariables.getStaffKey();
        StaffId = globalVariables.getStaffId();
        StoreId = globalVariables.getStoreId();
        java.util.Date date = new java.util.Date();
        TodayDate = DateUtil.convertToStringDateOnly(date);
        //custPurHistAdapter = new DbCustPurHistAdapter(this);
        lvtodaysalesstaff = (ListView) findViewById(R.id.lv_todaysalesstaff_list);
        getTodaySalesForStaff();
        //todaysSaleArrayList = custPurHistAdapter.getTodaysSaleForStaff(StaffId);  //Change this
       /* if (todaysSaleArrayList != null) {
            todaySaleAdapter = new TodaySaleStaffAdapter(this, todaysSaleArrayList);
            lvtodaysalesstaff.setAdapter(todaySaleAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void getTodaySalesForStaff() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(TodaySalesStaff.this);

            @Override
            protected void onPreExecute() {
             /*   dialog.setMessage("Getting Revenue Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", StaffKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("FromDate", TodayDate));
                nameValuePairs.add(new BasicNameValuePair("todate", TodayDate));
                nameValuePairs.add(new BasicNameValuePair("StaffId", String.valueOf(StaffId)));
                nameValuePairs.add(new BasicNameValuePair("AdminId", ""));
                nameValuePairs.add(new BasicNameValuePair("ProductName", ""));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TODAY_SALES_FORSTAFF, nameValuePairs);      //check the API Path
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
                //dialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
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
                            String prodName = jsonObject.getString("ProductName");
                            int soldquantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            float custPurAmnt = Float.parseFloat(jsonObject.getString("TotalAmount"));
                            long custMobile = Long.parseLong(jsonObject.getString("CusMobile"));
                            String soldDate = jsonObject.getString("SoldDate");
                            if (custMobile == 0) {
                                break;
                            }
                            todaysSaleStaff = new TodaysSale();
                            todaysSaleStaff.setProdName(prodName);
                            todaysSaleStaff.setQuantity(soldquantity);
                            todaysSaleStaff.setPurchaseAmnt(custPurAmnt);
                            todaysSaleStaff.setMobile(custMobile);
                            todaysSaleArrayList.add(todaysSaleStaff);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (todaysSaleArrayList != null && todaysSaleArrayList.size() > 0) {
                        todaySaleAdapter = new TodaySaleStaffAdapter(getApplicationContext(), todaysSaleArrayList);
                        lvtodaysalesstaff.setAdapter(todaySaleAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
