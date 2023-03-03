package com.notaryapp.ui.fragments.forgotpassword;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.R;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class ForgotPassword_EnterEmail extends Fragment {

    private View emailView;
    private Button submitBtn;
    private TextInputEditText emailEditTxt;
    private TextInputLayout emailTextInput;
    private IJsonListener iJsonListener;
    private String email;
    private boolean validEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        emailView = inflater.inflate(R.layout.fragment_forgot_pwd_enteremail, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        submitBtn = emailView.findViewById(R.id.btnSubmit);
        initIJsonListener();
        emailTextInput = emailView.findViewById(R.id.enterEmailText);
        emailEditTxt = emailView.findViewById(R.id.enterEmaildEdt);

        emailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validation.isEmailAddress(emailEditTxt,emailTextInput,true)){
                    validEmail = true;
                }else{
                    validEmail = false;
                }
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // loadFragment(new SignInFragment());
                email =  emailEditTxt.getText().toString().trim();
                if(Validation.isEmailAddress(emailEditTxt,emailTextInput,true)){
                    validEmail = true;
                }else{
                 //   emailTextInput.setError("Enter Email");
                }
                if(validEmail) {
                    emailChk();
                   //
                }
            }
        });

        return emailView;

    }
    private void emailChk() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("email", email);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.EMAILCHECK_FPWD, "emailChk");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    private void genOtpChangePasswordApi() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("username", email);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            //postapiRequest.requestWithOutAuth(getActivity(), iJsonListener, params, AppUrl.GENOTP_CHANGE_PWD, "genOTPChangePwd");
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GENOTP_CHANGE_PWD, "genOTPChangePwd");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER, fragment, true);
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
                       if (type.equals("genOTPChangePwd")) {

                            if (success.equals("1")) {
                              //  CustomDialog.notaryappToastDialog(getActivity(),"Otp sent to registered email");
                                loadFragment(new ForgotPassword_OTPFragment(email));
                            } else {
                                CustomDialog.notaryappDialogSingle(getActivity(),"Email is not registered with notaryapp");
                            }
                        } else  if (type.equals("emailChk")){

                           if (success.equals("1015")) { //
                               genOtpChangePasswordApi();
                           }else if(success.equals("1016")){
                               genOtpChangePasswordApi();
                           }else if(success.equals("1017")){
                               CustomDialog.notaryappDialogSingle(getActivity(),"Given email ID is not registered with notaryapp");

                              // CustomDialog.notaryappToastDialog(getActivity(),"Given email ID is not registered with notaryapp");
                             //  CustomDialog.notaryappDialogToa(g);
                           }else{

                           }

                        /*   if (success.equals("1117")) { //
                               CustomDialog.notaryappDialogSingle(getActivity(),"This email ID is already registered as Client/Signer/Witness");
                           }else if(success.equals("1015")){
                               genOtpChangePasswordApi();
                           }else if (success.equals("1016")){//already registered as notary
                               genOtpChangePasswordApi();
                           } else{
                               CustomDialog.notaryappDialogSingle(getActivity(),"Given email ID is not registered with notaryapp");
                           }*/

                        }else{

                       }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
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

            }
        };

    }
}
