package com.notaryapp.ui.fragments.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.notaryapp.R;

public class DashBoard_HelpActivity extends AppCompatActivity {

    private TextView phoneText, helpMail;
    private Activity activity;
    private Button back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard_help);
        activity = this;

        init();
        buttonControls();
    }

    private void buttonControls() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getActivity(), "phone clicked", Toast.LENGTH_SHORT).show();
                Uri u = Uri.parse("tel:" + phoneText.getText().toString());

                // Create the intent and set the data for the
                // intent as the phone number.
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try
                {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(i);
                }
                catch (Exception s)
                {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(activity, s.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        helpMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","email@email.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "notaryapp Support");
                intent.putExtra(Intent.EXTRA_TEXT, "Requesting support from notaryapp Team");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }
    private void init(){
        phoneText = findViewById(R.id.helpPhone1);
        helpMail = findViewById(R.id.helpMail);
        back = findViewById(R.id.btn_pro_back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
