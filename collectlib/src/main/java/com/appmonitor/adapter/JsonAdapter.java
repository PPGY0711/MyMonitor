package com.appmonitor.adapter;

import android.content.Context;

import com.appmonitor.cache.CacheArray;
import com.appmonitor.tools.HardwareTools;
import com.appmonitor.tools.InitApmTools;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * 数据适配层
 * 用于处理传来的Json，加上手机硬件信息传给缓存队列
 */
public class JsonAdapter {
    public JSONObject deviceInfo;
    public JSONObject handleMsg;
    public Context context;

    public static void upLoadToDataAdapter(JSONObject jsonObject){
        new UpLoadThread(jsonObject).run();
    }

    public JsonAdapter(JSONObject handleMsg) {
        this.deviceInfo = new JSONObject();
        this.handleMsg = handleMsg;
        this.context = InitApmTools.getAppContext();
    }

    public JSONObject addDeviceInfo(){
        setDeviceInfo();
        try{
            handleMsg.put("appKey",InitApmTools.getAppKey());
            handleMsg.put("deviceInfo",this.deviceInfo);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            handleMsg.put("reportTime",dateFormat.format(new Date()));
            return handleMsg;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getAllInfo(){
        return addDeviceInfo();
    }

    private void setDeviceInfo(){
        deviceInfo = HardwareTools.getHardwareInfo(context);
    }
}

class UpLoadThread implements Runnable{
    private JSONObject jsonObject;

    public UpLoadThread(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void run() {
        JsonAdapter adapter = new JsonAdapter(jsonObject);
        adapter.addDeviceInfo();
        System.out.println(jsonObject);
        CacheArray.addToCacheArray(jsonObject);
    }
}