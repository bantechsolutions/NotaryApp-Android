package com.notaryapp.ejournal.fragments.addseal;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.notaryapp.R;
import com.notaryapp.components.CarouselEffectTransformer;
import com.notaryapp.ejournal.adapter.VEJ_VAStampListAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AddStamp;
import com.notaryapp.roomdb.entity.VaLicense;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class VEJ_Notarize_AddSealFragment extends Fragment {

    private View view;
    private Button nextBtn;
    private Button btnBack, btnClose;
    public static final int REF_VIEW_CONTAINER = R.id.addSealContainer;
    private ArrayList<AddStamp> stampList = new ArrayList<>();
    private Integer stampCount, position;
    private DatabaseClient databaseClient;
    private static final int ADAPTER_TYPE_TOP = 1;
    private RecyclerView recyclerViewStamps;
    private GridLayoutManager layoutManager;
    private Context context;
    private IJsonListener iJsonListener;
    private String userName;
    private ArrayList<String> listLicenseNo = new ArrayList<>();
    private ViewPager viewPager;
    private ArrayList<VaLicense> arryLicense;
    private VaLicense vaLicense;
    private String update = "";
    private TextView titleText, text1;

    public VEJ_Notarize_AddSealFragment() {
        // Required empty public constructor
    }

    public VEJ_Notarize_AddSealFragment(String update) {

        this.update = update;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notarize_add_seal, container, false);
        init();
        //  setupRecycler();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  loadFragment(new Notarize_SealCodeFragment("1234567")) ;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void init() {
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        nextBtn = view.findViewById(R.id.nextBtn);
        titleText = view.findViewById(R.id.titleText);
        text1 = view.findViewById(R.id.text1);

        titleText.setVisibility(View.GONE);
        text1.setText("Please ensure that you choose the appropriate seal that you will be using for the NotaryApp™ e-Journal process.");
        //  recyclerViewStamps = view.findViewById(R.id.recyclerViewStamps);
        viewPager = view.findViewById(R.id.viewpagerTop);
        viewPager.setClipChildren(false);
        viewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(false, new CarouselEffectTransformer(getActivity())); // Set transformer
        arryLicense = new ArrayList<>();
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
       new GetNotary().execute();

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(), REF_VIEW_CONTAINER, fragment);
    }

    //Setup recycler
    private void setupViewPager() {

        VEJ_VAStampListAdapter adapter = new VEJ_VAStampListAdapter(getActivity(),update, stampList, ADAPTER_TYPE_TOP);
        viewPager.setAdapter(adapter);

        // tabLayout.setupWithViewPager(viewpagerTop, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                int width = viewPagerBackground.getWidth();
//                viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //   viewPagerBackground.setCurrentItem(index);
                }

            }
        });
    }

    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.card:
                if (view.getTag() != null) {
                    position = Integer.parseInt(view.getTag().toString());
                    Toast.makeText(context, "card clickeddddd " + position, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    class AddLicense extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .vaLicenseDao()
                    .deleteAll();
            for (int i = 0; i < arryLicense.size(); i++) {
                databaseClient.getAppDatabase()
                        .vaLicenseDao()
                        .insert(arryLicense.get(i));

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void name) {
            super.onPostExecute(name);
            //   setupViewPager();
            new GetStamps().execute();
        }

    }

    class GetStamps extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
           // for (int i = 0; i < arryLicense.size(); i++) {
                callGetStampsAPI();//arryLicense.get(i).getLicense());
          //  }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            //  setupViewPager();
        }
    }

    class GetNotary extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            userName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return userName;
        }

        @Override
        protected void onPostExecute(String userName) {
            super.onPostExecute(userName);

            callLicenseListAPI();
        }
    }

    private void callGetStampsAPI() {//String selectedItem){
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);
                // params.put("licenceNum",selectedItem);
                //  params.put("licenceNum","2274894");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.VA_STAMPS_DOWNLOAD, "StampsList");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callLicenseListAPI() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);
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

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                String license_no;
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("LicenseList")) {
                            if (data.has("success")) {
                                listLicenseNo.clear();
                                JSONArray license_array = data.getJSONArray("success");
                                if (license_array.length() != 0) {
                                    for (int i = 0; i < license_array.length(); i++) {
                                        JSONObject json_inside = license_array.getJSONObject(i);
                                        license_no = json_inside.getString("notary_license_number");
                                        listLicenseNo.add(license_no);
                                        vaLicense = new VaLicense(license_no, false,"");
                                        arryLicense.add(vaLicense);
                                    }
                                    new AddLicense().execute();
                                    CustomDialog.cancelProgressDialog();
                                    //   listLicenseNo.add("2323232");
                                    callGetStampsAPI();
                                } else {
                                    Toast.makeText(getActivity(), "No License Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (type.equals("StampsList")) {
//                            Log.e("TAG", "test");
                            stampList.clear();
                            if (data.has("Stamps")) {
                                String stampPath = null, licenseNum,license_state;
                                JSONArray stamp_array = data.getJSONArray("Stamps");
                                if (stamp_array.length() != 0) {
                                    for (int i = 0; i < stamp_array.length(); i++) {
                                        JSONObject json_inside = stamp_array.getJSONObject(i);
                                        stampPath = json_inside.getString("url");
                                        licenseNum = json_inside.getString("licenceNum");
                                        license_state = json_inside.getString("state");
//                                        Log.i("IMGPATH ", stampPath);
                                        stampList.add(new AddStamp(licenseNum, stampPath,license_state));
                                    }

                                } else {

                                }
                            }
                        }
                        setupViewPager();
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();

                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    // CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                //   CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

}