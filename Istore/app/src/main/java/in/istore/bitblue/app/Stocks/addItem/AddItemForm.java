package in.istore.bitblue.app.Stocks.addItem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.listStock.ListMyStock;
import in.istore.bitblue.app.Stocks.listStock.ViewStockItems;
import in.istore.bitblue.app.databaseAdapter.DbCategoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.Store;
import in.istore.bitblue.app.utilities.api.API;

public class AddItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage, bScanBarcode, bSubmit, bCancel;
    private EditText etbarcode, etdesc, etquantity, etminlimit, etcostprice, etsellprice;
    private ImageView ivProdImage;
    private AutoCompleteTextView actvcategory, actvProdName, actvsupplier;

    private ArrayList<String> categoryNameList, proSubCatNameList, suppNameList;
    private DbCategoryAdapter dbCategoryAdapter;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbSuppAdapter dbSuppAdapter;
    private GlobalVariables globalVariable;
    private Bitmap ProductThumbImage;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private byte[] byteThumbnailArray;
    private int proImgCount = 1, iquantity, iminlimit, StoreId;
    private float fcostprice, fsellprice;
    private String scanContent, imagePath, id, category, name, desc, quantity, minlimit, costprice, sellprice, supplier, Key, UserType, AddedOn, Status;
    private byte[] byteImage;
    private static final int CAPTURE_PIC_REQ = 1111;
    private static final int SCAN_BARCODE = 2222;
    private DbProductAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_form);
        globalVariable = (GlobalVariables) getApplicationContext();
        proImgCount = globalVariable.getProdImageCount();
        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();

        if (UserType.equals("Admin")) {               //Remove this if using local
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }                                          //
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

        bScanBarcode = (Button) findViewById(R.id.b_additems_scanBarcode);
        bScanBarcode.setOnClickListener(this);

        etbarcode = (EditText) findViewById(R.id.et_additems_barcode_prod_id);

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
                category = actvcategory.getText().toString();
                if (Check.ifNull(category)) {
                    actvcategory.setHint("Field Required");
                    actvcategory.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return true;
                }
                proSubCatNameList = dbProSubCatAdapter.getAllProductNamesIn(category);
                ArrayAdapter subcatadapter = null;
                if (proSubCatNameList != null) {
                    subcatadapter = new ArrayAdapter
                            (getApplicationContext(), R.layout.dropdownlist, proSubCatNameList);
                } else {
                    actvProdName.setHint("No Product Specified");
                    actvProdName.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                }
                if (subcatadapter != null) {
                    actvProdName.setAdapter(subcatadapter);
                }
                actvProdName.setThreshold(0);
                actvProdName.showDropDown();
                id = etbarcode.getText().toString();
                if (Check.ifNull(id)) {
                    name = actvProdName.getText().toString();
                    etbarcode.setText(Store.generateProdId(category, name));
                    disableFocus(etbarcode);
                }
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

    private void disableFocus(EditText etbarcode) {
        etbarcode.setFocusable(false);
        etbarcode.setFocusableInTouchMode(false);
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
            case R.id.b_additems_scanBarcode:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
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

                } catch (NumberFormatException ignored) {
                }
                if (imagePath != null) {
                    try {
                        //Convert Image path to byte array
                        FileInputStream instream = new FileInputStream(imagePath);
                        BufferedInputStream bif = new BufferedInputStream(instream);
                        byteImage = new byte[bif.available()];
                        int read = bif.read(byteImage);
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
                    Date date = new Date();
                    AddedOn = DateUtil.convertToStringDateOnly(date);
                    //  addProductToDatabase();
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

    private void addProductToDatabase() {
        new AsyncTask<String, String, String>() {

            ProgressDialog dialog = new ProgressDialog(AddItemForm.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Id", id));
                nameValuePairs.add(new BasicNameValuePair("Category", category));
                nameValuePairs.add(new BasicNameValuePair("Name", name));
                nameValuePairs.add(new BasicNameValuePair("Image", Arrays.toString(byteThumbnailArray)));
                nameValuePairs.add(new BasicNameValuePair("ProductDesc", desc));
                nameValuePairs.add(new BasicNameValuePair("Quantity", quantity));
                nameValuePairs.add(new BasicNameValuePair("Minlimit", minlimit));
                nameValuePairs.add(new BasicNameValuePair("Costprice", costprice));
                nameValuePairs.add(new BasicNameValuePair("Sellingprice", sellprice));
                nameValuePairs.add(new BasicNameValuePair("Supplier", supplier));
                nameValuePairs.add(new BasicNameValuePair("AddedOn", AddedOn));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("Key", Key));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_PRODUCT, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("status");
                        return Status;
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Added Product", Toast.LENGTH_LONG).show();
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        startActivityForResult(intent, CAPTURE_PIC_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        File file = null;
        boolean isProductExisting = false;
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //String scanFormat = scanningResult.getFormatName();
            scanContent = scanningResult.getContents();
            if (scanContent != null && !(scanContent.equals("")))
                isProductExisting = checkForExistingProduct(scanContent);
            if (isProductExisting) {
                Intent viewStockItem = new Intent(this, ViewStockItems.class);
                viewStockItem.putExtra("barcode", scanContent);
                startActivity(viewStockItem);
            } else {
                etbarcode.setText(scanContent);
                disableFocus(etbarcode);
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (requestCode == CAPTURE_PIC_REQ) {
            switch (resultCode) {
                case -1:
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
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
                    ProductThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.getPath()), 150, 150);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ProductThumbImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                    byteThumbnailArray = byteArrayOutputStream.toByteArray();
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
