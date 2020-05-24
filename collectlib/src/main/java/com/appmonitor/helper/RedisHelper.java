package com.appmonitor.helper;

import com.appmonitor.cache.CacheArray;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * Redis操作帮助类
 */
public class RedisHelper {
    private static String host = "119.45.118.29";
    private static int port = 6379;
    private static Jedis jedis;
    private static int expire = 120;
    private static int arrayNum = 0;

    public static void initRedisLink(){
        jedis = new Jedis(host,port);
        System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&& redis is link: " + jedis + "\n");
        startResendThread();
        startCheckThread();
    }

    public static void uploadJsonArray(String type, ArrayList<JSONObject> list){
        System.out.println("upload to redis and type is: " + type);
        for(int i = 0; i < list.size(); i++){
            System.out.println("["+i+"]: "+ list.get(i).toString());
            jedis.rpush(type+"_"+arrayNum,list.get(i).toString());
        }

        System.out.println("num of type " + type + "is: " + arrayNum);
        arrayNum++;
    }

    private static void startResendThread(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                System.out.println("\n &&&&&&&&&&&&&&& resend lastTime info \n ");
                RedisHelper.upLoadAllToMongoDB();
                StorageHelper.uploadLocalMonitorInfoToMongoDB();
                super.run();
            }
        };
        thread.run();
    }

    private static void startCheckThread(){
        //开启后台检查进程
        Thread thread = new Thread(){
            @Override
            public void run() {
                new CheckThread(jedis, true).run();
            }
        };
        thread.run();
    }

    protected static void upLoadAllToMongoDB(Jedis jedis){
        Set<String> keys = jedis.keys("*");
        if(keys.size() > 0){
            for(String key: keys){
                try{
                    if(jedis.type(key).equals("list")){
                        if(jedis.llen(key) > 0) {
                            List<String> list = jedis.lrange(key, 0, -1);
                            String type = key.substring(0,key.lastIndexOf("_"));
                            MongoDBHelper.uploadToMongoDB(type,list);
                            System.out.println("=================== upload to Mongo From redis and type is : " + type);
                            jedis.del(key);
                        }
                        else
                            jedis.del(key);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void upLoadAllToMongoDB(){
        upLoadAllToMongoDB(jedis);
    }

    public static void upLoadAllToRedis(){
        //另外将存有数据的cacheArray中的数据传到redis
        ArrayList<ArrayList<JSONObject>> cacheArray = CacheArray.getCacheArray();
        for(ArrayList<JSONObject> arrayList: cacheArray){
            if(arrayList.size()>0){
                try{
                    RedisHelper.uploadJsonArray(arrayList.get(0).getString("type"),arrayList);
                    arrayList.clear();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<JSONObject> getAllJSONObjectInRedis(){
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        Set<String> keys = jedis.keys("*");
        if(keys.size() > 0){
            for(String key: keys){
                try{
                    if(jedis.type(key).equals("list")){
                        if(jedis.llen(key) > 0) {
                            List<String> list = jedis.lrange(key, 0, -1);
                            for(String str: list){
                                jsonList.add(new JSONObject(str));
                            }
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return  jsonList;
    }
}

class CheckThread implements Runnable{
    private Jedis jedis;
    private boolean running;

    public CheckThread(Jedis jedis, boolean running) {
        this.jedis = jedis;
        this.running = running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        //每两分钟检查一次，如果有长度为10的list，就取出来传到MongoDB中去
        while(running){
            try{
                System.out.println("\n************************** Keys in redis will be upload after two minutes. ************************* \n");
                Thread.sleep(120000);
            }catch (Exception e){
                e.printStackTrace();
            }
            RedisHelper.upLoadAllToMongoDB(jedis);
            //另外将存有数据的cacheArray中的数据传到redis
            RedisHelper.upLoadAllToRedis();
        }
    }
}

