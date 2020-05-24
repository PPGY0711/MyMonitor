package com.appmonitor.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.appmonitor.adapter.JsonAdapter;
import com.appmonitor.cache.CacheArray;
import com.appmonitor.caton.Caton;
import com.appmonitor.threads.InitThread;
import com.appmonitor.threads.PermissionThread;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.appmonitor.caton.Caton.DEFAULT_COLLECT_INTERVAL;
import static com.appmonitor.caton.Caton.DEFAULT_THRESHOLD_TIME;
import static com.appmonitor.crash.CrashHandler.TAG;

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
                .collectInterval(1000) //监测采集堆栈时间间隔
                .thresholdTime(2000) // 触发卡顿时间阈值
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
							System.out.println(" ======================= ANRINFO OF CRASH: ============== ");
							System.out.println(anr);
                            System.out.println(" ======================= ANRINFO OF CRASH: ============== ");
                            if(!anr.equals("")){
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
                        ArrayList<JSONObject> list = new ArrayList<>();
                        list.add(catonObject);
                        saveCatchInfo2File(list);
                        //为了防止程序卡顿异常直接退出，这里直接写到log文件当中，下一次启动后再重传
                    }
                });
        Caton.initialize(builder);
        return builder;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param jsonObjectList
     * @return 文件名称
     */
    private static String saveCatchInfo2File(List<JSONObject> jsonObjectList) {
        StringBuffer sb = new StringBuffer();
        String type = "unknown";
        try {
            type = jsonObjectList.get(0).getString("type");
        }catch (Exception e){
            e.printStackTrace();
        }
        for(JSONObject jsonObject: jsonObjectList){
            Log.i(TAG,"---------------------write record--------------------" );
            sb.append("\n========================= start =========================\n");
            sb.append(jsonObject);
            sb.append("\n========================= end =========================\n");
        }
        return saveInfo2File(sb,type);
    }

    private static String saveInfo2File(StringBuffer sb,String type){
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = type + "-" + time + "-" + timestamp + ".txt";
            System.out.println("fileName :" + fileName + "\n");
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "appmonitor" + File.separator + type + File.separator;
                System.out.println("File Path: " + path);
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();
                // 创建新的文件
                if (!dir.exists()) dir.createNewFile();
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save"+type+"Info2File() an error occured while writing file... Exception:");
        }
        return null;
    }
}
