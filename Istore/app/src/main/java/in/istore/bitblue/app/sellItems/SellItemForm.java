package in.istore.bitblue.app.sellItems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.utilities.DbCursorAdapter;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class SellItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bBack, bEdit, bSell;
    private EditText etbarcode, etname, etdesc, etquantity, etprice;
    private String scanContent;
    private ImageView ivProdImage;
    private static final int CAPTURE_PIC_REQ = 2222;
    private GlobalVariables globalVariable;
    private int proImgCount = 1;
    private String imagePath, barcode, name, desc, quantity, price;
    private byte[] byteImage;
    private DbCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_item_form);
        scanContent = getIntent().getStringExtra("scanContentsellitem");
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SELL ITEMS");
    }

    private void initViews() {
        bSell = (Button) findViewById(R.id.b_sellitems_submit);
        bSell.setOnClickListener(this);

        bEdit = (Button) findViewById(R.id.b_sellitems_edit);
        bEdit.setOnClickListener(this);

        bBack = (Button) findViewById(R.id.b_sellitems_cancel);
        bBack.setOnClickListener(this);

        etbarcode = (EditText) findViewById(R.id.et_sellitems_barcode_prod_id);
        if (scanContent != null || scanContent != "")
            etbarcode.setText(scanContent);

        ivProdImage = (ImageView) findViewById(R.id.iv_sellitems_image);

        etbarcode = (EditText) findViewById(R.id.et_sellitems_barcode_prod_id);
        etname = (EditText) findViewById(R.id.et_sellitems_prod_name);
        etdesc = (EditText) findViewById(R.id.et_sellitems_prod_desc);
        etquantity = (EditText) findViewById(R.id.et_sellitems_prod_quantity);
        etprice = (EditText) findViewById(R.id.et_sellitems_prod_price);
    }


    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_sellitems_cancel:
                startActivity(new Intent(this, ListMyStock.class));
                break;
            case R.id.b_sellitems_edit:
                Toast.makeText(this,"Edit sell item",Toast.LENGTH_SHORT).show();
                etbarcode.setFocusableInTouchMode(true);
                etname.setFocusableInTouchMode(true);
                etdesc.setFocusableInTouchMode(true);
                etquantity.setFocusableInTouchMode(true);
                etprice.setFocusableInTouchMode(true);
                break;
            case R.id.b_sellitems_submit:
                break;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_items, menu);
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
}
