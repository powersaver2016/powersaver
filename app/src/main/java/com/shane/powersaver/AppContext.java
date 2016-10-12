package com.shane.powersaver;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

import com.shane.powersaver.base.BaseApplication;
import com.squareup.leakcanary.LeakCanary;

/**
 * AppContext
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 *
 */
public class AppContext extends BaseApplication {
    public static final int PAGE_SIZE = 20;// 默认分页大小

    public static boolean DEBUG = false;
    public static boolean TRACE = false;

    public static final String KEY_FRITST_START = "KEY_FRIST_START";

    public static final String KEY_NIGHT_MODE_SWITCH = "night_mode_switch";

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        /*
        // AppException 取消
        Thread.setDefaultUncaughtExceptionHandler(AppException
                .getAppExceptionHandler(this));
                */
    }

    public static Context getContext() {
        return BaseApplication._context;
    }
    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        //return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
        return false;
    }

    // 设置夜间模式
    public static void setNightModeSwitch(boolean on) {
        set(KEY_NIGHT_MODE_SWITCH, on);
    }

    public static Gson createGson() {
        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }

    public boolean isLogin() {
        return false;
    }
}
