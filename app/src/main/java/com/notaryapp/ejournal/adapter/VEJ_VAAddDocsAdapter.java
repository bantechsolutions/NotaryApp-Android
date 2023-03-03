package com.notaryapp.ejournal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public class VEJ_VAAddDocsAdapter extends RecyclerView.Adapter<VEJ_VAAddDocsAdapter.MyView>{

    private List<DocumentsModel> list;
    private Context context;private
    Bitmap bitmap;
    private String transactionId;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private DocumentsModel docPicsmodel;
    private int pos;
    private Activity mActivity;

    public class MyView extends RecyclerView.ViewHolder {

        private ImageView docImage,closeBtn;
        private ProgressBar img_tick_progress;

        private MyView(View view) {
            super(view);
            docImage = view.findViewById(R.id.docImage);
            closeBtn = view.findViewById(R.id.closeBtn);
            img_tick_progress = view.findViewById(R.id.img_tick_progress);
        }

        public void setDocImage(String imgPath){
            docImage.setImageBitmap(BitmapFactory.decodeFile(imgPath));
        }

    }

    public VEJ_VAAddDocsAdapter(List<DocumentsModel> horizontalList, String transactionId, Context context, Activity activity) {
        this.list = horizontalList;
        this.context = context;
        this.transactionId = transactionId;
        databaseClient = DatabaseClient.getInstance(context);
        mActivity = activity;
        initIJsonListener();
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doc, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyView holder, final int position) {


        docPicsmodel = list.get(position);
       // docImage.setImageBitmap(BitmapFactory.decodeFile(imgPath));
       // if(position>0) {
        try {
            String path = getSelfiePath(context, docPicsmodel.getPhotoId());
            if(new File(path).exists()) {
                //Utils.loadImage(mActivity, path, holder.docImage, holder.img_tick_progress);
                holder.setDocImage(path);
                holder.img_tick_progress.setVisibility(View.GONE);
            }else{
                Utils.loadImage(mActivity, docPicsmodel.getPhotoId(), holder.docImage, holder.img_tick_progress);
            }
            //holder.setDocImage(path);
        }catch (Exception e){
            Log.v("test",e.toString());
            holder.img_tick_progress.setVisibility(View.GONE);
        }
       // }
        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pos = holder.getAdapterPosition();

                deletePhoto();

//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//                Fragment fragment;
//                new DeleteImage().execute(docPicsmodel.getPhotoId());
//                list.remove(position);
//                notifyDataSetChanged();



            }
        });
    }

    private void deletePhoto() {
        // CustomDialog.showProgressDialog(context);
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            //String url = AppUrl.DELETE_DOC + "?tranId=" + transactionId + "&fileName=" + list.get(pos).getPhotoId();
            String url;
            try{
                url = AppUrl.DELETE_DOC + "?tranId=" + transactionId + "&fileName="
                        + list.get(pos).getPhotoId().split("/")[list.get(pos).getPhotoId().split("/").length-1];

            }catch (Exception e){
                url = "";
            }
            
            getApiRequest.request(context, iJsonListener, url, "delete");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
            new DeleteImage().execute(docPicsmodel.getStampName());
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static boolean checkSelfie(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        return image.exists();
    }

    private static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        return image.getAbsolutePath();
    }

    private void initIJsonListener() {

        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("delete")) {
                            String success = data.getString("success");
                            if (success.equals("1")) {

                                list.remove(pos);
                                notifyDataSetChanged();

                            }
                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    class DeleteImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            databaseClient.getAppDatabase().documentsImageDao().delete(list.get(pos));

            return null;
        }

        @Override
        protected void onPostExecute(Void docs) {
            super.onPostExecute(docs);

        }
    }
}
