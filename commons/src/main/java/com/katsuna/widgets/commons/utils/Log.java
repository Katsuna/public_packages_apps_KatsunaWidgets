package com.katsuna.widgets.commons.utils;

import android.content.Context;

public class Log {

    private static Context mContext;
    private static String mAppName = "";
    private static boolean mForceDebug = false;

    public static void initialize(Context context, String appName, boolean forceDebug) {
        mContext = context;
        mAppName = appName;
        mForceDebug = forceDebug;
    }

    private static final String TAG_DELIMETER = " - ";

    private static boolean debugEnabled() {
        return mForceDebug || android.util.Log.isLoggable(mAppName, android.util.Log.DEBUG);
    }

    private static boolean verboseEnabled() {
        return mForceDebug || android.util.Log.isLoggable(mAppName, android.util.Log.VERBOSE);
    }

    public static void d(Object obj, String msg) {
        if (debugEnabled()) {
            android.util.Log.d(mAppName, getPrefix(obj) + msg);
        }
    }

    public static void v(Object obj, String msg) {
        if (verboseEnabled()) {
            android.util.Log.v(mAppName, getPrefix(obj) + msg);
        }
    }

    public static void e(Object obj, String msg, Exception e) {
        android.util.Log.e(mAppName, getPrefix(obj) + msg, e);
        ExceptionLogger.save(mContext, mAppName, msg + e.getMessage());
    }

    public static void e(Object obj, String msg) {
        android.util.Log.e(mAppName, getPrefix(obj) + msg);
        ExceptionLogger.save(mContext, mAppName, msg);
    }

    public static void i(Object obj, String msg) {
        android.util.Log.i(mAppName, getPrefix(obj) + msg);
    }

    public static void w(Object obj, String msg) {
        android.util.Log.w(mAppName, getPrefix(obj) + msg);
    }

    public static void wtf(Object obj, String msg) {
        android.util.Log.wtf(mAppName, getPrefix(obj) + msg);
    }

    private static String getPrefix(Object obj) {
        return (obj == null ? "" : (obj.getClass().getSimpleName() + TAG_DELIMETER));
    }
}