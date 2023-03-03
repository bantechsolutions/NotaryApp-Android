package com.notaryapp.ui.fragments.membership;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.model.MembershipPackagePlans;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Membership_PremiumPackageFragment extends Fragment {
    private ConstraintLayout layoutStarter, layoutInter, layoutProffessional, layoutPayasGo;
    private ImageView starterImage, interImage, proImage, payImage;

    private View packView;
    private TextView txtTransStart, txteachStart, txtPackageStart;
    private TextView txtTransproff, txteachProff, txtPackageProff;
    private TextView txtTransInter, txteachInter, txtPackageInter, pgNetCost;
    private TextView txtTransPayAs;//,txteachPayAs,txtPackagePayAs;
    private ArrayList<MembershipPackagePlans> arryStart = new ArrayList<>();
    private ArrayList<MembershipPackagePlans> arryProff = new ArrayList<>();
    private ArrayList<MembershipPackagePlans> arryProInter = new ArrayList<>();
    private ArrayList<MembershipPackagePlans> arryPayGo = new ArrayList<>();
    private IJsonListener iJsonListener;
    private MembershipPackagePlans membershipPackagePlans;
    private Button btnClose, btnContinue;
    private int selectedId = -1;
    private ArrayList<MembershipPackagePlans> selectedArray;

    public Membership_PremiumPackageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        packView = inflater.inflate(R.layout.fragment_membership_premium_package, container, false);

        getPlan();
        init();

        layoutStarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                starterImage.setVisibility(View.VISIBLE);
                interImage.setVisibility(View.GONE);
                proImage.setVisibility(View.GONE);
                payImage.setVisibility(View.GONE);

                selectedId = 0;
                selectedArray = new ArrayList<>();
                selectedArray = arryStart;

            }
        });
        layoutInter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                starterImage.setVisibility(View.GONE);
                interImage.setVisibility(View.VISIBLE);
                proImage.setVisibility(View.GONE);
                payImage.setVisibility(View.GONE);

                selectedId = 1;
                selectedArray = new ArrayList<>();
                selectedArray = arryProInter;

            }
        });
        layoutProffessional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                starterImage.setVisibility(View.GONE);
                interImage.setVisibility(View.GONE);
                proImage.setVisibility(View.VISIBLE);
                payImage.setVisibility(View.GONE);

                selectedId = 2;
                selectedArray = new ArrayList<>();
                selectedArray = arryProff;

            }
        });
        layoutPayasGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                starterImage.setVisibility(View.GONE);
                interImage.setVisibility(View.GONE);
                proImage.setVisibility(View.GONE);
                payImage.setVisibility(View.VISIBLE);

                selectedId = 3;
                selectedArray = new ArrayList<>();
                selectedArray = arryPayGo;

            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedId != -1) {

                    Intent in = new Intent(getActivity(), MembershipInvoiceActivity.class);
                    in.putExtra("monthly_fee",selectedArray.get(0).getCost());
                    in.putExtra("type",selectedArray.get(0).getPlanType());
                    in.putExtra("category",selectedArray.get(0).getCategory());
                    in.putExtra("planName",selectedArray.get(0).getPlanName());
                    startActivity(in);


                } else {

                    CustomDialog.notaryappDialogSingle(getActivity(), "Please select ID Verification package to proceed");

                }
            }
        });
        return packView;
    }

    private void init() {
        btnClose = packView.findViewById(R.id.btn_pro_close2);
        btnContinue = packView.findViewById(R.id.btnContinue);

        layoutStarter = packView.findViewById(R.id.starterContainer);
        layoutInter = packView.findViewById(R.id.interContainer);
        layoutProffessional = packView.findViewById(R.id.proContainer);
        layoutPayasGo = packView.findViewById(R.id.payContainer);

        starterImage = packView.findViewById(R.id.starterImage);
        interImage = packView.findViewById(R.id.interImage);
        proImage = packView.findViewById(R.id.proImage);
        payImage = packView.findViewById(R.id.payImage);

        txtPackageStart = packView.findViewById(R.id.sCount);
        txteachStart = packView.findViewById(R.id.sCost);
        txtTransStart = packView.findViewById(R.id.sNetCost);

        txtPackageInter = packView.findViewById(R.id.iCount);
        txteachInter = packView.findViewById(R.id.iCost);
        txtTransInter = packView.findViewById(R.id.iNetCost);

        txtPackageProff = packView.findViewById(R.id.pCount);
        txteachProff = packView.findViewById(R.id.pCost);
        txtTransproff = packView.findViewById(R.id.pNetCost);
        txtTransPayAs = packView.findViewById(R.id.payCount);

        pgNetCost = packView.findViewById(R.id.pgNetCost);

        starterImage.setVisibility(View.GONE);
        interImage.setVisibility(View.GONE);
        proImage.setVisibility(View.GONE);
        payImage.setVisibility(View.GONE);

    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), MembershipActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    private void getPlan() {

        IJsonListener iJsonListener = new IJsonListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("app-all-plans")) {
                            String memCategory = "", memPlanName = "", benefits = "", tranlimit = "", oPrice = "", dprice = "",
                                    planType = "", monthlyFee = "", singleTranFee = "", category = "";

                            JSONArray plans_array = data.getJSONArray("plans");
                            if (plans_array.length() != 0) {
                                for (int i = 0; i < plans_array.length(); i++) {
                                    JSONObject json_inside = plans_array.getJSONObject(i);
                                    memCategory = json_inside.getString("vl_plan_cat");
                                    memPlanName = json_inside.getString("vl_plan_name");
                                    benefits = json_inside.getString("benefits");
                                    tranlimit = json_inside.getString("tranlimit");
                                    oPrice = json_inside.getString("oprice");
                                    dprice = json_inside.getString("dprice");
                                    planType = json_inside.getString("type");
                                    singleTranFee = json_inside.getString("single_tran_fee");
                                    monthlyFee = json_inside.getString("monthly_fee");
                                    category = json_inside.getString("category");

                                    if (planType.equals("VLP-1")) {

                                        arryStart.add(new MembershipPackagePlans(
                                                memPlanName, benefits, tranlimit, singleTranFee, oPrice, planType, monthlyFee,category));
                                    }
                                    if (planType.equals("VLP-2")) {
                                        arryProInter.add(new MembershipPackagePlans(
                                                memPlanName, benefits, tranlimit, singleTranFee, oPrice, planType, monthlyFee,category));
                                    }
                                    if (planType.equals("VLP-3")) {
                                        arryProff.add(new MembershipPackagePlans(
                                                memPlanName, benefits, tranlimit, singleTranFee, oPrice, planType, monthlyFee,category));
                                    }
                                    if (planType.equals("VLP-3")) {
                                        arryProff.add(new MembershipPackagePlans(
                                                memPlanName, benefits, tranlimit, singleTranFee, oPrice, planType, monthlyFee,category));
                                    }
                                    if (planType.equals("PAYG")) {
                                        arryPayGo.add(new MembershipPackagePlans(
                                                memPlanName, "SINGLE \nUSE", tranlimit, singleTranFee, oPrice, planType, monthlyFee,category));
                                    }

                                }

                                membershipPackagePlans = arryStart.get(0);
                                txtPackageStart.setText(membershipPackagePlans.getTransLimit());
                                txteachStart.setText("$" + membershipPackagePlans.getSingleFee());
                                txtTransStart.setText("$" + membershipPackagePlans.getCost());

                                membershipPackagePlans = arryProInter.get(0);
                                txtPackageInter.setText(membershipPackagePlans.getTransLimit());
                                txteachInter.setText("$" + membershipPackagePlans.getSingleFee());
                                txtTransInter.setText("$" + membershipPackagePlans.getCost());

                                membershipPackagePlans = arryProff.get(0);
                                txtPackageProff.setText(membershipPackagePlans.getTransLimit());
                                txteachProff.setText("$" + membershipPackagePlans.getSingleFee());
                                txtTransproff.setText("$" + membershipPackagePlans.getCost());

                                membershipPackagePlans = arryPayGo.get(0);
                                txtTransPayAs.setText(membershipPackagePlans.getBenefits());
                                pgNetCost.setText("$" + membershipPackagePlans.getCost());

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
                params.put("cat", "package");

            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_ALL_PLANS, "app-all-plans");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
