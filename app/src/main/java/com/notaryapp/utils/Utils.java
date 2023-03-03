package com.notaryapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.model.webresponse.Body;
import com.notaryapp.roomdb.entity.SignerDocType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Utils {

    public static String errorMessage(Activity activity){
        String errorMes = activity.getResources().getString(R.string.networkerror1);
        return errorMes;
    }
    public static void setKeyboardFocus(final EditText primaryTextField) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
            }
        }, 500);
    }
    public static void setKeyboardHide(final EditText primaryTextField,Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(primaryTextField.getWindowToken(), 0);
    }
    public static String capitalizeFirst(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    public static int getRandomNumber(){

        //Random rand = new Random();
        SecureRandom rand = new SecureRandom();
        int rand_int1 = rand.nextInt(10000);

        return rand_int1;

    }

    public static void showSnackBarV2(View coordinatorLayout, String msg) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public static ArrayList<SignerDocType> signerDocTypes = new ArrayList<>();

    public static ArrayList<SignerDocType> getSignerDocTypes() {
        return signerDocTypes;
    }

    public static void setSignerDocTypes(ArrayList<SignerDocType> signerDocTypes) {
        Utils.signerDocTypes = signerDocTypes;
    }

    public static void loadImage(Activity activity,
                                 String url,
                                 ImageView imageView,
                                 ProgressBar homeprogress){
        Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.logo));
                        homeprogress.setVisibility(View.GONE);
                    }
                });
    }
    public static void loadImageDashStarred(Activity activity,
                                 String url,
                                 String doc,
                                 ImageView imageView,
                                 ProgressBar homeprogress){

        if (doc.equals(activity.getResources().getString(R.string.lock_a_doc))){
            Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            homeprogress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_lock_a_doc_));
                            homeprogress.setVisibility(View.GONE);
                        }
                    });
        } else if(doc.equals(activity.getResources().getString(R.string.ejournal))){
            Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            homeprogress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_e_journal_));
                            homeprogress.setVisibility(View.GONE);
                        }
                    });
        } else {
            Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            homeprogress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_NotaryApp_));
                            homeprogress.setVisibility(View.GONE);
                        }
                    });
        }
        /*Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.logo));
                        homeprogress.setVisibility(View.GONE);
                    }
                });*/
    }


    public static void loadImageDashboard(Activity activity,
                                          String url,
                                          ImageView imageView,
                                          ProgressBar homeprogress){
        Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_license));
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_verify_id_only));
                        homeprogress.setVisibility(View.GONE);
                    }
                });
    }
    public static void loadImageDashboardVAU (Activity activity,
                                          String url,
                                          ImageView imageView,
                                          ProgressBar homeprogress){
        Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_license));
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_NotaryApp_));
                        homeprogress.setVisibility(View.GONE);
                    }
                });
    }
    public static void loadImageDashboardLAD(Activity activity,
                                          String url,
                                          ImageView imageView,
                                          ProgressBar homeprogress){
        Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_lock_a_doc_));
                        homeprogress.setVisibility(View.GONE);
                    }
                });
    }

    public static void loadImageDashboardVEJ(Activity activity,
                                             String url,
                                             ImageView imageView,
                                             ProgressBar homeprogress){
        Picasso.with(activity).load(AppUrl.IMAGE_LOAD+url).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(200, 200)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        homeprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageView.setBackground(activity.getResources().getDrawable(R.drawable.ic_e_journal_));
                        homeprogress.setVisibility(View.GONE);
                    }
                });
    }


    public static boolean isConnected() {
        try {
            final String command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        }catch (Exception e){
            return true;
        }
    }

    public static long dateDiff(String toDate, String fromDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date toDateObj = format.parse(toDate.replaceAll("Z$", "+0000"));
            Date fromDateObj = format.parse(fromDate.replaceAll("Z$", "+0000"));

            long diff = toDateObj.getTime() - fromDateObj.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = Math.round( (float) hours / 24);

            //long days1 = Math.round((float) hours/24);


            return days;
        } catch (ParseException e) {
            //e.printStackTrace();
            return 0;
        }
    }

    public static long dateDiff(long toDate, long fromDate){
        long diff = toDate - fromDate;
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return days;
    }

    public static String removeFirstCharecter(String valueString){
        return valueString.substring(1);
    }
    public static boolean getSearch(Body body, String searchString) {

        if (body.getSigner() != null
                && body.getSigner().size() > 0) {
            if (body.getSigner().get(0).getFirstName() != null
                    && body.getSigner().get(0).getFirstName().toLowerCase().contains(searchString)) {
                return true;
            }

        }

        if (body.getSeal() != null
                && body.getSeal().getSealCode() != null
                && body.getSeal().getSealCode().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getDocType() != null
                && body.getDocType().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getStatus() != null
                && body.getStatus().toLowerCase().contains(searchString)) {
            return true;
        }

        if (body.getSeal() != null
                && body.getSeal().getSealCode().contains(searchString)) {
            return true;
        }
        return false;
    }

    public static String getSealString(ArrayList<String> licenseList){
        String sealStringNumber = "";
        if(licenseList.size()>0) {
            for (int i = 0; i < licenseList.size(); i++) {
                sealStringNumber += licenseList.get(i) + ", ";
            }
            sealStringNumber = removeLastChar(sealStringNumber);
        }

        String sealString = "Unless it is prohibited by the laws of your state, we recommend that you complete " +
                "the \"Add Seal\" process. Only you will have access to the image of your seal " +
                "and we will archive it securely on the block chain as part of your " +
                "professional identity as a notary public.\n\n" +
                "You are skipping the Add Seal process for Commission Number(s) "+sealStringNumber+". Please note " +
                "that if you skip the \"Add Seal\" process, a generic seal image will be provided in lieu of the image of your " +
                "Seal within the notaryapp application";

        return sealString;
    }

    public static String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-2);
    }


    public static void toastCenter(Activity activity, String message){
        Toast toast = new Toast(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.custom_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.custom_toast_text);
        textView.setText(message);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static Bitmap resizeImage(Bitmap image){

        int width = image.getWidth();
        int height = image.getHeight();

        int scaleWidth = width / 3;
        int scaleHeight = height / 3;

        return Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false);
    }


    public static File saveBitmapToFile(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


}