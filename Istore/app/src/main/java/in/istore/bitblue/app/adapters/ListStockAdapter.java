package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.home.Stocks.sellItem.SellItem;
import in.istore.bitblue.app.pojo.Product;

public class ListStockAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private int lastPosition = -1;
    private ArrayList<Product> origproductArrayList;

    public ListStockAdapter(Context context, ArrayList<Product> productArrayList) {
        if (context != null && productArrayList != null) {
            this.productArrayList = productArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View listRow, ViewGroup parent) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();
            holder.category = (TextView) listRow.findViewById(R.id.tv_listitem_category);
            holder.quantity = (TextView) listRow.findViewById(R.id.tv_listitem_quantity);
            holder.image = (ImageView) listRow.findViewById(R.id.iv_listitem_img);
            holder.name = (TextView) listRow.findViewById(R.id.tv_listitem_name);
            holder.sellprice = (TextView) listRow.findViewById(R.id.tv_listitem_sellprice);
            listRow.setTag(holder);
        } else {
            holder = (ViewHolder) listRow.getTag();
        }
        final Product product = productArrayList.get(position);

        //byte[] outImage = ImageUtil.convertBase64ImagetoByteArrayImage(product.getProdImage());
        byte[] outImage = product.getImage();
        if (outImage != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.image.setImageBitmap(theImage);
        }
        holder.category.setText(product.getCategory());
        holder.quantity.setText(String.valueOf(product.getQuantity()));
        holder.name.setText(product.getName());
        holder.sellprice.setText(context.getResources().getString(R.string.rs) + " " + product.getSellprice());

        //This is used to select clicked listItem and get its details
        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.name = (TextView) view.findViewById(R.id.tv_listitem_name);
                holder.category = (TextView) view.findViewById(R.id.tv_listitem_category);
                String categoryName = holder.category.getText().toString();
                String prodName = holder.name.getText().toString();
                Intent viewItem = new Intent(context, SellItem.class);
                if (prodName != null && categoryName != null) {
                    viewItem.putExtra("prodName", prodName);
                    viewItem.putExtra("categoryName", categoryName);
                    viewItem.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(viewItem);
                } else {
                    Toast.makeText(context, "Item does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Animation when listview is scrolled
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        listRow.startAnimation(animation);
        lastPosition = position;
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
        TextView category, name, sellprice, quantity;
        ImageView image;
    }
}
