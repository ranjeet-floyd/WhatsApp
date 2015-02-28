package in.istore.bitblue.app.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
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
import java.util.List;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.Stocks;
import in.istore.bitblue.app.adapters.NavDrawAdapter;
import in.istore.bitblue.app.adminMenu.custInfo.CusInfoContent;
import in.istore.bitblue.app.adminMenu.staffMgmt.StaffMgntContent;
import in.istore.bitblue.app.adminMenu.suppInfo.SuppInfoContent;
import in.istore.bitblue.app.adminMenu.transactions.Trans;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.category.Categories;
import in.istore.bitblue.app.cloudprint.CloudPrint;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.home.dragGrid.DynamicGridView;
import in.istore.bitblue.app.home.dragGrid.GridDynamicAdapter;
import in.istore.bitblue.app.home.foldingdrawer.FoldingDrawerLayout;
import in.istore.bitblue.app.invoice.Invoice;
import in.istore.bitblue.app.loginScreen.ChangePassword;
import in.istore.bitblue.app.loginScreen.LoginPage;
import in.istore.bitblue.app.loginScreen.StaffMobile;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.pojo.GridItems;
import in.istore.bitblue.app.pojo.GridItemsList;
import in.istore.bitblue.app.staffMenu.custInfo.CusInfoForStaffContent;
import in.istore.bitblue.app.staffMenu.transactions.TransStaff;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.TinyDB;
import in.istore.bitblue.app.utilities.API;

public class HomePage extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button Glogout, Flogout, blogout;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private FoldingDrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawList, navDrawListAdmin, navDrawListStaff;

    private String FpersonName, Femail, GpersonName, Gemail, Name, Email, allContactNumber, googleFilePath, fBFilePath;
    private int responseGmail, responseFacebook, StaffId, Gresponse, StoreId;
    private static final int G_LOGOUT = 1;
    private static final int F_LOGOUT = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private long Mobile, UserMobile, UserId;
    private final static String LOGIN = "login";
    private boolean intentInProgress, signInClicked, previouslogin;

    private SharedPreferences preflogin;
    private List<NavDrawItems> navDrawItemsList, navDrawItemsListForAdmin, navDrawItemsListForStaff;
    private ArrayList<GridItems> gridItemsArrayList;
    private NavDrawAdapter navDrawAdapter;
    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private String filePath, UserType;
    private GlobalVariables globalVariable;
    private ConnectionResult connectionResult;
    private DbLoginCredAdapter dbloginCredAdapter;
    private DbStaffAdapter dbStaffAdapter;
    private TinyDB tinyDB;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    // Google client to interact with Google API
    private GoogleApiClient googleApiClient;

    //dynamicgrid
    private DynamicGridView gridView;

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
        responseGmail = getIntent().getIntExtra("gPlusLogin", 0);
        responseFacebook = getIntent().getIntExtra("fBLogin", 0);
        preflogin = getSharedPreferences(LOGIN, MODE_PRIVATE);
        globalVariable = (GlobalVariables) getApplicationContext();
        UserType = globalVariable.getUserType();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        Mobile = preflogin.getLong("Mobile", 0);
        dbStaffAdapter = new DbStaffAdapter(this);
        StaffId = dbStaffAdapter.getStaffId(Mobile);

        if (UserType != null) {
            if (UserType.equals("Admin")) {
                UserId = globalVariable.getAdminId();
                UserMobile = globalVariable.getAdminMobile();
                StoreId = globalVariable.getStoreId();
            } else if (UserType.equals("Staff")) {
                UserId = globalVariable.getStaffId();
                UserMobile = globalVariable.getStaffMobile();
            }
        }
        initViews();
        if (responseGmail == 1000) {
            Flogout.setVisibility(View.GONE);
            blogout.setVisibility(View.GONE);
            tvuserName.setText(globalVariable.getgName());
            tvuserEmail.setText(globalVariable.getgEmail());

            bitmap = BitmapFactory.decodeFile(tinyDB.getString("googleFilePath"));
            ivuserPic.setImageBitmap(bitmap);

            if (!googleApiClient.isConnected()) {
                //onConnected(savedInstanceState);
            }

        } else if (responseFacebook == 2000) {
            Glogout.setVisibility(View.GONE);
            blogout.setVisibility(View.GONE);

            //If facebook then get all values
            FpersonName = globalVariable.getFbName();
            Femail = globalVariable.getFbEmail();
            bitmap = BitmapFactory.decodeFile(tinyDB.getString("googleFilePath"));
            if (FpersonName != null) {
                tvuserName.setText(FpersonName);
            }
            if (Femail != null)
                tvuserEmail.setText(Femail);
            if (bitmap != null)
                ivuserPic.setImageBitmap(bitmap);
        } else if (UserType.equals("Admin")) {
            Name = preflogin.getString("Name", "");
            tvuserName.setText(globalVariable.getAdminName());
            tvuserEmail.setText(globalVariable.getAdminEmail());
            bitmap = BitmapFactory.decodeFile(tinyDB.getString("googleFilePath"));
            ivuserPic.setImageBitmap(bitmap);

            Flogout.setVisibility(View.GONE);
            Glogout.setVisibility(View.GONE);
        } else if (UserType.equals("Staff")) {
            Name = preflogin.getString("Name", "");
            tvuserName.setText(globalVariable.getStaffName());
            tvuserEmail.setText(globalVariable.getStaffEmail() == null || globalVariable.getStaffEmail().equals("null") ? "" : globalVariable.getStaffEmail());
            bitmap = BitmapFactory.decodeFile(tinyDB.getString("googleFilePath"));
            ivuserPic.setImageBitmap(bitmap);
            Flogout.setVisibility(View.GONE);
            Glogout.setVisibility(View.GONE);
        }
    }

    private void setToolbar() {
        tinyDB = new TinyDB(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText(tinyDB.getString("StoreName").toUpperCase());
    }

    private void initViews() {

        previouslogin = tinyDB.getBoolean("previousLogin");
        if (!previouslogin) retrieveContactsAndSendToDB();

        dbAdapter = new DbProductAdapter(this);
        dbloginCredAdapter = new DbLoginCredAdapter(this);
        dbStaffAdapter = new DbStaffAdapter(this);

        drawer = (FoldingDrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawer.setDrawerListener(actionBarDrawerToggle);
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        navDrawList = (ListView) findViewById(R.id.lv_nav_drawer);
        navDrawList.setOnItemClickListener(new DrawerItemClickListener());

        if (UserType != null) {
            if (UserType.equals("Admin")) {
                navDrawItemsList = getAdminListItems();
                navDrawAdapter = new NavDrawAdapter(this, R.layout.navdrawitem, navDrawItemsList);
                navDrawList.setAdapter(navDrawAdapter);

            } else if (UserType.equals("Staff")) {
                navDrawItemsList = getStaffListItems();
                navDrawAdapter = new NavDrawAdapter(this, R.layout.navdrawitem, navDrawItemsList);
                navDrawList.setAdapter(navDrawAdapter);
            }
        }

        tvuserName = (TextView) findViewById(R.id.tv_username);
        tvuserEmail = (TextView) findViewById(R.id.tv_useremail);
        ivuserPic = (ImageView) findViewById(R.id.iv_prof_image);

        Glogout = (Button) findViewById(R.id.b_google_logout);
        Glogout.setOnClickListener(this);

        Flogout = (Button) findViewById(R.id.b_facebook_logout);
        Flogout.setOnClickListener(this);

        blogout = (Button) findViewById(R.id.b_homepage_logout);
        blogout.setOnClickListener(this);

        gridView = (DynamicGridView) findViewById(R.id.dynamic_grid);
        if (UserType.equals("Admin"))
            gridItemsArrayList = GridItemsList.getAllGridItemsForAdmin();
        else if (UserType.equals("Staff"))
            gridItemsArrayList = GridItemsList.getAllGridItemsForStaff();

        gridView.setAdapter(new GridDynamicAdapter(this, gridItemsArrayList, 2));
        gridView.setOnDragListener(new DynamicGridView.OnDragListener() {
            @Override
            public void onDragStarted(int position) {
            }

            @Override
            public void onDragPositionsChanged(int oldPosition, int newPosition) {
            }
        });

        //Remove Wobble on Drop of Item
      /*  gridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                gridView.stopEditMode();
            }
        });*/
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode(position);
                return true;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItems gridItem = (GridItems) parent.getAdapter().getItem(position);
                switch (gridItem.getTvgridItemTitle()) {
                    case "Transactions":
                        if (UserType.equals("Admin"))
                            startActivity(new Intent(getApplicationContext(), Trans.class));
                        else if (UserType.equals("Staff"))
                            startActivity(new Intent(getApplicationContext(), TransStaff.class));
                        break;
                    case "Category":
                        startActivity(new Intent(getApplicationContext(), Categories.class));
                        break;
                    case "Manage Staff":
                        startActivity(new Intent(getApplicationContext(), StaffMgntContent.class));
                        break;
                    case "Stocks":
                        startActivity(new Intent(getApplicationContext(), Stocks.class));
                        break;
                }
            }
        });
    }

    private void retrieveContactsAndSendToDB() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            allContactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            addContactsToDb();
        }
        phones.close();
    }

    private void addContactsToDb() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(HomePage.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("UserMob", String.valueOf(UserMobile)));
                nameValuePairs.add(new BasicNameValuePair("ContactNum", allContactNumber));
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("ID", String.valueOf(UserId)));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_USER_CONTACT, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
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
                    tinyDB.putBoolean("previousLogin", true);
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private List<NavDrawItems> getAdminListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        //  drawerList.add(new NavDrawItems("Staff Management", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Supplier Info", R.drawable.suppliericon));
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.customericon));
        // drawerList.add(new NavDrawItems("Transactions", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Invoice", R.drawable.invoiceicon));
        drawerList.add(new NavDrawItems("Print Receipt", R.drawable.inprinticon));
        drawerList.add(new NavDrawItems("Change Password", R.drawable.ic_action_labels));

        return drawerList;
    }

    private List<NavDrawItems> getStaffListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.customericon));
        drawerList.add(new NavDrawItems("Invoice", R.drawable.invoiceicon));
        drawerList.add(new NavDrawItems("Print Receipt", R.drawable.inprinticon));
        drawerList.add(new NavDrawItems("Change Password", R.drawable.ic_action_labels));
        return drawerList;
    }

    private void selectItem(int position) {
        if (UserType.equals("Admin")) {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, SuppInfoContent.class));
                    break;
                case 1:
                    startActivity(new Intent(this, CusInfoContent.class));
                    break;
                case 2:
                    startActivity(new Intent(this, Invoice.class));
                    break;
                case 3:
                    startActivity(new Intent(this, CloudPrint.class));
                    break;
                case 4:
                    startActivity(new Intent(this, ChangePassword.class));
                    break;
            }
            navDrawList.setItemChecked(position, true);
        } else if (UserType.equals("Staff")) {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, CusInfoForStaffContent.class));
                    break;
                case 1:
                    startActivity(new Intent(this, Invoice.class));
                    break;
                case 2:
                    startActivity(new Intent(this, CloudPrint.class));
                    break;
                case 3:
                    startActivity(new Intent(this, ChangePassword.class));
                    break;
            }
            navDrawList.setItemChecked(position, true);
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
                        "For setting up Google Cloud Print go to:\n" + cloudprintLink)
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
    /*        case R.id.action_category:
                startActivity(new Intent(this, Categories.class));
                break;*/
/*            case R.id.action_changePasswd:
                startActivity(new Intent(this, ChangePassword.class));
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {

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
        if (responseGmail == 1000) {
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
        } else if (gridView.isEditMode()) {
            gridView.stopEditMode();
        }
    }

    protected void onStart() {
        super.onStart();
        if (responseGmail == 1000) {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
    }

    protected void onStop() {
        super.onStop();
        if (responseGmail == 1000) {
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        } else if (responseFacebook == 2000) {
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        //signInWithGplus();
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
                //  verifyEmailWithDB();
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
        if (responseGmail == 1000) {
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
        if (responseGmail == 1000)
            googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (responseGmail == 1000) {

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
