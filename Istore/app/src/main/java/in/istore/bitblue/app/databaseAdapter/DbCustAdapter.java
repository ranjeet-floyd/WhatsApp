package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.istore.bitblue.app.pojo.Customer;
import in.istore.bitblue.app.utilities.DBHelper;

public class DbCustAdapter {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDb;
    private Context context;

    public DbCustAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
    }

    public DbCustAdapter openWritableDatabase() {
        sqLiteDb = dbHelper.getWritableDatabase();
        return this;
    }

    public long insertCustPurchaseInfo(String Mobile, String PurchaseAmount, String StoreId) {
        ContentValues updaterow = new ContentValues();
        updaterow.put(DBHelper.COL_CUSTPURCHASE_AMOUNT, PurchaseAmount);

        ContentValues insertrow = new ContentValues();
        insertrow.put(DBHelper.COL_CUSTPURCHASE_MOBILE, Mobile);
        insertrow.put(DBHelper.COL_CUSTPURCHASE_AMOUNT, PurchaseAmount);
        insertrow.put(DBHelper.STOREID_COL, StoreId);
        insertrow.put(DBHelper.IS_UPDATED, "no");
        openWritableDatabase();
        long result = 0;
        if (isCustAlreadyExist(Mobile)) {
            result = sqLiteDb.update(DBHelper.TABLE_CUSTINFO, updaterow, DBHelper.COL_CUSTPURCHASE_MOBILE + "='" + Mobile + "'", null);
        } else {
            result = sqLiteDb.insert(DBHelper.TABLE_CUSTINFO, null, insertrow);
        }
        return result;
    }

    public boolean isCustAlreadyExist(String Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.CUSTOMER_PURCHASE_AMOUNT_TABLE, DBHelper.CUSTOMERPURCHASEAMOUNT_COLUMNS,
                DBHelper.CUSTOMERPURCHASEAMOUNT_CUSTOMER_MOBILE_COL + "='" + Mobile + "'", null, null, null, null);
        return c != null && c.moveToFirst();
    }

    public ArrayList<Customer> getAllCustomerPurchaseAmount() {
        ArrayList<Customer> customerArrayList = new ArrayList<Customer>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CUSTINFO, DBHelper.CUSTPURCHASE_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Customer customer = new Customer();
                customer.setMobile(c.getLong(c.getColumnIndexOrThrow(DBHelper.COL_CUSTPURCHASE_MOBILE)));
                customer.setPurchaseAmount(c.getString(c.getColumnIndexOrThrow(DBHelper.COL_CUSTPURCHASE_AMOUNT)));
                customerArrayList.add(customer);
            } while (c.moveToNext());
            return customerArrayList;
        } else {
            return null;
        }
    }
}
