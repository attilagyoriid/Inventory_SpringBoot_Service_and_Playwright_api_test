@api
Feature: Inventory Service Item API Endpoints Validation

    As a user 
    I want to have an Inventory Service
    So that I can manage items 

  Scenario: Create item with invalid data
    When I send a POST request to "/items" with invalid data:
      | name | quantity | price |
      |      | -1      | -10.0 |
    Then the response status should be 400
    And the response should contain validation errors:
      | field    | error                                      |
      | name     | Name is required                          |
      | quantity | Quantity must be greater than or equal to 0|
      | price    | Price must be greater than or equal to 0   |

  Scenario: Get item with non-existing ID
    When I send a GET request to "/items/<id>"
    Then the response status should be 404
    And the response should contain error message "Item not found with id: <id>"

    Examples:
      | id                                  |
      | 00000000-0000-0000-0000-000000000000|
      | 11111111-1111-1111-1111-111111111111|
      | 22222222-2222-2222-2222-222222222222|

  Scenario: Update item with invalid data
    When I send a PUT request to "/items/00000000-0000-0000-0000-000000000000" with invalid data:
      | name | quantity | price |
      |      | -1      | -10.0 |
    Then the response status should be 400
    And the response should contain validation errors:
      | field    | error                                      |
      | name     | Name is required                          |
      | quantity | Quantity must be greater than or equal to 0|
      | price    | Price must be greater than or equal to 0   |


  Scenario: Update non-existing item
    When I send a PUT request to "/items/<id>" with valid data:
      | name     | quantity | price |
      | Test Item| 10      | 99.99 |
    Then the response status should be 404
    And the response should contain error message "Item not found with id: <id>"

    Examples:
      | id                                  |
      | 00000000-0000-0000-0000-000000000000|
      | 11111111-1111-1111-1111-111111111111|
      | 22222222-2222-2222-2222-222222222222|

  Scenario: Delete non-existing item
    When I send a DELETE request to "/items/<id>"
    Then the response status should be 404
    And the response should contain error message "Item not found with id: <id>"

    Examples:
      | id                                  |
      | 00000000-0000-0000-0000-000000000000|
      | 11111111-1111-1111-1111-111111111111|
      | 22222222-2222-2222-2222-222222222222|

  Scenario: Create item with valid data
    When I send a POST request to "/items" with valid data:
      | name        | quantity | price |
      | Test Item 1 | 10      | 99.99 |
    Then the response status should be 201
    And the response should contain item details:
      | name        | quantity | price |
      | Test Item 1 | 10      | 99.99 |

  Scenario: Get all items with data
    Given I have created an item with data:
      | name        | quantity | price |
      | Test Item 2 | 5       | 49.99 |
    When I send a GET request to "/items"
    Then the response status should be 200
    And the response should contain items:
      | name        | quantity | price |
      | Test Item 2 | 5       | 49.99 |

  Scenario: Get item by ID
    Given I have created an item with data:
      | name        | quantity | price |
      | Test Item 3 | 15      | 29.99 |
    When I send a GET request to "/items/{id}" with the created item id
    Then the response status should be 200
    And the response should contain item details:
      | name        | quantity | price |
      | Test Item 3 | 15      | 29.99 |

  Scenario: Update item with valid data
    Given I have created an item with data:
      | name        | quantity | price |
      | Test Item 4 | 20      | 39.99 |
    When I send a PUT request to "/items/{id}" with valid data:
      | name            | quantity | price |
      | Updated Item 4  | 25      | 44.99 |
    Then the response status should be 200
    And the response should contain item details:
      | name           | quantity | price |
      | Updated Item 4 | 25      | 44.99 |

  Scenario: Delete existing item
    Given I have created an item with data:
      | name        | quantity | price |
      | Test Item 5 | 30      | 59.99 |
    When I send a DELETE request to "/items/{id}" with the created item id
    Then the response status should be 204