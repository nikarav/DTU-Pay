package service;


import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import utils.*;
import dto.ReturnAccount;
import dto.ReturnAccountInfo;
import messaging.Event;
import messaging.MessageQueue;
import entities.Account;

public class AccountService<T> {

    private MessageQueue queue;
    private Map<String, CompletableFuture<T>> accRequests  = new ConcurrentHashMap<>();;
    private Helpers helpers = helpers = new Helpers();;

    public AccountService(MessageQueue q) {
        queue = q;

        //Get Account
        queue.addHandler(EventTypes.ACCOUNT_REQUEST_COMPLETED, this::handleAccountGetInfoRequestCompleted);
        queue.addHandler(EventTypes.ACCOUNT_REQUEST_FAILED, this::handleAccountGetInfoRequestFailed);

        //Create Account
        queue.addHandler(EventTypes.ACCOUNT_REGISTRATION_COMPLETED, this::handleAccountRegistrationCompleted);
        queue.addHandler(EventTypes.ACCOUNT_REGISTRATION_FAILED, this::handleAccountRegistrationFailed);

    }

//    Get Account
    public ReturnAccount getAccount(String accountId) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        accRequests.put(corrID, completableFuture);

        // Create an "AccountRequested" event

        Event event = new Event(EventTypes.ACCOUNT_REQUESTED, corrID, new Object[] { accountId });
        queue.publish(event);

        ReturnAccount response;

        try {
            //wait for response
            response = ((ReturnAccount) accRequests.get(corrID).join());
        }catch (CancellationException | CompletionException e) {
            accRequests.remove(corrID, completableFuture);
            throw new Exception(e.getMessage());
        }

        accRequests.remove(corrID, completableFuture);
        return response;
    }

    public void handleAccountGetInfoRequestCompleted(Event e){
        handleAccountEvents(e);
    }

    public void handleAccountGetInfoRequestFailed(Event e){
        handleAccountEvents(e);
    }

//    Create Account
    public T registerAccount(Account s) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        accRequests.put(corrID, completableFuture);

        // Create an "AccountRegistrationRequested" event
        Event event = new Event(EventTypes.ACCOUNT_REGISTRATION_REQUESTED, corrID, new Object[]{s});
        queue.publish(event);

        T response;

        try {
            //wait for response
            response = ((T) accRequests.get(corrID).join());
        }catch (CancellationException | CompletionException e) {
            accRequests.remove(corrID, completableFuture);
            throw new Exception(e.getMessage());
        }

        accRequests.remove(corrID, completableFuture);
        return response;
    }

    public void handleAccountRegistrationCompleted(Event e) {
        handleAccountEvents(e);
    }

    public void handleAccountRegistrationFailed(Event e) {
        handleAccountEvents(e);
    }

    public void handleAccountEvents(Event e) {
        String receivedCorrID = e.getCorrID();
        String eventType = e.getType();
        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);

        // Get correlation id and check if it exists inside the hashmap
        if (!accRequests.containsKey(receivedCorrID)){
            // correlation id does not exist
            System.out.println("CorrelationID: " + receivedCorrID + " does not exist.");
            return;
        }

        System.out.println("CorrelationID: " + receivedCorrID + " is credible");

        var currentCompletableFuture = accRequests.get(receivedCorrID);

        switch (eventType){
            case EventTypes.ACCOUNT_REQUEST_COMPLETED:
                var respAccInfoSuc = e.getArgument(0, ReturnAccount.class);
                System.out.println("Account information retrieval complete");
                currentCompletableFuture.complete((T) respAccInfoSuc);
                break;
            case EventTypes.ACCOUNT_REQUEST_FAILED:
                var respAccInfoFail = e.getArgument(0, ReturnAccount.class);
                System.out.println("Account retrieval failed: " + respAccInfoFail.getErrorMessage());
                currentCompletableFuture.complete((T) respAccInfoFail);
                break;
            case EventTypes.ACCOUNT_REGISTRATION_COMPLETED:
                var accountId = e.getArgument(0, String.class);
                System.out.println("Account registration complete with accountId: " + accountId);
                ReturnAccountInfo accInfoSucc = new ReturnAccountInfo();
                accInfoSucc.setAccountId(accountId);
                accInfoSucc.setErrorMessage("");
                currentCompletableFuture.complete((T) accInfoSucc);
                break;
            case EventTypes.ACCOUNT_REGISTRATION_FAILED:
                var errorMessage = e.getArgument(0, String.class);
                System.out.println("Account registration failed: " + errorMessage);
                ReturnAccountInfo accInfoFail = new ReturnAccountInfo();
                accInfoFail.setAccountId("");
                accInfoFail.setErrorMessage(errorMessage);
                currentCompletableFuture.complete((T) accInfoFail);
                break;
            default:
                System.out.println("Error could not map Event Type");
        }
    }
}
