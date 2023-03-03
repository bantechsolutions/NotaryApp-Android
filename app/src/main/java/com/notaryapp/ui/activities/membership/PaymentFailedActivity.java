package com.notaryapp.ui.activities.membership;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.notaryapp.R;
import com.notaryapp.ui.fragments.membership.MembershipInvoiceActivity;

public class PaymentFailedActivity extends AppCompatActivity {
    private Button closeBtn, retryBtn;
    private String packageType = "", category = "", monthly_fee = "", planID= "", planName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failed);
        closeBtn = findViewById(R.id.btn_pro_close3);
        retryBtn = findViewById(R.id.upgradeBtn);

        packageType = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");
        monthly_fee = getIntent().getStringExtra("monthly_fee");
        planID = getIntent().getStringExtra("planID");
        planName = getIntent().getStringExtra("planName");

        if (packageType == null) {
            packageType = "";
        }

        if (category == null) {
            category = "";
        }

        if (monthly_fee == null) {
            monthly_fee = "";
        }

        if (planID == null) {
            planID = "";
        }
        if (planName == null) {
            planName = "";
        }

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(PaymentFailedActivity.this, MembershipInvoiceActivity.class);
                in.putExtra("monthly_fee",monthly_fee);
                in.putExtra("type",packageType);
                in.putExtra("planName",planName);
                in.putExtra("category",category);
                in.putExtra("planID",planID);
                startActivity(in);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                finish();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentFailedActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                finish();
            }
        });


    }
}