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

    // private CallbackContext callback = null;

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

            cordova.getActivity().runOnUiThread(runnable);
            callbackContext.success(SumUpAPI.Response);
            return true;
        }

        if (action.equals("settings")) {

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpAPI.openPaymentSettingsActivity(cordova.getActivity(), REQUEST_CODE_LOGIN_SETTING);
                    callbackContext.success();
                }
            };

            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("logout")) {

            Runnable runnable = new Runnable() {
                public void run() {

                    SumUpAPI.logout();
                    callbackContext.success();
                }
            };

            cordova.getActivity().runOnUiThread(runnable);
            return true;
        }

        if (action.equals("pay")) {

            Runnable runnable = new Runnable() {
                public void run() {
                    
                    SumUpPayment payment = SumUpPayment.builder()
                            // mandatory parameters
                            .total(new BigDecimal("8.00")) // minimum 1.00
                            .currency(SumUpPayment.Currency.DKK)
                            // optional: add details
                            .title("Taxi Ride").receiptEmail("customer@mail.com").receiptSMS("+3531234567890")
                            // optional: Add metadata
                            .addAdditionalInfo("AccountId", "taxi0334").addAdditionalInfo("From", "Paris")
                            .addAdditionalInfo("To", "Berlin")
                            // optional: foreign transaction ID, must be unique!
                            .foreignTransactionId(UUID.randomUUID().toString()) // can not exceed 128 chars
                            .build();

                    SumUpAPI.checkout(cordova.getActivity(), payment, REQUEST_CODE_PAYMENT);
                }
            };

            cordova.getActivity().runOnUiThread(runnable);
            callbackContext.success(SumUpAPI.Response);
            return true;
        }

        return false;
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_LOGIN) {
            Bundle extra = data.getExtras();
            int code = extra.getInt(SumUpAPI.Response.RESULT_CODE);
            String message = extra.getString(SumUpAPI.Response.MESSAGE);

            JSONObject res = new JSONObject();
            try {
                res.put("code", code);
                res.put("message", message);
            } catch (Exception e) {
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK, res);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }

        if (requestCode == REQUEST_CODE_PAYMENT) {

            Bundle extras = data.getExtras();

            String code = "";
            String txcode = "";
            String message = "";
            if (extras != null) {
                message = "" + extras.getString(SumUpAPI.Response.MESSAGE);
                txcode = "" + extras.getString(SumUpAPI.Response.TX_CODE);
                code = "" + extras.getInt(SumUpAPI.Response.RESULT_CODE);
            }

            JSONObject res = new JSONObject();
            try {
                res.put("code", code);
                res.put("message", message);
                res.put("txcode", txcode);
            } catch (Exception e) {
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK, res);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }

        if (requestCode == REQUEST_CODE_LOGIN_SETTING) {
            SumUpAPI.openPaymentSettingsActivity(this.cordova.getActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
        }

        if (requestCode == REQUEST_CODE_PAYMENT_SETTINGS) {
            Bundle extra = data.getExtras();
            int code = extra.getInt(SumUpAPI.Response.RESULT_CODE);
            String message = extra.getString(SumUpAPI.Response.MESSAGE);

            JSONObject res = new JSONObject();
            try {
                res.put("code", code);
                res.put("message", message);
            } catch (Exception e) {
            }

            PluginResult result = new PluginResult(PluginResult.Status.OK, res);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }
    }
    */
}