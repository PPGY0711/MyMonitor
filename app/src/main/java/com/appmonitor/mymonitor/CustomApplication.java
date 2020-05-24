package com.appmonitor.mymonitor;

import android.app.Application;

import com.appmonitor.caton.Caton;
import com.appmonitor.tools.InitApmTools;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        InitApmTools.initMontiorTools(getApplicationContext(),"AHUmGXC9", this).start();
        //可以接收该函数返回的builder，用于自定义卡顿检测的一些时间参数
        Caton.Builder builder = InitApmTools.registerCatonMonitor(getApplicationContext(),Caton.MonitorMode.LOOPER);
    }
}
