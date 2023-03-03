package com.notaryapp.ui.activities.verifyauthenticate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.notaryapp.R;
import com.notaryapp.adapter.EmailListAdapter;
import com.notaryapp.adapter.ShareDocsAdapter;
import com.notaryapp.adapter.ShareSignersAdapter;
import com.notaryapp.interfacelisterners.DocumentSelectedListerner;
import com.notaryapp.interfacelisterners.EmailAddedListerner;
import com.notaryapp.interfacelisterners.ShareListerner;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Validation;
import com.notaryapp.utilsretrofit.GetDataService;
import com.notaryapp.utilsretrofit.RetrofitClientInstance;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShareActivity extends BaseActivity {

    private RecyclerView recyclerSigners,recyclerDoc, selectedEmails;
    private List<SignerReg> signerList;
    private List<DocumentsModel> docList;
    private DatabaseClient databaseClient;
    private Button shareBtn, btnClose, addEmail;
    private ArrayList<String> addNewemail;
    private String transId ="";
    private ShareListerner signerInterface;
    private IJsonListener generatePdfListerner;
    private EmailAddedListerner emailAddedListerner;
    private DocumentSelectedListerner documentSelectedListerner;
    List<Boolean> isSelectedSigners = new ArrayList<>();
    List<Boolean> isSelectedDocs = new ArrayList<>();
    List<String> stitchedDoc = new ArrayList<>();

    private TextInputEditText regEdit_Email;
    private TextInputLayout textInputLayoutEmail;
    private String validEmail;
    private boolean isvalidEmail;

    ArrayList<String> signerEmail;
    ArrayList<String> selecteDocList;
    private ArrayList<Integer> stampCountArray;

    StringBuilder stringBuilderSigner;
    StringBuilder stringBuilderDoc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_share_docs);
        init();

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
        addNewemail = new ArrayList<>();
        signerEmail = new ArrayList<>();
        selecteDocList = new ArrayList<>();
        transId = getIntent().getStringExtra("transactionId");
        if(transId == null){
            transId = "";
        }

        signerInterface = (emailId, position, add) -> {

            if(add) {
                isSelectedSigners.set(position, add);
                signerEmail.add(emailId);
            }
            else{
                isSelectedSigners.set(position, add);
                signerEmail.remove(emailId);
            }
            refreshSigner();

        };

        emailAddedListerner = new EmailAddedListerner() {
            @Override
            public void onDeleteEmail(String emailId, int position) {

                addNewemail.remove(emailId);
                updateEmailList();
            }
        };

        documentSelectedListerner = new DocumentSelectedListerner() {
            @Override
            public void onSinerSelected(String doc, int position, Boolean add) {

                if(add) {
                    isSelectedDocs.set(position, add);
                    selecteDocList.add(doc);
                }
                else{
                    isSelectedDocs.set(position, add);
                    selecteDocList.remove(doc);
                }
                refreshDoc();
            }
        };

        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Validation.hasValue(regEdit_Email, textInputLayoutEmail)) {
                        isvalidEmail = Validation.isEmailAddress(regEdit_Email, textInputLayoutEmail, true);
                        if (isvalidEmail) {
                            validEmail = regEdit_Email.getText().toString();

                            addNewemail.add(validEmail);
                            regEdit_Email.setText("");
                            updateEmailList();
                        } else {
                            validEmail = "";
                        }
                    }

                updateEmailList();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {

            new GetSigners().execute().get();

        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        try {

            new GetDocs().execute().get();

        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        generatePdfListerner = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String Type) {

                if (Type.equals("generatePdf")) {

                    try {
                        String success = data.getString("success");
                        if (success.equals("1")) {

                            docuemtStitched();
                        }

                    } catch (Exception e) {

                        CustomDialog.cancelProgressDialog();
                        // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                        //e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.cancelProgressDialog();
                // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);

            }

            @Override
            public void onFetchStart() {

                CustomDialog.cancelProgressDialog();
                // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);

            }
        };

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> emails = new ArrayList<>();

                if(signerEmail.size() > 0){
                    emails.addAll(signerEmail);
                }
                if(addNewemail.size() > 0){
                    emails.addAll(addNewemail);
                }

                if(emails.size() > 0){
                    if(selecteDocList.size()> 0){
                        stringBuilderSigner = new StringBuilder();
                        stringBuilderDoc = new StringBuilder();
                        for(int i=0;i<emails.size(); i++){

                            if(i == (emails.size()-1)) {
                                stringBuilderSigner.append(emails.get(i));
                            }
                            else{
                                stringBuilderSigner.append(emails.get(i)).append(",");
                            }

                        }
                        for(int i=0;i<selecteDocList.size(); i++){

                            if(i == (selecteDocList.size()-1)) {
                                stringBuilderDoc.append(selecteDocList.get(i)).append(".pdf");
                            }
                            else{
                                stringBuilderDoc.append(selecteDocList.get(i)).append(".pdf").append(",");
                            }
                        }
                        //generatePdf();
//                        pdf();
                        try {
                            generatePdfNew();
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }

                    else{
                        CustomDialog.notaryappDialogSingle(ShareActivity.this, "Please Select Document");
                    }
                }
                else{
                    CustomDialog.notaryappDialogSingle(ShareActivity.this, "Please Select Signers.");
                }


            }
        });
    }

    /*private void pdf() {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("tranId", transId)
                    .addFormDataPart("email", stringBuilderSigner.toString().trim())
                    .addFormDataPart("docName", stringBuilderDoc.toString().trim())
                    .build();
            Request request = new Request.Builder()
                    .url(AppUrl.GENERATE_PDF)
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();

            Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignored){

        }

    }*/


    private void generatePdf() {

        CustomDialog.showProgressDialog(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, AppUrl.GENERATE_PDF, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        docuemtStitched();

                    } else {
                        //Log.d("TAG", "Response: " + resultResponse);
                        //  CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    //Log.d("TAG", "JSON Error: " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d("TAG", "Volley Error: " + error);
                //  showUploadSnackBar();
                //CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
            }
        })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("tranId", transId.trim());
                params.put("email", stringBuilderSigner.toString().trim());
                params.put("docName", stringBuilderDoc.toString().trim());

                return params;
            }

        };

        queue.add(multipartRequest);
    }

    public void generatePdfNew() throws JSONException {
        try {
            //final String token = "Bearer " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TOKEN", "0000");
            final String token = AppUrl.Authorization_Key;

            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tranId", transId.trim());
            jsonObject.addProperty("email", stringBuilderSigner.toString().trim());
            jsonObject.addProperty("docName", stringBuilderDoc.toString().trim());

            Call<ResponseBody> call = service.getGenaratePDF(token, jsonObject);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        JSONObject obj = null;
                        try {
                            /*obj = new JSONObject(response
                                    .body().string().trim());*/
                            if (new JSONObject(response
                                    .body().string().trim()).getString("success").equalsIgnoreCase("1")) {
                                CustomDialog.cancelProgressDialog();
                                docuemtStitched();

                            } else {
                                //Log.d("TAG", "Response: " + resultResponse);
                                //  CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                            }
                        } catch (JSONException | IOException e) {
                            //e.printStackTrace();
                        }

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test", e11.toString());
        }
    }

    private void updateEmailList() {

        EmailListAdapter stepsAdapter2 = new EmailListAdapter(addNewemail,emailAddedListerner,this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        selectedEmails.setLayoutManager(layoutManager);
        selectedEmails.setAdapter(stepsAdapter2);

    }

    private void init() {

        textInputLayoutEmail = findViewById(R.id.textInput_email);
        regEdit_Email = findViewById(R.id.emailId);
        btnClose = findViewById(R.id.btn_pro_close);
        recyclerSigners = findViewById(R.id.recyclerSigners);
        recyclerDoc= findViewById(R.id.recyclerDoc);
        selectedEmails= findViewById(R.id.selectedEmails);
        shareBtn = findViewById(R.id.shareBtn);
        addEmail = findViewById(R.id.addEmail);

    }

    class GetSigners extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            //Getting the signer data from database
            signerList = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigners();

            return 0;
        }
        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            refreshSigner();
            updateEmailList();
        }
    }

    private void refreshSigner() {

        try {
            if (signerList.size() >= 1){


                if(isSelectedSigners.size() == 0) {
                    for (int i = 0; i < signerList.size(); i++) {
                        isSelectedSigners.add(false);
                    }
                }

                ShareSignersAdapter stepsAdapter1 = new ShareSignersAdapter(signerList, signerInterface,isSelectedSigners, this);
                GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerSigners.setLayoutManager(layoutManager);
                recyclerSigners.setAdapter(stepsAdapter1);

            }
            else {
                Toast.makeText(this, "No recipients found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    class GetDocs extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            stampCountArray = new ArrayList<>();

            //Getting the signer data from database
            List<DocumentsModel> list = new ArrayList<>();
            List<DocumentsModel> listcount = new ArrayList<>();

            list = databaseClient.getAppDatabase()
                    .documentsImageDao()
                    .getDisDocs();

            docList = list;

            int listCount = 0;
            for(int i=0;i<list.size();i++){

                listCount = databaseClient.getAppDatabase()
                        .documentsImageDao()
                        .countDocs(docList.get(i).getStampName());

                stampCountArray.add(listCount);
            }

            return 0;
        }
        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            refreshDoc();
        }
    }

    private void refreshDoc() {

        try {
            if (docList.size() >= 1){

                if(isSelectedDocs.size() == 0) {
                    for (int i = 0; i < docList.size(); i++) {
                        isSelectedDocs.add(false);
                    }
                }

                ShareDocsAdapter stepsAdapter2 = new ShareDocsAdapter(docList,documentSelectedListerner,isSelectedDocs,stampCountArray,this);
                GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
                recyclerDoc.setLayoutManager(layoutManager);
                recyclerDoc.setAdapter(stepsAdapter2);

            }
            else {
                Toast.makeText(this, "No documents found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }


    private  void docuemtStitched() {
        final Dialog dialog = new Dialog(ShareActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Documents Shared");
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

}
