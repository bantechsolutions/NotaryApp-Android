  package com.notaryapp.ui.fragments.dashboard_new;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notaryapp.R;
import com.notaryapp.adapter.DashboardItemAdapterNew;
import com.notaryapp.interfacelisterners.IFragmentListener;
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

import org.jetbrains.annotations.NotNull;
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

public class Dashboard_TabAllFragmentNew extends Fragment implements PopulateDashboardListener {

    private View view;
    private RecyclerView recyclerView;
    private List<AllTransactions> dashItemsList;
    private List<Body> dashItemsListTabAll, dashItemsListTabAll1;
    int tabColor;
    private DatabaseClient databaseClient;

    private static final String ARG_SEARCHTERM = "search_term";
    private String mSearchTerm = null;

    //ArrayList<AllTransactions> strings = null;
    private IFragmentListener mIFragmentListener = null;
    DashboardItemAdapterNew dashboardItemAdapter = null;
    private Activity activity;
    private LoadingCompletedInterface loadingCompletedInterface;
    private ShimmerFrameLayout mShimmerViewContainer;
    private String isLoader = "";

    private int pageSize = 10;
    private TransByPage transByPage;
    private List<Body> dashItemsAllList;
    private List<Body> dashItemsAllListSearchList;
    private ConstraintLayout paginationView;
    private int totalSize = 0;
    private int totalPages = 0;
    private int currentpage = 0;

    private int totalCount;
    private boolean isLoading = false;
    private int currentSize;
    private boolean pData = true, succfullFetch= true;
    private int pageCount=0, currentPageCount=0;
    private boolean isVisible = false, LoadingStatus= false;
    private String tType ="", tSortOrder="DESC", tKeyword="";

    private TextView searchInfo, searchClear, sortView, prev, reset, next;
    private TextView verifyHeadText, verifyHeadText2, verifyHeadText3;



    private RelativeLayout allTabViewHeaderText;
    private DashBoardActivity dashBoardActivity;
    private String savedEmail;




    public Dashboard_TabAllFragmentNew() {
        // Required empty public constructor
    }



    public Dashboard_TabAllFragmentNew(String isLoader) {
        // Required empty public constructor
        this.isLoader = isLoader;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dashboard_tab_all, container, false);
        activity = getActivity();
        init();

        DashBoard_WelcomeFragmentNew mainActivity = new DashBoard_WelcomeFragmentNew();
        mainActivity.getDataFromFragment_one(dashItemsList);
        if (getArguments() != null) {
            mSearchTerm = (String) getArguments().get(ARG_SEARCHTERM);
        }
        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {
                if (complted)
                    CustomDialog.cancelProgressDialog();
                else {
                    CustomDialog.showProgressDialog(getActivity());
                }

            }
        };

        initScrollListener();

        return view;
    }

    public void searchSort(String searchKey) {

        new SearchDB().execute(searchKey);
    }

    public void sort(String type) {

        if (type.equalsIgnoreCase("Latest")) {
            //new SelectAll().execute();
            tSortOrder = "Desc";
            //startShimmer();
            if (Utils.isConnected()) {
                try {
                    //getAllTransactions(0);
                    getAllTransactions();
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity()
                        , getActivity().getResources().getString(R.string.networkerror)
                        , Toast.LENGTH_LONG).show();
            }

        } else {
            tSortOrder = "Asc";
            startShimmer();
            if (Utils.isConnected()) {
                try {
                    //getAllTransactions(0);
                    getAllTransactions();
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity()
                        , getActivity().getResources().getString(R.string.networkerror)
                        , Toast.LENGTH_LONG).show();
            }
            //new Sort().execute();

        }
    }


    private void setupRecycler(List<Body> dashItemsList) {

        try {
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dashItemsListTabAll.clear();
                        for (int i=0; i < dashItemsList.size(); i++){
                            dashItemsListTabAll.add(dashItemsList.get(i));
                            dashItemsListTabAll1.add(dashItemsList.get(i));
                        }

                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        dashboardItemAdapter = new DashboardItemAdapterNew(loadingCompletedInterface,
                                dashItemsListTabAll, getActivity());
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),
                                1,
                                GridLayoutManager.VERTICAL,
                                false);

                        // lahar modify
                        recyclerView.getRecycledViewPool().clear();
                        dashboardItemAdapter.notifyDataSetChanged();

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(dashboardItemAdapter);


                        Log.d("ALL TAB","LIST UPDATED");

                    }
                });
            }
        }
        catch (Exception e){
            Log.d("ALL TAB", "setupRecycler: "+ e.toString());
        }


    }

    private void clearRecycler() {

        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    dashboardItemAdapter = new DashboardItemAdapterNew(loadingCompletedInterface,
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
            Log.d("ALL TAB","ClearRecycler "+ e.toString());
        }


    }

    private void init() {
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        sortView = view.findViewById(R.id.sort);
        prev = view.findViewById(R.id.prev);
        reset = view.findViewById(R.id.reset);
        next = view.findViewById(R.id.next);
        verifyHeadText = view.findViewById(R.id.verifyHeadText);
        verifyHeadText2 = view.findViewById(R.id.verifyHeadText2);
        verifyHeadText3 = view.findViewById(R.id.verifyHeadText3);
        mShimmerViewContainer.startShimmer();
        databaseClient = DatabaseClient.getInstance(getActivity());
        recyclerView = view.findViewById(R.id.recyclerviewAll);
        paginationView = view.findViewById(R.id.paginationView);
        allTabViewHeaderText = view.findViewById(R.id.allTabViewHeaderText);
        dashItemsList = new ArrayList<>();
        dashItemsListTabAll = new ArrayList<>();
        dashItemsListTabAll1 = new ArrayList<>();
        dashItemsAllList = new ArrayList<>();
        dashItemsAllListSearchList = new ArrayList<>();
        new SelectEmail().execute();
        if ((isLoader != null && isLoader.equalsIgnoreCase("1"))
                || mSearchTerm != null) {
            //  new SelectAll().execute();
        }
        tType = "ALL";
        tKeyword = "";
        tSortOrder = "DESC";

        /*if (Utils.isConnected()) {
            try {
                //getAllTransactions(0);
                getAllTransactions();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity()
                    , getActivity().getResources().getString(R.string.networkerror)
                    , Toast.LENGTH_LONG).show();
        }*/

        ////
        /*String[] filterArray = {"All", "Monthly", "Yearly", "3 Years", "Lifetime"};


        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filterArray);
        spinnerFilter.setAdapter(filterAdapter);*/

        ////


        /*prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnected()) {
                    if (currentpage > 0) {
                        currentpage = currentpage - 1;
                        startShimmer();
                        try {
                            getAllTransactions(currentpage);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        e.printStackTrace();
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
                            e.printStackTrace();
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
        });*/
    }

    public void startShimmer() {
        clearRecycler();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
    }


    public static Dashboard_TabAllFragmentNew newInstance(String searchTerm) {
        Dashboard_TabAllFragmentNew fragment = new Dashboard_TabAllFragmentNew("2");
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SEARCHTERM, searchTerm);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onReceiveItemList(String type, String searchStr, String typeStr, String orderStr) {
        if (type.equalsIgnoreCase("search")) {
            try {
                /*if (dashItemsAllListSearchList != null
                        && dashItemsAllListSearchList.size() > 0) {
                    dashItemsAllListSearchList.clear();
                }

                for (int i = 0; i < dashItemsAllList.size(); i++) {
                    if (Utils.getSearch(dashItemsAllList.get(i), searchStr)) {
                        dashItemsAllListSearchList.add(dashItemsAllList.get(i));
                    }
                }*/

//            if (arrayStarTransList.size() > 0) {
//                issearchStar = true;
//                starredContainer.setVisibility(View.VISIBLE);
//                //searchView.setVisibility(View.VISIBLE);
//            } else {
//                issearchStar = false;
//                starredContainer.setVisibility(View.GONE);
//            }
                //setupRecycler(dashItemsAllListSearchList);

                if (searchStr.equalsIgnoreCase("") | searchStr.equalsIgnoreCase("null")){
                    tType = typeStr;
                    tSortOrder = orderStr;
                    tKeyword="";
                    if (Utils.isConnected()) {
                        try {
                            //getAllTransactions(0);
                            getAllTransactions();
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity()
                                , getActivity().getResources().getString(R.string.networkerror)
                                , Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Exception e){

            }
        } else if(type.equalsIgnoreCase("search_clcik")){
            //Toast.makeText(activity, searchStr, Toast.LENGTH_SHORT).show();
            tType = typeStr;
            tKeyword = searchStr;
            if (orderStr.equalsIgnoreCase("Latest")) {
                //new SelectAll().execute();
                tSortOrder = "Desc";
            }else {
                tSortOrder = "Asc";
            }
            if (Utils.isConnected()) {
                try {
                    //getAllTransactions(0);
                    getAllTransactions();
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity()
                        , getActivity().getResources().getString(R.string.networkerror)
                        , Toast.LENGTH_LONG).show();
            }
        }else if(type.equalsIgnoreCase("tranType")){
            tKeyword = searchStr;
            if (orderStr.equalsIgnoreCase("Latest")) {
                //new SelectAll().execute();
                tSortOrder = "Desc";
            }else {
                tSortOrder = "Asc";
            }
            //tSortOrder = orderStr;
            //Toast.makeText(activity, searchStr, Toast.LENGTH_SHORT).show();
            if (typeStr.equalsIgnoreCase("VID")) {
                //new SelectAll().execute();
                tType = "VID";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }

            } else if(typeStr.equalsIgnoreCase("VAU")){
                tType = "VAU";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }
            }
            else if(typeStr.equalsIgnoreCase("LAD")){
                tType = "LAD";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }
            }else if(typeStr.equalsIgnoreCase("VEJ")){
                tType = "VEJ";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }
            }
            else {
                tType = "ALL";
                //tKeyword =searchStr;
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }
                //new Sort().execute();

            }
        }else if(type.equalsIgnoreCase("sort")){
            tType = typeStr;
            tKeyword = searchStr;
            //Toast.makeText(activity, searchStr, Toast.LENGTH_SHORT).show();
            if (orderStr.equalsIgnoreCase("Latest")) {
                //new SelectAll().execute();
                tSortOrder = "Desc";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }

            } else {
                tSortOrder = "Asc";
                LoadingStatus= false;
                if (Utils.isConnected()) {
                    try {
                        //getAllTransactions(0);
                        getAllTransactions();
                    } catch (JSONException e) {
                        //e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity()
                            , getActivity().getResources().getString(R.string.networkerror)
                            , Toast.LENGTH_LONG).show();
                }
                //new Sort().execute();

            }
        }
        else {
            Collections.reverse(dashItemsAllList);
            setupRecycler(dashItemsAllList);
        }
    }

    class SelectAll extends AsyncTask<Void, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(Void... voids) {
            dashItemsList.clear();
            dashItemsList = databaseClient.getAppDatabase()
                    .allTransactionsDao()
                    .getAllTrans();

            return dashItemsList;
        }

        @Override
        protected void onPostExecute(List<AllTransactions> transLocalArray) {
            super.onPostExecute(transLocalArray);
            //setupRecycler();
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }

    }

    class SearchDB extends AsyncTask<String, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(String... params) {
            dashItemsList.clear();
            String searchText = params[0];
            if (!searchText.equalsIgnoreCase("")) {
                searchText = '%' + searchText + '%';
                dashItemsList = databaseClient.getAppDatabase().allTransactionsDao()
                        .getSearchDesc(searchText);
            } else {
                dashItemsList = databaseClient.getAppDatabase()
                        .allTransactionsDao()
                        .getAllTrans();
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
                    .getAllTransAsc();

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

            dashItemsListTabAll.add(null);
            //dashboardItemAdapter.notifyDataSetChanged();
            Log.d("PAGE_NUMBER", String.valueOf(pageCount));
            dashboardItemAdapter.notifyItemInserted(dashItemsListTabAll.size()-1);

            final String token = "AUTH_KEY";
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", savedEmail);
            params.put("pageSize", String.valueOf(pageSize));
            params.put("pageNo", String.valueOf(pageCount));
            params.put("sortBy", "id");
            params.put("type", tType);
            params.put("sortOrder", tSortOrder);
            params.put("keyword",tKeyword);
            Log.d("GETTRAN_PAGE_P", params.toString());
            Call<ResponseBody> call = service.getTransactions(token, params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        Log.d("INNER_POST", "TRUE_1"+ response.body().toString());
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(final Void... params) {
                                Gson gson = new Gson();
                                dashItemsListTabAll1 = new ArrayList<>();
                                Type type = new TypeToken<TransByPage>() {
                                }.getType();
                                try {
                                    /*if (dashItemsAllList != null
                                            && dashItemsAllList.size() > 0) {
                                        dashItemsAllList.clear();
                                    }*/

                                    transByPage = gson.fromJson(response
                                            .body().string().trim(), type);

                                    if (transByPage != null
                                            && transByPage.getTransactions() != null
                                            && transByPage.getTransactions().getBody() != null) {
                                        //totalCount = transByPage.getTotal();
                                        //Log.d("ALL_TOTAL_TRAN",totalCount.toString());
                                        dashItemsListTabAll1.addAll(transByPage.getTransactions().getBody());
                                    } else {
                                        Log.d("NO_DATA", "NO DATA");
                                    }
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                                if(transByPage != null
                                        && transByPage.getTotal() != null) {
                                    if (transByPage.getTotal() > 0) {
                                        allTabViewHeaderText.setVisibility(View.GONE);
                                        //paginationView.setVisibility(View.VISIBLE);
                                        paginationView.setVisibility(View.GONE);
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
                                        allTabViewHeaderText.setVisibility(View.VISIBLE);
                                        paginationView.setVisibility(View.GONE);
                                        totalPages = 0;
                                    }
                                    if (totalPages > 1) {
                                        //paginationView.setVisibility(View.VISIBLE);
                                        paginationView.setVisibility(View.GONE);
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

                                    dashItemsListTabAll.remove(dashItemsListTabAll.size()-1);
                                    int scrollPosition = dashItemsListTabAll.size();
                                    dashboardItemAdapter.notifyItemRemoved(scrollPosition);

                                    //setupRecycler(dashItemsAllList);
                                    //succfullFetch = true;
                                    //pageCount++;
                                    int j=0;
                                    while (j < dashItemsListTabAll1.size()){
                                        dashItemsListTabAll.add(dashItemsListTabAll1.get(j));
                                        //dashItemsListTabAll1.add(dashItemsListTabAll1.get(j));
                                        currentSize++;
                                        //pData = true;
                                        j++;
                                        //isLoading = false;
                                    }
                                    //currentpage++;
                                    pageCount++;
                                    succfullFetch = true;
                                    dashboardItemAdapter.notifyDataSetChanged();
                                    isLoading = false;

                                    Log.d("INNER_POST", "TRUE");
                                } else {
                                    dashboardItemAdapter.notifyDataSetChanged();
                                    isLoading = false;
                                    Log.d("INNER_POST", "FALSE");
                                }
                            }
                        }.execute();

                    } else {
                        Log.d("INNER_POST", "FALSE");
                        dashboardItemAdapter.notifyDataSetChanged();
                        //isLoading = false;

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test_ALL_Fail", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test_ALL_e", e11.toString());
        }
    }

    public void getAllTransactions() throws JSONException {
        allTabViewHeaderText.setVisibility(View.GONE);
        try {

            final String token = "AUTH_KEY";
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", savedEmail);
            params.put("pageSize", String.valueOf(pageSize));
            params.put("pageNo", "0");
            params.put("sortBy", "id");
            params.put("type", tType);
            params.put("sortOrder", tSortOrder);
            params.put("keyword",tKeyword);
            Log.d("GETTRAN_PAGE_1", params.toString());
            Call<ResponseBody> call = service.getTransactions(token, params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(final Void... params) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<TransByPage>() {
                                }.getType();
                                try {
                                    /*if (dashItemsAllList != null
                                            && dashItemsAllList.size() > 0) {
                                        dashItemsAllList.clear();
                                    }*/
                                    if (dashItemsAllList.size() > 0) {
                                        dashItemsAllList.clear();
                                    }

                                    transByPage = gson.fromJson(response
                                            .body().string().trim(), type);
                                    if (transByPage != null
                                            && transByPage.getTransactions() != null
                                            && transByPage.getTransactions().getBody() != null) {
                                        totalCount = transByPage.getTotal();
                                        pageCount = 0;
                                        //pageCount++;
                                        isLoading = false;
                                        //Log.d("ALL_TOTAL_TRAN",totalCount.toString());
                                        dashItemsAllList.addAll(transByPage.getTransactions().getBody());
                                    }
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                                if(transByPage != null
                                  && transByPage.getTotal() != null) {
                                    if (transByPage.getTotal() > 0) {
                                        allTabViewHeaderText.setVisibility(View.GONE);
                                        paginationView.setVisibility(View.GONE);
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
                                        allTabViewHeaderText.setVisibility(View.VISIBLE);
                                        verifyHeadText2.setVisibility(View.VISIBLE);
                                        verifyHeadText3.setVisibility(View.VISIBLE);
                                        if (tKeyword.length()>0){
                                            verifyHeadText.setText("No Records Found.");
                                            verifyHeadText2.setVisibility(View.GONE);
                                            verifyHeadText3.setVisibility(View.GONE);

                                        } else {
                                            if (tType.equalsIgnoreCase("VID")){
                                                verifyHeadText.setText("No Verify ID Transaction Yet.");
                                                verifyHeadText2.setVisibility(View.GONE);
                                                verifyHeadText3.setText("Start a new Verify ID now.");

                                            } else if(tType.equalsIgnoreCase("VAU")){
                                                verifyHeadText.setText("No Notary-App® Transaction Yet.");
                                                verifyHeadText2.setVisibility(View.GONE);
                                                verifyHeadText3.setText("Start a new Notary-App® now.");
                                            } else if(tType.equalsIgnoreCase("VEJ")){
                                                verifyHeadText.setText("No e-Journal™ Transaction Yet.");
                                                verifyHeadText2.setVisibility(View.GONE);
                                                verifyHeadText3.setText("Start a new e-Journal™ now.");
                                            } else if(tType.equalsIgnoreCase("LAD")){
                                                verifyHeadText.setText("No Lock-A-Doc™ Transaction Yet.");
                                                verifyHeadText2.setVisibility(View.GONE);
                                                verifyHeadText3.setText("Start a new Lock-A-Doc™ now.");
                                            } else {

                                            }
                                        }

                                        paginationView.setVisibility(View.GONE);
                                        totalPages = 0;
                                    }
                                    if (totalPages > 1) {
                                        paginationView.setVisibility(View.GONE);
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
                                    pageCount++;
                                    setupRecycler(dashItemsAllList);
                                }
                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test_ALL_Fail", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test_ALL_e", e11.toString());
        }
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                try {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();



                    if (!isLoading){
                        //Log.d("IS_LOADING","TRUE");
                        Log.d("DIFF_TAG", String.valueOf(dashItemsListTabAll.size())+" : " + totalCount);

                        if (linearLayoutManager != null &&
                                linearLayoutManager.findLastCompletelyVisibleItemPosition() == dashItemsListTabAll.size() -1 &&
                                dashItemsListTabAll.size() < totalCount && succfullFetch == true){
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
                            Log.d("IS_LOADING","TRUE");


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

        try {
            getAllTransactions(pageCount);
            Log.d("PAGE_ADAPTER", String.valueOf(pageCount));
        } catch (JSONException e) {
            //e.printStackTrace();
        }


        /*dashItemsListTabAll.remove(dashItemsListTabAll.size()-1);
        int scrollPosition = dashItemsListTabAll.size();
        dashboardItemAdapter.notifyItemRemoved(scrollPosition);
        currentSize = scrollPosition;



        if (currentSize < totalCount && pData == true){
            pData= false;
            pageCount++;
            try {
                getAllTransactions(pageCount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int j=0;
        while (j < dashItemsListTabAll1.size() && succfullFetch == true){
            dashItemsListTabAll.add(dashItemsListTabAll1.get(j));
            //dashItemsListTabAll1.add(dashItemsListTabAll1.get(j));
            currentSize++;
            pData = true;
            j++;
        }

        succfullFetch = false;
        dashboardItemAdapter.notifyDataSetChanged();
        isLoading = false;*/
        /*dashboardItemAdapter.notifyDataSetChanged();
        isLoading = false;*/

    }

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }



}
