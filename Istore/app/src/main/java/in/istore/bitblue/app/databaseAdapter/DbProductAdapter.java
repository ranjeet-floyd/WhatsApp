package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbProductAdapter {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbProductAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbProductAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertProductDetails(String Id, byte[] ImagePath, String Category, String Name, String Desc, int Quantity, int MinLimit, float CostPrice, float SellPrice, String Supplier) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateAndTime(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_ID, Id);
        row.put(DBHelper.COL_PROD_IMAGE, ImagePath);
        row.put(DBHelper.COL_PROD_CATEGORY, Category);
        row.put(DBHelper.COL_PROD_NAME, Name);
        row.put(DBHelper.COL_PROD_DESC, Desc);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_MINLIMIT, MinLimit);
        row.put(DBHelper.COL_PROD_COSTPRICE, CostPrice);
        row.put(DBHelper.COL_PROD_SELLINGPRICE, SellPrice);
        row.put(DBHelper.COL_PROD_SUPPLIER, Supplier);
        row.put(DBHelper.COL_PROD_ADDEDDATE, todayDate);   //Insert current date in Unix Time format.
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_PRODUCT, null, row);
        closeDatabase();
        return result;
    }


    public Product getProductDetails(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
            byte[] image = c.getBlob(1);
            String category = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_CATEGORY));
            String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
            String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC));
            int quantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
            int minlimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
            float costprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_COSTPRICE));
            float sellprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLINGPRICE));
            String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SUPPLIER));
            return new Product(id, image, category, name, quantity, desc, minlimit, costprice, sellprice, supplier);
        } else {
            return null;
        }
    }

    public Product getExistingProductDetails(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
            byte[] image = c.getBlob(1);
            String category = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_CATEGORY));
            String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
            String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC));
            int quantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
            int minlimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
            float costprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_COSTPRICE));
            float sellprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLINGPRICE));
            String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SUPPLIER));
            return new Product(id, image, category, name, quantity, desc, minlimit, costprice, sellprice, supplier);
        } else {
            return null;
        }
    }

    public ArrayList<Product> getAllSoldProducts(int limitt, int rowCount) {
        String id, name, solddate;
        byte[] image;
        int soldquantity, remquantity;
        float sellprice;

        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();

        String Columns = DBHelper.COL_PROD_ID + "," + DBHelper.COL_PROD_NAME + "," +
                DBHelper.COL_PROD_IMAGE + "," + DBHelper.COL_PROD_SOLDQUANTITY + "," + DBHelper.COL_PROD_REMAINQUANTITY + ","
                + DBHelper.COL_PROD_SELLPRICE + "," + DBHelper.COL_PROD_SOLDDATE;
        String RAW_QUERY = "SELECT DISTINCT " + Columns +
                " FROM " + DBHelper.TABLE_SOLD_ITEMS +
                " GROUP BY " + DBHelper.COL_PROD_ID +
                " ORDER BY " + DBHelper.COL_PROD_SOLDDATE + " DESC " +
                " LIMIT " + limitt + " OFFSET " + rowCount;

        Cursor csolDetails = sqLiteDb.rawQuery(RAW_QUERY, null);
        if ((csolDetails != null) && csolDetails.moveToFirst()) {
            do {
                Product product = new Product();

                id = csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
                name = csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
                image = csolDetails.getBlob(2);
                soldquantity = csolDetails.getInt(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDQUANTITY));
                remquantity = csolDetails.getInt(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_REMAINQUANTITY));
                sellprice = csolDetails.getFloat(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLPRICE));
                solddate = csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDDATE));
                product.setImage(image);
                product.setName(name);
                product.setId(id);
                product.setSoldQuantity(soldquantity);
                product.setRemQuantity(remquantity);
                product.setSellingPrice(sellprice);
                product.setSoldDate(solddate);
                productArrayList.add(product);
            } while (csolDetails.moveToNext());
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }
    }


    public ArrayList<Product> getAllProducts(int limit, int rowCount) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        String RAW_QUERY = "SELECT *" +
                " FROM " + DBHelper.TABLE_PRODUCT +
                " WHERE " + DBHelper.COL_PROD_QUANTITY + " > 0" +
                " LIMIT " + limit +
                " OFFSET " + rowCount;
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
                byte[] image = c.getBlob(1);
                String category = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_CATEGORY));
                String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
                String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC));
                int quantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
                int minlimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
                float costprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_COSTPRICE));
                float sellprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLINGPRICE));
                String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SUPPLIER));
                String addedDate = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ADDEDDATE));
                Product product = new Product(id, image, category, name, desc, quantity, minlimit, costprice, sellprice, supplier, addedDate);
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
                " FROM " + DBHelper.TABLE_PRODUCT +
                " WHERE " + DBHelper.COL_PROD_STATUS + "='" + status + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
                byte[] image = c.getBlob(1);
                String category = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_CATEGORY));
                String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
                String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC));
                int quantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
                int minlimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
                float costprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_COSTPRICE));
                float sellprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLINGPRICE));
                String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SUPPLIER));
                Product product = new Product(id, image, category, name, quantity, desc, minlimit, costprice, sellprice, supplier);
                productArrayList.add(product);
            } while (c.moveToNext());
            closeDatabase();
            return productArrayList;
        } else {
            return null;
        }

    }

    public int updateProductQuantity(String Id, int Quantity) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateAndTime(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_ADDEDDATE, todayDate);
        openWritableDatabase();
        int result = sqLiteDb.update(DBHelper.TABLE_PRODUCT, row, DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public long updateProductDetails(String Id, byte[] ImagePath, String Category, String Name, String Desc, int Quantity, int MinLimit, float SellPrice, String Supplier) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateAndTime(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_IMAGE, ImagePath);
        row.put(DBHelper.COL_PROD_CATEGORY, Category);
        row.put(DBHelper.COL_PROD_NAME, Name);
        row.put(DBHelper.COL_PROD_DESC, Desc);
        row.put(DBHelper.COL_PROD_QUANTITY, Quantity);
        row.put(DBHelper.COL_PROD_MINLIMIT, MinLimit);
        row.put(DBHelper.COL_PROD_SELLINGPRICE, SellPrice);
        row.put(DBHelper.COL_PROD_SUPPLIER, Supplier);
        row.put(DBHelper.COL_PROD_ADDEDDATE, todayDate);

        openWritableDatabase();
        long result = sqLiteDb.update(DBHelper.TABLE_PRODUCT, row, DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public long updateFavoriteProductDetails(String Id, int isFavorite) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PROD_FAVORITE, isFavorite);
        openWritableDatabase();
        long result = sqLiteDb.update(DBHelper.TABLE_PRODUCT, row, DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public int deleteProduct(String Id) {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.TABLE_PRODUCT,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        closeDatabase();
        return result;
    }

    public boolean idAlreadyPresent(String id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS, DBHelper.COL_PROD_ID + "=" + id, null, null, null, null, null);
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
        int result = sqLiteDb.delete(DBHelper.TABLE_PRODUCT, null, null);
        closeDatabase();
        return result;
    }

    public ArrayList<Product> sortBy(String column) {
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        openWritableDatabase();
        String orderBy = getColumnName(column);
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                null, null, null, null, orderBy);
        if (c != null && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ID));
                byte[] image = c.getBlob(1);
                String category = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_CATEGORY));
                String name = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME));
                String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC));
                int quantity = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_QUANTITY));
                int minlimit = c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_PROD_MINLIMIT));
                float costprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_COSTPRICE));
                float sellprice = c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLINGPRICE));
                String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_SUPPLIER));
                String addedDate = c.getString(c.getColumnIndexOrThrow(DBHelper.COL_PROD_ADDEDDATE));
                Product product = new Product(id, image, category, name, desc, quantity, minlimit, costprice, sellprice, supplier, addedDate);
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
            case "category":
                COLUMN = DBHelper.COL_PROD_CATEGORY;
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
            case "minlimit":
                COLUMN = DBHelper.COL_PROD_MINLIMIT;
                break;
            case "costprice":
                COLUMN = DBHelper.COL_PROD_COSTPRICE;
                break;
            case "sellingprice":
                COLUMN = DBHelper.COL_PROD_SELLINGPRICE;
                break;
            case "supplier":
                COLUMN = DBHelper.COL_PROD_SUPPLIER;
                break;
            case "addedOn":
                COLUMN = DBHelper.COL_PROD_ADDEDDATE;
                break;
        }
        return COLUMN;
    }

    public boolean isProductAvail(String id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public int getRowCount() {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                null, null, null, null, null);
        int rowCount = c.getCount();
        closeDatabase();
        return rowCount;
    }
}