package com.notaryapp.ui.activities.notaryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.notaryapp.R;
import com.notaryapp.ui.activities.notaryapp.adapter.TSViewPageAdapter;

public class notaryappReportActivity extends AppCompatActivity {

    TabLayout tsTabLayout;
    ViewPager tsViewPager;
    TSViewPageAdapter tsViewPageAdapter;

    TextView headerText;
    SpannableString spannableString;

    Context mContext;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title__report);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void init() {

        headerText = (TextView)findViewById(R.id.headerText);
        tsTabLayout = (TabLayout) findViewById(R.id.tsTabs);
        tsViewPager = (ViewPager) findViewById(R.id.tsViewPager);
        back = (Button) findViewById(R.id.btn_pro_close);
        tsViewPageAdapter = new TSViewPageAdapter(getSupportFragmentManager());
        tsViewPager.setAdapter(tsViewPageAdapter);
        tsTabLayout.setupWithViewPager(tsViewPager);

        spannableString = new SpannableString(getString(R.string.notaryapp_detailed_report));

        ForegroundColorSpan yellow = new ForegroundColorSpan(getColor(R.color.colorOrangeYellow));
        ForegroundColorSpan blue = new ForegroundColorSpan(getColor(R.color.user_input_color));

        spannableString.setSpan(yellow,0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(blue,5,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        headerText.setText(spannableString);

    }
}