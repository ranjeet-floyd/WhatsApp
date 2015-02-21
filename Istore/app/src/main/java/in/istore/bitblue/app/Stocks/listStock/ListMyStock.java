package in.istore.bitblue.app.Stocks.listStock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.addItem.AddItemForm;
import in.istore.bitblue.app.adapters.ListStockAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.ImageUtil;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class ListMyStock extends ActionBarActivity
        implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener,
        AbsListView.OnScrollListener {

    private TextView tvnodata, toolTitle;
    private SearchView searchView;
    private Toolbar toolbar;
    private View footerView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem, delAllItem, sortItems;

    private DbProductAdapter dbAdapter;
    private ListStockAdapter listAdapter;
    private ListView lvproductList;
    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private GlobalVariables globalVariable;
    private boolean loadingMoreItems;
    private int offset = 0, limit = 10, StoreId;
    private String UserType, Key, CategoryName;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_stock);
        setToolbar();
        initViews();
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if ((lastInScreen == totalItemCount) && !(loadingMoreItems)) {
            //  new LoadMoreItems().execute();
        }
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
        CategoryName = globalVariable.getCategoryName();

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_listmystock_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);

        addNewItem = (FloatingActionButton) findViewById(R.id.fab_listmystock_additem);
        addNewItem.setOnClickListener(this);

        delAllItem = (FloatingActionButton) findViewById(R.id.fab_listmystock_delallitem);
        delAllItem.setOnClickListener(this);

        sortItems = (FloatingActionButton) findViewById(R.id.fab_listmystock_sortitem);
        sortItems.setOnClickListener(this);

        tvnodata = (TextView) findViewById(R.id.tv_listmystock_nodata);

        lvproductList = (ListView) findViewById(R.id.lv_listmystock_itemlist);
        lvproductList.setTextFilterEnabled(true);

        //  lvproductList.setOnScrollListener(this);

      /*  footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listfooter, null, false);
        lvproductList.addFooterView(footerView);*/
/*
        dbAdapter = new DbProductAdapter(this);
        offset = 0;
        limit = 10;*/

        //SearchView for List Stock
        searchView = (SearchView) findViewById(R.id.sv_listmystock_search);

        getAllProductsForCategory(StoreId, Key, CategoryName);
        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Search Item");
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            lvproductList.clearTextFilter();
        } else {
            if (itemMenu.isExpanded()) {
                itemMenu.toggle();
                onMenuCollapsed();
            }
            lvproductList.setFilterText(searchText);
        }
        return true;
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText(CategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_listmystock_additem:
                Intent addItem = new Intent(this, AddItemForm.class);
                addItem.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(addItem);
                break;

            case R.id.fab_listmystock_delallitem:
                if (productArrayList == null || productArrayList.size() == 0) {
                    Toast.makeText(this, "No Items to Delete", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogForDelete();
                }
                break;
            case R.id.fab_listmystock_sortitem:
                if (productArrayList == null || productArrayList.size() == 0) {
                    Toast.makeText(this, "No Items to Sort", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogForSort();
                }
                break;
        }
    }

    private void showDialogForDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\tAre You Sure?." +
                "\n \tYou Will Lose All The Data.")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })
                .setNeutralButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                                if (productArrayList.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
                                } else {
                                    File dir = new File(getExternalFilesDir(""), "/Istore");
                                    try {
                                        FileUtils.deleteDirectory(dir);
                                    } catch (IOException e) {
                                        Log.e("Unable to Delete: ", "Istore");
                                    }
                                    int ret = dbAdapter.deleteAllProduct();
                                    if (ret < 0) {
                                        Toast.makeText(getApplicationContext(), "Error : When Deleting", Toast.LENGTH_SHORT).show();
                                    } else {
                                        showdeleteComplete();
                                    }
                                }
                            }
                        }

                );
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showdeleteComplete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("\tAll Items were Deleted")
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        productArrayList.clear();
                        listAdapter = new ListStockAdapter(getApplicationContext(), productArrayList);
                        lvproductList.setAdapter(listAdapter);
                        tvnodata.setVisibility(View.VISIBLE);
                        itemMenu.toggle();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showDialogForSort() {
        final String[] items = {DBHelper.COL_PROD_ID, DBHelper.COL_PROD_NAME, DBHelper.COL_PROD_ADDEDDATE};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort Items by");

        //Setting single choice item show a dialog box with radio buttons each item in item array is radio button
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int property) {
                if ("id".equals(items[property])) {
                    sortItemsBy(items[0]);
                } else if ("name".equals(items[property])) {
                    sortItemsBy(items[1]);
                } else if ("addeddOn".equals(items[property])) {
                    sortItemsBy(items[2]);
                }
                dialog.dismiss();
                itemMenu.toggle();
            }
        });
        builder.show();
    }

    private void sortItemsBy(String column) {
        productArrayList = dbAdapter.sortBy(column);
        if (productArrayList != null) {
            listAdapter = new ListStockAdapter(this, productArrayList);
            lvproductList.setAdapter(listAdapter);
            Toast.makeText(getApplicationContext(), "Items sorted by " + column, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (itemMenu.isExpanded()) {
            itemMenu.toggle();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMenuExpanded() {/*
        lvproductList.setAlpha(0.1f);
        lvproductList.setVisibility(View.INVISIBLE);*/
    }

    @Override
    public void onMenuCollapsed() {/*
        lvproductList.setAlpha(1.0f);
        lvproductList.setVisibility(View.VISIBLE);*/

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    }

    private class LoadMoreItems extends AsyncTask<String, String, ArrayList<Product>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<Product> doInBackground(String... strings) {
            ArrayList<Product> productsList;
            loadingMoreItems = true;
            offset += 10;
            if (dbAdapter != null) {
                productsList = dbAdapter.getAllProducts(limit, offset);
                return productsList;
            } else
                return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productsList) {
            if (productsList != null && productsList.size() > 0) {
                productArrayList.addAll(productsList);
                listAdapter.notifyDataSetChanged();
            } else footerView.setVisibility(View.GONE);
            loadingMoreItems = false;
        }
    }

    private void getAllProductsForCategory(final int StoreId, final String Key, final String CategoryName) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ListMyStock.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Products...");
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

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_ALLPRODUCTS_FORCATEGORY, nameValuePairs);
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
                    Toast.makeText(getApplicationContext(), "No Products For this Category", Toast.LENGTH_LONG).show();
                }
                // categoryArrayList =getAllCategories(StoreId);
                // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                // lvcategories.setAdapter(categoryAdapter);
                else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String productId = jsonObject.getString("Id");
                            String productImage = jsonObject.getString("Image");
                            String productName = jsonObject.getString("Name");
                            String productAddedDate = jsonObject.getString("AddedOn");
                            if (productId == null || productId.equals("null")) {
                                break;
                            }
                            product = new Product();
                            product.setId(productId);
                            product.setImage(ImageUtil.convertBase64ImagetoByteArrayImage(productImage));
                            product.setName(productName);
                            product.setAddedDate(productAddedDate);
                            productArrayList.add(product);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (productArrayList != null && productArrayList.size() > 0) {
                        listAdapter = new ListStockAdapter(getApplicationContext(), productArrayList);
                        lvproductList.setAdapter(listAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Product Available", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
