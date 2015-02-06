package in.istore.bitblue.app.loginScreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.Mail;
import in.istore.bitblue.app.utilities.Store;

public class SignUpAdmin extends ActionBarActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView toolTitle;
    private EditText etname, etmobNum, etEmail;
    private Button bContinue;
    private EditText[] allEditTexts;

    private String Name, Email, Passwd;
    private int StoreId;
    private long Mobile;
    private DbLoginCredAdapter loginCredAdapter;
    private GlobalVariables globalVariable;

    private final static String STORE_ID = "StoreId";
    private SharedPreferences.Editor prefstoreid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        globalVariable = (GlobalVariables) getApplicationContext();
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {

        prefstoreid = getSharedPreferences(STORE_ID, MODE_PRIVATE).edit();
        etname = (EditText) findViewById(R.id.et_signup_name);
        etmobNum = (EditText) findViewById(R.id.et_signup_mobnum);
        etEmail = (EditText) findViewById(R.id.et_signup_email);

        allEditTexts = new EditText[]{etname, etmobNum, etEmail};
        bContinue = (Button) findViewById(R.id.b_signup_continue);
        bContinue.setOnClickListener(this);

        loginCredAdapter = new DbLoginCredAdapter(this);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_signup_continue:
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                CharSequence email = etEmail.getText().toString();
                checkForValidation(allEditTexts);
                boolean isValidEmail = checkEmailValidation(email);
                if (!isValidEmail) {
                    showHint(etEmail);
                    break;
                }

                //If everything is ok send email to user with randomly generated password
                StoreId = Store.generateStoreId();
                Passwd = Store.generatePassword();
                globalVariable.setAdminPass(Passwd);
                globalVariable.setStoreId(StoreId);
                prefstoreid.putInt("storeid", StoreId);
                prefstoreid.commit();
                Name = etname.getText().toString();
                Email = etEmail.getText().toString();
                Mobile = Long.parseLong(etmobNum.getText().toString());
                sendMailToUser(Email, Passwd);
                break;
        }
    }

    private void sendMailToUser(final String email, final String passwd) {
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(SignUpAdmin.this);
                dialog.setTitle("");
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();

            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return sendEmailTo(email, passwd);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result != null && result) {
                    if (isMobileExists(Mobile)) {
                        Toast.makeText(getApplicationContext(), "Mobile Number Already Exists", Toast.LENGTH_LONG).show();
                    } else if (isEmailExists(Email)) {
                        Toast.makeText(getApplicationContext(), "Email Already Exists", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
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
                }
            }
        }.execute();
    }

    private boolean isMobileExists(long mobile) {
        return loginCredAdapter.isMobileExists(mobile);
    }

    private boolean isEmailExists(String Email) {
        return loginCredAdapter.isEmailExists(Email);
    }

    private boolean sendEmailTo(String toPerson, String passwd) {
        Mail mail = new Mail("bitstorehelpdesk@gmail.com", "bitbluetech");

        String[] toArr = {toPerson};
        mail.setTo(toArr);
        mail.setFrom("bitstorehelpdesk@gmail.com");
        mail.setSubject("Your Password for BitStore App");
        mail.setBody("\nHi, " + Name +
                "\n Your Password is:" + passwd);
        try {
            if (mail.send()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void showHint(EditText editText) {
        editText.setText("");
        editText.setHint("Enter Proper Email");
        editText.setHintTextColor(getResources().getColor(R.color.material_red_A400));
    }

    private boolean checkEmailValidation(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
}
