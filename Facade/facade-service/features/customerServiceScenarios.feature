Feature: Account registration
  Scenario: Account registration success
    Given there is an account with firstName "thomas", lastName "spyrou", cpr "1", type "0", bankAccountID "234"
    When the account is being registered
    Then the event "AccountRegistrationRequested" is published
    When the account is received from account management
    Then the account is created


  Scenario: Account registration fail
    Given there is an account with firstName "thomas", lastName "spyrou", cpr "2", type "0", bankAccountID "234"
    When the account is being registered
    Then the event "AccountRegistrationRequested" is published for the failed scenario
    When the account is not received from account management
    Then the account is not created
