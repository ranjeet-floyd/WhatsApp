package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "istore.db";
    public static final int DATABASE_VERSION = 31;   //TO UPDATE DATABASE CHANGE THIS VERSION NUMBER

    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_QUANTITY_HISTORY = "quantityhistory";
    public static final String TABLE_SOLD_ITEMS = "solditems";
    public static final String TABLE_LOGIN_CRED_ADMIN = "logincredadmin";
    public static final String TABLE_STAFFMGNT = "staffmgnt";
    public static final String TABLE_SUPPINFO = "suppinfo";
    public static final String TABLE_CUSTINFO = "custinfo";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_SUBCATEGORY = "subcategory";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_CUST_CART_PURCHASE = "custcartpurchase";
    public static final String TABLE_CUST_PURCHASE_HISTORY = "custpurchasehistory";
    public static final String TABLE_TOTAL_SALES_BY_DATE = "totalsalesbydate";
    public static final String TABLE_OUTOFSTOCK_ITEMS = "outofstockitems";

    //Product Table Column
    public static final String COL_PROD_ID = "id";
    public static final String COL_PROD_IMAGE = "image";
    public static final String COL_PROD_CATEGORY = "category";
    public static final String COL_PROD_NAME = "name";
    public static final String COL_PROD_DESC = "desc";
    public static final String COL_PROD_QUANTITY = "quantity";
    public static final String COL_PROD_MINLIMIT = "minlimit";
    public static final String COL_PROD_COSTPRICE = "costprice";
    public static final String COL_PROD_SELLINGPRICE = "sellingprice";
    public static final String COL_PROD_SUPPLIER = "supplier";
    public static final String COL_PROD_STATUS = "status";
    public static final String COL_PROD_ADDEDDATE = "addeddOn";
    public static final String COL_PROD_FAVORITE = "isfavorite";

    //SoldItems Table Column

    public static final String COL_PROD_SELLPRICE = "sellPrice";
    public static final String COL_PROD_SOLDQUANTITY = "soldquantity";
    public static final String COL_PROD_REMAINQUANTITY = "remquantity";
    public static final String COL_PROD_SOLDDATE = "soldDate";

    //LoginCred Table Columns
    public static final String COL_LOGINCRED_NAME = "name";
    public static final String COL_LOGINCRED_EMAIL = "email";
    public static final String COL_LOGINCRED_MOBNUM = "mobile";
    public static final String COL_LOGINCRED_PASSWD = "passwd";
    public static final String COL_LOGINCRED_STOREID = "storeid";
    public static final String COL_LOGINCRED_STORENAME = "storename";
    public static final String COL_LOGINCRED_CREATION_DATE = "createdOn";

    //StaffManagement Table Columns
    public static final String COL_STAFFMGNT_STOREID = "storeid";
    public static final String COL_STAFFMGNT_STAFFID = "staffid";
    public static final String COL_STAFFMGNT_NAME = "staffname";
    public static final String COL_STAFFMGNT_MOBNUM = "staffmobile";
    public static final String COL_STAFFMGNT_ADDRESS = "staffaddress";
    public static final String COL_STAFFMGNT_PASSWD = "staffpasswd";
    public static final String COL_STAFFMGNT_JOIN_DATE = "staffjoinOn";
    public static final String COL_STAFFMGNT_TOTALSALES = "stafftotsale";

    //Supplier Table Columns
    public static final String COL_SUPPINFO_NAME = "suppname";
    public static final String COL_SUPPINFO_MOBILE = "suppmobile";
    public static final String COL_SUPPINFO_ADDRESS = "suppaddress";
    public static final String COL_SUPPINFO_STARTING_DATE = "suppstartdate";

    //CustomerPurchaseAmount Table Column
    public static final String COL_CUSTPURCHASE_MOBILE = "custmobile";
    public static final String COL_CUSTPURCHASE_AMOUNT = "custpurchaseAmt";

    //Category Table Column
    public static final String COL_CATEGORY_ID = "categoryid";
    public static final String COL_CATEGORY_NAME = "categoryName";

    //Subcategory Table Column
    public static final String COL_SUBCATEGORY_ID = "prosubcatid";
    public static final String COL_SUBCATEGORY_NAME = "prosubcatName";

    //Cart Table Column
    public static final String COL_CARTITEM_ID = "cid";
    public static final String COL_CARTITEM_NAME = "name";
    public static final String COL_CARTITEM_QUANTITY = "quantity";
    public static final String COL_CARTITEM_SELLINGPRICE = "sellingprice";
    public static final String COL_CARTITEM_TOTALPRICE = "totalprice";

    //CustomerPurchase Table Columns
    public static final String COL_CUSTCARTPURCHASE_ID = "custpurid";
    public static final String COL_CUSTCARTPURCHASE_MOBILE = "custmobile";
    public static final String COL_CUSTCARTPURCHASE_PROD_NAME = "custprodname";
    public static final String COL_CUSTCARTPURCHASE_PROD_QUANTITY = "custquantity";
    public static final String COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE = "custsellprice";
    public static final String COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE = "custtotalprice";
    public static final String COL_CUSTCARTPURCHASE_STAFF_ID = "custstaffid";
    public static final String COL_CUSTCARTPURCHASE_PURCHASE_DATE = "custpurchasedate";

    //TotalStockSales Table Column
    public static final String COL_TOTALSALES_PERDAY_SALESAMOUNT = "perdaysales";
    public static final String COL_TOTALSALES_PERDAY_DATE = "perdaydate";

    //OutOfStockItems Table Column
    public static final String COL_OUTOFSTOCK_PRODID = "oosprodid";
    public static final String COL_OUTOFSTOCK_PRODNAME = "oosprodname";
    public static final String COL_OUTOFSTOCK_REMAIN_QUANTITY = "remainquantity";
    public static final String COL_OUTOFSTOCK_SUPPMOBILE = "suppmobile";

    public static final String[] PRODUCT_COLUMNS = {COL_PROD_ID, COL_PROD_IMAGE, COL_PROD_CATEGORY, COL_PROD_NAME, COL_PROD_DESC, COL_PROD_QUANTITY, COL_PROD_MINLIMIT, COL_PROD_COSTPRICE, COL_PROD_SELLINGPRICE, COL_PROD_SUPPLIER, COL_PROD_ADDEDDATE, COL_PROD_FAVORITE};
    public static final String[] QUANTITY_DATE_COLUMNS = {COL_PROD_ID, COL_PROD_QUANTITY, COL_PROD_ADDEDDATE};
    public static final String[] SOLD_ITEM_COLUMNS = {COL_PROD_ID, COL_PROD_NAME, COL_PROD_IMAGE, COL_PROD_SOLDQUANTITY, COL_PROD_REMAINQUANTITY, COL_PROD_SOLDDATE, COL_PROD_SELLPRICE};
    public static final String[] LOGIN_CRED_ADMIN_COLUMNS = {COL_LOGINCRED_NAME, COL_LOGINCRED_EMAIL, COL_LOGINCRED_MOBNUM, COL_LOGINCRED_PASSWD, COL_LOGINCRED_STOREID, COL_LOGINCRED_CREATION_DATE};
    public static final String[] STAFFMGNT_COLUMNS = {COL_STAFFMGNT_STOREID, COL_STAFFMGNT_STAFFID, COL_STAFFMGNT_NAME, COL_STAFFMGNT_MOBNUM, COL_STAFFMGNT_ADDRESS, COL_STAFFMGNT_PASSWD, COL_STAFFMGNT_JOIN_DATE, COL_STAFFMGNT_TOTALSALES};
    public static final String[] SUPPINFO_COLUMNS = {COL_SUPPINFO_NAME, COL_SUPPINFO_MOBILE, COL_SUPPINFO_ADDRESS, COL_SUPPINFO_STARTING_DATE};
    public static final String[] CUSTPURCHASE_COLUMNS = {COL_CUSTPURCHASE_MOBILE, COL_CUSTPURCHASE_AMOUNT};
    public static final String[] CATEGORY_COLUMNS = {COL_CATEGORY_ID, COL_CATEGORY_NAME};
    public static final String[] PROSUBCAT_COLUMNS = {COL_SUBCATEGORY_ID, COL_CATEGORY_NAME, COL_SUBCATEGORY_NAME,};
    public static final String[] CARTITEM_COLUMNS = {COL_CARTITEM_ID, COL_CARTITEM_NAME, COL_CARTITEM_QUANTITY, COL_CARTITEM_SELLINGPRICE, COL_CARTITEM_TOTALPRICE};
    public static final String[] CUSTCARTPURCHASE_COLUMNS = {COL_CUSTCARTPURCHASE_ID, COL_CUSTCARTPURCHASE_MOBILE, COL_CUSTCARTPURCHASE_PROD_NAME, COL_CUSTCARTPURCHASE_PROD_QUANTITY, COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE, COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE, COL_CUSTCARTPURCHASE_STAFF_ID};
    public static final String[] CUSTPURCHASEHISTORY_COLUMNS = {COL_CUSTCARTPURCHASE_ID, COL_CUSTCARTPURCHASE_MOBILE, COL_CUSTCARTPURCHASE_PROD_NAME, COL_CUSTCARTPURCHASE_PROD_QUANTITY, COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE, COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE, COL_CUSTCARTPURCHASE_STAFF_ID, COL_CUSTCARTPURCHASE_PURCHASE_DATE};
    public static final String[] TOTALSALES_BYDATE_COLUMNS = {COL_TOTALSALES_PERDAY_SALESAMOUNT, COL_TOTALSALES_PERDAY_DATE};
    public static final String[] OUTOFSTOCK_COLUMNS = {COL_OUTOFSTOCK_PRODID, COL_OUTOFSTOCK_PRODNAME, COL_OUTOFSTOCK_REMAIN_QUANTITY, COL_OUTOFSTOCK_SUPPMOBILE};

    //Product Table to store product details
    public static final String CREATE_TABLE_PRODUCT =
            "CREATE TABLE " + TABLE_PRODUCT + "(" +
                    COL_PROD_ID + " TEXT PRIMARY KEY," +
                    COL_PROD_IMAGE + " TEXT," +
                    COL_PROD_CATEGORY + " TEXT," +
                    COL_PROD_NAME + " TEXT," +
                    COL_PROD_DESC + " TEXT," +
                    COL_PROD_QUANTITY + " INTEGER," +
                    COL_PROD_MINLIMIT + " INTEGER," +
                    COL_PROD_COSTPRICE + " REAL," +
                    COL_PROD_SELLINGPRICE + " REAL," +
                    COL_PROD_SUPPLIER + " TEXT," +
                    COL_PROD_ADDEDDATE + " TEXT," +
                    COL_PROD_FAVORITE + " INTEGER)";

    //QuantityHistory Table to store product sold on some particular date
    public static final String CREATE_TABLE_QUANTITY_HISTORY =
            "CREATE TABLE " + TABLE_QUANTITY_HISTORY + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_QUANTITY + " INTEGER," +
                    COL_PROD_ADDEDDATE + " TEXT)";

    //SoldItems Table to store items sold
    public static final String CREATE_TABLE_SOLD_ITEMS =
            "CREATE TABLE " + TABLE_SOLD_ITEMS + "(" +
                    COL_PROD_ID + " TEXT," +
                    COL_PROD_NAME + " TEXT," +
                    COL_PROD_IMAGE + " TEXT," +
                    COL_PROD_SOLDQUANTITY + " INTEGER," +
                    COL_PROD_REMAINQUANTITY + " INTEGER," +
                    COL_PROD_SOLDDATE + " TEXT," +
                    COL_PROD_SELLPRICE + " REAL)";

    //LoginCredentials Table to store ADMIN information
    public static final String CREATE_TABLE_LOGIN_CRED_ADMIN =
            "CREATE TABLE " + TABLE_LOGIN_CRED_ADMIN + "(" +
                    COL_LOGINCRED_MOBNUM + " INTEGER PRIMARY KEY," +
                    COL_LOGINCRED_STOREID + " INTEGER UNIQUE," +
                    COL_LOGINCRED_STORENAME + " TEXT," +
                    COL_LOGINCRED_NAME + " TEXT," +
                    COL_LOGINCRED_EMAIL + " TEXT," +
                    COL_LOGINCRED_PASSWD + " TEXT," +
                    COL_LOGINCRED_CREATION_DATE + " TEXT)";

    //StaffMgnt Table to store STAFF information
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

    //SuppInfo Table to store SUPPLIER information
    public static final String CREATE_TABLE_SUPPINFO =
            "CREATE TABLE " + TABLE_SUPPINFO + "(" +
                    COL_SUPPINFO_MOBILE + " INTEGER PRIMARY KEY," +
                    COL_SUPPINFO_NAME + " TEXT," +
                    COL_SUPPINFO_ADDRESS + " TEXT," +
                    COL_SUPPINFO_STARTING_DATE + " TEXT)";

    //CustInfo Table to store CUSTOMER information
    public static final String CREATE_TABLE_CUSTINFO =
            "CREATE TABLE " + TABLE_CUSTINFO + "(" +
                    COL_CUSTPURCHASE_MOBILE + " INTEGER PRIMARY KEY," +
                    COL_CUSTPURCHASE_AMOUNT + " REAL)";

    //Category Table
    public static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + TABLE_CATEGORY + "(" +
                    COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_CATEGORY_NAME + " TEXT)";

    //Products Table to store products in category
    public static final String CREATE_TABLE_SUBCATEGORY =
            "CREATE TABLE " + TABLE_SUBCATEGORY + "(" +
                    COL_SUBCATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_CATEGORY_NAME + " TEXT, " +
                    COL_SUBCATEGORY_NAME + " TEXT)";

    //Cart Table to store cart items
    public static final String CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + "(" +
                    COL_CARTITEM_ID + " TEXT PRIMARY KEY," +
                    COL_CARTITEM_NAME + " TEXT, " +
                    COL_CARTITEM_QUANTITY + " INTEGER, " +
                    COL_CARTITEM_SELLINGPRICE + " REAL, " +
                    COL_CARTITEM_TOTALPRICE + " REAL)";

    //CustomerCartpurchases Table to store products purchased by ONE customer
    public static final String CREATE_TABLE_CUSTCARTPURCHASE =
            "CREATE TABLE " + TABLE_CUST_CART_PURCHASE + "(" +
                    COL_CUSTCARTPURCHASE_ID + " TEXT," +
                    COL_CUSTCARTPURCHASE_MOBILE + " INTEGER, " +
                    COL_CUSTCARTPURCHASE_PROD_NAME + " TEXT, " +
                    COL_CUSTCARTPURCHASE_PROD_QUANTITY + " INTEGER, " +
                    COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_STAFF_ID + " INTEGER)";

    //CustomerPurchaseHistory Table to store all SOLD PRODUCTS FOR ALL CUSTOMERS
    public static final String CREATE_TABLE_CUSTPURCHASEHISTORY =
            "CREATE TABLE " + TABLE_CUST_PURCHASE_HISTORY + "(" +
                    COL_CUSTCARTPURCHASE_ID + " TEXT," +
                    COL_CUSTCARTPURCHASE_MOBILE + " INTEGER, " +
                    COL_CUSTCARTPURCHASE_PROD_NAME + " TEXT, " +
                    COL_CUSTCARTPURCHASE_PROD_QUANTITY + " INTEGER, " +
                    COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_STAFF_ID + " INTEGER," +
                    COL_CUSTCARTPURCHASE_PURCHASE_DATE + " TEXT)";

    //TotalSales Table to store SalesAmount for each day
    public static final String CREATE_TABLE_TOTAL_SALES_BY_DATE =
            "CREATE TABLE " + TABLE_TOTAL_SALES_BY_DATE + "(" +
                    COL_TOTALSALES_PERDAY_SALESAMOUNT + " REAL," +
                    COL_TOTALSALES_PERDAY_DATE + " TEXT)";

    //OutofStock Table to store Outofstock items
    public static final String CREATE_TABLE_OUTOFSTOCK_ITEMS =
            "CREATE TABLE " + TABLE_OUTOFSTOCK_ITEMS + "(" +
                    COL_OUTOFSTOCK_PRODID + " TEXT," +
                    COL_OUTOFSTOCK_PRODNAME + " TEXT," +
                    COL_OUTOFSTOCK_REMAIN_QUANTITY + " INTEGER," +
                    COL_OUTOFSTOCK_SUPPMOBILE + " INTEGER)";

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
        sqLiteDatabase.execSQL(CREATE_TABLE_SUPPINFO);
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTINFO);
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SUBCATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_CART);
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTCARTPURCHASE);
        sqLiteDatabase.execSQL(CREATE_TABLE_CUSTPURCHASEHISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_TOTAL_SALES_BY_DATE);
        sqLiteDatabase.execSQL(CREATE_TABLE_OUTOFSTOCK_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITY_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLD_ITEMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN_CRED_ADMIN);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFFMGNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUPPINFO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTINFO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBCATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CUST_CART_PURCHASE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CUST_PURCHASE_HISTORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TOTAL_SALES_BY_DATE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTOFSTOCK_ITEMS);

        onCreate(sqLiteDatabase);
    }
}

