package utils;

public final class EventTypes {

    public static final String ACCOUNT_REQUESTED = "AccountRequested";
    public static final String ACCOUNT_REQUEST_COMPLETED = "AccountRequestCompleted";
    public static final String ACCOUNT_REQUEST_FAILED = "AccountRequestFailed";
    public static final String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
    public static final String ACCOUNT_REGISTRATION_COMPLETED = "AccountRegistrationCompleted";
    public static final String ACCOUNT_REGISTRATION_FAILED = "AccountRegistrationFailed";

    public static final String PAYMENT_SUCCESS = "PaymentSuccess";
    public static final String PAYMENT_FAILED = "PaymentFailed";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";

    public static final String TOKEN_REQUESTED = "RequestToken";
    public static final String TOKEN_REQUEST_COMPLETED = "TokenRequestCompleted";
    public static final String TOKEN_REQUEST_FAILED = "TokenRequestFailed";

    public static final String MANAGER_REPORT_REQUESTED = "ManagerReportRequested";
    public static final String MANAGER_REPORT_REQUEST_COMPLETED = "ManagerReportRequestCompleted";
    public static final String MANAGER_REPORT_REQUEST_FAILED = "ManagerReportRequestFailed";

    public static final String CUSTOMER_REPORT_REQUESTED = "CustomerReportRequested";
    public static final String CUSTOMER_REPORT_REQUEST_COMPLETED = "CustomerReportRequestCompleted";
    public static final String CUSTOMER_REPORT_REQUEST_FAILED = "CustomerReportRequestFailed";

    public static final String MERCHANT_REPORT_REQUESTED = "MerchantReportRequested";
    public static final String MERCHANT_REPORT_REQUEST_COMPLETED = "MerchantReportRequestCompleted";
    public static final String MERCHANT_REPORT_REQUEST_FAILED = "MerchantReportRequestFailed";



    private EventTypes(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
