package in.istore.bitblue.app.cart;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.CartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCartAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurchaseHistoryAdapter;
import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class Cart extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvcartitems;
    private TextView tvTotalPayAmnt;

    private ArrayList<CartItem> cartItemArrayList;
    private DbCartAdapter dbCartAdapter;
    private CartAdapter cartAdapter;
    private GlobalVariables globalVariable;
    private DbCustPurchaseHistoryAdapter custPurHistAdapter;
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
        custPurHistAdapter = new DbCustPurchaseHistoryAdapter(this);
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

                String itemid = null, name = null;
                int quantity = 0;
                float sellingprice = 0, totalprice = 0;
                long Mobile = 7666563796L, result = 0;

                for (CartItem cartItem : cartItemArrayList) {
                    itemid = cartItem.getItemId();
                    name = cartItem.getItemName();
                    quantity = cartItem.getItemSoldQuantity();
                    sellingprice = cartItem.getItemSellPrice();
                    totalprice = cartItem.getItemTotalAmnt();
                    Mobile = 7666563796L;
                    result = dbCartAdapter.insertEachCartItem(itemid, Mobile, name, quantity, sellingprice, totalprice, StaffId);
                }
                if (result <= 0) {
                    Toast.makeText(this, "Insert Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cart is Sold", Toast.LENGTH_SHORT).show();
                    custPurHistAdapter.addToSoldHistory(itemid, Mobile, name, quantity, sellingprice, totalprice, StaffId);
                    dbCartAdapter.emptyCart();
                    dbCartAdapter.clearAllPurchases();
                    cartItemArrayList.clear();
                    cartAdapter = new CartAdapter(this, cartItemArrayList);
                    lvcartitems.setAdapter(cartAdapter);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
