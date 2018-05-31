package com.nuvopoint.cordova;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import com.sumup.merchant.api.SumUpState;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.Models.TransactionInfo;

import java.math.BigDecimal;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Sumup extends CordovaPlugin {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;
    private static final int REQUEST_CODE_LOGIN_SETTING = 4;

    private CallbackContext callback = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Runnable runnable = new Runnable() {
            public void run() {
                SumUpState.init(cordova.getActivity());
            }
        };

        cordova.getActivity().runOnUiThread(runnable);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        String affiliateKey = this.cordova.getActivity().getString(cordova.getActivity().getResources()
                .getIdentifier("SUMUP_API_KEY", "string", cordova.getActivity().getPackageName()));

        if (action.equals("login")) {

            Runnable runnable = new Runnable() {

                public void run() {

                    SumUpLogin sumUplogin = SumUpLogin.builder(affiliateKey).build();
                    SumUpAPI.openLoginActivity(cordova.getActivity(), sumUplogin, REQUEST_CODE_LOGIN);
                }
            };

            callback = callbackContext;
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("settings")) {

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpAPI.openPaymentSettingsActivity(cordova.getActivity(), REQUEST_CODE_LOGIN_SETTING);
                }
            };

            callback = callbackContext;
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("logout")) {

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpAPI.logout();
                }
            };

            callback = callbackContext;
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("prepare")) {

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpAPI.prepareForCheckout();
                }
            };

            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("pay")) {

            BigDecimal amount;
            try {
                amount = new BigDecimal(args.get(0).toString());
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Can't parse amount"));
                return false;
            }

            SumUpPayment.Currency currency;
            try {
                currency = SumUpPayment.Currency.valueOf(args.get(1).toString());
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Can't parse currency"));
                return false;
            }

            String title;
            try {
                title = args.get(2).toString();
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Can't parse title"));
                return false;
            }

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpPayment payment = SumUpPayment.builder().total(amount).currency(currency).title(title)
                            .skipSuccessScreen().build();

                    SumUpAPI.checkout(cordova.getActivity(), payment, REQUEST_CODE_PAYMENT);
                }
            };

            callback = callbackContext;
            cordova.setActivityResultCallback(this);
            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_LOGIN) {
            try {
                Bundle extra = data.getExtras();
                String code = "" + extra.getInt(SumUpAPI.Response.RESULT_CODE);
                String message = extra.getString(SumUpAPI.Response.MESSAGE);

                String[] res = new String[] { code, message };
                PluginResult result = new PluginResult(PluginResult.Status.OK, res);
                result.setKeepCallback(true);
                callback.sendPluginResult(result);

            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.getMessage()));
            }
        }

        if (requestCode == REQUEST_CODE_PAYMENT) {

            try {
                Bundle extras = data.getExtras();

                String txcode;
                TransactionInfo txinfo;
                if (extras != null) {
                    txcode = extras.getString(SumUpAPI.Response.TX_CODE);
                    txinfo = extras.getParcelable(SumUpAPI.Response.TX_INFO);
                }

                String[] res = new String[] { txcode, txinfo.getCard().getType() };
                PluginResult result = new PluginResult(PluginResult.Status.OK, res);
                result.setKeepCallback(true);
                callback.sendPluginResult(result);

            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.getMessage()));
            }
        }

        if (requestCode == REQUEST_CODE_LOGIN_SETTING) {
            SumUpAPI.openPaymentSettingsActivity(this.cordova.getActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
        }

        if (requestCode == REQUEST_CODE_PAYMENT_SETTINGS) {
            try {
                Bundle extra = data.getExtras();
                String code = "" + extra.getInt(SumUpAPI.Response.RESULT_CODE);
                String message = extra.getString(SumUpAPI.Response.MESSAGE);

                String[] res = new String[] { code, message };
                PluginResult result = new PluginResult(PluginResult.Status.OK, res);
                result.setKeepCallback(true);
                callback.sendPluginResult(result);

            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.getMessage()));
            }
        }
    }
}
