package com.notaryapp.ui.fragments.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.adapter.CommissionAdapter;
import com.notaryapp.model.CommissionItems;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.LicenseListActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashBoard_LicenseListFragment extends Fragment {

    private final String TAG = "AddLicenseFragment";
    private View addLicenseCView;
    private Button btnClose, btnAddAnother;
    private Info info;
    //private TextView licenseNumber, stateName;
    private String userName;
    private DatabaseClient databaseClient;
    ///
    private ArrayList<CommissionItems> commissionItems;
    private ListView listView;
    public DashBoard_LicenseListFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        addLicenseCView = inflater.inflate(R.layout.fragment_added_license_number, container, false);
        init();
        databaseClient = DatabaseClient.getInstance(getContext());

        new GetLicenses().execute();

        btnAddAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new DashBoard_AddLicenseFragment(" "," "));
                // getActivity().onBackPressed();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);

            }
        });
        return addLicenseCView;
    }

    private void init() {
        btnClose = addLicenseCView.findViewById(R.id.btn_addlice_close);
        btnAddAnother = addLicenseCView.findViewById(R.id.btnAddAnother);
        //licenseNumber = addLicenseCView.findViewById(R.id.LicenseNo);
        //stateName = addLicenseCView.findViewById(R.id.stateName);
        listView = addLicenseCView.findViewById(R.id.list_comm);
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
                                commissionItems =new ArrayList<>();
                                String license_num = "", SstateName = "", license_status="";
                                JSONArray license_array = data.getJSONArray("success");
                                if (license_array.length() != 0) {
                                    StringBuilder licenseNo = new StringBuilder();
                                    StringBuilder stateN = new StringBuilder();
                                    for (int i = 0; i < license_array.length(); i++) {

                                        JSONObject json_inside = license_array.getJSONObject(i);
                                        license_num = json_inside.getString("notary_license_number");
                                        //SstateName = json_inside.getString("notary_license_state_name");
                                        SstateName = json_inside.getString("notary_license_state");
                                        license_status = json_inside.getString("status");

                                        licenseNo.append(license_num).append("\n");
                                        stateN.append(SstateName).append("\n");
                                        commissionItems.add(new CommissionItems(SstateName,license_num, license_status));
                                    }
                                    /*for (int i = 0; i < 30; i++) {

                                        //JSONObject json_inside = license_array.getJSONObject(i);
                                        license_num = "123123";
                                        //SstateName = json_inside.getString("notary_license_state_name");
                                        SstateName = "California "+ i;
                                        license_status = "ok";

                                        licenseNo.append(license_num).append("\n");
                                        stateN.append(SstateName).append("\n");
                                        commissionItems.add(new CommissionItems(SstateName,license_num, license_status));
                                    }*/
                                    //stateName.setText(stateN);
                                    //licenseNumber.setText(licenseNo);

                                    CommissionAdapter commissionAdapter = new CommissionAdapter(getContext(), R.layout.items_commission_row_data, commissionItems);
                                    listView.setAdapter(commissionAdapter);
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
        FragmentViewUtil.replaceFragment(getActivity(), LicenseListActivity.REF_VIEW_CONTAINER, fragment, true);
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
