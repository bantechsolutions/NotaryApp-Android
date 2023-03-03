package com.notaryapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.OrderItems;
import com.notaryapp.ui.activities.membership.OrderDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyView> {

    private List<OrderItems> list;
    private Activity context;
    private LoadingCompletedInterface loadingCompletedInterface;

    public OrdersAdapter(LoadingCompletedInterface loadingCompletedInterface, Activity context, List<OrderItems> horizontalList) {
        this.list = horizontalList;
        this.context = context;
        this.loadingCompletedInterface = loadingCompletedInterface;
    }



    @NotNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final OrderItems item = list.get(position);

        holder.planName.setText(getNameType(item.getObjecttype(), item.getUnit_purchased(), item.getPlanname()));
        holder.orderIdText.setText("Order No. "+item.getId());
        if(item.getPrice().equalsIgnoreCase("0.0")){
            holder.amountText.setText("$3.99");
        }
        else{
            holder.amountText.setText("$"+item.getPrice());
        }

        holder.dateText.setText(getTimeStamp(item.getCreated_on()));
        holder.imageStatus.setImageDrawable(getImage(item.getStatus()));

        if(item.getObjecttype().equalsIgnoreCase("subscription")){
            Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(item.getPlanname());
            if (matcher.find()){
                holder.crownImage.setImageResource(R.drawable.ic_silver_crown);
            } else {
                holder.crownImage.setImageResource(R.drawable.crown_gold);
            }

            holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head_membership));
            holder.crownImage.setVisibility(View.VISIBLE);
            holder.idcount.setVisibility(View.GONE);
            holder.verification.setVisibility(View.GONE);
        }
        else{

            holder.idcount.setVisibility(View.VISIBLE);
            holder.verification.setVisibility(View.VISIBLE);

            holder.crownImage.setVisibility(View.GONE);
            holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head));

            holder.idcount.setText(item.getUnit_purchased());
        }


        holder.conLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context.getApplicationContext(), OrderDetailsActivity.class);
                intent.putExtra("orderDetails", (Serializable) item);
                context.startActivity(intent);

            }
        });


            loadingCompletedInterface.loadingCompleted(true);

    }


    private Drawable getImage(String status) {
        Drawable image = null;

        if(status.equalsIgnoreCase("completed")){

            image = context.getResources().getDrawable(R.drawable.ic_completed);
        }
        else if(status.equalsIgnoreCase("pending")){

            image = context.getResources().getDrawable(R.drawable.order_pending);
        }
        else{

            image = context.getResources().getDrawable(R.drawable.order_failed);
        }

        return image;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public String getTimeStamp(String time) {

        String date = "";

        String dojDateArry[] = time.split("T");
        date = dojDateArry[0];

        return date;
    }

    public String getNameType(String item, String unit_purchased, String plan_name) {

        String itemname = "";
        if(item.equalsIgnoreCase("subscription")){
            itemname = plan_name;
        }
        else{
            itemname = "Notary-App®"+ unit_purchased;
        }
        return itemname;
    }

    public class MyView extends RecyclerView.ViewHolder {

        private ConstraintLayout profileImageLayout, conLayout;
        private ImageView imageStatus, crownImage;
        private TextView planName, orderIdText,amountText,dateText, idcount, verification;

        private MyView(View view) {
            super(view);

            idcount = view.findViewById(R.id.idcount);
            verification = view.findViewById(R.id.verification);

            profileImageLayout = view.findViewById(R.id.profileImageLayout);
            conLayout = view.findViewById(R.id.conLayout);

            crownImage = view.findViewById(R.id.crownImage);
            imageStatus = view.findViewById(R.id.imageStatus);

            planName = view.findViewById(R.id.planName);
            orderIdText = view.findViewById(R.id.orderIdText);
            amountText = view.findViewById(R.id.dateText);
            dateText = view.findViewById(R.id.sealText);

        }
    }
}
