package com.notaryapp.utils;


import android.content.Intent;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.ui.activities.notaryappSplashActivity;

public class BaseActivity extends AppCompatActivity implements  Foreground.Listener {


    private CountDownTimer counttimer;
    public CountDownTimer counttimerInactivity;
    private Boolean isTimedOutFlag = false;
    Boolean isOnBackGround = false;
    public CountDownTimer Counttimer;
    public CountDownTimer exitTimer;
    public Boolean exitFlagBack = false;
    //public Boolean lost = false;
    //public Boolean alreadyNotifylost = false;


    /*foreground */

    public Foreground.Binding listenerBinding;

    public void setTimer(){


        Counttimer =new CountDownTimer(600000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                exitFlagBack = false;
            }
        }.start();

        exitTimer =new CountDownTimer(3000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                exitFlagBack = false;
            }
        }.start();
    }


    public void onBecameForeground() {

        isOnBackGround  = false;

        if (counttimer != null)
                counttimer.cancel();

            if (isTimedOutFlag) {
                isTimedOutFlag = false;
                Intent myIntent = new Intent(this, notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        }
        //Log.i("Foreground.TAG", getClass().getName() + " became foreground");


    public void onBecameBackground() {

//        Intent myIntent = new Intent(this, MainActivity.class);
//        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(myIntent);

        //Log.i("Foreground.TAG", getClass().getName() + " went background");
        counttimerInactivity.cancel();
        isOnBackGround  = true;
        counttimer = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {

               // Log.d("Foreground.TAG", "onBecameBackground "+millisUntilFinished);

            }

            public void onFinish() {

                //Log.d("Foreground.TAG", "onBecameBackground onFinish");

                isTimedOutFlag =true;
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // not strictly necessary as Foreground only holds a weak reference
        // to the listener to defensively prevent leaks, but its always better
        // to be explicit and WR's play monkey with the Garbage Collector
        listenerBinding.unbind();
        counttimerInactivity.cancel();
    }
    /* Ends*/

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        counttimerInactivity.cancel();
        counttimerInactivity.start();
    }




    protected void onPause(){

        super.onPause();
        counttimerInactivity.cancel();

//        Toast.makeText(NextActivity.this, "user background",
//              Toast.LENGTH_SHORT).show();

    }

    protected void onResume(){

        super.onResume();
        counttimerInactivity.start();
//        Toast.makeText(NextActivity.this, "user foreground",
//                Toast.LENGTH_SHORT).show();
    }


}
