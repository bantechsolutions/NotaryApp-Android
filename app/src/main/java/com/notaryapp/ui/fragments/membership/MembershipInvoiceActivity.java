package com.notaryapp.ui.fragments.membership;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.core.StripeError;
import com.stripe.android.model.Customer;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.notaryapp.R;
import com.notaryapp.model.MembershipPackagePlans;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.stripe.ErrorDialogHandler;
import com.notaryapp.stripe.ExampleEphemeralKeyProvider;
import com.notaryapp.stripe.ProgressDialogController;
import com.notaryapp.stripe.StripeService;
import com.notaryapp.ui.activities.membership.CongratsMemberActivity;
import com.notaryapp.ui.activities.membership.PaymentFailedActivity;
import com.notaryapp.ui.activities.membership.VerificationAddedActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;

public class MembershipInvoiceActivity extends AppCompatActivity {

    @NonNull
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private ProgressBar mProgressBar;
    private OkHttpClient httpClient = new OkHttpClient();

    private ArrayList<MembershipPackagePlans> selectedPlan = new ArrayList<>();
    private TextView transText, txtPackageCost, txtTransCost, txtNetCost, txtDisplay, cardNo, termsText, planDesc;
    private Button btnMakePayment, btnBack, btnClose;
    private MembershipPackagePlans membershipPackagePlans;
    private double cost;
    private String selectedPlanType, tranCount;
    private ConstraintLayout packageContainer;
    private String customerID;
    private UserReg userReg;
    private VACustomer vaCustomer;
    private DatabaseClient databaseClient;
    private Context context;
    private String name, email, objecttype;
    private PaymentSession paymentSession;
    private PaymentSessionData mPaymentSessionData;
    private String packageType = "", category = "", monthly_fee = "", planID= "", planName="";

    private Stripe mStripe;
    private StripeService mStripeService;
    private String mClientSecret;
    private ProgressDialogController mProgressDialogController;
    private ErrorDialogHandler mErrorDialogHandler;

    private Stripe stripe;
    private boolean isPaymentDone = false;
    private String orderId = "";
    private boolean isPaymentMethodSelected = false;
    private CheckBox checkBox;
    private Activity activity;
    private String PaymentStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_invoice);

        context = getApplicationContext();
        databaseClient = DatabaseClient.getInstance(context);

        new UserDetails().execute();
        new Getplans().execute();

        init();

        activity = this;
        mProgressDialogController = new ProgressDialogController(getSupportFragmentManager(),
                getResources());
        mErrorDialogHandler = new ErrorDialogHandler(this);

        packageType = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");
        monthly_fee = getIntent().getStringExtra("monthly_fee");
        planID = getIntent().getStringExtra("planID");
        planName = getIntent().getStringExtra("planName");

        Log.d("SUB_DATA", packageType+ " "+ category+ " "+ monthly_fee + " "+ planID );


        if (packageType == null) {
            packageType = "";
        }

        if (category == null) {
            category = "";
        }

        if (monthly_fee == null) {
            monthly_fee = "";
        }

        if (planID == null) {
            planID = "";
        }
        if (planName == null) {
            planName = "";
        }

        txtTransCost.setText("$" + monthly_fee);
        txtNetCost.setText("$" + monthly_fee);

        if (category.equalsIgnoreCase("subscription")) {

            transText.setText("MemberShip");
            String planDscText = "";
            Pattern p = Pattern.compile("\\bmonthly\\b",Pattern.CASE_INSENSITIVE);
            Matcher matcher = p.matcher(planName);
            //Notary-App® package for $3.99 per month
            if (matcher.find()){
                planDscText = planName + " for $"+ monthly_fee+" per month";
            } else {
                planDscText = planName + " for $"+ monthly_fee+" per year";
            }
            planDesc.setText(planDscText);
            txtDisplay.setVisibility(View.GONE);
            packageContainer.setVisibility(View.GONE);

        } else {

            //String planName = getIntent().getStringExtra("planName");

            transText.setText("Package");
            planDesc.setText(planName);
            txtDisplay.setVisibility(View.GONE);
            packageContainer.setVisibility(View.GONE);

        }

        mProgressBar.setVisibility(View.VISIBLE);
        CustomDialog.showProgressDialog(activity);

        cardNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchWithCustomer();
            }
        });

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), Disclaimer.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        });

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPaymentMethodSelected) {

                    if(checkBox.isChecked()) {
                        createOrder();
                    }
                    else{

                        CustomDialog.notaryappDialogSingle(activity, "Please accept terms & condition.");
                    }
                }
                else{
                    CustomDialog.notaryappDialogSingle(activity, "Please select a Payment Method");
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void createOrder() {

        CustomDialog.showProgressDialog(MembershipInvoiceActivity.this);
        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("create-order")) {

                            orderId = String.valueOf(data.getInt("id"));


                            if (category.equalsIgnoreCase("subscription")) {

                                createSubscription();
                            } else {

                                createPackage();
                            }
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.cancelProgressDialog();
            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("email", email);
                params.put("objecttype", category);
                params.put("plancode", packageType);
                params.put("status", "pending");

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_ORDER, "create-order");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void createSubscription() {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        Log.d("SUB_DATA", data.toString());
                        if (type.equals("create-subscription-with-order")) {

                            new UpdateSubscription().execute(data.getString("id"), data.getString("status"));
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.cancelProgressDialog();
            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {


                String customerId = mPaymentSessionData.getPaymentMethod().customerId;
                String paymentMethodId = mPaymentSessionData.getPaymentMethod().id;

                params.put("customerId", customerId);
                //params.put("plan", "price_1Lm9kqC0Ia2OaUoHlDERXglx"); // plan_HFs05NqtfuKl3v
                params.put("plan", planID);
                params.put("paymentMethodId", paymentMethodId);
                params.put("order_id", orderId);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_SUB_WITH_ORDER, "create-subscription-with-order");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void createPackage() {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("create-payment-intent-with-order")) {

                            confirmPayment(data.getString("intentId"));
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.cancelProgressDialog();
            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {


                String customerId = mPaymentSessionData.getPaymentMethod().customerId;
                String paymentMethodId = mPaymentSessionData.getPaymentMethod().id;

                double dollarAmount = Double.valueOf(monthly_fee);
                double centAmount = dollarAmount * 100.00;
                int centAmountAsInt = (int) centAmount;

                params.put("cust_id", customerId);
                params.put("amount", String.valueOf(centAmountAsInt));
                params.put("currency", "usd");
                params.put("order_id", orderId);
                //params.put("paymentMethodId", paymentMethodId);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_PAYMENT_INTENT_WITH_ORDER, "create-payment-intent-with-order");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void confirmPayment(String intentId) {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        Log.d("PACKAGE_DATA", data.toString());
                        if (type.equals("confirm-payment-intent")) {

                            CustomDialog.cancelProgressDialog();
                            //finish();

                            if (data.getString("status").equalsIgnoreCase("succeeded")){
                                Intent intent = new Intent(getApplicationContext(), VerificationAddedActivity.class);
                                intent.putExtra("type", packageType);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                finish();
                            } else {
                                Intent intent = new Intent(getApplicationContext(), PaymentFailedActivity.class);
                                //intent.putExtra("type", packageType);
                                intent.putExtra("monthly_fee",monthly_fee);
                                intent.putExtra("type",packageType);
                                intent.putExtra("category",category);
                                intent.putExtra("planName",planName);
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                finish();
                            }


                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

                CustomDialog.cancelProgressDialog();
            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {

                String paymentMethodId = mPaymentSessionData.getPaymentMethod().id;

                params.put("intentId", intentId);
                params.put("paymentMethodId", paymentMethodId);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.CONFIRM_PAYMENT_INTENT, "confirm-payment-intent");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void launchWithCustomer() {
        // SDK UPDATE STRIPE
        new PaymentMethodsActivityStarter(this).startForResult(new PaymentMethodsActivityStarter.Args.Builder()
                .setShouldShowGooglePay(false)
                .build());
    }

    private void init() {

        mProgressBar = findViewById(R.id.customer_progress_bar);

        transText = findViewById(R.id.transText);
        packageContainer = findViewById(R.id.packageContainer);
        btnClose = findViewById(R.id.btn_plan_close);
        btnBack = findViewById(R.id.btn_plan_back);
        txtPackageCost = findViewById(R.id.costText);
        txtTransCost = findViewById(R.id.pCostText);
        txtNetCost = findViewById(R.id.netCostText);
        btnMakePayment = findViewById(R.id.btnMakePayment);
        txtDisplay = findViewById(R.id.planDesc2);
        planDesc = findViewById(R.id.planDesc);

        cardNo = findViewById(R.id.cardNo);
        checkBox = findViewById(R.id.checkBox);
        termsText = findViewById(R.id.termsText);
    }

    private void configCustomerContext() {

        final CustomerSession customerSession = setupCustomerSession();
        setupPaymentSession(customerSession);

    }

    @NonNull
    private CustomerSession setupCustomerSession() {

        CustomerSession.initCustomerSession(this,
                new ExampleEphemeralKeyProvider(new ProgressListenerImpl(this), customerID));
        final CustomerSession customerSession = CustomerSession.getInstance();
        customerSession.retrieveCurrentCustomer(
                new InitialCustomerRetrievalListener(this));
        return customerSession;
    }

    private void createCustomerForPayment() {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {

                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("create-customer")) {

                            customerID = data.getString("id");
                            configCustomerContext();
                            new Updateplans().execute();
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {

            }

            @Override
            public void onFetchStart() {

            }
        };

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("name", name);
                params.put("email", email);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(this, iJsonListener, params, AppUrl.CREATE_CUSTOMER, "create-customer");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void setupPaymentSession(@NonNull CustomerSession customerSession) {

        PaymentSessionConfig paymentSessionConfig = new PaymentSessionConfig.Builder()
                .setShippingMethodsRequired(false)
                .setShippingInfoRequired(false)
                .build();

        paymentSession = new PaymentSession((ComponentActivity) this, paymentSessionConfig);

        paymentSession.init(new PaymentSession.PaymentSessionListener() {
            @Override
            public void onCommunicatingStateChanged(boolean b) {

            }

            @Override
            public void onError(int i, @NotNull String s) {

            }

            @Override
            public void onPaymentSessionDataChanged(@NotNull PaymentSessionData paymentSessionData) {

                {
                    final PaymentMethod paymentMethod = paymentSessionData.getPaymentMethod();

                    if (paymentMethod != null) {
                        final PaymentMethod.Card card = paymentMethod.card;

                        if (card != null) {

                            mPaymentSessionData = paymentSessionData;
                            cardNo.setText(card.brand + " " + card.last4);
                            isPaymentMethodSelected = true;
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }

    }

    private static final class ProgressListenerImpl
            implements ExampleEphemeralKeyProvider.ProgressListener {

        @NonNull
        private final WeakReference<MembershipInvoiceActivity> mActivityRef;

        private ProgressListenerImpl(@NonNull MembershipInvoiceActivity activity) {
            this.mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void onStringResponse(@NonNull String response) {
            final MembershipInvoiceActivity activity = mActivityRef.get();
            if (activity != null && response.startsWith("Error: ")) {
//                activity.mErrorDialogHandler.show(response);
            }
        }
    }

    private final class InitialCustomerRetrievalListener
            implements CustomerSession.CustomerRetrievalListener {
        private InitialCustomerRetrievalListener(@NonNull MembershipInvoiceActivity activity) {
            super();
        }

        /*@Override
        public void onCustomerRetrieved(@NotNull Customer customer) {

        }

        @Override
        public void onError(int i, @NotNull String s, @Nullable StripeError stripeError) {

        }*/

        @Override
        public void onCustomerRetrieved(@NonNull Customer customer) {
            final MembershipInvoiceActivity activity = MembershipInvoiceActivity.this;
            if (activity == null) {
                return;
            }

            activity.mProgressBar.setVisibility(View.GONE);
            CustomDialog.cancelProgressDialog();
        }


        //@Override
        public void onError(int httpCode, @NonNull String errorMessage,
                            @Nullable StripeError stripeError) {
            final MembershipInvoiceActivity activity = MembershipInvoiceActivity.this;
            if (activity == null) {
                return;
            }

            activity.mProgressBar.setVisibility(View.GONE);
            CustomDialog.cancelProgressDialog();
        }
    }


    class UserDetails extends AsyncTask<Void, Void, UserReg> {

        @Override
        protected UserReg doInBackground(Void... voids) {
            userReg = databaseClient.getAppDatabase().userRegDao().getUser();
            //  String email = userReg.getEmail();
            return userReg;
        }

        @Override
        protected void onPostExecute(UserReg userReg) {
            super.onPostExecute(userReg);

            name = userReg.getFirstName() + userReg.getLastName();
            email = userReg.getEmail();

        }
    }

    class Getplans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {

            vaCustomer = databaseClient.getAppDatabase().vaCustomerDao().getCustomer();

            return vaCustomer;
        }

        @Override
        protected void onPostExecute(VACustomer vaCustomer) {
            super.onPostExecute(vaCustomer);

            customerID = vaCustomer.getStripe_customer_id();

            if (customerID.equalsIgnoreCase("null") || customerID.equalsIgnoreCase("")) {
                createCustomerForPayment();
            } else {
                configCustomerContext();
            }

        }
    }

    class Updateplans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {

            databaseClient.getAppDatabase().vaCustomerDao().updateCustomerId(customerID);

            return vaCustomer;
        }

        @Override
        protected void onPostExecute(VACustomer vaCustomer) {
            super.onPostExecute(vaCustomer);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }

    class UpdateSubscription extends AsyncTask<String, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(String... params) {

            String subscriptionid = params[0];
            PaymentStatus = params[1];
            databaseClient.getAppDatabase().vaCustomerDao().updateSubscriptionId(subscriptionid);

            return vaCustomer;
        }

        @Override
        protected void onPostExecute(VACustomer vaCustomer) {
            super.onPostExecute(vaCustomer);

            CustomDialog.cancelProgressDialog();

            if (PaymentStatus.equalsIgnoreCase("active")){
                Intent in = new Intent(context, CongratsMemberActivity.class);
                in.putExtra("planName",planName);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
                //startActivity(new Intent(getApplicationContext(), CongratsMemberActivity.class));
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                finish();
            } else {
                Intent intent = new Intent(context, PaymentFailedActivity.class);
                intent.putExtra("monthly_fee",monthly_fee);
                intent.putExtra("type",packageType);
                intent.putExtra("planName",planName);
                intent.putExtra("category",category);
                intent.putExtra("planID",planID);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                finish();
            }


        }
    }
}
