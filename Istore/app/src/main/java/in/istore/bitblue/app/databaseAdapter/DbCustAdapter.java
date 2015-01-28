package in.istore.bitblue.app.databaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public void closeDatabase() {
        sqLiteDb.close();
    }

    public long insertCustInfo(long Mobile, String PurchaseAmount) {

        ContentValues updaterow = new ContentValues(); //if mobile number present update purchase amt
        updaterow.put(DBHelper.COL_CUSTPURCHASE_AMOUNT, PurchaseAmount);

        ContentValues insertrow = new ContentValues(); //if not present make an entry for mobile and amount
        insertrow.put(DBHelper.COL_CUSTPURCHASE_MOBILE, Mobile);
        insertrow.put(DBHelper.COL_CUSTPURCHASE_AMOUNT, PurchaseAmount);
        openWritableDatabase();
        long result = 0;
        if (isCustAlreadyExist(Mobile)) {
            result = sqLiteDb.update(DBHelper.TABLE_CUSTINFO, updaterow, DBHelper.COL_CUSTPURCHASE_MOBILE + "='" + Mobile + "'", null);
        } else {
            result = sqLiteDb.insert(DBHelper.TABLE_CUSTINFO, null, insertrow);
        }
        closeDatabase();
        Log.e("Customer Information Db Result: ", String.valueOf(result));
        return result;
    }

    public boolean isCustAlreadyExist(long Mobile) {
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CUSTINFO, DBHelper.CUSTPURCHASE_COLUMNS,
                DBHelper.COL_CUSTPURCHASE_MOBILE + "='" + Mobile + "'", null, null, null, null);
        if (c != null && c.moveToFirst()) {
            closeDatabase();
            return true;
        } else {
            closeDatabase();
            return false;
        }
    }

    public ArrayList<Customer> getAllCustomerPurchaseAmount() {
        ArrayList<Customer> customerArrayList = new ArrayList<Customer>();
        openWritableDatabase();
        Cursor c = sqLiteDb.query(DBHelper.TABLE_CUSTINFO, DBHelper.CUSTPURCHASE_COLUMNS,
                null, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            do {
                Customer customer = new Customer();
                customer.setMobile(c.getLong(c.getColumnIndexOrThrow("custmobile")));
                customer.setPurchaseAmount(c.getLong(c.getColumnIndexOrThrow("custpurchaseAmt")));
                customerArrayList.add(customer);
            } while (c.moveToNext());
            closeDatabase();
            return customerArrayList;
        } else {
            return null;
        }
    }
}
