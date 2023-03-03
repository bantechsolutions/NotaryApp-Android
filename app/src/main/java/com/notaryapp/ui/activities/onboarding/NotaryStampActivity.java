package com.notaryapp.ui.activities.onboarding;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.notaryapp.R;
import com.notaryapp.adapter.Dashboard_NotaryStampAdapter;
import com.notaryapp.components.CarouselEffectTransformer;
import com.notaryapp.interfacelisterners.CaptureStampListerner;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.StampModel;
import com.notaryapp.model.webresponse.SealResponse;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AddLicense;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.LicenseStampCheck;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.activities.verifyauthenticate.SealVideoActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotaryStampActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private ImageView videoImage;
    private ViewPager viewpagerTop;
    private TabLayout tabLayout;
    public static final int ADAPTER_TYPE_TOP = 1;
    private Button btnClose, btnBack, btnContinue;
    private ImageView imgInfo;
    private ArrayList<AddLicense> listLicenseNo = new ArrayList<>();
    //  private ArrayList<AddLicense> listLicenseCorousel = new ArrayList<>();
    private ArrayList<String> licenseNo = new ArrayList<>();

    private DatabaseClient databaseClient;
    private boolean tick = false, captureFlag = false;

    private int position, stampCount;
    private String btnName, selectedItem, userName;
    private Spinner spinnerLicense;
    private IJsonListener iJsonListener;
    private Info info;

    private LicenseStampCheck licenseStampCheck;
    private List<LicenseStampCheck> arryStampChk;

    private ArrayList<String> licenseList;
    private ArrayList<String> stateList;
    private String selectedLicenseNo, selectedState;
    private StampModel stampModel;
    private StampModel AllstampModel;
    private ArrayList<StampModel> stampArray;
    private ArrayList<StampModel> AllStampArray;

    private int licensePosition = -1;
    private int stampPosition = -1;
    private CaptureStampListerner captureStampListerner;
    private String sName;
    private LoadingCompletedInterface loadingCompletedInterface;
    private Button btnSkipAddingSeals;
    private ArrayList<SealResponse> sealResponse;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notary_stamps);
        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();
            }
        }.start();
        setTimer();
        /*time out*/

        init();
        spinnerLicense.setOnItemSelectedListener(this);

        captureStampListerner = new CaptureStampListerner() {
            @Override
            public void captureStamp(String from, int position, String licenseNo, String state, boolean flag, String stampName) {

                sName = stampName;
                Intent intent = new Intent(getApplicationContext(), NotaryBaseActivity.class);
                intent.putExtra("From", from);
                intent.putExtra("stampName", stampName);
                intent.putExtra("Position", position);
                intent.putExtra("License", licenseNo);
                intent.putExtra("Flag", flag);
                intent.putExtra("State", state);
                startActivity(intent);

            }
        };

        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {

                CustomDialog.cancelProgressDialog();
            }
        };

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // is all stamps added
                callAllStampAdded();

            }
        });


        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(NotaryStampActivity.this, info.getSelectId());
            }
        });

        videoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SealVideoActivity.class);
                intent.putExtra("from", "NotaryStampActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        btnSkipAddingSeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notaryappDialogSingle(NotaryStampActivity.this);
            }
        });

    }

    private void callAllStampAdded() {

        boolean allAdded = false;

        if (licenseList.size() > 0) {

            for (int i = 0; i < licenseList.size(); i++) {

                if (AllStampArray.size() > 0) {

                    for (int j = 0; j < AllStampArray.size(); j++) {
                        if (licenseList.get(i).equalsIgnoreCase(AllStampArray.get(j).getLicenseNo()) &&
                                stateList.get(i).equalsIgnoreCase(AllStampArray.get(j).getState())) {
                            allAdded = true;
                            break;
                        } else {
                            allAdded = false;
                        }
                    }
                    if (!allAdded) {
                        CustomDialog.notaryappDialogSingle(NotaryStampActivity.this,
                                "Please take the photo of all the seal used with commission number ");
                        break;
                    }
                }

            }

            if (allAdded) {
                Intent intent = new Intent(NotaryStampActivity.this, NotaryPledgeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
                CustomDialog.notaryappDialogSingle(NotaryStampActivity.this,
                        "Please take the photo of all the seal used with commission number ");
            }

        }

    }

    /**
     * Initialize all required variables
     */
    private void init() {

        databaseClient = DatabaseClient.getInstance(this);

        imgInfo = findViewById(R.id.infoIcon);
        btnBack = findViewById(R.id.btn_lice_list_back);
        btnClose = findViewById(R.id.btn_lice_list_close);
        btnContinue = findViewById(R.id.btnComplete);
        //imgTik = findViewById(R.id.img_tick);
        spinnerLicense = findViewById(R.id.spinnerLicense);
        //  btnCapture  = findViewById(R.id.btnCaptureStamp);
        videoImage = findViewById(R.id.videoImage);

        tabLayout = findViewById(R.id.tab_layout);

        btnSkipAddingSeals = findViewById(R.id.btnSkipAddingSeals);

        viewpagerTop = findViewById(R.id.viewpagerTop);
        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(this)); // Set transformer
        initIJsonListener();

        new GeInfo().execute();
        new GetLicenses().execute();
    }

    /**
     * Setup viewpager and it's events
     */
    private void setupViewPager() {

        Dashboard_NotaryStampAdapter adapter = new Dashboard_NotaryStampAdapter(this, loadingCompletedInterface, captureStampListerner, selectedLicenseNo, selectedState, stampArray);
        viewpagerTop.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewpagerTop, true);

        viewpagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageSelected(int position) {
                stampPosition = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        if (!sName.equalsIgnoreCase("")) {
            if (licensePosition != -1) {

                viewpagerTop.setCurrentItem(stampPosition);
            }
        } else {
            CustomDialog.showProgressDialog(this);
            if (stampPosition > 0) {
                viewpagerTop.setCurrentItem(stampPosition);
            }
        }
        CustomDialog.cancelProgressDialog();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.licensePosition = position;
        selectedLicenseNo = licenseList.get(position);
        selectedState = stateList.get(position);

        callGetStampsAPI();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("selectedLicenseNo", licensePosition);
        outState.putInt("selectedStamp", stampPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (licenseList != null) {
            if (licensePosition != -1)
                callGetStampsAPI();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void callGetStampsAPI() {
        CustomDialog.showProgressDialog(NotaryStampActivity.this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(NotaryStampActivity.this, iJsonListener, params, AppUrl.VA_STAMPS_DOWNLOAD, "StampsList");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class GetLicenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            userName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            callLicenseListAPI();
        }
    }

    private void callLicenseListAPI() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userName", userName);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(this, iJsonListener, params, AppUrl.LICENSE_LIST, "LicenseCall");

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
                String name, license_no, address, city, state, country, phone;
                try {
                    if (data != null) {
                        if (type.equals("LicenseCall")) {
                            if (data.has("success")) {
                                licenseList = new ArrayList<>();
                                stateList = new ArrayList<>();

                                JSONArray license_array = data.getJSONArray("success");
                                if (license_array.length() != 0) {
                                    for (int i = 0; i < license_array.length(); i++) {
                                        JSONObject json_inside = license_array.getJSONObject(i);
                                        license_no = json_inside.getString("notary_license_number");
                                        //state = json_inside.getString("notary_license_state_name");
                                        state = json_inside.getString("notary_license_state");

                                        licenseList.add(license_no);
                                        stateList.add(state);
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NotaryStampActivity.this, R.layout.identity_type, licenseList);
                                    spinnerLicense.setAdapter(dataAdapter);
                                } else {
                                    Toast.makeText(NotaryStampActivity.this, "No License Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (type.equals("StampsList")) {

                            if (data.has("Stamps")) {

                                JSONArray stamp_array = data.getJSONArray("Stamps");
                                stampArray = new ArrayList<>();
                                AllStampArray = new ArrayList<>();

                                if (stamp_array.length() != 0) {
                                    for (int i = 0; i < stamp_array.length(); i++) {
                                        stampModel = new StampModel();
                                        AllstampModel = new StampModel();

                                        JSONObject json_inside = stamp_array.getJSONObject(i);

                                        AllstampModel.setLicenseNo(json_inside.getString("licenceNum"));
                                        AllstampModel.setState(json_inside.getString("state"));
                                        AllstampModel.setUrl(json_inside.getString("url"));
                                        AllstampModel.setStampName(json_inside.getString("stampFileName"));

                                        if (selectedLicenseNo.equalsIgnoreCase(json_inside.getString("licenceNum"))) {
                                            if (selectedState.equalsIgnoreCase(json_inside.getString("state"))) {

                                                stampModel.setLicenseNo(json_inside.getString("licenceNum"));
                                                stampModel.setState(json_inside.getString("state"));
                                                stampModel.setUrl(json_inside.getString("url"));
                                                stampModel.setStampName(json_inside.getString("stampFileName"));

                                                stampArray.add(stampModel);
                                            }
                                        }
                                        AllStampArray.add(AllstampModel);
                                    }
                                }

                                if (stampArray != null
                                        && !(stampArray.size() > 0)) {
                                    if (btnSkipAddingSeals != null) {
                                        btnSkipAddingSeals.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (btnSkipAddingSeals != null) {
                                        btnSkipAddingSeals.setVisibility(View.GONE);
                                    }
                                }
                                setupViewPager();
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {
            }

            @Override
            public void onFetchStart() {
            }
        };
    }


    private void notaryappDialogSingle(final Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_default_seal);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        //text.setText(message);
        text.setText(Utils.getSealString(licenseList));
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Utils.isConnected()) {
                        getDefaultSeal();
                    } else {
                        Toast.makeText(NotaryStampActivity.this,
                                "Please Check your Internet Conectivity!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void getDefaultSeal() throws JSONException {
        try {
            final String token = AppUrl.Authorization_Key;
            CustomDialog.showProgressDialog(NotaryStampActivity.this);
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("username", userName);

            Call<ResponseBody> call = service.getDefaultSeal(token, jsonObject);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(final Void... params) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<SealResponse>>() {
                                }.getType();
                                try {
                                    sealResponse = gson.fromJson(response
                                            .body().string().trim(), type);
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                                if (sealResponse != null
                                        && sealResponse.size() > 0) {
                                    if (dialog != null) {
                                        Toast.makeText(NotaryStampActivity.this, "Default Seal Set Successfully !", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        callGetStampsAPI();
                                    }
                                } else {
                                    Toast.makeText(NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
                                }
                                CustomDialog.cancelProgressDialog();
                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test", t.toString());
                    Toast.makeText(NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
                    CustomDialog.cancelProgressDialog();
                }

            });
        } catch (Exception e11) {
            Log.v("test", e11.toString());
            Toast.makeText(NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
            CustomDialog.cancelProgressDialog();
        }
    }
}
