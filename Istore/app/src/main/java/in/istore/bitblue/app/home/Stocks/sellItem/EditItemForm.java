package in.istore.bitblue.app.home.Stocks.sellItem;

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
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.Stocks.listStock.ListMyStock;
import in.istore.bitblue.app.databaseAdapter.DbCategoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.utilities.Check;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.ImageUtil;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class EditItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage, bUpdate, bBack;
    private EditText etbarcode, etdesc, etquantity, etminlimit, etcostprice, etsellingprice;
    private ImageView ivProdImage;
    private AutoCompleteTextView actvcategory, actvprodname, actvsupplier;


    private static final int CAPTURE_PIC_REQ = 2222;
    private String id, category, categoryName, name, desc, supplier;
    private int quantity, minlimit;
    private float costprice, sellprice;
    private String imagePath, Status, AddedOn, UserType, Key;
    private int proImgCount = 1, StoreId;
    private byte[] byteThumbnailArray, byteImage;

    private ArrayList<String> categoryNameList = new ArrayList<>(), proSubCatNameList = new ArrayList<>(), suppNameList = new ArrayList<>();
    private DbCategoryAdapter dbCategoryAdapter;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private DbSuppAdapter dbSuppAdapter;
    private DbProductAdapter dbAdapter;
    private GlobalVariables globalVariable;
    private TinyDB tinyDB;
    private Bitmap ProductThumbImage;
    private File imageFile;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_form);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("EDIT ITEM");
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }

        tinyDB = new TinyDB(this);
        proImgCount = tinyDB.getInt("imageCount");
        if (proImgCount == 0)
            tinyDB.putInt("imageCount", proImgCount);

        dbAdapter = new DbProductAdapter(this);

        dbCategoryAdapter = new DbCategoryAdapter(this);
//        categoryNameList = dbCategoryAdapter.getAllCategoryNames();

        // categoryNameList = tinyDB.getList("CategoryNames");
        dbProSubCatAdapter = new DbProSubCatAdapter(this);

        dbSuppAdapter = new DbSuppAdapter(this);
        // suppNameList = dbSuppAdapter.getAllSupplierNames();

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
        categoryName = getIntent().getStringExtra("prodCategory");
        name = getIntent().getStringExtra("prodName");

        ivProdImage = (ImageView) findViewById(R.id.iv_edititems_image);
        etbarcode = (EditText) findViewById(R.id.et_edititems_barcode_prod_id);
        if ((id != null) && (!id.equals(""))) {
            etbarcode.setText(id);
        } else {
            Toast.makeText(this, "ID NOT FOUND", Toast.LENGTH_SHORT).show();
        }

        actvcategory.setText(categoryName);
        actvcategory.setFocusable(false);
        actvcategory.setFocusableInTouchMode(false);
        actvprodname.setText(name);
        actvprodname.setFocusable(false);
        actvprodname.setFocusableInTouchMode(false);

        etdesc = (EditText) findViewById(R.id.et_edititems_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_edititems_prod_quantity);
        etminlimit = (EditText) findViewById(R.id.et_edititems_prod_minlimit);
        etcostprice = (EditText) findViewById(R.id.et_edititems_prod_costprice);
        etsellingprice = (EditText) findViewById(R.id.et_edititems_prod_sellingprice);

        getSupplierNames(StoreId, Key);
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

    public void getAllSubCategories(final int StoreId, final String Key, final String CategoryName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(EditItemForm.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Fetching Product Names...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("CategoryName", CategoryName));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_ALL_SUBCATEGORIES, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
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
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getApplicationContext(), "No Categories Found", Toast.LENGTH_LONG).show();
                }
                // categoryArrayList =getAllCategories(StoreId);
                // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                // lvcategories.setAdapter(categoryAdapter);
                else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String subCategoryName = jsonObject.getString("SubCategoryName");
                            if (subCategoryName == null || subCategoryName.equals("null")) {
                                break;
                            }
                            proSubCatNameList.add(subCategoryName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (proSubCatNameList != null && proSubCatNameList.size() > 0) {
                        return;
                    } else
                        Toast.makeText(getApplicationContext(), "No Products Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void getSupplierNames(final int StoreId, final String Key) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(EditItemForm.this);
                dialog.setCancelable(false);
                dialog.setMessage("Getting Suppliers...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                /*suppArrayList = dbsuppAdapter.getAllSuppInfo();                             ///remove if using api
                 if (suppArrayList != null && suppArrayList.size() > 0)
                    return true;
                else return false;*/
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Storeid", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("AdminKey", Key));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_SUPPLIER_INFO, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;//
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
               /* if (result) {
                    suppAdapter = new ViewSuppAdapter(getActivity(), suppArrayList);             //remove if using api
                    lvViewSupp = (ListView) view.findViewById(R.id.lv_viewSupp);
                    lvViewSupp.setAdapter(suppAdapter);                            //
                }*/
                if (Response == null) {                                          //
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getApplicationContext(), "No Supplier found", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String suppName = jsonObject.getString("Suppname");
                            if (suppName == null || suppName.equals("null")) {
                                break;
                            }
                            suppNameList.add(suppName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (suppNameList != null && suppNameList.size() > 0) {
                        return;
                    } else
                        Toast.makeText(getApplicationContext(), "No Supplier Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
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
                    Date date = new Date();
                    AddedOn = DateUtil.convertToStringDateOnly(date);
                    updateProductInfo();
                   /* long ret = dbAdapter.updateProductDetails(id, byteImage, null, name, desc, quantity, 0, sellprice, null);
                    if (ret < 0) {
                        Toast.makeText(this, "Record Not Updated: " + ret, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Record Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ListMyStock.class));
                    }*/
                }
                break;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        startActivityForResult(intent, CAPTURE_PIC_REQ);
    }

    private void updateProductInfo() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(EditItemForm.this);

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
                nameValuePairs.add(new BasicNameValuePair("PId", id));
                nameValuePairs.add(new BasicNameValuePair("Category", category));
                nameValuePairs.add(new BasicNameValuePair("Name", name));
                nameValuePairs.add(new BasicNameValuePair("Image", ImageUtil.convertByteArrayImagetoBase64Image(byteThumbnailArray)));
                nameValuePairs.add(new BasicNameValuePair("ProductDesc", desc));
                nameValuePairs.add(new BasicNameValuePair("Quantity", String.valueOf(quantity)));
                nameValuePairs.add(new BasicNameValuePair("Minlimit", String.valueOf(minlimit)));
                nameValuePairs.add(new BasicNameValuePair("Costprice", String.valueOf(costprice)));
                nameValuePairs.add(new BasicNameValuePair("Sellingprice", String.valueOf(sellprice)));
                nameValuePairs.add(new BasicNameValuePair("Supplier", supplier));
                nameValuePairs.add(new BasicNameValuePair("AddedOn", AddedOn));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_UPDATE_PRODUCTDETAILS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("Status");
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
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Updated Product", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), ListMyStock.class));
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
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
                        imageFile = new File(getExternalFilesDir("/IstoreEditItem"), "product " + tinyDB.getInt("imageCount") + ".png");
                        imageFile.createNewFile();
                        FileOutputStream fo = new FileOutputStream(imageFile);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    proImgCount++;
                    tinyDB.putInt("imageCount", proImgCount);
                    ProductThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imageFile.getPath()), 150, 150);//convert image to thumb
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ProductThumbImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                    byteThumbnailArray = byteArrayOutputStream.toByteArray();

                    Uri uri = Uri.fromFile(imageFile);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, ListMyStock.class));
    }
}
