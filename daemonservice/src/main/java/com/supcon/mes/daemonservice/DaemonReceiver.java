package com.supcon.mes.daemonservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DaemonReceiver extends BroadcastReceiver {
    private static final String TAG = "DaemonReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.e(TAG, "onReceive() action: " + intent.getAction());
//            Toast.makeText(context,"设备开机了",Toast.LENGTH_SHORT).show();
        }
        DaemonHolder.startService();
    }
}
