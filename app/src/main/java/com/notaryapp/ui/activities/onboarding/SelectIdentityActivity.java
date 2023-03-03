
package com.notaryapp.ui.activities.onboarding;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDeallocationCallback;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyInitiateCallback;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.IdentityType;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class SelectIdentityActivity extends BaseActivity implements NetverifyDeallocationCallback {

    private Spinner spinner_identityType;
    private Button verifyBtn,backBtn;
    public static final int REF_VIEW_CONTAINER = R.id.identityRoot;
    private final String TAG="SelectIdentityFragment";
    private ImageView imgInfo;
    private ConstraintLayout watchNowcLayout;

    private Info info;
    private boolean permission = false,flag=false;
    private String selectedIdType="";
    private Context context ;
    private String savedEmail,scanReference;
    private IJsonListener iJsonListener;
    private DatabaseClient databaseClient;
    private NetverifySDK netverifySDK = null;
    private NVDocumentType selectedDocument;

    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String scanRef;
    private Set<NVDocumentType> arry;
    private ArrayList<String> arrySpin;

    private ArrayList<NVDocumentType> arryDoc;
    private JumioKeys jumioKeys;
    private JumioScanDetails jumioScanDetails;
    private IdentityType identityType;
    private List<String> deletedType;
    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_identity);

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

        selectedType = getIntent().getStringExtra("SELECTEDTYPE");
        if(selectedType == null){
            selectedType = "";
        }
        spinner_identityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedIdType = arrySpin.get(position);
                arryDoc.clear();
                if(selectedIdType.equals("Passport")) {
                    arryDoc.add(NVDocumentType.PASSPORT);
                }else if(selectedIdType.equals("Driver's License")) {
                    arryDoc.add(NVDocumentType.DRIVER_LICENSE);
                }else if(selectedIdType.equals("Identity Card")) {
                    arryDoc.add(NVDocumentType.IDENTITY_CARD);
                }else if(selectedIdType.equals("Visa")) {
                    arryDoc.add(NVDocumentType.VISA);
                }else{

                }
//                Log.e("SELECTEDdOCCCC", arryDoc.get(0).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedType.equals("")) {
                    new AddIDType().execute();
                }
                //Testing
//              startActivity(new Intent(SelectIdentityActivity.this,VerifyBaseActivity.class));
                //Testing

                if (permission){
                    //Sourav 10-12-2020 // Undo


                    initializeNetverifySDK();

                    //Intent openLink = new Intent(SelectIdentityActivity.this, AddSelfieActivity.class);
                    //startActivity(openLink);
                    //testing//

                    /*Intent intent = new Intent(SelectIdentityActivity.this,VerifyBaseActivity.class);
                    intent.putExtra("SCANREFF","c6bd4470-5fd9-458b-9e80-958bafd15bbc");
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);*/

                }
                else {
                    callCustomDialog();
                    flag = true;
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("ClickMe","Click");
                CustomDialog.notaryappInfoDialog(SelectIdentityActivity.this,info.getSelectId());
            }
        });

        watchNowcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/10sKGfWIKqM"));
                startActivity(openLink);
            }
        });
    }
    private void callCustomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Permissions are not granted");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", SelectIdentityActivity.this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        dialog.show();
    }
    private void init(){
        context = this;
        spinner_identityType = findViewById(R.id.identityType);
        verifyBtn = findViewById(R.id.btn_verify_Start);
        backBtn = findViewById(R.id.btn_verify_back);
        imgInfo =findViewById(R.id.infoIcon);
        watchNowcLayout = findViewById(R.id.watch_now_cLayout);
        arryDoc = new ArrayList<>();
        arrySpin = new ArrayList<>();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        new GeInfo().execute();
        //new GetDeletedType().execute();
        checkCameraPermission();

        arrySpin.add("Driver's License");
        arrySpin.add("Passport");
        arrySpin.add("Identity Card");
        arrySpin.add("Visa");

        new SelectIDType().execute();

    }
    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

            permission = true;

        }else {
            // Do something, when permissions are already granted
            permission = false;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // getActivity().finish();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(flag)
            this.finish();
    }
    class SelectIDType extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... voids) {

            List<String> selectedArry = databaseClient.getAppDatabase().identityTypeDao().getAllSelectedIds();
            //  count = identityType.getTypeId();
            //  selectedType = identityType.getSelectedType();
            return selectedArry;
        }

        @Override
        protected void onPostExecute(List<String> selectedArry) {
            super.onPostExecute(selectedArry);
            String type;
            if (selectedArry.size() == 0) {
                arrySpin.clear();
                arrySpin.add("Driver's License");
                arrySpin.add("Passport");
                arrySpin.add("Identity Card");
                arrySpin.add("Visa");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectIdentityActivity.this, R.layout.spinner_text_view, arrySpin);
                spinner_identityType.setAdapter(dataAdapter);
                //new DeleteAllSelectId().execute();
            } else {
                if (selectedType.equals("")) {
                    //  arrySpin.clear();
                    for (int i = 0; i < selectedArry.size(); i++) {
                        type = selectedArry.get(i);
                        if (arrySpin.contains(type)) {
                            arrySpin.remove(type);
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectIdentityActivity.this, R.layout.spinner_text_view, arrySpin);
                    spinner_identityType.setAdapter(dataAdapter);
                } else {
                    arrySpin.clear();
                    arrySpin.add(selectedType);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectIdentityActivity.this, R.layout.spinner_text_view, arrySpin);
                    spinner_identityType.setAdapter(dataAdapter);
                }
            }
        }
    }
    class AddIDType extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            identityType = new IdentityType(selectedIdType, false, 0);
            databaseClient.getAppDatabase().identityTypeDao().insert(identityType);

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }
    }
    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            savedEmail = databaseClient.getAppDatabase().userRegDao().getEmail();

            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            scanRef = email + Calendar.getInstance().getTimeInMillis();
            apiToken = jumioKeys.getJumiokey();
            apiSecret = jumioKeys.getJumiosecret();
            dataCenter = JumioDataCenter.US;
//            Log.e("TAGGGGGGG", scanRef);
        }

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
    private void initializeNetverifySDK() {
        CustomDialog.showProgressDialog(this);
        try {
            // You can get the current SDK version using the method below.
//			NetverifySDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
//            if (!NetverifySDK.isSupportedPlatform(this))
//                Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
//            if (NetverifySDK.isRooted(this))
//                Log.w(TAG, "Device is rooted");

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(this, apiToken, apiSecret, dataCenter);
//            Log.e("apitOKENNNNNNN",apiToken);
            netverifySDK.setEnableVerification(true);

            netverifySDK.setPreselectedCountry("USA");
            netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);
            netverifySDK.setPreselectedDocumentTypes(arryDoc);
            netverifySDK.setCustomerInternalReference(scanRef);

            // netverifySDK.setEnableIdentityVerification(switchIdentityVerification.isChecked());
            netverifySDK.setEnableIdentityVerification(false);
            netverifySDK.setCustomTheme(R.style.CustomNetverifyTheme);
            netverifySDK.sendDebugInfoToJumio(false);

            // Use the following method to initialize the SDK before displaying it
            netverifySDK.initiate(new NetverifyInitiateCallback() {
                @Override
                public void onNetverifyInitiateSuccess() {
                    try {
                        if (netverifySDK != null) {
                            // view.setEnabled(false);
                            netverifySDK.start();
//                            Log.e("Invalid","Jumio Initialized");
                            // startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
                            CustomDialog.cancelProgressDialog();
                        }
                    } catch (MissingPermissionException e) {
                        Toast.makeText(SelectIdentityActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        // view.setEnabled(true);
                    }catch (Exception e){
//                        Log.e("InvalidId",e.getMessage());
                    }
                }
                @Override
                public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
                }
            });

        } catch (PlatformNotSupportedException | NullPointerException e) {
//            Log.e(TAG, "Error in initializeNetverifySDK: ", e);
            Toast.makeText(SelectIdentityActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            netverifySDK = null;
        }
    }

    //  @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("JUmioREturnnnnnnn",data.toString());
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null) {
                Toast.makeText(SelectIdentityActivity.this,"Invalid ID", Toast.LENGTH_LONG).show();
                return;
            }
            scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE).trim();
//            Log.e("scanReferencecccc",scanReference);

            if (resultCode == Activity.RESULT_OK) {
                //Handle the success case and retrieve data
                NetverifyDocumentData documentData = (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                //NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;

                //  Toast.makeText(SelectIdentityActivity.this, documentData.getFirstName() + "  " + documentData.getLastName(), Toast.LENGTH_LONG).show();
                jumioScanDetails = new JumioScanDetails(scanReference,documentData.getFirstName(),
                        documentData.getLastName(),documentData.getSelectedDocumentType().toString());
                //   new InsertJumioScanDetails().execute();

                //  String fName = jumioScanDetails.getFirstName();
                Intent intent = new Intent(SelectIdentityActivity.this,VerifyBaseActivity.class);
                intent.putExtra("SCANREFF",scanReference);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
                String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
                String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);

                new DeleteAllID().execute();
            }

            //At getActivity() point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            if (netverifySDK != null) {
                netverifySDK.destroy();
                //  netverifySDK.checkDeallocation(getActivity());
                netverifySDK = null;
            }
        }
    }

    @Override
    public void onNetverifyDeallocated() {
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (btnStart != null) {
//                        btnStart.setEnabled(true);
//                    }
//                }
//            });
//        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

    }


    class DeleteAllID extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            databaseClient.getAppDatabase().identityTypeDao().deleteAll();
            //  count = identityType.getTypeId();
            //  selectedType = identityType.getSelectedType();
            return "";
        }

        @Override
        protected void onPostExecute(String selectedArry) {
            super.onPostExecute(selectedArry);

        }
    }

    /////////////////////////////
    private void loadFragment(Fragment fragment) {

        FragmentViewUtil.replaceFragment(this,
                VerifyBaseActivity.REF_VIEW_CONTAINER,
                fragment,false);

    }
}
