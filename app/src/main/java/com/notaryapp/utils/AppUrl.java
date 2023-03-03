package com.notaryapp.utils;

public class AppUrl {

    private static final String API_DEV_URL = "YOUR_DEV_URL";
    private static final String API_UAT_URL = "YOUR_UAT_URL";
    
    public static final String API_PRODUCTION_URL = "YOUR_PRODUCTION_URL";
    //Change URL Below to switch DEV and UAT

    //private static final String API_DECIDER_URL = API_DEV_URL;
    /*public static final String API_DECIDER_URL = API_UAT_URL;
    public static final String API_BASE_URL = API_UAT_URL+"api/v1/notaryapp/registration/";
    public static final String API_CONFIG_BASE_URL = API_DECIDER_URL+"api/v1/";
    public static final String API_PAYMENT_URL = API_DECIDER_URL+"api/v1/notaryapp/stripe/";
    public static final String API_JUMIO_URL = API_DECIDER_URL+"api/v1/jumio/";
    public static final String VERIFY_AUTHENTICATE_URL = API_DECIDER_URL+"api/v1/verifyauth/";
    public static final String GENERATE_PROFILE = API_DECIDER_URL+"profileurl";
    //notaryapp   
    public static final String GET_COUPON_CODE = "DEV_URL";
    public static final String GET_TS_TRANS_DETAILS = "DEV_URL";

    //VEJ
    public static final String GENERATE_PDF_VEJ = API_DECIDER_URL+"genVEJPDF";
    public static final String DOWNLOAD_PDF_VEJ = API_DECIDER_URL+"Detailed-Report-";*/


    /*************Production***************/
    public static final String API_DECIDER_URL = API_PRODUCTION_URL;
    public static final String API_BASE_URL = API_PRODUCTION_URL+"api/v1/notaryapp/registration/";
    public static final String API_CONFIG_BASE_URL = API_PRODUCTION_URL+"api/v1/";
    public static final String API_PAYMENT_URL = API_PRODUCTION_URL+"api/v1/notaryapp/stripe/";
    public static final String VERIFY_AUTHENTICATE_URL = API_PRODUCTION_URL+"api/v1/verifyauth/";
    public static final String API_JUMIO_URL = API_PRODUCTION_URL+"api/v1/jumio/";
    public static final String GENERATE_PROFILE = API_PRODUCTION_URL+"profileurl";
    //notaryapp
    public static final String GET_COUPON_CODE = "CUPON_URL";
    public static final String GET_TS_TRANS_DETAILS = "DETAILS_URL";
    //VEJ
    public static final String GENERATE_PDF_VEJ = API_PRODUCTION_URL+"genVEJPDF";
    public static final String DOWNLOAD_PDF_VEJ = API_DECIDER_URL+"Detailed-Report-";




    /*************Production*************\\\**/

    // JUMIO
    public static final String JUMIO_BASE_URL = "JUMIO_BASE_URL" ;
    //Doc
    public static final String VIEW_PDF = "PDF_VIEW_URL";
    //profileUrl
//    public static final String GENERATE_PROFILE = API_UAT_URL+"8085/profileurl";

    //Registration
    //public static final String LOGIN = API_BASE_URL + "authenticate";
    public static final String LOGIN = API_BASE_URL + "authenticate-v2";
    public static final String REGISTER_NOTARY = API_BASE_URL + "registerNotary";
    public static final String GENOTP_URL = API_BASE_URL + "genOtp";
    public static final String OTPCHK_URL = API_BASE_URL + "otpCheck";
    public static final String EMAILCHECK_FPWD = API_BASE_URL + "emailCheckFPwd";
    public static final String GENOTP_CHANGE_PWD = API_BASE_URL + "genOtpforPwd";
    public static final String CHECKOTP_CHANGE_PWD = API_BASE_URL + "checkOtpForPwd";
    public static final String FORGOT_PWD = API_BASE_URL + "forgot";
    public static final String GET_NOTARY_LICENSE_DETAILS = API_BASE_URL + "notaryLicenceByState";
    public static final String GET_STATE_LIST = API_BASE_URL + "getStates";
    public static final String INSERT_NOTARY_LICENSE = API_BASE_URL + "addNewLicense";
    public static final String UPDATE_NOTARY_LICENSE = API_BASE_URL + "updateNewLicense";
    public static final String UPLOAD_STAMP = API_BASE_URL + "uploadStamps";
    public static final String NOTARY_PLEDGE = API_BASE_URL + "pledge";
    public static final String ID_CHECK = API_BASE_URL + "idCheckDone";
    public static final String UPDATE_PROFILE = API_BASE_URL + "updateNotaryProfile";
    public static final String GET_PROFILE = API_BASE_URL + "getNotaryProfile";
    public static final String LICENSE_LIST = API_BASE_URL + "licenceList";
    public static final String STAMPS_DOWNLOAD = API_BASE_URL + "getStamps";
    public static final String VA_STAMPS_DOWNLOAD = API_BASE_URL + "stampDownloader";
    public static final String ENROLL_NOTARY = API_BASE_URL + "enrollNotary";
    public static final String REGISTER_CLIENT = API_BASE_URL + "registerClient";
    public static final String SAVE_DEVICE = API_BASE_URL + "deviceMarker";
    public static final String DEVICE_TRACER = API_BASE_URL + "deviceMarker";
    public static final String UPLOAD_NOTARY_DP = API_BASE_URL + "insertDP";
    public static final String UPLOAD_CLIENT_SIGNER_WITNESS_DP = API_BASE_URL + "insertPics";
    //Jumio
    public static final String GET_JUMIO_SCANDETAILS = API_JUMIO_URL + "jumioData";
    //Config
    public static final String GET_PUBLIC_KEY = API_CONFIG_BASE_URL + "config/getStripePk";
    public static final String GET_JUMIO_KEYS = API_CONFIG_BASE_URL + "config/getJumioKeys";
    public static final String GET_ALLINFO = API_CONFIG_BASE_URL + "info/getAllInfo";
    public static final String GET_FAQ = API_CONFIG_BASE_URL + "faq/getFAQ";
    public static final String GET_DOC_TYPES = API_CONFIG_BASE_URL + "dType/getDTypes";
    public static final String GET_PRIVACY_POLICY = API_CONFIG_BASE_URL + "policy/getPolicyDocs?docType=privacy-policy";
    public static final String GET_TERMS_CONDITION = API_CONFIG_BASE_URL + "policy/getPolicyDocs?docType=terms-conditions";
    public static final String GET_DISCLAIMER= API_CONFIG_BASE_URL + "policy/getPolicyDocs?docType=disclaimer";
    //VerifyAuthenticate
    public static final String CREATE_TRANSACTION = VERIFY_AUTHENTICATE_URL + "createTran";
    public static final String SAVE_SIGNER = VERIFY_AUTHENTICATE_URL + "updateTran";
    public static final String SAVE_WITNESS = VERIFY_AUTHENTICATE_URL + "updateTranW";
    public static final String SEAL_CODES = VERIFY_AUTHENTICATE_URL + "sealGen";
    public static final String SEAL_MODIFY_CODES = VERIFY_AUTHENTICATE_URL + "modifySeal";
    public static final String SIGN_DOC = VERIFY_AUTHENTICATE_URL + "docSigned";
    public static final String ADD_SEAL_COMP = VERIFY_AUTHENTICATE_URL + "completeSeal";
    public static final String ADD_DOC = VERIFY_AUTHENTICATE_URL + "addNotaryDoc";
    public static final String ADD_DOC_COMP = VERIFY_AUTHENTICATE_URL + "completeDocUploads";
    public static final String LOCATION_ADD = VERIFY_AUTHENTICATE_URL + "locationUpdate";
    public static final String COMPLETE_TRANSACTION = VERIFY_AUTHENTICATE_URL + "tranComplete";
    public static final String ALL_TRANSACTIONS = VERIFY_AUTHENTICATE_URL + "getTransactions";
    public static final String STAR_TRANS = VERIFY_AUTHENTICATE_URL + "starTran";
    public static final String PENDING_TRANSACTIONS = VERIFY_AUTHENTICATE_URL + "getSignerTrans";
    public static final String DELETE_DOC = VERIFY_AUTHENTICATE_URL + "deleteDoc";
    public static final String GENERATE_PDF = VERIFY_AUTHENTICATE_URL + "sharePDF";
    public static final String UPLOAD_DOC_VL = VERIFY_AUTHENTICATE_URL + "saveNotaryAppDoc";
    public static final String CHECK_EMAIL = VERIFY_AUTHENTICATE_URL + "checkEmail";
    public static final String PAGENATED_TRANSACTIONS = VERIFY_AUTHENTICATE_URL + "getTransByPage?username=";
    //Payment
    public static final String UPDATE_TRANSACTIONS = API_PAYMENT_URL+"subtract-unit";
    public static final String GET_PAYMENT_HISTORY = API_PAYMENT_URL+"my-transactions";
    public static final String CREATE_CUSTOMER = API_PAYMENT_URL+"create-customer";
    public static final String GET_ALL_PLANS = API_PAYMENT_URL+"app-all-plans";
    public static final String GET_ALL_PRICING_PLANS = API_PAYMENT_URL+"app-all-pricing-plans";
    //public static final String CHK_TRAN_COUNT = API_PAYMENT_URL+"app-user-status";
    public static final String CHK_TRAN_COUNT = API_PAYMENT_URL+"app-user-status-v2";
    public static final String CREATE_ORDER = API_PAYMENT_URL+"create-order";
    public static final String CREATE_SUB_WITH_ORDER = API_PAYMENT_URL+"create-subscription-with-order";
    public static final String CREATE_PAYMENT_INTENT_WITH_ORDER = API_PAYMENT_URL+"create-payment-intent-with-order";
    public static final String CONFIRM_PAYMENT_INTENT = API_PAYMENT_URL+"confirm-payment-intent";
    public static final String CANCEL_SUBSCRIPTION = API_PAYMENT_URL+"cancel-subscription";

    //Ephemeral Key
    public static final String EPHEMERAL_KEY = API_PAYMENT_URL+"ephemeral-Key?";


    public static final String DEVICE = "Android";
    public static final String DEVICE_TYPE = "UUID";

    //https://uat.notaryapp.com/api/v1/verifyauth/gencrashlog
    public static final String CRASH_LOG = VERIFY_AUTHENTICATE_URL + "gencrashlog";


    //Image Load
    public static final String IMAGE_LOAD = VERIFY_AUTHENTICATE_URL + "resizeImage?width=100&url=";


    //LAD
    public static final String CREATE_TRANSACTION_LAD = VERIFY_AUTHENTICATE_URL + "createTranL";
    public static final String LAD_CODES = VERIFY_AUTHENTICATE_URL + "sealGenL";

    //VEJ
    public static final String CREATE_TRANSACTION_VEJ = VERIFY_AUTHENTICATE_URL + "createTranV";
    //public static final String UPLOAD_SIGNATURE_VEJ = API_BASE_URL + "saveSignerSign";
    public static final String UPLOAD_SIGNATURE_VEJ = API_BASE_URL + "saveSignerSign";
    public static final String UPLOAD_THUMBPRINT_VEJ = API_BASE_URL + "saveSignerThumb";
    public static final String ADD_JOURNAL = API_BASE_URL + "saveSignerJournal";
    public static final String ADD_NOTE = API_BASE_URL + "saveSignerNote";


    //Authorization_key
    public static final String Authorization_Key = "AUTH_STRING";

    // Reactivate & Deactivate
    public static final String REACTIVATE = API_BASE_URL + "reactive";
    public static final String DEACTIVATE = API_BASE_URL + "inactive";

    // Server Notification
    public static final String GET_SERVER_NOTIFICATION = API_BASE_URL + "getServerNotification";
}
