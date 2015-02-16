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

    public long insertStaffInfo(int StoreId, int StaffId, int AdminId, String Name, long Mobile, String Passwd, String Address, String Joindate) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_STAFFMGNT_STOREID, StoreId);
        row.put(DBHelper.COL_STAFFMGNT_STAFFID, StaffId);
        row.put(DBHelper.COL_STAFFMGNT_ADMINID, AdminId);
        row.put(DBHelper.COL_STAFFMGNT_NAME, Name);
        row.put(DBHelper.COL_STAFFMGNT_MOBNUM, Mobile);
        row.put(DBHelper.COL_STAFFMGNT_PASSWD, Passwd);
        row.put(DBHelper.COL_STAFFMGNT_ADDRESS, Address);
        row.put(DBHelper.COL_STAFFMGNT_STAFFEMAIL, " ");
        row.put(DBHelper.COL_STAFFMGNT_JOIN_DATE, Joindate);

        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.TABLE_STAFFMGNT, null, row);
    }

    public long updateStaffSales(long StaffId, float TotalSalesAmount) {
        ContentValues updateRow = new ContentValues();
        updateRow.put(DBHelper.COL_STAFFMGNT_TOTALSALES, TotalSalesAmount + getStaffSales(StaffId));
        openWritableDatabase();
        return (long) sqLiteDb.update(DBHelper.TABLE_STAFFMGNT, updateRow, DBHelper.COL_STAFFMGNT_STAFFID + "='" + StaffId + "'", null);
    }

    private float getStaffSales(long StaffId) {
        float TotalSalesAmount;
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_STAFFID + "='" + StaffId + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            TotalSalesAmount = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_TOTALSALES));
            return TotalSalesAmount;
        } else {
            return 0;
        }
    }

    public ArrayList<Staff> getAllStaffInfo(int StoreId) {
        ArrayList<Staff> staffArrayList = new ArrayList<Staff>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_STOREID + "='" + StoreId + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Staff staff = new Staff();
                staff.setStaffId(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_STAFFID)));
                staff.setName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_NAME)));
                staff.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_MOBNUM)));
                staff.setTotalSales(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_TOTALSALES)));
                staffArrayList.add(staff);
            } while (c.moveToNext());
            return staffArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<Integer> getAllStaffIds() {
        ArrayList<Integer> staffIdArrayList = new ArrayList<Integer>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, new String[]{DBHelper.COL_STAFFMGNT_STAFFID},
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                int staffId = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_STAFFID));
                staffIdArrayList.add(staffId);
            } while (c.moveToNext());
            return staffIdArrayList;
        } else {
            return null;
        }
    }

    public boolean isValidCred(long Mobile, String Passwd) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_MOBNUM + "=" + Mobile + " AND " +
                        DBHelper.COL_STAFFMGNT_PASSWD + "='" + Passwd + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmailExists(String Email) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_STAFFEMAIL + "='" + Email + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public long addEmailforMobile(long Mobile, String Email) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_STAFFMGNT_STAFFEMAIL, Email);

        openWritableDatabase();
        return (long) sqLiteDb.update(DBHelper.TABLE_STAFFMGNT, row, DBHelper.COL_STAFFMGNT_MOBNUM + "='" + Mobile + "'", null);
    }

    public String getStaffName(long Mobile) {
        String StaffName;
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            StaffName = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_NAME));
            return StaffName;
        } else {
            return null;
        }
    }

    public int getStaffId(long Mobile) {
        int StaffName;
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            StaffName = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_STAFFID));
            return StaffName;
        } else {
            return -1;
        }
    }

    public int getStoreId(long Mobile) {
        int StaffName;
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_STAFFMGNT, DBHelper.STAFFMGNT_COLUMNS,
                DBHelper.COL_STAFFMGNT_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            StaffName = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_STAFFMGNT_STOREID));
            return StaffName;
        } else {
            return -1;
        }
    }
}
