package com.appmonitor.threads;

import com.appmonitor.adapter.JsonAdapter;
import com.appmonitor.caton.Caton;
import com.appmonitor.crash.CrashHandler;
import com.appmonitor.cache.CacheArray;
import com.appmonitor.helper.MongoDBHelper;
import com.appmonitor.helper.RedisHelper;
import com.appmonitor.helper.StorageHelper;
import com.appmonitor.tools.InitApmTools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InitThread extends Thread{

    @Override
    public void run() {
        System.out.println("\n *************** before init Monitor Agent *************** \n");
        StorageHelper.initStorage();
        new CrashThread().run();
        new DbThread().run();
    }

}

class DbThread implements Runnable{
    @Override
    public void run() {
        System.out.println("\n *************** before init the database *****************\n");
        CacheArray.initCacheArray();
        MongoDBHelper.initMongoDB();
        RedisHelper.initRedisLink();
    }
}

class CrashThread implements Runnable{

    @Override
    public void run() {
        System.out.println("before initCrashHandler");
        CrashHandler.getInstance().init(InitApmTools.getApplication(), true);
        CrashHandler.setCloseAnimation(android.R.anim.fade_out);
        System.out.println("after initCrashHandler");
    }

}
