package com.notaryapp.ejournal.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.fragments.adddoc.VEJ_Notarize_AddDocFragmentLanding;
import com.notaryapp.ejournal.fragments.adddoc.VEJ_Notarize_AddDocsFragment;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

import java.io.File;
import java.io.IOException;

public class VEJ_AddDocsActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private String docName ="";
    private String serverDocName ="";
    //private boolean isCamara

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_docs);

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


        docName = getIntent().getStringExtra("docName");
        serverDocName = getIntent().getStringExtra("serverDocName");

        if(docName != null && serverDocName != null){
            //loadFragment(new Notarize_AddDocCameraFragment(docName,serverDocName)) ;
            loadFragment(new VEJ_Notarize_AddDocFragmentLanding(docName,serverDocName)) ;
        }
        else{
            loadFragment(new VEJ_Notarize_AddDocsFragment());
        }


    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Notarize_AddDocCameraFragment demoFragment = (Notarize_AddDocCameraFragment)
//                getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER);
//        demoFragment.onActivityResult(requestCode, resultCode, data);

        VEJ_Notarize_AddDocFragmentLanding demoFragment = (VEJ_Notarize_AddDocFragmentLanding)
                getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER);
        demoFragment.onActivityResult(requestCode, resultCode, data);
    }

    public Bitmap uriToBitmap(Context c, Uri uri) {
        if (c == null && uri == null) {
            return null;
        }
        try {
            if (c != null){
                return MediaStore.Images.Media.getBitmap(c.getContentResolver(), Uri.fromFile(new File(uri.getPath())));
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

}
