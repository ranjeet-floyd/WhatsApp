package in.istore.bitblue.app.sellItems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.DbCursorAdapter;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.soldItems.SoldItems;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class SellItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;

    private Button bBack, bEdit, bUpdate, bSell, bCaptureImage;
    private EditText etbarcode, etname, etdesc, etquantity, etprice;
    private ImageView ivProdImage;
    private LinearLayout llButtons;

    private String scanContent;
    private static final int CAPTURE_PIC_REQ = 2222;
    private GlobalVariables globalVariable;
    private int proImgCount = 1;
    private String imagePath, id, name, desc, quantity, price;
    private byte[] byteImage;
    private Bitmap bitmap;

    private DbCursorAdapter cursorAdapter;

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

        id = getIntent().getStringExtra("id");  //Obtained when list item is selected
        scanContent = getIntent().getStringExtra("scanContentsellitem");  //Obtained when barcode is scanned
        cursorAdapter = new DbCursorAdapter(this);
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

        bUpdate = (Button) findViewById(R.id.b_sellitems_update);
        bUpdate.setOnClickListener(this);

        bBack = (Button) findViewById(R.id.b_sellitems_cancel);
        bBack.setOnClickListener(this);

        bCaptureImage = (Button) findViewById(R.id.b_sellitems_captureImage);
        bCaptureImage.setOnClickListener(this);

        ivProdImage = (ImageView) findViewById(R.id.iv_sellitems_image);
        if (bitmap != null) {
            ivProdImage.setImageBitmap(bitmap);
        }

        llButtons = (LinearLayout) findViewById(R.id.ll_buttons);

        etbarcode = (EditText) findViewById(R.id.et_sellitems_barcode_prod_id);
        if (scanContent != null) {
            etbarcode.setText(scanContent);
        } else if (id != null) {
            etbarcode.setText(id);
        } else Toast.makeText(this, "Id not found", Toast.LENGTH_SHORT).show();

        etname = (EditText) findViewById(R.id.et_sellitems_prod_name);
        etname.setText(name);

        etdesc = (EditText) findViewById(R.id.et_sellitems_prod_desc);
        etdesc.setText(desc);

        etquantity = (EditText) findViewById(R.id.et_sellitems_prod_quantity);
        etquantity.setText(quantity);

        etprice = (EditText) findViewById(R.id.et_sellitems_prod_price);
        etprice.setText(price);
    }

    private void getProductvia(String scanContent) {
        Product product = cursorAdapter.getProductDetails(scanContent);
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
        Product product = cursorAdapter.getProductDetails(id);
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
            case R.id.b_sellitems_cancel:
                startActivity(new Intent(this, ListMyStock.class));
                break;
            case R.id.b_sellitems_edit:
                adjustlayoutEDIT();
                break;
            case R.id.b_sellitems_captureImage:
                captureImage();
                break;

            case R.id.b_sellitems_update:

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
                    long ret = cursorAdapter.updateProductDetails(id, byteImage, name, desc, quantity, price,0);
                    if (ret < 0) {
                        Toast.makeText(this, "Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                    } else {
                        adjustlayoutUPDATE();
                        Toast.makeText(this, "Record Updated", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.b_sellitems_submit:
                long ret = cursorAdapter.updateSoldProductDetails(id);
                if (ret < 0) {
                    Toast.makeText(this, "Sold Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Sold Record Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, SoldItems.class));
                }
                break;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        startActivityForResult(intent, CAPTURE_PIC_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File file = null;
        if (requestCode == CAPTURE_PIC_REQ) {
            switch (resultCode) {
                case -1:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivProdImage.setImageBitmap(bitmap);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                    File dir = new File(Environment.getExternalStorageDirectory(), "/Istore");
                    if (dir.mkdir()) {
                        Log.e("App", "created directory, Istore");
                    } else {
                        Log.e("App", "Already created directory, Istore");
                    }
                    try {
                        file = new File(Environment.getExternalStorageDirectory() + "/Istore", "product " + proImgCount + ".png");
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    globalVariable.increaseProImgCount();
                    proImgCount = globalVariable.getProdImageCount();

                    Uri uri = getImageContentUri(this, file);
                    imagePath = getRealPathFromURI(uri);
                    Log.e("URI:", uri.toString());
                    Log.e("Path:", getRealPathFromURI(uri));
                    break;
            }
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
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

    private void adjustlayoutEDIT() {
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


    }

    private void adjustlayoutUPDATE() {
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
    }

}
