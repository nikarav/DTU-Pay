package services;
/*
authors:
De Santos Bernal Veronica - s210098
Spyrou Thomas - s213161
 */


import DTO.CreateAccountData;
import DTO.ReturnAccount;
import Entities.Account;

import handlers.AccountHandler;
import messaging.Event;
import messaging.MessageQueue;
import utils.EventTypes;

public class AccountService {

    MessageQueue queue;
    AccountHandler accHandler = new AccountHandler();

    public AccountService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(EventTypes.ACCOUNT_REGISTRATION_REQUESTED, this::handleAccountRegistrationRequested);
        this.queue.addHandler(EventTypes.BANK_ACCOUNT_ID_REQUESTED, this::handleBankAccountIdRequested);
        this.queue.addHandler(EventTypes.DELETE_ACCOUNT_REQUESTED, this::handleDeleteAccountRequested);
        this.queue.addHandler(EventTypes.ACCOUNT_REQUESTED, this::handleAccountRequested);
        //this.queue.addHandler("AccountsRequested", this::handleAccountsRequested, Queue.QUEUE_NAME);
    }

    public Event handleAccountRegistrationRequested(Event ev) {
        checkCorrelationID(ev);
        Event event;
        var receivedAccount = ev.getArgument(0, CreateAccountData.class);

        try{
            var newAccountId = accHandler.createAccountFromData(receivedAccount);
            // Create an "AccountRegistrationCompleted" event
            event = new Event(EventTypes.ACCOUNT_REGISTRATION_COMPLETED, ev.getCorrID(), new Object[] {newAccountId});
        }catch (Exception e){
            // Create an "AccountRegistrationFailed" event
            event = new Event(EventTypes.ACCOUNT_REGISTRATION_FAILED, ev.getCorrID(), new Object[] {e.getMessage()});
        }
        queue.publish(event);
        return event;
    }

    public Event handleBankAccountIdRequested(Event ev){
        checkCorrelationID(ev);
        Event event;

        int count = ev.getArgument(0,int.class);

        String[] ids = new String[count];
        for (int i = 0; i < count; i++) {
            ids[i] = ev.getArgument(i+1, String.class);
        }

        try{

            Object[] arguments = new Object[count];
            for (int i = 0; i < count; i++) {
                arguments[i] = accHandler.getAccountBankId(ids[i]);
            }
            event = new Event(EventTypes.BANK_ACCOUNT_ID_RETRIEVAL_SUCCESS, ev.getCorrID(), arguments);
        }
        catch (Exception e) {
            event = new Event(EventTypes.BANK_ACCOUNT_ID_RETRIEVAL_FAILED, ev.getCorrID(), new Object[] { e.getMessage()});
        }
        queue.publish(event);
        return event;
    }

    public void handleDeleteAccountRequested(Event ev){
        checkCorrelationID(ev);
        Event event;
        var receivedAccountId = ev.getArgument(0, String.class);
        try{
            boolean accountDeleted =  accHandler.deleteAccountInfo(receivedAccountId);
            if(accountDeleted){
                event = new Event(EventTypes.DELETE_ACCOUNT_SUCCESS, ev.getCorrID(), new Object[] {});
            }
            else{
                event = new Event(EventTypes.DELETE_ACCOUNT_NOT_EXIST, ev.getCorrID(), new Object[] { receivedAccountId });
            }
        }
        catch (Exception e){
            event = new Event(EventTypes.DELETE_ACCOUNT_FAILED, ev.getCorrID(), new Object[] {});
        }
        queue.publish(event);
    }

    public Event handleAccountRequested(Event ev){
        checkCorrelationID(ev);
        Event event;
        var receivedAccountId = ev.getArgument(0, String.class);
        try{

            Account accountToBeReturned = accHandler.getAccount(receivedAccountId);

            if(accountToBeReturned != null){
                event = new Event(EventTypes.ACCOUNT_REQUEST_COMPLETED, ev.getCorrID(), new Object[] {new ReturnAccount(accountToBeReturned, true, "")});

            }else{
                event = new Event(EventTypes.ACCOUNT_REQUEST_FAILED, ev.getCorrID(), new Object[] { new ReturnAccount(null, true, "Account " + receivedAccountId + " does not exist") });
            }
        }
        catch (Exception e){
            event = new Event(EventTypes.ACCOUNT_REQUEST_FAILED, ev.getCorrID(), new Object[] {new ReturnAccount(null, false, e.getMessage())});
        }
        queue.publish(event);
        return event;
    }

    //TODO
//    public void handleAccountsRequested(Event ev){
//        Event event;
//        try{
//            var accounts = accHandler.getAccounts();
//            event = new Event("AccountsRequestSucceeded", new Object[] {});
//        }
//        catch (Exception e){
//            event = new Event("AccountsRequestFailed", new Object[] {});
//        }
//        queue.publish(event);
//    }

    public void checkCorrelationID(Event e) {
        String receivedCorrID = e.getCorrID();
        String eventType = e.getType();
        System.out.println("Received event "
                + eventType
                + " with correlationID: "
                + receivedCorrID);
    }
   }