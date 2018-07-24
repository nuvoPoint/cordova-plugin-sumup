package com.nuvopoint.cordova.sumup;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SumUpService extends IntentService {

    private static final String TAG = "SumUpService";

    public SumUpService() {
        super(SumUpService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }
}