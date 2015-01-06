package in.istore.bitblue.app.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.listMyStock.Product;

public class ListStockAdapter extends BaseAdapter {

    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder;

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
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
        holder.image.setImageBitmap(theImage);

        holder.name.setText(product.getName());

        return listRow;
    }

    private static class ViewHolder {
        TextView id, name;
        ImageView image;
    }
}
