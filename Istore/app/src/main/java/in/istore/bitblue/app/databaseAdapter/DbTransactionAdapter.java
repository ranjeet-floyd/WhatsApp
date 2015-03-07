package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import in.istore.bitblue.app.utilities.DBHelper;

public class DbTransactionAdapter {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbTransactionAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbTransactionAdapter openWritableDatabase() {
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

}
