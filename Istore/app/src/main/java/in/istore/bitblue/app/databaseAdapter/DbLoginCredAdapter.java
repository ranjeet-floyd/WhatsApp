package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbLoginCredAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbLoginCredAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbLoginCredAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertAdminInfo(String Name, String Email, String Passwd, long Mobile, int StoreId) {
        Date d = new Date();
        String CreationDate = DateUtil.convertToStringDateAndTime(d);
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_LOGINCRED_STOREID, StoreId);
        row.put(DBHelper.COL_LOGINCRED_NAME, Name);
        row.put(DBHelper.COL_LOGINCRED_EMAIL, Email);
        row.put(DBHelper.COL_LOGINCRED_PASSWD, Passwd);
        row.put(DBHelper.COL_LOGINCRED_MOBNUM, Mobile);
        row.put(DBHelper.COL_LOGINCRED_CREATION_DATE, CreationDate);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_LOGIN_CRED_ADMIN, null, row);
        closeDatabase();
        return result;
    }

    public int updateAdminInfo(long Mobile, int StoreId, String StoreName) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_LOGINCRED_STORENAME, StoreName);
        openWritableDatabase();
        int result = sqLiteDb.update(DBHelper.TABLE_LOGIN_CRED_ADMIN, row, DBHelper.COL_LOGINCRED_MOBNUM + "=" + Mobile + " AND " +
                DBHelper.COL_LOGINCRED_STOREID + "=" + StoreId, null);
        closeDatabase();
        return result;
    }

    public boolean isValidCred(long Mobile, String Passwd) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_MOBNUM + "=" + Mobile + " AND " +
                        DBHelper.COL_LOGINCRED_PASSWD + "='" + Passwd + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public boolean isEmailExists(String Email) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_EMAIL + "='" + Email + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public boolean isMobileExists(long Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public int getStoreId(long Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_LOGINCRED_STOREID));
        } else {
            closeDatabase();
            return 0;            //Store id is between 11111 and 99999 so return 0 if store id not found
        }
    }

    public long getAdminMobile(String email) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_EMAIL + "='" + email + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_LOGINCRED_MOBNUM));
        } else {
            closeDatabase();
            return 0;
        }
    }

    public String[] getAdminNameAndEmail(long Mobile) {
        String[] adminNameAndEmail = new String[2];
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_LOGIN_CRED_ADMIN, DBHelper.LOGIN_CRED_ADMIN_COLUMNS,
                DBHelper.COL_LOGINCRED_MOBNUM + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            adminNameAndEmail[0] = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_LOGINCRED_NAME)); //Name
            adminNameAndEmail[1] = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_LOGINCRED_EMAIL));  //Email
            return adminNameAndEmail;
        } else {
            closeDatabase();
            return null;
        }
    }

}
