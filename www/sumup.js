var exec = cordova.require('cordova/exec');

var Sumup = function () {
    console.log('Sumup instanced');
};

Sumup.prototype.init = function (msg, onSuccess, onError) {
    var errorCallback = function (obj) {
        onError(obj);
    };

    var successCallback = function (obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Sumup', 'init', [msg]);
};

Sumup.prototype.login = function (success, failure) {
    cordova.exec(success, failure, 'Sumup', 'login', []);
};

Sumup.prototype.logout = function (success, failure) {
    cordova.exec(success, failure, 'Sumup', 'logout', []);
};

Sumup.prototype.settings = function (success, failure) {
    cordova.exec(success, failure, 'Sumup', 'settings', []);
};

Sumup.prototype.prepare = function (success, failure) {
    cordova.exec(success, failure, 'Sumup', 'prepare', []);
};

Sumup.prototype.pay = function (success, failure, amount, currencycode, title) {
    cordova.exec(success, failure, 'Sumup', 'pay', [amount, currencycode, title]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = Sumup;
}