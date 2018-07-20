var exec = require('cordova/exec');

// iOS
// SMPSumUpSDKErrorGeneral             = 0,        // General error
// SMPSumUpSDKErrorActivationNeeded    = 1,        // The merchant's account is not activated
// SMPSumUpSDKErrorAccountGeneral      = 20,
// SMPSumUpSDKErrorAccountNotLoggedIn  = 21,       // The merchant is not logged into his account.
// SMPSumUpSDKErrorAccountIsLoggedIn   = 22,       // A merchant is logged in already. Call logout before logging in again.
// SMPSumUpSDKErrorCheckoutGeneral     = 50,
// SMPSumUpSDKErrorCheckoutInProgress  = 51,       // Another checkout process is currently in progress.

module.exports = {
    login: function (accessToken) {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'login', accessToken ? [accessToken] : []));
    },
    auth: function (accessToken) {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'auth', accessToken ? [accessToken] : []));
    },
    logout: function () {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'logout', []));
    },
    settings: function () {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'settings', []));
    },
    prepare: function () {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'prepare', []));
    },
    pay: function (amount, currencycode, title) {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'pay', [amount, currencycode, title]));
    },
}
