package in.istore.bitblue.app.listMyStock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.addItems.AddItems;

public class ListMyStock extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private FloatingActionsMenu addItemMenu;
    private FloatingActionButton addNewItem;

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

        addNewItem=  (FloatingActionButton) findViewById(R.id.fab_listmystock_additem);
        addNewItem.setOnClickListener(this);
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
        }
    }
}
