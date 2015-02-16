package in.istore.bitblue.app.home.dragGrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.GridItems;

public class GridDynamicAdapter extends BaseDynamicGridAdapter {
    private ArrayList<GridItems> gridItemList;
    private Context context;

    public GridDynamicAdapter(Context context, ArrayList<GridItems> gridItemList, int columnCount) {
        super(context, gridItemList, columnCount);
        this.gridItemList = gridItemList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grid, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.build(getItem(position));
        return convertView;
    }

    private class ViewHolder {
        private TextView titleText;
        private ImageView image;
        private LinearLayout llgridLayout;

        private ViewHolder(View view) {
            titleText = (TextView) view.findViewById(R.id.b_homepage_item);
            image = (ImageView) view.findViewById(R.id.iv_homepage_item);
            llgridLayout = (LinearLayout) view.findViewById(R.id.ll_homepage_item);
        }

        void build(Object gridItem) {
            GridItems gridItems = (GridItems) gridItem;
            titleText.setText(gridItems.getTvgridItemTitle());
            image.setImageResource(gridItems.getIvgridItemImage());
            llgridLayout.setBackgroundDrawable(context.getResources().getDrawable(gridItems.getLlgridLayout()));
        }
    }
}