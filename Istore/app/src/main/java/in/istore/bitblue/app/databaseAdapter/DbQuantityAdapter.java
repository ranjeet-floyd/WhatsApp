package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbQuantityAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbQuantityAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbQuantityAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertQuantityDetails(String Id, String Quantity) {
        Date date = new Date();
        long todayDate = date.getTime();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_DATE, todayDate);   //Insert current date in Unix Time format.

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_QUANTITY_HISTORY, null, row);
        closeDatabase();
        return result;
    }

    public Product getQuatityDetails(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_QUANTITY_HISTORY, DBHelper.QUANTITY_DATE_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow("id"));
            byte[] image = c.getBlob(1);
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            String desc = c.getString(c.getColumnIndexOrThrow("desc"));
            String quantity = c.getString(c.getColumnIndexOrThrow("quantity"));
            String price = c.getString(c.getColumnIndexOrThrow("price"));
            Product product = new Product(id, image, name, desc, quantity, price);
            return product;
        } else {
            return null;
        }
    }
}
