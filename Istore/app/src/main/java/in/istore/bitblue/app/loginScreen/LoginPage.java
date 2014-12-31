package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import in.istore.bitblue.app.R;

public class LoginPage extends Activity implements View.OnClickListener {
    private Button bGmail, bFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initViews();
    }

    private void initViews() {
        bGmail = (Button) findViewById(R.id.bGmail);
        bGmail.setOnClickListener(this);

        bFacebook = (Button) findViewById(R.id.bFacebook);
        bFacebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.bGmail:
                Intent Home1 = new Intent(this, HomePage.class);   //REMOVE THIS WHEN IMPLEMENTING API
                startActivity(Home1);
                break;
            case R.id.bFacebook:
                Intent Home2 = new Intent(this, HomePage.class);   //REMOVE THIS WHEN IMPLEMENTING API
                startActivity(Home2);
                break;
        }
    }
}
