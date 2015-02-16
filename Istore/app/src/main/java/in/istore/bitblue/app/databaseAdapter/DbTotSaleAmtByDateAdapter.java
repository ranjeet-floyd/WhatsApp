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

    public long insertTodaySales(float TotalSalesAmount) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        ContentValues insertAmount = new ContentValues();
        insertAmount.put(DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT, TotalSalesAmount + getTodaySales());
        insertAmount.put(DBHelper.COL_TOTALSALES_PERDAY_DATE, todayDate);

        ContentValues updateAmount = new ContentValues();
        updateAmount.put(DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT, TotalSalesAmount + getTodaySales());

        openWritableDatabase();
        long result = 0;
        if (isAlreadyExists(todayDate)) {
            //update
            result = sqLiteDb.update(DBHelper.TABLE_TOTAL_SALES_BY_DATE, updateAmount, DBHelper.COL_TOTALSALES_PERDAY_DATE + "='" + todayDate + "'", null);
        } else {
            //insert
            result = sqLiteDb.insert(DBHelper.TABLE_TOTAL_SALES_BY_DATE, null, insertAmount);
        }
        return result;
    }

    public float getTodaySales() {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        float TodaySales = 0;
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_TOTAL_SALES_BY_DATE, DBHelper.TOTALSALES_BYDATE_COLUMNS,
                DBHelper.COL_TOTALSALES_PERDAY_DATE + "='" + todayDate + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            TodaySales = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_TOTALSALES_PERDAY_SALESAMOUNT));
            return TodaySales;
        } else {
            return 0;
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
