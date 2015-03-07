package in.istore.bitblue.app.cart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import in.istore.bitblue.app.adapters.CartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbTotSaleAmtByDateAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.invoice.Invoice;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.Store;

public class Cart extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvcartitems;
    private TextView tvTotalPayAmnt;
    private ProgressBar progressBar;

    private ArrayList<CartItem> cartItemArrayList = new ArrayList<CartItem>();
    private DbCartAdapter dbCartAdapter;
    private DbCustAdapter dbCustAdapter;
    private DbStaffAdapter dbStaffAdapter;
    private DbSoldItemAdapter dbSoldItemAdapter;
    private DbProductAdapter dbProductAdapter;
    private DbCustPurHistAdapter dbCustPurHistAdapter;

    private CartAdapter cartAdapter;
    private GlobalVariables globalVariable;
    private DbCustPurHistAdapter custPurHistAdapter;
    private DbTotSaleAmtByDateAdapter dbTotSaleAmtByDateAdapter;
    private SharedPreferences.Editor prefCustMobile;
    public static String CUST_MOBILE = "custmobile";

    private int optype, StoreId, prodQuantity;
    private long Mobile, PersonId, InVoiceNum;
    private float totalPayAmount, prodSellPrice, prodTotalPrice;
    private String Key, TotPayAmnt, UserType, OpType, DeliveryAddress, prodId, prodName, soldDate, Status;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private CartItem cartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("CART");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();

        findViewById(R.id.totalpayAmountlayout).setVisibility(View.GONE);
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
            PersonId = globalVariable.getAdminId();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
            PersonId = globalVariable.getStaffId();
        }
        StoreId = globalVariable.getStoreId();

        prefCustMobile = getSharedPreferences(CUST_MOBILE, MODE_PRIVATE).edit();
        lvcartitems = (ListView) findViewById(R.id.lv_cart_itemlist);
        tvTotalPayAmnt = (TextView) findViewById(R.id.tv_cart_totalpayamount);

        dbCartAdapter = new DbCartAdapter(this);
        dbCustAdapter = new DbCustAdapter(this);
        dbSoldItemAdapter = new DbSoldItemAdapter(this);
        custPurHistAdapter = new DbCustPurHistAdapter(this);
        dbTotSaleAmtByDateAdapter = new DbTotSaleAmtByDateAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);
        dbProductAdapter = new DbProductAdapter(this);

        //cartItemArrayList = dbCartAdapter.getAllCartItems();
        getAllCartItemsOnServer();
        //getAllCartItemsOnLocal();

        // totalPayAmount = dbCartAdapter.getTotalPayAmount();
       /* if (totalPayAmount != 0) {
            tvTotalPayAmnt.setText(String.valueOf(totalPayAmount));
        }
        if (cartItemArrayList != null) {
            cartAdapter = new CartAdapter(this, cartItemArrayList);
            lvcartitems.setAdapter(cartAdapter);
        } else Toast.makeText(this, "Cart is Empty", Toast.LENGTH_SHORT).show();*/
    }

    private void getAllCartItemsOnLocal() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Cart.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Cart Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                cartItemArrayList = dbCartAdapter.getAllCartItems();
                TotPayAmnt = getTotalPayAmount();
                return "";
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                tvTotalPayAmnt.setText(TotPayAmnt);
                if (cartItemArrayList != null && cartItemArrayList.size() > 0) {
                    cartAdapter = new CartAdapter(getApplicationContext(), cartItemArrayList);
                    lvcartitems.setAdapter(cartAdapter);
                } else
                    Toast.makeText(getApplicationContext(), "Cart is Empty", Toast.LENGTH_LONG).show();
            }

            private String getTotalPayAmount() {
                float totalAmount = 0;
                for (CartItem cartItem : cartItemArrayList) {
                    totalAmount += cartItem.getItemTotalAmnt();
                }
                return String.valueOf(totalAmount);
            }

        }.execute();
    }

    private void getAllCartItemsOnServer() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Cart.this);

            @Override
            protected void onPreExecute() {
              /*  dialog.setMessage("Getting Cart Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_CART_ITEMS, nameValuePairs);
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
                    Toast.makeText(getApplicationContext(), "No Items in Cart", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            prodId = jsonObject.getString("Cid");
                            prodName = jsonObject.getString("Name");
                            prodQuantity = Integer.parseInt(jsonObject.getString("Quantity"));
                            prodSellPrice = Float.parseFloat(jsonObject.getString("Sellingprice"));
                            prodTotalPrice = Float.parseFloat(jsonObject.getString("Totalprice"));
                            if (prodId == null || prodId.equals("null")) {
                                break;
                            }

                            cartItem = new CartItem();
                            cartItem.setItemId(prodId);
                            cartItem.setItemName(prodName);
                            cartItem.setItemSoldQuantity(prodQuantity);
                            cartItem.setItemSellPrice(prodSellPrice);
                            cartItem.setItemTotalAmnt(prodTotalPrice);
                            totalPayAmount += cartItem.getItemTotalAmnt();
                            cartItemArrayList.add(cartItem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cartItemArrayList != null && cartItemArrayList.size() > 0) {
                        findViewById(R.id.totalpayAmountlayout).setVisibility(View.VISIBLE);
                        tvTotalPayAmnt.setText(getResources().getString(R.string.rs) + " " + String.valueOf(totalPayAmount));
                        cartAdapter = new CartAdapter(getApplicationContext(), cartItemArrayList);
                        lvcartitems.setAdapter(cartAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Product Available", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    }
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sell:
                showAddCustMobileDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddCustMobileDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_cust_mobile_address_dialog);
        dialog.setTitle("Customer Details");

        final EditText etMobile = (EditText) dialog.findViewById(R.id.et_addcust_dialog_mobile);
        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rg_custdetails_radiogroup);
        final RadioButton rbSell = (RadioButton) dialog.findViewById(R.id.rb_custdetails_sell);
        final RadioButton rbDeliver = (RadioButton) dialog.findViewById(R.id.rb_custdetails_delivery);
        final EditText etDeliver = (EditText) dialog.findViewById(R.id.et_custdetails_deliveryaddress);
        etDeliver.setVisibility(View.GONE);
        Button add = (Button) dialog.findViewById(R.id.b_addcust_dialog_addMobile);
        rbSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDeliver.setVisibility(View.GONE);
            }
        });
        rbDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etDeliver.setVisibility(View.VISIBLE);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    Mobile = Long.parseLong(etMobile.getText().toString());
                    optype = rg.getCheckedRadioButtonId();
                    DeliveryAddress = etDeliver.getText().toString();
                    if (optype == R.id.rb_custdetails_delivery) {
                        if (DeliveryAddress.equals("")) {
                            etDeliver.setText("");
                            etDeliver.setHint("Delivery Address");
                            etDeliver.setHintTextColor(getResources().getColor(R.color.material_red_300));
                            return;
                        } else OpType = rbDeliver.getText().toString();

                    } else if (optype == R.id.rb_custdetails_sell) {
                        OpType = rbSell.getText().toString();
                    }
                    if (DeliveryAddress != null)
                        globalVariable.setDeliveryAddress(DeliveryAddress);
                    else {
                        globalVariable.setDeliveryAddress("");
                        DeliveryAddress = "";
                    }
                    prefCustMobile.putLong("custMobile", Mobile).commit();
                    //totalPayAmount = Float.parseFloat(tvTotalPayAmnt.getText().toString());
                    Date date = new Date();
                    soldDate = DateUtil.convertToStringDateOnly(date);
                    InVoiceNum = Store.generateInVoiceNumber();
                    addToDb();
                    showSoldItemsToCustomer();
                } catch (NumberFormatException nfe) {
                    etDeliver.setText("");
                    etDeliver.setHint("Mobile Number");
                    etDeliver.setHintTextColor(getResources().getColor(R.color.material_red_300));
                    return;
                }
            }
        });

        dialog.show();
    }

    private void addToDb() {
        for (CartItem cartItem : cartItemArrayList) {
            String prodId = cartItem.getItemId();
            String prodName = cartItem.getItemName();
            float prodSellPrice = cartItem.getItemSellPrice();
            int prodQuantity = cartItem.getItemSoldQuantity();
            float prodTotalPrice = cartItem.getItemTotalAmnt();
            addSoldProductToDbTablesOnServer(prodId, prodName, prodSellPrice, prodQuantity);
            //addSoldProductToDbTablesOnLocal(prodId, prodName, prodSellPrice, prodQuantity);
        }
    }

    private void addSoldProductToDbTablesOnLocal(final String prodId, final String prodName, final float prodSellPrice,
                                                 final int prodQuantity) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Cart.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                addToSoldItemTable();
                addToCustPurchaseTable();
                addToCustPurchaseAmountTable();
                addToTotalStockSales();
                return "";
            }

            private void addToSoldItemTable() {
                String Image = dbProductAdapter.getProductImage(prodId);
                dbSoldItemAdapter.insertSoldItemQuantityDetail(prodId, Image, prodName, String.valueOf(prodSellPrice),
                        String.valueOf(prodQuantity), String.valueOf(Mobile), OpType, DeliveryAddress, String.valueOf(StoreId));
            }

            private void addToCustPurchaseTable() {
                custPurHistAdapter.insertIntoCustomerPurchase(prodId, String.valueOf(Mobile), prodName,
                        String.valueOf(prodQuantity), String.valueOf(prodSellPrice), String.valueOf(prodTotalPrice),
                        String.valueOf(PersonId), String.valueOf(StoreId));
            }

            private void addToCustPurchaseAmountTable() {
                dbCustAdapter.insertCustPurchaseInfo(String.valueOf(Mobile), TotPayAmnt, String.valueOf(StoreId));
            }

            private void addToTotalStockSales() {
                dbTotSaleAmtByDateAdapter.insertTodaySales(TotPayAmnt, String.valueOf(StoreId));
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
            }
        }.execute();
    }

    private void addSoldProductToDbTablesOnServer(final String prodId, final String prodName,
                                                  final float prodSellPrice, final int prodQuantity) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Cart.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Name", prodName));
                nameValuePairs.add(new BasicNameValuePair("ItemID", prodId));
                nameValuePairs.add(new BasicNameValuePair("SellPrice", String.valueOf(prodSellPrice)));
                nameValuePairs.add(new BasicNameValuePair("Soldquantity", String.valueOf(prodQuantity)));
                nameValuePairs.add(new BasicNameValuePair("SoldDate", soldDate));
                //nameValuePairs.add(new BasicNameValuePair("SellBy", "0"));
                // nameValuePairs.add(new BasicNameValuePair("Image", ""));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("CusMobile", String.valueOf(Mobile)));
                nameValuePairs.add(new BasicNameValuePair("OpType", OpType));
                nameValuePairs.add(new BasicNameValuePair("ID", String.valueOf(PersonId)));
                nameValuePairs.add(new BasicNameValuePair("Key", Key));
                nameValuePairs.add(new BasicNameValuePair("DeliveryAdd", DeliveryAddress));
                nameValuePairs.add(new BasicNameValuePair("InvoiceNum", String.valueOf(InVoiceNum)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_SOLDITEM, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("status");
                        return Status;
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
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Failed to add Product", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void showSoldItemsToCustomer() {
        String itemid, name;
        int quantity;
        float sellingprice, totalprice;
        long result = 0;
 /*       for (CartItem cartItem : cartItemArrayList) {
            itemid = cartItem.getItemId();
            name = cartItem.getItemName();
            quantity = cartItem.getItemSoldQuantity();
            sellingprice = cartItem.getItemSellPrice();
            totalprice = cartItem.getItemTotalAmnt();
            result = dbCartAdapter.insertEachCartItem(itemid, Mobile, name, quantity, sellingprice, totalprice, StaffId);
        }*/
       /* if (result <= 0) {
            Toast.makeText(this, "Insert Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cart is Sold", Toast.LENGTH_SHORT).show();
            for (CartItem cartItem : cartItemArrayList) {
                itemid = cartItem.getItemId();
                name = cartItem.getItemName();
                quantity = cartItem.getItemSoldQuantity();
                sellingprice = cartItem.getItemSellPrice();
                totalprice = cartItem.getItemTotalAmnt();
                long resulthist = custPurHistAdapter.addToSoldHistory(itemid, Mobile, name, quantity, sellingprice, totalprice, StaffId);
            }
            long res = dbCustAdapter.insertCustPurchaseInfo(Mobile, totalPayAmount);
            if (res <= 0) {
                Toast.makeText(getApplicationContext(), "Insert purchase fail", Toast.LENGTH_SHORT).show();
            }
            long res1 = dbTotSaleAmtByDateAdapter.insertTodaySales(totalPayAmount);
            if (res1 <= 0) {
                Toast.makeText(getApplicationContext(), "Insert Total Amount Failed", Toast.LENGTH_SHORT).show();
            }

            long staffsalesres = dbStaffAdapter.updateStaffSales(StaffId, totalPayAmount);
            if (staffsalesres <= 0) {
                Toast.makeText(getApplicationContext(), "Update Total Staff Sales Failed", Toast.LENGTH_SHORT).show();
            }*/

           /* dbCartAdapter.emptyCart();
            dbCartAdapter.clearAllPurchases();
            cartItemArrayList.clear();
            cartAdapter = new CartAdapter(this, cartItemArrayList);
            lvcartitems.setAdapter(cartAdapter);*/

        Intent invoice = new Intent(this, Invoice.class);
        invoice.putExtra("Mobile", Mobile);
        invoice.putExtra("InVoiceNumber", InVoiceNum);

        startActivity(invoice);
    }
}
