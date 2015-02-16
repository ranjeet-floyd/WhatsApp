package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class LoginPage extends Activity implements View.OnClickListener {

    private SignInButton bGmail;
    private LoginButton bFacebook;
    private ImageView userImage;
    private Button bsignup, blogin, bForgotPass;
    private EditText etmobNum, etPass;
    private EditText[] allEditTexts;
    private RadioGroup rgUserType;
    private RadioButton rbAdmin, rbStaff;

    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    private String userName, userEmail, regid;
    private long Mobile;
    private int Id, AdminId, StaffId, StoreId, StaffTotalSale;
    private String AdminName, AdminEmail, AdminKey, StaffName, StaffEmail, StaffKey, UserType, Pass;
    private GlobalVariables globalVariable;
    private Bitmap bitmap;
    private DbLoginCredAdapter dbloginCredAdapter;
    private DbStaffAdapter dbStaffAdapter;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    private final static String LOGIN = "login";
    private SharedPreferences.Editor preflogin;

    //For Azure Notifications
    public static final String SENDER_ID = "838791774954";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    public static MobileServiceClient mClient;

    //For Facebook
    private Facebook facebook;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChanged(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_page);

        //Gcm Setup
        gcm = GoogleCloudMessaging.getInstance(this);

        //Azure Setup
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        String connectionString = "Endpoint=sb://istore.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Kgoog4L8WY1+2g2vRf1ZJuKa7ttS2iEpVxegHGuLRcs=";
        hub = new NotificationHub("istorehub", connectionString, this);
        registerWithNotificationHubs();

        //Facebook Setup
        facebook = new Facebook("365374323640416");
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        initViews();
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    regid = gcm.register(SENDER_ID);
                    hub.register(regid);
                } catch (Exception e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
            }
        }.execute(null, null, null);
    }

    private void initViews() {
        preflogin = getSharedPreferences(LOGIN, MODE_PRIVATE).edit();

        dbloginCredAdapter = new DbLoginCredAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);

        bGmail = (SignInButton) findViewById(R.id.sign_in_button);
        bGmail.setOnClickListener(this);
        setGooglePlusButtonText(bGmail, "Sign In");

        bFacebook = (LoginButton) findViewById(R.id.authButton);
        bFacebook.setOnClickListener(this);
        bFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends", "publish_stream"));
        bFacebook.setText("Sign In");
        globalVariable = (GlobalVariables) getApplicationContext();

        bsignup = (Button) findViewById(R.id.b_login_signup);
        bsignup.setOnClickListener(this);

        blogin = (Button) findViewById(R.id.b_login_login);
        blogin.setOnClickListener(this);

        bForgotPass = (Button) findViewById(R.id.b_login_forgotpass);
        bForgotPass.setOnClickListener(this);
        etmobNum = (EditText) findViewById(R.id.et_login_mob_num);
        etPass = (EditText) findViewById(R.id.et_login_password);
        allEditTexts = new EditText[]{etmobNum, etPass};

        retrieveContactsAndSendToDB();

    }

    private void retrieveContactsAndSendToDB() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        ArrayList<String> contactNumbers = new ArrayList<String>();
        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactNumbers.add(phoneNumber);

        }
        phones.close();
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.sign_in_button:
                Intent googleplus = new Intent(getApplicationContext(), HomePage.class);
                googleplus.putExtra("google", GMAIL);
                startActivity(googleplus);
                break;

            case R.id.b_login_signup:
                clearField(allEditTexts);
                startActivity(new Intent(this, SignUpAdmin.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.b_login_forgotpass:
                startActivity(new Intent(this, ForgotPass.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.b_login_login:
                checkForValidation(allEditTexts);
                try {
                    Mobile = Long.parseLong(etmobNum.getText().toString());
                } catch (NumberFormatException nfe) {
                    checkForValidation(allEditTexts);
                    break;
                }
                Pass = etPass.getText().toString();
                if (Mobile > 0)
                    proceedToLoginProcess();
                break;
        }
    }

    private void proceedToLoginProcess() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(LoginPage.this);

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
                nameValuePairs.add(new BasicNameValuePair("Pass", Pass));
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
                    clearField(allEditTexts);
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                    clearField(allEditTexts);
                } else if (UserType.equals("Admin")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        Toast.makeText(getApplicationContext(), "Success:Login Admin", Toast.LENGTH_SHORT).show();
                        getAdminInfo();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                    }
                } else if (UserType.equals("Staff")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        Toast.makeText(getApplicationContext(), "Success:Login Staff", Toast.LENGTH_SHORT).show();
                        getStaffInfo();
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
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

    private void checkForValidation(EditText[] allEditTexts) {
        for (EditText editText : allEditTexts) {
            if (editText.getText().toString().equals("")) {
                editText.setHint("Field Required");
                editText.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                return;
            }
        }
    }

    private void clearField(EditText[] allEditTexts) {
        for (EditText editText :
                allEditTexts) {
            editText.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    // METHODS FACEBOOK
    public void onSessionStateChanged(final Session session, final SessionState state, final Exception exception) {
        if (session != null && session.isOpened()) {
            postOnWall("Test");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {

                        userName = user.getFirstName() + " " + user.getLastName();
                        userEmail = user.getProperty("email").toString();

                        globalVariable.setFbName(userName);
                        globalVariable.setFbEmail(userEmail);

                        ProfilePictureView ppv = (ProfilePictureView) findViewById(R.id.fbImg);
                        // ppv.setProfileId(user.getId());
                        userImage = ((ImageView) ppv.getChildAt(0));
                        bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                        String filePath = createImageFromBitmap(bitmap);

                        boolean isEmailExistForAdmin = dbloginCredAdapter.isEmailExists(userEmail);
                        boolean isEmailExistForStaff = dbStaffAdapter.isEmailExists(userEmail);
                        if (isEmailExistForAdmin) {
                            long adminMobile = dbloginCredAdapter.getAdminMobile(userEmail);
                            globalVariable.setAdminMobile(adminMobile);
                            Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                            homePageFacebook.putExtra("facebook", FACEBOOK);
                            homePageFacebook.putExtra("filePath", filePath);
                            startActivity(homePageFacebook);
                        } else if (isEmailExistForStaff) {
                            Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                            homePageFacebook.putExtra("facebook", FACEBOOK);
                            homePageFacebook.putExtra("filePath", filePath);
                            startActivity(homePageFacebook);
                        } else {
                            Intent staffMobile = new Intent(getApplicationContext(), StaffMobile.class);
                            staffMobile.putExtra("facebook", FACEBOOK);
                            startActivity(staffMobile);
                        }
                    }
                }
            }).executeAsync();
        } else {
            Log.e("Script", "Connection fail");
        }
    }

    public void postOnWall(final String msg) {
        new AsyncTask<String, String, String>() {
            String response;

            @Override
            protected String doInBackground(String... strings) {
                try {
                    Bundle parameters = new Bundle();
                    parameters.putString("message", "Timely App For Android");
                    parameters.putString("description", "Good Design");
                    response = facebook.request("me/feed", parameters,
                            "POST");

                    if (response == null || response.equals("") ||
                            response.equals("false")) {
                        return null;
                    }
                } catch (Exception e) {
                    return null;
                }
                return "success";
            }

            @Override
            protected void onPostExecute(String result) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Failed to post ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Posted on Facebook ", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public String createImageFromBitmap(Bitmap bmp) {
        try {
            int size = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArr = bos.toByteArray();
            bos.flush();
            bos.close();
            File mFile = new File(Environment.getExternalStorageDirectory(), "FbImage.png");

            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(bArr);
            fos.flush();
            fos.close();
            return mFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {

        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }
}
