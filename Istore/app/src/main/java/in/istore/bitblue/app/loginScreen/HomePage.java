package in.istore.bitblue.app.loginScreen;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.addItems.AddItems;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.navDrawer.NavDrawAdapter;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.sellItems.SellItems;
import in.istore.bitblue.app.viewSoldItems.ViewSoldItems;

public class HomePage extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawList;
    private List<NavDrawItems> navDrawItemsList;
    private NavDrawAdapter navDrawAdapter;
    private String personName, email;
    private int responseGmail, responseFacebook;
    private boolean intentInProgress, signInClicked;
    private Bundle args;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;

    // Your Facebook APP ID
    private static final String APP_ID = "365374323640416";

    // Instance of Facebook Class
    private Facebook facebook;
    private AsyncFacebookRunner mAsyncRunner;
    private SharedPreferences mPrefs;
    // Google client to interact with Google API
    private GoogleApiClient googleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private ConnectionResult connectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setToolbar();
        initViews();

        responseGmail = getIntent().getIntExtra("gmail", 0);
        Log.e("responseGmail", String.valueOf(responseGmail));

        responseFacebook = getIntent().getIntExtra("facebook", 0);
        Log.e("responseFacebook", String.valueOf(responseFacebook));

        if (responseGmail == 1)
        // Initializing google plus api client
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();
            onConnected(args);
        } else if (responseFacebook == 2)
        //Initializing facebook sdk
        {
                facebook = new Facebook(APP_ID);
                mAsyncRunner = new AsyncFacebookRunner(facebook);
                loginFacebook();//this method when called when you required..}
        }
    }

    private void loginFacebook() {
        if (!facebook.isSessionValid()) {
            facebook.authorize(this, new String[] { "email", "publish_stream",
                    "read_stream" }, new LoginDialogListener());
        } else {
            getProfileInformation();
        }

    }

    protected void onStart() {
        super.onStart();
        if (responseGmail == 1) {
            googleApiClient.connect();
        }
    }

    protected void onStop() {
        super.onStop();
        if (responseGmail == 1) {
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }
    }

    private void signInWithGplus() {
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
            getProfileInformationforGmail();
        }
    }
/*    *//**
     * Sign-out from google
     * *//*
    private void signOutFromGplus() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }
    }*/

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        signInClicked = false;
        signInWithGplus();
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformationforGmail() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(googleApiClient);
                personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                email = Plus.AccountApi.getAccountName(googleApiClient);
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                tvuserName = (TextView) findViewById(R.id.tv_username);
                tvuserName.setText(personName);

                tvuserEmail = (TextView) findViewById(R.id.tv_useremail);
                tvuserEmail.setText(email);

                ivuserPic = (ImageView) findViewById(R.id.iv_prof_image);
                new LoadProfileImage(ivuserPic).execute(personPhotoUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView profImg;

        public LoadProfileImage(ImageView profImg) {
            this.profImg = profImg;
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
            profImg.setImageBitmap(profImage);
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                signInClicked = false;
            }
            intentInProgress = false;
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else if (responseFacebook == 2)
            Session.getActiveSession().onActivityResult(this, requestCode, responseCode, intent);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setLogo(R.drawable.istore_title_remback);
    }

    private void initViews() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        navDrawItemsList = new ArrayList<NavDrawItems>();
        navDrawItemsList = getListItems();
        navDrawAdapter = new NavDrawAdapter(this, R.layout.navdrawitem, navDrawItemsList);

        navDrawList = (ListView) findViewById(R.id.lv_nav_drawer);
        navDrawList.setAdapter(navDrawAdapter);
        navDrawList.setOnItemClickListener(new DrawerItemClickListener());

        blistStock = (Button) findViewById(R.id.b_list_my_stock);
        blistStock.setOnClickListener(this);

        bviewSoldItems = (Button) findViewById(R.id.b_view_sold_items);
        bviewSoldItems.setOnClickListener(this);

        bAddItems = (Button) findViewById(R.id.b_add_items);
        bAddItems.setOnClickListener(this);

        bSellItems = (Button) findViewById(R.id.b_sell_items);
        bSellItems.setOnClickListener(this);
    }

    private List<NavDrawItems> getListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Import Data", R.drawable.importdata));
        drawerList.add(new NavDrawItems("Export Data", R.drawable.exportdata));
        return drawerList;
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);

        }
    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                Intent ListStock = new Intent(this, ListMyStock.class);  ///Remove this
                startActivity(ListStock);
                break;
            case 1:
                Intent SellItems = new Intent(this, SellItems.class);  //Remove this
                startActivity(SellItems);
                break;
        }
        navDrawList.setItemChecked(position, true);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_list_my_stock:
                Intent ListStock = new Intent(this, ListMyStock.class);
                startActivity(ListStock);
                break;
            case R.id.b_view_sold_items:
                Intent ViewSoldItem = new Intent(this, ViewSoldItems.class);
                startActivity(ViewSoldItem);
                break;
            case R.id.b_add_items:
                Intent AddItems = new Intent(this, AddItems.class);
                startActivity(AddItems);
                break;
            case R.id.b_sell_items:
                Intent SellItems = new Intent(this, SellItems.class);
                startActivity(SellItems);
                break;

        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    private class LoginDialogListener implements Facebook.DialogListener {

        public void onComplete(Bundle values) {
            try {

                getProfileInformation();

            } catch (Exception error) {
                Toast.makeText(HomePage.this, error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }

        public void onFacebookError(FacebookError error) {
            Toast.makeText(HomePage.this,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_LONG).show();
        }

        public void onError(DialogError error) {
            Toast.makeText(HomePage.this,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
            Toast.makeText(HomePage.this,
                    "Something went wrong. Please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void getProfileInformation() {
        try {

            JSONObject profile = Util.parseJson(facebook.request("me"));
            Log.e("Profile", "" + profile);

            final String mUserId = profile.getString("id");
            final String mUserToken = facebook.getAccessToken();
            final String mUserName = profile.getString("name");
            final String mUserEmail = profile.getString("email");

            runOnUiThread(new Runnable() {

                public void run() {

                    Log.e("FaceBook_Profile",""+mUserId+"\n"+mUserToken+"\n"+mUserName+"\n"+mUserEmail);

                    Toast.makeText(getApplicationContext(),
                            "Name: " + mUserName + "\nEmail: " + mUserEmail,
                            Toast.LENGTH_LONG).show();
                }

            });

        } catch (FacebookError e) {

            e.printStackTrace();
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
