# This is a [Playwright](https://playwright.dev/) project to cover api test level using BDD scenarios with cucumber implementation

- Author: Attila Gyori
- Email: gyoriattila@yahoo.com
- Website: https://attila-gyori.com

![Playwright](assets/playwright.png)

> Playwright with Cucumber, API endpoint test, cucumber report project.

### Built With

This section lists any major frameworks/libraries used to bootstrap project.

[![Playwright][Playwright]][Playwright-url]
[![Typescript][Typescript]][Typescript-url]
[![Cucumber][Cucumber]][Cucumber-url]

## 🚀 Execute the project

### Install dependencies

```bash
npm i
```

### Spin up Service to be tested (Inventory Service Spring boot application)

```bash
Read README.md in Inventory Service folder
```

### Execute api tests

```bash
npm run test:api --baseurl=http://localhost:8095
```

### Open test run result

Open generated report in playwright-test\test-results\cucumber-report.html with your browser to see the result.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[Playwright]: https://img.shields.io/static/v1?style=for-the-badge&message=Playwright&color=2EAD33&logo=Playwright&logoColor=FFFFFF&label=
[Playwright-url]: https://playwright.dev/
[Cucumber]: https://img.shields.io/badge/cucumber-8A2BE2
[Cucumber-url]: https://cucumber.io/docs/installation/javascript/
[Typescript]: https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white
[Typescript-url]: https://www.typescriptlang.org/
