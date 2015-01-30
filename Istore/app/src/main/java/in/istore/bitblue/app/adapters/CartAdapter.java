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

public class CartAdapter extends BaseAdapter {
    private ArrayList<CartItem> cartArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public CartAdapter(Context context, ArrayList<CartItem> cartArrayList) {
        if (context != null && cartArrayList != null) {
            this.cartArrayList = cartArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return cartArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.cartlistitem, null);
            holder = new ViewHolder();
            holder.itemName = (TextView) listRow.findViewById(R.id.tv_cartlistitem_name);
            holder.itemSoldQuan = (TextView) listRow.findViewById(R.id.tv_cartlistitem_soldquantity);
            holder.itemTotAmnt = (TextView) listRow.findViewById(R.id.tv_cartlistitem_totalamount);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        CartItem cartItem = cartArrayList.get(position);
        holder.itemName.setText(cartItem.getItemName());
        holder.itemSoldQuan.setText(String.valueOf(cartItem.getItemSoldQuantity()));
        holder.itemTotAmnt.setText(String.valueOf(cartItem.getItemTotalAmnt()));
        return listRow;
    }

    private static class ViewHolder {
        TextView itemName, itemSoldQuan, itemTotAmnt;
    }

}
