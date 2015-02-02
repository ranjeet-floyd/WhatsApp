package in.istore.bitblue.app.adminMenu.transactions.totalrevenue;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbTotSaleAmtByDateAdapter;
import in.istore.bitblue.app.utilities.DatePickerFragment;
import in.istore.bitblue.app.utilities.DateUtil;

public class TotalRevSelectRange extends ActionBarActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView toolTitle, tvFrom, tvTo, tvselectedRangeRev;
    private Button bFrom, bTo, bView, bViewDetails;
    private LinearLayout llRevDetails;

    private String from, to, formattedFrom, formattedTo;
    private int cur;
    private static final int FROM_DATE = 1;
    private static final int TO_DATE = 2;
    private final static String TOTAL_REVENUE_FOR_RANGE = "TotalRevenueForRange";
    private final static String TRANSACTION = "transaction";
    private float TotalRevenue, totalRevForRange;
    private SharedPreferences prefTransaction;
    private DbTotSaleAmtByDateAdapter dbtotSalebyDateAdapter;
    private SharedPreferences.Editor prefTotalRevenueForRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_sales);
        setToolbar();
        initViews();
    }

    private void initViews() {
        prefTotalRevenueForRange = getSharedPreferences(TOTAL_REVENUE_FOR_RANGE, MODE_PRIVATE).edit();

        dbtotSalebyDateAdapter = new DbTotSaleAmtByDateAdapter(this);

        bFrom = (Button) findViewById(R.id.b_totrevenue_from);
        bFrom.setOnClickListener(this);

        bTo = (Button) findViewById(R.id.b_totrevenue_to);
        bTo.setOnClickListener(this);

        bView = (Button) findViewById(R.id.b_totalrevenue_viewrevenue);
        bView.setOnClickListener(this);

        bViewDetails = (Button) findViewById(R.id.b_totrevenue_viewrevdetails);
        bViewDetails.setOnClickListener(this);

        tvFrom = (TextView) findViewById(R.id.tv_totrev_from);
        tvTo = (TextView) findViewById(R.id.tv_totrev_to);
        tvselectedRangeRev = (TextView) findViewById(R.id.tv_totrev_selectedRangeRev);

        llRevDetails = (LinearLayout) findViewById(R.id.ll_totrev_revenuedetail);
        llRevDetails.setVisibility(View.GONE);


    }

    private void setToolbar() {
        prefTransaction = getSharedPreferences(TRANSACTION, MODE_PRIVATE);
        TotalRevenue = prefTransaction.getFloat("TotalRevenue", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_draw_icon_remback);
        toolTitle.setText("Total Revenue: Rs." + TotalRevenue);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View button) {
        switch (button.getId()) {
            case R.id.b_totrevenue_from:
                cur = FROM_DATE;
                showDatePicker();
                break;
            case R.id.b_totrevenue_to:
                cur = TO_DATE;
                showDatePicker();
                break;
            case R.id.b_totalrevenue_viewrevenue:
                if (bFrom.getText().toString().equals(bTo.getText().toString())) {
                    String Error = "Select proper range";
                    showAlertDialog(Error);
                    break;
                }
                if (bFrom.getText().toString().equals("from") || bTo.getText().toString().equals("to")) {
                    String Error = "Enter From and To Date";
                    showAlertDialog(Error);
                    break;
                } else {
                    totalRevForRange = dbtotSalebyDateAdapter.getTotalRevenueforRange(formattedFrom, formattedTo);
                    prefTotalRevenueForRange.putFloat("totalrevforrange", totalRevForRange);
                    prefTotalRevenueForRange.commit();
                    tvFrom.setText(from);
                    tvTo.setText(to);
                    tvselectedRangeRev.setText(String.valueOf(totalRevForRange));
                    llRevDetails.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.b_totrevenue_viewrevdetails:
                prefTotalRevenueForRange.putString("formattedfromdate", formattedFrom);
                prefTotalRevenueForRange.putString("formattedtodate", formattedTo);
                prefTotalRevenueForRange.putString("fromdate", from);
                prefTotalRevenueForRange.putString("todate", to);
                prefTotalRevenueForRange.commit();


                Intent revDetails = new Intent(this, TotalRevViewDetails.class);
                startActivity(revDetails);
                break;
        }
    }

    private void showAlertDialog(String error) {
        new AlertDialog.Builder(this)
                .setTitle("Error").setIcon(getResources().getDrawable(R.drawable.erroricon))
                .setMessage(error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if (cur == FROM_DATE) {
                bFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                from = bFrom.getText().toString();
                formattedFrom = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(from);
            }

            if (cur == TO_DATE) {
                bTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                to = bTo.getText().toString();
                formattedTo = DateUtil.convertFromDD_MM_YYYYtoYYYY_MM_DD(to);

            }
        }

    };

}
