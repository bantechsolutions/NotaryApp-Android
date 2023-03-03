package com.notaryapp.ejournal.fragments.signDoc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_NotarizeStepsActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SignDocs;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class VEJ_Notarize_SignDocs_Sign_DocumentsFragment extends Fragment {

    private View view;
    private Button startBtn;
    private Button btnBack,btnClose;
    public static final int REF_VIEW_CONTAINER = R.id.signDocsViewContainer;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId;
    private CheckBox checkBox1;

    public VEJ_Notarize_SignDocs_Sign_DocumentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notarize_sign_docs_sign_documents, container, false);
        init();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox1.isChecked()){
                    //Intent intent = new Intent(SignDocumnet1.this, SignDocument2.class);
                    //startActivity(intent);
                    signDocChk();
                } else {
                    Toast.makeText(getActivity(), "Please tick all clauses to proceed to the next step.", Toast.LENGTH_SHORT).show();
                }


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
            }
        });
        return view;
    }

    private void init() {
        startBtn = view.findViewById(R.id.startBtn);
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        checkBox1 = view.findViewById(R.id.checkBox1);


        initIJsonListener();
        databaseClient =  DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
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
    class UpdateVACheck extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            SignDocs signDocs = new SignDocs();
            signDocs.setEmail(savedEmail);
            signDocs.setSuccess(true);
            databaseClient.getAppDatabase().signDocsDao().insert(signDocs);
          //  databaseClient.getAppDatabase().vaCheckDao().updateSignDoc();
            return null;
        }
        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
           startActivity(new Intent(getActivity(), VEJ_NotarizeStepsActivity.class));
            //getActivity().onBackPressed();
        }
    }
    private void signDocChk() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("tranid", transactionId);
              //  params.put("tranid", "92831699-6172-4074-8165-07981167d248");
            } catch (Exception e) {
                //e.printStackTrace();
            }
           
            // postapiRequest.request(getActivity(), iJsonListener, params, url, "docSigned");
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.SIGN_DOC,"signDoc");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data,String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("success")) {
                            String success = data.getString("success");
                            if(type.equals("signDoc")) {
                                if(success.equals("1")){
                                    new UpdateVACheck().execute();

                                }
                            }
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(),"Error! No data fetched from server");
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                  //  CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                // RequestQueueService.showAlert(msg,getActivity());
               // CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

}
