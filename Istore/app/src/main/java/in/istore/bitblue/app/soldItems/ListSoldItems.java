package in.istore.bitblue.app.soldItems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import in.istore.bitblue.app.adapters.SoldItemAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.utilities.DBHelper;

public class ListSoldItems extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener,
        AbsListView.OnScrollListener {
    private TextView tvnodata, toolTitle;
    private Toolbar toolbar;
    private ListView lvsoldproductList;
    private View footerView;
    private SearchView searchView;
    private MenuItem sortBy;

    private DbProductAdapter dbAdapter;
    private SoldItemAdapter listAdapter;
    private ArrayList<Product> soldproductArrayList;
    private String sold = "sold";
    private boolean loadingMoreItems;
    private int offset = 0;
    private int limit = 10;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
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
        soldproductArrayList = dbAdapter.getAllSoldProducts(sold, limit, offset);
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
                                        Log.e("Unable to delete Directory: ", "Istore");
                                    }
                                    int ret = dbAdapter.deleteAllProduct(sold);
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
        soldproductArrayList = dbAdapter.sortBy(column, status);
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
            if (dbAdapter != null) {
                productsList = dbAdapter.getAllSoldProducts(sold, limit, offset);
                return productsList;
            } else
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sold_items, menu);
        sortBy = menu.findItem(R.id.mi_sortBy);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mi_sortBy) {
            if (soldproductArrayList == null || soldproductArrayList.size() == 0) {
                Toast.makeText(this, "No Items to Sort", Toast.LENGTH_SHORT).show();
            } else {
                showDialogForSort();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
