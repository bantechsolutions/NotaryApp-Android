package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.selfie.crediblewitness.CredibleWitnessAddSelfieActivity;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signergvtid.Notarize_SignerProfileFragment.PICK_IMAGE;

public class Notarize_SignerWitnessSignerProfileFragment extends Fragment {

    public static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private View view;

    private Button btnBack,btnClose;
    private ImageView imgWitness,imgProEdit;
    private ConstraintLayout relativeLayout;
    private Button submitBtn;
    private CardView addWitnessBtn;
    private List<WitnessReg> witnessList, witnessListMain;
    private RecyclerView recyclerWitness;
    private GridLayoutManager layoutManager;
    private DatabaseClient databaseClient;
    private Integer witnessCount = 0;
    private String fName, lName, email,phone,address1, address2, city, state, zip;
    private boolean validFName, validLName, validEmail, validCEmail, validPhone, validAddress1, validCity, validState, validZip;
    private TextInputEditText firstNameEdt,lastNameEdt,emailIdEdt, confirmEmailIdEdt, phoneNoEdt,addressOneEdt,
            addressTwoEdt,cityNameEdt,stateNameEdt,zipCodeEdt;
    private TextInputLayout firstNameText,lastNameText,emailText, confirmEmailText, phoneText,address1Text,address2Text,
            cityText,stateText,zipText;
    public static final String TAG = "VerifyWitness";
    private CircleImageView profileImg;
    private SignerReg signerReg,signerRegData;
    private String notaryEmail,rowId;
    private IJsonListener iJsonListener;
    private ConstraintLayout witnessOneView, witnessTwoView,plusBoxOne,plusBoxTwo;
    private Bitmap bitmap,bitmap1;
    private String selfiePath,imgProName = "",tranId;
    private ImageView itemImageOne,itemImageTwo;
    private TextView witnessName1,witnessName2;
    private WitnessReg witnessReg;
    private TelephonyManager tm ;
    private String countryCodeValue;
    private Info info;
    private ImageView imageInfo, emailInfo, confirmEmailInfo, phoneInfo;
    private boolean flag = false;
    private Uri imageUri;
    private Spinner spinnerState;
    private List<String> stateList;
    private String from = "";
    private String idType="";

    private String signerEmail = null;

    public Notarize_SignerWitnessSignerProfileFragment(String imgProName, Uri imageUri, String signerEmail) {
        this.imgProName = imgProName;
        this.imageUri = imageUri;
        this.signerEmail = signerEmail;
    }
    public Notarize_SignerWitnessSignerProfileFragment() {

    }
    public Notarize_SignerWitnessSignerProfileFragment(String from) {

        this.from = from;
    }
    public Notarize_SignerWitnessSignerProfileFragment(String from, String signerEmail) {

        this.from = from;
        this.signerEmail = signerEmail;
    }
    public Notarize_SignerWitnessSignerProfileFragment(String from, String signerEmail, String idType) {

        this.from = from;
        this.signerEmail = signerEmail;
        this.idType= idType;
    }

    public Notarize_SignerWitnessSignerProfileFragment(String imgProName, Uri imageUri, String signerEmail, String idType) {
        this.imgProName = imgProName;
        this.imageUri = imageUri;
        this.signerEmail = signerEmail;
        this.idType = idType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notarize_verify_witness, container, false);
        init();
        tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        if(countryCodeValue == null || countryCodeValue.equalsIgnoreCase("")){
            countryCodeValue = "US";
        }
        buttonControls();
        return view;
    }

    private void init() {
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new GetStates().execute();
        new SelectData().execute();
        new GetWitness().execute();
        new GeInfo().execute();

        itemImageOne = view.findViewById(R.id.itemImage1);
        itemImageTwo = view.findViewById(R.id.itemImage2);
        plusBoxOne = view.findViewById(R.id.plusBox1);
        plusBoxTwo = view.findViewById(R.id.plusBox2);
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        submitBtn = view.findViewById(R.id.submitBtn);
        firstNameEdt = view.findViewById(R.id.firstName);
        lastNameEdt = view.findViewById(R.id.lastName);
        emailIdEdt = view.findViewById(R.id.email);
        confirmEmailIdEdt = view.findViewById(R.id.confirmEmail);
        phoneNoEdt = view.findViewById(R.id.phoneNo);
        imageInfo = view.findViewById(R.id.infoIcon);
        emailInfo = view.findViewById(R.id.infoEmailIcon);
        confirmEmailInfo = view.findViewById(R.id.infoCEmailIcon);
        phoneInfo = view.findViewById(R.id.infoPhoneIcon);
        imgProEdit = view.findViewById(R.id.imgProEdit);

        addressOneEdt = view.findViewById(R.id.address1);
        addressTwoEdt = view.findViewById(R.id.address2);
        cityNameEdt = view.findViewById(R.id.city);
     //   stateNameEdt = view.findViewById(R.id.state);
        zipCodeEdt = view.findViewById(R.id.zip);
        profileImg = view.findViewById(R.id.imageProfile);
        relativeLayout = view.findViewById(R.id.regRelativeLayout);
        witnessName1 = view.findViewById(R.id.itemTitle1);
        witnessName2 = view.findViewById(R.id.itemTitle2);
        imgWitness = view.findViewById(R.id.imgWitness);

        firstNameText = view.findViewById(R.id.firstNameText);
        lastNameText = view.findViewById(R.id.lastNameText);
        emailText = view.findViewById(R.id.emailText);
        confirmEmailText = view.findViewById(R.id.confirmEmailText);
        phoneText = view.findViewById(R.id.phoneText);
        address1Text = view.findViewById(R.id.address1Text);
        address2Text = view.findViewById(R.id.address2Text);
        cityText = view.findViewById(R.id.cityText);
      //  stateText = view.findViewById(R.id.stateText);
        zipText = view.findViewById(R.id.zipText);
        spinnerState = view.findViewById(R.id.stateSpinner);

        witnessOneView = view.findViewById(R.id.witnessOneContainer);
        witnessTwoView = view.findViewById(R.id.witnessTwoContainer);
        new DeleteAllSelectId().execute();
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
    private void buttonControls() {
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        firstNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(firstNameEdt,firstNameText)){
                    validFName = Validation.isFirstName(firstNameEdt,firstNameText ,true);
                }
            }
        });
        lastNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(lastNameEdt,lastNameText)){
                    validLName = Validation.isLastName(lastNameEdt,lastNameText, true);//hasText();
                }
            }
        });
        emailIdEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(emailIdEdt,emailText)) {
                    validEmail = Validation.isEmailAddress(emailIdEdt, emailText, true);
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
                if (Validation.hasValue(phoneNoEdt,phoneText)) {
                  //  validPhone = Validation.isSignerPhoneNumber(phoneNo, phoneText, true);
                }
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
        addressTwoEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(addressTwoEdt,address2Text)) {
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
      /*  stateNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
               // completedDialog();
                getActivity().finish();
                /*if(from.equalsIgnoreCase("pending")){
                    getActivity().finish();
                }
                else {
                    //LAHAR
                    //loadFragment(new Notarize_SignerWitnessPreviewFragment(imageUri, imgProName));
                    getActivity().finish();
                    //loadFragment(new Notarize_SignerWitnessCameraFragment());
                }*/
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  completedDialog();
               // getActivity().onBackPressed();
                getActivity().finish();
                /*if(from.equalsIgnoreCase("pending")){
                    getActivity().finish();
                }
                else{
                    //LAHAR
                    //loadFragment(new Notarize_SignerWitnessPreviewFragment(imageUri,imgProName));
                    getActivity().finish();
                    //loadFragment(new Notarize_SignerWitnessCameraFragment());

                }*/

            }
        });
        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(),info.getAddress());
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

        //Button click to start next fragment.
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(witnessCount == 2){
                    getActivity().finish();
               //     flag = true;
               // new SelectData().execute();
                   // AddWitnessSigner();
                }else {
                    updateSignerAddClient();
                }
            }
        });

        //Button click to start next fragment.
        witnessOneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (witnessCount == 0) {
                    /*if(from.equals("pending")) {
                        loadFragment(new Notarize_WitnessDocTypeFragment(1));
                    }else{
                        loadFragment(new Notarize_WitnessDocTypeFragment());
                    }*/
                    if (idType.equalsIgnoreCase("manually")){
                        Intent intent = new Intent(getActivity(), CredibleWitnessAddSelfieActivity.class);
                        intent.putExtra("FirstName", "");
                        intent.putExtra("LastName", "");
                        intent.putExtra("ScanRef", "");
                        intent.putExtra("SignerEmail", signerEmail);
                        intent.putExtra("pending", "1");
                        intent.putExtra("IdType", "manually");
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        getActivity().finish();
                    } else {
                        loadFragment(new Notarize_WitnessDocTypeFragment(1, signerEmail));
                    }
                    //Testing
                 /*   if(from.equals("pending")){
                        loadFragment(new Notarize_SignerWitnessProfileFragment("Bubble",
                                "Pappali", "de9779e7-3bbe-4987-9856-48f1641be188",1));

                    }else {
                        loadFragment(new Notarize_SignerWitnessProfileFragment("Bubble", "Pappali", "de9779e7-3bbe-4987-9856-48f1641be188"));
                    }*/
                    //Testing
                }

            }
        });

        //Button click to start next fragment.
        witnessTwoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    ((VerifySignerActivity) getActivity()).isOpenTwo = true;
                }catch (Exception ignored){

                }

                if(witnessCount == 0) {
                    CustomDialog.notaryappDialogSingle(getActivity(),"Add first Witness");
                }else{
                    /*if(from.equals("pending")) {
                        loadFragment(new Notarize_WitnessDocTypeFragment(1));
                    }else{
                        loadFragment(new Notarize_WitnessDocTypeFragment());
                    }*/
                    if (idType.equalsIgnoreCase("manually")){
                        Intent intent = new Intent(getActivity(), CredibleWitnessAddSelfieActivity.class);
                        intent.putExtra("FirstName", "");
                        intent.putExtra("LastName", "");
                        intent.putExtra("ScanRef", "");
                        intent.putExtra("SignerEmail", signerEmail);
                        intent.putExtra("pending", "1");
                        intent.putExtra("IdType", "manually");
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        getActivity().finish();
                    } else {
                        loadFragment(new Notarize_WitnessDocTypeFragment(1, signerEmail));
                    }
                    //Testing
                  /*  if(from.equals("pending")){
                        loadFragment(new Notarize_SignerWitnessProfileFragment("Ambily", "ps",
                                "f533ef8b-bf5a-4c95-bbd3-119fc6d667d1",1));
                    }else {
                        loadFragment(new Notarize_SignerWitnessProfileFragment("Ambily", "ps",
                                "f533ef8b-bf5a-4c95-bbd3-119fc6d667d1"));
                    }*/
                    //Testing
                }
            }
        });
        imgProEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

    }

    private void updateSignerAddClient(){
        if (!Validation.hasFirstText(firstNameEdt, firstNameText)) {
            firstNameText.setError(null);
            firstNameText.setErrorEnabled(false);
            firstNameText.setError("Enter First Name");
            validFName = false;
        } else {
            firstNameText.setError(null);
            validFName = true;
        }
        if (!Validation.hasValue(lastNameEdt, lastNameText)) {
            lastNameText.setError(null);
            lastNameText.setErrorEnabled(false);
            lastNameText.setError("Enter Last Name");
            validLName = false;
        } else {
            lastNameText.setError(null);
            validLName = true;
        }
        if (!Validation.isEmailAddress(emailIdEdt, emailText, true)) {
            emailText.setError(null);
            emailText.setErrorEnabled(false);
            emailText.setError("This is a mandatory field. Please enter the correct email ID.");
            validEmail = false;
        } else {
            emailText.setError(null);
            validEmail = true;
        }
        if (!Validation.isEmailAddress(confirmEmailIdEdt, confirmEmailText,true)) {
            confirmEmailText.setError(null);
            confirmEmailText.setErrorEnabled(false);
            confirmEmailText.setError("Enter Confirm Email");
            validCEmail = false;
        } else {
            if (emailIdEdt.getText().toString().equalsIgnoreCase(confirmEmailIdEdt.getText().toString())){
                confirmEmailText.setError(null);
                validCEmail = true;
            } else {
                validCEmail = false;
                confirmEmailText.setError(null);
                confirmEmailText.setErrorEnabled(false);
                confirmEmailText.setError("The Email and Confirm Email fields do not match.");
            }
            //validEmail = true;
        }
        if (!Validation.isPhoneNumber(phoneNoEdt, phoneText, true)){
            phoneText.setError(null);
            phoneText.setErrorEnabled(false);
            //phoneText.setError("Enter valid phone number");
            if (phoneNoEdt.getText().toString().equalsIgnoreCase("")){
                phoneText.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
            } else {
                phoneText.setError("Enter valid phone number.");
            }
            validPhone = false;
        } else {
            phoneText.setError(null);
            validPhone = true;
        }
        if (!Validation.hasValue(addressOneEdt, address1Text)) {
            address1Text.setError(null);
            address1Text.setErrorEnabled(false);
            address1Text.setError("Enter Address Line");
            validAddress1 = false;
        } else {
            address1Text.setError(null);
            validAddress1 = true;
        }
       /* if (Validation.hasValue(stateNameEdt, stateText)) {
            validState = true;
        }else{
            validState = false;
        }*/
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
        if (Validation.hasValue(cityNameEdt, cityText)) {
            cityText.setError(null);
            validCity = true;
        }else{
            cityText.setError(null);
            cityText.setErrorEnabled(false);
            cityText.setError("Enter city name");
            validCity = false;
        }
        if (Validation.hasValue(zipCodeEdt, zipText)) {
            if (zipCodeEdt.getText().length() == 5) {
                zipText.setError(null);
                validZip = true;
            } else {
                validZip = false;
                zipText.setError("Enter valid zip code");
            }
        } else {
            validZip = false;
            zipText.setError("Enter valid zip code");
        }

        if (validFName && validLName && validEmail && validCEmail && validPhone && validAddress1 && validCity && validState && validZip) {
            try {
                if(Validation.hasValue(phoneNoEdt,phoneText)) {
                    if (Validation.isPhoneNumber(phoneNoEdt, phoneText, true)) {
                        fName = firstNameEdt.getText().toString().trim();
                        lName = lastNameEdt.getText().toString().trim();
                        email = emailIdEdt.getText().toString().trim();
                        phone = phoneNoEdt.getText().toString().trim();
                        address1 = addressOneEdt.getText().toString().trim();
                        address2 = addressTwoEdt.getText().toString().trim();
                        city = cityNameEdt.getText().toString().trim();
                     //   state = stateNameEdt.getText().toString().trim();
                        zip = zipCodeEdt.getText().toString().trim();


                        signerReg = new SignerReg();

                        signerReg.setFirstName(fName);
                        signerReg.setLastName(lName);
                        signerReg.setEmail(email);
                        signerReg.setPhoneNo(phone);
                        signerReg.setWitness(true);
                        signerReg.setAddressOne(address1);
                        signerReg.setAddressTwo(address2);
                        signerReg.setCityName(city);
                        signerReg.setStateName(state);
                        signerReg.setZipCode(zip);
                        signerReg.setProImagePath(imgProName);
                        if (idType.equalsIgnoreCase("manually")){
                            signerReg.setSignerType("WITNESSMANUALLY");
                        } else {
                            signerReg.setSignerType("WIT");
                        }

                        //emailCheck();
                        if (notaryEmail.equalsIgnoreCase(email)){
                            CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a signer.");
                        } else {
                            confirmFormSubmit();
                            //addWitnessSigner();
                        }
                        //Testing
                        //   new SaveSigner().execute();
                        //Testing
                    } else {
                        phoneText.setError(null);
                        phoneText.setErrorEnabled(false);
                        //phoneText.setError("Enter valid phone number");
                        if (phoneNoEdt.getText().toString().equalsIgnoreCase("")){
                            phoneText.setError("This is a mandatory field. Please enter the 10-digit mobile number.");
                        } else {
                            phoneText.setError("Enter valid phone number.");
                        }
                    }
                }else{
                    fName = firstNameEdt.getText().toString().trim();
                    lName = lastNameEdt.getText().toString().trim();
                    email = emailIdEdt.getText().toString().trim();
                    phone = phoneNoEdt.getText().toString().trim();
                    address1 = addressOneEdt.getText().toString().trim();
                    address2 = addressTwoEdt.getText().toString().trim();
                    city = cityNameEdt.getText().toString().trim();
                   // state = stateNameEdt.getText().toString().trim();
                    zip = zipCodeEdt.getText().toString().trim();


                    signerReg = new SignerReg();

                    signerReg.setFirstName(fName);
                    signerReg.setLastName(lName);
                    signerReg.setEmail(email);
                    signerReg.setPhoneNo(phone);
                    signerReg.setWitness(true);
                    signerReg.setAddressOne(address1);
                    signerReg.setAddressTwo(address2);
                    signerReg.setCityName(city);
                    signerReg.setStateName(state);
                    signerReg.setZipCode(zip);
                    signerReg.setProImagePath(imgProName);
                    if (idType.equalsIgnoreCase("manually")){
                        signerReg.setSignerType("WITNESSMANUALLY");
                    } else {
                        signerReg.setSignerType("WIT");
                    }

                    //emailCheck();
                    if (notaryEmail.equalsIgnoreCase(email)){
                        CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a signer.");
                    } else {
                        confirmFormSubmit();
                        //addWitnessSigner();
                    }
                    //Testing
                    //   new SaveSigner().execute();
                    //Testing
                }
            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
            }

        }
    }

    private void completedDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText("You would loose all data ... ");

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
               new DeleteFromCloseAll().execute();
                getActivity().finish();
            }
        });
        dialog.show();
    }

    class DeleteFromCloseAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteLast(fName);
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

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
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    saveNewSelfie(imgProName,bitmap);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
    }
    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(getActivity());
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
                BuildConfig.APPLICATION_ID + ".provider",selfie_new);
        profileImg.setImageURI(imgUri);

        String path = selfie_new.getAbsolutePath();
       // uploadFile();

        return imgUri;
    }
    private void uploadFile() {
         CustomDialog.showProgressDialog(getActivity());
        //docIdType = "Signerphoto";
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppUrl.UPLOAD_CLIENT_SIGNER_WITNESS_DP, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                       /* for(int i=0;i<witnessList.size();i++) {
                            addWitness(witnessList.get(i));
                            uploadWitnessFile(witnessList.get(i).getProImagePath(),witnessList.get(i).getEmail());
                        }
                        getActivity().finish();*/
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
        }, new Response.ErrorListener() {
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
                params.put("pUser", notaryEmail);
                params.put("tranId",tranId);
                params.put("userType","Sr");
                //   params.put("idDocType",docIdType);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (profileImg == null) {
                    Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(imgProName, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                profileImg.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
    }

   public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
   class GetWitness extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            /*witnessCount = databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitnessCount();*/

            /*witnessList = databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitness();*/
            witnessListMain = databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitness();

            witnessCount = 0;
            witnessList = new ArrayList<>();
            for (int i=0;i<witnessListMain.size();i++){
                //Log.d("WIT_SIGN", signerRegData.getEmail() + " "+ witnessList.get(i).getSignerEmail());
                if (signerEmail != null){
                    //Log.d("SIGNER_LIST", witnessListMain.get(i).getFirstName());
                    if (signerEmail.equalsIgnoreCase(witnessListMain.get(i).getSignerEmail())){
                        witnessList.add(witnessListMain.get(i));
                        witnessCount = witnessCount + 1;
                        //Log.d("WITNESS_COUNT", String.valueOf(witnessCount));
                    }
                }
            }

            return witnessCount;
        }

        @Override
        protected void onPostExecute(Integer witnessCount) {
            super.onPostExecute(witnessCount);
            if(witnessCount==0){
                if(from.equals("pending")){
                    witnessOneView.setVisibility(View.VISIBLE);
                    witnessTwoView.setVisibility(View.INVISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    firstNameEdt.setText(signerRegData.getFirstName());
                    lastNameEdt.setText(signerRegData.getLastName());
                    emailIdEdt.setText(signerRegData.getEmail());
                    confirmEmailIdEdt.setText(signerRegData.getEmail());
                    phoneNoEdt.setText(signerRegData.getPhoneNo());
                    addressOneEdt.setText(signerRegData.getAddressOne());
                    addressTwoEdt.setText(signerRegData.getAddressTwo());
                    cityNameEdt.setText(signerRegData.getCityName());
//                    Log.e("Stateeeeee",signerRegData.getStateName());
//                    Log.e("Stateeeeee", "Size "+stateList.size());
                    zipCodeEdt.setText(signerRegData.getZipCode());
                    spinnerState.setSelection(stateList.indexOf(signerRegData.getStateName()));
                    selfiePath = getSelfiePath(getActivity(), signerRegData.getProImagePath());
                    bitmap = BitmapFactory.decodeFile(selfiePath);
                    if(bitmap != null){
                        profileImg.setImageBitmap(bitmap);
                    }else {
                        Picasso.with(getActivity()).load(signerRegData.getProImagePath())
                                .centerCrop()
                                .resize(200,200)
                                .into(profileImg);
                    }


                    firstNameText.setEnabled(false);
                    lastNameEdt.setEnabled(false);
                    emailText.setEnabled(false);
                    confirmEmailText.setVisibility(View.GONE);
                    confirmEmailInfo.setVisibility(View.GONE);
                    phoneNoEdt.setEnabled(false);
                    addressOneEdt.setEnabled(false);
                    addressTwoEdt.setEnabled(false);
                    cityNameEdt.setEnabled(false);
                    // stateNameEdt.setEnabled(false);
                    spinnerState.setEnabled(false);
                    zipCodeEdt.setEnabled(false);
                }else {
                    witnessOneView.setVisibility(View.INVISIBLE);
                    witnessTwoView.setVisibility(View.INVISIBLE);
                }
            } else if (witnessCount == 1) {
                witnessReg = witnessList.get(0);
               // witnessOneName = witnessFname;
                witnessOneView.setVisibility(View.VISIBLE);
                selfiePath = getSelfiePath(getActivity(), witnessReg.getProImagePath());
               // selfiePath = getSelfiePath(getActivity(), "witness1.png");
                bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null){
                    itemImageOne.setVisibility(View.VISIBLE);
                    itemImageOne.setImageBitmap(bitmap);

                }else {
                    itemImageOne.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(witnessReg.getProImagePath())
                            .centerCrop()
                            .resize(200,200)
                            .into(itemImageOne);

                }
                witnessName1.setText(witnessReg.getFirstName());

                witnessTwoView.setVisibility(View.VISIBLE);
                plusBoxTwo.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);

                witnessOneView.setEnabled(false);
                firstNameEdt.setText(signerRegData.getFirstName());
                lastNameEdt.setText(signerRegData.getLastName());
                emailIdEdt.setText(signerRegData.getEmail());
                confirmEmailIdEdt.setText(signerRegData.getEmail());
                phoneNoEdt.setText(signerRegData.getPhoneNo());
                addressOneEdt.setText(signerRegData.getAddressOne());
                addressTwoEdt.setText(signerRegData.getAddressTwo());
                cityNameEdt.setText(signerRegData.getCityName());
//                Log.e("Stateeeeee",signerRegData.getStateName());
//                Log.e("Stateeeeee", "Size "+stateList.size());
                zipCodeEdt.setText(signerRegData.getZipCode());
                spinnerState.setSelection(stateList.indexOf(signerRegData.getStateName()));
                selfiePath = getSelfiePath(getActivity(), signerRegData.getProImagePath());
                bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null){
                    profileImg.setImageBitmap(bitmap);

                }else{
                    Picasso.with(getActivity()).load(signerRegData.getProImagePath())
                            .centerCrop()
                            .resize(200,200)
                            .into(profileImg);
                }

                firstNameText.setEnabled(false);
                lastNameEdt.setEnabled(false);
                emailText.setEnabled(false);
                confirmEmailText.setVisibility(View.GONE);
                confirmEmailInfo.setVisibility(View.GONE);
                phoneNoEdt.setEnabled(false);
                addressOneEdt.setEnabled(false);
                addressTwoEdt.setEnabled(false);
                cityNameEdt.setEnabled(false);
               // stateNameEdt.setEnabled(false);
                spinnerState.setEnabled(false);
                zipCodeEdt.setEnabled(false);
            } else if(witnessCount == 2){
                relativeLayout.setVisibility(View.VISIBLE);
                firstNameEdt.setText(signerRegData.getFirstName());
                lastNameEdt.setText(signerRegData.getLastName());
                emailIdEdt.setText(signerRegData.getEmail());
                confirmEmailIdEdt.setText(signerRegData.getEmail());
                phoneNoEdt.setText(signerRegData.getPhoneNo());
                addressOneEdt.setText(signerRegData.getAddressOne());
                addressTwoEdt.setText(signerRegData.getAddressTwo());
                cityNameEdt.setText(signerRegData.getCityName());
//                Log.e("Stateeeeee",signerRegData.getStateName());
                spinnerState.setSelection(stateList.indexOf(signerRegData.getStateName()));
                zipCodeEdt.setText(signerRegData.getZipCode());
                firstNameText.setEnabled(false);
                lastNameEdt.setEnabled(false);
                emailText.setEnabled(false);
                confirmEmailText.setVisibility(View.GONE);
                confirmEmailInfo.setVisibility(View.GONE);
                phoneNoEdt.setEnabled(false);
                addressOneEdt.setEnabled(false);
                addressTwoEdt.setEnabled(false);
                cityNameEdt.setEnabled(false);
           //     stateNameEdt.setEnabled(false);
                spinnerState.setEnabled(false);
                zipCodeEdt.setEnabled(false);
                selfiePath = getSelfiePath(getActivity(), signerRegData.getProImagePath());
                bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null){
                    profileImg.setImageBitmap(bitmap);
                }else{
                    Picasso.with(getActivity()).load(signerRegData.getProImagePath())
                            .centerCrop()
                            .resize(200,200)
                            .into(profileImg);
                }

                witnessReg = witnessList.get(0);
                selfiePath = getSelfiePath(getActivity(), witnessReg.getProImagePath());
                bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null) {
                    witnessOneView.setVisibility(View.VISIBLE);
                    itemImageOne.setImageBitmap(bitmap);
                }else {
                    witnessOneView.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(witnessReg.getProImagePath())
                            .centerCrop()
                            .resize(200,200)
                            .into(itemImageOne);
                }
                plusBoxOne.setVisibility(View.INVISIBLE);
                itemImageOne.setVisibility(View.VISIBLE);
                witnessName1.setText(witnessReg.getFirstName());

                witnessReg = witnessList.get(1);
                witnessName2.setText(witnessReg.getFirstName());
                selfiePath = getSelfiePath(getActivity(), witnessReg.getProImagePath());
                bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null) {
                    witnessTwoView.setVisibility(View.VISIBLE);
                    itemImageTwo.setImageBitmap(bitmap);
                }else{
                    witnessTwoView.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity()).load(witnessReg.getProImagePath())
                            .centerCrop()
                            .resize(200,200)
                            .into(itemImageTwo);
                }
                itemImageTwo.setVisibility(View.VISIBLE);
                plusBoxTwo.setVisibility(View.INVISIBLE);

                witnessOneView.setEnabled(false);
                witnessTwoView.setEnabled(false);
            }
        }
    }
   class DeleteAllSelectId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .witnessSelectIDDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
   private class SaveSigner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .signerRegDao()
                    .insert(signerReg);

            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            //  loadFragment(new Notarize_SignerWitnessProfileFragment());
            signerEmail = signerReg.getEmail();
            witnessOneView.setVisibility(View.VISIBLE);

            relativeLayout.setVisibility(View.GONE);
            firstNameEdt.setEnabled(false);
            lastNameEdt.setEnabled(false);
            emailText.setEnabled(false);
            confirmEmailText.setVisibility(View.GONE);
            confirmEmailInfo.setVisibility(View.GONE);
            phoneNoEdt.setEnabled(false);
            addressOneEdt.setEnabled(false);
            addressTwoEdt.setEnabled(false);
            cityNameEdt.setEnabled(false);
         //   stateNameEdt.setEnabled(false);
            spinnerState.setEnabled(false);
            zipCodeEdt.setEnabled(false);
          //  addWitnessSigner();
            uploadFile();
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
    }
    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            //scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            notaryEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            rowId = databaseClient.getAppDatabase().transactionsDao().getTransactionId();
            tranId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            if (signerEmail != null){
                signerRegData  = databaseClient.getAppDatabase().signerRegDao().getSigner(signerEmail);
            }
            //signerRegData  = databaseClient.getAppDatabase().signerRegDao().getSignersWitness();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if(signerReg != null) {

                imgProName = signerRegData.getProImagePath();

            }
            if (signerEmail != null){
                imgProName = signerRegData.getProImagePath();
            }

            if (!imgProName.equals("") || !imgProName.equalsIgnoreCase("null")) {

                if (checkSelfie(getActivity(), imgProName) && signerEmail == null) {
                    selfiePath = getSelfiePath(getActivity(), imgProName);
                    bitmap = BitmapFactory.decodeFile(selfiePath);
                    // profileImg.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
                    profileImg.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
                }
            }
        }

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
                params.put("user", email);

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
    private void addWitnessSigner(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("userNameN",notaryEmail);
                params.put("userNameS",email);
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
                params.put("scan_reference","");
                params.put("id",rowId);
                params.put( "country",countryCodeValue);
                if (idType.equalsIgnoreCase("manually")){
                    params.put("userCat","WITNESSMANUALLY");
                } else {
                    params.put("userCat","WIT");
                }
           /*     params.put("userNameN",notaryEmail);
                params.put("userNameS",signerRegData.getEmail());
                params.put("firstName",signerRegData.getFirstName());
                params.put("lastName",signerRegData.getLastName());
                params.put("phone",signerRegData.getPhoneNo());
                params.put("address1",signerRegData.getAddressOne());
                params.put("address2",signerRegData.getAddressTwo());
                params.put("city",signerRegData.getCityName());
                params.put("state",signerRegData.getStateName());
                params.put("zip",signerRegData.getZipCode());
                params.put("scan_reference","");
                params.put("id",rowId);
                params.put( "country",countryCodeValue);
                 params.put("userCat","WIT");*/
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.SAVE_SIGNER,"saveSigner");
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
                        if(type.equals("saveSigner")) {
                            //new SaveSigner().execute();
                            if(new JSONObject(responseData.getString("success")).getString("body")
                                    .equalsIgnoreCase("1")){
                                new SaveSigner().execute();
                            }else {
                                Toast toast = Toast.makeText(getActivity(),
                                        new JSONObject(responseData.getString("success")).getString("body"),
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                           //uploadFile();
                        }else if(type.equals("saveWitness")){
                           // uploadWitnessFile();
                        }else if(type.equals("email")){
                            String success = responseData.getString("success");
                            if (success.equals("1117")) { //
                              //  new SaveSigner().execute();
                                addWitnessSigner();
                                //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                            }else if(success.equals("1118")){
                               // new SaveSigner().execute();
                                addWitnessSigner();
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
                addWitnessSigner();

            }
        });
        dialog.show();
    }
}
