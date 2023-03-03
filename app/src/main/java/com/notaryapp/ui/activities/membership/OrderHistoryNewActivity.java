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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.notaryapp.R;
import com.notaryapp.adapter.OrdersAdapterNew;
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

public class OrderHistoryNewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    OrdersAdapterNew ordersAdapter = null;
    private String email;
    private TextView filtertype, sorttype, note;
    private Spinner filter, sort;
    private RecyclerView recyclerView;
    private DatabaseClient databaseClient;
    private Context mContext;
    private ListAdapter mlistAdapter;
    private List<OrderItems> orderItemsArray, orderItemsArray1;
    private OrderItems orderItem;
    private Button close;
    private LoadingCompletedInterface loadingCompletedInterface;
    private ShimmerFrameLayout mShimmerViewContainer;
    private int totalCount = 0;
    private boolean isLoading = false, succfullFetch = true;
    private boolean succfullFetchData = true;
    private int pageCount=0;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_history_new);
        init();

        mContext = getApplicationContext();
        //CustomDialog.showProgressDialog(OrderHistoryNewActivity.this);
        String[] sortArray = {"Latest", "Old"};
        String[] filterArray = {"All","Completed", "Pending", "Failed"};

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(OrderHistoryNewActivity.this, android.R.layout.simple_list_item_1, sortArray);
        sort.setAdapter(sortAdapter);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(OrderHistoryNewActivity.this, android.R.layout.simple_list_item_1, filterArray);
        filter.setAdapter(filterAdapter);

        sort.setOnItemSelectedListener(this);
        filter.setOnItemSelectedListener(this);

        //sort.setSelection(0);
        //filter.setSelection(0);


        //sorttype.setText("Filter");
        //filtertype.setText("Sort by");
        note.setVisibility(View.GONE);

        //Log.d("ORDER_HISTORY", "1st Execute");


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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (succfullFetchData && succfullFetch){
                    ordersAdapter.getFilter().filter(newText);
                }
                //ordersAdapter.getFilter().filter(newText);
                return false;
            }
        });


        /*filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtertype.setText(filter.getItemAtPosition(i).toString());
                Log.d("ORDER_HISTORY_S","FILTER");
                getHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (view == sort) {
                    sorttype.setText(sort.getItemAtPosition(i).toString());
                } else {
                    filtertype.setText(filter.getItemAtPosition(i).toString());
                }
                sorttype.setText(sort.getItemAtPosition(i).toString());
                Log.d("ORDER_HISTORY_S","SORT");
                getHistory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

    }




    private void init() {
        recyclerView = findViewById(R.id.recyclerHistory);

        filtertype = findViewById(R.id.filtertype);
        sorttype = findViewById(R.id.sorttype);
        filter = findViewById(R.id.filter);
        sort = findViewById(R.id.sort);
        close = findViewById(R.id.btn_pro_close);
        note = findViewById(R.id.note);
        searchView = findViewById(R.id.searchView);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        orderItemsArray = new ArrayList<>();
        orderItemsArray1 = new ArrayList<>();

        //mShimmerViewContainer.startShimmer();

        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        new SelectData().execute();

        initScrollListener();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        /*Log.d("ORDER_HISTORY", view.toString());
        Log.d("ORDER_HISTORY", sort.toString());*/

        /*if (view == sort) {
            sorttype.setText(sort.getItemAtPosition(i).toString());
            Log.d("ORDER_HISTORY", "SORT");
        } else {
            Log.d("ORDER_HISTORY", "FILTER");
            filtertype.setText(filter.getItemAtPosition(i).toString());
        }*/
        /*if (email != null && !email.equals("") && !email.equals("null")
                && !filtertype.getText().toString().equals("All") && !sorttype.getText().toString().equals("Latest")) {
            getHistory(sorttype.getText().toString(), filtertype.getText().toString());
            //Log.d("ORDER_HISTORY", "3rd Execute");
        }
        else{
            Log.d("ORDER_HISTORY", "4th Execute");
            getHistory("Latest", "");
        }
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        if(email != null && !email.equals("") && !email.equals("null")){
            getHistory();
        }*/
        if (succfullFetchData){
            mShimmerViewContainer.startShimmer();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getHistory();
            succfullFetchData = false;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*private void getHistory(String sort, String filter) {

        //CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("GET_PAYMENT_HISTORY")) {


                            if (orderItemsArray.size() > 0) {
                                orderItemsArray.clear();
                            }

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

                                ordersAdapter = new OrdersAdapterNew(loadingCompletedInterface, OrderHistoryNewActivity.this,orderItemsArray);
                                GridLayoutManager layoutManager = new GridLayoutManager(OrderHistoryNewActivity.this, 1, GridLayoutManager.VERTICAL, false);
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
                CustomDialog.notaryappDialogSingle(OrderHistoryNewActivity.this, Utils.errorMessage(OrderHistoryNewActivity.this));
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
            } else if(filter.toLowerCase().equalsIgnoreCase("")){
                //params.put("status", "");
            } else {
                params.put("status", filter.toLowerCase());
            }
            if (sort.equalsIgnoreCase("Latest")){
                params.put("sortOrder", "ASC");
            } else {
                params.put("sortOrder", "DESC");
            }


            postapiRequest.request(OrderHistoryNewActivity.this, iJsonListener, params, AppUrl.GET_PAYMENT_HISTORY, "GET_PAYMENT_HISTORY");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }*/

    private void getHistory() {

        //CustomDialog.showProgressDialog(this);
        note.setVisibility(View.GONE);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        Log.d("DATA_RECEIVE", data.toString());
                        if (type.equals("GET_PAYMENT_HISTORY")) {
                            //orderItemsArray = new ArrayList<>();
                            totalCount = data.getInt("total");
                            if (orderItemsArray.size() > 0){
                                orderItemsArray.clear();
                            }
                            //Log.d("ORDER_TOTAL_COUNT", String.valueOf(totalCount));

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
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                pageCount = 1;
                                succfullFetchData = true;
                                Log.d("PAGE_ADAPTER1", String.valueOf(pageCount));
                                ordersAdapter = new OrdersAdapterNew(loadingCompletedInterface, OrderHistoryNewActivity.this,orderItemsArray);
                                GridLayoutManager layoutManager = new GridLayoutManager(OrderHistoryNewActivity.this, 1, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(ordersAdapter);

                            }
                            else{

                                CustomDialog.cancelProgressDialog();
                                succfullFetchData = true;
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
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
                CustomDialog.notaryappDialogSingle(OrderHistoryNewActivity.this, Utils.errorMessage(OrderHistoryNewActivity.this));
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
            params.put("pageSize", "10");
            params.put("pageNo", "0");
            /*if (!filtertype.getText().toString().equalsIgnoreCase("All")){
                params.put("status", filtertype.getText().toString().toLowerCase());
            }
            if (sorttype.getText().toString().equalsIgnoreCase("Latest")){
                params.put("sortOrder", "ASC");
                //Log.d("ORDER_HISTORY_S","ASC");
            } else {
                //Log.d("ORDER_HISTORY_S","DESC");
                params.put("sortOrder", "DESC");
            }*/
            if (!filter.getSelectedItem().toString().equalsIgnoreCase("All")){
                params.put("status", filter.getSelectedItem().toString().toLowerCase());
                //params.put("sortOrder", "DESC");
            }
            if (sort.getSelectedItem().toString().equalsIgnoreCase("Latest")){
                params.put("sortOrder", "DESC");
                //Log.d("ORDER_HISTORY_S","ASC");
            } else {
                //Log.d("ORDER_HISTORY_S","DESC");
                params.put("sortOrder", "ASC");
            }




            postapiRequest.request(OrderHistoryNewActivity.this, iJsonListener, params, AppUrl.GET_PAYMENT_HISTORY, "GET_PAYMENT_HISTORY");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getHistory(int page_no) {


        orderItemsArray.add(null);
        ordersAdapter.notifyItemInserted(orderItemsArray.size()-1);
        Log.d("ORDER_ENTER", "ENTERED"+pageCount);
        //ordersAdapter.notifyDataSetChanged();

        //CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        Log.d("DATA_RECEIVE", data.toString());
                        if (type.equals("GET_PAYMENT_HISTORY")) {
                            orderItemsArray1 = new ArrayList<>();
                            //totalCount = data.getInt("total");
                            //Log.d("ORDER_TOTAL_COUNT", String.valueOf(totalCount));

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


                                orderItemsArray1.add(orderItem);
                            }

                            /*if(orderItemsArray.size() > 0){

                                note.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                mShimmerViewContainer.stopShimmer();
                                mShimmerViewContainer.setVisibility(View.GONE);
                                ordersAdapter = new OrdersAdapterNew(loadingCompletedInterface, OrderHistoryNewActivity.this,orderItemsArray);
                                GridLayoutManager layoutManager = new GridLayoutManager(OrderHistoryNewActivity.this, 1, GridLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(ordersAdapter);

                            }
                            else{

                                CustomDialog.cancelProgressDialog();
                                recyclerView.setVisibility(View.GONE);
                                note.setVisibility(View.VISIBLE);

                            }*/

                            orderItemsArray.remove(orderItemsArray.size() - 1);
                            int scrollPosition = orderItemsArray.size();
                            ordersAdapter.notifyItemRemoved(scrollPosition);

                            int j = 0;
                            while (j < orderItemsArray1.size()){
                                orderItemsArray.add(orderItemsArray1.get(j));
                                j++;
                            }

                            pageCount++;
                            succfullFetch = true;
                            ordersAdapter.notifyDataSetChanged();
                            isLoading = false;

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
                CustomDialog.notaryappDialogSingle(OrderHistoryNewActivity.this, Utils.errorMessage(OrderHistoryNewActivity.this));
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
            params.put("pageSize", "10");
            params.put("pageNo", String.valueOf(page_no));
            /*if (!filtertype.getText().toString().equalsIgnoreCase("All")){
                params.put("status", filtertype.getText().toString().toLowerCase());
            }
            if (sorttype.getText().toString().equalsIgnoreCase("Latest")){
                params.put("sortOrder", "ASC");
                //Log.d("ORDER_HISTORY_S","ASC");
            } else {
                //Log.d("ORDER_HISTORY_S","DESC");
                params.put("sortOrder", "DESC");
            }*/
            if (!filter.getSelectedItem().toString().equalsIgnoreCase("All")){
                params.put("status", filter.getSelectedItem().toString().toLowerCase());
                //params.put("sortOrder", "DESC");
            }
            if (sort.getSelectedItem().toString().equalsIgnoreCase("Latest")){
                params.put("sortOrder", "DESC");
                //Log.d("ORDER_HISTORY_S","ASC");
            } else {
                //Log.d("ORDER_HISTORY_S","DESC");
                params.put("sortOrder", "ASC");
            }




            postapiRequest.request(OrderHistoryNewActivity.this, iJsonListener, params, AppUrl.GET_PAYMENT_HISTORY, "GET_PAYMENT_HISTORY");

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
              //getHistory();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();



                    if (!isLoading){
                        Log.d("IS_LOADING","TRUE");
                        //Log.d("DIFF_TAG", String.valueOf(dashItemsListTabAll.size())+" : " + totalCount);

                        if (linearLayoutManager != null &&
                                linearLayoutManager.findLastCompletelyVisibleItemPosition() == orderItemsArray.size() -1 &&
                                orderItemsArray.size() < totalCount && succfullFetch == true){
                            //Toast.makeText(getActivity(), "LOAD..LOAD", Toast.LENGTH_SHORT).show();
                        /*if (LoadingStatus){
                            loadMore();
                            isLoading = true;
                        } else {
                            LoadingStatus = true;
                        }*/
                            succfullFetch = false;
                            loadMore();
                            isLoading = true;
                            Log.d("IS_LOADING","TRUE1");


                        }


                    }

                }catch (IndexOutOfBoundsException e){
                    Log.e("TAG", "Inconsistency detected");
                }


            }
        });
    }

    private void loadMore() {
        //dashItemsListTabAll.add(null);
        //dashboardItemAdapter.notifyDataSetChanged();
        //dashboardItemAdapter.notifyItemInserted(dashItemsListTabAll.size()-1);
        //Log.d("CRUSH_DATA", String.valueOf(dashItemsListTabAll.size()-1));

        //dashItemsListTabAll.remove(dashItemsListTabAll.size()-1);

        getHistory(pageCount);
        Log.d("PAGE_ADAPTER", String.valueOf(pageCount));

    }
}
