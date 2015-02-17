package in.istore.bitblue.app.Stocks.sellItem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.listSoldStock.SoldItemForm;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class SellItem extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;

    private Button bEdit, baddtocart;
    private TextView tvbarcode, tvname, tvsellprice;
    private TextView tvdesc;
    private ImageView ivProdImage;

    private GlobalVariables globalVariable;
    private String id, scanContent, name, desc;
    private float sellprice;
    private byte[] byteImage;
    private Bitmap bitmap;

    private DbProductAdapter dbProAdapter;

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
        toolTitle.setText("Product Details");
    }

    private void initViews() {

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        scanContent = getIntent().getStringExtra("scanContentsellitem");  //Obtained when barcode is scanned
        dbProAdapter = new DbProductAdapter(this);
        if (id != null) {
            getProductfor(id);
        } else if (scanContent != null) {
            id = scanContent;
            getProductfor(id);
        } else {
            startActivity(new Intent(this, SellItemsMenu.class));
        }

        baddtocart = (Button) findViewById(R.id.b_sellitems_addtocart);
        baddtocart.setOnClickListener(this);

        bEdit = (Button) findViewById(R.id.b_sellitems_edit);
        bEdit.setOnClickListener(this);

        ivProdImage = (ImageView) findViewById(R.id.iv_sellitems_image);
        if (bitmap != null) {
            ivProdImage.setImageBitmap(bitmap);
        }

        tvbarcode = (TextView) findViewById(R.id.tv_sellitems_barcode_prod_id);
        if (scanContent != null) {
            tvbarcode.setText(scanContent);
        } else if (id != null) {
            tvbarcode.setText(id);
        } else startActivity(new Intent(this, SellItemsMenu.class));


        tvname = (TextView) findViewById(R.id.tv_sellitems_prod_name);
        tvname.setText(name);

        tvdesc = (TextView) findViewById(R.id.tv_sellitem_prod_desc);
        tvdesc.setText(desc);

        tvsellprice = (TextView) findViewById(R.id.tv_sellitems_prod_sellprice);
        tvsellprice.setText(String.valueOf(sellprice));
    }

    private void getProductfor(String id) {
        Product product = dbProAdapter.getProductDetails(id);
        if (product != null) {
            name = product.getName();
            desc = product.getDesc();
            sellprice = product.getSellingPrice();
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

            case R.id.b_sellitems_addtocart:
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
