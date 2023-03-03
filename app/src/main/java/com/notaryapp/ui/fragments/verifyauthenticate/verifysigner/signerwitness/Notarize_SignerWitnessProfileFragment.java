package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import static com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signergvtid.Notarize_SignerProfileFragment.PICK_IMAGE;


public class Notarize_SignerWitnessProfileFragment extends Fragment {


    public static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private WitnessReg witnessReg;
    private DatabaseClient databaseClient;
    private final String TAG = "EditSignerFragment";
    private View editSignerView;
    private Button submitBtn;
    private Button btnBack,btnClose;
    private CircleImageView imgProfile;
    private ImageView profileEdit;
    private String fName="", lName="", email, phone,address1, address2, city, state, zip;
    private boolean validFName, validLName, validEmail, validCEmail, validPhone, validAddress1, validCity, validState, validZip;
    private TextInputEditText firstNameEdt,lastNameEdt,emailIdEdt, confirmEmailIdEdt, phoneNoEdt,addressOneEdt,
            addressTwoEdt,cityNameEdt,stateNameEdt,zipCodeEdt;
    private TextInputLayout firstNameLay,lastNameLay,emailLay, confirmEmailText, phoneNoLay,address1Text,address2Text,cityText,stateText,zipText;


    private Info info;
    private ImageView imageInfo, emailInfo, confirmEmailInfo, phoneInfo;
    private Context context;
    private String scanRef,notaryEmail,rowId,signerEmail,tranId;
    private Integer witnessCount;
    private JumioScanDetails scanDetails;
    private IJsonListener iJsonListener;
    private Picasso picasso;
    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String downloadUrl;
    private TelephonyManager tm ;
    private String countryCodeValue,savedWitImage;
    private Spinner spinnerState;
    private List<String> stateList;
    private int pending;
    private String image;
    private String idType="";

    public Notarize_SignerWitnessProfileFragment(){

    }
    public Notarize_SignerWitnessProfileFragment(String fName,String lName,String scanRef,String image) {
        this.fName =fName;
        this.lName = lName;
        this.scanRef = scanRef;
        this.image = image;

    }
    public Notarize_SignerWitnessProfileFragment(String fName,String lName,String scanRef,String image,int pending) {
        this.fName =fName;
        this.lName = lName;
        this.scanRef = scanRef;
        this.pending = pending;
        this.image = image;
    }
    public Notarize_SignerWitnessProfileFragment(String fName, String lName, String scanRef, String signerEmail, String image, int pending, String idType) {
        this.fName =fName;
        this.lName = lName;
        this.scanRef = scanRef;
        this.pending = pending;
        this.image = image;
        this.signerEmail = signerEmail;
        this.idType = idType;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editSignerView = inflater.inflate(R.layout.fragment_notarize_profile_signerwitness, container, false);
        init();
        tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        if(countryCodeValue == null || countryCodeValue.equalsIgnoreCase("")){
            countryCodeValue = "US";
        }
        buttonControls();

        return editSignerView;
    }

    private void init() {
        btnBack = editSignerView.findViewById(R.id.btn_pro_back);
        btnClose = editSignerView.findViewById(R.id.btn_pro_close);
        submitBtn = editSignerView.findViewById(R.id.submitBtn);
        firstNameEdt = editSignerView.findViewById(R.id.firstName);
        lastNameEdt = editSignerView.findViewById(R.id.lastName);
        emailIdEdt = editSignerView.findViewById(R.id.email);
        confirmEmailIdEdt = editSignerView.findViewById(R.id.confirmEmail);
        phoneNoEdt = editSignerView.findViewById(R.id.phoneNo);
        imgProfile = editSignerView.findViewById(R.id.imageProfile);
        imageInfo = editSignerView.findViewById(R.id.infoIcon);
        emailInfo = editSignerView.findViewById(R.id.infoEmailIcon);
        confirmEmailInfo = editSignerView.findViewById(R.id.infoCEmailIcon);
        phoneInfo = editSignerView.findViewById(R.id.infoPhoneIcon);
        profileEdit = editSignerView.findViewById(R.id.imageEdit);
        addressOneEdt = editSignerView.findViewById(R.id.address1);
        addressTwoEdt = editSignerView.findViewById(R.id.address2);
        cityNameEdt = editSignerView.findViewById(R.id.city);
       // stateNameEdt = editSignerView.findViewById(R.id.state);
        zipCodeEdt = editSignerView.findViewById(R.id.zip);

        firstNameLay = editSignerView.findViewById(R.id.firstNameText);
        lastNameLay = editSignerView.findViewById(R.id.lastNameText);
        emailLay = editSignerView.findViewById(R.id.emailText);
        confirmEmailText = editSignerView.findViewById(R.id.confirmEmailText);
        phoneNoLay = editSignerView.findViewById(R.id.phoneText);
        address1Text = editSignerView.findViewById(R.id.address1Text);
        address2Text = editSignerView.findViewById(R.id.address2Text);
        cityText = editSignerView.findViewById(R.id.cityText);
       // stateText = editSignerView.findViewById(R.id.stateText);
        zipText = editSignerView.findViewById(R.id.zipText);
        spinnerState = editSignerView.findViewById(R.id.stateSpinner);

        long millis=System.currentTimeMillis();
        savedWitImage = "witness_"+millis+".png";

        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        new GetStates().execute();
        initIJsonListener();
    }
    private void buttonControls() {
        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        firstNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(firstNameEdt,firstNameLay)){
                    validFName = Validation.isFirstName(firstNameEdt,firstNameLay ,true);
                }
            }
        });
        lastNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(lastNameEdt,lastNameLay)){
                    validLName = Validation.isLastName(lastNameEdt,lastNameLay, true);//hasText();
                }
            }
        });
        emailIdEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(emailIdEdt,emailLay)) {
                    validEmail = Validation.isEmailAddress(emailIdEdt, emailLay, true);
                }else{

                }
            }
        });
        confirmEmailIdEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(confirmEmailIdEdt, confirmEmailText)){
                    validCEmail = Validation.isEmailAddress(confirmEmailIdEdt, confirmEmailText, true);
                    validCEmail = Validation.hasEmailMatchedUpdate(emailIdEdt, confirmEmailIdEdt, confirmEmailText);
                }
            }
        });

        confirmEmailIdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validCEmail = Validation.isEmailAddress(confirmEmailIdEdt, confirmEmailText, true);
                //validEmail = Validation.isEmailAddress(emailIdEdt, emailText, true);
                validEmail = Validation.hasEmailMatchedUpdate(emailIdEdt, confirmEmailIdEdt, confirmEmailText);

            }
        });

        confirmEmailIdEdt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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
        phoneNoEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /*if (Validation.hasValue(phoneNoEdt,phoneNoLay)) {
//                    validPhone = Validation.isSignerPhoneNumber(phoneNoEdt, phoneNoLay, true);
                    //validPhone = Validation.hasValue(phoneNoEdt);
                } else {

                }*/
            }
        });
//        phoneNoEdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        addressOneEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(addressOneEdt,address1Text)) {
                    validAddress1 = true;//Validation.isSignerPhoneNumber(addressOne, address1Text, true);
                } else {

                }
            }
        });
        cityNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(cityNameEdt,cityText)) {
                    validCity = true;// Validation.isSignerPhoneNumber(phoneNo, phoneNoLay, true);
                } else {
                    validCity = false;
                }
            }
        });
     /*   stateNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(stateNameEdt,stateText)) {
                    validState = true;//Validation.isSignerPhoneNumber(phoneNo, phoneNoLay, true);
                } else {
                    validState = false;
                }
            }
        });*/
        zipCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(zipCodeEdt,zipText)) {
                    validZip = true;//Validation.isSignerPhoneNumber(phoneNo, phoneNoLay, true);
                } else {
                    validZip = false;
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
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getActivity().onBackPressed();
                if(pending == 1){
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment("pending"));
                }else {
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment());
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getActivity().onBackPressed();
                if(pending == 1){
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment("pending"));
                }else {
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment());
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validation.hasFirstText(firstNameEdt, firstNameLay)) {
                    firstNameLay.setError(null);
                    firstNameLay.setErrorEnabled(false);
                    firstNameLay.setError("Enter First Name");
                    validFName = false;
                } else {
                    validFName = true;
                }
                if (!Validation.hasValue(lastNameEdt, lastNameLay)) {
                    lastNameLay.setError(null);
                    lastNameLay.setErrorEnabled(false);
                    lastNameLay.setError("Enter Last Name");
                    validLName = false;
                } else {
                    validLName = true;
                }
                if (!Validation.isEmailAddress(emailIdEdt, emailLay,true)) {
                    emailLay.setError(null);
                    emailLay.setErrorEnabled(false);
                    emailLay.setError("This is a mandatory field. Please enter the correct email ID.");
                    validEmail = false;
                } else {
                    validEmail = true;
                }
                if (!Validation.isEmailAddress(confirmEmailIdEdt, confirmEmailText,true)) {
                    confirmEmailText.setError(null);
                    confirmEmailText.setErrorEnabled(false);
                    confirmEmailText.setError("Enter Confirm Email");
                    validCEmail = false;
                } else {
                    if (emailIdEdt.getText().toString().equalsIgnoreCase(confirmEmailIdEdt.getText().toString())){
                        validCEmail = true;
                    } else {
                        validCEmail = false;
                        confirmEmailText.setError(null);
                        confirmEmailText.setErrorEnabled(false);
                        confirmEmailText.setError("The Email and Confirm Email fields do not match.");
                    }
                    //validEmail = true;
                }
                if (!Validation.isPhoneNumber(phoneNoEdt, phoneNoLay, true)){
                    phoneNoLay.setError(null);
                    phoneNoLay.setErrorEnabled(false);
                    //phoneText.setError("Enter valid phone number");
                    if (phoneNoEdt.getText().toString().equalsIgnoreCase("")){
                        phoneNoLay.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
                    } else {
                        phoneNoLay.setError("Enter valid phone number.");
                    }
                    validPhone = false;
                } else {
                    phoneNoLay.setError(null);
                    validPhone = true;
                }
                if (!Validation.hasValue(addressOneEdt, address1Text)) {
                    address1Text.setError(null);
                    address1Text.setErrorEnabled(false);
                    address1Text.setError("Enter Address Line");
                    validAddress1 = false;
                } else {
                    validAddress1 = true;
                }
                if (!Validation.hasValue(cityNameEdt, cityText)) {
                    cityText.setError(null);
                    cityText.setErrorEnabled(false);
                    cityText.setError("Enter city name");
                    validCity = false;
                } else {
                    cityText.setError(null);
                    validCity = true;
                }
                if (Validation.hasValue(zipCodeEdt, zipText)) {
                    if(zipCodeEdt.getText().length() == 5) {
                        zipText.setError(null);
                        validZip = true;
                    }else{
                        validZip = false;
                        zipText.setError(null);
                        zipText.setErrorEnabled(false);
                        zipText.setError("Enter valid zip code");
                    }
                }else{
                    validZip = false;
                    zipText.setError(null);
                    zipText.setErrorEnabled(false);
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
                if (validFName && validLName && validEmail && validCEmail && validPhone && validCity && validState && validZip) {
                    try {
                        if(Validation.hasValue(phoneNoEdt,phoneNoLay)){
                            if(Validation.isPhoneNumber(phoneNoEdt,phoneNoLay,true)){
                        fName = firstNameEdt.getText().toString().trim().toLowerCase();
                        lName = lastNameEdt.getText().toString().trim().toLowerCase();
                        email = emailIdEdt.getText().toString().trim();
                        phone = phoneNoEdt.getText().toString().trim();
                        address1 = addressOneEdt.getText().toString().trim();
                        address2 = addressTwoEdt.getText().toString().trim();
                        city = cityNameEdt.getText().toString().trim();
                       // state = stateNameEdt.getText().toString().trim();

                        zip = zipCodeEdt.getText().toString().trim();

                        witnessReg = new WitnessReg();

                        witnessReg.setFirstName(Utils.capitalizeFirst(fName));
                        witnessReg.setLastName(Utils.capitalizeFirst(lName));
                        witnessReg.setEmail(email);
                        witnessReg.setSignerEmail(signerEmail);
                        witnessReg.setPhoneNo(phone);
                        witnessReg.setAddressOne(address1);
                        witnessReg.setAddressTwo(address2);
                        witnessReg.setCityName(city);
                        witnessReg.setStateName(state);
                        witnessReg.setZipCode(zip);
                        witnessReg.setProImagePath(savedWitImage);
                        witnessReg.setScanRef(scanRef);
                        //emailCheck();
                                if (notaryEmail.equalsIgnoreCase(email)){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a witness.");
                                } else {
                                    confirmFormSubmit();
                                    //addWitness();
                                }
                        //esting
                        //new SaveWitness().execute();
                        //esting
                    }else{
                                phoneNoLay.setError(null);
                                phoneNoLay.setErrorEnabled(false);
                                //phoneText.setError("Enter valid phone number");
                                if (phoneNoEdt.getText().toString().equalsIgnoreCase("")){
                                    phoneNoLay.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
                                } else {
                                    phoneNoLay.setError("Enter valid phone number.");
                                }
                    }
                }else{
                            fName = firstNameEdt.getText().toString().trim().toLowerCase();
                            lName = lastNameEdt.getText().toString().trim().toLowerCase();
                            email = emailIdEdt.getText().toString().trim();
                            phone = phoneNoEdt.getText().toString().trim();
                            address1 = addressOneEdt.getText().toString().trim();
                            address2 = addressTwoEdt.getText().toString().trim();
                            city = cityNameEdt.getText().toString().trim();
                          //  state = stateNameEdt.getText().toString().trim();
                            zip = zipCodeEdt.getText().toString().trim();

                            witnessReg = new WitnessReg();

                            witnessReg.setFirstName(Utils.capitalizeFirst(fName));
                            witnessReg.setLastName(Utils.capitalizeFirst(lName));
                            witnessReg.setEmail(email);
                            witnessReg.setSignerEmail(signerEmail);
                            witnessReg.setPhoneNo(phone);
                            witnessReg.setAddressOne(address1);
                            witnessReg.setAddressTwo(address2);
                            witnessReg.setCityName(city);
                            witnessReg.setStateName(state);
                            witnessReg.setZipCode(zip);
                            witnessReg.setProImagePath(savedWitImage);
                            witnessReg.setScanRef(scanRef);
                            //emailCheck();
                            if (notaryEmail.equalsIgnoreCase(email)){
                                CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a witness.");
                            } else {
                                confirmFormSubmit();
                                //addWitness();
                            }
                            //esting
                            //new SaveWitness().execute();
                            //esting
                        }
                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
                    }

                }
            }
        });

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(), info.getAddress());
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
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
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
    private void downloadProfile(String url){
        CustomDialog.showProgressDialog(getActivity());
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
        picasso = new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(url)
                .centerCrop()
                .resize(200,200)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgProfile);
        CustomDialog.cancelProgressDialog();


    }
    private void uploadFile() {
        CustomDialog.showProgressDialog(getActivity());
        //docIdType = "Signerphoto";
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, AppUrl.UPLOAD_CLIENT_SIGNER_WITNESS_DP, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        //  getActivity().finish();
                        //loadFragment(new Notarize_SignerWitnessSignerProfileFragment());
                        Intent intent = new Intent(getActivity(), VerifySignerActivity.class);
//                            intent.putExtra("PENDING", "");
                        intent.putExtra("EMAIL", signerEmail);
                        intent.putExtra("IdType", idType);
                        //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                        intent.putExtra("USERTYPE", "Wt");
                        //  intent.putExtra("USERTYPE", "signer");
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    } else {
//                        Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
//                    Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
//                Log.d(TAG, "Volley Error: " + error);
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
                params.put("pUser", signerEmail);
                params.put("tranId",tranId);
                params.put("userType","Wt");

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imgProfile == null) {
                    Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(savedWitImage, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                imgProfile.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }
    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            notaryEmail =  databaseClient.getAppDatabase().userRegDao().getEmail();
            //signerEmail =  databaseClient.getAppDatabase().signerRegDao().getSignerEmail();
            rowId = databaseClient.getAppDatabase().transactionsDao().getTransactionId();
            tranId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if (!idType.equalsIgnoreCase("manually")) {
                if (scanDetails != null) {
                    scanRef = scanDetails.getScanRef();
                    fName = scanDetails.getFirstName().toLowerCase();
                    lName = scanDetails.getLastName().toLowerCase();
                    firstNameEdt.setText(Utils.capitalizeFirst(fName));
                    lastNameEdt.setText(Utils.capitalizeFirst(lName));
                } else {
                    firstNameEdt.setText(Utils.capitalizeFirst(fName.toLowerCase()));
                    lastNameEdt.setText(Utils.capitalizeFirst(lName.toLowerCase()));
                }

                //scanRef = "60e95403-bc97-4e20-b382-2f30c11e39da";

                apiToken = jumioKeys.getJumiokey();
                apiSecret = jumioKeys.getJumiosecret();
//            Log.e("Jumioooooo",apiToken+"    "+apiSecret);
                dataCenter = JumioDataCenter.US;
                downloadUrl = AppUrl.JUMIO_BASE_URL + scanRef + "/images/face";
            }
            //downloadProfile(downloadUrl);
            showImage(); //Sourav 20201215
        }

    }

    class SaveWitness extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .insert(witnessReg);

            witnessCount = databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitnessCount();
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            /*picasso.load(downloadUrl)
                    .centerCrop()
                    .resize(200,200)
                    .into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(witnessCount == 1) {
                       saveNewSelfie(savedWitImage, bitmap);
                       // saveNewSelfie("witness1.png", bitmap);
                    }
                    if(witnessCount == 2) {
                        saveNewSelfie(savedWitImage, bitmap);
                     // saveNewSelfie("witness2.png", bitmap);
                    }
                 }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });*/
            uploadFile();
           // loadFragment(new Notarize_SignerWitnessSignerProfileFragment());
        }
    }
    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
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
                BuildConfig.APPLICATION_ID + ".provider",selfie_new);
        String path = selfie_new.getAbsolutePath();
        imgProfile.setImageURI(imgUri);
      //  uploadFile();
        return imgUri;
    }

    private void emailCheck() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
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
    private void addWitness(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("userNameN",notaryEmail);
                params.put("userNameS",signerEmail);
                params.put("userNameW", email);
                params.put("firstName",fName);
                params.put("lastName",lName);
                params.put("phone",phone.replace(" ", "")
                        .replace("(", "")
                        .replace(")","").replace("-",""));
                params.put("address1",address1);
                params.put("address2",address2);
                params.put("city",city);
                params.put("state",state);
                params.put("zip",zip);
                //params.put("phone ",email);
                params.put("scan_reference",scanRef);
                params.put("id",rowId);
                params.put( "country",countryCodeValue);
                // params.put("id","264");
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.SAVE_WITNESS,"saveWitness");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData,String type ) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {
                        if(type.equals("saveWitness")) {
                            //new SaveWitness().execute();
                            // Added
                            if(new JSONObject(responseData.getString("success")).getString("body")
                                    .equalsIgnoreCase("1")){
                                new SaveWitness().execute();
                            }else {
                                Toast toast = Toast.makeText(getActivity(),
                                        new JSONObject(responseData.getString("success")).getString("body"),
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                        else if(type.equals("email")){
                            String success = responseData.getString("success");
                            if (success.equals("1117")) { //
                              //  new SaveWitness().execute();
                                addWitness();
                                //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                            }else if(success.equals("1118")){
                              //  new SaveWitness().execute();
                               addWitness();
                            }else if(success.equals("1116")){
                                CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Notary");
                            }else{

                            }
                        }
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    //e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {
                //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    private void showImage() {
        try {
            File f = new File(image);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            saveNewSelfie(savedWitImage, b);
        } catch (IOException e) {
            //e.printStackTrace();
        }

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
                addWitness();

            }
        });
        dialog.show();
    }
}
