package com.appmonitor.caton;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Printer;
import static com.appmonitor.caton.Config.log;


public class UILooperObserver implements Printer {
    private final static String TAG = "UILooperObserver";
    private final static String LOG_BEGIN = ">>>>> Dispatching to";
    private final static String LOG_END = "<<<<< Finished to";
    public final static long ANR_TRIGGER_TIME = 5000L;
    private long mPreMessageTime = 0;
    private long mPreThreadTime = 0;
    private BlockHandler mBlockHandler;

    public UILooperObserver(BlockHandler blockHandler) {
        this.mBlockHandler = blockHandler;
        Looper.getMainLooper().setMessageLogging(this);
    }

    @Override
    public void println(String x) {
        if (x.startsWith(LOG_BEGIN)) {
            mPreMessageTime = SystemClock.elapsedRealtime();
            mPreThreadTime = SystemClock.currentThreadTimeMillis();
        } else if (x.startsWith(LOG_END)) {
            if (mPreMessageTime != 0) {
                long messageElapseTime = SystemClock.elapsedRealtime() - mPreMessageTime;
                long threadElapseTime = SystemClock.currentThreadTimeMillis() - mPreThreadTime;
                if (messageElapseTime > Config.THRESHOLD_TIME) {
                    log(TAG, String.format("messageElapseTime : %s, threadElapseTime : %s"
                            , messageElapseTime, threadElapseTime));
                    mBlockHandler.notifyBlockOccurs(messageElapseTime >= ANR_TRIGGER_TIME
                            , messageElapseTime, threadElapseTime);
                }
            }
        }
    }

}
