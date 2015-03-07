package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.DBHelper;
import in.istore.bitblue.app.utilities.DateUtil;

public class DbSoldItemAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbSoldItemAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbSoldItemAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertSoldItemQuantityDetail(String Id, String Image, String Name, String SellPrice,
                                             String SoldQuantity, String CusMobile,
                                             String Optype, String DeliveryAddress, String StoreId) {
        Date date = new Date();
        String todayDate = DateUtil.convertToStringDateAndTime(date);
        ContentValues row = new ContentValues();
        row.put(DBHelper.SOLDPRODUCT_ID_COL, Id);
        row.put(DBHelper.SOLDPRODUCT_IMAGE_COL, Image);
        row.put(DBHelper.SOLDPRODUCT_NAME_COL, Name);
        row.put(DBHelper.SOLDPRODUCT_SELLINGPRICE_COL, SellPrice);
        row.put(DBHelper.SOLDPRODUCT_SOLDQUANTITY_COL, SoldQuantity);
        row.put(DBHelper.SOLDPRODUCT_SOLDDATE_COL, todayDate);
        row.put(DBHelper.SOLDPRODUCT_CUSTOMER_MOBILE_COL, CusMobile);
        row.put(DBHelper.SOLDPRODUCT_OPTYPE_COL, Optype);
        row.put(DBHelper.SOLDPRODUCT_DELIVERADDRESS_COL, DeliveryAddress);
        row.put(DBHelper.STOREID_COL, StoreId);
        openWritableDatabase();
        return sqLiteDb.insert(DBHelper.SOLD_PRODUCT_TABLE, null, row);
    }

    public Product getSoldProductDetails(String Id) {
        openWritableDatabase();
        Product product = new Product();
        String orderBy = DBHelper.COL_PROD_SOLDDATE + " DESC";
        String limit = "1";

        Cursor csolDetails = sqLiteDb.query(DBHelper.TABLE_SOLD_ITEMS, DBHelper.SOLD_ITEM_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, orderBy, limit); //get the latest record from duplicate records

        if ((csolDetails != null && csolDetails.moveToFirst())) {
            product.setId(csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_ID)));
            product.setSoldQuantity(csolDetails.getInt(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDQUANTITY)));
            product.setRemQuantity(csolDetails.getInt(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_REMAINQUANTITY)));
            product.setSoldDate(csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDDATE)));
            product.setSellPrice(csolDetails.getFloat(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLPRICE)));
        } else {
            return null;
        }

        Cursor cprodetalis = sqLiteDb.query(DBHelper.TABLE_PRODUCT, DBHelper.PRODUCT_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + Id + "'", null, null, null, null);

        if (cprodetalis != null && cprodetalis.moveToFirst()) {
            product.setImage(cprodetalis.getBlob(1));
            product.setName(cprodetalis.getString(cprodetalis.getColumnIndexOrThrow(DBHelper.COL_PROD_NAME)));
            product.setDesc(cprodetalis.getString(cprodetalis.getColumnIndexOrThrow(DBHelper.COL_PROD_DESC)));
        } else {
            return null;
        }
        return product;
    }

    public ArrayList<Product> getAllSoldDetailsfor(String id) {
        openWritableDatabase();
        ArrayList<Product> productList = new ArrayList<Product>();
        Cursor csolDetails = sqLiteDb.query(DBHelper.TABLE_SOLD_ITEMS, DBHelper.SOLD_ITEM_COLUMNS,
                DBHelper.COL_PROD_ID + "='" + id + "'", null, null, null, null);
        if (csolDetails != null && csolDetails.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(id);
                product.setSoldQuantity(csolDetails.getInt(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDQUANTITY)));
                product.setSoldDate(csolDetails.getString(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SOLDDATE)));
                product.setSellPrice(csolDetails.getFloat(csolDetails.getColumnIndexOrThrow(DBHelper.COL_PROD_SELLPRICE)));
                productList.add(product);
            } while (csolDetails.moveToNext());
            return productList;
        } else {
            return null;
        }
    }
}
