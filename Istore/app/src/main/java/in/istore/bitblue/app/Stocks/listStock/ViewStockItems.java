package in.istore.bitblue.app.Stocks.listStock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.addItem.AddItemsMenu;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbQuantityAdapter;
import in.istore.bitblue.app.pojo.Product;

public class ViewStockItems extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etbarcode, etname, etquantity;
    private ImageView ivProdImage;
    private Button bBack, bUpdate;

    private String id;
    private int origquantity;
    private byte[] byteImage;
    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private DbQuantityAdapter dbquanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stock_items);
        id = getIntent().getStringExtra("barcode");
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("VIEW STOCK ITEM");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        dbAdapter = new DbProductAdapter(this);
        dbquanAdapter = new DbQuantityAdapter(this);
        Product product;

        bBack = (Button) findViewById(R.id.b_viewstockitem_back);
        bBack.setOnClickListener(this);
        bUpdate = (Button) findViewById(R.id.b_viewstockitem_update);
        bUpdate.setOnClickListener(this);

        ivProdImage = (ImageView) findViewById(R.id.iv_viewstockitem_image);
        etbarcode = (EditText) findViewById(R.id.et_viewstockitem_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_viewstockitem_prod_name);
        etquantity = (EditText) findViewById(R.id.et_viewstockitem_prod_quantity);

        if ((id != null) && (!id.equals(""))) {
            product = getProductDetails(id);
            if (product != null) {
                origquantity = product.getQuantity();
                etbarcode.setText(id);
                etname.setText(product.getName());
                etquantity.setText(String.valueOf(origquantity));
                byteImage = product.getImage();
                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
                if (bitmap != null) {
                    ivProdImage.setImageBitmap(bitmap);
                }
            }
        }
    }

    public Product getProductDetails(String id) {
        return dbAdapter.getExistingProductDetails(id);
    }

    @Override
    public void onClick(View button) {

        switch (button.getId()) {
            case R.id.b_viewstockitem_back:
                startActivity(new Intent(this, AddItemsMenu.class));
                break;
            case R.id.b_viewstockitem_update:
                int addedquantity;
                try {
                    addedquantity = Integer.parseInt(etquantity.getText().toString());
                } catch (Exception e) {
                    addedquantity = 0;
                }
                if (addedquantity == 0) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {
                    int totalquatity = getTotalQuantity(origquantity, addedquantity);
                    int retprod = updateProductDetails(id, totalquatity);
                    long retquant = insertQuantityDetails(id, addedquantity);
                    if (retprod <= 0 || retquant < 0) {
                    } else {
                        Toast.makeText(this, "Quantity Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ListMyStock.class));
                    }
                }
                break;
        }
    }

    private int getTotalQuantity(int origquantity, int addedquantity) {
        return origquantity + addedquantity;
    }

    private long insertQuantityDetails(String id, int addedquantity) {
        return dbquanAdapter.insertQuantityDetails(id, addedquantity);
    }

    private int updateProductDetails(String id, int totalquatity) {
        return dbAdapter.updateProductQuantity(id, totalquatity);
    }
}
