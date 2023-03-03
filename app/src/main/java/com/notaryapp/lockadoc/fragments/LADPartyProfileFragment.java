package com.notaryapp.lockadoc.fragments;

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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.personallyknown.Notarize_SignerPersonalPreviewFragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signergvtid.Notarize_SignerProfileFragment.PICK_IMAGE;


public class LADPartyProfileFragment extends Fragment {

    private Button btnBack, btnClose;
    private ImageView imageInfo, imgProEdit, emailInfo, confirmEmailInfo, phoneInfo;
    private LADParties signerReg;
    private DatabaseClient databaseClient;
    private final String TAG = "EditSignerFragment";
    private View view;
    private Button submitBtn;
    private CircleImageView imgProfile;
    private String fName, lName, email, phone, address1, address2, city, state, zip;
    private boolean validFName, validLName, validEmail, validCEmail,  validPhone, validAddress1, validCity, validState, validZip;
    private TextInputEditText firstNameEdt, lastNameEdt, emailIdEdt, confirmEmailIdEdt, phoneNoEdt, addressOneEdt,
            addressTwoEdt, cityNameEdt, stateNameEdt, zipCodeEdt;
    private TextInputLayout firstNameText, lastNameText, emailText, confirmEmailText, phoneText, address1Text, address2Text, cityText, stateText, zipText;
    private String notaryEmail, rowId;

    private IJsonListener iJsonListener;
    private String selfiePath, signerImg;
    private Bitmap bitmap;
    private Info info;
    private TelephonyManager tm;
    private String countryCodeValue, tranId;
    private Spinner spinnerState;
    private List<String> stateList;

    public LADPartyProfileFragment(String signerImg) {
        this.signerImg = signerImg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.lad_fragment_edit_signer_personal, container, false);
        init();
        tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        countryCodeValue = tm.getNetworkCountryIso();
        if(countryCodeValue == null || countryCodeValue.equalsIgnoreCase("")){
            countryCodeValue = "US";
        }
        buttonControls();
        return view;
    }

    private void buttonControls() {
//        phoneNoEdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(new Notarize_SignerPersonalPreviewFragment(signerImg));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(new Notarize_SignerPersonalPreviewFragment(signerImg));
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
               /* if (Validation.hasValue(phoneNo,phoneText)) {
                    validPhone = Validation.isSignerPhoneNumber(phoneNo, phoneText, true);
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

      /*  stateNameEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    zipText.setError(null);
                } else {
                    validZip = false;
                    zipText.setError("Enter valid zip code");
                }
            }
        });
        //Button click to start next fragment.
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
               /* if (!Validation.hasValue(stateNameEdt,stateText)) {
                    stateText.setError(null);
                    stateText.setErrorEnabled(false);
                    stateText.setError("Enter state name");
                } else {
                    validState = true;
                }*/
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
                /*if (isValidEmailId(emailIdEdt.getText().toString().trim())) {

                }*/
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
                                //  state = stateNameEdt.getText().toString().trim();
                                zip = zipCodeEdt.getText().toString().trim();

                                signerReg = new LADParties();
                                databaseClient = DatabaseClient.getInstance(getActivity());
                                signerReg.setFirstName(fName);
                                signerReg.setLastName(lName);
                                signerReg.setEmail(email);
                                signerReg.setPhoneNo(phone);
                                signerReg.setAddressOne(address1);
                                signerReg.setAddressTwo(address2);
                                signerReg.setCityName(city);
                                signerReg.setStateName(state);
                                signerReg.setZipCode(zip);
                                signerReg.setSignerType("PER");
                                signerReg.setProImagePath(signerImg);
                                signerReg.setWitness(false);
                                //     AddPersonalSigner();
                                //emailCheck();
                                confirmFormSubmit();
                                //        startActivity(new Intent(getActivity(), NotarizeStepsActivity.class));
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
                            //  state = stateNameEdt.getText().toString().trim();
                            zip = zipCodeEdt.getText().toString().trim();

                            signerReg = new LADParties();
                            databaseClient = DatabaseClient.getInstance(getActivity());
                            signerReg.setFirstName(fName);
                            signerReg.setLastName(lName);
                            signerReg.setEmail(email);
                            signerReg.setPhoneNo(phone);
                            signerReg.setAddressOne(address1);
                            signerReg.setAddressTwo(address2);
                            signerReg.setCityName(city);
                            signerReg.setStateName(state);
                            signerReg.setZipCode(zip);
                            signerReg.setSignerType("PER");
                            signerReg.setProImagePath(signerImg);
                            signerReg.setWitness(false);
                            //     AddPersonalSigner();
                            //emailCheck();
                            confirmFormSubmit();
                            //        startActivity(new Intent(getActivity(), NotarizeStepsActivity.class));
                        }

                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
                    }

                } else {
                    //Toast.makeText(getActivity(), "Invalid mail id", Toast.LENGTH_LONG).show();
                }
            }

        });
        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomDialog.notaryappInfoDialog(getActivity(),info.getAddress());
                CustomDialog.notaryappInfoDialog(getActivity(), "Only the party(ies) email address and phone number are mandatory. However, we recommend that you enter as much as information about the party as possible because if you ever find yourself in an unfortunate situation regarding this contract, the information might be important.");
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
        imgProEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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

    /*boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }*/

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
                imgProfile.setImageURI(selectedImageUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    //  saveNewSelfie(imgProName,bitmap);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
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

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    private void init() {
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
        new GeInfo().execute();
        new GetStates().execute();
        imageInfo = view.findViewById(R.id.infoIcon);
        emailInfo = view.findViewById(R.id.infoEmailIcon);
        confirmEmailInfo = view.findViewById(R.id.infoCEmailIcon);
        phoneInfo = view.findViewById(R.id.infoPhoneIcon);
        btnBack = view.findViewById(R.id.btn_pro_back);
        btnClose = view.findViewById(R.id.btn_pro_close);
        submitBtn = view.findViewById(R.id.submitBtn);
        imgProfile = view.findViewById(R.id.imageProfile);
        imgProEdit = view.findViewById(R.id.imgProEdit);

        firstNameEdt = view.findViewById(R.id.firstName);
        lastNameEdt = view.findViewById(R.id.lastName);
        emailIdEdt = view.findViewById(R.id.email);
        confirmEmailIdEdt = view.findViewById(R.id.confirmEmail);
        phoneNoEdt = view.findViewById(R.id.phoneNo);
        addressOneEdt = view.findViewById(R.id.address1);
        addressTwoEdt = view.findViewById(R.id.address2);
        cityNameEdt = view.findViewById(R.id.city);
        //  stateNameEdt = view.findViewById(R.id.state);
        zipCodeEdt = view.findViewById(R.id.zip);
        firstNameText = view.findViewById(R.id.firstNameText);
        lastNameText = view.findViewById(R.id.lastNameText);
        emailText = view.findViewById(R.id.emailText);
        confirmEmailText = view.findViewById(R.id.confirmEmailText);
        phoneText = view.findViewById(R.id.phoneText);
        address1Text = view.findViewById(R.id.address1Text);
        address2Text = view.findViewById(R.id.address2Text);
        cityText = view.findViewById(R.id.cityText);
        // stateText = view.findViewById(R.id.stateText);
        zipText = view.findViewById(R.id.zipText);
        spinnerState = view.findViewById(R.id.stateSpinner);

        if (checkSelfie(getActivity(), signerImg)) {
            selfiePath = getSelfiePath(getActivity(), signerImg);
            bitmap = BitmapFactory.decodeFile(selfiePath);
            // profileImg.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
            imgProfile.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
        }
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

    private void uploadFile() {
        //final String token = "Bearer " + PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        CustomDialog.showProgressDialog(getActivity());
        
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppUrl.UPLOAD_CLIENT_SIGNER_WITNESS_DP, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        getActivity().finish();
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //   CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
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
                params.put("pUser", notaryEmail);
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
                params.put("file", new DataPart(signerImg, VolleyHelper
                        .getFileDataFromDrawable(getActivity().getApplicationContext(),
                                imgProfile.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(getActivity()).addToRequestQueue(multipartRequest);
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

    class SaveSigner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .ladPartiesDao()
                    .insert(signerReg);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            uploadFile();
        }
    }

    class SelectData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            //scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            notaryEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            rowId = databaseClient.getAppDatabase().transactionsDao().getTransactionId();
            tranId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
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

    private void AddPersonalSigner() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                params.put("userNameN", notaryEmail);
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
                params.put("scan_reference", "");
                params.put("id", rowId);
                params.put("country", countryCodeValue);
                params.put("userCat", "PER");
                //Log.w("RowPersonal",rowId);
                // Toast.makeText(getActivity(),rowId,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //e.printStackTrace();
            }
//            
//            postapiRequest.request(getActivity(),iJsonListener,params,url,"saveSigner");
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
//                            signerReg = new SignerReg();
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

                            if (success.equals("1117")) { //already registered as Client/Signer/Witness
                                AddPersonalSigner();
                                //  CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                            } else if (success.equals("1118")) {//New user
                                AddPersonalSigner();
                            } else if (success.equals("1116")) {
                                //CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Notary");
                                AddPersonalSigner();
                            } else {

                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  CustomDialog.notaryappDialogSingle(getActivity(),e.getMessage());
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                // CustomDialog.notaryappDialogSingle(getActivity(),"errorMess");
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(getActivity());
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
        String path = selfie_new.getAbsolutePath();
        // uploadFile();

        return imgUri;
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
                AddPersonalSigner();

            }
        });
        dialog.show();
    }
}
