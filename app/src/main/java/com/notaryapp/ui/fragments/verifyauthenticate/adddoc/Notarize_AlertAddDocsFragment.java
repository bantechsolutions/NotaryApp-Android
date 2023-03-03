package com.notaryapp.ui.fragments.verifyauthenticate.adddoc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

//import com.labters.documentscanner.ImageCropActivity;
//import com.labters.documentscanner.helpers.ScannerConstants;
//import com.scanlibrary.ScanActivity;
//import com.scanlibrary.ScanConstants;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.FragmentViewUtil;


public class Notarize_AlertAddDocsFragment extends Fragment {

    private View docAlertView;
    private Button btnOk;
    private Button btnBack,btnClose;
    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private DatabaseClient databaseClient;
    private Context context;
    private String docName;

    private String serverDocName;
    private static final int REQUEST_CODE = 99;

    public Notarize_AlertAddDocsFragment(String docName, String serverDocName) {
        this.docName = docName;
        this.serverDocName = serverDocName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        docAlertView = inflater.inflate(R.layout.fragment_notarize_alert_add_docs, container, false);
        init();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new Notarize_AddDocCameraFragment(docName,serverDocName)) ;
                loadFragment(new Notarize_AddDocFragmentLanding(docName,serverDocName)) ;
                //new DeleteImages().execute();
                //startScan(1);
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
        return docAlertView;
    }
    protected void startScan(int preference) {
        //Intent intent = new Intent(getActivity(), ScanActivity.class);
        //intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA);
        //startActivityForResult(intent, REQUEST_CODE);
    }
    private void init() {
        btnOk = docAlertView.findViewById(R.id.btnOk);
        btnBack = docAlertView.findViewById(R.id.btn_pro_back);
        btnClose = docAlertView.findViewById(R.id.btn_pro_close);

        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
    }
}
