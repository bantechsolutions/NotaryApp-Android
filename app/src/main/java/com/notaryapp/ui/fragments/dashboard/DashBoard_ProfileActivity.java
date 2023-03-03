
package com.notaryapp.ui.fragments.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.notaryapp.BuildConfig;
import com.notaryapp.R;
import com.notaryapp.notaryappApplication;
import com.notaryapp.components.ScrollViewExt;
import com.notaryapp.interfacelisterners.NestedScrollViewListener;
import com.notaryapp.roomdb.DatabaseClient;
import com.notaryapp.roomdb.entity.Info;
import com.notaryapp.roomdb.entity.ProfilePicture;
import com.notaryapp.roomdb.entity.UserReg;
import com.notaryapp.utils.AppUrl;
import com.notaryapp.utils.CustomDialog;
import com.notaryapp.utils.Utils;
import com.notaryapp.utils.Validation;
import com.notaryapp.volley.IJsonListener;
import com.notaryapp.volley.POSTAPIRequest;
import com.notaryapp.volley.RequestQueueService;
import com.notaryapp.volley.VolleyHelper;
import com.notaryapp.volley.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;



public class DashBoard_ProfileActivity extends AppCompatActivity implements NestedScrollViewListener, Animation.AnimationListener {

    public static final int PICK_IMAGE = 1;
    private final String TAG = "ProfileFragment";
    notaryappApplication notaryappApplication;
    Activity activity;
    private Animation animMoveUp;
    private ScrollViewExt scrollView;
    private ConstraintLayout footerbtn;
    private Button btnUpdateProfile, btnInfo;
    private TextView txtAddLicense;
    private RelativeLayout txtupdateProfile;
    // private RelativeLayout relative_add_license;
    private CircleImageView imgSelfie;
    private DatabaseClient databaseClient;
    private Context context;
    private UserReg userReg;
    private Bitmap bitmap;
    private String selfiePath, newProfileImg;
    // private boolean validFName, validLName, validEmail, validPhone, validAddress,validAddress1,validComany;
    // private EditText firstNameEdit,lastNameEdit,emailEdit,phoneEdit,addressEdit,addressEdit1,companyEdit;
    //private String fName,lName,email,phone,address,address1,company;
    private IJsonListener iJsonListener;
    private TextInputEditText profileEdit_LastName;
    private TextInputEditText profileEdit_Email;
    private TextInputEditText profileEdit_PhoneNo;
    private TextInputEditText profileEdit_address1;
    private TextInputEditText profileEdit_address2;
    private TextInputEditText profileEdit_FirstName;
    private TextInputEditText profileEdit_company;
    private TextInputEditText profileEdit_city;
    // private TextInputEditText profileEdit_state;
    private TextInputEditText profileEdit_zip;
    private String firstName, lastName, email, phone, company, address1, address2, city, state, zip;
    private boolean validFName, validLName, validEmail, validPhone, validCompany, validAddress1, validAddress2, validCity, validState, validZip;
    private TextInputLayout textInputLayoutFName, textInputLayoutLName, textInputLayoutEmail,
            textInputLayoutPhone, textInputLayoutCompany, textInputLayoutAddress1, textInputLayoutAddress2,
            textInputLayoutCity, textInputLayoutState, textInputLayoutZip;
    private Info info;
    private String proImg;
    private String proImageName;
    private ProfilePicture profilePicture;
    private Spinner spinnerState;
    private List<String> stateList;
    private Button back;

    private StringBuilder initString;
    private StringBuilder finalString;

    private ProgressBar img_profile_progress;

    private int randomForImg;

    public DashBoard_ProfileActivity() {
        // Required empty public constructor
    }

    public static boolean checkSelfie(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);

        return image.exists();

    }

    public static String getSelfiePath(Context context, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);
        File image = new File(directory, name);
        String path = image.getAbsolutePath();
        return path;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard_profile);
        // Inflate the layout for this fragment
        activity = this;
        init();

        notaryappApplication = (notaryappApplication) getApplication();

        new UserDetails().execute();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String timeSt = String.valueOf(timestamp.getTime());
        proImg = timeSt + ".png";
        /*proImg = SignInFragment.proImageName;*/
        //Log.d("PRO_IMG", proImg);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finalString = new StringBuilder();

                finalString.append(profileEdit_FirstName.getText().toString()).append(profileEdit_LastName.getText().toString())
                        .append(profileEdit_Email.getText().toString()).append(profileEdit_PhoneNo.getText().toString())
                        .append(profileEdit_company.getText().toString()).append(profileEdit_address1.getText().toString())
                        .append(profileEdit_address2.getText().toString()).append(profileEdit_city.getText().toString())
                        .append(stateList.indexOf(state)).append(profileEdit_zip.getText().toString());

                if (!initString.toString().equals(finalString.toString())) {
                    initString = finalString;
                    updateProfile();
                } else {
                    onBackPressed();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        footerbtn.setVisibility(View.INVISIBLE);

        animMoveUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);
        animMoveUp.setAnimationListener(this);

        scrollView.setScrollViewListener(this);

        txtupdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        profileEdit_FirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_FirstName, textInputLayoutFName)) {
                    validFName = Validation.isFirstName(profileEdit_FirstName, textInputLayoutFName, true);
                }
            }
        });
        profileEdit_LastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_LastName, textInputLayoutLName)) {
                    validLName = Validation.isLastName(profileEdit_LastName, textInputLayoutLName, true);//hasText();
                }
            }
        });
        profileEdit_Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_Email, textInputLayoutEmail)) {
                    validEmail = Validation.isEmailAddress(profileEdit_Email, textInputLayoutEmail, true);
                } else {

                }
            }
        });
        profileEdit_PhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_PhoneNo, textInputLayoutPhone)) {
                    validPhone = Validation.isPhoneNumber(profileEdit_PhoneNo, textInputLayoutPhone, true);
                } else {

                }
            }
        });
        profileEdit_company.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_company, textInputLayoutCompany)) {
                    validCompany = Validation.isFirstName(profileEdit_company, textInputLayoutCompany, true);
                } else {

                }
            }
        });
        profileEdit_address1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_address1, textInputLayoutAddress1)) {
                    validAddress1 = true;
                } else {

                }
            }
        });
        profileEdit_city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_city, textInputLayoutCity)) {
                    validCity = true;
                } else {

                }
            }
        });
     /*   profileEdit_state.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_state,textInputLayoutState)) {
                    validState = true;
                } else {

                }
            }
        });*/
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        profileEdit_zip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Validation.hasValue(profileEdit_zip, textInputLayoutZip)) {
                    validZip = true;
                } else {

                }
            }
        });

//        profileEdit_PhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
//        profileEdit_PhoneNo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                /* Let me prepare a StringBuilder to hold all digits of the edit text */
//                StringBuilder digits = new StringBuilder();
//
//                /* this is the phone StringBuilder that will hold the phone number */
//
//                StringBuilder phone = new StringBuilder();
//
//                /* let's take all characters from the edit text */
//                char[] chars = profileEdit_PhoneNo.getText().toString().toCharArray();
//
//                /* a loop to extract all digits */
//                for (int x = 0; x < chars.length; x++) {
//                    if (Character.isDigit(chars[x])) {
//                        /* if its a digit append to digits string builder */
//                        digits.append(chars[x]);
//                    }
//                }
//
//
//                if (digits.toString().length() >=3) {
//                    /* our phone formatting starts at the third character  and starts with the country code*/
//                    String countryCode = new String();
//
//                    /* we build the country code */
//                    countryCode += "(" + digits.toString().substring(0, 3) + ") ";
//
//                    /** and we append it to phone string builder **/
//                    phone.append(countryCode);
//
//                    /** if digits are more than or just 6, that means we already have our state code/region code **/
//                    if (digits.toString().length()>=6)
//                    {
//
//                        String regionCode=new String();
//                        /** we build the state/region code **/
//                        regionCode+=digits.toString().substring(3,6)+"-";
//                        /** we append the region code to phone **/
//                        phone.append(regionCode);
//
//                        /** the phone number will not go over 12 digits  if ten, set the limit to ten digits**/
//                        if (digits.toString().length()>=10)
//                        {
//                            phone.append(digits.toString().substring(6,10));
//                        }else
//                        {
//                            phone.append(digits.toString().substring(6));
//                        }
//                    }else
//                    {
//                        phone.append(digits.toString().substring(3));
//                    }
//
//                    /** remove the watcher  so you can not capture the affectation you are going to make, to avoid infinite loop on text change **/
//                    profileEdit_PhoneNo.removeTextChangedListener(this);
//
//                    /** set the new text to the EditText **/
//                    profileEdit_PhoneNo.setText(phone.toString());
//                    /** bring the cursor to the end of input **/
//                    profileEdit_PhoneNo.setSelection(profileEdit_PhoneNo.getText().toString().length());
//                    /* bring back the watcher and go on listening to change events */
//                    profileEdit_PhoneNo.addTextChangedListener(this);
//
//
//                } else {
//                    return;
//                }
//
//            }
//        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.notaryappInfoDialog(DashBoard_ProfileActivity.this, info.getProfile());
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                // Get the path from the Uri
                final String path = getPathFromURI(selectedImageUri);
                if (path != null) {
                    File f = new File(path);
                    selectedImageUri = Uri.fromFile(f);
                }

                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    bitmap = handleSamplingAndRotationBitmap(context, selectedImageUri);
                    saveNewSelfie(proImg, bitmap);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        }
    }

    private static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImageUri) throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImageUri);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height * 1f;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2f;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /*private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImageUri) throws IOException {
        ExifInterface ei = new ExifInterface(selectedImageUri.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }*/

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

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

    private void init() {

        int randomForImg = Utils.getRandomNumber();
        proImageName = randomForImg + ".png";

        scrollView = findViewById(R.id.scrollView);
        footerbtn = findViewById(R.id.footer);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        imgSelfie = findViewById(R.id.img_profile);
        txtupdateProfile = findViewById(R.id.updateProfile);
        // relative_add_license = findViewById(R.id.relative_add_license);
        btnInfo = findViewById(R.id.btnProfileInformation);

        txtAddLicense = findViewById(R.id.txt_addLicense);
        profileEdit_FirstName = findViewById(R.id.firstName);
        profileEdit_LastName = findViewById(R.id.lastName);
        profileEdit_Email = findViewById(R.id.email);
        profileEdit_PhoneNo = findViewById(R.id.phoneNo);
        profileEdit_company = findViewById(R.id.company);
        profileEdit_address1 = findViewById(R.id.addressLine1);
        profileEdit_address2 = findViewById(R.id.addressLine2);
        profileEdit_city = findViewById(R.id.cityEditTxt);
        //   profileEdit_state =findViewById(R.id.stateEditTxt);
        profileEdit_zip = findViewById(R.id.zipEditTxt);

        textInputLayoutFName = findViewById(R.id.firstNameText);
        textInputLayoutLName = findViewById(R.id.lastNameText);
        textInputLayoutEmail = findViewById(R.id.emailText);
        textInputLayoutPhone = findViewById(R.id.phoneText);
        textInputLayoutCompany = findViewById(R.id.companyText);
        textInputLayoutAddress1 = findViewById(R.id.address1Text);
        textInputLayoutAddress2 = findViewById(R.id.address2Text);
        textInputLayoutCity = findViewById(R.id.cityText);
        //   textInputLayoutState =findViewById(R.id.stateText);
        textInputLayoutZip = findViewById(R.id.zipText);
        spinnerState = findViewById(R.id.stateSpinner);

        back = findViewById(R.id.btn_pro_back);
        img_profile_progress = findViewById(R.id.img_profile_progress);

        context = getApplicationContext();
        databaseClient = DatabaseClient.getInstance(context);
        initIJsonListener();
        userReg = new UserReg();
        new GetStates().execute();
        new GeInfo().execute();
        new GETDP().execute();

    }

    private void updateProfile() {
        if (!Validation.hasFirstText(profileEdit_FirstName, textInputLayoutFName)) {
            textInputLayoutFName.setError("Enter FirstName");
        } else {
            validFName = true;
        }
        if (!Validation.hasValue(profileEdit_LastName, textInputLayoutLName)) {
            textInputLayoutLName.setError("Enter LastName");
        } else {
            validLName = true;
        }
        if (!Validation.hasEmailText(profileEdit_Email, textInputLayoutLName)) {
            textInputLayoutEmail.setError("Enter Email");
        } else {
            validEmail = true;
        }
        if (!Validation.hasPhoneTextSignup(profileEdit_PhoneNo, textInputLayoutPhone)) {
            textInputLayoutPhone.setError("Enter valid phone number");
            validPhone = false;
        } else {
            validPhone = true;
        }
        if (!Validation.hasValue(profileEdit_company, textInputLayoutCompany)) {
            textInputLayoutCompany.setError("Enter Company");
        } else {
            validCompany = true;
        }
        if (!Validation.hasValue(profileEdit_address1, textInputLayoutAddress1)) {
            textInputLayoutAddress1.setError("Enter address 1");
        } else {
            validAddress1 = true;
        }
       /* if(Validation.hasValue(profileEdit_address2,textInputLayoutAddress2)){
            validAddress2=true;

        }else {
            textInputLayoutAddress2.setError("Enter address 2");
        }*/

        if (Validation.hasValue(profileEdit_city, textInputLayoutCity)) {
            validCity = true;

        } else {
            textInputLayoutCity.setError("Enter city");
        }
       /* if(Validation.hasValue(profileEdit_state,textInputLayoutState)){
            validState=true;

        }else {
            textInputLayoutState.setError("Enter state");
        }*/
        if (state.equals("Select State")) {
            //    Toast.makeText(activity, , Toast.LENGTH_LONG).show();
            validState = false;
            CustomDialog.notaryappDialogSingle(activity, "Please select your state");
        } else if (state.equals("null")) {
            validState = false;
            CustomDialog.notaryappDialogSingle(activity, "Please select your state");
        } else {
            validState = true;
        }
        if (Validation.hasValue(profileEdit_zip, textInputLayoutZip)) {
            validZip = true;

        } else {
            textInputLayoutZip.setError("Enter zip code");
        }

        if (validFName && validLName && validEmail && validPhone && validCompany &&
                validAddress1 && validCity && validState && validZip) {
            getData();

            callUpdateProfileApi();
//                    loadFragment(new Validate_ProcessFragment()) ;
            validFName = Validation.hasFirstText(profileEdit_FirstName);
            if (!validFName) {
                textInputLayoutFName.setError("Enter FirstName");
            }
        }
    }

    private void getData() {
        try {
            firstName = profileEdit_FirstName.getText().toString().trim();
            lastName = profileEdit_LastName.getText().toString().trim();
            email = profileEdit_Email.getText().toString().trim();
            phone = profileEdit_PhoneNo.getText().toString().trim();
            address1 = profileEdit_address1.getText().toString().trim();
            address2 = profileEdit_address2.getText().toString().trim();
            company = profileEdit_company.getText().toString().trim();
            city = profileEdit_city.getText().toString().trim();
            //   state = profileEdit_state.getText().toString().trim();
            zip = profileEdit_zip.getText().toString().trim();

            userReg.setFirstName(firstName);
            userReg.setLastName(lastName);
            userReg.setEmail(email);
            userReg.setPhoneNo(phone);
            userReg.setAddress(address1);
            userReg.setAddress(address2);
            userReg.setCompany(company);
            userReg.setCity(city);
            userReg.setState(state);
            userReg.setZip(zip);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        if (diff == 0) {
            // do stuff
            footerbtn.setVisibility(View.VISIBLE);
            btnUpdateProfile.startAnimation(animMoveUp);
            //    btnConfirm.startAnimation(animMoveUp);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    btnUpdateProfile.clearAnimation();
                    //   btnConfirm.clearAnimation();
                }
            }, 300);


        } else if (diff > 0) {

            btnUpdateProfile.clearAnimation();
            //     btnConfirm.clearAnimation();
            footerbtn.setVisibility(View.INVISIBLE);

        }
    }

    public Uri saveNewSelfie(String name, Bitmap myBitmap) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("notaryapp", Context.MODE_PRIVATE);

        //  newProfileImg = "selfie_new.png";

        File selfie_new = new File(directory, name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(selfie_new);

            int nh = (int) (myBitmap.getHeight() * (512.0 / myBitmap.getWidth()));
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
        Uri imgUri = FileProvider.getUriForFile(activity,
                BuildConfig.APPLICATION_ID + ".provider", selfie_new);
        String path = selfie_new.getAbsolutePath();
        imgSelfie.setImageURI(imgUri);
        uploadProfile();
        return imgUri;
    }

    private void uploadProfile() {
        CustomDialog.showProgressDialog(activity);
        //final String token = "Bearer " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TOKEN", "0000");
        final String token = AppUrl.Authorization_Key;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, AppUrl.UPLOAD_NOTARY_DP, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                profilePicture = new ProfilePicture();
                String resultResponse = new String(response.data);
                try {
                    JSONObject obj = new JSONObject(resultResponse);
                    if (obj.getString("success").equalsIgnoreCase("1")) {
                        profilePicture.setProfilePic(obj.getString("fileDownloadUri"));
                        new SaveDp().execute();
                        CustomDialog.cancelProgressDialog();

                    } else {
                        //Log.d(TAG, "Response: " + resultResponse);
                        //  CustomDialog.notaryappDialogSingle(activity, Utils.errorMessage(activity));
                    }
                    CustomDialog.cancelProgressDialog();
                } catch (JSONException e) {
                    //e.printStackTrace();
                    //Log.d(TAG, "JSON Error: " + e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomDialog.cancelProgressDialog();
                //Log.d(TAG, "Volley Error: " + error);
                //  showUploadSnackBar();
                //CustomDialog.notaryappDialogSingle(activity,Utils.errorMessage(activity));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userName", email);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (imgSelfie == null) {
                    //Log.i(TAG, "avatar null");
                }
                params.put("file", new DataPart(proImageName, VolleyHelper
                        .getFileDataFromDrawable(getApplicationContext(),
                                imgSelfie.getDrawable()), "image/jpg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueService.getInstance(activity).addToRequestQueue(multipartRequest);
    }

    private void callUpdateProfileApi() {
        CustomDialog.showProgressDialog(activity);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("phone", phone.replace(" ", "")
                        .replace("(", "")
                        .replace(")", "").replace("-", ""));
                params.put("username", email);
                params.put("address1", address1);
                params.put("address2", address2);
                params.put("company", company);
                params.put("city", city);
                params.put("state", state);
                params.put("zip", zip);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(activity, iJsonListener, params, AppUrl.UPDATE_PROFILE, "updateProfile");
            //   Toast.makeText(MainActivity.this,"POST API called",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void callGetProfileApi() {
        CustomDialog.showProgressDialog(activity);
        try {
            POSTAPIRequest postapiRequest = new POSTAPIRequest();
            JSONObject params = new JSONObject();
//            HashMap<String, String> params = new HashMap<>();
            try {
                params.put("userName", email);

            } catch (Exception e) {
                //e.printStackTrace();
            }
            postapiRequest.request(activity, iJsonListener, params, AppUrl.GET_PROFILE, "getProfile");
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
                        if (type.equals("updateProfile")) {
                            String success = data.getString("success");
                            if (success.equals("1111")) {
                                CustomDialog.cancelProgressDialog();
                                new UpdateUserDetails().execute();
                                // loadFragment(new ProfileBase_AddLicenseFragment());
                            } else {

                            }
                        } else if (type.equals("getProfile")) {
                            // String success = data.getString("success");

                            initString = new StringBuilder();
                            JSONArray jArray = data.getJSONArray("success");
                            if (jArray.length() != 0) {
                                JSONObject json_inside = jArray.getJSONObject(0);
                                company = json_inside.getString("company");
                                address1 = json_inside.getString("address1");
                                address2 = json_inside.getString("address2");
                                String fName = json_inside.getString("first_name");
                                String lName = json_inside.getString("last_name");
                                fName = fName.toLowerCase();
                                lName = lName.toLowerCase();
                                profileEdit_FirstName.setText(Utils.capitalizeFirst(fName));
                                profileEdit_LastName.setText(Utils.capitalizeFirst(lName));
                                profileEdit_Email.setText(json_inside.getString("username"));

                                String phoneFormatted = "";
                                int phoneLength = json_inside.getString("phone").length();
                                // String formatPhone = json_inside.getString("phone");
                                /*if (json_inside.getString("phone").substring(0, 1).equals("+")) {
                                    if (json_inside.getString("phone").substring(0, 3).equals("+91")) {
                                        phoneFormatted = json_inside.getString("phone").substring(3, phoneLength);
                                    }
                                    if (json_inside.getString("phone").substring(0, 2).equals("+1")) {
                                        phoneFormatted = json_inside.getString("phone").substring(2, phoneLength);
                                    }
                                } else {
                                    phoneFormatted = json_inside.getString("phone");
                                }*/
                                if (phoneLength>10){
                                    phoneFormatted = json_inside.getString("phone").substring(phoneLength - 10);
                                }else if(phoneLength<10) {
                                    phoneFormatted = "";
                                } else {
                                    phoneFormatted = json_inside.getString("phone");
                                }
                                phoneFormatted = phoneFormatted.replace(" ", "");

                                //   Toast.makeText(context,witness_obj.getString("first_name") +"\n"+witness_obj.getString("photo")+" " +email,Toast.LENGTH_LONG).show();


                                profileEdit_PhoneNo.setText(phoneFormatted);
                                city = json_inside.getString("city");
                                state = json_inside.getString("state");
                                zip = json_inside.getString("zip");
                                profileEdit_city.setText(json_inside.getString("city"));
                                //profileEdit_state.setText(json_inside.getString("state"));
                                profileEdit_zip.setText(json_inside.getString("zip"));
                                if (company.equals("null")) {
                                    profileEdit_company.requestFocus();
                                    Utils.setKeyboardFocus(profileEdit_company);
                                    profileEdit_company.setText("");

                                } else {
                                    profileEdit_company.setText(json_inside.getString("company"));
                                }
                                if (address1.equalsIgnoreCase("null")) {
                                    profileEdit_address1.setText("");
                                } else {
                                    profileEdit_address1.setText(json_inside.getString("address1"));
                                }
                                if (address2.equalsIgnoreCase("null")) {
                                    profileEdit_address2.setText("");
                                } else {
                                    profileEdit_address2.setText(json_inside.getString("address2"));
                                }
                                profileEdit_Email.setEnabled(false);
                                if (city == null) {
                                    profileEdit_city.setText("");
                                } else {
                                    profileEdit_city.setText(json_inside.getString("city"));
                                }
                               /* if(state == null){
                                    profileEdit_state.setText("");
                                }else {
                                    profileEdit_state.setText(json_inside.getString("state"));
                                }*/
                                spinnerState.setSelection(stateList.indexOf(state));
                                if (zip == null) {
                                    profileEdit_zip.setText("");
                                } else {
                                    profileEdit_zip.setText(json_inside.getString("zip"));
                                }
                            }

                            initString.append(profileEdit_FirstName.getText().toString()).append(profileEdit_LastName.getText().toString())
                                    .append(profileEdit_Email.getText().toString()).append(profileEdit_PhoneNo.getText().toString())
                                    .append(profileEdit_company.getText().toString()).append(profileEdit_address1.getText().toString())
                                    .append(profileEdit_address2.getText().toString()).append(profileEdit_city.getText().toString())
                                    .append(stateList.indexOf(state)).append(profileEdit_zip.getText().toString());
                        }

                    }
                } catch (Exception e) {
                    CustomDialog.cancelProgressDialog();
                    CustomDialog.notaryappDialogSingle(activity, Utils.errorMessage(activity));
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

    class GetStates extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            stateList = databaseClient.getAppDatabase().statesDao().getStateName();
            stateList.add(0, "Select State");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_text_view, stateList);
            spinnerState.setAdapter(stateAdapter);
        }
    }

    class GeInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            info = DatabaseClient.getInstance(activity).getAppDatabase()
                    .infoDao()
                    .getInfo();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class UserDetails extends AsyncTask<Void, Void, UserReg> {

        @Override
        protected UserReg doInBackground(Void... voids) {
            userReg = databaseClient.getAppDatabase().userRegDao().getUser();
            //  String email = userReg.getEmail();
            return userReg;
        }

        @Override
        protected void onPostExecute(UserReg userReg) {
            super.onPostExecute(userReg);
            email = userReg.getEmail();
            callGetProfileApi();

//            firstNameEdit.setText(userReg.getFirstName());
//            lastNameEdit.setText(userReg.getLastName());
//            emailEdit.setText(userReg.getEmail());
//            phoneEdit.setText(userReg.getPhoneNo());
//            addressEdit.setText(userReg.getAddress());
//            addressEdit1.setText(userReg.getAddress1());
//            companyEdit.setText(userReg.getCompany());
            // Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        }
    }

    class UpdateUserDetails extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase().userRegDao().updateUser(userReg);

            return "";
        }

        @Override
        protected void onPostExecute(String userReg) {
            super.onPostExecute(userReg);
            notaryappDialogSingle(activity, "Profile Updated successfully");
            //   Toast.makeText(context, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void notaryappDialogSingle(final Activity activity, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_single);

        TextView text = (TextView) dialog.findViewById(R.id.alertMess);
        text.setText(message);
        //  Button dialogAllowButton = (Button) dialog.findViewById(R.id.btnDontAllow);
        //  dialogAllowButton.setVisibility(View.GONE);
        Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
    }

    class GETDP extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String profilePic = databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .getProfilePic();

            return profilePic;
        }

        @Override
        protected void onPostExecute(String profilePic) {
            super.onPostExecute(profilePic);

            if (profilePic == null) {
            } else {

                Picasso.with(context).load(profilePic).centerCrop()
                        .noFade()
                        .placeholder(R.drawable.progress_animation_image_loader)
                        .resize(180, 180)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .into(imgSelfie, new Callback() {
                            @Override
                            public void onSuccess() {
                                img_profile_progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                imgSelfie.setBackground(activity.getResources().getDrawable(R.drawable.logo));
                            }
                        });
//                Picasso.with(context)
//                        .load(profilePic)
//                        .centerCrop()
//                        .resize(180,180)
//                        .networkPolicy(NetworkPolicy.NO_CACHE)
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                        .into(imgSelfie);
            }
        }
    }

    class SaveDp extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .deleteAll();

            databaseClient.getAppDatabase()
                    .saveProfilePic()
                    .insert(profilePicture);

            return null;
        }

        @Override
        protected void onPostExecute(String email) {
            super.onPostExecute(email);

            new GETDP().execute();
            notaryappApplication.getObserver().setValue("After Value Changed!");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}

