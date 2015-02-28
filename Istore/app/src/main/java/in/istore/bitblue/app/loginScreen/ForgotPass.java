package in.istore.bitblue.app.loginScreen;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
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
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.Mail;
import in.istore.bitblue.app.utilities.API;

public class ForgotPass extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etMobile;
    private Button bSubmit;

    private String UserType, Mobile, Email, Password;
    private GlobalVariables globalVariable;
    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        globalVariable = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("FORGOT PASSWORD");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        etMobile = (EditText) findViewById(R.id.et_forgotpass_mobilenumber);
        bSubmit = (Button) findViewById(R.id.b_forgotpass_submit);
        bSubmit.setOnClickListener(this);
    }

    private void retrievePassword() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(ForgotPass.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Retrieving Password...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Mobile", Mobile));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_FORGOT_PASSWORD, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Email = jsonObject.getString("Email");
                        Password = jsonObject.getString("Password");
                        return Password;
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
                    Toast.makeText(getApplicationContext(), "Mobile Number Does not Exists", Toast.LENGTH_LONG).show();
                } else {
                    //sendMailToUser(Email, Password);      //commented due to internet unavailable
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your Password is: " + Password;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                }
            }
        }.execute();
    }

    private void sendMailToUser(final String Email, final String Password) {
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(ForgotPass.this);
                dialog.setTitle("");
                dialog.setMessage("Sending Mail...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();

            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return sendEmailTo(Email, Password);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result != null && result) {
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your Password is: " + Password;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                }
               /* if (result != null && result) {    //remove if using api
                    if (isMobileExists(Mobile)) {
                        Toast.makeText(getApplicationContext(), "Mobile Number Already Exists", Toast.LENGTH_LONG).show();
                    } else if (isEmailExists(Email)) {
                        Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Password has been sent to your Email", Toast.LENGTH_LONG).show();
                        long dbresult = loginCredAdapter.insertAdminInfo(Name, Email, Passwd, Mobile, StoreId);
                        if (dbresult <= 0) {
                        } else {
                            Intent storeName = new Intent(SignUpAdmin.this, StoreName.class);
                            storeName.putExtra("Mobile", Mobile);
                            storeName.putExtra("StoreId", StoreId);
                            startActivity(storeName);
                        }
                    }
               } else {
                    Toast.makeText(getApplicationContext(), "Mail sending failed Check Network", Toast.LENGTH_SHORT).show();
                }*/
            }
        }.execute();
    }

    private boolean sendEmailTo(String toPerson, String passwd) {
        Mail mail = new Mail("bitstorehelpdesk@gmail.com", "bitbluetech");
        String[] toArr = {toPerson};
        mail.setTo(toArr);
        mail.setFrom("bitstorehelpdesk@gmail.com");
        mail.setSubject("Your Password for BitStore App");
        mail.setBody("\nHi, User" +
                "\n Password for mobile number " + Mobile + " is:" + passwd);
        try {
            if (mail.send()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
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

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_forgotpass_submit:
                Mobile = etMobile.getText().toString();
                if (Mobile.equals("")) {
                    etMobile.setHint("Field Required");
                    etMobile.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    break;
                }
                retrievePassword();
                break;
        }
    }
}
