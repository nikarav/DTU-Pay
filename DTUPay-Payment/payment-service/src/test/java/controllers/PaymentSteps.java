package controllers;
/*
authors:
De Santos Bernal Verconica - s210098
Laforce Erik Aske - s194620
 */

import dto.RequestPaymentData;
import dto.responses.ReturnAccountInfo;
import dtu.ws.fastmoney.AccountInfo;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import services.PaymentService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentSteps {
    MessageQueue queue = mock(MessageQueue.class);
    PaymentService paymentServices = new PaymentService(queue);
    Event event;
    static String CustomerBankAccount;
    static String MerchantBankAccount;

    @Before
    public void before(){
        var user = new User();
        user.setCprNumber("TestCPRCustomer1-1");
        user.setFirstName("Test");
        user.setLastName("Test");

        var user2 = new User();
        user2.setCprNumber("TestCPRMerchant1-1");
        user2.setFirstName("Test");
        user2.setLastName("Test");

        var bank = new BankServiceService().getBankServicePort();

        List<AccountInfo> accounts = bank.getAccounts();
        for(int i = 0; i< accounts.size(); i++){
            AccountInfo info = accounts.get(i);
            if(
                    info.getUser().getCprNumber().equals(
                            user.getCprNumber()
                    )
                            ||
                            info.getUser().getCprNumber().equals(
                                    user2.getCprNumber()
                            )
            ){
                try {
                    bank.retireAccount(info.getAccountId());
                } catch (BankServiceException_Exception e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            CustomerBankAccount = bank.createAccountWithBalance(user, new BigDecimal(1000));
            MerchantBankAccount = bank.createAccountWithBalance(user2, new BigDecimal(1000));
        } catch (BankServiceException_Exception e) {
            System.out.println("HERE: \n\n\n");
            e.printStackTrace();
            System.out.println("\n\n\n Done ");
        }
    }

    @AfterAll
    public static void afterAll() {
        var bank = new BankServiceService().getBankServicePort();

        try {
            bank.retireAccount(CustomerBankAccount);
            bank.retireAccount(MerchantBankAccount);
        } catch (BankServiceException_Exception e) {
            e.printStackTrace();
        }
    }

    // Scenario: Payment request - successful
    @Given("A paymentRequest is made with amount {int}, token {string} and cor id {string}")
    public void PaymentRequest(int amount, String token, String corId) {
        event = paymentServices.handlePaymentRequest(
                new Event(
                        "PaymentRequested",
                        corId,
                        new Object[]{
                                new RequestPaymentData(amount, token, "", MerchantBankAccount, "TestDisc")
                        }));
    }

    @Then("The event was pushed")
    public void theEventWasPushedWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }

    @When("The token has been consumed and User id {string} has been returned")
    public void theTokenHasBeenConsumedAndUserIdHasBeenReturned(String userId) {
        event = paymentServices.handleUseTokenCompleted(
                new Event(
                        "UseTokenCompleted",
                        event.getCorrID(),
                        new Object[]{
                                userId
                        }));
    }

    @Then("The event was pushed to retrieve account customer id")
    public void theEventWasPushedToRetrieveAccountCustomerIdWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }

    @When("The account has been handled and we have an account id customer")
    public void theAccountHasBeenHandledAndWeHaveAnAccountId() {

        event = paymentServices.handleBankAccountRequestSuccess(
                new Event(
                        "BankAccountIdRetrievalSucceeded",
                        event.getCorrID(),
                        new Object[]{
                                CustomerBankAccount,
                                MerchantBankAccount
                        }));
    }

    @Then("The event was pushed to retrieve merchant customer id")
    public void theEventWasPushedToRetrieveMerchantCustomerIdWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }

    @When("The account has been handled and we have an account id merchant")
    public void theAccountHasBeenHandledAndWeHaveAnAccountIdMerchant() {

        ReturnAccountInfo ret = new ReturnAccountInfo();
        ret.setBankAccountId(MerchantBankAccount);

        event = paymentServices.handleBankAccountRequestSuccess(
                new Event(
                        "BankAccountIdRetrievalSucceeded",
                        event.getCorrID(),
                        new Object[]{
                                ret
                        }));
    }

    @And("The success event was pushed")
    public void theSuccessEventWasPushedWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }


    // Scenario: Payment - token failed
    @Given("A new paymentRequest is made with amount {int}, token {string} and cor id {string}")
    public void aNewPaymentRequestIsMadeWithAmountTokenAndCorId(int amount, String token, String corId) {
        event = paymentServices.handlePaymentRequest(
                new Event(
                        "PaymentRequested",
                        corId,
                        new Object[]{
                                new RequestPaymentData(amount, token, "", MerchantBankAccount, "TestDisc")
                        }));
    }

    @When("The event is pushed")
    public void theEventIsPushedWithExchangeTypeAndWithARoutingKey() {
        verify(queue).publish(event);
    }

    @Then("The token is consumed and User id {string} is not returned")
    public void theTokenCannotBeConsumedAndUserIdIsNotReturned(String userId) {
        event = paymentServices.handleUseTokenFailed(
                new Event(
                        "UseTokenFailed",
                        event.getCorrID(),
                        new Object[]{
                                userId
                        }));
    }

    @And("The event failure is pushed")
    public void theEventIsPushedWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }


    // Scenario: Payment - customer account failed
    @Then("The account has not been handled and we do not have an account id customer")
    public void theAccountHasNotBeenHandledAndWeHaveAnAccountIdCustomer()  {
        ReturnAccountInfo ret = new ReturnAccountInfo();
        ret.setBankAccountId(CustomerBankAccount);

        event = paymentServices.handleBankAccountRequestFailure(
                new Event(
                        "accountEvents",
                        event.getCorrID(),
                        new Object[]{
                                ret.getBankAccountId()
                        }));
    }

    @And("The account failure event is pushed")
    public void theAccountFailureEventIsPushedWithTypeAndRoutingKey() {
        verify(queue).publish(event);
    }


}
