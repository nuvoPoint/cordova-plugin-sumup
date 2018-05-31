var exec = cordova.require('cordova/exec');

module.exports = {
    login: function (success, failure) {
        cordova.exec(success, failure, 'Sumup', 'login', []);
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
    pay: function (success, failure, amount, currencycode, title) {
        cordova.exec(success, failure, 'Sumup', 'pay', [amount, currencycode, title]);
    },
}
