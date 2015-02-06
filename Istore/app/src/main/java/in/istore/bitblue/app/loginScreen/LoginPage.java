package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class LoginPage extends Activity implements View.OnClickListener {

    private SignInButton bGmail;
    private LoginButton bFacebook;
    private ImageView userImage;
    private Button bsignup, blogin, bForgotPass;
    private EditText etmobNum, etPass;
    private EditText[] allEditTexts;

    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    private String userName, userEmail, regid;
    private long Mobile;
    private int StaffId, StoreId;
    private String AdminName, Pass, StaffName, AdminEmail;

    private GlobalVariables globalVariable;
    private Bitmap bitmap;
    private DbLoginCredAdapter dbloginCredAdapter;
    private DbStaffAdapter dbStaffAdapter;

    private final static String LOGIN = "login";
    private SharedPreferences.Editor preflogin;

    //For Azure Notifications
    public static final String SENDER_ID = "838791774954";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    public static MobileServiceClient mClient;

    //For Facebook
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
        setContentView(R.layout.activity_login_page);

        //Gcm Setup
        gcm = GoogleCloudMessaging.getInstance(this);

        //Azure Setup
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        String connectionString = "Endpoint=sb://istore.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Kgoog4L8WY1+2g2vRf1ZJuKa7ttS2iEpVxegHGuLRcs=";
        hub = new NotificationHub("istorehub", connectionString, this);
        registerWithNotificationHubs();

        //Facebook Setup
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
        bFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends"));
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
                            boolean validCredforAdmin = dbloginCredAdapter.isValidCred(Mobile, Pass);
                            boolean validCredforStaff = dbStaffAdapter.isValidCred(Mobile, Pass);
                            if (validCredforAdmin) {
                                String[] adminNameAndEmail = dbloginCredAdapter.getAdminNameAndEmail(Mobile);
                                StoreId = dbloginCredAdapter.getStoreId(Mobile);
                                globalVariable.setStoreId(StoreId);
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
                            } else return null;
                        }

                        @Override
                        protected void onPostExecute(String creds) {
                            dialog.dismiss();
                            if (creds == null) {
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
                            }
                        }
                    }.execute();
                break;
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
