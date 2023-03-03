package com.notaryapp.ui.activities.selfie;

import android.content.Intent;
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

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.onboarding.ProfileActivity;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

public class SelfiePreviewFragment extends Fragment {

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


    public SelfiePreviewFragment() {
    }

    public SelfiePreviewFragment(String savedImage) {
    }

    public SelfiePreviewFragment(Uri imageUri, String savedImage) {
        this.imageUri = imageUri;
        this.savedImage = savedImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        previewView = inflater.inflate(R.layout.fragment_onbording_camara_peview, container, false);
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
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("FirstName", ((AddSelfieActivity)getActivity()).firstName);
                intent.putExtra("LastName", ((AddSelfieActivity)getActivity()).lastName);
                intent.putExtra("ScanRef", ((AddSelfieActivity)getActivity()).scanRef);
                intent.putExtra("image", imageUri.getPath());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                getActivity().finish();
                ///Sourav 10122020
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new SelfieCameraFragment());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new SelfieCameraFragment());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                //LAHAR
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new SelfieCameraFragment());
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