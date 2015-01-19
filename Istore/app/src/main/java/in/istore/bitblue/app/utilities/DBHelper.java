package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "istore.db";
    public static final int DATABASE_VERSION = 1;   //TO UPDATE DATABASE CHANGE THIS VERSION NUMBER

    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_QUANTITY_HISTORY = "quantityhistory";
    public static final String TABLE_SOLD_ITEMS = "solditems";

    public static final String COL_PROD_ID = "id";
    public static final String COL_PROD_IMAGE = "image";
    public static final String COL_PROD_NAME = "name";
    public static final String COL_PROD_DESC = "desc";
    public static final String COL_PROD_QUANTITY = "quantity";
    public static final String COL_PROD_PRICE = "price";
    public static final String COL_PROD_STATUS = "status";
    public static final String COL_PROD_DATE = "date";

    public static final String COL_PROD_FAVORITE = "isfavorite";

    public static final String COL_PROD_SELLPRICE = "sellPrice";
    public static final String COL_PROD_SOLDQUANTITY = "soldquantity";
    public static final String COL_PROD_REMAINQUANTITY = "remquantity";
    public static final String COL_PROD_SOLDDATE = "soldDate";

    public static final String[] PRODUCT_COLUMNS = {COL_PROD_ID, COL_PROD_IMAGE, COL_PROD_NAME, COL_PROD_DESC, COL_PROD_QUANTITY, COL_PROD_PRICE, COL_PROD_STATUS, COL_PROD_DATE, COL_PROD_FAVORITE};
    public static final String[] QUANTITY_DATE_COLUMNS = {COL_PROD_ID, COL_PROD_QUANTITY, COL_PROD_DATE, COL_PROD_STATUS};
    public static final String[] SOLD_ITEM_COLUMN = {COL_PROD_ID, COL_PROD_SOLDQUANTITY, COL_PROD_REMAINQUANTITY, COL_PROD_SOLDDATE, COL_PROD_SELLPRICE};

    //Product table to store Product Details
    public static final String CREATE_TABLE_PRODUCT =
            "CREATE TABLE " + TABLE_PRODUCT + "(" +
                    COL_PROD_ID + " TEXT PRIMARY KEY," +
                    COL_PROD_IMAGE + " TEXT," +
                    COL_PROD_NAME + " TEXT," +
                    COL_PROD_DESC + " TEXT," +
                    COL_PROD_QUANTITY + " TEXT," +
                    COL_PROD_PRICE + " TEXT," +
                    COL_PROD_STATUS + " TEXT," +
                    COL_PROD_DATE + " INTEGER," +
                    COL_PROD_FAVORITE + " INTEGER )";


    //Used to store the product sold on some particular date
    public static final String CREATE_TABLE_QUANTITY_HISTORY =
            "CREATE TABLE " + TABLE_QUANTITY_HISTORY + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_QUANTITY + " TEXT," +
                    COL_PROD_DATE + " INTEGER," +
                    COL_PROD_STATUS + " TEXT)";


    public static final String CREATE_TABLE_SOLD_ITEMS =
            "CREATE TABLE " + TABLE_SOLD_ITEMS + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_SOLDQUANTITY + " TEXT," +
                    COL_PROD_REMAINQUANTITY + " TEXT," +
                    COL_PROD_SOLDDATE + " INTEGER," +
                    COL_PROD_SELLPRICE + " TEXT)";

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e("Success:", "Database Helper");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCT);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUANTITY_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SOLD_ITEMS);
        Log.e("Success:", "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLD_ITEMS);
        onCreate(sqLiteDatabase);
    }
}

