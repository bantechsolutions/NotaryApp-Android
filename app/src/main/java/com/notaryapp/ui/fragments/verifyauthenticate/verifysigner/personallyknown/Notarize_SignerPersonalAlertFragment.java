package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.personallyknown;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.Notarize_SignerVerifyTypeFragment;
import com.notaryapp.utils.FragmentViewUtil;

public class Notarize_SignerPersonalAlertFragment extends Fragment {
    private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private View view;
    private Button btnOk,btnBack,btnClose;


    public Notarize_SignerPersonalAlertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notarize_alert_signer_personal, container, false);
        init();

        //Button click for credible witness.
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loadFragment(new Notarize_SignerPersonalCameraFragment());

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

        return view;
    }

    private void init() {
        btnOk = view.findViewById(R.id.btnOk);
        btnBack = view.findViewById(R.id.btn_back);
        btnClose = view.findViewById(R.id.btn_close);

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
    }

}
