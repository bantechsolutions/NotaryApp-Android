package com.notaryapp.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.notaryapp.R;

public class notaryappShareAppActivity extends AppCompatActivity {

    Button btnShare, btnNotNow, btnGoToDashBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notaryapp_share_app);
        btnShare = (Button)findViewById(R.id.btnShare);
        btnNotNow = (Button)findViewById(R.id.btnNotNow);
        btnGoToDashBoard = (Button)findViewById(R.id.btnGoToDashboard);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "Have you tried the best new notary application? Check it out. It's completely FREE!\nhttps://www.Notary-App.com/";
                intent.putExtra(Intent.EXTRA_SUBJECT, "Prevent fraud with the FREE notary application. Download Now!");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent,"Share Notary-App® via:"));
            }
        });

        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(notaryappShareAppActivity.this,DashBoardActivity.class);
                //startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finishAffinity();
                finish();
            }
        });

        btnGoToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(notaryappShareAppActivity.this,DashBoardActivity.class);
                //startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finishAffinity();
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}