package com.notaryapp.ui.fragments.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.notaryapp.R;
import com.notaryapp.adapter.FAQAdapter;
import com.notaryapp.model.Faq;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashBoard_FAQActivity extends AppCompatActivity {

    private TextView txtFaq;
    private String questions,answers;
    private Faq faq;
    private List<Faq> faqArray ;
    private IJsonListener iJsonListener;
    FAQAdapter faqAdapter;
    RecyclerView recyclerFAQ;
    Activity activity;
    private Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard_faq);
        activity = this;

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        CustomDialog.showProgressDialog(activity);
        getFAQDetails();

    }
    private void init(){
        recyclerFAQ = findViewById(R.id.recyclerFAQ);
        back = findViewById(R.id.btn_pro_back);
        initIJsonListener();

    }
    private void getFAQDetails() {

        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            getApiRequest.request(activity,iJsonListener, AppUrl.GET_FAQ  ,"faq");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (data.has("faq")) {
                            if(type.equals("faq")){
                                JSONArray jumio_keys = data.getJSONArray("faq");
                                faqArray = new ArrayList<>();
                                if (jumio_keys.length() != 0) {
                                    for (int i = 0; i < jumio_keys.length(); i++) {
                                        JSONObject json_inside = jumio_keys.getJSONObject(i);
                                        questions = json_inside.getString("question");
                                        answers = json_inside.getString("answer");
                                        faq = new Faq(questions,answers);
                                        faqArray.add(faq);
                                    }
                                    setupRecycler();
                                }
                            }
                        }
                    } else {
                        CustomDialog.cancelProgressDialog();
                        //  RequestQueueService.showAlert("Error! No data fetched", activity);
                        CustomDialog.notaryappDialogSingle(activity,"Error! No data fetched");
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //  RequestQueueService.showAlert("Something went wrong", activity);
                    CustomDialog.notaryappDialogSingle(activity, Utils.errorMessage(activity));

                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                // RequestQueueService.showAlert(msg,activity);
                CustomDialog.notaryappDialogSingle(activity, Utils.errorMessage(activity));
            }
            @Override
            public void onFetchStart() {
            }
        };
    }

    private void setupRecycler() {
        if(faqArray.size() > 0) {
            faqAdapter = new FAQAdapter(faqArray,DashBoard_FAQActivity.this);
            GridLayoutManager layoutManager = new GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false);
            ((SimpleItemAnimator) recyclerFAQ.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerFAQ.setLayoutManager(layoutManager);
            recyclerFAQ.setAdapter(faqAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right);
    }
}
