package in.istore.bitblue.app.home.Stocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.Stocks.addItem.AddItemForm;
import in.istore.bitblue.app.home.Stocks.listSoldStock.ListSoldItems;
import in.istore.bitblue.app.home.Stocks.listStock.CategoryList;
import in.istore.bitblue.app.home.Stocks.sellItem.SellItemsMenu;

public class Stocks extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("Stocks Menu ");
    }

    private void initViews() {
        blistStock = (Button) findViewById(R.id.b_stocks_list_my_stock);
        blistStock.setOnClickListener(this);

        bviewSoldItems = (Button) findViewById(R.id.b_stocks_view_sold_items);
        bviewSoldItems.setOnClickListener(this);

        bAddItems = (Button) findViewById(R.id.b_stocks_add_items);
        bAddItems.setOnClickListener(this);

        bSellItems = (Button) findViewById(R.id.b_stocks_sell_items);
        bSellItems.setOnClickListener(this);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_stocks_list_my_stock:
                Intent ListStock = new Intent(this, CategoryList.class);
                startActivity(ListStock);
                break;
            case R.id.b_stocks_view_sold_items:
                Intent ListSoldItem = new Intent(this, ListSoldItems.class);
                startActivity(ListSoldItem);
                break;
            case R.id.b_stocks_add_items:
                Intent AddItems = new Intent(this, AddItemForm.class);
                startActivity(AddItems);
                break;
            case R.id.b_stocks_sell_items:
                Intent SellItems = new Intent(this, SellItemsMenu.class);
                startActivity(SellItems);
                break;
        }
    }
}
