package in.istore.bitblue.app.listMyStock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.adapters.ListStockAdapter;
import in.istore.bitblue.app.addItems.AddItems;
import in.istore.bitblue.app.utilities.DBHelper;

public class ListMyStock extends ActionBarActivity
        implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener,
        AbsListView.OnScrollListener {

    private TextView tvnodata, toolTitle;
    private Toolbar toolbar;
    private View footerView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem, delAllItem, sortItems;
    private MenuItem sortBy;
    
    private DbProductAdapter dbAdapter;
    private ListStockAdapter listAdapter;
    private ListView lvproductList;
    private ArrayList<Product> productArrayList;
    private SearchView searchView;

    private boolean loadingMoreItems;
    private int offset = 0;
    private int limit = 10;
    private String available = "not sold";

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
            new LoadMoreItems().execute();
        }
    }

    private void initViews() {

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
        lvproductList.setOnScrollListener(this);

        footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listfooter, null, false);
        lvproductList.addFooterView(footerView);

        dbAdapter = new DbProductAdapter(this);
        offset = 0;
        limit = 10;
        productArrayList = dbAdapter.getAllProducts(available, limit, offset);
        listAdapter = new ListStockAdapter(this, productArrayList);
        lvproductList.setAdapter(listAdapter);


        //This condition becomes true on run and false on debug
        if (productArrayList == null || productArrayList.size() == 0) {
            tvnodata.setVisibility(View.VISIBLE);
        } else {
            tvnodata.setVisibility(View.GONE);
        }

        searchView = (SearchView) findViewById(R.id.sv_listmystock_search);
        lvproductList.setTextFilterEnabled(true);
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
        toolTitle.setText("LIST STOCK");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_my_stock, menu);
        sortBy = menu.findItem(R.id.sortBy);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sortBy) {
            if (productArrayList == null || productArrayList.size() == 0) {
                Toast.makeText(this, "No Items to Sort", Toast.LENGTH_SHORT).show();
            } else {
                showDialogForSort();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_listmystock_additem:
                Intent addItem = new Intent(this, AddItems.class);
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
               /* if (productArrayList == null || productArrayList.size() == 0) {
                    Toast.makeText(this, "No Items to Sort", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogForSort();
                }*/
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
                                        Log.e("Unable to delete Directory: ", "Istore");
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
        final String[] items = {DBHelper.COL_PROD_ID, DBHelper.COL_PROD_NAME, DBHelper.COL_PROD_DATE};
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
        String status = "not sold";
        productArrayList = dbAdapter.sortBy(column, status);
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
                productsList = dbAdapter.getAllProducts(available, limit, offset);
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
}
