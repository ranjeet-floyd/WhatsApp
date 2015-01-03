package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.Arrays;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class LoginPage extends Activity implements View.OnClickListener {
    private SignInButton bGmail;
    private LoginButton bFacebook;
    private ImageView userImage;
    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    private String userName, userEmail;
    private GlobalVariables globalVariable;
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
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        bGmail = (SignInButton) findViewById(R.id.sign_in_button);
        bGmail.setOnClickListener(this);
        setGooglePlusButtonText(bGmail, "Log in with Google+");

        bFacebook = (LoginButton) findViewById(R.id.authButton);
        bFacebook.setOnClickListener(this);
        bFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends"));

        globalVariable = (GlobalVariables) getApplicationContext();

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
            case R.id.authButton:
                break;
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
    public void onSessionStateChanged(final Session session, final SessionState state, Exception exception) {
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
                        ppv.setProfileId(user.getId());
                        userImage = ((ImageView) ppv.getChildAt(0));
                        Bitmap bitmap = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                        globalVariable.setProfPic(bitmap);
                        Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                        homePageFacebook.putExtra("facebook", FACEBOOK);
                        startActivity(homePageFacebook);
                    }
                }
            }).executeAsync();
        } else {
            Log.e("Script", "Connection fail");
        }
    }
}
