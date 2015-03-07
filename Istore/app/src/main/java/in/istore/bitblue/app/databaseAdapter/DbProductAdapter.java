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

    public long addProduct(String Id, String Image, String Category, String Name, String Desc, String Quantity, String MinLimit,
                           String CostPrice, String SellPrice, String StoreId, String Supplier) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.PRODUCT_ID_COL, Id);
        row.put(DBHelper.PRODUCT_IMAGE_COL, Image);
        row.put(DBHelper.PRODUCT_CATEGORY_COL, Category);
        row.put(DBHelper.PRODUCT_NAME_COL, Name);
        row.put(DBHelper.PRODUCT_DESC_COL, Desc);
        row.put(DBHelper.PRODUCT_QUANTITY_COL, Quantity);
        row.put(DBHelper.PRODUCT_MINLIMIT_COL, MinLimit);
        row.put(DBHelper.PRODUCT_COSTPRICE_COL, CostPrice);
        row.put(DBHelper.PRODUCT_SELLINGPRICE_COL, SellPrice);
        row.put(DBHelper.PRODUCT_SUPPLIER_COL, Supplier);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        row.put(DBHelper.PRODUCT_ADDEDON_COL, todayDate);
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.PRODUCT_TABLE, null, row);
    }

    public Product getProductDetails(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                DBHelper.PRODUCT_ID_COL + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String prodImage = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_IMAGE_COL));
            String name = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_NAME_COL));
            String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_DESC_COL));
            String quantity = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_QUANTITY_COL));
            String sellPrice = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_SELLINGPRICE_COL));
            return new Product(prodImage, name, quantity, desc, sellPrice);
        } else {
            return null;
        }
    }

    public Product getExistingProductDetails(String categoryName, String prodName, String Id) {
        openWritableDatabase();
        Cursor c = null;
        if (categoryName.equals("") && prodName.equals(""))
            c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                    DBHelper.PRODUCT_ID_COL + "='" + Id + "'", null, null, null, null);
        else if (Id.equals(""))
            c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                    DBHelper.PRODUCT_CATEGORY_COL + "='" + categoryName + "' AND " +
                            DBHelper.PRODUCT_NAME_COL + "='" + prodName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_ID_COL));
            String image = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_IMAGE_COL));
            String category = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_CATEGORY_COL));
            String name = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_NAME_COL));
            String desc = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_DESC_COL));
            String quantity = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_QUANTITY_COL));
            String minlimit = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_MINLIMIT_COL));
            String costprice = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_COSTPRICE_COL));
            String sellprice = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_SELLINGPRICE_COL));
            String supplier = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_SUPPLIER_COL));
            String addedDate = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_ADDEDON_COL));
            return new Product(id, image, category, name, desc, quantity, minlimit, costprice, sellprice, supplier, addedDate);
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
        return result;
    }

    public int updateRemainingProductQuantity(String prodId, int Quantity) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateAndTime(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.PRODUCT_QUANTITY_COL, Quantity);
        openWritableDatabase();
        int result = sqLiteDb.update(DBHelper.PRODUCT_TABLE, row, DBHelper.PRODUCT_ID_COL + "='" + prodId + "'", null);
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
        return result;
    }

    public long updateProductQuantityAndSellPrice(String prodCategory, String prodName, String Quantity, String SellPrice, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.PRODUCT_QUANTITY_COL, Quantity);
        row.put(DBHelper.PRODUCT_SELLINGPRICE_COL, SellPrice);
        openWritableDatabase();
        return (long) sqLiteDb.update(DBHelper.PRODUCT_TABLE, row,
                DBHelper.PRODUCT_CATEGORY_COL + "='" + prodCategory + "' AND " +
                        DBHelper.PRODUCT_NAME_COL + "='" + prodName + "' AND " +
                        DBHelper.STOREID_COL + "='" + StoreId, null);
    }

    public int deleteProduct(String Id) {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.TABLE_PRODUCT,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null);
        return result;
    }

    public boolean idAlreadyPresent(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS, DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null, null);
        if (c != null && c.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int deleteAllProduct() {
        openWritableDatabase();
        int result = sqLiteDb.delete(DBHelper.TABLE_PRODUCT, null, null);
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
            return true;
        } else {
            return false;
        }
    }

    public int getRowCount() {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                null, null, null, null, null);
        int rowCount = c.getCount();
        return rowCount;
    }

    public boolean isProductExisting(String categoryName, String prodName, String prodId) {
        openWritableDatabase();
        Cursor c = null;
        if (categoryName.equals("") && prodName.equals("")) {
            c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                    DBHelper.PRODUCT_ID_COL + "='" + prodId + "'", null, null, null, null, null);
        } else if (prodId.equals("")) {
            c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                    DBHelper.PRODUCT_CATEGORY_COL + "='" + categoryName + "' AND " +
                            DBHelper.PRODUCT_NAME_COL + "='" + prodName + "'", null, null, null, null, null);
        }
        return (c != null && c.getCount() != 0);
    }

    public ArrayList<Product> getAllProductForCategory(String categoryName) {
        openWritableDatabase();
        ArrayList<Product> productArrayList = new ArrayList<Product>();
        Cursor c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                DBHelper.PRODUCT_CATEGORY_COL + "='" + categoryName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_ID_COL));
                String image = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_IMAGE_COL));
                String name = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_NAME_COL));
                String quantity = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_QUANTITY_COL));
                String addedDate = c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_ADDEDON_COL));
                Product product = new Product(id, image, name, categoryName, quantity, addedDate);
                productArrayList.add(product);
            } while (c.moveToNext());
            return productArrayList;
        } else {
            return null;
        }
    }

    public String getMinlimit(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                DBHelper.PRODUCT_ID_COL + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst())
            return c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_MINLIMIT_COL));
        else return null;

    }

    public String getSupplierMobile(String Id) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                DBHelper.PRODUCT_ID_COL + "='" + Id + "'", null, null, null, null);
        if (c != null && c.moveToFirst())
            return c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_SUPPLIER_COL));
        else return null;
    }

    public long decreaseProductQuantity(String Id, String remQuantity) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.PRODUCT_QUANTITY_COL, remQuantity);
        openWritableDatabase();
        int result = sqLiteDb.update(DBHelper.PRODUCT_TABLE, row, DBHelper.PRODUCT_ID_COL + "='" + Id + "'", null);
        return result;
    }

    public String getProductImage(String prodId) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.PRODUCT_TABLE, DBHelper.PRODUCT_COLUMNS,
                DBHelper.PRODUCT_ID_COL + "='" + prodId + "'", null, null, null, null);
        if (c != null && c.moveToFirst())
            return c.getString(c.getColumnIndexOrThrow(DBHelper.PRODUCT_IMAGE_COL));
        else return null;
    }
}