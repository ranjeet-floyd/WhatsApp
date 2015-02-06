package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Outofstock;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbOutOfStockAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbOutOfStockAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbOutOfStockAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long addtoOutOfStockList(String Id, String Name, int RemQuantity, long SuppMobile) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_OUTOFSTOCK_PRODID, Id);
        row.put(DBHelper.COL_OUTOFSTOCK_PRODNAME, Name);
        row.put(DBHelper.COL_OUTOFSTOCK_REMAIN_QUANTITY, RemQuantity);
        row.put(DBHelper.COL_OUTOFSTOCK_SUPPMOBILE, SuppMobile);
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_OUTOFSTOCK_ITEMS, null, row);
        return result;
    }

    public ArrayList<Outofstock> getAllOutOfStockItems() {
        ArrayList<Outofstock> outofstockArrayList = new ArrayList<Outofstock>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_OUTOFSTOCK_ITEMS, DBHelper.OUTOFSTOCK_COLUMNS, null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Outofstock outofstock = new Outofstock();
                outofstock.setProdName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_OUTOFSTOCK_PRODNAME)));
                outofstock.setRemQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_OUTOFSTOCK_REMAIN_QUANTITY)));
                outofstock.setSuppMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_OUTOFSTOCK_SUPPMOBILE)));
                outofstockArrayList.add(outofstock);
            } while (c.moveToNext());
            return outofstockArrayList;
        } else {
            return null;
        }

    }

    public boolean isProductBelowStock(String id) {

        String RAW_QUERY = "SELECT " + DBHelper.COL_PROD_QUANTITY + "," + DBHelper.COL_PROD_MINLIMIT +
                " FROM " + DBHelper.TABLE_PRODUCT +
                " WHERE " + DBHelper.COL_PROD_ID + "='" + id + "'";
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            int remQuantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
            int minLimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
            if (remQuantity < minLimit)
                return true;
            else return false;
        } else
            return false;
    }

    public int getOutOfStockItems() {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_OUTOFSTOCK_ITEMS, null, null, null, null, null, null);
        if (c != null) {
            return c.getCount();
        } else return 0;
    }

}
