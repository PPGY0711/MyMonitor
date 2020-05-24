package com.appmonitor.helper;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MongoDB操作帮助类
 */
public class MongoDBHelper {
    private static String host = "119.45.118.29";
    private static int port = 27017;
    private static String dbName = "appmonitor";
    private static String userName = "test_rw";
    private static String password = "appmonitor";
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static void initMongoDB(){
        try{
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = new ServerAddress(host, port);
            System.out.println("================= serverAddress: ========================" + serverAddress.toString());
            //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(userName, dbName, password.toCharArray());
            System.out.println("================= mongoCredential: ======================" + mongoCredential.toString());
            //通过连接认证获取MongoDB连接
            mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
            //连接到数据库
            mongoDatabase = mongoClient.getDatabase(dbName);

            System.out.println("================= MongoDB is successfully link, and client is: " + mongoClient +", database is: " + mongoDatabase);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void uploadToMongoDB(String type, List<String> jsonObjectList){
        System.out.println("upload to mongo and type is: " + type);
        String collectionName;
        if(type.contains("WebViewMonitor_")){
            collectionName = type.replace("WebViewMonitor_","")+"Info";
        }
        else{
            collectionName = type + "Info";
        }
        System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&& CollectionName: " + collectionName +"\n");
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        try{
            List<Document> documents = new ArrayList<Document>();
            for(int i= 0; i < jsonObjectList.size(); i++){
                JSONObject jsonObject = new JSONObject(jsonObjectList.get(i));
                Document document = Document.parse(jsonObject.toString());
                documents.add(document);
            }
            collection.insertMany(documents);
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public static void uploadToMongoDB(List<JSONObject> jsonObjectList){
        if(jsonObjectList.size()>0){
            try{
                for(int i= 0; i < jsonObjectList.size(); i++){
                    String collectionName;
                    String type = jsonObjectList.get(i).getString("type");
                    if(type.contains("WebViewMonitor_")){
                        collectionName = type.replace("WebViewMonitor_","")+"Info";
                    }
                    else{
                        collectionName = type + "Info";
                    }
                    System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&& CollectionName: " + collectionName +"\n");
                    MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
                    Document document = Document.parse(jsonObjectList.get(i).toString());
                    collection.insertOne(document);
                }
            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            }
        }
    }
}
