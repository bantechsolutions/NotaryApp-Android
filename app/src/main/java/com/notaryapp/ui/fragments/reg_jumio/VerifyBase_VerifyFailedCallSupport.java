package com.notaryapp.ui.fragments.reg_jumio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;

public class VerifyBase_VerifyFailedCallSupport extends Fragment {

   // public static final int REF_VIEW_CONTAINER = R.id.identityRoot;
    private View retryView;
    private int selectedId;
    private Button btncallSupport,btnLoadDash,btn_close;
    public VerifyBase_VerifyFailedCallSupport(){

    }
    public VerifyBase_VerifyFailedCallSupport(int selectedId){
        this.selectedId = selectedId;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retryView = inflater.inflate(R.layout.fragment_verifyfailed_callsupport, container, false);
        btncallSupport = retryView.findViewById(R.id.btncallSupport);
        btnLoadDash = retryView.findViewById(R.id.btnLoadDash);
        btn_close = retryView.findViewById(R.id.btn_close);

        btncallSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8888888888"));
                startActivity(intent);
            }
        });
        btnLoadDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), OnboardingBaseActivity.class);
                startActivity(intent);
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return retryView;
    }
}
