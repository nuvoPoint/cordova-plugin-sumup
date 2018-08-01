declare module 'cordova-plugin-sumup' {
  export interface SumUpError {
    code: number;
    message?: string;
  }

  export interface SumUpPayment {
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

  /**
   * Login to SumUp
   * Supports an optional access token.
   * If no access token is given, a login screen will be shown.
   * If an error occurs, the promise is rejected with a SumUpError object.
   *
   * @export
   * @param {string} [accessToken]
   * @returns {Promise<void>} When the promise resolves, it means login was successful.
   */
  export function login(accessToken?: string): Promise<void>;

  export function auth(accessToken?: string): Promise<void>;

  /**
   * Logout of SumUp
   * If an error occurs, the promise is rejected with a SumUpError object.
   *
   * @export
   * @returns {Promise<void>}
   */
  export function logout(): Promise<void>;

  /**
   * Opens the settings dialog
   * If an error occurs, the promise is rejected with a SumUpError object.
   *
   * @export
   * @returns {Promise<void>}
   */
  export function settings(): Promise<void>;

  /**
   * Will awake the terminal for a transaction. Use a bit before the transaction is expected to take place, to make the process faster.
   * Will silently fail if not logged in, and does not support errors.
   * 
   * The promise will always resolve, and never fail.
   *
   * @export
   * @returns {Promise<void>}
   */
  export function prepare(): Promise<void>;

  export function close(): Promise<void>;

  /**
   * Do a payment.
   * If an error occurs, the promise is rejected with a SumUpError object.
   *
   * @export
   * @param {number} amount
   * @param {string} currencycode
   * @param {string} title
   * @returns {Promise<SumUpPayment>} If the payment is successful
   */
  export function pay(amount: number, currencycode: string, title: string): Promise<SumUpPayment>;
}