package in.istore.bitblue.app.soldItems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.listMyStock.Product;

public class ViewItem extends ActionBarActivity {
    private Toolbar toolbar;

    private EditText tvbarcode, tvname, tvdesc, tvquantity, tvprice;
    private ImageView ivProdImage;

    private String id, name, desc, quantity, price;
    private byte[] byteImage;
    private Bitmap bitmap;

    private DbProductAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
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

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        cursorAdapter = new DbProductAdapter(this);
        if (id != null) {
            getProductfor(id);
        } else {
            Toast.makeText(this, "Nothing was found", Toast.LENGTH_SHORT).show();
        }

        ivProdImage = (ImageView) findViewById(R.id.iv_viewitem_image);
        if (bitmap != null) {
            ivProdImage.setImageBitmap(bitmap);
        }

        tvbarcode = (EditText) findViewById(R.id.et_viewitem_barcode_prod_id);
        if (id != null) {
            tvbarcode.setText(id);
        } else Toast.makeText(this, "Id not found", Toast.LENGTH_SHORT).show();

        tvname = (EditText) findViewById(R.id.et_viewitem_prod_name);
        tvname.setText(name);

        tvdesc = (EditText) findViewById(R.id.et_viewitem_prod_desc);
        tvdesc.setText(desc);

        tvquantity = (EditText) findViewById(R.id.et_viewitem_prod_quantity);
        tvquantity.setText(quantity);

        tvprice = (EditText) findViewById(R.id.et_viewitem_prod_price);
        tvprice.setText(price);
    }

    private void getProductfor(String id) {
        Product product = cursorAdapter.getProductDetails(id);
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
}
