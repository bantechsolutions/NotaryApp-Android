
package com.notaryapp.ui.fragments.forgotpassword;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;


public class ForgotPassword_OTPFragment extends Fragment {

    final StringBuilder sb=new StringBuilder();
    private EditText pin1,pin2,pin3,pin4;
    private String pin1Text,pin2Text,pin3Text,pin4Text;
    private View otpView;
    private TextView emailText,changeemailtxt;
    private Button btn_verifyOtp;
    private String otpTyped,savedEmail;//,otpCheckUrl,genOtpURl;
    private TextView txt_resendOtp;
    private IJsonListener iJsonListener;

    private String newPassword;
    private String email;

    public ForgotPassword_OTPFragment(String email) {
        this.email = email;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        otpView = inflater.inflate(R.layout.fragment_forgot_pwd_otp, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        new SelectEmail().execute();
        init();

        changeemailtxt.setText(email);

        pin1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(sb.length()==1) {
                    sb.deleteCharAt(0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sb.length()==0 & pin1.length()==1)
                {
                    sb.append(s);
                    String pinCheck = getPIN();
                    if(pinCheck.length()==4){
                        proceedHome();
                    }
                    else {
                        pin1.clearFocus();
                        pin2.requestFocus();
                        pin2.setCursorVisible(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sb.length()==0) {

                    pin1.requestFocus();
                }
            }
        });

        pin2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(sb.length()==1) {
                    sb.deleteCharAt(0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sb.length()==0 & pin2.length()==1)
                {
                    sb.append(s);
                    String pinCheck = getPIN();
                    if(pinCheck.length()==4){
                        proceedHome();
                    }
                    else {
                        pin2.clearFocus();
                        pin3.requestFocus();
                        pin3.setCursorVisible(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sb.length()==0) {

                    pin2.clearFocus();
                    pin1.requestFocus();
                    pin1.setCursorVisible(true);
                }
            }
        });

        pin3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(sb.length()==1) {
                    sb.deleteCharAt(0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sb.length()==0 & pin3.length()==1)
                {
                    sb.append(s);
                    String pinCheck = getPIN();
                    if(pinCheck.length()==4){
                        proceedHome();
                    }
                    else {
                        pin3.clearFocus();
                        pin4.requestFocus();
                        pin4.setCursorVisible(true);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sb.length()==0) {

                    pin3.clearFocus();
                    pin2.requestFocus();
                    pin2.setCursorVisible(true);
                }
            }
        });

        pin4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(sb.length()==1) {
                    sb.deleteCharAt(0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sb.length()==0 & pin4.length()==1)
                {
                    sb.append(s);
                    String pinCheck = getPIN();
                    if(pinCheck.length()==4){
                        proceedHome();
                    }
                    else {
                        pin4.clearFocus();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(sb.length()==0) {

                    pin4.clearFocus();
                    pin3.requestFocus();
                    pin3.setCursorVisible(true);
                }else {
                    Utils.setKeyboardHide(pin4,getActivity());
                 //   InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                //    imm.hideSoftInputFromWindow(pin4.getWindowToken(), 0);
                }
            }
        });
        btn_verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpTyped = getPIN();
                if(otpTyped.length()==4) {
                    checkOtpChangePasswordApi();
                }else{

                }
                 //   Utils.setKeyboardFocus(pin1);
                  //  CustomDialog.notaryappDialogSingle(getActivity(),"Please fill OTP");
             //   Toast.makeText(getActivity(), "Please fill OTP", Toast.LENGTH_LONG).show();
//Testing
           //    startActivity(new Intent(getActivity(), Documents_RequiredActivity.class));

            }
        });

        txt_resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin1.setText("");
                pin2.setText("");
                pin3.setText("");
                pin4.setText("");
                pin1.requestFocus();
              //  Utils.setKeyboardFocus(pin1);
                genOtpChangePasswordApi();

            }
        });
        changeemailtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                }
        });
        return otpView;
    }

    private void init() {
        pin1 = otpView.findViewById(R.id.pin1);
        pin2 = otpView.findViewById(R.id.pin2);
        pin3 = otpView.findViewById(R.id.pin3);
        pin4 = otpView.findViewById(R.id.pin4);
//        emailText = otpView.findViewById(R.id.emailId);
        //  change_emailLink = otpView.findViewById(R.id.changeEmail);
        btn_verifyOtp = otpView.findViewById(R.id.verifyOtp);
        txt_resendOtp = otpView.findViewById(R.id.resendBtn);
        changeemailtxt = otpView.findViewById(R.id.changeEmail);

      //  otpCheckUrl = AppUrl.API_BASE_URL+"otpCheck";
    //    genOtpURl = AppUrl.API_BASE_URL+"genOtp";
        initIJsonListener();
        Utils.setKeyboardFocus(pin1);
    }
    private void proceedHome() {
    }

    public String getPIN(){

        String loginPin = "";
        boolean flag = false;
        try {
            if((pin1.length()==1)&&(pin2.length()==1)&&(pin3.length()==1)&&(pin4.length()==1)){
                flag = true;
                pin1Text = pin1.getText().toString();
                pin2Text = pin2.getText().toString();
                pin3Text = pin3.getText().toString();
                pin4Text = pin4.getText().toString();
            }
            else
                flag = false;

            if (flag)
                loginPin = pin1Text+""+pin2Text+""+pin3Text+""+pin4Text;
            else
                loginPin = "";

            //Log.d("Login","loginPin "+loginPin);

        }catch (Exception e){
            //e.printStackTrace();
            loginPin = "";

        }

        return  loginPin;
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER,fragment,true);

    }
    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            savedEmail =  DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
//            emailText.setText(email);
        }

    }
    private void genOtpChangePasswordApi(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                //Creating POST body in JSON format
                //to send in POST request
                params.put("username",email);
                //  params.put("password",loginPwd);

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.GENOTP_CHANGE_PWD,"genOTPChangePwd");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
                //e.printStackTrace();
            }
        }

        private void checkOtpChangePasswordApi(){
            CustomDialog.showProgressDialog(getActivity());
            try{
                POSTAPIRequest postapiRequest=new POSTAPIRequest();
                JSONObject params=new JSONObject();
                try {
                    //Creating POST body in JSON format
                    //to send in POST request
                params.put("username",email);
                params.put("otp",otpTyped);
                //  params.put("password",loginPwd);

            }catch (Exception e){
                //e.printStackTrace();
            }
                postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.CHECKOTP_CHANGE_PWD,"checkOTPChangePwd");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
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
                        String success = data.getString("success");
                        if(type.equals("checkOTPChangePwd")){
                            if(success.equals("1010")){
                                //    Toast.makeText(getActivity().getApplicationContext(), "User not exists for OTP verification", Toast.LENGTH_LONG).show();

                            }else if(success.equals("1011")){
                                Toast.makeText(getActivity().getApplicationContext(), "Email already verified", Toast.LENGTH_LONG).show();

                            }else if(success.equals("1012")){
                                //   Toast.makeText(getActivity().getApplicationContext(), "No such user", Toast.LENGTH_LONG).show();

                            }else if(success.equals("1013")){
                            //    CustomDialog.notaryappToastDialog(getActivity(),"EMail verified");
                           //     Toast.makeText(getActivity().getApplicationContext(), "EMail verified", Toast.LENGTH_LONG).show();
                                // Valid OTP and FragmentViewUtil.removeFragment(getActivity(), getActivity().getSupportFragmentManager().findFragmentById(R.id.onboardingActivityViewContainer));
                               // loadFragment(new ForgotPassword_Selfie(savedEmail));
                                loadFragment(new ForgotPasswordFragment(email));
                            }else if(success.equals("1014")){
                                CustomDialog.notaryappDialogSingle(getActivity()," Invalid OTP ");
                              //  Toast.makeText(getActivity().getApplicationContext(), " Invalid OTP ", Toast.LENGTH_LONG).show();
                                pin1.setText("");
                                pin2.setText("");
                                pin3.setText("");
                                pin4.setText("");
                                pin1.requestFocus();
                                Utils.setKeyboardFocus(pin1);
                            }

                        }else if(type.equals("genOTPChangePwd")){
                            if(success.equals("1")){
                                //gen OTP
                                pin1.setText("");
                                pin2.setText("");
                                pin3.setText("");
                                pin4.setText("");
                                pin1.requestFocus();
                                Utils.setKeyboardFocus(pin1);
                                CustomDialog.notaryappDialogSingle(getActivity(),"Otp sent to registered email");
                              //  Toast.makeText(getActivity().getApplicationContext(), "Otp sent to registered email", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                //    RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");

            }

            @Override
            public void onFetchStart() {

            }
        };
    }
}
