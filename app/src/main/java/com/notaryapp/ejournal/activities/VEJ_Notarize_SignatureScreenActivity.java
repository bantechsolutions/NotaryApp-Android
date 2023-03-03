package com.notaryapp.ejournal.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.ejournal.vejdrawview.DrawView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VEJ_Notarize_SignatureScreenActivity extends AppCompatActivity {

    private DrawView paint;
    private Button btnRetry, btn_done;
    private String savedImage;
    private CardView card_camera;
    private Button btnBack,btnClose;
    private String signerEmail, signerFirstName;
    private TextView SignerFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vej_signature_screen);

        init();
        buttonControls();

        signerEmail = getIntent().getStringExtra("SIGNER_EMAIL");
        signerFirstName = getIntent().getStringExtra("SIGNER_FIRSTNAME");

        SignerFirstName.setText(signerFirstName);

        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });



    }

    private void buttonControls() {
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint.allClear();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, VEJ_SignAndThumbActivity.class);
                startActivity(intent);
                finish();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, VEJ_SignAndThumbActivity.class);
                startActivity(intent);
                finish();
            }
        });

        card_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, SignAndThumbCameraPreviewActivity.class);
                intent.putExtra("fragmentType","SignatureCamera");
                startActivity(intent);
                finish();*/
                Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, VEJ_SignAndThumbCameraPreviewActivity.class);
                intent.putExtra("SIGNER_EMAIL",signerEmail);
                intent.putExtra("SIGNER_FIRSTNAME",signerFirstName);
                intent.putExtra("fragmentType","SignatureCamera");
                startActivity(intent);
                finish();

            }
        });
    }

    private void init() {
        paint = (DrawView)findViewById(R.id.drawView);
        btnRetry = (Button)findViewById(R.id.btn_retry);
        btn_done = findViewById(R.id.btn_done);
        card_camera = findViewById(R.id.card_camera);
        btnBack = findViewById(R.id.btn_pro_back);
        btnClose = findViewById(R.id.btn_pro_close);
        SignerFirstName = findViewById(R.id.signerFirstNameTxt);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    private void saveImage(){
        long millis=System.currentTimeMillis();
        savedImage = "signer_signature_"+millis+".png";

        File docpath;
        ContextWrapper cw = new ContextWrapper(this.getApplicationContext());
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Bitmap bmp=paint.save();
        docpath = new File(directory,  savedImage);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(docpath);

            int nh = (int) ( bmp.getHeight() * (512.0 / bmp.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();
        } catch (Exception e) {
//            Log.e("SAVE_IMAGE", e.getMessage(), e);
        } finally {
            try {
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        Uri imgUri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",docpath);

        Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, VEJ_SignAndThumbCameraPreviewActivity.class);
        intent.putExtra("SIGNER_EMAIL",signerEmail);
        intent.putExtra("SIGNER_FIRSTNAME",signerFirstName);
        intent.putExtra("imgUri",imgUri.toString());
        intent.putExtra("savedImage",savedImage);
        intent.putExtra("fragmentType","SignaturePreview");
        startActivity(intent);
        finish();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VEJ_Notarize_SignatureScreenActivity.this, VEJ_SignAndThumbActivity.class);
        startActivity(intent);
        finish();

    }
}