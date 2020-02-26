package com.supcon.mes;

import android.app.Application;
import android.util.Log;

import com.supcon.mes.daemonservice.DaemonHolder;
import com.supcon.mes.daemonservice.DaemonUtil;
import com.supcon.mes.service.HeartBeatService;

public class App extends Application {

    private String TAG="DemoJobService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"-->"+DaemonUtil.getProcessName(this));
        DaemonHolder.init(this,HeartBeatService.class);
    }
}
