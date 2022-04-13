package service;

import dto.RequestPaymentData;
import messaging.Event;
import messaging.MessageQueue;
import utils.EventTypes;
import utils.Helpers;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;


public class PaymentService<T> {
    private MessageQueue queue;
    private Map<String, CompletableFuture<T>>  payRequests = new ConcurrentHashMap<String, CompletableFuture<T>>();
    private Helpers helpers  = new Helpers();

    public PaymentService(MessageQueue q) {
        queue = q;

        //Request Payment
        queue.addHandler(EventTypes.PAYMENT_SUCCESS, this::handlePaymentSuccess);
        queue.addHandler(EventTypes.PAYMENT_FAILED, this::handlePaymentFailure);
    }


    public T createPayment(RequestPaymentData paymentData) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        payRequests.put(corrID, completableFuture);

        // Create an "PaymentRequested" event
        Event event = new Event(EventTypes.PAYMENT_REQUESTED, corrID, new Object[] { paymentData });
        queue.publish(event);

        T response;

        try {
            //wait for response
            response = ((T) payRequests.get(corrID).join());
        }catch (CancellationException | CompletionException e) {
            payRequests.remove(corrID, completableFuture);
            throw new Exception(e);
        }

        payRequests.remove(corrID, completableFuture);
        return response;
    }

    public void handlePaymentSuccess(Event e){
        handleEvents(e);
    }

    public void handlePaymentFailure(Event e){
        handleEvents(e);
    }

    public void handleEvents(Event e) {
        String recCorrID = e.getCorrID();
        String eventType = e.getType();
        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + recCorrID);

        // Get correlation id and check if it exists inside the hashmap
        if (!payRequests.containsKey(recCorrID)){
            // correlation id does not exist
            System.out.println("CorrelationID: " + recCorrID + " does not exist.");
            return;
        }

        System.out.println("CorrelationID: " + recCorrID + " is credible");

        var currentCompletableFuture = payRequests.get(recCorrID);

        switch (eventType){
            case EventTypes.PAYMENT_SUCCESS:
                var respPaySuc = e.getArgument(0, Boolean.class);
                System.out.println("Payment Successful");
                currentCompletableFuture.complete((T) respPaySuc);
                break;
            case EventTypes.PAYMENT_FAILED:
                var respPayFail = e.getArgument(0, String.class);
                System.out.println("Payment failed: ");
                currentCompletableFuture.complete((T) respPayFail);
                break;
            default:
                System.out.println("Error could not map Event Type");
        }
    }
}
