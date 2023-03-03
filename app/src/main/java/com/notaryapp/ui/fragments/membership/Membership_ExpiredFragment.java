package com.notaryapp.ui.fragments.membership;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.FragmentViewUtil;


public class Membership_ExpiredFragment extends Fragment {

    private View expireView;
    private TextView txtMoreDeatils;
    private Button closeBtn,upgradeBtn;
    private String from;
    public Membership_ExpiredFragment() {
        // Required empty public constructor
    }
    public Membership_ExpiredFragment(String from) {
      this.from = from;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        expireView = inflater.inflate(R.layout.fragment_membership_expired, container, false);
        init();

//        txtMoreDeatils.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), MoreDetailsActivity.class));
//            }
//        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.equals("Dashboard")){
                    getActivity().finish();
                }else {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), DashBoardActivity.class));
                }
            }
        });
        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Membership_PackageFragment()) ;
            }
        });
        return expireView;
    }
    private void init(){
        closeBtn = expireView.findViewById(R.id.btn_pro_close3);
        txtMoreDeatils = expireView.findViewById(R.id.textMoreDetails);
        upgradeBtn = expireView.findViewById(R.id.upgradeBtn);
        if(from == null){
            from = "";
        }
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), MembershipActivity.REF_VIEW_CONTAINER, fragment, true);
    }
}
