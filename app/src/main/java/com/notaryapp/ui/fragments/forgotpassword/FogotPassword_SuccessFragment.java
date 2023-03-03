package com.notaryapp.ui.fragments.forgotpassword;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.ui.fragments.registration.SignInFragment;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;

public class FogotPassword_SuccessFragment extends Fragment {

    private final String TAG="SignInFragment";
    private View changePwdView;
    private FragmentTransaction fragmentTransaction;
    private LinearLayout linearLayoutReg;
    private EditText loginEmailEdt;
    private  EditText loginPwdEdt;
    private Button continueLoginBtn;
    private String newPassword,confirmPassword;
    private boolean validEmail,validPwd;
    private IJsonListener iJsonListener;
    private DatabaseClient databaseClient;
    private Context context;

    public FogotPassword_SuccessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        changePwdView = inflater.inflate(R.layout.fragment_forgot_pwd_success, container, false);
        init();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // ButterKnife.bind(this, signInView);

        continueLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SignInFragment());
            }
        });

        return changePwdView;

    }
    private void init(){
        continueLoginBtn = changePwdView.findViewById(R.id.btnContinueLogin);

        //initIJsonListener();
        context = getActivity().getApplicationContext();

        databaseClient =  DatabaseClient.getInstance(context);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), OnboardingBaseActivity.REF_VIEW_CONTAINER,fragment,true);

    }


}
