package in.istore.bitblue.app.staffMenu.transactions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.home.transactions.outofstock.OutOfStock;
import in.istore.bitblue.app.staffMenu.transactions.todaysales.TodaySalesStaff;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class TransStaff extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvTodaySalesStaff, tvOutOfStock;
    private Button bTodaySales, bOutofStock;
    private ProgressBar progressBar;

    private float TodaySales;
    private int OutOfStockItems, StoreId;
    private long StaffId;
    private String StaffKey, TodSale, OutOfStock, TodayDate;

    private final static String TRANSACTION_STAFF = "transactionstaff";
    private SharedPreferences.Editor preftransactionstaff;
    private DbCustPurHistAdapter dbCustPurHistAdapter;
    private DbOutOfStockAdapter dbOutOfStockAdapter;
    private GlobalVariables globalVariables;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_staff);
        globalVariables = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("Transaction Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        /*dbCustPurHistAdapter = new DbCustPurHistAdapter(this);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);*/

        globalVariables = (GlobalVariables) getApplicationContext();
        tinyDB = new TinyDB(this);
        StaffId = globalVariables.getStaffId();
        StoreId = globalVariables.getStoreId();
        StaffKey = globalVariables.getStaffKey();

        Date date = new Date();
        TodayDate = DateUtil.convertToStringDateOnly(date);
        //TodaySales = dbCustPurHistAdapter.getTodaySalesForStaffId(StaffId);  //Change this
        //OutOfStockItems = dbOutOfStockAdapter.getOutOfStockItems();

        preftransactionstaff = getSharedPreferences(TRANSACTION_STAFF, MODE_PRIVATE).edit();

        preftransactionstaff.commit();

        tvTodaySalesStaff = (TextView) findViewById(R.id.tv_transaction_todaysalesstaff);
        // tvTodaySalesStaff.setText(String.valueOf(TodaySales));
        tvOutOfStock = (TextView) findViewById(R.id.tv_transaction_outofstockitemsstaff);
        // tvOutOfStock.setText(String.valueOf(OutOfStockItems));

        bTodaySales = (Button) findViewById(R.id.b_transaction_todaySalesstaff);
        bOutofStock = (Button) findViewById(R.id.b_transaction_outofstockstaff);
        bTodaySales.setOnClickListener(this);
        bOutofStock.setOnClickListener(this);
        getTodaySales();
        getOutOfStockItems();
    }

    private void getTodaySales() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(TransStaff.this);

            @Override
            protected void onPreExecute() {
             /*   dialog.setMessage("Fetching Details...");
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
                nameValuePairs.add(new BasicNameValuePair("StaffId ", String.valueOf(StaffId)));

                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_GET_SUM_OF_TOTAL_REVENUE_FOR_STAFF_BETWEEN_RANGE, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        TodSale = jsonObject.getString("TotalAmount");
                        return TodSale;
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                //dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (TodSale == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvTodaySalesStaff.setText(getResources().getString(R.string.rs) + "  " + TodSale);
                    tinyDB.putString("TodaySalesStaff", TodSale);
                }
            }

        }.execute();
    }

    public void getOutOfStockItems() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(TransStaff.this);

            @Override
            protected void onPreExecute() {
               /* dialog.setMessage("Fetching Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", StaffKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_GET_OUTOFSTOCK_ITEMS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        OutOfStock = jsonObject.getString("Count");
                        return OutOfStock;
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
                } else if (OutOfStock == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvOutOfStock.setText(String.valueOf(OutOfStock) + " " + "ITEM(S)");
                    tinyDB.putString("OutOfStockItem", OutOfStock);
                }
            }
        }.execute();
    }


    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_transaction_todaySalesstaff:
                Intent todaySalesStaff = new Intent(this, TodaySalesStaff.class);
                startActivity(todaySalesStaff);
                break;
            case R.id.b_transaction_outofstockstaff:
                Intent outOfStock = new Intent(this, OutOfStock.class);
                startActivity(outOfStock);
                break;

        }
    }
}
