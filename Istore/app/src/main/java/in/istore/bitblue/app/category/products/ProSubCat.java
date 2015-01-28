package in.istore.bitblue.app.category.products;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.ProSubCatAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProSubCatAdapter;
import in.istore.bitblue.app.pojo.ProductSubCategory;

public class ProSubCat extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private String CategoryName;
    private SearchView searchView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem;
    private ListView lvprosubcat;

    private ArrayList<ProductSubCategory> prodSubCatArrayList;
    private DbProSubCatAdapter dbProSubCatAdapter;
    private ProSubCatAdapter proSubCatAdapter;

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
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Products: " + CategoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_prosubcat_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);


        addNewItem = (FloatingActionButton) findViewById(R.id.fab_prosubcat_additem);
        addNewItem.setOnClickListener(this);
        searchView = (SearchView) findViewById(R.id.sv_prosubcat_search);

        lvprosubcat = (ListView) findViewById(R.id.lv_prosubcat);
        lvprosubcat.setTextFilterEnabled(true);
        dbProSubCatAdapter = new DbProSubCatAdapter(this);
        prodSubCatArrayList = dbProSubCatAdapter.getAllProSubCategories(CategoryName);
        if (prodSubCatArrayList != null) {
            proSubCatAdapter = new ProSubCatAdapter(this, prodSubCatArrayList);
            lvprosubcat.setAdapter(proSubCatAdapter);
        } else Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        setupSearchView();
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
        dialog.setTitle("Add New Product");

        final EditText etcatName = (EditText) dialog.findViewById(R.id.et_additem_dialog_categoryname);
        Button add = (Button) dialog.findViewById(R.id.b_additem_dialog_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String subcategoryName = etcatName.getText().toString();
                if (dbProSubCatAdapter.isCategoryAlreadyExist(subcategoryName)) {
                    Toast.makeText(getApplicationContext(), "Category Already Present", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    long result = dbProSubCatAdapter.addNewProSubCategory(CategoryName, subcategoryName);
                    if (result > 0) {
                        prodSubCatArrayList = dbProSubCatAdapter.getAllProSubCategories(CategoryName);
                        proSubCatAdapter = new ProSubCatAdapter(getApplicationContext(), prodSubCatArrayList);
                        lvprosubcat.setAdapter(proSubCatAdapter);
                        Toast.makeText(getApplicationContext(), "Added " + subcategoryName, Toast.LENGTH_SHORT).show();
                        itemMenu.toggle();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to Add", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
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
}
