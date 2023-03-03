package com.notaryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;

public class SignerLocationAdapter extends RecyclerView.Adapter<SignerLocationAdapter.MyView>{

    private String loc;
    private Context context;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView loc;


        private MyView(View view) {
            super(view);
            loc = view.findViewById(R.id.loc);

        }

    }


    public SignerLocationAdapter(String loc, Context context) {
        this.loc = loc;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_list, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.loc.setText(loc);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
