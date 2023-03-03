package com.notaryapp.ui.activities.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.fragments.addlicense.ProfileBase_AddLicenseFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;

public class ProfileBaseActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.profileroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notary_base_profile);

        /*time out*/
        listenerBinding =
                Foreground.get(getApplication()).addListener(this);
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

        loadFragment(new ProfileBase_AddLicenseFragment()) ;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(REF_VIEW_CONTAINER, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
