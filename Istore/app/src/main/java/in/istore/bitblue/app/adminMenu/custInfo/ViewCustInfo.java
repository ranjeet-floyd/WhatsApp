package in.istore.bitblue.app.adminMenu.custInfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.istore.bitblue.app.R;

public class ViewCustInfo extends Fragment {

    public ViewCustInfo() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_cust_info, container, false);
    }

}
