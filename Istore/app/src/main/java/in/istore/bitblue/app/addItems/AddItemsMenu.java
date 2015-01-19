package in.istore.bitblue.app.addItems;

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

public class AddItemsMenu extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bBarcode, bManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        setToolbar();
        initViews();
    }

    private void initViews() {
        bBarcode = (Button) findViewById(R.id.b_add_items_barcode);
        bBarcode.setOnClickListener(this);

        bManual = (Button) findViewById(R.id.b_add_items_manual);
        bManual.setOnClickListener(this);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("ADD ITEMS");
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_add_items_barcode:
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
                break;
            case R.id.b_add_items_manual:
                Intent additem = new Intent(this, AddItemForm.class);
                startActivity(additem);
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
            Intent addItemForm = new Intent(this, AddItemForm.class);
            addItemForm.putExtra("scanContentaddItem", scanContent);
            startActivity(addItemForm);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
