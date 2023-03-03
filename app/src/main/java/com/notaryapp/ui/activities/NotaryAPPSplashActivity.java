package com.notaryapp.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.jumio.core.enums.JumioDataCenter;
import com.notaryapp.R;
import com.notaryapp.components.GifImage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class notaryappSplashActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 199;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private UserReg userReg;
    private DatabaseClient databaseClient;

    private String jumio_api ="" ;
    private String jumio_secret ="" ;
    private JumioDataCenter dataCenter = JumioDataCenter.US;
    private IJsonListener iJsonListener;
    private String password,selectId,profile,license,address,govtId,docLicense,stamp,captureStamp,
    mulLicense,alternateId;
    String profileLicenses,verifyID,vaSigners,vaIDtype,vaSigndocuments,witnessIDtype,vaAddseal;
    String vaAdddocuments,vaAddLocation,apnNumber,confirmLocation,notaryLicense;
    private JumioKeys jumioKeys;
    private Info info;
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    private GifImage gifImageView;
    private Boolean flagPause = false, flagWFChange = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        //checkForUpdate();

        gifImageView = findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.dual_ball_gif);

        databaseClient =  DatabaseClient.getInstance(notaryappSplashActivity.this);
        if (Utils.isConnected()){
            flagWFChange = false;
            flagPause = false;
            gifImageView.setVisibility(View.VISIBLE);
            initIJsonListener();
            getJumioToken();
            getInfo();

        } else {
            CustomDialog.notaryappDialogSingle(notaryappSplashActivity.this, "Please check your internet connection.");
            gifImageView.setVisibility(View.GONE);
            /*final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog_single);

            TextView text = dialog.findViewById(R.id.alertMess);
            text.setText("Please check your internet connection.");

            Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnOk);
            dialogAllowButton.setText("OK");
            dialogAllowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // DeleteImages().execute();
                    finish();
                    dialog.dismiss();

                }
            });
            dialog.show();*/

        }


        userReg = new UserReg();
        userReg.setFirstName("");
        userReg.setLastName("");
        userReg.setEmail("");
        userReg.setPhoneNo("");
        userReg.setSuccess(true);
        new SaveUser().execute();

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
                            IMMEDIATE,
                            // The current activity making the update request.
                            notaryappSplashActivity.this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(notaryappSplashActivity.this, OnboardingBaseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        });
    }

    class SaveUser extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            int count =databaseClient.getAppDatabase()
                    .userRegDao()
                    .getCount();
            //adding to database
            if(count == 0) {
                databaseClient.getAppDatabase()
                        .userRegDao()
                        .insert(userReg);
            }
            return "";
        }
        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }
    }
    class InsertJumioKeys extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            int count =databaseClient.getAppDatabase()
                    .jumioKeysDao()
                    .getCount();
            //adding to database
            if(count == 0) {
                databaseClient.getAppDatabase()
                        .jumioKeysDao()
                        .insert(jumioKeys);
            }else{
                databaseClient.getAppDatabase()
                        .jumioKeysDao()
                        .update(jumioKeys.getJumiokey()
                                ,jumioKeys.getJumiosecret());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }
    class InfoDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            int count =databaseClient.getAppDatabase()
                    .infoDao()
                    .getCount();
            //adding to database
            if(count == 0) {
                databaseClient.getAppDatabase()
                        .infoDao()
                        .insert(info);
            }else{
                databaseClient.getAppDatabase()
                        .infoDao()
                        .update(info.getPassword(),info.getSelectId(),info.getProfile()
                                ,info.getLicense());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            CustomDialog.cancelProgressDialog();
        }

    }

    private void getJumioToken() {

        try{
            GETAPIRequest getApiRequest=new GETAPIRequest();
            //   JSONObject params=new JSONObject();
            //String url = "YOUR_LIVE_URLapi/v1/notaryapp/registration/getServerNotification?userName=test_e@teste.com";
            getApiRequest.request(this,iJsonListener, AppUrl.GET_JUMIO_KEYS ,"Jumio");
            //getApiRequest.request(this,iJsonListener, url ,"Jumio");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void getInfo() {

        try{
            GETAPIRequest getApiRequest=new GETAPIRequest();
            getApiRequest.request(this,iJsonListener, AppUrl.GET_ALLINFO ,"Info");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if(type.equals("Jumio")) {
                        JSONArray jumio_keys = data.getJSONArray("config");

                            String api_key = "";
                            if (jumio_keys.length() != 0) {
                                for (int i = 0; i < jumio_keys.length(); i++) {
                                    JSONObject json_inside = jumio_keys.getJSONObject(i);
                                    api_key = json_inside.getString("apiKey");
                                    if (api_key.equals("api_token")) {
                                        jumio_api = json_inside.getString("apiValue");
                                    } else {
                                        jumio_secret = json_inside.getString("apiValue");
                                    }
                                }
                                jumioKeys = new JumioKeys(jumio_api,jumio_secret);
                                new InsertJumioKeys().execute();
                            }
                        }else if(type.equals("Info")){
                            JSONArray jumio_keys = data.getJSONArray("info");
                            if (jumio_keys.length() != 0) {
//                                //Log.w("TAGGGGGGG",jumio_keys.toString());
                                String infoKey, infoValue;
                                for (int i = 0; i < jumio_keys.length(); i++) {
                                    JSONObject json_inside = jumio_keys.getJSONObject(i);
                                    infoKey = json_inside.getString("infoKey");
                                    if (infoKey.equals("password")) {
                                        password = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("notaryappID")) {
                                        selectId = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("profilePhoto")) {
                                        profile = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("licenseNo")) {
                                        license = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("address")) {
                                        address = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("caGovernmentID")) {
                                        govtId = json_inside.getString("infoValue");
                                    }
                                    else if (infoKey.equals("caLicense")) {
                                        docLicense = json_inside.getString("infoValue");
                                    }
                                    else if (infoKey.equals("caStamp")) {
                                        stamp = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("mulLicenses")) {
                                        mulLicense = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("capturingStamp")) {
                                        captureStamp = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("alternateID")) {
                                        alternateId = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("profileLicenses")) {
                                        profileLicenses = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("verifyID")) {
                                        verifyID = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("vaSigners")) {
                                        vaSigners = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("vaIDtype")) {
                                        vaIDtype = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("vaSigndocument")) {
                                        vaSigndocuments = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("witnessIDtype")) {
                                        witnessIDtype = json_inside.getString("infoValue");
                                    } else if (infoKey.equals("vaAddseal")) {
                                        vaAddseal = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("vaAdddocuments")) {
                                        vaAdddocuments = json_inside.getString("infoValue");
                                    }else if (infoKey.equals("vaAddLocation")) {
                                        vaAddLocation = json_inside.getString("infoValue").trim();
                                    }else if (infoKey.equals("vaApnnumber")) {
                                        apnNumber = json_inside.getString("infoValue").trim();
                                    }else if (infoKey.equals("confirmLocation")) {
                                        confirmLocation = json_inside.getString("infoValue").trim();
                                    }else if (infoKey.equals("notaryLicense")) {
                                        notaryLicense = json_inside.getString("infoValue").trim();
                                    }
                                    else {
                                    }

                                }
                                info = new Info(password, selectId, profile, license,address,govtId,
                                        docLicense,stamp,mulLicense,captureStamp,alternateId,profileLicenses,verifyID,
                                        vaSigners,vaIDtype,vaSigndocuments,witnessIDtype,vaAddseal,vaAdddocuments,vaAddLocation,
                                        apnNumber,confirmLocation,notaryLicense);
                                new InfoDetails().execute();
                                //checkForUpdate();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(notaryappSplashActivity.this, OnboardingBaseActivity.class);
                                        //Intent intent = new Intent(notaryappSplashActivity.this, PaymentFailedActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, SPLASH_DISPLAY_LENGTH);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (flagPause){
            if (Utils.isConnected()){
                flagWFChange=false;
                gifImageView.setVisibility(View.VISIBLE);
                initIJsonListener();
                getJumioToken();
                getInfo();
            } else {
                CustomDialog.notaryappDialogSingle(notaryappSplashActivity.this, "Please check your internet connection.");
                gifImageView.setVisibility(View.GONE);

            }
        } else {
            flagPause = true;
        }

    }

    /*@Override
    protected void onPause() {
        super.onPause();
        if (flagPause){
            if (Utils.isConnected()){
                flagWFChange=false;
                gifImageView.setVisibility(View.VISIBLE);
                initIJsonListener();
                getJumioToken();
                getInfo();
            } else {
                CustomDialog.notaryappDialogSingle(notaryappSplashActivity.this, "Please check your internet connection.");
                gifImageView.setVisibility(View.GONE);

            }
        } else {
            flagPause = true;
        }
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            notaryappSplashActivity.this,
                                            MY_REQUEST_CODE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
//                log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
                //checkForUpdate();
                //finish();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            if (flagWFChange){
                try {
                    if (Utils.isConnected()){
                        flagWFChange = false;
                        gifImageView.setVisibility(View.VISIBLE);
                        initIJsonListener();
                        getJumioToken();
                        getInfo();

                    }
                } catch (Exception e){

                }

            }/* else {
                flagWFChange = true;
            }*/

        }

    }
}
