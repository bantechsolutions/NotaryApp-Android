package com.notaryapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.components.GifImage;

public class CustomDialog {
    //Start showing progress
    private static Dialog mProgressDialog;

    public static void showProgressDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    if (mProgressDialog.isShowing() == true) cancelProgressDialog();
                }

                mProgressDialog = new Dialog(activity);
                mProgressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                mProgressDialog.setContentView(R.layout.custom_dialog_progress);
                GifImage gifImageView = mProgressDialog.findViewById(R.id.GifImageView);
                gifImageView.setGifImageResource(R.drawable.dual_ball_gif);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.show();
                mProgressDialog.setCancelable(false);
            }
        });

    }

    //Stop showing progress
    public static void cancelProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    public static void notaryappInfoDialog(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);

        Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void notaryappInfoDialogAlert(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_info);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);

        TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
        textHead.setText("Alert !");


        Button closeButton = (Button) dialog.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void notaryappDialogSingle(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void notaryappToastDialog(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_toast);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);
        dialog.show();
        // Hide after some seconds
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 4000);

    }

    public static void shownotaryappProgress(final Activity activity) {
        mProgressDialog = new Dialog(activity);
        mProgressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.custom_dialog_progress);
        GifImage gifImageView = mProgressDialog.findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.gif_progress);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

    }

 /*   public static void cancelnotaryappProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }*/
}
