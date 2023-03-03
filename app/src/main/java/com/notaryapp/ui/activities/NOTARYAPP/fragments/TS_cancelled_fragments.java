package com.notaryapp.ui.activities.notaryapp.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.model.ReportItems;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.notaryapp.adapter.TSRecyclerViewAdapter1;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class TS_cancelled_fragments extends Fragment {

    private Context mContext;
    String email;
    private TextView sorttype, note, noRecord;
    private ListAdapter mlistAdapter;
    private Spinner sortSpinner, filterSpinner;
    private DatabaseClient databaseClient;
    private ReportItems reportItems, reportItems1;
    private List<ReportItems> listItemsArray, listItemsArray1, getListItemsArray_c, getListItemsArray_c1;
    int totalCount, pageCount=1, currentPageCount=1;
    String orderBy="", pageNumber="1", planBy="";
    boolean pageFlag = false, succfullFetch = false;
    int currentSize;

    boolean isLoading = false;
    boolean isVisible = false;
    boolean pData = true;
    private int j;

    RecyclerView recyclerView;

    TSRecyclerViewAdapter1 tsRecyclerViewAdapter1;

    ConstraintLayout constraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ts_cancelled_fragments, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();
        databaseClient = DatabaseClient.getInstance(mContext);

        new SelectData().execute();

        init();


    }

    private void populateData() {

        CustomDialog.showProgressDialog(getActivity());

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String Type) {
                try {


                    if (data != null){
                        Log.d("DATA_FETCH_CAN", data.toString());
                        pageCount = 1;

                        if (Type.equals("GET_TS_REPORT_CANCELLED")){
                            getListItemsArray_c = new ArrayList<>();
                            getListItemsArray_c1 = new ArrayList<>();

                            String tc = data.getString("total_results");
                            totalCount = Integer.parseInt(tc);

                            JSONArray jsonArray = data.getJSONArray("results");

                            for (int i = 0; i <jsonArray.length(); i++){
                                reportItems = new ReportItems();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject userDetailOBJ = jsonObject.getJSONObject("user_details");

                                String fName = userDetailOBJ.getString("first_name");
                                String lName = userDetailOBJ.getString("last_name");

                                reportItems.setUser_name(fName +" " + lName);
                                reportItems.setUser_email(userDetailOBJ.getString("email"));
                                reportItems.setUser_phone(userDetailOBJ.getString("phone"));

                                JSONObject propertyOBJ = jsonObject.getJSONObject("property_details");



                                String unitData = propertyOBJ.getString("unit");
                                String streetData = propertyOBJ.getString("street");
                                String cityData = propertyOBJ.getString("city");
                                String stateData = propertyOBJ.getString("state");
                                String zipData = propertyOBJ.getString("zip");

                                reportItems.setStreet(streetData);
                                reportItems.setUnit_no(unitData);
                                reportItems.setCity(cityData);
                                reportItems.setState(stateData);
                                reportItems.setZip(zipData);

                                if (unitData.equals("")){
                                    String fullAddress = streetData + " " + cityData + " " + stateData + " " + zipData;
                                    reportItems.setFull_address(fullAddress);
                                } else {
                                    String fullAddress = streetData + " " + unitData + " " + cityData + " " + stateData + " " + zipData;
                                    reportItems.setFull_address(fullAddress);
                                }

                                JSONObject planOBJ = jsonObject.getJSONObject("plan_details");

                                reportItems.setReport_id(planOBJ.getString("transaction_number"));
                                reportItems.setCreated_date(planOBJ.getString("created_datestr"));
                                reportItems.setPlan_title(planOBJ.getString("title"));
                                reportItems.setPlan_type(planOBJ.getString("type"));
                                reportItems.setPlan_status(planOBJ.getString("status"));
                                reportItems.setPlan_updated_on(planOBJ.getString("updated_on_datestr"));
                                reportItems.setPlan_start_date(planOBJ.getString("current_period_start_datestr"));
                                reportItems.setPlan_end_date(planOBJ.getString("current_period_end_datestr"));
                                reportItems.setPlan_price(planOBJ.getString("price"));



                                getListItemsArray_c.add(reportItems);
                                getListItemsArray_c1.add(reportItems);
                            }

                            tsRecyclerViewAdapter1 = new TSRecyclerViewAdapter1(getListItemsArray_c, mContext);
                            recyclerView.setAdapter(tsRecyclerViewAdapter1);


                            CustomDialog.cancelProgressDialog();



                        }

                        if (totalCount==0){
                            noRecord.setVisibility(View.VISIBLE);
                        } else {
                            noRecord.setVisibility(View.GONE);
                        }

                    }
                } catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {

            }
        };

        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("email", email);
            params.put("status", "inactive");
            params.put("sortby","updated_on");
            params.put("order", orderBy);
            if (!planBy.equalsIgnoreCase("All")){
                params.put("plan_name", planBy);
            }
            params.put("page", "1");
            params.put("pagesize","10");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timeSt = String.valueOf(timestamp.getTime());
            String URL_TS_CAN = AppUrl.GET_TS_TRANS_DETAILS+"?ID="+timeSt;

            postapiRequest.request(mContext, iJsonListener, params, URL_TS_CAN, "GET_TS_REPORT_CANCELLED");
            Log.d("POST_HIT_CAN", params.toString());
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void init() {

        sorttype = (TextView) requireActivity().findViewById(R.id.sort);
        sortSpinner = (Spinner) requireActivity().findViewById(R.id.sorticon);
        filterSpinner = (Spinner)requireActivity().findViewById(R.id.filter);
        recyclerView = (RecyclerView) requireActivity().findViewById(R.id.recyclerViewts1);
        noRecord = (TextView) requireActivity().findViewById(R.id.noRecord1);

        //orderBy="DESC";
        //planBy = "All";



    }




    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !isVisible) {
            Log.e("~~onResume: ", "::onLatestResume");
            //your code
        }
        isVisible = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        if (isVisibleToUser && isVisible) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //your code
                    databaseClient = DatabaseClient.getInstance(mContext);
                    new SelectData().execute();
                    init();
                    getFilterData();
                    //populateData();
                }
            }, 1000);

        }
    }

    private void getFilterData() {

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, "TEST DATA", Toast.LENGTH_SHORT).show();
                Spinner spin = (Spinner)parent;
                Spinner spin2 = (Spinner)parent;

                if (spin.getId() == R.id.sorticon)
                {
                    if (sortSpinner.getItemAtPosition(position).toString().equals("Latest")){
                        orderBy="DESC";
                    }
                    else {
                        orderBy="ASC";
                    }
                } else if (spin2.getId() == R.id.filter){
                    if (filterSpinner.getItemAtPosition(position).toString().equals("All")){
                        planBy="all";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("Monthly")){
                        planBy="monthly";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("Yearly")){
                        planBy="yearly";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("3 Years")){
                        planBy="threeyr";
                    } else{
                        planBy="lifetime";
                    }
                }

                populateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(mContext, "TEST DATA", Toast.LENGTH_SHORT).show();
                Spinner spin = (Spinner)parent;
                Spinner spin2 = (Spinner)parent;

                if (spin.getId() == R.id.sorticon)
                {
                    if (sortSpinner.getItemAtPosition(position).toString().equals("Latest")){
                        orderBy="DESC";
                    }
                    else {
                        orderBy="ASC";
                    }
                } else if (spin2.getId() == R.id.filter){
                    if (filterSpinner.getItemAtPosition(position).toString().equals("All")){
                        planBy="all";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("Monthly")){
                        planBy="monthly";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("Yearly")){
                        planBy="yearly";
                    } else if (filterSpinner.getItemAtPosition(position).toString().equals("3 Years")){
                        planBy="threeyr";
                    } else{
                        planBy="lifetime";
                    }
                }

                populateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int position1 = filterSpinner.getSelectedItemPosition();


        if (filterSpinner.getItemAtPosition(position1).toString().equals("All")){
            planBy="all";
        } else if (filterSpinner.getItemAtPosition(position1).toString().equals("Monthly")){
            planBy="monthly";
        } else if (filterSpinner.getItemAtPosition(position1).toString().equals("Yearly")){
            planBy="yearly";
        } else if (filterSpinner.getItemAtPosition(position1).toString().equals("3 Years")){
            planBy="threeyr";
        } else{
            planBy="lifetime";
        }

        int position2 = sortSpinner.getSelectedItemPosition();

        if (sortSpinner.getItemAtPosition(position2).toString().equals("Latest")){
            orderBy="DESC";
        }
        else {
            orderBy="ASC";
        }

        populateData();
        initScrollListener();


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

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading){
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == getListItemsArray_c.size() - 1){
                        loadMore();
                        isLoading = true;
                    }
                }
            }

        });
    }

    private void loadMore(){
        getListItemsArray_c.add(null);
        tsRecyclerViewAdapter1.notifyItemInserted(getListItemsArray_c.size() -1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getListItemsArray_c.remove(getListItemsArray_c.size()-1);
                int scrollPosition = getListItemsArray_c.size();
                tsRecyclerViewAdapter1.notifyItemRemoved(scrollPosition);
                currentSize = scrollPosition;
                //int nextLimit = currentSize + 10;

                /*if (currentSize < totalCount){
                    populateData("2");
                }*/

                if (currentSize < totalCount && pData == true){
                    pageCount++;
                    pageNumber = Integer.toString(pageCount);
                    populateData(pageNumber);
                    pData =false;
                }

                j=0;
                //while (currentSize - 1 < nextLimit)
                while (j < getListItemsArray_c1.size() && succfullFetch == true){



                    reportItems = new ReportItems();

                    ReportItems reportItems2 = getListItemsArray_c1.get(j);


                    reportItems.setReport_id(reportItems2.getReport_id());
                    reportItems.setUser_name(reportItems2.getUser_name());
                    reportItems.setUser_email(reportItems2.getUser_email());
                    reportItems.setUser_phone(reportItems2.getUser_phone());

                    reportItems.setStreet(reportItems2.getStreet());
                    reportItems.setUnit_no(reportItems2.getUnit_no());
                    reportItems.setCity(reportItems2.getCity());
                    reportItems.setState(reportItems2.getState());
                    reportItems.setZip(reportItems2.getZip());

                    reportItems.setFull_address(reportItems2.getFull_address());

                    reportItems.setCreated_date(reportItems2.getCreated_date());

                    reportItems.setPlan_title(reportItems2.getPlan_title());
                    reportItems.setPlan_type(reportItems2.getPlan_type());
                    reportItems.setPlan_status(reportItems2.getPlan_status());
                    reportItems.setPlan_updated_on(reportItems2.getPlan_updated_on());
                    reportItems.setPlan_start_date(reportItems2.getPlan_start_date());
                    reportItems.setPlan_end_date(reportItems2.getPlan_end_date());
                    reportItems.setPlan_price(reportItems2.getPlan_price());





                    getListItemsArray_c.add(reportItems);
                    currentSize++;
                    j++;
                    pData = true;


                }

                succfullFetch = false;


                tsRecyclerViewAdapter1.notifyDataSetChanged();
                isLoading=false;
            }
        }, 2000);
    }

    private void populateData(String pageNumber1) {

        //CustomDialog.showProgressDialog(getActivity());

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String Type) {
                try {


                    if (data != null){

                        if (Type.equals("GET_TS_REPORT")){
                            //listItemsArray = new ArrayList<>();
                            getListItemsArray_c1 = new ArrayList<>();

                            String tc = data.getString("total_results");
                            totalCount = Integer.parseInt(tc);

                            JSONArray jsonArray = data.getJSONArray("results");

                            for (int i = 0; i <jsonArray.length(); i++){
                                //currentSize++;
                                reportItems = new ReportItems();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                JSONObject userDetailOBJ = jsonObject.getJSONObject("user_details");

                                String fName = userDetailOBJ.getString("first_name");
                                String lName = userDetailOBJ.getString("last_name");

                                reportItems.setUser_name(fName +" " + lName);
                                reportItems.setUser_email(userDetailOBJ.getString("email"));
                                reportItems.setUser_phone(userDetailOBJ.getString("phone"));

                                JSONObject propertyOBJ = jsonObject.getJSONObject("property_details");



                                String unitData = propertyOBJ.getString("unit");
                                String streetData = propertyOBJ.getString("street");
                                String cityData = propertyOBJ.getString("city");
                                String stateData = propertyOBJ.getString("state");
                                String zipData = propertyOBJ.getString("zip");

                                reportItems.setStreet(streetData);
                                reportItems.setUnit_no(unitData);
                                reportItems.setCity(cityData);
                                reportItems.setState(stateData);
                                reportItems.setZip(zipData);

                                if (unitData.equals("")){
                                    String fullAddress = streetData + " " + cityData + " " + stateData + " " + zipData;
                                    reportItems.setFull_address(fullAddress);
                                } else {
                                    String fullAddress = stateData + " " + unitData + " " + cityData + " " + stateData + " " + zipData;
                                    reportItems.setFull_address(fullAddress);
                                }

                                JSONObject planOBJ = jsonObject.getJSONObject("plan_details");

                                reportItems.setReport_id(planOBJ.getString("transaction_number"));
                                reportItems.setCreated_date(planOBJ.getString("created_datestr"));
                                reportItems.setPlan_title(planOBJ.getString("title"));
                                reportItems.setPlan_type(planOBJ.getString("type"));
                                reportItems.setPlan_status(planOBJ.getString("status"));
                                reportItems.setPlan_updated_on(planOBJ.getString("updated_on_datestr"));
                                reportItems.setPlan_start_date(planOBJ.getString("current_period_start_datestr"));
                                reportItems.setPlan_end_date(planOBJ.getString("current_period_end_datestr"));
                                reportItems.setPlan_price(planOBJ.getString("price"));



                                //listItemsArray.add(reportItems);
                                getListItemsArray_c1.add(reportItems);
                            }
                            succfullFetch = true;

                            /*tsRecyclerViewAdapter.notifyDataSetChanged();
                            isLoading=false;*/


                            //tsRecyclerViewAdapter = new TSRecyclerViewAdapter(listItemsArray, mContext);
                            //recyclerView.setAdapter(tsRecyclerViewAdapter);
                            //tsRecyclerViewAdapter.notifyDataSetChanged();


                            //CustomDialog.cancelProgressDialog();



                        }

                    }
                } catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {

            }
        };

        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("email", email);
            params.put("status", "inactive");
            params.put("sortby","updated_on");
            params.put("order", orderBy);
            if (!planBy.equalsIgnoreCase("All")){
                params.put("plan_name", planBy);
            }
            params.put("page", pageNumber1);
            params.put("pagesize","10");


            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timeSt = String.valueOf(timestamp.getTime());
            String URL_TS_CAN = AppUrl.GET_TS_TRANS_DETAILS+"?ID="+timeSt;

            postapiRequest.request(mContext, iJsonListener, params, URL_TS_CAN, "GET_TS_REPORT");


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
            //populateData();
            //initAdapter();
        }

    }



}