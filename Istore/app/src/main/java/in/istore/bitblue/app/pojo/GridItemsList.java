package in.istore.bitblue.app.pojo;

import java.util.ArrayList;

import in.istore.bitblue.app.R;

public class GridItemsList {

    public static ArrayList<GridItems> getAllGridItems() {
        ArrayList<GridItems> gridItemsArrayList = new ArrayList<GridItems>();

        GridItems itemTransaction = new GridItems(R.drawable.tran, "Transactions", R.drawable.transactionshadow);
        gridItemsArrayList.add(itemTransaction);

        GridItems itemUnknown = new GridItems(R.drawable.category, "Category", R.drawable.categoryshadow);
        gridItemsArrayList.add(itemUnknown);

        GridItems itemManageStaff = new GridItems(R.drawable.staffmgnt, "Manage Staff", R.drawable.managestaffshadow);
        gridItemsArrayList.add(itemManageStaff);

        GridItems itemStocks = new GridItems(R.drawable.stock, "Stocks", R.drawable.stockshadow);
        gridItemsArrayList.add(itemStocks);

        return gridItemsArrayList;
    }
}
