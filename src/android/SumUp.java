package com.nuvopoint.cordova.sumup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import com.sumup.merchant.api.SumUpState;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.cardreader.ReaderLibManager;
import com.sumup.merchant.CoreState;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.readerlib.CardReaderManager;
import com.sumup.merchant.Models.UserModel;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SumUp extends CordovaPlugin {

  private static final int REQUEST_CODE_LOGIN = 1;
  private static final int REQUEST_CODE_PAYMENT = 2;
  private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

  private CallbackContext callback = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    cordova.getActivity().runOnUiThread(() -> SumUpState.init(cordova.getActivity().getApplicationContext()));
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    String affiliateKey = this.cordova.getActivity().getString(cordova.getActivity().getResources()
            .getIdentifier("SUMUP_API_KEY", "string", cordova.getActivity().getPackageName()));

    if (action.equals("login")) {
      Runnable runnable = () -> {
        Object accessToken = null;
        try {
          accessToken = args.get(0);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }
        SumUpLogin sumUpLogin;
        if (accessToken != null) {
          sumUpLogin = SumUpLogin.builder(affiliateKey).accessToken(accessToken.toString()).build();
        } else {
          sumUpLogin = SumUpLogin.builder(affiliateKey).build();
        }
        SumUpAPI.openLoginActivity(cordova.getActivity(), sumUpLogin, REQUEST_CODE_LOGIN);
      };

      callback = callbackContext;
      cordova.setActivityResultCallback(this);
      cordova.getActivity().runOnUiThread(runnable);

      return true;
    }

    if (action.equals("auth")) {
      cordova.getThreadPool().execute(() -> {
        Object accessToken = null;
        try {
          accessToken = args.get(0);
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }

        if (accessToken != null) {
          UserModel um;
          um = CoreState.Instance().get(UserModel.class);
          um.setAccessToken(accessToken.toString());
          callbackContext.success();
        } else {
          callbackContext.error("No accessToken");
        }
      });

      return true;
    }

    if (action.equals("settings")) {
      callback = callbackContext;
      cordova.setActivityResultCallback(this);
      cordova.getActivity().runOnUiThread(() -> SumUpAPI.openPaymentSettingsActivity(cordova.getActivity(), REQUEST_CODE_PAYMENT_SETTINGS));

      return true;
    }

    if (action.equals("logout")) {
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));

      Handler handler = new Handler(cordova.getActivity().getMainLooper());
      handler.post(() -> SumUpAPI.logout());

      return true;
    }

    if (action.equals("prepare")) {

      Handler handler = new Handler(cordova.getActivity().getMainLooper());
      handler.post(() -> {

        ReaderLibManager rlm;
        rlm = CoreState.Instance().get(ReaderLibManager.class);

        if(!rlm.isReadyToTransmit() && CardReaderManager.getInstance() != null) {
          SumUpAPI.prepareForCheckout();
        }
      });

      return true;
    }

    if (action.equals("close")) {

      Handler handler = new Handler(cordova.getActivity().getMainLooper());
      handler.post(() -> {

        if(CardReaderManager.getInstance() != null) {
          CardReaderManager.getInstance().stopDevice();
        }
      });

      return true;
    }

    if (action.equals("pay")) {
      BigDecimal amount;

      try {
        amount = new BigDecimal(args.get(0).toString());
      } catch (Exception e) {
        JSONObject obj = new JSONObject();
        obj.put("code", 0);
        obj.put("message", "Can't parse amount");
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));

        return false;
      }

      SumUpPayment.Currency currency;
      try {
        currency = SumUpPayment.Currency.valueOf(args.get(1).toString());
      } catch (Exception e) {
        JSONObject obj = new JSONObject();
        obj.put("code", 0);
        obj.put("message", "Can't parse currency");
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));

        return false;
      }

      String title;
      try {
        title = args.get(2).toString();
      } catch (Exception e) {
        JSONObject obj = new JSONObject();
        obj.put("code", 0);
        obj.put("message", "Can't parse title");
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));

        return false;
      }

      SumUpPayment payment = SumUpPayment.builder()
        .total(amount)
        .currency(currency)
        .title(title)
        .skipSuccessScreen()
        .build();

      Runnable runnable = () -> {
        SumUpAPI.checkout(cordova.getActivity(), payment, REQUEST_CODE_PAYMENT);
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
        if (data != null) {
          Bundle extras = data.getExtras();
          Integer code = extras.getInt(SumUpAPI.Response.RESULT_CODE);
          String message = extras.getString(SumUpAPI.Response.MESSAGE);

          JSONObject obj = new JSONObject();
          obj.put("code", code);
          obj.put("message", message);

          if (code == 1) {
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          } else {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          }
        } else {
          JSONObject obj = new JSONObject();
          obj.put("code", 0);
          obj.put("message", "Login canceled");
          PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
          result.setKeepCallback(false);
          callback.sendPluginResult(result);
        }

      } catch (Exception e) {
        try {
          JSONObject obj = new JSONObject();
          obj.put("code", 0);
          obj.put("message", e.getMessage());
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));
        } catch (Exception er) {
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, er.getMessage()));
        }
      }
    }

    if (requestCode == REQUEST_CODE_PAYMENT) {

      try {
        if (data != null) {
          Bundle extras = data.getExtras();
          Integer code = extras.getInt(SumUpAPI.Response.RESULT_CODE);
          String message = extras.getString(SumUpAPI.Response.MESSAGE);
          TransactionInfo txinfo = extras.getParcelable(SumUpAPI.Response.TX_INFO);

          JSONObject obj = new JSONObject();

          if (txinfo != null) {
            obj.put("transaction_code", txinfo.getTransactionCode());
            obj.put("merchant_code", txinfo.getMerchantCode());
            obj.put("amount", txinfo.getAmount());
            obj.put("tip_amount", txinfo.getTipAmount());
            obj.put("vat_amount", txinfo.getVatAmount());
            obj.put("currency", txinfo.getCurrency());
            obj.put("status", txinfo.getStatus());
            obj.put("payment_type", txinfo.getPaymentType());
            obj.put("entry_mode", txinfo.getEntryMode());
            obj.put("installments", txinfo.getInstallments());
            obj.put("card_type", txinfo.getCard().getType());
          }

          if (code == 1) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          } else {
            obj.put("code", code);

            UserModel um;
            um = CoreState.Instance().get(UserModel.class);
            if(!um.isLoggedIn()) {
              obj.put("code", SumUpAPI.Response.ResultCode.ERROR_INVALID_TOKEN);
            } else {
              obj.put("code", code);
            }

            PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          }
        } else {
          PluginResult result = new PluginResult(PluginResult.Status.ERROR);
          result.setKeepCallback(false);
          callback.sendPluginResult(result);
        }

      } catch (Exception e) {
        try {
          JSONObject obj = new JSONObject();
          obj.put("code", 0);
          obj.put("message", e.getMessage());
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));
        } catch (Exception er) {
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, er.getMessage()));
        }
      }
    }

    if (requestCode == REQUEST_CODE_PAYMENT_SETTINGS) {
      try {

        if (data != null) {
          Bundle extras = data.getExtras();
          Integer code = extras.getInt(SumUpAPI.Response.RESULT_CODE);
          String message = extras.getString(SumUpAPI.Response.MESSAGE);

          JSONObject obj = new JSONObject();
          obj.put("code", code);
          obj.put("message", message);

          if (code == 1) {
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          } else {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
            result.setKeepCallback(false);
            callback.sendPluginResult(result);
          }
        } else {
          JSONObject obj = new JSONObject();
          obj.put("code", 0);
          obj.put("message", "Settings done");

          PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
          result.setKeepCallback(false);
          callback.sendPluginResult(result);
        }

      } catch (Exception e) {
        try {
          JSONObject obj = new JSONObject();
          obj.put("code", 0);
          obj.put("message", e.getMessage());
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, obj));
        } catch (Exception er) {
          callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, er.getMessage()));
        }
      }
    }
  }
}
