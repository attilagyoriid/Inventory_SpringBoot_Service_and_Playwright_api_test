import { defineConfig, devices } from '@playwright/test';
require('dotenv').config();

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',

  use: {
    baseURL: `${process.env.npm_config_baseurl || process.env.BASE_URL || 'http://localhost:8095'}`,
    extraHTTPHeaders: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    navigationTimeout: 60000,
    trace: 'on',
  },
  projects: [
    {
      name: 'api-tests',
      grep: /@api/,
      use: {},
    },
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },

    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },

    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
});
