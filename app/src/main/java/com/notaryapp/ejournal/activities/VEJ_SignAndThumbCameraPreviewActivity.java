package com.notaryapp.ejournal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.fragments.addsignaturethumb.VEJ_NotarizeSignatureAndThumbFragment;
import com.notaryapp.ejournal.fragments.addsignaturethumb.VEJ_SignatureCameraFragment;
import com.notaryapp.ejournal.fragments.addsignaturethumb.VEJ_SignaturePreviewFragment;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class VEJ_SignAndThumbCameraPreviewActivity extends BaseActivity {
    public static final int REF_VIEW_CONTAINER = R.id.signAndThumbCameraPreviewViewContainer;

    private String fragemntType, signerEmail, signerFirstName;
    private VEJ_SignaturePreviewFragment signaturePreviewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vej_sign_and_thumb_camera_preview);

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


        fragemntType = getIntent().getStringExtra("fragmentType");


        if (fragemntType == null){
            loadFragment(new VEJ_NotarizeSignatureAndThumbFragment()) ;

        } else if(fragemntType.equalsIgnoreCase("SignaturePreview")) {
            //fragemntType = null;
            Uri imgUri = Uri.parse(getIntent().getStringExtra("imgUri"));
            String savedImage = getIntent().getStringExtra("savedImage");
            signerEmail = getIntent().getStringExtra("SIGNER_EMAIL");
            signerFirstName = getIntent().getStringExtra("SIGNER_FIRSTNAME");
            loadFragment(new VEJ_SignaturePreviewFragment(imgUri, savedImage, signerEmail, signerFirstName));
        } else if (fragemntType.equalsIgnoreCase("SignatureCamera")){
            signerEmail = getIntent().getStringExtra("SIGNER_EMAIL");
            signerFirstName = getIntent().getStringExtra("SIGNER_FIRSTNAME");
            loadFragment(new VEJ_SignatureCameraFragment(signerEmail, signerFirstName));
        }


    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }

    @Override
    public void onBackPressed() {

        /*if (signaturePreviewFragment.isVisible()){
            Intent intent = new Intent(this, SignatureScreenActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }*/
        /*super.onBackPressed();
        if (fragemntType == null){
            //fragemntType = null;
            finish();

        }
        else if(fragemntType.equalsIgnoreCase("SignaturePreview")) {
            fragemntType = null;
            Intent intent = new Intent(this, SignatureScreenActivity.class);
            startActivity(intent);
            finish();
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER).onActivityResult(requestCode, resultCode, data);
    }
}