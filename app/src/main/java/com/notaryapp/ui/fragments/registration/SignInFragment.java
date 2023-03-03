package com.notaryapp.ui.fragments.registration;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.ProfilePicture;
import com.notaryapp.roomdb.entity.States;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.ui.activities.onboarding.NotaryPledgeActivity;
import com.notaryapp.ui.activities.onboarding.NotaryStampActivity;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.ui.activities.onboarding.ProfileActivity;
import com.notaryapp.ui.activities.youtubevideo.YoutubeVideoPlayActivity;
import com.notaryapp.ui.fragments.forgotpassword.ForgotPassword_EnterEmail;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignInFragment extends Fragment {

    //public static String proImageName;
    //="SignInFragment";
    private View signInView;
    private FragmentTransaction fragmentTransaction;
    private LinearLayout linearLayoutReg;
    private TextInputEditText loginEmailEdt;
    private TextInputEditText loginPwdEdt;
    private TextInputLayout emailHelper, passwordHelper;
    private Button loginBtn;
    private String loginEmail, loginPwd, savedEmail;
    //private boolean validEmail, validPwd;
    private IJsonListener iJsonListener;
    private DatabaseClient databaseClient;
    private TextView forgotPasswordTxt;
    private Context context;
    //private ImageView eyeShow, eyeHide;
    // private boolean completePledge, completeStamp, completeIdentity;
    private VACustomer memPlans;
    private int randomForImg, stateCount;
    private String appId, firstName, lastName, profileImageURL;
    private int userStatus;
    private ProfilePicture profilePicture;
    private VACustomer vaCustomer;
    private Activity activity;
    private States states;
    private ArrayList<States> arryStates = new ArrayList<>();

    private static final int PERMISSION_REQUEST_CODE = 200;
    private JSONObject jsonObjectValue;
    /////
    private static final String AUTH_KEY = "key=YOUR-SERVER-KEY";
    private TextView mTextView;
    private String token;
    ////

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private SharedPreferences pref;


    // AES Keys
    private final String secretKey = "4FE1qVbsS9fdggdggfdtvsrfdgqDjh2k1Ns8w9ryyu7de34OAhGo7E";
    private final String IV = "jYXDmwC6GnkFIKet3qqy9Xrswrwruc7XH0ewrwerHhcx1eerwwerfggL";

    byte[] encrypted, decrypted;

    ///
    private String userName=null, userPassword=null;
    private ProgressBar img_profile_progress;

    private ConstraintLayout videoOnboarding;

    private ProgressBar img_progress;

    private String goToscreen="";


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        signInView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        activity = getActivity();

        init();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        videoOnboarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), YoutubeVideoPlayActivity.class);
                intent.putExtra("YOUTUBE_URL", "PnnrgfggnEbfggjCWyjnQ");
                startActivity(intent);
            }
        });

        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginBtn.setEnabled(false);

                loginEmail = loginEmailEdt.getText().toString().trim();
                loginPwd = loginPwdEdt.getText().toString().trim();
                if (loginEmail.equals("") && loginPwd.equals("")) {
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please enter Username and Password");
                    loginBtn.setEnabled(true);
                } else if (loginEmail.equals("")) {
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please enter UserName");
                    loginBtn.setEnabled(true);
                } else if (loginPwd.equals("")) {
                    CustomDialog.notaryappDialogSingle(getActivity(), "Please enter Password");
                    loginBtn.setEnabled(true);
                } else {
                    callSignInApi();
                }
            }
        });
        linearLayoutReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignUpFragment());
                //Testing
                //loadFragment(new OTPFragment());
                //Testing
            }
        });
        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Testing
                //loadFragment(new ForgotPasswordFragment());
                //Testing
                loadFragment(new ForgotPassword_EnterEmail());
            }
        });

        //////
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCAM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
                    Log.i("FCAM TOKEN", token);
                }
            }
        });
        /////

        return signInView;

    }

    private void init() {
        linearLayoutReg = signInView.findViewById(R.id.regiLayout);
        loginBtn = signInView.findViewById(R.id.signInBtn);
        loginEmailEdt = signInView.findViewById(R.id.loginEmailId);
        loginPwdEdt = signInView.findViewById(R.id.loginPassword);
        forgotPasswordTxt = signInView.findViewById(R.id.forgotPassword);

        emailHelper = signInView.findViewById(R.id.loginEmailLay);
        passwordHelper = signInView.findViewById(R.id.loginpassLay);
        /*randomForImg = Utils.getRandomNumber();
        proImageName = randomForImg + ".png";*/
        videoOnboarding = signInView.findViewById(R.id.videoOnboarding);
        initIJsonListener();
        context = getActivity().getApplicationContext();

        ///// Bio
        executor =ContextCompat.getMainExecutor(context);
        biometricPrompt = new BiometricPrompt(SignInFragment.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                /*loginEmail = pref.getString("username", null);
                String decryptedText = pref.getString("password",null);


                decrypted = Base64.decode(decryptedText, Base64.DEFAULT);

                loginPwd = decrypt(decrypted, secretKey, IV);*/

                loginEmail = userName;
                loginPwd = userPassword;


                callSignInApi();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        //pref = this.getActivity().getSharedPreferences("login_details", Context.MODE_PRIVATE);

        try {
            userName = getEncryptedSharedPreferences().getString("username", null);
            userPassword = getEncryptedSharedPreferences().getString("password", null);
        } catch (GeneralSecurityException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        loginEmailEdt.setText(userName);

        ////


        databaseClient = DatabaseClient.getInstance(context);
        new GetStatesCount().execute();



        if (userName != null && !userName.isEmpty() && userPassword != null && !userPassword.isEmpty()){

            if (Build.VERSION.SDK_INT >=29 && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){

                try {
                    promptInfo =new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Unlock Notary-App®")
                            .setDescription("Confirm your screen lock PIN, Pattern or Password")
                            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                            .setConfirmationRequired(true)
                            .build();

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            biometricPrompt.authenticate(promptInfo);
                        }
                    },500);

                } catch (IllegalArgumentException e){

                }

            }


        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER, fragment, true);

    }

    class GetStatesCount extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            stateCount = databaseClient.getAppDatabase().statesDao().getcount();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (stateCount == 0) {
                callGetStates();
            }
        }
    }

    class SaveStates extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (stateCount == 0) {
                for (int i = 0; i < arryStates.size(); i++) {
                    databaseClient.getAppDatabase().statesDao().insert(arryStates.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    private void callGetStates() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();

            //getApiRequest.requestWithOutAuth(getActivity(), iJsonListener, AppUrl.GET_STATE_LIST, "NotaryState");
            getApiRequest.request(getActivity(), iJsonListener, AppUrl.GET_STATE_LIST, "NotaryState");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callActiveApi(){
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("username", loginEmail);
                /*params.put("password", loginPwd);
                params.put("os", AppUrl.DEVICE);
                params.put("registration_id", token);*/
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.REACTIVATE, "reactivate");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callSignInApi() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("username", loginEmail);
                params.put("password", loginPwd);
                params.put("os", AppUrl.DEVICE);
                params.put("registration_id", token);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.LOGIN, "signin");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void checkNotification() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.GET_SERVER_NOTIFICATION + "?userName=" + loginEmail;
            getApiRequest.request(getActivity(), iJsonListener, url, "ServerNotification");
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
                        if (type.equals("signin")) {
                            Log.d("SIGNIN", data.toString());
                            if (data.has("success")) {
                                String success = data.getString("success");

                                if (data.has("token")) {
                                    PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("TOKEN", data.getString("token")).commit();
                                }

                                if (success.equals("1")) {
                                    firstName = data.getString("first_name");
                                    lastName = data.getString("last_name");
                                    userStatus = Integer.parseInt(data.getString("active"));
                                    //String dpData = data.getString("dp");
                                    if (userStatus==0){
                                        if (data.has("dp")){
                                            JSONArray dpImage = data.getJSONArray("dp");
                                            if (dpImage.length() > 0) {
                                                JSONObject dpImage_obj = dpImage.getJSONObject(0);
                                                //profilePicture.setProfilePic(dpImage_obj.getString("url"));
                                                profileImageURL =  dpImage_obj.getString("url");
                                            }
                                        }

                                        CustomDialog.cancelProgressDialog();
                                        callActiveDialog();
                                    } else {
                                        if (data.has("dp")) {
                                        /*if (!(dpData.equals("null"))){
                                            profilePicture = new ProfilePicture();
                                            JSONArray dp = data.getJSONArray("dp");
                                            if (dp.length() > 0) {
                                                JSONObject dp_obj = dp.getJSONObject(0);
                                                profilePicture.setProfilePic(dp_obj.getString("url"));
                                                new SaveDp().execute();
                                            }*/
                                            profilePicture = new ProfilePicture();
                                            JSONArray dp = data.getJSONArray("dp");
                                            if (dp.length() > 0) {
                                                JSONObject dp_obj = dp.getJSONObject(0);
                                                profilePicture.setProfilePic(dp_obj.getString("url"));
                                                new SaveDp().execute();
                                            }
                                        /*profilePicture = new ProfilePicture();
                                        JSONArray dp = data.getJSONArray("dp");
                                        if (dp.length() > 0) {
                                            JSONObject dp_obj = dp.getJSONObject(0);
                                            profilePicture.setProfilePic(dp_obj.getString("url"));
                                            new SaveDp().execute();
                                        }*/

                                        }

                                        if (data.has("steps")) {
                                            checkSteps(data);
                                        }
                                    }


                                    //loginEmailEdt.setText("");
                                    //loginPwdEdt.setText("");
                                } else if (success.equals("1919")) {
                                    CustomDialog.cancelProgressDialog();
                                    CustomDialog.notaryappDialogSingle(getActivity(), "Invalid credentials");
                                    loginBtn.setEnabled(true);
                                    //  RequestQueueService.showAlert(, getActivity());
                                } else if (success.equals("1818")) {
                                    CustomDialog.cancelProgressDialog();
                                    CustomDialog.notaryappDialogSingle(getActivity(), "User is not registered with notaryapp");
                                    loginBtn.setEnabled(true);
                                    //  RequestQueueService.showAlert(, getActivity());
                                } else if (success.equals("1212")) {
                                    CustomDialog.cancelProgressDialog();
                                    CustomDialog.notaryappDialogSingle(getActivity(), "User exists, but not a Notary User");
                                    loginBtn.setEnabled(true);
                                    //  RequestQueueService.showAlert(, getActivity());
                                } else if (success.equals("0")) {
                                    if (data.has("OS")) {
                                        CustomDialog.cancelProgressDialog();
                                        CustomDialog.notaryappDialogSingle(getActivity(), "You registered with an iOS device & cannot log into an Android device with the same account. Try logging into an iOS device.");
                                        loginBtn.setEnabled(true);
                                    }
                                }
                            } else {

                                CustomDialog.cancelProgressDialog();
                                CustomDialog.notaryappDialogSingle(getActivity(), "User Not Registered");
                                loginBtn.setEnabled(true);
                            }
                        } else if (type.equals("NotaryState")) {
                            String stateCode, state;
                            JSONArray states_array = data.getJSONArray("states");
                            if (states_array.length() != 0) {
                                for (int i = 0; i < states_array.length(); i++) {
                                    JSONObject json_inside = states_array.getJSONObject(i);
                                    stateCode = json_inside.getString("state_code");
                                    state = json_inside.getString("state");
                                    states = new States(state, stateCode);
                                    arryStates.add(states);
                                }
                                new SaveStates().execute();
                            }
                        } else if (type.equals("reactivate")){
                            if (data.has("success")){
                                String success = data.getString("success");
                                if (success.equals("1")){
                                    callSignInApi();
                                }
                            }
                        } else if(type.equals("ServerNotification")){
                            //CustomDialog.cancelProgressDialog();
                            //Log.d("NOTIFICATION_DATA", data.toString());
                            if (data.has("count")){
                                String nCount = data.getString("count");
                                if (nCount.equalsIgnoreCase("1")){
                                    JSONObject json_data = data.getJSONObject("notification");
                                    String data_title = json_data.getString("title");
                                    String data_description = json_data.getString("description");
                                    String data_icon = json_data.getString("icon");
                                    showServerNotification(data_title, data_description, data_icon);
                                } else {
                                    if (goToscreen.equalsIgnoreCase("dashboard")){
                                        goToDashboard();
                                    } else {
                                        goToMembership();
                                    }

                                }
                            }
                        }

                        else {

                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(getActivity(), "Server Unavailable. Please try again later");
                    loginBtn.setEnabled(true);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.cancelProgressDialog();
                CustomDialog.notaryappDialogSingle(getActivity(), "Server Unavailable. Please try again later");
                loginBtn.setEnabled(true);
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    private void showServerNotification(String data_title, String data_description, String data_icon) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_notification_dialog);

        ImageView image = dialog.findViewById(R.id.imageUrl);
        TextView alertHead = dialog.findViewById(R.id.alertHead);
        TextView alertMessage = dialog.findViewById(R.id.alertMessage);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        Button btnClose = dialog.findViewById(R.id.btnClose);
        img_progress = dialog.findViewById(R.id.img_progress);


        alertHead.setText(data_title);
        alertMessage.setText(data_description);

        if(!data_icon.equalsIgnoreCase("") && !data_icon.equalsIgnoreCase("null")) {
            Picasso.with(context).load(data_icon).fit()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            img_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            image.setBackground(context.getResources().getDrawable(R.drawable.logo));
                        }
                    });
//                    Picasso.with(context).load(docmodel.getPhotoId())
//                            .centerCrop()
//                            .resize(200, 200)
//                            .into(holder.docImage);
        } else {
            image.setVisibility(View.GONE);
            img_progress.setVisibility(View.GONE);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goToscreen.equalsIgnoreCase("dashboard")){
                    goToDashboard();
                } else {
                    goToMembership();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goToscreen.equalsIgnoreCase("dashboard")){
                    goToDashboard();
                } else {
                    goToMembership();
                }
            }
        });

        /*TextView text = dialog.findViewById(R.id.alertMess);
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
                new ScanDocumentActivity.DeleteFromCloseAll().execute();

            }
        });*/
        dialog.show();
    }

    private void goToDashboard() {
        startActivity(new Intent(getActivity(), DashBoardActivity.class));
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        getActivity().finish();
    }

    private void goToMembership() {
        startActivity(new Intent(getActivity(), MembershipActivity.class));
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        getActivity().finish();
    }

    private void callActiveDialog() {
        loginBtn.setEnabled(true);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_active_account_dialog);

        CircleImageView profile_Img = (CircleImageView) dialog.findViewById(R.id.img_profile);
        img_profile_progress = (ProgressBar) dialog.findViewById(R.id.img_profile_progress);
        TextView fullName = (TextView) dialog.findViewById(R.id.textUserName);

        fullName.setText(firstName);

        Picasso.with(context).load(profileImageURL).centerCrop()
                .noFade()
                .placeholder(R.drawable.progress_animation_image_loader)
                .resize(180, 180)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profile_Img, new Callback() {
                    @Override
                    public void onSuccess() {
                        img_profile_progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        profile_Img.setBackground(activity.getResources().getDrawable(R.drawable.logo));
                    }
                });

        //profile_Img.setImageURI(Uri.parse(profileImageURL));
        /*TextView text = (TextView) dialog1.findViewById(R.id.alertMess);
        text.setText("Do you want to logout?");*/

        Button dialogButtonUnregister = (Button) dialog.findViewById(R.id.btnActivate);
        dialogButtonUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(getActivity(), "You CLicked", Toast.LENGTH_SHORT).show();
                callActiveApi();
            }
        });
        Button dialogButtonClose = (Button) dialog.findViewById(R.id.btn_close);
        dialogButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                //unsubscribeReason();
                dialog.dismiss();
            }
        });
        dialog.show();

        //Toast.makeText(context, "Not Working", Toast.LENGTH_SHORT).show();
    }

    private void checkSteps(JSONObject data) {

        try {

            JSONObject steps = new JSONObject(data.toString()).getJSONObject("steps");
            appId = steps.getString("appId");

            if (steps.getBoolean("alldone")) {
                JSONObject plan = new JSONObject(data.toString()).getJSONObject("plan");

                //Sourav 06/10/2020
                //int daysLeft = data.getInt("daysLeft"); //Sourav
                int daysLeft;
                try {
                    daysLeft = (int) Utils.dateDiff(data.getString("current_date"),
                            data.getJSONObject("plan").getString("ending_at"));
                    daysLeft = daysLeft * -1;
                } catch (Exception e) {
                    daysLeft = data.getInt("daysLeft");

                }


                //End
                int remainigTrans = data.getInt("remainigTrans");

                vaMembershipManager(plan, daysLeft, remainigTrans);

            } else if (!steps.getBoolean("email_verified")) {

                new UpdateEmail().execute();
                loadFragment(new OTPFragment());
            } else {

                if (checkPermission()) {

                    moveToNext(steps);

                } else {
                    jsonObjectValue = steps;
                    requestPermission();
                }

            }


        } catch (Exception e) {
        }
    }

    private void requestPermission() {

        requestPermissions(new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                },
                PERMISSION_REQUEST_CODE
        );
    }

    private void moveToNext(JSONObject steps) {

        try {
            if (!steps.getBoolean("identity_check")) {

                new UpdateEmail().execute();
                loadFragment(new DocumentsRequiredFragment());

            } else if (!steps.getBoolean("licence")) {

                new UpdateEmail().execute();
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            } else if (!steps.getBoolean("stamp")) {

                new UpdateEmail().execute();
                startActivity(new Intent(getActivity(), NotaryStampActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            } else if (!steps.getBoolean("pledge")) {

                new UpdateEmail().execute();
                startActivity(new Intent(getActivity(), NotaryPledgeActivity.class));
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        } catch (Exception e) {

        }

    }

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_PHONE_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void vaMembershipManager(JSONObject plan, int daysleft, int remainigTrans) {

        try {
            //Now check result sent by our POSTAPIRequest class
            if (plan != null) {

                vaCustomer = new VACustomer();

                vaCustomer.setCusId(plan.getInt("id"));
                vaCustomer.setEmail(plan.getString("email"));
                vaCustomer.setDaysLeft(daysleft);
                vaCustomer.setStripe_customer_id(plan.getString("stripe_customer_id"));
                vaCustomer.setCurrent_membership(plan.getString("current_membership"));
                vaCustomer.setTotal_bought(plan.getInt("total_bought"));
                vaCustomer.setTotal_used(plan.getInt("total_used"));
                vaCustomer.setTransactionsLeft(plan.getInt("total_bought")-plan.getInt("total_used"));
                vaCustomer.setCreated_at(plan.getString("created_at"));
                vaCustomer.setUpdated_at(plan.getString("updated_at"));
                vaCustomer.setStarted_at(plan.getString("started_at"));
                vaCustomer.setEnding_at(plan.getString("ending_at"));
                vaCustomer.setIs_active(plan.getInt("is_active"));
                vaCustomer.setStripe_active_subscription_id(plan.getString("stripe_active_subscription_id"));

                new CustomerDetails().execute();

                if (plan.getString("current_membership").equalsIgnoreCase("trial")) {
                    new UpdateEmail().execute();
                    new storeData().execute();
                    goToscreen = "membership";
                    checkNotification();
                    /*Intent i = new Intent(getActivity(), MembershipActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/

                } else {
                    new UpdateEmail().execute();

                    new storeData().execute();
                    goToscreen = "dashboard";
                    checkNotification();

                    /*startActivity(new Intent(getActivity(), DashBoardActivity.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    getActivity().finish();*/
                }
            }
        } catch (Exception e) {
            CustomDialog.cancelProgressDialog();
            CustomDialog.notaryappDialogSingle(getActivity(), "Server Unavailable. Please try again later");
            //e.printStackTrace();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class SaveDp extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .insert(profilePicture);

            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }
    }


    class CustomerDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            if (databaseClient.getAppDatabase().vaCustomerDao().getCustomer() != null) {

                databaseClient.getAppDatabase().vaCustomerDao().delete();
            }
            databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .insert(vaCustomer);

            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class storeData extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... voids) {


            /*try {
                encrypted = encrypt(loginPwd.getBytes(), secretKey, IV);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            String loginPWdEn = "";
            try {
                loginPWdEn = Base64.encodeToString(encrypted,Base64.DEFAULT);
            } catch (Exception e) {
                //e.printStackTrace();
            }*/





            /*SharedPreferences.Editor editor = pref.edit();
            editor.putString("username",loginEmail);
            //editor.putString("password", loginPWdEn);
            editor.putString("password",loginPwd);
            editor.apply();*/
            try {
                getEncryptedSharedPreferences().edit()
                        .putString("username", loginEmail)
                        .putString("password",loginPwd)
                        .apply();
            } catch (GeneralSecurityException e) {
                //e.printStackTrace();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return "";
        }

    }


    class UpdateEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {




            databaseClient.getAppDatabase()
                    .userRegDao()
                    .update(loginEmail, firstName, lastName);

            databaseClient.getAppDatabase()
                    .userRegDao()
                    .updateAppID(appId);
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean phone = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (camera && storage && phone)
                        moveToNext(jsonObjectValue);
                    else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{
                                                                    Manifest.permission.CAMERA,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                                    Manifest.permission.READ_PHONE_STATE

                                                            },
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
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

    /*public static byte[] encrypt(byte[] plaintext, String key, String IV) throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherText = cipher.doFinal(plaintext);
        return cipherText;
    }

    public static String decrypt(byte[] cipherText, String key, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText);

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;

    }*/

    public SharedPreferences getEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "secret_shared_prefs_file",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        return sharedPreferences;
    }

}

