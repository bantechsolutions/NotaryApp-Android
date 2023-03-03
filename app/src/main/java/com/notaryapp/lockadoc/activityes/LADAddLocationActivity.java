package com.notaryapp.lockadoc.activityes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.textfield.TextInputEditText;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

public class LADAddLocationActivity extends BaseActivity {

    private Button submitBtn, btnBack;
    private ImageView imgInfo;
    private GoogleMap mMap;
    private Marker marker;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationClient;
    private TextInputEditText street, city, state, pin;
    private DatabaseClient databaseClient;
    private UserLocation userLocation;
    private String transactionId;
    private IJsonListener iJsonListener;
    private Info info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lad_activity_add_location);

        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();

            }
        }.start();
        setTimer();
        /*time out*/

        init();
        buttonControls();
    }

    private void init() {
        btnBack = findViewById(R.id.btn_pro_back);
        imgInfo = findViewById(R.id.imgInfo);
        submitBtn = findViewById(R.id.submitBtn);
        street = findViewById(R.id.street);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        pin = findViewById(R.id.pin);
        userLocation = new UserLocation();
        databaseClient = DatabaseClient.getInstance(LADAddLocationActivity.this);
        new SelectData().execute();
        new GeInfo().execute();
        initIJsonListener();
        new SaveLocation().execute();
    }

    private void buttonControls() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateLocation();
                //  finish();
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomDialog.notaryappInfoDialog(LADAddLocationActivity.this, info.getVaAddLocation());
                CustomDialog.notaryappInfoDialog(LADAddLocationActivity.this, "This step in Lock-A-Doc™ process will confirm and store the location this contract was made.");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            /*savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();*/
            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
        }
    }

    class SaveLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            userLocation = databaseClient.getAppDatabase()
                    .userLocationDao()
                    .getLocation();

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            street.setText(userLocation.getStreetName());
            city.setText(userLocation.getCityName());
            state.setText(userLocation.getStateName());
            pin.setText(userLocation.getPinCode());

        }
    }

    private void updateLocation() {
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
            try {
                String streetUser = street.getText().toString().trim();
                String cityUser = city.getText().toString().trim();
                String stateUser = state.getText().toString().trim();
                String pinUser = pin.getText().toString().trim();

                params.put("street", streetUser);
                params.put("city", cityUser);
                params.put("state", stateUser);
                params.put("zip", pinUser);
                params.put("tranid", transactionId);
                //  params.put("tranid","42e8335e-bf02-44c8-b15c-c8d1676ccfd5");
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.LOCATION_ADD, "Location");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("success")) {
                            if (type.equals("Location")) {
                                String success = data.getString("success");
                                if (success.equals("1")) {
                                    startActivity(new Intent(LADAddLocationActivity.this, LADStepsActivity.class));
                                    finish();
                                }
                            } else {

                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();

                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    // CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
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
    }
}
