package com.notaryapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.roomdb.entity.AddStamp;
import com.notaryapp.ui.activities.verifyauthenticate.AddSealActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.addseal.Notarize_SealCodeFragment;

import java.util.List;

public class VAStampsAdapter extends RecyclerView.Adapter<VAStampsAdapter.MyView>{

    private List<AddStamp> list;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView licenseNo;
        private ImageView stampImage;
        private CardView box;


        private MyView(View view) {
            super(view);
            licenseNo = view.findViewById(R.id.licenseNo);
            stampImage = view.findViewById(R.id.stampImage);
            box = view.findViewById(R.id.box);
        }

        void setItemTitle(String title) {
            licenseNo.setText(title);
        }
    }

    public VAStampsAdapter(List<AddStamp> horizontalList) {
        this.list = horizontalList;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_va_stamps, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final AddStamp stampModel = list.get(position);
        holder.setItemTitle(stampModel.getLicense_num());

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment fragment;
                    fragment = new Notarize_SealCodeFragment(stampModel.getLicense_num(),stampModel.getStampImgPath());
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace( AddSealActivity.REF_VIEW_CONTAINER, fragment).addToBackStack(null).commit();
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
