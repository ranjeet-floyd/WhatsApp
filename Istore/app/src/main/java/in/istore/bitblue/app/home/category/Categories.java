package in.istore.bitblue.app.home.category;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import in.istore.bitblue.app.adapters.CategoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCategoryAdapter;
import in.istore.bitblue.app.pojo.Category;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class Categories extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private SearchView searchView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem;
    private ListView lvcategories;
    private ProgressBar progressBar;

    private ArrayList<Category> categoryArrayList = new ArrayList<Category>();
    private ArrayList<Category> pendingCategories = new ArrayList<Category>();
    private ArrayList<String> categoryNamesList = new ArrayList<String>();
    private DbCategoryAdapter dbCategoryAdapter;
    private CategoryAdapter categoryAdapter;
    private GlobalVariables globalVariable;
    private TinyDB tinyDB;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Category category;

    private int StoreId;
    private String CategoryName;
    private String Key, UserType, Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("CATEGORIES");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_category_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);

        addNewItem = (FloatingActionButton) findViewById(R.id.fab_category_additem);
        addNewItem.setOnClickListener(this);

        searchView = (SearchView) findViewById(R.id.sv_category_search);

        lvcategories = (ListView) findViewById(R.id.lv_category);
        lvcategories.setTextFilterEnabled(true);

        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }
        StoreId = globalVariable.getStoreId();
        dbCategoryAdapter = new DbCategoryAdapter(this);
        getAllCategoriesOnServer(StoreId, Key);
        // getAllCategoriesOnLocal();
        setupSearchView();
    }

    private void getAllCategoriesOnLocal() {
        categoryArrayList = dbCategoryAdapter.getAllCategories();
        if (categoryArrayList != null && categoryArrayList.size() > 0) {
            categoryAdapter = new CategoryAdapter(this, categoryArrayList);
            lvcategories.setAdapter(categoryAdapter);
        } else Toast.makeText(this, "No Categories Available", Toast.LENGTH_SHORT).show();
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
            case R.id.fab_category_additem:
                showAddItemDialog();
                break;
        }
    }

    private void showAddItemDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.setTitle("Add a Category");

        final EditText etcatName = (EditText) dialog.findViewById(R.id.et_additem_dialog_categoryname);
        Button add = (Button) dialog.findViewById(R.id.b_additem_dialog_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CategoryName = etcatName.getText().toString();
                // addCategoryOnLocal();
                // fetchPendingUpdates();

                //Check for existing category on server before insert
                boolean isCategoryExists = checkForExistingCategoryOnServer(CategoryName);
                if (isCategoryExists)
                    Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_SHORT).show();
                else {
                    addCategoryForThisStoreOnServer();
                    categoryArrayList.clear();
                    getAllCategoriesOnServer(StoreId, Key);
                }

            }

        });

        dialog.show();
    }

    private void fetchPendingUpdates() {
        pendingCategories = dbCategoryAdapter.fetchPendingRowsToUpdate();
        // Intent updatePendingRows = new Intent(getApplicationContext(), CategoryService.class);
        // startActivity(updatePendingRows);

    }

    private void addCategoryOnLocal() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Categories.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Adding Category...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                if (isCategoryAlreadyPresentOnLocal(CategoryName))
                    return "alreadyExists";
                else {
                    long result = dbCategoryAdapter.addNewCategory(CategoryName, String.valueOf(StoreId));
                    if (result > 0)
                        return "addedCategory";
                    else
                        return "failedToAdd";                                                                                     ///
                }
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response.equals("alreadyExists")) {
                    Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("failedToAdd")) {
                    Toast.makeText(getApplicationContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("addedCategory")) {
                    Toast.makeText(getApplicationContext(), CategoryName + " Added", Toast.LENGTH_SHORT).show();
                    categoryArrayList = dbCategoryAdapter.getAllCategories();
                    categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                    lvcategories.setAdapter(categoryAdapter);
                    itemMenu.toggle();
                }
            }
        }.execute();

    }

    private boolean isCategoryAlreadyPresentOnLocal(String categoryName) {
        if (categoryArrayList != null) {
            for (Category category : categoryArrayList) {
                if (category.getCategoryName().equalsIgnoreCase(categoryName))
                    return true;
            }
            return false;
        } else return false;
    }

    private boolean checkForExistingCategoryOnServer(String categoryName) {
        for (Category category : categoryArrayList) {
            if (category.getCategoryName().equalsIgnoreCase(categoryName))
                return true;
        }
        return false;
    }

    private void addCategoryForThisStoreOnServer() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Categories.this);

            @Override
            protected void onPreExecute() {
               /* dialog.setMessage("Adding Category...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
                //Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("CategoryName", CategoryName));
                nameValuePairs.add(new BasicNameValuePair("Key", Key));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_CATEGORY, nameValuePairs);
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
                // dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    // categoryArrayList =getAllCategories(StoreId);
                    // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                    // lvcategories.setAdapter(categoryAdapter);
                    Toast.makeText(getApplicationContext(), "Added Category: " + CategoryName, Toast.LENGTH_SHORT).show();
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Category Already Exists", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void getAllCategoriesOnServer(final int StoreId, final String Key) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(Categories.this);

            @Override
            protected void onPreExecute() {
                /*dialog.setMessage("Getting Categories For Your Store");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();*/
                //Toast.makeText(getApplicationContext(), "Please Wait...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_ALL_CATEGORIES, nameValuePairs);
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
                            String categoryName = jsonObject.getString("CategoryName");
                            if (categoryName == null || categoryName.equals("null")) {
                                break;
                            }
                            category = new Category();
                            category.setCategoryName(categoryName);
                            categoryArrayList.add(category);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (categoryArrayList != null && categoryArrayList.size() > 0) {
                        categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                        lvcategories.setAdapter(categoryAdapter);
                        for (Category category : categoryArrayList) {
                            categoryNamesList.add(category.getCategoryName());
                        }
                        tinyDB = new TinyDB(getApplicationContext());
                        tinyDB.putList("CategoryNames", categoryNamesList);
                    } else
                        Toast.makeText(getApplicationContext(), "No Categories Available", Toast.LENGTH_SHORT).show();
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            lvcategories.clearTextFilter();
        } else {
            if (itemMenu.isExpanded()) {
                itemMenu.toggle();
                onMenuCollapsed();
            }
            lvcategories.setFilterText(searchText);
        }
        return true;
    }
}
