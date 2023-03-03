package com.notaryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.rounded_imageView.RoundedImageView;

import java.util.List;

public class SignerProfileAdapter extends RecyclerView.Adapter<SignerProfileAdapter.MyView> {

    private List<SignerReg> list;
    private Context context;
    private String fName = "";
    private String lName = "";
    private String sType = "";

    static class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private ImageView selected;
        private RoundedImageView proImage;
        private ConstraintLayout itemView;
        private ProgressBar homeprogress;


        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);
            proImage = view.findViewById(R.id.proImage);
            selected = view.findViewById(R.id.selected);
            selected.setVisibility(View.GONE);
            homeprogress = view.findViewById(R.id.proImgProgress);
        }

        void setItemTitle(String title) {
            itemTitle.setText(title);

        }
    }


    public SignerProfileAdapter(List<SignerReg> horizontalList, Context context) {
        this.list = horizontalList;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_signers, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final SignerReg stepsModel = list.get(position);
        fName = stepsModel.getFirstName();
        lName = stepsModel.getLastName();
        sType = stepsModel.getSignerType();

        if (!stepsModel.getProImagePath().equalsIgnoreCase("")
                && !stepsModel.getProImagePath().equalsIgnoreCase("null")) {

            Picasso.with(context).load(stepsModel.getProImagePath()).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(holder.proImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.homeprogress.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {
                            holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.logo));

                            /*holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.ic_id_card));
                            holder.homeprogress.setVisibility(View.GONE);*/
                        }
                    });


//            Picasso.with(context).load(stepsModel.getProImagePath())
//                    .centerCrop()
//                    .resize(200, 200)
//                    .into(holder.proImage);
        } else {

            if (sType.equals("LAD"))
            {
                holder.proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lock_a_doc_));
                holder.homeprogress.setVisibility(View.GONE);
            } else {
                //holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.ic_id_card));
                holder.proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_NotaryApp_));
                holder.homeprogress.setVisibility(View.GONE);
            }
            /*//holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.ic_id_card));
            holder.proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_idcard_));
            holder.homeprogress.setVisibility(View.GONE);*/



        }

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


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
