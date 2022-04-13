Feature: GetData
  Scenario: GetAllData
    Given The DataBase is empty
    And User exists with the id "Test1" and 1 tokens
    And User exists with the id "Test2" and 2 tokens
    And User exists with the id "Test3" and 3 tokens
    When The database is requested
    Then The event was pushed