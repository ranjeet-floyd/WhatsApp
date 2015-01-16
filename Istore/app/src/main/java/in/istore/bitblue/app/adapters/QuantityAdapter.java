package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbQuantityAdapter;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.utilities.DateUtil;

public class QuantityAdapter extends BaseAdapter {
    private ArrayList<Product> quantityList = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private DbQuantityAdapter dbAdapter;

    public QuantityAdapter(Context context, ArrayList<Product> quantityList) {
        if (context != null && quantityList != null) {
            this.quantityList = quantityList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
            dbAdapter = new DbQuantityAdapter(context);
        }
    }

    @Override
    public int getCount() {
        return quantityList.size();
    }

    @Override
    public Object getItem(int position) {
        return quantityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup parent) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.quantityhistoryitem, null);
            holder = new ViewHolder();
            holder.addedquantity = (TextView) listRow.findViewById(R.id.tv_quanthistitem_addedquantity);
            holder.date = (TextView) listRow.findViewById(R.id.tv_quanthistitem_date);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();
        Product quantity = quantityList.get(position);

        holder.addedquantity.setText(quantity.getQuantity());
        holder.date.setText(DateUtil.getStringDate(quantity.getDate()));

        return listRow;
    }

    private static class ViewHolder {
        TextView addedquantity, date;
    }
}


