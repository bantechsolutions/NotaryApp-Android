package com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.personallyknown;

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
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

public class Notarize_SignerPersonalPreviewFragment extends Fragment {

    private Button confirmBtn;
    private View previewView;



    private final String TAG="CameraPreviewFragment";
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


    public Notarize_SignerPersonalPreviewFragment() {
    }

    public Notarize_SignerPersonalPreviewFragment(String savedImage) {
    }

    public Notarize_SignerPersonalPreviewFragment(Uri imageUri, String savedImage) {
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
              //  CustomDialog.showProgressDialog(getActivity());
                //uploadFile(selfieUrl);
                loadFragment(new Notarize_SignerPersonalProfileFragment(savedImage));
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerPersonalCameraFragment());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerPersonalCameraFragment());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                loadFragment(new Notarize_SignerPersonalCameraFragment());
            }
        });

        return previewView;
    }

    private void init(){
        CustomDialog.showProgressDialog(getActivity());
        databaseClient = DatabaseClient.getInstance(getActivity());
        confirmBtn = previewView.findViewById(R.id.btnOk);


        errorMess = getResources().getText(R.string.networkerror).toString();
        //bitmap = Notarize_SignerPersonalCameraFragment.croppedBitmap;
        imageView = previewView.findViewById(R.id.imageView);
        btnOk = previewView.findViewById(R.id.btnOk);
        btnCancel = previewView.findViewById(R.id.btnCancel);
        btn_close = previewView.findViewById(R.id.btn_close);
        btnBack = previewView.findViewById(R.id.btn_back);
        btnOk.setEnabled(true);

        imageView.setImageURI(imageUri);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER,fragment,true);
    }
}