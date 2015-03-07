package in.istore.bitblue.app.home.transactions.totalrevenue.filterby;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import in.istore.bitblue.app.adapters.FilterByStaffIdAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class FilterByStaffId extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvstaffidTotRev;
    private AutoCompleteTextView actvStaffId;
    private Button bSubmit;
    private ListView lvfilterproname;
    private LinearLayout llprogressBar, llstaffIds;
    private ProgressBar progressBar;

    private String fromdate, todate, AdminKey, formattedFrom, formattedTo, staffRevenue;
    private int StaffId, StoreId;
    private float totrevforrange;
    private final static String FROM_TO = "fromto";

    private ArrayList<Integer> staffIdList = new ArrayList<Integer>();
    private ArrayList<SoldProduct> stafftotrevArrayList = new ArrayList<SoldProduct>();
    private SharedPreferences prefFromTo;

    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private FilterByStaffIdAdapter filtstaffidAdapter;
    private DbStaffAdapter dbStaffAdapter;
    private TinyDB tinyDB;

    private GlobalVariables globalVariable;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private SoldProduct stafftotrevdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_by_staff_id);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        tinyDB = new TinyDB(this);
        prefFromTo = getSharedPreferences(FROM_TO, MODE_PRIVATE);
        fromdate = prefFromTo.getString("fromdate", "");
        todate = prefFromTo.getString("todate", "");
        totrevforrange = prefFromTo.getFloat("totrevforrange", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);

        toolTitle.setText("Revenue: " + getResources().getString(R.string.rs) + " " + tinyDB.getString("totrevforrange"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        globalVariable = (GlobalVariables) getApplicationContext();
        AdminKey = globalVariable.getAdminKey();
        StoreId = globalVariable.getStoreId();

        dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);
        formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);

        //staffIdList = dbStaffAdapter.getAllStaffIds();
        llprogressBar = (LinearLayout) findViewById(R.id.ll_filterstaffprogressbar);
        llstaffIds = (LinearLayout) findViewById(R.id.ll_staffids);
        getAllStaffIdsOnServer();

        tvstaffidTotRev = (TextView) findViewById(R.id.tv_filterbystaffid_stafftotrev);
        lvfilterproname = (ListView) findViewById(R.id.lv_filterbystaffid);

        bSubmit = (Button) findViewById(R.id.b_filterbystaffid_submit);
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stafftotrevArrayList.clear();
                filtstaffidAdapter = new FilterByStaffIdAdapter(getApplicationContext(), stafftotrevArrayList);
                lvfilterproname.setAdapter(filtstaffidAdapter);
                StaffId = Integer.parseInt(actvStaffId.getText().toString());
                getRevenueGeneratedByStaffOnServer(StaffId);
                getRevenueDetailsOnServerFor(StaffId);
                /*tvstaffidTotRev.setText(String.valueOf(getTotalRevenueByStaff()));
                soldProductArrayList = dbCustPurHistAdapter.getSoldHistoryForStaffId(StaffId);
                if (soldProductArrayList != null) {
                    filtstaffidAdapter = new FilterByStaffIdAdapter(getApplicationContext(), soldProductArrayList);
                    lvfilterproname.setAdapter(filtstaffidAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                    lvfilterproname.setAdapter(null);
                }*/
            }
        });
    }

    private void getRevenueGeneratedByStaffOnServer(final int staffId) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(FilterByStaffId.this);

            @Override
            protected void onPreExecute() {
              /*  dialog.setMessage("Getting Revenue Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.show();*/
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("FromDate", formattedFrom));
                nameValuePairs.add(new BasicNameValuePair("todate", formattedTo));
                nameValuePairs.add(new BasicNameValuePair("StaffId", String.valueOf(staffId)));
                nameValuePairs.add(new BasicNameValuePair("productName", ""));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_STAFF_BETWEEN_RANGE, nameValuePairs);      //check the API Path
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        staffRevenue = jsonObject.getString("TotalAmount");
                        return staffRevenue;
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                // dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (staffRevenue == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvstaffidTotRev.setText(staffRevenue);
                }
            }
        }.execute();

    }

    private void getRevenueDetailsOnServerFor(final int staffId) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(FilterByStaffId.this);

            @Override
            protected void onPreExecute() {
              /*  dialog.setMessage("Getting Revenue Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("FromDate", formattedFrom));
                nameValuePairs.add(new BasicNameValuePair("todate", formattedTo));
                nameValuePairs.add(new BasicNameValuePair("StaffId", String.valueOf(staffId)));
                nameValuePairs.add(new BasicNameValuePair("AdminId", ""));
                nameValuePairs.add(new BasicNameValuePair("ProductName", ""));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TOTAL_REVENUE_FOR_STAFF, nameValuePairs);      //check the API Path
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
                            stafftotrevdetails = new SoldProduct();
                            stafftotrevdetails.setItemName(prodName);
                            stafftotrevdetails.setItemSoldQuantity(soldquantity);
                            stafftotrevdetails.setItemTotalAmnt(custPurAmnt);
                            stafftotrevdetails.setMobile(custMobile);
                            stafftotrevdetails.setDate(soldDate);
                            stafftotrevArrayList.add(stafftotrevdetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (stafftotrevArrayList != null && stafftotrevArrayList.size() > 0) {
                        filtstaffidAdapter = new FilterByStaffIdAdapter(getApplicationContext(), stafftotrevArrayList);
                        lvfilterproname.setAdapter(filtstaffidAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();

    }

    private void getAllStaffIdsOnServer() {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                llstaffIds.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("AdminKey", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_STAFF_IDS, nameValuePairs);
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
                llstaffIds.setVisibility(View.VISIBLE);
                llprogressBar.setVisibility(View.GONE);
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
                            int staffId = Integer.parseInt(jsonObject.getString("StaffID"));
                            if (staffId == 0) {
                                break;
                            }
                            staffIdList.add(staffId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (staffIdList != null && staffIdList.size() > 0) {
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
                    } else
                        Toast.makeText(getApplicationContext(), "No Staff Ids Available", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public float getTotalRevenueByStaff() {
        String formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        String formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);
        return dbCustPurHistAdapter.getTotalSalesForStaffId(StaffId, formattedFrom, formattedTo);
    }
}
