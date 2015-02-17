package in.istore.bitblue.app.adminMenu.custInfo;

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
import in.istore.bitblue.app.adapters.ViewCustAdapter;
import in.istore.bitblue.app.databaseAdapter.DbCustAdapter;
import in.istore.bitblue.app.pojo.Customer;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.api.API;

public class ViewCust extends Fragment {
    private ListView lvViewCust;
    private ViewCustAdapter custAdapter;
    private ArrayList<Customer> custArrayList = new ArrayList<Customer>();
    private DbCustAdapter dbcustAdapter;
    private GlobalVariables globalVariable;

    private int StoreId;
    private String UserType, AdminKey;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Customer customer;

    public ViewCust() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_cust, container, false);
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        dbcustAdapter = new DbCustAdapter(getActivity());
        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();
        AdminKey = globalVariable.getAdminKey();
        // getAllCustomersPurchaseAmount(view, StoreId, Key);
       /* new AsyncTask<String, String, Boolean>() {
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
                custArrayList = dbcustAdapter.getAllCustomerPurchaseAmount();
                if (custArrayList != null && custArrayList.size() > 0)
                    return true;
                else return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
                    custAdapter = new ViewCustAdapter(getActivity(), custArrayList);
                    lvViewSupp = (ListView) view.findViewById(R.id.lv_viewCust);
                    lvViewSupp.setAdapter(custAdapter);
                }
            }
        }.execute();*/

    }

    private void getAllCustomersPurchaseAmount(final View view, final int StoreId, final String AdminKey) {
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
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_VIEW_ALLCUSTOMERS, nameValuePairs);
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
                            long custMobile = Long.parseLong(jsonObject.getString(""));
                            long custPurAmnt = Long.parseLong(jsonObject.getString(""));
                            if (custMobile == 0) {
                                break;
                            }
                            customer = new Customer();
                            customer.setMobile(custMobile);
                            customer.setPurchaseAmount(custPurAmnt);
                            custArrayList.add(customer);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (custArrayList != null && custArrayList.size() > 0) {
                        custAdapter = new ViewCustAdapter(getActivity(), custArrayList);
                        lvViewCust = (ListView) view.findViewById(R.id.lv_viewCust);
                        lvViewCust.setAdapter(custAdapter);
                    } else
                        Toast.makeText(getActivity(), "No Customers Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
