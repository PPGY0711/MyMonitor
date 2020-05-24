package com.appmonitor.caton;

import android.util.Log;

public class Config {
    public static long THRESHOLD_TIME = 0;
    public static boolean LOG_ENABLED = true;

    public static void log(String tag, String msg) {
        if (LOG_ENABLED) {
            Log.e(tag, msg);
        }
    }
}
