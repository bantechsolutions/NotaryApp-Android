package com.notaryapp.stripe;

import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.NonNull;

/**
 * Class used to show and hide the progress spinner.
 */
public class ProgressDialogController {

    @NonNull
    private final Resources mRes;
    @NonNull private final FragmentManager mFragmentManager;
    @Nullable
    private WeakReference<ProgressDialogFragment> mProgressFragmentRef;

    public ProgressDialogController(@NonNull FragmentManager fragmentManager,
                                    @NonNull Resources res) {
        mFragmentManager = fragmentManager;
        mRes = res;
    }

    public void show(@StringRes int resId) {
        dismiss();
        final ProgressDialogFragment progressDialogFragment =
                ProgressDialogFragment.newInstance(mRes.getString(resId));
        progressDialogFragment.show(mFragmentManager, "progress");
        mProgressFragmentRef = new WeakReference<>(progressDialogFragment);
    }

    public void dismiss() {
        final ProgressDialogFragment progressDialogFragment = getDialogFragment();
        if (progressDialogFragment != null) {
            progressDialogFragment.dismiss();

            if (mProgressFragmentRef != null) {
                mProgressFragmentRef.clear();
                mProgressFragmentRef = null;
            }
        }
    }

    @Nullable
    private ProgressDialogFragment getDialogFragment() {
        return mProgressFragmentRef != null ? mProgressFragmentRef.get() : null;
    }
}
