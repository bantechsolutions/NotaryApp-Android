package com.notaryapp.ui.fragments.verifyauthenticate.adddoc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.theartofdev.edmodo.cropper.BuildConfig;
import com.theartofdev.edmodo.cropper.CropImage;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.adapter.VAAddDocsAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Notarize_AddDocCameraFragment extends Fragment implements SurfaceHolder.Callback {


    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private RelativeLayout btnCapture;
    private Button btnBack, btnClose,btnDone;
    private RelativeLayout confirmBtn;
    private View cameraFrontView;
    private DatabaseClient databaseClient;
    private Context context;
    private String selectedTypeRoom;
    private ConstraintLayout previewLayout;
    private View borderCamera;
    private Bitmap bitmap, croppedBitmap;
    private String[] doc_type;

    private boolean back = false;
    RecyclerView recyclerAddDocs;
    //private List<DocPics> docList;
    private List<DocumentsModel> docList;
    private GridLayoutManager layoutManager;
    private VAAddDocsAdapter vaAddDocsAdapter;

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private Camera.Size previewSizeOptimal;
    private TextView mainHead;
    private int photoi;
    Activity activity;
    private IJsonListener iJsonListener;
    private String savedEmail,transactionId,docName,serverDocName, savedImage;
  //  private String serverDocName;

    //private boolean back;
    public Notarize_AddDocCameraFragment(String docName) {
        this.docName = docName;
    }
    public Notarize_AddDocCameraFragment(String docName, String serverDocName) {
        this.docName = docName;
        this.serverDocName = serverDocName;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cameraFrontView = inflater.inflate(R.layout.fragment_notarize_add_docs_camera, container, false);


        init();
        activity = getActivity();
        buttonControls();
        setupRecycler();
        return cameraFrontView;
    }

    private void buttonControls() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();

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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(docList.size() == 0){
                    CustomDialog.notaryappDialogSingle(getActivity(),"No documents uploaded");
                }else {
                    completedDialog();
                }
            }
        });
    }
    private void completedDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Have all the files been uploaded?");
        Button dialogButton = (Button) dialog.findViewById(R.id.btnNo);
        dialogButton.setText("NO");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("YES");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // DeleteImages().execute();
                dialog.dismiss();
                docList.clear();
                addDocComplete();
            }
        });
        dialog.show();
    }
    private void setupRecycler() {
        try {
            new GetImageList().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if(docList.size()>0) {
           // docList.remove(0);
            vaAddDocsAdapter = new VAAddDocsAdapter(docList, transactionId, getActivity().getApplicationContext(),getActivity());
            layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            recyclerAddDocs.setLayoutManager(layoutManager);
            recyclerAddDocs.setAdapter(vaAddDocsAdapter);
        }
    }
    private void init() {
        // confirmBtn = cameraFrontView.findViewById(R.id.btnCapture);
        initIJsonListener();
        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        docList = new ArrayList<>();
        recyclerAddDocs = cameraFrontView.findViewById(R.id.recyclerView);
        btnCapture = cameraFrontView.findViewById(R.id.btnCapture);
        btnBack = cameraFrontView.findViewById(R.id.btn_camera_back);
        btnClose = cameraFrontView.findViewById(R.id.btn_camera_close);
        btnDone = cameraFrontView.findViewById(R.id.doneBtn);
        mainHead = cameraFrontView.findViewById(R.id.mainHead);
        previewLayout = cameraFrontView.findViewById(R.id.preview_layout);
        borderCamera = cameraFrontView.findViewById(R.id.border_camera);

        doc_type = getResources().getStringArray(R.array.identityType);
        surfaceView = cameraFrontView.findViewById(R.id.camera_preview_surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       // btnDone.setEnabled(false);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), REF_VIEW_CONTAINER, fragment, true);
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
//            loadFragment(new Notarize_AddDocPreviewFragment(docName,serverDocName, transactionId));
        }
    };

    private void saveImage(Bitmap myBitmap) {
        File docpath;
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        savedImage = "va_" + getRandomNumber()+".png";
        docpath = new File(directory,  savedImage);
        /*FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(docpath);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 90, fos);
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
        }*/

        Uri imgUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",docpath);
            CustomDialog.cancelProgressDialog();
        /*CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);*/
        loadFragment(new Notarize_AddDocPreviewFragment(savedImage, imgUri,docName,serverDocName, transactionId));

    }

    class GetImageList extends AsyncTask<Void, Void, List<DocumentsModel>> {

        @Override
        protected List<DocumentsModel> doInBackground(Void... voids) {
           // docList = databaseClient.getAppDatabase().docPicsDao().getDocPics(docName);
            docList = databaseClient.getAppDatabase().documentsImageDao().getFilteredDocs(serverDocName);
            return docList;
        }

        @Override
        protected void onPostExecute(List<DocumentsModel> docs) {
            super.onPostExecute(docs);

        }
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

    private void addDocComplete() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {

                params.put("tranid", transactionId);
                params.put("docName", serverDocName);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.ADD_DOC_COMP,"addDocComp");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
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
                            if(type.equals("addDocComp")) {
                                    getActivity().finish();
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
                    //e.printStackTrace();
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
    }

    public static int getRandomNumber(){

        //Random rand = new Random();
        SecureRandom rand = new SecureRandom();
        int rand_int1 = rand.nextInt(100000);

        return rand_int1;

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

                /*FileOutputStream fos = null;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), result.getUri());

                    fos = new FileOutputStream(docpath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
//                    Log.e("SAVE_IMAGE", e.getMessage(), e);
                }*/

                //loadFragment(new Notarize_AddDocPreviewFragment(savedImage, result.getUri(),docName,serverDocName, transactionId));

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}