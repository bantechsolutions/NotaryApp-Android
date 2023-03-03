package com.notaryapp.ui.fragments.verifyauthenticate.adddoc;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.notaryapp.R;
import com.notaryapp.adapter.VAStepDocsAdapter;
import com.notaryapp.adapter.VAStepsAdapter;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.SignerReg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ShareDocsFragment extends Fragment {

    private View shareView;
    private RecyclerView recyclerD,recyclerR;
    private List<SignerReg> signerList;
    private List<DocumentsModel> docList;
    private DatabaseClient databaseClient;
    private Integer signerCount = 0, docsCount = 0;
    private List<Integer> stampCountArray;

    public ShareDocsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        shareView = inflater.inflate(R.layout.fragment_share_docs, container, false);
        init();

        try {
            signerCount = new GetSigners().execute().get();
            docsCount = new GetDocs().execute().get();
            if (signerCount > 1){
                VAStepsAdapter stepsAdapter1 = new VAStepsAdapter(signerList,getActivity());
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
                recyclerR.setLayoutManager(layoutManager);
                recyclerR.setAdapter(stepsAdapter1);
            }
            else {
                Toast.makeText(getActivity(), "No recipients found.", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException | InterruptedException e) {
            //e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        return shareView;
    }

    private void init() {
//        recyclerD = shareView.findViewById(R.id.recyclerDocs);
//        recyclerR= shareView.findViewById(R.id.recyclerRecipients);

    }


    class GetSigners extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {



            //Getting the signer data from database
            signerList = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigners();

            //Getting the count of rows
            signerCount = databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getCount();

            //Adding "Add Witness" as first element

            return signerCount;
        }
        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
        }
    }

    class GetDocs extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            stampCountArray = new ArrayList<>();
            //Getting the signer data from database
            docList = databaseClient.getAppDatabase()
                    .documentsImageDao()
                    .getDisDocs();

            int listCount = 0;

            for(int i=0;i<signerList.size();i++){

                listCount = databaseClient.getAppDatabase()
                        .documentsImageDao()
                        .countDocs(docList.get(i).getStampName());

                stampCountArray.add(listCount);
            }

            return docsCount;
        }
        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);

            if (docsCount > 1){
                VAStepDocsAdapter stepsAdapter2 = new VAStepDocsAdapter(docList,stampCountArray,getActivity());
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
                recyclerR.setLayoutManager(layoutManager);
                recyclerR.setAdapter(stepsAdapter2);
            }
            else {
                Toast.makeText(getActivity(), "No documents found.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
