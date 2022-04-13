package Controllers;
/*
authors:
Hejlsberg Jacob Kølbjerg - s194618
Laforce Erik Aske - s194620
 */

import services.TokenServices;
import session.TokenDataBase;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.wildfly.common.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TokenManagementSteps {
    MessageQueue queue = mock(MessageQueue.class);
        TokenServices tokenServices = new TokenServices(queue);
    Event event;

    @Before
    public void before(){
        TokenDataBase.TokenMap = new HashMap<String, ArrayList<UUID>>();
    }

    @AfterAll
    public static void afterAll() {
        TokenDataBase.TokenMap = new HashMap<String, ArrayList<UUID>>();
    }

    @Given("No user exists with the id {string}")
    public void noUserExistsWithTheId(String userId) {
        Assert.assertTrue(TokenDataBase.TokenMap.get(userId) == null);
    }

    @When("The user with id {string} requests {int} token")
    public void theUserRequestsToken(String userId, int amountOfTokens) {
        var event2 = new Event("RequestToken", UUID.randomUUID().toString(), new Object[]{userId, amountOfTokens});
        event = tokenServices.handleTokenRequest(event2);
    }

    @Then("Then user with id {string} has been created")
    public void thenUserHasBeenCreated(String userId) {
        var tokens = TokenDataBase.TokenMap.get(userId);
        Assert.assertFalse(tokens == null);
    }

    @Given("User exists with the id {string} and {int} tokens")
    public void userExistsWithTheId(String userId, int amountOfTokens) {
        Assert.assertTrue(TokenDataBase.TokenMap.get(userId) == null);
        TokenDataBase.TokenMap.put(userId, new ArrayList<UUID>());

        var event2 = new Event("RequestToken", UUID.randomUUID().toString(), new Object[]{userId, amountOfTokens});
        event = tokenServices.handleTokenRequest(event2);
    }

    @And("The user with id {string} has {int} tokens")
    public void theUserWithIdHasTokens(String userId, int amountOfTokens) {
        System.out.println(TokenDataBase.TokenMap.get(userId).size());
        Assert.assertTrue(TokenDataBase.TokenMap.get(userId).size() == amountOfTokens);
    }

    @When("The user with id {string} consumes a token")
    public void theUserWithIdConsumesAToken(String userId) {
        UUID token = TokenDataBase.TokenMap.get(userId).get(1);
        event = tokenServices.handleUseToken(new Event("UseToken", UUID.randomUUID().toString(), new Object[]{token}));
    }

    @When("The user consumes a token with wrong uuid")
    public void theUserWithIdConsumesATokenWithId() {
        event = tokenServices.handleUseToken(new Event("UseToken", UUID.randomUUID().toString(), new Object[]{UUID.randomUUID()}));
    }

    @Given("The DataBase is empty")
    public void theDataBaseIsEmpty() {
        TokenDataBase.TokenMap = new HashMap<>();
    }

    @When("The database is requested")
    public void theDatabaseIsRequested() {
        event = tokenServices.handleGetData(new Event("GetData", UUID.randomUUID().toString(), new Object[0]));
    }

    @And("The event was pushed with object")
    public void theEventWasPushedWithObject() {
        //verify(queue).publish(event);
    }

    @Then("The event was pushed")
    public void theEventWasPushedWithExchangeTypeAndRoutingKey() {
        verify(queue).publish(event);
    }
}
