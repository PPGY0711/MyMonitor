package com.appmonitor.json;

import android.webkit.JavascriptInterface;
import com.appmonitor.adapter.JsonAdapter;
import org.json.JSONObject;

public class AjaxJSObject {
    @JavascriptInterface
    public void sendAjaxInfo(String jsonInfo){
        handleAjaxInfo(jsonInfo);
    }

    public void handleAjaxInfo(String jsonInfo){
        System.out.println("========================== come in handleAjaxInfo================");
        System.out.println(jsonInfo);
        System.out.println("========================== finish handleAjaxInfo================");
        //1.解析json, 2.计算性能指标
        JSONObject jsonObject = parseAjaxJSONObject(jsonInfo);
        System.out.println(jsonObject);
        //3.上报到缓存队列
        JsonAdapter.upLoadToDataAdapter(jsonObject);
    }

    private JSONObject parseAjaxJSONObject(String jsonInfo){
        try{
//            JSONArray jsonArray = new JSONArray(jsonInfo);
            JSONObject jsonObject = new JSONObject(jsonInfo);
            JSONObject payload = jsonObject.getJSONObject("payload");
            JSONObject performanceCounting = new JSONObject();
            long res_time = payload.getLong("res_time");
            long req_time = payload.getLong("req_time");
            long cb_end_time = payload.getLong("cb_end_time");
            long cb_start_time = payload.getLong("cb_start_time");
            long firstbyte_time = payload.getLong("firstbyte_time");
            long lastbyte_time = payload.getLong("lastbyte_time");
            //终端用户响应时间
            performanceCounting.put("responseTime", res_time-req_time);
            //响应数据下载时间
            performanceCounting.put("downloadTime",lastbyte_time-firstbyte_time);
            //回调执行时间
            performanceCounting.put("callbackTime",cb_end_time-cb_start_time);
            jsonObject.put("performanceCounting",performanceCounting);
            return  jsonObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}