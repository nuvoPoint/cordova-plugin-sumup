export type Success = () => void;
export type Failure = (err: Error) => void;

declare module 'cordova-plugin-sumup' {
  export function login(accessToken?: string, success?: Success, failure?: Failure): void;
  export function logout(success?: Success, failure?: Failure): void;
  export function settings(success?: Success, failure?: Failure): void;
  export function prepare(success?: Success, failure?: Failure): void;
  export function pay(amount: number, currencycode: string, title: string, success?: ([transactionCode, cardType]: [string, string]) => void, failure?: Failure): void;
}