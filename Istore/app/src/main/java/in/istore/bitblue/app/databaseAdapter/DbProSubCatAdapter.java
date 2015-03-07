package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.ProductSubCategory;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbProSubCatAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbProSubCatAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbProSubCatAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long addNewProductInCategory(String CategoryName, String SubCategoryName, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CATEGORY_NAME, CategoryName);
        row.put(DBHelper.COL_SUBCATEGORY_NAME, SubCategoryName);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_SUBCATEGORY, null, row);
        return result;
    }

    public ArrayList<ProductSubCategory> getAllProSubCategories(String Category) {
        ArrayList<ProductSubCategory> productsInCategory = new ArrayList<ProductSubCategory>();
        openWritableDatabase();
        String CATEGORY_CATEGORYNAME = DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_NAME_COL;
        String SUBCATEGORY_CATEGORYNAME = DBHelper.SUBCATEGORY_TABLE + "." + DBHelper.SUBCATEGORY_CATEGORYNAME_COL;
        String rawQuery = "SELECT " + DBHelper.SUBCATEGORY_PRODUCTCATEGORY_NAME_COL +
                " FROM " + DBHelper.SUBCATEGORY_TABLE +
                " INNER JOIN " + DBHelper.CATEGORY_TABLE + " ON " +
                CATEGORY_CATEGORYNAME + "=" + SUBCATEGORY_CATEGORYNAME +
                " WHERE " + SUBCATEGORY_CATEGORYNAME + "='" + Category + "'";
        Cursor c = sqLiteDb.rawQuery(rawQuery, null);
        if (c != null && c.moveToFirst()) {
            do {
                ProductSubCategory productSubCategory = new ProductSubCategory();
                productSubCategory.setProductSubCategoryName(c.getString(c.getColumnIndexOrThrow(DBHelper.SUBCATEGORY_PRODUCTCATEGORY_NAME_COL)));
                productsInCategory.add(productSubCategory);
            } while (c.moveToNext());
            return productsInCategory;
        } else {
            return null;
        }
    }

    public boolean isProductAlreadyExist(String SubCategoryName) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_SUBCATEGORY, DBHelper.PROSUBCAT_COLUMNS,
                DBHelper.COL_SUBCATEGORY_NAME + "='" + SubCategoryName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getAllProductNamesIn(String Category) {
        ArrayList<String> prodCatNameList = new ArrayList<String>();
        openWritableDatabase();
        String CATEGORY_CATEGORYNAME = DBHelper.CATEGORY_TABLE + "." + DBHelper.CATEGORY_NAME_COL;
        String SUBCATEGORY_CATEGORYNAME = DBHelper.SUBCATEGORY_TABLE + "." + DBHelper.SUBCATEGORY_CATEGORYNAME_COL;

        String rawQuery = "SELECT " + DBHelper.SUBCATEGORY_PRODUCTCATEGORY_NAME_COL +
                " FROM " + DBHelper.SUBCATEGORY_TABLE +
                " INNER JOIN " + DBHelper.CATEGORY_TABLE + " ON " +
                CATEGORY_CATEGORYNAME + "=" + SUBCATEGORY_CATEGORYNAME +
                " WHERE " + SUBCATEGORY_CATEGORYNAME + "='" + Category + "'";

        Cursor c = sqLiteDb.rawQuery(rawQuery, null);
        if (c != null && c.moveToFirst()) {
            do {
                prodCatNameList.add(c.getString(c.getColumnIndexOrThrow(DBHelper.SUBCATEGORY_PRODUCTCATEGORY_NAME_COL)));
            } while (c.moveToNext());
            return prodCatNameList;
        } else {
            return null;
        }
    }

    public ArrayList<String> getAllProductNames() {
        ArrayList<String> prodNameList = new ArrayList<String>();
        openWritableDatabase();
        String rawQuery = "SELECT " + DBHelper.SUBCATEGORY_CATEGORYNAME_COL +
                " FROM " + DBHelper.SUBCATEGORY_TABLE;
        Cursor c = sqLiteDb.rawQuery(rawQuery, null);
        if (c != null && c.moveToFirst()) {
            do {
                prodNameList.add(c.getString(c.getColumnIndexOrThrow(DBHelper.SUBCATEGORY_CATEGORYNAME_COL)));
            } while (c.moveToNext());
            return prodNameList;
        } else {
            return null;
        }
    }

    public ArrayList<ProductSubCategory> fetchPendingRowsToUpdate() {
        ArrayList<ProductSubCategory> productsInCategory = new ArrayList<ProductSubCategory>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.SUBCATEGORY_TABLE, DBHelper.PRODUCTSUBCATEGORY_COLUMNS,
                DBHelper.IS_UPDATED + "='" + "no" + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                ProductSubCategory prodInCategory = new ProductSubCategory();
                prodInCategory.setProductSubCategoryName(c.getString(c.getColumnIndexOrThrow(DBHelper.SUBCATEGORY_PRODUCTCATEGORY_NAME_COL)));
                productsInCategory.add(prodInCategory);
            } while (c.moveToNext());
            return productsInCategory;
        } else {
            return null;
        }
    }

}
