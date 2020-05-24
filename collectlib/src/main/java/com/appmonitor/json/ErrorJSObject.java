package com.appmonitor.json;

import android.webkit.JavascriptInterface;
import com.appmonitor.adapter.JsonAdapter;
import org.json.JSONObject;

public class ErrorJSObject {
    @JavascriptInterface
    public void sendErrorInfo(String jsonInfo){
        handleErrorInfo(jsonInfo);
    }

    public void handleErrorInfo(String jsonInfo){
        System.out.println("========================== come in handleErrorInfo================");
        System.out.println(jsonInfo);
        System.out.println("========================== finish handleErrorInfo================");
        //1.解析json, 2.计算性能指标
        JSONObject jsonObject = parseErrorJSONObject(jsonInfo);
        System.out.println(jsonObject);
        //3.上报到缓存队列
        jsonObject = new JsonAdapter(jsonObject).addDeviceInfo();
        System.out.println(jsonObject);
        JsonAdapter.upLoadToDataAdapter(jsonObject);
    }

    private JSONObject parseErrorJSONObject(String jsonInfo){
        try{
            return new JSONObject(jsonInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}