// src/steps/item-api.steps.ts
import { After, Before, Given, Then, When } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ICustomWorld } from '../../../framework/support/world';

Before(async function (this: ICustomWorld) {
  await this.init();
});

After(async function (this: ICustomWorld) {
  await this.dispose();
});

When(
  'I send a POST request to {string} with invalid data:',
  async function (this: ICustomWorld, endpoint: string, dataTable: any) {
    const invalidData = dataTable.hashes()[0];
    console.log('endpoint requested: ,', endpoint);
    this.response = await this.apiContext?.post(endpoint, {
      data: {
        name: invalidData.name,
        quantity: parseInt(invalidData.quantity),
        price: parseFloat(invalidData.price),
      },
    });
    this.responseBody = await this.response?.json();
  }
);

When('I send a GET request to {string}', async function (this: ICustomWorld, endpoint: string) {
  this.response = await this.apiContext?.get(endpoint);
  this.responseBody = await this.response?.json();
});

When(
  'I send a PUT request to {string} with invalid data:',
  async function (this: ICustomWorld, endpoint: string, dataTable: any) {
    const invalidData = dataTable.hashes()[0];
    this.response = await this.apiContext?.put(endpoint, {
      data: {
        name: invalidData.name,
        quantity: parseInt(invalidData.quantity),
        price: parseFloat(invalidData.price),
      },
    });
    this.responseBody = await this.response?.json();
  }
);

When(
  'I send a PUT request to {string} with valid data:',
  async function (this: ICustomWorld, endpoint: string, dataTable: any) {
    const validData = dataTable.hashes()[0];
    const actualEndpoint = endpoint.replace('{id}', this.testData.get('createdItemId'));
    this.response = await this.apiContext?.put(actualEndpoint, {
      data: {
        name: validData.name,
        quantity: parseInt(validData.quantity),
        price: parseFloat(validData.price),
      },
    });
    this.responseBody = await this.response?.json();
  }
);

When('I send a DELETE request to {string}', async function (this: ICustomWorld, endpoint: string) {
  this.response = await this.apiContext?.delete(endpoint);
  try {
    this.responseBody = await this.response?.json();
  } catch (error) {
    this.responseBody = {};
  }
});

Then('the response status should be {int}', async function (this: ICustomWorld, status: number) {
  expect(this.response?.status()).toBe(status);
});

Then('the response should contain validation errors:', async function (this: ICustomWorld, dataTable: any) {
  const expectedErrors = dataTable.hashes();

  expect(this.responseBody.message).toBe('Validation failed');
  expect(Array.isArray(this.responseBody.errors)).toBeTruthy();

  expectedErrors.forEach((expectedError: any) => {
    const matchingError = this.responseBody.errors.find((error: any) => error.field === expectedError.field);
    expect(matchingError).toBeTruthy();
    expect(matchingError.error).toBe(expectedError.error.trim());
  });
});

Then('the response should contain error message {string}', async function (this: ICustomWorld, message: string) {
  expect(this.responseBody.message).toBe(message);
});

Given('I have created an item with data:', async function (this: ICustomWorld, dataTable: any) {
  const itemData = dataTable.hashes()[0];
  this.response = await this.apiContext?.post('/items', {
    data: {
      name: itemData.name,
      quantity: parseInt(itemData.quantity),
      price: parseFloat(itemData.price),
    },
  });
  this.responseBody = await this.response?.json();
  this.testData.set('createdItemId', this.responseBody.id);
  expect(this.response?.status()).toBe(201);
});

When(
  'I send a POST request to {string} with valid data:',
  async function (this: ICustomWorld, endpoint: string, dataTable: any) {
    const validData = dataTable.hashes()[0];
    this.response = await this.apiContext?.post(endpoint, {
      data: {
        name: validData.name,
        quantity: parseInt(validData.quantity),
        price: parseFloat(validData.price),
      },
    });
    this.responseBody = await this.response?.json();
  }
);

When(
  'I send a GET request to {string} with the created item id',
  async function (this: ICustomWorld, endpoint: string) {
    const actualEndpoint = endpoint.replace('{id}', this.testData.get('createdItemId'));
    this.response = await this.apiContext?.get(actualEndpoint);
    this.responseBody = await this.response?.json();
  }
);

When(
  'I send a PUT request to {string} with the created item id',
  async function (this: ICustomWorld, endpoint: string, dataTable: any) {
    const actualEndpoint = endpoint.replace('{id}', this.testData.get('createdItemId'));
    const validData = dataTable.hashes()[0];
    this.response = await this.apiContext?.put(actualEndpoint, {
      data: {
        name: validData.name,
        quantity: parseInt(validData.quantity),
        price: parseFloat(validData.price),
      },
    });
    this.responseBody = await this.response?.json();
  }
);

When(
  'I send a DELETE request to {string} with the created item id',
  async function (this: ICustomWorld, endpoint: string) {
    const actualEndpoint = endpoint.replace('{id}', this.testData.get('createdItemId'));
    this.response = await this.apiContext?.delete(actualEndpoint);
    try {
      this.responseBody = await this.response?.json();
    } catch (error) {
      // For 204 No Content responses
      this.responseBody = {};
    }
  }
);

Then('the response should be an empty array', async function (this: ICustomWorld) {
  expect(Array.isArray(this.responseBody)).toBeTruthy();
  expect(this.responseBody.length).toBe(0);
});

Then('the response should contain item details:', async function (this: ICustomWorld, dataTable: any) {
  const expectedData = dataTable.hashes()[0];
  expect(this.responseBody.name).toBe(expectedData.name);
  expect(this.responseBody.quantity).toBe(parseInt(expectedData.quantity));
  expect(this.responseBody.price).toBe(parseFloat(expectedData.price));
});

Then('the response should contain items:', async function (this: ICustomWorld, dataTable: any) {
  const expectedItems = dataTable.hashes();
  expect(Array.isArray(this.responseBody)).toBeTruthy();
  expect(this.responseBody.length).toBeGreaterThanOrEqual(expectedItems.length);

  expectedItems.forEach((expectedItem) => {
    const matchingItem = this.responseBody.find(
      (item: any) =>
        item.name === expectedItem.name &&
        item.quantity === parseInt(expectedItem.quantity) &&
        item.price === parseFloat(expectedItem.price)
    );
    expect(matchingItem).toBeTruthy();
  });
});
