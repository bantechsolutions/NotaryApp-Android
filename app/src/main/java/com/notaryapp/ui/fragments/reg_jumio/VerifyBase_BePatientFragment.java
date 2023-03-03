
package com.notaryapp.ui.fragments.reg_jumio;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notaryapp.R;
import com.notaryapp.components.GifImage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.ui.activities.onboarding.SelectIdentityActivity;
import com.notaryapp.ui.activities.onboarding.VerifyBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.JumioAPIRequest;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class VerifyBase_BePatientFragment extends Fragment {

   // public static final int REF_VIEW_CONTAINER = R.id.identityRoot;
    private final String TAG="BePatientFragment";
    private View bePatientView;
    private FragmentTransaction fragmentTransaction;
    private int selectedId;

    private TextView mainHead;
    private ConstraintLayout footerView;

    private String scanRef,savedEmail;
    private IJsonListener iJsonListener;
    private DatabaseClient databaseClient;
    private boolean selectIDCompleted=false;
    private Button btnClose,btnBack,btnAbort;
    private String documentStatus,transactionStatus,verificationStatus ="",docExpiryStatus;
   // private Bundle bundle = new Bundle();
    private String firstName,lastName;
    private GifImage gifImageView;

    private int seconds = 0;
    private boolean runningPositive = true;
    private boolean runningNegative;
    private boolean waitTimeExpired = false;
    private Runnable runnable;
    private JumioScanDetails jumioScanDetails;

    private boolean wasRunning;

    public VerifyBase_BePatientFragment(String scanReff) {
        this.scanRef = scanReff;
    }

    public VerifyBase_BePatientFragment() {
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bePatientView = inflater.inflate(R.layout.fragment_be_patient, container, false);
        init();

        runningPositive = true;
        runningNegative = false;
        if (savedInstanceState != null) {

            seconds
                    = savedInstanceState
                    .getInt("seconds");
            runningPositive
                    = savedInstanceState
                    .getBoolean("runningPositive");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");
        }


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*proceedDialog("If you cancel now, verification process will be terminated." +
                        " Please wait the full 5 minutes for a response or You can try again to verify the same ID or" +
                        " Choose Another ID.");*/
                proceedDialog("Are you sure you would like to cancel this verification?");

            }
        });

        runTimer();

        return bePatientView;

    }


    @Override
    public void onStart() {
        super.onStart();

        gifImageView = bePatientView.findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.process_tick);

    }

    // Save the state of the stopwatch
    // if it's about to be destroyed.
    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("runningPositive", runningPositive);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
    }

    // If the activity is paused,
    // stop the stopwatch.
    @Override
    public void onPause() {
        super.onPause();
        wasRunning = runningPositive;
        runningPositive = false;
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    @Override
    public void onResume() {
        super.onResume();
        if (wasRunning) {
            runningPositive = true;
        }
    }


    private void proceedDialog(String errMess) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText(errMess);
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
                startActivity(new Intent(getActivity(), SelectIdentityActivity.class));
            }
        });
        dialog.show();
    }

    private void runTimer()
    {
        // Get the text view.
        final TextView timeView
                = bePatientView.findViewById(R.id.textTimer);
        mainHead = bePatientView.findViewById(R.id.mainHead);
        final Handler handler = new Handler();

        if (!runningPositive && !runningNegative) {

            if(runnable != null) {
                handler.removeCallbacks(runnable);
            }

        }
        else{

            runnable = (new Runnable() {
                @Override
                public void run()
                {
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int secs = seconds % 60;
                    // Format the seconds into hours, minutes,
                    // and seconds.
                    String time
                            = String
                            .format(Locale.getDefault(),
                                    "%02d:%02d",
                                    minutes, secs);
                    // Set the text view text.
                    timeView.setText(time);

                    if(minutes == 0 && secs == 0 ){
                        if(runningPositive){
//                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Please be patient as we verify your identity.");
                        }
                        else{

                            footerView.setVisibility(View.VISIBLE);
                            waitTimeExpired = true;
                            runningNegative = false;
                            runningPositive = false;
                            loadFragment(new VerifyBase_VerifiFailedRetryFragment(null, null, null, null));
                            return;
                        }
                    }
                    if(runningNegative){
                        if(minutes == 0 && secs == 5 ){
                            getJumioResponse();
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }

                    if (minutes == 0 && secs == 10) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        }
                    }
                    if (minutes == 0 && secs == 20) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        }
                    }

                    if(minutes == 0 && secs == 30 ){
                        if(runningPositive){
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        }
                        else{
                            footerView.setVisibility(View.VISIBLE);
                            mainHead.setText("30 more seconds.");
                        }
                    }
                    if (minutes == 0 && secs == 40) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        }
                    }
                    if (minutes == 0 && secs == 50) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        }
                    }
                    if(minutes == 1 && secs == 0 ){
                        if(runningPositive){
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("This ID has gone to manual review. Please hold.");
                        }
                        else{
                            footerView.setVisibility(View.VISIBLE);
                            mainHead.setText("Still in manual review …up to 60 more seconds.");
                        }
                    }
                    if (minutes == 1 && secs == 10) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("This ID has gone to manual review. Please hold.");
                        }
                    }
                    if (minutes == 1 && secs == 20) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("This ID has gone to manual review. Please hold.");
                        }
                    }
                    if(minutes == 1 && secs == 30 ){
                        if(runningPositive){
                            getJumioResponse();
                            mainHead.setText("Manual review may take an additional few minutes.");
                            footerView.setVisibility(View.GONE);
                        }
                        else{
                            mainHead.setText("Still in manual review … up to 90 more seconds.");
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    if(minutes == 2 && secs == 0 ){
                        //getJumioResponse();
                        if(runningPositive){
                            mainHead.setText("We recommend you wait for a response, but you may cancel this verification at this time.");
                            footerView.setVisibility(View.VISIBLE);
                        }
                        else{
                            mainHead.setText("On rare occasions, this process may take longer than normal. Is there another task you can perform while you are waiting?");
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    if(minutes == 2 && secs == 29 ){
                        mainHead.setText("Sorry for this delay. Be assured that our experts are attempting to verify this ID.");

                        //getJumioResponse();
                        if(runningPositive){
                            runningPositive = false;
                            runningNegative = true;
                            footerView.setVisibility(View.VISIBLE);
                        }
                        else{
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    // Post the code again
                    // with a delay of 1 second.

                    if (runningPositive) {
                        seconds++;
                    } else if (runningNegative) {
                        seconds--;
                    }

                    handler.postDelayed(this, 1000);
                }
            });

            handler.post(runnable);
        }
    }

    private void init() {

        btnAbort = bePatientView.findViewById(R.id.btnAbort);
        btnAbort.setText("CANCEL");
        footerView = bePatientView.findViewById(R.id.footerView);

        btnBack = bePatientView.findViewById(R.id.btn_be_back);
        btnClose = bePatientView.findViewById(R.id.btn_be_close);
        databaseClient =  DatabaseClient.getInstance(getActivity());
        new SelectEmail().execute();
        initIJsonListener();
    }
    private void loadFragment(Fragment fragment) {

        runningPositive = false;
        runningNegative = false;
        FragmentViewUtil.replaceFragment(getActivity(),
                VerifyBaseActivity.REF_VIEW_CONTAINER,
                fragment,false);
        runTimer(); // stopRunnable

    }

    private void idCheckCall(){

        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("userName",savedEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params,AppUrl.ID_CHECK,"idCheck");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void getJumioResponse(){
      //  CustomDialog.showProgressDialog(getActivity());
    //   String url = AppUrl.JUMIO_BASE_URL+scanRef+"/data";
        try{
            JumioAPIRequest postapiRequest=new JumioAPIRequest();
            JSONObject params=new JSONObject();
            try {
               params.put("userName",savedEmail);
                params.put("scanRef",scanRef);


            }catch (Exception e){
                //e.printStackTrace();
            }

            if(getActivity()!=null) {
                postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_JUMIO_SCANDETAILS, "Jumio");
            }
        }catch (Exception e){
            //Log.e("Errorrrrrrrrr",e.getMessage());
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData,String type ) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {
                        if(type.equals("Jumio")) {

                            try {
                                JSONObject document = new JSONObject(responseData.toString()).getJSONObject("document");
                                JSONObject transaction = new JSONObject(responseData.toString()).getJSONObject("transaction");
                                //JSONObject verification = new JSONObject(responseData.toString()).getJSONObject("verification");
                                String docType = document.getString("type");
                                //JSONObject identityVerification = new JSONObject(verification.toString()).getJSONObject("identityVerification");
                                transactionStatus = transaction.getString("status");
                                if(transactionStatus == null) {
                                    transactionStatus = "";
                                }
                                documentStatus = document.getString("status");// "status": "APPROVED_VERIFIED"
                                if(documentStatus == null){
                                    documentStatus = "";
                                }

                                if(documentStatus.equals("APPROVED_VERIFIED")) {
                                if(docType.equals("DRIVING_LICENSE") || (docType.equals("PASSPORT"))) {
                                    Date today = new Date();
                                    //Date expiryDate;
                                    Date expiryDate = new Date();
                                    String docExpiry = document.getString("expiry");

                                    /*try {
                                        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        expiryDate = formatter.parse(docExpiry);
                                        formatter.format(today);
                                        if (expiryDate != null) {
                                            formatter.format(expiryDate);

                                            if (today.compareTo(expiryDate) > 0) {
                                                docExpiryStatus = "EXPIRED";
                                            } else {
                                                docExpiryStatus = "NOT EXPIRED";
                                            }
                                        } else {
                                            docExpiryStatus = "EXPIRED";
                                        }
                                    }
                                    catch (Exception e){
                                        docExpiryStatus = "EXPIRED";
                                    }*/
                                    try {
                                        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        expiryDate = formatter.parse(docExpiry);
                                        formatter.format(today);
                                        formatter.format(expiryDate);
                                    }
                                    catch (Exception e){

                                    }

                                    if (today.compareTo(expiryDate) > 0) {
                                        docExpiryStatus = "EXPIRED";
                                    } else {
                                        docExpiryStatus = "NOT EXPIRED";
                                    }
                                }else{
                                    docExpiryStatus = "NOT EXPIRED";
                                }
                                    firstName = document.getString("firstName");
                                    lastName = document.getString("lastName");
                                    jumioScanDetails = new JumioScanDetails(scanRef,firstName,lastName,docType);
                                    new InsertJumioScanDetails().execute();

                                    transactionStatus = transaction.getString("status");//"status": "DONE",
                                    /*if(transactionStatus == null){
                                        transactionStatus = "";
                                    }*/
                                    //Sourav 10-12-2020
                                    /*verificationStatus = identityVerification.getString("similarity");//"status": "DONE",
                                    if(verificationStatus == null){
                                        verificationStatus = "";
                                    }*/

                                        /*if (transactionStatus.equals("DONE") && verificationStatus.equals("MATCH")
                                                && docExpiryStatus.equals("NOT EXPIRED")) {*/
                                    if (transactionStatus.equals("DONE") && docExpiryStatus.equals("NOT EXPIRED")) {
                                            idCheckCall();
                                            // loadFragment(new Validate_VerifyOkFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus));
                                    } else {
                                            loadFragment(new VerifyBase_VerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus));
                                    }

                               }else{
                                    loadFragment(new VerifyBase_VerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus));
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }else if(type.equals("idCheck")) {
                            if (responseData.has("success")) {
                                String success = responseData.getString("success");
                                if (success.equals("1")) {
                                    loadFragment(new VerifyBase_VerifyOkFragment(documentStatus,
                                            transactionStatus,
                                            verificationStatus,
                                            docExpiryStatus,
                                            firstName,
                                            lastName,
                                            scanRef));
                                }
                            }
                            else{

                                loadFragment(new VerifyBase_VerifiFailedRetryFragment( "Internal Server Error, Please try again", "", "", ""));

                            }
                        }
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                  //  CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    //  getActivity().finish();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                if(waitTimeExpired && !runningPositive && !runningNegative) {
                    runningPositive = false;
                    runningNegative = false;
                    runTimer(); // stopRunnable
                    loadFragment(new VerifyBase_VerifiFailedRetryFragment("Internal Server Error, Please try again", "", "", ""));
                }
                else {
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (waitTimeExpired && !runningPositive && !runningNegative) {
                                runningPositive = false;
                                runningNegative = false;
                                runTimer(); // stop runnable

                                loadFragment(new VerifyBase_VerifiFailedRetryFragment("Internal Server Error, Please try again", "", "", ""));

                            } else {
                                getJumioResponse();

                            }

                        }
                    }, 10000);
                }
        }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
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

            // loadFragment(new VerifyBase_BePatientFragment());
        }
    }
    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            savedEmail =  DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .userRegDao()
                    .getEmail();
         //   scanRef = databaseClient.getAppDatabase().scanDetailsDao().getScanRef();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

}
