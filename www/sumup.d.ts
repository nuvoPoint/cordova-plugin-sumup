declare module 'cordova-plugin-sumup' {
  export class Sumup {
    login(success, failure): void;
    logout(success, failure): void;
    settings(success, failure): void;
    prepare(success, failure): void;
    pay(success: ([transactionCode, cardType]: [string, string]) => void, failure, amount, currencycode, title): void;
  }
}