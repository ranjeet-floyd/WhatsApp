package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.CartItem;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbCartAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCartAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCartAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long addItemToCart(String Id, String Name, int Quantity, float SellingPrice, float Total) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CARTITEM_ID, Id);
        row.put(DBHelper.COL_CARTITEM_NAME, Name);
        row.put(DBHelper.COL_CARTITEM_QUANTITY, Quantity);
        row.put(DBHelper.COL_CARTITEM_SELLINGPRICE, SellingPrice);
        row.put(DBHelper.COL_CARTITEM_TOTALPRICE, (Quantity * SellingPrice));

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_CART, null, row);
        return result;
    }

    public ArrayList<CartItem> getAllCartItems() {
        ArrayList<CartItem> cartArrayList = new ArrayList<CartItem>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CART, DBHelper.CARTITEM_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setItemId(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CARTITEM_ID)));
                cartItem.setItemName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CARTITEM_NAME)));
                cartItem.setItemSoldQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CARTITEM_QUANTITY)));
                cartItem.setItemSellPrice(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CARTITEM_SELLINGPRICE)));
                cartItem.setItemTotalAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CARTITEM_TOTALPRICE)));
                cartArrayList.add(cartItem);
            } while (c.moveToNext());
            return cartArrayList;
        } else {
            return null;
        }
    }

    public boolean isAlreadyinCart(String id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CART, DBHelper.CARTITEM_COLUMNS,
                DBHelper.COL_CARTITEM_ID + "='" + id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public int updateCartItemQuantityandAmount(String Id, String Name, int Quantity, float totalAmount) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CARTITEM_QUANTITY, Quantity);
        row.put(DBHelper.COL_CARTITEM_TOTALPRICE, totalAmount);
        openWritableDatabase();
        int result = sqLiteDb.update(DBHelper.TABLE_CART, row, DBHelper.COL_CARTITEM_ID + "='" + Id + "'OR " + DBHelper.COL_CARTITEM_NAME + "='" + Name + "'", null);
        return result;
    }

    public float getTotalPayAmount() {

        String SUM_QUERY = "SELECT SUM(" + DBHelper.COL_CARTITEM_TOTALPRICE + ")" +
                " FROM " + DBHelper.TABLE_CART;
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(SUM_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_CARTITEM_TOTALPRICE + ")"));
        } else {
            return 0;
        }
    }

    public long insertEachCartItem(String Id, long Mobile, String Name, int Quantity, float SellingPrice, float Total, long StaffId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CUSTCARTPURCHASE_ID, Id);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_MOBILE, Mobile);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME, Name);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE, SellingPrice);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE, Total);
        row.put(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID, StaffId);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_CUST_CART_PURCHASE, null, row);
        return result;
    }

    //To empty cart once the cart is sold
    public int emptyCart() {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.TABLE_CART, null, null);
        return result;
    }

    //To empty all purchases by one customer once the cart is sold
    public int clearAllPurchases() {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.TABLE_CUST_CART_PURCHASE, null, null);
        return result;
    }

}
