package in.istore.bitblue.app.adminMenu.transactions.totalrevenue.filterby;

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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.FilterByProdNameAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class FilterByProdName extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle, tvprodTotRev;
    private AutoCompleteTextView actvProdName;
    private Button bSubmit;
    private ListView lvfilterproname;

    private String fromdate, todate, prodName, AdminKey, formattedFrom, formattedTo, prodRevenue;
    private int StoreId;
    private float totrevforrange;
    private final static String FROM_TO = "fromto";

    private ArrayList<String> prodNameList = new ArrayList<String>();
    private ArrayList<SoldProduct> soldProductArrayList = new ArrayList<SoldProduct>();
    private SharedPreferences prefFromTo;

    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private FilterByProdNameAdapter filtProNameAdapter;
    private LinearLayout llprogressBar, llproductnames;
    private GlobalVariables globalVariable;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private SoldProduct prodtotrevdetails;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_by_prod_name);
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
        setSupportActionBar(toolbar);

        toolTitle.setText(fromdate + " to " + todate + " Rs: " + tinyDB.getString("totrevforrange"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        AdminKey = globalVariable.getAdminKey();
        StoreId = globalVariable.getStoreId();

        formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);

        llprogressBar = (LinearLayout) findViewById(R.id.ll_filterproductprogressbar);
        llproductnames = (LinearLayout) findViewById(R.id.ll_productlist);
        getAllProductNames();
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
                soldProductArrayList.clear();
                filtProNameAdapter = new FilterByProdNameAdapter(getApplicationContext(), soldProductArrayList);
                lvfilterproname.setAdapter(filtProNameAdapter);
                prodName = actvProdName.getText().toString();
                getRevenueGeneratedByProduct(prodName);
                getRevenueDetailsForProduct(prodName);
                /*tvprodTotRev.setText(String.valueOf(getTotalRevenueByProduct()));
                soldProductArrayList = dbCustPurHistAdapter.getPurchaseHistoryFor(prodName);
                if (soldProductArrayList != null) {
                    filtProNameAdapter = new FilterByProdNameAdapter(getApplicationContext(), soldProductArrayList);
                    lvfilterproname.setAdapter(filtProNameAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    private void getRevenueGeneratedByProduct(final String prodName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(FilterByProdName.this);

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
                nameValuePairs.add(new BasicNameValuePair("FromDate", formattedFrom));
                nameValuePairs.add(new BasicNameValuePair("todate", formattedTo));
                nameValuePairs.add(new BasicNameValuePair("productName", prodName));
                nameValuePairs.add(new BasicNameValuePair("StaffId", ""));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_PRODUCT_BETWEEN_RANGE, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        prodRevenue = jsonObject.getString("TotalAmount");
                        return prodRevenue;
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
                } else if (prodRevenue == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvprodTotRev.setText(prodRevenue);
                }
            }
        }.execute();
    }

    private void getRevenueDetailsForProduct(final String prodName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(FilterByProdName.this);

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
                nameValuePairs.add(new BasicNameValuePair("FromDate", formattedFrom));
                nameValuePairs.add(new BasicNameValuePair("todate", formattedTo));
                nameValuePairs.add(new BasicNameValuePair("StaffId", ""));
                nameValuePairs.add(new BasicNameValuePair("AdminId", ""));
                nameValuePairs.add(new BasicNameValuePair("ProductName", prodName));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TOTAL_REVENUE_FOR_PRODNAME, nameValuePairs);      //check the API Path
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
                            int soldquantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            float custPurAmnt = Float.parseFloat(jsonObject.getString("TotalAmount"));
                            long custMobile = Long.parseLong(jsonObject.getString("CusMobile"));
                            String soldDate = jsonObject.getString("SoldDate");
                            if (custMobile == 0) {
                                break;
                            }
                            prodtotrevdetails = new SoldProduct();
                            if (Id.equals("-1"))
                                prodtotrevdetails.setId("admin");
                            else prodtotrevdetails.setId(Id);
                            prodtotrevdetails.setItemSoldQuantity(soldquantity);
                            prodtotrevdetails.setItemTotalAmnt(custPurAmnt);
                            prodtotrevdetails.setMobile(custMobile);
                            prodtotrevdetails.setDate(soldDate);
                            soldProductArrayList.add(prodtotrevdetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (soldProductArrayList != null && soldProductArrayList.size() > 0) {
                        filtProNameAdapter = new FilterByProdNameAdapter(getApplicationContext(), soldProductArrayList);
                        lvfilterproname.setAdapter(filtProNameAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void getAllProductNames() {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                llproductnames.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_PRODUCTNAMES, nameValuePairs);
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
                llproductnames.setVisibility(View.VISIBLE);
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
                            String prodName = jsonObject.getString("ProductName");
                            if (prodName == null) {
                                break;
                            }
                            prodNameList.add(prodName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (prodNameList != null && prodNameList.size() > 0) {
                        actvProdName = (AutoCompleteTextView) findViewById(R.id.actv_filterbyprodname_prodname);
                        actvProdName.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                // prodNameList = dbProSubCatAdapter.getAllProductNames();
                                ArrayAdapter subcatadapter = new ArrayAdapter
                                        (getApplicationContext(), R.layout.dropdownlist, prodNameList);
                                actvProdName.setThreshold(0);
                                actvProdName.setAdapter(subcatadapter);
                                actvProdName.showDropDown();
                                return false;
                            }
                        });
                    } else
                        Toast.makeText(getApplicationContext(), "No Product Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private float getTotalRevenueByProduct() {
        String formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(fromdate);
        String formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(todate);
        return dbCustPurHistAdapter.getTotalSalesForProduct(prodName, formattedFrom, formattedTo);
    }
}
