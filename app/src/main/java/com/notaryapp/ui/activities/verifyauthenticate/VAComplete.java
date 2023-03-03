package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.notaryappShareAppActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VAComplete extends BaseActivity {

    private Button btnSubmit, btnShare;
    private String transactionId;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private Transactions transactions;
    private SignerReg signerReg;
    private WitnessReg witnessReg;
    private String savedEmail = "";
    private ArrayList<WitnessReg> wArrayList;
    private ArrayList<SignerReg> sArrayList;
    private ArrayList<DocumentsModel> addDocArrayList;
    private ImageView ivInfoAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ca_complete);

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


        databaseClient = DatabaseClient.getInstance(this);

        btnSubmit = findViewById(R.id.btnLoadDash);
        btnShare = findViewById(R.id.btnShare);

        new SelectData().execute();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getShareDetails();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VAComplete.this, notaryappShareAppActivity.class);
                //Intent intent = new Intent(VAComplete.this, DashBoardActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {

                    addDocArrayList = new ArrayList<DocumentsModel>();
                    sArrayList = new ArrayList<SignerReg>();
                    wArrayList = new ArrayList<WitnessReg>();

                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("share")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.getString("id");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();
                            if(signer_obj.has("signer")) {
                                Object signersItem = signer_obj.get("signer");
                                if (!signersItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray signerArray = (JSONArray) signersItem;
                                    // do all kinds of JSONArray'ish things with urlArray

                                    for (int i = 0; i < signerArray.length(); i++) {
                                        JSONObject signer = signerArray.getJSONObject(i);

                                        fName = signer.getString("first_name");
                                        lName = signer.getString("last_name");
                                        email = signer.getString("signer");
//                                        phone = signer.getString("phone");

                                        String formatPhone = signer.getString("phone");
                                        int phoneLength = signer.getString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")){
                                            if(formatPhone.substring(0, 3).equals("+91")){
                                                phone = formatPhone.substring(3,phoneLength);
                                            }
                                            if(formatPhone.substring(0, 2).equals("+1")){
                                                phone = formatPhone.substring(2,phoneLength);
                                            }
                                        }else{
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");


                                        scanRef = signer.getString("scan_reference");
                                        profileImgPath = signer.getString("photo");

                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false);
                                        sArrayList.add(signerReg);
                                    }
                                    new SaveSigner().execute();

                                }
                            }

                            if(signer_obj.has("witness")) {
                                Object witnessItem = signer_obj.get("witness");
                                if (!witnessItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray witnessArray = (JSONArray) witnessItem;

                                    for (int i = 0; i < witnessArray.length(); i++) {
                                        JSONObject witness_obj = witnessArray.getJSONObject(i);

                                        fName = witness_obj.getString("first_name");
                                        lName = witness_obj.getString("last_name");
                                        email = witness_obj.getString("witness");
                                        signerEmail = witness_obj.getString("signer");
//                                        phone = witness_obj.getString("phone");

                                        String formatPhone = witness_obj.getString("phone");
                                        int phoneLength = witness_obj.getString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")){
                                            if(formatPhone.substring(0, 3).equals("+91")){
                                                phone = formatPhone.substring(3,phoneLength);
                                            }
                                            if(formatPhone.substring(0, 2).equals("+1")){
                                                phone = formatPhone.substring(2,phoneLength);
                                            }
                                        }else{
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");
                                        scanRef = witness_obj.getString("scan_reference");
                                        witnessReg = new WitnessReg(fName, lName, email, phone, scanRef, signerEmail, witProImgPath);
                                        //  new SaveWitness().execute();
                                        wArrayList.add(witnessReg);
                                    }
                                    new SaveWitness().execute();

                                }
                            }

                            if(signer_obj.has("notaDoc")) {
                                Object notaDocItem = signer_obj.get("notaDoc");
                                if (!notaDocItem.toString().equalsIgnoreCase("null")) {
                                    JSONArray notaDocArray = (JSONArray) notaDocItem;

                                    for (int i = 0; i < notaDocArray.length(); i++) {
                                        JSONObject docsObj = notaDocArray.getJSONObject(i);

                                        if (docsObj.has("docuName")) {
                                            String docuName = docsObj.getString("docuName");
                                            String url = docsObj.getString("url");

                                            docName = docsObj.getString("docname");

                                            addDocArrayList.add(new DocumentsModel(docuName, url, docName));

                                        }
                                    }
                                    new SaveImages().execute();
                                }
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
            }

            @Override
            public void onFetchStart() {

            }
        };

        ivInfoAlert = (ImageView) findViewById(R.id.ivInfoAlert);
        ivInfoAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog.notaryappInfoDialogAlert(VAComplete.this,getResources().getString(R.string.alert_test));
            }
        });
    }



    private void getShareDetails() {
        // CustomDialog.showProgressDialog(context);
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + transactionId;
            getApiRequest.request(this, iJsonListener, url, "share");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    class DeleteAll extends AsyncTask<Void, Void, String> {

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
            databaseClient.getAppDatabase().transactionsDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            //  Intent intent = new Intent(getActivity(), Notarize_AlertActivity.class);
            //   startActivity(intent);
            // getActivity().finish();
        }

    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {

            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();

            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
        }

    }

    class AddTranId extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().transactionsDao().insert(transactions);
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class SaveSigner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < sArrayList.size(); i++) {
                databaseClient.getAppDatabase()
                        .signerRegDao()
                        .insert(sArrayList.get(i));
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

        }
    }

    class SaveWitness extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < wArrayList.size(); i++) {
                databaseClient.getAppDatabase()
                        .witnessRegDao()
                        .insert(wArrayList.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

        }
    }

    class SaveImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            for (int i = 0; i < addDocArrayList.size(); i++) {

                databaseClient.getAppDatabase().documentsImageDao().insert(addDocArrayList.get(i));
            }


            //Saving the image name to database

            //selectedType =databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
            intent.putExtra("transactionId", transactionId);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}



