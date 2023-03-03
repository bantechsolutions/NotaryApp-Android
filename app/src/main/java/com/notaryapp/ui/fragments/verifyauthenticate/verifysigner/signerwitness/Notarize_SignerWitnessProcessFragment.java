package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness;

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
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.components.GifImage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.ui.activities.selfie.crediblewitness.CredibleWitnessAddSelfieActivity;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notarize_SignerWitnessProcessFragment extends Fragment {

    private Button btnAbort;
    private View processView;

    private ConstraintLayout footerView;
    private TextView mainHead;

    private String selectedType, savedEmail, url, statusUrl;
    private IJsonListener iJsonListener;
    private String onfidoReponse, errorMess;
    private DatabaseClient databaseClient;
    private String scanRef;
    private String documentStatus = "", transactionStatus = "", verificationStatus = "", docExpiryStatus = "";
    private String firstName, lastName;
    private GifImage gifImageView;

    private int seconds = 0;
    private boolean runningPositive = true;
    private boolean runningNegative;
    private boolean waitTimeExpired = false;
    private boolean showDialogue = false;
    private int balanceTransCount, pending;
    private Runnable runnable;
    private JumioScanDetails jumioScanDetails;
    private boolean Notarize_SignerWitnessVerifyOkFragment0 = false;
    private boolean Notarize_SignerWitnessVerifyOkFragment1 = false;

    private boolean wasRunning;

    private String signerEmail = "";

    public Notarize_SignerWitnessProcessFragment(String selectedType, String scanRef) {
        this.selectedType = selectedType;
        this.scanRef = scanRef;
    }

    public Notarize_SignerWitnessProcessFragment(String selectedType, String scanRef, int pending) {
        this.selectedType = selectedType;
        this.scanRef = scanRef;
        this.pending = pending;
    }
    public Notarize_SignerWitnessProcessFragment(String selectedType, String scanRef, int pending, String signerEmail) {
        this.selectedType = selectedType;
        this.scanRef = scanRef;
        this.pending = pending;
        this.signerEmail = signerEmail;
    }

    public Notarize_SignerWitnessProcessFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        processView = inflater.inflate(R.layout.fragment_va_process, container, false);
        showDialogue = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean("showDialogue", false);

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


        btnAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!showDialogue) {
                    /*proceedDialog("If you cancel now, your account will be charged for the " +
                            "verification attempt. If you wait the full 5 minutes and Notary-App™ is " +
                            "still unable to complete the verification, then your account will not be charged.");*/
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

    private void proceedDialog(String errMess) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
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
        //  gifImageView = processView.findViewById(R.id.GifImageView);
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        // gifImageView.setGifImageResource(R.drawable.process_tick);
        new SelectEmail().execute();

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

            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        } else {
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
                            runningPositive = false;
                            loadFragment(new Notarize_SignerWitnessVerifiFailedRetryFragment(null,
                                    null, null, null, "", signerEmail), "");
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

    private void loadFragment(Fragment fragment, String TAG) {

        if (TAG.equals("Notarize_SignerWitnessVerifyOkFragment0")) {
            if (Notarize_SignerWitnessVerifyOkFragment0) {
                Notarize_SignerWitnessVerifyOkFragment0 = false;
                if (getActivity() != null) {

                    runningPositive = false;
                    runningNegative = false;
                    runTimer(); // stopRunnable
                    FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, false);

                }
            } else if (!Notarize_SignerWitnessVerifyOkFragment1) {
                Notarize_SignerWitnessVerifyOkFragment1 = false;
                if (getActivity() != null) {

                    runningPositive = false;
                    runningNegative = false;
                    runTimer(); // stopRunnable
                    FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, false);

                }
            }
        } else {
            if (getActivity() != null) {

                runningPositive = false;
                runningNegative = false;
                runTimer(); // stopRunnable
                FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, false);

            }
        }

    }

    private void getJumioResponse() {

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userName", savedEmail);
                params.put("scanRef", scanRef);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            if (getActivity() != null) {
                postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_JUMIO_SCANDETAILS, "Jumio");
            }
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
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

                                            if (expiryDate != null) {
                                                formatter.format(expiryDate);

                                                if (today.compareTo(expiryDate) > 0) {
                                                    docExpiryStatus = "Expired";
                                                } else {
                                                    docExpiryStatus = "Not Expired";
                                                }
                                            } else {
                                                docExpiryStatus = "Expired";
                                            }
                                        } catch (Exception e) {
                                            docExpiryStatus = "Expired";
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

                                    jumioScanDetails = new JumioScanDetails(scanRef, firstName, lastName, docType);
                                    new InsertJumioScanDetails().execute();

                                    transactionStatus = transaction.getString("status");
                                    //JSONObject identityVerification = new JSONObject(verification.toString()).getJSONObject("identityVerification");
                                    //verificationStatus = identityVerification.getString("similarity");
                                    //if (verificationStatus == null) {
                                    //  verificationStatus = "";
                                    //}

                                    //if (transactionStatus.equals("DONE") && docExpiryStatus.equals("Not Expired") && verificationStatus.equals("MATCH"))
                                    if (transactionStatus.equalsIgnoreCase("DONE") && docExpiryStatus.equalsIgnoreCase("NOT EXPIRED"))
                                    {
                                        if (pending == 1) {
                                            Notarize_SignerWitnessVerifyOkFragment0 = false;
                                            Notarize_SignerWitnessVerifyOkFragment1 = true;
                                            //Toast.makeText(getActivity(),"111",Toast.LENGTH_LONG).show();
                                            if (((VerifySignerActivity)getActivity()).isOpenOne) {
                                                ((VerifySignerActivity)getActivity()).isOpenOne = false;
                                                loadFragment(new Notarize_SignerWitnessVerifyOkFragment(firstName, lastName, scanRef, 1, signerEmail), "Notarize_SignerWitnessVerifyOkFragment0");
                                            } else{


                                                if(getActivity() instanceof VerifySignerActivity ){

                                                    Notarize_SignerWitnessVerifyOkFragment0 = true;
                                                    Notarize_SignerWitnessVerifyOkFragment1 = false;

                                                    if (((VerifySignerActivity)getActivity()).isOpenTwo) {
                                                        ((VerifySignerActivity)getActivity()).isOpenTwo = false;
                                                        loadFragment(new Notarize_SignerWitnessVerifyOkFragment(firstName, lastName, scanRef, signerEmail), "Notarize_SignerWitnessVerifyOkFragment0");
                                                    }
                                                }else if(getActivity() instanceof CredibleWitnessAddSelfieActivity){

                                                    Notarize_SignerWitnessVerifyOkFragment0 = true;
                                                    Notarize_SignerWitnessVerifyOkFragment1 = false;
                                                    if (((CredibleWitnessAddSelfieActivity)getActivity()).isOpenTwo) {
                                                        ((CredibleWitnessAddSelfieActivity)getActivity()).isOpenTwo = false;
                                                        loadFragment(new Notarize_SignerWitnessVerifyOkFragment(firstName, lastName, scanRef, signerEmail), "Notarize_SignerWitnessVerifyOkFragment0");
                                                    }

                                                }


                                            }
                                        } else {
                                            Notarize_SignerWitnessVerifyOkFragment0 = true;
                                            Notarize_SignerWitnessVerifyOkFragment1 = false;
                                            //Toast.makeText(getActivity(),"3333",Toast.LENGTH_LONG).show();
                                            if(getActivity() instanceof VerifySignerActivity ){
                                                if (((VerifySignerActivity)getActivity()).isOpenTwo) {
                                                    ((VerifySignerActivity)getActivity()).isOpenTwo = false;
                                                    loadFragment(new Notarize_SignerWitnessVerifyOkFragment(firstName, lastName, scanRef, signerEmail), "Notarize_SignerWitnessVerifyOkFragment0");
                                                }
                                            }else if(getActivity() instanceof CredibleWitnessAddSelfieActivity){
                                                if (((CredibleWitnessAddSelfieActivity)getActivity()).isOpenTwo) {
                                                    ((CredibleWitnessAddSelfieActivity)getActivity()).isOpenTwo = false;
                                                    loadFragment(new Notarize_SignerWitnessVerifyOkFragment(firstName, lastName, scanRef, signerEmail), "Notarize_SignerWitnessVerifyOkFragment0");
                                                }
                                            }

                                        }
                                        //  loadFragment(new Notarize_SignerWitnessVerifyOkFragment(documentStatus,transactionStatus,verificationStatus,docExpiryStatus));
                                    } else {
                                        String retry = "NO";
                                        if (selectedType.equals("")) {
                                            retry = "NO";
                                        } else {
                                            retry = "YES";
                                        }
                                        loadFragment(new Notarize_SignerWitnessVerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, retry, signerEmail), "");
                                    }
//
                                } else {
                                    loadFragment(new Notarize_SignerWitnessVerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, "", signerEmail), "");

                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {

                if (waitTimeExpired && !runningPositive && !runningNegative) {
                    runningNegative = false;
                    runningPositive = false;
                    runTimer(); // stop runnable
                    loadFragment(new Notarize_SignerWitnessVerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, "YES"), "");
                } else {
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (waitTimeExpired && !runningPositive && !runningNegative) {
                                runningPositive = false;
                                runningNegative = false;
                                runTimer(); // stop runnable

                                loadFragment(new Notarize_SignerWitnessVerifiFailedRetryFragment(documentStatus, transactionStatus, verificationStatus, docExpiryStatus, "YES", signerEmail), "");

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
            //  startActivity(new Intent(getActivity(), VerifyBaseActivity.class));
            //   loadFragment(new Notarize_SignerWitnessProcessFragment(selectedType));
        }
    }

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            //  scanRef = databaseClient.getAppDatabase().scanDetailsDao().getScanRef();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            getJumioResponse();
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

            loadFragment(new Notarize_WitnessDocTypeFragment(1, signerEmail), "");
        }

    }
}