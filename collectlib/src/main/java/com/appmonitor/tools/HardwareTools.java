package com.appmonitor.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class HardwareTools {

    public static JSONObject getHardwareInfo(Context context){
        JSONObject deviceInfo = null;
        try{
            DeviceInfoUtils.setDeviceInfoUtilsContext(context);
            Map<String, String> appInfo = DeviceInfoUtils.getDeviceAllInfo(context);
            deviceInfo = new JSONObject(appInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return deviceInfo;
    }
}

class DeviceInfoUtils {
    private static UUID uuid;

    private static Context context;

    protected static void setDeviceInfoUtilsContext(Context appContext) {
        context = appContext;
        uuid = new DeviceUuidTools(appContext).getDeviceUuid();
        SDCardUtils.setSDCardUtilsContext(appContext);
    }

    /**
     * 获取设备宽度（px）
     *
     */
    private static Integer getDeviceWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     */
    private static Integer getDeviceHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取当前手机系统语言。
     */
    private static String getDeviceDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取SDKVersion
     * @return
     */
    private static String getSdkVersion(){
        return Integer.valueOf(android.os.Build.VERSION.SDK_INT).toString();
    }

    /**
     * 获取SDKVersion
     * @return
     */
    private static String getsecurityPatch(){
        return android.os.Build.VERSION.SECURITY_PATCH;
    }

    /**
     * 获取BuileType
     * @return
     */
    private static String getbuildType(){
        return Build.TYPE;
    }

    /**
     * 设备硬件及状态信息收集函数
     * @param context
     * @return
     */
    protected static Map<String, String> getDeviceAllInfo(Context context) {
        //获取硬件信息
        Map<String, String> deviceInfo = new HashMap<String, String>();
        deviceInfo.put("deviceID",uuid.toString());
        deviceInfo.put("deviceWidth", getDeviceWidth().toString());
        deviceInfo.put("deviceHeight", getDeviceHeight().toString());
        deviceInfo.put("defaultLanguage", getDeviceDefaultLanguage());
        deviceInfo.put("serialNum", android.os.Build.SERIAL);
        deviceInfo.put("manufacturer", android.os.Build.MANUFACTURER);
        deviceInfo.put("model", android.os.Build.MODEL);
        deviceInfo.put("fingerprint", android.os.Build.FINGERPRINT);
        deviceInfo.put("version",   android.os.Build.VERSION.RELEASE);
        deviceInfo.put("sdkVersion", getSdkVersion());
        deviceInfo.put("securityPatch", getsecurityPatch());
        deviceInfo.put("buildType", getbuildType());
        deviceInfo.put("userName", android.os.Build.USER);
        deviceInfo.put("productName", android.os.Build.PRODUCT);
        deviceInfo.put("ID",   android.os.Build.ID);
        deviceInfo.put("display", android.os.Build.DISPLAY);
        deviceInfo.put("hardware", android.os.Build.HARDWARE);
        deviceInfo.put("device", android.os.Build.DEVICE);
        deviceInfo.put("bootLoader", android.os.Build.BOOTLOADER);
        deviceInfo.put("board",   android.os.Build.BOARD);
        deviceInfo.put("codeName", android.os.Build.VERSION.CODENAME);
		deviceInfo.put("CPU_ABI",android.os.Build.CPU_ABI);
        //获取内存状态信息
        deviceInfo.put("isSDCardMount", SDCardUtils.isSDCardMount().toString());
        deviceInfo.put("RAM", SDCardUtils.getRAMInfo());
        deviceInfo.put("externalMemory", SDCardUtils.getTotalExternalMemorySize());
        deviceInfo.put("externalAvailableMemory", SDCardUtils.getAvailableExternalMemorySize());
        deviceInfo.put("internalMemory", SDCardUtils.getTotalInternalMemorySize());
        deviceInfo.put("internalAvailableMemory", SDCardUtils.getAvailableInternalMemorySize());
        return deviceInfo;
    }
}

class SDCardUtils {
    private static Context context;
    private static final int INTERNAL_STORAGE = 0;
    private static final int EXTERNAL_STORAGE = 1;

    protected static void setSDCardUtilsContext(Context appContext){
        context = appContext;
    }
    /**
     * 获取 手机 RAM 信息
     * */
    public static String getRAMInfo() {
        long totalSize;
        long availableSize;

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);

        MemoryInfo memoryInfo = new MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        totalSize = memoryInfo.totalMem;
        availableSize = memoryInfo.availMem;

        return "Available/Total：" + Formatter.formatFileSize(context, availableSize)
                + "/" + Formatter.formatFileSize(context, totalSize);
    }

    /**
     * 判断SD是否挂载
     */
    public static Boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取 手机 RAM 信息 方法 一
     * */
    public static String getTotalRAM() {
        long size;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        size = outInfo.totalMem;

        return Formatter.formatFileSize(context, size);
    }

    /**
     * 手机 RAM 信息 方法 二
     * */
    public static String getTotalRAMOther() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {

            totalRam = (int) Math.ceil((Float.valueOf(Float.valueOf(firstLine)
                    / (1024 * 1024)).doubleValue()));

            long totalBytes = 0;

        }

        return Formatter.formatFileSize(context, totalRam);
    }

    /**
     * 获取 手机 可用 RAM
     * */
    public static String getAvailableRAM() {
        long size;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        size = outInfo.availMem;

        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取手机内部存储空间
     *
     * @return 以M,G为单位的容量
     */
    public static String getTotalInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        return Formatter.formatFileSize(context, size);
    }

    /**
     * 获取手机内部可用存储空间
     *
     * @return 以M,G为单位的容量
     */
    public static String getAvailableInternalMemorySize() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }

    /**
     * 获取手机外部存储空间
     *
     * @return 以M,G为单位的容量
     */
    public static String getTotalExternalMemorySize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        return Formatter
                .formatFileSize(context, blockCountLong * blockSizeLong);
    }

    /**
     * 获取手机外部可用存储空间
     *
     * @return 以M,G为单位的容量
     */
    public static String getAvailableExternalMemorySize() {
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        return Formatter.formatFileSize(context, availableBlocksLong
                * blockSizeLong);
    }


}
