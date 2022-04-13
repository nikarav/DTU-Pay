#/*
#@authors:
#De Santos Bernal Verconica - s210098
#Spyrou Thomas - s213161
#*/

Feature: Payment service
  Scenario: Customer Report
    Given Customer user exists
    When i ask for a customer report
    Then the response status is 201
    When i unpackage the response
    Then i get a succesful response with 0 transactions

  Scenario: Merchant Report
    Given Merchant user exists
    When i ask for a merchant report
    Then the response status is 200
    When i unpackage the response
    Then i get a succesful response with 0 transactions

  Scenario: Manager Report
    When i ask for a manager report
    Then the response status is 200
    When i unpackage the response
    Then i get a succesful response
