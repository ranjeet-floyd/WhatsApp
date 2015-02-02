package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.Outofstock;

public class OutOfStockAdapter extends BaseAdapter {

    private ArrayList<Outofstock> outofstockArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public OutOfStockAdapter(Context context, ArrayList<Outofstock> outofstockArrayList) {
        if (context != null && outofstockArrayList != null) {
            this.outofstockArrayList = outofstockArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return outofstockArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return outofstockArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {

            listRow = mInflater.inflate(R.layout.outofstocklistitem, null);
            holder = new ViewHolder();
            holder.prodname = (TextView) listRow.findViewById(R.id.tv_outofstock_prodname);
            holder.remquantity = (TextView) listRow.findViewById(R.id.tv_outofstock_remquantity);
            holder.suppmobile = (TextView) listRow.findViewById(R.id.tv_outofstock_suppname);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        Outofstock outofstock = outofstockArrayList.get(position);
        holder.prodname.setText(outofstock.getProdName());
        holder.remquantity.setText(String.valueOf(outofstock.getRemQuantity()));
        holder.suppmobile.setText(String.valueOf(outofstock.getSuppMobile()));
        return listRow;
    }

    private static class ViewHolder {
        TextView prodname, remquantity, suppmobile;
    }
}
