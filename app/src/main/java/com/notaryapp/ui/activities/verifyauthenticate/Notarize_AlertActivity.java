package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;


public class Notarize_AlertActivity extends AppCompatActivity {
    //private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private View aletView;
    private Button btnOk;
    private Button btnBack,btnClose;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_notarize_alert);
        init();

        //Button click for credible witness.r
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new Notarize_VerifyWitnessFragment());
              //  loadFragment(new Notarize_SignerWitnessCameraFragment());

                    Intent intent = new Intent(Notarize_AlertActivity.this, NotarizeStepsActivity.class);
                    startActivity(intent);
                    finish();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

    }

    private void init() {
        btnBack = findViewById(R.id.btn_pro_back);
        btnClose = findViewById(R.id.btn_pro_close);
        btnOk = findViewById(R.id.btnOk);

    }


//    private void loadFragment(Fragment fragment) {
//        FragmentViewUtil.loadFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER,fragment);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent intent = new Intent(Notarize_AlertActivity.this, DashBoardActivity.class);
        //startActivity(intent);
        //finish();


        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

    }
}
