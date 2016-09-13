package com.shane.powersaver.cloudcontrol;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.util.Log;


import com.shane.powersaver.bean.base.Constants;
import com.shane.powersaver.provider.BroadcastManageCloudAppConfigure;
import com.shane.powersaver.provider.CloudAppConfigure;
import com.shane.powersaver.provider.GlobalFeatureConfigure;
import com.shane.powersaver.provider.GlobalFeatureConfigureHelper;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LocalUpdateUtils {
    public static final String TAG = LocalUpdateUtils.class.getName();
    public static final String FEATURE_STATUS = "hide_mode";
    public static final String DEVICEIDLE_STATUS = "idle_mode";
    public static final String MIUI_IDLE_STATUS = "miui_idle";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String NO_CORE_SYSTEM_APPS = "no_core_system_apps";
    public static final String MUSIC_APPS = "music_apps";
    public static final String LEVEL_ULTIMATE_SPECIAL_APPS = "level_ultimate_special_apps";
    public static final String BLE_SCAN_BLOCK_STATUS = "ble_scan_block";
    public static final String BLE_SCAN_BLOCK_PARAM = "ble_scan_param";
    public static final String FEATURE_BA_STATUS = "broadcast_alarm";
    public static final String FEATURE_FROZEN_STATUS = "frozen";
    public static final String FEATURE_SENSOR_STATUS = "set_sensor";
    public static final String FEATURE_HOT_FEEDBACK = "hot_feedback";
    public static final String FEATURE_NETWORK_FEEDBACK = "network_feedback";
    public static final String FEATURE_BG_LOCATION_DELAY_HOT = "location_delay_hot";
    public static final String FEATURE_BG_KILL_DELAY_HOT = "kill_delay_hot";
    private static final String FEATURE_LOCATION = "set_location";
    private static final String FEATURE_CONNECTION = "set_data_connection";
    private static final String FEATURE_KILL_DELAY = "kill_delay";
    private static final String FEATURE_SENSOR_DELAY = "sensor_delay";
    private static final String FEATURE_LCD = "set_lcd";
    private static final String APP_BG_DATA_DELAY_TIME = "app_delay";
    private static final String APP_BG_DATA_DELAY_COUNT = "set_delay_count";
    private static final String APP_BG_DATA_MIN_DATA_KB = "set_data_kb";
    private static final String APP_BG_DATA_MAX_INACTIVE_COUNT = "set_inactive_count";
    private static final String APP_BG_LOCATION_DELAY_TIME = "set_location_delay";
    private static final String APP_BG_DATA_DISABLE_SHORT_TIME = "set_data_disable_short";
    private static final String APP_BG_DATA_DISABLE_LONG_TIME = "set_data_disable_long";
    private static final String APP_BG_LOCATION_DISABLE_SHORT_TIME = "set_location_disable_short";

    private static final HashMap<String, String> sFeaturesMap = new HashMap<String, String>();
    static {
        sFeaturesMap.put(FEATURE_STATUS, GlobalFeatureConfigure.FEATURE_STATUS);
        sFeaturesMap.put(DEVICEIDLE_STATUS, GlobalFeatureConfigure.DEVICEIDLE_STATUS);
        sFeaturesMap.put(MIUI_IDLE_STATUS, GlobalFeatureConfigure.MIUI_IDLE_STATUS);
        sFeaturesMap.put(MIUI_STANDBY_STATUS, GlobalFeatureConfigure.MIUI_STANDBY_STATUS);
        sFeaturesMap.put(NO_CORE_SYSTEM_APPS, GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS);
        sFeaturesMap.put(MUSIC_APPS, GlobalFeatureConfigure.MUSIC_APPS);
        sFeaturesMap.put(LEVEL_ULTIMATE_SPECIAL_APPS, GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS);
        sFeaturesMap.put(BLE_SCAN_BLOCK_STATUS, GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS);
        sFeaturesMap.put(BLE_SCAN_BLOCK_PARAM, GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM);
        sFeaturesMap.put(FEATURE_BA_STATUS, GlobalFeatureConfigure.BA_STATUS);
        sFeaturesMap.put(FEATURE_FROZEN_STATUS, GlobalFeatureConfigure.FROZEN_STATUS);
        sFeaturesMap.put(FEATURE_SENSOR_STATUS, GlobalFeatureConfigure.SENSORS_STATUS);
        sFeaturesMap.put(FEATURE_LOCATION, GlobalFeatureConfigure.BG_LOCATION);
        sFeaturesMap.put(FEATURE_CONNECTION, GlobalFeatureConfigure.BG_DATA);
        sFeaturesMap.put(APP_BG_DATA_DELAY_TIME, GlobalFeatureConfigure.BG_DATA_DELAY_TIME);
        sFeaturesMap.put(APP_BG_DATA_DELAY_COUNT, GlobalFeatureConfigure.BG_DATA_DELAY_COUNT);
        sFeaturesMap.put(APP_BG_DATA_MIN_DATA_KB, GlobalFeatureConfigure.BG_DATA_MIN_DATA_KB);
        sFeaturesMap.put(APP_BG_DATA_MAX_INACTIVE_COUNT, GlobalFeatureConfigure.BG_DATA_MAX_INACTIVE_COUNT);
        sFeaturesMap.put(APP_BG_LOCATION_DELAY_TIME, GlobalFeatureConfigure.BG_LOCATION_DELAY_TIME);
        sFeaturesMap.put(APP_BG_DATA_DISABLE_SHORT_TIME, GlobalFeatureConfigure.BG_DATA_DISABLE_SHORT_TIME);
        sFeaturesMap.put(APP_BG_DATA_DISABLE_LONG_TIME, GlobalFeatureConfigure.BG_DATA_DISABLE_LONG_TIME);
        sFeaturesMap.put(APP_BG_LOCATION_DISABLE_SHORT_TIME, GlobalFeatureConfigure.BG_LOCATION_DISABLE_SHORT_TIME);
        sFeaturesMap.put(FEATURE_KILL_DELAY, GlobalFeatureConfigure.BG_KILL_DELAY);
        sFeaturesMap.put(FEATURE_SENSOR_DELAY, GlobalFeatureConfigure.SENSOR_DELAY);
        sFeaturesMap.put(FEATURE_BG_LOCATION_DELAY_HOT, GlobalFeatureConfigure.BG_LOCATION_DELAY_HOT);
        sFeaturesMap.put(FEATURE_BG_KILL_DELAY_HOT, GlobalFeatureConfigure.BG_KILL_DELAY_HOT);
        sFeaturesMap.put(FEATURE_HOT_FEEDBACK, GlobalFeatureConfigure.HOT_FEEDBACK_FEATURE);
        sFeaturesMap.put(FEATURE_NETWORK_FEEDBACK, GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE);
    }

    private static final HashMap<String, String> sAppMap = new HashMap<String, String>();

    static {
        sAppMap.put(FEATURE_LOCATION, CloudAppConfigure.Columns.BG_LOCATION);
        sAppMap.put(FEATURE_CONNECTION, CloudAppConfigure.Columns.BG_DATA);
        sAppMap.put(APP_BG_DATA_DELAY_TIME, CloudAppConfigure.Columns.BG_DATA_DELAY_TIME);
        sAppMap.put(APP_BG_DATA_DELAY_COUNT, CloudAppConfigure.Columns.BG_DATA_DELAY_COUNT);
        sAppMap.put(APP_BG_DATA_MIN_DATA_KB, CloudAppConfigure.Columns.BG_DATA_MIN_DATA_KB);
        sAppMap.put(APP_BG_DATA_MAX_INACTIVE_COUNT, CloudAppConfigure.Columns.BG_DATA_MAX_INACTIVE_COUNT);
        sAppMap.put(APP_BG_LOCATION_DELAY_TIME, CloudAppConfigure.Columns.BG_LOCATION_DELAY_TIME);
        sAppMap.put(FEATURE_KILL_DELAY, CloudAppConfigure.Columns.BG_KILL_DELAY);
        sAppMap.put(FEATURE_SENSOR_DELAY, CloudAppConfigure.Columns.BG_SENSOR_DELAY);
        sAppMap.put(FEATURE_BG_LOCATION_DELAY_HOT, CloudAppConfigure.Columns.BG_LOCATION_DELAY_HOT);
        sAppMap.put(FEATURE_BG_KILL_DELAY_HOT, CloudAppConfigure.Columns.BG_KILL_DELAY_HOT);
    }

    public static ContentValues getCloudAppContentValues(final Context context, final PowerKeeperCloudControlApp appRule) {
        ContentValues values = new ContentValues();
        values.put(CloudAppConfigure.Columns.PACKAGE_NAME, appRule.appName);
        for (String key: appRule.action.keySet()) {
            if (sAppMap.get(key) == null) continue;
            values.put(sAppMap.get(key), String.valueOf(appRule.action.get(key)));
        }
        return values;
    }

    public static boolean setCloudAppRule(final Context context, final List<ContentValues> cvsList) {
        ContentValues[] cvsArray = cvsList.toArray(new ContentValues[cvsList.size()]);
        Uri uri = CloudAppConfigure.CONTENT_URI;
        String method = CloudAppConfigure.Method.METHOD_OVERRIDE;
        Bundle extras = new Bundle();
        extras.setClassLoader(Thread.currentThread().getContextClassLoader());
        extras.putParcelableArray(method, cvsArray);
        context.getContentResolver().call(uri, method, null, extras);
        return true;
    }

    public static boolean setCloudAppRule(final Context context, final PowerKeeperCloudControlApp appRule) {
        boolean ret = true;
        boolean isExist = false;
        String selection = CloudAppConfigure.Columns.PACKAGE_NAME + " = " + "\"" + appRule.appName +"\"" ;
        String[] projection = new String[]{CloudAppConfigure.Columns.PACKAGE_NAME};
        Uri uri = CloudAppConfigure.CONTENT_URI;
        ContentValues values = new ContentValues();
        values.put(CloudAppConfigure.Columns.PACKAGE_NAME, appRule.appName);
        for (String key: appRule.action.keySet()) {
            if (sAppMap.get(key) == null) continue;
            values.put(sAppMap.get(key), String.valueOf(appRule.action.get(key)));
        }

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, null, null);
            if (cursor == null || cursor.getCount() < 1) {
                isExist = false;
            } else {
                isExist = true;
            }
            if (appRule.added) {
                if (isExist) {
                    cr.update(uri, values, selection, null);
                } else {
                    cr.insert(uri, values);
                }
            } else {
                if (isExist) {
                    cr.delete(uri, selection, null);
                } else {
                    ret = false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setCloudAppRule", e);
            ret = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Log.d(TAG, "setCloudAppRule: " + ret);
        return ret;
    }

    public static ContentValues getCloudFeatureContentValues(final Context context, final PowerKeeperCloudControlFeature featureRule) {
        String featureName = sFeaturesMap.get(featureRule.featureName);
        if (featureName == null) return null;
        ContentValues values = new ContentValues();
        values.put(featureName, featureRule.value);
        return values;
    }

    public static ContentValues getGlobalCloudFeatureContentValues(final Context context, final String key, final String value) {
        String featureName = sFeaturesMap.get(key);
        if (featureName == null) return null;
        ContentValues values = new ContentValues();
        values.put(featureName, value);
        return values;
    }

    public static boolean setCloudFeatureRule(final Context context, final PowerKeeperCloudControlFeature featureRule) {
        String featureName = sFeaturesMap.get(featureRule.featureName);
        if (featureName == null) return false;
        Bundle bundle = new Bundle();
        bundle.putString(featureName, featureRule.value);
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);

        Log.d(TAG, "setCloudFeatureRule: " + bundle.toString());
        return true;
    }

    public static boolean setCloudFeatureRule(final Context context, final List<ContentValues> cvsList) {
        Bundle bundle = new Bundle();
        for (ContentValues cvs : cvsList) {
            for (String key : cvs.keySet()) {
                bundle.putString(key, cvs.getAsString(key));
            }
        }
        GlobalFeatureConfigureHelper.updateGlobalConfigure(context, bundle);
        Log.d(TAG, "setCloudFeatureRule: " + bundle.toString());
        return true;
    }

    public static boolean setBroadcastCloudFeatureRule(final Context context, final List<ContentValues> cvsList) {
        ContentValues[] cvsArray = cvsList.toArray(new ContentValues[cvsList.size()]);
        Uri uri = BroadcastManageCloudAppConfigure.CONTENT_URI;
        String method = BroadcastManageCloudAppConfigure.Method.METHOD_OVERRIDE;
        Bundle extras = new Bundle();
        extras.setClassLoader(Thread.currentThread().getContextClassLoader());
        extras.putParcelableArray(method, cvsArray);
        context.getContentResolver().call(uri, method, null, extras);
        return true;
    }

    public static void saveLastDataMD5(final Context context, final String remoteMd5) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString(Constants.LAST_DATA_MD5, remoteMd5);
        editor.commit();
    }

    public static void saveLastUpdateTime(final Context context) {
        long time = System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString = format.format(new Date(time));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString(Constants.HIDEMODE_UPDATE_TIME, timeString);
        editor.commit();
    }

    public static String getLastUpdateTime(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String timeString = prefs.getString(Constants.HIDEMODE_UPDATE_TIME, null);
        return timeString;
    }

    // 本地数据的MD5值和云端比较, 不相同则本地数据为旧的
    public static boolean isLocalDataOld(Context context, final String remoteMd5) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String lastMd5 = prefs.getString(Constants.LAST_DATA_MD5, "");
        return !lastMd5.equalsIgnoreCase(remoteMd5);
    }

    public static String getURL(Context context, String key, String defURL) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String url = prefs.getString(key, defURL);
        return url;
    }

    public static void putURL(Context context, String key, String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString(key, url);
        editor.commit();
    }


    public static int getRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
}
