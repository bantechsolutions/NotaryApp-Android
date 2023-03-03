package com.notaryapp.ejournal.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.UserNote;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class VEJ_NoteActivity extends AppCompatActivity {

    private EditText headerText, subText;
    private Button submitBtn, btnBack;
    private String headerData, subData;
    private DatabaseClient databaseClient;
    private UserNote userNote;
    private String savedEmail,transactionId;
    private int noteCount=0;
    private TextView skipTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vej_note);

        init();

        userNote = new UserNote();
        try {
            noteCount = new GetUserNote().execute().get();
            if (noteCount > 0){
                headerText.setText(userNote.getNoteHeading());
                subData = userNote.getNoteDetail();
                if (subData.equalsIgnoreCase("null")){
                    subText.setVisibility(View.GONE);
                } else {
                    subText.setVisibility(View.VISIBLE);
                    subText.setText(userNote.getNoteDetail());
                }


            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }






        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerData = headerText.getText().toString().trim();
                subData = subText.getText().toString().trim();

                if(headerData.length() >0 ){
                    new saveNote().execute();
                } else {
                    //Toast.makeText(AddNote.this, "Text Field cannot empty!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(VEJ_NoteActivity.this, "Please add a Title for your notes.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        skipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void init(){

        databaseClient = DatabaseClient.getInstance(this);
        new SelectData().execute();

        headerText = findViewById(R.id.header_editText);
        subText = findViewById(R.id.sub_editText);
        submitBtn = findViewById(R.id.startBtn);
        btnBack = findViewById(R.id.btn_pro_back);
        skipTxt = findViewById(R.id.skipTxt);

    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
        }

    }
    class saveNote extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .userNoteDao()
                    .deleteAll();

            userNote = new UserNote();

            userNote.setNoteHeading(headerData);
            if (subData.length() > 0){
                userNote.setNoteDetail(subData);
            } else {
                userNote.setNoteDetail("null");
            }
            databaseClient.getAppDatabase()
                    .userNoteDao()
                    .insert(userNote);


            return "";
        }
        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            uplaodData();
            /*Intent intent = new Intent(VEJ_JournalEntryActivity.this, VEJ_NotarizeStepsActivity.class);
            startActivity(intent);
            finish();*/
        }
    }

    private void uplaodData() {
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        CustomDialog.showProgressDialog(this);
        
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,AppUrl.ADD_NOTE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        //getActivity().finish();
                        //loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
                        // loadFragment(new Notarize_SignerPersonalProfileFragment());
                        Intent intent = new Intent(VEJ_NoteActivity.this, VEJ_NotarizeStepsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //   CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tranid", transactionId);
                params.put("notehead",headerData);
                params.put("notesubhead ", subData);

                return params;
            }

            /*@Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                return params;
            }*/
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(this).addToRequestQueue(multipartRequest);
    }

    class GetUserNote extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            userNote = databaseClient.getAppDatabase()
                    .userNoteDao()
                    .getUserNote();

            return databaseClient.getAppDatabase()
                    .userNoteDao()
                    .getCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }
}