package com.notaryapp.ui.fragments.membership;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.adapter.MembershipSubscriptionPlansAdapter;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.MembershipSubscriptionPlans;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Membership_PackageFragment extends Fragment {
    private TextView dprice,txtPrice;
    private View planView;
    private Button btnUpgrade,btnClose;
    private String plan, disprice = "", type = "", category = "";
    private double monthly_fee;
    private String from;
    private RecyclerView recyclerView;
    private Activity activity;
    private GridLayoutManager layoutManager;
    private MembershipSubscriptionPlans membershipSubItem;
    private List<MembershipSubscriptionPlans> listMembershipSub;
    private LoadingCompletedInterface loadingCompletedInterface;
    MembershipSubscriptionPlansAdapter membershipSubscriptionPlansAdapter;

    public Membership_PackageFragment() {
        // Required empty public constructor
    }

    public Membership_PackageFragment(String from) {
        // Required empty public constructor
        this.from = from;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        planView = inflater.inflate(R.layout.fragment_membership_package, container, false);
        activity = getActivity();
        init();

        getPlan();

        loadingCompletedInterface = new LoadingCompletedInterface() {
            @Override
            public void loadingCompleted(boolean complted) {

                CustomDialog.cancelProgressDialog();
            }
        };
        //new SelectPlans().execute();
        /*
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(from == null){
                    from = "";
                }

                double dollarAmount = monthly_fee;
                double centAmount = dollarAmount*100.00;

                Intent in = new Intent(getActivity(), MembershipInvoiceActivity.class);
                in.putExtra("monthly_fee",String.valueOf(dollarAmount));
                in.putExtra("type",type);
                in.putExtra("category",category);
                startActivity(in);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                */
                //// comment

                /*if(from.equalsIgnoreCase("settings")){

                    double dollarAmount = Double.valueOf(monthly_fee);
                    double centAmount = dollarAmount*100.00;

                    Intent in = new Intent(getActivity(), MembershipInvoiceActivity.class);
                    in.putExtra("monthly_fee",String.valueOf(dollarAmount));
                    in.putExtra("type",type);
                    in.putExtra("category",category);
                    startActivity(in);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                } else if(plan.equalsIgnoreCase("trial")){
                    double dollarAmount = Double.valueOf(monthly_fee);
                    double centAmount = dollarAmount*100.00;

                    Intent in = new Intent(getActivity(), MembershipInvoiceActivity.class);
                    in.putExtra("monthly_fee",String.valueOf(dollarAmount));
                    in.putExtra("type",type);
                    in.putExtra("category",category);
                    startActivity(in);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    //loadFragment(new Membership_InvoiceFragment(monthly_fee, "subscription"));
                } else{
                    //Sourav 20201124
                    //loadFragment(new Membership_PremiumPackageFragment());

                    double dollarAmount = Double.valueOf(monthly_fee);
                    double centAmount = dollarAmount*100.00;

                    Intent in = new Intent(getActivity(), MembershipInvoiceActivity.class);
                    in.putExtra("monthly_fee",String.valueOf(dollarAmount));
                    in.putExtra("type",type);
                    in.putExtra("category",category);
                    startActivity(in);
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }*/

                //// comment
        /*
            }
        }); */
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return planView;
    }
    private void init(){
        /*dprice = planView.findViewById(R.id.dprice);
        txtPrice = planView.findViewById(R.id.txtPrice);
        btnUpgrade = planView.findViewById(R.id.upgradeBtn);*/
        btnClose = planView.findViewById(R.id.btn_pro_close2);
        recyclerView = planView.findViewById(R.id.recyclerPackage);
        listMembershipSub = new ArrayList<>();

    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), MembershipActivity.REF_VIEW_CONTAINER,fragment,true);
    }


    private void getPlan() {

        IJsonListener iJsonListener = new IJsonListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onFetchSuccess(JSONObject data, String typeList) {
                    CustomDialog.cancelProgressDialog();
                    //      RequestQueueService.cancelProgressDialog();
                    try {
                        //Now check result sent by our POSTAPIRequest class
                        if (data != null) {
                            //Log.d("PLAN_DETAILS", data.toString());
                            if (typeList.equals("app-all-plans")) {

                                if(data.has("plans")){
                                    //Log.d("PLAN_DETAILSS", String.valueOf(data.has("plans")));
                                    JSONArray plans = data.getJSONArray("plans");
                                    //Log.d("PLAN_DETAILSS", plans.toString());
                                    for(int i = 0; i < plans.length(); i++){
                                        //Log.d("PLAN_DETAILSS", String.valueOf(plans.length()));
                                        membershipSubItem = new MembershipSubscriptionPlans();

                                        JSONObject plan = plans.getJSONObject(i);

                                        //Log.d("PLAN_DETAILSS", plan.getString("vl_plan_name"));

                                        String planName = plan.getString("vl_plan_name");
                                        String planCategory = plan.getString("vl_plan_cat");
                                        String planType = plan.getString("type");
                                        String dPrice = plan.getString("dprice");
                                        String oPrice = plan.getString("oprice");
                                        String monthlyFee = plan.getString("monthly_fee");
                                        String category = plan.getString("category");
                                        String benefits = plan.getString("benefits");

                                        membershipSubItem.setPlanName(planName);
                                        membershipSubItem.setPlanCategory(planCategory);
                                        membershipSubItem.setPlanType(planType);
                                        membershipSubItem.setdPrice(dPrice);
                                        membershipSubItem.setoPrice(oPrice);
                                        membershipSubItem.setMonthlyFee(monthlyFee);
                                        membershipSubItem.setCategory(category);
                                        membershipSubItem.setBenefits(benefits);


                                        listMembershipSub.add(membershipSubItem);
                                    }




                                    /*JSONObject plan = plans.getJSONObject(0);

                                    category = plan.getString("category");
                                    type = plan.getString("type");
                                    disprice = String.valueOf(plan.getDouble("oprice"));
                                    monthly_fee = plan.getDouble("dprice");
                                    String price = String.valueOf(monthly_fee);
                                    dprice.setText("$"+disprice);
                                    txtPrice.setText("$"+price);*/

                                    setupRecycler();


                                }

                            }
                        }
                    } catch (Exception e) {
                        CustomDialog.cancelProgressDialog();
                        CustomDialog.notaryappDialogSingle(getActivity(), "Server Unavailable. Please try again later");
                        //e.printStackTrace();
                    }
                }

                @Override
                public void onFetchFailure(String msg) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(getActivity(), "Server Unavailable. Please try again later");
                }

                @Override
                public void onFetchStart() {

                }
            };


        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("cat", "subscription");

            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_ALL_PLANS, "app-all-plans");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void setupRecycler() {
        membershipSubscriptionPlansAdapter = new MembershipSubscriptionPlansAdapter(loadingCompletedInterface, activity, listMembershipSub);
        layoutManager = new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(membershipSubscriptionPlansAdapter);
    }

    @SuppressLint("StaticFieldLeak")
    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {
            VACustomer memPlans = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();
            return memPlans;
        }

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);

            plan = memPlans.getCurrent_membership();

        }
    }



}
