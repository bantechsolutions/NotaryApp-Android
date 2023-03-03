package com.notaryapp.ejournal.fragments.selfie.govissuedid;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_VerifySignerActivity;
import com.notaryapp.ejournal.fragments.verifysigner.personallyknown.VEJ_Notarize_SignerPersonalCameraFragment;
import com.notaryapp.ejournal.fragments.verifysigner.signergvtid.VEJ_Notarize_SignerProfileFragment;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

public class VEJ_GovIssuedIdSelfiePreviewFragment extends Fragment {

    private Button confirmBtn;
    private View previewView;

    private TextView headerText;

    private final String TAG="VEJ_CameraPreviewFragment";
    private FragmentTransaction fragmentTransaction;

    private Button btnOk,btnCancel,btnBack,btn_close;

    //  private View previewView;
    private ImageView imageView;
    //  private String url = AppUrl.API_BASE_URL+"uploadDoc";
    //  private String selfieUrl = AppUrl.API_BASE_URL+"uploadPhoto";
    private String fromFragment,imgPath;
    private Bitmap bitmap;
    private String userEmail,savedImage,docIdType,errorMess;
    private DatabaseClient databaseClient;
    private boolean front=true,back=false,selfie;
    private Uri imageUri;
    private Fragment fragment;
    final int PIC_CROP = 1;

    private VEJ_Notarize_SignerPersonalCameraFragment vej_notarize_signerPersonalCameraFragment;



    public VEJ_GovIssuedIdSelfiePreviewFragment() {
    }

    public VEJ_GovIssuedIdSelfiePreviewFragment(String savedImage) {
    }

    public VEJ_GovIssuedIdSelfiePreviewFragment(Uri imageUri, String savedImage) {
        this.imageUri = imageUri;
        this.savedImage = savedImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewView = inflater.inflate(R.layout.fragment_notarize_signerpersonal_peview, container, false);
        init();
        CustomDialog.cancelProgressDialog();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOk.setEnabled(false);
                //CustomDialog.showProgressDialog(getActivity());
                //uploadFile(selfieUrl);
                ///Sourav 10122020
                //loadFragment(new Notarize_SignerPersonalProfileFragment(savedImage));
                ///Sourav 10122020
                loadFragment(new VEJ_Notarize_SignerProfileFragment(((VEJ_GovIssuedIdAddSelfieActivity)getActivity()).firstName,
                        ((VEJ_GovIssuedIdAddSelfieActivity)getActivity()).lastName,
                        ((VEJ_GovIssuedIdAddSelfieActivity)getActivity()).scanRef,
                        ((VEJ_GovIssuedIdAddSelfieActivity)getActivity()).idType,
                        imageUri.getPath()));
                ///Sourav 10122020
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new VEJ_GovIssuedIdSelfieCameraFragment());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new VEJ_GovIssuedIdSelfieCameraFragment());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new VEJ_GovIssuedIdSelfieCameraFragment());
            }
        });


        return previewView;
    }


    private void init(){
        CustomDialog.showProgressDialog(getActivity());
        databaseClient = DatabaseClient.getInstance(getActivity());
        confirmBtn = previewView.findViewById(R.id.btnOk);

        errorMess = getResources().getText(R.string.networkerror).toString();
        //bitmap = vej_notarize_signerPersonalCameraFragment.getCroppedBitmap();
        imageView = previewView.findViewById(R.id.imageView);
        btnOk = previewView.findViewById(R.id.btnOk);
        btnCancel = previewView.findViewById(R.id.btnCancel);
        btn_close = previewView.findViewById(R.id.btn_close);
        btnBack = previewView.findViewById(R.id.btn_back);
        headerText = previewView.findViewById(R.id.note);
        btnOk.setEnabled(true);

        headerText.setText(R.string.review_the_signers_photograph);

        imageView.setImageURI(imageUri);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VEJ_VerifySignerActivity.REF_VIEW_CONTAINER,fragment,true);
    }
}