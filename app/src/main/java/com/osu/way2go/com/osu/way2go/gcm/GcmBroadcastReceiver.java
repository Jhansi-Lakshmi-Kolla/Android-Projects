package com.osu.way2go.com.osu.way2go.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jhansi_lak on 11/7/2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{
    private static final String TAG = "GcmBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmMessageHandler will handle the intent.
        Log.i(TAG, "GcmBroadcastReceiver invoked");
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmMessageHandler.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
