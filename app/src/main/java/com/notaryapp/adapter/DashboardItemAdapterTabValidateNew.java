package com.notaryapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.lockadoc.activityes.LADStepsActivity;
import com.notaryapp.model.webresponse.Body;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.AllTransactions;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignDocs;
import com.notaryapp.roomdb.entity.SignerDocType;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.CompletedValidateProfile;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.verifyauthenticate.DoneNotarizeActivity;
import com.notaryapp.ui.activities.verifyauthenticate.NotarizeStepsActivity;
import com.notaryapp.ui.activities.verifyauthenticate.ShareActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.rounded_imageView.RoundedImageView;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardItemAdapterTabValidateNew extends RecyclerView.Adapter<DashboardItemAdapterTabValidateNew.MyView> implements Filterable {

    private List<Body> list;
    private Context context;
    private String transactionId;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;
    private String date, dojDate, doc;
    private SignerReg signerReg;
    private LADParties ladParties;
    private String level;
    private String tranType;
    private boolean star = false;
    private AppCompatActivity activity;
    private Transactions transactions;
    private WitnessReg witnessReg;
    private UserLocation userLocation;

    private String documentName;

    private ArrayList<AllTransactions> mArrayList;
    private List<Body> mFilteredList;
    private ArrayList<WitnessReg> wArrayList;
    private ArrayList<SignerReg> sArrayList;
    private ArrayList<LADParties> ladPartiesArrayList;
    private ArrayList<SignerDocType> signerDocTypes;
    private ArrayList<DocumentsModel> addDocArrayList;

    private String licenseNo, selectedStamp;
    private SealAdded sealAdded;
    private LoadingCompletedInterface loadingCompletedInterface;

    public DashboardItemAdapterTabValidateNew(LoadingCompletedInterface loadingCompletedInterface,
                                              List<Body> horizontalList, Activity context) {
        this.list = horizontalList;
        mFilteredList = horizontalList;
        this.context = context;
        databaseClient = DatabaseClient.getInstance(context);
        this.loadingCompletedInterface = loadingCompletedInterface;
        initIJsonListener();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = list;
                } else {

                    ArrayList<Body> filteredList = new ArrayList<>();

                    for (Body allTransactionsl : list) {

                        if (allTransactionsl.getDocType().toLowerCase().contains(charString) ||
                                allTransactionsl.getTranType().toLowerCase().contains(charString) ||
                                allTransactionsl.getDoj().toLowerCase().contains(charString)) {

                            filteredList.add(allTransactionsl);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                mFilteredList = (ArrayList<Body>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NotNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dash, parent, false);

        return new MyView(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        try {

            final Body dashItem = list.get(position);
            date = dashItem.getDoj();
            String dojDateArry[] = date.split("T");
            dojDate = dojDateArry[0];
            if (dashItem.getTranType().equalsIgnoreCase("VID")) {
                //status = context.getResources().getString(R.string.verify_id_new);
                doc = context.getResources().getString(R.string.verify_id_new);
            } else if (dashItem.getTranType().equalsIgnoreCase("LAD")) {
                doc = context.getResources().getString(R.string.lock_a_doc);
            } else {
                //status = context.getResources().getString(R.string.veri_lock);
                doc = context.getResources().getString(R.string.veri_lock);
            }
            String status = dashItem.getStatus();
            //doc = dashItem.getTranType();
            transactionId = dashItem.getTranid();
            if (dashItem.getTranType().equalsIgnoreCase("VID")) {
                if (dashItem.getSigner() != null
                        && dashItem.getSigner().size() > 0
                        && dashItem.getSigner().get(0) != null
                        && dashItem.getSigner().get(0).getPhoto() != null
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("null")
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("")) {

                    Utils.loadImage((Activity) context, dashItem.getSigner().get(0).getPhoto(),
                            holder.imgPro, holder.homeprogress);
                } else {
                    //if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    //holder.imgPro.setImageResource(R.drawable.ic_document);
                    //holder.homeprogress.setVisibility(View.GONE);
                    Utils.loadImageDashboard((Activity) context,
                            "",
                            holder.imgPro,
                            holder.homeprogress);
                    //}
                }
            }else if (dashItem.getTranType().equalsIgnoreCase("LAD")) {
                if (dashItem.getSigner() != null
                        && dashItem.getSigner().size() > 0
                        && dashItem.getSigner().get(0) != null
                        && dashItem.getSigner().get(0).getPhoto() != null
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("null")
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("")) {

                    Utils.loadImageDashStarred((Activity) context, dashItem.getSigner().get(0).getPhoto(),doc,
                            holder.imgPro, holder.homeprogress);
                } else {
                    //if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    //holder.imgPro.setImageResource(R.drawable.ic_document);
                    //holder.homeprogress.setVisibility(View.GONE);
                    Utils.loadImageDashboardLAD((Activity) context,
                            "",
                            holder.imgPro,
                            holder.homeprogress);
                    //}
                }
            } else {
                if (dashItem.getSigner() != null
                        && dashItem.getSigner().size() > 0
                        && dashItem.getSigner().get(0) != null
                        && dashItem.getSigner().get(0).getPhoto() != null
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("null")
                        && !dashItem.getSigner().get(0).getPhoto().equalsIgnoreCase("")) {

                    Utils.loadImageDashStarred((Activity) context, dashItem.getSigner().get(0).getPhoto(),doc,
                            holder.imgPro, holder.homeprogress);
                } else {
                    Utils.loadImageDashboardVAU((Activity) context,
                            "",
                            holder.imgPro,
                            holder.homeprogress);

                }
            }

            star = dashItem.getStar();
            holder.setDate(dojDate);
            holder.setTitle(doc);
            if(star)
            {
                holder.imgStar.setImageResource(R.drawable.filled_star);
            }

            String fName = "";
            try {
                if (dashItem.getSigner() != null
                        && dashItem.getSigner().size() > 0
                        && dashItem.getSigner().get(0) != null
                        && !dashItem.getSigner().get(0).getFirstName().equalsIgnoreCase("")) {
                    fName = dashItem.getSigner().get(0).getFirstName().toLowerCase();
                }
            } catch (Exception e) {

            }
            fName = Utils.capitalizeFirst(fName);

            holder.setSignerName(fName);

            if (dashItem.getSeal() != null &&
                    !dashItem.getSeal().getSealCode().equalsIgnoreCase("")) {
                holder.textPipe.setVisibility(View.VISIBLE);
                holder.setSealCode(dashItem.getSeal().getSealCode());
            } else {

                if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    holder.textPipe.setVisibility(View.VISIBLE);
                    holder.setSealCode(dashItem.getDocType());
                } else {
                    holder.setSealCode("");
                    holder.textPipe.setVisibility(View.GONE);
                }

            }

            String state = dashItem.getDocType();
            if (state == null) {
                state = " # ";
            }
            holder.setDoc(state);
            holder.setTran(status);
            try {
                if (dashItem.getSigner().size() > 1) {
                    holder.signerCount.setVisibility(View.VISIBLE);
                    holder.signerCount.setText("+" + (dashItem.getSigner().size() - 1));
                } else {
                    holder.signerCount.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.signerCount.setVisibility(View.GONE);
            }


            holder.imgStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Body dashItem = list.get(position);
                    transactionId = dashItem.getTranid();
                    star =dashItem.getStar();
                    setStar();
                }
            });


            if (status.equalsIgnoreCase("Pending")) {
                holder.statusLayout.setBackground(context.getResources().getDrawable(R.drawable.pending_bg));
                holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.pending_bg));
                holder.imagestatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
            } else {
                holder.statusLayout.setBackground(context.getResources().getDrawable(R.drawable.done_bg));
                holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.done_bg));
                holder.imagestatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));

            }

            holder.conLayout.setOnClickListener(view -> {

                loadingCompletedInterface.loadingCompleted(false);

                if (status.equalsIgnoreCase("Pending")) {
                    transactionId = list.get(position).getTranid();
                    getPendingData(transactionId);
                } else {
                    transactionId = list.get(position).getTranid();
                    getCompletedData(transactionId, status);

//                if (list.get(position).getTranType()
//                        .equalsIgnoreCase(context.getResources().getString(R.string.verify_id_new))) {
//
//                    transactionId = list.get(position).getTranid();
//                    getCompletedData(transactionId, list.get(position).getTranType());
//                } else {
//
//                    transactionId = list.get(position).getTranid();
//                    getCompletedData(transactionId, list.get(position).getTranType());
//                }
                }

            });


            holder.profileImageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!dashItem.getTranType().equalsIgnoreCase("VID")) {
                        transactionId = list.get(position).getTranid();
                        getSignerDetails(transactionId);
                    }
                }
            });


            if (position == list.size()) {
                loadingCompletedInterface.loadingCompleted(true);
            }
        } catch (Exception e) {
            //Intent intent = Intent(activity.getApplicationContext(), DashBoardActivity.class)
            Log.v("test_item_Adapter", e.toString());
        }
    }

    private void completedDialog(int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = dialog.findViewById(R.id.alertMess);
        text.setText("Do you want to share the details");
        Button dialogButton = dialog.findViewById(R.id.btnNo);
        dialogButton.setText("CANCEL");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnYes);
        dialogAllowButton.setText("OK");
        dialogAllowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DeleteImages().execute();
                dialog.dismiss();
                transactionId = list.get(position).getTranid();
                getShareDetails(transactionId);
            }
        });
        dialog.show();
    }

    private void getShareDetails(String tranId) {
        // CustomDialog.showProgressDialog(context);
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + tranId;
            
            getApiRequest.request(context, iJsonListener, url, "share");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void getSignerDetails(String tranId) {

        IJsonListener shareJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {

                    List<SignerReg> signerList = new ArrayList<>();
                    List<LADParties> ladPartiesList = new ArrayList<>();

                    if (data != null) {

                        if (type.equals("signer")) {

                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            String city, state, zip, add1, add2, tranType;
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.getString("id");
                            tranType = signer_obj.getString("tranType");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();


                            if (signer_obj.has("signer")) {
                                Object signersItem = signer_obj.get("signer");
                                if (!signersItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray signerArray = (JSONArray) signersItem;
                                    // do all kinds of JSONArray'ish things with urlArray

                                    for (int i = 0; i < signerArray.length(); i++) {
                                        JSONObject signer = signerArray.getJSONObject(i);

                                        fName = signer.getString("first_name");
                                        lName = signer.getString("last_name");
                                        email = signer.getString("signer");
//                                        phone = signer.getString("phone");

                                        String formatPhone = signer.getString("phone");
                                        int phoneLength = signer.getString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")) {
                                            if (formatPhone.substring(0, 3).equals("+91")) {
                                                phone = formatPhone.substring(3, phoneLength);
                                            }
                                            if (formatPhone.substring(0, 2).equals("+1")) {
                                                phone = formatPhone.substring(2, phoneLength);
                                            }
                                        } else {
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");


                                        scanRef = signer.getString("scan_reference");
                                        profileImgPath = signer.getString("photo");
                                        city = signer.getString("city");
                                        state = signer.getString("state");
                                        zip = signer.getString("zip");
                                        add1 = signer.getString("address1");
                                        add2 = signer.getString("address2");
                                        /*signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false, city, state, zip, add1, add2);
                                        signerList.add(signerReg);*/
                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                tranType, false, city, state, zip, add1, add2);
                                        signerList.add(signerReg);

                                        ladParties = new LADParties(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false, city, state, zip, add1, add2);
                                        ladPartiesList.add(ladParties);

                                    }

                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(true);
                                    dialog.setContentView(R.layout.dashboard_popup);

                                    TextView text = (TextView) dialog.findViewById(R.id.textHead);
                                    if (tranType.equalsIgnoreCase("LAD")) {
                                        text.setText("PARTIES");
                                    } else {
                                        text.setText("Signers");
                                    }

                                    RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

                                    SignerProfileAdapter stepsAdapter = new SignerProfileAdapter(signerList, context);
                                    GridLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(stepsAdapter);

                                    dialog.show();
                                }
                            }

                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
            }

            @Override
            public void onFetchStart() {

            }
        };


        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + tranId;
            
            getApiRequest.request(context, shareJsonListener, url, "signer");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void getPendingData(String tranId) {
        // CustomDialog.showProgressDialog(context);
        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + tranId;
            
            getApiRequest.request(context, iJsonListener, url, "pendingTrans");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    List<LADParties> ladPartiesList;

    private void getCompletedData(String tranId, String tranType) {

        IJsonListener iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {

                    addDocArrayList = new ArrayList<DocumentsModel>();

                    sArrayList = new ArrayList<SignerReg>();
                    wArrayList = new ArrayList<WitnessReg>();
                    userLocation = new UserLocation();

                    ladPartiesList = new ArrayList<>();

                    signerDocTypes = new ArrayList<SignerDocType>();

                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("pendingTrans")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType = "",
                                    docName = "", apnNum, serverDocName, profileImgPath, tranType = "", witProImgPath = "";

                            String Scity = "", Sstate = "", Szip = "", Saddressline1 = "", Saddressline2 = "", signerType = "";

                            String tranid_varified = "", signertype = "", email_varified = "", verifytype = "";

                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.getString("id");
                            level = signer_obj.getString("level");
                            tranType = signer_obj.getString("tranType");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();

                            tranType = signer_obj.getString("tranType");
                            if (tranType.equalsIgnoreCase("VID")) {

                                if (signer_obj.has("docType"))
                                    docType = signer_obj.getString("docType");

                                if (signer_obj.has("signer")) {
                                    Object signersItem = signer_obj.get("signer");
                                    if (!signersItem.toString().equalsIgnoreCase("null")) {

                                        JSONArray signerArray = (JSONArray) signersItem;

                                        for (int i = 0; i < signerArray.length(); i++) {
                                            JSONObject signer = signerArray.getJSONObject(i);

                                            fName = signer.getString("first_name");
                                            lName = signer.getString("last_name");
                                            email = signer.getString("signer");
                                            phone = signer.getString("phone");
                                            scanRef = signer.getString("scan_reference");

                                            Saddressline1 = signer.getString("address1");
                                            Saddressline2 = signer.getString("address2");
                                            Scity = signer.getString("city");
                                            Sstate = signer.getString("state");
                                            Szip = signer.getString("zip");
                                            profileImgPath = signer.getString("photo");
                                            int phoneLength = phone.length();

                                            String phoneFormatted = "";
                                            // String formatPhone = json_inside.getString("phone");
                                            if (phone.substring(0, 1).equals("+")) {
                                                if (phone.substring(0, 3).equals("+91")) {
                                                    phoneFormatted = phone.substring(3, phoneLength);
                                                }
                                                if (phone.substring(0, 2).equals("+1")) {
                                                    phoneFormatted = phone.substring(2, phoneLength);
                                                }
                                            } else {
                                                phoneFormatted = phone;
                                            }
                                            phoneFormatted = phoneFormatted.replace(" ", "");


                                            signerReg = new SignerReg(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                    docType, false, Scity, Sstate, Szip, Saddressline1, Saddressline2);

                                            sArrayList.add(signerReg);

                                            ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                    docType, false, Scity, Sstate, Szip, Saddressline1, Saddressline2);
                                            ladPartiesList.add(ladParties);
                                        }
                                        new SaveSigner().execute();

                                    }
                                }
//                                // Sourav 20200921
//                                if(signer_obj.has("signerdoctype")) {
//                                    Object signersItem = signer_obj.get("signerdoctype");
//                                    if (!signersItem.toString().equalsIgnoreCase("null")) {
//                                        JSONArray signerArray = (JSONArray) signersItem;
//                                        for (int i = 0; i < signerArray.length(); i++) {
//                                            SignerDocType signerDocType = new SignerDocType();
//                                            JSONObject signer = signerArray.getJSONObject(i);
//
//                                            if(signer != null && signer.getString("tranid") != null) {
//                                                signerDocType.setTranid(signer.getString("tranid"));
//                                            }
//                                            if(signer != null && signer.getString("signertype") != null) {
//                                                signerDocType.setSignertype(signer.getString("signertype"));
//                                            }
//                                            if(signer != null && signer.getString("email") != null) {
//                                                signerDocType.setEmail(signer.getString("email"));
//                                            }
//                                            if(signer != null && signer.getString("verifytype") != null) {
//                                                signerDocType.setVerifytype(signer.getString("verifytype"));
//                                            }
//
//                                            signerDocTypes.add(signerDocType);
//                                        }
//
//                                    }
//                                }

                            } else {
                                if (signer_obj.has("signer")) {
                                    Object signersItem = signer_obj.get("signer");
                                    if (!signersItem.toString().equalsIgnoreCase("null")) {

                                        JSONArray signerArray = (JSONArray) signersItem;
                                        // do all kinds of JSONArray'ish things with urlArray

                                        for (int i = 0; i < signerArray.length(); i++) {
                                            JSONObject signer = signerArray.getJSONObject(i);

                                            fName = signer.getString("first_name");
                                            lName = signer.getString("last_name");
                                            email = signer.getString("signer");
                                            phone = signer.getString("phone");
                                            scanRef = signer.getString("scan_reference");

                                            Saddressline1 = signer.getString("address1");
                                            Saddressline2 = signer.getString("address2");
                                            Scity = signer.getString("city");
                                            Sstate = signer.getString("state");
                                            Szip = signer.getString("zip");
                                            signerType = signer.getString("userCat");

                                            profileImgPath = signer.getString("photo");
                                            String phoneFormatted = "";
                                            int phoneLength = phone.length();

                                            // String formatPhone = json_inside.getString("phone");
                                            if (phone.substring(0, 1).equals("+")) {
                                                if (phone.substring(0, 3).equals("+91")) {
                                                    phoneFormatted = phone.substring(3, phoneLength);
                                                }
                                                if (phone.substring(0, 2).equals("+1")) {
                                                    phoneFormatted = phone.substring(2, phoneLength);
                                                }
                                            } else {
                                                phoneFormatted = phone;
                                            }
                                            phoneFormatted = phoneFormatted.replace(" ", "");


                                            boolean witness = false;
                                            if (signerType.equals("WIT")) {
                                                witness = true;
                                            }
                                            /* else {
                                                witness = true;
                                            }*/

                                            signerReg = new SignerReg(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                    signerType, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2);

                                            sArrayList.add(signerReg);

                                            ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                    docType, false, Scity, Sstate, Szip, Saddressline1, Saddressline2);
                                            ladPartiesList.add(ladParties);
                                        }
                                        new SaveSigner().execute();

                                    }
                                }

                                // Sourav 20200921
                                if (signer_obj.has("signerdoctype")) {
                                    Object signersItem = signer_obj.get("signerdoctype");
                                    if (!signersItem.toString().equalsIgnoreCase("null")) {
                                        JSONArray signerArray = (JSONArray) signersItem;
                                        for (int i = 0; i < signerArray.length(); i++) {
                                            SignerDocType signerDocType = new SignerDocType();
                                            JSONObject signer = signerArray.getJSONObject(i);

                                            if (signer != null && signer.getString("tranid") != null) {
                                                signerDocType.setTranid(signer.getString("tranid"));
                                            }
                                            if (signer != null && signer.getString("signertype") != null) {
                                                signerDocType.setSignertype(signer.getString("signertype"));
                                            }
                                            if (signer != null && signer.getString("email") != null) {
                                                signerDocType.setEmail(signer.getString("email"));
                                            }
                                            if (signer != null && signer.getString("verifytype") != null) {
                                                signerDocType.setVerifytype(signer.getString("verifytype"));
                                            }
                                            signerDocTypes.add(signerDocType);
                                            Utils.setSignerDocTypes(signerDocTypes);
                                        }

                                    }
                                }

                                int lev = Integer.parseInt(level);
                                if (lev > 3) {
                                    new SaveSignDoc().execute();
                                }

                                if (signer_obj.has("witness")) {
                                    Object witnessItem = signer_obj.get("witness");
                                    if (!witnessItem.toString().equalsIgnoreCase("null")) {

                                        JSONArray witnessArray = (JSONArray) witnessItem;

                                        for (int i = 0; i < witnessArray.length(); i++) {
                                            JSONObject witness_obj = witnessArray.getJSONObject(i);

                                            fName = witness_obj.getString("first_name");
                                            lName = witness_obj.getString("last_name");
                                            email = witness_obj.getString("witness");
                                            signerEmail = witness_obj.getString("signer");
                                            phone = witness_obj.getString("phone");
                                            scanRef = witness_obj.getString("scan_reference");

                                            Saddressline1 = witness_obj.getString("address1");
                                            Saddressline2 = witness_obj.getString("address2");
                                            Scity = witness_obj.getString("city");
                                            Sstate = witness_obj.getString("state");
                                            Szip = witness_obj.getString("zip");
                                            witProImgPath = witness_obj.getString("photo");
                                            String phoneFormatted = "";
                                            // String formatPhone = json_inside.getString("phone");
                                            int phoneLength = phone.length();
                                            if (phone.substring(0, 1).equals("+")) {
                                                if (phone.substring(0, 3).equals("+91")) {
                                                    phoneFormatted = phone.substring(3, phoneLength);
                                                }
                                                if (phone.substring(0, 2).equals("+1")) {
                                                    phoneFormatted = phone.substring(2, phoneLength);
                                                }
                                            } else {
                                                phoneFormatted = phone;
                                            }

                                            phoneFormatted = phoneFormatted.replace(" ", "");

                                            //   Toast.makeText(context,"Test   "+witness_obj.getString("first_name")+witness_obj.getString("photo")+" " +email,Toast.LENGTH_LONG).show();


                                            witnessReg = new WitnessReg(fName, lName, email, phoneFormatted, scanRef, signerEmail, witProImgPath, Scity, Sstate, Szip, Saddressline1, Saddressline2);

                                            //  new SaveWitness().execute();
                                            wArrayList.add(witnessReg);
                                        }
                                        new SaveWitness().execute();

                                    }
                                }

                                if (signer_obj.has("notaDoc")) {
                                    Object notaDocItem = signer_obj.get("notaDoc");
                                    if (!notaDocItem.toString().equalsIgnoreCase("null")) {
                                        JSONArray notaDocArray = (JSONArray) notaDocItem;

                                        for (int i = 0; i < notaDocArray.length(); i++) {
                                            JSONObject docsObj = notaDocArray.getJSONObject(i);

                                            if (docsObj.has("docuName")) {
//                                                if (!docName.equalsIgnoreCase(docsObj.getString("docname"))) {
                                                String docuName = docsObj.optString("docuName");
                                                String url = docsObj.optString("url");

                                                docName = docsObj.optString("docname");

                                                addDocArrayList.add(new DocumentsModel(docuName, url, docName));
//                                                }

                                            }
                                        }

                                        new PendingImages().execute();

                                    }
                                }

                                if (signer_obj.has("seal")) {
                                    Object seal = signer_obj.get("seal");
                                    if (!seal.toString().equalsIgnoreCase("null")) {
                                        JSONObject sealObj = signer_obj.getJSONObject("seal");
                                        if (!sealObj.toString().equalsIgnoreCase("null")) {

                                            String licenseNo = sealObj.optString("license");
                                            String selectedStamp = sealObj.optString("sealUrl");
                                            String sealCode = sealObj.optString("sealCode");

                                            sealAdded = new SealAdded();

                                            sealAdded.setLicenseNum(licenseNo);
                                            sealAdded.setSealCode(sealCode);
                                            sealAdded.setSealUrl(selectedStamp);

                                            new UpdateSealStatus().execute();
                                        }
                                    }
                                }

                                if (signer_obj.has("location")) {
                                    if (!signer_obj.get("location").toString().equalsIgnoreCase("null")) {
                                        JSONObject locObj = signer_obj.getJSONObject("location");
                                        if (!locObj.toString().equalsIgnoreCase("null")) {
                                            String street = locObj.optString("street");
                                            String city = locObj.optString("city");
                                            String state = locObj.optString("state");
                                            String zip = locObj.optString("zip");

                                            userLocation.setStreetName(street);
                                            userLocation.setCityName(city);
                                            userLocation.setStateName(state);
                                            userLocation.setPinCode(zip);

                                            new SaveLocation().execute();
                                        }
                                    }
                                }

                            }

                            if (tranType.equalsIgnoreCase("VID")) {
                                Intent intent = new Intent(context, CompletedValidateProfile.class);
                                intent.putExtra("transactionId", transactionId);
                                context.startActivity(intent);
                            } else if (tranType.equalsIgnoreCase("LAD")) {
                                Intent intent = new Intent(context, LADStepsActivity.class);
                                intent.putExtra("transactionId", transactionId);
                                intent.putExtra("From", "Completed");
                                context.startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, DoneNotarizeActivity.class);
                                intent.putExtra("transactionId", transactionId);
                                context.startActivity(intent);
                            }

                            loadingCompletedInterface.loadingCompleted(true);
                        }
                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
            }

            @Override
            public void onFetchStart() {

            }
        };


        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + tranId;
            
            getApiRequest.request(context, iJsonListener, url, "pendingTrans");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void setStar() {
        // CustomDialog.showProgressDialog(context);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("tranid", transactionId);
                if(!star){
                    params.put("star", true);
                }
                else {
                    params.put("star", false);
                }
                //params.put("star", true);


            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(context, iJsonListener, params, AppUrl.STAR_TRANS, "star");
            //
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

                    addDocArrayList = new ArrayList<DocumentsModel>();
                    sArrayList = new ArrayList<SignerReg>();
                    wArrayList = new ArrayList<WitnessReg>();
                    userLocation = new UserLocation();
                    ladPartiesList = new ArrayList<>();

                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {
                        if (type.equals("star")) {
                            String success = data.optString("success");
                            if (success.equals("1")) {
                                new UpdateLocal().execute();
                            }
                        } else if (type.equals("pendingTrans")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone, scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum, serverDocName, profileImgPath, witProImgPath = "";

                            String Scity = "", Sstate = "", Szip = "", Saddressline1 = "", Saddressline2 = "", signerCat = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");
                            level = signer_obj.optString("level");
                            tranType = signer_obj.optString("tranType");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();

                            //noSigner = signer_obj.optString("signer");
//                            Toast.makeText(context, level, Toast.LENGTH_LONG).show();
//                            //Log.e("Notarise", level);

                            if (signer_obj.has("signer")) {
                                Object signersItem = signer_obj.get("signer");
                                boolean witness = false;
                                if (!signersItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray signerArray = (JSONArray) signersItem;
                                    // do all kinds of JSONArray'ish things with urlArray

                                    for (int i = 0; i < signerArray.length(); i++) {

                                        JSONObject signer = signerArray.getJSONObject(i);

                                        fName = signer.optString("first_name");
                                        lName = signer.optString("last_name");
                                        email = signer.optString("signer");
                                        phone = signer.optString("phone");
                                        scanRef = signer.optString("scan_reference");

                                        Saddressline1 = signer.optString("address1");
                                        Saddressline2 = signer.optString("address2");
                                        Scity = signer.optString("city");
                                        Sstate = signer.optString("state");
                                        Szip = signer.optString("zip");
                                        signerCat = signer.optString("userCat");
                                        profileImgPath = signer.optString("photo");

                                        if (signerCat.equalsIgnoreCase("WIT")) {
                                            witness = true;
                                        } else {
                                            witness = false;
                                        }
                                        String phoneFormatted = "";
                                        // String formatPhone = json_inside.optString("phone");
                                        int phoneLength = phone.length();
                                        if (phone.substring(0, 1).equals("+")) {
                                            if (phone.substring(0, 3).equals("+91")) {
                                                phoneFormatted = phone.substring(3, phoneLength);
                                            }
                                            if (phone.substring(0, 2).equals("+1")) {
                                                phoneFormatted = phone.substring(2, phoneLength);
                                            }
                                        } else {
                                            phoneFormatted = phone;
                                        }

                                        phoneFormatted = phoneFormatted.replace(" ", "");
                                        signerReg = new SignerReg(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                signerCat, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2);

                                        sArrayList.add(signerReg);
                                        if (tranType.equalsIgnoreCase("LAD")) {
                                            ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                    signerCat, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2);
                                            ladPartiesList.add(ladParties);
                                        }
                                    }
                                    new SaveSigner().execute();

                                }
                            }

                            int lev = Integer.parseInt(level);
                            if (lev > 3) {
                                new SaveSignDoc().execute();
                            }

                            if (signer_obj.has("witness")) {
                                Object witnessItem = signer_obj.get("witness");
                                if (!witnessItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray witnessArray = (JSONArray) witnessItem;

                                    for (int i = 0; i < witnessArray.length(); i++) {
                                        JSONObject witness_obj = witnessArray.getJSONObject(i);

                                        fName = witness_obj.optString("first_name");
                                        lName = witness_obj.optString("last_name");
                                        email = witness_obj.optString("witness");
                                        signerEmail = witness_obj.optString("signer");
                                        phone = witness_obj.optString("phone");
                                        scanRef = witness_obj.optString("scan_reference");

                                        profileImgPath = witness_obj.optString("photo");
                                        
                                        Saddressline1 = witness_obj.optString("address1");
                                        Saddressline2 = witness_obj.optString("address2");
                                        Scity = witness_obj.optString("city");
                                        Sstate = witness_obj.optString("state");
                                        Szip = witness_obj.optString("zip");

                                        String phoneFormatted = "";
                                        // String formatPhone = json_inside.optString("phone");

                                        int phoneLength = phone.length();
                                        if (phone.substring(0, 1).equals("+")) {
                                            if (phone.substring(0, 3).equals("+91")) {
                                                phoneFormatted = phone.substring(3, phoneLength);
                                            }
                                            if (phone.substring(0, 2).equals("+1")) {
                                                phoneFormatted = phone.substring(2, phoneLength);
                                            }
                                        } else {
                                            phoneFormatted = phone;
                                        }

                                        phoneFormatted = phoneFormatted.replace(" ", "");


                                        //   Toast.makeText(context,witness_obj.optString("first_name") +"\n"+witness_obj.optString("photo")+" " +email,Toast.LENGTH_LONG).show();


                                        witnessReg = new WitnessReg(fName, lName, email, phoneFormatted, scanRef, signerEmail, profileImgPath, Scity, Sstate, Szip, Saddressline1, Saddressline2);

                                        //  new SaveWitness().execute();
                                        wArrayList.add(witnessReg);
                                    }
                                    new SaveWitness().execute();

                                }
                            }

                            if (signer_obj.has("notaDoc")) {
                                Object notaDocItem = signer_obj.get("notaDoc");
                                if (!notaDocItem.toString().equalsIgnoreCase("null")) {
                                    JSONArray notaDocArray = (JSONArray) notaDocItem;

                                    for (int i = 0; i < notaDocArray.length(); i++) {
                                        JSONObject docsObj = notaDocArray.getJSONObject(i);

                                        if (docsObj.has("docuName")) {
                                            String docuName = docsObj.optString("docuName");
                                            String url = docsObj.optString("url");
                                            docName = docsObj.optString("docname");

                                            addDocArrayList.add(new DocumentsModel(docuName, url, docName));

                                        }
                                    }

                                    new PendingImages().execute();

                                }
                            }

                            if (signer_obj.has("seal")) {
                                Object seal = signer_obj.get("seal");
                                if (!seal.toString().equalsIgnoreCase("null")) {
                                    JSONObject sealObj = signer_obj.getJSONObject("seal");
                                    if (!sealObj.toString().equalsIgnoreCase("null")) {

                                        String licenseNo = sealObj.optString("license");
                                        String selectedStamp = sealObj.optString("sealUrl");
                                        String sealCode = sealObj.optString("sealCode");

                                        sealAdded = new SealAdded();

                                        sealAdded.setLicenseNum(licenseNo);
                                        sealAdded.setSealCode(sealCode);
                                        sealAdded.setSealUrl(selectedStamp);

                                        new UpdateSealStatus().execute();
                                    }
                                }
                            }

                            if (signer_obj.has("location")) {
                                if (!signer_obj.get("location").toString().equalsIgnoreCase("null")) {
                                    JSONObject locObj = signer_obj.getJSONObject("location");
                                    if (!locObj.toString().equalsIgnoreCase("null")) {
                                        String street = locObj.optString("street");
                                        String city = locObj.optString("city");
                                        String state = locObj.optString("state");
                                        String zip = locObj.optString("zip");

                                        userLocation.setStreetName(street);
                                        userLocation.setCityName(city);
                                        userLocation.setStateName(state);
                                        userLocation.setPinCode(zip);

                                        new SaveLocation().execute();
                                    }
                                }
                            }
                            if (tranType.equalsIgnoreCase("LAD")) {
                                Intent intent = new Intent(context, LADStepsActivity.class);
                                intent.putExtra("From", "Pending");
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            } else {
                                Intent intent = new Intent(context, NotarizeStepsActivity.class);
                                intent.putExtra("From", "Pending");
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        } else if (type.equals("share")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");
                            level = signer_obj.optString("level");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();

                            //noSigner = signer_obj.optString("signer");
//                            Toast.makeText(context, level, Toast.LENGTH_LONG).show();
//                            //Log.e("Notarise", level);


                            if (signer_obj.has("signer")) {
                                Object signersItem = signer_obj.get("signer");
                                if (!signersItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray signerArray = (JSONArray) signersItem;
                                    // do all kinds of JSONArray'ish things with urlArray

                                    for (int i = 0; i < signerArray.length(); i++) {
                                        JSONObject signer = signerArray.getJSONObject(i);

                                        fName = signer.optString("first_name");
                                        lName = signer.optString("last_name");
                                        email = signer.optString("signer");
                                        phone = signer.optString("phone");
                                        scanRef = signer.optString("scan_reference");
                                        profileImgPath = signer.optString("photo");

                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false);
                                        sArrayList.add(signerReg);
                                        if (tranType.equalsIgnoreCase("LAD")) {
                                            ladParties = new LADParties(fName, lName, email, phone, scanRef, profileImgPath,
                                                    "signetGovt", false);
                                            ladPartiesList.add(ladParties);
                                        }
                                    }
                                    new SaveSigner().execute();

                                }
                            }
                            if (signer_obj.has("witness")) {
                                Object witnessItem = signer_obj.get("witness");
                                if (!witnessItem.toString().equalsIgnoreCase("null")) {

                                    JSONArray witnessArray = (JSONArray) witnessItem;

                                    for (int i = 0; i < witnessArray.length(); i++) {
                                        JSONObject witness_obj = witnessArray.getJSONObject(i);

                                        fName = witness_obj.optString("first_name");
                                        lName = witness_obj.optString("last_name");
                                        email = witness_obj.optString("witness");
                                        signerEmail = witness_obj.optString("signer");
//                                        phone = witness_obj.optString("phone");

                                        String formatPhone = witness_obj.optString("phone");
                                        int phoneLength = witness_obj.optString("phone").length();
                                        if (formatPhone.substring(0, 1).equals("+")) {
                                            if (formatPhone.substring(0, 3).equals("+91")) {
                                                phone = formatPhone.substring(3, phoneLength);
                                            }
                                            if (formatPhone.substring(0, 2).equals("+1")) {
                                                phone = formatPhone.substring(2, phoneLength);
                                            }
                                        } else {
                                            phone = formatPhone;
                                        }
                                        phone = phone.replace(" ", "");


                                        scanRef = witness_obj.optString("scan_reference");
                                        witnessReg = new WitnessReg(fName, lName, email, phone, scanRef, signerEmail, witProImgPath);
                                        //  new SaveWitness().execute();
                                        wArrayList.add(witnessReg);
                                    }
                                    new SaveWitness().execute();

                                }
                            }

                            if (signer_obj.has("notaDoc")) {
                                Object notaDocItem = signer_obj.get("notaDoc");
                                if (!notaDocItem.toString().equalsIgnoreCase("null")) {
                                    JSONArray notaDocArray = (JSONArray) notaDocItem;

                                    for (int i = 0; i < notaDocArray.length(); i++) {
                                        JSONObject docsObj = notaDocArray.getJSONObject(i);

                                        if (docsObj.has("docuName")) {
//                                            if (!docName.equalsIgnoreCase(docsObj.optString("docname"))) {
                                            String docuName = docsObj.optString("docuName");
                                            String url = docsObj.optString("url");

                                            docName = docsObj.optString("docname");

                                            addDocArrayList.add(new DocumentsModel(docuName, url, docName));
//                                            }

                                        }
                                    }
                                    new SaveImages().execute();

                                }
                            }

                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    // CustomDialog.notaryappDialogSingle(VerifyBase_SelectIdentityFragment.this(), errorMess);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
            }

            @Override
            public void onFetchStart() {

            }
        };
    }

    public class MyView extends RecyclerView.ViewHolder {

        private TextView signerName, sealCode, nameText, dateText, docName, tranComp, txtPending, signerCount, textPipe;
        private ImageView imgStar, imagestatus;
        private RoundedImageView imgPro;
        private ConstraintLayout conLayout, profileImageLayout;
        private LinearLayout statusLayout;
        private ProgressBar homeprogress;

        private MyView(View view) {
            super(view);
            statusLayout = view.findViewById(R.id.statusLayout);
            sealCode = view.findViewById(R.id.sealText);
            signerName = view.findViewById(R.id.textSigner);
            conLayout = view.findViewById(R.id.conLayout);
            dateText = view.findViewById(R.id.dateText);
            nameText = view.findViewById(R.id.nameText);
            docName = view.findViewById(R.id.textDoc);
            tranComp = view.findViewById(R.id.textComp);
            imgStar = view.findViewById(R.id.imageStar);
            imgPro = view.findViewById(R.id.stampImage);
            imagestatus = view.findViewById(R.id.imagestatus);
            txtPending = view.findViewById(R.id.textComp);
            signerCount = view.findViewById(R.id.signerCount);
            profileImageLayout = view.findViewById(R.id.profileImageLayout);
            homeprogress = view.findViewById(R.id.homeprogress);

            textPipe = view.findViewById(R.id.textPipe);

        }


        public void setDate(String date) {
            dateText.setText(date);
        }

        public void setTitle(String name) {
            nameText.setText(name);
        }

        public void setDoc(String name) {
            docName.setText(name);
        }

        public void setTran(String comp) {
            tranComp.setText(comp);
        }

        public void setSignerName(String sName) {
            signerName.setText(sName);
        }

        public void setSealCode(String sCode) {
            sealCode.setText(sCode);
        }
    }

    class UpdateLocal extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            databaseClient.getAppDatabase().allTransactionsDao().updateStar(transactionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            context.startActivity(new Intent(context, DashBoardActivity.class));
            ((Activity) context).finish();
        }

    }

    class SaveSignDoc extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... voids) {
            String notaryEmail = databaseClient.getAppDatabase().userRegDao().getEmail();
            SignDocs signDocs = new SignDocs();
            signDocs.setEmail(notaryEmail);
            signDocs.setSuccess(true);
            databaseClient.getAppDatabase().signDocsDao().insert(signDocs);
            //  databaseClient.getAppDatabase().vaCheckDao().updateSignDoc();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
//            if (level.equals("4")) {
//                context.startActivity(new Intent(context, NotarizeStepsActivity.class));
//            }
            //getActivity().onBackPressed();
        }

    }

    class SaveWitness extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < wArrayList.size(); i++) {
                databaseClient.getAppDatabase()
                        .witnessRegDao()
                        .insert(wArrayList.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
      /*      picasso.load(downloadUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(witnessCount == 1) {
                        saveNewSelfie("witness1.png", bitmap);
                    }
                    if(witnessCount == 2) {
                        saveNewSelfie("witness2.png", bitmap);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });*/
            //getActivity().onBackPressed();
//            if (level.equals("3")) {
//                context.startActivity(new Intent(context, NotarizeStepsActivity.class));
//            }
//            if (level.equals("4")) {
//                new SaveSignDoc().execute();
//            }

        }
    }

    class SaveLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .userLocationDao()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .userLocationDao()
                    .insert(userLocation);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    class PendingImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            for (int i = 0; i < addDocArrayList.size(); i++) {

                databaseClient.getAppDatabase().documentsImageDao().insert(addDocArrayList.get(i));
            }


            //Saving the image name to database

            //selectedType =databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class SaveImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            for (int i = 0; i < addDocArrayList.size(); i++) {

                databaseClient.getAppDatabase().documentsImageDao().insert(addDocArrayList.get(i));
            }


            //Saving the image name to database

            //selectedType =databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            Intent intent = new Intent(context, ShareActivity.class);
            intent.putExtra("transactionId", transactionId);
            context.startActivity(intent);
        }

    }

    class UpdateSealStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().insert(sealAdded);

            //  databaseClient.getAppDatabase().vaCheckDao().updateAddSeal();
            return null;
        }

        @Override
        protected void onPostExecute(Void docs) {
            super.onPostExecute(docs);
            // Intent intent = new Intent(getActivity(), NotarizeStepsActivity.class);
            //  startActivity(intent);

        }
    }

    class SaveSigner extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .signerRegDao()
                    .deleteAll();

            for (int i = 0; i < sArrayList.size(); i++) {
                databaseClient.getAppDatabase()
                        .signerRegDao()
                        .insert(sArrayList.get(i));
            }

            if (ladPartiesList != null
                    && ladPartiesList.size() > 0) {
                databaseClient.getAppDatabase()
                        .ladPartiesDao()
                        .deleteAll();

                for (int i = 0; i < ladPartiesList.size(); i++) {
                    databaseClient.getAppDatabase()
                            .ladPartiesDao()
                            .insert(ladPartiesList.get(i));
                }

            }

            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
//            if(level.equals("2")) {
//                context.startActivity(new Intent(context, NotarizeStepsActivity.class));
//            }
            //getActivity().finish();
        }
    }

    class AddTranId extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().transactionsDao().insert(transactions);
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            //  Intent intent = new Intent(getActivity(), Notarize_AlertActivity.class);
            //   startActivity(intent);
            // getActivity().finish();
        }

    }

    class DeleteAll extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            //creating a task
            databaseClient.getAppDatabase().signerRegDao().deleteAll();
            databaseClient.getAppDatabase().signDocsDao().deleteAll();
            databaseClient.getAppDatabase().vaLicenseDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().documentsImageDao().deleteAll();
            databaseClient.getAppDatabase().userLocationDao().deleteAll();
            databaseClient.getAppDatabase().witnessRegDao().deleteAll();
            databaseClient.getAppDatabase().transactionsDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
            //  Intent intent = new Intent(getActivity(), Notarize_AlertActivity.class);
            //   startActivity(intent);
            // getActivity().finish();
        }

    }
}
