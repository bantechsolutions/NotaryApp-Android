package com.notaryapp.ui.activities.verifyauthenticate;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jumio.core.enums.JumioDataCenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.JumioKeys;
import com.notaryapp.roomdb.entity.JumioScanDetails;
import com.notaryapp.roomdb.entity.SignerDocType;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileWitnessActivity extends BaseActivity {


    private Button btnBack,btnClose,submitBtn;
    private WitnessReg witnessReg;
    private DatabaseClient databaseClient;
    private final String TAG = "EditSignerFragment";
    private CircleImageView imgProfile;
    private String fName, lName, email, phone,imgProName;
    private boolean validFName, validLName, validPhone;
    private TextInputEditText firstNameEdit,lastNameEdit,emailIdEdit,phoneNoEdit,addressOneEdit,
            addressTwoEdit,cityNameEdit,stateNameEdit,zipCodeEdit;
    private TextInputLayout firstNameLay,lastNameLay,emailLay,phoneNoLay;

    private Context context;
    private String scanRef,rowId;
    private JumioScanDetails scanDetails;
    private String savedEmail;
    private IJsonListener iJsonListener;
    public static final int PICK_IMAGE = 1;
    private Bitmap bitmap;
    private JumioKeys jumioKeys;
    private String apiToken = null;
    private String apiSecret = null;
    private JumioDataCenter dataCenter = null;
    private String downloadUrl,emailSelected,signerEmail;
    private String fNameForm,lNameForm,phoneForm,userTypeForm;
    private Integer idSigner,position;
    private String positionValue = "";
    private TextView head;

    //Sourav 20200921
    private ImageView doc1Img;
    private TextView frontIdText;
    private ArrayList<SignerDocType> signerDocTypes;
    private CardView card_front;

    private ProgressBar imageProfileProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile_witness);

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
       // buttonControls();
        context = this;
        databaseClient = DatabaseClient.getInstance(context);
        //new SelectData().execute();
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            userTypeForm= "";
        } else {
            witnessReg = (WitnessReg) getIntent().getSerializableExtra("Witness");
            if(getIntent().getStringExtra("position") != null) {
                if (getIntent().getStringExtra("position").equalsIgnoreCase("1")) {
                    positionValue = "WITNESS ONE";
                } else if (getIntent().getStringExtra("position").equalsIgnoreCase("2")) {
                    positionValue = "WITNESS TWO";
                }
            }

            try {
                signerDocTypes = Utils.getSignerDocTypes();
                if (signerDocTypes != null && signerDocTypes.size() > 0) {
                    for (int i = 0; i < signerDocTypes.size(); i++) {
                        if (!witnessReg.getEmail().equalsIgnoreCase("")
                                && witnessReg.getEmail().toLowerCase()
                                .equalsIgnoreCase(signerDocTypes.get(i).getEmail().toLowerCase())) {
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
            }catch (Exception e){
                Log.v("test",e.toString());
            }
        }
        firstNameEdit.setText(witnessReg.getFirstName());
        lastNameEdit.setText(witnessReg.getLastName());
        phoneNoEdit.setText(witnessReg.getPhoneNo());
        emailIdEdit.setText(witnessReg.getEmail());
        addressOneEdit.setText(witnessReg.getAddressOne());
        addressTwoEdit.setText(witnessReg.getAddressTwo());
        cityNameEdit.setText(witnessReg.getCityName());
        stateNameEdit.setText(witnessReg.getStateName());
        zipCodeEdit.setText(witnessReg.getZipCode());
        if(!positionValue.equalsIgnoreCase("")) {
            head.setText(positionValue);
        }

        firstNameEdit.setEnabled(false);
        lastNameEdit.setEnabled(false);
        emailIdEdit.setEnabled(false);
        phoneNoEdit.setEnabled(false);
        addressOneEdit.setEnabled(false);
        addressTwoEdit.setEnabled(false);
        cityNameEdit.setEnabled(false);
        stateNameEdit.setEnabled(false);
        zipCodeEdit.setEnabled(false);
        imgProName = witnessReg.getProImagePath();
        signerEmail = witnessReg.getSignerEmail();

        String  selfiePath = getSelfiePath(context, imgProName);
        Bitmap bitmap = BitmapFactory.decodeFile(selfiePath);
        if(bitmap == null){
            Picasso.with(context).load(imgProName).centerCrop()
                    .noFade()
                    .placeholder(R.drawable.progress_animation_image_loader)
                    .resize(200, 200)
                    .into(imgProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                            imageProfileProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            imgProfile.setBackground(context.getResources().getDrawable(R.drawable.logo));
                        }
                    });
            //Picasso.with(this).load(imgProName).into(imgProfile);
        }else {
            imageProfileProgress.setVisibility(View.GONE);
            imgProfile.setImageBitmap(bitmap);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileWitnessActivity.this.onBackPressed();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileWitnessActivity.this.onBackPressed();
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
        imgProfile = findViewById(R.id.imageProfile);
        firstNameLay = findViewById(R.id.firstNameText);
        lastNameLay = findViewById(R.id.lastNameText);
        emailLay = findViewById(R.id.emailText);
        phoneNoLay = findViewById(R.id.phoneText);
        head = findViewById(R.id.head);

        doc1Img = findViewById(R.id.doc1Img);
        frontIdText = findViewById(R.id.frontIdText);
        card_front = findViewById(R.id.card_front);

        imageProfileProgress = findViewById(R.id.imageProfileProgress);
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
                    bitmap = MediaStore.Images.Media.getBitmap(EditProfileWitnessActivity.this.getContentResolver(), selectedImageUri);
                    saveNewSelfie(imgProName,bitmap);
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

        //  newProfileImg = "selfie_new.png";

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

            scaled.compress(Bitmap.CompressFormat.PNG, 80, fos);
            //fos.close();
        } catch (Exception e) {
            //Log.e("SAVE_IMAGE", e.getMessage(), e);
        } finally {
            try {
                if (fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        // Uri imgUri = Uri.fromFile(docpath);
        Uri imgUri = FileProvider.getUriForFile(EditProfileWitnessActivity.this,
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

/*    class UpdateWitness extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

            int id = params[0];
            databaseClient.getAppDatabase()
                    .witnessRegDao()
                    .updateData(fName,lName,email,phone);
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            //startActivity(new Intent(EditProfileWitnessActivity.this,NotarizeStepsActivity.class));
            finish();
        }
    }

   class SelectData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            savedEmail =  databaseClient.getAppDatabase()
                    .userRegDao()
                    .getEmail();
            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

        }

    }*/
    /* private void downloadProfile(String url){
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
        CustomDialog.showProgressDialog(EditProfileWitnessActivity.this);
        try{
            POSTAPIRequest postapiRequest=new POSTAPIRequest();
            JSONObject params=new JSONObject();
            try {
                params.put("pUser",savedEmail);
                params.put("username",email);
                params.put("first_name",fName);
                params.put("last_name",lName);
                params.put("phone",phone);
                //params.put("phone ",email);
                //params.put("id","260");
            }catch (Exception e){
                e.printStackTrace();
            }
            postapiRequest.request(EditProfileWitnessActivity.this,iJsonListener,params, AppUrl.UPDATE_SIGNER,"updateClient");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initIJsonListener() {
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
                            if (success.equals("1118")) {

                                    new UpdateWitness().execute();

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
