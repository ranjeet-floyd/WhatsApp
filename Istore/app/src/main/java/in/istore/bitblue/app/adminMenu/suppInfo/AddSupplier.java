package in.istore.bitblue.app.adminMenu.suppInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.istore.bitblue.app.R;

public class AddSupplier extends Fragment {


    public AddSupplier() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_supplier, container, false);
    }
}
