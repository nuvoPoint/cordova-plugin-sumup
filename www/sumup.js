var exec = cordova.require('cordova/exec');

module.exports = {
    login: function (accessToken, success, failure) {
        cordova.exec(success, failure, 'Sumup', 'login', accessToken ? [accessToken] : []);
    },
    logout: function (success, failure) {
        cordova.exec(success, failure, 'Sumup', 'logout', []);
    },
    settings: function (success, failure) {
        cordova.exec(success, failure, 'Sumup', 'settings', []);
    },
    prepare: function (success, failure) {
        cordova.exec(success, failure, 'Sumup', 'prepare', []);
    },    
    pay: function (amount, currencycode, title, success, failure) {
        cordova.exec(success, failure, 'Sumup', 'pay', [amount, currencycode, title]);
    },
}
