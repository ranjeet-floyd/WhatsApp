package in.istore.bitblue.app.Stocks.listSoldStock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import in.istore.bitblue.app.adapters.SoldItemAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class ListSoldItems extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener,
        AbsListView.OnScrollListener {
    private TextView tvnodata, toolTitle;
    private Toolbar toolbar;
    private ListView lvsoldproductList;
    private View footerView;
    private SearchView searchView;

    private GlobalVariables globalVariable;
    private DbProductAdapter dbAdapter;
    private SoldItemAdapter listAdapter;
    private ArrayList<Product> soldproductArrayList = new ArrayList<Product>();

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Product product;

    private boolean loadingMoreItems;
    private int offset = 0, limit = 10, StoreId;
    private String UserType, Key;

    private FloatingActionsMenu itemMenu;
    private FloatingActionButton delAllItem, sortItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items);
        setToolbar();
        initViews();
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("SOLD ITEMS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        globalVariable = (GlobalVariables) getApplicationContext();
        UserType = globalVariable.getUserType();
        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_solditems_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);

        delAllItem = (FloatingActionButton) findViewById(R.id.fab_solditems_delallitem);
        delAllItem.setOnClickListener(this);

        sortItems = (FloatingActionButton) findViewById(R.id.fab_solditems_sortitem);
        sortItems.setOnClickListener(this);

        lvsoldproductList = (ListView) findViewById(R.id.lv_soldItem_itemlist);
        lvsoldproductList.setOnScrollListener(this);
        tvnodata = (TextView) findViewById(R.id.tv_soldItem_nodata);
        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listfooter, null, false);
        dbAdapter = new DbProductAdapter(this);
        lvsoldproductList.addFooterView(footerView);
        offset = 0;
        limit = 10;
        // soldproductArrayList = dbAdapter.getAllSoldProducts(limit, offset);
        getAllSoldProducts(StoreId, Key);
        if (soldproductArrayList == null || soldproductArrayList.size() == 0) {
            tvnodata.setVisibility(View.VISIBLE);
        } else {
            tvnodata.setVisibility(View.GONE);
            listAdapter = new SoldItemAdapter(this, soldproductArrayList);
            lvsoldproductList.setAdapter(listAdapter);
        }

        searchView = (SearchView) findViewById(R.id.sv_solditems_search);
        lvsoldproductList.setTextFilterEnabled(true);
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
            lvsoldproductList.clearTextFilter();
        } else {
            if (itemMenu.isExpanded()) {
                itemMenu.toggle();
                onMenuCollapsed();
            }
            lvsoldproductList.setFilterText(searchText);
        }
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastInScreen = firstVisibleItem + visibleItemCount;
        if ((lastInScreen == totalItemCount) && !(loadingMoreItems)) {
            new LoadMoreItems().execute();
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_solditems_delallitem:
                if (soldproductArrayList == null || soldproductArrayList.size() == 0) {
                    Toast.makeText(this, "No Items to Delete", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogForDelete();
                }
                break;
            case R.id.fab_solditems_sortitem:
                if (soldproductArrayList == null || soldproductArrayList.size() == 0) {
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
                                if (soldproductArrayList.size() == 0) {
                                    Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
                                } else {
                                    File dir = new File(getExternalFilesDir(""), "/Istore");
                                    try {
                                        FileUtils.deleteDirectory(dir);
                                    } catch (IOException e) {
                                        Log.e("Unable to delete: ", "Istore");
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
                        soldproductArrayList.clear();
                        listAdapter = new SoldItemAdapter(getApplicationContext(), soldproductArrayList);
                        lvsoldproductList.setAdapter(listAdapter);
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
                } else if ("date".equals(items[property])) {
                    sortItemsBy(items[2]);
                }
                dialog.dismiss();
                itemMenu.toggle();
            }
        });
        builder.show();
    }

    private void sortItemsBy(String column) {
        soldproductArrayList = dbAdapter.sortBy(column);
        if (soldproductArrayList != null) {
            listAdapter = new SoldItemAdapter(this, soldproductArrayList);
            lvsoldproductList.setAdapter(listAdapter);
            Toast.makeText(getApplicationContext(), "Items sorted by " + column, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMenuExpanded() {

    }

    @Override
    public void onMenuCollapsed() {

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
           /* if (dbAdapter != null) {                   REMOVE THIS
                productsList = dbAdapter.getAllSoldProducts(sold, limit, offset);
                return productsList;
            } else*/
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productsList) {
            if (productsList != null && productsList.size() > 0) {
                soldproductArrayList.addAll(productsList);
                listAdapter.notifyDataSetChanged();
            } else footerView.setVisibility(View.GONE);
            loadingMoreItems = false;
        }
    }

    private void getAllSoldProducts(final int StoreId, final String Key) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ListSoldItems.this);

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

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_ALLSOLDPRODUCTS, nameValuePairs);
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
                    Toast.makeText(getApplicationContext(), "No Products", Toast.LENGTH_LONG).show();
                }
                // categoryArrayList =getAllCategories(StoreId);
                // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                // lvcategories.setAdapter(categoryAdapter);
                else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String productId = jsonObject.getString("");
                            String productImage = jsonObject.getString("");
                            String productName = jsonObject.getString("");
                            String productAddedDate = jsonObject.getString("");
                            if (productId == null || productId.equals("null")) {
                                break;
                            }
                            product = new Product();
                            product.setId(productId);
                            product.setImage(convertStringtoByteArray(productImage));
                            product.setName(productName);
                            product.setAddedDate(productAddedDate);
                            soldproductArrayList.add(product);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (soldproductArrayList != null && soldproductArrayList.size() > 0) {
                        listAdapter = new SoldItemAdapter(getApplicationContext(), soldproductArrayList);
                        lvsoldproductList = (ListView) findViewById(R.id.lv_soldItem_itemlist);
                        lvsoldproductList.setAdapter(listAdapter);
                    } else
                        Toast.makeText(getApplicationContext(), "No Product Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private byte[] convertStringtoByteArray(String image) {
        String[] byteValues = image.substring(1, image.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }

}
