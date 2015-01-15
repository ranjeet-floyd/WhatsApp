package in.istore.bitblue.app.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;
import in.istore.bitblue.app.FileChooser.FileDialog;
import in.istore.bitblue.app.FileChooser.SelectionMode;
import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.DbCursorAdapter;

public class ImportData extends ActionBarActivity {

    private final static int REQUEST_LOAD = 200;
    private Button bBrowse;
    private String path, filePath, fileName;
    private DbCursorAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        dbAdapter = new DbCursorAdapter(this);
        bBrowse = (Button) findViewById(R.id.b_import_browse);
        bBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory());
                //can user select directories or not
                //no directory selection
                intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
                //alternatively you can set file filter
                //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });
                intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);
                startActivityForResult(intent, REQUEST_LOAD);
            }
        });
    }

    public synchronized void onActivityResult(final int requestCode,
                                              int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String name;
            File file = null;
            if (requestCode == REQUEST_LOAD) {
                path = data.getStringExtra(FileDialog.RESULT_PATH);
                if ((path != null) && !(path.equals(""))) {
                    name = FilenameUtils.getBaseName(path);
                    filePath = path.substring(0, (path.length()) - (name.length() + 4));
                    file = new File(path);
                }
                if (file.exists()) {
                    fileName = file.getName();
                    if (checkifCSVFile(file)) {
                        try {
                            new importdata().execute(fileName);
                        } catch (ActivityNotFoundException e) {
                        }
                    } else
                        Toast.makeText(this, "Not a csv File: " + fileName, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, "File Not Found: " + fileName, Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "File Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private class importdata extends AsyncTask<String, String, String> {

        private final ProgressDialog dialog = new ProgressDialog(ImportData.this);
        private File file;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Importing Database...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... fileName) {
            CSVReader csvReader;
            String[] row;
            String id, image, name, desc, quantity, price;
            long result = 0, total = 0;
            byte[] imageByteValue;
            file = new File(filePath, fileName[0]);
            try {
                csvReader = new CSVReader(new FileReader(file));
                while ((row = csvReader.readNext()) != null) {
                    total += row.length;
                    publishProgress("" + total);
                    id = row[0];
                    image = row[1];
                    imageByteValue = convertStringtoByteArray(image);
                    name = row[2];
                    desc = row[3];
                    quantity = row[4];
                    price = row[5];
                    if (dbAdapter.idAlreadyPresent(id))
                        continue;
                    else
                        result = dbAdapter.insertProductDetails(id, imageByteValue, name, desc, quantity, price);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "FileNotExists";
            }
            if (result < 0) {
                return "ErrorReadingFile";
            } else
                return "Success";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(Integer.parseInt(values[0]));
        }

        private byte[] convertStringtoByteArray(String image) {
            String[] byteValues = image.substring(1, image.length() - 1).split(",");
            byte[] bytes = new byte[byteValues.length];
            int len = bytes.length;
            for (int i = 0; i < len; i++) {
                bytes[i] = Byte.parseByte(byteValues[i].trim());
            }
            return bytes;
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

    private boolean checkifCSVFile(File file) {
        String name = file.getName();
        String csvFormat;
        if (name.equals("")) {
            showAlertDialog("Error", "Enter filename", R.drawable.erroricon);
            return false;
        } else {
            csvFormat = name.length() > 4 ? name.substring(name.length() - 4) : name;
            csvFormat = csvFormat.toLowerCase();
            return csvFormat.equals(".csv");
        }
    }

    private void showAlertDialog(String Title, String Message, int ResId) {
        new AlertDialog.Builder(ImportData.this)
                .setTitle(Title).setIcon(getResources().getDrawable(ResId))
                .setMessage(Message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }
}
