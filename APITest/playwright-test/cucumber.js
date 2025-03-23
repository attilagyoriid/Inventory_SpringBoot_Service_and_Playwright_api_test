module.exports = {
  default: {
    tags: process.env.npm_config_tags || '',
    formatOptions: {
      snippetInterface: 'async-await',
    },
    paths: ['tests/**/features/**/*.feature'],
    publishQuiet: false,
    dryRun: false,
    require: ['tests/**/steps/**/*.ts', 'framework/hooks/hooks.ts', 'framework/support/*.ts'],
    requireModule: ['ts-node/register'],
    format: [
      'html:test-results/cucumber-report.html',
      'json:test-results/cucumber-report.json',
      'junit:test-results/junit-report.xml',
      'rerun:@rerun.txt',
    ],
    parallel: +process.env.npm_config_parallel || 3,
  },
  rerun: {
    formatOptions: {
      snippetInterface: 'async-await',
    },
    publishQuiet: true,
    dryRun: false,
    require: ['tests/**/steps/**/*.ts', 'framework/hooks/hooks.ts'],
    requireModule: ['ts-node/register'],
    format: [
      'html:test-results/cucumber-report.html',
      'json:test-results/cucumber-report.json',
      'junit:test-results/junit-report.xml',
      'rerun:@rerun.txt',
    ],
    parallel: 2,
  },
};
