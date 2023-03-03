package com.notaryapp.ui.fragments.dashboard;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AddLicense;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.ui.activities.onboarding.ProfileBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.utils.ValidationLicense;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashBoard_AddLicenseFragment extends Fragment implements DatePicker.OnDateChangedListener {

    private final String TAG = "AddLicenseFragment";

    private CheckBox checkAddress;
    private View addLicenseView;
    private List<String> list;
    private List<String> stateCodeList;
    private List<String> stateList;
    private ImageView imgInfo1, imgInfo2;
    private Spinner spinnerLicense, spinnerState;
    private Button btnAddAnother, close;
    private TextInputEditText edit_License_Num, edit_address, edit_address1, edit_zip, edit_city,
            edit_phone, edit_company, license_ex_date;
    private TextInputLayout txtinput_licenseNum, txtInput_address1, txtInput_address2,
            txtInput_zip, txtInput_city, txtInput_company, txtInput_phoneNum, txtInput_licenseExpiryDate;
    private int licenseCount = 1;
    private String licenseNum, selectedLicenseState = "", savedEmail, selectedNotaryState = "";
    private boolean validLicense, chkLicenseInDb = false, flag = false, alreadyTaken = false;
    private AddLicense addLicense;
    private DatabaseClient databaseClient;
    private boolean validState, validZip, validPhone, validAddress, validAddress1, validCity,
            validLicenseState, validCompany, validExpiryDate;
    private IJsonListener iJsonListener;
    private Info info;
    private String appId = "", notaryStateCode, licenseStateCode,company;

    private ImageView ex_date_picker;
    private DatePickerDialog picker;
    private String mDate;
    private Date expiryDate, currentDate;
    private String licenceNumber, state_Name;

    public DashBoard_AddLicenseFragment(String licenceNumber, String state_Name) {
        this.licenceNumber=licenceNumber;
        this.state_Name=state_Name;

    }

    @Override
    public void onStart() {
        super.onStart();
        callGetStates();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addLicenseView = inflater.inflate(R.layout.fragment_dashboard_add_license_number, container, false);
        init();



        // errorMess = getResources().getText(R.string.networkerror).toString();

        list.add("Select state");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text_view, list);

        spinnerLicense.setAdapter(adapter);
        spinnerLicense.setEnabled(false);

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text_view, list);

        spinnerState.setAdapter(stateAdapter);



        edit_License_Num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                spinnerLicense.setSelection(0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  Toast.makeText(getActivity(),s.toString(),Toast.LENGTH_LONG).show();
                licenseNum = s.toString();
                int count = licenseNum.length();
                if (count > 1) {
                    flag = true;
                    spinnerLicense.setEnabled(true);

                } else {
                    spinnerLicense.setEnabled(false);
                }
            }
        });

        btnAddAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alreadyTaken) {
                    confirmLicense();
                } else {
                    CustomDialog.notaryappDialogSingle(getActivity(), "Licence is taken by another notary ");

                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });

//        edit_phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        spinnerLicense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    if (flag) {
                        validLicense = ValidationLicense.hasLicenseText(edit_License_Num, txtinput_licenseNum);
                        if (validLicense) {
                            selectedLicenseState = list.get(position);
                            licenseStateCode = stateCodeList.get(position - 1);
                            getNoteryLicenseDetailsApi(licenseNum, licenseStateCode);

                        } else {
                            //  Toast.makeText(getActivity(), "Please enter Valid License Number", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedNotaryState = list.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edit_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validAddress = ValidationLicense.hasAddressText(edit_address, txtInput_address1);
            }
        });
        edit_address1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validAddress1 = ValidationLicense.hasAddressText(edit_address, txtInput_address2);
            }
        });
        edit_company.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(edit_company, txtInput_company)) {
                    validCompany = true;
                } else {
                    validCompany = false;
                }
            }
        });
        edit_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validCity = ValidationLicense.hasCityText(edit_city, txtInput_city);
            }
        });

        edit_zip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 5) {
                    validZip = ValidationLicense.hasZipText(edit_zip, txtInput_zip);
                    txtInput_zip.setError("");
                } else {
                    txtInput_zip.setError("Enter valid zipcode");
                    validZip = false;
                }
            }
        });

        edit_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validAddress = ValidationLicense.hasAddressText(edit_address, txtInput_address1);
            }
        });
        edit_address1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validAddress1 = ValidationLicense.hasAddressText(edit_address1, txtInput_address2);
            }
        });
        edit_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validCity = ValidationLicense.hasCityText(edit_city, txtInput_city);
            }
        });
    /*    edit_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validState = ValidationLicense.hasStateText(edit_state, txtInput_state);
            }
        });*/

      /*  edit_zip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validZip = ValidationLicense.hasStateText(edit_zip, txtInput_zip);
            }
        });*/

        edit_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // validPhone = Validation.isSignerPhoneNumber(edit_phone, txtInput_phoneNum, true);
                validPhone = Validation.hasValue(edit_phone);
            }
        });
        imgInfo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(), info.getLicense());
            }
        });
        imgInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(), info.getAddress());
            }
        });
        checkAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkAddress.isChecked()) {
                    callGetProfileApi();
                } else {
                    edit_address.setText("");
                    edit_address1.setText("");
                    edit_city.setText("");
                    //   edit_state.setText("");
                    edit_zip.setText("");
                    edit_phone.setText("");
                    edit_company.setText("");

                }
            }
        });


        ex_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                Log.d("TAG", "day: "+day +" Month: "+ month +" year: "+ year);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                                String dateString = dateFormat.format(calendar.getTime());
                                //editText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                license_ex_date.setText(dateString);
                            }
                        }, year, month, day);
                picker.show();*/
                final Dialog  dtPickerDlg = new Dialog(getActivity());
                dtPickerDlg.setContentView(R.layout.date_picker);
                final DatePicker picker =(DatePicker) dtPickerDlg.findViewById(R.id.datePicker);
                Button btnOk =(Button) dtPickerDlg.findViewById(R.id.okbutton);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dtPickerDlg.dismiss();
                    }
                });
                Calendar c = Calendar.getInstance();
                int dd = c.get(Calendar.DAY_OF_MONTH);
                int mm = c.get(Calendar.MONTH);
                int yy = c.get(Calendar.YEAR);
                picker.init(yy,mm,dd,DashBoard_AddLicenseFragment.this);//last parameter is datechangelistener
                dtPickerDlg.show();
                dtPickerDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        //TextView tvMessage = (TextView)findViewById(R.id.textView);
                        license_ex_date.setText(mDate);
                    }
                });
            }
        });
        return addLicenseView;
    }

    private void init() {
        list = new ArrayList<>();
        stateCodeList = new ArrayList<>();
        stateList = new ArrayList<>();

        spinnerState = addLicenseView.findViewById(R.id.stateSpinner);
        spinnerLicense = addLicenseView.findViewById(R.id.spinnerLicense);
        btnAddAnother = addLicenseView.findViewById(R.id.btnAddAnother);
        edit_License_Num = addLicenseView.findViewById(R.id.license_no);
        edit_address = addLicenseView.findViewById(R.id.lice_address);
        edit_address1 = addLicenseView.findViewById(R.id.lice_address2);
        edit_city = addLicenseView.findViewById(R.id.lice_city);
        edit_company = addLicenseView.findViewById(R.id.lice_company);
        //  edit_state =  addLicenseView.findViewById(R.id.lice_state);
        edit_zip = addLicenseView.findViewById(R.id.lice_zip);
        edit_phone = addLicenseView.findViewById(R.id.lice_phoneNo);
        imgInfo1 = addLicenseView.findViewById(R.id.infolicense1);
        imgInfo2 = addLicenseView.findViewById(R.id.infolicense2);
        checkAddress = addLicenseView.findViewById(R.id.checkAddress);

        txtinput_licenseNum = addLicenseView.findViewById(R.id.textInput_lice_no);
        txtInput_address1 = addLicenseView.findViewById(R.id.textInput_lice_address);
        txtInput_address2 = addLicenseView.findViewById(R.id.textInput_lice_address1);
        txtInput_city = addLicenseView.findViewById(R.id.textInput_lice_city);
        txtInput_company = addLicenseView.findViewById(R.id.textInput_lice_company);
        //  txtInput_state =  addLicenseView.findViewById(R.id.textInput_lice_state);
        txtInput_zip = addLicenseView.findViewById(R.id.textInput_lice_zip);
        txtInput_phoneNum = addLicenseView.findViewById(R.id.textInput_lice_phoneNo);

        ////
        ex_date_picker = addLicenseView.findViewById(R.id.ex_date_picker);
        license_ex_date = addLicenseView.findViewById(R.id.license_ex_date);
        txtInput_licenseExpiryDate = addLicenseView.findViewById(R.id.textInput_lice_ex_date);


        close = addLicenseView.findViewById(R.id.btn_addlice_close);

        initIJsonListener();
        addLicense = new AddLicense();
        databaseClient = DatabaseClient.getInstance(getActivity().getApplicationContext());
        new SelectEmail().execute();
        new GeInfo().execute();
    }

    private void confirmLicense() {

        if (!ValidationLicense.hasExpiryDateText(license_ex_date, txtInput_licenseExpiryDate)) {
            txtInput_licenseExpiryDate.setError(null);
            txtInput_licenseExpiryDate.setErrorEnabled(false);
            txtInput_licenseExpiryDate.setError("Select expiration date");
        } else {
            if (!license_ex_date.getText().equals(" ")){
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                currentDate = new Date();

                try {
                    expiryDate =format.parse(license_ex_date.getText().toString().trim());
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                if (expiryDate.compareTo(currentDate)>0){
                    validExpiryDate = true;
                    //txtInput_licenseExpiryDate.setErrorEnabled(true);
                    txtInput_licenseExpiryDate.setError(null);
                }
                else {
                    txtInput_licenseExpiryDate.setError(null);
                    txtInput_licenseExpiryDate.setErrorEnabled(false);
                    txtInput_licenseExpiryDate.setError("Check expiration date");
                }


            }

            //validExpiryDate = true;
        }

        if (!ValidationLicense.hasAddressText(edit_address, txtInput_address1)) {
            txtInput_address1.setError(null);
            txtInput_address1.setErrorEnabled(false);
            txtInput_address1.setError("Enter Address");
        } else {
            validAddress = true;
        }
      /*  if (!ValidationLicense.hasAddressText(edit_address1, txtInput_address2)) {
            txtInput_address2.setError(null);
            txtInput_address2.setErrorEnabled(false);
            txtInput_address2.setError("Enter Address");
        } else {
            validAddress1 = true;
        }*/
        if (!ValidationLicense.hasCityText(edit_city, txtInput_city)) {
            txtInput_city.setError(null);
            txtInput_city.setErrorEnabled(false);
            txtInput_city.setError("Enter city");
        } else {
            validCity = true;
        }
      /*  if (!ValidationLicense.hasStateText(edit_state, txtInput_state)) {
            txtInput_state.setError(null);
            txtInput_state.setErrorEnabled(false);
            txtInput_state.setError("Enter State");
        } else {
            validState = true;
        }*/
        if (ValidationLicense.hasZipText(edit_zip, txtInput_zip)) {
           if(edit_zip.getText().length() == 5){
               validZip = true;
           }else {
               txtInput_zip.setError(null);
               txtInput_zip.setErrorEnabled(false);
               txtInput_zip.setError("Enter valid zip code");
           }
        } else {
            validZip = false;
        }


        if (selectedLicenseState.equals("Select state")) {
            //    Toast.makeText(getActivity(), , Toast.LENGTH_LONG).show();
            CustomDialog.notaryappDialogSingle(getActivity(), "Please select business state");
        } else {
            validLicenseState = true;
        }
        if (selectedNotaryState.equals("Select state")) {
            //    Toast.makeText(getActivity(), , Toast.LENGTH_LONG).show();
            CustomDialog.notaryappDialogSingle(getActivity(), "Please select state");
        } else {
            validState = true;
        }
        if (validLicense && validAddress && validCity && validState && validZip && validLicenseState && validExpiryDate) {
            addLicense.setAddress(edit_address.getText().toString().trim());
            addLicense.setAddressOne(edit_address1.getText().toString().trim());
            addLicense.setLicenseNum(edit_License_Num.getText().toString().trim());
            addLicense.setCity(edit_city.getText().toString().trim());
            addLicense.setLicenseState(licenseStateCode);
            addLicense.setState(selectedNotaryState);
            addLicense.setZip(edit_zip.getText().toString().trim());
           company = edit_company.getText().toString().trim();
            addLicense.setPhone(edit_phone.getText().toString().trim());
            addLicense.setExpiryDate(license_ex_date.getText().toString().trim());

            new SaveLicense().execute();

            edit_address.setText("");
            edit_address1.setText("");
            edit_city.setText("");
            spinnerState.setSelection(0);
            //edit_state.setText("");
            edit_zip.setText("");
            edit_phone.setText("");
            edit_License_Num.setText("");
            edit_company.setText("");
            license_ex_date.setText("");
            checkAddress.setChecked(false);

            if (chkLicenseInDb) {
                updateLicenseAPI();
            } else {
                insertLicenseAPI();
            }
            //Testing
            // loadFragment(new ProfileBase_AddLicenseConfirmFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), ProfileBaseActivity.REF_VIEW_CONTAINER, fragment, true);
    }

    private void callGetProfileApi() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", savedEmail);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_PROFILE, "getProfile");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getNoteryLicenseDetailsApi(String licenseNum, String stateCode) {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("license_num", licenseNum);
                params.put("state", stateCode);
//                params.put( "userName",savedEmail);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.GET_NOTARY_LICENSE_DETAILS, "NotaryDetails");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void updateLicenseAPI() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();

            try {
                params.put("address1", addLicense.getAddress());
                params.put("address2", addLicense.getAddressOne());
                params.put("city", addLicense.getCity());
                params.put("state", addLicense.getState());
                params.put("zip", addLicense.getZip());
                //params.put("notary_license", company);
                params.put("business_name", company);
                params.put("phone", addLicense.getPhone().replace(" ", "")
                        .replace("(", "")
                        .replace(")","").replace("-",""));
                params.put("license_num", addLicense.getLicenseNum());
                params.put("license_state", addLicense.getLicenseState());
                params.put("expiry_date",addLicense.getExpiryDate());
                params.put("userName", savedEmail);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            Log.d("DATA_UPDATE_License",params.toString());
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.UPDATE_NOTARY_LICENSE, "UpdateLicense");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void insertLicenseAPI() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();

            try {
                //Creating POST body in JSON format
                //to send in POST request
                params.put("address1", addLicense.getAddress());
                params.put("address2", addLicense.getAddressOne());
                params.put("city", addLicense.getCity());
                params.put("state", addLicense.getState());
                params.put("zip", addLicense.getZip());
                //params.put("notary_license", company);
                params.put("business_name", company);
                params.put("phone", addLicense.getPhone().replace(" ", "")
                        .replace("(", "")
                        .replace(")","").replace("-",""));
                params.put("license_num", addLicense.getLicenseNum());
                params.put("license_state", addLicense.getLicenseState());
                params.put("expiry_date", addLicense.getExpiryDate());
                params.put("userName", savedEmail);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            //Log.d("DATA_UPDATE_Insert",params.toString());
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.INSERT_NOTARY_LICENSE, "InsertLicense");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                String name = "", license_no = "", address = "", address1 = "", city = "", state = "", zip = "", country = "", phone = "";
                String availability = "", expiry_date="";
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("NotaryDetails")) {
                            if (data.has("success")) {

                                JSONObject success = data.getJSONObject("success");
                                JSONArray bodyData = success.getJSONArray("body");
                                if (bodyData.length() > 0) {
                                    JSONObject notery_obj = bodyData.getJSONObject(0);
                                    availability = notery_obj.getString("applicantId");
                                    if (availability.equalsIgnoreCase(appId)) {

                                        name = notery_obj.getString("full_name");
                                        license_no = notery_obj.getString("notary_license_number");
                                        address = notery_obj.getString("address_line1");
                                        address1 = notery_obj.getString("address_line2");
                                        city = notery_obj.getString("town_name");
                                        state = notery_obj.getString("state_name");
                                        country = notery_obj.getString("notary_license_state_code");
//                                        phone = notery_obj.getString("phone");
                                        expiry_date = notery_obj.getString("notary_licnese_expiry_date");

                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            expiryDate = dateFormat.parse(expiry_date);
                                            expiry_date = format.format(expiryDate);
                                        } catch (ParseException e){
                                            expiry_date = "";
                                            //e.printStackTrace();
                                        }



                                        //Formatting phone number and removing
                                        String formatPhone = notery_obj.getString("phone");
                                        int phoneLength = notery_obj.getString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")){
                                            if(formatPhone.substring(0, 3).equals("+91")){
                                                phone = formatPhone.substring(3,phoneLength);
                                            }
                                            if(formatPhone.substring(0, 2).equals("+1")){
                                                phone = formatPhone.substring(2,phoneLength);
                                            }
                                        }else{
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");


                                        zip = notery_obj.getString("postal_code");
                                        company =  notery_obj.getString("business_name");
                                        alreadyTaken = false;
                                        chkLicenseInDb = true;
                                        //edit_state.setText(state);
                                        if (address.equals("null")) {
                                            edit_address.setText("");
                                        } else {
                                            edit_address.setText(address);
                                        }
                                        if (zip.equals("null")) {
                                            edit_zip.setText("");
                                        } else {
                                            edit_zip.setText(zip);
                                        }
                                        if (address1.equals("null")) {
                                            edit_address1.setText("");
                                        } else {
                                            edit_address1.setText(address1);
                                        }
                                        edit_city.setText(city);
                                        if (phone.equals("null")) {
                                            edit_phone.setText("");
                                        } else {
                                            edit_phone.setText(phone);
                                        }
                                        if (company.equals("null")) {
                                            edit_company.setText("");
                                        } else {
                                            edit_company.setText(company);
                                        }
                                        if (expiry_date.equals("null")){
                                            license_ex_date.setText("");
                                        } else {
                                            license_ex_date.setText(expiry_date);
                                        }
                                        spinnerState.setSelection(stateList.indexOf(state));

                                    } else if (availability.equalsIgnoreCase("null")) {

                                        name = notery_obj.getString("full_name");
                                        license_no = notery_obj.getString("notary_license_number");
                                        address = notery_obj.getString("address_line1");
                                        address1 = notery_obj.getString("address_line2");
                                        city = notery_obj.getString("town_name");
                                        state = notery_obj.getString("state_name");
                                        country = notery_obj.getString("notary_license_state_code");
//                                        phone = notery_obj.getString("phone");
                                        expiry_date = notery_obj.getString("notary_licnese_expiry_date");

                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            expiryDate = dateFormat.parse(expiry_date);
                                            expiry_date = format.format(expiryDate);
                                        } catch (ParseException e){
                                            expiry_date = "";
                                            //e.printStackTrace();
                                        }

                                        //Formatting phone number and removing
                                        String formatPhone = notery_obj.getString("phone");
                                        int phoneLength = notery_obj.getString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")){
                                            if(formatPhone.substring(0, 3).equals("+91")){
                                                phone = formatPhone.substring(3,phoneLength);
                                            }
                                            if(formatPhone.substring(0, 2).equals("+1")){
                                                phone = formatPhone.substring(2,phoneLength);
                                            }
                                        }else{
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");


                                        zip = notery_obj.getString("postal_code");
                                        alreadyTaken = false;
                                        chkLicenseInDb = true;
                                        // edit_state.setText(state);
                                        if (address.equals("null")) {
                                            edit_address.setText("");
                                        } else {
                                            edit_address.setText(address);
                                        }
                                        if (zip.equals("null")) {
                                            edit_zip.setText("");
                                        } else {
                                            edit_zip.setText(zip);
                                        }
                                        if (address1.equals("null")) {
                                            edit_address1.setText("");
                                        } else {
                                            edit_address1.setText(address1);
                                        }
                                        edit_city.setText(city);
                                        if (phone.equals("null")) {
                                            edit_phone.setText("");
                                        } else {
                                            edit_phone.setText(phone);
                                        }
                                        if (expiry_date.equals("null")){
                                            license_ex_date.setText("");
                                        } else {
                                            license_ex_date.setText(expiry_date);
                                        }

                                    } else {
                                        CustomDialog.notaryappDialogSingle(getActivity(), "Licence is taken by another notary ");
                                        alreadyTaken = true;
                                        chkLicenseInDb = false;

                                        edit_address.setText(address);
                                        edit_zip.setText(zip);
                                        edit_address1.setText(address1);
                                        edit_city.setText(city);
                                        edit_phone.setText(phone);
                                        // edit_state.setText(state);
                                        edit_company.setText("");
                                        license_ex_date.setText(expiry_date);
                                    }
                                } else {
                                    chkLicenseInDb = false;
                                    alreadyTaken = false;

                                    edit_address.setText(address);
                                    edit_zip.setText(zip);
                                    edit_address1.setText(address1);
                                    edit_city.setText(city);
                                    edit_phone.setText(phone);
                                    //edit_state.setText(state);
                                    edit_company.setText("");
                                    license_ex_date.setText(expiry_date);
                                }
                            }
                        } else if (type.equals("NotaryState")) {
                            if (data.has("states")) {
                                list.clear();
                                list.add("Select state");
                                stateList.add("Select state");
                                String stateCode;
                                JSONArray states_array = data.getJSONArray("states");
                                if (states_array.length() != 0) {
                                    for (int i = 0; i < states_array.length(); i++) {
                                        JSONObject json_inside = states_array.getJSONObject(i);
                                        stateCode = json_inside.getString("state_code");
                                        state = json_inside.getString("state");
                                        list.add(state);
                                        stateList.add(state);
                                        stateCodeList.add(stateCode);
                                    }

                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text_view, list);
                                spinnerLicense.setAdapter(adapter);
                                edit_address.setError(null);
                                edit_address1.setError(null);
                                edit_city.setError(null);
                                //  edit_state.setError(null);
                                edit_zip.setError(null);
                                edit_phone.setError(null);
                                edit_License_Num.setError(null);
                                if (!(licenceNumber.equals(" ")) && !(state_Name.equals(" "))){
                                    //Toast.makeText(getContext(), licenceNumber + " " + state_Name, Toast.LENGTH_SHORT).show();
                                    edit_License_Num.setText(licenceNumber);
                                    spinnerLicense.setSelection(list.indexOf(state_Name));

                                    //getNoteryLicenseDetailsApi(licenceNumber, licenseStateCode);

                                }
                            }

                        } else if (type.equals("InsertLicense")) {
                            if (data.has("success")) {
                                String success = data.getString("success");
                                if (success.equals("1050")) {
                                    edit_License_Num.setText("");
                                    edit_address.setText("");
                                    edit_address1.setText("");
                                    edit_city.setText("");
                                    // edit_state.setText("");
                                    edit_zip.setText("");
                                    edit_phone.setText("");
                                    edit_License_Num.requestFocus();

                                    notaryappDialogSingle(getActivity(),"Licence Added");

                                } else {
                                    Toast.makeText(getActivity(), "Error in Licence Adding", Toast.LENGTH_LONG).show();
                                }
                            }

                        } else if (type.equals("UpdateLicense")) {
                            if (data.has("success")) {
                                String success = data.getString("success");
                                if (success.equals("1050")) {
                                    edit_License_Num.setText("");
                                    edit_address.setText("");
                                    edit_address1.setText("");
                                    edit_city.setText("");
                                    //   edit_state.setText("");
                                    edit_zip.setText("");
                                    edit_phone.setText("");
                                 //   ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_text_view, stateList);
                                 //   spinnerState.setAdapter(stateAdapter);
                                    /*edit_address.setError(null);
                                    edit_address1.setError(null);
                                    edit_city.setError(null);
                                    edit_state.setError(null);
                                    edit_zip.setError(null);
                                    edit_phone.setError(null);
                                    edit_License_Num.setError(null);*/
                                    edit_License_Num.requestFocus();

                                    notaryappDialogSingle(getActivity(),"Licence Updated");


                                } else {
                                    Toast.makeText(getActivity(), "Error in Licence updating", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else if (type.equals("getProfile")) {

                            JSONArray jArray = data.getJSONArray("success");
                            if (jArray.length() != 0) {
                                JSONObject json_inside = jArray.getJSONObject(0);

                                String formattedPhoneNo = "";
                                String formatPhone = json_inside.getString("phone");
                                int phoneLength = json_inside.getString("phone").length();
                                if (formatPhone.substring(0, 1).equals("+")){
                                    if(formatPhone.substring(0, 3).equals("+91")){
                                        formattedPhoneNo = formatPhone.substring(3,phoneLength);
                                    }
                                    if(formatPhone.substring(0, 2).equals("+1")){
                                        formattedPhoneNo = formatPhone.substring(2,phoneLength);
                                    }
                                }else{
                                    formattedPhoneNo = formatPhone;
                                }
                                formattedPhoneNo = formattedPhoneNo.replace(" ", "");



                                edit_phone.setText(formattedPhoneNo);
                                edit_address.setText(json_inside.getString("address1"));
                                edit_address1.setText(json_inside.getString("address2"));
                                edit_city.setText(json_inside.getString("city"));
                                // edit_state.setText(json_inside.getString("state"));
                                edit_zip.setText(json_inside.getString("zip"));
                                edit_company.setText(json_inside.getString("company"));
                                spinnerState.setSelection(stateList.indexOf(json_inside.getString("state")));
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //   CustomDialog.notaryappDialogSingle(getActivity(),"");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //  RequestQueueService.showAlert(msg,getActivity());
                CustomDialog.notaryappDialogSingle(getActivity(), Utils.errorMessage(getActivity()));
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }

    private void callGetStates() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();

            getApiRequest.request(getActivity(), iJsonListener, AppUrl.GET_STATE_LIST, "NotaryState");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year,monthOfYear,dayOfMonth);
        mDate = DateFormat.format("MM/dd/yyyy",c).toString();
    }

    class SaveLicense extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            int count = databaseClient.getAppDatabase()
                    .addLicenseDao()
                    .getCount(addLicense.getLicenseNum());
            if (count == 0) {
                databaseClient.getAppDatabase()
                        .addLicenseDao()
                        .insert(addLicense);
            } else {
                databaseClient.getAppDatabase()
                        .addLicenseDao()
                        .update(addLicense.getLicenseState(), addLicense.getAddress(), addLicense.getCity(), addLicense.getPhone(),
                                addLicense.getState(), addLicense.getZip(), addLicense.getLicenseNum(), addLicense.getExpiryDate());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        }
    }

    class SelectEmail extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            //creating a task
            savedEmail = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .userRegDao()
                    .getEmail();

            appId = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .userRegDao()
                    .getAppId();

            return savedEmail;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private void notaryappDialogSingle(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
    }
}
