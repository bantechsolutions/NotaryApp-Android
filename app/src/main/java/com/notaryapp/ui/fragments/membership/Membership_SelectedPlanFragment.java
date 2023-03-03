package com.notaryapp.ui.fragments.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.FragmentViewUtil;


public class Membership_SelectedPlanFragment extends Fragment {

    private View selectedPlanView;
    private TextView txtMoreDeatils,txtPlanName,txtPlanDes,txtPlanTrans,txtDays;
    private Button closeBtn,upgradeBtn;
    private String selectedPlan,planType,benefits;
    private int daysLeft;
    private int transCount;
    private String memCategory;
    private VACustomer memPlans;
    private String from;

    public Membership_SelectedPlanFragment() {
        // Required empty public constructor
    }
    public Membership_SelectedPlanFragment(String from) {
        this.from = from;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        selectedPlanView = inflater.inflate(R.layout.fragment_membership_selectedplan, container, false);
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
        return selectedPlanView;
    }
    private void init(){
       // selectedPlan = SignInFragment.MEM_PLANS;
       // benefits = SignInFragment.BENEFITS;
        closeBtn = selectedPlanView.findViewById(R.id.btn_pro_close4);
        txtMoreDeatils = selectedPlanView.findViewById(R.id.textMoreDetails);
        upgradeBtn = selectedPlanView.findViewById(R.id.upgradeBtn);
        txtPlanName = selectedPlanView.findViewById(R.id.planName);
        txtPlanDes = selectedPlanView.findViewById(R.id.planDesc);
        txtPlanTrans = selectedPlanView.findViewById(R.id.transactions);
        txtDays = selectedPlanView.findViewById(R.id.daysCount);
        new SelectPlans().execute();
        if(from == null){
            from = "";
        }
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

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);

            memCategory = memPlans.getCurrent_membership();
            selectedPlan = "selected name"; //memPlans.getPlanType();
            daysLeft = memPlans.getDaysLeft() > 0 ? memPlans.getDaysLeft() : 0;
            transCount = memPlans.getTransactionsLeft() > 0 ? memPlans.getTransactionsLeft() : 0;
            benefits = "benefits"; //memPlans.getBenefits();
            String planName = "plan name"; //memPlans.getName();

            txtPlanName.setText(planName);
            txtPlanDes.setText(benefits);
            txtDays.setText(daysLeft+" day");
            txtPlanTrans.setText(transCount+" ID Verifications");

        }

    }
}
