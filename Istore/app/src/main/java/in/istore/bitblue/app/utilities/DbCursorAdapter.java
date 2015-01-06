package in.istore.bitblue.app.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import in.istore.bitblue.app.listMyStock.Product;

public class DbCursorAdapter {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCursorAdapter(Context context) {
        this.context = context;

        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCursorAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;

    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertProductDetails(String Id, byte[] ImagePath, String Name, String Desc, String Quantity, String Price) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_IMAGE, ImagePath);
        row.put(DBHelper.COL_PROD_NAME, Name);
        row.put(DBHelper.COL_PROD_DESC, Desc);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_PRICE, Price);
        openWritableDatabase();
        Log.e("Success:", "Database Opened");
        long result = sqLiteDb.insert(DBHelper.DATABASE_TABLE, null, row);
        Log.e("Result:", String.valueOf(result));
        closeDatabase();
        Log.e("Success:", "Values Into database");

        return result;

    }

    public Product getProductDetails(int Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
                DBHelper.COL_PROD_ID + "=" + Id, null, null, null, null);
        if (c != null)
            c.moveToFirst();
        String id = c.getColumnName(1);
        byte[] image = c.getBlob(2);
        String name = c.getColumnName(3);
        String desc = c.getColumnName(4);
        String quantity = c.getColumnName(5);
        String price = c.getColumnName(5);
        Product product = new Product(id, image, name, desc, quantity, price);
        return product;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
                null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(c.getString(c.getColumnIndexOrThrow("id")));
                product.setImage((c.getBlob(1)));
                product.setName(c.getString(c.getColumnIndexOrThrow("name")));
                product.setDesc(c.getString(c.getColumnIndexOrThrow("desc")));
                product.setQuantity(c.getString(c.getColumnIndexOrThrow("quantity")));
                product.setPrice(c.getString(c.getColumnIndexOrThrow("price")));
                productArrayList.add(product);
            } while (c.moveToNext());
        }
        closeDatabase();
        return productArrayList;
    }

    public long updateProductDetails(String Id, byte[] ImagePath, String Name, String Desc, String Quantity, String Price) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_IMAGE, ImagePath);
        row.put(DBHelper.COL_PROD_NAME, Name);
        row.put(DBHelper.COL_PROD_DESC, Desc);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_PRICE, Price);
        openWritableDatabase();
        long result = sqLiteDb.update(DBHelper.DATABASE_TABLE, row, DBHelper.COL_PROD_ID + "=" + Id, null);
        closeDatabase();
        return result;
    }

    public int deleteProduct(int Id) {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.DATABASE_TABLE,
                DBHelper.COL_PROD_ID + "=" + Id, null);
        closeDatabase();
        return result;
    }

    public int deleteAllProduct() {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.DATABASE_TABLE, null, null);
        closeDatabase();
        return result;


    }
}