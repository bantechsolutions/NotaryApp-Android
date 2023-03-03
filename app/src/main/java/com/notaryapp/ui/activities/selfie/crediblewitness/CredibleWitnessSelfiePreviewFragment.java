package com.notaryapp.ui.activities.selfie.crediblewitness;

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
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.ui.activities.verifyauthenticate.VerifySignerActivity;
import com.notaryapp.ui.fragments.verifyauthenticate.verifysigner.signerwitness.Notarize_SignerWitnessProfileFragment;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.FragmentViewUtil;

public class CredibleWitnessSelfiePreviewFragment extends Fragment {

    private Button confirmBtn;
    private View previewView;

    private final String TAG = "CameraPreviewFragment";
    private FragmentTransaction fragmentTransaction;

    private Button btnOk, btnCancel, btnBack, btn_close;

    //  private View previewView;
    private ImageView imageView;
    //  private String url = AppUrl.API_BASE_URL+"uploadDoc";
    //  private String selfieUrl = AppUrl.API_BASE_URL+"uploadPhoto";
    private String fromFragment, imgPath;
    private Bitmap bitmap;
    private String userEmail, savedImage, docIdType, errorMess;
    private DatabaseClient databaseClient;
    private boolean front = true, back = false, selfie;
    private Uri imageUri;
    private Fragment fragment;
    private TextView headerText;
    final int PIC_CROP = 1;


    public CredibleWitnessSelfiePreviewFragment() {
    }

    public CredibleWitnessSelfiePreviewFragment(String savedImage) {
    }

    public CredibleWitnessSelfiePreviewFragment(Uri imageUri, String savedImage) {
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
                /*loadFragment(new Notarize_SignerProfileFragment(((CredibleWitnessAddSelfieActivity) getActivity()).firstName,
                        ((CredibleWitnessAddSelfieActivity) getActivity()).lastName,
                        ((CredibleWitnessAddSelfieActivity) getActivity()).scanRef,
                        imageUri.getPath()));*/

                if (((CredibleWitnessAddSelfieActivity) getActivity()).pending != null
                  && ((CredibleWitnessAddSelfieActivity) getActivity()).pending.equals("1")) {
                    loadFragment(new Notarize_SignerWitnessProfileFragment(((CredibleWitnessAddSelfieActivity) getActivity()).firstName,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).lastName,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).scanRef,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).signerEmail, imageUri.getPath(), 1,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).idType));
                } else {
                    loadFragment(new Notarize_SignerWitnessProfileFragment(((CredibleWitnessAddSelfieActivity) getActivity()).firstName,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).lastName,
                            ((CredibleWitnessAddSelfieActivity) getActivity()).scanRef,
                            imageUri.getPath()));
                }
                ///Sourav 10122020
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LAHAR
                getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new CredibleWitnessSelfieCameraFragment());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LAHAR
                getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new CredibleWitnessSelfieCameraFragment());
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LAHAR
                getActivity().onBackPressed();
                //loadFragment(new Notarize_SignerPersonalCameraFragment());
                loadFragment(new CredibleWitnessSelfieCameraFragment());
            }
        });

        return previewView;
    }

    private void init() {
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
        headerText = previewView.findViewById(R.id.note);
        btnOk.setEnabled(true);

        imageView.setImageURI(imageUri);

        headerText.setText("Witness Photo");
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), VerifySignerActivity.REF_VIEW_CONTAINER, fragment, true);
    }
}