package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.pojo.TodaysSale;
import in.istore.bitblue.app.pojo.TotRevDetails;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbCustPurHistAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCustPurHistAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCustPurHistAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertIntoCustomerPurchase(String Id, String Mobile, String Name, String Quantity,
                                           String SellingPrice, String Total, String PersonId, String StoreId) {
        ContentValues row = new ContentValues();
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_ID_COL, Id);
        row.put(DBHelper.CUSTOMERPURCHASE_CUSTOMER_MOBILE_COL, Mobile);
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_NAME_COL, Name);
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_QUANTITY_COL, Quantity);
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_SELLINGPRICE_COL, SellingPrice);
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_TOTALPRICE_COL, Total);
        row.put(DBHelper.CUSTOMERPURCHASE_PRODUCT_STAFFID_COL, PersonId);
        row.put(DBHelper.STOREID_COL, StoreId);
        row.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.CUSTOMER_PURCHASE_TABLE, null, row);
    }

    public ArrayList<SoldProduct> getPurchaseHistoryFor(String prodName) {
        ArrayList<SoldProduct> soldProductArrayList = new ArrayList<SoldProduct>();
        openWritableDatabase();
        String COLUMNS = DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_MOBILE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_OPTYPE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS;

        String RAW_QUERY = "SELECT " + COLUMNS +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME + " ='" + prodName + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                SoldProduct soldProduct = new SoldProduct();
                soldProduct.setStaffId(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID)));
                soldProduct.setItemSoldQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                soldProduct.setItemTotalAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                soldProduct.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));
                soldProduct.setDate(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE)));/*
                soldProduct.setOptype(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_OPTYPE)));
                soldProduct.setDeliveryAddress(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS)));*/
                soldProductArrayList.add(soldProduct);
            } while (c.moveToNext());
            return soldProductArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<SoldProduct> getCustomerInfoForStaffId(long StaffId) {
        ArrayList<SoldProduct> soldProductArrayList = new ArrayList<SoldProduct>();
        openWritableDatabase();
        String COLUMNS =
                DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + "," +
                        DBHelper.COL_CUSTCARTPURCHASE_MOBILE;

        String RAW_QUERY = "SELECT " + COLUMNS +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + " ='" + StaffId + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                SoldProduct soldProduct = new SoldProduct();
                soldProduct.setItemTotalAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                soldProduct.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));

                soldProductArrayList.add(soldProduct);
            } while (c.moveToNext());
            return soldProductArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<SoldProduct> getSoldHistoryForStaffId(int staffId) {
        ArrayList<SoldProduct> soldProductArrayList = new ArrayList<SoldProduct>();
        openWritableDatabase();
        String COLUMNS = DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_MOBILE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + "," +
                DBHelper.COL_CUSTCARTPURCHASE_OPTYPE;

        String RAW_QUERY = "SELECT " + COLUMNS +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + " ='" + staffId + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                SoldProduct soldProduct = new SoldProduct();
                soldProduct.setItemName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME)));
                soldProduct.setItemSoldQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                soldProduct.setItemTotalAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                soldProduct.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));
                soldProduct.setDate(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE)));/*
                soldProduct.setOptype(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_OPTYPE)));
                soldProduct.setDeliveryAddress(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS)));*/

                soldProductArrayList.add(soldProduct);
            } while (c.moveToNext());
            return soldProductArrayList;
        } else {
            return null;
        }
    }

    public float getTotalSalesForProduct(String productName, String from, String to) {
        String SUM_QUERY = "SELECT SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")" +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME + "='" + productName + "'" +
                " AND " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + " BETWEEN '" + from + "' AND '" + to + "'";
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(SUM_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")"));
        } else {
            return 0;
        }

    }

    public float getTodaySalesForStaffId(long StaffId) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);

        String SUM_QUERY = "SELECT SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")" +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + "='" + StaffId + "'" +
                " AND " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + "='" + todayDate + "'";
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(SUM_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")"));
        } else {
            return 0;
        }

    }

    public float getTotalSalesForStaffId(int staffId, String from, String to) {
        String SUM_QUERY = "SELECT SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")" +
                " FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + "='" + staffId + "'" +
                " AND " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + " BETWEEN '" + from + "' AND '" + to + "'";
        openWritableDatabase();
        Cursor c = sqLiteDb.rawQuery(SUM_QUERY, null);
        if (c != null && c.moveToFirst()) {
            return c.getFloat(c.getColumnIndexOrThrow("SUM(" + DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE + ")"));
        } else {
            return 0;
        }

    }

    public ArrayList<TotRevDetails> getAllSoldProductsHistoryBetween(String from, String to) {
        ArrayList<TotRevDetails> totRevDetailsArrayList = new ArrayList<TotRevDetails>();
        openWritableDatabase();
        String RAW_QUERY =
                "SELECT * FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                        " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + " BETWEEN '" + from + "' AND '" + to + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                TotRevDetails totRevDetails = new TotRevDetails();
                totRevDetails.setStaffid(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID)));
                totRevDetails.setProdName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME)));
                totRevDetails.setQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                totRevDetails.setPurchaseAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                totRevDetails.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));
                totRevDetails.setDate(DateUtil.convertFromYYYY_MM_DDtoDD_MM_YYYY(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE))));/*
                totRevDetails.setOptype(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_OPTYPE)));
                totRevDetails.setDeliveryAddress(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS)));*/
                totRevDetailsArrayList.add(totRevDetails);
            } while (c.moveToNext());
            return totRevDetailsArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<TodaysSale> getTodaysSale() {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        ArrayList<TodaysSale> todaysSaleArrayList = new ArrayList<TodaysSale>();
        openWritableDatabase();
        String RAW_QUERY =
                "SELECT * FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                        " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + "='" + todayDate + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                TodaysSale todaysSale = new TodaysSale();
                todaysSale.setStaffId(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID)));
                todaysSale.setProdName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME)));
                todaysSale.setQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                todaysSale.setPurchaseAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                todaysSale.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));/*
                todaysSale.setOptype(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_OPTYPE)));
                todaysSale.setDeliveryAddress(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS)));*/

                todaysSaleArrayList.add(todaysSale);
            } while (c.moveToNext());
            return todaysSaleArrayList;
        } else {
            return null;
        }
    }

    public ArrayList<TodaysSale> getTodaysSaleForStaff(long StaffId) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateOnly(date);
        ArrayList<TodaysSale> todaysSaleArrayList = new ArrayList<TodaysSale>();
        openWritableDatabase();
        String RAW_QUERY =
                "SELECT * FROM " + DBHelper.TABLE_CUST_PURCHASE_HISTORY +
                        " WHERE " + DBHelper.COL_CUSTCARTPURCHASE_PURCHASE_DATE + "='" + todayDate + "'" +
                        " AND " + DBHelper.COL_CUSTCARTPURCHASE_STAFF_ID + "='" + StaffId + "'";
        Cursor c = sqLiteDb.rawQuery(RAW_QUERY, null);
        if (c != null && c.moveToFirst()) {
            do {
                TodaysSale todaysSale = new TodaysSale();
                todaysSale.setProdName(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_NAME)));
                todaysSale.setQuantity(c.getInt(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_QUANTITY)));
                todaysSale.setPurchaseAmnt(c.getFloat(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_PROD_TOTAL_PRICE)));
                todaysSale.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_MOBILE)));/*
                todaysSale.setOptype(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_OPTYPE)));
                todaysSale.setDeliveryAddress(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTCARTPURCHASE_DELIVERYADDRESS)));*/
                todaysSaleArrayList.add(todaysSale);
            } while (c.moveToNext());
            return todaysSaleArrayList;
        } else {
            return null;
        }
    }

}
