package in.istore.bitblue.app.adminMenu.custInfo;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.istore.bitblue.app.R;

public class CusInfoContent extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private FragmentTabHost mTabHost;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_info);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        progressBar = (ProgressBar) toolbar.findViewById(R.id.ll_progressbar);
        setSupportActionBar(toolbar);
        toolTitle.setText("CUSTOMERS");
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
        mTabHost.addTab(mTabHost.newTabSpec("View Customers").setIndicator("Purchase Details"),
                ViewCust.class, null);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource((R.drawable.tabshadow));

    }
}
