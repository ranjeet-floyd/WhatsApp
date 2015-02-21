package in.istore.bitblue.app.adminMenu.transactions.todaysales;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.TodaySaleAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.TodaysSale;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.api.API;

public class TodaySales extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvtodaysales;

    private int StoreId;
    private float TodaySales;
    private String AdminKey, TodayDate;
    private ArrayList<TodaysSale> todaysSaleArrayList = new ArrayList<>();
    private TodaySaleAdapter todaySaleAdapter;
    private DbCustPurHistAdapter custPurHistAdapter;
    private final static String TRANSACTION = "transaction";
    private SharedPreferences preftransaction;

    private TinyDB tinyDB;
    private GlobalVariables globalVariables;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private TodaysSale todaySales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_sales);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        tinyDB = new TinyDB(this);
        //preftransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE);
        //TodaySales = preftransaction.getFloat("TodaySales", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Today's Total Sales: Rs " + tinyDB.getString("TodaySales"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        java.util.Date date = new java.util.Date();
        TodayDate = DateUtil.convertToStringDateOnly(date);
        globalVariables = (GlobalVariables) getApplicationContext();
        AdminKey = globalVariables.getAdminKey();
        StoreId = globalVariables.getStoreId();
        custPurHistAdapter = new DbCustPurHistAdapter(this);
        lvtodaysales = (ListView) findViewById(R.id.lv_todaysales_list);
        getTodaySales();
        //todaysSaleArrayList = custPurHistAdapter.getTodaysSale();
        /*if (todaysSaleArrayList != null) {
            todaySaleAdapter = new TodaySaleAdapter(this, todaysSaleArrayList);
            lvtodaysales.setAdapter(todaySaleAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void getTodaySales() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(TodaySales.this);

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
                nameValuePairs.add(new BasicNameValuePair("AdminKey", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("FromDate", TodayDate));
                nameValuePairs.add(new BasicNameValuePair("todate", TodayDate));
                nameValuePairs.add(new BasicNameValuePair("StaffId", ""));
                nameValuePairs.add(new BasicNameValuePair("ProductName", ""));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TOTAL_REVENUE_FOR_ALL_STAFFANDPRODUCTS, nameValuePairs);      //check the API Path
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

                            long staffId = Long.parseLong(jsonObject.getString("StaffId"));
                            String prodName = jsonObject.getString("ProductName");
                            int soldquantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            float custPurAmnt = Float.parseFloat(jsonObject.getString("TotalAmount"));
                            long custMobile = Long.parseLong(jsonObject.getString("CusMobile"));
                            if (custMobile == 0) {
                                break;
                            }
                            todaySales = new TodaysSale();
                            todaySales.setStaffId(staffId);
                            todaySales.setProdName(prodName);
                            todaySales.setQuantity(soldquantity);
                            todaySales.setPurchaseAmnt(custPurAmnt);
                            todaySales.setMobile(custMobile);
                            todaysSaleArrayList.add(todaySales);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (todaysSaleArrayList != null && todaysSaleArrayList.size() > 0) {
                        todaySaleAdapter = new TodaySaleAdapter(getApplicationContext(), todaysSaleArrayList);
                        lvtodaysales.setAdapter(todaySaleAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
