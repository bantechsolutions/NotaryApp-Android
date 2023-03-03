package com.notaryapp.ejournal.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JournalFees;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class VEJ_JournalEntryActivity extends AppCompatActivity {

    SwitchCompat slidingBtn;
    private TextInputLayout feeAmountLay;
    private TextInputEditText feeAmount;
    private JournalFees journalFees;
    private boolean validFees;
    private String fee, notaType;
    private Button btnSubmit, btnBack;
    private DatabaseClient databaseClient;
    private Spinner notaTypeSpinner;
    private boolean validFeeAmount;
    private Boolean feeCollected;
    private Integer isCollect;
    private String savedEmail,transactionId;
    private int journalCount;
    private String[] notarizationArray;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vej_journal_entry);
        //setContentView(R.layout.activity_notarize_steps_new);

        init();

        journalFees = new JournalFees();
        try {
            journalCount = new GetJounral().execute().get();
            if (journalCount > 0){

                feeAmount.setText(journalFees.getFeeAmount());
                notaTypeSpinner.setSelection(list.indexOf(journalFees.getNotarizationType()));
                //spinnerState.setSelection(stateList.indexOf(stateName));
                feeCollected = journalFees.isCollected();
                if (feeCollected){
                    slidingBtn.setChecked(true);
                    feeAmount.setEnabled(true);
                } else {
                    slidingBtn.setChecked(false);
                    feeAmount.setEnabled(false);
                }


            }



        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        slidingBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.d("CLICK_STATUS_1", String.valueOf(isChecked));
                    //feeAmountLay.setEnabled(true);
                    feeAmount.setEnabled(true);
                } else {
                    Log.d("CLICK_STATUS_2", String.valueOf(isChecked));
                    //feeAmount.setClickable(false);
                    feeAmount.setEnabled(false);
                }
            }
        });

        TextWatcher amountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int cursorPosition = feeAmount.getSelectionEnd();
                String originalStr = feeAmount.getText().toString();

                //To restrict only two digits after decimal place
                feeAmount.setFilters(new InputFilter[]{new MoneyValueFilter(Integer.parseInt(String.valueOf(2)))});

                try {
                    feeAmount.removeTextChangedListener(this);
                    String value = feeAmount.getText().toString();

                    if (value != null && !value.equals("")) {
                        if (value.startsWith(".")) {
                            feeAmount.setText("0.");
                            feeAmount.setSelection(cursorPosition + 1);
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            feeAmount.setText("");
                        }
                        //String str = feeAmount.getText().toString().replaceAll(",", "");
                        /*if (!value.equals(""))
                            feesEditText.setText(getDecimalFormattedString(str));

                        int diff = feesEditText.getText().toString().length() - originalStr.length();
                        feesEditText.setSelection(cursorPosition + diff);*/
                    }
                    feeAmount.addTextChangedListener(this);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                    feeAmount.addTextChangedListener(this);
                }
            }
        };

        feeAmount.addTextChangedListener(amountTextWatcher);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validFeeAmount = true;
                /*if (!Validation.hasJournalFee(feeAmount, feeAmountLay)) {
                    feeAmountLay.setError(null);
                    feeAmountLay.setErrorEnabled(false);
                    feeAmountLay.setError("Please enter the fee amount.");
                    validFeeAmount = false;
                } else {
                    feeAmountLay.setError("");
                    validFeeAmount = true;
                    //Toast.makeText(JournalEntry.this, "WIN", Toast.LENGTH_SHORT).show();
                }*/

                if (slidingBtn.isChecked()){
                    String text = feeAmount.getText().toString().trim();
                    // textInputLayout.setError(null);

                    // length 0 means there is no text
                    if (text.length() == 0) {
                        //textInputLayout.setError("Please enter documentName");
                        feeAmount.setText("0.00");
                    }
                } else {
                    feeAmount.setText("0.00");
                }

                if (validFeeAmount){
                    new saveJournal().execute();
                    //Toast.makeText(VEJ_JournalEntryActivity.this, notaTypeSpinner.getSelectedItem().toString()+"\n"+slidingBtn.isChecked()+"\n"+feeAmount.getText().toString().trim() , Toast.LENGTH_SHORT).show();
                }


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                /*startActivity(new Intent(getActivity(), VEJ_NotarizeStepsActivity.class));
                getActivity().finish();*/
            }
        });

    }
    private void init(){
        databaseClient = DatabaseClient.getInstance(this);
        new SelectData().execute();

        slidingBtn = findViewById(R.id.slidingBtn);
        feeAmountLay = findViewById(R.id.feeAmountLay);
        feeAmount = findViewById(R.id.feeAmount);
        btnSubmit = findViewById(R.id.startBtn);
        btnBack = findViewById(R.id.btn_pro_back);
        notaTypeSpinner = findViewById(R.id.nota_type_Spinner);

        notarizationArray = getResources().getStringArray(R.array.notarizationType);
        list = Arrays.asList(notarizationArray);


        slidingBtn.setChecked(false);
        feeAmount.setEnabled(false);
        //feeAmount.setClickable(false);

    }
    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            transactionId = databaseClient.getAppDatabase().transactionsDao().getTransactionKey();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
        }

    }

    class saveJournal extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .deleteAll();

            fee = feeAmount.getText().toString().trim();
            feeCollected = slidingBtn.isChecked();
            if (feeCollected){
                isCollect = 1;
            } else {
                isCollect = 0;
            }
            notaType = notaTypeSpinner.getSelectedItem().toString();

            journalFees = new JournalFees();


            journalFees.setFeeAmount(fee);
            journalFees.setCollected(feeCollected);
            journalFees.setNotarizationType(notaType);
            databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .insert(journalFees);

            return "";
        }
        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            uplaodData();
            /*Intent intent = new Intent(VEJ_JournalEntryActivity.this, VEJ_NotarizeStepsActivity.class);
            startActivity(intent);
            finish();*/
        }
    }

    private void uplaodData() {
        //final String token   = "Bearer "+ PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        CustomDialog.showProgressDialog(this);
        
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,AppUrl.ADD_JOURNAL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        CustomDialog.cancelProgressDialog();
                        //getActivity().finish();
                        //loadFragment(new VEJ_ThumbprintCameraFragment(signerEmail));
                        // loadFragment(new Notarize_SignerPersonalProfileFragment());
                        Intent intent = new Intent(VEJ_JournalEntryActivity.this, VEJ_NotarizeStepsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        // CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //   CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tranid", transactionId);
                params.put("jtype",notaType);
                params.put("jamount ", fee);
                params.put("iscollected", isCollect.toString());

                Log.d("JOURNAL_DATA", params.toString());

                return params;
            }

            /*@Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                return params;
            }*/
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(this).addToRequestQueue(multipartRequest);
    }

    class GetJounral extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //adding to database
            journalFees = databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .getJournalFees();
            return databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .getCount();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    class MoneyValueFilter extends DigitsKeyListener {
        private int digits;

        public MoneyValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
        }
    }
    public static String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            String str3 = "";
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = ".";
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3 = str3 + "." + str2;
                    return str3;
                }
                if (i == 3) {
                    str3 = "," + str3;
                    i = 0;
                }
                str3 = str1.charAt(k) + str3;
                i++;
            }
        }
        return "";
    }

}