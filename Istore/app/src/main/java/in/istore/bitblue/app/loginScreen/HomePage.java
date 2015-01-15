package in.istore.bitblue.app.loginScreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.adapters.NavDrawAdapter;
import in.istore.bitblue.app.addItems.AddItems;
import in.istore.bitblue.app.data.ExportData;
import in.istore.bitblue.app.data.ImportData;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.sellItems.SellItems;
import in.istore.bitblue.app.soldItems.SoldItems;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class HomePage extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems, Glogout, Flogout;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawList;
    private List<NavDrawItems> navDrawItemsList;
    private NavDrawAdapter navDrawAdapter;
    private String GpersonName, Gemail, FpersonName, Femail;
    private int responseGmail, responseFacebook;
    private boolean intentInProgress, signInClicked;
    private static final int RC_SIGN_IN = 0;
    private static final int G_LOGOUT = 1;
    private static final int F_LOGOUT = 2;

    private static final int PROFILE_PIC_SIZE = 400;
    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private GlobalVariables globalVariable;

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

        //Check which button was selected on login page
        responseGmail = getIntent().getIntExtra("gmail", 0);
        responseFacebook = getIntent().getIntExtra("facebook", 0);
        globalVariable = (GlobalVariables) getApplicationContext();
        initViews();
        if (responseGmail == 1) {
            //Hide Facebook Logout button in nav Drawer
            Flogout.setVisibility(View.GONE);

            // Initializing google plus api client
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();
            if (!googleApiClient.isConnected()) {
               // onConnected(savedInstanceState);
            }

        } else if (responseFacebook == 2) {

            //Hide Google+ Logout button in nav Drawer
            Glogout.setVisibility(View.GONE);

            //If facebook then get all values
            FpersonName = globalVariable.getUserName();
            Femail = globalVariable.getUserEmail();
            //bitmap = globalVariable.getProfPic();
            String path = getIntent().getStringExtra("filePath");
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
            bitmap = BitmapFactory.decodeFile(path);
            if (FpersonName != null) {
                tvuserName.setText(FpersonName);
            }
            if (Femail != null)
                tvuserEmail.setText(Femail);

            if (bitmap != null)
                ivuserPic.setImageBitmap(bitmap);
        }
        //Setup Facebook profile
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setLogo(R.drawable.istore_title_remback);
    }

    private void initViews() {

        dbAdapter = new DbProductAdapter(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawer.setDrawerListener(actionBarDrawerToggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

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

        tvuserName = (TextView) findViewById(R.id.tv_username);
        tvuserEmail = (TextView) findViewById(R.id.tv_useremail);
        ivuserPic = (ImageView) findViewById(R.id.iv_prof_image);

        Glogout = (Button) findViewById(R.id.b_google_logout);
        Glogout.setOnClickListener(this);

        Flogout = (Button) findViewById(R.id.b_facebook_logout);
        Flogout.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        if (responseGmail == 1) {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
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

    @Override
    public void onConnected(Bundle arg0) {
        signInClicked = false;
        if (responseGmail == 1)
            signInWithGplus();
    }

    private void signInWithGplus() {
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
            getProfileInformationforGmail();
        }
    }

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
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (responseGmail == 1) {
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
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformationforGmail() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(googleApiClient);
                GpersonName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                Gemail = Plus.AccountApi.getAccountName(googleApiClient);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                if (responseGmail == 1) {
                    tvuserName.setText(GpersonName);
                    tvuserEmail.setText(Gemail);
                    new LoadProfileImage(ivuserPic).execute(personPhotoUrl);
                }
            } else {
                Toast.makeText(this, "Login Failed.Check Network", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginPage.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
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
            case 0:  //IMPORT DATA
                startActivity(new Intent(this, ImportData.class));
                break;

            case 1:  //EXPORT DATA
                startActivity(new Intent(this, ExportData.class));
                break;
        }
        navDrawList.setItemChecked(position, true);
        drawer.closeDrawer(Gravity.LEFT);
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
        if (id == R.id.action_exit) {
            finish();
            return false;
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
                Intent ViewSoldItem = new Intent(this, SoldItems.class);
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
            case R.id.b_google_logout:
                showConfirmationDialog("Google+", G_LOGOUT);
                break;
            case R.id.b_facebook_logout:
                showConfirmationDialog("Facebook", F_LOGOUT);
                break;
        }
    }

    private void showConfirmationDialog(String string, final int status) {
        new AlertDialog.Builder(HomePage.this)
                .setTitle("Confirm")
                .setMessage("Sign out from " + string + " ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (status == G_LOGOUT) {
                            signOutFromGplus();
                        } else if (status == F_LOGOUT) {
                            signOutFromFacebook();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private void signOutFromFacebook() {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                // run the closeAndClearTokenInformation which does the following
                // DOCS : Closes the local in-memory Session object and clears any persistent
                // cache related to the Session.
                session.closeAndClearTokenInformation();
                Session.setActiveSession(null);
            }
        }
        Toast.makeText(this, "Signed Out from Facebook", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginPage.class));
        finish();
    }

    private void signOutFromGplus() {
        if (responseGmail == 1) {
            if (googleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(googleApiClient);
                googleApiClient.disconnect();
                // googleApiClient.connect();
                drawer.closeDrawer(Gravity.LEFT);
                Toast.makeText(this, "Signed Out from Google+", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginPage.class));
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
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

            if (profImage != null) {
                profImg.setImageBitmap(profImage);
            }
        }
    }
}
