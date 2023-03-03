package com.notaryapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.fragments.dashboard.DashBoard_LicenseListFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class LicenseListActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.membershipActivityViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_membership);

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

        loadFragment(new DashBoard_LicenseListFragment());

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this, REF_VIEW_CONTAINER, fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
