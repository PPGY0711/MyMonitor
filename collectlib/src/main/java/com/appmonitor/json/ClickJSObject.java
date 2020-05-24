package com.appmonitor.json;

import android.webkit.JavascriptInterface;
import com.appmonitor.adapter.JsonAdapter;
import org.json.JSONObject;

public class ClickJSObject {
    @JavascriptInterface
    public void sendClickInfo(String jsonInfo){
        handleClickInfo(jsonInfo);
    }

    public void handleClickInfo(String jsonInfo){
        System.out.println("========================== come in handleClickInfo================");
        System.out.println(jsonInfo);
        System.out.println("========================== finish handleClickInfo================");
        //1.解析json, 2.计算性能指标
        JSONObject jsonObject = parseClickJSONObject(jsonInfo);
        System.out.println(jsonObject);
        //3.上报到缓存队列
        JsonAdapter.upLoadToDataAdapter(jsonObject);
    }

    private JSONObject parseClickJSONObject(String jsonInfo){
        try{
            return new JSONObject(jsonInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}