package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.TodaysSale;

public class TodaySaleAdapter extends BaseAdapter {
    private ArrayList<TodaysSale> todaysSaleArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public TodaySaleAdapter(Context context, ArrayList<TodaysSale> todaysSaleArrayList) {
        if (context != null && todaysSaleArrayList != null) {
            this.todaysSaleArrayList = todaysSaleArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return todaysSaleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return todaysSaleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {

            listRow = mInflater.inflate(R.layout.todaysalelistitem, null);
            holder = new ViewHolder();
            holder.staffid = (TextView) listRow.findViewById(R.id.tv_todaysale_staffid);
            holder.prodname = (TextView) listRow.findViewById(R.id.tv_todaysale_prodname);
            holder.quantity = (TextView) listRow.findViewById(R.id.tv_todaysale_quantity);
            holder.purchaseamount = (TextView) listRow.findViewById(R.id.tv_todaysale_purchaseamnt);
            holder.mobile = (TextView) listRow.findViewById(R.id.tv_todaysale_mobile);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        TodaysSale todaysSale = todaysSaleArrayList.get(position);
        holder.staffid.setText(String.valueOf(todaysSale.getId()));
        holder.prodname.setText(todaysSale.getProdName());
        holder.quantity.setText(String.valueOf(todaysSale.getQuantity()));
        holder.purchaseamount.setText(String.valueOf(todaysSale.getPurchaseAmnt()));
        holder.mobile.setText(String.valueOf(todaysSale.getMobile()));
        return listRow;
    }


    private static class ViewHolder {
        TextView staffid, prodname, quantity, purchaseamount, mobile;
    }
}
