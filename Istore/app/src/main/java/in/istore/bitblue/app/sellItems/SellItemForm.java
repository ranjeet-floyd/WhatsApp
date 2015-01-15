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
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.soldItems.SoldItems;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class SellItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;

    private Button bEdit, bSell, bCaptureImage;
    private TextView tvbarcode, tvname, tvprice;
    private ExpandableTextView etvdesc;
    private ImageView ivProdImage;
    private SparseBooleanArray mCollapsedStatus;
    private String scanContent;
    private GlobalVariables globalVariable;
    private String id, name, desc, quantity, price;
    private byte[] byteImage;
    private Bitmap bitmap;

    private DbProductAdapter dbAdapter;

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
        dbAdapter = new DbProductAdapter(this);
        if (id != null) {
            getProductfor(id);
        } else if (scanContent != null) {
            getProductvia(scanContent);
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

        tvprice = (TextView) findViewById(R.id.et_sellitems_prod_price);
        tvprice.setText(price);
    }

    private void getProductvia(String scanContent) {
        Product product = dbAdapter.getProductDetails(scanContent);
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
        Product product = dbAdapter.getProductDetails(id);
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
                long ret = dbAdapter.updateSoldProductDetails(id);
                if (ret < 0) {
                    Toast.makeText(this, "Sold Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sold Record Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SoldItems.class));
                }
                break;
            /*case R.id.b_sellitems_update:

                id = etbarcode.getText().toString();
                name = etname.getText().toString();
                desc = etdesc.getText().toString();
                quantity = etquantity.getText().toString();
                price = etprice.getText().toString();
                if (imagePath != null) {
                    try {
                        //Convert Image path to byte array
                        FileInputStream instream = new FileInputStream(imagePath);
                        BufferedInputStream bif = new BufferedInputStream(instream);
                        byteImage = new byte[bif.available()];
                        bif.read(byteImage);
                    } catch (IOException e) {
                        Toast.makeText(this, "Error:Unable to get the Image Location", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Error: Update Product Image", Toast.LENGTH_LONG).show();
                    break;
                }
                if (Check.ifNull(id)) {
                    etbarcode.setHint("Field Required");
                    etbarcode.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(name)) {
                    etname.setHint("Field Required");
                    etname.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(desc)) {
                    etdesc.setHint("Field Required");
                    etdesc.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(quantity)) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(price)) {
                    etprice.setHint("Field Required");
                    etprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else {
                    long ret = cursorAdapter.updateProductDetails(id, byteImage, name, desc, quantity, price, 0);
                    if (ret < 0) {
                        Toast.makeText(this, "Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                    } else {
                        adjustlayoutUPDATE();
                        Toast.makeText(this, "Record Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                break;*/
        }
    }

    private void startEditItemActivity() {
        Intent editItem = new Intent(this, EditItemForm.class);
        editItem.putExtra("prodid", id);
        startActivity(editItem);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_items, menu);
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

   /* private void adjustlayoutEDIT() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        etbarcode.setFocusableInTouchMode(false);
        etname.setFocusableInTouchMode(true);
        etdesc.setFocusableInTouchMode(true);
        etquantity.setFocusableInTouchMode(true);
        etprice.setFocusableInTouchMode(true);

        bEdit.setVisibility(View.GONE);
        bSell.setVisibility(View.GONE);
        bBack.setVisibility(View.VISIBLE);
        bUpdate.setVisibility(View.VISIBLE);
        bCaptureImage.setVisibility(View.VISIBLE);
        bBack.setLayoutParams(params);
        bUpdate.setLayoutParams(params);

        llButtons.setWeightSum(2f);


    }*/

   /* private void adjustlayoutUPDATE() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        etbarcode.setFocusableInTouchMode(false);
        etbarcode.setFocusable(false);
        etname.setFocusableInTouchMode(false);
        etname.setFocusable(false);
        etdesc.setFocusableInTouchMode(false);
        etdesc.setFocusable(false);
        etquantity.setFocusableInTouchMode(false);
        etquantity.setFocusable(false);
        etprice.setFocusableInTouchMode(false);
        etprice.setFocusable(false);

        bBack.setVisibility(View.VISIBLE);
        bSell.setVisibility(View.VISIBLE);
        bEdit.setVisibility(View.VISIBLE);
        bUpdate.setVisibility(View.GONE);

        bSell.setLayoutParams(params);
        bBack.setLayoutParams(params);
        bEdit.setLayoutParams(params);

        llButtons.setWeightSum(3f);
    }*/

}
