declare type SumUpSuccess = () => void;
declare type SumUpFailure = (err: Error) => void;

declare module 'cordova-plugin-sumup' {
  export function login(accessToken?: string, success?: SumUpSuccess, failure?: SumUpFailure): void;
  export function logout(success?: SumUpSuccess, failure?: SumUpFailure): void;
  export function settings(success?: SumUpSuccess, failure?: SumUpFailure): void;
  export function prepare(success?: SumUpSuccess, failure?: SumUpFailure): void;
  export function pay(amount: number, currencycode: string, title: string, success?: ([transactionCode, cardType]: [string, string]) => void, failure?: SumUpFailure): void;
}