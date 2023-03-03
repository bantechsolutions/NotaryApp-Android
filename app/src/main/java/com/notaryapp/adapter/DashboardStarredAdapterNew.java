package com.notaryapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.ejournal.activities.VEJ_DoneNotarizeActivity;
import com.notaryapp.ejournal.activities.VEJ_NotarizeStepsActivity;
import com.notaryapp.interfacelisterners.LoadingCompletedInterface;
import com.notaryapp.lockadoc.activityes.LADShareActivity;
import com.notaryapp.lockadoc.activityes.LADStepsActivity;
import com.notaryapp.model.webresponse.Body;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.DocumentsModel;
import com.notaryapp.roomdb.entity.JournalFees;
import com.notaryapp.roomdb.entity.LADParties;
import com.notaryapp.roomdb.entity.SealAdded;
import com.notaryapp.roomdb.entity.SignDocs;
import com.notaryapp.roomdb.entity.SignerDocType;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.Transactions;
import com.notaryapp.roomdb.entity.UserLocation;
import com.notaryapp.roomdb.entity.UserNote;
import com.notaryapp.roomdb.entity.VACustomer;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.CompletedValidateProfile;
import com.notaryapp.ui.activities.DashBoardActivity;
import com.notaryapp.ui.activities.membership.MembershipActivity;
import com.notaryapp.ui.activities.verifyauthenticate.DoneNotarizeActivity;
import com.notaryapp.ui.activities.verifyauthenticate.NotarizeStepsActivity;
import com.notaryapp.ui.activities.verifyauthenticate.ShareActivity;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.GETAPIRequest;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardStarredAdapterNew extends RecyclerView.Adapter<DashboardStarredAdapterNew.MyView> {

    private List<Body> list;
    private Context context;
    private String date, dojDate, doc, transactionId;
    private DatabaseClient databaseClient;
    private IJsonListener iJsonListener;

    private Transactions transactions;
    private SignerReg signerReg;
    private WitnessReg witnessReg;
    private ArrayList<WitnessReg> wArrayList;
    private ArrayList<SignerReg> sArrayList;
    private ArrayList<DocumentsModel> addDocArrayList;
    private SealAdded sealAdded;
    private UserLocation userLocation;
    private JournalFees journalFees;
    private UserNote userNote;
    private LoadingCompletedInterface loadingCompletedInterface;
    private String level;

    private LADParties ladParties;
    private String tranType;
    private ArrayList<SignerDocType> signerDocTypes;

    private VACustomer memPlans;
    private int daysLeft;
    private int transCount;

    public DashboardStarredAdapterNew(LoadingCompletedInterface loadingCompletedInterface,
                                      List<Body> horizontalList,
                                      Context context) {
        this.list = horizontalList;
        this.context = context;
        databaseClient = DatabaseClient.getInstance(context);
        this.loadingCompletedInterface = loadingCompletedInterface;
        initIJsonListener();
        new SelectPlans().execute();
    }

    @Override
    public void onBindViewHolder(final MyView holder, @SuppressLint("RecyclerView") final int position) {

        try {
            Body allTransactions = list.get(position);
            date = allTransactions.getDoj();
            String dojDateArry[] = date.split("T");
            dojDate = dojDateArry[0];

            if (allTransactions.getTranType().equalsIgnoreCase("VID")) {
                doc = context.getResources().getString(R.string.verify_id_new);
            } else if(allTransactions.getTranType().equalsIgnoreCase("LAD")) {
                doc = context.getResources().getString(R.string.lock_a_doc);
            } else if(allTransactions.getTranType().equalsIgnoreCase("VEJ")) {
                doc = context.getResources().getString(R.string.ejournal);
            } else {
                doc = context.getResources().getString(R.string.veri_lock);
            }

            transactionId = allTransactions.getTranid();
            if (allTransactions.getSigner() != null
                    && allTransactions.getSigner().get(0) != null
                    && allTransactions.getSigner().get(0).getPhoto() != null
                    && !allTransactions.getSigner().get(0).getPhoto().equalsIgnoreCase("null")
                    && !allTransactions.getSigner().get(0).getPhoto().equalsIgnoreCase("")) {


                Utils.loadImageDashStarred((Activity) context,
                        allTransactions.getSigner().get(0).getPhoto(),
                        doc,
                        holder.imgPro,
                        holder.proImgProgress);
            } else {
                if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    holder.imgPro.setImageResource(R.drawable.ic_id_white);
                    try{
                        Utils.loadImageDashboard((Activity) context,
                                "",
                                holder.imgPro,
                                holder.proImgProgress);
                    }catch(Exception e){

                    }
                }

                if (doc.equals(context.getResources().getString(R.string.lock_a_doc))){
                    holder.imgPro.setImageResource(R.drawable.ic_id_white);

                    try{
                        Utils.loadImageDashboardLAD((Activity) context,
                                "",
                                holder.imgPro,
                                holder.proImgProgress);
                    }catch(Exception e){

                    }
                }
                if (doc.equals(context.getResources().getString(R.string.veri_lock))){
                    holder.imgPro.setImageResource(R.drawable.ic_id_white);

                    try{
                        Utils.loadImageDashboardVAU((Activity) context,
                                "",
                                holder.imgPro,
                                holder.proImgProgress);
                    }catch(Exception e){

                    }
                }

            }

            String fName = allTransactions.getSigner().get(0).getFirstName().toLowerCase();
            fName = Utils.capitalizeFirst(fName);

            holder.setSignerName(fName);

            if (allTransactions.getSeal() != null &&
                    !allTransactions.getSeal().getSealCode().equalsIgnoreCase("")) {
                holder.textPipe.setVisibility(View.VISIBLE);
                holder.setSealCode(allTransactions.getSeal().getSealCode());
            } else {
                if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    holder.textPipe.setVisibility(View.VISIBLE);
                    holder.setSealCode(allTransactions.getDocType());
                    holder.emailNumTxt.setText("0");
                    holder.emailNumTxtll.setVisibility(View.GONE);
                } else {
                    holder.emailNumTxtll.setVisibility(View.VISIBLE);
                    holder.setSealCode("");
                    holder.textPipe.setVisibility(View.GONE);
                    if(allTransactions.getDocsCount() != null
                       && allTransactions.getDocsCount() > 0) {
                        holder.emailNumTxt.setText(allTransactions.getDocsCount() + "");
                    }else {
                        holder.emailNumTxtll.setVisibility(View.GONE);
                    }
                }
            }
            try {
                if (doc.equals(context.getResources().getString(R.string.verify_id_new))) {
                    holder.emailNumTxt.setText("0");
                    holder.emailNumTxtll.setVisibility(View.GONE);
                } else {
                    holder.emailNumTxtll.setVisibility(View.VISIBLE);
                    if (allTransactions.getDocsCount() != null
                            && allTransactions.getDocsCount() > 0) {
                        holder.emailNumTxt.setText(allTransactions.getDocsCount() + "");
                    } else {
                        holder.emailNumTxtll.setVisibility(View.GONE);
                    }
                }
            }catch (Exception e){

            }

            holder.mailRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String doctype = list.get(position).getTranType();
                    try {
                        if (!doctype.equals("VID")) {
                            if (doctype.equals("LAD")) {
                                if (list.get(position).getStatus().equalsIgnoreCase("done")) {
                                    completedDialog(position);
                                }
                            } else if(doctype.equals("VEJ")){
                                if (list.get(position).getStatus().equalsIgnoreCase("done")) {
                                    completedDialog(position);
                                }
                            }
                            /*else {
                                completedDialog(position);
                            }*/
                            else {
                                if (doctype.equals("VAU")) {
                                    if (list.get(position).getStatus().equalsIgnoreCase("done")) {
                                        completedDialog(position);
                                    }
                                }
                            }
                        }
                    }catch (Exception e){

                    }
                }
            });

            try {
                if(!allTransactions.getTranType().equalsIgnoreCase("VID")) {
                    if (allTransactions.getSigner().size() > 0) {
                        holder.profileNumTxt.setVisibility(View.VISIBLE);
                        holder.profileNumTxt.setText(allTransactions.getSigner().size() + "");
                    } else {
                        holder.profileNumTxt.setVisibility(View.GONE);
                    }
                }else {
                    holder.profileNumTxt.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.profileNumTxt.setVisibility(View.GONE);
            }
            try {
                if(allTransactions.getSigner().size() > 0) {
                    holder.phoneNumtxt.setVisibility(View.VISIBLE);
                    holder.phoneNumtxt.setText(allTransactions.getSigner().size() + "");
                }else {
                    holder.phoneNumtxt.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                holder.phoneNumtxt.setVisibility(View.GONE);
            }
            try {
                if (allTransactions.getSigner().size() > 1) {
                    holder.signerCount.setVisibility(View.VISIBLE);
                    holder.signerCount.setText("+" + (allTransactions.getSigner().size() - 1));
                } else {
                    holder.signerCount.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }


            String status = allTransactions.getStatus();

            holder.setDate(dojDate);
//        holder.setTitle(doc);
            //holder.setDoc(allTransactions.getTranType());
            holder.setDoc(doc);
            holder.setTran(allTransactions.getStatus());

            if (status.equalsIgnoreCase("Pending")) {

                holder.signerCount.setBackground(context.getResources().getDrawable(R.drawable.pending_bg));

                holder.statusLayout.setBackground(context.getResources().getDrawable(R.drawable.pending_bg));
                holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.pending_bg));
                holder.imagestatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
            } else {
                holder.signerCount.setBackground(context.getResources().getDrawable(R.drawable.done_bg));

                holder.statusLayout.setBackground(context.getResources().getDrawable(R.drawable.done_bg));
                holder.profileImageLayout.setBackground(context.getResources().getDrawable(R.drawable.done_bg));
                holder.imagestatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done));

            }


            holder.imgStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Body dashItem = list.get(position);
                    transactionId = dashItem.getTranid();
                    setStar();
                }
            });


            holder.profileRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!allTransactions.getTranType().equalsIgnoreCase("VID")) {
                        transactionId = list.get(position).getTranid();
                        getSignerDetails(transactionId);
                    }

                }
            });

            holder.locationRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    transactionId = list.get(position).getTranid();
                    getLocation(transactionId);

                }
            });

            holder.phoneRound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    transactionId = list.get(position).getTranid();
                    getGetPhoneNumber(transactionId);

                }
            });

            holder.topContainer.setOnClickListener(view -> {

                loadingCompletedInterface.loadingCompleted(false);

                if (status.equalsIgnoreCase("Pending")) {
                    transactionId = list.get(position).getTranid();
                    if (daysLeft <= 0){
                        Intent i = new Intent(context, MembershipActivity.class);
                        //i.putExtra("from", "dash");
                        context.startActivity(i);
                        ///context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    } else {
                        getPendingData(transactionId);
                    }
                } else if(status.equalsIgnoreCase("Done")){
                    /*if (doc.equalsIgnoreCase(context.getResources().getString(R.string.verify_id_new))) {

                        transactionId = list.get(position).getTranid();
                        getCompletedData(transactionId, doc);
                    } else {

                        transactionId = list.get(position).getTranid();
                        getCompletedData(transactionId, doc);
                    }*/
                    transactionId = list.get(position).getTranid();
                    getCompletedData(transactionId, doc);
                }

            });
        } catch (
                Exception e) {

        }
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_starred_dashboard, parent, false);

        return new MyView(itemView);
    }

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
                    journalFees = new JournalFees();
                    userNote = new UserNote();

                    signerDocTypes = new ArrayList<SignerDocType>();

                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("pendingTrans")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone, scanRef, rowId, signerEmail, docType = "",
                                    docName = "", apnNum, serverDocName, profileImgPath, signatureImgPath, thumprintImgPath, tranType = "", witProImgPath = "";

                            String Scity = "", Sstate = "", Szip = "", Saddressline1 = "", Saddressline2 = "", signerType = "";

                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");
                            level = signer_obj.optString("level");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();

                            tranType = signer_obj.optString("tranType");
                            if (tranType.equalsIgnoreCase("VID")) {

                                if (signer_obj.has("docType"))
                                    docType = signer_obj.optString("docType");

                                if (signer_obj.has("signer")) {
                                    Object signersItem = signer_obj.get("signer");
                                    if (!signersItem.toString().equalsIgnoreCase("null")) {

                                        JSONArray signerArray = (JSONArray) signersItem;

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
                                            profileImgPath = signer.optString("photo");
                                            int phoneLength = phone.length();
                                            String phoneFormatted = "";
                                            // String formatPhone = json_inside.optString("phone");
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

                                            if (tranType.equalsIgnoreCase("LAD")) {
                                                ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                        docType, false, Scity, Sstate, Szip, Saddressline1, Saddressline2);
                                                ladPartiesList.add(ladParties);
                                            }
                                        }
                                        new SaveSigner().execute();

                                    }
                                }

                            } else if(tranType.equalsIgnoreCase("VEJ")){
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
                                            signatureImgPath = signer.getString("signature");
                                            thumprintImgPath = signer.getString("thumb");
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
                                                    signerType, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2, signatureImgPath, thumprintImgPath);

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
                                if (signer_obj.has("journalentry")) {
                                    if (!signer_obj.get("journalentry").toString().equalsIgnoreCase("null")) {
                                        JSONObject locObj = signer_obj.getJSONObject("journalentry");
                                        if (!locObj.toString().equalsIgnoreCase("null")) {
                                            String notaType = locObj.optString("jtype");
                                            String jAmount = locObj.optString("jamount");
                                            String isFeeCollected = locObj.optString("iscollected");

                                            journalFees.setNotarizationType(notaType);
                                            journalFees.setFeeAmount(jAmount);
                                            if (isFeeCollected.equalsIgnoreCase("true")){
                                                journalFees.setCollected(true);
                                            } else {
                                                journalFees.setCollected(false);
                                            }
                                            new SaveJournal().execute();
                                            //new SaveLocation().execute();
                                        }
                                    }
                                }

                                if (signer_obj.has("notes")) {
                                    if (!signer_obj.get("notes").toString().equalsIgnoreCase("null")) {
                                        JSONObject locObj = signer_obj.getJSONObject("notes");
                                        if (!locObj.toString().equalsIgnoreCase("null")) {
                                            String noteHead = locObj.optString("notehead");
                                            String noteSubdata = locObj.optString("notesubhead");

                                            userNote.setNoteHeading(noteHead);
                                            userNote.setNoteDetail(noteSubdata);

                                            new SaveNote().execute();
                                            //new SaveLocation().execute();
                                        }
                                    }
                                }

                            }else {


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

                                            Saddressline1 = signer.optString("address1");
                                            Saddressline2 = signer.optString("address2");
                                            Scity = signer.optString("city");
                                            Sstate = signer.optString("state");
                                            Szip = signer.optString("zip");
                                            signerType = signer.optString("userCat");

                                            profileImgPath = signer.optString("photo");
                                            String phoneFormatted = "";
                                            int phoneLength = phone.length();
                                            // String formatPhone = json_inside.optString("phone");
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
                                            if (tranType.equalsIgnoreCase("LAD")) {
                                                ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                        docType, false, Scity, Sstate, Szip, Saddressline1, Saddressline2);
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

                                            Saddressline1 = witness_obj.optString("address1");
                                            Saddressline2 = witness_obj.optString("address2");
                                            Scity = witness_obj.optString("city");
                                            Sstate = witness_obj.optString("state");
                                            Szip = witness_obj.optString("zip");
                                            witProImgPath = witness_obj.optString("photo");
                                            String phoneFormatted = "";
                                            int phoneLength = phone.length();
                                            http:
//54.166.156.19:4040/api/v1/notaryapp/registration/authenticate
                                            // String formatPhone = json_inside.optString("phone");
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

                                            //   Toast.makeText(context,"Test   "+witness_obj.optString("first_name")+witness_obj.optString("photo")+" " +email,Toast.LENGTH_LONG).show();


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
//                                                if (!docName.equalsIgnoreCase(docsObj.optString("docname"))) {
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
                            } else if (tranType.equalsIgnoreCase("VEJ")) {
                                Intent intent = new Intent(context, VEJ_DoneNotarizeActivity.class);
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

    private void getPendingData(String tranId) {
        pendingListerner();

        try {
            GETAPIRequest getApiRequest = new GETAPIRequest();
            String url = AppUrl.PENDING_TRANSACTIONS + "?tranId=" + tranId;
            
            getApiRequest.request(context, iJsonListener, url, "pendingTrans");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void pendingListerner() {
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
                    journalFees = new JournalFees();
                    userNote = new UserNote();

                    signerDocTypes = new ArrayList<SignerDocType>();

                    //Now check result sent by our POSTAPIRequest class
                    if (data != null) {

                        if (type.equals("pendingTrans")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone, scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum, serverDocName, profileImgPath, signatureImgPath, thumprintImgPath, tranType = "", witProImgPath = "";

                            String Scity = "", Sstate = "", Szip = "", Saddressline1 = "", Saddressline2 = "", signerCat = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");
                            level = signer_obj.optString("level");

                            tranType = signer_obj.getString("tranType");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();

                            //noSigner = signer_obj.optString("signer");
//                            Toast.makeText(context, level, Toast.LENGTH_LONG).show();
//                            //Log.e("Notarise", level);
                            if (tranType.equalsIgnoreCase("VEJ")){

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
                                            signatureImgPath = signer.getString("signature");
                                            thumprintImgPath = signer.getString("thumb");

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
                                                    signerCat, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2, signatureImgPath, thumprintImgPath);

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
                                if (signer_obj.has("journalentry")) {
                                    if (!signer_obj.get("journalentry").toString().equalsIgnoreCase("null")) {
                                        JSONObject locObj = signer_obj.getJSONObject("journalentry");
                                        if (!locObj.toString().equalsIgnoreCase("null")) {
                                            String notaType = locObj.optString("jtype");
                                            String jAmount = locObj.optString("jamount");
                                            String isFeeCollected = locObj.optString("iscollected");

                                            journalFees.setNotarizationType(notaType);
                                            journalFees.setFeeAmount(jAmount);
                                            if (isFeeCollected.equalsIgnoreCase("true")){
                                                journalFees.setCollected(true);
                                            } else {
                                                journalFees.setCollected(false);
                                            }
                                            new SaveJournal().execute();
                                            //new SaveLocation().execute();
                                        }
                                    }
                                }

                                if (signer_obj.has("notes")) {
                                    if (!signer_obj.get("notes").toString().equalsIgnoreCase("null")) {
                                        JSONObject locObj = signer_obj.getJSONObject("notes");
                                        if (!locObj.toString().equalsIgnoreCase("null")) {
                                            String noteHead = locObj.optString("notehead");
                                            String noteSubdata = locObj.optString("notesubhead");

                                            userNote.setNoteHeading(noteHead);
                                            userNote.setNoteDetail(noteSubdata);

                                            new SaveNote().execute();
                                            //new SaveLocation().execute();
                                        }
                                    }
                                }

                            } else {
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
                                            int phoneLength = phone.length();
                                            // String formatPhone = json_inside.optString("phone");
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
                                        /*ladParties = new LADParties(fName, lName, email, phoneFormatted, scanRef, profileImgPath,
                                                signerCat, witness, Scity, Sstate, Szip, Saddressline1, Saddressline2);
                                        ladPartiesList.add(ladParties);*/
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
                                            int phoneLength = phone.length();
                                            // String formatPhone = json_inside.optString("phone");
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
                            }



                            /*Intent intent = new Intent(context, NotarizeStepsActivity.class);
                            intent.putExtra("From", "Pending");
                            context.startActivity(intent);*/
                            Intent intent;
                            if (tranType.equalsIgnoreCase("LAD")) {
                                intent = new Intent(context, LADStepsActivity.class);
                                ((Activity) context).finish();
                            } else if(tranType.equalsIgnoreCase("VEJ")){
                                intent = new Intent(context, VEJ_NotarizeStepsActivity.class);
                            } else {
                                intent = new Intent(context, NotarizeStepsActivity.class);
                            }
                            intent.putExtra("From", "Pending");
                            context.startActivity(intent);
                            ((Activity) context).finish();
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

        private TextView dateText, docNameTxt, tranCompTxt, profileNumTxt, phoneNumtxt, emailNumTxt, signerCount, sealCode, signerName, textPipe;
        private ImageView imgPro, imgStar, imagestatus;
        private LinearLayout statusLayout;
        private ConstraintLayout topContainer, profileRound, locationRound, mailRound, phoneRound, profileImageLayout;
        private ProgressBar proImgProgress;
        private CoordinatorLayout emailNumTxtll;

        public MyView(View view) {
            super(view);
            dateText = view.findViewById(R.id.dateText);
            signerName = view.findViewById(R.id.nameText);
            docNameTxt = view.findViewById(R.id.textDocName);
            tranCompTxt = view.findViewById(R.id.textDone);
            imgPro = view.findViewById(R.id.proImg);
            imgStar = view.findViewById(R.id.starImg);
            mailRound = view.findViewById(R.id.documentRound);
            phoneRound = view.findViewById(R.id.phoneRound);
            profileRound = view.findViewById(R.id.profileRound);
            locationRound = view.findViewById(R.id.locationRound);
            profileNumTxt = view.findViewById(R.id.profileNumTxt);
            phoneNumtxt = view.findViewById(R.id.phoneNumtxt);
            emailNumTxt = view.findViewById(R.id.emailNumTxt);

            profileImageLayout = view.findViewById(R.id.profileImageLayout);
            imagestatus = view.findViewById(R.id.imagestatus);
            statusLayout = view.findViewById(R.id.statusLayout);
            signerCount = view.findViewById(R.id.signerCount);
            sealCode = view.findViewById(R.id.sealText);
            textPipe = view.findViewById(R.id.textPipe);

            topContainer = view.findViewById(R.id.topContainer);

            //proImgProgress = view.findViewById(R.id.homeprogress);
            proImgProgress = view.findViewById(R.id.proImgProgress);

            emailNumTxtll = (CoordinatorLayout) view.findViewById(R.id.emailNumTxtll);
        }

        public void setDate(String date) {
            dateText.setText(date);
        }

        public void setTitle(String name) {
            signerName.setText(name);
        }

        public void setDoc(String name) {
            docNameTxt.setText(name);
        }

        public void setTran(String comp) {
            tranCompTxt.setText(comp);
        }

        public void setSignerName(String sName) {
            signerName.setText(sName);
        }

        public void setSealCode(String sCode) {
            sealCode.setText(sCode);
        }
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
                    //List<LADParties> ladPartiesList = new ArrayList<>();

                    if (data != null) {

                        if (type.equals("signer")) {

                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");
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

                                        fName = signer.optString("first_name");
                                        lName = signer.optString("last_name");
                                        email = signer.optString("signer");
//                                        phone = signer.optString("phone");

                                        String formatPhone = signer.optString("phone");
                                        int phoneLength = signer.optString("phone").length();
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


                                        scanRef = signer.optString("scan_reference");
                                        profileImgPath = signer.optString("photo");

                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                tranType, false);
                                        signerList.add(signerReg);
                                    }

                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(true);
                                    dialog.setContentView(R.layout.dashboard_popup);

                                    TextView text = (TextView) dialog.findViewById(R.id.textHead);
                                    text.setText("Signers");

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

    private void getGetPhoneNumber(String tranId) {

        IJsonListener phoneJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {

                    List<SignerReg> signerList = new ArrayList<>();

                    if (data != null) {

                        if (type.equals("signer")) {

                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();


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
//                                        phone = signer.optString("phone");

                                        String formatPhone = signer.optString("phone");
                                        int phoneLength = signer.optString("phone").length();
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


                                        scanRef = signer.optString("scan_reference");
                                        profileImgPath = signer.optString("photo");

                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false);
                                        signerList.add(signerReg);
                                    }

                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setCancelable(true);
                                    dialog.setContentView(R.layout.dashboard_popup);

                                    TextView text = (TextView) dialog.findViewById(R.id.textHead);
                                    text.setText("Phone No");

                                    RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

                                    SignerPhoneNumberAdapter stepsAdapter = new SignerPhoneNumberAdapter(signerList, context);
                                    GridLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
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
            
            getApiRequest.request(context, phoneJsonListener, url, "signer");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    private void getLocation(String tranId) {

        IJsonListener phoneJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject data, String type) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {

                    List<SignerReg> signerList = new ArrayList<>();

                    if (data != null) {

                        if (type.equals("signer")) {

                            String fName, lName, email, phone, scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();


                            if (signer_obj.has("location")) {
                                if (!signer_obj.get("location").toString().equalsIgnoreCase("null")) {
                                    JSONObject locObj = signer_obj.getJSONObject("location");
                                    if (!locObj.toString().equalsIgnoreCase("null")) {

                                        String street = locObj.optString("street");
                                        String city = locObj.optString("city");
                                        String state = locObj.optString("state");
                                        String zip = locObj.optString("zip");

                                        String locationsInMap = street + "\n" +
                                                city + "\n" +
                                                state + "\n" +
                                                zip;

                                        final Dialog dialog = new Dialog(context);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setCancelable(true);
                                        dialog.setContentView(R.layout.dashboard_popup);

                                        TextView text = (TextView) dialog.findViewById(R.id.textHead);
                                        text.setText("Location");

                                        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

                                        SignerLocationAdapter stepsAdapter = new SignerLocationAdapter(locationsInMap, context);
                                        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setAdapter(stepsAdapter);

                                        dialog.show();

                                    }
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
            
            getApiRequest.request(context, phoneJsonListener, url, "signer");
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
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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

    private void setStar() {
        // CustomDialog.showProgressDialog(context);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("tranid", transactionId);
                params.put("star", false);

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

                    ladPartiesList = new ArrayList<LADParties>();

                    if (data != null) {
                        if (type.equals("star")) {
                            String success = data.optString("success");
                            if (success.equals("1")) {
                                new UpdateLocal().execute();
                            }
                        } else if (type.equals("share")) {
                            new DeleteAll().execute();
                            String fName, lName, email, phone = "", scanRef, rowId, signerEmail, docType,
                                    docName = "", apnNum = "", serverDocName, profileImgPath, witProImgPath = "";
                            JSONObject trans = data.getJSONObject("Transactions");
                            JSONArray bodyData = trans.getJSONArray("body");
                            JSONObject signer_obj = bodyData.getJSONObject(0);
                            rowId = signer_obj.optString("id");

                            transactions = new Transactions(rowId, transactionId, true);
                            new AddTranId().execute();


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
//                                        phone = signer.optString("phone");

                                        String formatPhone = signer.optString("phone");
                                        int phoneLength = signer.optString("phone").length();
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


                                        scanRef = signer.optString("scan_reference");
                                        profileImgPath = signer.optString("photo");

                                        signerReg = new SignerReg(fName, lName, email, phone, scanRef, profileImgPath,
                                                "signetGovt", false);
                                        sArrayList.add(signerReg);
                                        // lahar
                                        if (signer_obj.optString("tranType").equalsIgnoreCase("LAD")){
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

                                    if (signer_obj.optString("tranType").equalsIgnoreCase("LAD")){
                                        Intent intent = new Intent(context, LADShareActivity.class);
                                        intent.putExtra("transactionId", transactionId);
                                        context.startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(context, ShareActivity.class);
                                        intent.putExtra("transactionId", transactionId);
                                        context.startActivity(intent);
                                    }


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

            /*Intent intent = new Intent(context, ShareActivity.class);
            intent.putExtra("transactionId", transactionId);
            context.startActivity(intent);*/
        }

    }
    List<LADParties> ladPartiesList;
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
            if(ladPartiesList != null
                    && ladPartiesList.size()>0) {
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

        }
    }

    class AddTranId extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase().transactionsDao().insert(transactions);
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

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
            databaseClient.getAppDatabase().journalFeesDao().deleteAll();
            databaseClient.getAppDatabase().userNoteDao().deleteAll();
            return "";
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }

    class UpdateSealStatus extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase().sealAddedDao().deleteAll();
            databaseClient.getAppDatabase().sealAddedDao().insert(sealAdded);

            return null;
        }

        @Override
        protected void onPostExecute(Void docs) {
            super.onPostExecute(docs);

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

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);

        }

    }

    class PendingImages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {

            for (int i = 0; i < addDocArrayList.size(); i++) {

                databaseClient.getAppDatabase().documentsImageDao().insert(addDocArrayList.get(i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

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
    class SaveJournal extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .journalFeesDao()
                    .insert(journalFees);
            /*databaseClient.getAppDatabase()
                    .userLocationDao()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .userLocationDao()
                    .insert(userLocation);*/
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }
    class SaveNote extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .userNoteDao()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .userNoteDao()
                    .insert(userNote);
            /*databaseClient.getAppDatabase()
                    .userLocationDao()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .userLocationDao()
                    .insert(userLocation);*/
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    class SelectPlans extends AsyncTask<Void, Void, VACustomer> {

        @Override
        protected VACustomer doInBackground(Void... voids) {
            memPlans = databaseClient.getAppDatabase()
                    .vaCustomerDao()
                    .getCustomer();
            return memPlans;
        }

        @Override
        protected void onPostExecute(VACustomer memPlans) {
            super.onPostExecute(memPlans);
            daysLeft = memPlans.getDaysLeft();
            transCount = memPlans.getTransactionsLeft();
        }
    }

}
