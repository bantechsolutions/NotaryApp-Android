package com.notaryapp.ui.activities.onboarding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class NotaryPledgeActivity extends BaseActivity {

    private Button close, confirm, back;
    private CheckBox checkMain;
    private String errorMess, userName, name, licenseNum = "";
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private UserReg userReg;
    private TextView txtPledge;
    private String deviceId, deviceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notary_pledge);
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

        //Initialize
        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkMain.isChecked()) {
                    //callDeviceMarker();
                    callPledgeCompleteApi();
                } else {
                    CustomDialog.notaryappDialogSingle(NotaryPledgeActivity.this, "Please take pledge");
                }
            }
        });


    }

    @SuppressLint("HardwareIds")
    private void init() {
        close = findViewById(R.id.btn_pledge_close);
        confirm = findViewById(R.id.btnConfirm);
        back = findViewById(R.id.btn_pledge_back);
        checkMain = findViewById(R.id.checkBox);
        txtPledge = findViewById(R.id.txtMain);

        databaseClient = DatabaseClient.getInstance(this);
        initIJsonListener();
        new SelectLicense().execute();

        errorMess = this.getResources().getString(R.string.networkerror);
        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceName = Build.MANUFACTURER + "-" + Build.MODEL;
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void callLicenseListAPI() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userName", userName);
                //  params.put("pledge",true);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.LICENSE_LIST, "LicenseList");
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
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("success")) {
                            if (type.equals("NotaryPledge")) {
                                String success = data.getString("success");
                                if (success.equals("1")) {

                                    Intent intent = new Intent(NotaryPledgeActivity.this, VerifiedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("userName", userName);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                }
                            } else if (type.equals("DeviceMarker")) {
                                String success = data.getString("success");
                                if (success.equals("1")) {

                                    callPledgeCompleteApi();

                                } else {
                                    CustomDialog.notaryappDialogSingle(NotaryPledgeActivity.this, "Notary registration is restricted to one account per device.");
                                }
                            } else if (type.equals("LicenseList")) {
                                if (data.has("success")) {
                                    // listLicenseNo.clear();
                                    JSONArray license_array = data.getJSONArray("success");
                                    if (license_array.length() != 0) {
                                        for (int i = 0; i < license_array.length(); i++) {
                                            JSONObject json_inside = license_array.getJSONObject(i);
                                            licenseNum += json_inside.getString("notary_license_number") + "<br/> ";
                                        }
//                                        txtPledge.setText("By clicking continue, I declare under the penalty of perjury that my name is " +
//                                                name + " and my Notary Commission number(s) : "+ Utils.removeLastChar(licenseNum)+".");

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            txtPledge.setText(Html.fromHtml("By clicking continue, I declare under the penalty of perjury that my name is " +
                                                    name + " and my Notary Commission number(s) : <b><br/>" + licenseNum + "</b>", Html.FROM_HTML_MODE_LEGACY));
                                        } else {
                                            txtPledge.setText(Html.fromHtml("By clicking continue, I declare under the penalty of perjury that my name is " +
                                                    name + " and my Notary Commission number(s) : <b><br/>" + licenseNum + ".<b>"));
                                        }
                                    } else {
                                        // Toast.makeText(NotaryStampActivity.this, "No commission number added!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(NotaryPledgeActivity.this, "Error! No data fetched");
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(NotaryPledgeActivity.this, errorMess);

                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.notaryappDialogSingle(NotaryPledgeActivity.this, errorMess);
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    private void callDeviceMarker() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);
                params.put("deviceId", deviceId);
                params.put("idType", AppUrl.DEVICE_TYPE);
                params.put("deviceBrand", deviceName);
                params.put("os", AppUrl.DEVICE);

            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(this, iJsonListener, params, AppUrl.DEVICE_TRACER, "DeviceMarker");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callPledgeCompleteApi() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userName", userName);
                params.put("pledge", true);

            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(this, iJsonListener, params, AppUrl.NOTARY_PLEDGE, "NotaryPledge");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    class SelectLicense extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            userReg = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getUserData();

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            userName = userReg.getEmail();
            name = userReg.getFirstName() + " " + userReg.getLastName();
            callLicenseListAPI();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

    }
}
