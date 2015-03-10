package in.istore.bitblue.app.loginScreen;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class StoreName extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etName;
    private Button bDone;

    private long Mobile;
    private int StoreId;
    private String Name, Email, Passwd, StoreName, CreatedOn;
    private DbLoginCredAdapter loginCredAdapter;
    private GlobalVariables globalVariable;

    private TinyDB tinyDB;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

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
        toolTitle.setText("STORE NAME");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        tinyDB = new TinyDB(this);
        Name = getIntent().getStringExtra("Name");
        Email = getIntent().getStringExtra("Email");
        Passwd = getIntent().getStringExtra("Passwd");
        Mobile = getIntent().getLongExtra("Mobile", 0);
        StoreId = getIntent().getIntExtra("StoreId", 0);
        Passwd = globalVariable.getAdminPass();  //remove this if using api
        Date date = new Date();
        CreatedOn = DateUtil.convertToStringDateOnly(date);
        loginCredAdapter = new DbLoginCredAdapter(this);
        etName = (EditText) findViewById(R.id.et_storename_storename);
        bDone = (Button) findViewById(R.id.b_storename_done);
        bDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_storename_done:
                StoreName = etName.getText().toString();
                if (StoreName.equals("")) {
                    etName.setHint("Field Required");
                    etName.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                } else {
                    tinyDB.putString("StoreName", StoreName);
                    addAdminSignUpInfoToDBOnServer();
                    /*int result = loginCredAdapter.updateAdminInfo(Mobile, StoreId, StoreName);  //REmove if using api
                    if (result <= 0) {
                        Toast.makeText(this, "Not Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        String NotificationTitle = "BITSTORE PASSWORD";
                        String NotificationMessage = "Your Password is: " + Passwd;
                        sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                        startActivity(new Intent(this, LoginPage.class));
                    }     */                                       //
                }
                break;
        }
    }

    private void addAdminSignUpInfoToDBOnServer() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(in.istore.bitblue.app.loginScreen.StoreName.this);
                dialog.setMessage("Signing up...");
                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                in.istore.bitblue.app.utilities.json.JSONResponse.Status status = null;
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Name", Name));
                nameValuePairs.add(new BasicNameValuePair("Email", Email));
                nameValuePairs.add(new BasicNameValuePair("Mobile", String.valueOf(Mobile)));
                nameValuePairs.add(new BasicNameValuePair("Passwd", Passwd));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("Storename", StoreName));
                nameValuePairs.add(new BasicNameValuePair("CreatedOn", CreatedOn));
                String Response = jsonParser.makeAndroidHttpClientRequest(API.BITSTORE_ADMIN_SIGN_UP, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        status = new in.istore.bitblue.app.utilities.json.JSONResponse.Status();
                        status.setStatus(jsonObject.getString("status"));
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                    if (status != null) {
                        return status.getStatus();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String status) {
                dialog.dismiss();
                if (status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Mobile Number Added", Toast.LENGTH_SHORT).show();
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your Password is: " + Passwd;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                } else if (status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Mobile Number Already Exists", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void sendPasswordThroughNotification(String Title, String Message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(Title);
        mBuilder.setContentText(Message);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
