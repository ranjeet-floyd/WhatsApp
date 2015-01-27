package in.istore.bitblue.app.adminMenu.suppInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.istore.bitblue.app.R;

public class ViewSupplier extends Fragment {


    public ViewSupplier() {
        // Required empty public constructor
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_supplier, container, false);
    }
}
