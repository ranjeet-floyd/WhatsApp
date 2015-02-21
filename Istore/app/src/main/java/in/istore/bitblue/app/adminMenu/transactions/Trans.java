package in.istore.bitblue.app.adminMenu.transactions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import in.istore.bitblue.app.adminMenu.transactions.outofstock.OutOfStock;
import in.istore.bitblue.app.adminMenu.transactions.todaysales.TodaySales;
import in.istore.bitblue.app.adminMenu.transactions.totalrevenue.TotalRevSelectRange;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbTotSaleAmtByDateAdapter;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.api.API;

public class Trans extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvTotalSales, tvTodaySales, tvOutOfStock;
    private TextView bTotalSales, bTodaySales, bOutOfStock;

    private DbTotSaleAmtByDateAdapter dbTotSaleAmtByDateAdapter;
    private DbOutOfStockAdapter dbOutOfStockAdapter;

    private int StoreId;
    private String AdminKey, TotRev, TodayDate, TodSale, OutOfStock;

    private final static String TRANSACTION = "transaction";
    private SharedPreferences.Editor preftransaction;

    private GlobalVariables globalVariable;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Transaction Menu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        tinyDB = new TinyDB(this);
        globalVariable = (GlobalVariables) getApplicationContext();
        AdminKey = globalVariable.getAdminKey();
        StoreId = globalVariable.getStoreId();

        preftransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE).edit();

        dbTotSaleAmtByDateAdapter = new DbTotSaleAmtByDateAdapter(this);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        Date date = new Date();
        TodayDate = DateUtil.convertToStringDateOnly(date);
        getTotalRevenue();
        getTodaySales();
        getOutOfStockItems();

        tvTotalSales = (TextView) findViewById(R.id.tv_transaction_totalrevenue);
        tvTodaySales = (TextView) findViewById(R.id.tv_transaction_todaysales);
        tvOutOfStock = (TextView) findViewById(R.id.tv_transaction_outofstockitems);

        bTotalSales = (Button) findViewById(R.id.b_transaction_totalrevenue);
        bTotalSales.setOnClickListener(this);
        bTodaySales = (Button) findViewById(R.id.b_transaction_todaySales);
        bTodaySales.setOnClickListener(this);
        bOutOfStock = (Button) findViewById(R.id.b_transaction_outofstock);
        bOutOfStock.setOnClickListener(this);

    }

    private void getTotalRevenue() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Trans.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Fetching Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("AdminKey", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TOTAL_REVENUE, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        TotRev = jsonObject.getString("TotalAmount");
                        return TotRev;
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
                } else if (TotRev == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvTotalSales.setText(String.valueOf(TotRev));
                    tinyDB.putString("TotalRevenue", TotRev);
                }
            }
        }.execute();
    }

    private void getTodaySales() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Trans.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Fetching Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_TODAY_SALES, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        TodSale = jsonObject.getString("PerdaysalesAmount");
                        return TodSale;
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
                } else if (TodSale == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvTodaySales.setText(TodSale);
                    tinyDB.putString("TodaySales", TodSale);

                }
            }

        }.execute();
    }

    public void getOutOfStockItems() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Trans.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Fetching Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_OUTOFSTOCK_ITEMS, nameValuePairs);
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
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (OutOfStock == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    tvOutOfStock.setText(String.valueOf(OutOfStock));
                    tinyDB.putString("OOSItems", OutOfStock);
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_transaction_totalrevenue:
                Intent totalRevenue = new Intent(this, TotalRevSelectRange.class);
                startActivity(totalRevenue);
                break;
            case R.id.b_transaction_todaySales:
                Intent todaySales = new Intent(this, TodaySales.class);
                startActivity(todaySales);
                break;
            case R.id.b_transaction_outofstock:
                Intent outOfStock = new Intent(this, OutOfStock.class);
                startActivity(outOfStock);
                break;

        }
    }

}
