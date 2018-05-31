declare module 'cordova-plugin-sumup' {
  export function login(success, failure): void;
  export function logout(success, failure): void;
  export function settings(success, failure): void;
  export function prepare(success, failure): void;
  export function pay(success: ([transactionCode, cardType]: [string, string]) => void, failure, amount, currencycode, title): void;
}