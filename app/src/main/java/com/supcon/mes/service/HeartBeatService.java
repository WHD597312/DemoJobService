package com.supcon.mes.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.supcon.mes.daemonservice.AbsHeartBeatService;

public class HeartBeatService extends AbsHeartBeatService {

    private final static String TAG="HeartBeatService" ;
    @Override
    public void onStartService() {
        Log.i(TAG,"-->:onStartService");
    }

    @Override
    public void onStopService() {
        Log.i(TAG,"-->onStopService");
    }

    @Override
    public long getDelayExecutedMillis() {
        return 0;
    }

    @Override
    public long getHeartBeatMillis() {
        return 30*1000;
    }

    @Override
    public void onHeartBeat() {
        Log.i(TAG,"-->onHeartBeat");
    }
}
