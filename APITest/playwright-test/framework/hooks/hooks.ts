import { BeforeAll, AfterAll, Before, After } from '@cucumber/cucumber';
import { getEnv } from '../config/env/env';
import { createLogger } from 'winston';
import { options } from '../logger/logger';
import { CustomWorld } from '../support/world';
import { ICustomWorld } from '../../framework/support/world';

BeforeAll(async function () {
  getEnv();
});

Before(async function (this: CustomWorld, { pickle }) {});

After(async function ({ pickle }) {});

AfterAll(async () => {});

After(async function (this: ICustomWorld) {
  // Clean up created items
  const createdItemId = this.testData.get('createdItemId');
  if (createdItemId) {
    try {
      await this.apiContext?.delete(`/items/${createdItemId}`);
    } catch (error) {
      console.log('Error cleaning up item:', error);
    }
  }

  const createdItems = this.testData.get('createdItems');
  if (createdItems) {
    for (const item of createdItems) {
      try {
        await this.apiContext?.delete(`/items/${item.id}`);
      } catch (error) {
        console.log('Error cleaning up item:', error);
      }
    }
  }

  // Clear the test data
  this.testData.clear();
  await this.dispose();
});
