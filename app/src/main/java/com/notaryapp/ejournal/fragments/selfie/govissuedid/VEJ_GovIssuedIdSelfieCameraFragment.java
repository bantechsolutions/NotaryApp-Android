package com.notaryapp.ejournal.fragments.selfie.govissuedid;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_NotarizeStepsActivity;
import com.notaryapp.ejournal.activities.VEJ_VerifySignerActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class VEJ_GovIssuedIdSelfieCameraFragment extends Fragment implements SurfaceHolder.Callback {

    private RelativeLayout btnCapture;
    private Button btnBack, btnClose;
    private RelativeLayout confirmBtn;
    private View cameraFrontView;
    private DatabaseClient databaseClient;
    private Context context;
    private String selectedTypeRoom, savedImage;
    private ConstraintLayout previewLayout;
    private View borderCamera;
    //public static Bitmap bitmap, croppedBitmap;
    private Bitmap bitmap, croppedBitmap;


    private String[] doc_type;

    private boolean back = false;
    private boolean permission = false,flag=false;

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private Camera.Size previewSizeOptimal;
    private TextView mainHead;
    Activity activity;
    //private boolean back;
    public VEJ_GovIssuedIdSelfieCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cameraFrontView = inflater.inflate(R.layout.fragment_gov_camera, container, false);

        init();
        activity = getActivity();
        if (!permission){
            callCustomDialog();
            flag = true;
            //  CustomDialog.notaryappToastDialog(SelectIdentityActivity.this,"Permissions not granted");
            // Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
        }

        if (((VEJ_GovIssuedIdAddSelfieActivity)getActivity()).idType.equalsIgnoreCase("manually")){
            btnBack.setVisibility(View.VISIBLE);
        } else {
            btnBack.setVisibility(View.GONE);
        }
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Log.d("CAMERA_PAGE", "onClick: ");
                //getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalAlertFragment());
                //Log.d("CAMERA_PAGE", "onClick: ");


                Intent intent = new Intent(getActivity(), VEJ_NotarizeStepsActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);



            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalAlertFragment());
            }
        });

        return cameraFrontView;
    }
    private void callCustomDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Permissions are not granted");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            permission = true;

        }else {
            // Do something, when permissions are already granted
            permission = false;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(flag)
            getActivity().finish();
    }
    private void init() {
        // confirmBtn = cameraFrontView.findViewById(R.id.btnCapture);

        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
       // new GetSelectedItem().execute();

        btnCapture = cameraFrontView.findViewById(R.id.btnCapture);
        btnBack = cameraFrontView.findViewById(R.id.btn_camera_back);
        btnClose = cameraFrontView.findViewById(R.id.btn_camera_close);
        mainHead = cameraFrontView.findViewById(R.id.mainHead);
        previewLayout = cameraFrontView.findViewById(R.id.preview_layout);
        borderCamera = cameraFrontView.findViewById(R.id.border_camera);

        doc_type = getResources().getStringArray(R.array.identityType);
        surfaceView = cameraFrontView.findViewById(R.id.camera_preview_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        checkCameraPermission();

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VEJ_VerifySignerActivity.REF_VIEW_CONTAINER, fragment, true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                if (display.getRotation() == Surface.ROTATION_0) {
                    camera.setDisplayOrientation(90);
                } else if (display.getRotation() == Surface.ROTATION_270) {
                    camera.setDisplayOrientation(180);
                }

                //write some info
                int x1 = previewLayout.getWidth();
                int y1 = previewLayout.getHeight();

                int x2 = borderCamera.getWidth();
                int y2 = borderCamera.getHeight();

                String info = "Preview width:" + String.valueOf(x1) + "\n" + "Preview height:" + String.valueOf(y1) + "\n" +
                        "Border width:" + String.valueOf(x2) + "\n" + "Border height:" + String.valueOf(y2);
                //  resBorderSizeTV.setText(info);

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


            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            if (display.getRotation() == Surface.ROTATION_0) {

                //rotate bitmap, because camera sensor usually in landscape mode
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapPicture, 0, 0, bitmapPicture.getWidth(), bitmapPicture.getHeight(), matrix, true);
                //save file
                //  createImageFile(rotatedBitmap);

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
                    //  createImageFile(croppedBitmap);
                }

            } else if (display.getRotation() == Surface.ROTATION_270) {
                // for Landscape mode
            }


            saveImage(croppedBitmap);
//            loadFragment(new Notarize_SignerPersonalPreviewFragment());
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        CustomDialog.cancelProgressDialog();
    }

    private void saveImage(Bitmap myBitmap) {
        long millis=System.currentTimeMillis();
        savedImage = "signer_"+millis+".png";

        File docpath;
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        docpath = new File(directory,  savedImage);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(docpath);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();
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
        // Uri imgUri = Uri.fromFile(docpath);
        Uri imgUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",docpath);

        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(activity);
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
                if (!directory.exists()) {
                    directory.mkdir();
                }

                docpath = new File(directory,  savedImage);

                FileOutputStream fos = null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), result.getUri());

                    fos = new FileOutputStream(docpath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 40, fos);
                    fos.close();
                } catch (Exception e) {
//                    Log.e("SAVE_IMAGE", e.getMessage(), e);
                }

                loadFragment(new VEJ_GovIssuedIdSelfiePreviewFragment(result.getUri(), savedImage));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}