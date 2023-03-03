package com.notaryapp.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.notaryapp.R;
import com.notaryapp.notaryappApplication;
import com.notaryapp.ejournal.activities.VEJ_Notarize_AlertActivity;
import com.notaryapp.lockadoc.activityes.LADStepsActivity;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.ui.activities.membership.OrderHistoryNewActivity;
import com.notaryapp.ui.activities.membership.PaymentSettingsActivity;
import com.notaryapp.ui.activities.onboarding.PrivacyPolicy;
import com.notaryapp.ui.activities.onboarding.TermsAndService;
import com.notaryapp.ui.activities.notaryapp.notaryappDashBoardActivity;
import com.notaryapp.ui.activities.notaryapp.notaryappReportActivity;
import com.notaryapp.ui.activities.unsubscribe.UnsubscribeActivity;
import com.notaryapp.ui.activities.verifyauthenticate.Notarize_AlertActivity;
import com.notaryapp.ui.activities.youtubevideo.YoutubeDashboardActivity;
import com.notaryapp.ui.fragments.dashboard.DashBoard_AddStamp;
import com.notaryapp.ui.fragments.dashboard.DashBoard_AllowAccessFragment;
import com.notaryapp.ui.fragments.dashboard.DashBoard_FAQActivity;
import com.notaryapp.ui.fragments.dashboard.DashBoard_HelpActivity;
import com.notaryapp.ui.fragments.dashboard.DashBoard_NotaryStampActivity;
import com.notaryapp.ui.fragments.dashboard.DashBoard_ProfileActivity;
import com.notaryapp.ui.activities.notaryapp.notaryappCouponActivity;
import com.notaryapp.ui.fragments.dashboard_new.DashBoard_WelcomeFragmentNew;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.rounded_imageView.RoundedImageView;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoardActivity extends BaseActivity implements Observer {

    public static final int REF_VIEW_CONTAINER = R.id.dashboard_root;
    private AppBarConfiguration mAppBarConfiguration;
    private AppBarLayout mAppBarLayout;
    private ImageView navBtn;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CircleImageView profileImg;
    private ImageView imgProfile;
    private String selfiePath, selectedPlan;
    private Bitmap bitmap;
    private TextView txt_profileName, daysLeftText, userDrawerEmail, notaryappTextView;
    private DatabaseClient databaseClient;
    private UserReg userReg;
    private Toolbar toolbar;
    ConstraintLayout planHead;
    private int daysLeft;
    private int transCount;
    private String memCategory, planName;
    private String userName, userEmail;
    private VACustomer memPlans;
    private IJsonListener iJsonListener;
    private String questions, answers;
    //private ArrayList<Faq> faqArray ;
    ConstraintLayout homeBox, verifyBox, NotaryAppBox, regBox, privacyBox, termsBox, licenseBox, profileBox, howToBox, stampBox, transBox, memBox, memStatusBox, memOrderBox, memPayBox, helpBox, faqBox, logoutBox,
            notaryappBox, tsYourCodeBox, tsSalesDashBoardBox, tsDetailReportBox, eJournalBox, unsubscribeBox;
    private Boolean memOpen = false;
    private Boolean regOpen = false;
    private Boolean tsOpen = false;
    private Animation fab_open, fab_close, rotate_fro, rotate_back;
    ImageView memPlay, regPlay, tsPlay;
    ConstraintLayout ladBox;
    private Button webProfileBtn;
    private String webLink = "";
    private ConstraintLayout logoutCLayout;

    //  private Faq faq;
    private int faqCount;
    private String proImg;
    private boolean permission = false;

    private notaryappApplication notaryappApplication;
    private int position;
    private ProgressBar img_profile_progress;

    private SpannableString spannableTnotaryapp;

    private ConstraintLayout fab;
    boolean isFabOpen=false;

    private RoundedImageView crownCircle;


    ///
    SharedPreferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dash_board);

        notaryappApplication = (notaryappApplication) getApplication();
        notaryappApplication.getObserver().addObserver(this);

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


        //Setting the days remaining in top portion.
        init();


        rotate_fro = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.rotate_fro);
        rotate_back = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.rotate_back);

        new GETDP().execute();

        /////

    /*    Fragment  fragment = new DashBoard_WelcomeFragment();
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.dashboard_root, fragment);
            ft.commit();
        }*/

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        //**
        // link to profile
        planHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(DashBoardActivity.this, DashBoard_ProfileActivity.class);
                Intent intent = new Intent(DashBoardActivity.this, MembershipActivity.class);
                //Intent intent = new Intent(DashBoardActivity.this, notaryappShareAppActivity.class);
                intent.putExtra("from", "dash");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        });

        //  Handling side drawer components

        homeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                Fragment fragment = new DashBoard_WelcomeFragmentNew();
                fragmentLoadView(fragment);
            }
        });
        howToBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                Intent intent = new Intent(DashBoardActivity.this, YoutubeDashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        profileBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                Intent intent = new Intent(DashBoardActivity.this, DashBoard_ProfileActivity.class);
                intent.putExtra("from", "dash");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        verifyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                new DeleteAllSelectId().execute();
            }
        });

        notaryappBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                startActivity(new Intent(DashBoardActivity.this, notaryappDashBoardActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/
                if (tsOpen){
                    tsPlay.startAnimation(rotate_back);
                    tsYourCodeBox.setVisibility(View.GONE);
                    tsSalesDashBoardBox.setVisibility(View.GONE);
                    tsDetailReportBox.setVisibility(View.GONE);
                    tsOpen = false;
                } else {
                    tsPlay.startAnimation(rotate_fro);
                    tsYourCodeBox.setVisibility(View.VISIBLE);
                    tsSalesDashBoardBox.setVisibility(View.VISIBLE);
                    tsDetailReportBox.setVisibility(View.VISIBLE);
                    memStatusBox.setVisibility(View.GONE);
                    memOrderBox.setVisibility(View.GONE);
                    memPayBox.setVisibility(View.GONE);
                    licenseBox.setVisibility(View.GONE);
                    stampBox.setVisibility(View.GONE);
                    if (memOpen){
                        memOpen = false;
                        memPlay.startAnimation(rotate_back);
                    }
                    if (regOpen){
                        regOpen = false;
                        regPlay.startAnimation(rotate_back);
                    }

                    tsOpen = true;
                }
            }
        });

        tsYourCodeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsPlay.startAnimation(rotate_back);
                tsOpen = false;

                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                startActivity(new Intent(DashBoardActivity.this, notaryappCouponActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        });

        tsSalesDashBoardBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsPlay.startAnimation(rotate_back);
                tsOpen = false;

                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                startActivity(new Intent(DashBoardActivity.this, notaryappDashBoardActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        tsDetailReportBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tsPlay.startAnimation(rotate_back);
                tsOpen = false;

                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                startActivity(new Intent(DashBoardActivity.this, notaryappReportActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        ladBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                new DeleteAllLAD().execute();
            }
        });
        NotaryAppBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                new DeleteAll().execute();
            }
        });
        eJournalBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                new DeleteAllVEJ().execute();
            }
        });
//        memBox.setVisibility(View.GONE);
        memBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memOpen) {
                    memPlay.startAnimation(rotate_back);
                    memStatusBox.setVisibility(View.GONE);
                    memOrderBox.setVisibility(View.GONE);
                    memPayBox.setVisibility(View.GONE);
                    licenseBox.setVisibility(View.GONE);
                    stampBox.setVisibility(View.GONE);
                    tsYourCodeBox.setVisibility(View.GONE);
                    tsSalesDashBoardBox.setVisibility(View.GONE);
                    tsDetailReportBox.setVisibility(View.GONE);
                    memOpen = false;
                } else {
                    memPlay.startAnimation(rotate_fro);
                    memStatusBox.setVisibility(View.VISIBLE);
                    memOrderBox.setVisibility(View.VISIBLE);
                    memPayBox.setVisibility(View.VISIBLE);
                    licenseBox.setVisibility(View.GONE);
                    stampBox.setVisibility(View.GONE);
                    tsYourCodeBox.setVisibility(View.GONE);
                    tsSalesDashBoardBox.setVisibility(View.GONE);
                    tsDetailReportBox.setVisibility(View.GONE);
                    if (tsOpen){
                        tsOpen = false;
                        tsPlay.startAnimation(rotate_back);
                    }
                    if (regOpen){
                        regOpen = false;
                        regPlay.startAnimation(rotate_back);
                    }
                    memOpen = true;
                }
            }
        });
        regBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regOpen) {
                    regPlay.startAnimation(rotate_back);
                    licenseBox.setVisibility(View.GONE);
                    stampBox.setVisibility(View.GONE);
                    regOpen = false;
                } else {
                    regPlay.startAnimation(rotate_fro);
                    licenseBox.setVisibility(View.VISIBLE);
                    stampBox.setVisibility(View.VISIBLE);
                    memStatusBox.setVisibility(View.GONE);
                    memOrderBox.setVisibility(View.GONE);
                    memPayBox.setVisibility(View.GONE);
                    tsYourCodeBox.setVisibility(View.GONE);
                    tsSalesDashBoardBox.setVisibility(View.GONE);
                    tsDetailReportBox.setVisibility(View.GONE);

                    if (tsOpen){
                        tsOpen = false;
                        tsPlay.startAnimation(rotate_back);
                    }
                    if (memOpen){
                        memOpen = false;
                        memPlay.startAnimation(rotate_back);
                    }

                    regOpen = true;
                }
            }
        });
        stampBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regPlay.startAnimation(rotate_back);
                regOpen = false;

                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                drawer.closeDrawers();

                startActivity(new Intent(DashBoardActivity.this, DashBoard_NotaryStampActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        licenseBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regPlay.startAnimation(rotate_back);
                regOpen = false;

                licenseBox.setVisibility(View.GONE);
                stampBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);

                drawer.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), LicenseListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
    /*    transBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                Fragment fragment = new TransactionListFragment();
                fragmentLoadView(fragment);
            }
        });*/

        memStatusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memPlay.startAnimation(rotate_back);
                memOpen = false;

                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);

                drawer.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), MembershipActivity.class);
                intent.putExtra("from", "dash");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        memOrderBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memPlay.startAnimation(rotate_back);
                memOpen = false;

                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);


                drawer.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), OrderHistoryNewActivity.class);
                //intent.putExtra("from", "dash");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        memPayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memPlay.startAnimation(rotate_back);
                memOpen = false;

                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);


                drawer.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), PaymentSettingsActivity.class);
                intent.putExtra("from", "dash");
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        helpBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);

                drawer.closeDrawers();

                startActivity(new Intent(DashBoardActivity.this, DashBoard_HelpActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        faqBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);

                drawer.closeDrawers();
                startActivity(new Intent(DashBoardActivity.this, DashBoard_FAQActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        privacyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);

                drawer.closeDrawers();
                startActivity(new Intent(DashBoardActivity.this, PrivacyPolicy.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        termsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);

                drawer.closeDrawers();
                startActivity(new Intent(DashBoardActivity.this, TermsAndService.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        logoutBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                /*SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();*/
                try {
                    getEncryptedSharedPreferences().edit().clear().apply();
                } catch (GeneralSecurityException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                Fragment fragment = new DashBoard_AddStamp();
                fragmentLoadView(fragment);
                /*completedDialog();*/
            }
        });
        unsubscribeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                startActivity(new Intent(DashBoardActivity.this, UnsubscribeActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });
        if (!permission) {
            loadFragment(new DashBoard_AllowAccessFragment());
        } else {
            loadFragment(new DashBoard_WelcomeFragmentNew());
        }


    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(REF_VIEW_CONTAINER, fragment).commit();
    }


    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            permission = true;

        } else {
            // Do something, when permissions are already granted
            permission = false;
        }
    }

    private void fragmentLoadView(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.dashboard_root, fragment);
            ft.commit();
        }
        if(drawer!= null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    //    public  void setProfileImage(){
//        if(checkSelfie(this,"selfie.png")){
//            selfiePath = getSelfiePath(this, "selfie.png");
//            bitmap = BitmapFactory.decodeFile(selfiePath);
//            profileImg.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
//            imgProfile.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
//        }else if(checkSelfie(this,"selfie.png")){
//            selfiePath = getSelfiePath(this, "selfie.png");
//            bitmap = BitmapFactory.decodeFile(selfiePath);
//            profileImg.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
//            imgProfile.setImageBitmap(BitmapFactory.decodeFile(selfiePath));
//        }else{
//
//        }
//    }
    private void init() {

        position = 0;
        databaseClient = DatabaseClient.getInstance(this);
        new GetUserData().execute();

        // faqArray = new ArrayList<>();
        //new SelectCountFaq().execute();

        // selectedPlan = SignInFragment.PLANS_CATEGORY;
        toolbar = findViewById(R.id.toolBar);
        navBtn = findViewById(R.id.nav_side_menu);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(position);
        profileImg = hView.findViewById(R.id.img_profile);
        img_profile_progress  = hView.findViewById(R.id.img_profile_progress);
        txt_profileName = hView.findViewById(R.id.txt_profile);
        userDrawerEmail = hView.findViewById(R.id.userDrawerEmail);
        planHead = findViewById(R.id.planHead);

        imgProfile = findViewById(R.id.imageProfile);
        daysLeftText = findViewById(R.id.daysLeft);

        //Side menu items
        homeBox = findViewById(R.id.homeBox);
        profileBox = findViewById(R.id.profileBox);
        howToBox = findViewById(R.id.howToBox);
        verifyBox = findViewById(R.id.verifyBox);
        NotaryAppBox = findViewById(R.id.NotaryAppBox);
        eJournalBox = findViewById(R.id.eJournalBox);
        memBox = findViewById(R.id.memBox);
        memPlay = findViewById(R.id.memPlay);
        memStatusBox = findViewById(R.id.memStatusBox);
        memOrderBox = findViewById(R.id.memOrderBox);
        memPayBox = findViewById(R.id.memPayBox);
        regBox = findViewById(R.id.regBox);
        regPlay = findViewById(R.id.regPlay);
        licenseBox = findViewById(R.id.licenseBox);
        stampBox = findViewById(R.id.stampBox);
        faqBox = findViewById(R.id.faqBox);
        helpBox = findViewById(R.id.helpBox);
        privacyBox = findViewById(R.id.privacyBox);
        termsBox = findViewById(R.id.termsBox);
        logoutBox = findViewById(R.id.logoutBox);
        unsubscribeBox = findViewById(R.id.unsubscribeBox);
        notaryappBox = findViewById(R.id.notaryappBox);
        notaryappTextView = findViewById(R.id.notaryappText);
        tsPlay = findViewById(R.id.tsPlay);
        tsYourCodeBox = findViewById(R.id.tsYourCodeBox);
        tsSalesDashBoardBox = findViewById(R.id.tsSalesDashBoardBox);
        tsDetailReportBox = findViewById(R.id.tsDetailReportBox);

        webProfileBtn = (Button) findViewById(R.id.webProfileBtn);
        logoutCLayout = hView.findViewById(R.id.logoutCLayout);

        transBox = findViewById(R.id.transBox);

        ladBox = findViewById(R.id.ladBox);

        crownCircle = findViewById(R.id.crownCircle);

        spannableTnotaryapp = new SpannableString(getString(R.string.notaryapp));

        ForegroundColorSpan yellow = new ForegroundColorSpan(getColor(R.color.colorOrangeYellow));
        ForegroundColorSpan blue = new ForegroundColorSpan(getColor(R.color.user_input_color));

        spannableTnotaryapp.setSpan(yellow,0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTnotaryapp.setSpan(blue,5,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        notaryappTextView.setText(spannableTnotaryapp);

        //fab = (ConstraintLayout)findViewById(R.id.fab);

        pref = getSharedPreferences("login_details", Context.MODE_PRIVATE);
        //  new SelectNotaryName().execute();
        checkCameraPermission();

        webProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webLink.length()>0){
                    Intent intentWebLink = new Intent(Intent.ACTION_VIEW);
                    intentWebLink.setData(Uri.parse(webLink));
                    startActivity(intentWebLink);
                }
            }
        });

        logoutCLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                memStatusBox.setVisibility(View.GONE);
                memOrderBox.setVisibility(View.GONE);
                memPayBox.setVisibility(View.GONE);
                tsYourCodeBox.setVisibility(View.GONE);
                tsSalesDashBoardBox.setVisibility(View.GONE);
                tsDetailReportBox.setVisibility(View.GONE);
                drawer.closeDrawers();
                /*SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();*/
                try {
                    getEncryptedSharedPreferences().edit().clear().apply();
                } catch (GeneralSecurityException e) {
                    //e.printStackTrace();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
                Fragment fragment = new DashBoard_AddStamp();
                fragmentLoadView(fragment);


            }
        });


        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabOpen){
                    isFabOpen = true;
                } else {
                    isFabOpen = false;
                }
            }
        });*/

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        position = 0;
        outState.putInt("selectedLicenseNo", position);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.dashboard_root);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void update(Observable observable, Object o) {

        new GETDP().execute();
    }

    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {
            memPlans = databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();
            return memPlans;
        }

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);
            selectedPlan = memPlans.getCurrent_membership();
            daysLeft = memPlans.getDaysLeft();
            transCount = memPlans.getTransactionsLeft();


            // Sourav 20201124
            String daysText = memPlans.getDaysLeft() > 0
                    ? ( (transCount > 0 ? transCount : 0) + " Left")
                    : "Expired";

            //String daysText = "";
            daysLeftText.setText(daysText);
            //Log.d("DAYS_LEFT", String.valueOf(memPlans.getDaysLeft()));
            Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(memPlans.getCurrent_membership());
            if(memPlans.getDaysLeft() > 0) {

                if (selectedPlan.equalsIgnoreCase("trial")) {
                    imgProfile.setImageResource(R.drawable.ic_free_trial);
                    //Log.d()
                    //imgProfile.setImageResource(R.drawable.ic_person);
                } else {
                    if (matcher.find()){
                        imgProfile.setImageResource(R.drawable.ic_silver_crown);
                    } else {
                        imgProfile.setImageResource(R.drawable.crown_gold);
                    }
                }
            }else{
                imgProfile.setImageResource(R.drawable.ic_expire_account);
                //imgProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    class DeleteAllSelectId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .validateIdIdentityTypeDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*Intent intent = new Intent(DashBoardActivity.this, ValidateActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/
            if (daysLeft > 0 && transCount > 0) {
                Intent intent = new Intent(DashBoardActivity.this, ValidateActivity.class);
                startActivity(intent);
                //getActivity().finish();
            } else {
                Intent i = new Intent(DashBoardActivity.this, MembershipActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }
        }
    }

    class DeleteAllLAD extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .validateIdIdentityTypeDao().deleteAll();

            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();

            databaseClient.getAppDatabase().ladPartiesDao().deleteAll();
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (daysLeft > 0) {
                Intent intent = new Intent(DashBoardActivity.this, LADStepsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent i = new Intent(DashBoardActivity.this, MembershipActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);


            }

        }
    }

    class DeleteAll extends AsyncTask<Void, Void, String> {

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
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            /*Intent intent = new Intent(DashBoardActivity.this, Notarize_AlertActivity.class);
            startActivity(intent);
            //finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/

            if (daysLeft <= 0){
                Intent i = new Intent(DashBoardActivity.this, MembershipActivity.class);
                //i.putExtra("from", "dash");
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
            else if (transCount <=0 ){
                purchaseOrSkipNotification();
            } else {
                Intent intent = new Intent(DashBoardActivity.this, Notarize_AlertActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }

    }
    class DeleteAllVEJ extends AsyncTask<Void, Void, String> {

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
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            /*Intent intent = new Intent(DashBoardActivity.this, VEJ_Notarize_AlertActivity.class);
            startActivity(intent);
            //finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/
            if (daysLeft <= 0){
                Intent i = new Intent(DashBoardActivity.this, MembershipActivity.class);
                //i.putExtra("from", "dash");
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
            else if (transCount <=0 ){
                purchaseOrSkipNotification();
            } else {
                Intent intent = new Intent(DashBoardActivity.this, VEJ_Notarize_AlertActivity.class);
                startActivity(intent);
            }
        }

    }

    private void checkTransactions(){
        CustomDialog.showProgressDialog(DashBoardActivity.this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("chkTrans")) {

                            int total_bought = data.getInt("total_bought");
                            int total_used = data.getInt("total_used");

                            String plan = data.getString("current_membership");

                            int daysLeft;
                            try {
                                daysLeft = (int) Utils.dateDiff(data.getString("current_date")
                                        ,data.getString("ending_at"));
                            } catch (Exception e) {
                                daysLeft = 0;
                            }

                            Log.d("DATA_TOTAL_used", String.valueOf(daysLeft));
                            // 2020-11-21
                            //String daysText = !(daysLeft > 0) ? (((total_bought-total_used) > 0 ? (total_bought-total_used) : 0) + " Left") : "EXPIRED";
                            if(!plan.equalsIgnoreCase("trial")) {
                                if (!(daysLeft > 0)) {
                                    if (daysLeft != -1) {
                                        daysLeftText.setText((-1 * daysLeft) + " Days Left");
                                    } else {
                                        daysLeftText.setText((-1 * daysLeft) + " Day Left");
                                    }
                                } else {
                                    daysLeftText.setText("EXPIRED");
                                }
                            }else{
                                if((total_bought - total_used)>0) {
                                    daysLeftText.setText((total_bought - total_used) + " IDV Left");
                                }else{
                                    daysLeftText.setText("0 IDV Left");
                                }
                            }
                            /*if(!(daysLeft > 0)) {
                                if (plan.equalsIgnoreCase("trial")) {
                                    //imgProfile.setImageResource(R.drawable.ic_silver_crown);
                                    //imgProfile.setImageResource(R.drawable.ic_person);
                                } else {
                                    //imgProfile.setImageResource(R.drawable.ic_person);
                                }
                            }else{
                                //imgProfile.setImageResource(R.drawable.ic_expire_account);
                                //imgProfile.setImageResource(R.drawable.ic_person);
                            }*/
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(DashBoardActivity.this, Utils.errorMessage(DashBoardActivity.this));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(DashBoardActivity.this, msg);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };


        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email", userEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(DashBoardActivity.this,iJsonListener,params, AppUrl.CHK_TRAN_COUNT,"chkTrans");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if(drawer.isOpen()){
            drawer.closeDrawers();
        } else {
            completedDialog();
        }

    }

    private void completedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText("Do you want to exit?");

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
                //finish();
                //System.exit(0);
                //startActivity(new Intent(getApplicationContext(), OnboardingBaseActivity.class));
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                finishAffinity();
                finish();

            }
        });
        dialog.show();
    }


    class GETDP extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String profilePic = databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .getProfilePic();

            return profilePic;
        }

        @Override
        protected void onPostExecute(String profilePic) {
            super.onPostExecute(profilePic);
            if (profilePic != null) {

                if (!profilePic.equalsIgnoreCase("")
                        && !profilePic.equalsIgnoreCase("null")) {

                    Picasso.with(getApplicationContext()).load(profilePic).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(100, 100)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(profileImg, new Callback() {
                                @Override
                                public void onSuccess() {
                                    img_profile_progress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    profileImg.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.logo));
                                }
                            });

                    /*Picasso.with(getApplicationContext()).load(profilePic).centerCrop()
                            .noFade()
                            .resize(100, 100)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(crownCircle, new Callback() {
                                @Override
                                public void onSuccess() {
                                    //imgProfile.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    crownCircle.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_person));
                                }
                            });*/
                                        /*Picasso.with(getApplicationContext())
                            .load(profilePic)
                            .centerCrop()
                            .resize(180,180)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(profileImg);*/
                }
            }
        }
    }

    class GetUserData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String firstName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getName();
            String lastName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getLastName();

            userName = firstName + " " + lastName;

            userEmail = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            txt_profileName.setText(userName);
            userDrawerEmail.setText(userEmail);

            new SelectPlans().execute();
            checkTransactions();
            getWebProfile();

        }
    }

    private void getWebProfile() {
        CustomDialog.showProgressDialog(DashBoardActivity.this);

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("webUrlLink")) {

                            webLink = data.getString("url");

                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        // RequestQueueService.showAlert("Error! No data fetched", getActivity());
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // RequestQueueService.showAlert("Something went wrong", getActivity());
                    CustomDialog.notaryappDialogSingle(DashBoardActivity.this, Utils.errorMessage(DashBoardActivity.this));
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(DashBoardActivity.this, msg);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };


        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("email", userEmail);
            }catch (Exception e){
                //e.printStackTrace();
            }
            postapiRequest.request(DashBoardActivity.this,iJsonListener,params, AppUrl.GENERATE_PROFILE,"webUrlLink");
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    public String getUserEmail(){
        return userEmail;
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

    private void purchaseOrSkipNotification() {
        final Dialog dialog = new Dialog(DashBoardActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_purchase_skip_dialog);

        Button btnPurcahse = dialog.findViewById(R.id.btnPurchase);
        Button btnSkip = dialog.findViewById(R.id.btnSkip);
        Button btnClose = dialog.findViewById(R.id.btnClose);


        btnPurcahse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashBoardActivity.this, MembershipActivity.class);
                i.putExtra("from", "buypackage");
                startActivity(i);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                dialog.cancel();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, VEJ_Notarize_AlertActivity.class);
                startActivity(intent);
                dialog.cancel();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}