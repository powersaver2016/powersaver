package com.shane.powersaver.util;

import java.util.Arrays;

/**
 * LogUtil
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-11 18:59
 *
 */

public class LogUtil {
    private static final String TAG = "PowerSaver";
    private static final boolean DEBUG = true;

    private LogUtil() {
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + ":" + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + ":" + msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + ":" + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(TAG, tag + ":" + msg, tr);
    }

    public static void v(String tag, String msg) {
        android.util.Log.v(TAG, tag + ":" + msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        android.util.Log.v(TAG, tag + ":" + msg, tr);
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, tag + ":" + msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.i(TAG, tag + ":" + msg, tr);
        }
    }

    public static void wtf(String tag, String msg) {
        android.util.Log.wtf(TAG, tag + ":" + msg);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        android.util.Log.wtf(TAG, tag + ":" + msg, tr);
    }

    public static String logify(String privacy) {
        if (privacy == null) {
            return null;
        } else {
            char[] log = new char[privacy.length()];
            Arrays.fill(log, '*');
            return new String(log);
        }
    }
}
