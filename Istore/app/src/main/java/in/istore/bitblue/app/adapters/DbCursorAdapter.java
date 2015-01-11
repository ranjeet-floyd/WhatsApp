package in.istore.bitblue.app.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.utilities.DBHelper;

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
        row.put(DBHelper.COL_PROD_STATUS, "not sold");
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.DATABASE_TABLE, null, row);
        closeDatabase();
        return result;
    }

    public Product getProductDetails(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
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

    public ArrayList<Product> getAllProducts(String status, int limit ,int rowCount) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        String RAW_QUERY = "SELECT *" +
                " FROM " + DBHelper.DATABASE_TABLE +
                " WHERE " + DBHelper.COL_PROD_STATUS + "='" + status + "'" +
                " LIMIT "+limit +
                " OFFSET " + rowCount;
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
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
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }

    }

    public ArrayList<Product> getAllProducts(String status) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        String RAW_QUERY = "SELECT *" +
                " FROM " + DBHelper.DATABASE_TABLE +
                " WHERE " + DBHelper.COL_PROD_STATUS + "='" + status + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
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
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }

    }
    public long updateProductDetails(String Id, byte[] ImagePath, String Name, String Desc, String Quantity, String Price) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_IMAGE, ImagePath);
        row.put(DBHelper.COL_PROD_NAME, Name);
        row.put(DBHelper.COL_PROD_DESC, Desc);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_PRICE, Price);
        openWritableDatabase();
        long result = sqLiteDb.update(DBHelper.DATABASE_TABLE, row, DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public int deleteProduct(String Id) {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.DATABASE_TABLE,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public boolean idAlreadyPresent(String id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS, DBHelper.COL_PROD_ID + "=" + id, null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public int deleteAllProduct() {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.DATABASE_TABLE, null, null);
        closeDatabase();
        return result;


    }

    public long updateSoldProductDetails(String Id) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_STATUS, "sold");
        openWritableDatabase();
        long result = sqLiteDb.update(DBHelper.DATABASE_TABLE, row, DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public ArrayList<Product> getAllSoldProducts(String status) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
                DBHelper.COL_PROD_STATUS + "='" + status + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
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
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<Product> sortBy(String column) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        String orderBy = getColumnName(column);
        String status = "not sold";
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
                DBHelper.COL_PROD_STATUS + "='" + status + "'", null, null, null, orderBy);
        if (c != null && c.moveToFirst()) {
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
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }
    }

    public String getColumnName(String col) {
        String COLUMN = null;
        switch (col) {
            case "id":
                COLUMN = DBHelper.COL_PROD_ID;
                break;
            case "name":
                COLUMN = DBHelper.COL_PROD_NAME;
                break;
            case "desc":
                COLUMN = DBHelper.COL_PROD_DESC;
                break;
            case "quantity":
                COLUMN = DBHelper.COL_PROD_QUANTITY;
                break;
            case "price":
                COLUMN = DBHelper.COL_PROD_PRICE;
                break;
        }
        return COLUMN;
    }

    public int getRowCount() {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.DATABASE_TABLE, DBHelper.COLUMNS,
                null, null, null, null, null);
        int rowCount = c.getCount();
        closeDatabase();
        return rowCount;
    }
}