package com.notaryapp.lockadoc.activityes;

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
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.notaryapp.R;
import com.notaryapp.adapter.VAStepsAdapter;
import com.notaryapp.components.ButtonBounceInterpolator;
import com.notaryapp.lockadoc.adapter.LADStepDocsAdapter;
import com.notaryapp.lockadoc.adapter.LADStepPartyAdapter;
import com.notaryapp.lockadoc.adapter.LADStepPartyAdapterComplete;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.BlockChainViewActivity;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.activities.verifyauthenticate.AddDocsActivity;
import com.notaryapp.ui.activities.verifyauthenticate.AddSealActivity;
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

public class LADStepsActivity extends BaseActivity {

    private static final int REF_VIEW_CONTAINER = R.id.notarizeStepsContainer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private CardView verifySignersLay, signDocsLay, addLocationLay, sendLink;
    ConstraintLayout modBodySigners;
    //ConstraintLayout modHeaderSigners;
    RelativeLayout modHeaderSignDocs;
    RelativeLayout modHeaderAddDocs, modBodyAddDocs;
    ConstraintLayout modHeaderLocation, modBodyLocation, locationMap, modHash;
    RelativeLayout modBodyAddDocs1;
    private View childview1;
    private TextView loc;
    private LinearLayout infoText;
    private ImageView itemImg, locationEditIcon;
    private Button btnNotarize, btnBack, btnClose, btnBlockChain;
    private List<String> sealList;
    private List<DocumentsModel> docList;
    private RecyclerView recyclerSigners, recyclerSeal, recyclerAddDocs;
    private GridLayoutManager layoutManager;
    private VAStepsAdapter stepsAdapter1, stepsAdapter2;
    private LADStepPartyAdapter stepsAdapter11;
    private LADStepPartyAdapterComplete stepsAdapter12;
    private LADStepDocsAdapter stepsAdapter3;
    private DatabaseClient databaseClient;
    private List<SignerReg> signerList;
    private UserLocation userLocations;
    private Integer signDocsCount = 0, locationCount = 0;
    private String licenseNo, selectedImg, hashcode;
    private int balanceTransCount;
    private Transactions transactions;
    private IJsonListener iJsonListener;
    private String savedEmail, scanRef, stampName, transactionId, hashlink;
    private int count, signDocCount, addSealCount, addDocCount, addLocCount, witnessCount;
    private ImageView imgInfo1, imgInfo2, imgInfo5;
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
    ConstraintLayout sendLinkLayout;

    private ConstraintLayout overlay1, overlay3, overlay4, overlay7;
    public boolean isClick = true;

    List<LADParties> ladParties;

    private SignerReg signerReg;
    private WitnessReg witnessReg;


    private ArrayList<WitnessReg> wArrayList;
    private ArrayList<SignerReg> sArrayList;
    private ArrayList<DocumentsModel> addDocArrayList;
    private LinearLayout headerText;
    private String device_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lad_steps);

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
        } else {
            if (from.equalsIgnoreCase("pending")) {
                infoText.setVisibility(View.GONE);
                headerText.setVisibility(View.VISIBLE);
            } else if (from.equalsIgnoreCase("Completed")) {
                headerText.setVisibility(View.GONE);
                sendLinkLayout.setVisibility(View.GONE);
                btnNotarize.setText("Share");
                btnNotarize.setEnabled(true);
                modHash.setVisibility(View.VISIBLE);

            }
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
        try {
            if (!from.equalsIgnoreCase("Completed")) {
                completedDialog();
            }else {
                finish();
            }
        }catch (Exception e){
            completedDialog();
        }
    }

    private void setupRecyclers() {

        //First Step
        //   databaseClient = DatabaseClient.getInstance(this);
        try {
            new GetSigners().execute().get();
            if (signerList.size() >= 1) {
                /*infoText.setVisibility(View.GONE);
                verifySignersLay.setVisibility(View.GONE);
                signDocsLay.setVisibility(View.VISIBLE);
                // modHeaderSigners.setVisibility(View.VISIBLE);
                modBodySigners.setVisibility(View.VISIBLE);
                stepsAdapter1 = new VAStepsAdapter(signerList, this);
                layoutManager = new GridLayoutManager(LADStepsActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerSigners.setLayoutManager(layoutManager);
                recyclerSigners.setAdapter(stepsAdapter1);
                overlay1.setVisibility(View.GONE);*/
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
                // signDocsLay.setVisibility(View.GONE);
                modHeaderSignDocs.setVisibility(View.VISIBLE);
                // modBodyAddDocs1.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //Add doc Step
        docList = new ArrayList<>();
        new GetDocs().execute();

        //get hashcode

        try {
            hashcode = new getHashCode().execute().get();
        } catch (ExecutionException | InterruptedException e){
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }


        //Location Step
        userLocations = new UserLocation();
        try {
            locationCount = new GetLocation().execute().get();
            if (locationCount > 0) {
                btnNotarize.setEnabled(true);
                //  btnNotarize.setText("COMPLETE");
                addLocationLay.setVisibility(View.GONE);
                modHeaderLocation.setVisibility(View.VISIBLE);
                modBodyLocation.setVisibility(View.VISIBLE);
                //modHash.setVisibility(View.VISIBLE);
                overlay7.setVisibility(View.GONE);
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
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(final Void... params) {
                        transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final Void result) {
                        Intent intent = new Intent(getApplicationContext(), LADShareActivity.class);
                        intent.putExtra("transactionId", transactionId);
                        startActivity(intent);
                    }
                }.execute();
            }
        });

        //View click to start add signers.
        verifySignersLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("Pending")) {
                    Intent intent = new Intent(LADStepsActivity.this, ScanDocumentActivity.class);
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
                if (docList.size() >= 1) {
                    Intent intent = new Intent(LADStepsActivity.this, PhotographPartyDocumentActivity.class);
                    startActivity(intent);
                } else {
                    CustomDialog.notaryappDialogSingle(LADStepsActivity.this, "Please select VerifySigners");
                }
            }
        });

        //View click to start add location.
        addLocationLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ladParties != null && ladParties.size() > 0) {
                    if (ActivityCompat.checkSelfPermission(LADStepsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(LADStepsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LADStepsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                1000);
                    } else {
                        Intent intent = new Intent(LADStepsActivity.this, LADMapActivity.class);
                        startActivity(intent);
                    }
                } else {
                    CustomDialog.notaryappDialogSingle(LADStepsActivity.this, "Please select AddDoc");
                }

            }
        });
        //View click to start add location.
        locationEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(LADStepsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(LADStepsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LADStepsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            1000);
                } else {
                    Intent intent = new Intent(LADStepsActivity.this, LADMapActivity.class);
                    startActivity(intent);
                }
            }
        });

        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(final Void... params) {
                        transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final Void result) {
                        Intent intent = new Intent(getApplicationContext(), LADShareActivity.class);
                        intent.putExtra("transactionId", transactionId);
                        startActivity(intent);
                    }
                }.execute();

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!from.equalsIgnoreCase("Completed")) {
                        completedDialog();
                    }else {
                        finish();
                    }
                }catch (Exception e){
                    completedDialog();
                }
            }
        });
        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(LADStepsActivity.this, info.getVaSigners());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(LADStepsActivity.this, info.getVaSigndocuments());
            }
        });

        imgInfo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(LADStepsActivity.this, info.getVaAddLocation());
            }
        });

        btnBlockChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hashlink = "https://www.notaryapp.com/notary-directory/blockchain?hashcode=" + hashcode;
                //Log.d("HASHLINK", hashlink);
                //Intent intentWebLink = new Intent(Intent.ACTION_VIEW);
                //intentWebLink.setData(Uri.parse(hashlink));
                //startActivity(intentWebLink);
                Intent intent = new Intent(getApplicationContext(), BlockChainViewActivity.class);
                intent.putExtra("hash_link", hashlink);
                startActivity(intent);

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
        btnBlockChain = findViewById(R.id.btnBlockChain);
        itemImg = findViewById(R.id.itemImage);

        verifySignersLay = findViewById(R.id.verifySignersLay);
        // modHeaderSigners = findViewById(R.id.modHeaderSigners);
        modBodySigners = findViewById(R.id.modBodySigners);
        signDocsLay = findViewById(R.id.signDocsLay);
        modHeaderSignDocs = findViewById(R.id.modHeaderSignDocs);
        modHeaderAddDocs = findViewById(R.id.modHeaderAddDocs);
        modBodyAddDocs = findViewById(R.id.modBodyAddDocs);
        modBodyAddDocs1 = findViewById(R.id.modBodyAddDocs1);
        addLocationLay = findViewById(R.id.addLocationLay);
        modHeaderLocation = findViewById(R.id.modHeaderLocation);
        modBodyLocation = findViewById(R.id.modBodyLocation);
        locationEditIcon = findViewById(R.id.locationEditIcon);
        modHash = findViewById(R.id.modHash);
        infoText = findViewById(R.id.firstView);
        sendLink = findViewById(R.id.sendLink);

        imgInfo1 = findViewById(R.id.imgInfo1);
        imgInfo2 = findViewById(R.id.imgInfo2);
        imgInfo5 = findViewById(R.id.imgInfo5);

        overlay1 = findViewById(R.id.overlay1);
        overlay3 = findViewById(R.id.overlay3);
        overlay4 = findViewById(R.id.overlay4);
        overlay7 = findViewById(R.id.overlay7);
        headerText = findViewById(R.id.firstView);
        sendLinkLayout = findViewById(R.id.sendLinkLayout);

        loc = findViewById(R.id.loc);
        recyclerSigners = findViewById(R.id.recyclerSigners);
        recyclerAddDocs = findViewById(R.id.recyclerAddDocs);
        databaseClient = DatabaseClient.getInstance(this);

        transactions = new Transactions();

        sArrayList = new ArrayList<SignerReg>();
        wArrayList = new ArrayList<WitnessReg>();
        addDocArrayList = new ArrayList<DocumentsModel>();

        new SelectEmail().execute();
        initIJsonListener();
        new SelectCount().execute();
        new GeInfo().execute();
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
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.COMPLETE_TRANSACTION, "completeTrans");
        } catch (Exception e) {
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
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_TRANSACTION_LAD, "startTrans");
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
                CustomDialog.notaryappDialogSingle(LADStepsActivity.this,
                        Utils.errorMessage(LADStepsActivity.this));
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
            for (int i = 0; i < signerList.size(); i++) {
                signerType = signerList.get(i).getSignerType();

                if (signerType.equals("WIT")) {
                    witnessCount = databaseClient.getAppDatabase().witnessRegDao().getCount();
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer sList) {
            super.onPostExecute(sList);
            if (witnessCount > 0) {
                modBodyAddDocs1.setVisibility(View.VISIBLE);
            }
        }
    }

    class GetSignStatus extends AsyncTask<Void, Void, Integer> {
        int itemCount = 0;

        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            ladParties = databaseClient.getAppDatabase()
                    .ladPartiesDao()
                    .getStates();
            return itemCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (ladParties.size() > 0) {
                modBodyAddDocs1.setVisibility(View.VISIBLE);
                recyclerSigners.setVisibility(View.VISIBLE);
                signDocsLay.setVisibility(View.GONE);
                //  infoText.setVisibility(View.GONE);
                // verifySignersLay.setVisibility(View.GONE);
                //signDocsLay.setVisibility(View.VISIBLE);
                // modBodySigners.setVisibility(View.VISIBLE);
                if (from.equalsIgnoreCase("Completed")) {
                    stepsAdapter12 = new LADStepPartyAdapterComplete(ladParties, ladParties.size(), LADStepsActivity.this);
                    layoutManager = new GridLayoutManager(LADStepsActivity.this, 1,
                            GridLayoutManager.HORIZONTAL, false);
                    recyclerSigners.setLayoutManager(layoutManager);
                    recyclerSigners.setAdapter(stepsAdapter12);
                } else {
                    stepsAdapter11 = new LADStepPartyAdapter(ladParties, ladParties.size(), LADStepsActivity.this);
                    layoutManager = new GridLayoutManager(LADStepsActivity.this, 1,
                            GridLayoutManager.HORIZONTAL, false);
                    recyclerSigners.setLayoutManager(layoutManager);
                    recyclerSigners.setAdapter(stepsAdapter11);

                }
                overlay1.setVisibility(View.GONE);
                modHeaderSignDocs.setVisibility(View.VISIBLE);
                overlay4.setVisibility(View.GONE);

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
                if(from.equalsIgnoreCase("Completed")) {
                    stepsAdapter3 = new LADStepDocsAdapter(docList, stampCountArray, LADStepsActivity.this,true);
                }else{
                    stepsAdapter3 = new LADStepDocsAdapter(docList, stampCountArray, LADStepsActivity.this,false );
                }
                layoutManager = new GridLayoutManager(LADStepsActivity.this,
                        1, GridLayoutManager.HORIZONTAL, false);
                recyclerAddDocs.setLayoutManager(layoutManager);
                recyclerAddDocs.setAdapter(stepsAdapter3);
                modHeaderAddDocs.setVisibility(View.VISIBLE);
                modBodyAddDocs.setVisibility(View.VISIBLE);
                verifySignersLay.setVisibility(View.GONE);
                overlay1.setVisibility(View.GONE);
                //modHeaderSignDocs.setVisibility(View.VISIBLE);
                //overlay4.setVisibility(View.GONE);
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

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
//            savedEmail =  databaseClient.getAppDatabase()
//                    .userRegDao()
//                    .getEmail();
            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            completeTransaction();
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
            Intent intent = new Intent(LADStepsActivity.this, ScanDocumentActivity.class);
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
                Intent intent = new Intent(LADStepsActivity.this, AddSealActivity.class);
                startActivity(intent);

            } else {
                CustomDialog.notaryappDialogSingle(LADStepsActivity.this, "Please take signDoc");
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
                Intent intent = new Intent(LADStepsActivity.this, AddDocsActivity.class);
                startActivity(intent);
                //  finish();
            } else {
                CustomDialog.notaryappDialogSingle(LADStepsActivity.this, "Please select AddSeal");
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

    class getHashCode extends AsyncTask<String, Void, String> {
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
            return hashcode;
        }

        @Override
        protected void onPostExecute(String hashcode) {
            //super.onPostExecute(count);
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
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();

            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            startActivity(new Intent(LADStepsActivity.this, LADComplete.class));
            finish();
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
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
        }

    }

    class DeleteAllClose extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();

            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
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

                    Intent intent = new Intent(LADStepsActivity.this, LADMapActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    ///////////////////////////////////////////////////////////////
    //Share Doc


    @Override
    protected void onDestroy() {
        super.onDestroy();
        new DeleteAllClose().execute();
    }
}
