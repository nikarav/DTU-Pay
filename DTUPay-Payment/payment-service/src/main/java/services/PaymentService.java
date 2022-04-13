package services;
/*
authors:
Siskou Melina - s213158
Spyrou Thomas - s213161
 */

import dto.CreatePaymentData;
import dto.RequestPaymentData;
import handlers.PaymentHandler;
import messaging.Event;
import messaging.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.EventTypes;
import utils.Helpers;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


public class PaymentService<T> {

    MessageQueue queue;
    PaymentHandler paymentHandler = new PaymentHandler();
    private CompletableFuture<T> completableFuture;
    public Map<String, RequestPaymentData > requestState;
    private Helpers helpers;
    private String corrID;

    private static final Logger log
            = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(MessageQueue q){
        this.queue = q;
        this.queue.addHandler(EventTypes.PAYMENT_REQUESTED, this::handlePaymentRequest);
        this.queue.addHandler(EventTypes.BANK_ACCOUNT_ID_RETRIEVAL_SUCCESS, this::handleBankAccountRequestSuccess);
        this.queue.addHandler(EventTypes.BANK_ACCOUNT_ID_RETRIEVAL_FAILED, this::handleBankAccountRequestFailure);
        this.queue.addHandler(EventTypes.USE_TOKEN_COMPLETED, this::handleUseTokenCompleted);
        this.queue.addHandler(EventTypes.USE_TOKEN_FAILED, this::handleUseTokenFailed);
        this.queue.addHandler(EventTypes.TRANSACTIONS_REQUEST, this::handleTransactionsRequested);
        this.queue.addHandler(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST, this::handleCustomerTransactionsRequested);
        this.queue.addHandler(EventTypes.MERCHANT_TRANSACTIONS_REQUEST, this::handleMerchantTransactionsRequested);


        requestState = new ConcurrentHashMap<>();
        helpers = new Helpers();
    }

    public Event handlePaymentRequest(Event inEvent) {
        checkCorrelationID(inEvent);
        completableFuture = new CompletableFuture<>();

        //this object needs to be changed after token management is done
        //we'll retrieve accountId and will ask for bank account id for now
        var paymentInfoReceived = inEvent.getArgument(0, RequestPaymentData.class);
        log.info("Received Payment retrieve incoming payload");
        requestState.put(inEvent.getCorrID(), paymentInfoReceived);

        Event getAccountEvent = new Event(EventTypes.USE_TOKEN_REQUESTED, inEvent.getCorrID(), new Object[] {paymentInfoReceived.getToken()});
        queue.publish(getAccountEvent);
        return getAccountEvent;
    }

    public Event handleUseTokenCompleted(Event event) {
        checkCorrelationID(event);

        var customerAccountId = event.getArgument(0, String.class);
        log.info("[{}] - [{}]: Account Id retrieved from token.", event.getType(), event.getCorrID());

        var merchantAccountId = requestState.get(event.getCorrID()).getMerchantAccountId();
        Event getBankAccountEvent = new Event(EventTypes.BANK_ACCOUNT_ID_REQUESTED, event.getCorrID(), new Object[] {2,customerAccountId, merchantAccountId});
        queue.publish(getBankAccountEvent);
        return getBankAccountEvent;
    }

    public Event handleUseTokenFailed(Event event) {
        checkCorrelationID(event);

        var error = event.getArgument(0, String.class);
        log.info("[{}] - [{}]: Failed to retrieve account Id from token", event.getType(), event.getCorrID());

        Event accountIdFailed = new Event(EventTypes.PAYMENT_FAILED, event.getCorrID(), new Object[] {error});
        queue.publish(accountIdFailed);

        requestState.remove(event.getCorrID());
        return accountIdFailed;
    }

    public Event handleBankAccountRequestSuccess(Event event) {
        checkCorrelationID(event);
        Event outEvent;
        var paymentData = requestState.get(event.getCorrID());

        // 1st argument is the information about the Customer
        // 2nd argument is the information about the Merchant
        var customerBankId = event.getArgument(0, String.class);
        var merchantBankId = event.getArgument(1, String.class);

        var paymentDataToSend = new CreatePaymentData();

        log.info("[{}] - [{}]: Bank account id retrieved for customer and merchant. Payment can proceed.", event.getType(), event.getCorrID());
        try{
            paymentDataToSend.setCustomerBankAccountId(customerBankId);
            paymentDataToSend.setAmount(paymentData.getAmount());
            paymentDataToSend.setDescription(paymentData.getDescription());
            paymentDataToSend.setToken(paymentData.getToken());
            paymentDataToSend.setMerchantBankAccountId(merchantBankId);

            boolean transactionCompleted = paymentHandler.postCreatePayment(paymentDataToSend);
            outEvent = new Event(EventTypes.PAYMENT_SUCCESS, event.getCorrID(), new Object[] {true});
            queue.publish(outEvent);
        } catch (Exception e){
            log.info("[{}] - [{}]: Payment service handler failed. Reason: " + e.getMessage(), event.getType(), event.getCorrID());

            outEvent = new Event(EventTypes.PAYMENT_FAILED, event.getCorrID(), new Object[] {e.getMessage()});
            queue.publish(outEvent);
        }

        requestState.remove(event.getCorrID());
        return outEvent;
    }

    public Event handleBankAccountRequestFailure(Event event) {
        checkCorrelationID(event);

        log.info("[{}] - [{}]: Bank account id not found payment service.", event.getType(), event.getCorrID());

        var error = event.getArgument(0, String.class);

        Event accountIdFailed = new Event(EventTypes.PAYMENT_FAILED, event.getCorrID(), new Object[] {error});
        queue.publish(accountIdFailed);

        requestState.remove(event.getCorrID());

        return accountIdFailed;
    }

    public Event handleTransactionsRequested(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        try{
            var transactions = paymentHandler.getTransactionList();
            outEvent = new Event(EventTypes.TRANSACTIONS_REQUEST_SUCCESS, inEvent.getCorrID(), new Object[] {transactions});
            queue.publish(outEvent);
        }
        catch (Exception e){
            outEvent = new Event(EventTypes.TRANSACTIONS_REQUEST_FAILED, inEvent.getCorrID(), new Object[] {e.getMessage()});
            queue.publish(outEvent);
        }
        return outEvent;
    }

    public void handleCustomerTransactionsRequested(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        var customerId = inEvent.getArgument(0, String.class);

        try{
            var transactions = paymentHandler.getCustomerTransactions(customerId);
            outEvent = new Event(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST_SUCCESS, inEvent.getCorrID(), new Object[] {transactions});
            queue.publish(outEvent);
        }
        catch (Exception e){
            outEvent = new Event(EventTypes.CUSTOMER_TRANSACTIONS_REQUEST_FAILED, inEvent.getCorrID(), new Object[] {e.getMessage()});
            queue.publish(outEvent);
        }

    }

    public void handleMerchantTransactionsRequested(Event inEvent){
        checkCorrelationID(inEvent);
        Event outEvent;

        var customerId = inEvent.getArgument(0, String.class);

        try{
            var transactions = paymentHandler.getMerchantTransactions(customerId);
            outEvent = new Event(EventTypes.MERCHANT_TRANSACTIONS_REQUEST_SUCCESS, inEvent.getCorrID(), new Object[] {transactions});
            queue.publish(outEvent);
        }
        catch (Exception e){
            outEvent = new Event(EventTypes.MERCHANT_TRANSACTIONS_REQUEST_FAILED, inEvent.getCorrID(), new Object[] {e.getMessage()});
            queue.publish(outEvent);
        }
    }

    public void checkCorrelationID(Event e) {
        String receivedCorrID = e.getCorrID();
        String eventType = e.getType();
        log.info("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);
    }
}
