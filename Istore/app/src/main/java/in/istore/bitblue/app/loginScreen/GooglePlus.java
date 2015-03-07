package in.istore.bitblue.app.loginScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

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
import java.io.InputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.home.HomePage;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;

public class GooglePlus extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean intentInProgress, signInClicked;
    private String GpersonName, Gemail, filePath, Status, UserType, Password, StaffName, StaffEmail, StaffKey, AdminName, AdminEmail, AdminKey;
    private static final int PROFILE_PIC_SIZE = 400, RC_SIGN_IN = 0, GPLUS_LOGIN = 1000;
    private int Gresponse, Id, StoreId, AdminId, StaffId, StaffTotalSale;
    private boolean isAdminEmail, isStaffEmail;
    private long Mobile;

    private GlobalVariables globalVariable;
    private GoogleApiClient googleApiClient;
    private ConnectionResult connectionResult;
    private DbLoginCredAdapter dbloginCredAdapter;
    private DbStaffAdapter dbStaffAdapter;
    private TinyDB tinyDB;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleplus);
        tinyDB = new TinyDB(this);
        dbloginCredAdapter = new DbLoginCredAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);
        Gresponse = getIntent().getIntExtra("google", 0);
        globalVariable = (GlobalVariables) getApplicationContext();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        if (!googleApiClient.isConnected()) {
            onConnected(savedInstanceState);
        }
    }

    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            // googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        signInWithGooglePlus();
    }

    private void signInWithGooglePlus() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(GooglePlus.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Logging in...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }


            @Override
            protected String doInBackground(String... strings) {
                resolveSignInError();
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.dismiss();
                getProfileInformationforGmail();
            }
        }.execute();
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
        }
    }

    private void getProfileInformationforGmail() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(GooglePlus.this);
            String personPhotoUrl;

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Logging in...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                        Person currentPerson = Plus.PeopleApi
                                .getCurrentPerson(googleApiClient);
                        GpersonName = currentPerson.getDisplayName();
                        personPhotoUrl = currentPerson.getImage().getUrl();
                        Gemail = Plus.AccountApi.getAccountName(googleApiClient);

                        globalVariable.setgName(GpersonName);
                        globalVariable.setgEmail(Gemail);

                        personPhotoUrl = personPhotoUrl.substring(0,
                                personPhotoUrl.length() - 2)
                                + PROFILE_PIC_SIZE;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                dialog.dismiss();
                new LoadProfileImage().execute(personPhotoUrl);
                isEmailForAdminOrStaff();
            }
        }.execute();
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(googleApiClient);
                GpersonName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                Gemail = Plus.AccountApi.getAccountName(googleApiClient);

                globalVariable.setgName(GpersonName);
                globalVariable.setgEmail(Gemail);

                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                new LoadProfileImage().execute(personPhotoUrl);
                isEmailForAdminOrStaff();
                // if (isAdminEmail) {
                //long adminMobile = dbloginCredAdapter.getAdminMobile(Gemail);
                //globalVariable.setAdminMobile(adminMobile);
                  /*  Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                    homePageFacebook.putExtra("gresponse", Gresponse);
                    homePageFacebook.putExtra("filePath", filePath);
                    startActivity(homePageFacebook);*/
                // } else if (isStaffEmail) {
                   /* Intent homePageFacebook = new Intent(getApplicationContext(), HomePage.class);
                    homePageFacebook.putExtra("gresponse", Gresponse);
                    homePageFacebook.putExtra("filePath", filePath);
                    startActivity(homePageFacebook);*/
                // proceedToLoginProcess();
                //  }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void isEmailForAdminOrStaff() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(GooglePlus.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Logging in...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("EmailId", Gemail));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_CHECK_EMAIL_EXISTS, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Mobile = jsonObject.getLong("Mobile");
                        Password = jsonObject.getString("Password");          //
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
                } else if (Password == null || Password.equals("null")) {
                    Intent staffMobile = new Intent(getApplicationContext(), StaffMobile.class);
                    staffMobile.putExtra("Gname", GpersonName);
                    staffMobile.putExtra("Gemail", Gemail);
                    staffMobile.putExtra("gresponse", Gresponse);
                    staffMobile.putExtra("filepath", filePath);
                    startActivity(staffMobile);
                } else {
                    proceedToLoginProcess();
                }
            }
        }.execute();
    }

    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(GooglePlus.this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                signInClicked = false;
                Toast.makeText(this, "Login Failed. Check Network", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginPage.class));
            }
            intentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!intentInProgress) {
            // Store the ConnectionResult for later usage
            connectionResult = result;
            if (signInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog dialog = new ProgressDialog(GooglePlus.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Logging in...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap profImage = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                profImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return profImage;
        }

        protected void onPostExecute(Bitmap profImage) {
            if (profImage != null) {
                dialog.dismiss();
                filePath = createImageFromBitmap(profImage);
                tinyDB.putString("googleFilePath", filePath);
            }
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
            File mFile = new File(Environment.getExternalStorageDirectory(), "GoogleImage.png");

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

    private void proceedToLoginProcess() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(GooglePlus.this);

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
                nameValuePairs.add(new BasicNameValuePair("Pass", Password));
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
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                } else if (UserType.equals("Admin")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        getAdminInfo();
                        Intent homepage = new Intent(getApplicationContext(), HomePage.class);
                        homepage.putExtra("gPlusLogin", GPLUS_LOGIN);
                        startActivity(homepage);
                    }
                } else if (UserType.equals("Staff")) {
                    if (Id == 0) {
                        Toast.makeText(getApplicationContext(), "Error:Incorrect Credentials", Toast.LENGTH_SHORT).show();
                    } else if (Id > 0) {
                        getStaffInfo();
                        Intent homepage = new Intent(getApplicationContext(), HomePage.class);
                        homepage.putExtra("gPlusLogin", GPLUS_LOGIN);
                        startActivity(homepage);
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

}
