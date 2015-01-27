package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbSoldItemAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.utilities.DateUtil;

public class SoldHistoryAdapter extends BaseAdapter {
    private ArrayList<Product> soldhistlist = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private DbSoldItemAdapter dbsolAdapter;

    public SoldHistoryAdapter(Context context, ArrayList<Product> soldhistlist) {
        if (context != null && soldhistlist != null) {
            this.soldhistlist = soldhistlist;
            this.context = context;
            mInflater = LayoutInflater.from(context);
            dbsolAdapter = new DbSoldItemAdapter(context);
        }
    }

    @Override
    public int getCount() {
        return soldhistlist.size();
    }

    @Override
    public Object getItem(int position) {
        return soldhistlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup parent) {
        if (listRow == null) {

            listRow = mInflater.inflate(R.layout.soldhistoryitem, null);
            holder = new ViewHolder();
            holder.tvsoldquantity = (TextView) listRow.findViewById(R.id.tv_soldhistitem_soldquantity);
            holder.tvdate = (TextView) listRow.findViewById(R.id.tv_soldhistitem_date);
            holder.tvsellPrice = (TextView) listRow.findViewById(R.id.tv_soldhistitem_sellprice);
            listRow.setTag(holder);
        } else holder = (ViewHolder) listRow.getTag();

        Product product = soldhistlist.get(position);
        holder.tvsoldquantity.setText(product.getSoldQuantity());
        holder.tvdate.setText(DateUtil.getStringDate(product.getSoldDate()));
        holder.tvsellPrice.setText(product.getSellPrice());
        return listRow;
    }

    private static class ViewHolder {
        TextView tvsoldquantity, tvdate, tvsellPrice;
    }
}
