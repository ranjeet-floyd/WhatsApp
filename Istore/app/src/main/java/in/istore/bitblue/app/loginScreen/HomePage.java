package in.istore.bitblue.app.loginScreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.DbCursorAdapter;
import in.istore.bitblue.app.adapters.NavDrawAdapter;
import in.istore.bitblue.app.addItems.AddItems;
import in.istore.bitblue.app.listMyStock.ListMyStock;
import in.istore.bitblue.app.navDrawer.NavDrawItems;
import in.istore.bitblue.app.sellItems.SellItems;
import in.istore.bitblue.app.soldItems.SoldItems;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class HomePage extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private Toolbar toolbar;
    private Button blistStock, bviewSoldItems, bAddItems, bSellItems;
    private TextView tvuserName, tvuserEmail;
    private ImageView ivuserPic;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ListView navDrawList;
    private List<NavDrawItems> navDrawItemsList;
    private NavDrawAdapter navDrawAdapter;
    private String GpersonName, Gemail, FpersonName, Femail;
    private int responseGmail, responseFacebook;
    private boolean intentInProgress, signInClicked;
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private Bitmap bitmap;
    private DbCursorAdapter dbAdapter;
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
        if (responseGmail == 1)
        // Initializing google plus api client
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();
         //   onConnected(savedInstanceState);
        } else if (responseFacebook == 2) {
            //If facebook then get all intents
            FpersonName = globalVariable.getUserName();
            Femail = globalVariable.getUserEmail();
            bitmap = globalVariable.getProfPic();
            if (FpersonName != null)
                tvuserName.setText(FpersonName);
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

        dbAdapter = new DbCursorAdapter(this);

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

        tvuserName = (TextView) findViewById(R.id.tv_username);
        tvuserEmail = (TextView) findViewById(R.id.tv_useremail);
        ivuserPic = (ImageView) findViewById(R.id.iv_prof_image);

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
        if (responseGmail == 1)
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
        super.onActivityResult(requestCode, responseCode, intent);
        if (responseGmail == 1) {
            if (requestCode == RC_SIGN_IN) {
                if (responseCode != RESULT_OK) {
                    signInClicked = false;
                }
                intentInProgress = false;
                if (!googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
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
                new AlertDialog.Builder(HomePage.this)
                        .setTitle("IMPORT DATA").setIcon(getResources().getDrawable(R.drawable.successicon))
                        .setMessage("Do you want to Import csv file into DB?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                new ImportData().execute();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                break;
            case 1:  //EXPORT DATA
                new AlertDialog.Builder(HomePage.this)
                        .setTitle("EXPORT DATA").setIcon(getResources().getDrawable(R.drawable.successicon))
                        .setMessage("Do you want to export DB to csv file?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                new ExportData().execute();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                break;
        }
        navDrawList.setItemChecked(position, true);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private class ExportData extends AsyncTask<String, String, Boolean> {
        private SQLiteDatabase sqLiteDb;
        private DBHelper dbHelper;
        private File fileNoImage, fileImage;
        private File istoreData;
        private final ProgressDialog dialog = new ProgressDialog(HomePage.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Exporting Database...");
            dialog.show();

            dbHelper = new DBHelper(HomePage.this,
                    DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
            sqLiteDb = dbHelper.getReadableDatabase();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            // File dbFile = getDatabasePath(DBHelper.DATABASE_NAME);

            istoreData = new File(Environment.getExternalStorageDirectory(), "/IstoreData");
            if (!istoreData.exists()) {
                istoreData.mkdirs();
            }

            fileNoImage = new File(istoreData, "exportIstore.csv");
            fileImage = new File(istoreData, DBHelper.DATABASE_TABLE + ".csv");

            try {
                fileNoImage.createNewFile();
                fileImage.createNewFile();

                CSVWriter csvWriteNoImage = new CSVWriter(new FileWriter(fileNoImage));
                CSVWriter csvWriteImage = new CSVWriter(new FileWriter(fileImage));

                Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS, null, null, null, null, null);

                //Do not write column header to csv files
               /* csvWriteNoImage.writeNext(c.getColumnNames());
                csvWriteImage.writeNext(c.getColumnNames());*/

                while (c.moveToNext()) {

                    String noImage[] = {c.getString(c.getColumnIndexOrThrow("id")),
                            c.getString(c.getColumnIndexOrThrow("name")),
                            c.getString(c.getColumnIndexOrThrow("desc")),
                            c.getString(c.getColumnIndexOrThrow("quantity")),
                            c.getString(c.getColumnIndexOrThrow("price"))};
                    csvWriteNoImage.writeNext(noImage);

                    String image[] = {c.getString(c.getColumnIndexOrThrow("id")),
                            Arrays.toString((c.getBlob(1))),
                            c.getString(c.getColumnIndexOrThrow("name")),
                            c.getString(c.getColumnIndexOrThrow("desc")),
                            c.getString(c.getColumnIndexOrThrow("quantity")),
                            c.getString(c.getColumnIndexOrThrow("price"))};
                    csvWriteImage.writeNext(image);
                }
                csvWriteNoImage.close();
                csvWriteImage.close();
                dbHelper.close();
                return true;
            } catch (Exception sqlEx) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean status) {
            dialog.dismiss();
            if (status) {
                showAlertDialog("Success", "\tDatabase was Exported Successfully." +
                        "\n The location of file is: " + fileNoImage.getAbsolutePath(), R.drawable.successicon);
            } else {
                showAlertDialog("Error", "\tDatabase Export Error", R.drawable.erroricon);
            }
        }
    }

    private void showAlertDialog(String Title, String Message, int ResId) {
        new AlertDialog.Builder(HomePage.this)
                .setTitle(Title).setIcon(getResources().getDrawable(ResId))
                .setMessage(Message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }


    private class ImportData extends AsyncTask<String, String, String> {

        private final ProgressDialog dialog = new ProgressDialog(HomePage.this);
        private File fileNoImage, istoreData, file;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Importing Database...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            CSVReader csvReader;
            String[] row;
            String id, name, desc, quantity, price;
            byte[] image;
            long result = 0;
            istoreData = new File(Environment.getExternalStorageDirectory(), "/IstoreData");
            if (!istoreData.exists()) {
                return "FileNotExists";
            } else {
                fileNoImage = new File(istoreData, DBHelper.DATABASE_TABLE + ".csv");
                file = new File(fileNoImage.getPath());
            }
            try {
                csvReader = new CSVReader(new FileReader(file));
                while ((row = csvReader.readNext()) != null) {
                    id = row[0];
                    image = row[1].getBytes();
                    name = row[2];
                    desc = row[3];
                    quantity = row[4];
                    price = row[5];
                    if (dbAdapter.idAlreadyPresent(id))
                        continue;
                    else
                        result = dbAdapter.insertProductDetails(id, image, name, desc, quantity, price);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result < 0) {
                return "ErrorReadingFile";
            } else
                return "Success";

        }

        @Override
        protected void onPostExecute(String status) {
            dialog.dismiss();
            if (status.equals("FileNotExists")) {
                showAlertDialog("Error", "\tFile Does Not Exists", R.drawable.erroricon);
            } else if (status.equals("Success")) {
                showAlertDialog("Success", "\tCopied from csv to database", R.drawable.successicon);
            }
        }

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

        }
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {

        }
    }
}
