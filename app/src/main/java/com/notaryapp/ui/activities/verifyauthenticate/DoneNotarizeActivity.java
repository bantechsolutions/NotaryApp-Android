package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.adapter.VACompleteAdapter;
import com.notaryapp.adapter.VACompleteDocsAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.ui.activities.BlockChainViewActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.IJsonListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DoneNotarizeActivity extends BaseActivity {

    private static final int REF_VIEW_CONTAINER = R.id.notarizeStepsContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    CardView verifySignersLay, signDocsLay, addSealLay, addDocsLay, addLocationLay;
    ConstraintLayout modHeaderSigners, modBodySigners, footerView;
    ConstraintLayout modHeaderSignDocs, modHeaderSeal, modBodySeal, modHeaderAddDocs, modBodyAddDocs;
    ConstraintLayout modHeaderLocation, modBodyLocation, locationMap, modHash;
    private Guideline gLineBottom;
    private View childview1;
    private TextView loc, licenseText;
    private LinearLayout infoText;
    private ImageView itemImg, sealEdit, locationEditIcon;
    private Button btnNotarize, btnBack, btnClose, btnBlockChain;
    private List<String> sealList;
    private List<DocumentsModel> docList;
    private RecyclerView recyclerSigners, recyclerSeal, recyclerAddDocs;
    private GridLayoutManager layoutManager;
    private VACompleteAdapter stepsAdapter1, stepsAdapter2;
    private VACompleteDocsAdapter stepsAdapter3;
    private DatabaseClient databaseClient;
    private List<SignerReg> signerList;
    private UserLocation userLocations;
    private Integer signDocsCount = 0, locationCount = 0;
    private String licenseNo, selectedImg, hashcode;
    private int balanceTransCount;
    private Transactions transactions;
    private IJsonListener iJsonListener;
    private String savedEmail, scanRef, stampName, transactionId, hashlink;
    private int count, signDocCount, addSealCount, addDocCount, addLocCount;
    private ImageView imgInfo1, imgInfo2, imgInfo3, imgInfo4, imgInfo5;
    private Info info;
    private String from = "";
    private boolean enabledGPS;
    private LocationManager service;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;
    private List<Integer> stampCountArray;

    private ProgressBar itemImageProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notarize_steps);

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

        transactionId = getIntent().getStringExtra("transactionId");
        if (transactionId == null) {
            transactionId = "";
        }

        buttonClicks();
        setupRecyclers();

    }


    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclers();
    }


    private void setupRecyclers() {

        //First Step

        //   databaseClient = DatabaseClient.getInstance(this);
        try {
            new GetSigners().execute().get();
            if (signerList.size() >= 1) {
                verifySignersLay.setVisibility(View.GONE);
                modHeaderSigners.setVisibility(View.VISIBLE);
                modBodySigners.setVisibility(View.VISIBLE);
                stepsAdapter1 = new VACompleteAdapter(signerList, this);
                layoutManager = new GridLayoutManager(DoneNotarizeActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerSigners.setLayoutManager(layoutManager);
                recyclerSigners.setAdapter(stepsAdapter1);
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
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
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
                btnNotarize.setEnabled(true);
                addLocationLay.setVisibility(View.GONE);
                modHeaderLocation.setVisibility(View.VISIBLE);
                modBodyLocation.setVisibility(View.VISIBLE);
                modHash.setVisibility(View.VISIBLE);
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
    }


    private void buttonClicks() {
        //Button click to close and save.
        btnNotarize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                intent.putExtra("transactionId", transactionId);
                startActivity(intent);

            }
        });

        btnBlockChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashlink = "https://www.notaryapp.com/notary-directory/blockchain?hashcode=" + hashcode;
                //Log.d("HASHLINK", hashlink);
                //Intent intentWebLink = new Intent(Intent.ACTION_VIEW);
                //intentWebLink.setData(Uri.parse(hashlink));
                //startActivity(intentWebLink);*/
                Intent intent = new Intent(getApplicationContext(), BlockChainViewActivity.class);
                intent.putExtra("hash_link", hashlink);
                startActivity(intent);

            }
        });


        //View click to start add location.
        locationEditIcon.setVisibility(View.GONE);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFromCloseAll().execute();
            }
        });


        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DoneNotarizeActivity.this, info.getVaSigners());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DoneNotarizeActivity.this, info.getVaSigndocuments());
            }
        });
        imgInfo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DoneNotarizeActivity.this, info.getVaAddseal());
            }
        });
        imgInfo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DoneNotarizeActivity.this, info.getVaAdddocuments());
            }
        });
        imgInfo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DoneNotarizeActivity.this, info.getVaAddLocation());
            }
        });

        sealEdit.setVisibility(View.GONE);

    }

    private void init() {
        btnNotarize = findViewById(R.id.btnSelect);
        btnBack = findViewById(R.id.btn_pro_back);
        btnClose = findViewById(R.id.btn_pro_close);
        btnBlockChain = findViewById(R.id.btnBlockChain);
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
        modHash = findViewById(R.id.modHash);
        footerView = findViewById(R.id.rl);
        footerView.setVisibility(View.VISIBLE);
        sealEdit = findViewById(R.id.sealEditIcon);

        infoText = findViewById(R.id.firstView);
        infoText.setVisibility(View.GONE);

        imgInfo1 = findViewById(R.id.imgInfo1);
        imgInfo2 = findViewById(R.id.imgInfo2);
        imgInfo3 = findViewById(R.id.imgInfo3);
        imgInfo4 = findViewById(R.id.imgInfo4);
        imgInfo5 = findViewById(R.id.imgInfo5);

        loc = findViewById(R.id.loc);
        recyclerSigners = findViewById(R.id.recyclerSigners);
        recyclerAddDocs = findViewById(R.id.recyclerAddDocs);
        databaseClient = DatabaseClient.getInstance(this);

        transactions = new Transactions();
        new SelectEmail().execute();
        new SelectCount().execute();
        new GeInfo().execute();
        btnNotarize.setText("SHARE");
        btnNotarize.setEnabled(true);
        itemImageProgress = findViewById(R.id.itemImageProgress);

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
                hashcode = sealAdded.getSealCode();
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
                String textLicense = "License #" + licenseNo;
                licenseText.setText(textLicense);

                if (!selectedImg.equalsIgnoreCase("")) {

                    Picasso.with(DoneNotarizeActivity.this).load(selectedImg).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(200, 200)
                            .into(itemImg, new Callback() {
                                @Override
                                public void onSuccess() {
                                    itemImageProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    itemImg.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });
//                    Picasso.with(DoneNotarizeActivity.this)
//                            .load(selectedImg)
//                            .into(itemImg);
                }

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
                stepsAdapter3 = new VACompleteDocsAdapter(docList, stampCountArray, DoneNotarizeActivity.this);
                layoutManager = new GridLayoutManager(DoneNotarizeActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerAddDocs.setLayoutManager(layoutManager);
                recyclerAddDocs.setAdapter(stepsAdapter3);
                addDocsLay.setVisibility(View.GONE);
                modHeaderAddDocs.setVisibility(View.VISIBLE);
                modBodyAddDocs.setVisibility(View.VISIBLE);

            }

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
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            finish();
        }

    }

}
