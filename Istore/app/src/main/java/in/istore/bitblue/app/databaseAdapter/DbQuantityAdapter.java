package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.pojo.Product;
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

    public long insertQuantityDetails(String Id, int Quantity) {
        Date date = new Date();
        long todayDate = date.getTime();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_ADDEDDATE, todayDate);   //Insert current date in Unix Time format.

        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_QUANTITY_HISTORY, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<Product> getQuatityDetailsfor(String Id) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_QUANTITY_HISTORY, DBHelper.QUANTITY_DATE_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID)));
                product.setQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY)));
                product.setAddedDate(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ADDEDDATE)));
                productArrayList.add(product);
            } while (c.moveToNext());
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }
    }
}