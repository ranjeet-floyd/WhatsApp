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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.listMyStock.Product;
import in.istore.bitblue.app.soldItems.ViewItem;

public class SoldItemAdapter extends BaseAdapter {
    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;
    private int lastPosition = -1;

    public SoldItemAdapter(Context context, ArrayList<Product> productArrayList) {
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
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();
            holder.id = (TextView) listRow.findViewById(R.id.tv_listitem_id);
            holder.image = (ImageView) listRow.findViewById(R.id.iv_listitem_img);
            holder.name = (TextView) listRow.findViewById(R.id.tv_listitem_name);
            listRow.setTag(holder);

        } else {
            holder = (ViewHolder) listRow.getTag();
        }
        Product product = productArrayList.get(position);

        holder.id.setText(product.getId());
        byte[] outImage = product.getImage();
        if (outImage != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.image.setImageBitmap(theImage);
        }

        holder.name.setText(product.getName());

        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.id = (TextView) view.findViewById(R.id.tv_listitem_id);
                String id = holder.id.getText().toString();
                Intent viewItem = new Intent(context, ViewItem.class);
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
        TextView id, name;
        ImageView image;
    }
}
