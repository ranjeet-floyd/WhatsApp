package in.istore.bitblue.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.pojo.Supplier;

public class ViewSuppAdapter extends BaseAdapter {
    private ArrayList<Supplier> suppArrayList;
    private Context context;
    private LayoutInflater mInflater;
    private ViewHolder holder;

    public ViewSuppAdapter(Context context, ArrayList<Supplier> suppArrayList) {
        if (context != null && suppArrayList != null) {
            this.suppArrayList = suppArrayList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return suppArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return suppArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View listRow, ViewGroup viewGroup) {
        if (listRow == null) {
            listRow = mInflater.inflate(R.layout.viewsupplistitem, null);
            holder = new ViewHolder();
            holder.name = (TextView) listRow.findViewById(R.id.tv_view_supp_name);
            holder.mobile = (TextView) listRow.findViewById(R.id.tv_view_supp_mobile);
            listRow.setTag(holder);
        } else
            holder = (ViewHolder) listRow.getTag();
        Supplier supplier = suppArrayList.get(position);
        holder.name.setText(supplier.getName());
        holder.mobile.setText(String.valueOf(supplier.getMobile()));

        //This is used to select clicked listItem and get its details
        listRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                holder.name = (TextView) view.findViewById(R.id.tv_view_supp_name);
                holder.mobile = (TextView) view.findViewById(R.id.tv_view_supp_mobile);
                String name = holder.name.getText().toString();
                String mobile = holder.mobile.getText().toString();
                showCallDialog(name, mobile);
            }
        });
        return listRow;
    }

    private void showCallDialog(String name, final String mobile) {
        final AlertDialog d = new AlertDialog.Builder(context)
                .setPositiveButton("CALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + mobile));
                        context.startActivity(callIntent);
                    }
                })
                .setMessage("Call Supplier: " + name)
                .create();
        d.show();
    }

    private static class ViewHolder {
        TextView name, mobile;
    }
}
