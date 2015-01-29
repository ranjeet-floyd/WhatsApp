package in.istore.bitblue.app.soldItems;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.Check;

public class SoldItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private EditText etbarcode, etname, etdesc, etquantity, etprice;
    private ImageView ivProdImage;
    private Button bSellItem, bInc, bDec;

    private DbProductAdapter dbProAdapter;
    private DbSoldItemAdapter dbSolItmAdapter;

    private Bitmap bitmap;
    private String id, name, desc;
    private int quantity, maxlimit, minlimit = 0;
    private float sellprice;
    private byte[] byteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_item_form);
        id = getIntent().getStringExtra("productid");
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SOLD ITEM FORM");
    }

    private void initViews() {
        dbProAdapter = new DbProductAdapter(this);
        dbSolItmAdapter = new DbSoldItemAdapter(this);
        ivProdImage = (ImageView) findViewById(R.id.iv_solditem_image);
        etbarcode = (EditText) findViewById(R.id.et_solditem_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_solditem_prod_name);
        etdesc = (EditText) findViewById(R.id.et_solditem_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_solditem_prod_quantity);
        etprice = (EditText) findViewById(R.id.et_solditem_prod_price);

        bSellItem = (Button) findViewById(R.id.b_solditem_sell);
        bSellItem.setOnClickListener(this);

        bInc = (Button) findViewById(R.id.b_solditem_inc);
        bInc.setOnClickListener(this);

        bDec = (Button) findViewById(R.id.b_solditem_dec);
        bDec.setOnClickListener(this);

        if (id != null)
            getProductfor(id);
    }

    private void getProductfor(String id) {
        Product product = dbProAdapter.getProductDetails(id);
        if (product != null) {
            etbarcode.setText(id);

            name = product.getName();
            etname.setText(name);

            desc = product.getDesc();
            etdesc.setText(desc);

            quantity = product.getQuantity();
            etquantity.setText(String.valueOf(quantity));
            maxlimit = quantity;

            sellprice = product.getSellingPrice();
            etprice.setText(String.valueOf(sellprice));

            byteImage = product.getImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (byteImage != null) {
                bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
                ivProdImage.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_solditem_sell:
                int quant;
                id = etbarcode.getText().toString();
                try {
                    quant = quantity;
                } catch (NumberFormatException nfe) {
                    quant = 0;
                }
                if (Check.ifNull(etquantity.getText().toString())) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (quant > maxlimit) {
                    limitReached();
                    break;
                } else if (Check.ifNull(etprice.getText().toString())) {
                    etprice.setHint("Field Required");
                    etprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {
                    String soldQuan = etquantity.getText().toString();
                    int soldQuantity;
                    try {
                        soldQuantity = Integer.parseInt(soldQuan);
                    } catch (NumberFormatException nfe) {
                        soldQuantity = 0;
                    }

                    int remQuantity = maxlimit - soldQuantity;

                    long ret = dbSolItmAdapter.insertSoldItemQuantityDetail(id, soldQuantity, remQuantity, sellprice);
                    // long ret1 = dbProAdapter.updateSoldProductDetails(id);  //update status to 'sold' in product table.
                    long ret2 = dbProAdapter.updateProductQuantity(id, remQuantity);//update remaining quantity in product table

                    if (ret < 0 || ret2 <= 0) {
                        Toast.makeText(this, "Sold Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sold Record Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ListSoldItems.class));
                    }
                }
                break;
            case R.id.b_solditem_inc:
                int quantinc;
                try {
                    quantinc = Integer.parseInt(etquantity.getText().toString());
                    ++quantinc;
                } catch (NumberFormatException nfe) {
                    quantinc = 0;
                }
                if (quantinc > maxlimit) {
                    break;
                } else {
                    etquantity.setText(String.valueOf(quantinc));
                }
                break;
            case R.id.b_solditem_dec:
                int quantdec;
                try {
                    quantdec = Integer.parseInt(etquantity.getText().toString());
                    --quantdec;
                } catch (NumberFormatException nfe) {
                    quantdec = 0;
                }
                if (quantdec <= minlimit) {
                    break;
                } else {
                    etquantity.setText(String.valueOf(quantdec));
                }
                break;
        }
    }

    private void limitReached() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Max Stock Limit Reached");
        builder.setMessage("You cannot add anymore item unit");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}
