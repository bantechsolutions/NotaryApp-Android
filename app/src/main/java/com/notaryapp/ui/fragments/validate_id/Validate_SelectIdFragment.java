package com.notaryapp.ui.fragments.validate_id;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDeallocationCallback;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyInitiateCallback;
import com.jumio.nv.NetverifyMrzData;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.data.document.NVDocumentType;
import com.jumio.nv.data.document.NVDocumentVariant;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.ValidateId_IdentityType;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.ValidateActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class Validate_SelectIdFragment extends Fragment implements NetverifyDeallocationCallback {

    //public static String scanReference;
    private String scanReference;
    private String TAG = "Validate";
    private Button confirmBtn, backBtn, closeBtn;
    private View selectIdView;
    private Spinner validate_spinner_identityType;
    private ImageView imgInfo;
    private ArrayList<NVDocumentType> arrayList_identity;
    private String selectedIdType, selectedTypeRoom, email;
    private NVDocumentType selectedDocument;
    private int selectedId;
    private ValidateId_IdentityType identityType;
    private DatabaseClient databaseClient;
    private Context context;
    private boolean permission = false, flag = false;
    private Info info;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private Bundle args;
    private String scanRef;
    private NetverifySDK netverifySDK = null;
    private Set<NVDocumentType> arry;
    private ArrayList<String> arrySpin;

    private ArrayList<NVDocumentType> arryDoc;
    private Bundle bundle;
    private String selectedType = "";
    private JumioKeys jumioKeys;
    private JumioScanDetails jumioScanDetails;
    private IJsonListener iJsonListener;
    private int transactionCount;
    private int daysLeft;


    public Validate_SelectIdFragment() {
    }

    public Validate_SelectIdFragment(String selectedType) {
        this.selectedType = selectedType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        selectIdView = inflater.inflate(R.layout.fragment_validate_select_id, container, false);

        init();

        validate_spinner_identityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //array_identity = getResources().getStringArray(R.array.identityType);
                selectedId = position;
                selectedIdType = arrySpin.get(position);
                arryDoc.clear();
                switch (selectedIdType) {
                    case "Passport":
                        arryDoc.add(NVDocumentType.PASSPORT);
                        break;
                    case "Driver's License":
                        arryDoc.add(NVDocumentType.DRIVER_LICENSE);
                        break;
                    case "Identity Card":
                        arryDoc.add(NVDocumentType.IDENTITY_CARD);
                        break;
                    case "Visa":
                        arryDoc.add(NVDocumentType.VISA);
                        break;
                    default:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Button click to show start next fragment.
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedType.equals("")) {
                    new InsertIDType().execute();
                }
                //Testing
                //loadFragment(new Validate_ProcessFragment("Driving license", "49c72272-8343-4a00-8acb-3f858ccc3cfa"));
                //Testing
                if (transactionCount > 0 && (!(daysLeft > 0))) {
                    /*loadFragment(new Validate_ProcessFragment("Driving license", "49c72272-8343-4a00-8acb-3f858ccc3cfa"));*/
                    initializeNetverifySDK();
                } else {
                    proceedDialog("Plan expired. Please recharge");


                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DashBoardActivity.class));
                getActivity().finish();
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(), info.getSelectId());
            }
        });
        return selectIdView;
    }

    private void proceedDialog(String errMess) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(errMess);
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MembershipActivity.class));

            }
        });
        dialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            permission = true;

        } else {
            // Do something, when permissions are already granted
            permission = false;
        }
    }

    private void init() {
        checkCameraPermission();
        arryDoc = new ArrayList<>();
        imgInfo = selectIdView.findViewById(R.id.infolicense3);
        confirmBtn = selectIdView.findViewById(R.id.btn_validate);
        backBtn = selectIdView.findViewById(R.id.btn_verify_back);
        // closeBtn = selectIdView.findViewById(R.id.btn_close);
        validate_spinner_identityType = selectIdView.findViewById(R.id.validate_identityType);


        context = getActivity();
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        new GeInfo().execute();

        arrayList_identity = new ArrayList();

        arrySpin = new ArrayList<>();

        arrySpin.add("Driver's License");
        arrySpin.add("Passport");
        arrySpin.add("Identity Card");
        arrySpin.add("Visa");

        new SelectIDType().execute();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), ValidateActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    private void initializeNetverifySDK() {
        CustomDialog.showProgressDialog(getActivity());
        try {

//            if (!NetverifySDK.isSupportedPlatform(getActivity()))
//                Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
//            if (NetverifySDK.isRooted(getActivity()))
//                Log.w(TAG, "Device is rooted");

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(getActivity(), apiToken, apiSecret, dataCenter);
            netverifySDK.setEnableVerification(true);

            netverifySDK.setPreselectedCountry("USA");
            netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC);
            netverifySDK.setPreselectedDocumentTypes(arryDoc);
            netverifySDK.setCustomerInternalReference(scanRef);

            // netverifySDK.setEnableIdentityVerification(switchIdentityVerification.isChecked());
            netverifySDK.setEnableIdentityVerification(false);
            netverifySDK.setCustomTheme(R.style.CustomNetverifyTheme);
            netverifySDK.sendDebugInfoToJumio(true);

            // Use the following method to initialize the SDK before displaying it
            netverifySDK.initiate(new NetverifyInitiateCallback() {
                @Override
                public void onNetverifyInitiateSuccess() {
                    try {
                        if (netverifySDK != null) {
                            // view.setEnabled(false);
                            Log.d("ERR_MSG1","netverifySDK not null");
                            // netverifySDK.start();
                            startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
                            CustomDialog.cancelProgressDialog();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ERR_MSG", e.getMessage());
                        // view.setEnabled(true);
                    }
                }

                @Override
                public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
                }
            });

        } catch (PlatformNotSupportedException | NullPointerException e) {
//            Log.e(TAG, "Error in initialize Net verify SDK: ", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("ERR_MSG2", e.getMessage());
            netverifySDK = null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("JUmioREturnnnnnnn", data.toString());
        if (requestCode == NetverifySDK.REQUEST_CODE ) {

                if (resultCode == Activity.RESULT_OK) {

                    scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE).trim();
                    //Handle the success case and retri eve data
                    NetverifyDocumentData documentData = (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                    NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;
                    // Toast.makeText(getActivity(), documentData.getFirstName() + "  " + documentData.getLastName(), Toast.LENGTH_LONG).show();
               /* jumioScanDetails = new JumioScanDetails(scanReference, documentData.getFirstName(),
                        documentData.getLastName(), documentData.getSelectedDocumentType().toString());
                new InsertJumioScanDetails().execute();*/
                    loadFragment(new Validate_ProcessFragment(selectedType, scanReference));
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Handle the error cases as described in our documentation: https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_faq.md#managing-errors
                    String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
                    String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);
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

    private void checkTransactions() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("email", email);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.CHK_TRAN_COUNT, "chkTrans");
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
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("chkTrans")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");

                            transactionCount = total_bought - total_used;

                            //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();
                            try {
                                daysLeft = (int) Utils.dateDiff(data.getString("current_date")
                                        ,data.getString("ending_at"));
                            } catch (Exception e) {
                                daysLeft = 0;
                            }
                        }

                    } else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(), "Error! No data fetched");
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(getActivity(), msg);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    class InsertIDType extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            identityType = new ValidateId_IdentityType(selectedIdType, false, 0);
            databaseClient.getAppDatabase().validateIdIdentityTypeDao().insert(identityType);
            return "";
        }

        @Override
        protected void onPostExecute(String selectedArry) {
            super.onPostExecute(selectedArry);

        }
    }

    class SelectIDType extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... voids) {

            List<String> selectedArry = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getAllSelectedIds();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, arrySpin);
                validate_spinner_identityType.setAdapter(dataAdapter);
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
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, arrySpin);
                    validate_spinner_identityType.setAdapter(dataAdapter);
                } else {
                    arrySpin.clear();
                    arrySpin.add(selectedType);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, arrySpin);
                    validate_spinner_identityType.setAdapter(dataAdapter);
                }
            }
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

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            email = databaseClient.getAppDatabase().userRegDao().getEmail();

            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            return email;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            scanRef = email + Calendar.getInstance().getTimeInMillis();
            apiToken = jumioKeys.getJumiokey();
            apiSecret = jumioKeys.getJumiosecret();
            dataCenter = JumioDataCenter.US;

//            Log.e("TAGGGGGGG", scanRef);
//Checking transaction count if plan expired or NOT
            checkTransactions();
        }

    }
}