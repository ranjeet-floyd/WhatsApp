package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Category;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbCategoryAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCategoryAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCategoryAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long addNewCategory(String Name, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.CATEGORY_NAME_COL, Name);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.CATEGORY_TABLE, null, row);
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categoryArrayList = new ArrayList<Category>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.CATEGORY_TABLE, DBHelper.PRODUCTCATEGORY_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryName(c.getString(c.getColumnIndexOrThrow(DBHelper.CATEGORY_NAME_COL)));
                categoryArrayList.add(category);
            } while (c.moveToNext());
            return categoryArrayList;
        } else {
            return null;
        }
    }

    public boolean isCategoryAlreadyExist(String CategoryName) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CATEGORY, DBHelper.CATEGORY_COLUMNS,
                DBHelper.COL_CATEGORY_NAME + "='" + CategoryName + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> categoryNameList = new ArrayList<String>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.CATEGORY_TABLE, DBHelper.PRODUCTCATEGORY_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                categoryNameList.add(c.getString(c.getColumnIndexOrThrow(DBHelper.CATEGORY_NAME_COL)));
            } while (c.moveToNext());
            return categoryNameList;
        } else {
            return null;
        }

    }

    public ArrayList<Category> fetchPendingRowsToUpdate() {
        ArrayList<Category> categoryArrayList = new ArrayList<Category>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.CATEGORY_TABLE, DBHelper.PRODUCTCATEGORY_COLUMNS,
                DBHelper.IS_UPDATED + "='" + "no" + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryName(c.getString(c.getColumnIndexOrThrow(DBHelper.CATEGORY_NAME_COL)));
                category.setStoreId(c.getString(c.getColumnIndexOrThrow(DBHelper.STOREID_COL)));
                categoryArrayList.add(category);
            } while (c.moveToNext());
            return categoryArrayList;
        } else {
            return null;
        }
    }
}
