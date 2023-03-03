package com.notaryapp.ui.activities.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.fragments.NotaryBase_NotaryCameraFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class NotaryBaseActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.noteryRoot;
    Intent intent;
    private int position;
    private String license,from, stampName;
    private boolean captureFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notary_base);
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

        intent = getIntent();
        position =intent.getIntExtra("Position",0);
        license = intent.getStringExtra("License");
        from = intent.getStringExtra("From");
        stampName = intent.getStringExtra("stampName");
        captureFlag = intent.getBooleanExtra("Flag",true);
        String state = intent.getStringExtra("State");
            loadFragment(new NotaryBase_NotaryCameraFragment(position,license,from,captureFlag,state, stampName));

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
       // FragmentManager fragmentManager = getSupportFragmentManager();
      //  fragmentManager.beginTransaction().add(REF_VIEW_CONTAINER, fragment).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER).onActivityResult(requestCode, resultCode, data);

    }

}
