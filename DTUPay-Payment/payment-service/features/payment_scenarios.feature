Feature: Payment
  Scenario: Payment request - successful
    Given A paymentRequest is made with amount 100, token "TestToken" and cor id "CorID"
    Then The event was pushed
    When The token has been consumed and User id "TestCPRCustomer" has been returned
    Then The event was pushed to retrieve account customer id
    When The account has been handled and we have an account id customer
    And The success event was pushed

  Scenario: Payment - token failed
    Given A new paymentRequest is made with amount 100, token "TestToken" and cor id "CorID"
    When The event is pushed
    Then The token is consumed and User id "NoValidToken" is not returned
    And The event failure is pushed

  Scenario: Payment - customer account failed
    Given A paymentRequest is made with amount 100, token "TestToken" and cor id "CorID"
    When The event was pushed
    Then The token has been consumed and User id "TestCPRCustomerFail" has been returned
    When The event was pushed to retrieve account customer id
    Then The account has not been handled and we do not have an account id customer
    And  The account failure event is pushed


