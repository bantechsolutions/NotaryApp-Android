package com.notaryapp.ui.fragments.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;

public class DashBoard_AddStamp extends Fragment {

    private View dashBoardAddLicense;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        dashBoardAddLicense = inflater.inflate(R.layout.fragment_dashboard_base, container, false);
       // ButterKnife.bind(this,retryView);
        getActivity().finish();
        startActivity(new Intent(getActivity(), OnboardingBaseActivity.class));
        return dashBoardAddLicense;
    }
}
