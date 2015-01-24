package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
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

    public long insertAdminInfo(String Name, String Email,String Passwd ,String Mobile, int StoreId) {
        Date d = new Date();
        String CreationDate = DateUtil.convertToStringDate(d);
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
}
