package com.notaryapp.ui.activities.notaryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

public class notaryappCouponActivity extends AppCompatActivity {

    Activity activity;
    private Button back;
    Button salesDashBoardBtn;
    Button resourceBtn, goToWebsiteBtn;
    String email;
    DatabaseClient databaseClient;
    String couponCode, websiteURL, resourceURL;

    TextView tvLine1, tvLine2, tvLine3, pin1, pin2, pin3, pin4;
    SpannableString spannableString1, spannableString2, spannableString3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title__coupon_activity);
        activity = this;



        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        salesDashBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSalesDash = new Intent(notaryappCouponActivity.this, notaryappDashBoardActivity.class);
                startActivity(intentSalesDash);
            }
        });

        resourceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(resourceURL));
                startActivity(i);
            }
        });

        goToWebsiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(Intent.ACTION_VIEW);
                j.setData(Uri.parse(websiteURL));
                startActivity(j);
            }
        });


    }
    private void init(){

        tvLine1 = (TextView)findViewById(R.id.text1);
        tvLine2 = (TextView)findViewById(R.id.text2);
        tvLine3 = (TextView)findViewById(R.id.text3);
        back = findViewById(R.id.btn_pro_close);
        pin1 = (TextView)findViewById(R.id.pin1);
        pin2 = (TextView)findViewById(R.id.pin2);
        pin3 = (TextView)findViewById(R.id.pin3);
        pin4 = (TextView)findViewById(R.id.pin4);

        salesDashBoardBtn = (Button) findViewById(R.id.salesDashBoardBtn);
        resourceBtn = (Button) findViewById(R.id.resourceBtn);
        goToWebsiteBtn = (Button)findViewById(R.id.goToWebsiteBtn);

        spannableString1 = new SpannableString(getString(R.string.instruct_your_customer));
        spannableString2 = new SpannableString(getString(R.string.your_notaryapp_code_give));
        spannableString3 = new SpannableString(getString(R.string.your_notaryapp_code_is));

        ForegroundColorSpan yellow = new ForegroundColorSpan(getColor(R.color.colorOrangeYellow));
        ForegroundColorSpan blue = new ForegroundColorSpan(getColor(R.color.user_input_color));

        spannableString1.setSpan(yellow,35,40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString1.setSpan(blue,41,47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString2.setSpan(yellow,5,10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(blue,11,17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString3.setSpan(yellow,5,10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString3.setSpan(blue,11,17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        tvLine1.setText(spannableString1);
        tvLine2.setText(spannableString2);
        tvLine3.setText(spannableString3);

        databaseClient = DatabaseClient.getInstance(getApplicationContext());

        new SelectData().execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
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
            getCode();
        }

    }

    private void getCode() {

        CustomDialog.showProgressDialog(this);
        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String Type) {
                try {
                    if (data != null){

                        if (Type.equals("GET_COUPON_CODE")){
                                couponCode = data.getString("coupon_code");
                                String t1 = couponCode.substring(0,1);
                                pin1.setText(t1);
                                String t2 = couponCode.substring(1,2);
                                pin2.setText(t2);
                                String t3 = couponCode.substring(2,3);
                                pin3.setText(t3);
                                String t4 = couponCode.substring(3,4);
                                pin4.setText(t4);

                                websiteURL = data.getString("site_url");
                                resourceURL = data.getString("resource_url");

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
                CustomDialog.notaryappDialogSingle(notaryappCouponActivity.this, Utils.errorMessage(notaryappCouponActivity.this));
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
            String URL_TS_COUPON = AppUrl.GET_COUPON_CODE+"?ID="+timeSt;

            postapiRequest.request(notaryappCouponActivity.this, iJsonListener, params, URL_TS_COUPON, "GET_COUPON_CODE");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}