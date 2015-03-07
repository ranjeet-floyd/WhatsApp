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

    public long addItemToCart(String Id, String Name, String Quantity, String SellingPrice, String TotalPrice, String StoreId) {
        if (isAlreadyinCart(Id))
            return updateCartItemQuantityandTotalPrice(Id, Quantity, SellingPrice);
        else
            return addProductToCart(Id, Name, Quantity, SellingPrice, TotalPrice, StoreId);
    }

    public String getCurrentQuantity(String Id) {
        Cursor c = sqLiteDb.query(DBHelper.CART_TABLE, DBHelper.CART_COLUMNS,
                DBHelper.CART_PRODUCT_ID_COL + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return c.getString(c.getColumnIndexOrThrow(DBHelper.CART_PRODUCT_QUANTITY_COL));
        } else {
            return null;
        }
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
        Cursor c = sqLiteDb.query(DBHelper.CART_TABLE, DBHelper.CART_COLUMNS,
                DBHelper.CART_PRODUCT_ID_COL + "='" + id + "'", null, null, null, null);
        return c != null && c.moveToFirst();
    }

    public int updateCartItemQuantityandTotalPrice(String Id, String AddedQuantity, String SellingPrice) {
        String CurrentQuantity = getCurrentQuantity(Id);
        String TotalQuantity = String.valueOf(Integer.parseInt(CurrentQuantity) + Integer.parseInt(AddedQuantity));
        String TotalAmount = String.valueOf(Float.parseFloat(SellingPrice) * Integer.parseInt(AddedQuantity));
        ContentValues row = new ContentValues();
        row.put(DBHelper.CART_PRODUCT_QUANTITY_COL, TotalQuantity);
        row.put(DBHelper.CART_PRODUCT_TOTALPRICE_COL, TotalAmount);
        openWritableDatabase();
        return sqLiteDb.update(DBHelper.CART_TABLE, row, DBHelper.CART_PRODUCT_ID_COL + "='" + Id, null);
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

    public long addProductToCart(String Id, String Name, String Quantity, String SellingPrice, String TotalPrice, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.CART_PRODUCT_ID_COL, Id);
        row.put(DBHelper.CART_PRODUCT_NAME_COL, Name);
        row.put(DBHelper.CART_PRODUCT_QUANTITY_COL, Quantity);
        row.put(DBHelper.CART_PRODUCT_SELLINGPRICE_COL, SellingPrice);
        row.put(DBHelper.CART_PRODUCT_TOTALPRICE_COL, TotalPrice);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.CART_TABLE, null, row);
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
