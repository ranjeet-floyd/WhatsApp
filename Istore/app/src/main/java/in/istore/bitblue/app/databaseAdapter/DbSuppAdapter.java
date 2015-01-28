package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Supplier;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbSuppAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbSuppAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbSuppAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertSuppInfo(String Name, long Mobile, String Address, String Joindate) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_SUPPINFO_NAME, Name);
        row.put(DBHelper.COL_SUPPINFO_MOBILE, Mobile);
        row.put(DBHelper.COL_SUPPINFO_ADDRESS, Address);
        row.put(DBHelper.COL_SUPPINFO_STARTING_DATE, Joindate);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_SUPPINFO, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<Supplier> getAllSuppInfo() {
        ArrayList<Supplier> supplierArrayList = new ArrayList<Supplier>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_SUPPINFO, DBHelper.SUPPINFO_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Supplier supplier = new Supplier();
                supplier.setName(c.getString(c.getColumnIndexOrThrow("suppname")));
                supplier.setMobile(c.getLong(c.getColumnIndexOrThrow("suppmobile")));
                supplierArrayList.add(supplier);
            } while (c.moveToNext());
            closeDatabase();
            return supplierArrayList;
        } else {
            return null;
        }
    }

}
