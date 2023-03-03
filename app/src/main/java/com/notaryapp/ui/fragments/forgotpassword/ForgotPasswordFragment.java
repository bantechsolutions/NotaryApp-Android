package com.notaryapp.ui.fragments.forgotpassword;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class ForgotPasswordFragment extends Fragment {

    private final String TAG="SignInFragment";
    private View changePwdView;
    private FragmentTransaction fragmentTransaction;

    // public static final int REF_VIEW_CONTAINER = R.id.onboardingActivityViewContainer;

    private LinearLayout linearLayoutReg;
    private EditText enterPasswordEdt;
    private  EditText reenterPasswordEdt;
    private Button btnSubmit;
    private ImageView imgInfo;
    private String password,cPassword;
    private boolean validCPwd,validPwd;
    private IJsonListener iJsonListener;
    private DatabaseClient databaseClient;
    private Context context;
    private TextInputLayout rePasswordHelper,passwordHelper;
    private String otpTyped,savedEmail,otpCheckUrl,genOtpURl;
    private Info info;
    private String email;

    public ForgotPasswordFragment(String email) {
        // Required empty public constructor
        this.email = email;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        changePwdView = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        init();
        //new SelectEmail().execute();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validPwd && validCPwd){
                    password = enterPasswordEdt.getText().toString();
                    cPassword = reenterPasswordEdt.getText().toString();
                    callChangePasswordApi();
                }else{
                    if(!Validation.hasValue(enterPasswordEdt,passwordHelper)){
                        passwordHelper.setError("Enter Password");
                      //  enterPasswordEdt.requestFocus();
                    //    Utils.setKeyboardFocus(enterPasswordEdt);
                    }
                    if(!Validation.hasValue(reenterPasswordEdt,rePasswordHelper)){
                        rePasswordHelper.setError("Enter Password");
                    }

                }
           //     loadFragment(new FogotPasswordSuccessFragment());
            }
        });
        enterPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(enterPasswordEdt,passwordHelper)) {
                    validPwd = Validation.passwordValidation(enterPasswordEdt, passwordHelper, true);
                }
            }
        });
        enterPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validation.hasValue(enterPasswordEdt,passwordHelper)) {
                    validPwd = Validation.passwordValidation(enterPasswordEdt, passwordHelper, true);
                    if(s.length()>8 && Validation.hasValue(reenterPasswordEdt,rePasswordHelper)) {
                        validCPwd = Validation.hasPasswordMatched(enterPasswordEdt, reenterPasswordEdt,passwordHelper);
                    }
                }
            }
        });
        enterPasswordEdt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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
        reenterPasswordEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(Validation.hasValue(reenterPasswordEdt,rePasswordHelper)) {
                    validCPwd = Validation.passwordValidation(reenterPasswordEdt, passwordHelper, true);
                }
            }
        });
        reenterPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              // validPwd = Validation.passwordValidation(reenterPasswordEdt,rePasswordHelper,true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Validation.hasValue(reenterPasswordEdt,rePasswordHelper)) {
                    validCPwd = Validation.passwordValidation(reenterPasswordEdt, rePasswordHelper, true);
                     validPwd = Validation.hasPasswordMatched(enterPasswordEdt, reenterPasswordEdt,rePasswordHelper);
                }
            }
        });
        reenterPasswordEdt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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

        imgInfo.setOnClickListener(new View.OnClickListener() {
        //    @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
              //  imgInfo.setTooltipText(getResources().getString(R.string.passwordTooltip));
              //  balloon.showAlignTop(imgInfo);
                CustomDialog.notaryappInfoDialog(getActivity(),info.getPassword());
            }
        });
        return changePwdView;

    }
    private void init(){
        btnSubmit = changePwdView.findViewById(R.id.btnSubmit);
        enterPasswordEdt = changePwdView.findViewById(R.id.enterPasswordEdt);
        reenterPasswordEdt = changePwdView.findViewById(R.id.reenterPasswordEdt);
        passwordHelper = changePwdView.findViewById(R.id.enterPwdText);
        rePasswordHelper = changePwdView.findViewById(R.id.reenterPwdText);
        imgInfo = changePwdView.findViewById(R.id.infoIcon);
        initIJsonListener();
     //   new SelectEmail().execute();
        context = getActivity().getApplicationContext();

        databaseClient =  DatabaseClient.getInstance(context);
        new GeInfo().execute();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER,fragment,true);

    }
    private void callChangePasswordApi(){
        CustomDialog.showProgressDialog(getActivity());
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("password",password);
                params.put("userName",email);

            }catch (Exception e){
                //e.printStackTrace();
            }
            //postapiRequest.requestWithOutAuth(getActivity(),iJsonListener,params, AppUrl.FORGOT_PWD,"ChangePwd");
            postapiRequest.request(getActivity(),iJsonListener,params, AppUrl.FORGOT_PWD,"ChangePwd");
        }catch (Exception e){
            //e.printStackTrace();
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
        }

    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if(type.equals("ChangePwd")){
                            String success = data.getString("success");
                                if(success.equals("1")){
                                    loadFragment(new FogotPassword_SuccessFragment());
                                }

                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.notaryappDialogSingle(getActivity(),Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {

            }
        };
    }
}
