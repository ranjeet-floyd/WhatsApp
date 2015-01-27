package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Staff;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbStaffAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbStaffAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbStaffAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertStaffInfo(int StoreId, int StaffId, String Name, long Mobile, String Passwd, String Address, String Joindate) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_STAFFMGNT_STOREID, StoreId);
        row.put(DBHelper.COL_STAFFMGNT_STAFFID, StaffId);
        row.put(DBHelper.COL_STAFFMGNT_NAME, Name);
        row.put(DBHelper.COL_STAFFMGNT_MOBNUM, Mobile);
        row.put(DBHelper.COL_STAFFMGNT_PASSWD, Passwd);
        row.put(DBHelper.COL_STAFFMGNT_ADDRESS, Address);
        row.put(DBHelper.COL_STAFFMGNT_JOIN_DATE, Joindate);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_STAFFMGNT, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<Staff> getAllStaffInfo(int StoreId) {
        ArrayList<Staff> staffArrayList = new ArrayList<Staff>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_STOREID + "='" + StoreId + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Staff staff = new Staff();
                staff.setStaffId(c.getInt(c.getColumnIndexOrThrow("staffid")));
                staff.setName(c.getString(c.getColumnIndexOrThrow("staffname")));
                staff.setMobile(c.getLong(c.getColumnIndexOrThrow("staffmobile")));
                staff.setTotalSales(c.getInt(c.getColumnIndexOrThrow("stafftotsale")));
                staffArrayList.add(staff);
            } while (c.moveToNext());
            closeDatabase();
            return staffArrayList;
        } else {
            return null;
        }

    }
}
