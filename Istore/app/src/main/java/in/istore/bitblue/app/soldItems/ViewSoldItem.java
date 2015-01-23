package in.istore.bitblue.app.soldItems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.SoldHistoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.listMyStock.Product;

public class ViewSoldItem extends ActionBarActivity {
    private Toolbar toolbar;

    private TextView tvbarcode, tvname, tvdesc, tvsoldquantity, tvremquantity;
    private ImageView ivProdImage;
    private ListView lvsoldhist;

    private String id;
    private byte[] byteImage;
    private Bitmap bitmap;
    private ArrayList<Product> soldhistList;

    private DbSoldItemAdapter dbsolAdapter;
    private SoldHistoryAdapter soldhistAdapter;
    private Product product;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sold_item);
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

        ivProdImage = (ImageView) findViewById(R.id.iv_viewsolditem_image);

        tvbarcode = (TextView) findViewById(R.id.tv_viewsolditem_barcode_prod_id);
        tvname = (TextView) findViewById(R.id.tv_viewsolditem_prod_name);
        tvdesc = (TextView) findViewById(R.id.tv_viewsolditem_desc);
        tvsoldquantity = (TextView) findViewById(R.id.tv_viewsolditem_soldquan);
        tvremquantity = (TextView) findViewById(R.id.tv_viewsolditem_availquan);

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        dbsolAdapter = new DbSoldItemAdapter(this);


        if (id != null) {
            getSoldDetailsfor(id);
            soldhistList = getAllSoldDetailsfor(id);
            if (soldhistList != null) {
                soldhistAdapter = new SoldHistoryAdapter(this, soldhistList);
                lvsoldhist = (ListView) findViewById(R.id.lv_viewsolditem_soldhist);
                lvsoldhist.setAdapter(soldhistAdapter);
            }
        } else {
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show();
        }

    }

    private ArrayList<Product> getAllSoldDetailsfor(String id) {
        return dbsolAdapter.getAllSoldDetailsfor(id);
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
            tvbarcode.setText(id);
            tvname.setText(product.getName());
            tvdesc.setText(product.getDesc());
            tvsoldquantity.setText(product.getSoldQuantity());
            tvremquantity.setText(product.getRemQuantity());
        }

    }
}
