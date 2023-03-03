package com.notaryapp.ui.fragments.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.ui.activities.onboarding.SelectIdentityActivity;
import com.notaryapp.utils.CustomDialog;


public class DocumentsRequiredFragment extends Fragment {

    private DatabaseClient databaseClient;
    private Info info;
    private Activity activity;

    public DocumentsRequiredFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View noteView = inflater.inflate(R.layout.activity_documents__required, container, false);
        activity = getActivity();
        databaseClient = DatabaseClient.getInstance(activity);

        new GeInfo().execute();
        new DeleteAllSelectId().execute();
        Button okBtn = noteView.findViewById(R.id.btn_doc_reqiure_Ok);
        ImageView imgInfo1 = noteView.findViewById(R.id.info1);
        ImageView imgInfo2 = noteView.findViewById(R.id.info2);
        ImageView imgInfo3 = noteView.findViewById(R.id.info3);

        okBtn.setOnClickListener(v -> {

            startActivity(new Intent(getActivity(), SelectIdentityActivity.class));
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        });

        imgInfo1.setOnClickListener(v -> CustomDialog.notaryappInfoDialog(activity, info.getGovtId()));
        imgInfo2.setOnClickListener(v -> CustomDialog.notaryappInfoDialog(activity, info.getProfileLicenses()));
        imgInfo3.setOnClickListener(v -> CustomDialog.notaryappInfoDialog(activity, info.getStamp()));

        return noteView;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = databaseClient.getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
    class DeleteAllSelectId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            databaseClient.getAppDatabase()
                    .identityTypeDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}

