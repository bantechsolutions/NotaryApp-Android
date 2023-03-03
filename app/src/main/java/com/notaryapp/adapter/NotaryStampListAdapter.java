package com.notaryapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AddLicense;

import java.util.ArrayList;
import java.util.List;


public class NotaryStampListAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<AddLicense> listItems;
    private int adapterType;
    private int imgPosition;
    private DatabaseClient databaseClient;
    private List<String> listStamps;


    public NotaryStampListAdapter(Context context, ArrayList<AddLicense> listItems, int adapterType, int imgPosition) {
        this.context = context;
        this.listItems = listItems;
        this.adapterType = adapterType;
        this.imgPosition = imgPosition;
        databaseClient = DatabaseClient.getInstance(context);
        new GetStamps().execute();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.stamp_preview_item, null);
        try {
            Uri uri = null;
            Button btnCaptureStamp = (Button) view.findViewById(R.id.btnCaptureStamp);
            TextView licenseNo = view.findViewById(R.id.licenseNo);
            ImageView imgtick = view.findViewById(R.id.img_tick);
            licenseNo.setText("LICENSE # " + listItems.get(position).getLicenseNum()+
                    "\n"+listItems.get(position).getLicenseState());
            btnCaptureStamp.setTag(position);
         /*   Picasso.with(context)
                    .load("")
                    .into(imgtick);*/
            if (!listStamps.isEmpty()) {
                if (position == listStamps.size()) {
                    btnCaptureStamp.setText("CAPTURE SEAL");
                } else {
                    uri = Uri.parse(listStamps.get(position));
                    imgtick.setImageURI(uri);
                    btnCaptureStamp.setText("RETAKE");
//                    //Log.d("ADAPTER :", listStamps.get(position));
                }
            } else {
                btnCaptureStamp.setText("CAPTURE SEAL");
            }
            container.addView(view);

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    class GetStamps extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            int count = databaseClient.getAppDatabase().addStampDao().getStampCount();

            if (count == 0) {
                listStamps = new ArrayList<>();
            } else {
                listStamps = databaseClient.getAppDatabase()
                        .addStampDao()
                        .getStampPaths(listItems.get(0).getLicenseNum(),listItems.get(0).getLicenseState());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}