package in.istore.bitblue.app.adminMenu.suppInfo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.utilities.DatePickerFragment;
import in.istore.bitblue.app.utilities.GlobalVariables;

public class AddSupplier extends Fragment {

    private EditText etName, etMobile, etAddress;
    private Button bDate, baddSupp;
    private String startDate;
    private DbSuppAdapter suppAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private long adminMobile;
    private GlobalVariables globalVariable;
    private FragmentTabHost mTabHost;

    public AddSupplier() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_supplier, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        suppAdapter = new DbSuppAdapter(getActivity());
        etName = (EditText) view.findViewById(R.id.et_addsupp_name);
        etMobile = (EditText) view.findViewById(R.id.et_addsupp_mobile);
        etAddress = (EditText) view.findViewById(R.id.et_addsupp_address);
        final EditText[] allEditTexts = new EditText[]{etName, etMobile, etAddress};

        bDate = (Button) view.findViewById(R.id.b_addsupp_joindate);
        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        baddSupp = (Button) view.findViewById(R.id.b_addsupp_addsupp);
        baddSupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidation(allEditTexts);
                String name = etName.getText().toString();
                long mobile = Long.parseLong(etMobile.getText().toString());
                String address = etAddress.getText().toString();
                long result = suppAdapter.insertSuppInfo(name, mobile, address, startDate);
                if (result <= 0) {
                    Toast.makeText(getActivity(), "Record Not Added", Toast.LENGTH_SHORT).show();

                } else {
                    clearField(allEditTexts);
                    mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
                    Toast.makeText(getActivity(), "Record Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkForValidation(EditText[] allEditTexts) {
        for (EditText editText : allEditTexts) {
            if (editText.getText().toString().equals("")) {
                editText.setHint("Field Required");
                editText.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                return;
            }
        }
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
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            bDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            startDate = bDate.getText().toString();
        }

    };

    private void clearField(EditText[] allEditTexts) {
        for (EditText editText :
                allEditTexts) {
            editText.setText("");
        }
    }
}
