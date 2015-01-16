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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbProductAdapter;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.soldItems.ViewSoldItem;
import in.istore.bitblue.app.utilities.DateUtil;

public class SoldItemAdapter extends BaseAdapter {
    private ArrayList<Product> productArrayList = new ArrayList<Product>();
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
            holder.id = (TextView) listRow.findViewById(R.id.tv_soldlistitem_category);
            holder.image = (ImageView) listRow.findViewById(R.id.iv_soldlistitem_img);
            holder.name = (TextView) listRow.findViewById(R.id.tv_soldlistitem_name);
            holder.date = (TextView) listRow.findViewById(R.id.tv_soldlistitem_date);
            holder.favorite = (ToggleButton) listRow.findViewById(R.id.ib_soldlistitem_favorite);
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
        holder.date.setText(DateUtil.getStringDate(product.getDate()));
        if (product.getFavorite() == 1) {
            holder.favorite.setChecked(true);
        } else if (product.getFavorite() == 0) {
            holder.favorite.setChecked(false);
        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favorite.isChecked()) {
                    product.setFavorite(1);
                } else {
                    product.setFavorite(0);
                }
                dbProAdapter.updateFavoriteProductDetails(product.getId(), product.getFavorite());
            }
        });
        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.id = (TextView) view.findViewById(R.id.tv_soldlistitem_category);
                String id = holder.id.getText().toString();
                Intent viewItem = new Intent(context, ViewSoldItem.class);
                if (id != null) {
                    viewItem.putExtra("id", id);
                    context.startActivity(viewItem);
                } else {
                    Toast.makeText(context, "Item does not exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        listRow.startAnimation(animation);
        lastPosition = position;
        return listRow;
    }

    private static class ViewHolder {
        TextView id, name, date;
        ImageView image;
        ToggleButton favorite;
    }
}
