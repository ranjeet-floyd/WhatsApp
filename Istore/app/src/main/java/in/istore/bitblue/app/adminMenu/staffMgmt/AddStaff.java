package in.istore.bitblue.app.adminMenu.staffMgmt;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.utilities.DatePickerFragment;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.Mail;
import in.istore.bitblue.app.utilities.Store;

public class AddStaff extends Fragment {

    private EditText etName, etMobile, etAddress;
    private Button bDate, baddStaff;
    private String joinDate, StaffName, AdminEmail, StaffPass;
    private DbStaffAdapter staffAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private long adminMobile;
    private GlobalVariables globalVariable;
    private FragmentTabHost mTabHost;

    public AddStaff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_staff, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        staffAdapter = new DbStaffAdapter(getActivity());
        loginCredAdapter = new DbLoginCredAdapter(getActivity());
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();
        adminMobile = globalVariable.getAdminMobile();
        etName = (EditText) view.findViewById(R.id.et_addstaff_name);
        etMobile = (EditText) view.findViewById(R.id.et_addstaff_mobile);
        etAddress = (EditText) view.findViewById(R.id.et_addstaff_address);
        final EditText[] allEditTexts = new EditText[]{etName, etMobile, etAddress};

        bDate = (Button) view.findViewById(R.id.b_addstaff_joindate);
        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        baddStaff = (Button) view.findViewById(R.id.b_addstaff_addstaff);
        baddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForValidation(allEditTexts);
                int staffid = Store.generateStaffId();
                StaffPass = Store.generatePassword();
                int storeId = loginCredAdapter.getStoreId(adminMobile);
                AdminEmail = loginCredAdapter.getAdminEmail(storeId);
                StaffName = etName.getText().toString();
                long mobile = Long.parseLong(etMobile.getText().toString());
                String address = etAddress.getText().toString();
                sendMailToUser(AdminEmail, StaffPass);
                long result = staffAdapter.insertStaffInfo(storeId, staffid, StaffName, mobile, StaffPass, address, joinDate);
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
            joinDate = bDate.getText().toString();
        }

    };

    private void clearField(EditText[] allEditTexts) {
        for (EditText editText :
                allEditTexts) {
            editText.setText("");
        }
    }

    private void sendMailToUser(final String AdminEmail, final String passwd) {
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setTitle("");
                dialog.setMessage("Please Wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();

            }

            @Override
            protected Boolean doInBackground(String... strings) {
                return sendEmailTo(AdminEmail, passwd);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result != null && result) {
                    Toast.makeText(getActivity(), "Message Sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Mail sending failed Check Network", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private boolean sendEmailTo(String AdminEmail, String passwd) {
        Mail mail = new Mail("bitstorehelpdesk@gmail.com", "bitbluetech");

        String[] toArr = {AdminEmail};
        mail.setTo(toArr);
        mail.setFrom("bitstorehelpdesk@gmail.com");
        mail.setSubject("Your Password for BitStore App");
        mail.setBody("\nHi, " + StaffName +
                "\n Your Password is:" + passwd);
        try {
            if (mail.send()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
