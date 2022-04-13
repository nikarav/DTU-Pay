package controllers;

import dto.Transaction;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.wildfly.common.Assert;
import services.ReportingService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ReportingSteps {
    MessageQueue queue = mock(MessageQueue.class);
    ReportingService reportingService = new ReportingService(queue);
    Event event;

    List<Transaction> transactions = new ArrayList<Transaction>();

    @When("Manager requests a report")
    public void managerRequestsAReport() {
        var managerRequestEvent = new Event("ManagerReportRequested", UUID.randomUUID().toString(), new Object[]{});
        event = reportingService.handleManagerReportRequested(managerRequestEvent); // TransactionsRequested
    }

    @When("Manager receives transactions from payment service")
    public void managerHasRequestedAReport() {
        var managerRequestSuccessEvent = new Event("TransactionsRequestSucceeded", UUID.randomUUID().toString(), new Object[]{transactions});
        event = reportingService.handleTransactionsRequestSucceeded(managerRequestSuccessEvent); // ManagerReportRequestCompleted
    }

    @When("Manager does not receive transactions from payment service")
    public void managerDoesNotReceiveTransactionsFromPaymentService() {
        var managerRequestFailureEvent = new Event("TransactionsRequestFailed", UUID.randomUUID().toString(), new Object[]{"errorMessage"});
        event = reportingService.handleTransactionsRequestFailed(managerRequestFailureEvent); // ManagerReportRequestFailed
    }

    @When("Customer requests a report")
    public void customerRequestsAReport() {
        var customerRequestEvent = new Event("CustomerReportRequested", UUID.randomUUID().toString(), new Object[]{"testId"});
        event = reportingService.handleCustomerReportRequested(customerRequestEvent);
    }

    @When("Customer receives transactions from payment service")
    public void customerReceivesTransactionsFromPaymentService() {
        var customerRequestSuccessEvent = new Event("CustomerTransactionsRequestSucceeded", UUID.randomUUID().toString(), new Object[]{transactions});
        event = reportingService.handleCustomerTransactionsRequestSucceeded(customerRequestSuccessEvent);
    }

    @When("Customer does not receive transactions from payment service")
    public void customerDoesNotReceiveTransactionsFromPaymentService() {
        var customerRequestFailureEvent = new Event("CustomerTransactionsRequestFailed", UUID.randomUUID().toString(), new Object[]{"errorMessage"});
        event = reportingService.handleCustomerTransactionsRequestFailed(customerRequestFailureEvent);
    }

    @When("Merchant requests a report")
    public void merchantRequestsAReport() {
        var merchantRequestEvent = new Event("MerchantReportRequested", UUID.randomUUID().toString(), new Object[]{"testId"});
        event = reportingService.handleMerchantReportRequested(merchantRequestEvent);
    }

    @When("Merchant receives transactions from payment service")
    public void merchantReceivesTransactionsFromPaymentService() {
        var merchantRequestSuccessEvent = new Event("TransactionsRequestSucceeded", UUID.randomUUID().toString(), new Object[]{transactions});
        event = reportingService.handleMerchantTransactionsRequestSucceeded(merchantRequestSuccessEvent);
    }

    @When("Merchant does not receive transactions from payment service")
    public void merchantDoesNotReceiveTransactionsFromPaymentService() {
        var merchantRequestFailureEvent = new Event("MerchantTransactionsRequestFailed", UUID.randomUUID().toString(), new Object[]{"errorMessage"});
        event = reportingService.handleMerchantTransactionsRequestFailed(merchantRequestFailureEvent);
    }

    @Then("The event was pushed")
    public void theEventWasPushedWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }

//    @And("Manager gets a list of all the transactions")
//    public void managerGetsAListOfAllTheTransactions() {
////        verify(queue).publish(event);
//    }
}
