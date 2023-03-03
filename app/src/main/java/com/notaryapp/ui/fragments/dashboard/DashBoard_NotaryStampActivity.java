package com.notaryapp.ui.fragments.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.notaryapp.ui.activities.onboarding.NotaryBaseActivity;
import com.notaryapp.ui.activities.verifyauthenticate.SealVideoActivity;
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
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoard_NotaryStampActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView videoImage;
    private ViewPager viewpagerTop;
    private TabLayout tabLayout;
    private ArrayList<String> licenseList;
    private ArrayList<String> stateList;

    private DatabaseClient databaseClient;
    private String userName, selectedLicenseNo, selectedState;
    private Spinner spinnerLicense;
    private IJsonListener iJsonListener;
    private StampModel stampModel;
    private ArrayList<StampModel> stampArray;
    private int licensePosition = -1;
    private int stampPosition = -1;
    private CaptureStampListerner captureStampListerner;
    private String sName;
    private Activity activity;
    private Button back;
    private LoadingCompletedInterface loadingCompletedInterface;
    private Button btnSkip;
    private Dialog dialog;
    private ArrayList<SealResponse> sealResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard_addstamp);
        activity = this;

        databaseClient = DatabaseClient.getInstance(activity);
        init();
        spinnerLicense.setOnItemSelectedListener(this);

        captureStampListerner = new CaptureStampListerner() {
            @Override
            public void captureStamp(String from, int position, String licenseNo, String state, boolean flag, String stampName) {

                sName = stampName;

                Intent intent = new Intent(activity, NotaryBaseActivity.class);
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

        videoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SealVideoActivity.class);
                intent.putExtra("from", "NotaryStampActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });


    }

    private void init() {
        back = findViewById(R.id.btn_pro_back);
        spinnerLicense = findViewById(R.id.spinnerLicense);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewpagerTop = (ViewPager) findViewById(R.id.viewpagerTop);
        videoImage = findViewById(R.id.videoImage);
        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(activity)); // Set transformer

        btnSkip = findViewById(R.id.btnSkip);


        initIJsonListener();
        new SelectLicense().execute();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notaryappDialogSingle(DashBoard_NotaryStampActivity.this);
            }
        });
    }

    private void setupViewPager() {

        Dashboard_NotaryStampAdapter adapter = new Dashboard_NotaryStampAdapter(activity,
                loadingCompletedInterface, captureStampListerner, selectedLicenseNo, selectedState, stampArray);
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
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                }

            }
        });

        if (sName != null
                && !sName.equalsIgnoreCase("")) {
            if (licensePosition != -1) {

                viewpagerTop.setCurrentItem(stampPosition);
            }
        } else {
            CustomDialog.cancelProgressDialog();
            if (stampPosition > 0) {
                viewpagerTop.setCurrentItem(stampPosition);
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.licensePosition = position;
        selectedLicenseNo = licenseList.get(position);
        selectedState = stateList.get(position);
        callGetStampsAPI(selectedLicenseNo);
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
                callGetStampsAPI(licenseList.get(licensePosition));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void callLicenseListAPI() {
        CustomDialog.showProgressDialog(activity);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(activity, iJsonListener, params, AppUrl.LICENSE_LIST, "LicenseList");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callGetStampsAPI(String selectedLicenseNo) {
        CustomDialog.showProgressDialog(activity);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {

                params.put("userName", userName);
                params.put("licenceNum", selectedLicenseNo);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(activity, iJsonListener, params, AppUrl.STAMPS_DOWNLOAD, "StampsList");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {

        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                String license_no, state;
                try {

                    if (data != null) {
                        if (type.equals("LicenseList")) {
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
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.identity_type, licenseList);
                                    spinnerLicense.setAdapter(dataAdapter);

                                } else {
                                    Toast.makeText(activity, "No License Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (type.equals("StampsList")) {

                            if (data.has("Stamps")) {

                                JSONArray stamp_array = data.getJSONArray("Stamps");
                                stampArray = new ArrayList<>();

                                if (stamp_array.length() != 0) {
                                    for (int i = 0; i < stamp_array.length(); i++) {
                                        stampModel = new StampModel();
                                        JSONObject json_inside = stamp_array.getJSONObject(i);

                                        if (selectedState.equalsIgnoreCase(json_inside.getString("state"))) {

                                            stampModel.setLicenseNo(json_inside.getString("licenceNum"));
                                            stampModel.setState(json_inside.getString("state"));
                                            stampModel.setUrl(json_inside.getString("url"));
                                            stampModel.setStampName(json_inside.getString("stampFileName"));

                                            stampArray.add(stampModel);
                                        }
                                    }

                                }
                                if (stampArray != null
                                        && !(stampArray.size() > 0)) {
                                    btnSkip.setVisibility(View.VISIBLE);
                                } else {
                                    btnSkip.setVisibility(View.GONE);
                                }
                                setupViewPager();
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    class SelectLicense extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            userName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            callLicenseListAPI();
        }

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
                        Toast.makeText(DashBoard_NotaryStampActivity.this,
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
            CustomDialog.showProgressDialog(DashBoard_NotaryStampActivity.this);
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
                                        Toast.makeText(DashBoard_NotaryStampActivity.this, "Default Seal Set Successfully !", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        callGetStampsAPI(selectedLicenseNo);
                                    }
                                } else {
                                    Toast.makeText(DashBoard_NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
                                }
                                CustomDialog.cancelProgressDialog();
                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test", t.toString());
                    Toast.makeText(DashBoard_NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
                    CustomDialog.cancelProgressDialog();
                }

            });
        } catch (Exception e11) {
            Log.v("test", e11.toString());
            Toast.makeText(DashBoard_NotaryStampActivity.this, "Default Seal Set Fail ! Please try Again", Toast.LENGTH_LONG).show();
            CustomDialog.cancelProgressDialog();
        }
    }

}
