package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
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
import com.facebook.android.DialogError;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class LoginPage extends Activity implements View.OnClickListener {
    private SignInButton bGmail;
    private LoginButton bFacebook;
    private ImageView userImage;
    private Button bsignup;
    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    public static final String SENDER_ID = "838791774954";
    private String userName, userEmail, regid;

    private GlobalVariables globalVariable;
    private Bitmap bitmap;

    //For Azure Notifications
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

        //Gcm
        gcm = GoogleCloudMessaging.getInstance(this);

        //Azure Setup
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        String connectionString = "Endpoint=sb://istore.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Kgoog4L8WY1+2g2vRf1ZJuKa7ttS2iEpVxegHGuLRcs=";
        hub = new NotificationHub("istorehub", connectionString, this);
        registerWithNotificationHubs();
        //Facebook
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
                Toast.makeText(getApplicationContext(), "Device Registered: " + regid, Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    private void initViews() {
        bGmail = (SignInButton) findViewById(R.id.sign_in_button);
        bGmail.setOnClickListener(this);
        setGooglePlusButtonText(bGmail, "Log in with Google+");

        bFacebook = (LoginButton) findViewById(R.id.authButton);
        bFacebook.setOnClickListener(this);
        bFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        globalVariable = (GlobalVariables) getApplicationContext();

        bsignup = (Button) findViewById(R.id.b_login_signup);
        bsignup.setOnClickListener(this);

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

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.sign_in_button:
                Intent homePageGmail = new Intent(getApplicationContext(), HomePage.class);
                homePageGmail.putExtra("gmail", GMAIL);
                startActivity(homePageGmail);
                break;
            case R.id.b_login_signup:
                SocialAuthAdapter adapter = new SocialAuthAdapter(new ResponseListener());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1)
            Toast.makeText(getApplicationContext(), "Login Failed. Check Network", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Not sure whether to use this

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

                        globalVariable.setUserName(userName);
                        globalVariable.setUserEmail(userEmail);

                        ProfilePictureView ppv = (ProfilePictureView) findViewById(R.id.fbImg);
                        // ppv.setProfileId(user.getId());
                        userImage = ((ImageView) ppv.getChildAt(0));
                        bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                        String filePath = createImageFromBitmap(bitmap);
                        Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                        homePageFacebook.putExtra("facebook", FACEBOOK);
                        homePageFacebook.putExtra("filePath", filePath);
                        // startActivity(homePageFacebook);
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
            File mFile = new File(Environment.getExternalStorageDirectory(), "Image.png");

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

    private final class ResponseListener implements DialogListener {
        public void onComplete(Bundle values) {

            edit = (EditText) findViewById(R.id.editTxt);
            adapter.updateStatus(edit.getText().toString(), new MessageListener(), false);

        }

        @Override
        public void onError(SocialAuthError socialAuthError) {

        }

        public void onError(DialogError error) {
            Log.d("ShareButton", "Error");
        }

        public void onCancel() {
            Log.d("ShareButton", "Cancelled");
        }

        @Override
        public void onBack() {

        }
    }

    // To get status of message after authentication
    private final class MessageListener implements SocialAuthListener<Integer> {
        @Override
        public void onExecute(String s, Integer t) {
            Integer status = t;
            if (status == 200 || status == 201 || status == 204)
                Toast.makeText(LoginPage.this, "Message posted", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(LoginPage.this, "Message not posted", Toast.LENGTH_LONG).show();
        }

        public void onError(SocialAuthError e) {

        }
    }
}
