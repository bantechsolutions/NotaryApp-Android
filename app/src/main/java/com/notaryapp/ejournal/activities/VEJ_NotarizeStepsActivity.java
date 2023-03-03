package com.notaryapp.ejournal.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.components.ButtonBounceInterpolator;
import com.notaryapp.ejournal.adapter.VEJ_VAStepDocsAdapter;
import com.notaryapp.ejournal.adapter.VEJ_VAStepsAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JournalFees;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.UserNote;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class VEJ_NotarizeStepsActivity extends BaseActivity {

    private static final int REF_VIEW_CONTAINER = R.id.notarizeStepsContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    CardView verifySignersLay, signDocsLay, addSealLay, addDocsLay, addLocationLay, addJournalLay, addNoteLay, signatureThumbLay;
    ConstraintLayout modHeaderSigners, modBodySigners;
    ConstraintLayout modHeaderSignDocs, modHeaderSeal, modBodySeal, modHeaderAddDocs, modBodyAddDocs;
    ConstraintLayout modHeaderLocation, modBodyLocation, locationMap;
    ConstraintLayout modHeaderJournal, modBodyJournal;
    ConstraintLayout modHeaderNote, modBodyNote;
    ConstraintLayout modHeaderSignatureThumb;
    private View childview1;
    private TextView loc, licenseText;
    private LinearLayout infoText;
    private ImageView itemImg, sealEdit, locationEditIcon, journalEditIcon, noteEditIcon;
    private Button btnNotarize, btnBack, btnClose;
    private List<String> sealList;
    private List<DocumentsModel> docList;
    private RecyclerView recyclerSigners, recyclerSeal, recyclerAddDocs;
    private GridLayoutManager layoutManager;
    private VEJ_VAStepsAdapter stepsAdapter1, stepsAdapter2;
    private VEJ_VAStepDocsAdapter stepsAdapter3;
    private DatabaseClient databaseClient;
    private List<SignerReg> signerList;
    private UserLocation userLocations;
    private JournalFees journalFees;
    private UserNote userNote;
    private Integer signDocsCount = 0, locationCount = 0, journalCount = 0, noteCount= 0;
    private String licenseNo, selectedImg;
    private int balanceTransCount;
    private Transactions transactions;
    private IJsonListener iJsonListener;
    private String savedEmail, scanRef, stampName, transactionId;
    private int count, signDocCount, addSealCount, addDocCount, addLocCount, witnessCount, userLocationCount, witSignerCount;
    private ImageView imgInfo1, imgInfo2, imgInfo3, imgInfo4, imgInfo5, imgInfo6, imgInfo7, imgWarning;
    private Info info;
    private Intent intent;
    private String from = "";
    private boolean enabledGPS;
    private LocationManager service;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Integer> stampCountArray;
    private String signerType = "";
    private String jAmount, jFrac, jourAmount;
    private String isWitness= "";

    private ConstraintLayout overlay1, overlay2, overlay3, overlay4, overlay5, overlay6, overlay1_1;
    //public boolean isClick = true;
    private boolean isClick = true;

    private String device_information;
    private TextView notaTypeTxt, journalAmount, journalAmountFrac;
    private TextView noteHeadingText, noteSubText;
    //private JournalFees journalFees;
    private boolean mSignerFlag = true;
    private int journalFeesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vej_notarize_steps);

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
        from = getIntent().getStringExtra("From");
        if (from == null) {
            from = "";
        }
        if (from.equalsIgnoreCase("pending")) {
            infoText.setVisibility(View.GONE);
        }

        setupAnimations();
        buttonClicks();
        setupRecyclers();

    }


    private void setupAnimations() {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_button);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        ButtonBounceInterpolator interpolator = new ButtonBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        btnNotarize.startAnimation(myAnim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclers();
    }

    @Override
    public void onBackPressed() {
        completedDialog();
    }

    private void setupRecyclers() {

        //First Step

        //   databaseClient = DatabaseClient.getInstance(this);
        try {
            new GetSigners().execute().get();
            if (signerList.size() >= 1) {
                infoText.setVisibility(View.GONE);
                verifySignersLay.setVisibility(View.GONE);
                signDocsLay.setVisibility(View.VISIBLE);
                modHeaderSigners.setVisibility(View.VISIBLE);
                modBodySigners.setVisibility(View.VISIBLE);
                stepsAdapter1 = new VEJ_VAStepsAdapter(signerList, this);
                layoutManager = new GridLayoutManager(VEJ_NotarizeStepsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerSigners.setLayoutManager(layoutManager);
                recyclerSigners.setAdapter(stepsAdapter1);

                overlay1.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

//        });

        //Sign Docs Management
        try {
            signDocsCount = new GetSignStatus().execute().get();

            if (signDocsCount > 0) {
                signDocsLay.setVisibility(View.GONE);
                modHeaderSignDocs.setVisibility(View.VISIBLE);

                overlay1_1.setVisibility(View.GONE);

                //overlay2.setVisibility(View.GONE);
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //add Signature and thumb step
        try
        {
            if (signerList.size() >= 1){
                String SignaturePath;
                for (int i =0; i <signerList.size(); i++){
                    //Log.d("SIGNER_SIGN",signerList.get(i).getProImagePath());
                    try {
                        SignaturePath = signerList.get(i).getSignatureImagePath().toString();
                    } catch (Exception e){
                        SignaturePath = "null";
                        mSignerFlag = true;
                    }

                    if (mSignerFlag){
                        if (SignaturePath.equalsIgnoreCase("null")){
                            mSignerFlag = false;
                        }
                    }

                }
                if (mSignerFlag){
                    signatureThumbLay.setVisibility(View.GONE);
                    modHeaderSignatureThumb.setVisibility(View.VISIBLE);
                    overlay2.setVisibility(View.GONE);
                    //startBtn.setVisibility(View.VISIBLE);
                } else {
                    signatureThumbLay.setVisibility(View.VISIBLE);
                    modHeaderSignatureThumb.setVisibility(View.GONE);
                    overlay2.setVisibility(View.VISIBLE);
                }

            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

//        Add Seal Step
        new GetSealStatus().execute();

        //Add doc Step
        docList = new ArrayList<>();
        new GetDocs().execute();


        //Location Step
        userLocations = new UserLocation();
        try {
            locationCount = new GetLocation().execute().get();
            if (locationCount > 0) {
                //btnNotarize.setEnabled(true);
                //btnNotarize.setText("COMPLETE");
                addLocationLay.setVisibility(View.GONE);
                overlay5.setVisibility(View.GONE);
                modHeaderLocation.setVisibility(View.VISIBLE);
                modBodyLocation.setVisibility(View.VISIBLE);
                String locationsInMap = userLocations.getStreetName() + "\n" +
                        userLocations.getCityName() + "\n" +
                        userLocations.getStateName() + "\n" +
                        userLocations.getPinCode();
                loc.setText(locationsInMap);
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //Journal Step
        journalFees = new JournalFees();
        try {
            journalCount = new GetJounral().execute().get();
            if (journalCount > 0){

                btnNotarize.setEnabled(true);
                btnNotarize.setText("COMPLETE");

                addJournalLay.setVisibility(View.GONE);
                overlay6.setVisibility(View.GONE);
                modHeaderJournal.setVisibility(View.VISIBLE);
                modBodyJournal.setVisibility(View.VISIBLE);


                notaTypeTxt.setText(journalFees.getNotarizationType());
                jourAmount = journalFees.getFeeAmount();
                //journalAmount.setText(jourAmount);
                try {
                    String[] parts = jourAmount.split(Pattern.quote("."));
                    jAmount = parts[0];
                    jFrac = parts[1];
                    if (jFrac.length()==1){
                        jFrac = jFrac+"0";
                    }
                } catch (Exception e){
                    jAmount = jourAmount;
                    jFrac = "00";
                }
                journalAmount.setText(jAmount);
                journalAmountFrac.setText("."+jFrac);

            }



        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //Note Step
        userNote = new UserNote();
        try {
            noteCount = new GetUserNote().execute().get();
            if (noteCount > 0){
                addNoteLay.setVisibility(View.GONE);
                modHeaderNote.setVisibility(View.VISIBLE);
                modBodyNote.setVisibility(View.VISIBLE);
                noteHeadingText.setText(userNote.getNoteHeading());
                if (userNote.getNoteDetail().equalsIgnoreCase("null")){
                    noteSubText.setVisibility(View.GONE);
                } else {
                    noteSubText.setVisibility(View.VISIBLE);
                    noteSubText.setText(userNote.getNoteDetail());
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }


    }

    private void buttonClicks() {
        //Button click to close and save.
        btnNotarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectData().execute();
            }
        });

        //View click to start add signers.
        verifySignersLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (from.equals("Pending")) {
                    Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_VerifySignerActivity.class);
                    startActivity(intent);
                } else {
                    //      CustomDialog.notaryappDialogSingle(NotarizeStepsActivity.this,
                    //           "Initiated new transaction . Kindly complete all steps");
                    signDocsLay.setVisibility(View.VISIBLE);
                    createTransaction();

                }
//                Intent intent = new Intent(NotarizeStepsActivity.this, VerifySignerActivity.class);
//                startActivity(intent);
            }
        });

        //View click to start signing documents.
        signDocsLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signerList.size() >= 1) {
                    //addSealLay.setVisibility(View.VISIBLE);
                    if (isWitness.equals("WIT")) {
                        /*if (witnessCount == 2) {
                            Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_SignDocsActivity.class);
                            startActivity(intent);
                        } else {
                            CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this,
                                    "You can't complete transaction without verifying witness");
                        }*/
                        if (witnessCount == witSignerCount * 2) {
                            Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_SignDocsActivity.class);
                            startActivity(intent);
                        } else {
                            CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this,
                                    "You can't complete transaction without verifying witness");
                        }
                    } else {
                        Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_SignDocsActivity.class);
                        startActivity(intent);
                    }
                } else {
                    CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this, "Please select VerifySigners");
                }
                //  new SelectSealCount().execute();

            }
        });

        signatureThumbLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDocsLay.setVisibility(View.VISIBLE);
                new SelectDocSignCount1().execute();
            }
        });

        //View click to start add seals.
        addSealLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDocsLay.setVisibility(View.VISIBLE);
                new SelectDocSignCount().execute();

//Testing
//                Intent intent = new Intent(NotarizeStepsActivity.this, AddSealActivity.class);
//                startActivity(intent);
                //Testing
            }
        });

        //View click to start add documents.
        addDocsLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addLocationLay.setVisibility(View.VISIBLE);
                new SelectSealCount().execute();
            }
        });

        //View click to start add location.
        addLocationLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (docList.size() > 0) {

                    if (ActivityCompat.checkSelfPermission(VEJ_NotarizeStepsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(VEJ_NotarizeStepsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(VEJ_NotarizeStepsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                1000);

                    } else {
                        Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_MapActivity.class);
                        startActivity(intent);
                    }

                } else {
                    CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this, "Please select AddDoc");
                }

            }
        });
        //View click to start add Journal.
        addJournalLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    locationCount = new GetLocation().execute().get();
                    if (locationCount > 0){
                        Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_JournalEntryActivity.class);
                        startActivity(intent);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    //e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

            }
        });

        //View click to start add Note.
        addNoteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    journalCount = new GetJounral().execute().get();
                    if (journalCount > 0){
                        Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_NoteActivity.class);
                        startActivity(intent);

                    }
                } catch (ExecutionException | InterruptedException e) {
                    //e.printStackTrace();
                    Thread.currentThread().interrupt();
                }

            }
        });
        //View click to start add location.
        locationEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(VEJ_NotarizeStepsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(VEJ_NotarizeStepsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VEJ_NotarizeStepsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            1000);

                } else {
                    Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_MapActivity.class);
                    startActivity(intent);
                }
            }
        });

        journalEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_JournalEntryActivity.class);
                startActivity(intent);
            }
        });
        noteEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_NoteActivity.class);
                startActivity(intent);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedDialog();
            }
        });
        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_NotarizeStepsActivity.this, info.getVaSigners());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_NotarizeStepsActivity.this, info.getVaSigndocuments());
            }
        });
        imgInfo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_NotarizeStepsActivity.this, info.getVaAddseal());
            }
        });
        imgInfo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_NotarizeStepsActivity.this, info.getVaAdddocuments());
            }
        });
        imgInfo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_NotarizeStepsActivity.this, info.getVaAddLocation());
            }
        });
        sealEdit.setVisibility(View.GONE);
        sealEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick) {
                    Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_AddSealActivity.class);
                    intent.putExtra("update", "update");
                    startActivity(intent);
                }else {
                    Utils.toastCenter(VEJ_NotarizeStepsActivity.this,getResources().getString(R.string.click_message));
                }
            }
        });

    }

    private void completedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText("You would loose all data ... ");

        Button dialogButton = dialog.findViewById(R.id.btnNo);
        dialogButton.setText("CANCEL");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("OK");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DeleteImages().execute();
                dialog.dismiss();
                new DeleteFromCloseAll().execute();

            }
        });
        dialog.show();
    }

    private void init() {
        btnNotarize = findViewById(R.id.btnSelect);
        btnBack = findViewById(R.id.btn_pro_back);
        btnClose = findViewById(R.id.btn_pro_close);
        itemImg = findViewById(R.id.itemImage);

        verifySignersLay = findViewById(R.id.verifySignersLay);
        modHeaderSigners = findViewById(R.id.modHeaderSigners);
        modBodySigners = findViewById(R.id.modBodySigners);
        signDocsLay = findViewById(R.id.signDocsLay);
        modHeaderSignDocs = findViewById(R.id.modHeaderSignDocs);
        addSealLay = findViewById(R.id.addSealLay);
        modHeaderSeal = findViewById(R.id.modHeaderSeal);
        modBodySeal = findViewById(R.id.modBodySeal);
        licenseText = findViewById(R.id.licenseText);
        addDocsLay = findViewById(R.id.addDocsLay);
        modHeaderAddDocs = findViewById(R.id.modHeaderAddDocs);
        modBodyAddDocs = findViewById(R.id.modBodyAddDocs);
        addLocationLay = findViewById(R.id.addLocationLay);
        modHeaderLocation = findViewById(R.id.modHeaderLocation);
        modBodyLocation = findViewById(R.id.modBodyLocation);
        locationEditIcon = findViewById(R.id.locationEditIcon);
        addJournalLay = findViewById(R.id.addJournalLay);
        modHeaderJournal = findViewById(R.id.modHeaderJournal);
        modBodyJournal = findViewById(R.id.modBodyJournal);
        journalEditIcon = findViewById(R.id.journalEditIcon);
        addNoteLay = findViewById(R.id.addNoteLay);
        modHeaderSignatureThumb = findViewById(R.id.modHeaderSignatureThumb);
        modHeaderNote = findViewById(R.id.modHeaderNote);
        modBodyNote = findViewById(R.id.modBodyNote);
        noteEditIcon = findViewById(R.id.noteEditIcon);
        signatureThumbLay = findViewById(R.id.signatureThumbLay);
        sealEdit = findViewById(R.id.sealEditIcon);
        infoText = findViewById(R.id.firstView);
        notaTypeTxt = findViewById(R.id.notaTypeTxt);
        journalAmount = findViewById(R.id.journalFees);
        journalAmountFrac = findViewById(R.id.journalFeesFrac);
        noteHeadingText = findViewById(R.id.noteHeadingText);
        noteSubText = findViewById(R.id.noteSubText);

        imgInfo1 = findViewById(R.id.imgInfo1);
        imgInfo2 = findViewById(R.id.imgInfo2);
        imgInfo3 = findViewById(R.id.imgInfo3);
        imgInfo4 = findViewById(R.id.imgInfo4);
        imgInfo5 = findViewById(R.id.imgInfo5);
        imgInfo6 = findViewById(R.id.imgInfo6);
        imgInfo7 = findViewById(R.id.imgInfo7);
        imgWarning = findViewById(R.id.imgWarning);

        overlay1 = findViewById(R.id.overlay1);
        overlay1_1 = findViewById(R.id.overlay1_1);
        overlay2 = findViewById(R.id.overlay2);
        overlay3 = findViewById(R.id.overlay3);
        overlay4 = findViewById(R.id.overlay4);
        overlay5 = findViewById(R.id.overlay5);
        overlay6 = findViewById(R.id.overlay6);

        loc = findViewById(R.id.loc);
        recyclerSigners = findViewById(R.id.recyclerSigners);
        recyclerAddDocs = findViewById(R.id.recyclerAddDocs);
        databaseClient = DatabaseClient.getInstance(this);

        transactions = new Transactions();
        new SelectEmail().execute();
        initIJsonListener();
        new SelectCount().execute();
        new GeInfo().execute();
        btnNotarize.setEnabled(false);
    }

    private void completeTransaction() {
        CustomDialog.showProgressDialog(this);
        try {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceName = Build.MANUFACTURER;
            device_information = "ANDROID ID: "+deviceId +"\nDEVICE NAME: "+deviceName +"\nSERIAL: " + Build.SERIAL + "\nMODEL: " + Build.MODEL + "\nID: " + Build.ID + "\nManufacture: " + Build.MANUFACTURER +
                    "\nbrand: " + Build.BRAND +"\ntype: " + Build.TYPE +"\nuser: " + Build.USER +"\nBASE: " + Build.VERSION_CODES.BASE +"\nINCREMENTAL " + Build.VERSION.INCREMENTAL
                    +"\nSDK  " + Build.VERSION.SDK +"\nBOARD: " + Build.BOARD +"\nBRAND " + Build.BRAND +"\nHOST " + Build.HOST
                    +"\nFINGERPRINT: "+Build.FINGERPRINT +"\nVersion Code: " + Build.VERSION.RELEASE;
        } catch (Exception e){
            device_information = " ";
        }

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("tranid", transactionId);
                params.put("device_info",device_information);
                //change this to notary username

            } catch (Exception e) {
                //e.printStackTrace();
                Log.d("TRANS_ERROR", e.toString());
            }
            Log.d("TRANS_PARAM", params.toString());
            postapiRequest.request(this, iJsonListener, params, AppUrl.COMPLETE_TRANSACTION, "completeTrans");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void generatePdfVEJ() {
        CustomDialog.showProgressDialog(this);


        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("tranid", transactionId);
                //params.put("device_info",device_information);
                //change this to notary username

            } catch (Exception e) {
                //e.printStackTrace();
                //Log.d("TRANS_ERROR", e.toString());
            }
            //Log.d("TRANS_PARAM", params.toString());
            postapiRequest.request(this, iJsonListener, params, AppUrl.GENERATE_PDF_VEJ, "generatePdfVej");
        } catch (Exception e) {
            //Log.d("POST_API", e.toString());
            //e.printStackTrace();
        }
    }

    private void createTransaction() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userNameN", savedEmail);
                //change this to notary username
            } catch (Exception e) {
                //e.printStackTrace();
            }
//           
//            postapiRequest.request(this,iJsonListener,params, url,"startTrans");
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_TRANSACTION_VEJ, "startTrans");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("startTrans")) {
                            String tr_id = "";
                            String tr_key = "";
                            JSONObject transactionData = data.getJSONObject("success");
                            tr_id = transactionData.getString("id");
                            tr_key = transactionData.getString("tranid");
                            new SaveTransactionID().execute(tr_id, tr_key);
//                            if (transactionCount>0){
//                                Intent intent = new Intent(getActivity(), NotarizeStepsActivity.class);
//                                startActivity(intent);
//                            }
                        } else if (type.equals("completeTrans")) {
                            //Log.d("COMPLETE_TRANS",data.toString());
                            String success = data.getString("success");
                            if (success.equals("1")) {
                                //   updateTransactions();
                                new DeleteAll().execute();
                            }
                        } else if (type.equals("updateTran")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");

                            balanceTransCount = total_bought - total_used;

                            //  new UpdateMemPlans().execute();
                            //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();

                        } else if (type.equals("generatePdfVej")){
                            //Log.d("RESPOSE",data.toString());
                            String success = data.getString("success");
                            if (success.equals("1")) {
                                //   updateTransactions();
                                startActivity(new Intent(VEJ_NotarizeStepsActivity.this, VEJ_VAComplete.class));
                                finish();
                            }


                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this,
                        Utils.errorMessage(VEJ_NotarizeStepsActivity.this));
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class GetSigners extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            //Getting the signer data from database
            signerList = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigners();
            witSignerCount = 0;
            witnessCount = 0;
            for (int i = 0; i < signerList.size(); i++) {
                signerType = signerList.get(i).getSignerType();
                if (signerType.equals("WIT") || signerType.equals("WITNESSMANUALLY") ) {
                    //witnessCount = databaseClient.getAppDatabase().witnessRegDao().getCount();
                    //break;
                    witSignerCount = witSignerCount + 1;
                }
            }
            if (witSignerCount>0){
                isWitness = "WIT";
                witnessCount = databaseClient.getAppDatabase().witnessRegDao().getCount();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer sList) {
            super.onPostExecute(sList);
        }
    }

    class GetSignStatus extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            return databaseClient.getAppDatabase()
                    .signDocsDao()
                    .getCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    class GetSealStatus extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... voids) {
            //adding to database

            SealAdded sealAdded = new SealAdded();

            sealAdded = databaseClient.getAppDatabase()
                    .sealAddedDao().getAllLicense();

            if (sealAdded != null) {
                licenseNo = sealAdded.getLicenseNum();
                selectedImg = sealAdded.getSealUrl();
            }

//            Log.w("TAG", licenseNo + " , " + selectedImg);
            return licenseNo;
        }

        @Override
        protected void onPostExecute(String license) {
            super.onPostExecute(license);
            //licenseNo = license.getLicense();
            if (licenseNo != null) {

                addSealLay.setVisibility(View.GONE);
                modHeaderSeal.setVisibility(View.VISIBLE);
                modBodySeal.setVisibility(View.VISIBLE);
                //String textLicense = "License #" + licenseNo;
                licenseText.setText(licenseNo);

                if (!selectedImg.equalsIgnoreCase("")) {
                    Picasso.with(VEJ_NotarizeStepsActivity.this)
                            .load(selectedImg)
                            .into(itemImg);
                }

                overlay3.setVisibility(View.GONE);

                ////////////////////////////

                isClick = false;

                ///////////////////////////
            }
        }
    }

    class GetDocs extends AsyncTask<Void, Void, List<DocumentsModel>> {

        @Override
        protected List<DocumentsModel> doInBackground(Void... voids) {

            stampCountArray = new ArrayList<>();

            //Getting the signer data from database
            List<DocumentsModel> list = new ArrayList<>();
            List<DocumentsModel> listcount = new ArrayList<>();

            list = databaseClient.getAppDatabase()
                    .documentsImageDao()
                    .getDisDocs();

            docList = list;

            int listCount = 0;
            for (int i = 0; i < list.size(); i++) {

                listCount = databaseClient.getAppDatabase()
                        .documentsImageDao()
                        .countDocs(docList.get(i).getStampName());

                stampCountArray.add(listCount);
            }


            return list;
        }

        @Override
        protected void onPostExecute(List<DocumentsModel> docList) {
            super.onPostExecute(docList);
            //  try {
            // docList = new GetDocs().execute().get();
            //  docList.add()
            if (docList.size() >= 1) {
                stepsAdapter3 = new VEJ_VAStepDocsAdapter(docList, stampCountArray, VEJ_NotarizeStepsActivity.this);
                layoutManager = new GridLayoutManager(VEJ_NotarizeStepsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerAddDocs.setLayoutManager(layoutManager);
                recyclerAddDocs.setAdapter(stepsAdapter3);
                addDocsLay.setVisibility(View.GONE);
                modHeaderAddDocs.setVisibility(View.VISIBLE);
                modBodyAddDocs.setVisibility(View.VISIBLE);

                overlay4.setVisibility(View.GONE);
            }
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    class GetLocation extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            userLocations = databaseClient.getAppDatabase()
                    .userLocationDao()
                    .getLocation();
            return databaseClient.getAppDatabase()
                    .userLocationDao()
                    .getLocationCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    class GetJounral extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            journalFees = databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .getJournalFees();
            return databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .getCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }
    class GetUserNote extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            userNote = databaseClient.getAppDatabase()
                    .userNoteDao()
                    .getUserNote();

            return databaseClient.getAppDatabase()
                    .userNoteDao()
                    .getCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
//            savedEmail =  databaseClient.getAppDatabase()
//                    .userRegDao()
//                    .getEmail();
            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            Log.d("TRANS_ID",transactionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            completeTransaction();
            //generatePdfVEJ();
        }

    }

    class UpdateMemPlans extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .updateCount(balanceTransCount);

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            // if(balanceTransCount>0){

//            }else if(balanceTransCount == 0){
//                proceedDialog("No balance transactions remaining . Please upgrade Your plan to do more transactions");
//            }else{
//
//            }

            // loadFragment(new Validate_FinalFragment());
            //   getActivity().onBackPressed();
        }

    }

    class SaveTransactionID extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            databaseClient.getAppDatabase()
                    .transactionsDao().deleteAll();

            String rowId = params[0];
            String transId = params[1];

            transactions.setTransactionId(rowId);
            transactions.setTransactionKey(transId);

           /* count = databaseClient.getAppDatabase()
                    .transactionsDao().getCount();
            if (count == 0) {*/
            databaseClient.getAppDatabase()
                    .transactionsDao()
                    .insert(transactions);
          /*  } else {
                databaseClient.getAppDatabase()
                        .transactionsDao()
                        .update(transactions.getTransactionId()
                                , transactions.getTransactionKey());
            }*/

            return "";
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_VerifySignerActivity.class);
            startActivity(intent);
            // finish();
        }
    }

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            scanRef = databaseClient.getAppDatabase().scanDetailsDao().getScanRef();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }

    class SelectDocSignCount1 extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            signDocCount = databaseClient.getAppDatabase().signDocsDao().getCount();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if (signDocCount > 0) {
                Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_SignAndThumbActivity.class);
                startActivity(intent);

            } else {
                CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this, "Please take signDoc");
                // finish();
            }
        }

    }

    class SelectDocSignCount extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            signDocCount = databaseClient.getAppDatabase().signDocsDao().getCount();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if (signDocCount > 0) {
                Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_AddSealActivity.class);
                startActivity(intent);

            } else {
                CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this, "Please take signDoc");
                // finish();
            }
        }

    }

    class SelectSealCount extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            if (databaseClient.getAppDatabase().sealAddedDao().getAllLicense() != null) {
                addSealCount = 1;
            }
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if (addSealCount > 0) {
                Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_AddDocsActivity.class);
                startActivity(intent);
                //  finish();
            } else {
                CustomDialog.notaryappDialogSingle(VEJ_NotarizeStepsActivity.this, "Please select AddSeal");
            }
        }

    }

    class SelectCount extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            signDocCount = databaseClient.getAppDatabase().signDocsDao().getCount();
            addLocCount = databaseClient.getAppDatabase().userLocationDao().getLocationCount();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class DeleteAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            generatePdfVEJ();
            /*startActivity(new Intent(VEJ_NotarizeStepsActivity.this, VEJ_VAComplete.class));
            finish();*/
        }

    }

    class DeleteFromCloseAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            Intent startMain = new Intent(getApplicationContext(),DashBoardActivity.class);
            //startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finishAffinity();
            finish();
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(VEJ_NotarizeStepsActivity.this, VEJ_MapActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public boolean getIsClick(){
        //Log.d("IS_CLCICK", String.valueOf(isClick));
        return isClick;
    }

}
