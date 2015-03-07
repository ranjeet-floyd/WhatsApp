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

    public long addNewSupplier(String Name, String Mobile, String Address, String Joindate, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.SUPPLIER_NAME_COL, Name);
        row.put(DBHelper.SUPPLIER_MOBILE_COL, Mobile);
        row.put(DBHelper.SUPPLIER_ADDRESS_COL, Address);
        row.put(DBHelper.SUPPLIER_STARTDATE_COL, Joindate);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.SUPPLIER_TABLE, null, row);
        return result;
    }

    public ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> supplierArrayList = new ArrayList<Supplier>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.SUPPLIER_TABLE, DBHelper.SUPPLIER_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Supplier supplier = new Supplier();
                supplier.setName(c.getString(c.getColumnIndexOrThrow(DBHelper.SUPPLIER_NAME_COL)));
                supplier.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.SUPPLIER_MOBILE_COL)));
                supplierArrayList.add(supplier);
            } while (c.moveToNext());
            return supplierArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<String> getAllSupplierNames() {
        ArrayList<String> supplierArrayList = new ArrayList<String>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.SUPPLIER_TABLE, DBHelper.SUPPLIER_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                supplierArrayList.add(c.getString(c.getColumnIndexOrThrow(DBHelper.SUPPLIER_NAME_COL)));
            } while (c.moveToNext());
            return supplierArrayList;
        } else {
            return null;
        }
    }

    public long getSuppMobile(String suppName) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_SUPPINFO, DBHelper.SUPPINFO_COLUMNS,
                DBHelper.COL_SUPPINFO_NAME + "='" + suppName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_SUPPINFO_MOBILE));
        } else {
            return 0;
        }
    }

    public boolean isSupplierExists(String Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.SUPPLIER_TABLE, DBHelper.SUPPLIER_COLUMNS,
                DBHelper.SUPPLIER_MOBILE_COL + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }


}
