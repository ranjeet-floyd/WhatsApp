package in.istore.bitblue.app.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.NavDrawAdapter;
import in.istore.bitblue.app.addItems.AddItemsMenu;
import in.istore.bitblue.app.adminMenu.Trans;
import in.istore.bitblue.app.adminMenu.custInfo.CusInfoContent;
import in.istore.bitblue.app.adminMenu.staffMgmt.StaffMgntContent;
import in.istore.bitblue.app.adminMenu.suppInfo.SuppInfoContent;
import in.istore.bitblue.app.cart.Cart;
import in.istore.bitblue.app.category.Categories;
import in.istore.bitblue.app.cloudprint.CloudPrint;
import in.istore.bitblue.app.data.ExportData;
import in.istore.bitblue.app.data.ImportData;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.listStock.ListMyStock;
import in.istore.bitblue.app.loginScreen.LoginPage;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.sellItems.SellItemsMenu;
import in.istore.bitblue.app.soldItems.ListSoldItems;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class HomePage extends ActionBarActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems, Glogout, Flogout;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawList;
    private List<NavDrawItems> navDrawItemsList;
    private NavDrawAdapter navDrawAdapter;
    private String FpersonName, Femail, GName, Gemail;
    private int responseGmail, responseFacebook;
    private static final int G_LOGOUT = 1;
    private static final int F_LOGOUT = 2;

    private Bitmap bitmap;
    private DbProductAdapter dbAdapter;
    private GlobalVariables globalVariable;
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
        responseGmail = getIntent().getIntExtra("gresponse", 0);
        responseFacebook = getIntent().getIntExtra("facebook", 0);
        globalVariable = (GlobalVariables) getApplicationContext();
        initViews();
        if (responseGmail == 1) {
            //Hide Facebook Logout button in nav Drawer
            Flogout.setVisibility(View.GONE);
            GName = getIntent().getStringExtra("gName");
            Gemail = getIntent().getStringExtra("gEmail");
        } else if (responseFacebook == 2) {

            //Hide Google+ Logout button in nav Drawer
            Glogout.setVisibility(View.GONE);

            //If facebook then get all values
            FpersonName = globalVariable.getFbName();
            Femail = globalVariable.getFbEmail();
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
        } else {
            Flogout.setVisibility(View.GONE);
            Glogout.setVisibility(View.GONE);
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

        navDrawItemsList = getAdminListItems();
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


    private List<NavDrawItems> getAdminListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Staff Management", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Supplier Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Transactions", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Import Data", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Export Data", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Print Invoice", R.drawable.ic_action_computer));
        return drawerList;
    }

    private void selectItem(int position) {
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
                startActivity(new Intent(this, ImportData.class));
                break;

            case 5: //Export Data
                startActivity(new Intent(this, ExportData.class));
                break;

            case 6: //Cloud Print
                showCloudPrintDialog();
                break;
        }
        navDrawList.setItemChecked(position, true);
        drawer.closeDrawer(Gravity.LEFT);
    }

    private List<NavDrawItems> getStaffListItems() {
        ArrayList<NavDrawItems> drawerList = new ArrayList<NavDrawItems>();
        drawerList.add(new NavDrawItems("Customer Info", R.drawable.ic_action_computer));
        drawerList.add(new NavDrawItems("Transactions", R.drawable.ic_action_computer));
        return drawerList;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

}
