package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.listStock.ListMyStock;
import in.istore.bitblue.app.pojo.Category;

public class CategoryListAdapter extends BaseAdapter {
    private ArrayList<Category> categoryArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public CategoryListAdapter(Context context, ArrayList<Category> categoryArrayList) {
        if (context != null && categoryArrayList != null) {
            this.categoryArrayList = categoryArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.categorylistitem, null);
            holder = new ViewHolder();
            holder.catname = (TextView) listRow.findViewById(R.id.tv_category_catname);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        Category category = categoryArrayList.get(position);
        holder.catname.setText(category.getCategoryName());
        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.catname = (TextView) view.findViewById(R.id.tv_category_catname);
                String categoryName = holder.catname.getText().toString();
                Toast.makeText(context, categoryName, Toast.LENGTH_SHORT).show();

                Intent listmystock = new Intent(context, ListMyStock.class);
                listmystock.putExtra("categoryName", categoryName);
                context.startActivity(listmystock);
            }
        });
        return listRow;
    }

    private static class ViewHolder {
        TextView catname;
    }
}