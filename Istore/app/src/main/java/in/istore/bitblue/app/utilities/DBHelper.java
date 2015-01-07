package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "istore.db";
    public static final String DATABASE_TABLE = "product";
    public static final int DATABASE_VERSION = 1;

    public static final String COL_PROD_ID = "id";
    public static final String COL_PROD_IMAGE = "image";
    public static final String COL_PROD_NAME = "name";
    public static final String COL_PROD_DESC = "desc";
    public static final String COL_PROD_QUANTITY = "quantity";
    public static final String COL_PROD_PRICE = "price";
    public static final String COL_PROD_STATUS = "status";

    public static final String[] COLUMNS = {COL_PROD_ID, COL_PROD_IMAGE, COL_PROD_NAME, COL_PROD_DESC, COL_PROD_QUANTITY, COL_PROD_PRICE, COL_PROD_STATUS};

    public static final String CREATE_DATABASE =
            "CREATE TABLE " + DATABASE_TABLE + "(" +
                    COL_PROD_ID + " text PRIMARY KEY," +
                    COL_PROD_IMAGE + " text," +
                    COL_PROD_NAME + " text," +
                    COL_PROD_DESC + " text," +
                    COL_PROD_QUANTITY + " text," +
                    COL_PROD_PRICE + " text," +
                    COL_PROD_STATUS + " text)";

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e("Success:", "Database Helper");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DATABASE);
        Log.e("Success:", "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
