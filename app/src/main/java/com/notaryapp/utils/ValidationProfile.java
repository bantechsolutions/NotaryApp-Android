package com.notaryapp.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by campusiq on 27/04/18.
 */

public class   ValidationProfile {

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = " /^[56789]\\d{9}$/";//(\\d)(\\1){9,}";//"\\d{10}";//" // "\\d{3}-\\d{7}";//()
    private static final String PASSWORD_REGEX= "^(?=.*[A-Z])(?=.*?[#.?!@$%^&*-]).{8,}$";
    private static final String NAME_REGEX = "^[\\p{L} .'-]+$";// "^[a-zA-Z]+$";
    //("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")123-456-7890
    private static final String LICENSE_REGEX = "\\d{7}";
    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "10 valid Digits only";
    private static final String PASS_MSG = "Capital,small letters,spl char and numbers min 8";
    private static final String LICENSE_MSG = "7 Digits only";
    private static final String NAME_MSG = "Use alphabets only.";
 //   private static final String ADDRSS_MSG = "Use alphabets only.";
 //   private static final String COMP_MSG = "Use alphabets only.";
    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isEmailValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValidPhone(editText, PHONE_REGEX, PHONE_MSG, required);
    }

    public static boolean passwordValidation(EditText editText, boolean required) {
        return isPasswordValid(editText, PASSWORD_REGEX, PASS_MSG, required);
    }
//    public static boolean isAddress(EditText editText, boolean required) {
//        return isValidAddres(editText, PHONE_REGEX, PHONE_MSG, required);
//    }
//    public static boolean isCompany(EditText editText, boolean required) {
//        return isValidCompany(editText, PHONE_REGEX, PHONE_MSG, required);
//    }
    public static boolean isLicenseNumber(EditText editText, boolean required) {
        return isValid(editText, LICENSE_MSG, LICENSE_MSG, required);
    }
    public static boolean isFirstName(EditText editText, boolean required) {
        return isFNameValid(editText, NAME_REGEX, NAME_MSG, required);
    }
    public static boolean isLastName(EditText editText, boolean required) {
        return isLNameValid(editText, NAME_REGEX, NAME_MSG, required);
    }
    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isValidCompany(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasCompanyText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
           // editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isValidAddres(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasAddressText(editText)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
          //  editText.requestFocus();
            return false;
        }
        ;

        return true;
    }
    public static boolean isPasswordValid(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasPasswordText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
         //   editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isEmailValid(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasEmailText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
           // editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean hasEmailText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter valid email");
            return false;
        }

        return true;
    }
    public static boolean isFNameValid(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasFirstText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
         //   editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isLNameValid(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasLastText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
           // editText.requestFocus();
            return false;
        }

        return true;
    }
    // return true if the input field is valid, based on the parameter passed
    public static boolean isValidPhone(EditText editText, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();
        if(text.length()==12) {
            if (text.contains(" ")) {
                text= text.replaceAll(" ", "");
            }
        }
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasPhoneText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && text.matches(regex)) {
            editText.setError(errMsg);
            // editText.requestFocus();
            return false;
        }else

            return true;
    }
    public static boolean hasPhoneText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter valid phone number");

            return false;
        }else{

        }

        return true;
    }
    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasFirstText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter firstname");

            return false;
        }

        return true;
    }
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Fill");

            return false;
        }

        return true;
    }
    public static boolean hasAddressText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter address");

            return false;
        }

        return true;
    }
    public static boolean hasLastText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter lastname");

            return false;
        }

        return true;
    }
    public static boolean hasCompanyText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter Company");

            return false;
        }

        return true;
    }
    public static boolean hasFocusText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(null);

            return false;
        }

        return true;
    }
    public static boolean hasPasswordText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter password");

            return false;
        }

        return true;
    }
    public static boolean hasPasswordMatched(EditText editTextP,EditText editTextC) {

        String textp = editTextP.getText().toString().trim();
        String textc = editTextC.getText().toString().trim();

        // length 0 means there is no text
        if (!textp.equals(textc)) {
            editTextC.setError("Passwords should match");
           // editTextC.setBackgroundTintList();
            return false;
        }

        return true;
    }
}
