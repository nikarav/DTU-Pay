package handlers;
/*
authors:
De Santos Bernal Veronica - s210098
Spyrou Thomas - s213161
 */
import DTO.CreateAccountData;
import DTO.Utils;
import Entities.Account;
import Entities.AccountType;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountHandler {

    BankService bankService = new BankServiceService().getBankServicePort();
    Utils utils = new Utils();
    private List<Account> accounts = new ArrayList<Account>();

    public String createAccountFromData(CreateAccountData createAccountData) throws Exception {
        //Empty name
        if (createAccountData.getFirstName().equals("") || createAccountData.getLastName().equals("")) {
            throw new Exception("Empty name");
        } else if (createAccountData.getCprNumber().isEmpty()) {
            throw new Exception("No CPR number provided");
        }

        //Do not allow account with same cprNumber-bankAccountId combination to be created
        if (accounts.stream().anyMatch(acc -> acc.getCprNumber().equals(createAccountData.getCprNumber())
                && acc.getBankAccountId().equals(createAccountData.getBankAccountId()))) {
            throw new Exception("Account already exists");
        }

        //Check that bank account exists
        try {
            bankService.getAccount(createAccountData.getBankAccountId());
        } catch (Exception e) {
            throw new Exception("Bank does not recognise id: " + createAccountData.getBankAccountId());
        }

        Account accountToBeAdded = new Account();

        accountToBeAdded.setCprNumber(createAccountData.getCprNumber());
        accountToBeAdded.setBankAccountId(createAccountData.getBankAccountId());
        accountToBeAdded.setFirstName(createAccountData.getFirstName());
        accountToBeAdded.setLastName(createAccountData.getLastName());
        accountToBeAdded.setAccountId(utils.generateAccountId());

        switch (createAccountData.getType()) {
            case 0:
                accountToBeAdded.setType(AccountType.CUSTOMER);
                break;
            case 1:
                accountToBeAdded.setType(AccountType.MERCHANT);
                break;
            default:
                throw new Exception("Type did not match");
        }

        accounts.add(accountToBeAdded);
        return accountToBeAdded.getAccountId();
    }

    public Account getAccount(String accountID) {
        Optional<Account> accountToReturn = accounts.stream().
                filter((elm) -> elm.getAccountId().equals(accountID)).findFirst();

        return accountToReturn.orElse(null);
    }

    public String getAccountBankId(String accountID) throws Exception {
        String bankAccountId = "";

        Optional<Account> accountToReturn = accounts.stream().
                filter((elm) -> elm.getAccountId().equals(accountID)).findFirst();

        if(accountToReturn.isPresent()){
            bankAccountId = accountToReturn.get().getBankAccountId();
        }
        else{
            throw new Exception("Account does not exist");
        }

        return bankAccountId;
    }

    public List<Account> getAccounts() {
        return Stream.of(accounts)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public boolean deleteAccountInfo(String id) {
        return accounts.removeIf(e -> e.getAccountId().equals(id));
    }

}
