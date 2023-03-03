package com.notaryapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.verifyauthenticate.EditProfileActivity;
import com.notaryapp.ui.activities.verifyauthenticate.NotarizeStepsActivity;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.Utils;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VAStepsAdapter extends RecyclerView.Adapter<VAStepsAdapter.MyView>{

    private List<SignerReg> list;
    private List<WitnessReg> witnessList;
    private List<String> licenseList;
    private Context context;
    private String fName = "";
    private String lName = "";
    private String phone = "";
    private String email = "";
    private int id,witCount;
    private NotarizeStepsActivity notarizeStepsActivity ;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private CircleImageView itemImage;
        private ConstraintLayout plusBox,itemView,itemImageContainer;
        private ImageView imgVerified, icTick;
        private ProgressBar itemImageProgress;


        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);
            itemImage = view.findViewById(R.id.itemImage);
            plusBox = view.findViewById(R.id.plusBox);
            itemView = view.findViewById(R.id.itemView);
            itemImageContainer = view.findViewById(R.id.itemImageContainer);
            imgVerified = view.findViewById(R.id.imageVerified);
            icTick = view.findViewById(R.id.icTick);
            itemImageProgress = view.findViewById(R.id.itemImageProgress);
        }

        void setItemTitle(String title) {
            itemTitle.setText(title);

        }

        void setAddSigner() {
            itemImage.setVisibility(View.INVISIBLE);
            plusBox.setVisibility(View.VISIBLE);
            itemImageContainer.setVisibility(View.INVISIBLE);

        }
    }

    public VAStepsAdapter(List<SignerReg> horizontalList,Context context) {
        this.list = horizontalList;
        this.context = context;
        //new GetWitnessCount().execute();
        new GetWitness().execute();
        this.notarizeStepsActivity = (NotarizeStepsActivity)context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_va_steps, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, @SuppressLint("RecyclerView") final int position) {

        if (position==0){
            holder.setAddSigner();
            holder.setItemTitle("Add Signer");
            holder.imgVerified.setVisibility(View.INVISIBLE);
        }
        else{
            final SignerReg stepsModel = list.get(position-1);
            String signerType = stepsModel.getSignerType();

          /*  if(signerType.equals("GOV")){
                holder.imgTick.setVisibility(View.VISIBLE);
            }
            else{
                holder.imgTick.setVisibility(View.GONE);
            }*/
            fName = stepsModel.getFirstName();
            lName = stepsModel.getLastName();
            id = stepsModel.getId();
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
            if(signerType.equals("GOV")){
                holder.imgVerified.setImageResource(R.drawable.ic_idv_e);
                holder.icTick.setVisibility(View.VISIBLE);
            }else if(signerType.equals("IDMANUALLY")){
                holder.imgVerified.setImageResource(R.drawable.ic_idv_m);
            }else if(signerType.equals("WIT")){
                holder.imgVerified.setImageResource(R.drawable.ic_wv_e);
                holder.icTick.setVisibility(View.VISIBLE);
            }else if(signerType.equals("WITNESSMANUALLY")){
                holder.imgVerified.setImageResource(R.drawable.ic_wv_m);
            }else{
                holder.imgVerified.setImageResource(R.drawable.ic_skn);
            }
            if(stepsModel.getProImagePath() != null) {
                String selfiePath = getSelfiePath(context, stepsModel.getProImagePath());
                Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);
                if(bitmap != null) {
                    holder.itemImageProgress.setVisibility(View.GONE);
                    holder.itemImage.setImageBitmap(bitmap);
                }
                else{

                    if(!stepsModel.getProImagePath().equalsIgnoreCase("")
                            && !stepsModel.getProImagePath().equalsIgnoreCase("null")) {
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

//                        Picasso.with(context).load(stepsModel.getProImagePath())
//                                .centerCrop()
//                                .resize(200, 200)
//                                .into(holder.itemImage);
                    }
                }
            }
            else {
                    holder.itemImage.setImageResource(R.drawable.ic_person);
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                if(position == 0) {
                    /*if(notarizeStepsActivity.isClick) {
                        Intent intent = new Intent(activity, VerifySignerActivity.class);
                        activity.startActivity(intent);
                    }else{
                        Utils.toastCenter(notarizeStepsActivity,notarizeStepsActivity.getResources().getString(R.string.click_message));
                    }*/
                    if(notarizeStepsActivity.getIsClick()) {
                        Intent intent = new Intent(activity, VerifySignerActivity.class);
                        activity.startActivity(intent);
                    }else{
                        //Utils.toastCenter(vej_notarizeStepsActivity,vej_notarizeStepsActivity.getResources().getString(R.string.click_message));
                    }
                }
                if(position > 0){
                    String email = list.get(position -1).getEmail();
                    String userType = list.get(position - 1).getSignerType();
                    if(userType.equals("WIT") || userType.equals("WITNESSMANUALLY")){
                        for (int i=0; i<witnessList.size(); i++){
                            if (email.equalsIgnoreCase(witnessList.get(i).getSignerEmail())){
                                witCount = witCount + 1;
                            }
                        }
                        if(witCount == 2){
                            Intent intent = new Intent(activity, EditProfileActivity.class);
                            intent.putExtra("EMAIL", email);
                            //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                            intent.putExtra("USERTYPE", userType);
                            //  intent.putExtra("USERTYPE", "signer");
                            activity.startActivity(intent);
                        }else{
                            Intent intent = new Intent(activity, VerifySignerActivity.class);
//                            intent.putExtra("PENDING", "");
                            intent.putExtra("EMAIL", email);
                            //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                            intent.putExtra("USERTYPE", userType);
                            if (userType.equals("WITNESSMANUALLY")){
                                intent.putExtra("IdType", "manually");
                            } else {
                                intent.putExtra("IdType", "null");
                            }
                            //  intent.putExtra("USERTYPE", "signer");
                            activity.startActivity(intent);
                           /* Fragment fragment;
                            fragment = new Notarize_SignerWitnessSignerProfileFragment();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace( VerifySignerActivity.REF_VIEW_CONTAINER, fragment).addToBackStack(null).commit();*/
                        }

                    }else {
                        Intent intent = new Intent(activity, EditProfileActivity.class);
                        intent.putExtra("EMAIL", email);
                        //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                        intent.putExtra("USERTYPE", userType);
                        //  intent.putExtra("USERTYPE", "signer");
                        activity.startActivity(intent);
                    }
                }
            }
        });

    }
    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }
    @Override
    public int getItemCount() {
        return list.size()+1;
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
    class GetWitness extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            witnessList = DatabaseClient.getInstance(context).getAppDatabase()
                    .witnessRegDao()
                    .getWitness();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            witCount = 0;


            /*String proImgFirst = witnessList.get(0).getProImagePath();
            String proImgSecond = witnessList.get(1).getProImagePath();
            witnessName1.setText(witnessList.get(0).getFirstName());
            witnessName2.setText(witnessList.get(1).getFirstName());
            String  selfiePath = getSelfiePath(context, proImgFirst);
            Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);
            String  selfiePath1 = getSelfiePath(context, proImgSecond);
            Bitmap bitmap1 = BitmapFactory.decodeFile(selfiePath1);
            if(bitmap != null) {
                witImageOne.setVisibility(View.VISIBLE);
                witImageOne.setImageBitmap(bitmap);
            }
            else{
                if(!proImgFirst.equalsIgnoreCase("") && !proImgFirst.equalsIgnoreCase("null")) {
                    witImageOne.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(proImgFirst).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(180, 180)
                            .into(witImageOne, new Callback() {
                                @Override
                                public void onSuccess() {
                                    homeprogress1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    witImageOne.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });

//                    Picasso.with(context).load(proImgFirst)
//                            .centerCrop()
//                            .resize(180,180)
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            .into(witImageOne);
                }
            }
            if(bitmap1 != null) {
                witImageTwo.setVisibility(View.VISIBLE);
                witImageTwo.setImageBitmap(bitmap1);
            }
            else{
                if(!proImgSecond.equalsIgnoreCase("") && !proImgSecond.equalsIgnoreCase("null")) {
                    witImageTwo.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(proImgSecond).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(180, 180)
                            .into(witImageTwo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    homeprogress2.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    witImageTwo.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });
//                    Picasso.with(context).load(proImgSecond)
//                            .centerCrop()
//                            .resize(200,200)
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            .into(witImageTwo);
                }
            }*/
        }
    }
}
