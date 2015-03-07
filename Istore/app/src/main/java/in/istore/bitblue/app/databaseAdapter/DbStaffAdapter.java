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

    public long addNewStaff(String StoreId, String StaffId, String StaffEmail, String Name, String Mobile, String Passwd, String Address, String JoinDate, String StaffTotSales) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFID_COL, StaffId);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFEMAIL_COL, StaffEmail);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFNAME_COL, Name);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFMOBILE_COL, Mobile);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFADDRESS_COL, Address);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFPASSWORD_COL, Passwd);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFJOINON_COL, JoinDate);
        row.put(DBHelper.STAFFMANAGEMENT_STAFFTOTALSALES_COL, StaffTotSales);
        row.put(DBHelper.IS_UPDATED, "no");
        //row.put(DBHelper.STAFFMANAGEMENT_STAFFKEY_COL, StaffKey);
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.STAFF_MANAGEMENT_TABLE, null, row);
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

    public ArrayList<Staff> getAllStaffsOnLocal(String StoreId) {
        ArrayList<Staff> staffArrayList = new ArrayList<Staff>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.STAFF_MANAGEMENT_TABLE, DBHelper.STAFFMANAGEMENT_COLUMNS,
                DBHelper.STOREID_COL + "='" + StoreId + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Staff staff = new Staff();
                staff.setStaffId(c.getString(c.getColumnIndexOrThrow(DBHelper.STAFFMANAGEMENT_STAFFID_COL)));
                staff.setName(c.getString(c.getColumnIndexOrThrow(DBHelper.STAFFMANAGEMENT_STAFFNAME_COL)));
                staff.setMobile(c.getString(c.getColumnIndexOrThrow(DBHelper.STAFFMANAGEMENT_STAFFMOBILE_COL)));
                staff.setTotalSales(c.getString(c.getColumnIndexOrThrow(DBHelper.STAFFMANAGEMENT_STAFFTOTALSALES_COL)));
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

    public boolean isStaffExists(String Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.STAFF_MANAGEMENT_TABLE, DBHelper.STAFFMANAGEMENT_COLUMNS,
                DBHelper.STAFFMANAGEMENT_STAFFMOBILE_COL + "='" + Mobile + "'", null, null, null, null);
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
