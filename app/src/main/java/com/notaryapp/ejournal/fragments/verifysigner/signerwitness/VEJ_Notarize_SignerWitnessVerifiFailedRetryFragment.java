package com.notaryapp.ejournal.fragments.verifysigner.signerwitness;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_VerifySignerActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class VEJ_Notarize_SignerWitnessVerifiFailedRetryFragment extends Fragment {

    // public static final int REF_VIEW_CONTAINER = R.id.identityRoot;
    private View retryView;
    private Button btnRetry,btnUseAnother,btnClose;
    private String docExpiryStatus;
    private String TAG = "VerifiFailedRetryFragment";
    private TextView onfidoText,txtHead;
    //public String docStatus, transStatus, veriStatus,idStatus,selectedType,notaryEmail,retry;
    private String docStatus, transStatus, veriStatus,idStatus,selectedType,notaryEmail,retry;
    private DatabaseClient databaseClient;
    private int count,balanceTransCount,idCount =0,retryCount,selectIdCount ;
    private IJsonListener iJsonListener;
    private String signerEmail;

   /* public Notarize_SignerWitnessVerifiFailedRetryFragment(String idStatus) {
        this.idStatus = idStatus;
    }*/

    public VEJ_Notarize_SignerWitnessVerifiFailedRetryFragment
            (String docStatus, String trsnsStatus, String veriStatus, String docExpiryStatus,String retry) {
        this.docStatus = docStatus;
        this.transStatus = trsnsStatus;
        this.veriStatus = veriStatus;
        this.docExpiryStatus = docExpiryStatus;
        this.retry = retry;
    }
    public VEJ_Notarize_SignerWitnessVerifiFailedRetryFragment
            (String docStatus, String trsnsStatus, String veriStatus, String docExpiryStatus,String retry, String signerEmail) {
        this.docStatus = docStatus;
        this.transStatus = trsnsStatus;
        this.veriStatus = veriStatus;
        this.docExpiryStatus = docExpiryStatus;
        this.retry = retry;
        this.signerEmail = signerEmail;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        retryView = inflater.inflate(R.layout.fragment_notarizefailed_retry, container, false);
       init();
        if ((docStatus == null) &&
                (transStatus == null) && (veriStatus == null)
                && (docExpiryStatus == null)) {
            txtHead.setText("NO RESPONSE");
            onfidoText.setText("We are unable to complete this verification, and your account will not be charged. You can try again to verify the same ID or request an alternate ID from your customer.");
        }
        else if(docExpiryStatus == null){
            if (docStatus != null) {
                docStatus = Utils.capitalizeFirst(docStatus.toLowerCase());
            }
            onfidoText.setText("Document  : " + docStatus );
            updateTransactions();
        }else{
            if (docStatus != null) {
                docStatus = Utils.capitalizeFirst(docStatus.toLowerCase());
            }
            if (docExpiryStatus != null){
                docExpiryStatus = Utils.capitalizeFirst(docExpiryStatus.toLowerCase());
            }
            if (transStatus != null){
                transStatus = Utils.capitalizeFirst(transStatus.toLowerCase());
            }
            if (veriStatus != null){
                veriStatus = Utils.capitalizeFirst(veriStatus.toLowerCase());
            }
            onfidoText.setText("Document  : " + docStatus + "\n DocExpiry : " + docExpiryStatus + "\n Transaction :   " + transStatus + "\n Face Verification  : " + veriStatus);
            if(retry.equals("NO")) {
                updateTransactions();
            }
        }
        btnUseAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateSelectIdCount().execute();
              //  loadFragment(new Notarize_WitnessDocTypeFragment());
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new UpdateRetryCount().execute();
            }
        });
        return retryView;
    }

    private void init() {
        initIJsonListener();
        onfidoText = retryView.findViewById(R.id.txt_onfido_response);
        txtHead = retryView.findViewById(R.id.textHead);
        btnClose = retryView.findViewById(R.id.btn_close);
        btnUseAnother = retryView.findViewById(R.id.btn_use_another_id);
        btnRetry = retryView.findViewById(R.id.btn_retry);
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(),
                VEJ_VerifySignerActivity.REF_VIEW_CONTAINER,
                fragment, false);

    }
    class UpdateRetryCount extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... voids) {

            selectedType =  databaseClient.getAppDatabase().witnessSelectIDDao().getSelectedType();
            retryCount = databaseClient.getAppDatabase().witnessSelectIDDao().getRetryCount(selectedType);
            databaseClient.getAppDatabase().witnessSelectIDDao().updateRetryCount(selectedType);
           //int count =  databaseClient.getAppDatabase().witnessSelectIDDao().deletedCount();
            return retryCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (count >= 1) {
                retryDialog();
            }else {
                loadFragment(new VEJ_Notarize_WitnessDocTypeFragment(selectedType, signerEmail));
            }
        }
    }
    class UpdateSelectIdCount extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... voids) {
            selectIdCount =  databaseClient.getAppDatabase().witnessSelectIDDao().deletedCount();
            return selectIdCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (count == 4) {
                proceedDialog("SORRY!! You are not allowed to proceed. ");
                // CustomDialog.notaryappDialogSingle(getActivity(),"SORRY!! You are not allowed to proceed. ");
            }else {
                loadFragment(new VEJ_Notarize_WitnessDocTypeFragment(1, signerEmail));
            }
        }
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
        }

    }
  private void retryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("SORRY!! Retry option completed. ");
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
                getActivity().finish();

            }
        });
        dialog.show();
    }

    private void updateTransactions(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email",notaryEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.UPDATE_TRANSACTIONS,"Transactions");
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
                        if (data.has("success")) {
                            if (type.equals("Transactions")) {

                                int total_bought = data.getInt("total_bought");
                                int total_used = data.getInt("total_used");

                                balanceTransCount = total_bought - total_used;
                                    /*if(balanceTransCount>0){

                                    }else if(balanceTransCount == 0){
//                                        proceedDialog("SORRY!! Your transactions completed");
                                    }else{

                                    }*/
                                new UpdateMemPlans().execute();
                                    //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();

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
}
