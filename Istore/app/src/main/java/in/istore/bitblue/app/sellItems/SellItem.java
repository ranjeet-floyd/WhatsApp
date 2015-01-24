package in.istore.bitblue.app.sellItems;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.QuantityAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbQuantityAdapter;
import in.istore.bitblue.app.listStock.Product;
import in.istore.bitblue.app.soldItems.SoldItemForm;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class SellItem extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;

    private Button bEdit, bSell;
    private TextView tvbarcode, tvname, tvquantity, tvprice;
    private ExpandableTextView etvdesc;
    private ImageView ivProdImage;
    private ListView lvquanthist;

    private SparseBooleanArray mCollapsedStatus;
    private String scanContent;
    private GlobalVariables globalVariable;
    private String id, name, desc, quantity, price;
    private byte[] byteImage;
    private Bitmap bitmap;

    private ArrayList<Product> quantityList;
    private DbProductAdapter dbProAdapter;
    private DbQuantityAdapter dbQuanAdapter;
    private QuantityAdapter quantityAdapter;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SELL ITEM FORM");
    }

    private void initViews() {

        mCollapsedStatus = new SparseBooleanArray();
        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        scanContent = getIntent().getStringExtra("scanContentsellitem");  //Obtained when barcode is scanned
        dbProAdapter = new DbProductAdapter(this);
        dbQuanAdapter = new DbQuantityAdapter(this);
        if (id != null) {
            getProductfor(id);
            quantityList = getQuantityDetails(id);
        } else if (scanContent != null) {
            getProductvia(scanContent);
            quantityList = getQuantityDetails(scanContent);
        } else {
            Toast.makeText(this, "Nothing was found", Toast.LENGTH_SHORT).show();
        }

        bSell = (Button) findViewById(R.id.b_sellitems_submit);
        bSell.setOnClickListener(this);

        bEdit = (Button) findViewById(R.id.b_sellitems_edit);
        bEdit.setOnClickListener(this);

        ivProdImage = (ImageView) findViewById(R.id.iv_sellitems_image);
        if (bitmap != null) {
            ivProdImage.setImageBitmap(bitmap);
        }

        tvbarcode = (TextView) findViewById(R.id.et_sellitems_barcode_prod_id);
        if (scanContent != null) {
            tvbarcode.setText(scanContent);
        } else if (id != null) {
            tvbarcode.setText(id);
        } else Toast.makeText(this, "Id not found", Toast.LENGTH_SHORT).show();

        tvname = (TextView) findViewById(R.id.et_sellitems_prod_name);
        tvname.setText(name);

        etvdesc = (ExpandableTextView) findViewById(R.id.expand_text_view);
        etvdesc.setText(desc, mCollapsedStatus, 0);

        tvquantity = (TextView) findViewById(R.id.tv_sellitems_quantity);
        tvquantity.setText(quantity);

        tvprice = (TextView) findViewById(R.id.et_sellitems_prod_price);
        tvprice.setText(price);

        lvquanthist = (ListView) findViewById(R.id.lv_sellitems_quanthist);
        if (quantityList != null) {
            quantityAdapter = new QuantityAdapter(this, quantityList);
            lvquanthist.setAdapter(quantityAdapter);
        }
    }

    private ArrayList<Product> getQuantityDetails(String id) {
        return dbQuanAdapter.getQuatityDetailsfor(id);
    }

    private void getProductvia(String scanContent) {
        Product product = dbProAdapter.getProductDetails(scanContent);
        if (product != null) {
            name = product.getName();
            desc = product.getDesc();
            quantity = product.getQuantity();
            price = product.getPrice();
            byteImage = product.getImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
        }
    }

    private void getProductfor(String id) {
        Product product = dbProAdapter.getProductDetails(id);
        if (product != null) {
            name = product.getName();
            desc = product.getDesc();
            quantity = product.getQuantity();
            price = product.getPrice();
            byteImage = product.getImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (byteImage != null)
                bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_sellitems_edit:
                startEditItemActivity();
                break;

            case R.id.b_sellitems_submit:
                startSoldItemActivity();
                break;
        }
    }

    private void startSoldItemActivity() {
        Intent soldItemForm = new Intent(this, SoldItemForm.class);
        soldItemForm.putExtra("productid", id);
        startActivity(soldItemForm);
    }

    private void startEditItemActivity() {
        Intent editItem = new Intent(this, EditItemForm.class);
        editItem.putExtra("prodid", id);
        startActivity(editItem);
    }
}
