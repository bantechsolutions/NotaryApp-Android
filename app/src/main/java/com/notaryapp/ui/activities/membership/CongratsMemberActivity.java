package com.notaryapp.ui.activities.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CongratsMemberActivity extends AppCompatActivity {

    private Button closeBtn,upgradeBtn;

    private String savedEmail;
    private DatabaseClient databaseClient;
    private VACustomer vaCustomer;
    private int daysLeft = 0;
    private TextView memberText;
    private String planName="";
    private ImageView imageView;

    private String membershipType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_congrats_member);

        closeBtn = findViewById(R.id.btn_pro_close3);
        upgradeBtn = findViewById(R.id.upgradeBtn);
        memberText = findViewById(R.id.memberText);
        imageView = findViewById(R.id.imageView16);
        planName= getIntent().getStringExtra("planName");

        if (planName == null) {
            planName = "";
        }

        memberText.setText(planName);

        Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(planName);
        if (matcher.find()){
            imageView.setImageResource(R.drawable.congrats_silver_crown);
        } else {
            imageView.setImageResource(R.drawable.congrats_gold_crown);
        }

        databaseClient = DatabaseClient.getInstance(getApplicationContext());


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog.showProgressDialog(CongratsMemberActivity.this);

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
        CustomDialog.showProgressDialog(CongratsMemberActivity.this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("chkTrans")) {

                            vaCustomer = new VACustomer();


                            try {
                                daysLeft = (int) Utils.dateDiff(data.getString("current_date"),
                                        data.getString("ending_at"));
                                daysLeft = daysLeft * -1;
                            } catch (Exception e) {
                                daysLeft = data.getInt("daysLeft");

                            }

                            vaCustomer.setCusId(data.getInt("id"));
                            vaCustomer.setEmail(data.getString("email"));
                            vaCustomer.setDaysLeft(daysLeft);
                            vaCustomer.setStripe_customer_id(data.getString("stripe_customer_id"));
                            vaCustomer.setCurrent_membership(data.getString("current_membership"));
                            vaCustomer.setTotal_bought(data.getInt("total_bought"));
                            vaCustomer.setTotal_used(data.getInt("total_used"));
                            vaCustomer.setTransactionsLeft(data.getInt("total_bought")-data.getInt("total_used"));
                            vaCustomer.setCreated_at(data.getString("created_at"));
                            vaCustomer.setUpdated_at(data.getString("updated_at"));
                            vaCustomer.setStarted_at(data.getString("started_at"));
                            vaCustomer.setEnding_at(data.getString("ending_at"));
                            vaCustomer.setIs_active(data.getInt("is_active"));
                            vaCustomer.setStripe_active_subscription_id(data.getString("stripe_active_subscription_id"));

                            //membershipType  =  data.getString("current_membership");

                            new UpdateMemPlans().execute();
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(CongratsMemberActivity.this, Utils.errorMessage(CongratsMemberActivity.this));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(CongratsMemberActivity.this, msg);
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
            postapiRequest.request(CongratsMemberActivity.this,iJsonListener,params, AppUrl.CHK_TRAN_COUNT,"chkTrans");
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

            /*databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .updateCurrent_membership(membershipType);*/
            if (databaseClient.getAppDatabase().vaCustomerDao().getCustomer() != null) {

                databaseClient.getAppDatabase().vaCustomerDao().delete();
            }
            databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .insert(vaCustomer);

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
