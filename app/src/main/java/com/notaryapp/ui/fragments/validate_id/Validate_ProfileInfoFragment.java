package com.notaryapp.ui.fragments.validate_id;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.jumio.core.enums.JumioDataCenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.components.ScrollViewExt;
import com.notaryapp.interfacelisterners.NestedScrollViewListener;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.ui.activities.ValidateActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class Validate_ProfileInfoFragment extends Fragment implements NestedScrollViewListener, Animation.AnimationListener  {

    private final String TAG = "SignUpFragment";
    private Button confirmBtn,backBtn;
    private ScrollViewExt  previewScroll;
    private ConstraintLayout constraintLayout;
    private View profileView;
    private String selectedType,errorMess,selfiePath,url,notaryEmail,scanRef = "";
    private DatabaseClient databaseClient;
    private CardView cardView;
    private ImageView faceImg,doc1Img,doc2Img, emailInfo, confirmEmailInfo, phoneInfo;
    private TextView frontIdTxt,backIdTxt;
    private Context context;
    private Bitmap bitmap;
    private Animation animMoveUp;
    private boolean frontFlag,backFlag,faceFlag;
    private int balanceTransCount;

    private JumioScanDetails scanDetails;
    private IJsonListener iJsonListener;
    private TextInputEditText regEdit_LastName;
    private TextInputEditText regEdit_Email;
    private TextInputEditText regEdit_PhoneNo;
    private TextInputEditText regEdit_address1;
    private TextInputEditText regEdit_address2;
    private TextInputEditText regEdit_FirstName;
    private TextInputEditText cityNameEdt,stateName,zipCodeEdt;
    private TextInputEditText regEdit_ConfirmEmail;

    private String firstName, lastName, email, phone,address1,address2,city,state,zip,imgProName;
    private boolean validFName, validLName, validEmail, validCEmail, validPhone, validAddre1,validAddress2,
            validCity, validState, validZip;

    private TextInputLayout textInputLayoutFName,textInputLayoutLName,textInputLayoutEmail, textInputLayoutConfirmEmail,
            textInputLayoutPhone,textInputLayoutAddress1,textInputLayoutAddress2,cityText,stateText,zipText;

    // private Bundle bundle;

    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    // private String downloadUrl;
    private String docType,tranId;
    private Spinner spinnerState;
    private List<String> stateList;
    //private String firstName,lastName;

    public Validate_ProfileInfoFragment(String firstName,String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_validate_profile_info, container, false);

        init();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Validation.isFirstName(regEdit_FirstName,textInputLayoutFName,true)){
                    // textInputLayoutFName.setError(null);
                    textInputLayoutFName.setErrorEnabled(false);
                    textInputLayoutFName.setError("Use alphabets only");
                    validFName = false;
                }else{
                    textInputLayoutFName.setError(null);
                    validFName = true;
                }
                if(!Validation.isLastName(regEdit_LastName,textInputLayoutLName,true)){
                    validLName = false;
                    textInputLayoutLName.setError(null);
                    textInputLayoutLName.setErrorEnabled(false);
                    textInputLayoutLName.setError("Enter Last Name");

                }else{
                    textInputLayoutLName.setError(null);
                    validLName = true;
                }
                if(!Validation.isEmailAddress(regEdit_Email,textInputLayoutEmail,true)){
                    textInputLayoutEmail.setError(null);
                    textInputLayoutEmail.setErrorEnabled(false);
                    textInputLayoutEmail.setError("This is a mandatory field. Please enter the correct email ID.");
                    validEmail = false;
                }else{
                    /*if(notaryEmail.equals(regEdit_Email.getText().toString())){
                        CustomDialog.notaryappDialogSingle(getActivity(),"Entered email id is same as Notary");
                        validEmail = false;
                    }else {
                        validEmail = true;

                    }*/
                    textInputLayoutEmail.setError(null);
                    validEmail = true;
                }

                if (!Validation.isEmailAddress(regEdit_ConfirmEmail, textInputLayoutConfirmEmail,true)) {
                    textInputLayoutConfirmEmail.setError(null);
                    textInputLayoutConfirmEmail.setErrorEnabled(false);
                    textInputLayoutConfirmEmail.setError("Enter Confirm Email");
                    validCEmail = false;
                } else {
                    if (regEdit_Email.getText().toString().equalsIgnoreCase(regEdit_ConfirmEmail.getText().toString())){
                        textInputLayoutConfirmEmail.setError(null);
                        validCEmail = true;
                    } else {
                        validCEmail = false;
                        textInputLayoutConfirmEmail.setError(null);
                        textInputLayoutConfirmEmail.setErrorEnabled(false);
                        textInputLayoutConfirmEmail.setError("The Email and Confirm Email fields do not match.");
                    }
                    //validEmail = true;
                }
                if (!Validation.isPhoneNumber(regEdit_PhoneNo, textInputLayoutPhone, true)){
                    textInputLayoutPhone.setError(null);
                    textInputLayoutPhone.setErrorEnabled(false);
                    //phoneText.setError("Enter valid phone number");
                    if (regEdit_PhoneNo.getText().toString().equalsIgnoreCase("")){
                        textInputLayoutPhone.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
                    } else {
                        textInputLayoutPhone.setError("Enter valid phone number.");
                    }
                    validPhone = false;
                } else {
                    textInputLayoutPhone.setError(null);
                    validPhone = true;
                }
                if(!Validation.hasValue(regEdit_address1,textInputLayoutAddress1)){
                    textInputLayoutAddress1.setError(null);
                    textInputLayoutAddress1.setErrorEnabled(false);
                    textInputLayoutAddress1.setError("Enter address line");
                    validAddre1 = false;
                }else{
                    textInputLayoutAddress1.setError(null);
                    validAddre1 = true;
                }
                if (Validation.hasValue(zipCodeEdt, zipText)) {
                    if(zipCodeEdt.getText().length() == 5) {
                        validZip = true;
                        zipText.setError(null);
                    }else{
                        validZip = false;
                        zipText.setError("Enter valid zip code");
                    }
                }else{
                    validZip = false;
                    zipText.setError("Enter valid zip code");
                }

                if (state.equals("Select State")) {
                    //    Toast.makeText(getActivity(), , Toast.LENGTH_LONG).show();
                    validState = false;
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please select your state");
                } else if(state.equals("null")){
                    validState = false;
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please select your state");
                }else{
                    validState = true;
                }

                if(validFName && validLName && validEmail && validCEmail & validPhone && validAddre1 && validCity && validState && validZip) {
                    if(Validation.hasValue(regEdit_PhoneNo,textInputLayoutPhone)){
                        if(Validation.isPhoneNumber(regEdit_PhoneNo,textInputLayoutPhone,true)){
                            getData();
                            //emailCheck();
                            confirmFormSubmit();
                            //registerApiCall();
                        }else{
                            textInputLayoutEmail.setError(null);
                            textInputLayoutEmail.setErrorEnabled(false);
                            //phoneText.setError("Enter valid phone number");
                            if (regEdit_PhoneNo.getText().toString().equalsIgnoreCase("")){
                                textInputLayoutEmail.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
                            } else {
                                textInputLayoutEmail.setError("Enter valid phone number.");
                            }
                        }
                    }else{
                        getData();
                        //emailCheck();
                        confirmFormSubmit();
                        //registerApiCall();
                    }

                }
                /*else{
                    validFName = Validation.hasFirstText(regEdit_FirstName);
                    if(!validFName){
                        textInputLayoutFName.setError("Enter First Name");
                    }
                }*/
            }
        });


        regEdit_FirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(regEdit_FirstName,textInputLayoutFName)){
                    validFName = Validation.isFirstName(regEdit_FirstName,textInputLayoutFName ,true);
                }
            }
        });
        regEdit_FirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validation.hasValue(regEdit_FirstName,textInputLayoutFName)){
                    validFName = Validation.isFirstName(regEdit_FirstName,textInputLayoutFName ,true);
                }
            }
        });
        regEdit_LastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(regEdit_LastName,textInputLayoutLName)){
                    validLName = Validation.isLastName(regEdit_LastName,textInputLayoutLName, true);//hasText();
                }
            }
        });
        regEdit_LastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validation.hasValue(regEdit_LastName,textInputLayoutLName)){
                    validLName = Validation.isLastName(regEdit_LastName,textInputLayoutLName, true);//hasText();
                }
            }
        });
        regEdit_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_Email,textInputLayoutEmail)) {
                    validEmail = Validation.isEmailAddress(regEdit_Email, textInputLayoutEmail, true);
                }else{

                }
            }
        });

        regEdit_ConfirmEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_ConfirmEmail, textInputLayoutConfirmEmail)){
                    validCEmail = Validation.isEmailAddress(regEdit_ConfirmEmail, textInputLayoutConfirmEmail, true);
                    validCEmail = Validation.hasEmailMatchedUpdate(regEdit_Email, regEdit_ConfirmEmail, textInputLayoutConfirmEmail);
                }
            }
        });

        regEdit_ConfirmEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validCEmail = Validation.isEmailAddress(regEdit_ConfirmEmail, textInputLayoutConfirmEmail, true);
                //validEmail = Validation.isEmailAddress(emailIdEdt, emailText, true);
                validEmail = Validation.hasEmailMatchedUpdate(regEdit_Email, regEdit_ConfirmEmail, textInputLayoutConfirmEmail);

            }
        });

        regEdit_ConfirmEmail.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        regEdit_PhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if (Validation.hasValue(regEdit_PhoneNo,textInputLayoutPhone)) {
                    // validPhone = Validation.isSignerPhoneNumber(regEdit_PhoneNo, textInputLayoutPhone, true);
                } else {

                }*/
            }
        });

        regEdit_address1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_address1,textInputLayoutAddress1)) {
                    validAddre1 = true;
                } else {

                }
            }
        });

        regEdit_address2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if (Validation.hasValue(regEdit_address2,textInputLayoutAddress2)) {
                    //     validAddress2 = true;
                    //   validAddress2 = Validation.isSignerPhoneNumber(regEdit_address2, textInputLayoutAddress2, true);
                } else {

                }*/
            }
        });
        cityNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(cityNameEdt,cityText)) {
                    validCity = true;
                }else{

                }
            }
        });
       /* stateName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(stateName,stateText)) {
                   validState = true;
                } else {

                }
            }
        });*/

        zipCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(zipCodeEdt,zipText)) {
                    validZip = true;
                } else {

                }
            }
        });
        zipCodeEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 5){
                    validZip = true;
                    zipText.setError("");
                }else{
                    validZip = false;
                    zipText.setError("Enter valid zip code");
                }
            }
        });
//        regEdit_PhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity().onBackPressed();
                loadFragment(new Validate_VerifyOkFragment());
            }
        });

        emailInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(),"Please make sure that the email address you provide is correct.");
            }
        });
        confirmEmailInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(),"The Email and Confirm Email fields must match.");
            }
        });

        phoneInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(),"Please make sure that the mobile number you have provided is correct. Do not use white spaces or symbols. Only enter the 10-digit mobile number.");
            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return profileView;
    }
    private void getData(){
        try {
            firstName = regEdit_FirstName.getText().toString().trim();
            lastName = regEdit_LastName.getText().toString().trim();
            email = regEdit_Email.getText().toString().trim();
            phone = regEdit_PhoneNo.getText().toString().trim();
            address1 = regEdit_address1.getText().toString().trim();
            address2 = regEdit_address2.getText().toString().trim();
            city = cityNameEdt.getText().toString().trim();
            //  state = stateName.getText().toString().trim();
            zip = zipCodeEdt.getText().toString().trim();

        }catch (Exception e){
            //Log.e(TAG,e.getMessage());
        }
    }
    private void init(){
        spinnerState = profileView.findViewById(R.id.stateSpinner);
        confirmBtn = profileView.findViewById(R.id.btnVerify);
        cardView = profileView.findViewById(R.id.card_back);
        backBtn = profileView.findViewById(R.id.btn_pro_back);
        faceImg = profileView.findViewById(R.id.face);
        emailInfo = profileView.findViewById(R.id.infoEmailIcon);
        confirmEmailInfo = profileView.findViewById(R.id.infoCEmailIcon);
        phoneInfo = profileView.findViewById(R.id.infoPhoneIcon);
        doc1Img = profileView.findViewById(R.id.doc1Img);
        doc2Img = profileView.findViewById(R.id.doc2Img);
        previewScroll = profileView.findViewById(R.id.previewScroll);
        constraintLayout = profileView.findViewById(R.id.footer);

        regEdit_FirstName = profileView.findViewById(R.id.firstName);
        regEdit_LastName = profileView.findViewById(R.id.lastName);
        regEdit_Email = profileView.findViewById(R.id.email);
        regEdit_ConfirmEmail = profileView.findViewById(R.id.confirmEmail);
        regEdit_PhoneNo = profileView.findViewById(R.id.phoneNo);
        regEdit_address1 = profileView.findViewById(R.id.addressLine1);
        regEdit_address2 = profileView.findViewById(R.id.addressLine2);
        cityNameEdt = profileView.findViewById(R.id.city);
        //  stateName = profileView.findViewById(R.id.state);
        zipCodeEdt = profileView.findViewById(R.id.zip);

        textInputLayoutFName = profileView.findViewById(R.id.firstNameText);
        textInputLayoutLName = profileView.findViewById(R.id.lastNameText);
        textInputLayoutEmail = profileView.findViewById(R.id.emailText);
        textInputLayoutConfirmEmail = profileView.findViewById(R.id.confirmEmailText);
        textInputLayoutPhone = profileView.findViewById(R.id.phoneText);
        textInputLayoutAddress1 = profileView.findViewById(R.id.address1Text);
        textInputLayoutAddress2 = profileView.findViewById(R.id.address2Text);
        cityText = profileView.findViewById(R.id.cityText);
        //  stateText = profileView.findViewById(R.id.stateText);
        zipText = profileView.findViewById(R.id.zipText);

        frontIdTxt = profileView.findViewById(R.id.frontIdText);
        backIdTxt= profileView.findViewById(R.id.backIdText);

        long millis=System.currentTimeMillis();
        imgProName = "client_"+millis+".png";

        initIJsonListener();
        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        new GetStates().execute();
        animMoveUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.move_up);
        animMoveUp.setAnimationListener(this);
        previewScroll.setScrollViewListener(this);

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
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, stateList);
            spinnerState.setAdapter(stateAdapter);
        }
    }
    private void downloadImgFromJumio(String scanRefference ,String type){
        //   String scanRefference = "4a7c36a4-6b2d-4653-bd8d-7aa93a15acfa";
        CustomDialog.showProgressDialog(getActivity());
        url = "https://netverify.com/api/netverify/v2/scans/" + scanRefference + "/images/"+type;
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
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        if(type.equals("front")) {
            picasso.load(url)
                    .centerCrop()
                    .resize(200,200)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(doc1Img);
        }
        if(type.equals("face")) {
            picasso.load(url)
                    .centerCrop()
                    .resize(200,200)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(faceImg);
            picasso.load(url)
                    .centerCrop()
                    .resize(200,200)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            saveNewSelfie(imgProName,bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }
        if(type.equals("back")) {
            cardView.setVisibility(View.VISIBLE);
            picasso.load(url)
                    .centerCrop()
                    .resize(200,200)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(doc2Img);
        }
        CustomDialog.cancelProgressDialog();
        //  uploadFile();
    }
    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        Uri imgUri = null;
        FileOutputStream fos = null;
        try {
            ContextWrapper cw = new ContextWrapper(getActivity());
            File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

            File selfie_new = new File(directory, name);

            fos = new FileOutputStream(selfie_new);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();

            imgUri = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",selfie_new);

        } catch (Exception e) {
            //Log.e("SAVE_IMAGE", e.getMessage(), e);
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
        return imgUri;
    }
    private void uploadFile() {
        CustomDialog.showProgressDialog(getActivity());

        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST,AppUrl.UPLOAD_CLIENT_SIGNER_WITNESS_DP , new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        loadFragment(new Validate_FinalFragment());
                        // loadFragment(new Notarize_SignerPersonalProfileFragment());
                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
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
                params.put("userName", email);
                params.put("pUser", notaryEmail);
                params.put("tranId",tranId);
                params.put("userType","Cl");
                // params.put("userName", "krish1@gmail.com");
                //   params.put("idDocType",docIdType);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (faceImg == null) {
                    //Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(imgProName, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                faceImg.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), ValidateActivity.REF_VIEW_CONTAINER,fragment,true);
    }
    private void registerApiCall(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                //Creating POST body in JSON format
                //to send in POST request
                params.put("first_name",firstName);
                params.put("last_name",lastName);
                params.put("phone",phone.replace(" ", "")
                        .replace("(", "")
                        .replace(")","").replace("-",""));
                params.put("username",email);
                params.put("address1",address1);
                params.put("address2",address2);
                params.put("puser",notaryEmail);
                params.put("docType",docType);
                params.put("city",city);
                params.put("state",state);
                params.put("zip",zip);
                params.put("scan_reference",scanRef);
            }catch (Exception e){
                //e.printStackTrace();
            }


//            postapiRequest.request(getActivity(),iJsonListener,params, url,"register");

            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.REGISTER_CLIENT,"register");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void emailCheck() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//        HashMap<String, String> params = new HashMap<>();
            try {
                //Creating POST body in JSON format
                //to send in POST request
                params.put("email", email);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            //postapiRequest.requestWithOutAuth(getActivity(), iJsonListener, params, AppUrl.CHECK_EMAIL, "email");
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.CHECK_EMAIL, "email");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data,String type ) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("success")) {
                            String success = data.getString("success");
                            if (type.equals("register")) {

                                if(success.equals("1019")) {
                                    tranId = data.getString("TranId");
                                    // uploadFile();
                                    CustomDialog.cancelProgressDialog();
                                    loadFragment(new Validate_FinalFragment());
                                    //    setTransactions();
                                }else if(success.equals("1018")){
                                    // CustomDialog.notaryappDialogSingle(getActivity(),"Email id already registered.");
                                }
                                else{
                                    CustomDialog.notaryappDialogSingle(getActivity(),"Registration failed!");
                                }
                            }else if(type.equals("Transactions")){
                                if(success.equals("1")) {
                                    balanceTransCount = data.getInt("balance");
                                    //loadFragment(new Validate_FinalFragment());
                                    new UpdateMemPlans().execute();
                                    //    Toast.makeText(getActivity().getApplicationContext(), "Set Transactions", Toast.LENGTH_LONG).show();

                                }

                            }else if(type.equals("email")){
                                String success1 = data.getString("success");
                                if (success.equals("1117")) { //already registered as Client/Signer/Witness
                                    registerApiCall();
                                    //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                                }else if(success.equals("1118")){//New user
                                    registerApiCall();
                                }else if(success.equals("1116")){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Notary");
                                }else{

                                }
                               /* if (success1.equals("1117")) { //
                                    registerApiCall();
                                    //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                                }else if(success1.equals("1017")){
                                    registerApiCall();
                                }else if(success1.equals("1016")){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Notary");
                                }else{

                                }*/
                            }else{

                            }
                        }
                    }else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(),"Error! No data fetched");
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(getActivity(),msg);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            constraintLayout.setVisibility(View.VISIBLE);
            confirmBtn.startAnimation(animMoveUp);
            //  btnConfirm.startAnimation(animMoveUp);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    confirmBtn.clearAnimation();
                    // btnConfirm.clearAnimation();
                }
            }, 300);


        } else if (diff > 0) {

            confirmBtn.clearAnimation();
            //  btnConfirm.clearAnimation();
            constraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            //creating a task
            notaryEmail = databaseClient.getAppDatabase().userRegDao().getEmail();

            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return selectedType;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if(scanDetails != null){
                docType = scanDetails.getDocType();
                scanRef = scanDetails.getScanRef();
                firstName = scanDetails.getFirstName().toLowerCase();
                lastName = scanDetails.getLastName().toLowerCase();
                regEdit_FirstName.setText(Utils.capitalizeFirst(firstName));
                regEdit_LastName.setText(Utils.capitalizeFirst(lastName));
            }else{
                regEdit_FirstName.setText(Utils.capitalizeFirst(firstName.toLowerCase()));
                regEdit_LastName.setText(Utils.capitalizeFirst(lastName.toLowerCase()));
            }
              /*  fName = firstName.toLowerCase();
                lName = lastName.toLowerCase();
                regEdit_FirstName.setText(Utils.capitalizeFirst(fName));
                regEdit_LastName.setText(Utils.capitalizeFirst(lName));
*/

            apiToken = jumioKeys.getJumiokey();
            apiSecret = jumioKeys.getJumiosecret();
            //Log.e("Jumioooooo",apiToken+"    "+apiSecret);
            dataCenter = JumioDataCenter.US;
            //  downloadUrl = AppUrl.JUMIO_BASE_URL +scanRef+"/images/face";
            /*if(docType.equals("PASSPORT") || docType.equals("VISA")) {
                //downloadImgFromJumio(scanRef, "front");
                downloadImgFromJumio(scanRef, "face");
                showIcon(docType, "front");
                showIcon(docType, "back");
            }else{
                //downloadImgFromJumio(scanRef, "front");
                downloadImgFromJumio(scanRef, "face");
                //downloadImgFromJumio(scanRef, "back");
                showIcon(docType, "front");
                showIcon(docType, "back");
            }*/
            downloadImgFromJumio(scanRef, "face");
            showIcon(docType, "front");
            showIcon(docType, "back");

        }

    }
    class UpdateMemPlans extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

                databaseClient.getAppDatabase()
                        .vaCustomerDao()
                        .updateCount(balanceTransCount);

            return
                    "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            loadFragment(new Validate_FinalFragment());
            //getActivity().onBackPressed();
        }

    }
  private void showIcon(String docType, String type) {
      Picasso picasso = new Picasso.Builder(getActivity())
              .build();
      frontIdTxt.setText(docType.toUpperCase().replace("_"," ").trim());
      if (docType.equalsIgnoreCase("PASSPORT")) {
          if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
              doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
          }
          if (type.equals("back")) {
              //cardView.setVisibility(View.VISIBLE);
//              picasso.load(R.drawable.ic_licence)
////                        .error(R.drawable.ic_licence)
////                        .into(doc2Img);
              doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
          }
      } else if (docType.equalsIgnoreCase("VISA")) {
          if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
              doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
          }
          if (type.equals("back")) {
              //cardView.setVisibility(View.VISIBLE);
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc2Img);
              doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
          }
      }  else if (docType.equalsIgnoreCase("DRIVING_LICENSE")) {
          if (type.equals("front")) {
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc1Img);
              doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_licence));
          }
          if (type.equals("back")) {
              //cardView.setVisibility(View.VISIBLE);
//                picasso.load(R.drawable.ic_licence)
//                        .error(R.drawable.ic_licence)
//                        .into(doc2Img);
              doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_id_card));
          }
      }else {
          if (type.equals("front")) {
              doc1Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
          }
          if (type.equals("back")) {
              //cardView.setVisibility(View.VISIBLE);
              doc2Img.setImageDrawable(getResources().getDrawable(R.drawable.ic_document));
          }
      }
//        else if (docType.equalsIgnoreCase("")) {
//            if (type.equals("front")) {
//                picasso.load(url)
//                        .error(R.drawable.doc1_img)
//                        .into(doc1Img);
//            }
//            if (type.equals("back")) {
//                cardView.setVisibility(View.VISIBLE);
//                picasso.load(url)
//                        .error(R.drawable.doc1_img)
//                        .into(doc2Img);
//            }
//        }
  }
    private void confirmFormSubmit(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        TextView titleText = dialog.findViewById(R.id.textHead);
        titleText.setText("Confirm Form Submission");
        text.setText("To prevent fraud, Notary-App® stores data on the blockchain. This means once your data is submitted, you will not be able to modify it. We recommend that you thoroughly review the data before submitting it.");

        Button dialogButton = dialog.findViewById(R.id.btnNo);
        dialogButton.setText("CANCEL");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("OK");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DeleteImages().execute();
                dialog.dismiss();
                registerApiCall();

            }
        });
        dialog.show();
    }
}