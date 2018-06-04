var exec = require('cordova/exec');

module.exports = {
    login: function (accessToken) {
        return new Promise((resolve, reject) => cordova.exec(resolve, reject, 'SumUp', 'login', accessToken ? [accessToken] : []));
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
