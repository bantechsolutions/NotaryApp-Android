package com.notaryapp.ejournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.fragments.addsignaturethumb.VEJ_NotarizeSignatureAndThumbFragment;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class VEJ_SignAndThumbActivity extends BaseActivity {
    public static final int REF_VIEW_CONTAINER = R.id.signAndThumbViewContainer;

    private String fragemntType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vej_sign_and_thumb);

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


        //fragemntType = getIntent().getStringExtra("fragmentType");

        /////
        loadFragment(new VEJ_NotarizeSignatureAndThumbFragment()) ;



        //if (fragemntType == null){


        /*} else if(fragemntType.equalsIgnoreCase("SignaturePreview")) {
            Uri imgUri = Uri.parse(getIntent().getStringExtra("imgUri"));
            String savedImage = getIntent().getStringExtra("savedImage");
            loadFragment(new SignaturePreviewFragment(imgUri, savedImage));
        }*/


    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER).onActivityResult(requestCode, resultCode, data);
    }


}