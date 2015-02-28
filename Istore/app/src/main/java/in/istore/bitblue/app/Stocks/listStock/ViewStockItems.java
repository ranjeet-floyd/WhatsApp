package in.istore.bitblue.app.Stocks.listStock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.addItem.AddItemsMenu;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbQuantityAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.ImageUtil;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.API;

public class ViewStockItems extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etbarcode, etname, etquantity;
    private ImageView ivProdImage;
    private Button bBack, bUpdate;

    private String id, UserType, Key, ProdName, Status;
    private int StoreId;
    private byte[] byteImage;
    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private DbQuantityAdapter dbquanAdapter;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stock_items);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("VIEW STOCK ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        id = getIntent().getStringExtra("barcode");

        dbAdapter = new DbProductAdapter(this);
        dbquanAdapter = new DbQuantityAdapter(this);

        bBack = (Button) findViewById(R.id.b_viewstockitem_back);
        bBack.setOnClickListener(this);
        bUpdate = (Button) findViewById(R.id.b_viewstockitem_update);
        bUpdate.setOnClickListener(this);

        ivProdImage = (ImageView) findViewById(R.id.iv_viewstockitem_image);
        etbarcode = (EditText) findViewById(R.id.et_viewstockitem_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_viewstockitem_prod_name);
        etquantity = (EditText) findViewById(R.id.et_viewstockitem_prod_quantity);

        if ((id != null) && (!id.equals(""))) {
            getProductDetails(id);
            // product = getProductDetail(id);

/*            if (product != null) {
                origquantity = product.getQuantity();
                etbarcode.setText(id);
                etname.setText(product.getName());
                etquantity.setText(String.valueOf(origquantity));
                byteImage = product.getImage();
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
                if (bitmap != null) {
                    ivProdImage.setImageBitmap(bitmap);
                }
            }*/
        }
    }

    private void getProductDetails(final String id) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ViewStockItems.this);
            String prodId
                    ,
                    prodName
                    ,
                    prodDesc;
            byte[] prodImage;
            int prodQuantity;
            float prodSellPrice;

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
                nameValuePairs.add(new BasicNameValuePair("PId", id));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_PRODUCTDETAILS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        prodId = jsonObject.getString("Id");
                        prodName = jsonObject.getString("Name");
                        prodImage = ImageUtil.convertBase64ImagetoByteArrayImage(jsonObject.getString("Image"));
                        prodDesc = jsonObject.getString("ProductDesc");
                        prodQuantity = Integer.parseInt(jsonObject.getString("Quantity"));
                        prodSellPrice = Float.parseFloat(jsonObject.getString("Sellingprice"));
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
                } else if (prodId == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    //set all textviews
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (prodImage != null)
                        bitmap = BitmapFactory.decodeByteArray(prodImage, 0, prodImage.length, options);
                    etname.setText(prodName);
                    etbarcode.setText(prodId);
                    etquantity.setText(String.valueOf(prodQuantity));
                    ivProdImage.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }


    /* public Product getProductDetail(String id) {
         return dbAdapter.getExistingProductDetails(id);
     }
 */
    @Override
    public void onClick(View button) {

        switch (button.getId()) {
            case R.id.b_viewstockitem_back:
                startActivity(new Intent(this, AddItemsMenu.class));
                break;
            case R.id.b_viewstockitem_update:
                int addedquantity;
                try {
                    addedquantity = Integer.parseInt(etquantity.getText().toString());
                } catch (Exception e) {
                    addedquantity = 0;
                }
                if (addedquantity == 0) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {

                    //int retprod = updateProductDetails(id, totalquatity);
                   /* long retquant = insertQuantityDetails(id, addedquantity);
                    if (retprod <= 0 || retquant < 0) {
                    } else {
                        Toast.makeText(this, "Quantity Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ListMyStock.class));
                    }*/
                    updateProductQuantity(id, addedquantity);
                }
                break;
        }
    }

    private void updateProductQuantity(final String id, final int addedquantity) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ViewStockItems.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Updating Product Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("PId", id));
                nameValuePairs.add(new BasicNameValuePair("Quantity", String.valueOf(addedquantity)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_PRODUCT_QUANTITY, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("status");
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
                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Quantity Updated", Toast.LENGTH_LONG).show();
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Quantity Update Failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }

    private int getTotalQuantity(int origquantity, int addedquantity) {
        return origquantity + addedquantity;
    }

    private long insertQuantityDetails(String id, int addedquantity) {
        return dbquanAdapter.insertQuantityDetails(id, addedquantity);
    }

    private int updateProductDetails(String id, int totalquatity) {
        return dbAdapter.updateProductQuantity(id, totalquatity);
    }
}
