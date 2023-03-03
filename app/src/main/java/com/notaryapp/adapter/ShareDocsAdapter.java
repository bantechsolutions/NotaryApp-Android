package com.notaryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.DocumentSelectedListerner;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;

import java.util.List;

public class ShareDocsAdapter extends RecyclerView.Adapter<ShareDocsAdapter.MyView>{

    private final List<Boolean> isSelectedDocs;
    private List<DocumentsModel> list;
    private Context context;
    private Bitmap bitmap;
    DatabaseClient databaseClient;
    DocumentSelectedListerner documentSelectedListerner;
    List<Integer> stampCountArray;

    public class MyView extends RecyclerView.ViewHolder {

        private ImageView docImage,closeBtn;
        private TextView itemTitle, docCount;
        private ImageView itemImage, selected;
        private ConstraintLayout itemView;

        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);
            itemImage = view.findViewById(R.id.itemImage);
            itemView = view.findViewById(R.id.itemView);
            selected = view.findViewById(R.id.selected);
            docCount = view.findViewById(R.id.docCount);

        }

        public void setItemTitle(String title){
            itemTitle.setText(title);
        }

    }

    public ShareDocsAdapter(List<DocumentsModel> horizontalList, DocumentSelectedListerner documentSelectedListerner, List<Boolean> isSelectedDocs, List<Integer> stampCountArray, Context context) {
        this.list = horizontalList;
        this.context = context;
        this.documentSelectedListerner = documentSelectedListerner;
        this.isSelectedDocs = isSelectedDocs;
        this.stampCountArray = stampCountArray;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_docs, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        DocumentsModel docmodel = list.get(position);
        holder.setItemTitle(docmodel.getDocName());
        final Boolean selected = isSelectedDocs.get(position);

        holder.docCount.setText(String.valueOf(stampCountArray.get(position)));
//        Picasso.with(context)
//                .load(docmodel.getPhotoId())
//                .centerCrop()
//                .resize(200,200)
//                .into(holder.itemImage);

        if(selected){

            holder.selected.setVisibility(View.VISIBLE);
        }
        else{
            holder.selected.setVisibility(View.INVISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selected) {
                    documentSelectedListerner.onSinerSelected(docmodel.getStampName(),holder.getAdapterPosition(), false);
                }
                else{
                    documentSelectedListerner.onSinerSelected(docmodel.getStampName(),holder.getAdapterPosition(), true);
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
