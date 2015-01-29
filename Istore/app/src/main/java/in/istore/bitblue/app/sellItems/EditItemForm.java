package in.istore.bitblue.app.sellItems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbCategoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.listStock.ListMyStock;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class EditItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage, bUpdate, bBack;
    private EditText etbarcode, etdesc, etquantity, etminlimit, etcostprice, etsellingprice;
    private ImageView ivProdImage;
    private AutoCompleteTextView actvcategory, actvprodname, actvsupplier;


    private static final int CAPTURE_PIC_REQ = 2222;
    private String id, category, name, desc, supplier;
    private int quantity, minlimit;
    private float costprice, sellprice;
    private String imagePath;
    private byte[] byteImage;
    private int proImgCount = 1;

    private ArrayList<String> categoryNameList, proSubCatNameList, suppNameList;
    private DbCategoryAdapter dbCategoryAdapter;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbSuppAdapter dbSuppAdapter;
    private DbProductAdapter dbAdapter;
    private GlobalVariables globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_form);
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
        toolTitle.setText("EDIT ITEM");
    }

    private void initViews() {

        dbAdapter = new DbProductAdapter(this);

        dbCategoryAdapter = new DbCategoryAdapter(this);
        categoryNameList = dbCategoryAdapter.getAllCategoryNames();

        dbProSubCatAdapter = new DbProSubCatAdapter(this);

        dbSuppAdapter = new DbSuppAdapter(this);
        suppNameList = dbSuppAdapter.getAllSupplierNames();

        actvcategory = (AutoCompleteTextView) findViewById(R.id.actv_edititems_prod_category);
        actvprodname = (AutoCompleteTextView) findViewById(R.id.actv_edititems_prod_name);
        actvsupplier = (AutoCompleteTextView) findViewById(R.id.actv_edititems_prod_supplier);

        ArrayAdapter catadapter = new ArrayAdapter
                (this, R.layout.dropdownlist, categoryNameList);
        actvcategory.setThreshold(0);
        actvcategory.setAdapter(catadapter);

        ArrayAdapter suppAdapter = new ArrayAdapter
                (this, R.layout.dropdownlist, suppNameList);
        actvsupplier.setThreshold(0);
        actvsupplier.setAdapter(suppAdapter);

        id = getIntent().getStringExtra("prodid");
        ivProdImage = (ImageView) findViewById(R.id.iv_edititems_image);

        etbarcode = (EditText) findViewById(R.id.et_edititems_barcode_prod_id);
        if ((id != null) && (!id.equals(""))) {
            etbarcode.setText(id);
        } else {
            Toast.makeText(this, "ID NOT FOUND", Toast.LENGTH_SHORT).show();
        }

        actvcategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (categoryNameList == null || categoryNameList.size() == 0) {
                    actvcategory.setHint("No Category Specified");
                    actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return true;
                } else
                    actvcategory.showDropDown();
                return false;
            }
        });

        actvprodname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String CategoryName = actvcategory.getText().toString();
                if (Check.ifNull(CategoryName)) {
                    actvcategory.setHint("Field Required");
                    actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return true;
                }
                proSubCatNameList = dbProSubCatAdapter.getAllProductNames(CategoryName);
                ArrayAdapter subcatadapter = new ArrayAdapter
                        (getApplicationContext(), R.layout.dropdownlist, proSubCatNameList);
                actvprodname.setThreshold(0);
                actvprodname.setAdapter(subcatadapter);
                actvprodname.showDropDown();
                return false;
            }
        });

        etdesc = (EditText) findViewById(R.id.et_edititems_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_edititems_prod_quantity);
        etminlimit = (EditText) findViewById(R.id.et_edititems_prod_minlimit);
        etcostprice = (EditText) findViewById(R.id.et_edititems_prod_costprice);
        etsellingprice = (EditText) findViewById(R.id.et_edititems_prod_sellingprice);

        actvsupplier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (suppNameList == null || suppNameList.size() == 0) {
                    actvsupplier.setHint("No Supplier Specified");
                    actvsupplier.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return true;
                } else
                    actvsupplier.showDropDown();
                return false;
            }
        });


        bCaptureImage = (Button) findViewById(R.id.b_edititems_captureImage);
        bCaptureImage.setOnClickListener(this);

        bUpdate = (Button) findViewById(R.id.b_edititems_update);
        bUpdate.setOnClickListener(this);

        bBack = (Button) findViewById(R.id.b_edititems_back);
        bBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_edititems_back:
                startActivity(new Intent(this, SellItem.class));
                break;
            case R.id.b_edititems_captureImage:
                captureImage();
                break;
            case R.id.b_edititems_update:
                id = etbarcode.getText().toString();
                category = actvcategory.getText().toString();
                name = actvprodname.getText().toString();
                desc = etdesc.getText().toString();
                supplier = actvsupplier.getText().toString();
                try {
                    quantity = Integer.parseInt(etquantity.getText().toString());
                    minlimit = Integer.parseInt(etminlimit.getText().toString());
                    costprice = Float.parseFloat(etcostprice.getText().toString());
                    sellprice = Float.parseFloat(etsellingprice.getText().toString());
                } catch (Exception e) {
                }
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
                if (Check.ifNull(category)) {
                    actvcategory.setHint("Field Required");
                    actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (Check.ifNull(name)) {
                    actvprodname.setHint("Field Required");
                    actvprodname.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(desc)) {
                    etdesc.setHint("Field Required");
                    etdesc.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (quantity == 0) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (minlimit == 0) {
                    etminlimit.setHint("Field Required");
                    etminlimit.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (costprice == 0) {
                    etcostprice.setHint("Field Required");
                    etcostprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (sellprice == 0) {
                    etsellingprice.setHint("Field Required");
                    etsellingprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else if (Check.ifNull(supplier)) {
                    actvsupplier.setHint("Field Required");
                    actvsupplier.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;

                } else {
                    long ret = dbAdapter.updateProductDetails(id, byteImage, null, name, desc, quantity, 0, sellprice, null);
                    if (ret < 0) {
                        Toast.makeText(this, "Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Record Updated", Toast.LENGTH_SHORT).show();
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

                    File dir = new File(getExternalFilesDir(""), "/IstoreEditItem");
                    if (dir.mkdir()) {
                        Log.e("App", "created directory, IstoreEditItem");
                    } else {
                        Log.e("App", "Already created directory, IstoreEditItem");
                    }
                    try {
                        file = new File(getExternalFilesDir("/IstoreEditItem"), "product " + proImgCount + ".png");
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
}
