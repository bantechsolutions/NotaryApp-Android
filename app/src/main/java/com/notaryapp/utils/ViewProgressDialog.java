package com.notaryapp.utils;

import android.app.Activity;
import android.app.Dialog;

public class ViewProgressDialog {
    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public ViewProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
   /*     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.custom_dialog_progress);

        //...initialize the imageView form infalted layout
        ImageView gifImageView = dialog.findViewById(R.id.GifImageView);

        *//*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        *//*
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);

        //...now load that gif which we put inside the drawble folder here with the help of Glide

        Glide.with(activity)
                .load(R.drawable.gif_progress)
                .placeholder(R.drawable.gif_progress)
                .centerCrop()
                .crossFade()
                .into(imageViewTarget);*/

        //...finaly show it
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }

}
