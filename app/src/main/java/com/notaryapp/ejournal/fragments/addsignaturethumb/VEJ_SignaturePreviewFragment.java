package com.notaryapp.ejournal.fragments.addsignaturethumb;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_Notarize_SignatureScreenActivity;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbCameraPreviewActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VEJ_SignaturePreviewFragment extends Fragment {

    private View view;
    private Button btnBack,btnClose, btnCancel, btnOk;
    public static final int REF_VIEW_CONTAINER = R.id.signAndThumbCameraPreviewViewContainer;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId;

    private Uri imgUri;
    private String savedImage, signerEmail, signerFirstname;
    private ImageView imageView;

    public VEJ_SignaturePreviewFragment() {
        // Required empty public constructor
    }

    public VEJ_SignaturePreviewFragment(Uri imgUri, String savedImage, String signerEmail, String signerFirstname) {
        this.imgUri = imgUri;
        this.savedImage = savedImage;
        this.signerEmail=signerEmail;
        this.signerFirstname=signerFirstname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.vej_fragment_signature_peview, container, false);
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        CustomDialog.cancelProgressDialog();
        Log.d("IMG_LOG", imgUri.toString());
        imageView.setImageURI(imgUri);
        imageView.setBackgroundColor(Color.WHITE);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signDocChk();
                /*startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SignThumbInstruction.class);
                        startActivity(intent);
                    }
                });*/
                new saveSignature().execute();
                //loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));

                //Toast.makeText(getActivity(), "CLICKED BY YOU", Toast.LENGTH_SHORT).show();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                //loadFragment(new SignAndThumbFragment());
                Intent intent = new Intent(getActivity(), VEJ_Notarize_SignatureScreenActivity.class);
                intent.putExtra("SIGNER_EMAIL", signerEmail);
                intent.putExtra("SIGNER_FIRSTNAME", signerFirstname);
                startActivity(intent);
                getActivity().finish();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                Intent intent = new Intent(getActivity(), VEJ_Notarize_SignatureScreenActivity.class);
                intent.putExtra("SIGNER_EMAIL", signerEmail);
                intent.putExtra("SIGNER_FIRSTNAME", signerFirstname);
                startActivity(intent);
                getActivity().finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VEJ_Notarize_SignatureScreenActivity.class);
                intent.putExtra("SIGNER_EMAIL", signerEmail);
                intent.putExtra("SIGNER_FIRSTNAME", signerFirstname);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    private void init() {
        btnOk = view.findViewById(R.id.btnOk);
        btnBack = view.findViewById(R.id.btn_back);
        btnClose = view.findViewById(R.id.btn_close);
        btnCancel = view.findViewById(R.id.btnCancel);
        imageView = view.findViewById(R.id.imageView);


        //initIJsonListener();
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
    /*
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




    class saveSignature extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            /*SignerReg signerReg = new SignerReg();
            signerReg.setEmail(signerEmail);
            signerReg.setSignatureImagePath(savedImage);*/
            databaseClient.getAppDatabase()
                    .signerRegDao()
                    .updateSignature(signerEmail, savedImage);

            /*SignDocs signDocs = new SignDocs();
            signDocs.setEmail(savedEmail);
            signDocs.setSuccess(true);
            databaseClient.getAppDatabase().signDocsDao().insert(signDocs);*/
            //  databaseClient.getAppDatabase().vaCheckDao().updateSignDoc();
            return null;
        }
        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            // startActivity(new Intent(getActivity(),NotarizeStepsActivity.class));
            //getActivity().onBackPressed();
            uploadFile();
            //loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
        }
    }
    private void uploadFile() {
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        CustomDialog.showProgressDialog(getActivity());
        
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,AppUrl.UPLOAD_SIGNATURE_VEJ, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        //getActivity().finish();
                        loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
                        // loadFragment(new Notarize_SignerPersonalProfileFragment());
                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //   CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
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
                params.put("signer", signerEmail);
                params.put("tranid",transactionId);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                params.put("file", new DataPart(savedImage, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                imageView.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VEJ_SignAndThumbCameraPreviewActivity.REF_VIEW_CONTAINER,fragment,true);
    }


}
