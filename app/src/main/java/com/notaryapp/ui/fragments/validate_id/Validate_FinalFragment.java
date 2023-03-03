package com.notaryapp.ui.fragments.validate_id;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.ValidateActivity;
import com.notaryapp.ui.activities.notaryappShareAppActivity;
import com.notaryapp.utils.FragmentViewUtil;

public class Validate_FinalFragment extends Fragment {

    private Button confirmBtn;
    private View finalView;

    public Validate_FinalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        finalView = inflater.inflate(R.layout.fragment_validate_final, container, false);

        init();
        //Button click to show start next fragment.
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                Intent intent = new Intent(getActivity(), notaryappShareAppActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return finalView;
    }

    private void init(){
        confirmBtn = finalView.findViewById(R.id.btnLoadDash);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), ValidateActivity.REF_VIEW_CONTAINER,fragment,true);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}