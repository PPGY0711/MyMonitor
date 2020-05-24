package com.appmonitor.json;

import android.webkit.JavascriptInterface;
import com.appmonitor.adapter.JsonAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;


public class LoadingJSObject {
    @JavascriptInterface
    public void sendLoadingInfo(String jsonInfo){
        handleLoadingInfo(jsonInfo);
    }

    public void handleLoadingInfo(String jsonInfo){
        System.out.println("========================== come in handleLoadingInfo================");
        System.out.println(jsonInfo);
        System.out.println("========================== finish handleLoadingInfo================");
        //1.解析json, 2.计算性能指标
        JSONObject jsonObject = parseLoadingJSONObject(jsonInfo);
        System.out.println(jsonObject);
        //3.上报到缓存队列
        JsonAdapter.upLoadToDataAdapter(jsonObject);
    }

    public JSONObject parseLoadingJSONObject(String jsonInfo){
        try {
            JSONObject jsonObject = new JSONObject(jsonInfo);
            jsonObject.put("reportTime", new SimpleDateFormat().format("yyyy-MM-dd HH:mm:ss"));
            JSONObject payload = jsonObject.getJSONObject("payload");
            JSONObject navigationTiming = payload.getJSONObject("navigationTiming");
            JSONObject performanceCounting = new JSONObject();
            if(navigationTiming.length()!=0){
                performanceCounting = parseNavigationTiming(navigationTiming);
            }
            jsonObject.put("performanceCounting",performanceCounting);

            JSONArray resourceTiming = payload.getJSONArray("resourceTiming");
            JSONArray resourceCounting = new JSONArray();
            if(resourceTiming.length()!=0){
                resourceCounting = parseResourceTiming(resourceTiming);
            }
            jsonObject.put("resourceCounting",resourceCounting);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject parseNavigationTiming(JSONObject navigationTiming){
        JSONObject jsonObject = new JSONObject();
        try{
            long navigationStart = navigationTiming.getLong("navigationStart");
            long redirectStart = navigationTiming.getLong("redirectStart");
            long redirectEnd = navigationTiming.getLong("redirectEnd");
            long fetchStart = navigationTiming.getLong("fetchStart");
            long domainLookupStart = navigationTiming.getLong("domainLookupStart");
            long domainLookupEnd = navigationTiming.getLong("domainLookupEnd");
            long connectStart = navigationTiming.getLong("connectStart");
            long secureConnectionStart = navigationTiming.getLong("secureConnectionStart");
            long connectEnd = navigationTiming.getLong("connectEnd");
            long requestStart = navigationTiming.getLong("requestStart");
            long responseStart = navigationTiming.getLong("responseStart");
            long responseEnd = navigationTiming.getLong("responseEnd");
            long unloadEventStart = navigationTiming.getLong("unloadEventStart");
            long unloadEventEnd = navigationTiming.getLong("unloadEventEnd");
            long domLoading = navigationTiming.getLong("domLoading");
            long domInteractive = navigationTiming.getLong("domInteractive");
            long domContentLoadedEventStart = navigationTiming.getLong("domContentLoadedEventStart");
            long domContentLoadedEventEnd = navigationTiming.getLong("domContentLoadedEventEnd");
            long domComplete = navigationTiming.getLong("domComplete");
            long loadEventStart = navigationTiming.getLong("loadEventStart");
            long loadEventEnd = navigationTiming.getLong("loadEventEnd");
            long pageTime = navigationTiming.getLong("pageTime");
            //准备新页面耗时
            jsonObject.put("readyStart",fetchStart-navigationStart);
            //redirect重定向耗时
            jsonObject.put("redirectTime",redirectEnd-redirectStart);
            //dns查询耗时
            jsonObject.put("dnsTime",domainLookupEnd-domainLookupStart);
            //TTFB 读取页面第一个字节的时间
            jsonObject.put("ttfbTime",responseStart-navigationStart);
            //Appcache耗时
            jsonObject.put("appcacheTime",domainLookupStart-fetchStart);
            //unload前文档耗时
            jsonObject.put("unloadTime",unloadEventEnd-unloadEventStart);
            //TCP连接耗时
            jsonObject.put("tcpTime",connectEnd-connectStart);
            //request请求耗时
            jsonObject.put("requestTime",responseStart-requestStart);
            //reponse耗时
            jsonObject.put("responseTime",responseEnd-responseStart);
            //解析dom树耗时
            jsonObject.put("analysisTime",domComplete-domInteractive);
            //白屏时间
            jsonObject.put("blankTime",domInteractive-fetchStart);
            //domready耗时
            jsonObject.put("domReadyTime",domContentLoadedEventEnd-fetchStart);
            //load事件耗时
            jsonObject.put("loadEventTime",loadEventEnd-loadEventStart);
            //加载时间耗时
            jsonObject.put("loadTime",loadEventEnd-navigationStart);
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONArray parseResourceTiming(JSONArray resourceTiming){
        JSONArray jsonArray = new JSONArray();
        try{
            for(int j = 0; j < resourceTiming.length(); j++){
                JSONObject resourceObject = resourceTiming.getJSONObject(j);
                JSONObject jsonObject = new JSONObject();
                double connectEnd = resourceObject.getDouble("connectEnd");
                double connectStart = resourceObject.getDouble("connectStart");
                double domainLookupEnd = resourceObject.getDouble("domainLookupEnd");
                double domainLookupStart = resourceObject.getDouble("domainLookupStart");
                double duration = resourceObject.getDouble("duration");
                String entryType = resourceObject.getString("entryType");
                double fetchStart = resourceObject.getDouble("fetchStart");
                String initiatorType = resourceObject.getString("initiatorType");
                String name = resourceObject.getString("name");
                double redirectEnd = resourceObject.getDouble("redirectEnd");
                double redirectStart = resourceObject.getDouble("redirectStart");
                double requestStart = resourceObject.getDouble("requestStart");
                double responseEnd = resourceObject.getDouble("responseEnd");
                double responseStart = resourceObject.getDouble("responseStart");
//                double secureConnectionStart = resourceObject.getDouble("secureConncectionStart");
                double startTime = resourceObject.getDouble("startTime");
                jsonObject.put("redirectTime", redirectEnd-redirectStart);
                jsonObject.put("appcacheTime",domainLookupStart-fetchStart);
                jsonObject.put("dnsTime",domainLookupEnd-domainLookupStart);
                jsonObject.put("tcpTime",connectEnd-connectStart);
                jsonObject.put("requestTime",responseStart-requestStart);
                jsonObject.put("responseTime",responseEnd-responseStart);
                jsonObject.put("duration",duration);
                jsonObject.put("entryType",entryType);
                jsonObject.put("initiatorType",initiatorType);
                jsonObject.put("name",name);
                jsonArray.put(jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }
}
