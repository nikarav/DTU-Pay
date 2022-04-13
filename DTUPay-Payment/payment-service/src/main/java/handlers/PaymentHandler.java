package handlers;
/*
authors:
Karavasilis Nikolaos - s213685
Hejlsberg Jacob Kølbjerg - s194618
 */

import dto.CreatePaymentData;
import dto.Utils;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import messaging.MessageQueue;
import models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PaymentHandler {

    BankService bankService = new BankServiceService().getBankServicePort();
    private List<Transaction> transactionList = new ArrayList<Transaction>();
    Utils utils = new Utils();
    MessageQueue queue;


    public boolean postCreatePayment(CreatePaymentData paymentData) throws Exception{
        // Validation
        if(paymentData == null){
            throw new Exception("Invalid JSON");
        }
        else if(paymentData.getAmount() == 0){
            throw new Exception("No amount was given");
        }
        else if(paymentData.getCustomerBankAccountId().equals("") || paymentData.getMerchantBankAccountId().equals("")){
            throw new Exception("Some of the bank ids were not given");
        }
        else if(paymentData.getDescription().equals("")){
            throw new Exception("No payment description was given");
        }

        try{
            // Contact bank to do the payment
            bankService.transferMoneyFromTo(paymentData.getCustomerBankAccountId(),
                    paymentData.getMerchantBankAccountId(),
                    BigDecimal.valueOf(paymentData.getAmount()),
                    paymentData.getDescription());

            // Keep track of transactions
            Transaction transactionToBeAdded = new Transaction();
            transactionToBeAdded.setTransactionId(utils.generateTransationId());
            transactionToBeAdded.setAmount(paymentData.getAmount());
            transactionToBeAdded.setDescription(paymentData.getDescription());
            transactionToBeAdded.setCustomerBankId(paymentData.getCustomerBankAccountId());
            transactionToBeAdded.setMerchantBankId(paymentData.getMerchantBankAccountId());
            transactionToBeAdded.setToken(paymentData.getToken());
            transactionToBeAdded.setTime(LocalDateTime.now());

            transactionList.add(transactionToBeAdded);

            return true;
        }
        catch (BankServiceException_Exception e) {
            throw new Exception("Bank payment failed: " + e.getMessage());
        }
        catch (Exception e){
            throw new Exception("DTUPay payment failed:" + e.getMessage());
        }
    }

    public List<Transaction> getTransactionList(){
        return Stream.of(transactionList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Transaction> getMerchantTransactions(String merchantBankAccountId){
        List<Transaction> transactionsToReturn = transactionList.stream()
                .filter((elm) -> elm.getMerchantBankId().equals(merchantBankAccountId))
                .collect(Collectors.toList());

        //hide customerAccountId from merchant
        for (Transaction transaction : transactionsToReturn)
        {
            transaction.setCustomerBankId("");
        }
        return transactionsToReturn;
    }

    public List<Transaction> getCustomerTransactions(String customerBankAccountId){
        List<Transaction> transactionsToReturn = transactionList.stream()
                .filter((elm) -> elm.getCustomerBankId().equals(customerBankAccountId))
                .collect(Collectors.toList());

        return transactionsToReturn;
    }

}
