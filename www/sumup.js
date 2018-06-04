var exec = require('cordova/exec');

module.exports = {
    login: function (accessToken, success, failure) {
        cordova.exec(success, failure, 'SumUp', 'login', accessToken ? [accessToken] : []);
    },
    logout: function (success, failure) {
        cordova.exec(success, failure, 'SumUp', 'logout', []);
    },
    settings: function (success, failure) {
        cordova.exec(success, failure, 'SumUp', 'settings', []);
    },
    prepare: function (success, failure) {
        cordova.exec(success, failure, 'SumUp', 'prepare', []);
    },    
    pay: function (amount, currencycode, title, success, failure) {
        cordova.exec(success, failure, 'SumUp', 'pay', [amount, currencycode, title]);
    },
}
