package in.istore.bitblue.app.home.transactions.outofstock;

import android.app.ProgressDialog;
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
import in.istore.bitblue.app.adapters.OutOfStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.pojo.Outofstock;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class OutOfStock extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvOutOfStock;
    private ProgressBar progressBar;

    private int StoreId;
    private String outofStock, UserType, Key;
    private ArrayList<Outofstock> outofstockArrayList = new ArrayList<>();
    private DbOutOfStockAdapter dbOutOfStockAdapter;
    private OutOfStockAdapter outOfStockAdapter;

    private TinyDB tinyDB;
    private GlobalVariables globalVariables;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Outofstock oosItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_stock);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        // outofStock = dbOutOfStockAdapter.getOutOfStockItems();
        tinyDB = new TinyDB(this);
        outofStock = tinyDB.getString("OOSItems");

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("Out Of Stock Items: " + outofStock);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariables = (GlobalVariables) getApplicationContext();
        UserType = globalVariables.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariables.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariables.getStaffKey();
        }

        StoreId = globalVariables.getStoreId();
        getAllOutOfStockItems();
        lvOutOfStock = (ListView) findViewById(R.id.lv_outofstock_list);
       /* outofstockArrayList = dbOutOfStockAdapter.getAllOutOfStockItems();
        if (outofstockArrayList != null) {
            outOfStockAdapter = new OutOfStockAdapter(this, outofstockArrayList);
            lvOutOfStock.setAdapter(outOfStockAdapter);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void getAllOutOfStockItems() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(OutOfStock.this);

            @Override
            protected void onPreExecute() {
               /* dialog.setMessage("Getting Revenue Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_OUTOFSTOCKITEMS, nameValuePairs);      //check the API Path
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

                            String prodName = jsonObject.getString("Oosprodname");
                            int remquantity = Integer.parseInt(jsonObject.getString("Remainquantity"));
                            long suppMobile = Long.parseLong(jsonObject.getString("Suppmobile"));
                            int minLimit = Integer.parseInt(jsonObject.getString("MinQuantity"));
                            if (prodName == null) {
                                break;
                            }
                            oosItem = new Outofstock();
                            oosItem.setProdName(prodName);
                            oosItem.setRemQuantity(remquantity);
                            oosItem.setSuppMobile(suppMobile);
                            outofstockArrayList.add(oosItem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outofstockArrayList != null && outofstockArrayList.size() > 0) {
                        outOfStockAdapter = new OutOfStockAdapter(getApplicationContext(), outofstockArrayList);
                        lvOutOfStock.setAdapter(outOfStockAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Details Available", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
