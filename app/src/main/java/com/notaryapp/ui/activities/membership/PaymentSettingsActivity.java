package com.notaryapp.ui.activities.membership;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class PaymentSettingsActivity extends BaseActivity {

    private Button close, unsubscribeBtn;
    private TextView unsubscribeDesc;
    private WebView web_view;
    private String htmlString;
    private DatabaseClient databaseClient;
    private String subsciptionId;
    private VACustomer vaCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_settings);

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

        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        new SelectPlans().execute();
        getDocTypes();

        close = findViewById(R.id.btn_pro_back);
        web_view = findViewById(R.id.webView);
        unsubscribeDesc = findViewById(R.id.unsubscribeDesc);
        unsubscribeBtn = findViewById(R.id.unsubscribeBtn);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        unsubscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.showProgressDialog(PaymentSettingsActivity.this);

                if (subsciptionId.equalsIgnoreCase("") || subsciptionId.equalsIgnoreCase("null")) {

                    Intent intent = new Intent(getApplicationContext(), MembershipActivity.class);
                    intent.putExtra("from", "settings");
                    startActivity(intent);

                } else {

                    unsubscribe();

                }

            }
        });
    }

    private void unsubscribe() {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("CANCEL_SUBSCRIPTION")) {

                            new UpdateSubscription().execute();

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //   CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(PaymentSettingsActivity.this, Utils.errorMessage(PaymentSettingsActivity.this));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };

        CustomDialog.showProgressDialog(PaymentSettingsActivity.this);
        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("subscriptionId", subsciptionId);

            postapiRequest.request(PaymentSettingsActivity.this, iJsonListener, params, AppUrl.CANCEL_SUBSCRIPTION, "CANCEL_SUBSCRIPTION");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getDocTypes() {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("GET_PRIVACY_POLICY")) {
                            if (data.has("disclaimer")) {

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

            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            getApiRequest.request(this, iJsonListener, AppUrl.GET_DISCLAIMER, "GET_PRIVACY_POLICY");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void loadDatatoHtml() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Data...");
        progressDialog.setCancelable(false);

        web_view.requestFocus();
        web_view.getSettings().setLightTouchEnabled(true);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setGeolocationEnabled(true);
        web_view.setSoundEffectsEnabled(true);

        web_view.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);

        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
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

    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {
            VACustomer memPlans = databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();
            return memPlans;
        }

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);

            subsciptionId = memPlans.getStripe_active_subscription_id();

            if (subsciptionId != null) {
                if (subsciptionId.equalsIgnoreCase("") || subsciptionId.equalsIgnoreCase("null")) {
                    unsubscribeDesc.setText("Membership Inactive");
                    unsubscribeBtn.setText("SUBSCRIBE");
                } else {
                    unsubscribeDesc.setText("Membership Active");
                    unsubscribeBtn.setText("UNSUBSCRIBE");
                }
            } else {
                unsubscribeDesc.setText("Membership Inactive");
                unsubscribeBtn.setText("SUBSCRIBE");
            }
        }
    }

    class UpdateSubscription extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            databaseClient.getAppDatabase().vaCustomerDao().updateSubscriptionId("");

            return "";
        }

        @Override
        protected void onPostExecute(String vaCustomer) {
            super.onPostExecute(vaCustomer);
            CustomDialog.cancelProgressDialog();
            notaryappDialogSingle(PaymentSettingsActivity.this, "Membership Unsubscribed");

        }
    }

    private void notaryappDialogSingle(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }


}
