# Description
Beta version of a Cordova integration with SumUp's Android and iOS implementations.

# Installation
Add the plugin:
cordova plugin add https://github.com/nuvoPoint/cordova-plugin-sumup --variable SUMUP_API_KEY=INSERT_YOUR_KEY

# Usage
Importing the plugin (note, the plugin does NOT use global variables):

`import * as SumUp from 'cordova-plugin-sumup';`

## Authenticating

### Login to SumUp
`SumUp.login(accessToken?: string): Promise<void>`

Supports an optional access token.
- If no access token is given, a login screen will be shown.
- If an error occurs, the promise is rejected with a SumUpError object.

After logging in, you need to keep the access token up to date using (expires every hour):

`SumUp.auth(accessToken: string): Promise<void>`

Log out using
`SumUp.logout()`

## Taking Payments
`SumUp.pay(amount: number, currencycode: string, title: string): Promise<SumUpPayment>`

Example usage:
~~~~
async pay(payment: Payment) {
  try {
    const sumUpPayment = await SumUp.pay(payment.amount, payment.currency, 'My Business');
    const transactionCode = sumUpPayment.transaction_code;
    const cardName = sumUpPayment.card_type;
  } catch (e) {
    if (e.code === 5) {
      console.error('Token expired')
    }

    e.code = SumUpErrorCodes.PAYMENT_FAILED;
    throw e;
  }
}
~~~~

### Preparing the terminal
`prepare(): Promise<void>`

This will wake the terminal up. Beware, this can cause issues in the native plugin if used too liberally, see https://github.com/sumup/sumup-android-sdk/issues/57

Note `SumUpPayment` is an interface of the type:

~~~~
interface SumUpPayment {
    transaction_code: string;
    card_type: string;
    merchant_code: string;
    amount: number;
    tip_amount: number;
    vat_amount: number;
    currency: string;
    status: string;
    payment_type: string;
    entry_mode: string;
    installments: number;
  }
~~~~

## Misc
`SumUp.settings(): Promise<void>`

Opens the settings page.

# FINAL NOTES
We've moved away from Cordova, and are not actively supporting this repo at the moment.
