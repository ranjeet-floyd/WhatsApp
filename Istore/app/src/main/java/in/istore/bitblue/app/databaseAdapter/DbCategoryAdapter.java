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

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long addNewCategory(String Name) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_CATEGORY_NAME, Name);
        openWritableDatabase();
        long result = sqLiteDb.insert(DBHelper.TABLE_CATEGORY, null, row);
        closeDatabase();
        return result;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categoryArrayList = new ArrayList<Category>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CATEGORY, DBHelper.CATEGORY_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryName(c.getString(c.getColumnIndexOrThrow("categoryName")));
                categoryArrayList.add(category);
            } while (c.moveToNext());
            closeDatabase();
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
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }
}