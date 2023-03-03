package com.notaryapp.ejournal.fragments.adddoc;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.utils.FragmentViewUtil;


public class VEJ_Notarize_AddDocsFragment extends Fragment {

    private View addDocView;
    private Button startBtn;
    private Button btnBack,btnClose;
    public static final int REF_VIEW_CONTAINER = R.id.addDocsContainer;
    private Context context;
    private DatabaseClient databaseClient;

    public VEJ_Notarize_AddDocsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addDocView = inflater.inflate(R.layout.fragment_notarize_add_docs, container, false);
        init();
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  
                loadFragment(new VEJ_Notarize_AddDocDetailsFragment()) ;
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
        return addDocView;
    }

    private void init() {
        startBtn = addDocView.findViewById(R.id.startBtn);
        btnBack = addDocView.findViewById(R.id.btn_pro_back);
        btnClose = addDocView.findViewById(R.id.btn_pro_close);
        context = getActivity();
        databaseClient = DatabaseClient.getInstance(context);
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.loadFragment(getActivity(),REF_VIEW_CONTAINER,fragment);
    }

}
