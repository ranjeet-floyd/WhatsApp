package in.istore.bitblue.app.listMyStock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.addItems.AddItems;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbQuantityAdapter;
import in.istore.bitblue.app.utilities.Check;

public class ViewStockItems extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etbarcode, etname, etquantity;
    private ImageView ivProdImage;

    private String id;
    private String origquantity;
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
        Product product;

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
                etquantity.setText(origquantity);
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
                startActivity(new Intent(this, AddItems.class));
                break;
            case R.id.b_viewstockitem_update:
                String addedquantity = etquantity.getText().toString();
                if (Check.ifNull(addedquantity)) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {
                    String totalquatity = origquantity + addedquantity;
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

    private long insertQuantityDetails(String id, String addedquantity) {
        return dbquanAdapter.insertQuantityDetails(id, addedquantity);
    }

    private int updateProductDetails(String id, String totalquatity) {
        return dbAdapter.updateProductQuantity(id, totalquatity);
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_stock_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
