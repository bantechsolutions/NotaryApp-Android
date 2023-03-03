package com.notaryapp.ejournal.fragments.addsignaturethumb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_NotarizeStepsActivity;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbActivity;
import com.notaryapp.ejournal.adapter.VEJ_AllSignersRecycleerViewAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;

import java.util.List;

public class VEJ_NotarizeSignatureAndThumbFragment extends Fragment {

    private View view;
    private Button startBtn;
    private Button btnBack,btnClose;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    public static final int REF_VIEW_CONTAINER = R.id.signAndThumbViewContainer;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId;
    private RecyclerView recyclerViewSigners;

    private List<SignerReg> signerList;
    private List<WitnessReg> witnessList;

    private String signerType= "";
    private Boolean flag = true;


    private VEJ_AllSignersRecycleerViewAdapter allSignersRecycleerViewAdapter;
    private Context mContext;


    public VEJ_NotarizeSignatureAndThumbFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.vej_fragment_notarize_signature_and_thumb, container, false);
        mContext = getContext();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();

        new GetAllSigners().execute();




        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //loadFragment(new SignAndThumbInstructionFragment());
                startActivity(new Intent(getActivity(), VEJ_NotarizeStepsActivity.class));
                getActivity().finish();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                startActivity(new Intent(getActivity(), VEJ_NotarizeStepsActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                getActivity().finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                startActivity(new Intent(getActivity(), VEJ_NotarizeStepsActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

    private void init() {
        startBtn = view.findViewById(R.id.startBtn);
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        recyclerViewSigners = view.findViewById(R.id.recyclerViewSigners);

        startBtn.setVisibility(View.GONE);

        //initIJsonListener();
        databaseClient =  DatabaseClient.getInstance(getActivity());
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

    class GetAllSigners extends AsyncTask<Void, Void, String>

    {
        @Override
        protected String doInBackground(Void... voids) {

            //Getting the signer data from database
            signerList = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigners();


            return null;
        //signDocCount = databaseClient.getAppDatabase().signDocsDao().getCount();

    }

        @Override
        protected void onPostExecute(String email) {
        super.onPostExecute(email);

            /*for (int i=0; i <signerList.size(); i++){
                    if (flag){
                        if (!signerList.get(i).getSignatureImagePath().equals("null")){
                            flag = false;
                            startBtn.setVisibility(View.VISIBLE);
                        }
                    }
            }*/
            String SignaturePath;
            for (int i =0; i <signerList.size(); i++){
                //Log.d("SIGNER_SIGN",signerList.get(i).getProImagePath());
                try {
                    SignaturePath = signerList.get(i).getSignatureImagePath().toString();
                } catch (Exception e){
                    SignaturePath = "null";
                }

                if (flag){
                    if (SignaturePath.equalsIgnoreCase("null")){
                        flag = false;
                    }
                }

            }
            if (flag){
                startBtn.setVisibility(View.VISIBLE);
            }

            if (signerList.size() >= 1){

                //Toast.makeText(mContext, "111111111", Toast.LENGTH_SHORT).show();
                allSignersRecycleerViewAdapter = new VEJ_AllSignersRecycleerViewAdapter(signerList, mContext);
                recyclerViewSigners.setAdapter(allSignersRecycleerViewAdapter);
            }
        /*if (signDocCount > 0) {
            Intent intent = new Intent(NotarizeStepsActivity.this, SignAndThumbActivity.class);
            startActivity(intent);

        } else {
            CustomDialog.notaryappDialogSingle(NotarizeStepsActivity.this, "Please take signDoc");
            // finish();
        }*/
        }

    }


}
