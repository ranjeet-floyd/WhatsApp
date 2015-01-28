package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.category.products.ProSubCat;
import in.istore.bitblue.app.pojo.Category;

public class CategoryAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Category> categoryArrayList, origcategoryArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public CategoryAdapter(Context context, ArrayList<Category> categoryArrayList) {
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

                //call product subcategory intent
                Intent prosubcat = new Intent(context, ProSubCat.class);
                prosubcat.putExtra("category", categoryName);
                prosubcat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(prosubcat);
            }
        });
        return listRow;
    }


    //This filter is used to get the searchText from SearchView
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence textToFilter) {
                final FilterResults filterResults = new FilterResults();
                final ArrayList<Category> results = new ArrayList<Category>();
                if (origcategoryArrayList == null)
                    origcategoryArrayList = categoryArrayList;
                if (textToFilter != null) {
                    if (origcategoryArrayList != null && origcategoryArrayList.size() > 0) {
                        for (final Category category : origcategoryArrayList) {
                            if (category.getCategoryName().toLowerCase().contains(textToFilter.toString()))
                                results.add(category);
                        }
                    }

                    //'values' contains all the values computed by the filtering operation
                    filterResults.values = results;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence filterText, FilterResults results) {
                categoryArrayList = (ArrayList<Category>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView catname;
    }
}
