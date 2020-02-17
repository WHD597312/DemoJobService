package com.supcon.mes.daemonservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.supcon.mes.SupconAidl;

public class DaemonService extends Service{
    private static final String TAG = "DaemonService";
    //    private DaemonReceiver screenBroadcastReceiver = new DaemonReceiver();
    private ScreenBroadcastReceiver screenBroadcastReceiver = new ScreenBroadcastReceiver();

    private boolean bind=false;
    private void startBindService() {
        try {
            if (DaemonHolder.mService != null) {
                startService(new Intent(this, DaemonHolder.mService));
                bind=bindService(new Intent(this, DaemonHolder.mService), serviceConnection, Context.BIND_IMPORTANT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final SupconAidl aidl = new SupconAidl.Stub() {
        @Override
        public void startService() throws RemoteException {
            Log.d(TAG, "aidl startService()");
        }

        @Override
        public void stopService() throws RemoteException {
            Log.e(TAG, "aidl stopService()");
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() 已绑定");
            try {
                service.linkToDeath(() -> {
                    Log.e(TAG, "onServiceConnected() linkToDeath");
                    try {
                        aidl.startService();
                        startBindService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected() 已解绑");
            try {
                aidl.stopService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            onServiceDisconnected(name);
        }
    };

    DaemonReceiver daemonReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        startBindService();
        listenNetworkConnectivity();
//        registerDaemonReceiver();
        screenBroadcastReceiver.registerScreenBroadcastReceiver(this);
    }

    public class DaemonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                DaemonHolder.startService();
            }
        }
    }
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private boolean isRegistered = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Log.e(TAG, "onReceive() action: " + intent.getAction());
            }
            DaemonHolder.startService();
        }

        public void registerScreenBroadcastReceiver(Context context) {
            if (!isRegistered) {
                isRegistered = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_ON); // 开屏
                filter.addAction(Intent.ACTION_SCREEN_OFF); // 锁屏
                filter.addAction(Intent.ACTION_USER_PRESENT); // 解锁
                filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); // Home键
                context.registerReceiver(ScreenBroadcastReceiver.this, filter);
            }
        }

        public void unregisterScreenBroadcastReceiver(Context context) {
            if (isRegistered) {
                isRegistered = false;
                context.unregisterReceiver(ScreenBroadcastReceiver.this);
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
//        return null;
        return (IBinder) aidl;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        try {
            if (bind){
                unbindService(serviceConnection);
            }
            DaemonHolder.restartService(getApplicationContext(), getClass());
            screenBroadcastReceiver.unregisterScreenBroadcastReceiver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (daemonReceiver!=null){
//            unregisterReceiver(daemonReceiver);
//        }
    }

    private void listenNetworkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        Log.d(TAG, "onAvailable()");
                        DaemonHolder.startService();
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        Log.d(TAG, "onUnavailable()");
                        DaemonHolder.startService();
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        Log.d(TAG, "onLost()");
                        DaemonHolder.startService();
                    }
                });
            }
        }
    }
}
