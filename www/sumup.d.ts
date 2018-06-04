declare module 'cordova-plugin-sumup' {
  export function login(accessToken?: string): Promise<void>;
  export function logout(): Promise<void>;
  export function settings(): Promise<void>;
  export function prepare(): Promise<void>;
  export function pay(amount: number, currencycode: string, title: string): Promise<[string, string]>;
}