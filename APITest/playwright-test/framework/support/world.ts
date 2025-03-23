// framework/support/world.ts
import { IWorldOptions, setWorldConstructor } from '@cucumber/cucumber';
import { APIRequestContext, APIResponse, request } from '@playwright/test';

export interface ICustomWorld {
  apiContext?: APIRequestContext;
  response?: APIResponse;
  responseBody?: any;
  init(): Promise<void>;
  dispose(): Promise<void>;
  testData: Map<string, any>;
}

export class CustomWorld implements ICustomWorld {
  apiContext?: APIRequestContext;
  response?: APIResponse;
  responseBody?: any;
  testData: Map<string, any>;

  constructor(options: IWorldOptions) {
    this.testData = new Map<string, any>();
  }

  async init(): Promise<void> {
    this.apiContext = await request.newContext({
      baseURL: `${process.env.npm_config_baseurl || process.env.BASE_URL || 'http://localhost:8095'}`,
      extraHTTPHeaders: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
    });
  }

  async dispose(): Promise<void> {
    if (this.apiContext) {
      await this.apiContext.dispose();
      this.apiContext = undefined;
    }
  }
}

setWorldConstructor(CustomWorld);
