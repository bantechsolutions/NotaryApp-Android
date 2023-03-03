package com.notaryapp.ui.fragments.validate_id;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.components.GifImage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.ui.activities.ValidateActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.JumioAPIRequest;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Validate_ProcessFragment extends Fragment {

    private Button btnAbort, btnClose;
    private View processView;
    private LinearLayout layout;

    private ConstraintLayout footerView;

    private TextView mainHead;
    private String selectedType, savedEmail, url, statusUrl;
    private IJsonListener iJsonListener;
    private String onfidoReponse, errorMess;
    private DatabaseClient databaseClient;
    private String scanRef;
    private String documentStatus, transactionStatus, verificationStatus="", docExpiryStatus;
    private String firstName, lastName;
    private GifImage gifImageView;

    private int seconds = 0;
    private boolean runningPositive = true;
    private boolean runningNegative;
    private boolean waitTimeExpired = false;
    private boolean showDialogue = false;
    private int balanceTransCount;
    private Runnable runnable;
    private JumioScanDetails jumioScanDetails;
    private boolean Validate_VerifyOkFragment = false;

    private boolean wasRunning;

    private boolean isEnterOkScreen=true;

    public Validate_ProcessFragment() {

    }

    public Validate_ProcessFragment(String selectedType, String scanRef) {
        this.selectedType = selectedType;
        this.scanRef = scanRef;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        processView = inflater.inflate(R.layout.fragment_validate_process, container, false);

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

        init();

        showDialogue = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("showDialogue", false);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!showDialogue) {
                    /*proceedDialog("If you cancel now, your account will be charged for the verification attempt." +
                            " If you wait the full 5 minutes and Notary-App™ is still unable to complete the verification," +
                            " then your account will not be charged.");*/
                    proceedDialog("Are you sure you would like to cancel this verification?");


                } else {
                    updateTransactions();
                }
            }
        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runTimer();
            }
        });


        return processView;
    }

    @Override
    public void onStart() {
        super.onStart();
        gifImageView = processView.findViewById(R.id.GifImageView);
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


    private void runTimer() {
        // Get the text view.
        final TextView timeView
                = processView.findViewById(R.id.textTimer);
        mainHead = processView.findViewById(R.id.mainHead);
        final Handler handler = new Handler();


        if (!runningPositive && !runningNegative) {

            if(runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
        else{
            runnable = (new Runnable() {
                @Override
                public void run() {
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

                    if (minutes == 0 && secs == 0) {
                        if (runningPositive) {
//                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Please be patient as we verify your identity.");
                        } else {
                            footerView.setVisibility(View.VISIBLE);
                            waitTimeExpired = true;
                            runningNegative = false;
                            loadFragment(new Validate_VerifiFailedRetryFragment(null, null, null, null, ""),"");
                            return;
                        }
                    }
                    if (runningNegative) {
                        if (minutes == 0 && secs == 5) {
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
                    if (minutes == 0 && secs == 30) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("Processing this request.");
                        } else {
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
                    if (minutes == 1 && secs == 0) {
                        if (runningPositive) {
                            getJumioResponse();
                            footerView.setVisibility(View.GONE);
                            mainHead.setText("This ID has gone to manual review. Please hold.");
                        } else {
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
                    if (minutes == 1 && secs == 30) {
                        if (runningPositive) {
                            getJumioResponse();
                            mainHead.setText("Manual review may take an additional few minutes.");
                            footerView.setVisibility(View.GONE);
                        } else {
                            mainHead.setText("Still in manual review … up to 90 more seconds.");
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (minutes == 2 && secs == 0) {
                        //getJumioResponse();
                        if (runningPositive) {
                            mainHead.setText("We recommend you wait for a response, but you may cancel this verification at this time.");
                            footerView.setVisibility(View.VISIBLE);
                        } else {
                            mainHead.setText("On rare occasions, this process may take longer than normal. Is there another task you can perform while you are waiting?");
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (minutes == 2 && secs == 29) {
                        mainHead.setText("Sorry for this delay. Be assured that our experts are attempting to verify this ID.");

                        //getJumioResponse();
                        if (runningPositive) {
                            runningPositive = false;
                            runningNegative = true;
                            footerView.setVisibility(View.VISIBLE);
                        } else {
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }

                    if (runningPositive) {
                        seconds++;
                    } else if (runningNegative) {
                        seconds--;
                    }

                    // Post the code again
                    // with a delay of 1 second.
                    handler.postDelayed(this, 1000);
                }
            });

            handler.post(runnable);
        }
    }

    private void proceedDialog(String errMess) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_jumio);


        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(errMess);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnNo);
        dialogButton.setText("CANCEL");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CheckBox checkBox = dialog.findViewById(R.id.check);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putBoolean("showDialogue", checkBox.isChecked()).commit();

            }
        });

        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("OK");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DeleteImages().execute();
                updateTransactions();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void init() {
        btnAbort = processView.findViewById(R.id.btnAbort);
        btnAbort.setText("CANCEL");
        footerView = processView.findViewById(R.id.footerView);
        btnClose = processView.findViewById(R.id.btn_be_close);
        gifImageView = processView.findViewById(R.id.GifImageView);
        //  layout = processView.findViewById(R.id.linear);
        //layout.setVisibility(View.GONE);
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectEmail().execute();
    }

    private void loadFragment(Fragment fragment, String TAG) {

        if(TAG.equals("Validate_VerifyOkFragment")){
            if(Validate_VerifyOkFragment) {
                Validate_VerifyOkFragment = false;
                if (getActivity() != null) {
                    runningPositive = false;
                    runningNegative = false;
                    runTimer(); // stopRunnable
                    FragmentViewUtil.replaceFragment(getActivity(), ValidateActivity.REF_VIEW_CONTAINER, fragment, false);
                }
            }
        }
        else {
            if (getActivity() != null) {
                runningPositive = false;
                runningNegative = false;
                runTimer(); // stopRunnable
                FragmentViewUtil.replaceFragment(getActivity(), ValidateActivity.REF_VIEW_CONTAINER, fragment, false);
            }
        }
    }

    private void getJumioResponse() {

        JumioAPIRequest jumioapiRequest;

        try {
            jumioapiRequest = new JumioAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", savedEmail);
                params.put("scanRef", scanRef);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if (getActivity() != null) {
                jumioapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_JUMIO_SCANDETAILS, "Jumio");
            }

            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void updateTransactions() {
        //Implementing interfaces of FetchDataListener for POST api request
        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("Transactions")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");

                            balanceTransCount = total_bought - total_used;

                            new UpdateMemPlans().execute();
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

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("email", savedEmail);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.UPDATE_TRANSACTIONS, "Transactions");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {
                        if (type.equals("Jumio")) {

                            try {
                                JSONObject document = new JSONObject(responseData.toString()).getJSONObject("document");
                                JSONObject transaction = new JSONObject(responseData.toString()).getJSONObject("transaction");
                                //JSONObject verification = new JSONObject(responseData.toString()).getJSONObject("verification");
                                String docType = document.getString("type");

                                transactionStatus = transaction.getString("status");
                                if (transactionStatus == null) {
                                    transactionStatus = "";
                                }
                                documentStatus = document.getString("status");// "status": "APPROVED_VERIFIED"
                                if (documentStatus == null) {
                                    documentStatus = "";
                                }

                                if (documentStatus.equals("APPROVED_VERIFIED")) {
                                    if (docType.equals("DRIVING_LICENSE") || (docType.equals("PASSPORT"))) {
                                        Date today = new Date();
                                        Date expiryDate = new Date();
                                        String docExpiry = document.getString("expiry");

                                        /*try {
                                            java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                            expiryDate = formatter.parse(docExpiry);
                                            formatter.format(today);
                                            formatter.format(expiryDate);


                                            if (today.compareTo(expiryDate) > 0) {
                                                docExpiryStatus = "EXPIRED";
                                            } else {
                                                docExpiryStatus = "NOT EXPIRED";
                                            }
                                        } catch (Exception e) {
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
                                    } else {
                                        docExpiryStatus = "NOT EXPIRED";
                                    }

                                    firstName = document.getString("firstName");
                                    lastName = document.getString("lastName");
                                    jumioScanDetails = new JumioScanDetails(scanRef, firstName,
                                            lastName, docType);
                                    new InsertJumioScanDetails().execute();

                                    transactionStatus = transaction.getString("status");

                                    //JSONObject identityVerification = new JSONObject(verification.toString()).getJSONObject("identityVerification");
                                    /*verificationStatus = identityVerification.getString("similarity");
                                    if (verificationStatus == null) {
                                        verificationStatus = "";
                                    }*/

                                    /*if (transactionStatus.equals("DONE") && verificationStatus.equals("MATCH")
                                            && docExpiryStatus.equals("NOT EXPIRED")) {*/
                                    // Sourav 20201127
                                    if (transactionStatus.equals("DONE") && docExpiryStatus.equals("NOT EXPIRED"))
                                    {
                                        Validate_VerifyOkFragment = true;
                                        if(isEnterOkScreen) {
                                            isEnterOkScreen = false;
                                            loadFragment(new Validate_VerifyOkFragment(documentStatus, transactionStatus, firstName, lastName), "Validate_VerifyOkFragment");
                                            //   getActivity().onBackPressed();
                                        }
                                        //loadFragment(new Validate_VerifyOkFragment(documentStatus, transactionStatus, firstName, lastName), "Validate_VerifyOkFragment");
                                    } else {
                                        String retry = "NO";
                                        if (selectedType.equals("")) {
                                            retry = "NO";
                                        } else {
                                            retry = "YES";
                                        }
                                        /*runningPositive = false;
                                        runningNegative = false;
                                        runTimer();*/
                                        loadFragment(new Validate_VerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, retry), "");


                                    }

                                } else {
                                   /* runningPositive = false;
                                    runningNegative = false;
                                    runTimer();*/
                                    loadFragment(new Validate_VerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, ""),"");

                                }

                            } catch (Exception e) {
                                //e.printStackTrace();
                            }


                        } else {
                            String response = responseData.toString();
                            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //     CustomDialog.notaryappDialogSingle(getActivity(),"Jumio processing your identity ");
                    //  getActivity().finish();
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        /////// do sometinh if no responce from jumio
                        if(waitTimeExpired && !runningPositive && !runningNegative) {
                            runningPositive = false;
                            runningNegative = false;
                            runTimer(); // stop runnable
                            loadFragment(new Validate_VerifiFailedRetryFragment(documentStatus, transactionStatus,
                                    verificationStatus, docExpiryStatus, "YES"),"");
                        }
                        else{
                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if(waitTimeExpired && !runningPositive && !runningNegative) {
                                        runningPositive = false;
                                        runningNegative = false;
                                        runTimer(); // stop runnable
                                        loadFragment(new Validate_VerifiFailedRetryFragment(documentStatus, transactionStatus,
                                                verificationStatus, docExpiryStatus, "YES"),"");

                                    }
                                    else{
                                        getJumioResponse();

                                    }

                                }
                            },10000);
                        }


                    }
                });

            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
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

            loadFragment(new Validate_SelectIdFragment(),"");
        }

    }


    class InsertJumioScanDetails extends AsyncTask<String, Void, String> {

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

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            //  scanRef = databaseClient.getAppDatabase().scanDetailsDao().getScanRef().trim();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }
}