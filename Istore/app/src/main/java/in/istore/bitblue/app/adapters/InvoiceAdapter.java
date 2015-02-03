package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.CartItem;

public class InvoiceAdapter extends BaseAdapter {
    private ArrayList<CartItem> invoiceArrayList;
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;

    public InvoiceAdapter(Context context, ArrayList<CartItem> invoiceArrayList) {
        if (context != null && invoiceArrayList != null) {
            this.invoiceArrayList = invoiceArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return invoiceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return invoiceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.invoicelistitem, null);
            holder = new ViewHolder();
            holder.prodId = (TextView) listRow.findViewById(R.id.tv_invoiceitem_id);
            holder.prodName = (TextView) listRow.findViewById(R.id.tv_invoiceitem_name);
            holder.prodQuantity = (TextView) listRow.findViewById(R.id.tv_invoiceitem_quantity);
            holder.prodSellPrice = (TextView) listRow.findViewById(R.id.tv_invoiceitem_price);
            holder.prodTotalPrice = (TextView) listRow.findViewById(R.id.tv_invoiceitem_total);
            listRow.setTag(holder);
        } else {
            holder = (ViewHolder) listRow.getTag();
        }
        CartItem item = invoiceArrayList.get(position);
        holder.prodId.setText(item.getItemId());
        holder.prodName.setText(item.getItemName());
        holder.prodQuantity.setText(String.valueOf(item.getItemSoldQuantity()));
        holder.prodSellPrice.setText(String.valueOf(item.getItemSellPrice()));
        holder.prodTotalPrice.setText(String.valueOf(item.getItemTotalAmnt()));
        return listRow;
    }

    private static class ViewHolder {
        TextView prodId, prodName, prodQuantity, prodSellPrice, prodTotalPrice;
    }
}
