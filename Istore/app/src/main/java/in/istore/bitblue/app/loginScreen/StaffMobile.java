package in.istore.bitblue.app.loginScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class StaffMobile extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etMobile;
    private Button bSubmit, bSkip;

    private String  GEmail, FbEmail;
    private int responseGmail, responseFacebook;
    private DbStaffAdapter dbStaffAdapter;
    private GlobalVariables globalVariable;

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
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Staff Mobile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        dbStaffAdapter = new DbStaffAdapter(this);
        globalVariable = (GlobalVariables) getApplicationContext();

        responseGmail = getIntent().getIntExtra("gresponse", 0);
        responseFacebook = getIntent().getIntExtra("facebook", 0);

        if (responseGmail == 1) {
            GEmail = globalVariable.getgEmail();
        } else if (responseFacebook == 2) {
            FbEmail = globalVariable.getFbEmail();
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
                long Mobile = Long.parseLong(etMobile.getText().toString());
                if (responseGmail == 1) {
                    long result = dbStaffAdapter.addEmailforMobile(Mobile, GEmail);
                    if (result > 0) {
                        Toast.makeText(getApplicationContext(), "Email Added ", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Email Already Added", Toast.LENGTH_SHORT).show();
                    }
                } else if (responseFacebook == 2) {
                    long result = dbStaffAdapter.addEmailforMobile(Mobile, FbEmail);
                    if (result <= 0) {
                        Toast.makeText(getApplicationContext(), "Email Already Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Email Added ", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            case R.id.b_staffmobile_skip:
                startActivity(new Intent(this, SignUpAdmin.class));
                break;
        }
    }
}
