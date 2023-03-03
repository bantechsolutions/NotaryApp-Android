package com.notaryapp.stripe;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;

import java.lang.ref.WeakReference;

/**
 * A convenience class to handle displaying error dialogs.
 */
public class ErrorDialogHandler {

    @NonNull
    private final WeakReference<AppCompatActivity> mActivityRef;

    public ErrorDialogHandler(@NonNull AppCompatActivity activity) {
        mActivityRef = new WeakReference<>(activity);
    }

    public void show(@NonNull String errorMessage) {
        final AppCompatActivity activity = mActivityRef.get();
        if (activity == null) {
            return;
        }

        ErrorDialogFragment.newInstance(activity.getString(R.string.verification_failed), errorMessage)
                .show(activity.getSupportFragmentManager(), "error");
    }
}
