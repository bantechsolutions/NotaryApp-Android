package com.notaryapp.lockadoc.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notaryapp.R;
import com.notaryapp.adapter.DashboardItemAdapterTabLADNew;
import com.notaryapp.interfacelisterners.IFragmentListener;
import com.notaryapp.interfacelisterners.ISearch;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.interfacelisterners.PopulateDashboardListener;
import com.notaryapp.model.webresponse.Body;
import com.notaryapp.model.webresponse.TransByPage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AllTransactions;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.utilsretrofit.GetDataService;
import com.notaryapp.utilsretrofit.RetrofitClientInstance;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard_TabLADFragment extends Fragment implements ISearch, PopulateDashboardListener {

    private View view;
    private RecyclerView recyclerView;
    private List<AllTransactions> dashItemsList;
    private List<Body> dashItemsListTabLad;
    private int tabColor;
    private DatabaseClient databaseClient;
    private static final String ARG_SEARCHTERM = "search_term";
    private String mSearchTerm = null;
    private ArrayList<String> strings = null;
    private IFragmentListener mIFragmentListener = null;
    private DashboardItemAdapterTabLADNew dashboardItemAdapter = null;
    private Activity activity;
    private LoadingCompletedInterface loadingCompletedInterface;
    private ShimmerFrameLayout mShimmerViewContainer;
    private TransByPage transByPage;
    private List<Body> dashItemsValidateList;
    private int pageSize = 5;

    private ConstraintLayout paginationView;
    private int totalSize = 0;
    private int totalPages = 0;
    private int currentpage = 0;

    private TextView searchInfo, searchClear, sortView, prev, reset, next;
    private List<Body> dashItemsAllListSearchList;
    private DashBoardActivity dashBoardActivity;
    public Dashboard_TabLADFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard_tab_validate, container, false);
        activity = getActivity();
        init();
        //setupRecycler();
        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {

                if(complted)
                    CustomDialog.cancelProgressDialog();
                else{
                    CustomDialog.showProgressDialog(getActivity());
                }

            }
        };

        return view;
    }

    public void searchSort(String searchKey){

        new SearchDB().execute(searchKey);
    }

    public void sort(String type) {

        if (type.equalsIgnoreCase("Latest")){
            new SelectVAAll().execute();
        }
        else{

            new Sort().execute();
        }

    }


    private void setupRecycler(List<Body> dashItemsList) {

        try {
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        dashItemsListTabLad.clear();
                        for (int i=0; i < dashItemsList.size(); i++){
                            dashItemsListTabLad.add(dashItemsList.get(i));
                        }
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        dashboardItemAdapter = new DashboardItemAdapterTabLADNew(loadingCompletedInterface, dashItemsListTabLad, getActivity());
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);

                        // lahar modify
                        recyclerView.getRecycledViewPool().clear();
                        dashboardItemAdapter.notifyDataSetChanged();

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(dashboardItemAdapter);



                        Log.d("LOCKADOC TAB","LIST UPDATED");

                    }
                });
            }
        }
        catch (Exception e){
            Log.d("LAD TAB", "setupRecycler: "+ e.toString());
        }

    }
    public void startShimmer(){
        clearRecycler();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
    }
    private void clearRecycler() {

        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dashboardItemAdapter = new DashboardItemAdapterTabLADNew(loadingCompletedInterface,
                            new ArrayList<Body>(), getActivity());
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                            1,
                            GridLayoutManager.VERTICAL,
                            false);

                    // lahar modify
                    recyclerView.getRecycledViewPool().clear();
                    dashboardItemAdapter.notifyDataSetChanged();


                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(dashboardItemAdapter);



                }
            });
        }
        catch (Exception e){
            Log.d("LAD TAB","ClearRecycler "+ e.toString());
        }


    }

    private void init() {
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        databaseClient = DatabaseClient.getInstance(getActivity());
        recyclerView = view.findViewById(R.id.recyclerviewVal);
        dashItemsList = new ArrayList<>();
        dashItemsValidateList = new ArrayList<>();
        dashItemsListTabLad = new ArrayList<>();
        dashItemsAllListSearchList = new ArrayList<>();
        new Dashboard_TabLADFragment.SelectVAAll().execute();

        sortView = view.findViewById(R.id.sort);
        prev = view.findViewById(R.id.prev);
        reset = view.findViewById(R.id.reset);
        next = view.findViewById(R.id.next);
        paginationView = view.findViewById(R.id.paginationView);

        if (Utils.isConnected()) {
            try {
                getAllTransactions(0);
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity()
                    , getActivity().getResources().getString(R.string.networkerror)
                    , Toast.LENGTH_LONG).show();
        }

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected()) {
                    if (currentpage > 0) {
                        currentpage = currentpage - 1;
                        startShimmer();
                        try {
                            getAllTransactions(currentpage);
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }

                    } else {
                        CustomDialog.notaryappDialogSingle(activity, "First Page");
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected()) {
                    currentpage = 0;
                    startShimmer();
                    try {
                        getAllTransactions(currentpage);
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected()) {
                    if (currentpage < totalPages && currentpage >= 0) {
                        currentpage = currentpage + 1;
                        startShimmer();
                        try {
                            getAllTransactions(currentpage);
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }

                    } else {
                        CustomDialog.notaryappDialogSingle(activity, "You have reached the end of the Page");
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=mSearchTerm) {
            onTextQuery(mSearchTerm);
        }
    }

    @Override
    public void onTextQuery(String text) {
        dashboardItemAdapter.getFilter().filter(text);
        dashboardItemAdapter.notifyDataSetChanged();
    }

    public static Dashboard_TabLADFragment newInstance(String searchTerm){
        Dashboard_TabLADFragment fragment = new Dashboard_TabLADFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SEARCHTERM, searchTerm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onReceiveItemList(String type,String searchStr, String typeStr, String orderStr) {
        if(type.equalsIgnoreCase("search")) {
            /*if (dashItemsAllListSearchList != null
                    && dashItemsAllListSearchList.size() > 0) {
                dashItemsAllListSearchList.clear();
            }*/
            if(dashItemsValidateList != null) {
                if (dashItemsAllListSearchList.size()>0){
                    dashItemsAllListSearchList.clear();
                }
                for (int i = 0; i < dashItemsValidateList.size(); i++) {
                    if (Utils.getSearch(dashItemsValidateList.get(i), searchStr)) {
                        dashItemsAllListSearchList.add(dashItemsValidateList.get(i));
                    }
                }
            }

//            if (arrayStarTransList.size() > 0) {
//                issearchStar = true;
//                starredContainer.setVisibility(View.VISIBLE);
//                //searchView.setVisibility(View.VISIBLE);
//            } else {
//                issearchStar = false;
//                starredContainer.setVisibility(View.GONE);
//            }
            setupRecycler(dashItemsAllListSearchList);
        }else {
            Collections.reverse(dashItemsValidateList);
            setupRecycler(dashItemsValidateList);
        }
    }

    class SelectVAAll extends AsyncTask<Void, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(Void... voids) {
            dashItemsList.clear();
            dashItemsList = databaseClient.getAppDatabase()
                    .allTransactionsDao()
                    .getAllVIDTrans();

            return dashItemsList;
        }

        @Override
        protected void onPostExecute(List<AllTransactions> transLocalArray) {
            super.onPostExecute(transLocalArray);
            //setupRecycler();
        }
    }

    class SearchDB extends AsyncTask<String, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(String... params) {
            dashItemsList.clear();
            String searchText = params[0];
            if(!searchText.equalsIgnoreCase("")) {
                searchText = '%' + searchText + '%';
                dashItemsList = databaseClient.getAppDatabase().allTransactionsDao()
                        .getVerifySearch(searchText);
            }
            else{

                dashItemsList = databaseClient.getAppDatabase()
                        .allTransactionsDao()
                        .getAllVIDTrans();
            }
            return dashItemsList;
        }
        @Override
        protected void onPostExecute(List<AllTransactions> transLocalArray) {
            super.onPostExecute(transLocalArray);
            //setupRecycler();
        }
    }

    class Sort extends AsyncTask<Void, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(Void... voids) {
            dashItemsList.clear();
            dashItemsList = databaseClient.getAppDatabase()
                    .allTransactionsDao()
                    .getAllVIDTransAsc();

            return dashItemsList;
        }
        @Override
        protected void onPostExecute(List<AllTransactions> transLocalArray) {
            super.onPostExecute(transLocalArray);
            //setupRecycler();
        }

    }

    public void getAllTransactions(int page_no) throws JSONException {
        try {
            final String token = "AUTH_KEY";
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", dashBoardActivity.getUserEmail());
            params.put("pageSize", String.valueOf(pageSize));
            params.put("pageNo", String.valueOf(page_no));
            params.put("sortBy", "id");
            params.put("type", "LAD");
            Call<ResponseBody> call = service.getTransactions(token, params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        new AsyncTask<Void, Void, Void>() {
                            //@SuppressLint("StaticFieldLeak")
                            @Override
                            protected Void doInBackground(final Void... params) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<TransByPage>() {
                                }.getType();
                                try {

                                    /*if (dashItemsValidateList != null
                                            && dashItemsValidateList.size() > 0) {
                                        dashItemsValidateList.clear();
                                    }*/
                                    if (dashItemsValidateList.size() > 0){
                                        dashItemsValidateList.clear();
                                    }
                                    transByPage = gson.fromJson(response
                                            .body().string().trim(), type);
                                    if(transByPage != null
                                            && transByPage.getTransactions() != null
                                            && transByPage.getTransactions().getBody() != null) {
                                        dashItemsValidateList.addAll(transByPage.getTransactions().getBody());
                                    }
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                                if(transByPage != null && transByPage.getTotal() != null) {
                                    if (transByPage.getTotal() > 0) {
                                        paginationView.setVisibility(View.VISIBLE);
                                        try {
                                            totalSize = transByPage.getTotal();
                                        } catch (Exception e) {
                                            totalSize = 0;
                                        }
                                        if (totalSize > 5) {
                                            totalPages = (int) totalSize / pageSize;
                                            if (totalSize % pageSize > 0) {
                                                totalPages = totalPages + 1;
                                            }
                                        } else {
                                            totalPages = (int) totalSize / pageSize;
                                        }
                                    } else {
                                        paginationView.setVisibility(View.GONE);
                                        totalPages = 0;
                                    }
                                    if (totalPages > 1) {
                                        paginationView.setVisibility(View.VISIBLE);
                                        if (currentpage == 0) {
                                            prev.setVisibility(View.GONE);
                                        } else {
                                            prev.setVisibility(View.VISIBLE);
                                        }

                                        if (currentpage == totalPages - 1) {
                                            next.setVisibility(View.GONE);
                                        } else {
                                            next.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        paginationView.setVisibility(View.GONE);
                                    }
                                    setupRecycler(dashItemsValidateList);
                                }
                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test_LAD_Fail", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test_LAD_e", e11.toString());
        }
    }

}
