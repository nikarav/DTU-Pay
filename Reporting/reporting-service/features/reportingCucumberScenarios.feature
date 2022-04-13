Feature: Get Reports
  Scenario: Request transactions as a manager
    When Manager requests a report
    Then The event was pushed

  Scenario: Receive transactions as a manager
    When Manager receives transactions from payment service
    Then The event was pushed

  Scenario: Manage failed transaction retrieval as a manager
    When Manager does not receive transactions from payment service
    Then The event was pushed

  Scenario: Request transactions as a customer
    When Customer requests a report
    Then The event was pushed

  Scenario: Receive transactions as a customer
    When Customer receives transactions from payment service
    Then The event was pushed

  Scenario: Manage failed transaction retrieval as a customer
    When Customer does not receive transactions from payment service
    Then The event was pushed

  Scenario: Request transactions as a merchant
    When Merchant requests a report
    Then The event was pushed

  Scenario: Receive transactions as a merchant
    When Merchant receives transactions from payment service
    Then The event was pushed

  Scenario: Manage failed transaction retrieval as a merchant
    When Merchant does not receive transactions from payment service
    Then The event was pushed
