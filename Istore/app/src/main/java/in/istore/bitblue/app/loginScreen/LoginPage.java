package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import in.istore.bitblue.app.R;

public class LoginPage extends Activity implements View.OnClickListener {
    private SignInButton bGmail;
    private LoginButton bFacebook;
    private static final int GMAIL = 1;
    private static final int FACEBOOK = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initViews();
    }

    private void initViews() {
        bGmail = (SignInButton) findViewById(R.id.sign_in_button);
        bGmail.setOnClickListener(this);
        setGooglePlusButtonText(bGmail, "Log in with Google+");

        bFacebook = (LoginButton) findViewById(R.id.authButton);
        bFacebook.setOnClickListener(this);
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
                homePageGmail.putExtra("gmail",GMAIL);
                startActivity(homePageGmail);
                break;
            case R.id.authButton:
                Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                homePageFacebook.putExtra("facebook",FACEBOOK);
                startActivity(homePageFacebook);
                break;
        }
    }
}
