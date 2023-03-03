package com.notaryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.EmailAddedListerner;
import com.notaryapp.roomdb.DatabaseClient;

import java.util.ArrayList;

public class EmailListAdapter extends RecyclerView.Adapter<EmailListAdapter.MyView>{

    private ArrayList<String> list;
    private Context context;
    private Bitmap bitmap;
    DatabaseClient databaseClient;
    EmailAddedListerner emailAddedListerner;

    public class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;
        private ImageView delete;

        private MyView(View view) {
            super(view);

            itemTitle = view.findViewById(R.id.itemTitle);
            delete = view.findViewById(R.id.delete);

        }

    }

    public EmailListAdapter(ArrayList<String> emailList, EmailAddedListerner emailAddedListerner, Context context) {
        this.list = emailList;
        this.context = context;
        this.emailAddedListerner = emailAddedListerner;

    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_email_list, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.itemTitle.setText(list.get(position));


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailAddedListerner.onDeleteEmail(list.get(position), holder.getAdapterPosition());
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
