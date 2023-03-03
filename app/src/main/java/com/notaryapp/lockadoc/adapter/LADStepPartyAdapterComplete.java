package com.notaryapp.lockadoc.adapter;

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
import com.notaryapp.lockadoc.activityes.LADEditProfileActivity;
import com.notaryapp.lockadoc.activityes.PhotographPartyDocumentActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.utils.AppUrl;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LADStepPartyAdapterComplete extends RecyclerView.Adapter<LADStepPartyAdapterComplete.MyView>{

    private List<LADParties> list;
    private Context context;
    private Bitmap bitmap;
    DatabaseClient databaseClient;
    int stampCountArray;

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

    public LADStepPartyAdapterComplete(List<LADParties> horizontalList,
                                       int stampCountArray,
                                       Context context) {
        this.list = horizontalList;
        this.context = context;
        this.stampCountArray = stampCountArray;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lad_docs, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
           LADParties docmodel = list.get(position);
            String path = getSelfiePath(context, docmodel.getProImagePath());
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if(bitmap != null){
                holder.docImage.setImageBitmap(BitmapFactory.decodeFile(path));
            }
            else{

                if(!docmodel.getProImagePath().equalsIgnoreCase("") &&
                        !docmodel.getProImagePath().equalsIgnoreCase("null")) {
                    Picasso.with(context).load(AppUrl.IMAGE_LOAD+docmodel.getProImagePath()).centerCrop()
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
            holder.setItemTitle(docmodel.getFirstName());
            holder.count.setText(String.valueOf(stampCountArray));



        holder.docImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
               // if(position == 0) {
               //     Intent intent = new Intent(activity, PhotographPartyDocumentActivity.class);
               //     activity.startActivity(intent);
              //  } else{
                try {
                    LADParties docmodel = list.get(position);
                    Intent intent = new Intent(activity, LADEditProfileActivity.class);
                    intent.putExtra("EMAIL", docmodel.getEmail());
                    //Same activity is used for signer and witness profile update. So we check usertype to know which one to update.
                    intent.putExtra("USERTYPE", docmodel.getSignerType());
                    //  intent.putExtra("USERTYPE", "signer");
                    activity.startActivity(intent);
                }catch (Exception e){

                }
               // }
            }
        });

        holder.plusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                if(position == 0) {
                    Intent intent = new Intent(activity, PhotographPartyDocumentActivity.class);
                    activity.startActivity(intent);
                }
                else{
                    LADParties docmodel = list.get(position-1);

                    Intent intent = new Intent(activity, PhotographPartyDocumentActivity.class);
                    intent.putExtra("docName", docmodel.getFirstName());
                    intent.putExtra("serverDocName", docmodel.getLastName());
                    activity.startActivity(intent);
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
}
