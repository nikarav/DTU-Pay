package services;

import dto.ReturnTransactionInfo;
import dto.Transaction;
import messaging.Event;
import messaging.MessageQueue;

import utils.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ReportingService {

    MessageQueue queue;
    public Map<String, Object[] > requestState;

    public ReportingService(MessageQueue q) {
        this.queue = q;

        // MANAGER
        this.queue.addHandler(EventTypes.MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
        this.queue.addHandler(EventTypes.TRANSACTIONS_REQUEST_SUCCESS, this::handleTransactionsRequestSucceeded);
        this.queue.addHandler(EventTypes.TRANSACTIONS_REQUEST_FAILED, this::handleTransactionsRequestFailed);

        // CUSTOMER
        this.queue.addHandler(EventTypes.CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequested);
        this.queue.addHandler(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST_SUCCESS, this::handleCustomerTransactionsRequestSucceeded);
        this.queue.addHandler(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST_FAILED, this::handleCustomerTransactionsRequestFailed);

        // MERCHANT
        this.queue.addHandler(EventTypes.MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequested);
        this.queue.addHandler(EventTypes.MERCHANT_TRANSACTIONS_REQUEST_SUCCESS, this::handleMerchantTransactionsRequestSucceeded);
        this.queue.addHandler(EventTypes.MERCHANT_TRANSACTIONS_REQUEST_FAILED, this::handleMerchantTransactionsRequestFailed);

        requestState = new ConcurrentHashMap<>();
    }


    public Event handleManagerReportRequested(Event inEvent){
        checkCorrelationID(inEvent);

        requestState.put(inEvent.getCorrID(), new Object[]{});

        // Publish event for handling by Payment
        Event getManagerTransactions = new Event(EventTypes.TRANSACTIONS_REQUEST, inEvent.getCorrID(), new Object[] {});
        queue.publish(getManagerTransactions);

        return getManagerTransactions;
    }

    public Event handleTransactionsRequestSucceeded(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        var transactions = inEvent.getArgument(0, List.class);
        outEvent = new Event(EventTypes.MANAGER_REPORT_REQUEST_COMPLETED, inEvent.getCorrID(), new Object[] {new ReturnTransactionInfo(true, "", transactions)});

        queue.publish(outEvent);

        return outEvent;
    }

    public Event handleTransactionsRequestFailed(Event inEvent){
        checkCorrelationID(inEvent);

        var error = inEvent.getArgument(0, String.class);
        System.out.println("Transactions retrieval failed");

        Event outEvent = new Event(EventTypes.MANAGER_REPORT_REQUEST_FAILED, inEvent.getCorrID(), new Object[] { new ReturnTransactionInfo(false, error, Collections.<Transaction> emptyList())});
        queue.publish(outEvent);

        requestState.remove(inEvent.getCorrID());

        return outEvent;

    }

    public Event handleCustomerReportRequested(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;
        String type;

        var customerId = inEvent.getArgument(0, String.class);
        requestState.put(inEvent.getCorrID(), new Object[]{customerId});

        // Publish event for handling by Payment
        Event getManagerTransactions = new Event(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST, inEvent.getCorrID(), new Object[] {customerId});
        queue.publish(getManagerTransactions);

        return getManagerTransactions;

    }

    public Event handleCustomerTransactionsRequestSucceeded(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        var transactions = inEvent.getArgument(0, List.class);
        outEvent = new Event(EventTypes.CUSTOMER_REPORT_REQUEST_COMPLETED, inEvent.getCorrID(), new Object[] {new ReturnTransactionInfo(true, "", transactions)});

        queue.publish(outEvent);

        return outEvent;

    }

    public Event handleCustomerTransactionsRequestFailed(Event inEvent){
        checkCorrelationID(inEvent);

        var error = inEvent.getArgument(0, String.class);

        Event outEvent = new Event(EventTypes.CUSTOMER_REPORT_REQUEST_FAILED, inEvent.getCorrID(), new Object[] { new ReturnTransactionInfo(false, error, Collections.<Transaction> emptyList())});
        queue.publish(outEvent);

        requestState.remove(inEvent.getCorrID());

        return outEvent;

    }

    public Event handleMerchantReportRequested(Event inEvent) {
        checkCorrelationID(inEvent);
        Event outEvent;
        String type;

        var merchantId = inEvent.getArgument(0, String.class);
        requestState.put(inEvent.getCorrID(), new Object[]{merchantId});

        // Publish event for handling by Payment
        Event getManagerTransactions = new Event(EventTypes.MERCHANT_TRANSACTIONS_REQUEST, inEvent.getCorrID(), new Object[] {merchantId});
        queue.publish(getManagerTransactions);

        return getManagerTransactions;

    }

    public Event handleMerchantTransactionsRequestSucceeded(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        var transactions = inEvent.getArgument(0, List.class);
        outEvent = new Event(EventTypes.MERCHANT_REPORT_REQUEST_COMPLETED, inEvent.getCorrID(), new Object[] {new ReturnTransactionInfo(true, "", transactions)});

        queue.publish(outEvent);

        return outEvent;
    }

    public Event handleMerchantTransactionsRequestFailed(Event inEvent){
        checkCorrelationID(inEvent);

        var error = inEvent.getArgument(0, String.class);

        Event outEvent = new Event(EventTypes.MERCHANT_REPORT_REQUEST_FAILED, inEvent.getCorrID(), new Object[] { new ReturnTransactionInfo(false, error, Collections.<Transaction> emptyList())});
        queue.publish(outEvent);

        requestState.remove(inEvent.getCorrID());

        return outEvent;
    }

    public void checkCorrelationID(Event e) {
        String receivedCorrID = e.getCorrID();
        String eventType = e.getType();
        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);
    }
}

