#/*
#@authors:
#Hejlsberg Jacob Kølbjerg - s194618
#Laforce Erik Aske - s194620
#*/

Feature: Merchant service

  Scenario: Creation is successful
    When Merchant creation is requested
    Then the response status is 200

  Scenario: Creation successfully created and can be gotten by id
    When Merchant creation is requested
    Then the response status is 200
    And the merchant user exists



    #TODO try with missing information and get failed back