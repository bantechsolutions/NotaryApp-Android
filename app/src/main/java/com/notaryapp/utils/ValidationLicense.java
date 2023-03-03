package com.notaryapp.utils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Created by campusiq on 27/04/18.
 */

public class ValidationLicense {

   private static final String LICENSE_REGEX = "\\d{7}";
//    // Error Messages
//    private static final String REQUIRED_MSG = "Required";
//    private static final String EMAIL_MSG = "Invalid email";
//    private static final String PHONE_MSG = "10 Digits only";
//    private static final String PASS_MSG = "Capital,small letters,spl char and numbers min 8";
    private static final String LICENSE_MSG = "Valid license only";
    private static final String NAME_MSG = "Use alphabets only.";
    // call this method when you need to check email validation

    public static boolean isLicenseNumber(TextInputEditText editText, TextInputLayout textInputLayout,boolean required) {
        return isLicenseValid(editText,textInputLayout, LICENSE_REGEX, LICENSE_MSG, required);
    }
//    public static boolean isFirstName(EditText editText, boolean required) {
//        return isFNameValid(editText, NAME_REGEX, NAME_MSG, required);
//    }
//    public static boolean isLastName(EditText editText, boolean required) {
//        return isLNameValid(editText, NAME_REGEX, NAME_MSG, required);
//    }
    // return true if the input field is valid, based on the parameter passed
//    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {
//
//
//        String text = editText.getText().toString().trim();
//
//        // clearing the error, if it was previously set by some other values
//        editText.setError(null);
//
//        // text required and editText is blank, so return false
//        if ( required && !hasText(editText) ) return false;
//
//        // pattern doesn't match so returning false
//        if (required && !Pattern.matches(regex, text)) {
//            editText.setError(errMsg);
//            editText.requestFocus();
//            return false;
//        };
//
//        return true;
//    }

    public static boolean isLicenseValid(TextInputEditText editText, TextInputLayout textInputLayout , String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasLicenseText(editText,textInputLayout) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
           // editText.requestFocus();
            return false;
        }

        return true;
    }
    public static boolean hasLicenseText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
       // textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
           // textInputLayout.setError("Enter valid license");
            return false;
        }

        return true;
    }

    public static boolean hasText(TextInputEditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter zip");

            return false;
        }

        return true;
    }

    public static boolean hasCityText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
      //  textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
          //  textInputLayout.setError("Enter city");

            return false;
        }

        return true;
    }

    /////
    public static boolean hasExpiryDateText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        //  textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            //  textInputLayout.setError("Enter city");

            return false;
        }

        return true;
    }


    public static boolean hasStateText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
     //   textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            //textInputLayout.setError("Enter state");

            return false;
        }

        return true;
    }

    public static boolean hasZipText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
       // textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
          //  textInputLayout.setError("Enter state");

            return false;
        }

        return true;
    }

    public static boolean hasAddressText(TextInputEditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
       // textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
           // textInputLayout.setError("Enter address");

            return false;
        }

        return true;
    }
}
