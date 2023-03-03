package com.notaryapp.ui.fragments.reg_jumio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.onboarding.SelectIdentityActivity;
import com.notaryapp.ui.activities.onboarding.VerifyBaseActivity;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;

public class VerifyBase_VerifiFailedRetryFragment extends Fragment {

   // public static final int REF_VIEW_CONTAINER = R.id.identityRoot;
    private View retryView;
    private int selectedId;
    private String TAG = "VerifiFailedRetryFragment";
    private String idStatus;
    private TextView onfidoText,txtHead;
    private String docStatus,transStatus,veriStatus,docExpiryStatus;
    private DatabaseClient databaseClient;
    private int reTryCount,selectIdCount;
    private String selectedType;
    private Button btnRetry,btnAnotherId,btnClose;
    private Context context;
    int idCount =0 ;

    public VerifyBase_VerifiFailedRetryFragment(String idStatus){
        this.idStatus = idStatus;
    }
    public VerifyBase_VerifiFailedRetryFragment
            (String docStatus, String trsnsStatus,String veriStatus,String docExpiryStatus){
         this.docStatus = docStatus;
        this.transStatus = trsnsStatus;
        this.veriStatus = veriStatus;
        this.docExpiryStatus = docExpiryStatus;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        retryView = inflater.inflate(R.layout.fragment_verifyfailed_retry, container, false);
        //ButterKnife.bind(this,retryView);
        init();
        onfidoText = retryView.findViewById(R.id.txt_onfido_response);
        if ((docStatus == null) &&
                (transStatus == null) && (veriStatus == null)
                && (docExpiryStatus == null)) {
            txtHead.setText("NO RESPONSE");
            onfidoText.setText("You can either retry the verification with the same ID or Choose Another ID for verification");
        }else if(docExpiryStatus == null){
            if (docStatus != null) {
                docStatus = Utils.capitalizeFirst(docStatus.toLowerCase());
            }
            onfidoText.setText("Document  : " + docStatus );

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
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new UpdateRetryCount().execute();
            }
        });
        btnAnotherId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateSelectIdCount().execute();
                /*Intent intent = new Intent(getActivity(), SelectIdentityActivity.class);
                startActivity(intent);*/
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedDialog("SORRY!! You are not allowed to proceed. ");
            }
        });
        return retryView;
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(),
                VerifyBaseActivity.REF_VIEW_CONTAINER,
                fragment,false);

    }
    private void init(){
        txtHead = retryView.findViewById(R.id.textHead);
        btnAnotherId = retryView.findViewById(R.id.btn_use_another_id);
        btnRetry = retryView.findViewById(R.id.btn_retry);
        btnClose = retryView.findViewById(R.id.btn_close);

        onfidoText = retryView.findViewById(R.id.txt_onfido_response);

        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        //  new SelectIDType().execute();
    }
    class UpdateRetryCount extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... voids) {

            selectedType =  databaseClient.getAppDatabase().identityTypeDao().getSelectedType();
            reTryCount = databaseClient.getAppDatabase().identityTypeDao().getRetryCount(selectedType);
            databaseClient.getAppDatabase().identityTypeDao().updateRetryCount(selectedType);
          //  int count =  databaseClient.getAppDatabase().identityTypeDao().deletedCount();
            return reTryCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if(reTryCount >= 1){
                retryDialog();
            }else{
                Intent intent = new Intent(getActivity(), SelectIdentityActivity.class);
                intent.putExtra("SELECTEDTYPE",selectedType);
                startActivity(intent);
            }
        }
    }
    class UpdateSelectIdCount extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... voids) {
            selectIdCount =  databaseClient.getAppDatabase().identityTypeDao().deletedCount();
            return selectIdCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (count == 4) {
                proceedDialog("SORRY!! You are not allowed to proceed. ");
                // CustomDialog.notaryappDialogSingle(getActivity(),"SORRY!! You are not allowed to proceed. ");
            }else {
                Intent intent = new Intent(getActivity(), SelectIdentityActivity.class);
                startActivity(intent);
            }
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
   /* private void callCustomDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Would you like to Quit the Validation process and continue with the next steps?");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnNo);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                terminateDialog();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //
                startActivity(new Intent(getActivity(), ProfileBaseActivity.class));
            }
        });
        dialog.show();
    }*/
   /* private void terminateDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Are you sure you want to terminate the validation process??");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnNo);
        dialogButton.setText("YES");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OnboardingBaseActivity.class));
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("NO");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }*/

}
