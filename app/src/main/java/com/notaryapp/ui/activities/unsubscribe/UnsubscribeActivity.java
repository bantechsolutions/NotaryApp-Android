package com.notaryapp.ui.activities.unsubscribe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.notaryapp.R;

public class UnsubscribeActivity extends AppCompatActivity {

    private Button btnUnsub, btnBack;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsubscribe);
        btnUnsub = findViewById(R.id.btnUnsub);
        btnBack = findViewById(R.id.btn_pro_back);

        btnUnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminateDialog();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void terminateDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_unsubscribe_dialog);

        /*TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText("Do you want to logout?");*/

        Button dialogButtonUnregister = (Button) dialog.findViewById(R.id.btnUnregister);
        dialogButtonUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intent = new Intent(UnsubscribeActivity.this, UnsubscribeReasonActivity.class);
                intent.putExtra("ButtonPressed",dialogButtonUnregister.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);

            }
        });
        Button dialogButtonDeactive = (Button) dialog.findViewById(R.id.btnDeactive);
        dialogButtonDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intent = new Intent(UnsubscribeActivity.this, UnsubscribeReasonActivity.class);
                intent.putExtra("ButtonPressed",dialogButtonDeactive.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            }
        });

        Button btnClose = (Button) dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}