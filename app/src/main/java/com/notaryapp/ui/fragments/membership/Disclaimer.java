package com.notaryapp.ui.fragments.membership;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;

import org.json.JSONObject;

public class Disclaimer extends BaseActivity {

    Button close;
    private WebView web_view;
    private String htmlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandservice);

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


        close = findViewById(R.id.btn_close);
        web_view = findViewById(R.id.webView);

        getDocTypes();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getDocTypes() {

        CustomDialog.showProgressDialog(this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("GET_PRIVACY_POLICY")) {
                            if(data.has("disclaimer")){

                                htmlString = data.getString("disclaimer");

                                loadDatatoHtml();
                            }

                        }

                    } else {
                        CustomDialog.cancelProgressDialog();
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.cancelProgressDialog();
            }

            @Override
            public void onFetchStart() {
                CustomDialog.cancelProgressDialog();
            }
        };

        try{
            GETAPIRequest getApiRequest=new GETAPIRequest();
            getApiRequest.request(this,iJsonListener, AppUrl.GET_DISCLAIMER ,"GET_PRIVACY_POLICY");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void loadDatatoHtml() {

        web_view.requestFocus();
        web_view.getSettings().setLightTouchEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setGeolocationEnabled(true);
        web_view.setSoundEffectsEnabled(true);

        web_view.loadDataWithBaseURL(null,htmlString, "text/html", "utf-8", null);

        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                if (progress == 100) {
                    CustomDialog.cancelProgressDialog();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
        finish();
    }
}
