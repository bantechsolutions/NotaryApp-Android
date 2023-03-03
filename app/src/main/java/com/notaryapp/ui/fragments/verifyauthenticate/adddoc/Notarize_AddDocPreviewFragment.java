package com.notaryapp.ui.fragments.verifyauthenticate.adddoc;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class Notarize_AddDocPreviewFragment extends Fragment {

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private View previewView;
    private final String TAG="CameraPreviewFragment";
    private FragmentTransaction fragmentTransaction;
    private Button confirmBtn,btnCancel,btnBack;
    private ImageView imageView;
    private int selectedId;
    private int prevSelected = -1;
   // private String url = AppUrl.API_BASE_URL+"uploadDoc";
   // private String selfieUrl = AppUrl.API_BASE_URL+"uploadPhoto";
    private String fromFragment,imgPath;
    private Bitmap bitmap;
    private String userEmail,savedImage,docName,serverDocName;
    private DatabaseClient databaseClient;
    private boolean front=true,back=false,selfie;
    private Uri imageUri;
    private Fragment fragment;
    final int PIC_CROP = 1;
//    private DocPics docPics;
//    private AddDocs addDocs;

    DocumentsModel documentsModel;

    private String imageName,transId;;

    public Notarize_AddDocPreviewFragment(String imageName,
                                          Uri imageUri,
                                          String docName, String serverDocName, String transId) {
        this.docName = docName;
        this.serverDocName = serverDocName;
        this.transId = transId;
        this.savedImage = imageName;
        this.imageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewView =  inflater.inflate(R.layout.fragment_notarize_add_doc_preview, container, false);
        init();
        CustomDialog.cancelProgressDialog();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 CustomDialog.showProgressDialog(getActivity());
                    uploadFile();

                //Saving the bitmap in file system
              //  saveImage(bitmap);

              //  loadFragment(new Notarize_AddDocCameraFragment());
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return previewView;
    }

    private void init(){
        CustomDialog.showProgressDialog(getActivity());
        databaseClient = DatabaseClient.getInstance(getActivity());

        //Getting user's email od and transaction Id
        new SelectEmail().execute();
        confirmBtn = previewView.findViewById(R.id.btnOk);
        //bitmap = Notarize_AddDocCameraFragment.croppedBitmap;
        imageView = previewView.findViewById(R.id.imageView);
        btnCancel = previewView.findViewById(R.id.btnCancel);
        btnBack = previewView.findViewById(R.id.btn_back);

        loadImage();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), REF_VIEW_CONTAINER,fragment,true);
    }

    private void loadImage(){

        imageView.setImageURI(imageUri);
        documentsModel = new DocumentsModel(docName,savedImage,serverDocName);
    }

    private Uri saveImage(Bitmap myBitmap) {
        File docpath;
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        savedImage = "va_" + getRandomNumber()+".png";
        docpath = new File(directory,  savedImage);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(docpath);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();

            documentsModel = new DocumentsModel(docName,savedImage,serverDocName);

        } catch (Exception e) {
//            Log.e("SAVE_IMAGE", e.getMessage(), e);
        } finally {
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        Uri imgUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",docpath);

        String path = docpath.getAbsolutePath();

        imageView.setImageURI(imgUri);
        CustomDialog.cancelProgressDialog();
        return imgUri;

    }

    private void uploadFile() {
        // CustomDialog.showProgressDialog(getActivity());
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppUrl.UPLOAD_DOC_VL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        new SaveImages().execute();
                        loadFragment(new Notarize_AddDocCameraFragment(docName, serverDocName));
                    } else {
//                        Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
//                Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
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
                params.put("tranId", transId);
                params.put("docName",serverDocName);
                params.put("userName",userEmail);
                params.put("photoName",savedImage);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imageView == null) {
//                    Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(savedImage, VolleyHelper
                        .getFileDataFromDrawable(getActivity(),
                                imageView.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }

    class SelectEmail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            userEmail = databaseClient.getAppDatabase().userRegDao().getEmail();
            //  docName =databaseClient.getAppDatabase().docPicsDao().getDocName("doc");
            return userEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
//            saveImage(bitmap);
        }

    }
    class SaveImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //Saving the image name to database
            databaseClient.getAppDatabase().documentsImageDao().insert(documentsModel);

            return userEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }

    public static int getRandomNumber(){

        //Random rand = new Random();
        SecureRandom rand = new SecureRandom();
        int rand_int1 = rand.nextInt(100000);

        return rand_int1;

    }
}
