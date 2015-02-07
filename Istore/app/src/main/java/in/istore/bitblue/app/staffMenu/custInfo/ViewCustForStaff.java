package in.istore.bitblue.app.staffMenu.custInfo;

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
import in.istore.bitblue.app.adapters.ViewCustForStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class ViewCustForStaff extends Fragment {
    private ListView lvViewCust;

    private ViewCustForStaffAdapter custAdapter;
    private ArrayList<SoldProduct> soldprodArrayList;
    private DbCustPurHistAdapter dbcustpurhistAdapter;
    private GlobalVariables globalVariables;
    private long StaffId;

    public ViewCustForStaff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_cust_for_staff, container, false);
        globalVariables = (GlobalVariables) getActivity().getApplicationContext();
        StaffId = globalVariables.getStaffId();
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        dbcustpurhistAdapter = new DbCustPurHistAdapter(getActivity());
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
                soldprodArrayList = dbcustpurhistAdapter.getCustomerInfoForStaffId(StaffId);  //Change this
                if (soldprodArrayList != null && soldprodArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
                    custAdapter = new ViewCustForStaffAdapter(getActivity(), soldprodArrayList);
                    lvViewCust = (ListView) view.findViewById(R.id.lv_viewCustForStaff);
                    lvViewCust.setAdapter(custAdapter);
                }
            }
        }.execute();
    }
}
