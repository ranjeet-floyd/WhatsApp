package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.TotRevDetails;
import in.istore.bitblue.app.utilities.DateUtil;

public class TotRevDetailsAdapter extends BaseAdapter {
    private ArrayList<TotRevDetails> totRevDetailsArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public TotRevDetailsAdapter(Context context, ArrayList<TotRevDetails> totRevDetailsArrayList) {
        if (context != null && totRevDetailsArrayList != null) {
            this.totRevDetailsArrayList = totRevDetailsArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return totRevDetailsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return totRevDetailsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {

            listRow = mInflater.inflate(R.layout.totrevdetailslistitem, null);
            holder = new ViewHolder();
            holder.staffid = (TextView) listRow.findViewById(R.id.tv_totrevdetails_staffid);
            holder.prodname = (TextView) listRow.findViewById(R.id.tv_totrevdetails_prodname);
            holder.quantity = (TextView) listRow.findViewById(R.id.tv_totrevdetails_quantity);
            holder.purchaseamount = (TextView) listRow.findViewById(R.id.tv_totrevdetails_purchaseamnt);
            holder.mobile = (TextView) listRow.findViewById(R.id.tv_totrevdetails_mobile);
            holder.purchaseDate = (TextView) listRow.findViewById(R.id.tv_totrevdetails_puchasedate);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        TotRevDetails totRevDetails = totRevDetailsArrayList.get(position);
        holder.staffid.setText(totRevDetails.getId());
        holder.prodname.setText(totRevDetails.getProdName());
        holder.quantity.setText(String.valueOf(totRevDetails.getQuantity()));
        holder.purchaseamount.setText(String.valueOf(totRevDetails.getPurchaseAmnt()));
        holder.mobile.setText(String.valueOf(totRevDetails.getMobile()));
        holder.purchaseDate.setText(DateUtil.getDateInDD_MM_YY(totRevDetails.getDate()));
        return listRow;
    }

    private static class ViewHolder {
        TextView staffid, prodname, quantity, purchaseamount, mobile, purchaseDate;
    }
}
