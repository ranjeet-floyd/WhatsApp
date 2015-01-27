package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

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
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class LoginPage extends Activity implements View.OnClickListener {
    private SignInButton bGmail;
    private LoginButton bFacebook;
    private ImageView userImage;
    private Button bsignup, blogin;
    private EditText etmobNum, etPass;
    private EditText[] allEditTexts;

    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    private String userName, userEmail, regid;
    private long Mobile;
    private String Pass;
    private GlobalVariables globalVariable;
    private Bitmap bitmap;
    private DbLoginCredAdapter loginCredAdapter;

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

        loginCredAdapter = new DbLoginCredAdapter(this);

        bGmail = (SignInButton) findViewById(R.id.sign_in_button);
        bGmail.setOnClickListener(this);
        setGooglePlusButtonText(bGmail, "Log in with Google+");

        bFacebook = (LoginButton) findViewById(R.id.authButton);
        bFacebook.setOnClickListener(this);
        bFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        globalVariable = (GlobalVariables) getApplicationContext();

        bsignup = (Button) findViewById(R.id.b_login_signup);
        bsignup.setOnClickListener(this);

        blogin = (Button) findViewById(R.id.b_login_login);
        blogin.setOnClickListener(this);

        etmobNum = (EditText) findViewById(R.id.et_login_mob_num);
        etPass = (EditText) findViewById(R.id.et_login_password);
        allEditTexts = new EditText[]{etmobNum, etPass};
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.sign_in_button:
                Intent googleplus = new Intent(getApplicationContext(), GooglePlus.class);
                googleplus.putExtra("gmail", GMAIL);
                startActivity(googleplus);
                break;
            case R.id.b_login_signup:
                clearField(allEditTexts);
                startActivity(new Intent(this, SignUpAdmin.class));
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
                    new AsyncTask<String, String, Boolean>() {
                        ProgressDialog dialog = new ProgressDialog(LoginPage.this);

                        @Override
                        protected void onPreExecute() {
                            dialog.setMessage("Logging in...");
                            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            dialog.setCancelable(false);
                            dialog.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... strings) {
                            boolean validCred = loginCredAdapter.isValidCred(Mobile, Pass);
                            if (validCred)
                                return true;
                            else return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean isValidCreds) {
                            dialog.dismiss();
                            if (!isValidCreds) {
                                clearField(allEditTexts);
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            } else {
                                globalVariable.setAdminMobile(Mobile);
                                startActivity(new Intent(LoginPage.this, HomePage.class));
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

        //Not sure to use this

       /* Session session = Session.getActiveSession();
        if (session != null && (session.isClosed() || session.isOpened())) {
            onSessionStateChanged(session, session.getState(), null);
        }*/
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
            Log.e("Script", "Connection success");
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

                        boolean isEmailExist = loginCredAdapter.isEmailExists(userEmail);
                        if (isEmailExist) {
                            long adminMobile = loginCredAdapter.getAdminMobile(userEmail);
                            Toast.makeText(getApplicationContext(), String.valueOf(adminMobile), Toast.LENGTH_SHORT).show();
                            globalVariable.setAdminMobile(adminMobile);
                            Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                            homePageFacebook.putExtra("facebook", FACEBOOK);
                            homePageFacebook.putExtra("filePath", filePath);
                            startActivity(homePageFacebook);
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

        // Find the TextView that is inside of the SignInButton and set its text
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
