package com.notaryapp.ui.activities.membership;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.adapter.OrdersAdapter;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.OrderItems;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    OrdersAdapter ordersAdapter = null;
    private String email;
    private TextView filtertype, sorttype, note;
    private Spinner filter, sort;
    private RecyclerView recyclerView;
    private DatabaseClient databaseClient;
    private Context mContext;
    private ListAdapter mlistAdapter;
    private List<OrderItems> orderItemsArray;
    private OrderItems orderItem;
    private Button close;
    private LoadingCompletedInterface loadingCompletedInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_history);
        init();

        mContext = getApplicationContext();
        CustomDialog.showProgressDialog(OrderHistoryActivity.this);
        String[] sortArray = {"Ascending", "Descending"};
        String[] filterArray = {"Sort by","Completed", "Pending", "Failed"};

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, sortArray);
        sort.setAdapter(sortAdapter);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(OrderHistoryActivity.this, android.R.layout.simple_list_item_1, filterArray);
        filter.setAdapter(filterAdapter);

        sort.setOnItemSelectedListener(this);
        filter.setOnItemSelectedListener(this);

        sort.setSelection(0);
        filter.setSelection(0);

        sorttype.setText("Filter");
        filtertype.setText("Sort by");
        note.setVisibility(View.GONE);


        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {

                CustomDialog.cancelProgressDialog();
            }
        };


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

    }




    private void init() {
        recyclerView = findViewById(R.id.recyclerHistory);

        filtertype = findViewById(R.id.filtertype);
        sorttype = findViewById(R.id.sorttype);
        filter = findViewById(R.id.filter);
        sort = findViewById(R.id.sort);
        close = findViewById(R.id.btn_pro_close);
        note = findViewById(R.id.note);

        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        new SelectData().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (view == sort) {
            sorttype.setText(sort.getItemAtPosition(0).toString());
        } else {
            filtertype.setText(filter.getItemAtPosition(i).toString());
        }
        if (email != null && !email.equals("") && !email.equals("null") && !filtertype.getText().toString().equals("Sort by")) {
            getHistory(sorttype.getText().toString(), filtertype.getText().toString());
        }
        else{
            getHistory();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getHistory(String sort, String filter) {

        CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("GET_PAYMENT_HISTORY")) {
                            orderItemsArray = new ArrayList<>();

                            JSONArray jsonArray = data.getJSONArray("transactions");
                            for(int i=0;i<jsonArray.length(); i++){
                                orderItem = new OrderItems();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                if(jsonObject.getString("objecttype").equals("package")){

                                    JSONObject dpriceOBJ = jsonObject.getJSONObject("product");

                                    orderItem.setEmail(jsonObject.getString("email"));
                                    orderItem.setId(jsonObject.getString("id"));
                                    orderItem.setObjecttype(jsonObject.getString("objecttype"));
                                    orderItem.setPrice(dpriceOBJ.getString("dprice"));
                                    orderItem.setCreated_on(jsonObject.getString("created_on"));
                                    orderItem.setUnit_purchased(jsonObject.getString("unit_purchased"));
                                    orderItem.setUpdated_on(jsonObject.getString("updated_on"));
                                    orderItem.setStripeobjectid(jsonObject.getString("stripeobjectid"));
                                    orderItem.setStatus(jsonObject.getString("status"));
                                    orderItem.setReceiptpdf(jsonObject.getString("receiptpdf"));
                                    orderItem.setReceipturl(jsonObject.getString("receipturl"));
                                    //orderItem.setPlanname(dpriceOBJ.getString("vl_plan_name"));

                                }
                                else{

                                    JSONObject dpriceOBJ = jsonObject.getJSONObject("product");

                                    orderItem.setEmail(jsonObject.getString("email"));
                                    orderItem.setId(jsonObject.getString("id"));
                                    orderItem.setObjecttype(jsonObject.getString("objecttype"));
                                    orderItem.setPrice(dpriceOBJ.getString("dprice"));
                                    orderItem.setCreated_on(jsonObject.getString("created_on"));
                                    orderItem.setUnit_purchased(jsonObject.getString("unit_purchased"));
                                    orderItem.setUpdated_on(jsonObject.getString("updated_on"));
                                    orderItem.setStripeobjectid(jsonObject.getString("stripeobjectid"));
                                    orderItem.setStatus(jsonObject.getString("status"));
                                    orderItem.setReceiptpdf(jsonObject.getString("receiptpdf"));
                                    orderItem.setReceipturl(jsonObject.getString("receipturl"));
                                    orderItem.setPlanname(dpriceOBJ.getString("vl_plan_name"));




                                }


                                orderItemsArray.add(orderItem);
                            }

                            if(orderItemsArray.size() > 0){

                                note.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                ordersAdapter = new OrdersAdapter(loadingCompletedInterface,OrderHistoryActivity.this,orderItemsArray);
                                GridLayoutManager layoutManager = new GridLayoutManager(OrderHistoryActivity.this, 1, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(ordersAdapter);

                            }
                            else{

                                CustomDialog.cancelProgressDialog();
                                recyclerView.setVisibility(View.GONE);
                                note.setVisibility(View.VISIBLE);

                            }

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //   CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(OrderHistoryActivity.this, Utils.errorMessage(OrderHistoryActivity.this));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };


        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("email", email);
            if (filter.toLowerCase().equalsIgnoreCase("Failed")){
                params.put("status", "canceled");
            } else {
                params.put("status", filter.toLowerCase());
            }


            postapiRequest.request(OrderHistoryActivity.this, iJsonListener, params, AppUrl.GET_PAYMENT_HISTORY, "GET_PAYMENT_HISTORY");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getHistory() {

        CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        Log.d("DATA_RECEIVE", data.toString());
                        if (type.equals("GET_PAYMENT_HISTORY")) {
                            orderItemsArray = new ArrayList<>();

                            JSONArray jsonArray = data.getJSONArray("transactions");
                            for(int i=0;i<jsonArray.length(); i++){
                                orderItem = new OrderItems();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);


                                if(jsonObject.getString("objecttype").equals("package")){

                                    JSONObject dpriceOBJ = jsonObject.getJSONObject("product");

                                    orderItem.setEmail(jsonObject.getString("email"));
                                    orderItem.setId(jsonObject.getString("id"));
                                    orderItem.setObjecttype(jsonObject.getString("objecttype"));
                                    orderItem.setPrice(dpriceOBJ.getString("dprice"));
                                    orderItem.setCreated_on(jsonObject.getString("created_on"));
                                    orderItem.setUnit_purchased(jsonObject.getString("unit_purchased"));
                                    orderItem.setUpdated_on(jsonObject.getString("updated_on"));
                                    orderItem.setStripeobjectid(jsonObject.getString("stripeobjectid"));
                                    orderItem.setStatus(jsonObject.getString("status"));
                                    orderItem.setReceiptpdf(jsonObject.getString("receiptpdf"));
                                    orderItem.setReceipturl(jsonObject.getString("receipturl"));
                                    //orderItem.setPlanname(dpriceOBJ.getString("vl_plan_name"));

                                }
                                else{

                                    JSONObject dpriceOBJ = jsonObject.getJSONObject("product");

                                    Log.d("PLAN_NAME", dpriceOBJ.getString("vl_plan_name") +" "+ dpriceOBJ.getString("dprice"));

                                    orderItem.setEmail(jsonObject.getString("email"));
                                    orderItem.setId(jsonObject.getString("id"));
                                    orderItem.setObjecttype(jsonObject.getString("objecttype"));
                                    orderItem.setPrice(dpriceOBJ.getString("dprice"));
                                    orderItem.setCreated_on(jsonObject.getString("created_on"));
                                    orderItem.setUnit_purchased(jsonObject.getString("unit_purchased"));
                                    orderItem.setUpdated_on(jsonObject.getString("updated_on"));
                                    orderItem.setStripeobjectid(jsonObject.getString("stripeobjectid"));
                                    orderItem.setStatus(jsonObject.getString("status"));
                                    orderItem.setReceiptpdf(jsonObject.getString("receiptpdf"));
                                    orderItem.setReceipturl(jsonObject.getString("receipturl"));
                                    orderItem.setPlanname(dpriceOBJ.getString("vl_plan_name"));


                                }


                                orderItemsArray.add(orderItem);
                            }

                            if(orderItemsArray.size() > 0){

                                note.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                ordersAdapter = new OrdersAdapter(loadingCompletedInterface,OrderHistoryActivity.this,orderItemsArray);
                                GridLayoutManager layoutManager = new GridLayoutManager(OrderHistoryActivity.this, 1, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(ordersAdapter);

                            }
                            else{

                                CustomDialog.cancelProgressDialog();
                                recyclerView.setVisibility(View.GONE);
                                note.setVisibility(View.VISIBLE);

                            }

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //   CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(OrderHistoryActivity.this, Utils.errorMessage(OrderHistoryActivity.this));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };


        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("email", email);

            postapiRequest.request(OrderHistoryActivity.this, iJsonListener, params, AppUrl.GET_PAYMENT_HISTORY, "GET_PAYMENT_HISTORY");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            email = databaseClient.getAppDatabase().userRegDao().getEmail();

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

//            getHistory(sorttype.getText().toString(), filtertype.getText().toString());
            getHistory();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
