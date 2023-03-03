package com.notaryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VACompleteDocsAdapter extends RecyclerView.Adapter<VACompleteDocsAdapter.MyView> {

    private List<DocumentsModel> list;
    private Context context;
    private Bitmap bitmap;
    DatabaseClient databaseClient;
    List<Integer> stampCountArray;
    private ProgressBar itemImageProgress;

    public class MyView extends RecyclerView.ViewHolder {

        private CircleImageView docImage;
        //private ImageView closeBtn;
        private TextView itemTitle, count;
        ConstraintLayout plusBox, itemImageContainer, docView;

        private MyView(View view) {
            super(view);
            docView = view.findViewById(R.id.docView);
            plusBox = view.findViewById(R.id.plusBox);
            itemTitle = view.findViewById(R.id.itemTitle);
            docImage = view.findViewById(R.id.itemImage);
            count = view.findViewById(R.id.count);
            itemImageContainer = view.findViewById(R.id.itemImageContainer);
            itemImageProgress = view.findViewById(R.id.itemImageProgress);

        }

        public void setItemTitle(String title) {
            itemTitle.setText(title);
        }


    }

    public VACompleteDocsAdapter(List<DocumentsModel> horizontalList, List<Integer> stampCountArray, Context context) {
        this.list = horizontalList;
        this.context = context;
        this.stampCountArray = stampCountArray;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_va_docs, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        DocumentsModel docmodel = list.get(position);

         //Sourav 20200920
//        if (!docmodel.getPhotoId().equalsIgnoreCase("") || !docmodel.getPhotoId().equalsIgnoreCase("null")) {
//
//            Picasso.with(context).load(docmodel.getPhotoId())
//                    .centerCrop()
//                    .resize(200,200)
//
//                    .into(holder.docImage);
//
//        } else {
            itemImageProgress.setVisibility(View.GONE);
            holder.docImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_document));
        //}

        holder.setItemTitle(docmodel.getDocName());
        holder.count.setText(String.valueOf(stampCountArray.get(position)));

        holder.docView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(context.getApplicationContext(), DocumentPdfActivity.class);
//                intent.putExtra("stampName", docmodel.getStampName());
//                context.startActivity(intent);

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



