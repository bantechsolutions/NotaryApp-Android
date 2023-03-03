package com.notaryapp.ejournal.fragments.verifysigner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_VerifySignerActivity;
import com.notaryapp.ejournal.fragments.selfie.govissuedid.VEJ_GovIssuedIdAddSelfieActivity;
import com.notaryapp.ejournal.fragments.verifysigner.personallyknown.VEJ_Notarize_SignerPersonalAlertFragment;
import com.notaryapp.ejournal.fragments.verifysigner.signergvtid.VEJ_Notarize_SignerDocTypeFragment;
import com.notaryapp.ejournal.fragments.verifysigner.signerwitness.VEJ_Notarize_SignerWitnessAlertFragment;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.utils.FragmentViewUtil;


public class VEJ_Notarize_SignerVerifyTypeFragment extends Fragment {

  //  private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;
    private View verifyTypeView;
    private CardView government,personal,witness, government_m, witness_m;
    private TextView idvCountText;
    private Button btn_validate;
    private Button btnBack,btnClose;
    private boolean transactionFlag = true;
    private int count;
    private boolean permission;
    private DatabaseClient databaseClient;
    private VACustomer memPlans;
    private int transCount;
    private ConstraintLayout overlay1, overlay2;


    public VEJ_Notarize_SignerVerifyTypeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        verifyTypeView = inflater.inflate(R.layout.fragment_notarize_signer_verify_type, container, false);
        init();
        buttonControls();

       return verifyTypeView;
    }

    @Override
    public void onResume() {
        super.onResume();
        transactionFlag = false;
    }

    private void buttonControls() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   getActivity().onBackPressed();
                getActivity().finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   getActivity().onBackPressed();
              getActivity().finish();
            }
        });
        //Button click for Government ID.
        government.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               loadFragment(new VEJ_Notarize_SignerDocTypeFragment());
                //Testing
               // loadFragment(new Notarize_SignerProfileFragment("Ambily","PS","f13f6d9b-7824-49e4-9d12-d5661abb1e3b"));
                //Testing
            }
        });

        government_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VEJ_GovIssuedIdAddSelfieActivity.class);
                intent.putExtra("IdType", "manually");
                intent.putExtra("FirstName", "");
                intent.putExtra("LastName", "");
                intent.putExtra("ScanRef", "");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                //getActivity().finish();
            }
        });

        //Button click for personally known.
        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(new VEJ_Notarize_SignerPersonalAlertFragment());
                //Testing
              // loadFragment(new Notarize_SignerPersonalProfileFragment("test"));//"Ambily","PS","f13f6d9b-7824-49e4-9d12-d5661abb1e3b"));
                //Testing
            }
        });

        //Button click for credible witness.
        witness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new VEJ_Notarize_SignerWitnessAlertFragment());
                //Testing
                // loadFragment(new Notarize_SignerWitnessSignerProfileFragment());//"Ambily","PS","f13f6d9b-7824-49e4-9d12-d5661abb1e3b"));
               // loadFragment(new Notarize_WitnessDocTypeFragment());
                //Testing
            }
        });
        witness_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new VEJ_Notarize_SignerWitnessAlertFragment("manually"));
            }
        });
    }

    private void init() {

        btnBack = verifyTypeView.findViewById(R.id.btn_pro_back);
        btnClose = verifyTypeView.findViewById(R.id.btn_pro_close);
        government = verifyTypeView.findViewById(R.id.government);
        personal = verifyTypeView.findViewById(R.id.personal);
        witness = verifyTypeView.findViewById(R.id.witness);
        government_m = verifyTypeView.findViewById(R.id.government_m);
        witness_m = verifyTypeView.findViewById(R.id.witness_m);
        idvCountText = verifyTypeView.findViewById(R.id.countText);
        overlay1 = verifyTypeView.findViewById(R.id.overlay1);
        overlay2 = verifyTypeView.findViewById(R.id.overlay2);
        checkCameraPermission();
        databaseClient = DatabaseClient.getInstance(getActivity());
        new DeleteAllSelectId().execute();
        new SelectPlans().execute();
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VEJ_VerifySignerActivity.REF_VIEW_CONTAINER,fragment,true);
    }
    private void checkCameraPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

            permission = true;

        }else {
            // Do something, when permissions are already granted
            permission = false;
        }
    }
   class DeleteAllSelectId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase().NotaryAppSelectIDDao().deleteAll();
            databaseClient.getAppDatabase().witnessSelectIDDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
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
            transCount = memPlans.getTransactionsLeft();

            if(transCount > 0) {
                idvCountText.setText(String.format("%02d", transCount));
                overlay1.setVisibility(View.GONE);
                overlay2.setVisibility(View.GONE);
            }else {
                idvCountText.setText("00");
                overlay1.setVisibility(View.VISIBLE);
                overlay2.setVisibility(View.VISIBLE);
            }
        }
    }
}
