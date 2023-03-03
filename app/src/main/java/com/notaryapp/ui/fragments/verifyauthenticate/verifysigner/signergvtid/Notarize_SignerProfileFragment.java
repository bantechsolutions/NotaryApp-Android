package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signergvtid;

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
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
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

import androidx.annotation.Nullable;
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
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.ui.activities.verifyauthenticate.NotarizeStepsActivity;
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


public class Notarize_SignerProfileFragment extends Fragment {

    private Button btnBack, btnClose;
    private SignerReg signerReg;
    private DatabaseClient databaseClient;
    private final String TAG = "EditSignerFragment";
    private View editSignerView;
    private Button submitBtn;
    private CircleImageView imgProfile;
    private String fName = "", lName = "", email, phone, address1, address2, city, state, zip, idType=" ";
    private boolean validFName, validLName, validEmail, validCEmail, validPhone, validAddress1, validCity, validState, validZip;
    private TextInputEditText firstNameEdt, lastNameEdt, emailIdEdt, confirmEmailIdEdt, phoneNoEdt, addressOneEdt,
            addressTwoEdt, cityNameEdt, stateNameEdt, zipCodeEdt;
    private TextInputLayout firstNameText, lastNameText, emailText, phoneText, confirmEmailText, address1Text, address2Text, cityText, stateText, zipText;

    private Info info;
    private ImageView imageInfo, emailInfo, confirmEmailInfo, phoneInfo;
    private Context context;
    private String scanRef, rowId;
    private JumioScanDetails scanDetails;
    private String savedEmail;
    private IJsonListener iJsonListener;
    public static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String downloadUrl;
    private String proImgName;
    private TelephonyManager tm;
    private String countryCodeValue, tranId;
    private Spinner spinnerState;
    private List<String> stateList;
    //  private String fName,lName;
    private String image;

    public Notarize_SignerProfileFragment(String firstName, String lastName, String scanRef, String idType, String image) {
        this.fName = firstName;
        this.lName = lastName;
        this.scanRef = scanRef;
        this.idType = idType;
        this.image = image;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editSignerView = inflater.inflate(R.layout.fragment_notarize_signer_profile, container, false);
        init();
        tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        if(countryCodeValue == null || countryCodeValue.equalsIgnoreCase("")){
            countryCodeValue = "US";
        }
        buttonControls();

//        phoneNoEdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        return editSignerView;
    }

    private void init() {

        initIJsonListener();
        btnBack = editSignerView.findViewById(R.id.btn_pro_back);
        btnClose = editSignerView.findViewById(R.id.btn_pro_close);
        submitBtn = editSignerView.findViewById(R.id.submitBtn);
        imgProfile = editSignerView.findViewById(R.id.imageProfile);
        imageInfo = editSignerView.findViewById(R.id.infoIcon);
        emailInfo = editSignerView.findViewById(R.id.infoEmailIcon);
        confirmEmailInfo = editSignerView.findViewById(R.id.infoCEmailIcon);
        phoneInfo = editSignerView.findViewById(R.id.infoPhoneIcon);

        firstNameEdt = editSignerView.findViewById(R.id.firstName);
        lastNameEdt = editSignerView.findViewById(R.id.lastName);
        emailIdEdt = editSignerView.findViewById(R.id.email);
        confirmEmailIdEdt = editSignerView.findViewById(R.id.confirmEmail);
        phoneNoEdt = editSignerView.findViewById(R.id.phoneNo);
        addressOneEdt = editSignerView.findViewById(R.id.address1);
        addressTwoEdt = editSignerView.findViewById(R.id.address2);
        cityNameEdt = editSignerView.findViewById(R.id.city);
        // stateNameEdt = editSignerView.findViewById(R.id.state);
        zipCodeEdt = editSignerView.findViewById(R.id.zip);
        firstNameText = editSignerView.findViewById(R.id.firstNameText);
        lastNameText = editSignerView.findViewById(R.id.lastNameText);
        emailText = editSignerView.findViewById(R.id.emailText);
        confirmEmailText = editSignerView.findViewById(R.id.confirmEmailText);
        phoneText = editSignerView.findViewById(R.id.phoneText);
        address1Text = editSignerView.findViewById(R.id.address1Text);
        address2Text = editSignerView.findViewById(R.id.address2Text);
        cityText = editSignerView.findViewById(R.id.cityText);
        //  stateText = editSignerView.findViewById(R.id.stateText);
        zipText = editSignerView.findViewById(R.id.zipText);
        spinnerState = editSignerView.findViewById(R.id.stateSpinner);

        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();

        new GeInfo().execute();
        new GetStates().execute();
        long millis = System.currentTimeMillis();
        proImgName = "signer_" + millis + ".png";
    }

    private void showImage() {
        try {
            File f = new File(image);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            saveNewSelfie(proImgName, b);
        } catch (IOException e) {
            //e.printStackTrace();
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
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                // completedDialog();

                loadFragment(new Notarize_VerifyOkFragment(fName, lName, scanRef));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_VerifyOkFragment(fName, lName, scanRef));
                //  completedDialog();
            }
        });

        firstNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(firstNameEdt, firstNameText)) {
                    validFName = Validation.isFirstName(firstNameEdt, firstNameText, true);
                }
            }
        });
        lastNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(lastNameEdt, lastNameText)) {
                    validLName = Validation.isLastName(lastNameEdt, lastNameText, true);//hasText();
                }
            }
        });
        emailIdEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(emailIdEdt, emailText)) {
                    validEmail = Validation.isEmailAddress(emailIdEdt, emailText, true);
                } else {

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
               /* if (Validation.hasValue(phoneNo,phoneNoLay)) {
                    validPhone = Validation.isSignerPhoneNumber(phoneNo, phoneNoLay, true);
                } else {

                }*/
            }
        });

        addressOneEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(addressOneEdt, address1Text)) {
                    validAddress1 = Validation.hasValue(addressOneEdt, address1Text);
                }
            }
        });


        cityNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(cityNameEdt, cityText)) {
                    validCity = Validation.hasValue(cityNameEdt, cityText);
                }
            }
        });

     /*   stateNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(stateNameEdt,stateText)){
                    validState = Validation.hasValue(stateNameEdt,stateText);
                }
            }
        });*/

        zipCodeEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(zipCodeEdt, zipText)) {
                    validZip = Validation.hasValue(zipCodeEdt, zipText);
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
                if (s.length() == 5) {
                    validZip = true;
                    zipText.setError("");
                } else {
                    validZip = false;
                    zipText.setError("Enter valid zip code");
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validation.isFirstName(firstNameEdt, firstNameText, true)) {
                    firstNameText.setError(null);
                    firstNameText.setErrorEnabled(false);
                    firstNameText.setError("Use alphabets only");
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
                if (!Validation.hasValue(cityNameEdt, cityText)) {
                    cityText.setError(null);
                    cityText.setErrorEnabled(false);
                    cityText.setError("Enter city name");
                    validCity = false;
                } else {
                    cityText.setError(null);
                    validCity = true;
                }
                if (state.equals("Select State")) {
                    //    Toast.makeText(getActivity(), , Toast.LENGTH_LONG).show();
                    validState = false;
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please select your state");
                } else if (state.equals("null")) {
                    validState = false;
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please select your state");
                } else {
                    validState = true;
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
                        if (Validation.hasValue(phoneNoEdt, phoneText)) {
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
                                signerReg.setAddressOne(address1);
                                signerReg.setAddressTwo(address2);
                                signerReg.setCityName(city);
                                signerReg.setStateName(state);
                                signerReg.setZipCode(zip);
                                if (idType.equalsIgnoreCase("manually")){
                                    signerReg.setSignerType("IDMANUALLY");
                                } else {
                                    signerReg.setSignerType("GOV");
                                }
                                signerReg.setProImagePath(proImgName);
                                signerReg.setWitness(false);
                                // new SaveSigner().execute();

                                //emailCheck();
                                if (savedEmail.equalsIgnoreCase(email)){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a signer.");
                                } else {
                                    confirmFormSubmit();
                                    //AddSigner();
                                }
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
                        } else {
                            fName = firstNameEdt.getText().toString().trim();
                            lName = lastNameEdt.getText().toString().trim();
                            email = emailIdEdt.getText().toString().trim();
                            phone = phoneNoEdt.getText().toString().trim();
                            address1 = addressOneEdt.getText().toString().trim();
                            address2 = addressTwoEdt.getText().toString().trim();
                            city = cityNameEdt.getText().toString().trim();
//                            state = stateNameEdt.getText().toString().trim();
                            zip = zipCodeEdt.getText().toString().trim();

                            signerReg = new SignerReg();
                            signerReg.setFirstName(fName);
                            signerReg.setLastName(lName);
                            signerReg.setEmail(email);
                            signerReg.setPhoneNo(phone);
                            signerReg.setAddressOne(address1);
                            signerReg.setAddressTwo(address2);
                            signerReg.setCityName(city);
                            signerReg.setStateName(state);
                            signerReg.setZipCode(zip);
                            if (idType.equalsIgnoreCase("manually")){
                                signerReg.setSignerType("IDMANUALLY");
                            } else {
                                signerReg.setSignerType("GOV");
                            }
                            signerReg.setProImagePath(proImgName);
                            signerReg.setWitness(false);
                            // new SaveSigner().execute();

                            //emailCheck();
                            if (savedEmail.equalsIgnoreCase(email)){
                                CustomDialog.notaryappDialogSingle(getActivity(),"You cannot verify yourself as a signer.");
                            } else {
                                confirmFormSubmit();
                                //AddSigner();
                            }
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
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

    }


    class GetStates extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            stateList = databaseClient.getAppDatabase().statesDao().getStateName();
            stateList.add(0, "Select State");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_view, stateList);
            spinnerState.setAdapter(stateAdapter);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, true);
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
            databaseClient.getAppDatabase().signerRegDao().deleteAll();

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

        if (data != null) {
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

                    saveNewSelfie(proImgName, bitmap);
                    //  uploadProfile();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
    }

    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

        //  newProfileImg = "selfie_new.png";

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) (myBitmap.getHeight() * (512.0 / myBitmap.getWidth()));
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
        Uri imgUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider", selfie_new);
        imgProfile.setImageURI(imgUri);
        String path = selfie_new.getAbsolutePath();

        return imgUri;
    }

    private void uploadProfile() {
        CustomDialog.showProgressDialog(getActivity());
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, AppUrl.UPLOAD_CLIENT_SIGNER_WITNESS_DP, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();

                        Intent intent = new Intent(context, NotarizeStepsActivity.class);
                        context.startActivity(intent);
                        getActivity().finish();

                    } else {
                        getActivity().finish(); // Sourav 20201127
                        //Log.d(TAG, "Response: " + resultResponse);
                        //  CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    getActivity().finish(); // Sourav 20201127
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                getActivity().finish(); // Sourav 20201127
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
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
                params.put("userName", email);
                params.put("pUser", savedEmail);
                params.put("tranId", tranId);
                params.put("userType", "Sr");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imgProfile == null) {
                    //Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(proImgName, VolleyHelper
                        .getFileDataFromDrawable(getActivity(),
                                imgProfile.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
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

    class SaveSigner extends AsyncTask<Void, Void, Void> {
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
            // startActivity(new Intent(getActivity(), NotarizeStepsActivity.class));
            uploadProfile();

        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            savedEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            rowId = databaseClient.getAppDatabase().transactionsDao().getTransactionId();
            tranId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            if (!idType.equalsIgnoreCase("manually")){
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
                //   scanRef = scanDetails.getScanRef();
                apiToken = jumioKeys.getJumiokey();
                apiSecret = jumioKeys.getJumiosecret();
                //Log.e("Jumio",apiToken+"    "+apiSecret);
                dataCenter = JumioDataCenter.US;
                downloadUrl = AppUrl.JUMIO_BASE_URL + scanRef + "/images/face";
            }
            //downloadProfile(downloadUrl); //Sourav 20201213
            showImage();//Sourav 20201213
//            placeImage();
        }

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

    private void downloadProfile(String url) {
        //  scanRefference = "1ccecf1f-8ebc-4b53-ae3c-63a807899d01";
        CustomDialog.showProgressDialog(getActivity());
        // String url = AppUrl.JUMIO_BASE_URL +scanRefference+"/images/face";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(apiToken, apiSecret);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .header("Accept", "image/jpeg , image/png")
                                .header("User-Agentvalue", "Veritale Data Solutions ,Inc. notaryapp/1.0")
                                .build();
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(url)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .resize(200, 200)
                .into(imgProfile);
        CustomDialog.cancelProgressDialog();
        picasso.load(url)
                .centerCrop()
                .resize(200, 200)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        saveNewSelfie(proImgName, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
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

    private void AddSigner() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userNameN", savedEmail);
                params.put("userNameS", email);
                params.put("firstName", fName);
                params.put("lastName", lName);
                params.put("phone", phone.replace(" ", "")
                        .replace("(", "")
                        .replace(")", "").replace("-", ""));
                params.put("address1", address1);
                params.put("address2", address2);
                params.put("city", city);
                params.put("state", state);
                params.put("zip", zip);
                //params.put("phone ",email);
                params.put("scan_reference", scanRef);
                //  params.put("scan_reference","0dd7b599-bf4d-4892-93b5-786585e854dd");
                params.put("id", rowId);
                params.put("country", countryCodeValue);
                if (idType.equalsIgnoreCase("manually")){
                    params.put("userCat", "IDMANUALLY");
                } else {
                    params.put("userCat", "GOV");
                }
                //params.put("id","260");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.SAVE_SIGNER, "saveSigner");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {

                        if (type.equals("saveSigner")) {
                            //new SaveSigner().execute();
                            if (new JSONObject(responseData.getString("success")).getString("body")
                                    .equalsIgnoreCase("1")) {
                                new SaveSigner().execute();
                            } else {
                                /*Toast toast = Toast.makeText(getActivity(),
                                        new JSONObject(responseData.getString("success")).getString("body"),
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();*/
                                CustomDialog.notaryappDialogSingle(getActivity(),new JSONObject(responseData.getString("success")).getString("body"));
                            }
                        } else if (type.equals("email")) {
                            String success = responseData.getString("success");
                            if (success.equals("1117")) { //Already signer/client/witness
                                AddSigner();
                                //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                            } else if (success.equals("1118")) {//New User
                                AddSigner();
                            } else if (success.equals("1116")) {
                                CustomDialog.notaryappDialogSingle(getActivity(), "This email ID is already registered as Notary");
                            } else {

                            }
                        }
                    }
                } catch (Exception e) {
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
                AddSigner();

            }
        });
        dialog.show();
    }
}
