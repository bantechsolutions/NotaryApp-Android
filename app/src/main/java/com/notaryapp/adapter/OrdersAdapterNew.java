package com.notaryapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.model.OrderItems;
import com.notaryapp.ui.activities.membership.OrderDetailsActivity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrdersAdapterNew extends RecyclerView.Adapter<OrdersAdapterNew.MyViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<OrderItems> list, list1;
    private List<OrderItems> fullList;
    private Activity context;
    private LoadingCompletedInterface loadingCompletedInterface;

    public OrdersAdapterNew(LoadingCompletedInterface loadingCompletedInterface, Activity context, List<OrderItems> horizontalList) {
        this.list = horizontalList;
        this.context = context;
        this.loadingCompletedInterface = loadingCompletedInterface;
        fullList = horizontalList;
    }



    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
            Log.d("LOADING_ADAPTER", "Loading1");
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_ts, parent, false);
            Log.d("LOADING_ADAPTER", "Loading");
            return new LoadingViewHolder(itemView);
        }

        /*View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);

        return new MyView(itemView);*/
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Log.d("LOADING_ADAPTER", "Name "+holder.toString());
        if (holder instanceof MyViewHolder){
            Log.d("LOADING_ADAPTER", "Loading__"+position);
            final OrderItems item = list.get(position);
            if(item == null){
                Log.d("LOADING_ADAPTER", "Null__"+position);
                showLoadingView((LoadingViewHolder) holder, position);
            } else {
                Log.d("FILTER_LIST", "PLANN_"+item.getPlanname());
                Log.d("LOADING_ADAPTER", "DATA__"+position);
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
                    holder.singleuse.setVisibility(View.GONE);
                }
                else{

                    holder.idcount.setVisibility(View.VISIBLE);
                    holder.verification.setVisibility(View.VISIBLE);

                    holder.crownImage.setVisibility(View.GONE);
                    holder.singleuse.setVisibility(View.GONE);
                    //holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head));

                    holder.idcount.setText(item.getUnit_purchased());
                    if (item.getUnit_purchased().equalsIgnoreCase("10")){
                        holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head));
                    }
                    else if(item.getUnit_purchased().equalsIgnoreCase("20")){
                        holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head_two));
                        //planCardColor.setBackground(getDrawable(R.drawable.plans_head_two));
                    }
                    else if(item.getUnit_purchased().equalsIgnoreCase("30")){
                        holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head_three));
                    } else {
                        holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.plans_head_four));
                        holder.idcount.setVisibility(View.GONE);
                        holder.verification.setVisibility(View.GONE);
                        holder.singleuse.setVisibility(View.VISIBLE);
                    }
                }


                holder.conLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context.getApplicationContext(), OrderDetailsActivity.class);
                        intent.putExtra("orderDetails", (Serializable) item);
                        context.startActivity(intent);

                    }
                });
            }





            //loadingCompletedInterface.loadingCompleted(true);
        }
        else {
            Log.d("LOADING_ADAPTER", "Loading_64654");
            showLoadingView((LoadingViewHolder) holder, position);
        }



    }

    private void showLoadingView(LoadingViewHolder MyViewHolder, int position){

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
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    list1 = fullList;
                    Log.d("FILTER_LIST", "FULL");
                } else {
                    Log.d("FILTER_LIST", "FILTER");
                    List<OrderItems> filteredList = new ArrayList<>();
                    for (OrderItems item : fullList){
                        if(item.getObjecttype().equalsIgnoreCase("subscription")){
                            Log.d("FILTER_LIST", "FOUND ");
                            if (item.getPlanname().toLowerCase().contains(charString)){
                                filteredList.add(item);
                            }
                        }
                        Log.d("FILTER_LIST", " "+ item.getId() + " "+
                                item.getObjecttype()+ " "+ item.getStatus()+ " "+ item.toString());
                        //Log.d("FILTER_LIST", "PLAN_NAME_: "+ item.getPlanname());
                        if (item.getId().toLowerCase().contains(charString)){
                            Log.d("FILTER_LIST", "MATH");
                            filteredList.add(item);
                        }
                    }
                    Log.d("FILTER_LIST", "SIZE_"+ filteredList.size());
                    list1 = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = list1;
                Log.d("FILTER_LIST", "TEST1");
                return filterResults;




                /*List<OrderItems> filteredList = new ArrayList<>();




                if (charSequence == null || charSequence.length() == 0){
                    fullList = list;
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (OrderItems item : list){
                        if (item.getPlanname().toString().contains(filterPattern)){
                            filteredList.add(item);
                        }
                    }
                    fullList = filteredList;
                }

                FilterResults results = new FilterResults();
                results.values = fullList;
                return results;*/


                //return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //list.clear();
                list = (ArrayList<OrderItems>) filterResults.values;
                Log.d("FILTER_LIST", "TEST2");
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout profileImageLayout, conLayout;
        private ImageView imageStatus, crownImage;
        private TextView planName, orderIdText,amountText,dateText, idcount, verification, singleuse;

        private MyViewHolder(View view) {
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

            singleuse = view.findViewById(R.id.singleuse);

        }
    }

    private class LoadingViewHolder extends OrdersAdapterNew.MyViewHolder{

        ProgressBar progressBar;
        public LoadingViewHolder(View itemView){
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
