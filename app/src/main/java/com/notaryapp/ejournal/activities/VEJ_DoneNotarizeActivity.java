package com.notaryapp.ejournal.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.notaryapp.adapter.VACompleteDocsAdapter;
import com.notaryapp.ejournal.adapter.VEJ_PDFPrintDocumentAdapter;
import com.notaryapp.ejournal.adapter.VEJ_VACompleteAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JournalFees;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.UserNote;
import com.notaryapp.ui.activities.BlockChainViewActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.activities.verifyauthenticate.ShareActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.IJsonListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class VEJ_DoneNotarizeActivity extends BaseActivity {

    private static final int REF_VIEW_CONTAINER = R.id.notarizeStepsContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    CardView verifySignersLay, signDocsLay, addSealLay, addDocsLay, addLocationLay, addJournalLay, addNoteLay, signatureThumbLay;
    ConstraintLayout modHeaderSigners, modBodySigners, footerView;
    ConstraintLayout modHeaderSignDocs, modHeaderSeal, modBodySeal, modHeaderAddDocs, modBodyAddDocs;
    ConstraintLayout modHeaderLocation, modBodyLocation, locationMap, modHash;
    ConstraintLayout modHeaderJournal, modBodyJournal;
    ConstraintLayout modHeaderNote, modBodyNote;
    ConstraintLayout modHeaderSignatureThumb;
    private Guideline gLineBottom;
    private View childview1;
    private TextView loc, licenseText;
    private LinearLayout infoText;
    private ImageView itemImg, sealEdit, locationEditIcon, journalEditIcon, noteEditIcon;
    private Button btnNotarize, btnBack, btnClose, btnBlockChain, btnPrintJournal;
    private List<String> sealList;
    private List<DocumentsModel> docList;
    private RecyclerView recyclerSigners, recyclerSeal, recyclerAddDocs;
    private GridLayoutManager layoutManager;
    private VEJ_VACompleteAdapter stepsAdapter1, stepsAdapter2;
    private VACompleteDocsAdapter stepsAdapter3;
    private DatabaseClient databaseClient;
    private List<SignerReg> signerList;
    private UserLocation userLocations;
    private JournalFees journalFees;
    private UserNote userNote;
    private Integer signDocsCount = 0, locationCount = 0, journalCount= 0, noteCount=0;
    private String licenseNo, selectedImg, hashcode;
    private int balanceTransCount;
    private Transactions transactions;
    private IJsonListener iJsonListener;
    private String savedEmail, scanRef, stampName, transactionId, hashlink;
    private int count, signDocCount, addSealCount, addDocCount, addLocCount;
    private ImageView imgInfo1, imgInfo2, imgInfo3, imgInfo4, imgInfo5;
    private Info info;
    private String from = "";
    private TextView notaTypeTxt, journalAmount, journalAmountFrac;
    private TextView noteHeadingText, noteSubText;
    private boolean enabledGPS;
    private LocationManager service;
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;
    private String jAmount, jFrac, jourAmount;
    private List<Integer> stampCountArray;
    private boolean mSignerFlag=true;

    private ProgressBar itemImageProgress;
    private PrintManager printManager;
    private String dest_file_path;

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
                stepsAdapter1 = new VEJ_VACompleteAdapter(signerList, this);
                layoutManager = new GridLayoutManager(VEJ_DoneNotarizeActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
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
                    //overlay2.setVisibility(View.GONE);
                    //startBtn.setVisibility(View.VISIBLE);
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

        //Journal Step
        journalFees = new JournalFees();
        try {
            journalCount = new GetJounral().execute().get();
            if (journalCount > 0){

                addJournalLay.setVisibility(View.GONE);
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


            } else {
                addNoteLay.setVisibility(View.GONE);
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

        btnPrintJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.showProgressDialog(VEJ_DoneNotarizeActivity.this);
                //downloadAndOpenPDF();
                new GetPDF().execute();


            }
        });


        //View click to start add location.
        locationEditIcon.setVisibility(View.GONE);
        journalEditIcon.setVisibility(View.GONE);
        noteEditIcon.setVisibility(View.GONE);


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteFromCloseAll().execute();
            }
        });


        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_DoneNotarizeActivity.this, info.getVaSigners());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_DoneNotarizeActivity.this, info.getVaSigndocuments());
            }
        });
        imgInfo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_DoneNotarizeActivity.this, info.getVaAddseal());
            }
        });
        imgInfo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_DoneNotarizeActivity.this, info.getVaAdddocuments());
            }
        });
        imgInfo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(VEJ_DoneNotarizeActivity.this, info.getVaAddLocation());
            }
        });

        sealEdit.setVisibility(View.GONE);

    }

    private void init() {
        btnNotarize = findViewById(R.id.btnSelect);
        btnBack = findViewById(R.id.btn_pro_back);
        btnClose = findViewById(R.id.btn_pro_close);
        btnBlockChain = findViewById(R.id.btnBlockChain);
        btnPrintJournal = findViewById(R.id.btnPrintJournal);
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
        modHeaderNote = findViewById(R.id.modHeaderNote);
        modBodyNote = findViewById(R.id.modBodyNote);
        noteEditIcon = findViewById(R.id.noteEditIcon);
        signatureThumbLay = findViewById(R.id.signatureThumbLay);
        modHeaderSignatureThumb = findViewById(R.id.modHeaderSignatureThumb);

        modHash = findViewById(R.id.modHash);
        footerView = findViewById(R.id.rl);
        footerView.setVisibility(View.VISIBLE);
        sealEdit = findViewById(R.id.sealEditIcon);

        notaTypeTxt = findViewById(R.id.notaTypeTxt);
        journalAmount = findViewById(R.id.journalFees);
        journalAmountFrac = findViewById(R.id.journalFeesFrac);
        noteHeadingText = findViewById(R.id.noteHeadingText);
        noteSubText = findViewById(R.id.noteSubText);

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
                //String textLicense = "License #" + licenseNo;
                licenseText.setText(licenseNo);

                if (!selectedImg.equalsIgnoreCase("")) {

                    Picasso.with(VEJ_DoneNotarizeActivity.this).load(selectedImg).centerCrop()
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
                stepsAdapter3 = new VACompleteDocsAdapter(docList, stampCountArray, VEJ_DoneNotarizeActivity.this);
                layoutManager = new GridLayoutManager(VEJ_DoneNotarizeActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
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
            finish();
        }

    }
    void downloadAndOpenPDF() {
        /*Thread thread = new Thread(){
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        File file_path = downloadFile(AppUrl.DOWNLOAD_PDF_VEJ);
                        int file_size = Integer.parseInt(String.valueOf(file_path.length()/1024));
                        Log.d("FILE_SIZE", String.valueOf(file_size));

                        if (file_size > 0){
                            printManager = (PrintManager) VEJ_DoneNotarizeActivity.this.getSystemService(Context.PRINT_SERVICE);
                            String jobName = getString(R.string.app_name) + "_"+hashcode;
                            PrintJob printJob = printManager.print(jobName, new VEJ_PDFPrintDocumentAdapter(VEJ_DoneNotarizeActivity.this, hashcode, file_path.getPath()), null);
                        } else {
                            Toast.makeText(VEJ_DoneNotarizeActivity.this, "adddad", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        };
        thread.start();*/
        new Thread(new Runnable() {
            public void run() {

                File file_path = downloadFile(AppUrl.DOWNLOAD_PDF_VEJ);
                int file_size = Integer.parseInt(String.valueOf(file_path.length()/1024));
                Log.d("FILE_SIZE", String.valueOf(file_size));

                if (file_size > 0){
                    printManager = (PrintManager) VEJ_DoneNotarizeActivity.this.getSystemService(Context.PRINT_SERVICE);
                    String jobName = getString(R.string.app_name) + "_"+hashcode;
                    PrintJob printJob = printManager.print(jobName, new VEJ_PDFPrintDocumentAdapter(VEJ_DoneNotarizeActivity.this, hashcode, file_path.getPath()), null);
                }




            }
        }).start();



    }

    File downloadFile(String dwnload_file_path) {
        File docpath = null;
        FileOutputStream fileOutput = null;
        try {
            long millis=System.currentTimeMillis();
            dest_file_path = "vej_"+millis+".pdf";
            Log.d("URL_LINK",hashcode+"_"+transactionId);
            URL url = new URL(dwnload_file_path+hashcode+"_"+transactionId+".pdf");
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            /*urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);*/

            // connect
            urlConnection.connect();


            //File docpath;
            ContextWrapper cw = new ContextWrapper(this.getApplicationContext());
            File directory = cw.getDir("Draw", Context.MODE_PRIVATE);
            if (!directory.exists()) {
                directory.mkdir();
            }

            docpath = new File(directory,  dest_file_path);

            // set the path where we want to save the file
            /*File SDCardRoot = Environment.getExternalStorageDirectory();
            // create a new file, to save the downloaded file
            file = new File(SDCardRoot, dest_file_path);*/

            fileOutput = new FileOutputStream(docpath);
            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
            Log.d("INPUT_DATA_1", "step5");

            // this is the total size of the file which we are
            // downloading
            /*totalsize = urlConnection.getContentLength();
            setText("Starting PDF download...");*/

            // create a buffer...
            byte[] buffer = new byte[1024 * 1024];
            int bufferLength = 0;
            Log.d("INPUT_DATA_2", "step2");

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                Log.d("BUFFER_PATH", String.valueOf(bufferLength));
                /*downloadedSize += bufferLength;
                per = ((float) downloadedSize / totalsize) * 100;
                setText("Total PDF File size  : "
                        + (totalsize / 1024)
                        + " KB\n\nDownloading PDF " + (int) per
                        + "% complete");*/
            }


            // close the output stream when complete //
            //fileOutput.close();
            CustomDialog.cancelProgressDialog();
            //setText("Download Complete. Open PDF Application installed in the device.");

        } catch (final MalformedURLException e) {
            /*setTextError("Some error occured. Press back and try again.",
                    Color.RED);*/
            CustomDialog.cancelProgressDialog();
            //CustomDialog.notaryappDialogSingle(this, "ERROR");
        } catch (final IOException e) {
            /*setTextError("Some error occured. Press back and try again.",
                    Color.RED);*/
            CustomDialog.cancelProgressDialog();
            //CustomDialog.notaryappDialogSingle(this, "ERROR");
        } catch (final Exception e) {
            /*setTextError(
                    "Failed to download image. Please check your internet connection.",
                    Color.RED);*/
        } finally {
            try {
                if(fileOutput != null){
                    fileOutput.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }


        return docpath;
    }

    class GetPDF extends AsyncTask<Void, Void, File> {

        @Override
        protected File doInBackground(Void... voids) {
            //creating a task
            File file_path = downloadFile(AppUrl.DOWNLOAD_PDF_VEJ);
            return file_path;
        }

        @Override
        protected void onPostExecute(File file_path) {
            super.onPostExecute(file_path);

            int file_size = Integer.parseInt(String.valueOf(file_path.length()/1024));
            Log.d("FILE_SIZE", String.valueOf(file_size));

            if (file_size > 0){
                printManager = (PrintManager) VEJ_DoneNotarizeActivity.this.getSystemService(Context.PRINT_SERVICE);
                String jobName = getString(R.string.app_name) + "_"+hashcode;
                PrintJob printJob = printManager.print(jobName, new VEJ_PDFPrintDocumentAdapter(VEJ_DoneNotarizeActivity.this, hashcode, file_path.getPath()), null);
            } else {
                Toast.makeText(VEJ_DoneNotarizeActivity.this, "Document not found.", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
