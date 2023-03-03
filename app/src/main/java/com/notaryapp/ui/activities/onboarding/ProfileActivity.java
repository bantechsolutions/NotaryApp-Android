
package com.notaryapp.ui.activities.onboarding;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jumio.core.enums.JumioDataCenter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.components.ScrollViewExt;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


public class ProfileActivity extends BaseActivity {//implements NestedScrollViewListener, Animation.AnimationListener {

    private final String TAG="ProfileFragment";

    public static final int PICK_IMAGE = 1;
    private Animation animMoveUp;
    private ScrollViewExt scrollView;
    private LinearLayout footerbtn;
    private Button imgInfo1, imgInfo2;
    private Button btnQuit,btnClose,btnBack;
    private ScrollViewExt  previewScroll;
    private View profileView;
    private TextView txtAddLicense;
    private RelativeLayout txtupdateProfile;
    private RelativeLayout relative_add_license;
    private CircleImageView imgSelfie;
    private DatabaseClient databaseClient;
    private Context context;
    private UserReg userReg;
    private Bitmap bitmap;
    private IJsonListener iJsonListener;
    private TextInputEditText profileEdit_LastName;
    private TextInputEditText profileEdit_Email;
    private TextInputEditText profileEdit_PhoneNo;
    private TextInputEditText profileEdit_address1;
    private TextInputEditText profileEdit_address2;
    private TextInputEditText profileEdit_FirstName;
    private TextInputEditText profileEdit_company;
    private TextInputEditText profileEdit_city;
    private TextInputEditText profileEdit_state;
    private TextInputEditText profileEdit_zip;
    private String firstName, lastName, email, phone,company,address1,address2,city,state ="",zip,stateCode;
    private boolean validFName, validLName, validEmail, validPhone,validCompany, validAddress1,validAddress2,validCity,validState,validZip;    private TextInputLayout textInputLayoutFName,textInputLayoutLName,textInputLayoutEmail,
            textInputLayoutPhone,textInputLayoutCompany,textInputLayoutAddress1,textInputLayoutAddress2,
            textInputLayoutCity,textInputLayoutState,textInputLayoutZip;

    private String jumioFirstName = "", jumioLastName = "",jumioScanRef="", selfieImage = "";

    //Intent intent;
    private String scanRef;
    private JumioScanDetails scanDetails;
    private Info info;

    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String downloadUrl;
    private int randomForImg;
    private String proImageName;
    private Spinner spinnerState;
    private List<String> stateList;
    //private String firstName,lastName;
    public ProfileActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();

            }
        }.start();
        setTimer();
        /*time out*/

        init();

        relative_add_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Validation.isFirstName(profileEdit_FirstName,textInputLayoutFName,true)){
                    textInputLayoutFName.setError("Enter FirstName");
                }else{
                    validFName = true;
                }
                if(!Validation.isLastName(profileEdit_LastName,textInputLayoutLName,true)){
                    textInputLayoutLName.setError("Enter LastName");
                }else{
                    validLName = true;
                }
                if(!Validation.hasEmailText(profileEdit_Email,textInputLayoutLName)){
                    textInputLayoutEmail.setError("Enter Email");
                }else{
                    validEmail = true;
                }
                if(!Validation.hasPhoneText(profileEdit_PhoneNo,textInputLayoutPhone)){
                    textInputLayoutPhone.setError("Enter Phone");
                }else{
                    validPhone = true;
                }
                if(!Validation.hasValue(profileEdit_company,textInputLayoutCompany)){
                    textInputLayoutCompany.setError("Enter Company");
                }else{
                    validCompany = true;
                }
                if(!Validation.hasValue(profileEdit_address1,textInputLayoutAddress1)){
                    textInputLayoutAddress1.setError("Enter address 1");
                }else{
                    validAddress1 = true;
                }
//                if(Validation.hasValue(profileEdit_address2,textInputLayoutAddress2)){
//                    validAddress2=true;
//
//                }else {
//                    textInputLayoutAddress2.setError("Enter address 2");
//                }
                if(Validation.hasValue(profileEdit_city,textInputLayoutCity)){
                    validCity=true;

                }else {
                    textInputLayoutCity.setError("Enter city");
                }
                if (state.equals("Select State")) {
                    //    Toast.makeText(getActivity(), , Toast.LENGTH_LONG).show();
                    validState = false;
                    CustomDialog.notaryappDialogSingle(ProfileActivity.this, "Please select your state");
                } else if(state.equals("null")){
                    validState = false;
                    CustomDialog.notaryappDialogSingle(ProfileActivity.this, "Please select your state");
                }else{
                    validState = true;
                }
                if(Validation.hasValue(profileEdit_zip,textInputLayoutZip)){
                    validZip=true;

                }else {
                    textInputLayoutZip.setError("Enter zip code");
                }

                if(validFName && validLName && validEmail && validPhone && validCompany &&
                        validAddress1 && validCity && validState && validZip ){
                    getData();

                    callUpdateProfileApi();
//                    loadFragment(new Validate_ProcessFragment()) ;

                }else{
                    validFName = Validation.hasFirstText(profileEdit_FirstName);
                     if(!validFName) {
                    textInputLayoutFName.setError("Enter FirstName");
                }
                }
            }
        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
                new GetStateCode().execute();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        profileEdit_FirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(profileEdit_FirstName,textInputLayoutFName)){
                    validFName = Validation.isFirstName(profileEdit_FirstName,textInputLayoutFName ,true);
                }
            }
        });
        profileEdit_LastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(profileEdit_LastName,textInputLayoutLName)){
                    validLName = Validation.isLastName(profileEdit_LastName,textInputLayoutLName, true);//hasText();
                }
            }
        });
        profileEdit_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_Email,textInputLayoutEmail)) {
                    validEmail = Validation.isEmailAddress(profileEdit_Email, textInputLayoutEmail, true);
                }else{

                }
            }
        });
        profileEdit_PhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_PhoneNo,textInputLayoutPhone)) {
                    validPhone = Validation.isPhoneNumber(profileEdit_PhoneNo, textInputLayoutPhone, true);
                } else {

                }
            }
        });
        profileEdit_company.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_company,textInputLayoutCompany)) {
                    validCompany = Validation.isFirstName(profileEdit_company, textInputLayoutCompany, true);
                } else {

                }
            }
        });
        profileEdit_address1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_address1,textInputLayoutAddress1)) {
                    validAddress1 = true;
                } else {

                }
            }
        });
        profileEdit_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_city,textInputLayoutCity)) {
                    validCity = true;
                } else {

                }
            }
        });
       /* profileEdit_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_state,textInputLayoutState)) {
                    validState = true;
                } else {

                }
            }
        });*/
        profileEdit_zip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_zip,textInputLayoutZip)) {
                    validZip = true;
                } else {

                }
            }
        });

//        profileEdit_PhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminateDialog();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminateDialog();
            }
        });

         txtupdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(ProfileActivity.this,info.getProfile());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(ProfileActivity.this,info.getProfileLicenses());
            }
        });
    }
    private void terminateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Do you want to logout?");

        Button dialogButton = (Button) dialog.findViewById(R.id.btnNo);
        dialogButton.setText("YES");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ProfileActivity.this, OnboardingBaseActivity.class));
                overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
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
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                // Get the path from the Uri
                final String path = getPathFromURI(selectedImageUri);
                if (path != null) {
                    File f = new File(path);
                    selectedImageUri = Uri.fromFile(f);
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    saveNewSelfie(proImageName,bitmap);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
//    private void loadFragment(Fragment fragment) {
//        FragmentViewUtil.replaceFragment(getActivity(), ProfileBaseActivity.REF_VIEW_CONTAINER,
//                fragment,true);
//    }

    private void init(){
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            jumioScanRef = extras.getString("ScanRef");
            jumioFirstName= extras.getString("FirstName");
            jumioLastName= extras.getString("LastName");
            selfieImage = extras.getString("image");
            //Log.e("ScanRefffffff",jumioScanRef);
            //Log.e("ScanRefffffff",jumioFirstName);
            //Log.e("ScanRefffffff",jumioLastName);
        }
        context = this;
        stateList = new ArrayList<>();
        databaseClient = DatabaseClient.getInstance(context);
        initIJsonListener();
        userReg = new UserReg();
        new GetStates().execute();
        new UserDetails().execute();

        new GeInfo().execute();

        randomForImg = Utils.getRandomNumber();
        proImageName = randomForImg+".png";
        scrollView =findViewById(R.id.scrollView);
        footerbtn =findViewById(R.id.footerbtn);
        btnQuit =findViewById(R.id.btnQuit);
        btnBack =findViewById(R.id.btn_pro_back);
        btnClose =findViewById(R.id.btn_pro_close);
        imgSelfie =findViewById(R.id.img_profile);
        txtupdateProfile =findViewById(R.id.updateProfile);
        relative_add_license =findViewById(R.id.relative_add_license);
        imgInfo1 = findViewById(R.id.btnProfileInfo);
        imgInfo2 = findViewById(R.id.btnNotaryInfo);
        previewScroll = findViewById(R.id.scrollView);
        spinnerState = findViewById(R.id.stateSpinner);

        profileEdit_FirstName =findViewById(R.id.firstName);
        profileEdit_LastName =findViewById(R.id.lastName);
        profileEdit_Email =findViewById(R.id.email);
        profileEdit_PhoneNo =findViewById(R.id.phoneNo);
        profileEdit_company =findViewById(R.id.company);
        profileEdit_address1 =findViewById(R.id.addressLine1);
        profileEdit_address2 =findViewById(R.id.addressLine2);
        profileEdit_city = findViewById(R.id.cityEditTxt);
       // profileEdit_state =findViewById(R.id.stateEditTxt);
        profileEdit_zip = findViewById(R.id.zipEditTxt);

        textInputLayoutFName =findViewById(R.id.firstNameText);
        textInputLayoutLName =findViewById(R.id.lastNameText);
        textInputLayoutEmail =findViewById(R.id.emailText);
        textInputLayoutPhone =findViewById(R.id.phoneText);
        textInputLayoutCompany =findViewById(R.id.companyText);
        textInputLayoutAddress1 =findViewById(R.id.address1Text);
        textInputLayoutAddress2 =findViewById(R.id.address2Text);
        textInputLayoutCity =findViewById(R.id.cityText);
        textInputLayoutState =findViewById(R.id.stateText);
        textInputLayoutZip =findViewById(R.id.zipText);


    }
    private void downloadProfile(String url){
        //scanRefference = "1ccecf1f-8ebc-4b53-ae3c-63a807899d01";
        CustomDialog.showProgressDialog(this);
       // String url = AppUrl.JUMIO_BASE_URL +scanRefference+"/images/face";
      //  https://netverify.com/api/netverify/v2/scans/e724747b-9097-443e-a772-493f95de1047/images/face
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator()
                {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException
                    {
                        String credential = Credentials.basic(apiToken, apiSecret);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .header("Accept","image/jpeg , image/png")
                                .header("User-Agentvalue","Veritale Data Solutions ,Inc. notaryapp/1.0")
                                .build();
                       // response.body().byteStream().read();
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(url)
                .centerCrop()
                .resize(180,180)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgSelfie);

        CustomDialog.cancelProgressDialog();
    picasso.load(url)
            .centerCrop()
            .resize(180,180)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        saveNewSelfie(proImageName,bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
});
    }
    private void getData(){
        try {
            firstName = profileEdit_FirstName.getText().toString().trim();
            lastName = profileEdit_LastName.getText().toString().trim();
            email = profileEdit_Email.getText().toString().trim();
            phone = profileEdit_PhoneNo.getText().toString().trim();
            address1 = profileEdit_address1.getText().toString().trim();
            address2 = profileEdit_address2.getText().toString().trim();
            company = profileEdit_company.getText().toString().trim();
            city = profileEdit_city.getText().toString().trim();
          //  state = profileEdit_state.getText().toString().trim();
            zip = profileEdit_zip.getText().toString().trim();

            userReg.setFirstName(firstName);
            userReg.setLastName(lastName);
            userReg.setEmail(email);
            userReg.setPhoneNo(phone);
            userReg.setAddress(address1);
            userReg.setAddress(address2);
            userReg.setCompany(company);
            userReg.setCity(city);
            userReg.setState(state);
           // userReg.setState(stateCode);
            userReg.setZip(zip);
        }catch (Exception e){
            //Log.e(TAG,e.getMessage());
        }
    }

    public Uri saveNewSelfie(String name,Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

      //  newProfileImg = "selfie_new.png";

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
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
        // Uri imgUri = Uri.fromFile(docpath);
        Uri imgUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",selfie_new);
        String path = selfie_new.getAbsolutePath();
        imgSelfie.setImageURI(imgUri);
        uploadProfile();
        return imgUri;
    }
    public static boolean checkSelfie(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);

        return image.exists();

    }
    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }

    private void uploadProfile() {

        CustomDialog.showProgressDialog(this);
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, AppUrl.UPLOAD_NOTARY_DP, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();

                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                      //  CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
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
                params.put("userName",email);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imgSelfie == null) {
                    //Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(proImageName, VolleyHelper
                        .getFileDataFromDrawable(getApplicationContext(),
                                imgSelfie.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(this).addToRequestQueue(multipartRequest);
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
                        if(type.equals("updateProfile")) {
                            String success = data.getString("success");
                            if (success.equals("1111")) {
                                CustomDialog.cancelProgressDialog();
                                new UpdateUserDetails().execute();
                                startActivity(new Intent(ProfileActivity.this,ProfileBaseActivity.class));
                                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
                              //  loadFragment(new ProfileBase_AddLicenseFragment());
                            } else {

                            }
                        }else if(type.equals("getProfile")){
                           // String success = data.getString("success");

                            JSONArray jArray =  data.getJSONArray("success");
                            if(jArray.length()!=0) {
                                JSONObject json_inside = jArray.getJSONObject(0);
                                company = json_inside.getString("company");
                                address1 = json_inside.getString("address1");
                                address2 = json_inside.getString("address2");
                                String fName = json_inside.getString("first_name").toLowerCase();
                                String lName = json_inside.getString("last_name").toLowerCase();
                               // String fName1 = Utils.capitalizeFirst(fName);
                                //Log.e("TAGGGG",fName + Utils.capitalizeFirst(fName));

                                profileEdit_FirstName.setText(Utils.capitalizeFirst(fName));
                                profileEdit_LastName.setText(Utils.capitalizeFirst(lName));
                                profileEdit_Email.setText(json_inside.getString("username"));

                                //Formatting phone number and removing
                                String phoneFormatted = "";
                                String formatPhone = json_inside.getString("phone");
                                int phoneLength = json_inside.getString("phone").length();
                                if (formatPhone.substring(0, 1).equals("+")){
                                    if(formatPhone.substring(0, 3).equals("+91")){
                                        phoneFormatted = formatPhone.substring(3,phoneLength);
                                    }
                                    if(formatPhone.substring(0, 2).equals("+1")){
                                        phoneFormatted = formatPhone.substring(2,phoneLength);
                                    }
                                }else{
                                    phoneFormatted = formatPhone;
                                }
                                phoneFormatted = phoneFormatted.replace(" ", "");


                                profileEdit_PhoneNo.setText(phoneFormatted);
                                city = json_inside.getString("city");
                                state = json_inside.getString("state");
                                zip = json_inside.getString("zip");
//                                profileEdit_city.setText(json_inside.getString("city"));
//                                profileEdit_state.setText(json_inside.getString("state"));
//                                profileEdit_zip.setText(json_inside.getString("zip"));
                                if(company.equals("null")){
                                    profileEdit_company.setText("");

                                }else {
                                    profileEdit_company.setText(json_inside.getString("company"));
                                }
                                if(address1.equalsIgnoreCase("null")){
                                    profileEdit_address1.setText("");
                                }else {
                                    profileEdit_address1.setText(json_inside.getString("address1"));
                                }
                                if(address2.equalsIgnoreCase("null")){
                                    profileEdit_address2.setText("");
                                }else {
                                    profileEdit_address2.setText(json_inside.getString("address2"));
                                }
                                if(city.equalsIgnoreCase("null")){
                                    profileEdit_city.setText("");
                                }else {
                                    profileEdit_city.setText(json_inside.getString("city"));
                                }
                                spinnerState.setSelection(stateList.indexOf(state));
                                /*if(state == "null"){
                                    profileEdit_state.setText("");
                                }else {
                                    profileEdit_state.setText(json_inside.getString("state"));
                                }*/
                                if(zip.equalsIgnoreCase("null")){
                                    profileEdit_zip.setText("");
                                }else {
                                    profileEdit_zip.setText(json_inside.getString("zip"));
                                }
                                profileEdit_Email.setFocusable(false);
                                profileEdit_Email.setClickable(false);
                                profileEdit_Email.setEnabled(false);
                                if(jumioFirstName.equals("") && jumioLastName.equals("")){
                                    String fName1 = scanDetails.getFirstName().toLowerCase();
                                    String lName1 = scanDetails.getLastName().toLowerCase();
                                    profileEdit_FirstName.setText(Utils.capitalizeFirst(fName1));
                                    profileEdit_LastName.setText(Utils.capitalizeFirst(lName1));
                                }else{
                                    String fName1 = jumioFirstName.toLowerCase();
                                    String lName1 = jumioLastName.toLowerCase();
                                    profileEdit_FirstName.setText(Utils.capitalizeFirst(fName1));
                                    profileEdit_LastName.setText(Utils.capitalizeFirst(lName1));
                                }
                                // new SelectData().execute();
                                //downloadUrl = AppUrl.JUMIO_BASE_URL + jumioScanRef + "/images/face";
                                //Log.e("SelectData",jumioScanRef);
                                //downloadProfile(downloadUrl);

                                //Sourav 20201210
                                setImage(selfieImage);
                            }
                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(ProfileActivity.this, Utils.errorMessage(ProfileActivity.this));
                    //e.printStackTrace();
                 }
            }

            @Override
            public void onFetchFailure(String msg) {

            }

            @Override
            public void onFetchStart() {

            }
        };
    }
    class UpdateUserDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase().userRegDao().updateUser(userReg);

            return "";
        }

        @Override
        protected void onPostExecute(String userReg) {
            super.onPostExecute(userReg);
         //   CustomDialog.notaryappToastDialog(ProfileActivity.this,"Profile Updated successfully");
        }
    }
    class GetStates extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            stateList =  databaseClient.getAppDatabase().statesDao().getStateName();
            stateList.add(0,"Select State");
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(ProfileActivity.this, R.layout.spinner_text_view, stateList);
            spinnerState.setAdapter(stateAdapter);
        }
    }
    class GetStateCode extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            stateCode =  databaseClient.getAppDatabase().statesDao().getStateCode(state);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private void  callUpdateProfileApi(){
        CustomDialog.showProgressDialog(this);
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("first_name",firstName);
                params.put("last_name",lastName);
                params.put("phone",phone.replace(" ", "")
                        .replace("(", "")
                        .replace(")","").replace("-",""));
                params.put("username",email);
                params.put("address1",address1);
                params.put("address2",address2);
                params.put("company",company);
                params.put("city",city);
                params.put("state",state);
               // params.put("state",stateCode);
                params.put("zip",zip);

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(this,iJsonListener,params, AppUrl.UPDATE_PROFILE,"updateProfile");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void  callGetProfileApi(){
        CustomDialog.showProgressDialog(this);
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("userName",email);

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(this,iJsonListener,params, AppUrl.GET_PROFILE,"getProfile");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    class UserDetails extends AsyncTask<Void, Void, UserReg> {

        @Override
        protected UserReg doInBackground(Void... voids) {
            userReg = databaseClient.getAppDatabase().userRegDao().getUser();
          //  String email = userReg.getEmail();
            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            return userReg;
        }

        @Override
        protected void onPostExecute(UserReg userReg) {
            super.onPostExecute(userReg);
            email = userReg.getEmail();
            if(scanDetails != null) {
                scanRef = scanDetails.getScanRef();
                if (jumioScanRef != null
                 && jumioScanRef.equals("")) {
                    jumioScanRef = scanRef;
                }
            }
            apiToken = jumioKeys.getJumiokey();
            apiSecret = jumioKeys.getJumiosecret();
            //Log.e("Jumioooooo",apiToken+"    "+apiSecret);
            dataCenter = JumioDataCenter.US;
            downloadUrl = AppUrl.JUMIO_BASE_URL +jumioScanRef+"/images/face";
            //Log.e("AfterDown;oad",jumioScanRef);
            callGetProfileApi();
//            firstNameEdit.setText(userReg.getFirstName());
//            lastNameEdit.d(userReg.getLastName());
//            emailEdit.setText(userReg.getEmail());
//            phoneEdit.setText(userReg.getPhoneNo());
//            companyEdit.setText(userReg.getCompany());
//            addressEdit.setText(userReg.getAddress());
//            addressEdit1.setText(userReg.getAddress1());
//            emailEdit.setEnabled(false);
            // Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            if(scanDetails != null) {

               // scanRef = scanDetails.getScanRef();
                apiToken = jumioKeys.getJumiokey();
                apiSecret = jumioKeys.getJumiosecret();
                //Log.e("Jumioooooo", apiToken + "    " + apiSecret);
                dataCenter = JumioDataCenter.US;
                downloadUrl = AppUrl.JUMIO_BASE_URL + jumioScanRef + "/images/face";
                //Log.e("SelectData",jumioScanRef);
                downloadProfile(downloadUrl);

            }
        }

    }
    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

    }


    public void setImage(String imagePath){
        try {
            File f=new File(imagePath);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            saveNewSelfie(proImageName,b);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

}
