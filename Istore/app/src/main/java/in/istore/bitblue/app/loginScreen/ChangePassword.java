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

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;

public class ChangePassword extends ActionBarActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etOldPass, etNewPass, etConfirmNewPass;
    private EditText[] allEditTexts;
    private Button bSubmit;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private GlobalVariables globalVariable;

    private String UserType, OldPass, NewPass, ConfirmNewPass, Key, Status, AdminEmail;
    private long Mobile;
    private int StoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("CHANGE PASSWORD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        etOldPass = (EditText) findViewById(R.id.et_changepass_oldpasswd);
        etNewPass = (EditText) findViewById(R.id.et_changepass_newpasswd);
        etConfirmNewPass = (EditText) findViewById(R.id.et_changepass_confirmnewpasswd);
        allEditTexts = new EditText[]{etOldPass, etNewPass, etConfirmNewPass};
        bSubmit = (Button) findViewById(R.id.b_changepass_submit);
        bSubmit.setOnClickListener(this);
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Mobile = globalVariable.getAdminMobile();
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Mobile = globalVariable.getStaffMobile();
            Key = globalVariable.getStaffKey();
            StoreId = globalVariable.getStoreId();
        }

    }

    private void checkForValidation(EditText[] allEditTexts) {
        for (EditText editText : allEditTexts) {
            if (editText.getText().toString().equals("")) {
                editText.setHint("Field Required");
                editText.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                break;
            }
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_changepass_submit:
                OldPass = etOldPass.getText().toString();
                NewPass = etNewPass.getText().toString();
                ConfirmNewPass = etConfirmNewPass.getText().toString();
                if (OldPass.equals("")) {
                    etOldPass.setHint("Field Required");
                    etOldPass.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (NewPass.equals("")) {
                    etNewPass.setHint("Field Required");
                    etNewPass.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (ConfirmNewPass.equals("")) {
                    etConfirmNewPass.setHint("Field Required");
                    etConfirmNewPass.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                } else if (isEqualNewAndOldPassword(OldPass, NewPass)) {
                    Toast.makeText(this, "Old and New Password must be different", Toast.LENGTH_LONG).show();
                    break;
                } else if (!isEqualNewAndConfirmPassword(NewPass, ConfirmNewPass)) {
                    Toast.makeText(this, "Password Mismatch", Toast.LENGTH_LONG).show();
                    break;
                } else
                    changePassword();
                break;
        }
    }

    private void changePassword() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ChangePassword.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                String Response = null;
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Mobile", String.valueOf(Mobile)));
                nameValuePairs.add(new BasicNameValuePair("OPass", OldPass));
                nameValuePairs.add(new BasicNameValuePair("NPass", NewPass));
                if (UserType.equals("Admin")) {
                    nameValuePairs.add(new BasicNameValuePair("AdminKey", Key));
                    Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_ADMIN_CHANGE_PASSWORD, nameValuePairs);
                } else if (UserType.equals("Staff")) {
                    nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                    Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_STAFF_CHANGE_PASSWORD, nameValuePairs);
                }
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        if (UserType.equals("Staff")) {
                            AdminEmail = jsonObject.getString("AdminEmail");
                            return AdminEmail;
                        } else if (UserType.equals("Admin")) {
                            Status = jsonObject.getString("status");
                            return Status;
                        }
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
                    clearField(allEditTexts);
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                    clearField(allEditTexts);
                } else if (AdminEmail != null) {
                    Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your new Password is: " + NewPass;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                    startActivity(new Intent(getApplicationContext(), HomePage.class));

                } else if (Status.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your new Password is: " + NewPass;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                } else if (Status.equals("2")) {
                    Toast.makeText(getApplicationContext(), "Password Change Failed", Toast.LENGTH_SHORT).show();
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

    private boolean isEqualNewAndConfirmPassword(String newPass, String confirmNewPass) {
        return newPass.equals(confirmNewPass);
    }

    private boolean isEqualNewAndOldPassword(String oldPass, String newPass) {
        return oldPass.equals(newPass);
    }

    private void clearField(EditText[] allEditTexts) {
        for (EditText editText :
                allEditTexts) {
            editText.setText("");
        }
    }
}
