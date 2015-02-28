package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.Stocks.listStock.ListMyStock;
import in.istore.bitblue.app.pojo.Category;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class CategoryListAdapter extends BaseAdapter {
    private ArrayList<Category> categoryArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;
    private GlobalVariables globalVariable;

    public CategoryListAdapter(Context context, ArrayList<Category> categoryArrayList) {
        if (context != null && categoryArrayList != null) {
            this.categoryArrayList = categoryArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
            globalVariable = (GlobalVariables) context.getApplicationContext();
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
                view.setBackgroundColor(context.getResources().getColor(R.color.material_blue_300));
                holder.catname = (TextView) view.findViewById(R.id.tv_category_catname);
                String categoryName = holder.catname.getText().toString();
                Intent listmystock = new Intent(context, ListMyStock.class);
                globalVariable.setCategoryName(categoryName);
                listmystock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(listmystock);
            }
        });
        return listRow;
    }

    private static class ViewHolder {
        TextView catname;
    }
}