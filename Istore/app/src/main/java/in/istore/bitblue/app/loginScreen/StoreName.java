package in.istore.bitblue.app.loginScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.HomePage;

public class StoreName extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etName;
    private Button bDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_name);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Store Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        etName = (EditText) findViewById(R.id.et_storename_storename);
        bDone = (Button) findViewById(R.id.b_storename_done);
        bDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_storename_done:
                startActivity(new Intent(this, HomePage.class));
                break;
        }
    }
}
