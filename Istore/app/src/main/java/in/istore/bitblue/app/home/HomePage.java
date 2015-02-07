package in.istore.bitblue.app.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.NavDrawAdapter;
import in.istore.bitblue.app.addItems.AddItemsMenu;
import in.istore.bitblue.app.adminMenu.custInfo.CusInfoContent;
import in.istore.bitblue.app.adminMenu.staffMgmt.StaffMgntContent;
import in.istore.bitblue.app.adminMenu.suppInfo.SuppInfoContent;
import in.istore.bitblue.app.adminMenu.transactions.Trans;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.category.Categories;
import in.istore.bitblue.app.cloudprint.CloudPrint;
import in.istore.bitblue.app.data.ExportData;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.invoice.Invoice;
import in.istore.bitblue.app.listStock.ListMyStock;
import in.istore.bitblue.app.loginScreen.LoginPage;
import in.istore.bitblue.app.loginScreen.StaffMobile;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.sellItems.SellItemsMenu;
import in.istore.bitblue.app.soldItems.ListSoldItems;
import in.istore.bitblue.app.staffMenu.custInfo.CusInfoForStaffContent;
import in.istore.bitblue.app.staffMenu.transactions.TransStaff;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class HomePage extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems, Glogout, Flogout, blogout;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawListAdmin, navDrawListStaff;

    private String FpersonName, Femail, GpersonName, Gemail, Name, Email;
    private int responseGmail, responseFacebook, StaffId, Gresponse;
    private static final int G_LOGOUT = 1;
    private static final int F_LOGOUT = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private long Mobile;
    private final static String LOGIN = "login";
    private boolean intentInProgress, signInClicked;

    private SharedPreferences preflogin;
    private List<NavDrawItems> navDrawItemsListForAdmin, navDrawItemsListForStaff;
    private NavDrawAdapter navDrawAdapter;
    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private String filePath;
    private GlobalVariables globalVariable;
    private ConnectionResult connectionResult;
    private DbLoginCredAdapter dbloginCredAdapter;
    private DbStaffAdapter dbStaffAdapter;

    // Google client to interact with Google API
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setToolbar();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        //Check which button was selected on login page
        responseGmail = getIntent().getIntExtra("google", 0);
        responseFacebook = getIntent().getIntExtra("facebook", 0);
        preflogin = getSharedPreferences(LOGIN, MODE_PRIVATE);
        globalVariable = (GlobalVariables) getApplicationContext();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        Mobile = preflogin.getLong("Mobile", 0);
        dbStaffAdapter = new DbStaffAdapter(this);
        StaffId = dbStaffAdapter.getStaffId(Mobile);
        initViews();
        if (responseGmail == 1) {
            //Hide Facebook Logout button in nav Drawer
            Flogout.setVisibility(View.GONE);
            blogout.setVisibility(View.GONE);
            if (!googleApiClient.isConnected()) {
                onConnected(savedInstanceState);
            }
        } else if (responseFacebook == 2) {
            //Hide Google+ Logout button in nav Drawer
            Glogout.setVisibility(View.GONE);
            blogout.setVisibility(View.GONE);

            //If facebook then get all values
            FpersonName = globalVariable.getFbName();
            Femail = globalVariable.getFbEmail();
            //bitmap = globalVariable.getProfPic();
            String path = getIntent().getStringExtra("filePath");
            bitmap = BitmapFactory.decodeFile(path);
            if (FpersonName != null) {
                tvuserName.setText(FpersonName);
            }
            if (Femail != null)
                tvuserEmail.setText(Femail);

            if (bitmap != null)
                ivuserPic.setImageBitmap(bitmap);
        } else if (StaffId <= 0) {
            Name = preflogin.getString("Name", "");
            tvuserName.setText(Name);
            Email = preflogin.getString("Email", "");
            globalVariable.setEmail(Email);
            tvuserEmail.setText(Email);
            Flogout.setVisibility(View.GONE);
            Glogout.setVisibility(View.GONE);

        } else if (StaffId > 0) {
            Name = preflogin.getString("Name", "");
            tvuserName.setText(Name);
            tvuserEmail.setText("");
            Flogout.setVisibility(View.GONE);
            Glogout.setVisibility(View.GONE);
        }
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
        dbloginCredAdapter = new DbLoginCredAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawer.setDrawerListener(actionBarDrawerToggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (StaffId <= 0) {
            navDrawItemsListForAdmin = new ArrayList<NavDrawItems>();

            navDrawItemsListForAdmin = getAdminListItems();
            navDrawAdapter = new NavDrawAdapter(this, R.layout.navdrawitem, navDrawItemsListForAdmin);

            navDrawListAdmin = (ListView) findViewById(R.id.lv_nav_drawer);
            navDrawListAdmin.setAdapter(navDrawAdapter);
            navDrawListAdmin.setOnItemClickListener(new DrawerItemClickListener());
        } else if (StaffId > 0) {
            navDrawItemsListForStaff = new ArrayList<NavDrawItems>();
            navDrawItemsListForStaff = getStaffListItems();
            navDrawAdapter = new NavDrawAdapter(this, R.layout.navdrawitem, navDrawItemsListForStaff);

            navDrawListStaff = (ListView) findViewById(R.id.lv_nav_drawer);
            navDrawListStaff.setAdapter(navDrawAdapter);
            navDrawListStaff.setOnItemClickListener(new DrawerItemClickListener());
        }
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

        blogout = (Button) findViewById(R.id.b_homepage_logout);
        blogout.setOnClickListener(this);
    }

    private List<NavDrawItems> getAdminListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Staff Management", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Supplier Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Transactions", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Invoice", R.drawable.ic_action_computer));
        return drawerList;
    }

    private List<NavDrawItems> getStaffListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Transactions", R.drawable.ic_action_computer));
        return drawerList;
    }

    private void selectItem(int position) {
        if (StaffId <= 0) {
            switch (position) {
                case 0: //Staff Managewment
                    startActivity(new Intent(this, StaffMgntContent.class));
                    break;

                case 1: //Supplier Info
                    startActivity(new Intent(this, SuppInfoContent.class));
                    break;

                case 2://Customer Info
                    startActivity(new Intent(this, CusInfoContent.class));
                    break;

                case 3: //Transaction
                    startActivity(new Intent(this, Trans.class));
                    break;

                case 4:// Import Data
                    startActivity(new Intent(this, Invoice.class));
                    break;

                case 5: //Export Data
                    startActivity(new Intent(this, ExportData.class));
                    break;

                case 6: //Cloud Print
                    showCloudPrintDialog();
                    break;
            }
            navDrawListAdmin.setItemChecked(position, true);
        } else if (StaffId > 0) {
            switch (position) {
                case 0: //Customer Info for Staff
                    startActivity(new Intent(this, CusInfoForStaffContent.class));
                    break;

                case 1: //Supplier Info
                    startActivity(new Intent(this, TransStaff.class));
                    break;
            }
            navDrawListStaff.setItemChecked(position, true);
        }
        drawer.closeDrawer(Gravity.LEFT);
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position);
        }
    }

    private void showCloudPrintDialog() {
        // Linkify the message
        String cloudPrint = "https://www.google.com/cloudprint/learn/howitworks.html";
        final SpannableString cloudprintLink = new SpannableString(cloudPrint);
        Linkify.addLinks(cloudprintLink, Linkify.ALL);

        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(getApplicationContext(), CloudPrint.class));
                    }
                })
                .setIcon(R.drawable.ic_drawer)
                .setMessage("Google Cloud Service must be enabled to print invoice.\n" +
                        "For setting up google cloud print go to:\n" + cloudprintLink)
                .create();

        d.show();

        // Make the textview clickable. Must be called after show()
        ((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
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
        switch (id) {
            case R.id.action_cart:
                startActivity(new Intent(this, Cart.class));
                break;
            case R.id.action_category:
                startActivity(new Intent(this, Categories.class));
                break;
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
                Intent ListSoldItem = new Intent(this, ListSoldItems.class);
                startActivity(ListSoldItem);
                break;
            case R.id.b_add_items:
                Intent AddItems = new Intent(this, AddItemsMenu.class);
                startActivity(AddItems);
                break;
            case R.id.b_sell_items:
                Intent SellItems = new Intent(this, SellItemsMenu.class);
                startActivity(SellItems);
                break;
            case R.id.b_google_logout:
                showConfirmationDialog("Google+", G_LOGOUT);
                break;
            case R.id.b_facebook_logout:
                showConfirmationDialog("Facebook", F_LOGOUT);
                break;

            case R.id.b_homepage_logout:
                preflogin.edit().clear().apply();
                startActivity(new Intent(this, LoginPage.class));
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
                Plus.AccountApi.revokeAccessAndDisconnect(googleApiClient)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Toast.makeText(getApplicationContext(), "Signed Out from Google+", Toast.LENGTH_SHORT).show();
                            }
                        });
                drawer.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, LoginPage.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Signed Out from Google+", Toast.LENGTH_SHORT).show();
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
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        signInWithGplus();
    }

    private void signInWithGplus() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog;

            @Override
            protected String doInBackground(String... strings) {
                resolveSignInError();
                return "";
            }

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(HomePage.this);
                dialog.setMessage("Please Wait...Signing In");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
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

    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(HomePage.this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    private void getProfileInformationforGmail() {
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
                verifyEmailWithDB();
            } else {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
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
                filePath = createImageFromBitmap(profImage);
                ivuserPic.setImageBitmap(profImage);
            }
        }
    }

    private void verifyEmailWithDB() {
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;
            boolean isEmailExistForAdmin
                    ,
                    isEmailExistForStaff;

            @Override
            protected Boolean doInBackground(String... strings) {
                isEmailExistForAdmin = dbloginCredAdapter.isEmailExists(Gemail);
                isEmailExistForStaff = dbStaffAdapter.isEmailExists(Gemail);
                return isEmailExistForAdmin || isEmailExistForStaff;
            }

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(HomePage.this);
                dialog.setMessage("Please Wait...Obtaining Info");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected void onPostExecute(Boolean isEmailExist) {
                dialog.dismiss();
                if (isEmailExist) {
                    long adminMobile = dbloginCredAdapter.getAdminMobile(Gemail);
                    globalVariable.setAdminMobile(adminMobile);
                    tvuserName.setText(GpersonName);
                    tvuserEmail.setText(Gemail);
                } else {
                    Intent staffMobile = new Intent(getApplicationContext(), StaffMobile.class);
                    staffMobile.putExtra("gresponse", Gresponse);
                    startActivity(staffMobile);
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (responseGmail == 1) {
            if (requestCode == RC_SIGN_IN) {
                if (responseCode != RESULT_OK) {
                    signInClicked = false;
                    startActivity(new Intent(this, LoginPage.class));
                }
                intentInProgress = false;
                if (!googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (responseGmail == 1)
            googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (responseGmail == 1) {

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

}
