package in.istore.bitblue.app.adminMenu.suppInfo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.utilities.DatePickerFragment;
import in.istore.bitblue.app.utilities.DateUtil;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class AddSupplier extends Fragment {

    private EditText etName, etMobile, etAddress;
    private EditText[] allEditTexts;

    private Button bDate, baddSupp;
    private String startDate;
    private DbSuppAdapter suppAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private String SuppName, SuppStartDate, SuppAddress, Status;
    private long adminMobile, SuppMobile;
    private int StoreId;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

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
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();
        suppAdapter = new DbSuppAdapter(getActivity());
        etName = (EditText) view.findViewById(R.id.et_addsupp_name);
        etMobile = (EditText) view.findViewById(R.id.et_addsupp_mobile);
        etAddress = (EditText) view.findViewById(R.id.et_addsupp_address);
        allEditTexts = new EditText[]{etName, etMobile, etAddress};

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
                SuppName = etName.getText().toString();
                SuppMobile = Long.parseLong(etMobile.getText().toString());
                SuppAddress = etAddress.getText().toString();
                Date date = new Date();
                SuppStartDate = DateUtil.convertToStringDateOnly(date);
                StoreId = globalVariable.getStoreId();
                /*long result = suppAdapter.insertSuppInfo(name, mobile, address, startDate);           //Remove if using api
                if (result <= 0) {
                    Toast.makeText(getActivity(), "Record Not Added", Toast.LENGTH_SHORT).show();

                } else {
                    clearField(allEditTexts);
                }*/                                                                                     //

                addSupplierInfoToDatabase();
            }

        });
    }

    private void addSupplierInfoToDatabase() {
        new AsyncTask<String, String, String>() {

            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Adding Supplier...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Suppname", SuppName));
                nameValuePairs.add(new BasicNameValuePair("Suppmobile", String.valueOf(SuppMobile)));
                nameValuePairs.add(new BasicNameValuePair("Suppaddress", SuppAddress));
                nameValuePairs.add(new BasicNameValuePair("Suppstartdate", SuppStartDate));
                nameValuePairs.add(new BasicNameValuePair("Storeid", String.valueOf(StoreId)));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_ADD_SUPPLIER, nameValuePairs);
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
                    Toast.makeText(getActivity(), "Added Supplier " + SuppName, Toast.LENGTH_LONG).show();
                    clearField(allEditTexts);
                } else if (Status.equals("2")) {
                    Toast.makeText(getActivity(), "Supplier Already Exists", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
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
