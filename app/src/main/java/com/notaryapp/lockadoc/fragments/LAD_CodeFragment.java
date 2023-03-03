package com.notaryapp.lockadoc.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.ui.activities.verifyauthenticate.SealVideoActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;


public class LAD_CodeFragment extends Fragment {

    private View view;
    private Button btnComplete;
    private Button btnBack,btnClose;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId,licenseNo,selectedStamp;
    private DatabaseClient databaseClient;
    private TextView pin1,pin2,pin3,pin4,pin5,pin6,pin7;
    private Context context;
    //private VideoView videoView;
    private ImageView videoImage;
    private SealAdded sealAdded;
    private String sealCode = "";
    private String update = "";
    private String docName ="";
    private String serverDocName ="";

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;

   /* public LAD_CodeFragment(String licenseNo, String selectedStamp) {
        this.licenseNo = licenseNo;
        this.selectedStamp = selectedStamp;
    }*/

    public LAD_CodeFragment(String docName, String serverDocName) {
        this.docName = docName;
        this.serverDocName = serverDocName;
    }

    public LAD_CodeFragment(String licenseNo, String update, String selectedStamp) {
        this.licenseNo = licenseNo;
        this.selectedStamp = selectedStamp;
        this.update = update;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.lad_fragment_code, container, false);
        init();
        //getVideoFromHttp("https://ia800209.us.archive.org/24/items/WildlifeSampleVideo/Wildlife.mp4");
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pin1.getText().toString().equals("")) {
                    CompleteSealCode();
                }else{
                    CustomDialog.notaryappDialogSingle(getActivity(),"Enter SealCode");
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
        videoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getActivity(),SealVideoActivity.class);
                intent.putExtra("from","lad");
                startActivity(intent);
            }
        });
        return view;
    }

    private void init() {
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        btnComplete = view.findViewById(R.id.btnComplete);
        pin1 = view.findViewById(R.id.pin1);
        pin2 = view.findViewById(R.id.pin2);
        pin3 = view.findViewById(R.id.pin3);
        pin4 = view.findViewById(R.id.pin4);
        pin5 = view.findViewById(R.id.pin5);
        pin6 = view.findViewById(R.id.pin6);
        pin7 = view.findViewById(R.id.pin7);

        videoImage= view.findViewById(R.id.videoImage);
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();

    }

    private void getSealCodes(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("notary",savedEmail);
                params.put("tranid",transactionId);
                //change this to notary username

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.LAD_CODES,"sealCodes");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void modifySealCodes(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("notary",savedEmail);
                params.put("license",licenseNo);
                params.put("tranid",transactionId);
                //change this to notary username

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.SEAL_MODIFY_CODES,"sealCodes");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void CompleteSealCode(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("tranid",transactionId);

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.ADD_SEAL_COMP,"complete");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData, String type ) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {
                        if(type.equals("sealCodes")) {
                            sealCode = responseData.getString("sealCode");
                            String t1 = sealCode.substring(0,1);
                            pin1.setText(t1);
                            String t2 = sealCode.substring(1,2);
                            pin2.setText(t2);
                            String t3 = sealCode.substring(2,3);
                            pin3.setText(t3);
                            String t4 = sealCode.substring(3,4);
                            pin4.setText(t4);
                            String t5 = sealCode.substring(4,5);
                            pin5.setText(t5);
                            String t6 = sealCode.substring(5,6);
                            pin6.setText(t6);
                            String t7 = sealCode.substring(6,7);
                            pin7.setText(t7);
                            //VideoStream
                            //getVideoFromHttp(AppUrl.VIDEO_STREAM);

                        }else if(type.equals("complete")){
                            String success = responseData.getString("success");
                            if(success.equals("1")){
                                sealAdded = new SealAdded();

                                sealAdded.setLicenseNum(licenseNo);
                                sealAdded.setSealCode(sealCode);
                                sealAdded.setSealUrl(selectedStamp);
                                new UpdateSealStatus().execute();

                            }
                        }


                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {
                //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            //scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();

            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();

            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);

            if(update.equalsIgnoreCase("")) {
                getSealCodes();
            }
            else{
                modifySealCodes();
            }
        }

    }

    class UpdateSealStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().insert(sealAdded);
            // databaseClient.getAppDatabase().vaCheckDao().updateAddSeal();
            return null;
        }

        @Override
        protected void onPostExecute(Void docs) {
            super.onPostExecute(docs);
            // Intent intent = new Intent(getActivity(), NotarizeStepsActivity.class);
            //  startActivity(intent);
            //getActivity().finish();
            loadFragment(new LAD_AddDocFragmentLanding(docName,serverDocName)) ;

        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
    }

}