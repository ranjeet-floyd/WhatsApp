package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.Staff;

public class ViewStaffAdapter extends BaseAdapter {

    private ArrayList<Staff> staffArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public ViewStaffAdapter(Context context, ArrayList<Staff> staffArrayList) {
        if (context != null && staffArrayList != null) {
            this.staffArrayList = staffArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return staffArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return staffArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.viewstafflistitem, null);
            holder = new ViewHolder();
            holder.id = (TextView) listRow.findViewById(R.id.tv_view_staff_staffid);
            holder.name = (TextView) listRow.findViewById(R.id.tv_view_staff_staffname);
            holder.mobile = (TextView) listRow.findViewById(R.id.tv_view_staff_staffmobile);
            holder.sales = (TextView) listRow.findViewById(R.id.tv_view_staff_staffsales);
            listRow.setTag(holder);
        } else {
            holder = (ViewHolder) listRow.getTag();
        }
        Staff staff = staffArrayList.get(position);
        holder.id.setText(String.valueOf(staff.getStaffId()));
        holder.name.setText(staff.getName());
        holder.mobile.setText(String.valueOf(staff.getMobile()));
        holder.sales.setText(String.valueOf(staff.getTotalSales()));
        return listRow;
    }

    private static class ViewHolder {
        TextView id, name, mobile, sales;
    }
}
