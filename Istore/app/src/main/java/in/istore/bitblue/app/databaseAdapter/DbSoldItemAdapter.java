package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.listMyStock.Product;
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

    public long insertSoldItemQuantityDetail(String Id, String SoldQuantity, String RemQuantity, String sellPrice) {
        Date date = new Date();
        long todayDate = date.getTime();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_SOLDQUANTITY, SoldQuantity);
        row.put(DBHelper.COL_PROD_REMAINQUANTITY, RemQuantity);
        row.put(DBHelper.COL_PROD_SOLDDATE, todayDate);   //Insert current date in Unix Time format.
        row.put(DBHelper.COL_PROD_SELLPRICE, sellPrice);

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_SOLD_ITEMS, null, row);
        closeDatabase();
        return result;
    }

    public Product getSoldProductDetails(String Id) {
        openWritableDatabase();
        Product product = new Product();
        String orderBy = DBHelper.COL_PROD_SOLDDATE + " DESC";
        String limit = "1";

        Cursor csolDetails = sqLiteDb.query(DBHelper.TABLE_SOLD_ITEMS, DBHelper.SOLD_ITEM_COLUMN,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, orderBy, limit); //get the latest record from duplicate records

        if ((csolDetails != null && csolDetails.moveToFirst())) {
            product.setId(csolDetails.getString(csolDetails.getColumnIndexOrThrow("id")));
            product.setSoldQuantity(csolDetails.getString(csolDetails.getColumnIndexOrThrow("soldquantity")));
            product.setRemQuantity(csolDetails.getString(csolDetails.getColumnIndexOrThrow("remquantity")));
            product.setSoldDate(csolDetails.getLong(csolDetails.getColumnIndexOrThrow("soldDate")));
            product.setSellPrice(csolDetails.getString(csolDetails.getColumnIndexOrThrow("sellPrice")));
        } else {
            return null;
        }

        Cursor cprodetalis = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);

        if (cprodetalis != null && cprodetalis.moveToFirst()) {
            product.setImage(cprodetalis.getBlob(1));
            product.setName(cprodetalis.getString(cprodetalis.getColumnIndexOrThrow("name")));
            product.setDesc(cprodetalis.getString(cprodetalis.getColumnIndexOrThrow("desc")));
        } else {
            return null;
        }

        closeDatabase();
        return product;
    }

    public ArrayList<Product> getAllSoldDetailsfor(String id) {
        openWritableDatabase();
        ArrayList<Product> productList = new ArrayList<Product>();
        Cursor csolDetails = sqLiteDb.query(DBHelper.TABLE_SOLD_ITEMS, DBHelper.SOLD_ITEM_COLUMN,
                DBHelper.COL_PROD_ID + "='" + id + "'", null, null, null, null);
        if (csolDetails != null && csolDetails.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(id);
                product.setSoldQuantity(csolDetails.getString(csolDetails.getColumnIndexOrThrow("soldquantity")));
                product.setSoldDate(csolDetails.getLong(csolDetails.getColumnIndexOrThrow("soldDate")));
                product.setSellPrice(csolDetails.getString(csolDetails.getColumnIndexOrThrow("sellPrice")));
                productList.add(product);
            } while (csolDetails.moveToNext());
            closeDatabase();
            return productList;
        } else {
            return null;
        }
    }
}
