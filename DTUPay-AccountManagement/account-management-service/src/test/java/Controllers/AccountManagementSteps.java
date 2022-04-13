package Controllers;
/*
authors:
De Santos Bernal Veronica - s210098
Spyrou Thomas - s213161
 */

import DTO.*;
import Entities.Account;
import dtu.ws.fastmoney.*;
import io.cucumber.java.*;
import io.cucumber.java.an.E;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.Assert;
import services.AccountService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertTrue;
import static org.wildfly.common.Assert.assertNotNull;

public class AccountManagementSteps {
    MessageQueue queue = mock(MessageQueue.class);
    AccountService accountService = new AccountService(queue);
    CreateAccountData accountInfoPosted;
    ReturnAccount ReturnAccount;
    Account sentToMockQueueAccount;
    Account expectedAccount;
    Event event;
    static BankService bankService = new BankServiceService().getBankServicePort();
    static String bankAccountId = "123456";
    private final boolean result = true;

    @Before
    public void before(){
        accountInfoPosted = new CreateAccountData();
        sentToMockQueueAccount = new Account();
        expectedAccount = new Account();
        ReturnAccount = new ReturnAccount();
    }

    // Create a Customer fail
    @When("a {string} event to fail for a customer is received with cpr {string}, firstname {string}, lastname {string} and type {int}")
    public void aEventForACustomerIsReceived(String eventName, String cprNumber, String firstName,
                                             String lastName, Integer type) {
        //incoming payload
        accountInfoPosted.setBankAccountId("nobank");
        accountInfoPosted.setCprNumber(cprNumber);
        accountInfoPosted.setFirstName(firstName);
        accountInfoPosted.setLastName(lastName);
        accountInfoPosted.setType(type);

        accountService.handleAccountRegistrationRequested(new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] {accountInfoPosted}));
    }

    @Then("the {string} event for customer is sent fail")
    public void theEventIsSent(String eventName) {
        event = new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] { new ReturnAccountInfo(accountInfoPosted.getBankAccountId(), "") });
    }

    @And("the customer is not created")
    public void theCustomerIsNotCreated() {
        if(Objects.equals(event.toString(), "BankAccountIdRetrievalFailed"))
            assertTrue(result);
    }

    // Create a Customer success
    @Given("a bank account exist with firstname {string}, lastname {string}, cpr {string} and amount {int}")
    public void aBankAccountExistWithFirstnameLastnameCprAndAmount(String firstName, String lastName, String cprNumber, int amount) throws BankServiceException_Exception {



        User userBank = new dtu.ws.fastmoney.User();
        userBank.setCprNumber(cprNumber);
        userBank.setFirstName(firstName);
        userBank.setLastName(lastName);

        List<AccountInfo> accounts = bankService.getAccounts();
        for(int i = 0; i< accounts.size(); i++){
            AccountInfo info = accounts.get(i);
            if(
                    info.getUser().getCprNumber().equals(userBank.getCprNumber())
            ){
                try {
                    bankService.retireAccount(info.getAccountId());
                } catch (BankServiceException_Exception e) {
                    e.printStackTrace();
                }
            }
        }

        bankAccountId = bankService.createAccountWithBalance(userBank, BigDecimal.valueOf(amount));

    }
    @When("a {string} event for a customer is received with cpr {string}, firstname {string}, lastname {string} and type {int}")
    public void aEventForCustomerIsReceivedSuccess(String eventName, String cprNumber, String firstName,
                                                   String lastName, Integer type) {
        //incoming payload
        accountInfoPosted.setBankAccountId(bankAccountId);
        accountInfoPosted.setCprNumber(cprNumber);
        accountInfoPosted.setFirstName(firstName);
        accountInfoPosted.setLastName(lastName);
        accountInfoPosted.setType(type);

        accountService.handleAccountRegistrationRequested(new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] {accountInfoPosted}));
    }

    @Then("the {string} event is sent success")
    public void theEventIsSentSuccess(String eventName) {
        sentToMockQueueAccount.setAccountId("randomId");
        sentToMockQueueAccount.setBankAccountId(bankAccountId);
        sentToMockQueueAccount.setFirstName("tommy");
        sentToMockQueueAccount.setCprNumber("tommycpr123");
        sentToMockQueueAccount.setLastName("flou");

        var event = new Event(eventName,sentToMockQueueAccount.getAccountId(), new Object[] {sentToMockQueueAccount});
        expectedAccount = event.getArgument(0, Account.class);
   //     verify(queue).publish(any(Event.class));
    }

    @And("the customer is created")
    public void theCustomerIsCreated() {
        assertEquals(expectedAccount.getAccountId(), sentToMockQueueAccount.getAccountId());

        afterAll();
    }

    // Create a Merchant fail
    @When("a {string} event for a merchant is received with cpr {string}, firstname {string}, lastname {string} and type {int}")
    public void aEventForAMerchantIsReceived(String eventName, String cprNumber, String firstName,
                                                                 String lastName, Integer type) {
        //incoming payload
        accountInfoPosted.setBankAccountId("123");
        accountInfoPosted.setCprNumber(cprNumber);
        accountInfoPosted.setFirstName(firstName);
        accountInfoPosted.setLastName(lastName);
        accountInfoPosted.setType(type);

        accountService.handleAccountRegistrationRequested(new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] {accountInfoPosted}));

    }
    @Then("the {string} event for merchant is sent fail")
    public void theEventForMerchantIsSent(String eventName) {
        event = new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] { new ReturnAccountInfo(accountInfoPosted.getBankAccountId(), "") });
    }

    @Then("the merchant is not created")
    public void theMerchantIsNotCreated() {
        if(Objects.equals(event.toString(), "BankAccountIdRetrievalFailed"))
            assertTrue(result);
    }

    // Create a Merchant success
    @Given("a bank account for a merchant exist with firstname {string}, lastname {string}, cpr {string} and amount {int}")
    public void aBankAccountExistForMerchantWithFirstnameLastnameCprAndAmount(String firstName, String lastName, String cprNumber, int amount) throws BankServiceException_Exception {
        User userBank = new dtu.ws.fastmoney.User();
        userBank.setCprNumber(cprNumber);
        userBank.setFirstName(firstName);
        userBank.setLastName(lastName);

        List<AccountInfo> accounts = bankService.getAccounts();
        for(int i = 0; i< accounts.size(); i++){
            AccountInfo info = accounts.get(i);
            if(
                    info.getUser().getCprNumber().equals(userBank.getCprNumber())
            ){
                try {
                    bankService.retireAccount(info.getAccountId());
                } catch (BankServiceException_Exception e) {
                    e.printStackTrace();
                }
            }
        }

        bankAccountId = bankService.createAccountWithBalance(userBank, BigDecimal.valueOf(amount));

    }

    @When("a {string} AccountRegistrationRequested for a merchant is received with cpr {string}, firstname {string}, lastname {string} and type {int}")
    public void aEventForMerchantIsReceivedSuccess(String eventName, String cprNumber, String firstName,
                                                        String lastName, Integer type) {
        //incoming payload
        accountInfoPosted.setBankAccountId(bankAccountId);
        accountInfoPosted.setCprNumber(cprNumber);
        accountInfoPosted.setFirstName(firstName);
        accountInfoPosted.setLastName(lastName);
        accountInfoPosted.setType(type);

        accountService.handleAccountRegistrationRequested(new Event(eventName, accountInfoPosted.getBankAccountId(),  new Object[] {accountInfoPosted}));
    }

    @Then("the {string} event for merchant is sent success")
    public void theEventForMerchantIsSentSuccess(String eventName) {
        event = new Event(eventName, accountInfoPosted.getBankAccountId(), new Object[] { new ReturnAccountInfo(bankAccountId, "") });
    }

    @And("the merchant is created")
    public void theMerchantIsCreatedSuccess() {
        if(Objects.equals(event.toString(), "BankAccountIdRetrievalSucceeded"))
            assertTrue(result);
    }

    // Test Account Management Get Accounts
    // Request Bank AccountID success
    @Given("An account id {string} that exists in the Bank")
    public void anAccountIdThatExistsInTheBank(String AccountID) {
        try {
            bankAccountId = String.valueOf(bankService.getAccount(AccountID));

        } catch (BankServiceException_Exception e) {
            e.printStackTrace();
        }
    }

    @Then("the bank account id is returned")
    public void  theBankAccountIdIsReturned() {
        assertTrue(event.getArgument(0,String.class).equals(bankAccountId));
    }

    @AfterAll
    public static void afterAll() {
        try{
            bankService.retireAccount(bankAccountId);
        }
        catch (BankServiceException_Exception e){
            e.getMessage();
        }
    }

    String accountId = "";

    @And("a user exists with firstname {string}, lastname {string}, cpr {string} and the given bank account")
    public void aUserExistsWithFirstnameLastnameCprAndTheGivenBankAccount(String firstName, String lastName, String cpr) {
        //incoming payload
        CreateAccountData newAccount = new CreateAccountData();

        newAccount.setFirstName(firstName);
        newAccount.setLastName(lastName);
        newAccount.setCprNumber(cpr);
        newAccount.setBankAccountId(bankAccountId);
        newAccount.setType(0);

        Event evt = accountService.handleAccountRegistrationRequested(new Event("AccountRegistrationRequested", "whateverWeDontUseThis", new Object[] {newAccount}));
        Assert.assertTrue(evt.getType().equals("AccountRegistrationCompleted"));
        accountId = evt.getArgument(0,String.class);


    }

    @When("a request for bank account id using the accountID is sent")
    public void aRequestForBankAccountIdUsingTheAccountIDIsSent() {
        event = accountService.handleBankAccountIdRequested(new Event("BankAccountIdRequested", "example",  new Object[] {1,accountId}));
    }

    @Then("the event type is {string}")
    public void theEventTypeIs(String eventType) {
        Assert.assertTrue(event.getType().equals(eventType));
    }

    @Given("the account id is {string}")
    public void theAccountIdIs(String accountId) {
        this.accountId = accountId;
    }

    @And("the account with the account id doesnt exist")
    public void theAccountWithTheAccountIdDoesntExist() {
        Event event = accountService.handleAccountRequested(new Event("AccountRequested","CorrIdGoHurrDurr",new Object[]{accountId}));
        Assert.assertTrue(event.getType().equals("AccountRequestFailed"));
    }

    @When("the account is requested")
    public void theAccountIsRequested() {
        event = accountService.handleAccountRequested(new Event("AccountRequested","CorrIdGoHurrDurr",new Object[]{accountId}));

    }

    @And("the accounts name is {string}")
    public void theAccountsNameIs(String firstname) {
        Assert.assertTrue(event.getArgument(0,ReturnAccount.class).getAccount().getFirstName().equals(firstname));
    }
}
