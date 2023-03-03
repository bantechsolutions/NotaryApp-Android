package com.notaryapp.utils;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class Validation {

    // Regular Expression
    // you can change the expression based on your need
   // private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]{2,4})$";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,4})$";
    private static final String PHONE_REGEX = "^[1-9]\\d{9}$";
   // private static final String PHONE_REGEX = "^\\+(?:[0-9] ?){6,14}[0-9]$";

   // private static final String PHONE_REGEX = "(\\d{3}\\)s\\d{3}s?\\d{4}";
    private static final String PASSWORD_REGEX= "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*?[#.,?!@$%^&*-]).{8,}$";
    private static final String NAME_REGEX = "^[\\p{L} ]+$";// "^[\\p{L} .'-]+$"; "^[a-zA-Z]+$";//"^[\\p{L},-']+$"
    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "Enter valid phone number";
    private static final String PASS_MSG = "Password should include 1 Capital letter, 1 Small letter," +
            "1 Special character,1 Number and Minimum 8";
   // /q private static final String LICENSE_MSG = "7 Digits only";
    private static final String NAME_MSG = "Use alphabets only.";
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
    public static boolean isEmailAddress(EditText editText, TextInputLayout textInputLayout, boolean required) {
        return isEmailValid(editText,textInputLayout, EMAIL_REGEX, EMAIL_MSG, required);
    }

    public static boolean  isEmailValid(EditText editText, TextInputLayout textInputLayout,String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
            // editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean passwordValidation(EditText editText,TextInputLayout textInputLayout, boolean required) {
        return isPasswordValid(editText,textInputLayout, PASSWORD_REGEX, PASS_MSG, required);
    }

    public static boolean signInPasswordValidation(EditText editText,TextInputLayout textInputLayout, boolean required) {
        return isPasswordValid(editText,textInputLayout, PASSWORD_REGEX, "Invalid password", required);
    }

    public static boolean isPasswordValid(EditText editText,TextInputLayout textInputLayout, String regex, String errMsg, boolean required) {


        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasPasswordText(editText ,textInputLayout) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);

            return false;
        };

        return true;
    }

    public static boolean isFirstName(EditText editText,TextInputLayout textInputLayout, boolean required) {
        return isFNameValid(editText,textInputLayout, NAME_REGEX, NAME_MSG, required);
    }
    public static boolean isFNameValid(EditText editText,TextInputLayout textInputLayout, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasFirstText(editText,textInputLayout) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
            //   editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isLastName(EditText editText,TextInputLayout textInputLayout, boolean required) {
        return isLNameValid(editText,textInputLayout, NAME_REGEX, NAME_MSG, required);
    }
    public static boolean isLNameValid(EditText editText,TextInputLayout textInputLayout, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();

        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasLastText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
            //   editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean isSignerPhoneNumber(EditText editText,TextInputLayout textInputLayout, boolean required) {
            if(editText.getText().toString().matches("[0-9]+") && editText.getText().toString().length() == 10){
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean isPhoneNumber(EditText editText,TextInputLayout textInputLayout, boolean required) {
        if(editText.getText().toString().matches("[0-9]+") && editText.getText().toString().length() == 10){
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean isPhoneNumberForSignUp(EditText editText, TextInputLayout textInputLayout, boolean required) {
        if (editText.getText().toString().matches("\\(\\d\\d\\d\\)\\s\\d\\d\\d\\-\\d\\d\\d\\d")) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isValidPhone(EditText editText,TextInputLayout textInputLayout, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
       // String text = editText.getText().toString().trim();
        if(text.length()==12) {
            if (text.contains(" ")) {
                text= text.replaceAll(" ", "");
            }
        }
        // clearing the error, if it was previously set by some other values
        textInputLayout.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasPhoneText(editText,textInputLayout) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            textInputLayout.setError(errMsg);
            if(text.length() == 10 && text.startsWith("0") || text.startsWith("1")){
                textInputLayout.setError(errMsg);
            }
            return false;
        };

        return true;
    }


    public static boolean hasValue(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();

        if (text.length() == 0) {
            return false;
        }
        return true;
    }
    public static boolean hasValue(EditText editText) {

        String text = editText.getText().toString().trim();

        if (text.length() == 0) {
            return false;
        }
        return true;
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
          //  editText.requestFocus();
            return false;
        };

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
       // if ( required && !hasEmailText(editText) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
           // editText.setError(errMsg);
           // editText.requestFocus();
            return false;
        };

        return true;
    }
    public static boolean hasEmailText(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Enter valid email");
            return false;
        }

        return true;
    }
//    public static boolean isFNameValid(EditText editText, String regex, String errMsg, boolean required) {
//
//
//        String text = editText.getText().toString().trim();
//
//        // clearing the error, if it was previously set by some other values
//        editText.setError(null);
//
//        // text required and editText is blank, so return false
//        if ( required && !hasFirstText(editText) ) return false;
//
//        // pattern doesn't match so returning false
//        if (required && !Pattern.matches(regex, text)) {
//            editText.setError(errMsg);
//         //   editText.requestFocus();
//            return false;
//        };
//
//        return true;
//    }
//    public static boolean isLNameValid(EditText editText, String regex, String errMsg, boolean required) {
//
//
//        String text = editText.getText().toString().trim();
//
//        // clearing the error, if it was previously set by some other values
//        editText.setError(null);
//
//        // text required and editText is blank, so return false
//        if ( required && !hasLastText(editText) ) return false;
//
//        // pattern doesn't match so returning false
//        if (required && !Pattern.matches(regex, text)) {
//            editText.setError(errMsg);
//           // editText.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
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
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
           // editText.requestFocus();
            return false;
        }

        return true;
    }
    public static boolean hasPasswordText(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Enter password");

            return false;
        }

        return true;
    }
    public static boolean hasFirstText(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Enter first name");

            return false;
        }

        return true;
    }
    public static boolean hasPhoneText(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Enter phone number");

            return false;
        }

        return true;
    }
    public static boolean hasPhoneTextSignup(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Enter phone number");

            return false;
        }
        if (text.length()<10){
            textInputLayout.setError("Enter a valid phone number");

            return false;
        }

        return true;
    }
    public static boolean hasPhoneText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
          //  editText.setError("Enter valid phone number");

            return false;
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
          //  editText.setError("Enter firstname");

            return false;
        }

        return true;
    }
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
         //   editText.setError("Fill");

            return false;
        }

        return true;
    }
    public static boolean hasLastText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
           // editText.setError("Enter lastname");

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


    public static boolean hasPasswordMatched(EditText editTextP,EditText editTextC,TextInputLayout textInputLayout) {

        String textp = editTextP.getText().toString().trim();
        String textc = editTextC.getText().toString().trim();

        // length 0 means there is no text
        if( textp.length()>0) {
            if (!textp.equals(textc)) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError("Passwords should match");
                // editTextC.setBackgroundTintList();
                return false;
            }
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError("Enter Password");
            if(textc.length()>0){

            }
        }

        return true;
    }
    public static boolean hasEmailMatched(EditText editTextP,EditText editTextC,TextInputLayout textInputLayout) {

        String textp = editTextP.getText().toString().trim();
        String textc = editTextC.getText().toString().trim();

        // length 0 means there is no text
        if( textp.length()>0) {
            if (!textp.equals(textc)) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError("Email should match");
                // editTextC.setBackgroundTintList();
                return false;
            }
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError("Enter Email");
            if(textc.length()>0){

            }
        }

        return true;
    }
    public static boolean hasEmailMatchedUpdate(EditText editTextP,EditText editTextC,TextInputLayout textInputLayout) {

        String textp = editTextP.getText().toString().trim();
        String textc = editTextC.getText().toString().trim();

        // length 0 means there is no text
        if( textp.length()>0) {
            if (!textp.equals(textc)) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError("The Email and Confirm Email fields do not match.");
                // editTextC.setBackgroundTintList();
                return false;
            }
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError("Enter Confirm Email");
        }

        return true;
    }
    public static boolean hasAPNMatched(EditText editTextP,EditText editTextC,TextInputLayout textInputLayout) {

        String textp = editTextP.getText().toString().trim();
        String textc = editTextC.getText().toString().trim();

        // length 0 means there is no text
        if( textp.length()>0) {
            if (!textp.equals(textc)) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError("Apn number should match");
                // editTextC.setBackgroundTintList();
                return false;
            }
        }else{
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError("Enter Apn number");
            if(textc.length()>0){

            }
        }

        return true;
    }
    public static boolean hasDocText(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        // textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textInputLayout.setError("Please enter documentName");
            return false;
        }

        return true;
    }

    public static boolean hasJournalFee(EditText editText,TextInputLayout textInputLayout) {

        String text = editText.getText().toString().trim();
        // textInputLayout.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            //textInputLayout.setError("Please enter documentName");
            return false;
        }

        return true;
    }


}
