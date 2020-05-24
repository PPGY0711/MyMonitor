package com.appmonitor.helper;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 读取内存帮助类
 */
public class StorageHelper {
    private static String filePathPrefix = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "appmonitor" + File.separator;
    private static Set<String> acceptFileType = new HashSet<String>();
    private static String TAG = "StorageHelper";
    private static MyFileFilter fileFilter = new MyFileFilter();
    public static void initStorage(){
        acceptFileType.add("javaCrash");
        acceptFileType.add("anrError");
        acceptFileType.add("catonError");
        acceptFileType.add("WebViewMonitor_ajax");
        acceptFileType.add("WebViewMonitor_error");
        acceptFileType.add("WebViewMonitor_click");
        acceptFileType.add("WebViewMonitor_resourceTiming");
    }

    public static void uploadLocalMonitorInfoToMongoDB() {
        List<JSONObject> logInfos = new ArrayList<JSONObject>();
        Log.w(TAG, "[1] filePathPrefix: " + filePathPrefix);
        File dir = new File(filePathPrefix);
        Log.w(TAG,"[2] if first dir exists: " + dir.exists());
        if (dir.exists()) {
            String[] dirs = dir.list();
            if (dirs != null && dirs.length > 0) {
                for (String typedir : dirs) {
                    Log.w(TAG,"[3] second dir is: " + typedir);
                    if (acceptFileType.contains(typedir)) {
                        File logFileDir = new File(filePathPrefix + typedir + File.separator);
                        Log.w(TAG,"[4] log file is: " + logFileDir);
                        String[] logs = logFileDir.list(fileFilter);
                        if (logs != null) {
                            for (String logFileName : logs) {
                                Log.w(TAG,"[5] log file is: " + logFileName);
                                String fileName = filePathPrefix + typedir + File.separator + logFileName;
                                Log.i(TAG, "------------------ log file name is: " + fileName);
                                FileInputStream fis;
                                BufferedReader reader;
                                String s, info;
                                try {
                                    fis = new FileInputStream(fileName);
                                    reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                                    while (true) {
                                        s = reader.readLine();
                                        if (s != null &&
                                                (!s.equals("\n") && !s.equals("") &&
                                                        !s.equals("========================= start =========================") &&
                                                        !s.equals("========================= end =========================")))
                                        {
                                            info = s;
                                            logInfos.add(new JSONObject(info));
                                        }
                                        if (s == null)
                                            break;
                                        Log.e(TAG, s);
                                    }
                                    reader.close();
                                    fis.close();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //每次读取后删除该文件
                                File log = new File(fileName);
                                if(log.exists() && log.isFile()){
                                    if(log.delete()){
                                        Log.i(TAG,"delete successfully!");
                                    }
                                    else{
                                        Log.i(TAG,"delete failed!");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        MongoDBHelper.uploadToMongoDB(logInfos);
    }

}

class MyFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".txt");
    }
}