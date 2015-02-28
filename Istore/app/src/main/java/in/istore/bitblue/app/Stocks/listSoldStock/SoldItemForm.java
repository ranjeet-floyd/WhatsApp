package in.istore.bitblue.app.Stocks.listSoldStock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import in.istore.bitblue.app.Stocks.listStock.ListMyStock;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbOutOfStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.ImageUtil;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.API;

public class SoldItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText etbarcode, etname, etdesc, etquantity, etprice;
    private ImageView ivProdImage;
    private Button bSellItem, bInc, bDec;

    private DbProductAdapter dbProAdapter;
    private DbSoldItemAdapter dbSolItmAdapter;
    private DbCartAdapter dbCartAdapter;
    private DbOutOfStockAdapter dbOutOfStockAdapter;
    private DbSuppAdapter dbSuppAdapter;
    private GlobalVariables globalVariable;

    private Bitmap bitmap;
    private String id, name, desc, sellQuantity, Key, UserType, Status;
    private int quantity, maxlimit, minlimit = 0, StoreId;
    private int prodQuantity;
    private float prodSellPrice;
    private long suppMobile;
    private byte[] byteImage;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_item_form);
        id = getIntent().getStringExtra("productid");
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SELL ITEM FORM");
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

        dbProAdapter = new DbProductAdapter(this);
        dbSolItmAdapter = new DbSoldItemAdapter(this);
        dbCartAdapter = new DbCartAdapter(this);
        dbOutOfStockAdapter = new DbOutOfStockAdapter(this);
        dbSuppAdapter = new DbSuppAdapter(this);
        ivProdImage = (ImageView) findViewById(R.id.iv_solditem_image);
        etbarcode = (EditText) findViewById(R.id.et_solditem_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_solditem_prod_name);
        etdesc = (EditText) findViewById(R.id.et_solditem_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_solditem_prod_quantity);
        etprice = (EditText) findViewById(R.id.et_solditem_prod_price);

        bSellItem = (Button) findViewById(R.id.b_solditem_sell);
        bSellItem.setOnClickListener(this);

        bInc = (Button) findViewById(R.id.b_solditem_inc);
        bInc.setOnClickListener(this);

        bDec = (Button) findViewById(R.id.b_solditem_dec);
        bDec.setOnClickListener(this);

        if (id != null)
            getItemInfo(id);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_solditem_sell:
                name = etname.getText().toString();

                int quant;
                id = etbarcode.getText().toString();
                quant = quantity;
                if (Check.ifNull(etquantity.getText().toString())) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (quant > maxlimit) {
                    //   limitReached();
                    break;
                } else if (Check.ifNull(etprice.getText().toString())) {
                    etprice.setHint("Field Required");
                    etprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {
                    String soldQuan = etquantity.getText().toString();
                    int soldQuantity;
                    try {
                        soldQuantity = Integer.parseInt(soldQuan);
                    } catch (NumberFormatException nfe) {
                        soldQuantity = 0;
                    }

                    if (soldQuantity > prodQuantity) {
                        etquantity.setText(String.valueOf(prodQuantity));
                        Toast.makeText(this, "You Cannot Sell more than " + prodQuantity + " items", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    sellQuantity = etquantity.getText().toString();
                    addSoldItemToCart();

                   /* int remQuantity = maxlimit - soldQuantity;
                    float totalAmount = soldQuantity * sellprice;
                    long soldret = dbSolItmAdapter.insertSoldItemQuantityDetail(id, byteImage, name, soldQuantity, remQuantity, sellprice);
                    long cartres = 0;
                    if (isAlreadyinCart(id)) {

                        //update quantity and total amount
                        cartres = dbCartAdapter.updateCartItemQuantityandAmount(id, name, soldQuantity, totalAmount);
                    } else {
                        cartres = dbCartAdapter.addItemToCart(id, name, soldQuantity, sellprice, totalAmount);
                    }


                    //update remaining quantity in product table and totalSalesAmount in Staff Table
                    long prodret = dbProAdapter.updateProductQuantity(id, remQuantity);
                    if (isbelowStock(id)) {
                        int stockexistsres = 0;
                        long stockres = 0;
                        if (isAlreadyinOutOfStockList(id)) {
                            stockexistsres = dbOutOfStockAdapter.updateOutOfStockItem(id, remQuantity);
                            if (stockexistsres <= 0) {
                                Toast.makeText(this, "update out of stock item failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            stockres = dbOutOfStockAdapter.addtoOutOfStockList(id, name, remQuantity, suppMobile);
                            if (stockres <= 0) {
                                Toast.makeText(this, "add out of stock item failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    if (prodret < 0 || soldret <= 0 || cartres < 0) {
                        Toast.makeText(this, "Not Updated ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Cart.class));
                    }*/
                }
                break;
            case R.id.b_solditem_inc:
                int quantinc;
                try {
                    quantinc = Integer.parseInt(etquantity.getText().toString());
                    ++quantinc;
                } catch (NumberFormatException nfe) {
                    quantinc = 0;
                }
                if (quantinc > maxlimit) {
                    break;
                } else {
                    etquantity.setText(String.valueOf(quantinc));
                }
                break;
            case R.id.b_solditem_dec:
                int quantdec;
                try {
                    quantdec = Integer.parseInt(etquantity.getText().toString());
                    --quantdec;
                } catch (NumberFormatException nfe) {
                    quantdec = 0;
                }
                if (quantdec <= minlimit) {
                    break;
                } else {
                    etquantity.setText(String.valueOf(quantdec));
                }
                break;
        }
    }

    private void addSoldItemToCart() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(SoldItemForm.this);

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
                nameValuePairs.add(new BasicNameValuePair("Cid", id));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("Name", name));
                nameValuePairs.add(new BasicNameValuePair("Quantity", sellQuantity));
                nameValuePairs.add(new BasicNameValuePair("Sellingprice", String.valueOf(prodSellPrice)));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_TO_CART, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("Status");
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
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Failed to add", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Cart.class));
                }
            }
        }.execute();
    }

    private void getItemInfo(final String id) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(SoldItemForm.this);
            String prodId
                    ,
                    prodName
                    ,
                    prodDesc;
            byte[] prodImage;

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
                    etbarcode.setText(prodId);
                    etname.setText(prodName);
                    etdesc.setText(prodDesc);
                    etquantity.setText(String.valueOf(prodQuantity));
                    etprice.setText(String.valueOf(prodSellPrice));
                    ivProdImage.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    private boolean isbelowStock(String id) {
        return dbOutOfStockAdapter.isProductBelowStock(id);
    }

    private boolean isAlreadyinCart(String id) {
        return dbCartAdapter.isAlreadyinCart(id);
    }

    private boolean isAlreadyinOutOfStockList(String id) {
        return dbOutOfStockAdapter.isProductAlreadyOutOfStock(id);
    }

    private void limitReached() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Max Stock Limit Reached");
        builder.setMessage("You cannot add anymore item unit");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ListMyStock.class));
    }
}
