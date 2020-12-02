package com.appmonitor.crash;

import android.app.ActivityManager;
import android.content.Context;
import com.appmonitor.adapter.JsonAdapter;
import com.appmonitor.tools.DateUtils;
import com.appmonitor.tools.ExitTools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //TAG
    public static final String TAG = "CrashHandler";
    //自定义Toast
    private static Toast mCustomToast;
    //提示文字
    private static String mCrashTip = "很抱歉,程序出现异常,即将退出.";
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler mCrashHandler;
    //程序的App对象
    public Application mApplication;
    //生命周期监听
    MyActivityLifecycleCallbacks mMyActivityLifecycleCallbacks = new MyActivityLifecycleCallbacks();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    //是否是Debug模式
    private boolean mIsDebug = true;
    //是否重启APP
    private boolean mIsRestartApp = false;
    //重启APP时间
    private long mRestartTime;
    //重启后的第一个Activity class文件
    private Class mClassOfFirstActivity;
    //是否已经toast
    private boolean hasToast;
    //用来存储设备信息和异常信息
    private Map<String, String> crashInfo = new HashMap<String, String>();
    //用于分类进行本地持久化
    private Map<String, List<JSONObject>> logArrayMap = new HashMap<String, List<JSONObject>>();
    /**
     * 私有构造函数
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     *
     * @return
     * @since V1.0
     */
    public static CrashHandler getInstance() {
        if (mCrashHandler == null)
            mCrashHandler = new CrashHandler();
        return mCrashHandler;
    }

    public static void setCloseAnimation(int closeAnimation) {
        MyActivityLifecycleCallbacks.sAnimationId = closeAnimation;
    }

    public static void setCustomToast(Toast customToast) {
        mCustomToast = customToast;
    }

    public static void setCrashTip(String crashTip) {
        mCrashTip = crashTip;
    }

    public void init(Application application, boolean isDebug, boolean isRestartApp, long restartTime, Class classOfFirstActivity) {
        mIsRestartApp = isRestartApp;
        mRestartTime = restartTime;
        mClassOfFirstActivity = classOfFirstActivity;
        initCrashHandler(application, isDebug);
        initLogMap();
    }

    public void init(Application application, boolean isDebug) {
        initCrashHandler(application, isDebug);
        initLogMap();
    }

    public void initLogMap(){
        List<JSONObject> anrErrorArray = new ArrayList<JSONObject>();
        List<JSONObject> javaCrashArray = new ArrayList<JSONObject>();
        List<JSONObject> catonErrorArray = new ArrayList<JSONObject>();
        List<JSONObject> webviewErrorArray = new ArrayList<JSONObject>();
        List<JSONObject> webviewAjaxArray = new ArrayList<JSONObject>();
        List<JSONObject> webviewTimingArray = new ArrayList<JSONObject>();
        List<JSONObject> webviewClickArray = new ArrayList<JSONObject>();
        logArrayMap.put("javaCrash",javaCrashArray);
        logArrayMap.put("catonError",catonErrorArray);
        logArrayMap.put("anrError",anrErrorArray);
        logArrayMap.put("WebViewMonitor_error",webviewErrorArray);
        logArrayMap.put("WebViewMonitor_ajax",webviewAjaxArray);
        logArrayMap.put("WebViewMonitor_resourceTiming",webviewTimingArray);
        logArrayMap.put("WebViewMonitor_click",webviewClickArray);
    }

    /**
     * 初始化
     * @since V1.0
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initCrashHandler(Application application, boolean isDebug) {
        mIsDebug = isDebug;
        mApplication = application;
        mApplication.registerActivityLifecycleCallbacks(mMyActivityLifecycleCallbacks);
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean isHandle = handleException(ex);
        if (!isHandle && mDefaultHandler != null) {
            // 如果没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                //给Toast留出时间
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "uncaughtException() InterruptedException:" + e);
            }

            if (mIsRestartApp) {
                //利用系统时钟进行重启任务
                AlarmManager mgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
                try {
                    Intent intent = new Intent(mApplication, mClassOfFirstActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent restartIntent = PendingIntent.getActivity(mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + mRestartTime, restartIntent); // x秒钟后重启应用
                } catch (Exception e) {
                    Log.e(TAG, "first class error:" + e);
                }
            }

            mMyActivityLifecycleCallbacks.removeAllActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            System.gc();

        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (!hasToast) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        Toast toast;
                        if (mCustomToast == null) {
                            toast = Toast.makeText(mApplication, mCrashTip, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                        } else {
                            toast = mCustomToast;
                        }
                        toast.show();
                        Looper.loop();
                        hasToast = true;
                    } catch (Exception e) {
                        Log.e(TAG, "handleException Toast error" + e);
                    }
                }
            }).start();
        }

        if (ex == null) {
            return false;
        }

        if (mIsDebug) {
            // 保存日志文件
            saveMonitorInfo(ex);
        }

        return true;
    }

    private void saveMonitorInfo(Throwable ex){
        List<JSONObject> uploadArray = new ArrayList<JSONObject>();
        JSONObject crashObject = getCrashInfo(ex);
        uploadArray.add(crashObject);
        List<JSONObject> otherList = ExitTools.allReadyJSONObject();
        uploadArray.addAll(otherList);
        System.out.println("%%%%%%%%%%%%%&&&&&&&&&&&&& upLoadArray size is: "+uploadArray.size());
        //对信息进行分类
        sortUploadInfoArray(uploadArray);
        //信息分类写入日志文件
        for(Map.Entry<String, List<JSONObject>> entry: logArrayMap.entrySet()){
            if(entry.getValue().size()>0){
                saveCatchInfo2File(entry.getValue());
            }
        }
    }

    private void sortUploadInfoArray(List<JSONObject> list){
        for(JSONObject jsonObject : list){
            try{
                String key = jsonObject.getString("type");
                System.out.println("@@@@@@@@@@############ sort function: type is: " + key);
                logArrayMap.get(key).add(jsonObject);
//                System.out.println("%%%%%%%%%%%%%%&&&&&&&&&&& list of key" + key + "is null? : " + (tmplist == null));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param jsonObjectList
     * @return 文件名称
     */
    private String saveCatchInfo2File(List<JSONObject> jsonObjectList) {
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

    private String saveInfo2File(StringBuffer sb,String type){
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = type + "-" + time + "-" + timestamp + ".txt";
            System.out.println("fileName :" + fileName + "\n");
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+ "appmonitor" + File.separator + type + File.separator;
                System.out.println("File Path: " + path);
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();
                // 创建新的文件
                if (!dir.exists()) dir.createNewFile();
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                // 答出log日志到控制台
                LogcatCrashInfo(path + fileName);
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save"+type+"Info2File() an error occured while writing file... Exception:");
        }
        return null;
    }

    /**
     * 将捕获的导致崩溃的错误信息保存在sdcard 和输出到LogCat中
     *
     * @param fileName
     * @since V1.0
     */
    private void LogcatCrashInfo(String fileName) {
        if (!new File(fileName).exists()) {
            Log.e(TAG, "LogcatCrashInfo() 日志文件不存在");
            return;
        }
        Log.i(TAG,"log file at: " + fileName);
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            while (true) {
                s = reader.readLine();
                if (s == null)
                    break;
                Log.e(TAG, s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // 关闭流
            try {
                reader.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到程序崩溃的详细信息
     */
    public JSONObject getCrashInfo(Throwable ex) {
        crashInfo.put("reportTime", DateUtils.getFormatTime(new Date()));
        crashInfo.put("type","javaCrash");
        crashInfo.put("crashMessage", ex.getMessage());
//        crashInfo.put("crashLocaleMessage" ,ex.getLocalizedMessage());
        crashInfo.put("PID",android.os.Process.myPid()+"");
        crashInfo.put("Process",getProcessName());
        crashInfo.put("crashStackSeparator","-------------------Trace separator-------------------");
        List<String> crashStackTrace = getDetailedStackTrace(ex);
        JSONObject crashJSObject = new JSONObject(crashInfo);
        try{
            crashJSObject.put("crashStackTrace", crashStackTrace);
            crashJSObject = new JsonAdapter(crashJSObject).addDeviceInfo();
            System.out.println("\n========================= start =========================\n");
            System.out.println("\n &&&&&&&&&&&&%%%%%%%%%%%%%%%%%%% crashInfo &&&&&&&&&&&&&&&&&&&&&&&&&&&%%%%%%%%%%%%%\n");
            System.out.println(crashJSObject.toString());
            System.out.println("\n========================= end =========================\n");
        }catch (Exception e){
            e.getStackTrace();
        }
        return crashJSObject;
    }

    private List<String> getDetailedStackTrace(Throwable ex){
        List<String> stackTrace = new ArrayList<String>();
        for(int i = 0 ; i < ex.getStackTrace().length ;i++){
            if(i==0)
                stackTrace.add("-------------------Trace separator-------------------");
            stackTrace.add(ex.getStackTrace()[i].toString());
        }
        while(ex.getCause()!=null){
            ex=ex.getCause();
            for(int i=0;i<ex.getStackTrace().length;i++) {
                if (i == 0)
                    stackTrace.add("-------------------Trace separator-------------------");
                stackTrace.add(ex.getStackTrace()[i].toString());
            }
        }
        for(int i =0; i>stackTrace.size();i++)
            System.out.println(stackTrace.get(i));
        return stackTrace;
    }

    public String getProcessName() {
        ActivityManager activityManager = (ActivityManager) mApplication.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = activityManager.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return "unknown";
    }
}
