import { Config } from 'stellate'

const config: Config = {
  config: {
    name: 'worldathletics',
    originUrl: 'wa-api-endpoint',
    injectHeaders: true,
    enablePlayground: true,
    passThroughOnly: false,
    headers: {
      'x-api-key': 'wa-api-key',
    },
    rules: [
      {
        description: 'cache all queries',
        maxage: 43200,
        swr: 86400,
        types: ['Query'],
      },
    ],
  },
}
export default config
