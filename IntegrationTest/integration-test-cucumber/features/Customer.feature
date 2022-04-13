#/*
#@authors:
#Hejlsberg Jacob Kølbjerg - s194618
#Laforce Erik Aske - s194620
#*/

Feature: Customer service
#	Scenario: TestTest
#		When TestMethod

	Scenario: Creation is successful
		When Customer creation is requested
		Then the response status is 201

	Scenario: Creation successfully created and can be gotten by id
		When Customer creation is requested
		Then the response status is 201
		And the user exists

	Scenario: Tokens can be gathered
		Given Customer user exists
		When 4 Tokens are requested
		Then the response status is 200
		When the tokens are extracted from the response
		Then We now have 4 tokens

#TODO TRY WITH MISSING INFORMATION AND GET FAILED BACK
