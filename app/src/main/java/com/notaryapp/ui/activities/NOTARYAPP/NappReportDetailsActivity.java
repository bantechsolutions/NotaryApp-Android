package com.notaryapp.ui.activities.notaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.model.ReportItems;

import java.util.Date;

public class notaryappReportDetailsActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    Button close;
    //TextView lastRenewalDate;
    TextView report_id, created_date, last_renewal_date, full_address, street, unit_no, city, state,
            zip, user_name, user_phone, user_email, planType;

    Date cratedDate = null;
    Date nextRenewaldate;
    String reportStatus;

    ReportItems list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_report_details);


        init();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       /* Intent intent = getIntent();
        String status = intent.getStringExtra("status");

        if (status.equals("FAILED")){
            constraintLayout.setVisibility(View.VISIBLE);
            last_renewal_date.setTextColor(getColor(R.color.colorAccent));
        }
        else {
            constraintLayout.setVisibility(View.GONE);
            last_renewal_date.setTextColor(getColor(R.color.blue));
        }*/

        list = (ReportItems) getIntent().getSerializableExtra("ReportDetails");
        reportStatus = getIntent().getExtras().getString("ReportStatus");

        if (reportStatus.equalsIgnoreCase("Active")){
            constraintLayout.setVisibility(View.GONE);
            last_renewal_date.setTextColor(getColor(R.color.blue));

        } else {
            constraintLayout.setVisibility(View.VISIBLE);
            last_renewal_date.setTextColor(getColor(R.color.orange));
        }

        if (list != null) {
            report_id.setText(list.getReport_id());


            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            try {
                cratedDate = simpleDateFormat.parse(list.getCreated_date());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Thu Dec 31 00:00:00 IST 1998
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String cDate = dateFormat.format(cratedDate);

            String cDateData = cDate.substring(0, 2) + " " + cDate.substring(3, 6) + " " + cDate.substring(7, 11);*/
            //String cDateData = DateFormating(list.getCreated_date());

            created_date.setText(list.getCreated_date());

            //cDate = "";
            street.setText(list.getStreet());
            unit_no.setText(list.getUnit_no());
            city.setText(list.getCity());
            state.setText(list.getState());
            user_name.setText(list.getUser_name());
            user_phone.setText(list.getUser_phone());
            user_email.setText(list.getUser_email());
            planType.setText(list.getPlan_title());

            if (list.getPlan_type().equalsIgnoreCase("Recurring")) {

                //String nRDateData = DateFormating(list.getPlan_end_date());
                last_renewal_date.setText(list.getPlan_end_date());
            } else {
                last_renewal_date.setText("");
            }


        }
        //emailText.setText(item.getEmail());

    }

    private void init() {
        constraintLayout = (ConstraintLayout) findViewById(R.id.statusBox);
        last_renewal_date = (TextView) findViewById(R.id.lastRenewalDate);
        report_id = (TextView) findViewById(R.id.reportID);
        created_date = (TextView) findViewById(R.id.dateCreated);
        street = (TextView) findViewById(R.id.street);
        planType = (TextView) findViewById(R.id.planType);
        unit_no = (TextView) findViewById(R.id.unitNo);
        city = (TextView) findViewById(R.id.city);
        state = (TextView) findViewById(R.id.state);
        zip = (TextView) findViewById(R.id.zip);
        user_name = (TextView) findViewById(R.id.user_name);
        user_phone = (TextView) findViewById(R.id.phone);
        user_email = (TextView) findViewById(R.id.email);
        close = (Button) findViewById(R.id.btn_pro_close);
    }

    private String DateFormating(String DateModyfy) {
        /*SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
            nextRenewaldate = simpleDateFormat1.parse("2021-04-15 13:41:49");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Thu Dec 31 00:00:00 IST 1998
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        String nRDate = dateFormat1.format(nextRenewaldate);

        String nRDateData = nRDate.substring(0, 2) + " " + nRDate.substring(3, 6) + " " + nRDate.substring(7, 11);

        return nRDateData;*/
        String monthData;
        String yearData = DateModyfy.substring(0,4);
        String dayData = DateModyfy.substring(8,10);
        String mData = DateModyfy.substring(5,6);
        if (mData.equals("01")){
             monthData="Jan";
        } else if (mData.equals("02")){
             monthData="Feb";
        } else if (mData.equals("03")){
             monthData="Mar";
        } else if (mData.equals("04")){
             monthData="Apr";
        } else if (mData.equals("05")){
             monthData="May";
        } else if (mData.equals("06")){
             monthData="Jun";
        } else if (mData.equals("07")){
             monthData="Jul";
        } else if (mData.equals("08")){
             monthData="Aug";
        } else if (mData.equals("09")){
             monthData="Sep";
        } else if (mData.equals("10")){
             monthData="Oct";
        } else if (mData.equals("11")){
             monthData="Nov";
        } else {
             monthData="Dec";
        }

        String dateData = dayData + " " + monthData + " " + yearData;

        return dateData;
    }
}