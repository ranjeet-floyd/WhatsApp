package in.istore.bitblue.app.adminMenu.staffMgmt;

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
import in.istore.bitblue.app.adapters.ViewStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.pojo.Staff;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class ViewStaff extends Fragment {
    private ListView lvViewStaff;
    private ViewStaffAdapter staffAdapter;
    private ArrayList<Staff> staffArrayList;
    private DbStaffAdapter dbstaffAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private GlobalVariables globalVariable;

    public ViewStaff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_staff, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();

        loginCredAdapter = new DbLoginCredAdapter(getActivity());
        dbstaffAdapter = new DbStaffAdapter(getActivity());
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
                int StoreId = loginCredAdapter.getStoreId(globalVariable.getAdminMobile());
                staffArrayList = dbstaffAdapter.getAllStaffInfo(StoreId);
                if (staffArrayList != null && staffArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean res) {
                dialog.dismiss();
                if (res == true) {
                    staffAdapter = new ViewStaffAdapter(getActivity(), staffArrayList);
                    lvViewStaff = (ListView) view.findViewById(R.id.lv_viewStaff);
                    lvViewStaff.setAdapter(staffAdapter);
                }
            }
        }.execute();


    }

}
