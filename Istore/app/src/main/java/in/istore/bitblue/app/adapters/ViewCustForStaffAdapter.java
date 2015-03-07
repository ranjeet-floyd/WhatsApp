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

public class ViewCustForStaffAdapter extends BaseAdapter {
    private ArrayList<SoldProduct> soldprodArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public ViewCustForStaffAdapter(Context context, ArrayList<SoldProduct> soldprodArrayList) {
        if (context != null && soldprodArrayList != null) {
            this.soldprodArrayList = soldprodArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return soldprodArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return soldprodArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.viewcustlistitem, null);
            holder = new ViewHolder();
            holder.mobile = (TextView) listRow.findViewById(R.id.tv_view_cust_mobile);
            holder.amount = (TextView) listRow.findViewById(R.id.tv_view_cust_amount);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();
        SoldProduct soldProduct = soldprodArrayList.get(position);
        holder.mobile.setText(String.valueOf(soldProduct.getMobile()));
        holder.amount.setText(context.getResources().getString(R.string.rs)
                + " " + String.valueOf(soldProduct.getItemTotalAmnt()));
        return listRow;
    }

    private static class ViewHolder {
        TextView mobile, amount;
    }
}
