package com.notaryapp.lockadoc.adapter;

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
import com.notaryapp.interfacelisterners.ShareListerner;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.rounded_imageView.RoundedImageView;

import java.util.List;

public class LADSharePartiesAdapter extends RecyclerView.Adapter<LADSharePartiesAdapter.MyView> {

    private List<LADParties> list;
    private List<Boolean> isSelected;
    private Context context;
    private String fName = "";
    private String lName = "";
    private ShareListerner shareInterface;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private ImageView selected;
        private RoundedImageView proImage;
        private ConstraintLayout itemView;
        private ProgressBar proImgProgress;


        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);
            proImage = view.findViewById(R.id.proImage);
            itemView = view.findViewById(R.id.itemView);
            selected = view.findViewById(R.id.selected);
            proImgProgress = view.findViewById(R.id.proImgProgress);
        }

        void setItemTitle(String title) {
            itemTitle.setText(title);

        }
    }

    public LADSharePartiesAdapter(List<LADParties> horizontalList, ShareListerner shareInterface, List<Boolean> isSelected, Context context) {
        this.list = horizontalList;
        this.context = context;
        this.shareInterface = shareInterface;
        this.isSelected = isSelected;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_parties, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final LADParties stepsModel = list.get(position);
        final Boolean selected = isSelected.get(position);
        fName = stepsModel.getFirstName();
        lName = stepsModel.getLastName();

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

        if (!stepsModel.getProImagePath().equalsIgnoreCase("")
                && !stepsModel.getProImagePath().equalsIgnoreCase("null")) {
            if (stepsModel.getProImagePath().contains("https://")) {
                Picasso.with(context).load(stepsModel.getProImagePath()).centerCrop()
                        .noFade()
                        .placeholder(R.drawable.progress_animation_image_loader)
                        .resize(200, 200)
                        .into(holder.proImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.proImgProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.logo));
                            }
                        });
            } else {
                Picasso.with(context).load(AppUrl.IMAGE_LOAD+AppUrl.API_BASE_URL
                        +stepsModel.getProImagePath()).centerCrop()
                        .noFade()
                        .placeholder(R.drawable.progress_animation_image_loader)
                        .resize(200, 200)
                        .into(holder.proImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.proImgProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.proImage.setBackground(context.getResources().getDrawable(R.drawable.logo));
                            }
                        });
            }

            //            Picasso.with(context).load(stepsModel.getProImagePath())
//                    .centerCrop()
//                    .resize(200, 200)
//                    .into(holder.proImage);
        }


        if (selected) {

            holder.selected.setVisibility(View.VISIBLE);
        } else {
            holder.selected.setVisibility(View.INVISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected) {
                    shareInterface.onSinerSelected(stepsModel.getEmail(), holder.getAdapterPosition(), false);
                } else {
                    shareInterface.onSinerSelected(stepsModel.getEmail(), holder.getAdapterPosition(), true);
                }
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
}
