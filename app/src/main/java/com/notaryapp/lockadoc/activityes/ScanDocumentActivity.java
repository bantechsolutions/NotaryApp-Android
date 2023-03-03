package com.notaryapp.lockadoc.activityes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.lockadoc.fragments.LAD_AddDocFragmentLanding;
import com.notaryapp.lockadoc.fragments.LAD_ScanDocsFragment;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.FragmentViewUtil;

public class ScanDocumentActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private String docName = "";
    private String serverDocName = "";

    private DatabaseClient databaseClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);

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

        databaseClient = DatabaseClient.getInstance(this);
        docName = getIntent().getStringExtra("docName");
        serverDocName = getIntent().getStringExtra("serverDocName");

        if (docName != null && serverDocName != null) {
            loadFragment(new LAD_AddDocFragmentLanding(docName, serverDocName));
        } else {
            loadFragment(new LAD_ScanDocsFragment());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            completedDialog();
        } catch (Exception e) {
            completedDialog();
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(this, REF_VIEW_CONTAINER, fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("test", "test");
        LAD_AddDocFragmentLanding demoFragment = (LAD_AddDocFragmentLanding)
                getSupportFragmentManager().findFragmentById(REF_VIEW_CONTAINER);
        demoFragment.onActivityResult(requestCode, resultCode, data);
    }

    private void completedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText("You would loose all data ... ");

        Button dialogButton = dialog.findViewById(R.id.btnNo);
        dialogButton.setText("CANCEL");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("OK");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DeleteImages().execute();
                dialog.dismiss();
                new DeleteFromCloseAll().execute();

            }
        });
        dialog.show();
    }

    class DeleteFromCloseAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            startActivity(new Intent(getApplicationContext(), DashBoardActivity.class));
            finish();
        }

    }

}