package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.Customer;

public class ViewCustAdapter extends BaseAdapter {
    private ArrayList<Customer> custArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public ViewCustAdapter(Context context, ArrayList<Customer> custArrayList) {
        if (context != null && custArrayList != null) {
            this.custArrayList = custArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return custArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return custArrayList.get(position);
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
        Customer customer = custArrayList.get(position);

        holder.mobile.setText(String.valueOf(customer.getMobile()));
        holder.amount.setText(String.valueOf(customer.getPurchaseAmount()));
        return listRow;
    }

    private static class ViewHolder {
        TextView mobile, amount;
    }
}
