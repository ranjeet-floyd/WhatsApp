package in.istore.bitblue.app.loginScreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class StaffMobile extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etMobile;
    private Button bSubmit, bSkip;

    private String Gname, Fbname, GEmail, FbEmail, Name, Email, Password, filepath;
    private int responseGmail, responseFacebook;

    private long Mobile;
    private int Id, AdminId, StaffId, StoreId, StaffTotalSale;
    private String AdminName, AdminEmail, AdminKey, StaffName, StaffEmail, StaffKey, UserType;


    private TinyDB tinyDB;
    private DbStaffAdapter dbStaffAdapter;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_mobile);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("NEW USER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        tinyDB = new TinyDB(this);
        dbStaffAdapter = new DbStaffAdapter(this);
        globalVariable = (GlobalVariables) getApplicationContext();

        responseGmail = getIntent().getIntExtra("gresponse", 0);
        responseFacebook = getIntent().getIntExtra("facebook", 0);
        Gname = getIntent().getStringExtra("GName");
        GEmail = getIntent().getStringExtra("Gemail");
        Fbname = getIntent().getStringExtra("GName");
        FbEmail = getIntent().getStringExtra("Gemail");
        filepath = getIntent().getStringExtra("filepath");
        if (responseGmail == 1000) {
            Name = globalVariable.getgName();
            Email = globalVariable.getgEmail();
        } else if (responseFacebook == 2000) {
            Name = globalVariable.getFbName();
            Email = globalVariable.getFbEmail();
        }

        etMobile = (EditText) findViewById(R.id.et_staffmobile_mobile);
        bSubmit = (Button) findViewById(R.id.b_staffmobile_submit);
        bSkip = (Button) findViewById(R.id.b_staffmobile_skip);

        bSubmit.setOnClickListener(this);
        bSkip.setOnClickListener(this);

    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_staffmobile_submit:
                Mobile = Long.parseLong(etMobile.getText().toString());
                if (responseGmail == 1000 || responseFacebook == 2000) {
                    addStaffEmailForMobile();
                }
                break;
            case R.id.b_staffmobile_skip:
                Intent signUp = new Intent(this, SignUpAdmin.class);
                signUp.putExtra("GNameAdmin", Name);
                signUp.putExtra("GEmailAdmin", Email);
                startActivity(signUp);
                break;
        }
    }

    private void addStaffEmailForMobile() {

        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(StaffMobile.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Logging in...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }


            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("MobileNumber", String.valueOf(Mobile)));
                nameValuePairs.add(new BasicNameValuePair("EmailId", Email));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_STAFF_EMAIL, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Password = jsonObject.getString("Password");
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;

            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                } else if (Password == null) {
                    Toast.makeText(getApplicationContext(), "---", Toast.LENGTH_LONG).show();
                } else {
                    proceedToLoginProcess();
                }
            }
        }.execute();
    }

    private void proceedToLoginProcess() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(StaffMobile.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Logging in...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                /*boolean validCredforAdmin = dbloginCredAdapter.isValidCred(Mobile, Pass);   //remove this if using api
                boolean validCredforStaff = dbStaffAdapter.isValidCred(Mobile, Pass);
                if (validCredforAdmin) {
                    String[] adminNameAndEmail = dbloginCredAdapter.getAdminNameAndEmail(Mobile);
                    StoreId = dbloginCredAdapter.getStoreId(Mobile);
                    globalVariable.setStoreId(StoreId);
                    globalVariable.setAdminId(0);
                    AdminName = adminNameAndEmail[0];
                    AdminEmail = adminNameAndEmail[1];
                    return "credAdmin";
                } else if (validCredforStaff) {
                    StaffName = dbStaffAdapter.getStaffName(Mobile);
                    StaffId = dbStaffAdapter.getStaffId(Mobile);
                    StoreId = dbStaffAdapter.getStoreId(Mobile);
                    globalVariable.setStaffId(StaffId);
                    globalVariable.setStoreId(StoreId);
                    return "credStaff";
                } else return null;*/
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Mobile", String.valueOf(Mobile)));
                nameValuePairs.add(new BasicNameValuePair("Pass", Password));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_LOGIN, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        UserType = jsonObject.getString("UserType");
                        Id = jsonObject.getInt("ID");
                        StoreId = jsonObject.getInt("StoreId");
                        return UserType;
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
               /* if (Response == null) {                 //remove this if using api
                    clearField(allEditTexts);
                } else if (StaffId <= 0) {
                    globalVariable.setAdminMobile(Mobile);
                    Intent HomePage = new Intent(LoginPage.this, HomePage.class);
                    preflogin.putString("Name", AdminName);
                    preflogin.putString("Email", AdminEmail);
                    preflogin.putLong("Mobile", Mobile);
                    preflogin.commit();
                    startActivity(HomePage);
                } else if (StaffId > 0) {
                    Intent HomePage = new Intent(LoginPage.this, HomePage.class);
                    preflogin.putString("Name", StaffName);
                    preflogin.putLong("Mobile", Mobile);
                    preflogin.commit();
                    startActivity(HomePage);
                }           */                       //
                if (Response == null) {
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                } else if (UserType.equals("Admin")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        tinyDB.putBoolean("FirstLogin", true);
                        getAdminInfo();
                        Intent homepage = new Intent(getApplicationContext(), HomePage.class);
                        if (responseGmail == 1000) {
                            homepage.putExtra("gPlusLogin", responseGmail);
                            startActivity(homepage);
                        } else if (responseFacebook == 2000) {
                            homepage.putExtra("fBLogin", responseFacebook);
                            startActivity(homepage);
                        }
                    }
                } else if (UserType.equals("Staff")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        tinyDB.putBoolean("FirstLogin", true);
                        getStaffInfo();
                        Intent homepage = new Intent(getApplicationContext(), HomePage.class);
                        if (responseGmail == 1000) {
                            homepage.putExtra("gPlusLogin", responseGmail);
                            startActivity(homepage);
                        } else if (responseFacebook == 2000) {
                            homepage.putExtra("fBLogin", responseFacebook);
                            startActivity(homepage);
                        }
                    }
                } else if (UserType.equals("NONE")) {
                    Toast.makeText(getApplicationContext(), "No Account Found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void getStaffInfo() {

        try {
            StaffId = Id;
            StaffName = jsonObject.getString("Name");
            StaffEmail = jsonObject.getString("Email");
            StaffTotalSale = jsonObject.getInt("StaffTotalSale");
            StaffKey = jsonObject.getString("Key");

            globalVariable.setUserType(UserType);
            globalVariable.setStoreId(StoreId);
            globalVariable.setStaffId(StaffId);
            globalVariable.setStaffName(StaffName);
            globalVariable.setStaffMobile(Mobile);
            globalVariable.setStaffEmail(StaffEmail);
            globalVariable.setStaffTotalSales(StaffTotalSale);
            globalVariable.setStaffKey(StaffKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAdminInfo() {
        try {
            AdminId = Id;
            AdminName = jsonObject.getString("Name");
            AdminEmail = jsonObject.getString("Email");
            AdminKey = jsonObject.getString("Key");

            globalVariable.setUserType(UserType);
            globalVariable.setStoreId(StoreId);
            globalVariable.setAdminId(AdminId);
            globalVariable.setAdminName(AdminName);
            globalVariable.setAdminMobile(Mobile);
            globalVariable.setAdminEmail(AdminEmail);
            globalVariable.setAdminKey(AdminKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
