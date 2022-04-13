package utils;

public final class EventTypes {

    public static final String USE_TOKEN_REQUESTED = "UseToken";
    public static final String USE_TOKEN_COMPLETED = "UseTokenCompleted";
    public static final String USE_TOKEN_FAILED = "UseTokenFailed";

    public static final String TOKEN_REQUESTED = "RequestToken";
    public static final String TOKEN_REQUEST_COMPLETED = "TokenRequestCompleted";
    public static final String TOKEN_REQUEST_FAILED = "TokenRequestFailed";

    public static final String GET_DATA_REQUEST = "GetData";
    public static final String GET_DATA_SUCCESS = "GetDataCompleted";

    private EventTypes(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }

}
