package com.notaryapp.stripe;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.notaryapp.utils.AppUrl;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Factory to generate our Retrofit instance.
 */
public class RetrofitFactory {

    // Put your Base URL here. Unless you customized it, the URL will be something like
    // https://hidden-beach-12345.herokuapp.com/ let url = ephemeral-Key?customerId=\(UserInfo.sharedInfo.customerID!)&version=\(apiVersion)"
    private static final String BASE_URL = AppUrl.API_PRODUCTION_URL;
    @Nullable
    private static Retrofit mInstance = null;

    @NonNull
    public static Retrofit getInstance() {
        if (mInstance == null) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                    // Set your desired log level. Use Level.BODY for debugging errors.
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            final Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            // Adding Rx so the calls can be Observable, and adding a Gson converter with
            // leniency to make parsing the results simple.
            mInstance = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .build();
        }

        return mInstance;
    }
}
