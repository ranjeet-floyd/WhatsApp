package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import in.istore.bitblue.app.utilities.DBHelper;

public class DbSoldItemAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbSoldItemAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbSoldItemAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertSoldItemQuantityDetail(String Id, String Quantity, String sellPrice) {
        Date date = new Date();
        long todayDate = date.getTime();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_DATE, todayDate);   //Insert current date in Unix Time format.
        row.put(DBHelper.COL_PROD_SELLPRICE, sellPrice);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_SOLD_ITEMS, null, row);
        closeDatabase();
        return result;
    }
}
