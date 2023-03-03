package com.notaryapp.lockadoc.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LAD_AddDocDetailsFragment extends Fragment {


    private View docDetailsView;
    private Button startBtn;
    private Button btnBack, btnClose;
    private Spinner docSpinner;
    private TextInputEditText docNameEdit, apnEdit, confirmApnEdit;
    private TextInputLayout docNameTxt, apnTxt, confirmApnTxt;
    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private String docName, apnNumber, confirmApn,docOtherType="";
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String savedEmail, transactionId, selectedDoc, serverDocName;
    private ArrayList<String> arrySpin;
    private boolean validApn, validCApn, validDocName;
    private ImageView imgInfo;
    private Info info;
    private EditText etOther;

    public LAD_AddDocDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        docDetailsView = inflater.inflate(R.layout.lad_fragment_add_doc_details, container, false);
        init();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.hasValue(docNameEdit, docNameTxt)) {
                    validDocName = true;
                } else {
                    validDocName = false;
                    docNameTxt.setError(null);
                    docNameTxt.setErrorEnabled(false);
                    docNameTxt.setError("Enter Document Name");
                }
                /*if (Validation.hasValue(apnEdit, apnTxt)) {
                    validApn = true;
                } else {
                    validApn = false;
                    apnTxt.setError(null);
                    apnTxt.setErrorEnabled(false);
                    apnTxt.setError("Enter APN number");
                }*/
                /*if (Validation.hasValue(confirmApnEdit, confirmApnTxt)) {
                    //validCApn = true;
                } else {
                    validCApn = false;
                    confirmApnTxt.setError(null);
                    confirmApnTxt.setErrorEnabled(false);
                    confirmApnTxt.setError("Enter APN number");
                }*/
                if(selectedDoc.equalsIgnoreCase("other")
                        && etOther.getText().toString().trim().equalsIgnoreCase("")) {
                    Utils.toastCenter(getActivity(),"Enter Other Document Type");
                }else{
                   // if (validDocName && validApn && validCApn) {
                    if (validDocName) {
                        //if(apnEdit.getText().toString().trim().equalsIgnoreCase(confirmApnEdit.getText().toString().trim()))
                        if(apnEdit.getText().toString().trim().equals(confirmApnEdit.getText().toString().trim())) {
                            getData();
                            addDoc();
                        }else {
                            //Utils.toastCenter(getActivity(),"Unique ID Number and Confirm Unique ID Number are different!");
                            Toast.makeText(getActivity(), "Unique ID Number and Confirm Unique ID Number are different!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        docSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(arrySpin.size() > 0) {
                    selectedDoc = arrySpin.get(position);
                    if(selectedDoc != null
                       && selectedDoc.equalsIgnoreCase("other")){
                        etOther.setVisibility(View.VISIBLE);
                    }else {
                        etOther.setText("");
                        etOther.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etOther.setText("");
                etOther.setVisibility(View.GONE);
            }
        });
        docNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasDocText(docNameEdit, docNameTxt)) {
                    validDocName = true;//Validation.isFirstName(docNameEdit, docNameTxt, true);
                }
            }
        });
        docNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasDocText(docNameEdit, docNameTxt)) {
                    validDocName = true;//Validation.isFirstName(docNameEdit, docNameTxt, true);
                    docNameTxt.setError(null);
                } else {
                    docNameTxt.setError("Enter Document name");
                }
            }
        });

        apnEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(apnEdit, apnTxt)) {
                    //     validApn = Validation.passwordValidation(apnEdit, apnTxt, true);
                    validApn = true;
                    apnTxt.setError(null);
                } else {
                    apnTxt.setError("Enter APN number");
                }
            }
        });

        apnEdit.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        apnEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //scrollView.fullScroll(View.FOCUS_DOWN);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasValue(apnEdit, apnTxt)) {
                    //  validApn = Validation.passwordValidation(apnEdit, apnTxt, true);
                    //   validCApn = Validation.passwordValidation(confirmApnEdit, confirmApnTxt, true);
                    validApn = true;
                }
                validCApn = Validation.hasValue(confirmApnEdit, confirmApnTxt);
                if (validApn && validCApn) {
                    validCApn = Validation.hasAPNMatched(apnEdit, confirmApnEdit, confirmApnTxt);

                }
            }
        });

        confirmApnEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(confirmApnEdit, confirmApnTxt)) {
                    validApn = true;
                    //  validApn = Validation.passwordValidation(apnEdit, confirmApnTxt, true);
                    validCApn = Validation.hasAPNMatched(apnEdit, confirmApnEdit, confirmApnTxt);
                }
            }
        });
        confirmApnEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Validation.hasValue(confirmApnEdit, confirmApnTxt)) {
                    //  validCApn = Validation.passwordValidation(confirmApnEdit, confirmApnTxt, true);
                    // validApn = Validation.passwordValidation(apnEdit, apnTxt, true);
                    validApn = Validation.hasAPNMatched(apnEdit, confirmApnEdit, confirmApnTxt);
                    validCApn = Validation.hasAPNMatched(apnEdit, confirmApnEdit, confirmApnTxt);
                    if (validApn)
                        confirmApnTxt.setError(null);
                } else {

                }
            }
        });

        confirmApnEdit.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(getActivity(), "If your document has a unique identifying number, please enter it here. If there is no unique identifier at all that you can use, simply leave it blank.");
            }
        });
        return docDetailsView;
    }

    private void init() {
        arrySpin = new ArrayList<>();
        imgInfo = docDetailsView.findViewById(R.id.imgInfo);
        docSpinner = docDetailsView.findViewById(R.id.docSpinner);
        startBtn = docDetailsView.findViewById(R.id.startBtn);
        docNameEdit = docDetailsView.findViewById(R.id.docName);
        apnEdit = docDetailsView.findViewById(R.id.apnNumber);
        confirmApnEdit = docDetailsView.findViewById(R.id.cApnNum);
        docNameTxt = docDetailsView.findViewById(R.id.docNameText);
        apnTxt = docDetailsView.findViewById(R.id.apnText);
        confirmApnTxt = docDetailsView.findViewById(R.id.capnText);
        btnBack = docDetailsView.findViewById(R.id.btn_pro_back);
        btnClose = docDetailsView.findViewById(R.id.btn_pro_close);
        etOther =docDetailsView.findViewById(R.id.etOther);
        initIJsonListener();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new SelectData().execute();
        new GeInfo().execute();
        getDocTypes();
       /* arrySpin.add("Compliance Agreement");
        arrySpin.add("Correction Agreement");
        arrySpin.add("Deed of Trust");
        arrySpin.add("Deed of Mortgage");
        arrySpin.add("Deed Grant");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.membership_spinner_textview, arrySpin);
        docSpinner.setAdapter(dataAdapter);*/
    }

    private void getData() {
        docName = docNameEdit.getText().toString().trim();
        apnNumber = apnEdit.getText().toString().trim();
        confirmApn = confirmApnEdit.getText().toString().trim();
        docOtherType = etOther.getText().toString().trim();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(), REF_VIEW_CONTAINER, fragment);
    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

//    class InsertDoc extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... voids) {
//
//
//            int docCount = databaseClient.getAppDatabase()
//                    .docPicsDao()
//                    .getDocCount();
//
//            // Adding the add document card for showing in dashboard
//            if (docCount == 0) {
//                DocPics docPic = new DocPics("Add Document", "Add Document","Add Document");
//
//                databaseClient.getAppDatabase()
//                        .docPicsDao()
//                        .insert(docPic);
//            }
//
////             databaseClient.getAppDatabase()
////                    .docPicsDao()
////                    .insert(docPics);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void voids) {
//            super.onPostExecute(voids);
//            // loadFragment(new Notarize_AlertAddDocsFragment(docName));
//            loadFragment(new Notarize_AlertAddDocsFragment(docName, serverDocName));
//        }
//
//    }

    class SelectData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            savedEmail = databaseClient.getAppDatabase()
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
    private void getDocTypes() {
        CustomDialog.showProgressDialog(getActivity());
        try{
            GETAPIRequest getApiRequest=new GETAPIRequest();
            getApiRequest.request(getActivity(),iJsonListener, AppUrl.GET_DOC_TYPES ,"docTypes");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        }catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void addDoc() {
        CustomDialog.showProgressDialog(getActivity());
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("docuType", selectedDoc);
                params.put("docOtherType", docOtherType);// Doc Other Type
                params.put("docuName", docName);
                params.put("apnNum", apnNumber);
                params.put("tranid", transactionId);
                //  params.put("tranid", "fa1b5d68-ce9c-4f84-a3cf-a082de0fb47a");

            } catch (Exception e) {
                //e.printStackTrace();
            }
            
            // postapiRequest.request(getActivity(), iJsonListener, params, url, "docSigned");
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.ADD_DOC, "addDoc");
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
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("addDoc")) {
                            if (data.has("success")) {
                                String success = data.getString("success");
                                if (success.equals("1")) {
                                    serverDocName = data.getString("Document");
//                                    Log.e("DocName", serverDocName);
//                                    docPics = new DocPics();
//                                    docPics.setDocName(documentName);
                                    //docPics = new DocPics(documentName,docName);
                                  //  new InsertDoc().execute();

                                    loadFragment(new LAD_AlertAddDocsFragment(docName, serverDocName));


                                }
                            }
                        }else if(type.equals("docTypes")){
                            if (data.has("dTypes")) {
                                JSONArray termsArray = data.getJSONArray("dTypes");
                                if (termsArray.length() != 0) {
//                                    Log.w("TAGGGGGGG", termsArray.toString());
                                    String docType;
                                    for (int i = 0; i < termsArray.length(); i++) {
                                        JSONObject json_inside = termsArray.getJSONObject(i);
                                        docType = json_inside.getString("dTypeName");
                                        arrySpin.add(docType);
                                    }
                                }
                            }
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, arrySpin);
                        docSpinner.setAdapter(dataAdapter);
                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", getActivity());
                        CustomDialog.notaryappDialogSingle(getActivity(), "Error! No data fetched from server");
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", getActivity());
                    //  CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                // RequestQueueService.showAlert(msg,getActivity());
                // CustomDialog.notaryappDialogSingle(getActivity(),"Something went wrong ...Please try after sometime");
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }
}
