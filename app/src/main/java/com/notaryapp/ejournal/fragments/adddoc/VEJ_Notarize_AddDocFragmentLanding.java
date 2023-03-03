package com.notaryapp.ejournal.fragments.adddoc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.labters.documentscanner.ImageCropActivity;
import com.labters.documentscanner.helpers.ScannerConstants;
import com.theartofdev.edmodo.cropper.CropImage;
import com.notaryapp.R;
import com.notaryapp.ejournal.adapter.VEJ_VAAddDocsAdapter;
import com.notaryapp.model.webresponse.TransByPage;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utilsretrofit.GetDataService;
import com.notaryapp.utilsretrofit.RetrofitClientInstance;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class VEJ_Notarize_AddDocFragmentLanding extends Fragment /*implements SurfaceHolder.Callback*/ {

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private RelativeLayout btnCapture;
    private Button btnBack, btnClose, btnDone;
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
    CardView cvInstruction;
    //private List<DocPics> docList;
    private List<DocumentsModel> docList;
    private GridLayoutManager layoutManager;
    private VEJ_VAAddDocsAdapter vaAddDocsAdapter;
    DocumentsModel documentsModel;
    private Camera camera;
    //private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private Camera.Size previewSizeOptimal;
    private TextView mainHead;
    private int photoi;
    Activity activity;
    private IJsonListener iJsonListener;
    private String savedEmail, transactionId, docName, serverDocName, savedImage;
    //  private String serverDocName;

    private String userEmail;
    private static final int REQUEST_CODE = 99;

    //private boolean back;
    public VEJ_Notarize_AddDocFragmentLanding(String docName) {
        this.docName = docName;
    }

    public VEJ_Notarize_AddDocFragmentLanding(String docName, String serverDocName) {
        this.docName = docName;
        this.serverDocName = serverDocName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cameraFrontView = inflater.inflate(R.layout.fragment_notarize_add_docs_landing, container, false);
        init();
        activity = getActivity();
        buttonControls();
        //setupRecycler();
        return cameraFrontView;
    }

    private void buttonControls() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
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
                if (docList.size() == 0) {
                    CustomDialog.notaryappDialogSingle(getActivity(), "No documents uploaded");
                } else {
                    completedDialog();
                }
            }
        });
    }


    private File photoFile = null;

    public void startScan() {
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        //Intent cameraIntent = new Intent("MediaStore.ACTION_IMAGE_CAPTURE");
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException var6) {
                Log.i("Main", "IOException");
            }

            if (photoFile != null) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                cameraIntent.putExtra("output", (Parcelable) Uri.fromFile(photoFile));
                getActivity().startActivityForResult(cameraIntent, 1231);
            }
        }
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
                //addDocCompleted();
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
        /*if (docList.size() > 0) {
            // docList.remove(0);
            cvInstruction.setVisibility(View.GONE);
            vaAddDocsAdapter = new VAAddDocsAdapter(docList, transactionId, getActivity().getApplicationContext());
            layoutManager = new GridLayoutManager(getActivity(), 3);
            recyclerAddDocs.setLayoutManager(layoutManager);
            recyclerAddDocs.setAdapter(vaAddDocsAdapter);
        } else {
            cvInstruction.setVisibility(View.GONE);
           // btnCapture.performClick();
        }*/
    }

    private void init() {
        // confirmBtn = cameraFrontView.findViewById(R.id.btnCapture);
        initIJsonListener();
        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        new SelectEmail().execute();
        docList = new ArrayList<>();
        recyclerAddDocs = cameraFrontView.findViewById(R.id.recyclerView);
        btnCapture = cameraFrontView.findViewById(R.id.btnCapture);
        btnBack = cameraFrontView.findViewById(R.id.btn_camera_back);
        btnClose = cameraFrontView.findViewById(R.id.btn_camera_close);
        btnDone = cameraFrontView.findViewById(R.id.doneBtn);
        mainHead = cameraFrontView.findViewById(R.id.mainHead);
        previewLayout = cameraFrontView.findViewById(R.id.preview_layout);
        borderCamera = cameraFrontView.findViewById(R.id.border_camera);
        cvInstruction = cameraFrontView.findViewById(R.id.cvInstruction);
        doc_type = getResources().getStringArray(R.array.identityType);
        loadImage();
        setupRecycler();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), REF_VIEW_CONTAINER, fragment, true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            if (docList == null
                    || !(docList.size() > 0)) {
                btnCapture.performClick();
            } else {
                if (docList.size() > 0) {
                    // docList.remove(0);
                    cvInstruction.setVisibility(View.GONE);
                    vaAddDocsAdapter = new VEJ_VAAddDocsAdapter(docList, transactionId, getActivity().getApplicationContext(),getActivity());
                    layoutManager = new GridLayoutManager(getActivity(), 3);
                    recyclerAddDocs.setLayoutManager(layoutManager);
                    recyclerAddDocs.setAdapter(vaAddDocsAdapter);
                } else {
                    cvInstruction.setVisibility(View.GONE);
                    // btnCapture.performClick();
                }
            }

        }
    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            savedEmail = databaseClient.getAppDatabase()
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
            
            // postapiRequest.request(getActivity(), iJsonListener, params, url, "docSigned");
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.ADD_DOC_COMP, "addDocComp");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("addDocComp")) {
                            getActivity().finish();
                        }

                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(), "Error! No data fetched from server");
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //  CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                Log.v("msg",msg);
                // RequestQueueService.showAlert(msg,getActivity());
                // CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    public static int getRandomNumber() {

        //Random rand = new Random();
        SecureRandom rand = new SecureRandom();
        int rand_int1 = rand.nextInt(100000);

        return rand_int1;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("RETURN_DATA", String.valueOf(data));
        //bitmap = (Bitmap)data.getExtras().get("data");
        //Log.d("RETURN_DATA_BIT", bitmap.toString());
        if (requestCode == 1111 && resultCode == -1 && data != null) {
            Uri selectedImage = data.getData();
            Bitmap btimap = (Bitmap) null;

            try {
                InputStream var15;
                if (selectedImage != null) {
                    boolean var8 = false;
                    boolean var9 = false;
                    //int var11 = false;
                    var15 = getActivity().getContentResolver().openInputStream(selectedImage);
                } else {
                    var15 = null;
                }

                InputStream inputStream = var15;
                btimap = BitmapFactory.decodeStream(inputStream);
                ScannerConstants.selectedImageBitmap = btimap;
                this.startActivityForResult(new Intent(getActivity(), ImageCropActivity.class), 1234);
            } catch (Exception var12) {
                //var12.printStackTrace();
            }
        } else if (requestCode == 1231 && resultCode == -1) {
            ScannerConstants.selectedImageBitmap = uriToBitmap(getActivity(), Uri.fromFile(photoFile));
            if (photoFile.exists() && photoFile.isFile() && photoFile.canWrite()) {
                //photoFile.delete();
                if(!photoFile.delete()){
                    Log.d("FILE_DELETE", "DELETED");
                }

            }
            this.startActivityForResult(new Intent(getActivity(), ImageCropActivity.class), 1234);

        } else if (requestCode == 1234 && resultCode == -1) {
            if (ScannerConstants.selectedImageBitmap != null) {
                CustomDialog.showProgressDialog(getActivity());
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == activity.RESULT_OK) {
                    File docpath;
                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    savedImage = "va_" + getRandomNumber() + ".png";
                    docpath = new File(directory, savedImage);
                    FileOutputStream fos = null;
                    try {
                        //Uri imageUri = (Uri) data.getExtras().get("scannedResult");
                        bitmap = ScannerConstants.selectedImageBitmap;

//                        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
//                        Canvas canvas = new Canvas(newBitmap);
//                        canvas.drawColor(Color.WHITE);
//                        canvas.drawBitmap(bitmap, 0, 0, null);
//                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                        newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
//                        bitmap = newBitmap;
                        //docpath = Utils.saveBitmapToFile(docpath);
                        fos = new FileOutputStream(docpath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                        //bitmap = Utils.resizeImage(bitmap);
                        //bitmap = ImageUtils.getInstant().getCompressedBitmap(docpath.getAbsolutePath());
                        fos.close();
                        loadImage();
                    } catch (Exception e) {
                        Log.v("test", e.toString());
                    }
                    uploadFile();
                }
            } else {
                Toast.makeText(getActivity(), (CharSequence) "Not OK", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile() {
        //final String token = "Bearer " + PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
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
                    } else {
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tranId", transactionId);
                params.put("docName", serverDocName);
                params.put("userName", userEmail);
                params.put("photoName", savedImage);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // if (imageView == null) {
//                    Log.i(TAG, "avatar null");
                // }
                params.put("file", new DataPart(savedImage, VolleyHelper
                        .getFileDataFromDrawable(getActivity(),
                                new BitmapDrawable(getResources(), bitmap)), "image/jpg"));
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
            setupRecycler();
        }

    }

    private void loadImage() {
        //imageView.setImageURI(imageUri);
        documentsModel = new DocumentsModel(docName, savedImage, serverDocName);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "veri_" + timeStamp + ".jpg";
        //File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), imageFileName);
        return photo;
    }

    public Bitmap uriToBitmap(Context c, Uri uri) {
        if (c == null && uri == null) {
            return null;
        }
        try {

            Uri selectedImageUri = Uri.fromFile(new File(uri.getPath()));
            return handleSamplingAndRotationBitmap(c, selectedImageUri);
            //return MediaStore.Images.Media.getBitmap(c.getContentResolver(), Uri.fromFile(new File(uri.getPath())));
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public void addDocCompleted() throws JSONException {
        try {
            final String token = AppUrl.Authorization_Key;
            GetDataService service = RetrofitClientInstance.getRetrofitInstanceForMasterData()
                    .create(GetDataService.class);
            JsonObject params = new JsonObject();
            try {
                params.addProperty("tranid", transactionId);
                params.addProperty("docName", serverDocName);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            Call<ResponseBody> call = service.getImageUpload(token, params);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       final retrofit2.Response<ResponseBody> response) {
                    if (response != null && response.body() != null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(final Void... params) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<TransByPage>() {
                                }.getType();
//                                try {
////                                    transByPage = gson.fromJson(response
////                                            .body().string().trim(), type);
////                                    if (transByPage != null
////                                            && transByPage.getTransactions() != null
////                                            && transByPage.getTransactions().getBody() != null) {
////                                        dashItemsAllList.addAll(transByPage.getTransactions().getBody());
////                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(final Void result) {
                            }
                        }.execute();

                    }
                }


                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("test", t.toString());
                }

            });
        } catch (Exception e11) {
            Log.v("test", e11.toString());
        }
    }
    private static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImageUri) throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImageUri);
        return img;
    }
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height * 1f;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2f;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}