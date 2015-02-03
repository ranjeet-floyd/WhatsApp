package in.istore.bitblue.app.cart;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.invoice.Invoice;
import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.CartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.databaseAdapter.DbTotSaleAmtByDateAdapter;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class Cart extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvcartitems;
    private TextView tvTotalPayAmnt;

    private ArrayList<CartItem> cartItemArrayList;
    private DbCartAdapter dbCartAdapter;
    private DbCustAdapter dbCustAdapter;
    private CartAdapter cartAdapter;
    private GlobalVariables globalVariable;
    private DbCustPurHistAdapter custPurHistAdapter;
    private DbTotSaleAmtByDateAdapter dbTotSaleAmtByDateAdapter;
    private long Mobile;
    private float totalPayAmount;
    private long StaffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        globalVariable = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("CART");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        StaffId = globalVariable.getStaffId();
        lvcartitems = (ListView) findViewById(R.id.lv_cart_itemlist);
        tvTotalPayAmnt = (TextView) findViewById(R.id.tv_cart_totalpayamount);

        dbCartAdapter = new DbCartAdapter(this);
        dbCustAdapter = new DbCustAdapter(this);
        custPurHistAdapter = new DbCustPurHistAdapter(this);
        dbTotSaleAmtByDateAdapter = new DbTotSaleAmtByDateAdapter(this);

        cartItemArrayList = dbCartAdapter.getAllCartItems();
        totalPayAmount = dbCartAdapter.getTotalPayAmount();
        if (totalPayAmount != 0) {
            tvTotalPayAmnt.setText(String.valueOf(totalPayAmount));
        }
        if (cartItemArrayList != null) {
            cartAdapter = new CartAdapter(this, cartItemArrayList);
            lvcartitems.setAdapter(cartAdapter);
        } else Toast.makeText(this, "Cart is Empty", Toast.LENGTH_SHORT).show();
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
        dialog.setContentView(R.layout.add_cust_mobile_dialog);
        dialog.setTitle("Customer Mobile Number");

        final EditText etMobile = (EditText) dialog.findViewById(R.id.et_addcust_dialog_mobile);
        Button add = (Button) dialog.findViewById(R.id.b_addcust_dialog_addMobile);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    Mobile = Long.parseLong(etMobile.getText().toString());
                    totalPayAmount = Float.parseFloat(tvTotalPayAmnt.getText().toString());
                    showSoldItemsToCustomer();
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showSoldItemsToCustomer() {
        String itemid = null, name = null;
        int quantity = 0;
        float sellingprice = 0, totalprice = 0;
        long result = 0;

        for (CartItem cartItem : cartItemArrayList) {
            itemid = cartItem.getItemId();
            name = cartItem.getItemName();
            quantity = cartItem.getItemSoldQuantity();
            sellingprice = cartItem.getItemSellPrice();
            totalprice = cartItem.getItemTotalAmnt();
            result = dbCartAdapter.insertEachCartItem(itemid, Mobile, name, quantity, sellingprice, totalprice, StaffId);
        }
        if (result <= 0) {
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
            /*dbCartAdapter.emptyCart();
            dbCartAdapter.clearAllPurchases();
            cartItemArrayList.clear();
            cartAdapter = new CartAdapter(this, cartItemArrayList);
            lvcartitems.setAdapter(cartAdapter);*/

            Intent invoice = new Intent(this, Invoice.class);
            invoice.putExtra("Mobile", Mobile);
            startActivity(invoice);
        }

    }

}
