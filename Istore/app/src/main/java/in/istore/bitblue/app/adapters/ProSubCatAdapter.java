package in.istore.bitblue.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.ProductSubCategory;

public class ProSubCatAdapter extends BaseAdapter implements Filterable {

    private ArrayList<ProductSubCategory> proSubCatArrayList, origproSubCatArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public ProSubCatAdapter(Context context, ArrayList<ProductSubCategory> proSubCatArrayList) {
        if (context != null && proSubCatArrayList != null) {
            this.proSubCatArrayList = proSubCatArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return proSubCatArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return proSubCatArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.subcategorylistitem, null);
            holder = new ViewHolder();
            holder.subcatname = (TextView) listRow.findViewById(R.id.tv_subcategory_subcatname);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();

        ProductSubCategory subCategory = proSubCatArrayList.get(position);
        holder.subcatname.setText(subCategory.getProductSubCategoryName());
        return listRow;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence textToFilter) {
                final FilterResults filterResults = new FilterResults();
                final ArrayList<ProductSubCategory> results = new ArrayList<ProductSubCategory>();
                if (origproSubCatArrayList == null)
                    origproSubCatArrayList = proSubCatArrayList;
                if (textToFilter != null) {
                    if (origproSubCatArrayList != null && origproSubCatArrayList.size() > 0) {
                        for (final ProductSubCategory subCategory : origproSubCatArrayList) {
                            if (subCategory.getProductSubCategoryName().toLowerCase().contains(textToFilter.toString()))
                                results.add(subCategory);
                        }
                    }

                    //'values' contains all the values computed by the filtering operation
                    filterResults.values = results;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence filterText, FilterResults results) {
                proSubCatArrayList = (ArrayList<ProductSubCategory>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView subcatname;
    }
}
