package com.notaryapp.ui.fragments.membership;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.FragmentViewUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Membership_StatusFragment extends Fragment {

    private View selectView;
    private Button upgradeBtn,btnClose;
    private TextView daysCount,transTxt,planName;
    private String plan;
    private ImageView crown;
    private VACustomer memPlans;
    private TextView planDesc;
    private TextView days;

    public Membership_StatusFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        selectView = inflater.inflate(R.layout.fragment_membership_status, container, false);
        init();

        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(plan.equalsIgnoreCase("trial")){
                    loadFragment(new Membership_PackageFragment());
                }
                else{
                    if(!(memPlans.getDaysLeft() > 0)) {
                        loadFragment(new Membership_PackageFragment());
                    }
                    //Sourav 20201124
                    else {
                        upgradeBtn.setText("ADD ID VERIFICATION PACKAGE");
                        loadFragment(new Membership_PremiumPackageFragment());
                    }
                }

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
            }
        });
        return selectView;
    }

    private void init() {
        btnClose = selectView.findViewById(R.id.btn_pro_close2);
        upgradeBtn = selectView.findViewById(R.id.upgradeBtn);
        daysCount = selectView.findViewById(R.id.daysCount);
        transTxt = selectView.findViewById(R.id.transactions);
        planName = selectView.findViewById(R.id.planName);
        crown = selectView.findViewById(R.id.crown);
        upgradeBtn.setVisibility(View.VISIBLE);
        days = selectView.findViewById(R.id.days);

        planDesc = selectView.findViewById(R.id.planDesc);
        new SelectPlans().execute();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), MembershipActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {

             memPlans = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();

            return memPlans;
        }

        @SuppressLint("StringFormatMatches")
        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);

            if(memPlans.getDaysLeft() > 0) {
                //daysCount.setText(String.valueOf(memPlans.getDaysLeft()));
                daysCount.setText(String.format("%02d", memPlans.getDaysLeft()));
            }else {
                daysCount.setText("00");
            }

            Log.d("NO_OF_TRANSACTION", String.valueOf(memPlans.getTransactionsLeft()));
            if(memPlans.getTransactionsLeft() > 0){
                transTxt.setText(String.valueOf(memPlans.getTransactionsLeft()));
            }else {
                transTxt.setText("0");
            }
            plan = memPlans.getCurrent_membership();
            if(plan.equalsIgnoreCase("trial")){
                upgradeBtn.setText("UPGRADE");
                planName.setText("Free Trial");
                crown.setImageResource(R.drawable.ic_free_trial);
                if(memPlans.getTransactionsLeft() >=0) {
                    //days.setText("Days Left\n " + memPlans.getTransactionsLeft() + " Transaction Left");
                    days.setText("Days Left");
                }else{
                    days.setText("Days Left");
                }
            }else{
                if(!(memPlans.getDaysLeft() > 0)) {
                    upgradeBtn.setText("RENEW");
                }else {
                    //upgradeBtn.setVisibility(View.GONE);
                    upgradeBtn.setText("ADD ID VERIFICATION PACKAGE");
                }
                days.setText("Days Left");
                /*else {
                    upgradeBtn.setText("ADD ID VERIFICATION PACKAGE");
                }*/
                //planName.setText("Notary-App®");
                planName.setText(memPlans.getCurrent_membership());
                Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(memPlans.getCurrent_membership());
                if (matcher.find()){
                    crown.setImageResource(R.drawable.ic_silver_crown);
                } else {
                    crown.setImageResource(R.drawable.crown_gold);
                }

            }

            if(plan.equalsIgnoreCase("trial")) {
                planDesc.setText(getActivity().getResources().getString(R.string.freeTrialNew));
            }else{
                if(memPlans.getDaysLeft() <= 5 && memPlans.getDaysLeft() > 0){
                    planDesc.setText(getActivity().getResources().getString(R.string.freeTrialNewExpSoon, plan));
                }else if(memPlans.getDaysLeft() > 5 ){
                    planDesc.setText(getActivity().getResources().getString(R.string.freeTrialNewActive, plan));
                }else{
                    planDesc.setText(getActivity().getResources().getString(R.string.freeTrialNewExp, plan));
                }
            }
            upgradeBtn.setEnabled(true);
        }
    }

}
