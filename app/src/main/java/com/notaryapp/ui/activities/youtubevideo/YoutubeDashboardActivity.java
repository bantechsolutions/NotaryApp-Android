package com.notaryapp.ui.activities.youtubevideo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;

public class YoutubeDashboardActivity extends BaseActivity {

    private ConstraintLayout ytv1CLayout, ytv2CLayout, ytv3CLayout, ytv4CLayout, ytv5CLayout;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_dashboard);

        init();

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

        ytv1CLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeDashboardActivity.this, YoutubeVideoPlayActivity.class);
                intent.putExtra("YOUTUBE_URL", "8t5qrt4kFtertdASQ");
                startActivity(intent);
            }
        });
        ytv2CLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeDashboardActivity.this, YoutubeVideoPlayActivity.class);
                intent.putExtra("YOUTUBE_URL", "zdWedetrZHtg7blJKYt");
                startActivity(intent);
            }
        });
        ytv3CLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeDashboardActivity.this, YoutubeVideoPlayActivity.class);
                intent.putExtra("YOUTUBE_URL", "bDcJhdfgegejpppFpE");
                startActivity(intent);
            }
        });
        ytv4CLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeDashboardActivity.this, YoutubeVideoPlayActivity.class);
                intent.putExtra("YOUTUBE_URL", "GhrteBLkasefgfgfbxQ");
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void init(){
        ytv1CLayout = (ConstraintLayout)findViewById(R.id.videoGroup1);
        ytv2CLayout = (ConstraintLayout)findViewById(R.id.videoGroup2);
        ytv3CLayout = (ConstraintLayout)findViewById(R.id.videoGroup3);
        ytv4CLayout = (ConstraintLayout)findViewById(R.id.videoGroup4);
        ytv5CLayout = (ConstraintLayout)findViewById(R.id.videoGroup5);
        backBtn = (Button)findViewById(R.id.btn_pro_close);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}