package in.istore.bitblue.app.sellItems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.listMyStock.ListMyStock;

public class SellItems extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bBarcode, bFromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_items);
        setToolbar();
        initViews();

    }

    private void initViews() {
        bBarcode = (Button) findViewById(R.id.b_sell_items_barcode);
        bBarcode.setOnClickListener(this);

        bFromList = (Button) findViewById(R.id.b_sell_items_fromlist);
        bFromList.setOnClickListener(this);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("SELL ITEM");
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell_items, menu);
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
    }*/

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_sell_items_barcode:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;
            case R.id.b_sell_items_fromlist:
                Intent sellitem = new Intent(this, ListMyStock.class);
                startActivity(sellitem);
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result

            //scanFormat not used currently
            String scanFormat = scanningResult.getFormatName();

            String scanContent = scanningResult.getContents();
            Intent sellItemForm = new Intent(this, SellItemForm.class);
            sellItemForm.putExtra("scanContentsellitem", scanContent);
            startActivity(sellItemForm);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
