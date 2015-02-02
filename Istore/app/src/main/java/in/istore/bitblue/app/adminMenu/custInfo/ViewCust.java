package in.istore.bitblue.app.adminMenu.custInfo;

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
import in.istore.bitblue.app.adapters.ViewCustAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustAdapter;
import in.istore.bitblue.app.pojo.Customer;

public class ViewCust extends Fragment {
    private ListView lvViewSupp;
    private ViewCustAdapter custAdapter;
    private ArrayList<Customer> custArrayList;
    private DbCustAdapter dbcustAdapter;
    public ViewCust() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_cust, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        dbcustAdapter = new DbCustAdapter(getActivity());
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
                custArrayList = dbcustAdapter.getAllCustomerPurchaseAmount();
                if (custArrayList != null && custArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
                    custAdapter = new ViewCustAdapter(getActivity(), custArrayList);
                    lvViewSupp = (ListView) view.findViewById(R.id.lv_viewCust);
                    lvViewSupp.setAdapter(custAdapter);
                }
            }
        }.execute();
    }
}
