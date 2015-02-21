package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.DateUtil;

public class FilterByProdNameAdapter extends BaseAdapter {
    private ArrayList<SoldProduct> soldProductArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public FilterByProdNameAdapter(Context context, ArrayList<SoldProduct> soldProductArrayList) {
        if (context != null && soldProductArrayList != null) {
            this.soldProductArrayList = soldProductArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return soldProductArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return soldProductArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.filterbypronamelistitem, null);
            holder = new ViewHolder();
            holder.staffid = (TextView) listRow.findViewById(R.id.tv_filterbyname_staffid);
            holder.quantity = (TextView) listRow.findViewById(R.id.tv_filterbyname_quantity);
            holder.totalsales = (TextView) listRow.findViewById(R.id.tv_filterbyname_totalsales);
            holder.custmobile = (TextView) listRow.findViewById(R.id.tv_filterbyname_custmobile);
            holder.date = (TextView) listRow.findViewById(R.id.tv_filterbyname_date);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        SoldProduct soldProduct = soldProductArrayList.get(position);
        holder.staffid.setText(String.valueOf(soldProduct.getStaffId()));
        holder.quantity.setText(String.valueOf(soldProduct.getItemSoldQuantity()));
        holder.totalsales.setText(String.valueOf(soldProduct.getItemTotalAmnt()));
        holder.custmobile.setText(String.valueOf(soldProduct.getMobile()));
        holder.date.setText(DateUtil.getDateInDD_MM_YYYY(soldProduct.getDate()));
        listRow.setTag(holder);

        return listRow;
    }

    private static class ViewHolder {
        TextView staffid, quantity, totalsales, custmobile, date;
    }
}
