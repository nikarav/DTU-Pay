package utils;

public final class EventTypes {

    public static final String BANK_ACCOUNT_ID_REQUESTED = "BankAccountIdRequested";
    public static final String BANK_ACCOUNT_ID_RETRIEVAL_SUCCESS = "BankAccountIdRetrievalSucceeded";
    public static final String BANK_ACCOUNT_ID_RETRIEVAL_FAILED = "BankAccountIdRetrievalFailed";

    public static final String PAYMENT_SUCCESS = "PaymentSuccess";
    public static final String PAYMENT_FAILED = "PaymentFailed";
    public static final String PAYMENT_REQUESTED = "PaymentRequested";

    public static final String USE_TOKEN_REQUESTED = "UseToken";
    public static final String USE_TOKEN_COMPLETED = "UseTokenCompleted";
    public static final String USE_TOKEN_FAILED = "UseTokenFailed";

    public static final String TRANSACTIONS_REQUEST = "TransactionsRequested";
    public static final String TRANSACTIONS_REQUEST_SUCCESS = "TransactionsRequestSucceeded";
    public static final String TRANSACTIONS_REQUEST_FAILED = "TransactionsRequestFailed";

    public static final String CUSTOMER_TRANSACTIONS_REQUEST = "CustomerTransactionsRequested";
    public static final String CUSTOMER_TRANSACTIONS_REQUEST_SUCCESS = "CustomerTransactionsRequestSucceeded";
    public static final String CUSTOMER_TRANSACTIONS_REQUEST_FAILED = "CustomerTransactionsRequestFailed";

    public static final String MERCHANT_TRANSACTIONS_REQUEST = "MerchantTransactionsRequested";
    public static final String MERCHANT_TRANSACTIONS_REQUEST_SUCCESS = "MerchantTransactionsRequestSucceeded";
    public static final String MERCHANT_TRANSACTIONS_REQUEST_FAILED = "MerchantTransactionsRequestFailed";

    private EventTypes(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
