
package com.fred.trafficlightsfillin.utils;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 异步任务
 */
public class BackgroundThread extends HandlerThread {

    private static BackgroundThread sInstance;
    private static Handler sHandler;

    public BackgroundThread() {
        super("BackgroundThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    private static void ensureThreadLocked() {
        if (sInstance == null) {
            sInstance = new BackgroundThread();
            sInstance.start();
            sHandler = new Handler(sInstance.getLooper());
        }
    }

    public static BackgroundThread get() {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            return sInstance;
        }
    }

    public static Handler getHandler() {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            return sHandler;
        }
    }

    public static void post(final Runnable runnable) {
        synchronized (BackgroundThread.class) {
            ensureThreadLocked();
            sHandler.post(runnable);
        }
    }

}
