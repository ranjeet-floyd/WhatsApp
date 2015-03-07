package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "istore.db";
    public static final int DATABASE_VERSION = 49
            ;   //TO UPDATE DATABASE CHANGE THIS VERSION NUMBER

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

    //--------------------------------------------------------------------------New Tables----------------------------------------------------------------------------------------------------------------------------------------------------
    public static final String ADMIN_LOGIN_TABLE = "AdminLogin";
    public static final String CART_TABLE = "Cart";
    public static final String CATEGORY_TABLE = "Category";
    public static final String CUSTOMER_PURCHASE_TABLE = "CustomerPurchase";
    public static final String CUSTOMER_PURCHASE_AMOUNT_TABLE = "CustomerPurchaseAmount";
    public static final String OUTOFSTOCKITEMS_TABLE = "OutOfStockItems";
    public static final String PRODUCT_TABLE = "Product";
    public static final String SOLD_PRODUCT_TABLE = "SoldProduct";
    public static final String STAFF_MANAGEMENT_TABLE = "StaffManagement";
    public static final String SUBCATEGORY_TABLE = "Subcategory";
    public static final String SUPPLIER_TABLE = "Supplier";
    public static final String TOTALSTOCKSALES_TABLE = "TotalStockSales";
    public static final String TRANSACTION_TABLE = "Transanction";
    public static final String USER_CONTACTS = "UserContacts";
    public static final String IS_UPDATED = "IsUpdated";
    public static final String STOREID_COL = "StoreId";

    //Admin Login Table
    public static final String ADMINID_COL = "ADMINId";
    public static final String ADMIN_NAME_COL = "Name";
    public static final String ADMIN_MOBILE_COL = "Mobile";
    public static final String ADMIN_PASSWD_COL = "Passwd";
    public static final String ADMIN_STORENAME_COL = "StoreName";
    public static final String ADMIN_CREATED_ON_COL = "CreatedOn";
    public static final String ADMIN_KEY_COL = "ADMINKEY";
    public static final String[] ADMIN_LOGIN_COLUMNS = {ADMINID_COL, ADMIN_NAME_COL, ADMIN_MOBILE_COL, ADMIN_PASSWD_COL,
            STOREID_COL, ADMIN_STORENAME_COL, ADMIN_CREATED_ON_COL, ADMIN_KEY_COL, IS_UPDATED};
    public static final String CREATE_ADMIN_LOGIN_TABLE =
            "CREATE TABLE " + ADMIN_LOGIN_TABLE + "(" +
                    ADMINID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ADMIN_NAME_COL + " TEXT," +
                    ADMIN_MOBILE_COL + " TEXT," +
                    ADMIN_PASSWD_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    ADMIN_STORENAME_COL + " TEXT," +
                    ADMIN_CREATED_ON_COL + " TEXT," +
                    ADMIN_KEY_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Cart Table
    public static final String CARTID_COL = "CARTId";
    public static final String CART_PRODUCT_ID_COL = "Cid";
    public static final String CART_PRODUCT_NAME_COL = "Name";
    public static final String CART_PRODUCT_QUANTITY_COL = "Quantity";
    public static final String CART_PRODUCT_SELLINGPRICE_COL = "SellingPrice";
    public static final String CART_PRODUCT_TOTALPRICE_COL = "TotalPrice";
    public static final String[] CART_COLUMNS = {CARTID_COL, CART_PRODUCT_ID_COL, CART_PRODUCT_NAME_COL, CART_PRODUCT_QUANTITY_COL,
            CART_PRODUCT_SELLINGPRICE_COL, CART_PRODUCT_TOTALPRICE_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_CART_TABLE =
            "CREATE TABLE " + CART_TABLE + "(" +
                    CARTID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CART_PRODUCT_ID_COL + " TEXT," +
                    CART_PRODUCT_NAME_COL + " TEXT," +
                    CART_PRODUCT_QUANTITY_COL + " TEXT," +
                    CART_PRODUCT_SELLINGPRICE_COL + " TEXT," +
                    CART_PRODUCT_TOTALPRICE_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Category Table
    public static final String CATEGORYID_COL = "CATEGORYId";
    public static final String CATEGORY_NAME_COL = "Categoryname";
    public static final String[] PRODUCTCATEGORY_COLUMNS = {CATEGORYID_COL, CATEGORY_NAME_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CATEGORY_TABLE + "(" +
                    CATEGORYID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CATEGORY_NAME_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Customer Purchase Table
    public static final String CUSTOMERPURCHASEID_COL = "CUSTPURId";
    public static final String CUSTOMERPURCHASE_CUSTOMER_MOBILE_COL = "Cusmobile";
    public static final String CUSTOMERPURCHASE_PRODUCT_ID_COL = "ProductId";
    public static final String CUSTOMERPURCHASE_PRODUCT_NAME_COL = "Cusprodname";
    public static final String CUSTOMERPURCHASE_PRODUCT_QUANTITY_COL = "Custquantity";
    public static final String CUSTOMERPURCHASE_PRODUCT_SELLINGPRICE_COL = "Custsellprice";
    public static final String CUSTOMERPURCHASE_PRODUCT_TOTALPRICE_COL = "Custtotalprice";
    public static final String CUSTOMERPURCHASE_PRODUCT_STAFFID_COL = "Custstaffid";
    public static final String[] CUSTOMERPURCHASE_COLUMNS = {CUSTOMERPURCHASEID_COL, CUSTOMERPURCHASE_CUSTOMER_MOBILE_COL,
            CUSTOMERPURCHASE_PRODUCT_ID_COL, CUSTOMERPURCHASE_PRODUCT_NAME_COL, CUSTOMERPURCHASE_PRODUCT_QUANTITY_COL,
            CUSTOMERPURCHASE_PRODUCT_SELLINGPRICE_COL, CUSTOMERPURCHASE_PRODUCT_TOTALPRICE_COL,
            CUSTOMERPURCHASE_PRODUCT_STAFFID_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_CUSTOMER_PURCHASE_TABLE =
            "CREATE TABLE " + CUSTOMER_PURCHASE_TABLE + "(" +
                    CUSTOMERPURCHASEID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CUSTOMERPURCHASE_CUSTOMER_MOBILE_COL + " TEXT, " +
                    CUSTOMERPURCHASE_PRODUCT_ID_COL + " TEXT," +
                    CUSTOMERPURCHASE_PRODUCT_NAME_COL + " TEXT," +
                    CUSTOMERPURCHASE_PRODUCT_QUANTITY_COL + " TEXT," +
                    CUSTOMERPURCHASE_PRODUCT_SELLINGPRICE_COL + " TEXT," +
                    CUSTOMERPURCHASE_PRODUCT_TOTALPRICE_COL + " TEXT," +
                    CUSTOMERPURCHASE_PRODUCT_STAFFID_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Customer Purchase Amount Table
    public static final String CUSTOMERPURCHASEAMOUNTID_COL = "CUSId";
    public static final String CUSTOMERPURCHASEAMOUNT_CUSTOMER_MOBILE_COL = "Custmobile";
    public static final String CUSTOMERPURCHASEAMOUNT_PURCHASEAMOUNT_COL = "CustpurchaseAmt";
    public static final String[] CUSTOMERPURCHASEAMOUNT_COLUMNS = {CUSTOMERPURCHASEAMOUNTID_COL,
            CUSTOMERPURCHASEAMOUNT_CUSTOMER_MOBILE_COL, CUSTOMERPURCHASEAMOUNT_PURCHASEAMOUNT_COL,
            STOREID_COL, IS_UPDATED};
    public static final String CREATE_CUSTOMER_PURCHASE_AMOUNT_TABLE =
            "CREATE TABLE " + CUSTOMER_PURCHASE_AMOUNT_TABLE + "(" +
                    CUSTOMERPURCHASEAMOUNTID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CUSTOMERPURCHASEAMOUNT_CUSTOMER_MOBILE_COL + " TEXT," +
                    CUSTOMERPURCHASEAMOUNT_PURCHASEAMOUNT_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Out Of Stock Items Table
    public static final String OUTOFSTOCKID_COL = "OOSPRODId";
    public static final String OUTOFSTOCK_PRODUCT_NAME_COL = "Oosprodname";
    public static final String OUTOFSTOCK_PRODUCT_REMAININGQUANTITY_COL = "Remainquantity";
    public static final String OUTOFSTOCK_PRODUCT_MINQUANTITY_COL = "MinQuantity";
    public static final String OUTOFSTOCK_SUPPLIER_MOBILE_COL = "Suppmobile";
    public static final String[] OUTOFSTOCKITEMS_COLUMNS = {OUTOFSTOCKID_COL,
            OUTOFSTOCK_PRODUCT_NAME_COL, OUTOFSTOCK_PRODUCT_REMAININGQUANTITY_COL, OUTOFSTOCK_PRODUCT_MINQUANTITY_COL,
            OUTOFSTOCK_SUPPLIER_MOBILE_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_OUTOFSTOCK_ITEMS_TABLE =
            "CREATE TABLE " + OUTOFSTOCKITEMS_TABLE + "(" +
                    OUTOFSTOCKID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OUTOFSTOCK_PRODUCT_NAME_COL + " TEXT," +
                    OUTOFSTOCK_PRODUCT_REMAININGQUANTITY_COL + " TEXT," +
                    OUTOFSTOCK_PRODUCT_MINQUANTITY_COL + " TEXT," +
                    OUTOFSTOCK_SUPPLIER_MOBILE_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Product Table
    public static final String PRODUCTID_COL = "PRODUCTId";
    public static final String PRODUCT_ID_COL = "Id";
    public static final String PRODUCT_CATEGORY_COL = "Category";
    public static final String PRODUCT_NAME_COL = "Name";
    public static final String PRODUCT_IMAGE_COL = "Image";
    public static final String PRODUCT_DESC_COL = "ProductDesc";
    public static final String PRODUCT_QUANTITY_COL = "Quatity";
    public static final String PRODUCT_MINLIMIT_COL = "MinLimit";
    public static final String PRODUCT_COSTPRICE_COL = "CostPrice";
    public static final String PRODUCT_SELLINGPRICE_COL = "SellingPrice";
    public static final String PRODUCT_SUPPLIER_COL = "Supplier";
    public static final String PRODUCT_ADDEDON_COL = "AddedOn";
    public static final String[] PRODUCT_COLUMNS = {PRODUCTID_COL, PRODUCT_ID_COL, PRODUCT_CATEGORY_COL, PRODUCT_NAME_COL,
            PRODUCT_IMAGE_COL, PRODUCT_DESC_COL, PRODUCT_QUANTITY_COL, PRODUCT_MINLIMIT_COL, PRODUCT_COSTPRICE_COL,
            PRODUCT_SELLINGPRICE_COL, PRODUCT_SUPPLIER_COL, PRODUCT_ADDEDON_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + PRODUCT_TABLE + "(" +
                    PRODUCTID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PRODUCT_ID_COL + " TEXT," +
                    PRODUCT_CATEGORY_COL + " TEXT," +
                    PRODUCT_NAME_COL + " TEXT," +
                    PRODUCT_IMAGE_COL + " TEXT," +
                    PRODUCT_DESC_COL + " TEXT," +
                    PRODUCT_QUANTITY_COL + " TEXT," +
                    PRODUCT_MINLIMIT_COL + " TEXT," +
                    PRODUCT_COSTPRICE_COL + " TEXT," +
                    PRODUCT_SELLINGPRICE_COL + " TEXT," +
                    PRODUCT_SUPPLIER_COL + " TEXT," +
                    PRODUCT_ADDEDON_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Sold Product Table
    public static final String SOLDPRODUCTID_COL = "SOLDId";
    public static final String SOLDPRODUCT_ID_COL = "ItemID";
    public static final String SOLDPRODUCT_NAME_COL = "Name";
    public static final String SOLDPRODUCT_SELLINGPRICE_COL = "SellPrice";
    public static final String SOLDPRODUCT_SOLDQUANTITY_COL = "Soldquantity";
    public static final String SOLDPRODUCT_SOLDDATE_COL = "SoldDate";
    public static final String SOLDPRODUCT_IMAGE_COL = "Image";
    public static final String SOLDPRODUCT_CUSTOMER_MOBILE_COL = "CusMobile";
    public static final String SOLDPRODUCT_OPTYPE_COL = "OpType";
    public static final String SOLDPRODUCT_DELIVERADDRESS_COL = "DeliveryAdd";
    public static final String[] SOLD_PRODUCT_COLUMNS = {SOLDPRODUCTID_COL, SOLDPRODUCT_ID_COL, SOLDPRODUCT_NAME_COL,
            SOLDPRODUCT_IMAGE_COL, SOLDPRODUCT_SELLINGPRICE_COL, SOLDPRODUCT_SOLDQUANTITY_COL, SOLDPRODUCT_SOLDDATE_COL,
            SOLDPRODUCT_CUSTOMER_MOBILE_COL, SOLDPRODUCT_OPTYPE_COL, SOLDPRODUCT_DELIVERADDRESS_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_SOLDPRODUCT_TABLE =
            "CREATE TABLE " + SOLD_PRODUCT_TABLE + "(" +
                    SOLDPRODUCTID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SOLDPRODUCT_ID_COL + " TEXT," +
                    SOLDPRODUCT_NAME_COL + " TEXT," +
                    SOLDPRODUCT_IMAGE_COL + " TEXT," +
                    SOLDPRODUCT_SELLINGPRICE_COL + " TEXT," +
                    SOLDPRODUCT_SOLDQUANTITY_COL + " TEXT," +
                    SOLDPRODUCT_SOLDDATE_COL + " TEXT," +
                    SOLDPRODUCT_CUSTOMER_MOBILE_COL + " TEXT," +
                    SOLDPRODUCT_OPTYPE_COL + " TEXT," +
                    SOLDPRODUCT_DELIVERADDRESS_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Staff Management Table
    public static final String STAFFMANAGEMENTID_COL = "SId";
    public static final String STAFFMANAGEMENT_STAFFID_COL = "Staffid";
    public static final String STAFFMANAGEMENT_STAFFEMAIL_COL = "Staffemail";
    public static final String STAFFMANAGEMENT_STAFFNAME_COL = "Staffname";
    public static final String STAFFMANAGEMENT_STAFFMOBILE_COL = "Staffmobile";
    public static final String STAFFMANAGEMENT_STAFFADDRESS_COL = "Staffaddress";
    public static final String STAFFMANAGEMENT_STAFFPASSWORD_COL = "Staffpasswd";
    public static final String STAFFMANAGEMENT_STAFFJOINON_COL = "StaffjoinOn";
    public static final String STAFFMANAGEMENT_STAFFTOTALSALES_COL = "Stafftotsale";
    public static final String STAFFMANAGEMENT_STAFFKEY_COL = "StaffKey";
    public static final String[] STAFFMANAGEMENT_COLUMNS = {STAFFMANAGEMENTID_COL, STAFFMANAGEMENT_STAFFID_COL,
            STAFFMANAGEMENT_STAFFEMAIL_COL, STAFFMANAGEMENT_STAFFNAME_COL, STAFFMANAGEMENT_STAFFMOBILE_COL,
            STAFFMANAGEMENT_STAFFADDRESS_COL, STAFFMANAGEMENT_STAFFPASSWORD_COL, STAFFMANAGEMENT_STAFFJOINON_COL,
            STAFFMANAGEMENT_STAFFTOTALSALES_COL, STAFFMANAGEMENT_STAFFKEY_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_STAFFMANAGEMENT_TABLE =
            "CREATE TABLE " + STAFF_MANAGEMENT_TABLE + "(" +
                    STAFFMANAGEMENTID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    STAFFMANAGEMENT_STAFFID_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFEMAIL_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFNAME_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFMOBILE_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFADDRESS_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFPASSWORD_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFJOINON_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFTOTALSALES_COL + " TEXT," +
                    STAFFMANAGEMENT_STAFFKEY_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Subcategory Table
    public static final String SUBCATEGORYID_COL = "PROSUBCATId";
    public static final String SUBCATEGORY_CATEGORYNAME_COL = "Categoryname";
    public static final String SUBCATEGORY_PRODUCTCATEGORY_NAME_COL = "ProsubcatName";
    public static final String[] PRODUCTSUBCATEGORY_COLUMNS = {SUBCATEGORYID_COL, SUBCATEGORY_CATEGORYNAME_COL,
            SUBCATEGORY_PRODUCTCATEGORY_NAME_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_SUBCATEGORY_TABLE =
            "CREATE TABLE " + SUBCATEGORY_TABLE + "(" +
                    SUBCATEGORYID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SUBCATEGORY_CATEGORYNAME_COL + " TEXT," +
                    SUBCATEGORY_PRODUCTCATEGORY_NAME_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Supplier Table
    public static final String SUPPLIERID_COL = "SUPPId";
    public static final String SUPPLIER_NAME_COL = "Suppname";
    public static final String SUPPLIER_MOBILE_COL = "Suppmobile";
    public static final String SUPPLIER_ADDRESS_COL = "Suppaddress";
    public static final String SUPPLIER_STARTDATE_COL = "Suppstartdate";
    public static final String[] SUPPLIER_COLUMNS = {SUPPLIERID_COL, SUPPLIER_NAME_COL, SUPPLIER_MOBILE_COL, SUPPLIER_ADDRESS_COL,
            SUPPLIER_STARTDATE_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_SUPPLIER_TABLE =
            "CREATE TABLE " + SUPPLIER_TABLE + "(" +
                    SUPPLIERID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SUPPLIER_NAME_COL + " TEXT," +
                    SUPPLIER_MOBILE_COL + " TEXT," +
                    SUPPLIER_ADDRESS_COL + " TEXT," +
                    SUPPLIER_STARTDATE_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //TotalStockSales Table
    public static final String TOTALSTOCKSALESID_COL = "STOCKId";
    public static final String TOTALSTOCKSALES_PERDAYSALES_COL = "PerdaysalesAmount";
    public static final String TOTALSTOCKSALES_PERDAYDATE_COL = "Perdaydate";
    public static final String[] TOTALSTOCKSALES_COLUMNS = {TOTALSTOCKSALESID_COL, TOTALSTOCKSALES_PERDAYSALES_COL,
            TOTALSTOCKSALES_PERDAYDATE_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_TOTALSTOCKSALES_TABLE =
            "CREATE TABLE " + TOTALSTOCKSALES_TABLE + "(" +
                    TOTALSTOCKSALESID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TOTALSTOCKSALES_PERDAYSALES_COL + " TEXT," +
                    TOTALSTOCKSALES_PERDAYDATE_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";

    //Transaction Table
    public static final String TRANSACTIONID_COL = "TRANSId";
    public static final String TRANSACTION_STAFFID_COL = "Staffid";
    public static final String TRANSACTION_ADMINID_COL = "AdminId";
    public static final String TRANSACTION_PRODUCT_NAME_COL = "ProductName";
    public static final String TRANSACTION_PRODUCT_QUANTITY_COL = "Quantity";
    public static final String TRANSACTION_TOTALAMOUNT_COL = "TotalAmount";
    public static final String TRANSACTION_CUSTOMER_MOBILE_COL = "CusMobile";
    public static final String TRANSACTION_SOLDDATE_COL = "SoldDate";
    public static final String TRANSACTION_INVOICENUMBER_COL = "InvoiceNum";
    public static final String TRANSACTION_OPTYPE_COL = "OpType";
    public static final String TRANSACTION_DELIVERYADDRESS_COL = "DeliveryAdd";
    public static final String[] TRANSACTION_COLUMNS = {TRANSACTIONID_COL, TRANSACTION_STAFFID_COL, TRANSACTION_ADMINID_COL,
            TRANSACTION_PRODUCT_NAME_COL, TRANSACTION_PRODUCT_QUANTITY_COL, TRANSACTION_TOTALAMOUNT_COL,
            TRANSACTION_CUSTOMER_MOBILE_COL, TRANSACTION_SOLDDATE_COL, TRANSACTION_INVOICENUMBER_COL, TRANSACTION_OPTYPE_COL,
            TRANSACTION_DELIVERYADDRESS_COL, STOREID_COL, IS_UPDATED};
    public static final String CREATE_TRANSACTION_TABLE =
            "CREATE TABLE " + TRANSACTION_TABLE + "(" +
                    TRANSACTIONID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRANSACTION_STAFFID_COL + " TEXT," +
                    TRANSACTION_ADMINID_COL + " TEXT," +
                    TRANSACTION_PRODUCT_NAME_COL + " TEXT," +
                    TRANSACTION_PRODUCT_QUANTITY_COL + " TEXT," +
                    TRANSACTION_TOTALAMOUNT_COL + " TEXT," +
                    TRANSACTION_CUSTOMER_MOBILE_COL + " TEXT," +
                    TRANSACTION_SOLDDATE_COL + " TEXT," +
                    TRANSACTION_INVOICENUMBER_COL + " TEXT," +
                    TRANSACTION_OPTYPE_COL + " TEXT," +
                    TRANSACTION_DELIVERYADDRESS_COL + " TEXT," +
                    STOREID_COL + " TEXT," +
                    IS_UPDATED + " TEXT)";
    //-----------------------------------------------------------------------------Old Tables------------------------------------------------------------------------------------------------------------------------------------------------------
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
    public static final String COL_STAFFMGNT_ADMINID = "adminid";
    public static final String COL_STAFFMGNT_STAFFID = "staffid";
    public static final String COL_STAFFMGNT_STAFFEMAIL = "staffemail";
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
    public static final String COL_CUSTCARTPURCHASE_OPTYPE = "optype";  //sale or delivery
    public static final String COL_CUSTCARTPURCHASE_DELIVERYADDRESS = "deliveryAddress";

    //TotalStockSales Table Column
    public static final String COL_TOTALSALES_PERDAY_SALESAMOUNT = "perdaysales";
    public static final String COL_TOTALSALES_PERDAY_DATE = "perdaydate";

    //OutOfStockItems Table Column
    public static final String COL_OUTOFSTOCK_PRODID = "oosprodid";
    public static final String COL_OUTOFSTOCK_PRODNAME = "oosprodname";
    public static final String COL_OUTOFSTOCK_REMAIN_QUANTITY = "remainquantity";
    public static final String COL_OUTOFSTOCK_SUPPMOBILE = "suppmobile";

    public static final String[] PRODUCT_COLOUMNS = {COL_PROD_ID, COL_PROD_IMAGE, COL_PROD_CATEGORY, COL_PROD_NAME, COL_PROD_DESC, COL_PROD_QUANTITY, COL_PROD_MINLIMIT, COL_PROD_COSTPRICE, COL_PROD_SELLINGPRICE, COL_PROD_SUPPLIER, COL_PROD_ADDEDDATE, COL_PROD_FAVORITE};
    public static final String[] QUANTITY_DATE_COLUMNS = {COL_PROD_ID, COL_PROD_QUANTITY, COL_PROD_ADDEDDATE};
    public static final String[] SOLD_ITEM_COLUMNS = {COL_PROD_ID, COL_PROD_NAME, COL_PROD_IMAGE, COL_PROD_SOLDQUANTITY, COL_PROD_REMAINQUANTITY, COL_PROD_SOLDDATE, COL_PROD_SELLPRICE};
    public static final String[] LOGIN_CRED_ADMIN_COLUMNS = {COL_LOGINCRED_NAME, COL_LOGINCRED_EMAIL, COL_LOGINCRED_MOBNUM, COL_LOGINCRED_PASSWD, COL_LOGINCRED_STOREID, COL_LOGINCRED_CREATION_DATE};
    public static final String[] STAFFMGNT_COLUMNS = {COL_STAFFMGNT_STOREID, COL_STAFFMGNT_ADMINID, COL_STAFFMGNT_STAFFID, COL_STAFFMGNT_STAFFEMAIL, COL_STAFFMGNT_NAME, COL_STAFFMGNT_MOBNUM, COL_STAFFMGNT_ADDRESS, COL_STAFFMGNT_PASSWD, COL_STAFFMGNT_JOIN_DATE, COL_STAFFMGNT_TOTALSALES};
    public static final String[] SUPPINFO_COLUMNS = {COL_SUPPINFO_NAME, COL_SUPPINFO_MOBILE, COL_SUPPINFO_ADDRESS, COL_SUPPINFO_STARTING_DATE};
    public static final String[] CUSTPURCHASE_COLUMNS = {COL_CUSTPURCHASE_MOBILE, COL_CUSTPURCHASE_AMOUNT};
    public static final String[] CATEGORY_COLUMNS = {COL_CATEGORY_ID, COL_CATEGORY_NAME};
    public static final String[] PROSUBCAT_COLUMNS = {COL_SUBCATEGORY_ID, COL_CATEGORY_NAME, COL_SUBCATEGORY_NAME,};
    public static final String[] CARTITEM_COLUMNS = {COL_CARTITEM_ID, COL_CARTITEM_NAME, COL_CARTITEM_QUANTITY, COL_CARTITEM_SELLINGPRICE, COL_CARTITEM_TOTALPRICE};
    public static final String[] CUSTPURCHASEHISTORY_COLUMNS = {COL_CUSTCARTPURCHASE_ID, COL_CUSTCARTPURCHASE_MOBILE, COL_CUSTCARTPURCHASE_PROD_NAME, COL_CUSTCARTPURCHASE_PROD_QUANTITY, COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE, COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE, COL_CUSTCARTPURCHASE_STAFF_ID, COL_CUSTCARTPURCHASE_PURCHASE_DATE, COL_CUSTCARTPURCHASE_OPTYPE, COL_CUSTCARTPURCHASE_DELIVERYADDRESS};
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
                    COL_STAFFMGNT_ADMINID + " INTEGER," +
                    COL_STAFFMGNT_STAFFID + " INTEGER UNIQUE," +
                    COL_STAFFMGNT_STAFFEMAIL + " TEXT," +
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
                    COL_CATEGORY_NAME + " TEXT," +
                    COL_SUBCATEGORY_NAME + " TEXT)";

    //Cart Table to store cart items
    public static final String CREATE_TABLE_CART =
            "CREATE TABLE " + TABLE_CART + "(" +
                    COL_CARTITEM_ID + " TEXT PRIMARY KEY," +
                    COL_CARTITEM_NAME + " TEXT," +
                    COL_CARTITEM_QUANTITY + " INTEGER," +
                    COL_CARTITEM_SELLINGPRICE + " REAL," +
                    COL_CARTITEM_TOTALPRICE + " REAL)";

    //CustomerCartpurchases Table to store products purchased by ONE customer
    public static final String CREATE_TABLE_CUSTCARTPURCHASE =
            "CREATE TABLE " + TABLE_CUST_CART_PURCHASE + "(" +
                    COL_CUSTCARTPURCHASE_ID + " TEXT," +
                    COL_CUSTCARTPURCHASE_MOBILE + " INTEGER, " +
                    COL_CUSTCARTPURCHASE_PROD_NAME + " TEXT, " +
                    COL_CUSTCARTPURCHASE_PROD_QUANTITY + " INTEGER," +
                    COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_STAFF_ID + " INTEGER)";

    //CustomerPurchaseHistory Table to store all SOLD PRODUCTS FOR ALL CUSTOMERS
    public static final String CREATE_TABLE_CUSTPURCHASEHISTORY =
            "CREATE TABLE " + TABLE_CUST_PURCHASE_HISTORY + "(" +
                    COL_CUSTCARTPURCHASE_ID + " TEXT," +
                    COL_CUSTCARTPURCHASE_MOBILE + " INTEGER," +
                    COL_CUSTCARTPURCHASE_PROD_NAME + " TEXT," +
                    COL_CUSTCARTPURCHASE_PROD_QUANTITY + " INTEGER," +
                    COL_CUSTCARTPURCHASE_PROD_SELLING_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + " REAL," +
                    COL_CUSTCARTPURCHASE_STAFF_ID + " INTEGER," +
                    COL_CUSTCARTPURCHASE_PURCHASE_DATE + " TEXT," +
                    COL_CUSTCARTPURCHASE_OPTYPE + " TEXT," +
                    COL_CUSTCARTPURCHASE_DELIVERYADDRESS + " TEXT)";

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

/*        //OLD TABLES
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
        sqLiteDatabase.execSQL(CREATE_TABLE_OUTOFSTOCK_ITEMS);*/

        //New Tables
        sqLiteDatabase.execSQL(CREATE_ADMIN_LOGIN_TABLE);
        sqLiteDatabase.execSQL(CREATE_CART_TABLE);
        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_CUSTOMER_PURCHASE_TABLE);
        sqLiteDatabase.execSQL(CREATE_CUSTOMER_PURCHASE_AMOUNT_TABLE);
        sqLiteDatabase.execSQL(CREATE_OUTOFSTOCK_ITEMS_TABLE);
        sqLiteDatabase.execSQL(CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(CREATE_SOLDPRODUCT_TABLE);
        sqLiteDatabase.execSQL(CREATE_STAFFMANAGEMENT_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUBCATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUPPLIER_TABLE);
        sqLiteDatabase.execSQL(CREATE_TOTALSTOCKSALES_TABLE);
        sqLiteDatabase.execSQL(CREATE_TRANSACTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldversion, int newversion) {
        //OLD TABLES
       /* sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTOFSTOCK_ITEMS);*/

        //New Tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ADMIN_LOGIN_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CART_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_PURCHASE_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CUSTOMER_PURCHASE_AMOUNT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OUTOFSTOCKITEMS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SOLD_PRODUCT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + STAFF_MANAGEMENT_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBCATEGORY_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUPPLIER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TOTALSTOCKSALES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE);
        onCreate(sqLiteDatabase);
    }
}

