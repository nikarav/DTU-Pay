Feature:
  Scenario: Request Bank AccountID success
    Given a bank account exist with firstname "tommy", lastname "flou", cpr "melinaki" and amount 0
    And a user exists with firstname "tommy", lastname "flou", cpr "melinaki" and the given bank account
    When a request for bank account id using the accountID is sent
    Then the event type is "BankAccountIdRetrievalSucceeded"
    Then the bank account id is returned

  Scenario: Request Bank AccountID fail
    Given the account id is "IDontExist"
    And the account with the account id doesnt exist
    When a request for bank account id using the accountID is sent
    Then the event type is "BankAccountIdRetrievalFailed"

  #Bank account creation
  #User creation
  #Bank account request

  Scenario: Request DTU AccountID success
    Given a bank account exist with firstname "tommy", lastname "flou", cpr "melinaki" and amount 0
    And a user exists with firstname "tommy", lastname "flou", cpr "melinaki" and the given bank account
    When the account is requested
    Then the event type is "AccountRequestCompleted"
    And the accounts name is "tommy"


  Scenario: Request DTU AccountID fail
    Given the account id is "IDontExist"
    And the account with the account id doesnt exist
    When the account is requested
    Then the event type is "AccountRequestFailed"


  #  Scenario: Delete DTU AccountID success


  #  Scenario: Delete DTU AccountID success