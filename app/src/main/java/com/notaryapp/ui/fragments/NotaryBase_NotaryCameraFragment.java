package com.notaryapp.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.notaryapp.components.CameraPreview;

public class NotaryBase_NotaryCameraFragment extends Fragment implements  SurfaceHolder.Callback {

    private RelativeLayout btnCapture;
    private Button btnBack,btnClose;

    private Context mContext;
    private View noteryCameraView;
    private Camera.PictureCallback mPicture;
    private Bitmap bitmap, croppedBitmap;

    private TextView mainHead;
    private String selectedIDType,fromFragment,docIdType,savedEmail;
    private final String TAG="CameraFragment";
    private DatabaseClient databaseClient ;

    private ConstraintLayout previewLayout;

    private View borderCamera;

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    ImageView imgPreview ;
    private boolean previewing = false;
    private boolean captureFlag = false, addStampFlag = false;
    private int position ;
    private String license_num,from,imgName,license_state, stampName;
    Activity activity;

    Camera.Size previewSizeOptimal;
    private boolean isUploading = false;

    public NotaryBase_NotaryCameraFragment(int position, String license_num, String from,boolean captureFlag,String license_state, String stampName) {
       this.position = position;
       this.license_num = license_num;
       this.from = from;
       this.captureFlag = captureFlag;
       this.license_state = license_state;
       this.stampName = stampName;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        noteryCameraView = inflater.inflate(R.layout.fragment_notery_camera, container, false);
        mainHead =  noteryCameraView.findViewById(R.id.mainHead);
        databaseClient = DatabaseClient.getInstance(getActivity());
        mContext = getContext();

        activity = getActivity();

        new SelectEmail().execute();

        previewLayout = noteryCameraView.findViewById(R.id.preview_layout);
        borderCamera= noteryCameraView.findViewById(R.id.border_camera);
        imgPreview = noteryCameraView.findViewById(R.id.imgPriwe);
        surfaceView =  noteryCameraView.findViewById(R.id.camera_preview_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btnCapture = noteryCameraView.findViewById(R.id.btnCapture);
        btnBack = noteryCameraView.findViewById(R.id.btn_camera_back);
        btnClose = noteryCameraView.findViewById(R.id.btn_camera_close);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCapture.setEnabled(false);
                CustomDialog.showProgressDialog(getActivity());
                if (camera != null) {
                    camera.takePicture(myShutterCallback,
                            myPictureCallback_RAW, myPictureCallback_JPG);

                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
//         mCamera.startPreview();
        return noteryCameraView;
    }

    @Override
    public void onStart() {
        super.onStart();
        btnCapture.setEnabled(true);
        if(!isUploading) {
            CustomDialog.cancelProgressDialog();
        }
    }

    public void saveImage(Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String license_state1 = license_state.toLowerCase();
        imgName = license_num+license_state1+"_"+position+".png";

        File mypath = new File(directory, imgName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 50, fos);

            //fos.close();
        } catch (Exception e) {
            //Log.e("SAVE_IMAGE", e.getMessage(), e);
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
                BuildConfig.APPLICATION_ID + ".provider",mypath);

        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(activity);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                //get preview sizes
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

                //find optimal - it very important
                previewSizeOptimal = getOptimalPreviewSize(previewSizes, parameters.getPreviewSize().width,
                        parameters.getPreviewSize().height);

                //set parameters
                if (previewSizeOptimal != null) {
                    parameters.setPreviewSize(previewSizeOptimal.width, previewSizeOptimal.height);
                }

                if (camera.getParameters().getFocusMode().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                camera.setParameters(parameters);

                //rotate screen, because camera sensor usually in landscape mode
                Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                if (display.getRotation() == Surface.ROTATION_0) {
                    camera.setDisplayOrientation(90);
                } else if (display.getRotation() == Surface.ROTATION_270) {
                    camera.setDisplayOrientation(180);
                }

                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }


    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }


    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    Camera.PictureCallback myPictureCallback_RAW = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };


    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmapPicture
                    = BitmapFactory.decodeByteArray(data, 0, data.length);

            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRotation() == Surface.ROTATION_0) {

                //rotate bitmap, because camera sensor usually in landscape mode
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), matrix, true);
                //save file
                //createImageFile(rotatedBitmap);

                //calculate aspect ratio
                float koefX = (float) rotatedBitmap.getWidth() / (float) previewLayout.getWidth();
                float koefY = (float) rotatedBitmap.getHeight() / (float) previewLayout.getHeight();

                //get viewfinder border size and position on the screen
                int x1 = borderCamera.getLeft();
                int y1 = borderCamera.getTop();

                int x2 = borderCamera.getWidth();
                int y2 = borderCamera.getHeight();

                //calculate position and size for cropping
                int cropStartX = Math.round(x1 * koefX);
                int cropStartY = Math.round(y1 * koefY);

                int cropWidthX = Math.round(x2 * koefX);
                int cropHeightY = Math.round(y2 * koefY);

                //check limits and make crop
                if (cropStartX + cropWidthX <= rotatedBitmap.getWidth() && cropStartY + cropHeightY <= rotatedBitmap.getHeight()) {
                    croppedBitmap = Bitmap.createBitmap(rotatedBitmap, cropStartX, cropStartY, cropWidthX, cropHeightY);
                } else {
                    croppedBitmap = null;
                }

                //save result
                if (croppedBitmap != null) {
                    // createImageFile(croppedBitmap);

                    saveImage(croppedBitmap);
                }

            }
        }
    };

    private void uploadFile() {

        isUploading = true;
        CustomDialog.showProgressDialog(getActivity());
        addStampFlag = true;
        String retake;
        if(captureFlag){
            retake = "False";
        }else{
            retake = "True";
            imgName = stampName;
        }

        docIdType = "stamp";
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppUrl.UPLOAD_STAMP, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {

                        isUploading = false;
                        CustomDialog.cancelProgressDialog();
                        getActivity().onBackPressed();
                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
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
                params.put("userName", savedEmail);
                params.put("licenceNum",license_num);
                params.put("idDocType","stamp");
                params.put("retry",retake);
                params.put("state",license_state);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imgPreview == null) {
                    //Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(imgName, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                imgPreview.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }
    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            savedEmail =  DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == activity.RESULT_OK) {

                File docpath;
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
                if (directory.exists()) {
                    //directory.delete();
                    if (!directory.delete()){
                        // directory delete faild!
                    }
                }
                directory.mkdir();

                docpath = new File(directory,  imgName);

                FileOutputStream fos = null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), result.getUri());

                    fos = new FileOutputStream(docpath);

                    int nh = (int) ( bitmap.getHeight() * (64.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 64, nh, true);

                    scaled.compress(Bitmap.CompressFormat.PNG, 20, fos);
                    //fos.close();
                } catch (Exception e) {
                    //Log.e("SAVE_IMAGE", e.getMessage(), e);
                } finally {
                    try {
                        if (fos != null){
                            fos.close();
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }

                imgPreview.setImageURI(result.getUri());

                uploadFile();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
