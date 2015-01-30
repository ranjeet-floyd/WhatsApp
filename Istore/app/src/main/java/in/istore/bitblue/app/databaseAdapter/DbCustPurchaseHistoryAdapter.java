package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbCustPurchaseHistoryAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCustPurchaseHistoryAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCustPurchaseHistoryAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long addToSoldHistory(String Id, long Mobile, String Name, int Quantity, float SellingPrice, float Total, long StaffId) {
        Date date = new Date();
        String purchaseDate = DateUtil.convertToStringDate(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CUSTCARTPURCHASE_ID, Id);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_MOBILE, Mobile);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME, Name);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE, SellingPrice);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE, Total);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID, StaffId);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE, purchaseDate);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_CUST_PURCHASE_HISTORY, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<SoldProduct> getAllSoldProductsHistory() {
        ArrayList<SoldProduct> cartArrayList = new ArrayList<SoldProduct>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CUST_PURCHASE_HISTORY, DBHelper.CUSTPURCHASEHISTORY_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                SoldProduct soldProduct = new SoldProduct();
                soldProduct.setItemId(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_ID)));
                soldProduct.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));
                soldProduct.setItemName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME)));
                soldProduct.setItemSoldQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                soldProduct.setItemSellPrice(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE)));
                soldProduct.setItemTotalAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                soldProduct.setStaffId(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID)));
                soldProduct.setDate(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE)));

                cartArrayList.add(soldProduct);
            } while (c.moveToNext());
            closeDatabase();
            return cartArrayList;
        } else {
            return null;
        }
    }
}
