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
import android.widget.ToggleButton;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.pojo.Product;
import in.istore.bitblue.app.sellItems.SellItem;

public class ListStockAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private int lastPosition = -1;
    private ArrayList<Product> origproductArrayList;
    private DbProductAdapter dbProAdapter;

    public ListStockAdapter(Context context, ArrayList<Product> productArrayList) {
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
    public View getView(int position, View listRow, ViewGroup parent) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();
            holder.id = (TextView) listRow.findViewById(R.id.tv_listitem_category);
            holder.image = (ImageView) listRow.findViewById(R.id.iv_listitem_img);
            holder.name = (TextView) listRow.findViewById(R.id.tv_listitem_name);
            holder.date = (TextView) listRow.findViewById(R.id.tv_listitem_date);
            holder.favorite = (ToggleButton) listRow.findViewById(R.id.ib_listitem_favorite);
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
        holder.date.setText(product.getAddedDate());
        if (product.getIsFavorite() == 1) {
            holder.favorite.setChecked(true);
        } else if (product.getIsFavorite() == 0) {
            holder.favorite.setChecked(false);
        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favorite.isChecked()) {
                    product.setIsFavorite(1);
                } else {
                    product.setIsFavorite(0);
                }
                dbProAdapter.updateFavoriteProductDetails(product.getId(), product.getIsFavorite());
            }
        });

        //This is used to select clicked listItem and get its details
        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.id = (TextView) view.findViewById(R.id.tv_listitem_category);
                String id = holder.id.getText().toString();
                Intent viewItem = new Intent(context, SellItem.class);
                if (id != null) {
                    viewItem.putExtra("id", id);
                    context.startActivity(viewItem);
                } else {
                    Toast.makeText(context, "Item does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Animation when listview is scrolled
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);

        //  Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
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
        TextView id, name, date;
        ImageView image;
        ToggleButton favorite;
    }
}
