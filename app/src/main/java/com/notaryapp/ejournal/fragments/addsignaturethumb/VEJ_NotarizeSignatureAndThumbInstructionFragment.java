package com.notaryapp.ejournal.fragments.addsignaturethumb;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_Notarize_SignatureScreenActivity;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;

public class VEJ_NotarizeSignatureAndThumbInstructionFragment extends Fragment {

    private View view;
    private Button startBtn;
    private Button btnBack,btnClose;
    public static final int REF_VIEW_CONTAINER = R.id.signAndThumbViewContainer;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId;
    private String signerEmail;
    private String signerFirstName;

    public VEJ_NotarizeSignatureAndThumbInstructionFragment() {
        // Required empty public constructor
    }

    public VEJ_NotarizeSignatureAndThumbInstructionFragment(String signerEmail, String signerFirstName){
        this.signerEmail = signerEmail;
        this.signerFirstName = signerFirstName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.vej_fragment_notarize_signature_thumb_instruction, container, false);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), VEJ_Notarize_SignatureScreenActivity.class);
                intent.putExtra("SIGNER_EMAIL", signerEmail);
                intent.putExtra("SIGNER_FIRSTNAME", signerFirstName);
                startActivity(intent);
                getActivity().finish();

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


        //initIJsonListener();
        //databaseClient =  DatabaseClient.getInstance(getActivity());
        //new SelectData().execute();
    }
    /*class SelectData extends AsyncTask<String, Void, Void> {

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
           // startActivity(new Intent(getActivity(),NotarizeStepsActivity.class));
            getActivity().onBackPressed();
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
                e.printStackTrace();
            }
           
            // postapiRequest.request(getActivity(), iJsonListener, params, url, "docSigned");
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.SIGN_DOC,"signDoc");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
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
                    e.printStackTrace();
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
    }*/

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VEJ_SignAndThumbActivity.REF_VIEW_CONTAINER,fragment,true);
    }
}
