package in.istore.bitblue.app.adminMenu.suppInfo;

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
import in.istore.bitblue.app.adapters.ViewSuppAdapter;
import in.istore.bitblue.app.databaseAdapter.DbSuppAdapter;
import in.istore.bitblue.app.pojo.Supplier;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.API;

public class ViewSupplier extends Fragment {
    private ListView lvViewSupp;
    private ViewSuppAdapter suppAdapter;
    private ArrayList<Supplier> suppArrayList = new ArrayList<Supplier>();
    private DbSuppAdapter dbsuppAdapter;
    private GlobalVariables globalVariable;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Supplier supplier;
    private int StoreId;
    private String AdminKey;

    public ViewSupplier() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_supplier, container, false);
        globalVariable = (GlobalVariables) getActivity().getApplicationContext();
        StoreId = globalVariable.getStoreId();
        AdminKey = globalVariable.getAdminKey();
        if (suppArrayList.size() > 0) {
            suppArrayList.clear();
        }
        initViews(view);
        return view;
    }

    private void initViews(final View view) {
        dbsuppAdapter = new DbSuppAdapter(getActivity());
        viewSupplierForThisStore(view);

    }

    private void viewSupplierForThisStore(final View view) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("Getting Suppliers...");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                /*suppArrayList = dbsuppAdapter.getAllSuppInfo();                             ///remove if using api
                 if (suppArrayList != null && suppArrayList.size() > 0)
                    return true;
                else return false;*/
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("Storeid", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", AdminKey));
                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_SUPPLIER_INFO, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                    } catch (JSONException jException) {
                        jException.printStackTrace();
                    }
                }
                return Response;//
            }

            @Override
            protected void onPostExecute(String Response) {
                dialog.dismiss();
               /* if (result) {
                    suppAdapter = new ViewSuppAdapter(getActivity(), suppArrayList);             //remove if using api
                    lvViewSupp = (ListView) view.findViewById(R.id.lv_viewSupp);
                    lvViewSupp.setAdapter(suppAdapter);                            //
                }*/
                if (Response == null) {                                          //
                    Toast.makeText(getActivity(), "Response null", Toast.LENGTH_LONG).show();

                } else if (Response.equals("error")) {
                    Toast.makeText(getActivity(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getActivity(), "No Supplier found", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            supplier = new Supplier();
                            String id = jsonObject.getString("Suppname");
                            if (id == null || id.equals("null")) {
                                break;
                            }
                            supplier.setName(jsonObject.getString("Suppname"));
                            supplier.setMobile(Long.parseLong(jsonObject.getString("Suppmobile")));
                            suppArrayList.add(supplier);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (suppArrayList != null && suppArrayList.size() > 0) {
                        suppAdapter = new ViewSuppAdapter(getActivity(), suppArrayList);
                        lvViewSupp = (ListView) view.findViewById(R.id.lv_viewSupp);
                        lvViewSupp.setAdapter(suppAdapter);
                    } else
                        Toast.makeText(getActivity(), "No Supplier Available", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
