package in.istore.bitblue.app.adminMenu.suppInfo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.ViewSuppAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.pojo.Supplier;

public class ViewSupplier extends Fragment {
    private ListView lvViewSupp;
    private ViewSuppAdapter suppAdapter;
    private ArrayList<Supplier> suppArrayList;
    private DbSuppAdapter dbsuppAdapter;

    public ViewSupplier() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_supplier, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        dbsuppAdapter = new DbSuppAdapter(getActivity());
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Please wait...");
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                suppArrayList = dbsuppAdapter.getAllSuppInfo();
                if (suppArrayList != null && suppArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
                    suppAdapter = new ViewSuppAdapter(getActivity(), suppArrayList);
                    lvViewSupp = (ListView) view.findViewById(R.id.lv_viewSupp);
                    lvViewSupp.setAdapter(suppAdapter);
                }
            }
        }.execute();
    }
}
