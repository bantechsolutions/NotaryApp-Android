package com.notaryapp.ejournal.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_SignAndThumbActivity;
import com.notaryapp.ejournal.fragments.addsignaturethumb.VEJ_NotarizeSignatureAndThumbInstructionFragment;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VEJ_AllSignersRecycleerViewAdapter extends RecyclerView.Adapter<VEJ_AllSignersRecycleerViewAdapter.MyView> {

    private List<SignerReg> list;
    private Context context;
    private String fName = "";
    private String lName = "";
    private String sType = "";

    private String sCity="";
    private String sState="";

    static class MyView extends RecyclerView.ViewHolder {

        /*private TextView itemTitle;
        private ImageView selected;
        private RoundedImageView proImage;
        private ConstraintLayout itemView;
        private ProgressBar homeprogress;*/

        private CircleImageView signerProImg;
        private ProgressBar progressBar;
        private TextView signerName;
        private TextView signerCity;
        private TextView signerState;
        private ImageView imageVerified;
        private Button statusBtn;
        private ImageView thumprintImg;
        private ImageView penImg;
        private CardView cardView1;
        private ConstraintLayout overlay_png, overlay_thumb;


        private MyView(View view) {
            super(view);
            signerProImg = view.findViewById(R.id.itemImage);
            progressBar = view.findViewById(R.id.itemImageProgress);
            signerName = view.findViewById(R.id.signerNameTxt);
            signerCity = view.findViewById(R.id.signerCityTxt);
            signerState = view.findViewById(R.id.signerStateTxt);
            imageVerified = view.findViewById(R.id.imageVerified);
            statusBtn = view.findViewById(R.id.statusBtn);
            thumprintImg = view.findViewById(R.id.thumprintImg);
            penImg = view.findViewById(R.id.penImg);
            cardView1 = view.findViewById(R.id.cardView1);
            overlay_png = view.findViewById(R.id.overlay_png);
            overlay_thumb = view.findViewById(R.id.overlay_thumb);
        }

        void setItemTitle(String title) {
            signerName.setText(title);

        }
        void setItemCity(String title) {
            signerCity.setText(title);

        }
        void setItemState(String title) {
            signerState.setText(title);

        }
    }


    public VEJ_AllSignersRecycleerViewAdapter(List<SignerReg> horizontalList, Context context) {
        this.list = horizontalList;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vej_item_all_signers, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final SignerReg signerReg = list.get(position);

        fName = signerReg.getFirstName();
        lName = signerReg.getLastName();
        sType = signerReg.getSignerType();

        sCity = signerReg.getCityName();
        sState = signerReg.getStateName();

        //holder.signerCity.setText(sCity);
        //holder.signerState.setText(sState);
        holder.setItemCity(sCity);
        holder.setItemState(sState);

        if(sType.equals("GOV")){
            holder.imageVerified.setImageResource(R.drawable.ic_idv_e);
        }else if(sType.equals("IDMANUALLY")){
            holder.imageVerified.setImageResource(R.drawable.ic_idv_m);
        }else if(sType.equals("WIT")){
            holder.imageVerified.setImageResource(R.drawable.ic_wv_e);
        }else if(sType.equals("WITNESSMANUALLY")){
            holder.imageVerified.setImageResource(R.drawable.ic_wv_m);
        } else{
            holder.imageVerified.setImageResource(R.drawable.ic_skn);
        }

        /*if (!signerReg.getSignatureImagePath().equalsIgnoreCase("")
                && !signerReg.getSignatureImagePath().equalsIgnoreCase("null")){
            holder.statusBtn.setText("Collected");
        } else {
            holder.statusBtn.setText("Collect");
        }*/

        if(signerReg.getProImagePath() != null) {
            String selfiePath = getSelfiePath(context, signerReg.getProImagePath());
            Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);
            if(bitmap != null) {
                holder.progressBar.setVisibility(View.GONE);
                holder.signerProImg.setImageBitmap(bitmap);
            }
            else{

                if(!signerReg.getProImagePath().equalsIgnoreCase("")
                        && !signerReg.getProImagePath().equalsIgnoreCase("null")) {
                    Picasso.with(context).load(signerReg.getProImagePath()).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(200, 200)
                            .into(holder.signerProImg, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    holder.signerProImg.setBackground(context.getResources().getDrawable(R.drawable.logo));
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
            holder.signerProImg.setImageResource(R.drawable.ic_person);
        }

        /*if (!signerReg.getProImagePath().equalsIgnoreCase("")
                && !signerReg.getProImagePath().equalsIgnoreCase("null")) {

            Log.d("SIGNER_PIC",signerReg.getProImagePath().toString());

            Picasso.with(context).load(signerReg.getProImagePath()).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(holder.signerProImg, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {
                            holder.signerProImg.setBackground(context.getResources().getDrawable(R.drawable.logo));

                            *//*holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.ic_id_card));
                            holder.homeprogress.setVisibility(View.GONE);*//*
                        }
                    });


//            Picasso.with(context).load(stepsModel.getProImagePath())
//                    .centerCrop()
//                    .resize(200, 200)
//                    .into(holder.proImage);
        }*/

        if (fName != null) {
            fName = fName.toLowerCase();
            fName = Utils.capitalizeFirst(fName);
        }
        if (lName != null) {
            lName = lName.toLowerCase();
            lName = Utils.capitalizeFirst(lName);
        }
        if ((fName != null) && (lName != null)) {
            holder.setItemTitle(Utils.capitalizeFirst(fName + " " + lName));
        } else {
            holder.setItemTitle(fName + " " + lName);
        }

        holder.setItemTitle(fName + " " + lName);




        /*if (!signerReg.getSignatureImagePath().toString().equalsIgnoreCase("null")){
            Toast.makeText(context, "Collected", Toast.LENGTH_SHORT).show();
        } else {
            //holder.statusBtn.setText("Collect");
            Toast.makeText(context, "Collected", Toast.LENGTH_SHORT).show();
        }*/

        String pathSignature;
        String pathThumb;
        try
        {
            pathSignature = signerReg.getSignatureImagePath().toString();
        }
        catch (Exception e){
            pathSignature = "Null";
        }
        try
        {
            pathThumb = signerReg.getThumbImagePath().toString();
        }
        catch (Exception e){
            pathThumb = "Null";
        }
        //Log.d("THUMPRINT", signerReg.getSignatureImagePath() + " " + signerReg.getThumbImagePath()+ " "+ path);
        /*if (!path.equalsIgnoreCase("null")){
            Toast.makeText(context, "Collected", Toast.LENGTH_SHORT).show();
        } else {
            //holder.statusBtn.setText("Collect");
            Toast.makeText(context, "Collect", Toast.LENGTH_SHORT).show();
        }*/

        Log.d("THUMPRINT", pathSignature);
        try
        {
            if (!pathSignature.equalsIgnoreCase("null")){
                //Toast.makeText(context, "Collected", Toast.LENGTH_SHORT).show();
                holder.overlay_png.setVisibility(View.GONE);
                holder.statusBtn.setText("Collected");


            } else {
                //Toast.makeText(context, "Collect", Toast.LENGTH_SHORT).show();
                holder.overlay_png.setVisibility(View.VISIBLE);
                holder.statusBtn.setText("Collect");
            }
        } catch (Exception e){

        }
        try
        {
            if (!pathThumb.equalsIgnoreCase("null")){
                //Toast.makeText(context, "Collected", Toast.LENGTH_SHORT).show();
                holder.overlay_thumb.setVisibility(View.GONE);

            } else {
                //Toast.makeText(context, "Collect", Toast.LENGTH_SHORT).show();
                holder.overlay_thumb.setVisibility(View.VISIBLE);

            }
        } catch (Exception e){

        }

        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signerEmail = list.get(position).getEmail();
                String signerFirstName = list.get(position).getFirstName();
                Log.d("SIGNEREMAIL", signerEmail + " "+signerFirstName);
                //Toast.makeText(context, signerReg.getFirstName() + " " + signerReg.getLastName() , Toast.LENGTH_SHORT).show();
                loadFragment(new VEJ_NotarizeSignatureAndThumbInstructionFragment(signerEmail, signerFirstName));

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
    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment((FragmentActivity) context, VEJ_SignAndThumbActivity.REF_VIEW_CONTAINER, fragment, true);
    }
}
