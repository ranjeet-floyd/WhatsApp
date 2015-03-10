package in.istore.bitblue.app.staffMenu.custInfo;

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
import in.istore.bitblue.app.adapters.ViewCustForStaffAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustPurHistAdapter;
import in.istore.bitblue.app.pojo.SoldProduct;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.API;

public class ViewCustForStaff extends Fragment {
    private ListView lvViewCust;

    private ViewCustForStaffAdapter custAdapter;
    private ArrayList<SoldProduct> soldprodArrayList = new ArrayList<SoldProduct>();
    private DbCustPurHistAdapter dbcustpurhistAdapter;
    private GlobalVariables globalVariable;

    private long StaffId;
    private int StoreId;
    private String UserType, StaffKey;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private SoldProduct soldProduct;

    public ViewCustForStaff() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_cust_for_staff, container, false);
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();

        //dbcustpurhistAdapter = new DbCustPurHistAdapter(getActivity());
        StoreId = globalVariable.getStoreId();
        StaffId = globalVariable.getStaffId();
        StaffKey = globalVariable.getStaffKey();

        getCustomerPurchaseAmountForStaff(view, StoreId, StaffId, StaffKey);
/*
        new AsyncTask<String, String, Boolean>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Please wait...");
                dialog.show();
            }

            @Override
            protected Boolean doInBackground(String... strings) {
                soldprodArrayList = dbcustpurhistAdapter.getCustomerInfoForStaffId(StaffId);
                if (soldprodArrayList != null && soldprodArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
                    custAdapter = new ViewCustForStaffAdapter(getActivity(), soldprodArrayList);
                    lvViewCust = (ListView) view.findViewById(R.id.lv_viewCustForStaff);
                    lvViewCust.setAdapter(custAdapter);
                }
            }
        }.execute();
*/
    }

    private void getCustomerPurchaseAmountForStaff(final View view, final int StoreId, final long StaffId, final String Key) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Customers Details...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("StaffId", String.valueOf(StaffId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));

                String Response = jsonParser.makeHttpUrlConnectionRequest(API.BITSTORE_VIEW_STAFF_CUSTOMERS, nameValuePairs);
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
                if (Response == null) {
                    Toast.makeText(getActivity(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getActivity(), "No Customers", Toast.LENGTH_LONG).show();
                }
                // categoryArrayList =getAllCategories(StoreId);
                // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                // lvcategories.setAdapter(categoryAdapter);
                else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            long custMobile = Long.parseLong(jsonObject.getString("Cusmobile"));
                            String custPurAmnt = jsonObject.getString("Custtotalprice");
                            if (custMobile == 0) {
                                break;
                            }
                            soldProduct = new SoldProduct();
                            soldProduct.setMobile(custMobile);
                            soldProduct.setItemTotalAmnt(Float.parseFloat(custPurAmnt));
                            soldprodArrayList.add(soldProduct);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (soldprodArrayList != null && soldprodArrayList.size() > 0) {
                        custAdapter = new ViewCustForStaffAdapter(getActivity(), soldprodArrayList);
                        lvViewCust = (ListView) view.findViewById(R.id.lv_viewCustForStaff);
                        lvViewCust.setAdapter(custAdapter);
                    } else
                        Toast.makeText(getActivity(), "No Customers Available", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
