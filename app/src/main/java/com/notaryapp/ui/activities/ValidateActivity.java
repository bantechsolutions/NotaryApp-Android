package com.notaryapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.jumio.nv.custom.NetverifyCustomSDKController;
import com.jumio.nv.custom.NetverifyCustomScanView;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.fragments.validate_id.Validate_SelectIdFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class  ValidateActivity extends BaseActivity {//implements NetverifyDeallocationCallback {

    public static final int REF_VIEW_CONTAINER = R.id.validateActivityViewContainer;

    private NetverifyCustomScanView customScanView;
    private NetverifyCustomSDKController customSDKController;
    private Bundle bundle;
  //  private ArrayList<NVDocumentType> arryDoc;
    private Context context;
    private DatabaseClient databaseClient;
    private String scanRef,email;
    //public static String scanReference;

    //public boolean isEnterOkScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

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


        databaseClient = DatabaseClient.getInstance(context);

         loadFragment(new Validate_SelectIdFragment()) ;


      // loadFragment(new Validate_ProcessFragment()) ;


     //   loadFragment(new Validate_VerifiFailedRetryFragment("NOT A VALID ID"));
   // loadFragment(new Validate_ProfileInfoFragment("Ambily","Ps"));
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment currentFrag = new Validate_SelectIdFragment();
        Fragment frg = getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER);

        if(frg != null) {

//            Log.d("LOGGGGGGGGGGGGGG",currentFrag.getClass().toString());
//            Log.d("LOGGGGGGGGGGGGGG",currentFrag.getTag());
//            Log.d("LOGGGGGGGGGGGGGG",frg.getTag());

            if (currentFrag.getClass().toString().equals(frg.getClass().toString())) {
                startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
                finish();
            } else {
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

            }
        }
        else {
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

        }


    }
}
