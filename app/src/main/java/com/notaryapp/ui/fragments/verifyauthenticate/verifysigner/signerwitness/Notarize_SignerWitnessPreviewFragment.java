package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

public class Notarize_SignerWitnessPreviewFragment extends Fragment {

    private View previewView;
    private final String TAG="CameraPreviewFragment";
    private FragmentTransaction fragmentTransaction;
    private Button confirmBtn,btnCancel,btnBack;
    private ImageView imageView;
    private int selectedId;
    private int prevSelected = -1;
    private String fromFragment,imgPath;
    private Bitmap bitmap;
    private String userEmail,selectedType,selectedType_save,savedImage,docIdType,errorMess;
   // private DatabaseClient databaseClient;
    private boolean front=true,back=false,selfie;
    private Uri imageUri;
    private Fragment fragment;
    final int PIC_CROP = 1;
    private String idType="";

    private Notarize_SignerWitnessCameraFragment vej_notarize_signerWitnessCameraFragment;

    public Notarize_SignerWitnessPreviewFragment() {
    }

    public Notarize_SignerWitnessPreviewFragment(Uri imageUri, String savedImage) {
        this.imageUri = imageUri;
        this.savedImage = savedImage;
    }

    public Notarize_SignerWitnessPreviewFragment(Uri imageUri, String savedImage, String idType) {
        this.imageUri = imageUri;
        this.savedImage = savedImage;
        this.idType = idType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewView = inflater.inflate(R.layout.fragment_notarize_signercrediblewitness_peview, container, false);
        init();
        CustomDialog.cancelProgressDialog();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idType.equalsIgnoreCase("manually")){
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment(savedImage,imageUri, null, "manually"));
                } else {
                    loadFragment(new Notarize_SignerWitnessSignerProfileFragment(savedImage,imageUri, null, "null"));
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                if (idType.equalsIgnoreCase("manually")){
                    loadFragment(new Notarize_SignerWitnessCameraFragment("manually"));
                } else {
                    loadFragment(new Notarize_SignerWitnessCameraFragment());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                if (idType.equalsIgnoreCase("manually")){
                    loadFragment(new Notarize_SignerWitnessCameraFragment("manually"));
                } else {
                    loadFragment(new Notarize_SignerWitnessCameraFragment());
                }
            }
        });

        return previewView;
    }

    private void init(){
        CustomDialog.showProgressDialog(getActivity());
        //databaseClient = DatabaseClient.getInstance(getActivity());
        confirmBtn = previewView.findViewById(R.id.btnOk);
        errorMess = getResources().getText(R.string.networkerror).toString();
        //bitmap = Notarize_SignerWitnessCameraFragment.croppedBitmap;
     //  databaseClient.getAppDatabase().userRegDao().getEmail();
        imageView = previewView.findViewById(R.id.imageView);
        btnCancel = previewView.findViewById(R.id.btnCancel);
        btnBack = previewView.findViewById(R.id.btn_back);

        imageView.setImageURI(imageUri);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER,fragment,true);
    }
}