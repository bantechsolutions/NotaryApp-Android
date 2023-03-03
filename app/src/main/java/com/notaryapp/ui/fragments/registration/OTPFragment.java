
package com.notaryapp.ui.fragments.registration;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class OTPFragment extends Fragment {

    final StringBuilder sb=new StringBuilder();
    private EditText pin1,pin2,pin3,pin4;
    private String pin1Text,pin2Text,pin3Text,pin4Text;
    private View otpView;
    private TextView emailText,changeemailtxt;
    private Button btn_verifyOtp;
    private String otpTyped,savedEmail;//,otpCheckUrl,genOtpURl;
    private TextView change_emailLink,txt_resendOtp;
    private IJsonListener iJsonListener;
    private String TAG = "OTPFragment";
    public static boolean checkOtpActivity = false;


    public OTPFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        otpView = inflater.inflate(R.layout.fragment_otp, container, false);

        new SelectEmail().execute();
        init();

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
                   // InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                   // imm.hideSoftInputFromWindow(pin4.getWindowToken(), 0);
                }
            }
        });
        btn_verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpTyped = getPIN();
                if(otpTyped.length()==4) {
                    otpChkCall();
                }else
                    CustomDialog.notaryappDialogSingle(getActivity(),"Please enter all 4 digits of the One-Time Password (OTP) you have received on your registered email and phone number. ");
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
                genOTP();
                Utils.setKeyboardFocus(pin1);
            }
        });
        change_emailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOtpActivity = true;
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
        emailText = otpView.findViewById(R.id.changeEmail);
        change_emailLink = otpView.findViewById(R.id.changeEmail);
        btn_verifyOtp = otpView.findViewById(R.id.verifyOtp);
        txt_resendOtp = otpView.findViewById(R.id.resendBtn);

        initIJsonListener();
        pin1.requestFocus();
        Utils.setKeyboardFocus(pin1);
        genOTP();
    }
    private void proceedHome() {
    }

    public String getPIN(){

        String loginPin = "";
        boolean flag = false;
        try {
            if((pin1.length()==1)&&(pin2.length()==1)&&(pin3.length()==1)&&(pin4.length()==1)){
                flag = true;
                pin1Text = pin1.getText().toString().trim();
                pin2Text = pin2.getText().toString().trim();
                pin3Text = pin3.getText().toString().trim();
                pin4Text = pin4.getText().toString().trim();
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
           // email = email;
            emailText.setText(email);
            SpannableString content = new SpannableString(email);
            content.setSpan(new UnderlineSpan(), 0, email.length(), 0);
            change_emailLink.setText(Html.fromHtml("Change email <u> "+email+"</u>"));
          //
        }

    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER,fragment,false);

    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data,String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("success")) {
                            String success = data.getString("success");
                            if(type.equals("checkOTP")){
                                if(success.equals("1010")){
                                    //    Toast.makeText(getActivity().getApplicationContext(), "User not exists for OTP verification", Toast.LENGTH_LONG).show();

                                }else if(success.equals("1011")){
                                    CustomDialog.notaryappDialogSingle(getActivity(),"Email already verified");
                                       //  Toast.makeText(getActivity().getApplicationContext(), "Email already verified", Toast.LENGTH_LONG).show();

                                }else if(success.equals("1012")){
                                    //   Toast.makeText(getActivity().getApplicationContext(), "No such user", Toast.LENGTH_LONG).show();

                                }else if(success.equals("1013")){
                                   // CustomDialog.notaryappToastDialog(getActivity(),"Email verified");
//                                       Toast.makeText(getActivity().getApplicationContext(), "Email verified", Toast.LENGTH_LONG).show();
                                   // Valid OTP and
                                 //   FragmentViewUtil.removeFragment(getActivity(), getActivity().getSupportFragmentManager().findFragmentByTag(TAG));
                                 // loadFragment(new BaseFragment());

                                    loadFragment(new AllowAccessFragment());

                                }else if(success.equals("1014")){
                                    CustomDialog.notaryappDialogSingle(getActivity()," Invalid One-Time Password (OTP)");
                                        // Toast.makeText(getActivity().getApplicationContext(), " Invalid OTP", Toast.LENGTH_LONG).show();
                                    pin1.setText("");
                                    pin2.setText("");
                                    pin3.setText("");
                                    pin4.setText("");
                                    pin1.requestFocus();
                                    Utils.setKeyboardFocus(pin1);
                                }
                            }else if(type.equals("genOTP")){
                                if(success.equals("1")){
                                    //gen OTP
                                    pin1.setText("");
                                    pin2.setText("");
                                    pin3.setText("");
                                    pin4.setText("");
                                    pin1.requestFocus();
                                    Utils.setKeyboardFocus(pin1);
                                  //  CustomDialog.notaryappToastDialog(getActivity(),"An One-Time Password (OTP) has been sent to your registered email ID.");
                                   // Toast.makeText(getActivity().getApplicationContext(), "Otp sent to registered email", Toast.LENGTH_LONG).show();
                                }
                            }else{
                            }
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                      //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(),"Error! No data fetched");
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                  //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");

                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
               // RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }
    private void otpChkCall(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("username",savedEmail);
                params.put("otp",otpTyped);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params,AppUrl.OTPCHK_URL,"checkOTP");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    private void genOTP(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                //Creating POST body in JSON format
                //to send in POST request
                params.put("username",savedEmail);

            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(),iJsonListener,params,AppUrl.GENOTP_URL,"genOTP");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            //e.printStackTrace();
        }
    }
}
