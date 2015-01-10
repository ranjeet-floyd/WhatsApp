package in.istore.bitblue.app.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;

import in.istore.bitblue.app.adapters.DbCursorAdapter;
import in.istore.bitblue.app.utilities.DBHelper;

public class ExportData extends AsyncTask<String, String, Boolean> {
    private Context context;
    private SQLiteDatabase sqLiteDb;
    private DBHelper dbHelper;

    public ExportData(Context context) {
        this.context = context;
    }

    private final ProgressDialog dialog = new ProgressDialog(context);

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Exporting Database...");
        dialog.show();

        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        sqLiteDb = dbHelper.getReadableDatabase();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

       // File dbFile = getDatabasePath("excerDB.db");

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "excerDB.csv");
        try {
            file.createNewFile();
          //  CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS, null, null, null, null, null);
          //  csvWrite.writeNext(c.getColumnNames());
            while (c.moveToNext()) {
                String arrStr[] = {c.getString(0), c.getString(1),
                        c.getString(2), c.getString(3), c.getString(4)};
         //       csvWrite.writeNext(arrStr);
            }
            dbHelper.close();
            return true;
        } catch (Exception sqlEx) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
    }
}
