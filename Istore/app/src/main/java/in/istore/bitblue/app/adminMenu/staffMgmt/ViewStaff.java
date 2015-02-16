package in.istore.bitblue.app.adminMenu.staffMgmt;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.ViewStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbLoginCredAdapter;
import in.istore.bitblue.app.databaseAdapter.DbStaffAdapter;
import in.istore.bitblue.app.pojo.Staff;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class ViewStaff extends Fragment {
    private ListView lvViewStaff;
    private ViewStaffAdapter staffAdapter;
    private ArrayList<Staff> staffArrayList = new ArrayList<Staff>();
    private DbStaffAdapter dbstaffAdapter;
    private DbLoginCredAdapter loginCredAdapter;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;

    private Staff staff;
    private int StoreId;
    private String AdminKey;

    public ViewStaff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_staff, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();
        StoreId = globalVariable.getStoreId();
        AdminKey = globalVariable.getAdminKey();
        loginCredAdapter = new DbLoginCredAdapter(getActivity());
        dbstaffAdapter = new DbStaffAdapter(getActivity());
        viewStaffForThisStore(view);
    }

    private void viewStaffForThisStore(final View view) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Retrieving Staff's for this Store...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                /*int StoreId = loginCredAdapter.getStoreId(globalVariable.getAdminMobile());   //Remove if using api  change parameter from string to Boolean
                staffArrayList = dbstaffAdapter.getAllStaffInfo(StoreId);
                if (staffArrayList != null && staffArrayList.size() > 0)
                    return true;
                else return false;*/                                       //
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Storeid", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("AdminKey", AdminKey));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_VIEW_STAFF, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
                   /* staffAdapter = new ViewStaffAdapter(getActivity(), staffArrayList);         //remove if using api
                    lvViewStaff = (ListView) view.findViewById(R.id.lv_viewStaff);
                    lvViewStaff.setAdapter(staffAdapter);*/
                if (Response == null) {                                          //
                    Toast.makeText(getActivity(), "Response null", Toast.LENGTH_LONG).show();

                } else if (Response.equals("error")) {
                    Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getActivity(), "No Staff found", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            staff = new Staff();
                            String id = jsonObject.getString("Staffid");
                            if (id == null || id.equals("null")) {
                                break;
                            }
                            staff.setStaffId(Integer.parseInt(jsonObject.getString("Staffid")));
                            staff.setName(jsonObject.getString("Staffname"));
                            staff.setMobile(Long.parseLong(jsonObject.getString("Staffmobile")));
                            staff.setTotalSales(Long.parseLong(jsonObject.getString("Stafftotsale")));
                            staffArrayList.add(staff);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if (staffArrayList != null && staffArrayList.size() > 0) {
                        staffAdapter = new ViewStaffAdapter(getActivity(), staffArrayList);
                        lvViewStaff = (ListView) view.findViewById(R.id.lv_viewStaff);
                        lvViewStaff.setAdapter(staffAdapter);
                    } else
                        Toast.makeText(getActivity(), "No Staff found", Toast.LENGTH_LONG).show();

                }
            }
        }.execute();

    }

}
