package in.istore.bitblue.app.listMyStock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.DbCursorAdapter;
import in.istore.bitblue.app.adapters.ListStockAdapter;
import in.istore.bitblue.app.addItems.AddItems;

public class ListMyStock extends ActionBarActivity implements View.OnClickListener {

    private TextView tvnodata;

    private Toolbar toolbar;
    private FloatingActionsMenu addItemMenu;
    private FloatingActionButton addNewItem, delAllItem;

    private DbCursorAdapter dbAdapter;
    private ListStockAdapter listAdapter;
    private ListView lvproductList;
    private ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_stock);
        setToolbar();
        initViews();
    }

    private void initViews() {

        addItemMenu = (FloatingActionsMenu) findViewById(R.id.fab_listmystock_menu);
        addItemMenu.setOnClickListener(this);

        addNewItem = (FloatingActionButton) findViewById(R.id.fab_listmystock_additem);
        addNewItem.setOnClickListener(this);

        delAllItem = (FloatingActionButton) findViewById(R.id.fab_listmystock_delallitem);
        delAllItem.setOnClickListener(this);

        dbAdapter = new DbCursorAdapter(this);
        productArrayList = dbAdapter.getAllProducts();

        tvnodata = (TextView) findViewById(R.id.tv_listmystock_nodata);

        if (productArrayList == null || productArrayList.size() == 0) {
            tvnodata.setVisibility(View.VISIBLE);
        } else {
            tvnodata.setVisibility(View.GONE);
            listAdapter = new ListStockAdapter(this, productArrayList);
            lvproductList = (ListView) findViewById(R.id.lv_listmystock_itemlist);
            lvproductList.setAdapter(listAdapter);


        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("MY STOCK");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_my_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.fab_listmystock_additem:
                startActivity(new Intent(this, AddItems.class));
                break;

            case R.id.fab_listmystock_delallitem:
                showDialogForDelete();
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

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (addItemMenu.isExpanded()) {
            addItemMenu.toggle();
        } else {
            super.onBackPressed();
        }
    }
}
