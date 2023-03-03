package com.notaryapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.interfacelisterners.CaptureStampListerner;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.StampModel;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Dashboard_NotaryStampAdapter extends PagerAdapter {

    private Activity context;
    private ArrayList<StampModel> stampModels;
    private String selectedLicenseNo;
    private String selectedState;
    private CaptureStampListerner captureStampListerner;
    private LoadingCompletedInterface loadingCompletedInterface;

    public Dashboard_NotaryStampAdapter(Activity context, LoadingCompletedInterface loadingCompletedInterface, CaptureStampListerner captureStampListerner, String selectedLicenseNo, String selectedState, ArrayList<StampModel> stampModels) {
        this.context = context;
        this.stampModels = stampModels;
        this.selectedLicenseNo = selectedLicenseNo;
        this.selectedState = selectedState;
        this.captureStampListerner = captureStampListerner;
        this.loadingCompletedInterface = loadingCompletedInterface;
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_stamp_item, null);
        Button btnCaptureStamp = view.findViewById(R.id.btnCaptureStamp);
        TextView licenseNo = view.findViewById(R.id.licenseNo);
        ImageView imgtick = view.findViewById(R.id.img_tick);
        ProgressBar imageProfileProgress = view.findViewById(R.id.imageProfileProgress);
        RelativeLayout relCoverFlow = view.findViewById(R.id.relCoverFlow);
        String from;
        boolean flag;
        licenseNo.setText(selectedLicenseNo + "\n" +
                selectedState);
        try {
            CustomDialog.showProgressDialog(context);
            if (stampModels.size() > 0) {
                if (position == stampModels.size()) {
                    CustomDialog.cancelProgressDialog();
                    loadingCompletedInterface.loadingCompleted(true);
                    imgtick.setImageResource(R.drawable.ic_add_stamp);
                    btnCaptureStamp.setText("CAPTURE SEAL");
                    from = "CAPTURE SEAL";
                    flag = true;
                    loadingCompletedInterface.loadingCompleted(true);
                    imageProfileProgress.setVisibility(View.GONE);
                    btnCaptureStamp.setVisibility(View.VISIBLE);
                } else {
                    String url = stampModels.get(position).getUrl();
                    if (stampModels.get(position).getStampName().contains("stamppdf")) {
                        btnCaptureStamp.setVisibility(View.GONE);
                    } else {
                        btnCaptureStamp.setVisibility(View.VISIBLE);
                    }
                    CustomDialog.cancelProgressDialog();
                    from = "RETAKE";
                    flag = false;
                    //Utils.loadImage(context, url, imgtick, imageProfileProgress);
                    Picasso.with(context).load(url)
                            .centerCrop()
                            .resize(400, 400)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(imgtick, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    CustomDialog.cancelProgressDialog();
                                    loadingCompletedInterface.loadingCompleted(true);
                                    imageProfileProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    imgtick.setImageResource(R.drawable.ic_tik);
                                    CustomDialog.cancelProgressDialog();
                                    loadingCompletedInterface.loadingCompleted(true);
                                    imageProfileProgress.setVisibility(View.GONE);
                                }
                            });
                    relCoverFlow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            notaryappDialogSingle(context,
                                    url);
                        }
                    });
                }
            } else {
                btnCaptureStamp.setVisibility(View.VISIBLE);
                imgtick.setImageResource(R.drawable.ic_add_stamp);
                btnCaptureStamp.setText("CAPTURE SEAL");
                loadingCompletedInterface.loadingCompleted(true);
                from = "CAPTURE SEAL";
                flag = true;
                CustomDialog.cancelProgressDialog();
                imageProfileProgress.setVisibility(View.GONE);
            }

            btnCaptureStamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (from.equalsIgnoreCase("RETAKE")) {
                        captureStampListerner.captureStamp(from, position, selectedLicenseNo, selectedState, flag, stampModels.get(position).getStampName());
                    } else {
                        captureStampListerner.captureStamp(from, position, selectedLicenseNo, selectedState, flag, "");
                    }

                }
            });
        } catch (Exception e) {
            Log.v("error",e.toString());
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return stampModels.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    private void notaryappDialogSingle(final Activity activity, String url) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_image_dialog);
        Button btn_close = dialog.findViewById(R.id.btn_close);
        ImageView sealImage = dialog.findViewById(R.id.sealImage);
        TextView textHead = dialog.findViewById(R.id.textHead);
        ProgressBar imageProfileProgress = dialog.findViewById(R.id.imageProfileProgress);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        textHead.setText("SEAL - " + selectedLicenseNo);
        Utils.loadImage(context, url, sealImage, imageProfileProgress);

        dialog.show();
    }
}