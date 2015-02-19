package in.istore.bitblue.app.Stocks.listSoldStock;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.SoldHistoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class ViewSoldItem extends ActionBarActivity {
    private Toolbar toolbar;

    private TextView tvbarcode, tvname, tvdesc, tvsoldquantity, tvremquantity;
    private ImageView ivProdImage;
    private ListView lvsoldhist;

    private String id, UserType, Key, ProdName;
    private byte[] byteImage;
    private int StoreId;
    private Bitmap bitmap;
    private ArrayList<Product> soldhistList;

    private GlobalVariables globalVariable;
    private DbSoldItemAdapter dbsolAdapter;
    private SoldHistoryAdapter soldhistAdapter;
    private Product product;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sold_item);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("VIEW SOLD ITEM");
    }

    private void initViews() {

        globalVariable = (GlobalVariables) getApplicationContext();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }
        StoreId = globalVariable.getStoreId();

        product = new Product();
        ivProdImage = (ImageView) findViewById(R.id.iv_viewsolditem_image);

        tvbarcode = (TextView) findViewById(R.id.tv_viewsolditem_barcode_prod_id);
        tvname = (TextView) findViewById(R.id.tv_viewsolditem_prod_name);
        tvdesc = (TextView) findViewById(R.id.tv_viewsolditem_desc);
        tvsoldquantity = (TextView) findViewById(R.id.tv_viewsolditem_soldquan);
        tvremquantity = (TextView) findViewById(R.id.tv_viewsolditem_availquan);

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        dbsolAdapter = new DbSoldItemAdapter(this);


        if (id != null) {
            // getSoldDetailsfor(id);
           /* soldhistList = getAllSoldDetailsfor(id);
            if (soldhistList != null) {
                soldhistAdapter = new SoldHistoryAdapter(this, soldhistList);
                lvsoldhist = (ListView) findViewById(R.id.lv_viewsolditem_soldhist);
                lvsoldhist.setAdapter(soldhistAdapter);
            }*/
            getSoldProductDetails(id);
        } else {
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show();
        }

    }

    private void getSoldProductDetails(final String id) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ViewSoldItem.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Product Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("ItemId ", id));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_SOLDPRODUCTDETAILS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        ProdName = jsonObject.getString("");
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
                } else if (ProdName == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    //set all textviews
                }
            }
        }.execute();
    }

    private ArrayList<Product> getAllSoldDetailsfor(String id) {
        return dbsolAdapter.getAllSoldDetailsfor(id);
    }

    private void getSoldDetailsfor(String id) {
        product = dbsolAdapter.getSoldProductDetails(id);
        if (product != null) {
            byteImage = product.getImage();
            if (byteImage != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(byteImage);
                bitmap = BitmapFactory.decodeStream(imageStream);
                ivProdImage.setImageBitmap(bitmap);
            }
            tvbarcode.setText(id);
            tvname.setText(product.getName());
            tvdesc.setText(product.getDesc());
            tvsoldquantity.setText(String.valueOf(product.getSoldQuantity()));
            tvremquantity.setText(String.valueOf(product.getRemQuantity()));
        }

    }
}
