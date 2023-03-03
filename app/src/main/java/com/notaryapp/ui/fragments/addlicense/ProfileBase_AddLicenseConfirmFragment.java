package com.notaryapp.ui.fragments.addlicense;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.ui.activities.onboarding.NotaryStampActivity;
import com.notaryapp.ui.activities.onboarding.ProfileBaseActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileBase_AddLicenseConfirmFragment extends Fragment {

    private final String TAG = "AddLicenseFragment";

    private Context myContext;
    private View addLicenseCView;
    private Button btnConfirm, btnClose, btnAddAnother;

    private Info info;

    private TextView licenseNumber, stateName;
    private String userName;
    private DatabaseClient databaseClient;

    public ProfileBase_AddLicenseConfirmFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            addLicenseCView = inflater.inflate(R.layout.fragment_add_license_number_confirm, container, false);
        init();

        databaseClient = DatabaseClient.getInstance(getContext());

        new GetLicenses().execute();

        btnAddAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileBase_AddLicenseFragment());
               getActivity().onBackPressed();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotaryStampActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileBaseActivity.class);
                startActivity(intent);
            }
        });
        return addLicenseCView;
    }

    private void init() {
        btnConfirm = addLicenseCView.findViewById(R.id.btnConfirm);
        btnClose = addLicenseCView.findViewById(R.id.btn_addlice_close);
        btnAddAnother = addLicenseCView.findViewById(R.id.btnAddAnother);
        licenseNumber = addLicenseCView.findViewById(R.id.LicenseNo);
        stateName = addLicenseCView.findViewById(R.id.stateName);

    }

    private void callLicenseListAPI() {
        CustomDialog.showProgressDialog(getActivity());

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();

                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("LicenseList")) {
                            if (data.has("success")) {
                                String license_num = "", SstateName = "";
                                JSONArray license_array = data.getJSONArray("success");
                                if (license_array.length() != 0) {
                                    StringBuilder licenseNo = new StringBuilder();
                                    StringBuilder stateN = new StringBuilder();
                                    for (int i = 0; i < license_array.length(); i++) {

                                        JSONObject json_inside = license_array.getJSONObject(i);
                                        license_num = json_inside.getString("notary_license_number");
                                        //SstateName = json_inside.getString("notary_license_state_name");
                                        SstateName = json_inside.getString("notary_license_state");

                                        licenseNo.append(license_num).append("\n");
                                        stateN.append(SstateName).append("\n");
                                    }
                                    stateName.setText(stateN);
                                    licenseNumber.setText(licenseNo);
                                }

                            }
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

        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", userName);
                //  params.put("pledge",true);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(getActivity(), iJsonListener, params, AppUrl.LICENSE_LIST, "LicenseList");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), ProfileBaseActivity.REF_VIEW_CONTAINER, fragment, false);
    }

    class GetLicenses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            userName = databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            callLicenseListAPI();
        }
    }
}
