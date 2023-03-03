package com.notaryapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jumio.core.enums.JumioDataCenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.components.ScrollViewExt;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.IJsonListener;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class CompletedValidateProfile extends BaseActivity {

    private final String TAG = "SignUpFragment";
    private Button confirmBtn, backBtn, btnclose;
    private ScrollViewExt previewScroll;
    private ConstraintLayout constraintLayout;
    private Guideline gLineBottom;
    private String selectedType, errorMess, selfiePath, url, notaryEmail, scanRef;
    private DatabaseClient databaseClient;
    private CardView cardView;
    private ImageView faceImg, doc1Img, doc2Img;
    private TextView frontIdTxt, backIdTxt;
    private Context context;
    private Bitmap bitmap;
    private Animation animMoveUp;
    private boolean frontFlag, backFlag, faceFlag;
    private int balanceTransCount;

    private JumioScanDetails scanDetails;
    private IJsonListener iJsonListener;
    private TextInputEditText regEdit_LastName;
    private TextInputEditText regEdit_Email;
    private TextInputEditText regEdit_PhoneNo;
    private TextInputEditText regEdit_address1;
    private TextInputEditText regEdit_address2;
    private TextInputEditText regEdit_FirstName;
    private TextInputEditText cityName, stateName, zipCode;

    private List<SignerReg> signerList;

    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    // private String downloadUrl;
    private String docType, tranId;
    //private String firstName,lastName;
    private ProgressBar imageProfileProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_validate_profile_done);

        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);

        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();

            }
        }.start();
        setTimer();
        /*time out*/


        init();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CompletedValidateProfile.this.onBackPressed();
                onBackPressed();
            }
        });

        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CompletedValidateProfile.this.onBackPressed();
                onBackPressed();
            }
        });
    }

    private void init() {
        gLineBottom = findViewById(R.id.guidelineBottom);
        confirmBtn = findViewById(R.id.btnVerify);
        cardView = findViewById(R.id.card_back);
        btnclose = findViewById(R.id.btn_pro_close);
        backBtn = findViewById(R.id.btn_pro_back);
        faceImg = findViewById(R.id.face);
        doc1Img = findViewById(R.id.doc1Img);
        doc2Img = findViewById(R.id.doc2Img);
        previewScroll = findViewById(R.id.previewScroll);
        constraintLayout = findViewById(R.id.footer);

        regEdit_FirstName = findViewById(R.id.firstName);
        regEdit_LastName = findViewById(R.id.lastName);
        regEdit_Email = findViewById(R.id.email);
        regEdit_PhoneNo = findViewById(R.id.phoneNo);
        regEdit_address1 = findViewById(R.id.addressLine1);
        regEdit_address2 = findViewById(R.id.addressLine2);
        cityName = findViewById(R.id.city);
        stateName = findViewById(R.id.state);
        zipCode = findViewById(R.id.zip);

        regEdit_FirstName.setEnabled(false);
        regEdit_LastName.setEnabled(false);
        regEdit_Email.setEnabled(false);
        regEdit_PhoneNo.setEnabled(false);
        regEdit_address1.setEnabled(false);
        regEdit_address2.setEnabled(false);
        cityName.setEnabled(false);
        stateName.setEnabled(false);
        zipCode.setEnabled(false);

        frontIdTxt = findViewById(R.id.frontIdText);
        backIdTxt = findViewById(R.id.backIdText);

        context = getApplicationContext();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
       /* animMoveUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);
        animMoveUp.setAnimationListener(this);
        previewScroll.setScrollViewListener(this);*/

        constraintLayout.setVisibility(View.GONE);
        confirmBtn.setVisibility(View.GONE);
        gLineBottom.setVisibility(View.GONE);

        imageProfileProgress = findViewById(R.id.imageProfileProgress);
    }

    private void downloadImgFromJumio(String scanRefference, String type) {
        //   String scanRefference = "4a7c36a4-6b2d-4653-bd8d-7aa93a15acfa";
        CustomDialog.showProgressDialog(CompletedValidateProfile.this);
        url = "https://netverify.com/api/netverify/v2/scans/" + scanRefference + "/images/" + type;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(apiToken, apiSecret);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .header("Accept", "image/jpeg , image/png")
                                .header("User-Agentvalue", "Veritale Data Solutions ,Inc. notaryapp/1.0")
                                .build();
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(CompletedValidateProfile.this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        if (type.equals("face")) {

            picasso.load(url)
                    .transform(new CropCircleTransformation())
                    .error(R.drawable.ic_person)
                    .into(faceImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageProfileProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            faceImg.setBackground(getApplication().getResources().getDrawable(R.drawable.logo));
                        }
                    });

        }
        if (type.equals("front")) {
            picasso.load(url)
                    .transform(new CropCircleTransformation())
                    .error(R.drawable.doc1_img)
                    .into(doc1Img);
        }
        if (type.equals("back")) {
            cardView.setVisibility(View.VISIBLE);
            picasso.load(url)
                    .transform(new CropCircleTransformation())
                    .error(R.drawable.doc1_img)
                    .into(doc2Img);
        }
        CustomDialog.cancelProgressDialog();
        //  uploadFile();
    }

    private void showIcon(String docType, String type) {
        Picasso picasso = new Picasso.Builder(CompletedValidateProfile.this)
                .build();
        frontIdTxt.setText(docType.toUpperCase().replace("_"," ").trim());
        if (docType.equalsIgnoreCase("PASSPORT")) {
            if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
                doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
            }
            if (type.equals("back")) {
                  //cardView.setVisibility(View.VISIBLE);
//              picasso.load(R.drawable.ic_licence)
////                        .error(R.drawable.ic_licence)
////                        .into(doc2Img);
                doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
            }
        } else if (docType.equalsIgnoreCase("VISA")) {
            if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
                doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
            }
            if (type.equals("back")) {
                  //cardView.setVisibility(View.VISIBLE);
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc2Img);
                doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
            }
        }  else if (docType.equalsIgnoreCase("DRIVING_LICENSE")) {
            if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
                doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_licence));
            }
            if (type.equals("back")) {
                  //cardView.setVisibility(View.VISIBLE);
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc2Img);
                doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
            }
        }else {
            if (type.equals("front")) {
                doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
            }
            if (type.equals("back")) {
                //cardView.setVisibility(View.VISIBLE);
                doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
            }
        }
//        else if (docType.equalsIgnoreCase("")) {
//            if (type.equals("front")) {
//                picasso.load(url)
//                        .error(R.drawable.doc1_img)
//                        .into(doc1Img);
//            }
//            if (type.equals("back")) {
//                cardView.setVisibility(View.VISIBLE);
//                picasso.load(url)
//                        .error(R.drawable.doc1_img)
//                        .into(doc2Img);
//            }
//        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            notaryEmail = databaseClient.getAppDatabase().userRegDao().getEmail();

            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return selectedType;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            apiToken = jumioKeys.getJumiokey();
            apiSecret = jumioKeys.getJumiosecret();

            new GetSigners().execute();
        }

    }

    class GetSigners extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            //Getting the signer data from database
            signerList = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigners();

            return null;
        }

        @Override
        protected void onPostExecute(Integer sList) {
            super.onPostExecute(sList);

            scanRef = signerList.get(0).getScanReference();
            docType = signerList.get(0).getSignerType();
            dataCenter = JumioDataCenter.US;

            /*if (docType.equals("PASSPORT") || docType.equals("VISA")) {
                //downloadImgFromJumio(scanRef, "front");
                downloadImgFromJumio(scanRef, "face");
                showIcon(docType, "front");
                showIcon(docType, "back");
            } else {
                //downloadImgFromJumio(scanRef, "front");
                downloadImgFromJumio(scanRef, "face");
                //downloadImgFromJumio(scanRef, "back");
                showIcon(docType, "front");
                showIcon(docType, "back");

            }*/

            downloadImgFromJumio(scanRef, "face");
            showIcon(docType, "front");
            showIcon(docType, "back");

            regEdit_FirstName.setText(signerList.get(0).getFirstName());
            regEdit_LastName.setText(signerList.get(0).getLastName());
            regEdit_Email.setText(signerList.get(0).getEmail());
            regEdit_PhoneNo.setText(signerList.get(0).getPhoneNo());
            regEdit_address1.setText(signerList.get(0).getAddressOne());
            regEdit_address2.setText(signerList.get(0).getAddressTwo());
            cityName.setText(signerList.get(0).getCityName());
            stateName.setText(signerList.get(0).getStateName());
            zipCode.setText(signerList.get(0).getZipCode());

        }
    }
}