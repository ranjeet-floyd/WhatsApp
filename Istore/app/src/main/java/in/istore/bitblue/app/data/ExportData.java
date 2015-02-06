package in.istore.bitblue.app.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVWriter;
import in.istore.bitblue.app.FileChooser.FileDialog;
import in.istore.bitblue.app.FileChooser.SelectionMode;
import in.istore.bitblue.app.R;
import in.istore.bitblue.app.utilities.DBHelper;

public class ExportData extends ActionBarActivity {

    private final static int REQUEST_SAVE = 100;
    private Button bBrowse;
    private String path, filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);
        bBrowse = (Button) findViewById(R.id.b_export_browse);
        bBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory());

                intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

                intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);
                startActivityForResult(intent, REQUEST_SAVE);
            }
        });
    }

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String name;
            File file = null;
            if (requestCode == REQUEST_SAVE) {
                path = data.getStringExtra(FileDialog.RESULT_PATH);
                if ((path != null) && !(path.equals(""))) {
                    name = FilenameUtils.getBaseName(path);
                    filePath = path.substring(0, (path.length()) - (name.length() + 4));
                    file = new File(path);
                }
                try {
                    if (file != null) {
                        if (file.exists()) {
                            showAlertDialog("DUPLICATE FILE", "File Already Exists.\nCreate a new file to export data", R.drawable.erroricon);
                        } else {
                            file.createNewFile();
                            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
                            if (checkifCSVFile(file)) {
                                new exportdata().execute(file.getName().toLowerCase());
                            } else {
                                FileUtils.forceDelete(file);
                                showAlertDialog("Error", "Enter correct format (filename.csv)", R.drawable.erroricon);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "File Not Selected", Toast.LENGTH_SHORT).show();
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

    private class exportdata extends AsyncTask<String, String, Boolean> {
        private SQLiteDatabase sqLiteDb;
        private DBHelper dbHelper;
        private File fileImage;
        private final ProgressDialog dialog = new ProgressDialog(ExportData.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Exporting Database...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            dbHelper = new DBHelper(ExportData.this,
                    DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
            sqLiteDb = dbHelper.getReadableDatabase();
        }

        @Override
        protected Boolean doInBackground(String... fileName) {
            long total = 0;
            fileImage = new File(filePath, fileName[0]);
            try {
                CSVWriter csvWriteImage = new CSVWriter(new FileWriter(fileImage));
                Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS, null, null, null, null, null);
                int columnCount = c.getColumnCount();
                while (c.moveToNext()) {
                    total += columnCount;
                    publishProgress("" + total);
                    String image[] = {c.getString(c.getColumnIndexOrThrow("id")),
                            Arrays.toString((c.getBlob(1))),
                            c.getString(c.getColumnIndexOrThrow("name")),
                            c.getString(c.getColumnIndexOrThrow("desc")).trim(),
                            c.getString(c.getColumnIndexOrThrow("quantity")),
                            c.getString(c.getColumnIndexOrThrow("price"))};
                    csvWriteImage.writeNext(image);
                }
                csvWriteImage.close();
                dbHelper.close();
                return true;
            } catch (Exception sqlEx) {
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(Boolean status) {
            dialog.dismiss();
            if (status) {
                showAlertDialog("Success", "\tDatabase was Exported Successfully." +
                        "\n The location of file is: " + fileImage.getAbsolutePath(), R.drawable.successicon);
            } else {
                showAlertDialog("Error", "\tDatabase Export Error", R.drawable.erroricon);
            }
        }
    }

    private void showAlertDialog(String Title, String Message, int ResId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExportData.this)
                .setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        if (ResId == 0)
            alertDialog.create().show();
        else {
            alertDialog.setIcon(getResources().getDrawable(ResId)).create().show();
        }

    }
}
