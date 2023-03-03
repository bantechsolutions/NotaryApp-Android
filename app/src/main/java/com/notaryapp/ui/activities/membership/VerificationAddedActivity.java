package com.notaryapp.ui.activities.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class VerificationAddedActivity extends AppCompatActivity {

    private Button closeBtn,upgradeBtn;
    private String savedEmail;
    private DatabaseClient databaseClient;
    private int transactionCount;
    private TextView planDesc;
    private String planDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verification_added);
        planDesc = findViewById(R.id.planDesc);
        databaseClient = DatabaseClient.getInstance(getApplicationContext());
        String planType = getIntent().getStringExtra("type");

        if (planType.equals("VLP-1")) {
            planDesc.setText("\"You have successfully added 10 ID Verification Pack.\"");
        }
        if (planType.equals("VLP-2")) {

            planDesc.setText("\"You have successfully added 20 ID Verification Pack.\"");
        }
        if (planType.equals("VLP-3")) {

            planDesc.setText("\"You have successfully added 30 ID Verification Pack.\"");
        }

        if (planType.equals("PAYG")) {
            planDesc.setText("\"You have successfully added 1 ID Verification Pack.\"");
        }

        closeBtn = findViewById(R.id.btn_pro_close3);
        upgradeBtn = findViewById(R.id.upgradeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.showProgressDialog(VerificationAddedActivity.this);

                // Remove the square after some time
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        updateCount();
                    }
                }, 1000);

            }
        });
        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.showProgressDialog(VerificationAddedActivity.this);

                // Remove the square after some time
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        updateCount();
                    }
                }, 1000);
            }
        });

    }

    private void updateCount() {

        new SelectData().execute();
    }

    private void checkTransactions(){
        CustomDialog.showProgressDialog(VerificationAddedActivity.this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("chkTrans")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");
                            transactionCount = total_bought - total_used;

                            new UpdateMemPlans().execute();
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(VerificationAddedActivity.this, Utils.errorMessage(VerificationAddedActivity.this));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(VerificationAddedActivity.this, msg);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };


        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email",savedEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(VerificationAddedActivity.this,iJsonListener,params, AppUrl.CHK_TRAN_COUNT,"chkTrans");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            savedEmail = databaseClient.getAppDatabase().userRegDao().getEmail();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            checkTransactions();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
        finish();
    }

    class UpdateMemPlans extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .updateCount(transactionCount);

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
            finish();
        }

    }

}
