package in.istore.bitblue.app.Stocks.listStock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.istore.bitblue.app.R;
import in.istore.bitblue.app.adapters.CategoryListAdapter;
import in.istore.bitblue.app.pojo.Category;
import in.istore.bitblue.app.utilities.GlobalVariables;
import in.istore.bitblue.app.utilities.JSONParser;
import in.istore.bitblue.app.utilities.API;

public class CategoryList extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolTitle;
    private ListView lvcategorylist;

    private ArrayList<Category> categoryArrayList = new ArrayList<Category>();
    private GlobalVariables globalVariable;
    private CategoryListAdapter categorylistAdapter;

    private JSONParser jsonParser = new JSONParser();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayList<NameValuePair> nameValuePairs;
    private Category category;

    private int StoreId;
    private String Key, UserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        setToolbar();
        initViews();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolTitle.setText("SELECT CATEGORY");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        globalVariable = (GlobalVariables) getApplicationContext();
        lvcategorylist = (ListView) findViewById(R.id.lv_category);
        StoreId = globalVariable.getStoreId();
        UserType = globalVariable.getUserType();
        if (UserType.equals("Admin")) {
            Key = globalVariable.getAdminKey();
        } else if (UserType.equals("Staff")) {
            Key = globalVariable.getStaffKey();
        }
        getAllCategories(StoreId, Key);
    }

    public void getAllCategories(final int StoreId, final String Key) {
        new AsyncTask<String, String, String>() {
            ProgressDialog dialog = new ProgressDialog(CategoryList.this);

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Getting Categories For Your Store");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("StoreId", String.valueOf(StoreId)));
                nameValuePairs.add(new BasicNameValuePair("key", Key));

                String Response = jsonParser.makeHttpPostRequest(API.BITSTORE_GET_ALL_CATEGORIES, nameValuePairs);
                if (Response == null || Response.equals("error")) {
                    return Response;
                } else {
                    try {
                        jsonArray = new JSONArray(Response);
                        return Response;
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
                    Toast.makeText(getApplicationContext(), "Response null", Toast.LENGTH_LONG).show();
                } else if (Response.equals("error")) {
                    Toast.makeText(getApplicationContext(), "Error 500", Toast.LENGTH_LONG).show();
                } else if (jsonArray == null) {
                    Toast.makeText(getApplicationContext(), "No Categories Found", Toast.LENGTH_LONG).show();
                }
                // categoryArrayList =getAllCategories(StoreId);
                // categoryAdapter = new CategoryAdapter(getApplicationContext(), categoryArrayList);
                // lvcategories.setAdapter(categoryAdapter);
                else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            String categoryName = jsonObject.getString("CategoryName");
                            if (categoryName == null || categoryName.equals("null")) {
                                break;
                            }
                            category = new Category();
                            category.setCategoryName(categoryName);
                            categoryArrayList.add(category);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (categoryArrayList != null && categoryArrayList.size() > 0) {
                        categorylistAdapter = new CategoryListAdapter(getApplicationContext(), categoryArrayList);
                        lvcategorylist = (ListView) findViewById(R.id.lv_categorylist);
                        lvcategorylist.setAdapter(categorylistAdapter);
                    } else {
                    }
                }
            }
        }.execute();
    }
}
