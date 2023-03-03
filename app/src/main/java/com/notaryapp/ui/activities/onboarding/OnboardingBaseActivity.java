
package com.notaryapp.ui.activities.onboarding;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.fragments.registration.SignInFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;

public class OnboardingBaseActivity extends BaseActivity {

    private ConstraintLayout view;
    public static final int REF_VIEW_CONTAINER = R.id.onboardingActivityViewContainer;
    //public static String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        view = findViewById(R.id.onboardingActivityViewContainer);
        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

//                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
//                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(myIntent);
//                finishAffinity();
//                finish();

            }
        }.start();
        setTimer();
        /*time out*/


        // Instance of sign in fragment
        loadFragment(new SignInFragment()) ;
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER);


        if (fragment.getTag().equals("SignInFragment")) {
            if (exitFlagBack) {
                // exitApp();

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finishAffinity();
                finish();

            } else {

                exitFlagBack = true;
                Utils.showSnackBarV2(view, "Press again to exit");
                exitTimer.start();


            }

        } else {
            super.onBackPressed();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER).onActivityResult(requestCode, resultCode, data);

    }
}
