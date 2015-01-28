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

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long addNewProSubCategory(String CategoryName, String SubCategoryName) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CATEGORY_NAME, CategoryName);
        row.put(DBHelper.COL_SUBCATEGORY_NAME, SubCategoryName);
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_SUBCATEGORY, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<ProductSubCategory> getAllProSubCategories(String Category) {
        ArrayList<ProductSubCategory> categoryArrayList = new ArrayList<ProductSubCategory>();
        openWritableDatabase();

        String CATEGORY_CATEGORYNAME = DBHelper.TABLE_CATEGORY + "." + DBHelper.COL_CATEGORY_NAME;
        String SUBCATEGORY_CATEGORYNAME = DBHelper.TABLE_SUBCATEGORY + "." + DBHelper.COL_CATEGORY_NAME;

        String rawQuery = "SELECT " + DBHelper.COL_SUBCATEGORY_NAME +
                " FROM " + DBHelper.TABLE_SUBCATEGORY +
                " INNER JOIN " + DBHelper.TABLE_CATEGORY + " ON " +
                CATEGORY_CATEGORYNAME + "=" + SUBCATEGORY_CATEGORYNAME +
                " WHERE " + SUBCATEGORY_CATEGORYNAME + "='" + Category + "'";

        Cursor c = sqLiteDb.rawQuery(rawQuery, null);
        if (c != null && c.moveToFirst()) {
            do {
                ProductSubCategory productSubCategory = new ProductSubCategory();
                productSubCategory.setProductSubCategoryName(c.getString(c.getColumnIndexOrThrow("prosubcatName")));
                categoryArrayList.add(productSubCategory);
            } while (c.moveToNext());
            closeDatabase();
            return categoryArrayList;
        } else {
            return null;
        }
    }

    public boolean isCategoryAlreadyExist(String SubCategoryName) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_SUBCATEGORY, DBHelper.PROSUBCAT_COLUMNS,
                DBHelper.COL_SUBCATEGORY_NAME + "='" + SubCategoryName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }
}
