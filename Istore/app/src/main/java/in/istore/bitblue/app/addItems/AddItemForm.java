package in.istore.bitblue.app.addItems;

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
import in.istore.bitblue.app.listStock.ViewStockItems;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class AddItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage, bSubmit, bCancel;
    private EditText etbarcode, etdesc, etquantity, etminlimit, etcostprice, etsellprice;
    private ImageView ivProdImage;
    private AutoCompleteTextView actvcategory, actvProdName, actvsupplier;

    private ArrayList<String> categoryNameList, proSubCatNameList, suppNameList;
    private DbCategoryAdapter dbCategoryAdapter;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbSuppAdapter dbSuppAdapter;
    private GlobalVariables globalVariable;


    private int proImgCount = 1, iquantity, iminlimit;
    private float fcostprice, fsellprice;
    private String imagePath, id, category, name, desc, quantity, minlimit, costprice, sellprice, supplier;
    private byte[] byteImage;
    private String scanContent;
    private static final int CAPTURE_PIC_REQ = 1111;

    private DbProductAdapter dbAdapter;


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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("ADD ITEMS");
    }

    private void initViews() {
        boolean isProductExisting = false;

        dbAdapter = new DbProductAdapter(this);

        dbCategoryAdapter = new DbCategoryAdapter(this);
        categoryNameList = dbCategoryAdapter.getAllCategoryNames();

        dbProSubCatAdapter = new DbProSubCatAdapter(this);

        dbSuppAdapter = new DbSuppAdapter(this);
        suppNameList = dbSuppAdapter.getAllSupplierNames();

        actvcategory = (AutoCompleteTextView) findViewById(R.id.actv_additems_category);
        actvProdName = (AutoCompleteTextView) findViewById(R.id.actv_additems_prod_name);
        actvsupplier = (AutoCompleteTextView) findViewById(R.id.actv_additems_prod_supplier);

        ArrayAdapter catadapter = new ArrayAdapter
                (this, R.layout.dropdownlist, categoryNameList);
        actvcategory.setThreshold(0);
        actvcategory.setAdapter(catadapter);

        ArrayAdapter suppAdapter = new ArrayAdapter
                (this, R.layout.dropdownlist, suppNameList);
        actvsupplier.setThreshold(0);
        actvsupplier.setAdapter(suppAdapter);

        bCaptureImage = (Button) findViewById(R.id.b_additems_captureImage);
        bCaptureImage.setOnClickListener(this);

        bSubmit = (Button) findViewById(R.id.b_additems_submit);
        bSubmit.setOnClickListener(this);

        bCancel = (Button) findViewById(R.id.b_additems_cancel);
        bCancel.setOnClickListener(this);

        etbarcode = (EditText) findViewById(R.id.et_additems_barcode_prod_id);
        if (scanContent != null && !(scanContent.equals("")))
            isProductExisting = checkForExistingProduct(scanContent);
        if (isProductExisting) {
            Intent viewStockItem = new Intent(this, ViewStockItems.class);
            viewStockItem.putExtra("barcode", scanContent);
            startActivity(viewStockItem);
        } else {
            etbarcode.setText(scanContent);

            ivProdImage = (ImageView) findViewById(R.id.iv_additems_image);

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

            actvProdName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    String CategoryName = actvcategory.getText().toString();
                    if (Check.ifNull(CategoryName)) {
                        actvcategory.setHint("Field Required");
                        actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                        return true;
                    }
                    proSubCatNameList = dbProSubCatAdapter.getAllProductNamesIn(CategoryName);
                    ArrayAdapter subcatadapter = new ArrayAdapter
                            (getApplicationContext(), R.layout.dropdownlist, proSubCatNameList);
                    actvProdName.setThreshold(0);
                    actvProdName.setAdapter(subcatadapter);
                    actvProdName.showDropDown();
                    return false;
                }
            });
            etdesc = (EditText) findViewById(R.id.et_additems_prod_desc);
            etquantity = (EditText) findViewById(R.id.et_additems_prod_quantity);
            etminlimit = (EditText) findViewById(R.id.et_additems_prod_minlimit);
            etcostprice = (EditText) findViewById(R.id.et_additems_prod_costprice);
            etsellprice = (EditText) findViewById(R.id.et_additems_prod_sellprice);

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

        }

    }

    private boolean checkForExistingProduct(String id) {
        boolean isProductAvailable, isidPresent;
        String status = "not sold";
        isProductAvailable = dbAdapter.isProductAvail(id);
        isidPresent = dbAdapter.idAlreadyPresent(id);
        return (isidPresent && isProductAvailable);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_additems_captureImage:
                captureImage();
                break;
            case R.id.b_additems_cancel:
                startActivity(new Intent(this, AddItemsMenu.class));
                break;
            case R.id.b_additems_submit:
                id = etbarcode.getText().toString();
                category = actvcategory.getText().toString();
                name = actvProdName.getText().toString();
                desc = etdesc.getText().toString();
                quantity = etquantity.getText().toString();
                minlimit = etminlimit.getText().toString();
                costprice = etcostprice.getText().toString();
                sellprice = etsellprice.getText().toString();
                supplier = actvsupplier.getText().toString();
                try {
                    iquantity = Integer.parseInt(quantity);
                    iminlimit = Integer.parseInt(minlimit);
                    fcostprice = Float.parseFloat(costprice);
                    fsellprice = Float.parseFloat(sellprice);

                } catch (NumberFormatException nfe) {
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
                    Toast.makeText(this, "Capture Product Image", Toast.LENGTH_LONG).show();
                    break;
                }
                if (Check.ifNull(id)) {
                    etbarcode.setHint("Field Required");
                    etbarcode.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (dbAdapter.idAlreadyPresent(id)) {
                    Toast.makeText(this, "\tId " + id + " ALREADY EXISTS\n please enter unique id ", Toast.LENGTH_LONG).show();
                    break;
                } else if (Check.ifNull(category)) {
                    actvcategory.setHint("Field Required");
                    actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (Check.ifNull(name)) {
                    actvProdName.setHint("Field Required");
                    actvProdName.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (Check.ifNull(desc)) {
                    etdesc.setHint("Field Required");
                    etdesc.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (Check.ifNull(quantity)) {
                    etquantity.setHint("Field Required");
                    etquantity.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (iquantity == 0) {
                    Toast.makeText(this, "Quantity cannot be blank", Toast.LENGTH_SHORT).show();
                    break;
                } else if (Check.ifNull(minlimit)) {
                    etminlimit.setHint("Field Required");
                    etminlimit.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (iminlimit == 0) {
                    Toast.makeText(this, "Min limit cannot be blank", Toast.LENGTH_SHORT).show();
                    break;
                } else if (Check.ifNull(costprice) || Check.ifNull(sellprice)) {
                    etcostprice.setHint("Field Required");
                    etcostprice.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (fcostprice == 0 || fsellprice == 0) {
                    Toast.makeText(this, "Price cannot be blank", Toast.LENGTH_SHORT).show();
                    break;
                } else if (Check.ifNull(supplier)) {
                    actvsupplier.setHint("Field Required");
                    actvsupplier.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else {
                    long ret = dbAdapter.insertProductDetails(id, byteImage, category, name, desc, iquantity, iminlimit, fcostprice, fsellprice, supplier);
                    if (ret < 0) {
                    } else {
                        Toast.makeText(this, "Record Added", Toast.LENGTH_SHORT).show();
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


                    //getExternalFilesDir("") will locate the storage for this app
                    // Path is: storage/emulated/0/Android/data/in.istore.bitblue.app/files/Istore
                    File dir = new File(getExternalFilesDir(""), "/Istore");
                    if (dir.mkdir()) {
                        Log.e("App", "created directory, Istore");
                    } else {
                        Log.e("App", "Already created directory, Istore");
                    }
                    try {
                        file = new File(dir, "product " + proImgCount + ".png");
                        file.createNewFile();
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    globalVariable.increaseProImgCount();
                    proImgCount = globalVariable.getProdImageCount();

                    Uri uri = Uri.fromFile(file);
                    imagePath = getRealPathFromURI(uri);
                    Log.e("URI:", uri.toString());
                    Log.e("Path:", getRealPathFromURI(uri));
                    break;
            }
        }
    }

    //Get the URI of Image
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

    //Get location of image in the storage
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
