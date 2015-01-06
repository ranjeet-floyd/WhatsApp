package in.istore.bitblue.app.addItems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.DbCursorAdapter;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class AddItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage, bSubmit, bCancel;
    private EditText etbarcode, etname, etdesc, etquantity, etprice;
    private String scanContent;
    private ImageView ivProdImage;
    private static final int CAPTURE_PIC_REQ = 1111;
    private GlobalVariables globalVariable;
    private int proImgCount = 1;
    private String imagePath, barcode, name, desc, quantity, price;
    private byte[] byteImage;
    private DbCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_form);

        scanContent = getIntent().getStringExtra("scanContentaddItem");
        globalVariable = (GlobalVariables) getApplicationContext();
        proImgCount = globalVariable.getProdImageCount();

        setToolbar();
        initViews();
    }
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("ADD ITEMS");
    }

    private void initViews() {
        bCaptureImage = (Button) findViewById(R.id.b_additems_captureImage);
        bCaptureImage.setOnClickListener(this);

        bSubmit = (Button) findViewById(R.id.b_additems_submit);
        bSubmit.setOnClickListener(this);

        bCancel = (Button) findViewById(R.id.b_additems_cancel);
        bCancel.setOnClickListener(this);

        etbarcode = (EditText) findViewById(R.id.et_additems_barcode_prod_id);
        if (scanContent != null || scanContent != "")
            etbarcode.setText(scanContent);

        ivProdImage = (ImageView) findViewById(R.id.iv_additems_image);

        etbarcode = (EditText) findViewById(R.id.et_additems_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_additems_prod_name);
        etdesc = (EditText) findViewById(R.id.et_additems_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_additems_prod_quantity);
        etprice = (EditText) findViewById(R.id.et_additems_prod_price);

        cursorAdapter = new DbCursorAdapter(this);

    }


    @Override
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
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_additems_captureImage:
                captureImage();
                break;
            case R.id.b_additems_cancel:
                startActivity(new Intent(this, AddItems.class));
                break;
            case R.id.b_additems_submit:
                barcode = etbarcode.getText().toString();
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
                    Toast.makeText(this, "Capture Product Image", Toast.LENGTH_LONG).show();
                    break;
                }
                if (Check.ifNull(barcode)) {
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
                    long ret = cursorAdapter.insertProductDetails(barcode, byteImage, name, desc, quantity, price);
                    if (ret < 0) {
                    } else {
                        startActivity(new Intent(this, ListMyStock.class));
                    }
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
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
