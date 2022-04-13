package utils;

public final class EventTypes {

    public static final String ACCOUNT_REQUESTED = "AccountRequested";
    public static final String ACCOUNT_REQUEST_COMPLETED = "AccountRequestCompleted";
    public static final String ACCOUNT_REQUEST_FAILED = "AccountRequestFailed";

    public static final String ACCOUNT_REGISTRATION_REQUESTED = "AccountRegistrationRequested";
    public static final String ACCOUNT_REGISTRATION_COMPLETED = "AccountRegistrationCompleted";
    public static final String ACCOUNT_REGISTRATION_FAILED = "AccountRegistrationFailed";

    public static final String DELETE_ACCOUNT_REQUESTED = "DeleteAccountRequested";
    public static final String DELETE_ACCOUNT_SUCCESS = "DeleteAccountRequested";
    public static final String DELETE_ACCOUNT_NOT_EXIST = "DeleteAccountRequested";
    public static final String DELETE_ACCOUNT_FAILED = "DeleteAccountRequested";

    public static final String BANK_ACCOUNT_ID_REQUESTED = "BankAccountIdRequested";
    public static final String BANK_ACCOUNT_ID_RETRIEVAL_SUCCESS = "BankAccountIdRetrievalSucceeded";
    public static final String BANK_ACCOUNT_ID_RETRIEVAL_FAILED = "BankAccountIdRetrievalFailed";



    private EventTypes(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
