package in.istore.bitblue.app.loginScreen;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class StoreName extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etName;
    private Button bDone;

    private long Mobile;
    private int StoreId;
    private String Passwd;
    private DbLoginCredAdapter loginCredAdapter;
    private GlobalVariables globalVariable;

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
        globalVariable = (GlobalVariables) getApplicationContext();
        Mobile = getIntent().getLongExtra("Mobile", 0);
        StoreId = getIntent().getIntExtra("StoreId", 0);
        Passwd = globalVariable.getAdminPass();
        loginCredAdapter = new DbLoginCredAdapter(this);
        etName = (EditText) findViewById(R.id.et_storename_storename);
        bDone = (Button) findViewById(R.id.b_storename_done);
        bDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_storename_done:
                String storeName = etName.getText().toString();
                if (storeName.equals("")) {
                    etName.setHint("Field Required");
                    etName.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                } else {
                    int result = loginCredAdapter.updateAdminInfo(Mobile, StoreId, storeName);
                    if (result <= 0) {
                        Toast.makeText(this, "Not Updated", Toast.LENGTH_SHORT).show();
                    } else {

                        String NotificationTitle = "BITSTORE PASSWORD";
                        String NotificationMessage = "Your Password is: " + Passwd;
                        sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                        startActivity(new Intent(this, LoginPage.class));
                    }
                }
                break;
        }
    }

    private void sendPasswordThroughNotification(String Title, String Message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(Title);
        mBuilder.setContentText(Message);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

}
