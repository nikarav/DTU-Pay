package service;

import dto.ReturnTransactionInfo;
import messaging.Event;
import messaging.MessageQueue;
import utils.EventTypes;
import utils.Helpers;

import java.util.Map;
import java.util.concurrent.*;


public class ReportService<T> {

    private MessageQueue queue;
    private Map<String, CompletableFuture<T>> reportsRequests = new ConcurrentHashMap<>();
    private Helpers helpers = new Helpers();


    public ReportService(MessageQueue queue){
        System.out.println("Report service queue has started");
        this.queue = queue;

        // Manager
        this.queue.addHandler(EventTypes.MANAGER_REPORT_REQUEST_COMPLETED, this::handleManagerReportRequestCompleted);
        this.queue.addHandler(EventTypes.MANAGER_REPORT_REQUEST_FAILED, this::handleManagerReportFailed);

        // Customer
        this.queue.addHandler(EventTypes.CUSTOMER_REPORT_REQUEST_COMPLETED, this::handleCustomerReportRequestCompleted);
        this.queue.addHandler(EventTypes.CUSTOMER_REPORT_REQUEST_FAILED, this::handleCustomerReportRequestFailed);

        // Merchant
        this.queue.addHandler(EventTypes.MERCHANT_REPORT_REQUEST_COMPLETED, this::handleMerchantReportCompleted);
        this.queue.addHandler(EventTypes.MERCHANT_REPORT_REQUEST_FAILED, this::handleMerchantReportFailed);
    }


    //Get manager Report
    public ReturnTransactionInfo getReportManager() throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();

        System.out.println("Before " + reportsRequests.keySet());

        reportsRequests.put(corrID, completableFuture);

        System.out.println("After " + reportsRequests.keySet());


        Event event = new Event(EventTypes.MANAGER_REPORT_REQUESTED, corrID, new Object[] {});
        queue.publish(event);

        ReturnTransactionInfo response;

        try{
            //wait for response
            response = (ReturnTransactionInfo) reportsRequests.get(corrID).join();

        } catch (CancellationException | CompletionException e){
            reportsRequests.remove(corrID, completableFuture);
            throw new Exception(e.getMessage());
        }

        reportsRequests.remove(corrID, completableFuture);
        //read response
        if (response.isSuccess()) {
            return response;
        } else {
            throw new Exception(response.getErrorMessage());
        }
    }

    //customer
    public ReturnTransactionInfo getReportCustomer(String id) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        reportsRequests.put(corrID, completableFuture);

        Event event = new Event(EventTypes.CUSTOMER_REPORT_REQUESTED, corrID, new Object[] {id});
        queue.publish(event);

        ReturnTransactionInfo response;

        try{
            //wait for response
            response = (ReturnTransactionInfo) reportsRequests.get(corrID).join();

        } catch (CancellationException | CompletionException e){
            reportsRequests.remove(corrID, completableFuture);
            throw new Exception(e.getMessage());
        }

        reportsRequests.remove(corrID, completableFuture);

        //read response
        if (response.isSuccess()) {
            return response;
        } else {
            throw new Exception(response.getErrorMessage());
        }
    }

    //merchant
    public ReturnTransactionInfo getReportMerchant(String id) throws Exception {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        String corrID = helpers.generateCorrelationID();
        reportsRequests.put(corrID, completableFuture);

        Event event = new Event(EventTypes.MERCHANT_REPORT_REQUESTED, corrID, new Object[] {id});
        queue.publish(event);

        ReturnTransactionInfo response;

        try{
            //wait for response
            response = (ReturnTransactionInfo) reportsRequests.get(corrID).join();

        } catch (CancellationException | CompletionException e){
            reportsRequests.remove(corrID, completableFuture);
            throw new Exception(e.getMessage());
        }

        reportsRequests.remove(corrID, completableFuture);
        //read response
        if (response.isSuccess()) {
            return response;
        } else {
            throw new Exception(response.getErrorMessage());
        }
    }

    private void handleReportEvents(Event event) {
        String receivedCorrID = event.getCorrID();
        String eventType = event.getType();

        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);


        System.out.println("KEYS:" + reportsRequests.keySet());
        if(!reportsRequests.containsKey(receivedCorrID)){
            System.out.println("CorrelationID: " + receivedCorrID + " does not exist.");
            return;
        }

        System.out.println("CorrelationID: " + receivedCorrID + " is credible");

        var currentCompletableFuture = reportsRequests.get(receivedCorrID);


        switch (eventType){
            case EventTypes.MANAGER_REPORT_REQUEST_COMPLETED:
                var responseReportManagerSuccess = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for manager retrieval complete");
                currentCompletableFuture.complete((T) responseReportManagerSuccess);
                break;
            case EventTypes.MANAGER_REPORT_REQUEST_FAILED:
                var responseReportManagerFail = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for manager retrieval complete" + responseReportManagerFail.getErrorMessage());
                currentCompletableFuture.complete((T) responseReportManagerFail);
                break;
            case EventTypes.CUSTOMER_REPORT_REQUEST_COMPLETED:
                var responseReportCustomerSuccess = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for customer retrieval complete");
                currentCompletableFuture.complete((T) responseReportCustomerSuccess);
                break;
            case EventTypes.CUSTOMER_REPORT_REQUEST_FAILED:
                var responseReportCustomerFail = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for customer retrieval complete:" + responseReportCustomerFail.getErrorMessage());
                currentCompletableFuture.complete((T) responseReportCustomerFail);
                break;
            case EventTypes.MERCHANT_REPORT_REQUEST_COMPLETED:
                var responseReportMerchantSuccess = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for merchant retrieval complete");
                currentCompletableFuture.complete((T) responseReportMerchantSuccess);
                break;
            case EventTypes.MERCHANT_REPORT_REQUEST_FAILED:
                var responseReportMerchantFail = event.getArgument(0, ReturnTransactionInfo.class);
                System.out.println("Transaction for merchant retrieval complete:" + responseReportMerchantFail.getErrorMessage());
                currentCompletableFuture.complete((T) responseReportMerchantFail);
                break;
        }
    }

    // MANAGER
    public void handleManagerReportFailed(Event event) {
        handleReportEvents(event);
    }

    public void handleManagerReportRequestCompleted(Event event) {
        handleReportEvents(event);
    }

    // CUSTOMER
    public void handleCustomerReportRequestFailed(Event event) {
        handleReportEvents(event);
    }

    public void handleCustomerReportRequestCompleted(Event event) {
        handleReportEvents(event);
    }

    // MERCHANT
    private void handleMerchantReportFailed(Event event) {
        handleReportEvents(event);
    }

    private void handleMerchantReportCompleted(Event event) {
        handleReportEvents(event);
    }


}
