package com.notaryapp.ui.fragments.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.FragmentViewUtil;

public class Membership_FreeTrialFragment extends Fragment {

    private View selectView;
    private Button upgradeBtn,closeBtn;
    private TextView daysCount,moreDetailsTxt,transTxt;
    private String from;
    private VACustomer vaCustomer;


    public Membership_FreeTrialFragment() {

        // Required empty public constructor
    }
    public Membership_FreeTrialFragment(String from) {
            this.from = from;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        selectView = inflater.inflate(R.layout.fragment_membership_free_trial, container, false);
        init();

        //Button click for upgrade
        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Membership_PackageFragment()) ;
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.equals("Dashboard")){
                    getActivity().finish();
                }else {
                    //getActivity().finish();
                    startActivity(new Intent(getActivity(), DashBoardActivity.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    getActivity().finish();
                }
            }
        });
        moreDetailsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                startActivity(new Intent(getActivity(), MoreDetailsActivity.class));
            }
        });
        return selectView;
    }

    //Method to initialize elements
    private void init() {
        closeBtn = selectView.findViewById(R.id.btn_pro_close3);
        moreDetailsTxt = selectView.findViewById(R.id.textMoreDetails);
        upgradeBtn = selectView.findViewById(R.id.upgradeBtn);
        daysCount = selectView.findViewById(R.id.daysCount);
        transTxt = selectView.findViewById(R.id.transactions);
        if(from == null){
            from = "";
        }
        new SelectPlans().execute();


    }

    //Method for loading fragments in Membership Activity
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), MembershipActivity.REF_VIEW_CONTAINER, fragment, true);
    }
    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {
            vaCustomer = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();

            return vaCustomer;
        }

        @Override
        protected void onPostExecute(VACustomer vaCustomer) {
            super.onPostExecute(vaCustomer);
            String daysText = (vaCustomer.getDaysLeft() > 0 ? vaCustomer.getDaysLeft() : 0) + " DAYS";
            String transaction = (vaCustomer.getTransactionsLeft() > 0 ? vaCustomer.getTransactionsLeft() : 0) + " ID Verification(s)";
            daysCount.setText(daysText);
            transTxt.setText(transaction);
        }
    }

}
