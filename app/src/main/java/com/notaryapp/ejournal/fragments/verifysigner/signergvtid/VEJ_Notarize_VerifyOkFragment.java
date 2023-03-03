package com.notaryapp.ejournal.fragments.verifysigner.signergvtid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_VerifySignerActivity;
import com.notaryapp.ejournal.fragments.selfie.govissuedid.VEJ_GovIssuedIdAddSelfieActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class VEJ_Notarize_VerifyOkFragment extends Fragment {
    private View retryView;
    private Button okBtn;
    private TextView txtDoc,txtTrnsaction,txtFace;
    private String docStatus,transStatus,veriStatus,docExpiryStatus;
    private String notaryEmail;
    private DatabaseClient databaseClient;
    private int balanceTransCount;
    private IJsonListener iJsonListener;
    private String firstName,lastName,scanRef;
    private boolean transUpdate = false;

    public VEJ_Notarize_VerifyOkFragment(){

    }
    public VEJ_Notarize_VerifyOkFragment(String firstName, String lastName, String scanRef){
        this.firstName = firstName;
        this.lastName = lastName;
        this.scanRef = scanRef;
    }
    public VEJ_Notarize_VerifyOkFragment(String docStatus, String trsnsStatus, String veriStatus, String docExpiryStatus){
        this.docStatus = docStatus;
        this.transStatus = trsnsStatus;
        this.veriStatus = veriStatus;
        this.docExpiryStatus = docExpiryStatus;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retryView = inflater.inflate(R.layout.fragment_validate_ok, container, false);

        init();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!transUpdate) {
                    updateTransactions();
                }
                }
        });

        return retryView;
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(),
                VEJ_VerifySignerActivity.REF_VIEW_CONTAINER,
                fragment, false);

    }

    private void init(){
        okBtn = retryView.findViewById(R.id.btn_verify_ok);
        txtDoc = retryView.findViewById(R.id.txt_doc_response);
        txtTrnsaction = retryView.findViewById(R.id.txt_transaction_response);
        txtFace = retryView.findViewById(R.id.txt_face_response);
      //  txtDoc.setText("Documents : "+docStatus+"\n DocExpiry : "+docExpiryStatus);
      //  txtTrnsaction.setText("Transaction : "+transStatus);
       // txtFace.setText("Face Similarity : "+veriStatus);
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
        initIJsonListener();



    }
    private void updateTransactions(){
        transUpdate = true;
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email",notaryEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request0(getActivity(),iJsonListener,params, AppUrl.UPDATE_TRANSACTIONS,"Transactions");
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

                        if (type.equals("Transactions")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");

                            balanceTransCount = total_bought - total_used;
                            new UpdateMemPlans().execute();
                            //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();

                        }
                    }
                    else {
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
    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            notaryEmail = databaseClient.getAppDatabase().userRegDao().getEmail();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }
    class UpdateMemPlans extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .updateCount(balanceTransCount);

            return
                    "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            Intent intent = new Intent(getActivity(), VEJ_GovIssuedIdAddSelfieActivity.class);
            intent.putExtra("FirstName", firstName);
            intent.putExtra("LastName", lastName);
            intent.putExtra("ScanRef", scanRef);
            intent.putExtra("IdType", "");
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            getActivity().finish();
            /*loadFragment(new Notarize_SignerProfileFragment(firstName,lastName,scanRef));*/


            //   getActivity().onBackPressed();

            // loadFragment(new Validate_FinalFragment());
            //   getActivity().onBackPressed();
        }

    }
}
