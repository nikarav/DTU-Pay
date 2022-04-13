Feature: CreateToken
  Scenario: CreateTokenFirstTimeSuccess
    Given No user exists with the id "TestId"
    When The user with id "TestId" requests 1 token
    Then Then user with id "TestId" has been created
    And The user with id "TestId" has 1 tokens
    Then The event was pushed

  Scenario: CreateTokenSuccess
    Given User exists with the id "TestId2" and 0 tokens
    When The user with id "TestId2" requests 2 token
    Then The user with id "TestId2" has 2 tokens
    Then The event was pushed

  Scenario: CreateTokenFailAlreadyHas2
    Given User exists with the id "TestId3" and 2 tokens
    When The user with id "TestId3" requests 2 token
    Then The user with id "TestId3" has 2 tokens
    Then The event was pushed

  Scenario: CreateTokenFailTooMany
    Given User exists with the id "TestId4" and 1 tokens
    When The user with id "TestId4" requests 7 token
    Then The user with id "TestId4" has 1 tokens
    Then The event was pushed

  Scenario: CreateTokenFailTooFew
    Given User exists with the id "TestId5" and 1 tokens
    When The user with id "TestId5" requests 0 token
    Then The user with id "TestId5" has 1 tokens
    Then The event was pushed