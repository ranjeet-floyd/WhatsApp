package in.istore.bitblue.app.addItems;

import android.content.Intent;
import android.graphics.Bitmap;
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

import in.istore.bitblue.app.R;

public class AddItemForm extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Button bCaptureImage;
    private EditText etbarcode;
    private String scanContent;
    private ImageView ivProdImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_form);
        scanContent = getIntent().getStringExtra("scanContent");
        setToolbar();
        initViews();
    }

    private void initViews() {
        bCaptureImage = (Button) findViewById(R.id.b_additems_captureImage);
        bCaptureImage.setOnClickListener(this);

        etbarcode = (EditText) findViewById(R.id.et_additems_barcode_prod_id);
        if (scanContent != null || scanContent != "")
            etbarcode.setText(scanContent);

        ivProdImage = (ImageView) findViewById(R.id.iv_additems_image);
        
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText("ADD ITEMS");
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

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_additems_captureImage:
                        captureImage();
                break;

        }
    }

    private void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        ivProdImage.setImageBitmap(bp);
        //Use above image to store in database
    }


}
