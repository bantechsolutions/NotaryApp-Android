package com.notaryapp.ui.fragments.registration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.R;
import com.notaryapp.components.ScrollViewExt;
import com.notaryapp.interfacelisterners.NestedScrollViewListener;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.ui.activities.onboarding.PrivacyPolicy;
import com.notaryapp.ui.activities.onboarding.TermsAndService;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpFragment extends Fragment implements NestedScrollViewListener, Animation.AnimationListener {

    private CheckBox checkBox;
    private TextView termsText,privacyPolicy;
    private TextInputEditText regEdit_LastName;
    private TextInputEditText regEdit_Email;
    private TextInputEditText regEdit_PhoneNo;
    private TextInputEditText regEdit_Password;
    private TextInputEditText regEdit_ConfirmPwd;
    private TextInputEditText regEdit_FirstName;
    private Button regButtonSub;
    private ConstraintLayout regRelativeLayout;
    private ScrollViewExt regScroll;
    private ScrollView scrollView;
    private ImageButton regBackBtn;
    private Animation animMoveUp;
    private View signUpView;
    private final String TAG = "SignUpFragment";
    private IJsonListener iJsonListener;
    private Context context;
    private String firstName, lastName, email, phone, password, appId = "",deviceId;
    private boolean validFName, validLName, validEmail, validPhone, validPassword, validCPassword;
    private UserReg userReg;
    private DatabaseClient databaseClient;
    private boolean permission = false;
    private ImageView imgInfo;
    private Info info;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Map<String, Locale> localeMap;;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private String countryCodeFromMap, countryNameFromMap;
    private boolean fromPermission;

    private TextInputLayout textInputLayoutFName, textInputLayoutLName, textInputLayoutEmail,
            textInputLayoutPhone, textInputLayoutPassword, textInputLayoutCPassword;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        activity = getActivity();
        signUpView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = getActivity().getApplicationContext();
        databaseClient = DatabaseClient.getInstance(context);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        checkGPSPermission();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        getCountry(lat,lon);

                        if (mFusedLocationProviderClient != null) {
                            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
        getLocation();


        init();

        regButtonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validation.hasFirstText(regEdit_FirstName, textInputLayoutFName)) {
                    validFName = true;
                } else {
                    textInputLayoutFName.setError(null);
                    textInputLayoutFName.setErrorEnabled(false);
                    textInputLayoutFName.setError("Enter FirstName");
                    validFName = false;

                }
                if (Validation.hasValue(regEdit_LastName, textInputLayoutLName)) {
                    validLName = true;
                } else {
                    textInputLayoutLName.setError(null);
                    textInputLayoutLName.setErrorEnabled(false);
                    textInputLayoutLName.setError("Enter LastName");
                    validLName = false;

                }
                if (Validation.hasEmailText(regEdit_Email, textInputLayoutEmail)) {
                    validEmail = true;
                } else {
                    textInputLayoutEmail.setError(null);
                    textInputLayoutEmail.setErrorEnabled(false);
                    textInputLayoutEmail.setError("Enter Email");
                    validEmail = false;

                }
                /*if (!Validation.isPhoneNumberForSignUp(regEdit_PhoneNo, textInputLayoutPhone,true)) {
                    textInputLayoutPhone.setError(null);
                    textInputLayoutPhone.setErrorEnabled(false);
                    textInputLayoutPhone.setError("Enter Phone number");
                    validPhone = false;
                } else {
                    validPhone = true;
                }*/
                if (!Validation.hasPhoneTextSignup(regEdit_PhoneNo, textInputLayoutPhone)) {
                    textInputLayoutPhone.setError(null);
                    textInputLayoutPhone.setErrorEnabled(false);
                    textInputLayoutPhone.setError("Enter valid phone number");
                    validPhone = false;
                } else {
                    textInputLayoutPhone.setError(null);
                    validPhone = true;
                }
                if(Validation.hasValue(regEdit_Password, textInputLayoutPassword)){
                    if(regEdit_Password.getText().toString().equals(regEdit_ConfirmPwd.getText().toString())){
                        validPassword = true;
                    }else{
                        validPassword = false;
                    }

                }else{
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                    textInputLayoutPassword.setError("Enter Password");
                    validPassword = false;

                }
                if(Validation.hasValue(regEdit_ConfirmPwd, textInputLayoutCPassword)){
                    if(regEdit_Password.getText().toString().equals(regEdit_ConfirmPwd.getText().toString())){
                        validCPassword = true;
                    }else{
                        validCPassword = false;
                    }
                }else{
                    textInputLayoutCPassword.setError(null);
                    textInputLayoutCPassword.setErrorEnabled(false);
                    textInputLayoutCPassword.setError("Enter Confirm Password");
                    validCPassword = false;

                }
               /* if (!Validation.passwordValidation(regEdit_Password, textInputLayoutPassword,true)) {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                    textInputLayoutPassword.setError("Enter Password");
                } else {
                    validPassword = true;
                }
                if (!Validation.passwordValidation(regEdit_ConfirmPwd, textInputLayoutCPassword,true)) {
                    textInputLayoutCPassword.setError(null);
                    textInputLayoutCPassword.setErrorEnabled(false);
                    textInputLayoutCPassword.setError("Enter Confirm Password");
                } else {
                    validCPassword = true;
                }*/
              /*  if (!Validation.hasPasswordMatched(regEdit_Password, regEdit_ConfirmPwd, textInputLayoutPassword)) {
                    textInputLayoutCPassword.setError(null);
                    textInputLayoutCPassword.setErrorEnabled(false);
                    textInputLayoutCPassword.setError("Passwords should match");
                    validPassword = false;
                    validCPassword = false;
                } else {
                    validPassword = true;
                    validCPassword = true;
                }*/

                if (validFName && validLName && validEmail && validPhone && validPassword && validCPassword) {

                    if(checkBox.isChecked()) {



                        if (checkPermission()) {

                            getData();
                            emailCheck();
                            validFName = false;
                            validLName = false;
                            validEmail = false;
                            validPhone = false;
                            validPassword = false;
                            validCPassword = false;

                        } else {
                            requestPermission();
                        }



                    }
                    else{

                        CustomDialog.notaryappDialogSingle(getActivity(), "Please accept terms & condition.");
                    }


                    //Testing
                    //loadFragment(new AllowAccessFragment());
                    //Testing
                } else {
                    validFName = Validation.hasFirstText(regEdit_FirstName);
//                    if(!validFName){
//                        textInputLayoutFName.setError("Enter FirstName");
//                    }
                }
            }
        });
        regEdit_FirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_FirstName, textInputLayoutFName)) {
                    validFName = Validation.isFirstName(regEdit_FirstName, textInputLayoutFName, true);
                }
            }
        });

        regEdit_FirstName.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Validation.hasValue(regEdit_FirstName, textInputLayoutFName)) {
                    validFName = Validation.isFirstName(regEdit_FirstName, textInputLayoutFName, true);
                }
            }
        });

        regEdit_LastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_LastName, textInputLayoutLName)) {
                    validLName = Validation.isLastName(regEdit_LastName, textInputLayoutLName, true);
                }
            }
        });

        regEdit_LastName.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Validation.hasValue(regEdit_LastName, textInputLayoutLName)) {
                    validLName = Validation.isLastName(regEdit_LastName, textInputLayoutLName, true);
                }
            }
        });

        regEdit_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_Email, textInputLayoutEmail)) {
                    validEmail = Validation.isEmailAddress(regEdit_Email, textInputLayoutEmail, true);
                } else {

                }
            }
        });

        regEdit_Email.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Validation.hasValue(regEdit_Email, textInputLayoutEmail)) {
                    validEmail = Validation.isEmailAddress(regEdit_Email, textInputLayoutEmail, true);
                }
            }
        });

        ///lahar
        /*regEdit_PhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_PhoneNo, textInputLayoutPhone)) {
                    validPhone = Validation.isPhoneNumber(regEdit_PhoneNo, textInputLayoutPhone, true);
                } else {

                }
            }
        });*/

//        regEdit_PhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        regEdit_Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_Password, textInputLayoutPassword)) {
                    validPassword = Validation.passwordValidation(regEdit_Password, textInputLayoutPassword, true);

                    //validCPassword = Validation.hasPasswordMatched(regEdit_Password, regEdit_ConfirmPwd, textInputLayoutPassword);
                }
            }
        });

        regEdit_Password.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        regEdit_Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //scrollView.fullScroll(View.FOCUS_DOWN);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasValue(regEdit_Password, textInputLayoutPassword)) {
                    validPassword = Validation.passwordValidation(regEdit_Password, textInputLayoutPassword, true);
                    validCPassword = Validation.passwordValidation(regEdit_ConfirmPwd, textInputLayoutCPassword, true);

                }
                validCPassword = Validation.hasValue(regEdit_ConfirmPwd, textInputLayoutCPassword);
                if (validPassword && validCPassword) {
                    validCPassword = Validation.hasPasswordMatched(regEdit_Password, regEdit_ConfirmPwd, textInputLayoutPassword);

                }
            }
        });

        regEdit_ConfirmPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(regEdit_ConfirmPwd, textInputLayoutCPassword)) {
                    validCPassword = Validation.passwordValidation(regEdit_Password, textInputLayoutCPassword, true);
                    validCPassword = Validation.hasPasswordMatched(regEdit_Password, regEdit_ConfirmPwd, textInputLayoutCPassword);
                }
            }
        });


        regEdit_ConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasValue(regEdit_ConfirmPwd, textInputLayoutCPassword)) {
                    validCPassword = Validation.passwordValidation(regEdit_ConfirmPwd, textInputLayoutCPassword, true);
                    validPassword = Validation.passwordValidation(regEdit_Password, textInputLayoutPassword, true);
                    validPassword = Validation.hasPasswordMatched(regEdit_Password, regEdit_ConfirmPwd, textInputLayoutCPassword);
                }
            }
        });

        regEdit_ConfirmPwd.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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


        regBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  balloon.showAlignTop(imgInfo);

                CustomDialog.notaryappInfoDialog(getActivity(),info.getPassword());
            }
        });
        return signUpView;
    }

    private void checkGPSPermission() {


        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@io.reactivex.annotations.NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(activity, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        //e1.printStackTrace();
                    }
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);

        } else {

            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (OTPFragment.checkOtpActivity) {
            regEdit_FirstName.setEnabled(false);
            regEdit_LastName.setEnabled(false);
            regEdit_PhoneNo.setEnabled(false);
            regEdit_Password.setEnabled(false);
            regEdit_ConfirmPwd.setEnabled(false);
            regEdit_FirstName.setFocusable(false);
            regEdit_LastName.setFocusable(false);
            regEdit_PhoneNo.setFocusable(false);
            regEdit_Password.setFocusable(false);
            regEdit_ConfirmPwd.setFocusable(false);
            regEdit_FirstName.setClickable(false);
            regEdit_LastName.setClickable(false);
            regEdit_PhoneNo.setClickable(false);
            regEdit_Password.setClickable(false);
            regEdit_ConfirmPwd.setClickable(false);
            regEdit_Email.requestFocus();
        }
        OTPFragment.checkOtpActivity = false;
    }

    private void init() {
        regEdit_FirstName = signUpView.findViewById(R.id.firstName);
        regEdit_LastName = signUpView.findViewById(R.id.lastName);
        regEdit_Email = signUpView.findViewById(R.id.email);
        regEdit_PhoneNo = signUpView.findViewById(R.id.phoneNo);
        regEdit_Password = signUpView.findViewById(R.id.password);
        regEdit_ConfirmPwd = signUpView.findViewById(R.id.confirmPassword);
        regButtonSub = signUpView.findViewById(R.id.submitBtn);
        regRelativeLayout = signUpView.findViewById(R.id.footer);
        regScroll = signUpView.findViewById(R.id.regScrollView);
        regBackBtn = signUpView.findViewById(R.id.backBtn);
        textInputLayoutFName = signUpView.findViewById(R.id.firstNameText);
        textInputLayoutLName = signUpView.findViewById(R.id.lastNameText);
        textInputLayoutEmail = signUpView.findViewById(R.id.emailText);
        textInputLayoutPhone = signUpView.findViewById(R.id.phoneText);
        textInputLayoutPassword = signUpView.findViewById(R.id.passwordText);
        textInputLayoutCPassword = signUpView.findViewById(R.id.confirmPasswordText);
        scrollView = signUpView.findViewById(R.id.scrollview);
        imgInfo = signUpView.findViewById(R.id.infoIcon);
        checkBox = signUpView.findViewById(R.id.checkBox);

        termsText = signUpView.findViewById(R.id.termsText);
        privacyPolicy = signUpView.findViewById(R.id.privacyPolicy);

        initIJsonListener();
        userReg = new UserReg();
        new GeInfo().execute();

        animMoveUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.move_up);
        animMoveUp.setAnimationListener(this);
        regScroll.setScrollViewListener(this);
        checkCameraPermission();
        deviceId =  Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity().getApplicationContext(), TermsAndService.class));
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity().getApplicationContext(), PrivacyPolicy.class));
            }
        });
        //lahar
        //regEdit_PhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void getData() {
        try {
            getDeviceLocation();
            firstName = regEdit_FirstName.getText().toString().trim();
            lastName = regEdit_LastName.getText().toString().trim();
            email = regEdit_Email.getText().toString().trim();
            phone = regEdit_PhoneNo.getText().toString()
                    .replace("(","")
                    .replace(")","")
                    .replace(" ","")
                    .replace("-","")
                    .trim();
            if(phone != null
               && phone.length() == 11){
                phone = Utils.removeFirstCharecter(phone);
            }
            password = regEdit_Password.getText().toString().trim();
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@io.reactivex.annotations.NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

//                                Toast.makeText(MapActivity.this, "last location "+String.valueOf(mLastKnownLocation.getLatitude())+String.valueOf(mLastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();

//                                mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude();

                                getCountry(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {

//                                            Toast.makeText(MapActivity.this, "null location "+String.valueOf(mLastKnownLocation.getLatitude())+String.valueOf(mLastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        getCountry(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

//                                        Toast.makeText(MapActivity.this, "current location "+String.valueOf(mLastKnownLocation.getLatitude())+String.valueOf(mLastKnownLocation.getLongitude()), Toast.LENGTH_SHORT).show();

                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
//                            Toast.makeText(MapActivity.this, "unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void getCountry(double latitude, double longitude){

        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            countryCodeFromMap = addresses.get(0).getCountryCode();
            countryNameFromMap = addresses.get(0).getCountryName();

            if(fromPermission){
                fromPermission = false;
                registerApiCall();
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }


    private void loadFragment(Fragment fragment) {

        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            permission = true;

        } else {
            // Do something, when permissions are already granted
            permission = false;
        }
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
        // We take the last son in the scrollview
        /*View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            regRelativeLayout.setVisibility(View.VISIBLE);
            regButtonSub.startAnimation(animMoveUp);
            //  btnConfirm.startAnimation(animMoveUp);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    regButtonSub.clearAnimation();
                    // btnConfirm.clearAnimation();
                }
            }, 300);


        } else if (diff > 0) {

            regButtonSub.clearAnimation();
            //  btnConfirm.clearAnimation();
            regRelativeLayout.setVisibility(View.INVISIBLE);

        }*/
    }
    class SaveUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            userReg.setFirstName(firstName);
            userReg.setLastName(lastName);
            userReg.setEmail(email);
            userReg.setAppId(appId);
            userReg.setPhoneNo(phone);
            userReg.setSuccess(true);

            int count = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getCount();
            //adding to database
            if (count == 0) {
                databaseClient.getAppDatabase()
                        .userRegDao()
                        .insert(userReg);
            } else {
                databaseClient.getAppDatabase()
                        .userRegDao()
                        .update(email, appId,firstName, lastName, phone, 1);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
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

    private void requestPermission() {


        requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                },
                PERMISSION_REQUEST_CODE
        );
    }

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        int result1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void registerApiCall() {
        CustomDialog.showProgressDialog(activity);

        initCountryCodeMapping();

        String countryCodeValue = "US";
        TelephonyManager tm  = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {

            if(tm.getSimCountryIso().equalsIgnoreCase(tm.getNetworkCountryIso())) {
                countryCodeValue = tm.getSimCountryIso();
                if(countryCodeValue.equals("") || countryCodeValue.equals("null")){
                    countryCodeValue = tm.getNetworkCountryIso();
                }
            }
        }

        if(countryCodeValue.equals("") || countryCodeValue.equals("null")){
            countryCodeValue = "US";
        }


        String currentISO3CountryCode = new Locale("", countryCodeValue).getISO3Country();
        String countrycode  = iso3CountryCodeToIso2CountryCode(currentISO3CountryCode);

        if(countrycode.equalsIgnoreCase(countryCodeValue)){
            countryCodeValue = countrycode;
        }
        else {
            countryCodeValue = "US";
        }

        if(countryCodeFromMap != null) {
            if (countryCodeFromMap.equalsIgnoreCase(countryCodeValue)) {
                countryCodeValue = countryCodeFromMap;
            } else {
                if (countryNameFromMap.equalsIgnoreCase("United States")) {
                    countryCodeValue = "US";
                } else {
                    countryCodeValue = countryCodeFromMap;
                }
            }



            try {
                POSTAPIRequest postapiRequest = new POSTAPIRequest();
                JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
                try {
                    //Creating POST body in JSON format
                    //to send in POST request

                    params.put("country", countryCodeValue.toUpperCase());
                    params.put("first_name", firstName);
                    params.put("last_name", lastName);
                    params.put("phone", phone);
                    params.put("username", email);
                    params.put("password", password);
                    params.put("os", AppUrl.DEVICE);

                } catch (Exception e) {

                }
                //Log.d("SIGN_PARA", params.toString());
                //    postapiRequest.request(getActivity(), iJsonListener, params, url1, "register");
                //postapiRequest.requestWithOutAuth(getActivity(), iJsonListener, params, AppUrl.REGISTER_NOTARY, "register");
                postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.REGISTER_NOTARY, "register");
                //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        else{
            fromPermission = true;
            checkGPSPermission();
        }


    }

    private void initCountryCodeMapping() {

        String[] countries = Locale.getISOCountries();
        localeMap = new HashMap<String, Locale>(countries.length);
        for (String country : countries) {
            Locale locale = new Locale("", country);
            localeMap.put(locale.getISO3Country().toUpperCase(), locale);
        }
    }

    private String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        return localeMap.get(iso3CountryCode).getCountry();
    }

    private void emailCheck() {
        CustomDialog.showProgressDialog(activity);
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
            //postapiRequest.requestWithOutAuth(getActivity(), iJsonListener, params,  AppUrl.CHECK_EMAIL, "email");
            postapiRequest.request(getActivity(), iJsonListener, params,  AppUrl.CHECK_EMAIL, "email");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                        if (data.has("success")) {

                            String success = data.getString("success");
                            if (type.equals("register")) {
                                appId = data.getString("appID");
                                if(data.has("token")){
                                    PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("TOKEN", data.getString("token")).commit();

                                }
                                if (success.equals("1018")) { //already exists
                                    // Toast.makeText(getActivity().getApplicationContext(), "Invalid Emil", Toast.LENGTH_LONG).show();
                                    //EmailCheck
//                                    if (permission) {
//                                        startActivity(new Intent(getActivity(), Documents_RequiredActivity.class));
//                                    } else {
//                                        loadFragment(new AllowAccessFragment("1016"));
//                                    }
                                } else if (success.equals("1019")) {
                                    // Toast.makeText(getActivity().getApplicationContext(), "Registered successfully", Toast.LENGTH_LONG).show();
                                    new SaveUser().execute();
                                    loadFragment(new OTPFragment());
                                }
                            } else if (type.equals("email")) {
                                if (success.equals("1117")) { //Already signer/client/witness
                                   // new SaveUser().execute();
                                  //  registerApiCall();
                                    CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                                }else if(success.equals("1118")){//New User
                                    new SaveUser().execute();
                                   // callDeviceTracer();
                                    registerApiCall();
                                }else if(success.equals("1116")){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"User already registered with Notary-App®");
                                }
                            }else if(type.equals("DeviceTracer")) {
                                // String success = data.getString("success");
                                if (success.equalsIgnoreCase("0")) {
                                    CustomDialog.notaryappDialogSingle(getActivity(), "User already registered with same emailId or device");
                                } else {
                                   // registerApiCall();
                                }
                            }else{

                            }
                        }else {
                            CustomDialog.cancelProgressDialog();
                            // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                            CustomDialog.notaryappDialogSingle(getActivity(), "Error! No data fetched");
                        }
                        }
                        //EmailCheck

                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));

                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.cancelProgressDialog();
                CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean phone = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean location = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (phone && location)
                    {
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                        getData();
                        emailCheck();
                        validFName = false;
                        validLName = false;
                        validEmail = false;
                        validPhone = false;
                        validPassword = false;
                        validCPassword = false;
                    }

                    else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            showMessageOKCancel("Please grand the permissions to receive SMS",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{

                                                                Manifest.permission.READ_PHONE_STATE,
                                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                                Manifest.permission.ACCESS_COARSE_LOCATION

                                                        },
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;

                            /*if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("Please grand the permissions to receive SMS",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{

                                                                    Manifest.permission.READ_PHONE_STATE,
                                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION

                                                            },
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                                showMessageOKCancel("Please grand the permissions to receive SMS",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{

                                                                    Manifest.permission.READ_PHONE_STATE,
                                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION

                                                            },
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                            else{
                                showMessageOKCancel("Please grand the permissions to receive SMS",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{

                                                                    Manifest.permission.READ_PHONE_STATE,
                                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                                    Manifest.permission.ACCESS_COARSE_LOCATION

                                                            },
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }*/
                        }
                        else{

                            showMessageOKCancel("Please grand the permissions to receive SMS",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{

                                                                Manifest.permission.READ_PHONE_STATE,
                                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                                Manifest.permission.ACCESS_COARSE_LOCATION

                                                        },
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) {
                getDeviceLocation();
            }
            else{
                CustomDialog.cancelProgressDialog();
            }
        }

    }

}
