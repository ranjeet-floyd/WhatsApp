package in.istore.bitblue.app.navDrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.istore.bitblue.app.R;

public class NavigationDrawer extends Fragment {

    public static NavigationDrawer newInstance(String param1, String param2) {
        NavigationDrawer fragment = new NavigationDrawer();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NavigationDrawer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }
}
