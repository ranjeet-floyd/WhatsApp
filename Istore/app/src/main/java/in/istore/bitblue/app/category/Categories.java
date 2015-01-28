package in.istore.bitblue.app.category;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import in.istore.bitblue.app.adapters.CategoryAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCategoryAdapter;
import in.istore.bitblue.app.pojo.Category;

public class Categories extends ActionBarActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private SearchView searchView;
    private FloatingActionsMenu itemMenu;
    private FloatingActionButton addNewItem;
    private ListView lvcategories;

    private ArrayList<Category> categoryArrayList;
    private DbCategoryAdapter dbCategoryAdapter;
    private CategoryAdapter categoryAdapter;

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
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("CATEGORIES");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        itemMenu = (FloatingActionsMenu) findViewById(R.id.fab_category_menu);
        itemMenu.setOnFloatingActionsMenuUpdateListener(this);

        addNewItem = (FloatingActionButton) findViewById(R.id.fab_category_additem);
        addNewItem.setOnClickListener(this);

        searchView = (SearchView) findViewById(R.id.sv_category_search);

        lvcategories = (ListView) findViewById(R.id.lv_category);

        lvcategories.setTextFilterEnabled(true);
        dbCategoryAdapter = new DbCategoryAdapter(this);
        categoryArrayList = dbCategoryAdapter.getAllCategories();
        if (categoryArrayList != null) {
            categoryAdapter = new CategoryAdapter(this, categoryArrayList);
            lvcategories.setAdapter(categoryAdapter);
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
            case R.id.fab_category_additem:
                showAddItemDialog();
                break;
        }
    }

    private void showAddItemDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.setTitle("Add New Category");

        final EditText etcatName = (EditText) dialog.findViewById(R.id.et_additem_dialog_categoryname);
        Button add = (Button) dialog.findViewById(R.id.b_additem_dialog_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String categoryName = etcatName.getText().toString();
                if (dbCategoryAdapter.isCategoryAlreadyExist(categoryName)) {
                    Toast.makeText(getApplicationContext(), "Category Already Present", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    long result = dbCategoryAdapter.addNewCategory(categoryName);
                    if (result > 0) {
                        categoryArrayList = dbCategoryAdapter.getAllCategories();
                        categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                        lvcategories.setAdapter(categoryAdapter);
                        Toast.makeText(getApplicationContext(), "Added " + categoryName, Toast.LENGTH_SHORT).show();
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
