package com.appmonitor.cache;

import android.util.Log;

import com.appmonitor.helper.MongoDBHelper;
import com.appmonitor.helper.RedisHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存队列
 * 上报策略：每累计10条上报到redis，或者每隔2分钟上报到MongoDB，或者App退出时从redis上报到MongoDB中
 * 1.Json形式的数据队列
 * 2.json数据属于的类型队列
 * 3.读取数据库配置类
 */
public class CacheArray {

//    public JSONObject uploadJson;
    private static ArrayList<JSONObject> uploadAnrArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadAjaxArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadErrorArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadClickArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadTimingArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadCrashArray = new ArrayList<JSONObject>();
    private static ArrayList<JSONObject> uploadCatonArray = new ArrayList<JSONObject>();
    private static ArrayList<ArrayList<JSONObject>> cacheArray = new ArrayList<ArrayList<JSONObject>>();
    private static int volume = 10; //缓存队列容量

    public static void initCacheArray(){
        cacheArray.add(uploadAnrArray);
        cacheArray.add(uploadAjaxArray);
        cacheArray.add(uploadCatonArray);
        cacheArray.add(uploadClickArray);
        cacheArray.add(uploadCrashArray);
        cacheArray.add(uploadTimingArray);
        cacheArray.add(uploadErrorArray);
    }

    public static ArrayList<ArrayList<JSONObject>> getCacheArray(){
        return cacheArray;
    }

    public static void addToCacheArray(JSONObject msg){
        try{
            String type = msg.getString("type");
            System.out.println("which one to upload: " + type);
            switch (type){
                case "WebViewMonitor_error":
                    uploadToCertainArray(type, msg, uploadErrorArray);
                    break;
                case "WebViewMonitor_ajax":
                    uploadToCertainArray(type, msg, uploadAjaxArray);
                    break;
                case "WebViewMonitor_click":
                    uploadToCertainArray(type, msg, uploadClickArray);
                    break;
                case "WebViewMonitor_resourceTiming":
                    uploadToCertainArray(type, msg, uploadTimingArray);
                    break;
                case "javaCrash":
                    uploadToCertainArray(type, msg, uploadCrashArray);
                    break;
                case "catonError":
                    uploadToCertainArray(type, msg, uploadCatonArray);
                    break;
                case "anrError":
                    uploadToCertainArray(type, msg, uploadAnrArray);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void uploadToCertainArray(String type, JSONObject jsonObject, ArrayList<JSONObject> list){
        if(list.size() < volume){
            list.add(jsonObject);
            System.out.println("-------------------- add to cacheArray and type is: " + type + ", size: " + list.size());
            System.out.println("-------------------- msg: \n");
            System.out.println(jsonObject.toString());
        }
        else{
            System.out.println("-------------------- upload to redis and type is: " + type);
            //缓存队列上传给redis
            RedisHelper.uploadJsonArray(type, list);
            list.clear();
            list.add(jsonObject);
        }
    }

    public static void upLoadAllToMongoDB(){
        for(ArrayList<JSONObject> arrayList: cacheArray){
            if(arrayList.size()>0){
                try{
                    String type = arrayList.get(0).getString("type");
                    List<String> list = new ArrayList<String>();
                    for(JSONObject jsonObject: arrayList){
                        list.add(jsonObject.toString());
                    }
                    Log.i("CacheArray", "Upload to MongoDB");
                    MongoDBHelper.uploadToMongoDB(type,list);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
