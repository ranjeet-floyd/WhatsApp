package in.istore.bitblue.app.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.Product;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Product> productArrayList = new ArrayList<Product>();
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, name;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
           // id = (TextView) view.findViewById(R.id.tv_listitem_id);
            name = (TextView) view.findViewById(R.id.tv_listitem_name);
            image = (ImageView) view.findViewById(R.id.iv_listitem_img);
        }
    }

    public RecyclerAdapter(ArrayList<Product> productArrayList) {
        if (productArrayList != null) {
            this.productArrayList = productArrayList;
        }
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem, parent, false);
        ViewHolder vh = new ViewHolder(listRow);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product product = productArrayList.get(position);
        holder.id.setText(product.getId());
        byte[] outImage = product.getImage();
        if (outImage != null) {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            holder.image.setImageBitmap(theImage);
        }
        holder.name.setText(product.getName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}
