Feature: Test Account Management microservice
  Scenario: Create a Customer failed
    When a "AccountRegistrationRequested" event to fail for a customer is received with cpr "tommycpr123", firstname "tommy", lastname "flou" and type 0
    Then the "AccountRegistrationFailed" event for customer is sent fail
    And the customer is not created

Scenario: Create a customer success
  Given a bank account exist with firstname "tommy", lastname "flou", cpr "melinaki" and amount 0
  When a "AccountRegistrationRequested" event for a customer is received with cpr "tommycpr123", firstname "tommy", lastname "flou" and type 0
  Then the "AccountRegistrationCompleted" event is sent success
  And the customer is created

  Scenario: Create a Merchant failed
    When a "AccountRegistrationRequested" event for a merchant is received with cpr "tommycpr123", firstname "tommy", lastname "flou" and type 1
    Then the "AccountRegistrationFailed" event for merchant is sent fail
    And the merchant is not created

 Scenario: Create a Merchant success
   Given a bank account for a merchant exist with firstname "niko", lastname "mel", cpr "123456" and amount 10
   When a "AccountRegistrationRequested" AccountRegistrationRequested for a merchant is received with cpr "niko", firstname "mel", lastname "123456" and type 1
   Then the "AccountRegistrationCompleted" event for merchant is sent success
   And the merchant is created

# completed
