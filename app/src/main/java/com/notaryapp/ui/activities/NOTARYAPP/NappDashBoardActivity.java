package com.notaryapp.ui.activities.notaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.sql.Timestamp;

public class notaryappDashBoardActivity extends AppCompatActivity {

    Activity activity;
    DatabaseClient databaseClient;
    private Button back;
    TextView headText;
    String email;
    SpannableString spannableString;
    TextView totalSubSoldCount, soldThisMonthCount, revenueThisMonthAmount, totalRevenueAmount, monthlySubCount, annualSubCount, threeYearsSubCount, lifetimeSubCount;
    Button viewDetailReportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notaryapp_dashboard);
        activity = this;

        init();

        viewDetailReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(notaryappDashBoardActivity.this,notaryappReportActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void init() {

        headText = (TextView)findViewById(R.id.headText);
        totalSubSoldCount = (TextView) findViewById(R.id.totalSubSoldCount);
        soldThisMonthCount = (TextView) findViewById(R.id.soldThisMonthCount);
        revenueThisMonthAmount = (TextView) findViewById(R.id.revenueThisMonthAmount);
        monthlySubCount = (TextView) findViewById(R.id.monthlySubCount);
        totalRevenueAmount = (TextView) findViewById(R.id.totalRevenueAmount);
        annualSubCount = (TextView) findViewById(R.id.annualSubCount);
        threeYearsSubCount = (TextView) findViewById(R.id.threeYearsSubCount);
        lifetimeSubCount = (TextView) findViewById(R.id.lifetimeSubCount);
        back = (Button) findViewById(R.id.btn_pro_close);

        viewDetailReportBtn = (Button) findViewById(R.id.viewDetailReportBtn);

        spannableString = new SpannableString(getString(R.string.notaryapp_sales_dashboard));


        ForegroundColorSpan yellow = new ForegroundColorSpan(getColor(R.color.colorOrangeYellow));
        ForegroundColorSpan blue = new ForegroundColorSpan(getColor(R.color.user_input_color));

        spannableString.setSpan(yellow,0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blue,5,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        headText.setText(spannableString);

        databaseClient = DatabaseClient.getInstance(getApplicationContext());

        new SelectData().execute();
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            email = databaseClient.getAppDatabase().userRegDao().getEmail();

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

//            getHistory(sorttype.getText().toString(), filtertype.getText().toString());
            getDashBoardDetails();
        }

    }

    private void getDashBoardDetails() {

        CustomDialog.showProgressDialog(this);
        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String Type) {
                try {
                    if (data != null){

                        if (Type.equals("GET_DASH_DETAILS")){

                            JSONObject jsonObject = data.getJSONObject("baseline");
                            totalSubSoldCount.setText(jsonObject.getString("total_subs"));
                            soldThisMonthCount.setText(jsonObject.getString("month_subs"));

                            String revMonth = jsonObject.getString("month_revenue");
                            if (revMonth.equalsIgnoreCase("null")){
                                revenueThisMonthAmount.setText("0");
                            } else {
                                revenueThisMonthAmount.setText("$"+revMonth);
                            }

                            String totalRev = jsonObject.getString("total_revenue");

                            if (totalRev.equalsIgnoreCase("null")){
                                totalRevenueAmount.setText("0");
                            } else {
                                totalRevenueAmount.setText("$"+totalRev);
                            }


                            monthlySubCount.setText(jsonObject.getString("total_monthly"));
                            annualSubCount.setText(jsonObject.getString("total_yearly"));
                            threeYearsSubCount.setText(jsonObject.getString("total_threeyearly"));
                            lifetimeSubCount.setText(jsonObject.getString("total_lifetime"));

                            /*couponCode = data.getString("coupon_code");
                            String t1 = couponCode.substring(0,1);
                            pin1.setText(t1);
                            String t2 = couponCode.substring(1,2);
                            pin2.setText(t2);
                            String t3 = couponCode.substring(2,3);
                            pin3.setText(t3);
                            String t4 = couponCode.substring(3,4);
                            pin4.setText(t4);

                            websiteURL = data.getString("site_url");
                            resourceURL = data.getString("resource_url");*/

                            CustomDialog.cancelProgressDialog();



                        }

                    }
                } catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(notaryappDashBoardActivity.this, Utils.errorMessage(notaryappDashBoardActivity.this));
            }

            @Override
            public void onFetchStart() {

            }
        };

        POSTAPIRequest postapiRequest = new POSTAPIRequest();
        JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("email", email);
            //params.put("status", "Sort by");

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String timeSt = String.valueOf(timestamp.getTime());
            String URL_TS_DASH = AppUrl.GET_TS_TRANS_DETAILS+"?ID="+timeSt;

            postapiRequest.request(notaryappDashBoardActivity.this, iJsonListener, params, URL_TS_DASH, "GET_DASH_DETAILS");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}