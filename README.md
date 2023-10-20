[![Powered by Stellate, the GraphQL Edge Cache](https://stellate.co/badge.svg)](https://stellate.co/?ref=powered-by)

# World Athletics GraphQL Credentials Fetcher

Periodically visits the World Athletics website and fetches the GraphQL credentials from the network requests using Selenium.
Credentials are then pushed to Stellate using stellate-cli.

Used for the [WorldAthletics Proxy Server](https://github.com/nimarion/worldathletics)

## Built with 

- [Selenium](https://www.selenium.dev/) - The Selenium Browser Automation Project
- [Stellate](https://stellate.co/) - GraphQL Edge Caching
- [Github Actions](https://github.com/features/actions) - CI/CD