package dtu.calculator;
/*
@authors:
Hejlsberg Jacob Kølbjerg - s194618
Laforce Erik Aske - s194620
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DTO.Account;
import DTO.RequestPaymentData;
import DTO.RequestTokens;
import DTO.ReturnTransactionInfo;
import dtu.ws.fastmoney.*;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import org.wildfly.common.Assert;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ExampleSteps {
	String result;
	int responseStatus;
	static Client client = ClientBuilder.newClient();
	BankService bank = new BankServiceService().getBankServicePort();
	String CustomerBankAccount;
	String MerchantBankAccount;
	String CustomerDTUAccount;
	String MerchantDTUAccount;
	Object responseEntity;
	ArrayList<String> Customertokens = new ArrayList<String>();

	@AfterAll
	public static void meThinkThisGoodIdea(){
		client.close();
	}

	@Before
	public void createBankAccounts() {
		var user = new User();
		user.setCprNumber("TestCPRCustomer1");
		user.setFirstName("Test");
		user.setLastName("Test");

		var user2 = new User();
		user2.setCprNumber("TestCPRMerchant1");
		user2.setFirstName("Test");
		user2.setLastName("Test");

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
		System.out.println("Space cleared");

		try {
			CustomerBankAccount = bank.createAccountWithBalance(user2, new BigDecimal(1000));
			MerchantBankAccount = bank.createAccountWithBalance(user, new BigDecimal(1000));
		} catch (BankServiceException_Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void retireBankAccounts() {
		try {
			bank.retireAccount(CustomerBankAccount);
			bank.retireAccount(MerchantBankAccount);
		} catch (BankServiceException_Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void initVariables(){
		Customertokens = new ArrayList<String>();
	}


	
	@When("TestMethod")
	public void iCallTheHelloService() {
		Account account = new Account();
		account.setBankAccountId("5ce9f33c-7231-4796-8bd8-34585e400b9c");
		account.setCprNumber("TestCustomerCPR");
		account.setFirstName("John");
		account.setLastName("Smith");
		account.setType("0");

		WebTarget target = client.target("http://localhost:8080/customer/register");
		Response response = target.request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

		Assert.assertTrue(response.getStatus() == 201);
		result = response.readEntity(String.class);
		response.close();
	}
	
	@Then("I get the answer {string}")
	public void iGetTheAnswer(String string) {
		assertEquals(string,result);
	}

	@When("Customer creation is requested")
	public void customer_creation_is_requested() {
		Account account = new Account();
		account.setBankAccountId(CustomerBankAccount);
		account.setCprNumber("TestCustomerCPR");
		account.setFirstName("John");
		account.setLastName("Smith");
		account.setType("0");

		WebTarget target = client.target("http://localhost:8080/customer/register");
		Response response = target.request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

		responseStatus = response.getStatus();

		CustomerDTUAccount = response.readEntity(String.class);
		response.close();
		System.out.println("HERE: "+CustomerDTUAccount + " Compared to "+ UUID.randomUUID());

	}

	@Then("the response status is {int}")
	public void the_response_status_is(Integer int1) {
		System.out.println(int1 + " should be " + responseStatus);
		if(responseStatus!=int1){
			System.out.println("Abra");
		}
		Assert.assertTrue(responseStatus == int1);
	}

	@Then("the user exists")
	public void the_user_exists() {
		WebTarget target = client.target("http://localhost:8080/customer/"+CustomerDTUAccount);
		Response response = target.request().get();
		Assert.assertTrue(responseStatus == 201);
	}

	@Given("Customer user exists")
	public void customerUserExists() {
		Account account = new Account();
		account.setBankAccountId(CustomerBankAccount);
		account.setCprNumber("TestCustomerCPR");
		account.setFirstName("John");
		account.setLastName("Smith");
		account.setType("0");

		WebTarget target = client.target("http://localhost:8080/customer/register");
		System.out.println("Here i go");
		Response response = target.request().post(Entity.entity(account, MediaType.APPLICATION_JSON));
		System.out.println("Heyyy im still here");

		responseStatus = response.getStatus();
		CustomerDTUAccount = response.readEntity(String.class);
		System.out.println(CustomerDTUAccount);
		response.close();
		Assert.assertTrue(responseStatus == 201);
	}

	@When("{int} Tokens are requested")
	public void tokensAreRequested(int arg0) {
		RequestTokens tokenrequest = new RequestTokens();
		tokenrequest.setCid(CustomerDTUAccount);
		tokenrequest.setAmount(arg0);
		WebTarget target = client.target("http://localhost:8080/customer/getToken");
		Response response = target.request().post(Entity.entity(tokenrequest, MediaType.APPLICATION_JSON));
		System.out.println("Got the tokens");
		responseStatus = response.getStatus();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}
		response.close();

	}

	@Then("We now have {int} tokens")
	public void We_now_have_tokens(Integer int1) {
		Assert.assertTrue(Customertokens.size()==int1);
	}

	@When("Merchant creation is requested")
	public void merchant_creation_is_requested() {
		Account account = new Account();
		account.setBankAccountId(MerchantBankAccount);
		account.setCprNumber("TestCustomerCPRMerchant21");
		account.setFirstName("Andreas");
		account.setLastName("Laursen");
		account.setType("1");

		WebTarget target = client.target("http://localhost:8080/merchant/register");
		Response response = target.request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

		responseStatus = response.getStatus();

		MerchantDTUAccount = response.readEntity(String.class);
		response.close();
	}

	@Then("the merchant user exists")
	public void the_merchant_user_exists() {
		WebTarget target = client.target("http://localhost:8080/merchant/"+CustomerDTUAccount);
		Response response = target.request().get();
		Assert.assertTrue(responseStatus == 200);
	}

	@And("Merchant user exists")
	public void merchantUserExists() {
		Account account = new Account();
		account.setBankAccountId(MerchantBankAccount);
		account.setCprNumber("TestMerchantCPR");
		account.setFirstName("John");
		account.setLastName("Smith");
		account.setType("1");

		WebTarget target = client.target("http://localhost:8080/merchant/register");
		Response response = target.request().post(Entity.entity(account, MediaType.APPLICATION_JSON));

		responseStatus = response.getStatus();
		MerchantDTUAccount = response.readEntity(String.class);
		response.close();
		Assert.assertTrue(responseStatus == 200);
	}

	@When("A payment of {int} using the first token is done")
	public void a_payment_of_using_the_first_token_is_done(Integer int1) {
		RequestPaymentData payment = new RequestPaymentData();
		payment.setAmount(int1);
		payment.setToken(Customertokens.get(0));
		Customertokens.remove(0);
		payment.setMerchantAccountId(MerchantDTUAccount);
		payment.setCustomerAccountId("");
		payment.setDescription("REEE");
		WebTarget target = client.target("http://localhost:8080/merchant/payment");
		Response response = target.request().post(Entity.entity(payment, MediaType.APPLICATION_JSON));
		responseStatus = response.getStatus();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}
		response.close();
	}

	@When("{int} Tokens are requested by {string}")
	public void tokensAreRequestedBy(int amount, String userID) {
		RequestTokens tokenrequest = new RequestTokens();
		tokenrequest.setCid(userID);
		tokenrequest.setAmount(amount);
		WebTarget target = client.target("http://localhost:8080/customer/getToken");
		Response response = target.request().post(Entity.entity(tokenrequest, MediaType.APPLICATION_JSON));
		System.out.println("Got the tokens");
		responseStatus = response.getStatus();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}


		response.close();
	}

	@When("the tokens are extracted from the response")
	public void theTokensAreExtractedFromTheResponse() {
		for (String s : (ArrayList<String>) responseEntity){
			Customertokens.add(s);
		}
	}

	@And("the customer has {int} in the bank")
	public void theCustomerHasInTheBank(int balance) throws BankServiceException_Exception {
		System.out.println(bank.getAccount(CustomerBankAccount).getBalance());
		Assert.assertTrue(bank.getAccount(CustomerBankAccount).getBalance().intValue()==balance);
	}

	@And("the merchant has {int} in the bank")
	public void theMerchantHasInTheBank(int balance) throws BankServiceException_Exception {
		System.out.println(bank.getAccount(MerchantBankAccount).getBalance());
		Assert.assertTrue(bank.getAccount(MerchantBankAccount).getBalance().intValue()==balance);
	}

	@When("i ask for a customer report")
	public void iAskForACustomerReport() {
		WebTarget target = client.target("http://localhost:8080/report/customer/"+CustomerDTUAccount);
		Response response = target.request().get();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}

		System.out.println("Heya");

		response.close();

	}

	@When("i ask for a merchant report")
	public void iAskForAMerchantReport() {
		WebTarget target = client.target("http://localhost:8080/report/merchant/"+MerchantDTUAccount);
		Response response = target.request().get();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}

		System.out.println("Heya");

		response.close();

	}

	ReturnTransactionInfo transactionInfo;
	@When("i unpackage the response")
	public void iUnpackageTheResponse() {
		HashMap transactionHash = (HashMap) responseEntity;
		transactionInfo = new ReturnTransactionInfo();
		transactionInfo.isSuccess = (boolean) transactionHash.get("success");
		transactionInfo.errorMessage = (String) transactionHash.get("errorMessage");
		transactionInfo.transactions = (ArrayList) transactionHash.get("transactions");
	}

	@Then("i get a succesful response with {int} transactions")
	public void iGetASuccesfulResponseWithTransactions(int amount) {
		Assert.assertTrue(transactionInfo.isSuccess);
		Assert.assertTrue(transactionInfo.transactions.size()==amount);
	}

	@When("i ask for a manager report")
	public void iAskForAManagerReport() {
		WebTarget target = client.target("http://localhost:8080/report/manager");
		Response response = target.request().get();
		responseStatus = response.getStatus();
		try{
			responseEntity = response.readEntity(Object.class);
		}catch (Exception e){
			responseEntity = null;
		}

		System.out.println("Heya");

		response.close();
	}

	@Then("i get a succesful response")
	public void iGetASuccesfulResponse() {
		Assert.assertTrue(transactionInfo.isSuccess);
	}
}

