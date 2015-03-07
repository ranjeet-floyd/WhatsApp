package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.pojo.Product;

public class SoldItemAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Product> productArrayList;
    private ArrayList<Product> origproductArrayList;
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private int lastPosition = -1;
    private DbProductAdapter dbProAdapter;

    public SoldItemAdapter(Context context, ArrayList<Product> productArrayList) {
        if (context != null && productArrayList != null) {
            this.productArrayList = productArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
            dbProAdapter = new DbProductAdapter(context);
        }
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return productArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.soldlistitem, null);
            holder = new ViewHolder();
            holder.id = (TextView) listRow.findViewById(R.id.tv_soldlistitem_id);
            holder.image = (ImageView) listRow.findViewById(R.id.iv_soldlistitem_img);
            holder.name = (TextView) listRow.findViewById(R.id.tv_soldlistitem_name);
            holder.date = (TextView) listRow.findViewById(R.id.tv_soldlistitem_date);
            holder.sellprice = (TextView) listRow.findViewById(R.id.tv_soldlistitem_sellprice);
            holder.quantity = (TextView) listRow.findViewById(R.id.tv_soldlistitem_quantity);
            holder.custmob = (TextView) listRow.findViewById(R.id.tv_soldlistitem_custMob);
            holder.deliverAddress = (TextView) listRow.findViewById(R.id.tv_soldlistitem_deliverAddress);
            holder.lldeliverAddress = (LinearLayout) listRow.findViewById(R.id.ll_solditem_deliveraddress);
            listRow.setTag(holder);
        } else {
            holder = (ViewHolder) listRow.getTag();
        }
        final Product product = productArrayList.get(position);
        holder.id.setText(product.getId());
        byte[] outImage = product.getImage();
        if (outImage != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.image.setImageBitmap(theImage);
        }

        holder.name.setText(product.getName());
        holder.date.setText(product.getSoldDate());
        holder.sellprice.setText(context.getResources().getString(R.string.rs) + " " + String.valueOf(product.getSellPrice()));
        holder.quantity.setText(String.valueOf(product.getSoldQuantity()));
        holder.custmob.setText(product.getCustMob());
        String deliverAddress = product.getDeliverAddress();
        if (deliverAddress == null || deliverAddress.equals("null")) {
            holder.lldeliverAddress.setVisibility(View.GONE);
        } else {
            holder.lldeliverAddress.setVisibility(View.VISIBLE);
            holder.deliverAddress.setText(deliverAddress);
        }
        notifyDataSetChanged();

       /* listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.id = (TextView) view.findViewById(R.id.tv_soldlistitem_category);
                String id = holder.id.getText().toString();
                Intent viewItem = new Intent(context, ViewSoldItem.class);
                if (id != null) {
                    viewItem.putExtra("id", id);
                    viewItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(viewItem);
                } else {
                    Toast.makeText(context, "Item does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
      /*  Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
        listRow.startAnimation(animation);
        lastPosition = position;*/
        return listRow;
    }

    //This filter is used to get the searchText from SearchView
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence textToFilter) {
                final FilterResults filterResults = new FilterResults();
                final ArrayList<Product> results = new ArrayList<Product>();
                if (origproductArrayList == null)
                    origproductArrayList = productArrayList;
                if (textToFilter != null) {
                    if (origproductArrayList != null && origproductArrayList.size() > 0) {
                        for (final Product product : origproductArrayList) {
                            if (product.getName().toLowerCase().contains(textToFilter.toString()))
                                results.add(product);
                            else if (product.getId().toLowerCase().contains(textToFilter.toString()))
                                results.add(product);
                        }
                    }
                    //'values' contains all the values computed by the filtering operation
                    filterResults.values = results;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence filterText, FilterResults results) {
                productArrayList = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView id, name, date, sellprice, quantity, custmob, deliverAddress;
        LinearLayout lldeliverAddress;
        ImageView image;
    }
}
