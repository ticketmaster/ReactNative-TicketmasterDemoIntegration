# React Native Ticketmaster SDK Application Integration Demo

This is an example integration of the Ticketmaster Ignite SDK, Retail, Tickets, and Authentication frameworks.

# Getting Started

> **Note**: Make sure you have completed the [React Native - Environment Setup](https://reactnative.dev/docs/environment-setup) instructions until the "Creating a new application" step, before proceeding.

# Running the app

Clone the project and then

```bash
cd ReactNative-TicketmasterDemoIntegration
yarn
cd ios
pod install
```

## Start the Metro Server

First, you will need to start **Metro**, the JavaScript _bundler_ that ships _with_ React Native.

To start Metro, run the following command from the _root_ of your React Native project:

```bash

# using npm
npm start

# OR using Yarn
yarn start
```

## Step 2: Start your Application

Let Metro Bundler run in its _own_ terminal. Open a _new_ terminal from the _root_ of your React Native project. Run the following command to start your _Android_ or _iOS_ app:

### For Android

```bash
# using npm
npm run android

# OR using Yarn
yarn android
```

### For iOS

```bash
# using npm
npm run ios

# OR using Yarn
yarn ios
```

This is one way to run your app — you can also run it directly from within Android Studio and Xcode respectively.

## Environment variables

You will need an API key for this app to run, you can get one here [Developer Account](https://developer-acct.ticketmaster.com/user/login)

For the Retail SDK (PrePurchase and Purchase) views, you will need an event ID, usually you would get this from API responses data from [Discovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/). In this app, you can create a .env file in the root of the project and put in the below variables. Replace "someapikey" with the API key from your Ticketmaster Developer Account.

```bash
API_KEY=someapikey
DEMO_EVENT_ID=1100607693B119D8
DEMO_ATTRACTION_ID=2873404
DEMO_HOST_ID=82789
DEMO_VENUE_ID=KovZpZAEdntA
```
