package com.notaryapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.MembershipSubscriptionPlans;
import com.notaryapp.ui.fragments.membership.MembershipInvoiceActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MembershipSubscriptionPlansAdapter extends RecyclerView.Adapter<MembershipSubscriptionPlansAdapter.MyView> {

    private List<MembershipSubscriptionPlans> list;
    private Activity context;
    private LoadingCompletedInterface loadingCompletedInterface;

    public MembershipSubscriptionPlansAdapter(LoadingCompletedInterface loadingCompletedInterface, Activity context, List<MembershipSubscriptionPlans> list){
        this.context = context;
        this.list = list;
        this.loadingCompletedInterface = loadingCompletedInterface;
    }


    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membership_subscription_list, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, @SuppressLint("RecyclerView") int position) {
        final MembershipSubscriptionPlans plan = list.get(position);

        if (plan.getPlanType().equalsIgnoreCase("NotaryAppYEARLY")){
            holder.crownImage.setImageDrawable(context.getResources().getDrawable(R.drawable.crown_gold));
            /*holder.packageName.setText(context.getResources().getText(R.string.yearly_membership));
            holder.packageDesc.setText(context.getResources().getText(R.string.billed_annually));
            holder.packageOPrice.setText(plan.getdPrice());
            holder.packageOPrice.setVisibility(View.VISIBLE);
            holder.strikethroughView.setVisibility(View.VISIBLE);
            holder.packagePrice.setText(plan.getoPrice());*/

        } else {
            holder.crownImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_silver_crown));
        }

        holder.packageName.setText(plan.getPlanName());
        holder.packageDesc.setText(plan.getBenefits());

        if (plan.getoPrice().equalsIgnoreCase("0.0")){
            holder.packageOPrice.setVisibility(View.INVISIBLE);
            holder.strikethroughView.setVisibility(View.INVISIBLE);
        } else {
            String subOPrice = "$"+plan.getoPrice();
            holder.packageOPrice.setText(subOPrice);
            holder.packageOPrice.setVisibility(View.VISIBLE);
            holder.strikethroughView.setVisibility(View.VISIBLE);
        }

        String subDPrice = "$"+plan.getdPrice();
        
        holder.packagePrice.setText(subDPrice);

        holder.getNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, plan.getoPrice().toString(), Toast.LENGTH_SHORT).show();
                //loadingCompletedInterface.loadingCompleted(false);
                //CustomDialog.showProgressDialog(context);
                getPricePlanID(position);
            }
        });

        //holder.crownImage.setImageDrawable(getImage(plan.getPlanType()));
        //holder.packageName.setText(getPackageName(plan.getPlanName()));

    }

    private void getPricePlanID(int position) {
        final MembershipSubscriptionPlans planID = list.get(position);
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
                        if (typeList.equals("all-pricing-plans")) {

                            if(data.has("data")){
                                //Log.d("PLAN_DETAILSS", String.valueOf(data.has("plans")));
                                JSONArray plans = data.getJSONArray("data");

                                JSONObject plan = plans.getJSONObject(0);
                                String pricingID = plan.getString("id");

                                //Toast.makeText(context, pricingID, Toast.LENGTH_SHORT).show();


                                Intent in = new Intent(context, MembershipInvoiceActivity.class);
                                in.putExtra("monthly_fee",planID.getdPrice());
                                in.putExtra("type",planID.getPlanType());
                                in.putExtra("planName",planID.getPlanName());
                                in.putExtra("category",planID.getCategory());
                                in.putExtra("planID",pricingID);
                                context.startActivity(in);
                                context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                                loadingCompletedInterface.loadingCompleted(true);



                            }

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(context, "Server Unavailable. Please try again later");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.cancelProgressDialog();
                CustomDialog.notaryappDialogSingle(context, "Server Unavailable. Please try again later");
            }

            @Override
            public void onFetchStart() {

            }
        };


        CustomDialog.showProgressDialog(context);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("plan", planID.getPlanType());

            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(context, iJsonListener, params, AppUrl.GET_ALL_PRICING_PLANS, "all-pricing-plans");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }



    /*private Drawable getImage(String planType) {
        Drawable image = null;

        if (planType.equalsIgnoreCase("NotaryAppYEARLY")){
            image = context.getResources().getDrawable(R.drawable.ic_gold_crown);
        } else {
            image = context.getResources().getDrawable(R.drawable.ic_silver_crown);
        }

        return image;

    }*/

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class MyView extends RecyclerView.ViewHolder{

        private ImageView crownImage;
        private TextView packageName, packageDesc, packagePrice, packageOPrice;
        private Button getNowBtn;
        private View strikethroughView;

        private MyView(View view) {
            super(view);

            crownImage = view.findViewById(R.id.icon_crown);
            packageName = view.findViewById(R.id.packageName);
            packageDesc = view.findViewById(R.id.packageDesc);
            packagePrice = view.findViewById(R.id.packagePrice);
            packageOPrice = view.findViewById(R.id.packageOPrice);
            getNowBtn = view.findViewById(R.id.btnGetNow);
            strikethroughView = view.findViewById(R.id.strikeThroughView);
        }

    }
}
