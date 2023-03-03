package com.notaryapp.ui.fragments.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.fragments.dashboard_new.DashBoard_WelcomeFragmentNew;
import com.notaryapp.utils.FragmentViewUtil;

public class DashBoard_AllowAccessFragment extends Fragment {

    private String TAG = "AllowAccessFragment";
    Button btn_back;
    Button btn_access_Ok;
    private View noteView;
    private String checkReg;
    private Activity mActivity;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    public DashBoard_AllowAccessFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        noteView = inflater.inflate(R.layout.fragment_allow_access, container, false);
        btn_access_Ok = noteView.findViewById(R.id.btn_access_Ok);
        //btn_back = noteView.findViewById(R.id.btn_back);
        mActivity =getActivity();
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
        btn_access_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkPermission();
                }
            }
        });
        return noteView;
    }
    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment(getActivity(), DashBoardActivity.REF_VIEW_CONTAINER,fragment,false);
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Camera and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        requestPermissions(new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                requestPermissions(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            // Do something, when permissions are already granted
                loadFragment(new DashBoard_WelcomeFragmentNew());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            // When request is cancelled, the results array are empty
            if (
                    (grantResults.length > 0) &&
                            (grantResults[0]
                                    + grantResults[1]
                                    == PackageManager.PERMISSION_GRANTED
                            )
            ) {
                // Permissions are granted
               // getActivity().onBackPressed();
                   loadFragment(new DashBoard_WelcomeFragmentNew());

            } else {
                // Permissions are denied
                Toast.makeText(mActivity, "Permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
