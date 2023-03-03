package com.notaryapp.lockadoc.activityes;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.lockadoc.fragments.LADCameraFragment;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class PhotographPartyDocumentActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);

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
        loadFragment(new LADCameraFragment());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v("tets","etsgd");
    }

    public void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER).onActivityResult(requestCode, resultCode, data);
    }

}