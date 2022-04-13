package controllers;
/*
@authors:
Karavasilis Nikos s213685
Spyrou Thomas s213161
 */

import java.util.concurrent.ConcurrentHashMap;
import dto.ReturnAccountInfo;
import entities.Account;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import service.AccountService;
import utils.EventTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import static org.junit.Assert.assertNotNull;


public class CustomerRegistrationSteps {
    private Map<String, CompletableFuture<Event>> publishedEvents = new ConcurrentHashMap<>();
    private Map<String, CompletableFuture<ReturnAccountInfo>> accRequests;

    //private Account accountSent;// = new Account();

    private MessageQueue queue = new MessageQueue() {

        @Override
        public void publish(Event event) {
            var account = event.getArgument(0, Account.class);
            publishedEvents.get(account.getCprNumber()).complete(event);
        }

        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }

    };

    private AccountService accountService = new AccountService(queue);
    private Account account; //= new Account();
    private Account accountSent;
    private CompletableFuture<ReturnAccountInfo> accountCompletableFuture = new CompletableFuture<>();
    private Map<Account, String> correlationIds = new HashMap<>();

    @Before
    public void before(){

    }

    @Given("there is an account with firstName {string}, lastName {string}, cpr {string}, type {string}, bankAccountID {string}")
    public void thereIsACustomerWithFirstNameLastNameCprTypeBankAccountID(String firstName, String lastName, String cpr,
                                                                          String type, String bankAccountID) {
        account = new Account();
        account.setLastName(lastName);
        account.setFirstName(firstName);
        account.setType(type);
        account.setBankAccountId(bankAccountID);
        account.setCprNumber(cpr);
        publishedEvents.put(account.getCprNumber(), new CompletableFuture<>());
        assertNotNull(account);
    }

    @When("the account is being registered")
    public void theCustomerIsBeingRegistered() {
        Thread t = new Thread(() -> {
            try {
                var result = (ReturnAccountInfo) accountService.registerAccount(account);
                accountCompletableFuture.complete(result); //wait
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t.start();
    }

    @Then("the event {string} is published")
    public void theEventIsPublished(String eventType) {
        Event event = publishedEvents.get(account.getCprNumber()).join();
        assertEquals(eventType, event.getType());
        accountSent = new Account();
        accountSent = event.getArgument(0, Account.class);
        var correlationId = event.getCorrID();
        correlationIds.put(accountSent, correlationId);
    }

    @When("the account is received from account management")
    public void theCustomerIsReceivedFromAccountManagement() {
        accountService.handleAccountRegistrationCompleted(
                new Event(EventTypes.ACCOUNT_REGISTRATION_COMPLETED, correlationIds.get(accountSent),
                        new Object[] {UUID.randomUUID().toString()}));
    }

    @Then("the account is created")
    public void theCustomerIsCreated() {
        assertNotNull(accountCompletableFuture.join().getAccountId());

    }

    @Then("the event {string} is published for the failed scenario")
    public void theEventIsPublishedForTheFailedScenario(String eventType) {
        Event event = publishedEvents.get(account.getCprNumber()).join();
        assertEquals(eventType, event.getType());
        accountSent = new Account();
        accountSent = event.getArgument(0, Account.class);
        var correlationId = event.getCorrID();

        correlationIds.put(accountSent, correlationId);
    }

    @When("the account is not received from account management")
    public void theAccountIsNotReceivedFromAccountManagement() {
        accountService.handleAccountRegistrationFailed(
                new Event(EventTypes.ACCOUNT_REGISTRATION_FAILED, correlationIds.get(accountSent),
                        new Object[] {"Failed"}));

    }

    @Then("the account is not created")
    public void theAccountIsNotCreated() {
        assertNotNull(accountCompletableFuture.join().getErrorMessage());

    }

//    @When("the account is not received from account management")
//    public void theCustomerIsNotReceivedFromAccountManagement() {
//        accountService.handleAccountRegistrationFailed(
//                new Event("AccountRegistrationFailed", correlationIds.get(accountSent),
//                        new Object[] {"Failed to add customer"}));
//    }
//
//    @Then("the account is not created")
//    public void theCustomerIsNotCreated() {
//        assertNotNull(accountCompletableFuture.join().getErrorMessage());
//    }



}

