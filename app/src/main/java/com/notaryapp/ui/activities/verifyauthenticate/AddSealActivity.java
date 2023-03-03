package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.addseal.Notarize_AddSealFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class AddSealActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.addSealContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_seal);

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

        Intent intent = getIntent();
        String update = intent.getStringExtra("update");

        if(update != null){
            loadFragment(new Notarize_AddSealFragment(update));
        }
        else{

            loadFragment(new Notarize_AddSealFragment());
        }


    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this,REF_VIEW_CONTAINER,fragment);
    }
}
