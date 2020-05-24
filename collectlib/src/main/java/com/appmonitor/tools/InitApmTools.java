package com.appmonitor.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.appmonitor.adapter.JsonAdapter;
import com.appmonitor.cache.CacheArray;
import com.appmonitor.caton.Caton;
import com.appmonitor.threads.InitThread;
import com.appmonitor.threads.PermissionThread;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.appmonitor.caton.Caton.DEFAULT_COLLECT_INTERVAL;
import static com.appmonitor.caton.Caton.DEFAULT_THRESHOLD_TIME;

public class InitApmTools {

    private static Context appContext;
    private static String appKey;
    private static InitThread initThread = new InitThread();
    private static Application application;
    private static PermissionThread permissionThread;
    private static Caton.MonitorMode mode = Caton.MonitorMode.LOOPER;
    private static boolean loggingEnabled = true;
    private long thresholdTime = DEFAULT_THRESHOLD_TIME;
    private long collectInterval = DEFAULT_COLLECT_INTERVAL;

    public static InitThread initMontiorTools(Context context, String key, Application app) {
        appContext = context;
        appKey = key;
        application = app;
        return initThread;
    }

    public static PermissionThread initPermission(Activity activity){
        permissionThread = new PermissionThread(activity);
        return permissionThread;
    }

    public static Context getAppContext(){
        return appContext;
    }

    public static String getAppKey(){
        return appKey;
    }

    public static Application getApplication(){
        return application;
    }

    public static Caton.Builder registerCatonMonitor(Context context, Caton.MonitorMode mode){
        Caton.Builder builder = new Caton.Builder(context)
                .monitorMode(mode)//默认监测模式为Caton.MonitorMode.LOOPER，这样指定Caton.MonitorMode.FRAME
                .loggingEnabled(true)// 是否打印log
                .callback(new Caton.Callback() { //设置触发卡顿时回调
                    @Override
                    public void onBlockOccurs(String[] stackTraces, String anr, long... blockArgs) {
                        // stackTraces : 收集到的堆栈，以便分析卡顿原因。 anr : 如果应用发生ANR，这个就是ANR相关信息，没发生ANR，则为空。
                        //采用Caton.MonitorMode.FRAME模式监测时，blockArgs的size为1，blockArgs[0] 即是发生掉帧的数。
                        //采用Caton.MonitorMode.LOOPER模式监测时，blockArgs的size为2，blockArgs[0] 为UI线程卡顿时间值，blockArgs[1]为在此期间UI线程能执行到的时间。
                        JSONObject catonObject = new JSONObject();
                        catonObject = new JsonAdapter(catonObject).getAllInfo();
                        try{
							String totalInfo = "";
							List<String> anrStackTraces = new ArrayList<String>();
							for(int i = 0; i < stackTraces.length; i++){
								totalInfo += stackTraces[i] + "\n";
							}
							String[] realStackTraces = totalInfo.split("\n");
							for(int i = 0; i <realStackTraces.length; i++){
								if(!realStackTraces[i].equals("\n")){
									if(realStackTraces[i].indexOf('\t')!=-1){
										realStackTraces[i] = realStackTraces[i].substring(realStackTraces[i].indexOf('\t')+1);
									}
									anrStackTraces.add(realStackTraces[i]);
								}
							}
							catonObject.put("stackTraces",anrStackTraces);
                            if(anr != null && anr!=""){
								catonObject.put("anrInfo",anr);
                                catonObject.put("type","anrError");
                            }
                            else{
                                catonObject.put("type","catonError");
                            }
                            if(blockArgs.length==1){
                                catonObject.put("monitorModel","FRAME");
                                catonObject.put("skippedFrames",blockArgs[0]);
								catonObject.put("uiCatonTime",0);
                                catonObject.put("uiRunTime",0);
                            }
                            else{
                                catonObject.put("monitorModel","LOOPER");
                                catonObject.put("skippedFrames",0);
                                catonObject.put("uiCatonTime",blockArgs[0]);
                                catonObject.put("uiRunTime",blockArgs[1]);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        System.out.println("\n----------------- Caton Error Happened! ------------------\n");
                        CacheArray.addToCacheArray(catonObject);
                    }
                });
        Caton.initialize(builder);
        return builder;
    }

}
