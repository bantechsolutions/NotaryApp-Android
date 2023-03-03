package com.notaryapp.utils;

import android.app.Activity;

import com.notaryapp.roomdb.DatabaseClient;

import java.util.List;

public class GetStateName implements Runnable{
   Activity activity;
  static List<String> stateName;
    GetStateName(Activity activity){
        this.activity = activity;
    }
    public static String getState(String code){
       //stateName.
        String stateName = "";
        return stateName;
    }
    public static String getCode(Activity activity,String state){
        String stateCode = "";


        return stateCode;
    }

    @Override
    public void run() {
        stateName = DatabaseClient.getInstance(activity).getAppDatabase().statesDao().getStateName();
    }
}
