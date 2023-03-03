package com.notaryapp.stripe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.notaryapp.R;


public class ErrorDialogFragment extends DialogFragment {
    @NonNull
    public static ErrorDialogFragment newInstance(@NonNull String title,
                                                  @NonNull String message) {
        final ErrorDialogFragment fragment = new ErrorDialogFragment();

        final Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);

        fragment.setArguments(args);

        return fragment;
   }

    public ErrorDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String title = getArguments().getString("title");
        final String message = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok,
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }
}
