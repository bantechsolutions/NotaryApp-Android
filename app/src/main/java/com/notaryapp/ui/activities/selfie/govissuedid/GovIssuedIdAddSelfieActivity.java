package com.notaryapp.ui.activities.selfie.govissuedid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class GovIssuedIdAddSelfieActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    //public boolean isOpenOne = true;
    //public boolean isOpenTwo = true;
    String firstName ="",lastName = "", scanRef = "", idType="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_signer);
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

        firstName = getIntent().getStringExtra("FirstName");
        lastName = getIntent().getStringExtra("LastName");
        scanRef = getIntent().getStringExtra("ScanRef");
        idType = getIntent().getStringExtra("IdType");


        loadFragment(new GovIssuedIdSelfieCameraFragment());
        /*if(userType != null){
            loadFragment(new Notarize_SignerPersonalCameraFragment());
        }else {
            loadFragment(new Notarize_SignerVerifyTypeFragment());
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

    @Override
    public void onBackPressed() {
        return;
    }
}
