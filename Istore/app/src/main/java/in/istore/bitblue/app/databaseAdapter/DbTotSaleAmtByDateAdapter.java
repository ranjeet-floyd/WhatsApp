package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbTotSaleAmtByDateAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbTotSaleAmtByDateAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbTotSaleAmtByDateAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertTodaySales(String TotalSalesAmount, String StoreId) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        ContentValues insertAmount = new ContentValues();
        insertAmount.put(DBHelper.TOTALSTOCKSALES_PERDAYSALES_COL,
                Float.parseFloat(TotalSalesAmount) + Float.parseFloat(getTodaySales()));
        insertAmount.put(DBHelper.TOTALSTOCKSALES_PERDAYDATE_COL, todayDate);
        insertAmount.put(DBHelper.STOREID_COL, StoreId);
        insertAmount.put(DBHelper.IS_UPDATED, "no");

        ContentValues updateAmount = new ContentValues();
        updateAmount.put(DBHelper.TOTALSTOCKSALES_PERDAYSALES_COL, TotalSalesAmount + getTodaySales());

        openWritableDatabase();
        if (isAlreadyExists(todayDate))
            return sqLiteDb.update(DBHelper.TOTALSTOCKSALES_TABLE, updateAmount,
                    DBHelper.TOTALSTOCKSALES_PERDAYDATE_COL + "='" + todayDate + "'", null);
        else
            return sqLiteDb.insert(DBHelper.TOTALSTOCKSALES_TABLE, null, insertAmount);
    }

    public String getTodaySales() {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TOTALSTOCKSALES_TABLE, DBHelper.TOTALSTOCKSALES_COLUMNS,
                DBHelper.TOTALSTOCKSALES_PERDAYDATE_COL + "='" + todayDate + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return c.getString(c.getColumnIndexOrThrow(DBHelper.TOTALSTOCKSALES_PERDAYSALES_COL));
        } else {
            return null;
        }
    }

    public float getTotalRevenue() {

        String SUM_QUERY = "SELECT SUM(" + DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT + ")" +
                " FROM " + DBHelper.TABLE_TOTAL_SALES_BY_DATE;
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(SUM_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT + ")"));
        } else {
            return 0;
        }
    }

    private boolean isAlreadyExists(String todayDate) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_TOTAL_SALES_BY_DATE, DBHelper.TOTALSALES_BYDATE_COLUMNS,
                DBHelper.COL_TOTALSALES_PERDAY_DATE + "='" + todayDate + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public float getTotalRevenueforRange(String from, String to) {
        String RAW_QUERY =
                "SELECT SUM(" + DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT + ")" +
                        " FROM " + DBHelper.TABLE_TOTAL_SALES_BY_DATE +
                        " WHERE " + DBHelper.COL_TOTALSALES_PERDAY_DATE + " BETWEEN '" + from + "' AND '" + to + "'";
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT + ")"));
        } else {
            return 0;
        }
    }
}
