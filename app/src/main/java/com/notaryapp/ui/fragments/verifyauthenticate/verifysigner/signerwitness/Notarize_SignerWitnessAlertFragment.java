package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.Notarize_SignerVerifyTypeFragment;
import com.notaryapp.utils.FragmentViewUtil;


public class Notarize_SignerWitnessAlertFragment extends Fragment {
    //private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private View aletView;
    private Button btnOk;
    private Button btnBack,btnClose;
    private String idType="";

    public Notarize_SignerWitnessAlertFragment() {
        // Required empty public constructor
    }

    public Notarize_SignerWitnessAlertFragment(String idType) {this.idType = idType;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        aletView = inflater.inflate(R.layout.fragment_notarize_alert_witness, container, false);
        init();

        //Button click for credible witness.
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new Notarize_VerifyWitnessFragment());
                if (idType.equalsIgnoreCase("manually")){
                    loadFragment(new Notarize_SignerWitnessCameraFragment("manually"));
                } else {
                    loadFragment(new Notarize_SignerWitnessCameraFragment());
                }

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerVerifyTypeFragment());
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerVerifyTypeFragment());
            }
        });
        return aletView;
    }

    private void init() {
        btnBack = aletView.findViewById(R.id.btn_pro_back);
        btnClose = aletView.findViewById(R.id.btn_pro_close);
        btnOk = aletView.findViewById(R.id.btnOk);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER,fragment);
    }
}
