package com.notaryapp.ejournal.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.SignerDocType;
import com.notaryapp.roomdb.entity.SignerReg;
import com.notaryapp.roomdb.entity.WitnessReg;
import com.notaryapp.ui.activities.notaryappSplashActivity;
import com.notaryapp.utils.BaseActivity;
import com.notaryapp.utils.Foreground;
import com.notaryapp.utils.Utils;
import com.notaryapp.volley.IJsonListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VEJ_EditProfileActivity extends BaseActivity {

    private RelativeLayout regRelativeLayout;
    private Button btnBack,btnClose,submitBtn;
    private SignerReg signerReg;
    private DatabaseClient databaseClient;
    private final String TAG = "EditSignerFragment";
    private CircleImageView imgProfile,witImageOne,witImageTwo;;
    private String imgProName;
    private String fName, lName, email, phone, address1, address2, city, state, zip;
    private boolean validFName, validLName, validEmail, validPhone, validAddress1, validCity, validState, validZip;
    private TextInputEditText firstNameEdit,lastNameEdit,emailIdEdit,phoneNoEdit,addressOneEdit,
            addressTwoEdit,cityNameEdit,stateNameEdit,zipCodeEdit;
    private TextInputLayout firstNameText,lastNameText,emailText,phoneText,address1Text,address2Text,cityText,stateText,zipText;
    private ConstraintLayout layOutWitnessOne,layoutWitnessTwo;
    private ProgressBar homeprogressImageProfile,homeprogress1,homeprogress2;

    private Context context;
   // private String scanRef,rowId;
    //private JumioScanDetails scanDetails;
    private String savedEmail;
    private IJsonListener iJsonListener;
    public static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private String downloadUrl,emailSelected,proImgName;
    private String fNameForm,lNameForm,phoneForm,userTypeForm;
//    private Integer idSigner,position;
   // private List<SignerReg> signerList;
    private boolean witness;
    private List<WitnessReg> witnessList, witnessListMain;
    private String done = "";
    private TextView witnessName1,witnessName2, head;

    //Sourav 20200921
    private ImageView doc1Img;
    private TextView frontIdText;
    private ArrayList<SignerDocType> signerDocTypes;
    private CardView card_front;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        /*time out*/
        listenerBinding = Foreground.get(getApplication()).addListener(this);
        counttimerInactivity = new CountDownTimer(600000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                Intent myIntent = new Intent(getApplicationContext(), notaryappSplashActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finishAffinity();
                finish();

            }
        }.start();
        setTimer();
        /*time out*/


        init();
        context = VEJ_EditProfileActivity.this;
        databaseClient = DatabaseClient.getInstance(context);
        new SelectData().execute();
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            userTypeForm= "";
        } else {
            userTypeForm = extras.getString("USERTYPE");
            emailSelected= extras.getString("EMAIL");
            new GetSigners().execute();
            //buttonControls();
            try {
                signerDocTypes = Utils.getSignerDocTypes();
                if (signerDocTypes != null && signerDocTypes.size() > 0) {
                    for (int i = 0; i < signerDocTypes.size(); i++) {
                        if (!emailSelected.equalsIgnoreCase("")
                                && emailSelected.toLowerCase().equalsIgnoreCase(signerDocTypes.get(i).getEmail().toLowerCase())) {
                            if (signerDocTypes.get(i).getVerifytype() != null) {
                                card_front.setVisibility(View.VISIBLE);
                                frontIdText.setText(signerDocTypes.get(i).getVerifytype().replace("_"," "));
                                break;
                            }
                        }
                    }
                }else{
                    card_front.setVisibility(View.GONE);
                }
                if(!userTypeForm.equalsIgnoreCase("")) {
                    if(userTypeForm.equalsIgnoreCase("PER ")) {
                        head.setText(getResources().getString(R.string.notary_verified));
                    }else if(userTypeForm.equalsIgnoreCase("WIT") || userTypeForm.equalsIgnoreCase("WITNESSMANUALLY")){
                        head.setText(getResources().getString(R.string.witness_verified));
                    }else{
                        head.setText(getResources().getString(R.string.notaryapp_verified));
                    }
                }
            }catch (Exception e){

            }

        }
        done = getIntent().getStringExtra("DONE");
        if(done != null) {
            if (done.equalsIgnoreCase("DONE")) {

                //head.setVisibility(View.GONE);
                regRelativeLayout.setVisibility(View.GONE);

                firstNameEdit.setEnabled(false);
                lastNameEdit.setEnabled(false);
                emailIdEdit.setEnabled(false);
                phoneNoEdit.setEnabled(false);
                addressOneEdit.setEnabled(false);
                addressTwoEdit.setEnabled(false);
                cityNameEdit.setEnabled(false);
                stateNameEdit.setEnabled(false);
                zipCodeEdit.setEnabled(false);
                if(userTypeForm.equals("WIT") || userTypeForm.equalsIgnoreCase("WITNESSMANUALLY")){
                    layOutWitnessOne.setVisibility(View.VISIBLE);
                    layoutWitnessTwo.setVisibility(View.VISIBLE);
                    card_front.setVisibility(View.GONE);
                    new GetWitness().execute();
                }else{
                    layOutWitnessOne.setVisibility(View.GONE);
                    layoutWitnessTwo.setVisibility(View.GONE);
                }
            }
        }


        layOutWitnessOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_EditProfileActivity.this, VEJ_EditProfileWitnessActivity.class);
                intent.putExtra("Witness",witnessList.get(0));
                intent.putExtra("position","1");
                startActivity(intent);
            }
        });

        layoutWitnessTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VEJ_EditProfileActivity.this,VEJ_EditProfileWitnessActivity.class);
                intent.putExtra("Witness",witnessList.get(1));
                intent.putExtra("position","2");
                startActivity(intent);
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEJ_EditProfileActivity.this.onBackPressed();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEJ_EditProfileActivity.this.onBackPressed();
            }
        });
    }

    private void init() {
        //initIJsonListener();
        btnBack = findViewById(R.id.btn_back);
        btnClose = findViewById(R.id.btn_close);
        submitBtn = findViewById(R.id.submitBtn);

        firstNameEdit = findViewById(R.id.firstName);
        lastNameEdit = findViewById(R.id.lastName);
        emailIdEdit = findViewById(R.id.email);
        phoneNoEdit = findViewById(R.id.phoneNo);
        addressOneEdit = findViewById(R.id.address1);
        addressTwoEdit = findViewById(R.id.address2);
        cityNameEdit = findViewById(R.id.city);
        stateNameEdit = findViewById(R.id.state);
        zipCodeEdit = findViewById(R.id.zip);
        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        address1Text = findViewById(R.id.address1Text);
        address2Text = findViewById(R.id.address2Text);
        cityText = findViewById(R.id.cityText);
        stateText = findViewById(R.id.stateText);
        zipText = findViewById(R.id.zipText);
        imgProfile = findViewById(R.id.imageProfile);

        witnessName1 = findViewById(R.id.itemTitle1);
        witnessName2 = findViewById(R.id.itemTitle2);

        witImageOne = findViewById(R.id.itemImage1);
        witImageTwo = findViewById(R.id.itemImage2);

        regRelativeLayout = findViewById(R.id.regRelativeLayout);

        head = findViewById(R.id.head);
        doc1Img = findViewById(R.id.doc1Img);
        frontIdText = findViewById(R.id.frontIdText);
        card_front = findViewById(R.id.card_front);


        layOutWitnessOne = findViewById(R.id.witnessOneContainer);
        layoutWitnessTwo = findViewById(R.id.witnessTwoContainer);

        long millis=System.currentTimeMillis();
        proImgName = "signer_"+millis+".png";

        homeprogressImageProfile = findViewById(R.id.homeprogressImageProfile);
        homeprogress1 = findViewById(R.id.homeprogress1);
        homeprogress2 = findViewById(R.id.homeprogress2);
    }

    class GetWitness extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            /*witnessList =  databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitness();*/

            witnessListMain = databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .getWitness();

            //witnessCount = 0;
            witnessList = new ArrayList<>();
            for (int i=0;i<witnessListMain.size();i++){
                //Log.d("WIT_SIGN", signerRegData.getEmail() + " "+ witnessList.get(i).getSignerEmail());
                if (emailSelected != null){
                    Log.d("SIGNER_LIST", witnessListMain.get(i).getFirstName());
                    if (emailSelected.equalsIgnoreCase(witnessListMain.get(i).getSignerEmail())){
                        witnessList.add(witnessListMain.get(i));
                        //witnessCount = witnessCount + 1;
                        //Log.d("WITNESS_COUNT", String.valueOf(witnessCount));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            String proImgFirst = witnessList.get(0).getProImagePath();
            String proImgSecond = witnessList.get(1).getProImagePath();
            witnessName1.setText(witnessList.get(0).getFirstName());
            witnessName2.setText(witnessList.get(1).getFirstName());
            String  selfiePath = getSelfiePath(context, proImgFirst);
            Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);
            String  selfiePath1 = getSelfiePath(context, proImgSecond);
            Bitmap bitmap1 = BitmapFactory.decodeFile(selfiePath1);
            if(bitmap != null) {
                witImageOne.setVisibility(View.VISIBLE);
                witImageOne.setImageBitmap(bitmap);
            }
            else{
                if(!proImgFirst.equalsIgnoreCase("") && !proImgFirst.equalsIgnoreCase("null")) {
                    witImageOne.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(proImgFirst).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(180, 180)
                            .into(witImageOne, new Callback() {
                                @Override
                                public void onSuccess() {
                                    homeprogress1.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    witImageOne.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });

//                    Picasso.with(context).load(proImgFirst)
//                            .centerCrop()
//                            .resize(180,180)
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            .into(witImageOne);
                }
            }
            if(bitmap1 != null) {
                witImageTwo.setVisibility(View.VISIBLE);
                witImageTwo.setImageBitmap(bitmap1);
            }
            else{
                if(!proImgSecond.equalsIgnoreCase("") && !proImgSecond.equalsIgnoreCase("null")) {
                    witImageTwo.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(proImgSecond).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(180, 180)
                            .into(witImageTwo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    homeprogress2.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    witImageTwo.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });
//                    Picasso.with(context).load(proImgSecond)
//                            .centerCrop()
//                            .resize(200,200)
//                            .networkPolicy(NetworkPolicy.NO_CACHE)
//                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                            .into(witImageTwo);
                }
            }
        }
    }
    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                // Get the path from the Uri
                final String path = getPathFromURI(selectedImageUri);
                if (path != null) {
                    File f = new File(path);
                    selectedImageUri = Uri.fromFile(f);
                }

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(VEJ_EditProfileActivity.this.getContentResolver(), selectedImageUri);
                    saveNewSelfie(proImgName,bitmap);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
    }
//
    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);

            //fos.close();
        } catch (Exception e) {
//            Log.e("SAVE_IMAGE", e.getMessage(), e);
        } finally {
            try {
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        // Uri imgUri = Uri.fromFile(docpath);
        Uri imgUri = FileProvider.getUriForFile(VEJ_EditProfileActivity.this,
                BuildConfig.APPLICATION_ID + ".provider",selfie_new);

        imgProfile.setImageURI(imgUri);
        String path = selfie_new.getAbsolutePath();
        return imgUri;
    }
//
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    class UpdateSigner extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

         //   int id = params[0];
            databaseClient.getAppDatabase()
                    .signerRegDao()
                    .updateData(fName,lName,email,phone,address1,address2,city,state,zip);
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
         //   startActivity(new Intent(EditProfileActivity.this,NotarizeStepsActivity.class));
            finish();
        }
    }

    class GetSigners extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            signerReg =  databaseClient.getAppDatabase()
                    .signerRegDao()
                    .getSigner(emailSelected);
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            // idSigner = signerReg.getId();
            //Formatting phone number and removing
            fNameForm = signerReg.getFirstName();
            lNameForm = signerReg.getLastName();
            phoneForm = signerReg.getPhoneNo();
            email = signerReg.getEmail();
            imgProName = signerReg.getProImagePath();
            city = signerReg.getCityName();
            state = signerReg.getStateName();
            zip = signerReg.getZipCode();
            witness = signerReg.isWitness();

            firstNameEdit.setText(fNameForm);
            lastNameEdit.setText(lNameForm);
            phoneNoEdit.setText(phoneForm);
            emailIdEdit.setText(email);
            emailIdEdit.setEnabled(false);
            addressOneEdit.setText(signerReg.getAddressOne());
            addressTwoEdit.setText(signerReg.getAddressTwo());
            cityNameEdit.setText(signerReg.getCityName());
            stateNameEdit.setText(signerReg.getStateName());
            zipCodeEdit.setText(signerReg.getZipCode());

            firstNameEdit.setEnabled(false);
            lastNameEdit.setEnabled(false);
            emailIdEdit.setEnabled(false);
            phoneNoEdit.setEnabled(false);
            addressOneEdit.setEnabled(false);
            addressTwoEdit.setEnabled(false);
            cityNameEdit.setEnabled(false);
            stateNameEdit.setEnabled(false);
            zipCodeEdit.setEnabled(false);

            String  selfiePath = getSelfiePath(context, imgProName);
            Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);


            if(bitmap != null) {
                imgProfile.setImageBitmap(bitmap);
            }
            else{

                if(!imgProName.equalsIgnoreCase("") && !imgProName.equalsIgnoreCase("null")) {
                    //Picasso.with(context).load(imgProName).into(imgProfile);
                    Picasso.with(context).load(imgProName).centerCrop()
                            .noFade()
                            .placeholder(R.drawable.progress_animation_image_loader)
                            .resize(180, 180)
                            .into(imgProfile, new Callback() {
                                @Override
                                public void onSuccess() {
                                    homeprogressImageProfile.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    imgProfile.setBackground(getResources().getDrawable(R.drawable.logo));
                                }
                            });
                }
            }

            if(userTypeForm.equals("WIT") || userTypeForm.equalsIgnoreCase("WITNESSMANUALLY")){
                layOutWitnessOne.setVisibility(View.VISIBLE);
                layoutWitnessTwo.setVisibility(View.VISIBLE);
                new GetWitness().execute();
            }else{
                layOutWitnessOne.setVisibility(View.GONE);
                layoutWitnessTwo.setVisibility(View.GONE);
            }

        }
    }
    class SelectData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
          //  scanDetails = databaseClient.getAppDatabase().scanDetailsDao().getDetails();
            //  selectedType = databaseClient.getAppDatabase().validateIdIdentityTypeDao().getSelectIdType();
            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
//            rowId = databaseClient.getAppDatabase().transactionsDao().getTransactionId();
//            jumioKeys = databaseClient.getAppDatabase().jumioKeysDao().getJumioKeys();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);
        }

    }
 /*   private void downloadProfile(String url){
        //  scanRefference = "1ccecf1f-8ebc-4b53-ae3c-63a807899d01";
        CustomDialog.showProgressDialog(EditProfileActivity.this);
        // String url = AppUrl.JUMIO_BASE_URL +scanRefference+"/images/face";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .authenticator(new Authenticator()
                {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException
                    {
                        String credential = Credentials.basic(apiToken, apiSecret);
                        return response.request().newBuilder()
                                .header("Authorization", credential)
                                .header("Accept","image/jpeg , image/png")
                                .header("User-Agentvalue","Veritale Data Solutions ,Inc. notaryapp/1.0")
                                .build();
                    }
                })
                .build();
        Picasso picasso = new Picasso.Builder(EditProfileActivity.this)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
        picasso.load(url)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgProfile);
        CustomDialog.cancelProgressDialog();

    }*/

    //Calling the API to update the Signer
/*    private void UpdateClientAPI(){
        CustomDialog.showProgressDialog(EditProfileActivity.this);
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("puser",savedEmail);
                params.put("username",email);
                params.put("first_name",fName);
                params.put("last_name",lName);
                params.put("phone",phone);
                params.put("city",city);
                params.put("address1",address1);
                params.put("address2",address2);
                params.put("state",state);
                params.put("zip",zip);
                //params.put("phone ",email);
                //params.put("id","260");

            }catch (Exception e){
                e.printStackTrace();
            }
            postapiRequest.request(EditProfileActivity.this,iJsonListener,params, AppUrl.UPDATE_SIGNER,"updateClient");
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

/*    private void initIJsonListener() {
        //Implementing interfaces of FetchDataListener for POST api request
        iJsonListener = new IJsonListener() {
            @Override
            public void onFetchSuccess(JSONObject responseData,String type ) {
                CustomDialog.cancelProgressDialog();
                //      RequestQueueService.cancelProgressDialog();
                try {
                    //Now check result sent by our POSTAPIRequest class
                    if (responseData != null) {
                        if(type.equals("updateClient")) {
                            String success = responseData.getString("success");
                            if (success.equals("1019")) {

                                    new UpdateSigner().execute();

                            }
                        }
                    }
                }catch (Exception e){
                    CustomDialog.cancelProgressDialog();
                    //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFetchFailure(String msg) {
                //CustomDialog.notaryappDialogSingle(getActivity(),errorMess);
            }

            @Override
            public void onFetchStart() {
                //  RequestQueueService.showProgressDialog(getActivity());
            }
        };
    }*/
}
