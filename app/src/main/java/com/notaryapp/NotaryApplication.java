package com.notaryapp;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.notaryapp.crashlog.CustomExceptionHandler;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.ProfilePictureObserver;

import org.json.JSONArray;
import org.json.JSONObject;


public class notaryappApplication extends Application {

    //public Context context = null;
    private Context context = null;
    ProfilePictureObserver profilePictureObserver;

    @Override
    public void onCreate() {
        super.onCreate();

        profilePictureObserver = new ProfilePictureObserver();
        context = getApplicationContext();
        Foreground.init(this);

        getStripePublishableKey();

        //Crashlog

        //setErrorLog();
    }

    private void getStripePublishableKey(){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppUrl.GET_PUBLIC_KEY,
                response -> {
                    try{
                        JSONObject data = new JSONObject(response);
                        JSONArray token_array = data.getJSONArray("config");
                        JSONObject json_inside = token_array.getJSONObject(0);
                        String publicKey =  json_inside.getString("apiValue");
                        PaymentConfiguration.init(
                                this,
                                publicKey);
                    }
                    catch (Exception e){
                        PaymentConfiguration.init(
                                this,
                                "stripe_key");
                    }
                }, error -> PaymentConfiguration.init(
                this,
                "stripe_key"));

        queue.add(stringRequest);

    }

    public ProfilePictureObserver getObserver() {
        return profilePictureObserver;
    }

    private void setErrorLog() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }
    }
}
