Feature: Consume tokens
  Scenario: ConsumeTokenSuccess
    Given User exists with the id "TestId6" and 3 tokens
    When The user with id "TestId6" consumes a token
    Then The user with id "TestId6" has 2 tokens

  Scenario: ConsumeTokenFailure
    Given User exists with the id "TestId7" and 0 tokens
    When The user consumes a token with wrong uuid
    Then The user with id "TestId7" has 0 tokens
    Then The event was pushed
