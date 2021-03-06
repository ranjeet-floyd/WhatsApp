package in.istore.bitblue.app.home.Stocks.sellItem;

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
import android.widget.ImageView;
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
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.home.Stocks.listSoldStock.SoldItemForm;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.ImageUtil;
import in.istore.bitblue.app.utilities.JSONParser;

public class SellItem extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bEdit, baddtocart;
    private TextView tvbarcode, tvname, tvsellprice, toolTitle;
    private TextView tvdesc;
    private ImageView ivProdImage;
    private ProgressBar progressBar;

    private GlobalVariables globalVariable;
    private String id, productId, scanContent, category, name, desc, Key, UserType;
    private float sellprice;
    private byte[] byteImage;
    private Bitmap bitmap;
    private int StoreId;

    private DbProductAdapter dbProAdapter;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item_form);
        globalVariable = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("PRODUCT DETAILS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }
        StoreId = globalVariable.getStoreId();
        name = getIntent().getStringExtra("prodName");  //Obtained when list item is selected
        category = getIntent().getStringExtra("categoryName");
        scanContent = getIntent().getStringExtra("scanContentsellitem");  //Obtained when barcode is scanned
        ivProdImage = (ImageView) findViewById(R.id.iv_sellitems_image);
        tvbarcode = (TextView) findViewById(R.id.tv_sellitems_barcode_prod_id);
        /*if (scanContent != null) {
            tvbarcode.setText(scanContent);
        } else if (id != null) {
            tvbarcode.setText(id);
        } else startActivity(new Intent(this, SellItemsMenu.class));*/
        tvname = (TextView) findViewById(R.id.tv_sellitems_prod_name);
        tvdesc = (TextView) findViewById(R.id.tv_sellitem_prod_desc);
        tvsellprice = (TextView) findViewById(R.id.tv_sellitems_prod_sellprice);
//        dbProAdapter = new DbProductAdapter(this);
        if (id != null) {
            // getProductfor(id);
            getProductDetailsOnServer(id, "", "");                 //Check this, not required
        } else if (scanContent != null) {
            id = scanContent;
            // getProductfor(id);
            getProductDetailsOnServer(id, "", "");
            //getProductDetailsOnLocal(id, "", "");
        } else if (name != null && category != null) {
            getProductDetailsOnServer("", category, name);
            //getProductDetailsOnLocal("", category, name);
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SellItemsMenu.class));
        }
        baddtocart = (Button) findViewById(R.id.b_sellitems_addtocart);
        baddtocart.setOnClickListener(this);
        // bEdit = (Button) findViewById(R.id.b_sellitems_edit);
        // bEdit.setOnClickListener(this);
    }

    private void getProductDetailsOnLocal(final String prodID, final String categoryName, final String productName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(SellItem.this);
            Product product;
            Bitmap image;
            byte prodImage[];

            String prodName
                    ,
                    prodDesc
                    ,
                    prodSellPrice;

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Product Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                product = dbProAdapter.getExistingProductDetails(categoryName, productName, prodID);
                if (product != null)
                    return "exists";
                else
                    return "notexists";
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response.equals("exists")) {
                    prodImage = ImageUtil.convertBase64ImagetoByteArrayImage(product.getProdImage());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (prodImage != null) {
                        bitmap = BitmapFactory.decodeByteArray(prodImage, 0, prodImage.length, options);
                        ivProdImage.setImageBitmap(bitmap);
                    }
                    tvname.setText(productName);
                    tvbarcode.setText(prodID);
                    tvdesc.setText(product.getDesc());
                    tvsellprice.setText(product.getSellprice());
                } else if (Response.equals("notexists")) {
                    Toast.makeText(getApplicationContext(), "Product not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

    private void getProductDetailsOnServer(final String prodID, final String categoryName, final String prodName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(SellItem.this);
            String prodDesc;
            byte[] prodImage;
            int prodQuantity;
            float prodSellPrice;

            @Override
            protected void onPreExecute() {
              /*  dialog.setMessage("Getting Product Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("PId", prodID));
                nameValuePairs.add(new BasicNameValuePair("CategoryName", categoryName));
                nameValuePairs.add(new BasicNameValuePair("Name", prodName));

                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_GET_PRODUCTDETAILS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        productId = jsonObject.getString("Id");
                        category = jsonObject.getString("Category");
                        name = jsonObject.getString("Name");
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
                //dialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (productId == null) {
                    Toast.makeText(getApplicationContext(), "No Product Found", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), SellItemsMenu.class));
                } else {
                    //set all textviews
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (prodImage != null)
                        bitmap = BitmapFactory.decodeByteArray(prodImage, 0, prodImage.length, options);
                    tvname.setText(name);
                    tvbarcode.setText(productId);
                    tvdesc.setText(prodDesc);
                    tvsellprice.setText(getResources().getString(R.string.rs) + " " + String.valueOf(prodSellPrice));
                    ivProdImage.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
          /*  case R.id.b_sellitems_edit:
                startEditItemActivity();
                break;*/

            case R.id.b_sellitems_addtocart:
                startSoldItemActivity();
                break;
        }
    }

    private void startSoldItemActivity() {
        Intent soldItemForm = new Intent(this, SoldItemForm.class);
        soldItemForm.putExtra("productid", productId);
        startActivity(soldItemForm);
    }

    private void startEditItemActivity() {
        Intent editItem = new Intent(this, EditItemForm.class);
        editItem.putExtra("prodid", id);
        editItem.putExtra("prodCategory", category);
        editItem.putExtra("prodName", name);
        startActivity(editItem);
    }
}
