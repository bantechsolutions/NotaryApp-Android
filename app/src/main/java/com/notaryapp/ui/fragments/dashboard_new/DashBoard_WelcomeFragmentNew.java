package com.notaryapp.ui.fragments.dashboard_new;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.notaryapp.R;
import com.notaryapp.adapter.DashboardStarredAdapterNew;
import com.notaryapp.adapter.DashboardTabAdapter;
import com.notaryapp.ejournal.activities.VEJ_Notarize_AlertActivity;
import com.notaryapp.interfacelisterners.IDataCallback;
import com.notaryapp.interfacelisterners.IFragmentListener;
import com.notaryapp.interfacelisterners.ISearch;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.lockadoc.fragments.Dashboard_TabLADFragment;
import com.notaryapp.lockadoc.activityes.LADStepsActivity;
import com.notaryapp.model.webresponse.Body;
import com.notaryapp.model.webresponse.TransByPage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AllTransactions;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.LicenseListActivity;
import com.notaryapp.ui.activities.ValidateActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.ui.activities.verifyauthenticate.Notarize_AlertActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.utilsretrofit.GetDataService;
import com.notaryapp.utilsretrofit.RetrofitClientInstance;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.material.tabs.TabLayout.*;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


public class DashBoard_WelcomeFragmentNew extends Fragment implements
        OnTabSelectedListener,
        SearchView.OnQueryTextListener,
        IFragmentListener {

    private View homeView;
    private ConstraintLayout paginationView, sortContainer, floatN, floatL, floatV, floatR, floatE, fab, starredContainer, floatBg, blurBox, welcome, tabView, searchResultContainer;
    private ConstraintLayout floatN_click, floatL_click, floatV_click, floatE_click;
    private ImageView floatN_info_btn, floatL_info_btn, floatV_info_btn, floatE_info_btn;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private PulsatorLayout pulsator;
    private DatabaseClient databaseClient;
    private TextView searchInfo, searchClear, sortView, prev, reset, next, tTypeText;
    private SearchView searchView;
    private List<AllTransactions> dashItemsList;
    private RecyclerView recyclerSearch;
    private ImageView sorticon;
    private EditText searchEditText;
    private ProgressBar img_progress;

    //Starred Recycler
    DashboardStarredAdapterNew dashboardStarredAdapter;
    RecyclerView recyclerStarred;
    private GridLayoutManager layoutManager;
    // private List<DashStarredModel> starredList;

    //Tabs
    private TabLayout tabLayout;
    private ViewPager tabsViewPager;
    private DashboardTabAdapter dashboardTabAdapter;

    private int daysLeft;
    private int transCount;
    private String selectedPlan, planName, notaryEmail;
    private VACustomer memPlans;
    private IJsonListener iJsonListener;

    private AllTransactions allTransactions;
    private List<AllTransactions> arryAllTrans;
    private List<AllTransactions> arryStarTrans;
    private List<AllTransactions> allTrans;

    //private DashboardItemAdapter dashboardItemAdapter;
    ArrayList<ISearch> iSearch = new ArrayList<>();
    ArrayList<String> listData = null;
    private String query;
    IDataCallback iDataCallback = null;
    String adapter;



    private LoadingCompletedInterface loadingCompletedInterface;

    private int nextTabposition = 0;
    private int tabposition = 0;
    private boolean issearchStar = false;
    private Activity activity;
    private int pageSize = 10;
    private int totalPages = 0;
    private int currentpage = 0;
    private int totalSize = 0;

    Dashboard_TabAllFragmentNew dashboard_tabAllFragment;
    Dashboard_TabValidateFragmentNew dashboard_tabValidateFragment;
    Dashboard_TabAuthFragmentNew dashboard_tabAuthFragment;
    Dashboard_TabVEJFragmentNew dashboard_tabVEJFragment;
    Dashboard_TabLADFragment dashboard_tabLADFragment;
    TransByPage transByPage;
    List<Body> dashItemsAllList;
    List<Body> dashItemsAuthList;
    List<Body> dashItemsValidateList;

    List<Body> arrayStarTransList;

    private ReviewManager manager;

    private static final int MY_REQUEST_CODE = 199;
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;

    private Boolean flag = true;
    private ConstraintLayout warningContainer;
    private TextView license_msg;

    private Spinner spinnerFilter;

    private DashBoardActivity dashBoardActivity;
    private String savedEmail;


    public void setiDataCallback(IDataCallback iDataCallback) {
        this.iDataCallback = iDataCallback;
        iDataCallback.onFragmentCreated(arryAllTrans);
    }

    @Override
    public void addiSearch(ISearch iSearch) {
        this.iSearch.add(iSearch);
    }

    @Override
    public void removeISearch(ISearch iSearch) {
        this.iSearch.remove(iSearch);
    }

    public DashBoard_WelcomeFragmentNew() {
        // Required empty public constructor
    }

    public DashBoard_WelcomeFragmentNew(String adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_dashboard_welcome, container, false);
        activity = getActivity();
        init();
        setupAnimations();
        buttonControls();

        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {
                if (complted) {
                    CustomDialog.cancelProgressDialog();
                } else {
                    CustomDialog.showProgressDialog(activity);
                }
            }
        };

        return homeView;
    }

    private void init() {
        //new SelectEmail().execute();
        sortContainer = homeView.findViewById(R.id.sortContainer);
        warningContainer = homeView.findViewById(R.id.warningContainer);
        license_msg = homeView.findViewById(R.id.license_msg);

        floatN = homeView.findViewById(R.id.floatN);
        floatL = homeView.findViewById(R.id.floatL); // Lock-A-Doc
        floatV = homeView.findViewById(R.id.floatV);
        floatR = homeView.findViewById(R.id.floatR);
        floatE = homeView.findViewById(R.id.floatE);
        floatN_click = homeView.findViewById(R.id.floatN_click);
        floatL_click = homeView.findViewById(R.id.floatL_click); // Lock-A-Doc
        floatV_click = homeView.findViewById(R.id.floatV_click);
        floatE_click = homeView.findViewById(R.id.floatE_click);
        floatN_info_btn = homeView.findViewById(R.id.floatN_info_btn);
        floatL_info_btn = homeView.findViewById(R.id.floatL_info_btn); // Lock-A-Doc
        floatV_info_btn = homeView.findViewById(R.id.floatV_info_btn);
        floatE_info_btn = homeView.findViewById(R.id.floatE_info_btn);
        fab = homeView.findViewById(R.id.fab);
        // floatBg = homeView.findViewById(R.id.dim);
        pulsator = homeView.findViewById(R.id.pulsator);
        recyclerStarred = homeView.findViewById(R.id.recyclerStarred);
        starredContainer = homeView.findViewById(R.id.starredContainer);
        tabLayout = homeView.findViewById(R.id.tab_layout);
        tabsViewPager = homeView.findViewById(R.id.viewPagerTabs);
        searchView = homeView.findViewById(R.id.searchView);
        blurBox = homeView.findViewById(R.id.blurBox);
        welcome = homeView.findViewById(R.id.welcomeContainer);
        tabView = homeView.findViewById(R.id.tabView);
        sortView = homeView.findViewById(R.id.sort);
        tTypeText = homeView.findViewById(R.id.tTypeText);
        sorticon = homeView.findViewById(R.id.sorticon);

        prev = homeView.findViewById(R.id.prev);
        reset = homeView.findViewById(R.id.reset);
        next = homeView.findViewById(R.id.next);
        paginationView = homeView.findViewById(R.id.paginationView);


        dashItemsAllList = new ArrayList<>();
        dashItemsAuthList = new ArrayList<>();
        dashItemsValidateList = new ArrayList<>();
        arrayStarTransList = new ArrayList<>();

        dashboard_tabAllFragment = new Dashboard_TabAllFragmentNew("");
        dashboard_tabValidateFragment = new Dashboard_TabValidateFragmentNew();
        dashboard_tabAuthFragment = new Dashboard_TabAuthFragmentNew();
        dashboard_tabLADFragment = new Dashboard_TabLADFragment();

        ///
        dashboard_tabVEJFragment = new Dashboard_TabVEJFragmentNew();


        databaseClient = DatabaseClient.getInstance(activity);

        /////
        spinnerFilter = homeView.findViewById(R.id.filterDash);

        String[] filterArray = {"All", "e-Journal™", "Verify ID", "Lock-A-Doc™"};


        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, filterArray);
        spinnerFilter.setAdapter(filterAdapter);

        /////


        ////////////////////////////
        //Sourav 20200930
        tabView.setVisibility(View.VISIBLE);
        sortContainer.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHint("Search");
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        new DeleteTransToShow().execute();
        setupTabs();
        //appReview();

        appUpdateManager = AppUpdateManagerFactory.create(getContext());
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        checkForUpdate();
        //CommissionListAPI();
        //setupStarredRecycler();
        /////////////////////////////

        if (Utils.isConnected()) {
            new SelectEmail().execute();
            //getAllTransactions1(0);
        } else {
            Toast.makeText(getActivity()
                    , getActivity().getResources().getString(R.string.networkerror)
                    , Toast.LENGTH_LONG).show();
        }

        warningContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LicenseListActivity.class);
                startActivity(intent);
            }
        });

        //searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) getActivity());
        //lahar search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getActivity(), "ssdsdsds", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        /////
        searchView.setOnQueryTextListener(this);
        ////
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    spinnerFilter.setSelection(0);
                    //Toast.makeText(activity, "SEARCH CLICK", Toast.LENGTH_SHORT).show();
                    dashboard_tabAllFragment.startShimmer();
                    /*if (spinnerFilter.getSelectedItem().toString().equals("Verify ID")){
                        dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "VID", sortView.getText().toString());
                    } else if (spinnerFilter.getSelectedItem().toString().equals("Notary-App®")){
                        dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "VAU", sortView.getText().toString());
                    } else if (spinnerFilter.getSelectedItem().toString().equals("Lock-A-Doc™")){
                        dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "LAD", sortView.getText().toString());
                    } else if (spinnerFilter.getSelectedItem().toString().equals("e-Journal™")){
                        dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "VEJ", sortView.getText().toString());
                    } else{
                        dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "ALL", sortView.getText().toString());
                    }*/
                    dashboard_tabAllFragment.onReceiveItemList("search_clcik", searchEditText.getText().toString(), "ALL", sortView.getText().toString());


                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    getView().getWindowToken();
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        ////
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String[] filterArray = {"Verify ID", "Notary-App®", "Lock-A-Doc™", "e-Journal™"};
                //style="@style/DashSpinnerTheme"
                //android:layout_width="13dp"
                //android:layout_height="13dp"
                tTypeText.setText(spinnerFilter.getItemAtPosition(position).toString());
                dashboard_tabAllFragment.startShimmer();
                if (spinnerFilter.getItemAtPosition(position).toString().equals("Verify ID")){
                    dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "VID", sortView.getText().toString());
                } else if (spinnerFilter.getItemAtPosition(position).toString().equals("Notary-App®")){
                    dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "VAU", sortView.getText().toString());
                } else if (spinnerFilter.getItemAtPosition(position).toString().equals("Lock-A-Doc™")){
                    dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "LAD", sortView.getText().toString());
                } else if (spinnerFilter.getItemAtPosition(position).toString().equals("e-Journal™")){
                    dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "VEJ", sortView.getText().toString());
                } else{
                    dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "ALL", sortView.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tTypeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerFilter.performClick();
            }
        });

        sortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (sortView.getText().toString().equalsIgnoreCase("Old")) {
                    sortView.setText("Latest");
                    sorticon.setImageDrawable(getResources().getDrawable(R.drawable.latest_arrow));
                    if (tabposition == 0) {
                        dashboard_tabAllFragment.startShimmer();
                        //////////////////////////
                        //Collections.reverse(dashItemsAllList);
                        if (spinnerFilter.getSelectedItem().toString().equals("Verify ID")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VID", "Latest");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("Notary-App®")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VAU", "Latest");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("Lock-A-Doc™")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "LAD", "Latest");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("e-Journal™")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VEJ", "Latest");
                        } else{
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "ALL", "Latest");
                        }
                        //dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");
                        //dashboard_tabAllFragment.onReceiveItemList("sort","Latest");
                        /////////////////////////////
                        /*Dashboard_TabAllFragmentNew fragment =
                                (Dashboard_TabAllFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    } else if (tabposition == 1) {
                        /////////////////////////
                        //Collections.reverse(dashItemsValidateList);
                        dashboard_tabValidateFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");

                        ////////////////////////
                        /*Dashboard_TabValidateFragmentNew fragment =
                                (Dashboard_TabValidateFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    } else if (tabposition == 2) {
                        ////////////////////
                        //Collections.reverse(dashItemsAuthList);
                        dashboard_tabLADFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");
                        //////////////////
                        /*Dashboard_TabAuthFragmentNew fragment =
                                (Dashboard_TabAuthFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    }else if (tabposition == 3) {
                        ////////////////////
                        //Collections.reverse(dashItemsAuthList);
                        dashboard_tabAuthFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");
                        //////////////////
                        /*Dashboard_TabAuthFragmentNew fragment =
                                (Dashboard_TabAuthFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    }
                } else {
                    sortView.setText("Old");
                    sorticon.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));

                    if (tabposition == 0) {
                        dashboard_tabAllFragment.startShimmer();
                        //////////////////////////
                        //Collections.reverse(dashItemsAllList);
                        if (spinnerFilter.getSelectedItem().toString().equals("Verify ID")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VID", "Old");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("Notary-App®")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VAU", "Old");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("Lock-A-Doc™")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "LAD", "Old");
                        } else if (spinnerFilter.getSelectedItem().toString().equals("e-Journal™")){
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "VEJ", "Old");
                        } else{
                            dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), "ALL", "Old");
                        }
                        //dashboard_tabAllFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Old");
                        //dashboard_tabAllFragment.onReceiveItemList("sort","Old");
                        /////////////////////////////
                        /*Dashboard_TabAllFragmentNew fragment =
                                (Dashboard_TabAllFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    } else if (tabposition == 1) {
                        /////////////////////////
                        Collections.reverse(dashItemsValidateList);
                        dashboard_tabValidateFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");

                        ////////////////////////
                        /*Dashboard_TabValidateFragmentNew fragment =
                                (Dashboard_TabValidateFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    } else if (tabposition == 2) {
                        ////////////////////
                        Collections.reverse(dashItemsAuthList);
                        dashboard_tabLADFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");
                        //////////////////
                        /*Dashboard_TabAuthFragmentNew fragment =
                                (Dashboard_TabAuthFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    }else if (tabposition == 3) {
                        ////////////////////
                        Collections.reverse(dashItemsAuthList);
                        dashboard_tabAuthFragment.onReceiveItemList("sort", searchEditText.getText().toString(), spinnerFilter.getSelectedItem().toString(), "Latest");
                        //////////////////
                        /*Dashboard_TabAuthFragmentNew fragment =
                                (Dashboard_TabAuthFragmentNew) dashboardTabAdapter.getRegisteredFragment(tabposition);
                        fragment.sort("Latest");*/
                    }
                }

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isConnected()) {
                    if (currentpage > 0) {

                        nextTabposition = tabposition;
                        currentpage = currentpage - 1;
                        //getPaginationTransactions(currentpage);

                        dashboard_tabAllFragment.startShimmer();
                        dashboard_tabValidateFragment.startShimmer();
                        dashboard_tabAuthFragment.startShimmer();
                        dashboard_tabLADFragment.startShimmer();
                        try {
                            getAllTransactions1(currentpage);
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
                    nextTabposition = tabposition;
                    currentpage = 0;

                    dashboard_tabAllFragment.startShimmer();
                    dashboard_tabValidateFragment.startShimmer();
                    dashboard_tabAuthFragment.startShimmer();
                    dashboard_tabLADFragment.startShimmer();

                    //getPaginationTransactions(currentpage);
                    try {
                        getAllTransactions1(currentpage);
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

                        nextTabposition = tabposition;
                        currentpage = currentpage + 1;
                        //getPaginationTransactions(currentpage);
                        dashboard_tabAllFragment.startShimmer();
                        dashboard_tabValidateFragment.startShimmer();
                        dashboard_tabAuthFragment.startShimmer();
                        dashboard_tabLADFragment.startShimmer();
                        try {
                            getAllTransactions1(currentpage);
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

    private void CommissionListAPI() {
        //CustomDialog.showProgressDialog(getActivity());

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("LicenseList")) {
                            if (data.has("success")) {
                                //commissionItems =new ArrayList<>();
                                String license_num = "", SstateName = "", license_status="", msg="";

                                JSONArray license_array = data.getJSONArray("success");
                                if (license_array.length() != 0) {
                                    StringBuilder licenseNo = new StringBuilder();
                                    StringBuilder stateN = new StringBuilder();
                                    for (int i = 0; i < license_array.length(); i++) {

                                        JSONObject json_inside = license_array.getJSONObject(i);
                                        license_num = json_inside.getString("notary_license_number");
                                        //SstateName = json_inside.getString("notary_license_state_name");
                                        //SstateName = json_inside.getString("notary_license_state");
                                        license_status = json_inside.getString("status");
                                        msg = json_inside.getString("msg");

                                        if (flag){
                                            if (license_status.equalsIgnoreCase("warning")){
                                                flag = false;
                                                warningContainer.setVisibility(VISIBLE);
                                                license_msg.setText(msg);
                                            } else if (license_status.equalsIgnoreCase("error")){
                                                flag = false;
                                                warningContainer.setVisibility(VISIBLE);
                                                license_msg.setText(msg);
                                            }else if(license_status.equalsIgnoreCase("missing")){
                                                flag = false;
                                                warningContainer.setVisibility(VISIBLE);
                                                license_msg.setText(msg);
                                            }
                                            else {
                                                //warningContainer.setVisibility(GONE);
                                            }
                                        }

                                        //licenseNo.append(license_num).append("\n");
                                        //stateN.append(SstateName).append("\n");
                                        //commissionItems.add(new CommissionItems(SstateName,license_num, license_status));
                                    }
                                    //stateName.setText(stateN);
                                    //licenseNumber.setText(licenseNo);

                                    //CommissionAdapter commissionAdapter = new CommissionAdapter(getContext(), R.layout.items_commission_row_data, commissionItems);
                                    //listView.setAdapter(commissionAdapter);
                                }

                            }
                        }

                    }
                } catch (Exception e) {
                    //CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //   CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", savedEmail);
                //  params.put("pledge",true);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.LICENSE_LIST, "LicenseList");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    private void appReview() {

        try {
            manager = ReviewManagerFactory.create(getActivity());
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                    flow.addOnCompleteListener(task1 -> {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        //Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // There was some problem, log or handle the error code.

                }
            });

        }catch (IllegalArgumentException e){

        }

    }


    private void buttonControls() {
        //Button click to show start animation of buttons.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pulsator.stop();
                animateFAB();
            }
        });

        //Button click to show start next fragment.
        floatN_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV.setClickable(false);
                floatL.setClickable(false);
                //floatN.setClickable(false);
                floatR.setClickable(false);
                floatE.setClickable(false);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);


                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);


                new DeleteAll().execute();
            }
        });

        //Button click to show start next fragment.//Lock-A-Doc
        floatL_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);


                new DeleteAllLAD().execute();
            }
        });

        //Button click to show start next fragment.
        floatV_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);

                new DeleteAllSelectId().execute();
            }
        });

        floatE_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);

                new DeleteAllVEJ().execute();
            }
        });

        floatV_info_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(activity, "You click", Toast.LENGTH_SHORT).show();
                infoPopUp("floatv");

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);
            }
        });
        floatL_info_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(activity, "You click", Toast.LENGTH_SHORT).show();
                infoPopUp("floatl");

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);
            }
        });

        floatN_info_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(activity, "You click", Toast.LENGTH_SHORT).show();
                infoPopUp("floatn");

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);
            }
        });

        floatE_info_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(activity, "You click", Toast.LENGTH_SHORT).show();
                infoPopUp("floate");

                fab.startAnimation(rotate_backward);
                floatV.startAnimation(fab_close);
                floatL.startAnimation(fab_close);
                //floatN.startAnimation(fab_close);
                floatR.startAnimation(fab_close);
                floatE.startAnimation(fab_close);

                floatV_click.setClickable(false);
                floatL_click.setClickable(false);
                //floatN_click.setClickable(false);
                floatE_click.setClickable(false);

                floatV_info_btn.setClickable(false);
                floatL_info_btn.setClickable(false);
                //floatN_info_btn.setClickable(false);
                floatE_info_btn.setClickable(false);

                isFabOpen = false;
                pulsator.start();

                blurBox.setVisibility(View.GONE);
            }
        });
    }

    private void infoPopUp(String type) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_notification_dialog);

        ImageView image = dialog.findViewById(R.id.imageUrl);
        TextView alertHead = dialog.findViewById(R.id.alertHead);
        TextView alertMessage = dialog.findViewById(R.id.alertMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        img_progress = dialog.findViewById(R.id.img_progress);

        img_progress.setVisibility(GONE);
        btnOk.setVisibility(GONE);
        image.setVisibility(GONE);


        if (type.equals("floatv")){
            alertHead.setText(R.string.verify_id_only);
            alertMessage.setText(R.string.use_this_button_if_you);
        } else if (type.equals("floatl")){
            alertHead.setText(R.string.lock_a_doc);
            alertMessage.setText(R.string.use_this_button_to_upload_and);
        } else if (type.equals("floatn")){
            alertHead.setText(R.string.skinny_ejournal);
            alertMessage.setText(R.string.use_this_shortened_version_of);
        } else if (type.equals("floate")){
            alertHead.setText(R.string.ejournal);
            alertMessage.setText(R.string.replace_all_other_notarial_journals);
        } else {

        }



        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("nextTabposition", nextTabposition);
        super.onSaveInstanceState(outState);
    }


    private void setupTabs() {

        if (getActivity() != null) {
            dashboardTabAdapter = new DashboardTabAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), query);
            dashboardTabAdapter.AddFragment(dashboard_tabAllFragment, "All");
            //dashboardTabAdapter.AddFragment(dashboard_tabValidateFragment, "Verify ID");
            //dashboardTabAdapter.AddFragment(dashboard_tabLADFragment, "Lock-A-Doc™");
            //dashboardTabAdapter.AddFragment(dashboard_tabAuthFragment, "Notary-App®");
            //dashboardTabAdapter.AddFragment(dashboard_tabVEJFragment, "e-Journal");
            tabsViewPager.setAdapter(dashboardTabAdapter);
            int limit = (dashboardTabAdapter.getCount() > 1 ? dashboardTabAdapter.getCount() - 1 : 1);
            tabLayout.setupWithViewPager(tabsViewPager);
            tabLayout.setOnTabSelectedListener(this);
            tabLayout.getChildAt(0).setVisibility(GONE);



            tabsViewPager.setCurrentItem(nextTabposition);
            tabsViewPager.setOffscreenPageLimit(limit);
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }

    }


    private void setupAnimations() {
        //Loading animations
        fab_open = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.rotate_backward);
    }


    public void setupStarredRecycler() {
        welcome.setVisibility(View.GONE);
        dashboardStarredAdapter = new DashboardStarredAdapterNew(loadingCompletedInterface, arrayStarTransList, activity);
        layoutManager = new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);

        /*recyclerStarred.getRecycledViewPool().clear();
        dashboardStarredAdapter.notifyDataSetChanged();*/

        recyclerStarred.setLayoutManager(layoutManager);
        recyclerStarred.setAdapter(dashboardStarredAdapter);
    }


    private void animateFAB() {

        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            floatV.startAnimation(fab_close);
            floatL.startAnimation(fab_close);
            //floatN.startAnimation(fab_close);
            floatR.startAnimation(fab_close);
            floatE.startAnimation(fab_close);

            floatV_click.setClickable(false);
            floatL_click.setClickable(false);
            //floatN_click.setClickable(false);
            floatE_click.setClickable(false);

            floatV_info_btn.setClickable(false);
            floatL_info_btn.setClickable(false);
            //floatN_info_btn.setClickable(false);
            floatE_info_btn.setClickable(false);
            isFabOpen = false;
            pulsator.start();

            blurBox.setVisibility(View.GONE);

            //Log.d("Animation:", "close");
        } else {
            blurBox.setVisibility(View.VISIBLE);

            fab.startAnimation(rotate_forward);
            floatV.startAnimation(fab_open);
            floatL.startAnimation(fab_open);
            //floatN.startAnimation(fab_open);
            floatR.startAnimation(fab_open);
            floatE.startAnimation(fab_open);

            floatL_click.setClickable(true);
            //floatN_click.setClickable(true);
            floatV_click.setClickable(true);
            floatE_click.setClickable(true);

            floatL_info_btn.setClickable(true);
            //floatN_info_btn.setClickable(true);
            floatV_info_btn.setClickable(true);
            floatE_info_btn.setClickable(true);

            isFabOpen = true;
            pulsator.stop();
            //Log.d("Animation:","open");
        }
    }

    public void getDataFromFragment_one(List<AllTransactions> listData) {
        arryAllTrans = listData;
        //Log.e("-->", "" + listData.toString());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null) {
            setupSearchRecycler(query);
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        dashItemsList = new ArrayList<>();
//        starredContainer.setVisibility(View.GONE);
//        tabView.setVisibility(View.GONE);
//        welcome.setVisibility(View.GONE);
//        new SelectAll().execute(newText);

        if (newText != null) {
            setupSearchRecycler(newText);
            //Toast.makeText(activity, newText, Toast.LENGTH_SHORT).show();
        }
        if(!TextUtils.isEmpty(newText))
        {
            //search user.
            //SearchUser(newText);


        }else
        {
            //Text search is empty get all users.
            //Toast.makeText(activity, "EMPTY", Toast.LENGTH_SHORT).show();
            dashboard_tabAllFragment.startShimmer();
            dashboard_tabAllFragment.onReceiveItemList("tranType", searchEditText.getText().toString(), "All", sortView.getText().toString());
            //dashboard_tabAllFragment.onReceiveItemList("tranType", "All");

        }
        return true;
    }

    @Override
    public void onTabSelected(Tab tab) {
        tabsViewPager.setCurrentItem(tab.getPosition());
        tabposition = tab.getPosition();

        sortView.setText("Latest");
        sorticon.setImageDrawable(getResources().getDrawable(R.drawable.latest_arrow));
    }



    @Override
    public void onTabUnselected(Tab tab) {

    }

    @Override
    public void onTabReselected(Tab tab) {

    }


    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {
        @Override
        protected VACustomer doInBackground(Void... voids) {
            memPlans = databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();
            notaryEmail = databaseClient.getAppDatabase().userRegDao().getEmail();
            return memPlans;
        }

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);
            selectedPlan = memPlans.getCurrent_membership();
            daysLeft = memPlans.getDaysLeft();
            transCount = memPlans.getTransactionsLeft();
        }
    }

    /*class AddTrans extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < allTrans.size(); i++) {
                databaseClient.getAppDatabase()
                        .allTransactionsDao()
                        .insert(allTrans.get(i));
            }
            allTrans.clear();
            return null;
        }

        @Override
        protected void onPostExecute(Void name) {
            super.onPostExecute(name);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (arryAllTrans.size() > 0) {

                        tabView.setVisibility(View.VISIBLE);
                        sortContainer.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        welcome.setVisibility(View.INVISIBLE);
                    } else {
                        tabView.setVisibility(View.INVISIBLE);
                        sortContainer.setVisibility(View.INVISIBLE);
                        searchView.setVisibility(View.GONE);
                        welcome.setVisibility(View.VISIBLE);

                    }
                    //Sourav 20200930
                    setupTabs();

                    //}
                    if (arryStarTrans.size() > 0) {
                        issearchStar = true;
                        starredContainer.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        setupStarredRecycler();
                    } else {
                        issearchStar = false;
                    }

                    if (totalPages > 0) {
                        paginationView.setVisibility(View.VISIBLE);
                    } else {
                        paginationView.setVisibility(View.GONE);
                    }

                    dashboardItemAdapter = new DashboardItemAdapter(loadingCompletedInterface, arryAllTrans, activity);

                }
            });

        }
    }*/

    class DeleteTrans extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .allTransactionsDao()
                    .delete();

            return null;
        }

        @Override
        protected void onPostExecute(Void name) {
            super.onPostExecute(name);
        }
    }

    class DeleteTransToShow extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .allTransactionsDao()
                    .delete();

            return null;
        }

        @Override
        protected void onPostExecute(Void name) {
            super.onPostExecute(name);
            //setupTabs();

            new SelectPlans().execute();
            pulsator.start();
        }
    }

    class DeleteAllSelectId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .validateIdIdentityTypeDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (daysLeft > 0 && transCount > 0) {
                Intent intent = new Intent(activity, ValidateActivity.class);
                startActivity(intent);
                //getActivity().finish();
            } else {
                Intent i = new Intent(activity, MembershipActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }

        }
    }

    class DeleteAllLAD extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .validateIdIdentityTypeDao().deleteAll();

            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();

            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (daysLeft > 0) {
                Intent intent = new Intent(activity, LADStepsActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                Intent i = new Intent(activity, MembershipActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }

        }
    }

    class DeleteAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);


            /*if (daysLeft > 0 && transCount > 0) {
                Intent intent = new Intent(activity, Notarize_AlertActivity.class);
                startActivity(intent);
                //getActivity().finish();
            } else {
                Intent i = new Intent(activity, MembershipActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }*/

            if (daysLeft <= 0){
                Intent i = new Intent(activity, MembershipActivity.class);
                //i.putExtra("from", "dash");
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
            else if (transCount <=0 ){
                purchaseOrSkipNotification();
            } else {
                Intent intent = new Intent(activity, Notarize_AlertActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }

        }

    }

    class DeleteAllVEJ extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            if (daysLeft <= 0){
                Intent i = new Intent(activity, MembershipActivity.class);
                //i.putExtra("from", "dash");
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
            else if (transCount <=0 ){
                purchaseOrSkipNotification();
            } else {
                Intent intent = new Intent(activity, VEJ_Notarize_AlertActivity.class);
                startActivity(intent);
            }


            /*if (daysLeft > 0 && transCount > 0) {
                Intent intent = new Intent(activity, VEJ_Notarize_AlertActivity.class);
                startActivity(intent);
                //getActivity().finish();
            } else {
                Intent i = new Intent(activity, MembershipActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }*/

        }

    }

    private void purchaseOrSkipNotification() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_purchase_skip_dialog);

        Button btnPurcahse = dialog.findViewById(R.id.btnPurchase);
        Button btnSkip = dialog.findViewById(R.id.btnSkip);
        Button btnClose = dialog.findViewById(R.id.btnClose);


        btnPurcahse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, MembershipActivity.class);
                i.putExtra("from", "buypackage");
                startActivity(i);
                activity.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                dialog.cancel();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, VEJ_Notarize_AlertActivity.class);
                startActivity(intent);
                dialog.cancel();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void setupSearchRecycler(String query) {

        if (dashItemsAllList != null
                && dashItemsAllList.size() > 0) {
            dashItemsAllList.clear();
        }
        if (dashItemsValidateList != null
                && dashItemsValidateList.size() > 0) {
            dashItemsValidateList.clear();
        }

        if (arrayStarTransList != null
                && arrayStarTransList.size() > 0) {
            arrayStarTransList.clear();
        }

        if (dashItemsAuthList != null
                && dashItemsAuthList.size() > 0) {
            dashItemsAuthList.clear();
        }
        try {
            for (int i = 0; i < transByPage.getTransactions().getBody().size(); i++) {
                if (getSearch(transByPage.getTransactions().getBody().get(i), query)) {
                    if (transByPage.getTransactions().getBody().get(i).getStar()) {
                        arrayStarTransList.add(transByPage.getTransactions().getBody().get(i));
                    } else {
                        dashItemsAllList.add(transByPage.getTransactions().getBody().get(i));
                        if (transByPage.getTransactions().getBody().get(i).getTranType()
                                .equalsIgnoreCase("VID")) { //Verify ID
                            dashItemsValidateList.add(transByPage.getTransactions().getBody().get(i));
                        } else { // Notary-App®
                            dashItemsAuthList.add(transByPage.getTransactions().getBody().get(i));
                        }
                    }
                }
            }
        }catch (Exception e){

        }

        if (arrayStarTransList.size() > 0) {
            issearchStar = true;
            starredContainer.setVisibility(View.VISIBLE);
            //searchView.setVisibility(View.VISIBLE);
        } else {
            issearchStar = false;
            starredContainer.setVisibility(View.GONE);
            //searchView.setVisibility(View.GONE);
        }
        setupStarredRecycler();
        dashboard_tabAllFragment.onReceiveItemList("search",query, spinnerFilter.getSelectedItem().toString(), sortView.getText().toString());
        //dashboard_tabAuthFragment.onReceiveItemList("search",query);
        //dashboard_tabValidateFragment.onReceiveItemList("search",query);
        //dashboard_tabLADFragment.onReceiveItemList("search", query);

    }

    class SearchDB extends AsyncTask<String, Void, List<AllTransactions>> {

        @Override
        protected List<AllTransactions> doInBackground(String... params) {

            arryStarTrans.clear();
            String searchText = params[0];

            if (!searchText.equalsIgnoreCase("")) {
                searchText = '%' + searchText + '%';
                arryStarTrans = databaseClient.getAppDatabase().allTransactionsDao()
                        .getStarSearch(searchText);
            } else {
                arryStarTrans = databaseClient.getAppDatabase().allTransactionsDao()
                        .getAllStar();
            }

            return arryStarTrans;
        }

        @Override
        protected void onPostExecute(List<AllTransactions> transLocalArray) {
            super.onPostExecute(transLocalArray);
            setupStarredRecycler();
        }
    }

    public void getAllTransactions1(int page_no) throws JSONException {
        try {
            final String token = "AUTH_KEY";
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", savedEmail);
            params.put("pageSize", String.valueOf(pageSize));
            params.put("pageNo", String.valueOf(page_no));
            params.put("sortBy", "id");
            params.put("star", "1");
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
                                    /*if (dashItemsValidateList != null
                                            && dashItemsValidateList.size() > 0) {
                                        dashItemsValidateList.clear();
                                    }*/
                                    if (dashItemsValidateList.size() > 0) {
                                        dashItemsValidateList.clear();
                                    }

                                    /*if (arrayStarTransList != null
                                            && arrayStarTransList.size() > 0) {
                                        arrayStarTransList.clear();
                                    }*/
                                    if (arrayStarTransList.size() > 0) {
                                        arrayStarTransList.clear();
                                    }

                                    /*if (dashItemsAuthList != null
                                            && dashItemsAuthList.size() > 0) {
                                        dashItemsAuthList.clear();
                                    }*/
                                    if (dashItemsAuthList.size() > 0) {
                                        dashItemsAuthList.clear();
                                    }
                                    transByPage = gson.fromJson(response
                                            .body().string().trim(), type);
                                    if(transByPage != null
                                     && transByPage.getTransactions() != null
                                     && transByPage.getTransactions().getBody() != null
                                    && transByPage.getTransactions().getBody().size()>0) {
                                        for (int i = 0; i < transByPage.getTransactions().getBody().size(); i++) {
                                            if (transByPage.getTransactions().getBody().get(i).getStar()) {
                                                arrayStarTransList.add(transByPage.getTransactions().getBody().get(i));
                                            } else {
                                                dashItemsAllList.add(transByPage.getTransactions().getBody().get(i));
                                                if (transByPage.getTransactions().getBody().get(i).getTranType()
                                                        .equalsIgnoreCase("VID")) { //Verify ID
                                                    dashItemsValidateList.add(transByPage.getTransactions().getBody().get(i));
                                                } else { // Notary-App®
                                                    dashItemsAuthList.add(transByPage.getTransactions().getBody().get(i));
                                                }
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                                if (arrayStarTransList.size() > 0) {
                                    issearchStar = true;
                                    starredContainer.setVisibility(View.VISIBLE);
                                    //searchView.setVisibility(View.VISIBLE);
                                } else {
                                    issearchStar = false;
                                    starredContainer.setVisibility(View.GONE);
                                    //searchView.setVisibility(View.GONE);
                                }
                                setupStarredRecycler();

                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test", e11.toString());
        }
    }

    //(tranType='Notary-App®' AND signerName LIKE :query AND star = 0)
    // OR (tranType='Notary-App®' AND sealCode LIKE :query AND star = 0)
    // OR (tranType='Notary-App®' AND docuType LIKE :query AND star = 0)
    // OR (tranType='Notary-App®' AND transcomp LIKE :query AND star = 0)
    // OR (tranType='Notary-App®' AND tranId LIKE :query AND star = 0)
    // ORDER BY id DESC")
    public boolean getSearch(Body body, String searchString) {

        if (body.getSigner() != null
                && body.getSigner().size() > 0) {
            if (body.getSigner().get(0).getFirstName() != null
                    && body.getSigner().get(0).getFirstName().toLowerCase().contains(searchString)) {
                  return true;
            }

        }

        if (body.getSeal() != null
                && body.getSeal().getSealCode() != null
                && body.getSeal().getSealCode().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getDocType() != null
                && body.getDocType().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getStatus() != null
                && body.getStatus().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getTranid() != null
                && body.getTranid().toLowerCase().contains(searchString)) {
            return true;
        }
        return false;
    }

    private void checkForUpdate() {

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            //
                            IMMEDIATE,
                            // The current activity making the update request.
                            getActivity(),
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else {
                appReview();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != getActivity().RESULT_OK) {
//                log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                //checkForUpdate();

            }
        }
    }

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();

            //Log.d("SAVED_EMAIL", savedEmail);
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            try {
                getAllTransactions1(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CommissionListAPI();
        }

    }




}



