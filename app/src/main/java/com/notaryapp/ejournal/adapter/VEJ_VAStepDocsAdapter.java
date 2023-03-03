package com.notaryapp.ejournal.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_AddDocsActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.utils.AppUrl;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VEJ_VAStepDocsAdapter extends RecyclerView.Adapter<VEJ_VAStepDocsAdapter.MyView>{

    private List<DocumentsModel> list;
    private Context context;
    private Bitmap bitmap;
    DatabaseClient databaseClient;
    List<Integer> stampCountArray;

    public class MyView extends RecyclerView.ViewHolder {

        private CircleImageView docImage;
        //private ImageView closeBtn;
        private TextView itemTitle,count;
        ConstraintLayout plusBox,itemImageContainer;
        private ProgressBar itemImageProgress;

        private MyView(View view) {
            super(view);
            plusBox = view.findViewById(R.id.plusBox);
            //closeBtn = view.findViewById(R.id.closeBtn);
            itemTitle = view.findViewById(R.id.itemTitle);
            docImage = view.findViewById(R.id.itemImage);
            count = view.findViewById(R.id.count);
            itemImageContainer = view.findViewById(R.id.itemImageContainer);
            itemImageProgress = view.findViewById(R.id.itemImageProgress);

        }

        public void setItemTitle(String title){
            itemTitle.setText(title);
        }

        public void setDocImage(String imgPath){
            docImage.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        }

    }

    public VEJ_VAStepDocsAdapter(List<DocumentsModel> horizontalList, List<Integer> stampCountArray, Context context) {
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

        if (position==0){
            holder.setItemTitle("Add Document");
            holder.plusBox.setVisibility(View.VISIBLE);
            holder.docImage.setVisibility(View.INVISIBLE);
            holder.itemImageContainer.setVisibility(View.INVISIBLE);
            holder.count.setVisibility(View.INVISIBLE);
        }
        else{

            DocumentsModel docmodel = list.get(position-1);
            String path = getSelfiePath(context, docmodel.getPhotoId());
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if(bitmap != null){
                holder.docImage.setImageBitmap(BitmapFactory.decodeFile(path));
            }
            else{

                if(!docmodel.getPhotoId().equalsIgnoreCase("") && !docmodel.getPhotoId().equalsIgnoreCase("null")) {
                    Picasso.with(context).load(AppUrl.IMAGE_LOAD+docmodel.getPhotoId()).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(200, 200)
                            .into(holder.docImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.itemImageProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    holder.docImage.setBackground(context.getResources().getDrawable(R.drawable.logo));
                                }
                            });
//                    Picasso.with(context).load(docmodel.getPhotoId())
//                            .centerCrop()
//                            .resize(200, 200)
//                            .into(holder.docImage);
                }
            }
            holder.setItemTitle(docmodel.getDocName());
            holder.count.setText(String.valueOf(stampCountArray.get(position-1)));

        }

        holder.docImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                if(position == 0) {
                    Intent intent = new Intent(activity, VEJ_AddDocsActivity.class);
                    activity.startActivity(intent);
                }
                else{
                    DocumentsModel docmodel = list.get(position-1);

                    Intent intent = new Intent(activity, VEJ_AddDocsActivity.class);
                    intent.putExtra("docName", docmodel.getDocName());
                    intent.putExtra("serverDocName", docmodel.getStampName());
                    activity.startActivity(intent);
                }

            }
        });

        holder.plusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                if(position == 0) {
                    Intent intent = new Intent(activity, VEJ_AddDocsActivity.class);
                    activity.startActivity(intent);
                }
                else{
                    DocumentsModel docmodel = list.get(position-1);

                    Intent intent = new Intent(activity, VEJ_AddDocsActivity.class);
                    intent.putExtra("docName", docmodel.getDocName());
                    intent.putExtra("serverDocName", docmodel.getStampName());
                    activity.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        
        return list.size()+1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static boolean checkSelfie(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);

        return image.exists();

    }

    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }

//    class DeleteImage extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... params) {
//            String pId = params[0];
//            databaseClient.getAppDatabase().docPicsDao().deleteRow(pId);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void docs) {
//            super.onPostExecute(docs);
//
//        }
//    }
}
