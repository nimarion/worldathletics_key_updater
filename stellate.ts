import { Config } from 'stellate'

const config: Config = {
  "config": {
    "name": "worldathletics",
    "originUrl": "wa-api-endpoint",
    "injectHeaders": true,
    "enablePlayground": true,
    "passThroughOnly": false,
    "headers": {
      "x-api-key": "wa-api-key"
    },
    "rules": [
      {
        "types": [
          "Query"
        ],
        "maxAge": 43200,
        "swr": 86400,
        "description": "cache all queries"
      }
    ],
    "customAttributes": {
      "athleteId": {
        "header": "x-athlete-id"
      }
    },
    "rootTypeNames": {
      "query": "Query",
      "mutation": "Mutation"
    }
  }
}

export default config;
