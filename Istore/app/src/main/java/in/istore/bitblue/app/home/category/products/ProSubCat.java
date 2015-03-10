package in.istore.bitblue.app.home.category.products;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.ProSubCatAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.home.category.Categories;
import in.istore.bitblue.app.pojo.ProductSubCategory;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;

public class ProSubCat extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private Toolbar toolbar;
    private TextView toolTitle;

    private SearchView searchView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem;
    private ListView lvprosubcat;
    private ProgressBar progressBar;

    private ArrayList<ProSubCat> proSubCatArrayList = new ArrayList<ProSubCat>();
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    private ArrayList<ProductSubCategory> prodSubCatArrayList = new ArrayList<ProductSubCategory>();
    private DbProSubCatAdapter dbProSubCatAdapter;
    private ProSubCatAdapter proSubCatAdapter;
    private ProductSubCategory productSubCategory;
    private int StoreId;
    private String UserType, Key, CategoryName, SubCategoryName, Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_subcat);
        CategoryName = getIntent().getStringExtra("category");
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("Products: " + CategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }
        StoreId = globalVariable.getStoreId();

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_prosubcat_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);

        addNewItem = (FloatingActionButton) findViewById(R.id.fab_prosubcat_additem);
        addNewItem.setOnClickListener(this);
        searchView = (SearchView) findViewById(R.id.sv_prosubcat_search);

        lvprosubcat = (ListView) findViewById(R.id.lv_prosubcat);
        lvprosubcat.setTextFilterEnabled(true);

        dbProSubCatAdapter = new DbProSubCatAdapter(this);

        getAllSubCategoriesOnServer(StoreId, Key, CategoryName);
        //getAllSubCategoriesOnLocal();
        setupSearchView();
    }

    private void getAllSubCategoriesOnLocal() {
        prodSubCatArrayList = dbProSubCatAdapter.getAllProSubCategories(CategoryName);
        if (prodSubCatArrayList != null && prodSubCatArrayList.size() > 0) {
            proSubCatAdapter = new ProSubCatAdapter(this, prodSubCatArrayList);
            lvprosubcat.setAdapter(proSubCatAdapter);
        } else Toast.makeText(this, "No Products Available", Toast.LENGTH_SHORT).show();
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("");
        try {
            //Set Custom icon For SearchView
            Field searchField = SearchView.class
                    .getDeclaredField("mSearchButton");
            searchField.setAccessible(true);
            ImageView searchBtn = (ImageView) searchField.get(searchView);
            searchBtn.setImageResource(R.drawable.ic_action_search);  //SearchView icon
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_prosubcat_additem:
                showAddItemDialog();
                break;
        }
    }

    private void showAddItemDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.setTitle("Add Product");

        final EditText etcatName = (EditText) dialog.findViewById(R.id.et_additem_dialog_categoryname);
        Button add = (Button) dialog.findViewById(R.id.b_additem_dialog_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SubCategoryName = etcatName.getText().toString();
                //addNewProductOnLocal();
                boolean isProductExists = checkForExistingProductOnServer(SubCategoryName);
                if (isProductExists)
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_SHORT).show();
                else {
                    addSubCategoryForCategoryOnServer(CategoryName);
                    prodSubCatArrayList.clear();
                    getAllSubCategoriesOnServer(StoreId, Key, CategoryName);
                }


            }
        });
        dialog.show();
    }

    private boolean checkForExistingProductOnServer(String productName) {

        for (ProductSubCategory prodSubCat : prodSubCatArrayList) {
            if (prodSubCat.getProductSubCategoryName().equalsIgnoreCase(productName))
                return true;
        }
        return false;
    }

    private void addSubCategoryForCategoryOnServer(final String categoryName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ProSubCat.this);

            @Override
            protected void onPreExecute() {
                /*dialog.setMessage("Adding Product...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Categoryname", categoryName));
                nameValuePairs.add(new BasicNameValuePair("ProsubcatName", SubCategoryName));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_ADD_SUBCATEGORY, nameValuePairs);
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
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Added Product: " + SubCategoryName, Toast.LENGTH_SHORT).show();
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "SubCategory Already Exists", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void addNewProductOnLocal() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ProSubCat.this);

            @Override
            protected void onPreExecute() {
             /*   dialog.setMessage("Adding Product...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
            }

            @Override
            protected String doInBackground(String... strings) {
                if (isProductAlreadyExistsOnLocal())
                    return "alreadyExists";
                else {
                    long result = dbProSubCatAdapter.addNewProductInCategory(CategoryName, SubCategoryName, String.valueOf(StoreId));
                    if (result > 0)
                        return "addedProduct";
                    else
                        return "failedToAdd";                                                                                     ///
                }
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response.equals("alreadyExists")) {
                    Toast.makeText(getApplicationContext(), "Product Already Exists", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("failedToAdd")) {
                    Toast.makeText(getApplicationContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("addedProduct")) {
                    Toast.makeText(getApplicationContext(), SubCategoryName + " Added", Toast.LENGTH_SHORT).show();
                    prodSubCatArrayList = dbProSubCatAdapter.getAllProSubCategories(CategoryName);
                    proSubCatAdapter = new ProSubCatAdapter(getApplicationContext(), prodSubCatArrayList);
                    lvprosubcat.setAdapter(proSubCatAdapter);
                    itemMenu.toggle();
                }


            }
        }.execute();

    }

    private boolean isProductAlreadyExistsOnLocal() {
        if (prodSubCatArrayList != null) {
            for (ProductSubCategory product : prodSubCatArrayList) {
                if (product.getProductSubCategoryName().equalsIgnoreCase(SubCategoryName))
                    return true;
            }
            return false;
        } else return false;
    }

    public void getAllSubCategoriesOnServer(final int StoreId, final String Key, final String CategoryName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ProSubCat.this);

            @Override
            protected void onPreExecute() {
              /*  dialog.setMessage("Getting Products For Your Store");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));
                nameValuePairs.add(new BasicNameValuePair("CategoryName", CategoryName));
                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_GET_ALL_SUBCATEGORIES, nameValuePairs);
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
                //dialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
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
                            productSubCategory = new ProductSubCategory();
                            productSubCategory.setProductSubCategoryName(subCategoryName);
                            prodSubCatArrayList.add(productSubCategory);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (prodSubCatArrayList != null && prodSubCatArrayList.size() > 0) {
                        proSubCatAdapter = new ProSubCatAdapter(getApplicationContext(), prodSubCatArrayList);
                        lvprosubcat = (ListView) findViewById(R.id.lv_prosubcat);
                        lvprosubcat.setAdapter(proSubCatAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Products Available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onMenuExpanded() {

    }

    @Override
    public void onMenuCollapsed() {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Categories.class));
    }
}
