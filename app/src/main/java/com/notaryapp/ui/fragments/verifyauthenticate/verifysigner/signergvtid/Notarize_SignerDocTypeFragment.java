package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signergvtid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.NotaryApp_IdentityType;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.Notarize_SignerVerifyTypeFragment;
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

//First screen of 'Notarize a Document'.
public class Notarize_SignerDocTypeFragment extends Fragment implements NetverifyDeallocationCallback {

    private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private String TAG = "SignerDocType";
    private View docTypeview;
    private Button btnSelect;
    private Spinner docSpinner;
    private Button btnBack,btnClose;

    private String savedEmail,scanReference;
    private DatabaseClient databaseClient;

    private String selectedIdType;
    private Context context ;
    private NetverifySDK netverifySDK = null;
    private NVDocumentType selectedDocument;

    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String scanRef, selectedType="";
    private ArrayList<String> arrySpin;
    private ImageView imgInfo;
    private Info info;
    private NotaryApp_IdentityType identityType;

    private ArrayList<NVDocumentType> arryDoc;
    private JumioKeys jumioKeys;
    private JumioScanDetails jumioScanDetails;
    private IJsonListener iJsonListener;
    private int transactionCount;
    private int daysLeft;

    public Notarize_SignerDocTypeFragment() {
        // Required empty public constructor
    }

    public Notarize_SignerDocTypeFragment(String selectedType) {
        this.selectedType = selectedType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        docTypeview = inflater.inflate(R.layout.fragment_notarize_signer_doc_type, container, false);
        //Initializations
        init();

        //Button click to start next fragment.
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedType.equals("")) {
                    new InsertIDType().execute();
                }

                //Testing
              /*loadFragment(new Notarize_ProcessFragment("Driving license", "49c72272-8343-4a00-8acb-3f858ccc3cfa"));*/
                //Testing

                if(transactionCount>0 && (!(daysLeft > 0))){
                    /*loadFragment(new Notarize_ProcessFragment("Identity Card", "c6bd4470-5fd9-458b-9e80-958bafd15bbc"));*/
                    initializeNetverifySDK();
                }else{
                    // CustomDialog.notaryappDialogSingle(getActivity(),"Plan expired. Please recharge");
                    //proceedDialog("Plan expired. Please recharge");
                    proceedDialog("Either your plan has expired or you " +
                            "have used all your free transactions.");

                }


            }
        });
        docSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIdType = arrySpin.get(position).toString();
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
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerVerifyTypeFragment());
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(),info.getSelectId());
            }
        });
        return docTypeview;
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
    @Override
    public void onPause() {
        super.onPause();
     /*   if(flag) {
            getActivity().onBackPressed();
        }*/
    }

    private void init() {
        context = getActivity();
        imgInfo = docTypeview.findViewById(R.id.infoId);
        btnBack = docTypeview.findViewById(R.id.btn_pro_back);
        btnClose = docTypeview.findViewById(R.id.btn_pro_close);
        btnSelect = docTypeview.findViewById(R.id.btnSelect);
        docSpinner = docTypeview.findViewById(R.id.docSpinner);
        docSpinner.setVisibility(View.VISIBLE);

        arryDoc = new ArrayList<>();
        databaseClient = DatabaseClient.getInstance(context);
        initIJsonListener();
        new SelectData().execute();
        new GeInfo().execute();
        arrySpin = new ArrayList<>();

            arrySpin.add("Driver's License");
            arrySpin.add("Passport");
            arrySpin.add("Identity Card");
            arrySpin.add("Visa");

        new SelectIDType().execute();
    }

    private void initializeNetverifySDK() {
        CustomDialog.showProgressDialog(getActivity());
        try {
//            if (!NetverifySDK.isSupportedPlatform(getActivity()))
                //Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
//            if (NetverifySDK.isRooted(getActivity()))
                //Log.w(TAG, "Device is rooted");

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
                           // netverifySDK.start();
                             startActivityForResult(netverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
                            CustomDialog.cancelProgressDialog();
                        }
                    } catch (MissingPermissionException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        // view.setEnabled(true);
                    }
                }
                @Override
                public void onNetverifyInitiateError(String errorCode, String errorMessage, boolean retryPossible) {
                }
            });

        } catch (PlatformNotSupportedException | NullPointerException e) {
            //Log.e(TAG, "Error in initialize Net verify SDK: ", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            netverifySDK = null;
        }
    }
    class InsertJumioScanDetails   extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            int count = databaseClient.getAppDatabase().scanDetailsDao().getCount();
            if (count == 0) {
                databaseClient.getAppDatabase().scanDetailsDao().insert(jumioScanDetails);
            } else {
                databaseClient.getAppDatabase().scanDetailsDao().update(jumioScanDetails.getScanRef()
                        , jumioScanDetails.getFirstName(), jumioScanDetails.getLastName(),
                        jumioScanDetails.getDocType().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);


        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER,fragment);
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
            //Log.e("TAGGGGGGG", scanRef);
            checkTransactions();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (data == null)
                return;

            scanReference = data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);

            if (resultCode == Activity.RESULT_OK) {
                //Handle the success case and retrieve data
                NetverifyDocumentData documentData = (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                //NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;

                //Toast.makeText(getActivity(), documentData.getFirstName() + "  " + documentData.getLastName(), Toast.LENGTH_LONG).show();
                jumioScanDetails = new JumioScanDetails(scanReference,documentData.getFirstName(),
                        documentData.getLastName(),documentData.getSelectedDocumentType().toString());
              //  new InsertJumioScanDetails().execute();
                docSpinner.setVisibility(View.INVISIBLE);
                loadFragment(new Notarize_ProcessFragment(selectedType,scanReference));
                //lahar


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

    class InsertIDType extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            identityType = new NotaryApp_IdentityType(selectedIdType, false, 0);
            databaseClient.getAppDatabase().NotaryAppSelectIDDao().insert(identityType);
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

            List<String> selectedArry = databaseClient.getAppDatabase().NotaryAppSelectIDDao().getAllSelectedIds();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, arrySpin);
                docSpinner.setAdapter(dataAdapter);
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
                    docSpinner.setAdapter(dataAdapter);
                } else {
                    arrySpin.clear();
                    arrySpin.add(selectedType);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, arrySpin);
                    docSpinner.setAdapter(dataAdapter);
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
    private void checkTransactions(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email",savedEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.CHK_TRAN_COUNT,"chkTrans");
        }catch (Exception e){
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

                            try {
                                daysLeft = (int) Utils.dateDiff(data.getString("current_date")
                                        ,data.getString("ending_at"));
                            } catch (Exception e) {
                                daysLeft = 0;
                            }
                            //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();

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
}
