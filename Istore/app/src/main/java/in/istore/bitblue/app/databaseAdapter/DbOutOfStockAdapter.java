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

    public long addtoOutOfStockList(String Name, String RemQuantity, String MinLimit, String SuppMobile, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.OUTOFSTOCK_PRODUCT_NAME_COL, Name);
        row.put(DBHelper.OUTOFSTOCK_PRODUCT_REMAININGQUANTITY_COL, RemQuantity);
        row.put(DBHelper.OUTOFSTOCK_PRODUCT_MINQUANTITY_COL, MinLimit);
        row.put(DBHelper.OUTOFSTOCK_SUPPLIER_MOBILE_COL, SuppMobile);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.OUTOFSTOCKITEMS_TABLE, null, row);
    }

    public int updateOutOfStockItem(String name, String RemQuantity) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.OUTOFSTOCK_PRODUCT_REMAININGQUANTITY_COL, RemQuantity);
        openWritableDatabase();
        return sqLiteDb.update(DBHelper.OUTOFSTOCKITEMS_TABLE, row,
                DBHelper.OUTOFSTOCK_PRODUCT_NAME_COL + "='" + name + "'", null);
    }

    public ArrayList<Outofstock> getAllOutOfStockItems() {
        ArrayList<Outofstock> outofstockArrayList = new ArrayList<Outofstock>();

        String RAW_QUERY = "SELECT DISTINCT " + DBHelper.COL_OUTOFSTOCK_PRODNAME + "," + DBHelper.COL_OUTOFSTOCK_REMAIN_QUANTITY + "," + DBHelper.COL_OUTOFSTOCK_SUPPMOBILE +
                " FROM " + DBHelper.TABLE_OUTOFSTOCK_ITEMS;

        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
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
            return remQuantity < minLimit;
        } else
            return false;
    }

    public int getOutOfStockItems() {
        openWritableDatabase();
        String RAW_QUERY = "SELECT DISTINCT " + DBHelper.COL_OUTOFSTOCK_PRODNAME +
                " FROM " + DBHelper.TABLE_OUTOFSTOCK_ITEMS;
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null) {
            return c.getCount();
        } else return 0;
    }

    public boolean isProductAlreadyOutOfStock(String Name) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.OUTOFSTOCKITEMS_TABLE, DBHelper.OUTOFSTOCK_COLUMNS,
                DBHelper.OUTOFSTOCK_PRODUCT_NAME_COL + "='" + Name + "'", null, null, null, null);
        return c != null && c.moveToFirst();
    }

}
