package service;

import dto.RequestTokens;
import messaging.Event;
import messaging.MessageQueue;
import utils.EventTypes;
import utils.Helpers;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService<T> {

    private MessageQueue queue;
    private Map<String, CompletableFuture<T>> tokenRequests =  new ConcurrentHashMap<String, CompletableFuture<T>>();
    private Helpers helpers =  new Helpers();

    public TokenService(MessageQueue q) {
        queue = q;

        //Get Tokens
        queue.addHandler(EventTypes.TOKEN_REQUEST_COMPLETED, this::handleTokenRequestCompleted);
        queue.addHandler(EventTypes.TOKEN_REQUEST_FAILED, this::handleTokenRequestFailed);
    }

    //    Post tokens
    public T getTokens(RequestTokens reqTokens) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        tokenRequests.put(corrID, completableFuture);

        // Create an "RequestToken" event and
        // publish it to the EXCHANGE_TOKEN exchange.

        Event event = new Event(EventTypes.TOKEN_REQUESTED, corrID, new Object[] { reqTokens.getCid(), reqTokens.getAmount() });
        queue.publish(event);

        T response;

        try {
            //wait for response
            response = (T) tokenRequests.get(corrID).join();
        }catch (CancellationException | CompletionException e) {
            tokenRequests.remove(corrID, completableFuture);
            throw new Exception(e);
        }

        tokenRequests.remove(corrID, completableFuture);
        return response;
    }

    public Event handleTokenRequestCompleted(Event event) {
        handleAccountEvents(event);
        return event;
    }

    public Event handleTokenRequestFailed(Event event) {
        handleAccountEvents(event);
        return event;
    }

    public void handleAccountEvents(Event e) {
        String receivedCorrID = e.getCorrID();
        String eventType = e.getType();
        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);

        // Get correlation id and check if it exists inside the hashmap
        if (!tokenRequests.containsKey(receivedCorrID)){
            // correlation id does not exist
            System.out.println("CorrelationID: " + receivedCorrID + " does not exist.");
            return;
        }

        System.out.println("CorrelationID: " + receivedCorrID + " is credible");

        var currentCompletableFuture = tokenRequests.get(receivedCorrID);


        switch (eventType){
            case EventTypes.TOKEN_REQUEST_COMPLETED:
                ArrayList<UUID> tokens = new ArrayList<>();
                var tokensReceived = e.getArgument(0, Integer.class);
                System.out.println("Received " + tokensReceived + " Tokens");
                for (int i = 1; i < tokensReceived + 1; i++ ){
                    // Read the tokens one by one
                    tokens.add(e.getArgument(i, UUID.class));
                }
                currentCompletableFuture.complete((T) tokens);
                break;
            case EventTypes.TOKEN_REQUEST_FAILED:
                var tokensFail = e.getArgument(0, String.class);
                System.out.println("Account retrieval failed: " + tokensFail);
                currentCompletableFuture.complete((T) tokensFail);
                break;
            default:
                System.out.println("Error could not map Event Type");
        }
    }
}
