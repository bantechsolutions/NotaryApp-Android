package com.notaryapp.ui.activities.unsubscribe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.onboarding.OnboardingBaseActivity;
import com.notaryapp.ui.activities.onboarding.PrivacyPolicy;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class UnsubscribeReasonActivity extends AppCompatActivity {

    private CheckBox checkBox_1, checkBox_2, checkBox_3, checkBox_4, checkBox_5,
                    checkBox_6, checkBox_7;
    private EditText editTextReason3, editTextReason6, editTextReason7;
    private Button btnDeactivate, btnBack;

    private TextView tv_reason_1, tv_reason_2, tv_reason_3, tv_reason_4,
                    tv_reason_5, tv_reason_6, tv_reason_7;

    private String msz = "";
    private String reason ="";
    private DatabaseClient databaseClient;
    private String loginEmail;
    private IJsonListener iJsonListener;
    private boolean isChecked = true;
    private String buttonText, privacyReasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsubscribe_reason);

        buttonText = getIntent().getExtras().get("ButtonPressed").toString();
        init();
        btnDeactivate.setText(buttonText);

        privacyReasonText = "I have a privacy concern. Go through our privacy policy.";

        SpannableString ss = new SpannableString(privacyReasonText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i = new Intent(UnsubscribeReasonActivity.this, PrivacyPolicy.class);
                startActivity(i);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue));
            }
        };

        ss.setSpan(clickableSpan,41,55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_reason_5.setText(ss);
        tv_reason_5.setMovementMethod(LinkMovementMethod.getInstance());

        btnDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = true;

                if (checkBox_1.isChecked()){
                    msz = tv_reason_1.getText().toString();
                }
                else if (checkBox_2.isChecked()){
                    msz = tv_reason_2.getText().toString();
                }
                else if (checkBox_3.isChecked()){
                    msz = tv_reason_3.getText().toString();
                    reason = editTextReason3.getText().toString().trim();
                    msz = msz + "\n"+ reason;
                }
                else if (checkBox_4.isChecked()){
                    msz = tv_reason_4.getText().toString();
                }
                else if (checkBox_5.isChecked()){
                    msz = tv_reason_5.getText().toString();
                }
                else if (checkBox_6.isChecked()){
                    msz = tv_reason_6.getText().toString();
                    reason = editTextReason6.getText().toString().trim();
                    msz = msz + "\n"+ reason;
                }
                else if (checkBox_7.isChecked()){
                    msz = tv_reason_7.getText().toString();
                    reason = editTextReason7.getText().toString().trim();
                    msz = msz + "\n"+ reason;
                }
                else{
                    msz = "Please select an option.";
                    isChecked = false;
                    CustomDialog.notaryappDialogSingle(UnsubscribeReasonActivity.this, msz);
                }

                if (isChecked){
                    callRectiveApi();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }


    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox)view).isChecked();

        switch(view.getId()) {
            case R.id.checkBox_1:
                if (checked){
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason6.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_2:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason6.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_3:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason6.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(true);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_4:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason6.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_5:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_6.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason6.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_6:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_7.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason7.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(true);
                    editTextReason7.setEnabled(false);
                }
                break;
            case R.id.checkBox_7:
                if (checked){
                    checkBox_1.setChecked(false);
                    checkBox_2.setChecked(false);
                    checkBox_3.setChecked(false);
                    checkBox_4.setChecked(false);
                    checkBox_5.setChecked(false);
                    checkBox_6.setChecked(false);
                    editTextReason3.setText("");
                    editTextReason6.setText("");
                    editTextReason3.setEnabled(false);
                    editTextReason6.setEnabled(false);
                    editTextReason7.setEnabled(true);
                }
                break;
        }
    }

    private void callRectiveApi(){
        CustomDialog.showProgressDialog(this);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("username", loginEmail);
                params.put("reason", msz);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(UnsubscribeReasonActivity.this, iJsonListener, params, AppUrl.DEACTIVATE, "deactivate");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void init(){
        databaseClient = DatabaseClient.getInstance(this);
        new SelectData().execute();

        checkBox_1 = findViewById(R.id.checkBox_1);
        checkBox_2 = findViewById(R.id.checkBox_2);
        checkBox_3 = findViewById(R.id.checkBox_3);
        checkBox_4 = findViewById(R.id.checkBox_4);
        checkBox_5 = findViewById(R.id.checkBox_5);
        checkBox_6 = findViewById(R.id.checkBox_6);
        checkBox_7 = findViewById(R.id.checkBox_7);
        editTextReason3 = findViewById(R.id.editTextReason3);
        editTextReason6 = findViewById(R.id.editTextReason6);
        editTextReason7 = findViewById(R.id.editTextReason7);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        btnBack = findViewById(R.id.btn_pro_back);
        tv_reason_1 = findViewById(R.id.tv_reason_1);
        tv_reason_2 = findViewById(R.id.tv_reason_2);
        tv_reason_3 = findViewById(R.id.tv_reason_3);
        tv_reason_4 = findViewById(R.id.tv_reason_4);
        tv_reason_5 = findViewById(R.id.tv_reason_5);
        tv_reason_6 = findViewById(R.id.tv_reason_6);
        tv_reason_7 = findViewById(R.id.tv_reason_7);

        initIJsonListener();
    }

    class SelectData extends AsyncTask<String, Void, Void>

    {

        @Override
        protected Void doInBackground(String... voids) {
            loginEmail =  databaseClient.getAppDatabase()
                .userRegDao()
                .getEmail();
            return null;
        }

        @Override
            protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
        }

    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("deactivate")){
                            if (data.has("success")){
                                String success = data.getString("success");

                                if (success.equals("1")){
                                    try {
                                        getEncryptedSharedPreferences().edit().clear().apply();
                                    } catch (GeneralSecurityException | IOException e) {
                                        //e.printStackTrace();
                                    }
                                    terminateDialog();

                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(UnsubscribeReasonActivity.this, "Server Unavailable. Please try again later");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                CustomDialog.cancelProgressDialog();
                CustomDialog.notaryappDialogSingle(UnsubscribeReasonActivity.this, "Server Unavailable. Please try again later");
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    public SharedPreferences getEncryptedSharedPreferences() throws GeneralSecurityException, IOException {
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                "secret_shared_prefs_file",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        return sharedPreferences;
    }

    private void terminateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView textHead = (TextView) dialog.findViewById(R.id.textHead);
        textHead.setText("DEACTIVATED!");

        TextView textAlert = (TextView) dialog.findViewById(R.id.alertMess);
        textAlert.setText("Your account has been deactivated!");

        Button btnClose = (Button) dialog.findViewById(R.id.btnOk);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(UnsubscribeReasonActivity.this, OnboardingBaseActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

}