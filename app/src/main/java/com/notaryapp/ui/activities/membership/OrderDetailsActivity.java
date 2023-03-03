package com.notaryapp.ui.activities.membership;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.notaryapp.R;
import com.notaryapp.model.OrderItems;
import com.notaryapp.utils.CustomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView expiredHeadText,orderNumber,emailText,amount,orderDate,lastDate,receipt;
    ConstraintLayout planCardColor;
    ImageView crownImage;
    TextView unitCount, unitText, singleuse;

    Button downloadResp, back;
    OrderItems item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_details);
        init();

        ForegroundColorSpan skyBlue = new ForegroundColorSpan(getColor(R.color.skyBlue));
        ForegroundColorSpan skyBlue2 = new ForegroundColorSpan(getColor(R.color.skyBlue));

        item = (OrderItems) getIntent().getSerializableExtra("orderDetails");

        //planCardColor.setBackground(getDrawable(R.drawable.plans_head_membership));
        expiredHeadText.setText(getNameType(item.getObjecttype(), item.getUnit_purchased(), item.getPlanname()));
        if(item.getObjecttype().equalsIgnoreCase("subscription")) {
            crownImage.setVisibility(View.VISIBLE);
            unitText.setVisibility(View.GONE);
            unitCount.setVisibility(View.GONE);
            singleuse.setVisibility(View.GONE);
            planCardColor.setBackground(getDrawable(R.drawable.plans_head_membership));
            Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(item.getPlanname());
            if (matcher.find()){
                crownImage.setImageResource(R.drawable.ic_silver_crown);
            } else {
                crownImage.setImageResource(R.drawable.crown_gold);
            }
        } else {
            crownImage.setVisibility(View.GONE);
            unitText.setVisibility(View.VISIBLE);
            unitCount.setVisibility(View.VISIBLE);
            singleuse.setVisibility(View.GONE);
            unitCount.setText(item.getUnit_purchased());
            if (item.getUnit_purchased().equalsIgnoreCase("10")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head));
            }
            else if(item.getUnit_purchased().equalsIgnoreCase("20")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_two));
            }
            else if(item.getUnit_purchased().equalsIgnoreCase("30")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_three));
            }
            else {
                expiredHeadText.setText("Pay as you go");
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_four));
                unitText.setVisibility(View.GONE);
                unitCount.setVisibility(View.GONE);
                singleuse.setVisibility(View.VISIBLE);
            }
        }

        /*if(item.getObjecttype().equalsIgnoreCase("subscription")) {
            planCardColor.setBackground(getDrawable(R.drawable.plans_head_membership));
        } else {
            if (item.getPlanname().equalsIgnoreCase("Notary-App®10")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head));
            }
            else if(item.getPlanname().equalsIgnoreCase("Notary-App®20")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_two));
            }
            else if(item.getPlanname().equalsIgnoreCase("Notary-App®30")){
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_three));
            }
            else {
                planCardColor.setBackground(getDrawable(R.drawable.plans_head_four));
            }
        }*/

        orderNumber.setText("Order No. "+item.getId());
        emailText.setText(item.getEmail());

        if(item.getPrice().equalsIgnoreCase("0.0")){

            amount.setText("\u25CF Amount paid - $3.99");
        }
        else{
            String amountPaidText = "\u25CF Amount paid - $"+item.getPrice();
            SpannableString spannableAmountPaidText = new SpannableString(amountPaidText);
            spannableAmountPaidText.setSpan(skyBlue, 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableAmountPaidText.setSpan(skyBlue2, amountPaidText.length()-item.getPrice().length()-1,amountPaidText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            amount.setText(spannableAmountPaidText);
            //amount.setText("\u25CF Amount paid - $"+item.getPrice());
        }

        String OrderDateText = getTimeStamp("\u25CF Order Date - "+item.getCreated_on());
        SpannableString spannableOrderDateText = new SpannableString(OrderDateText);
        spannableOrderDateText.setSpan(skyBlue, 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        orderDate.setText(spannableOrderDateText);
        //orderDate.setText(getTimeStamp("\u25CF Order Date - "+item.getCreated_on()));
        String LastUpdatedText = getTimeStamp("\u25CF Last Updated - "+item.getUpdated_on());
        SpannableString spannableLastUpdatedText = new SpannableString(LastUpdatedText);
        spannableLastUpdatedText.setSpan(skyBlue, 0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lastDate.setText(spannableLastUpdatedText);
        //lastDate.setText(getTimeStamp("\u25CF Last Updated - "+item.getUpdated_on()));

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = item.getReceipturl();
                if(url != null && !url.equalsIgnoreCase("") && !url.equalsIgnoreCase("null")) {

                    if (url.startsWith("http://") || url.startsWith("https://")) {

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (i.resolveActivity(getPackageManager()) != null) {
                            startActivity(i);
                        }
                    }
                    else{
                        CustomDialog.notaryappDialogSingle(OrderDetailsActivity.this, "Url Error");
                    }
                }
                else{
                    CustomDialog.notaryappDialogSingle(OrderDetailsActivity.this, "Url Error");
                }
            }
        });

        downloadResp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = item.getReceiptpdf();
                if(url != null && !url.equalsIgnoreCase("") && !url.equalsIgnoreCase("null")) {

                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        if (i.resolveActivity(getPackageManager()) != null) {
                            startActivity(i);
                        }

                    } else{
                        CustomDialog.notaryappDialogSingle(OrderDetailsActivity.this, "Url Error");
                    }
                } else{
                    CustomDialog.notaryappDialogSingle(OrderDetailsActivity.this, "Url Error");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


    }

    public String getNameType(String item, String unit_purchased, String plan_name) {

        String itemname = "";
        if(item.equalsIgnoreCase("subscription")){
            itemname = plan_name;
        }
        else{
            itemname = "Notary-App®"+ unit_purchased;
            downloadResp.setVisibility(View.GONE);
        }
        return itemname;
    }

    public String getTimeStamp(String time) {

        String date = "";

        String dojDateArry[] = time.split("T");
        date = dojDateArry[0];

        return date;
    }


    private void init() {

        expiredHeadText = findViewById(R.id.expiredHeadText);

        orderNumber = findViewById(R.id.orderNumber);
        emailText = findViewById(R.id.emailText);
        amount = findViewById(R.id.amount);
        orderDate = findViewById(R.id.orderDate);
        lastDate = findViewById(R.id.lastDate);
        receipt = findViewById(R.id.receipt);
        planCardColor = findViewById(R.id.ImageLayout);
        crownImage = findViewById(R.id.crownImage);
        unitCount = findViewById(R.id.idcount);
        unitText = findViewById(R.id.verification);
        singleuse = findViewById(R.id.singleuse);

        downloadResp = findViewById(R.id.downloadResp);
        back = findViewById(R.id.btn_pro_back);

    }
}
