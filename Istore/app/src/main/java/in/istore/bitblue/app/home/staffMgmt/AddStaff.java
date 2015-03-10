package in.istore.bitblue.app.home.staffMgmt;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.utilities.API;
import in.istore.bitblue.app.utilities.DatePickerFragment;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.Mail;
import in.istore.bitblue.app.utilities.Store;

public class AddStaff extends Fragment {

    private EditText etName, etMobile, etAddress;
    EditText[] allEditTexts;
    private Button bDate, baddStaff;
    private String joinDate, StaffName, StaffEmail, StaffAddress, AdminEmail, StaffPass, Status;
    private DbStaffAdapter staffAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private long AdminMobile, StaffMobile;
    private int StoreId, StaffId;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private float StaffTotSales;

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
        AdminMobile = globalVariable.getAdminMobile();
        etName = (EditText) view.findViewById(R.id.et_addstaff_name);
        etMobile = (EditText) view.findViewById(R.id.et_addstaff_mobile);
        etAddress = (EditText) view.findViewById(R.id.et_addstaff_address);
        allEditTexts = new EditText[]{etName, etMobile, etAddress};

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
                StoreId = globalVariable.getStoreId();
                StaffId = Store.generateStaffId();
                if (etName.getText().toString().equals("")) {
                    etName.setHint("Field Required");
                    etName.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return;
                } else if (etMobile.getText().toString().equals("")) {
                    etMobile.setHint("Field Required");
                    etMobile.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return;
                } else if (etAddress.getText().toString().equals("")) {
                    etAddress.setHint("Field Required");
                    etAddress.setHintTextColor(getResources().getColor(R.color.material_red_A400));
                    return;
                }
                StaffName = etName.getText().toString();
                if (!etMobile.getText().toString().equals(""))
                    StaffMobile = Long.parseLong(etMobile.getText().toString());
                StaffAddress = etAddress.getText().toString();
                StaffPass = Store.generatePassword();
                AdminEmail = globalVariable.getAdminEmail();
                Date date = new Date();
                joinDate = DateUtil.convertToStringDateOnly(date);
                 addStaffInfoOnServer();
               // addStaffInfoOnLocal();
                // long result = staffAdapter.insertStaffInfo(StoreId, StaffId, 0, StaffName, StaffMobile, StaffPass, StaffAddress, joinDate);      //Remove if using api
               /* if (result <= 0) {
                    Toast.makeText(getActivity(), "Record Not Added", Toast.LENGTH_SHORT).show();
                } else {  */
            }
        });
    }

    private void addStaffInfoOnLocal() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Adding Staff...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                if (isStaffAlreadyPresent(String.valueOf(StaffMobile)))
                    return "alreadyExists";
                else {
                    long result = staffAdapter.addNewStaff(String.valueOf(StoreId), String.valueOf(StaffId), StaffEmail, StaffName, String.valueOf(StaffMobile), StaffPass, StaffAddress, joinDate, String.valueOf(StaffTotSales));
                    if (result > 0)
                        return "addedStaff";
                    else
                        return "failedToAdd";
                }
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response.equals("alreadyExists")) {
                    Toast.makeText(getActivity(), "Staff Already Exists", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("failedToAdd")) {
                    Toast.makeText(getActivity(), "Failed to add", Toast.LENGTH_SHORT).show();
                } else if (Response.equals("addedStaff")) {
                    Toast.makeText(getActivity(), StaffName + " Added", Toast.LENGTH_SHORT).show();
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your Password is: " + StaffPass;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                }
            }
        }.execute();

    }

    private boolean isStaffAlreadyPresent(String staffMobile) {
        return staffAdapter.isStaffExists(staffMobile);

    }

    private void addStaffInfoOnServer() {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Adding Staff...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Storeid", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("Staffid", String.valueOf(StaffId)));
                nameValuePairs.add(new BasicNameValuePair("Staffemail", String.valueOf(StaffEmail)));
                nameValuePairs.add(new BasicNameValuePair("Staffname", StaffName));
                nameValuePairs.add(new BasicNameValuePair("Staffmobile", String.valueOf(StaffMobile)));
                nameValuePairs.add(new BasicNameValuePair("Staffaddress", StaffAddress));
                nameValuePairs.add(new BasicNameValuePair("Staffpasswd", StaffPass));
                nameValuePairs.add(new BasicNameValuePair("StaffjoinOn", joinDate));
                nameValuePairs.add(new BasicNameValuePair("Stafftotsale", String.valueOf(StaffTotSales)));

                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_STAFF_SIGN_UP, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        jsonObject = jsonArray.getJSONObject(0);
                        Status = jsonObject.getString("status");
                        return Status;
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }


            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                if (Response == null) {
                    Toast.makeText(getActivity(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (Status.equals("1")) {
                    Toast.makeText(getActivity(), "Added Staff " + StaffName, Toast.LENGTH_LONG).show();
                    String NotificationTitle = "BITSTORE PASSWORD";
                    String NotificationMessage = "Your Password is: " + StaffPass;
                    sendPasswordThroughNotification(NotificationTitle, NotificationMessage);
                    sendMailToUser(AdminEmail, StaffPass);
                    clearField(allEditTexts);
                } else if (Status.equals("2")) {
                    Toast.makeText(getActivity(), "Staff Already Exists", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void sendPasswordThroughNotification(String Title, String Message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setContentTitle(Title);
        mBuilder.setContentText(Message);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
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

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            bDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            joinDate = bDate.getText().toString();
        }

    };
}
