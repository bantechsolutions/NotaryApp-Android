package com.notaryapp.ui.activities.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class VerifiedActivity extends BaseActivity {
    //public static final int REF_VIEW_CONTAINER = R.id.onboardingActivityViewContainer;
    private Button btnLogin;
    private TextView txtClaim;
    private String userName, url;
    private String errorMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verified);

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

        btnLogin = findViewById(R.id.btnLogin);
        txtClaim = findViewById(R.id.txtClam_ur_profile);
        userName = getIntent().getStringExtra("userName");

        generateProfile();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifiedActivity.this, OnboardingBaseActivity.class));
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
                finish();
            }
        });
        txtClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(url != null && !url.equals("") && !url.equals("null")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                }
                else{

                    CustomDialog.notaryappDialogSingle(VerifiedActivity.this,"URL not found");
                }

            }
        });
    }


    private void generateProfile(){
        CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("Profileurl")) {
                            if (data.has("url")) {

                                url = data.getString("url");
                            }
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(VerifiedActivity.this,"Error Generating URL");
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                      CustomDialog.notaryappDialogSingle(VerifiedActivity.this,"Error Generating URL");

                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                // RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(VerifiedActivity.this, Utils.errorMessage(VerifiedActivity.this));
            }

            @Override
            public void onFetchStart() {

            }
        };

        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email",userName);

            }catch (Exception e){
                //e.printStackTrace();
            }

            postapiRequest.request(this,iJsonListener,params, AppUrl.GENERATE_PROFILE,"Profileurl");

        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

    }

}
