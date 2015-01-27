package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "istore.db";
    public static final int DATABASE_VERSION = 4;   //TO UPDATE DATABASE CHANGE THIS VERSION NUMBER

    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_QUANTITY_HISTORY = "quantityhistory";
    public static final String TABLE_SOLD_ITEMS = "solditems";
    public static final String TABLE_LOGIN_CRED_ADMIN = "logincredadmin";
    public static final String TABLE_STAFFMGNT = "staffmgnt";

    //Product Table Column
    public static final String COL_PROD_ID = "id";
    public static final String COL_PROD_IMAGE = "image";
    public static final String COL_PROD_NAME = "name";
    public static final String COL_PROD_DESC = "desc";
    public static final String COL_PROD_QUANTITY = "quantity";
    public static final String COL_PROD_PRICE = "price";
    public static final String COL_PROD_STATUS = "status";
    public static final String COL_PROD_DATE = "date";
    public static final String COL_PROD_FAVORITE = "isfavorite";

    //SoldItems Table Column
    public static final String COL_PROD_SELLPRICE = "sellPrice";
    public static final String COL_PROD_SOLDQUANTITY = "soldquantity";
    public static final String COL_PROD_REMAINQUANTITY = "remquantity";
    public static final String COL_PROD_SOLDDATE = "soldDate";

    //LoginCred Columns
    public static final String COL_LOGINCRED_NAME = "name";
    public static final String COL_LOGINCRED_EMAIL = "email";
    public static final String COL_LOGINCRED_MOBNUM = "mobile";
    public static final String COL_LOGINCRED_PASSWD = "passwd";
    public static final String COL_LOGINCRED_STOREID = "storeid";
    public static final String COL_LOGINCRED_STORENAME = "storename";
    public static final String COL_LOGINCRED_CREATION_DATE = "createdOn";

    //StaffManagement Table
    public static final String COL_STAFFMGNT_STOREID = "storeid";
    public static final String COL_STAFFMGNT_STAFFID = "staffid";
    public static final String COL_STAFFMGNT_NAME = "staffname";
    public static final String COL_STAFFMGNT_MOBNUM = "staffmobile";
    public static final String COL_STAFFMGNT_ADDRESS = "staffaddress";
    public static final String COL_STAFFMGNT_PASSWD = "staffpasswd";
    public static final String COL_STAFFMGNT_JOIN_DATE = "staffjoinOn";
    public static final String COL_STAFFMGNT_TOTALSALES = "stafftotsale";

    public static final String[] PRODUCT_COLUMNS = {COL_PROD_ID, COL_PROD_IMAGE, COL_PROD_NAME, COL_PROD_DESC, COL_PROD_QUANTITY, COL_PROD_PRICE, COL_PROD_STATUS, COL_PROD_DATE, COL_PROD_FAVORITE};
    public static final String[] QUANTITY_DATE_COLUMNS = {COL_PROD_ID, COL_PROD_QUANTITY, COL_PROD_DATE, COL_PROD_STATUS};
    public static final String[] SOLD_ITEM_COLUMNS = {COL_PROD_ID, COL_PROD_SOLDQUANTITY, COL_PROD_REMAINQUANTITY, COL_PROD_SOLDDATE, COL_PROD_SELLPRICE};
    public static final String[] LOGIN_CRED_ADMIN_COLUMNS = {COL_LOGINCRED_NAME, COL_LOGINCRED_EMAIL, COL_LOGINCRED_MOBNUM, COL_LOGINCRED_PASSWD, COL_LOGINCRED_STOREID, COL_LOGINCRED_CREATION_DATE};
    public static final String[] STAFFMGNT_COLUMNS = {COL_STAFFMGNT_STOREID, COL_STAFFMGNT_STAFFID, COL_STAFFMGNT_NAME, COL_STAFFMGNT_MOBNUM, COL_STAFFMGNT_ADDRESS, COL_STAFFMGNT_PASSWD, COL_STAFFMGNT_JOIN_DATE, COL_STAFFMGNT_TOTALSALES};

    //Product Table to store product details
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
                    COL_PROD_FAVORITE + " INTEGER)";

    //QuantityHistory Table to store product sold on some particular date
    public static final String CREATE_TABLE_QUANTITY_HISTORY =
            "CREATE TABLE " + TABLE_QUANTITY_HISTORY + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_QUANTITY + " TEXT," +
                    COL_PROD_DATE + " INTEGER," +
                    COL_PROD_STATUS + " TEXT)";

    //SoldItems Table to store items sold
    public static final String CREATE_TABLE_SOLD_ITEMS =
            "CREATE TABLE " + TABLE_SOLD_ITEMS + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_SOLDQUANTITY + " TEXT," +
                    COL_PROD_REMAINQUANTITY + " TEXT," +
                    COL_PROD_SOLDDATE + " INTEGER," +
                    COL_PROD_SELLPRICE + " TEXT)";

    //Login Credentials to store ADMIN information
    public static final String CREATE_TABLE_LOGIN_CRED_ADMIN =
            "CREATE TABLE " + TABLE_LOGIN_CRED_ADMIN + "(" +
                    COL_LOGINCRED_MOBNUM + " INTEGER PRIMARY KEY," +
                    COL_LOGINCRED_STOREID + " INTEGER UNIQUE," +
                    COL_LOGINCRED_STORENAME + " TEXT," +
                    COL_LOGINCRED_NAME + " TEXT," +
                    COL_LOGINCRED_EMAIL + " TEXT," +
                    COL_LOGINCRED_PASSWD + " TEXT," +
                    COL_LOGINCRED_CREATION_DATE + " TEXT)";

    //Login Credentials to store STAFF information
    public static final String CREATE_TABLE_STAFFMGNT =
            "CREATE TABLE " + TABLE_STAFFMGNT + "(" +
                    COL_STAFFMGNT_MOBNUM + " INTEGER PRIMARY KEY," +
                    COL_STAFFMGNT_STOREID + " INTEGER," +
                    COL_STAFFMGNT_STAFFID + " INTEGER UNIQUE," +
                    COL_STAFFMGNT_NAME + " TEXT," +
                    COL_STAFFMGNT_PASSWD + " TEXT," +
                    COL_STAFFMGNT_ADDRESS + " TEXT," +
                    COL_STAFFMGNT_JOIN_DATE + " TEXT," +
                    COL_STAFFMGNT_TOTALSALES + " INTEGER)";

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PRODUCT);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUANTITY_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SOLD_ITEMS);
        sqLiteDatabase.execSQL(CREATE_TABLE_LOGIN_CRED_ADMIN);
        sqLiteDatabase.execSQL(CREATE_TABLE_STAFFMGNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLD_ITEMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN_CRED_ADMIN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFFMGNT);
        onCreate(sqLiteDatabase);
    }
}

