package com.notaryapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AddStamp;
import com.notaryapp.ui.activities.verifyauthenticate.AddSealActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.addseal.Notarize_SealCodeFragment;
import com.notaryapp.volley.IJsonListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class VAStampListAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> listItems;
    private int adapterType;
    ///  boolean tick;
    private int imgPosition;
    private DatabaseClient databaseClient;
    private List<AddStamp> listStamps;
    private String selectdLicense, license, userName;
    private Activity activity;
    private CircleImageView imgtick;
    private IJsonListener iJsonListener;
    private AddStamp addSelectedStamp = new AddStamp();
    private String selectedImg;
    private String update = "";

    private ProgressBar img_tick_progress;
    public VAStampListAdapter(Context context,String update, ArrayList<AddStamp> listStamps, int adapterType) {
        this.context = context;
        this.listStamps = listStamps;
        this.adapterType = adapterType;
        databaseClient = DatabaseClient.getInstance(context);
        this.update = update;
        // initIJsonListener();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.va_stamp_item, null);
        try {
            AddStamp addStamp = listStamps.get(position);
            CoordinatorLayout topLay = view.findViewById(R.id.topImg);
            // Button btnCaptureStamp =  view.findViewById(R.id.btnCaptureStamp);
            TextView txtlicenseNo = view.findViewById(R.id.licenseNo);
            imgtick = view.findViewById(R.id.img_tick);
            ImageView imgtickTop = view.findViewById(R.id.img_tick1);
            CardView card = view.findViewById(R.id.card);
            img_tick_progress = view.findViewById(R.id.img_tick_progress);

            license = addStamp.getLicense_num();
            txtlicenseNo.setText("LICENSE # " + license);
            Log.d("STAMP_IMG", addStamp.getLicense_num() + " " + addStamp.getStampImgPath());
            Picasso.with(context).load(addStamp.getStampImgPath()).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(imgtick, new Callback() {
                        @Override
                        public void onSuccess() {
                            img_tick_progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imgtick.setBackground(context.getResources().getDrawable(R.drawable.logo));
                        }
                    });
//            Picasso.with(context)
//                    .load(addStamp.getStampImgPath())
//                    .centerCrop()
//                    .resize(200,200)
//                    .into(imgtick);


            // btnCaptureStamp.setTag(position);

            //new DownloadStamp().execute(license);
            //        if(!listStamps.isEmpty()) {
//                    if (position == listStamps.size()) {
//                        btnCaptureStamp.setText("CAPTURE STAMP");
//                    } else {
//                        uri = Uri.parse(listStamps.get(position));
//                        imgtick.setImageURI(uri);
//                        btnCaptureStamp.setText("RETAKE");
//                        //Log.d("ADAPTER :", listStamps.get(position));
//                    }
//            }else{
//                btnCaptureStamp.setText("SELECT STAMP");
//            }
            container.addView(view);

       /*     btnCaptureStamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment;
                    fragment = new Notarize_SealCodeFragment(listItems.get(position));
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace( AddSealActivity.REF_VIEW_CONTAINER, fragment).addToBackStack(null).commit();

                }
            });*/

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppCompatActivity activity = (AppCompatActivity) context;

                    imgtickTop.setVisibility(View.VISIBLE);
                    topLay.setVisibility(View.INVISIBLE);
                    selectdLicense = listStamps.get(position).getLicense_num();
                    addSelectedStamp = listStamps.get(position);

                    Fragment fragment;
                    fragment = new Notarize_SealCodeFragment(selectdLicense,update,addSelectedStamp.getStampImgPath());
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(AddSealActivity.REF_VIEW_CONTAINER, fragment).addToBackStack(null).commit();

                    //    Toast.makeText(context,"card clicked "+position +"  "+listItems.get(position),Toast.LENGTH_LONG).show();
                    //  loadFragment(new Notarize_SealCodeFragment(listItems.get(position))) ;

                }
            });

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
        return listStamps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

/*
    class DownloadStamp extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... voids) {
            // callGetStampsAPI(voids[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String license) {
            super.onPostExecute(license);

        }
    }

    class UpdateLicense extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            selectdLicense = addSelectedStamp.getLicense_num();
            selectedImg = addSelectedStamp.getStampImgPath();
            databaseClient.getAppDatabase().vaLicenseDao().update(selectdLicense, selectedImg);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }
*/


    /*private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                String name, license_no, address, city, state, country, phone;
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("StampsList")) {
                            //Log.e("TAG", "test");
                            // imgPath.clear();
                            if (data.has("Stamps")) {
                                String item = null;
                                JSONArray stamp_array = data.getJSONArray("Stamps");
                                if (stamp_array.length() != 0) {
                                    for (int i = 0; i < stamp_array.length(); i++) {
                                        JSONObject json_inside = stamp_array.getJSONObject(i);
                                        item = json_inside.getString("url");
                                        //Log.i("IMGPATH ", item);
                                        Picasso.with(context)
                                                .load(item)
                                                .into(imgtick);
                                    }

                                } else {

                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();

                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    // CustomDialog.notaryappDialogSingle(getActivity(),"");
                    e.printStackTrace();
                }
            }


            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                //   CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }*/
}