#/*
#@authors:
#Karavasilis Nikolaos - s213685
#Siskou Melina - s213158
#*/

Feature: Payment service
  Scenario: Successful payment
    Given Customer user exists
    And Merchant user exists
    When 4 Tokens are requested
    Then the response status is 200
    When the tokens are extracted from the response
    And A payment of 10 using the first token is done
    Then the response status is 200
    And the customer has 990 in the bank
    And the merchant has 1010 in the bank

  Scenario: Ultimate test
    Given Customer user exists
    And Merchant user exists
    When 2 Tokens are requested
    Then the response status is 200
    When the tokens are extracted from the response
    When 2 Tokens are requested
    Then the response status is 400
    And A payment of 10 using the first token is done
    Then the response status is 200
    And A payment of 40 using the first token is done
    Then the response status is 200
    And the customer has 950 in the bank
    And the merchant has 1050 in the bank


#TODOLIST
  #Fake token
  #Fake Merchant
  #Failing bank accounts
  #Re-use token