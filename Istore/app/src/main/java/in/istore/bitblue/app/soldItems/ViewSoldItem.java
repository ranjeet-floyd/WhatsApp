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

import java.io.ByteArrayInputStream;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.listMyStock.Product;

public class ViewSoldItem extends ActionBarActivity {
    private Toolbar toolbar;

    private EditText etbarcode, etname, etdesc, etsoldquantity, etremquantity, etsellprice;
    private ImageView ivProdImage;

    private String id;
    private byte[] byteImage;
    private Bitmap bitmap;

    private DbSoldItemAdapter dbsolAdapter;
    private Product product;

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

        product = new Product();

        ivProdImage = (ImageView) findViewById(R.id.iv_viewitem_image);

        etbarcode = (EditText) findViewById(R.id.et_viewitem_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_viewitem_prod_name);
        etdesc = (EditText) findViewById(R.id.et_viewitem_prod_desc);
        etsoldquantity = (EditText) findViewById(R.id.et_viewitem_prod_soldquantity);
        etremquantity = (EditText) findViewById(R.id.et_viewitem_prod_remquantity);
        etsellprice = (EditText) findViewById(R.id.et_viewitem_prod_sellprice);

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        dbsolAdapter = new DbSoldItemAdapter(this);

        if (id != null) {
            getSoldDetailsfor(id);
        } else {
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show();
        }

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
            etbarcode.setText(id);
            etname.setText(product.getName());
            etdesc.setText(product.getDesc());
            etsoldquantity.setText(product.getSoldQuantity());
            etremquantity.setText(product.getRemQuantity());
            etsellprice.setText(product.getSellPrice());
        }

    }
}
