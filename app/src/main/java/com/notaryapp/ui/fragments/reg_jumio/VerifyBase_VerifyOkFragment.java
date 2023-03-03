package com.notaryapp.ui.fragments.reg_jumio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.selfie.AddSelfieActivity;
import com.notaryapp.utils.FragmentViewUtil;


public class VerifyBase_VerifyOkFragment extends Fragment {
    private View retryView;
    private Button okBtn;
    private TextView txtDoc,txtTrnsaction,txtFace;
    private String docStatus,transStatus,veriStatus,docExpiryStatus;
    private String firstName, lastName,scanRef;
    private static final int REF_VIEW_CONTAINER = R.id.verifySignerContainer;

    public VerifyBase_VerifyOkFragment(){

    }
    public VerifyBase_VerifyOkFragment (String docStatus, String trsnsStatus,String veriStatus,
                                        String docExpiryStatus, String firstName, String lastName,String scanRef){
        this.docStatus = docStatus;
        this.transStatus = trsnsStatus;
        this.veriStatus = veriStatus;
        this.docExpiryStatus = docExpiryStatus;
        this.firstName = firstName;
        this.lastName = lastName;
        this.scanRef = scanRef;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retryView = inflater.inflate(R.layout.fragment_verify_ok, container, false);
       init();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("FirstName", firstName);
                intent.putExtra("LastName", lastName);
                intent.putExtra("ScanRef", scanRef);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);*/

                Intent intent = new Intent(getActivity(), AddSelfieActivity.class);
                intent.putExtra("FirstName", firstName);
                intent.putExtra("LastName", lastName);
                intent.putExtra("ScanRef", scanRef);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                //loadFragment(new Notarize_SignerPersonalCameraFragment());

            }
        });

        return retryView;
    }

    private void init(){
        okBtn = retryView.findViewById(R.id.btn_verify_ok);
        txtDoc = retryView.findViewById(R.id.txt_doc_response);
        txtTrnsaction = retryView.findViewById(R.id.txt_transaction_response);
        txtFace = retryView.findViewById(R.id.txt_face_response);
      //  txtDoc.setText("Documents : "+docStatus +"\n DocExpiry : "+docExpiryStatus);
      //  txtTrnsaction.setText("Transaction : "+transStatus);
      //  txtFace.setText("Face Similarity : "+veriStatus);
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(), REF_VIEW_CONTAINER, fragment);
    }
}
