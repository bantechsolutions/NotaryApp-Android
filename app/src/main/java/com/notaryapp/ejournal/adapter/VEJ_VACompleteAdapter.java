package com.notaryapp.ejournal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_EditProfileActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VEJ_VACompleteAdapter extends RecyclerView.Adapter<VEJ_VACompleteAdapter.MyView>{

    private List<SignerReg> list;
    private List<String> licenseList;
    private Context context;
    private String fName = "";
    private String lName = "";
    private String phone = "";
    private String email = "";
    private int id,witCount;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private CircleImageView itemImage;
        private ImageView imageVerified;
        private ConstraintLayout plusBox,itemView,itemImageContainer;
        private ProgressBar itemImageProgress;


        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);
            itemImage = view.findViewById(R.id.itemImage);
            plusBox = view.findViewById(R.id.plusBox);
            itemView = view.findViewById(R.id.itemView);
            itemImageContainer = view.findViewById(R.id.itemImageContainer);
            imageVerified = view.findViewById(R.id.imageVerified);

            itemImageProgress= view.findViewById(R.id.itemImageProgress);
        }

        void setItemTitle(String title) {
            itemTitle.setText(title);

        }

    }

    public VEJ_VACompleteAdapter(List<SignerReg> horizontalList, Context context) {
        this.list = horizontalList;
        this.context = context;
        new GetWitnessCount().execute();
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_va_steps, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, @SuppressLint("RecyclerView") final int position) {

            final SignerReg stepsModel = list.get(position);
            fName = stepsModel.getFirstName();
            lName = stepsModel.getLastName();
            id = stepsModel.getId();

            if(stepsModel.getSignerType().equals("GOV")){

                holder.imageVerified.setImageDrawable(context.getDrawable(R.drawable.ic_idv_e));
            } else if(stepsModel.getSignerType().equals("IDMANUALLY")){

                holder.imageVerified.setImageDrawable(context.getDrawable(R.drawable.ic_idv_m));
            } else if(stepsModel.getSignerType().equals("WIT")){

                holder.imageVerified.setImageDrawable(context.getDrawable(R.drawable.ic_wv_e));
            } else if(stepsModel.getSignerType().equals("WITNESSMANUALLY")){

                holder.imageVerified.setImageDrawable(context.getDrawable(R.drawable.ic_wv_m));
            } else{

                holder.imageVerified.setImageDrawable(context.getDrawable(R.drawable.ic_skn));
            }

            if(fName != null){
                fName = fName.toLowerCase();
                fName = Utils.capitalizeFirst(fName);
            }
            if(lName ==null) {
            }else{
                lName = lName.toLowerCase();
                lName = Utils.capitalizeFirst(lName);
            }
            if((fName != null) && (lName != null) ){
                holder.setItemTitle(Utils.capitalizeFirst( fName+" " +lName));
            }else{
                holder.setItemTitle(fName +" "+lName);
            }

            holder.setItemTitle(fName +" "+lName);


        if(!stepsModel.getProImagePath().equalsIgnoreCase("") || !stepsModel.getProImagePath().equalsIgnoreCase("null")){

            Picasso.with(context).load(stepsModel.getProImagePath()).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(holder.itemImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.itemImageProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.itemImage.setBackground(context.getResources().getDrawable(R.drawable.logo));
                        }
                    });

            /*Picasso.with(context).load(stepsModel.getProImagePath())
                    .centerCrop()
                    .resize(200,200)
                    .into(holder.itemImage);*/

        }
        else{
            holder.itemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                String email = list.get(position).getEmail();
                String userType = list.get(position).getSignerType();
                    /*Intent intent = new Intent(activity, EditProfileActivity.class);
                    String email = list.get(position).getEmail();
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("DONE", "DONE");
                    intent.putExtra("USERTYPE", "signer");
                    activity.startActivity(intent);*/
                Intent intent = new Intent(activity, VEJ_EditProfileActivity.class);
                intent.putExtra("EMAIL", email);
                //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                intent.putExtra("USERTYPE", userType);
                intent.putExtra("DONE", "DONE");
                activity.startActivity(intent);

                /*if(userType.equals("WIT")){
                   // if(witCount == 2){
                        Intent intent = new Intent(activity, EditProfileActivity.class);
                        intent.putExtra("EMAIL", email);
                        //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                        intent.putExtra("USERTYPE", userType);
                        intent.putExtra("DONE", "DONE");
                        activity.startActivity(intent);
                   *//* }else{
                        Intent intent = new Intent(activity, VerifySignerActivity.class);
//                            intent.putExtra("PENDING", "");
                        intent.putExtra("EMAIL", email);
                        //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                        intent.putExtra("USERTYPE", userType);
                        //  intent.putExtra("USERTYPE", "signer");
                        activity.startActivity(intent);
                           *//**//* Fragment fragment;
                            fragment = new Notarize_SignerWitnessSignerProfileFragment();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace( VerifySignerActivity.REF_VIEW_CONTAINER, fragment).addToBackStack(null).commit();*//**//*
                    }*//*

                }else {
                    Intent intent = new Intent(activity, EditProfileActivity.class);
                    intent.putExtra("EMAIL", email);
                    //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                    intent.putExtra("USERTYPE", userType);
                    intent.putExtra("DONE", "DONE");
                    activity.startActivity(intent);
                }*/

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    class GetWitnessCount extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            witCount = DatabaseClient.getInstance(context).getAppDatabase()
                    .witnessRegDao()
                    .getCount();
            return witCount;
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }
}
