package com.notaryapp.ejournal.fragments.addsignaturethumb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbActivity;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbCameraPreviewActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VEJ_ThumbprintPreviewFragment extends Fragment {

    private Button confirmBtn;
    private View previewView;



    private final String TAG="CameraPreviewFragment";
    private FragmentTransaction fragmentTransaction;

    private Button btnOk,btnCancel,btnBack,btn_close;

  //  private View previewView;
    private ImageView imageView;
  //  private String url = AppUrl.API_BASE_URL+"uploadDoc";
  //  private String selfieUrl = AppUrl.API_BASE_URL+"uploadPhoto";
    private String fromFragment,imgPath;
    private Bitmap bitmap;
    private String userEmail,savedImage,docIdType,errorMess;
    private DatabaseClient databaseClient;
    private boolean front=true,back=false,selfie;
    private Uri imageUri;
    private Fragment fragment;
    final int PIC_CROP = 1;
    private String signerEmail;
    private TextView note;
    private String savedEmail,transactionId;
    private VEJ_ThumbprintCameraFragment vej_thumbprintCameraFragment;


    public VEJ_ThumbprintPreviewFragment() {
    }

    public VEJ_ThumbprintPreviewFragment(String savedImage) {
    }

    public VEJ_ThumbprintPreviewFragment(Uri imageUri, String savedImage, String signerEmail) {
        this.imageUri = imageUri;
        this.savedImage = savedImage;
        this.signerEmail  = signerEmail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewView = inflater.inflate(R.layout.vej_fragment_signature_peview, container, false);
        init();

        note.setText("Thumbprint Preview");
        CustomDialog.cancelProgressDialog();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOk.setEnabled(false);
              //  CustomDialog.showProgressDialog(getActivity());
                //uploadFile(selfieUrl);
                //loadFragment(new Notarize_SignerPersonalProfileFragment(savedImage));
                new saveThumbprint().execute();
                /*Intent intent = new Intent(getActivity(), VEJ_SignAndThumbActivity.class);
                startActivity(intent);
                getActivity().finish();*/
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
                /*Intent intent = new Intent(getActivity(),VEJ_SignAndThumbActivity.class);
                startActivity(intent);
                getActivity().finish();*/
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                Intent intent = new Intent(getActivity(),VEJ_SignAndThumbActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return previewView;
    }

    private void init(){
        CustomDialog.showProgressDialog(getActivity());
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
        confirmBtn = previewView.findViewById(R.id.btnOk);


        errorMess = getResources().getText(R.string.networkerror).toString();
        //bitmap = VEJ_ThumbprintCameraFragment.croppedBitmap;
        //bitmap = vej_thumbprintCameraFragment.getCroppedBitmap();
        imageView = previewView.findViewById(R.id.imageView);
        btnOk = previewView.findViewById(R.id.btnOk);
        btnCancel = previewView.findViewById(R.id.btnCancel);
        btn_close = previewView.findViewById(R.id.btn_close);
        btnBack = previewView.findViewById(R.id.btn_back);
        note = previewView.findViewById(R.id.note);
        btnOk.setEnabled(true);

        imageView.setImageURI(imageUri);
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

    class saveThumbprint extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            /*SignerReg signerReg = new SignerReg();
            signerReg.setEmail(signerEmail);
            signerReg.setSignatureImagePath(savedImage);*/
            databaseClient.getAppDatabase()
                    .signerRegDao()
                    .updateThumb(signerEmail, savedImage);

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
            /*Intent intent = new Intent(getActivity(), VEJ_SignAndThumbActivity.class);
            startActivity(intent);
            getActivity().finish();*/
        }
    }

    private void uploadFile() {
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        CustomDialog.showProgressDialog(getActivity());
        
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,AppUrl.UPLOAD_THUMBPRINT_VEJ, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        //getActivity().finish();
                        //loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
                        // loadFragment(new Notarize_SignerPersonalProfileFragment());
                        Intent intent = new Intent(getActivity(), VEJ_SignAndThumbActivity.class);
                        startActivity(intent);
                        getActivity().finish();
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

                Log.d("THUMB_PARAM", params.toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                params.put("file", new DataPart(savedImage, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                imageView.getDrawable()), "image/jpg"));

                Log.d("THUMB_PARAM_IMG", params.toString());
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