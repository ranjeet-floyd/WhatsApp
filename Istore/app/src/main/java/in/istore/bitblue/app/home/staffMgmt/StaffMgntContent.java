package in.istore.bitblue.app.home.staffMgmt;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import in.istore.bitblue.app.R;

public class StaffMgntContent extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private FragmentTabHost mTabHost;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_mgnt);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("STAFF");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void initViews() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Add Staff").setIndicator("Add new Staff"),
                AddStaff.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("View Staff").setIndicator("View Staff"),
                ViewStaff.class, null);
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.material_blue_grey_300));
        }
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource((R.drawable.tabshadow));
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.material_blue_grey_300));
                }
                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource((R.drawable.tabshadow));
            }
        });
    }

}
