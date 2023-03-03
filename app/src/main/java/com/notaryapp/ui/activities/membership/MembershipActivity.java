package com.notaryapp.ui.activities.membership;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.ui.fragments.membership.FreeTrial_ExpiredFragment;
import com.notaryapp.ui.fragments.membership.Membership_ExpiredFragment;
import com.notaryapp.ui.fragments.membership.Membership_FreeTrialFragment;
import com.notaryapp.ui.fragments.membership.Membership_PackageFragment;
import com.notaryapp.ui.fragments.membership.Membership_PremiumPackageFragment;
import com.notaryapp.ui.fragments.membership.Membership_StatusFragment;
import com.notaryapp.ui.fragments.membership.Package_ExpiredFragment;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;

public class MembershipActivity extends BaseActivity {

    public static final int REF_VIEW_CONTAINER = R.id.membershipActivityViewContainer;
    private VACustomer vaCustomer;
    private String from = "";
    private DatabaseClient databaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_membership);

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

        from = getIntent().getStringExtra("from");
        if (from != null) {
            if (from.equalsIgnoreCase("dash")) {

                loadFragment(new Membership_StatusFragment());

            }
            else if (from.equalsIgnoreCase("settings")) {

                loadFragment(new Membership_PackageFragment("settings"));
            }
            else if (from.equalsIgnoreCase("buypackage")){
                loadFragment(new Membership_PremiumPackageFragment());
            }
        } else {
            new GetPlan().execute();
        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(REF_VIEW_CONTAINER, fragment).commit();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MembershipActivity.this, DashBoardActivity.class));
        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }*/

    class GetPlan extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {

            vaCustomer = databaseClient.getAppDatabase().vaCustomerDao().getCustomer();

            return vaCustomer;
        }

        @Override
        protected void onPostExecute(VACustomer vaCustomer) {
            super.onPostExecute(vaCustomer);

            /*if (vaCustomer.getDaysLeft() > 0) {
                if (vaCustomer.getCurrent_membership().equalsIgnoreCase("trial")) {
                    loadFragment(new Membership_FreeTrialFragment(from));
                }
            }*/

            if (vaCustomer.getCurrent_membership().equalsIgnoreCase("trial")) {
                if (vaCustomer.getDaysLeft() > 0) {
                    loadFragment(new Membership_FreeTrialFragment(from));
                }
                /*else if (vaCustomer.getTransactionsLeft() == 0){
                    // freetrial expired
                    loadFragment(new FreeTrial_ExpiredFragment(from));
                } */
                else{
                    // freetrial expired
                    loadFragment(new FreeTrial_ExpiredFragment(from));
                }
            } else {
                /*if ((vaCustomer.getDaysLeft() < 0 || vaCustomer.getDaysLeft() == 0) && vaCustomer.getTransactionsLeft() > 0) {
                    // Membership Expired
                    loadFragment(new Membership_ExpiredFragment(from));
                }
                if (vaCustomer.getTransactionsLeft() <= 0) {
                    // Package Expired
                    // loadFragment(new Membership_FreeTrialFragment(from));
                    loadFragment(new Package_ExpiredFragment(from));
                }*/
                if ((vaCustomer.getDaysLeft() < 0 || vaCustomer.getDaysLeft() == 0)) {
                    // Membership Expired
                    loadFragment(new Membership_ExpiredFragment(from));
                } else {
                    // Package Expired
                    // loadFragment(new Membership_FreeTrialFragment(from));
                    loadFragment(new Package_ExpiredFragment(from));
                }
            }
        }

    }

}
