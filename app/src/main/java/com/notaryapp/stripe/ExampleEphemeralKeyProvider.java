package com.notaryapp.stripe;


import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.jumio.commons.log.Log;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.notaryapp.utils.AppUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * An implementation of {@link } that can be used to generate
 * ephemeral keys on the backend.
 */
public class ExampleEphemeralKeyProvider implements EphemeralKeyProvider {

    @NonNull
    private final CompositeDisposable mCompositeDisposable;
    @NonNull private final StripeService mStripeService;
    @NonNull private final ProgressListener mProgressListener;
    @NonNull private String cusId;

    public ExampleEphemeralKeyProvider(@NonNull ProgressListener progressListener, String cusId) {
        this.cusId = cusId;
        mStripeService = RetrofitFactory.getInstance().create(StripeService.class);
        mCompositeDisposable = new CompositeDisposable();
        mProgressListener = progressListener;
    }


    @Override
    public void createEphemeralKey(@NonNull @Size(min = 4) String apiVersion,
                                   @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
        Map<String, String> apiParamMap = new HashMap<>();
        apiParamMap.put("api_version", apiVersion);
        final String token = AppUrl.Authorization_Key;

        Log.d("MEMBERSHIP_createEphemeralKey", "createEphemeralKey");

        mCompositeDisposable.add(mStripeService.createEphemeralKey(token,cusId,apiVersion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(

                        responseBody -> {
                            Log.d("MEMBERSHIP_createEphemeralKey", "createEphemeralKey---");
                            try {
                                final String rawKey = responseBody.string();
                                keyUpdateListener.onKeyUpdate(rawKey);
                                mProgressListener.onStringResponse(rawKey);

                            } catch (IOException e) {
                                Log.d("MEMBERSHIP_createEphemeralKey", "createEphemeralKey@@@");
                                keyUpdateListener.onKeyUpdateFailure(0, e.getMessage());
                            }
                        },
                        throwable ->
                                mProgressListener.onStringResponse(throwable.getMessage())));
    }

    public interface ProgressListener {
        void onStringResponse(@NonNull String response);
    }
}
