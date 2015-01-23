package in.istore.bitblue.app.print;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import in.istore.bitblue.app.FileChooser.FileDialog;
import in.istore.bitblue.app.R;

public class CloudPrint extends ActionBarActivity implements View.OnClickListener {
    private final static int REQUEST_LOADFORPRINT = 300;

    private TextView tvFilename;
    private Button bBrowse, bPrint;
    private String path, filePath, fileName;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_print);
        bBrowse = (Button) findViewById(R.id.b_cloudprint_browse);
        bPrint = (Button) findViewById(R.id.b_cloudprint_print);
        bBrowse.setOnClickListener(this);
        bPrint.setOnClickListener(this);
        tvFilename = (TextView) findViewById(R.id.tv_cloudprint_filename);
    }

    public synchronized void onActivityResult(final int requestCode,
                                              int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String name;
            File file = null;
            if (requestCode == REQUEST_LOADFORPRINT) {
                path = data.getStringExtra(FileDialog.RESULT_PATH);
                if ((path != null) && !(path.equals(""))) {
                    name = FilenameUtils.getBaseName(path);
                    filePath = path.substring(0, (path.length()) - (name.length() + 4));
                    file = new File(path);
                }
                if (file.exists()) {
                    fileName = file.getName();
                    tvFilename.setText("File: " + fileName);
                    bPrint.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Path: " + path, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "File Not Found: " + fileName, Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_cloudprint_browse:
                packageName = "com.pauloslf.cloudprint";
                boolean isAppInstalled = isPackageInstalled(packageName, this);

                if (isAppInstalled) {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.pauloslf.cloudprint");
                    startActivity(LaunchIntent);
                } else {
                    String cloudPrint = "Cloud Print App is Required. Proceed to PlayStore to Download.";
                    askToDownloadApp(cloudPrint);
                }

                /*Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory());

                //can user select directories or not
                //no directory selection
                intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

                //alternatively you can set file filter
                //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });
                intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
                startActivityForResult(intent, REQUEST_LOADFORPRINT);*/
                break;
            case R.id.b_cloudprint_print:

                /*File file = new File(path);
                Intent printIntent = new Intent(this, PrintDialogActivity.class);
                printIntent.setDataAndType(Uri.fromFile(file), "text*//**//*");
                printIntent.putExtra("title", "Android print demo");
                startActivity(printIntent);*/
                break;
        }
    }

    private void askToDownloadApp(String message) {
        new AlertDialog.Builder(CloudPrint.this)
                .setTitle("Cloud Print")
                .setMessage("\n" + message + "")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        dialogInterface.dismiss();
                        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                        i.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
