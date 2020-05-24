package com.appmonitor.tools;

import com.appmonitor.cache.CacheArray;
import com.appmonitor.helper.RedisHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExitTools {
    public static void upLoadToMongoBeforeExit(){
        System.out.println("\n`````````````````````` ExitTools invoked! ``````````````````````\n");
        //把目前redis当中未上传的key以及cacheArray中的key全部上传到MongoDB
        RedisHelper.upLoadAllToMongoDB();
        CacheArray.upLoadAllToMongoDB();
    }

    public static void upLoadToRedisbeforeExit(){
        RedisHelper.upLoadAllToRedis();
    }

    public static List<JSONObject> allReadyJSONObject(){
        List<JSONObject> res = new ArrayList<JSONObject>();
        for(ArrayList<JSONObject> arrayList: CacheArray.getCacheArray()){
            res.addAll(arrayList);
        }

//        //读redis中的，如果有错，就不在MainThread里面做这个
//        List<JSONObject> redisList = RedisHelper.getAllJSONObjectInRedis();
//        res.addAll(redisList);
        return res;
    }
}
