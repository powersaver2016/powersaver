package com.shane.powersaver.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GlobalFeatureConfigureHelper {
    private static final String TAG = GlobalFeatureConfigureHelper.class.getSimpleName();

    public static List<String> getSystemNonCoreApps(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        String nonCoreApps = null;
        if (bundle.containsKey(GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS)) {
            nonCoreApps = bundle.getString(GlobalFeatureConfigure.NO_CORE_SYSTEM_APPS);
        }
        List<String> nonCoreAppsList = new ArrayList<String>();
        if (nonCoreApps != null && !"".equals(nonCoreApps)) {
            final String[] names = nonCoreApps.split(":");
            for (int i = 0; i < names.length; i++) {
                nonCoreAppsList.add(names[i]);
            }
        }
        Log.i(TAG, "nonCoreAppsList: " + nonCoreAppsList);
        return nonCoreAppsList;
    }

    public static List<String> getMusicApps(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        String musicApps = null;
        if (bundle.containsKey(GlobalFeatureConfigure.MUSIC_APPS)) {
            musicApps = bundle.getString(GlobalFeatureConfigure.MUSIC_APPS);
        }
        List<String> musicAppsList = new ArrayList<String>();
        if (musicApps != null && !"".equals(musicApps)) {
            final String[] names = musicApps.split(":");
            for (int i = 0; i < names.length; i++) {
                musicAppsList.add(names[i]);
            }
        }
        Log.i(TAG, "musicAppsList: " + musicAppsList);
        return musicAppsList;
    }

    public static List<String> getUltimateSpecialApps(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        String levelUltimateSpecialApps = null;
        if (bundle.containsKey(GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS)) {
            levelUltimateSpecialApps = bundle.getString(GlobalFeatureConfigure.LEVEL_ULTIMATE_SPECIAL_APPS);
        }
        List<String> levelUltimateSpecialAppsList = new ArrayList<String>();
        if (levelUltimateSpecialApps != null && !"".equals(levelUltimateSpecialApps)) {
            final String[] names = levelUltimateSpecialApps.split(":");
            for (int i = 0; i < names.length; i++) {
                levelUltimateSpecialAppsList.add(names[i]);
            }
        }
        Log.i(TAG, "levelUltimateSpecialAppsList: " + levelUltimateSpecialAppsList);
        return levelUltimateSpecialAppsList;
    }

    public static String getUserConfigure(Context ctx) {
        int userId = 1;
        return getUserConfigure(ctx, userId);
    }

    public static String getUserConfigure(Context ctx, int userId) {
        Bundle bundle = queryGlobalConfigure(ctx, userId);
        String configure = GlobalFeatureConfigure.USER_CONFIGURE_DISABLE;
        if (bundle.containsKey(GlobalFeatureConfigure.USER_CONFIGURE_STATUS)) {
            configure = bundle.getString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS);
        }
        return configure;
    }

    public static void setUserConfigure(Context ctx, boolean enable) {
        int userId = 1;
        setUserConfigure(ctx, userId, enable);
    }

    public static void setUserConfigure(Context ctx, int userId, boolean enable) {
        Bundle bundle = new Bundle();
        if (enable) {
            bundle.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, GlobalFeatureConfigure.USER_CONFIGURE_LEVEL_ENHANCE);
        } else {
            bundle.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, GlobalFeatureConfigure.USER_CONFIGURE_DISABLE);
        }
        updateGlobalConfigure(ctx, userId, bundle);
    }

    public static void setUserConfigure(Context ctx, String configure) {
        int userId = 1;
        setUserConfigure(ctx, userId, configure);
    }

    public static void setUserConfigure(Context ctx, int userId, String configure) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalFeatureConfigure.USER_CONFIGURE_STATUS, configure);
        updateGlobalConfigure(ctx, userId, bundle);
    }

    public static boolean getCloudFeatureConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.FEATURE_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.FEATURE_STATUS).equals(GlobalFeatureConfigure.FEATURE_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudBroadcastAlarmConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.BA_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.BA_STATUS).equals(GlobalFeatureConfigure.BA_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudFrozenConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.FROZEN_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.FROZEN_STATUS).equals(GlobalFeatureConfigure.FROZEN_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudSensorConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.SENSORS_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.SENSORS_STATUS).equals(GlobalFeatureConfigure.SENSOR_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudNetRechableConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE)) {
            enable = bundle.getString(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE).equals(GlobalFeatureConfigure.NETWORK_FEEDBACK_FEATURE_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudDeviceIdleConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.DEVICEIDLE_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.DEVICEIDLE_STATUS).equals(GlobalFeatureConfigure.DEVICEIDLE_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudAppIdleConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.MIUI_STANDBY_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.MIUI_STANDBY_STATUS).equals(GlobalFeatureConfigure.APP_IDLE_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudQuickIdleConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.MIUI_IDLE_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.MIUI_IDLE_STATUS).equals(GlobalFeatureConfigure.QUICK_IDLE_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudBgLocationConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.BG_LOCATION)) {
            enable = bundle.getString(GlobalFeatureConfigure.BG_LOCATION).equals(GlobalFeatureConfigure.BG_LOCATION_ENABLE);
        }
        return enable;
    }

    public static boolean getCloudBleScanBlockConfigure(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS)) {
            enable = bundle.getString(GlobalFeatureConfigure.BLE_SCAN_BLOCK_STATUS).equals(GlobalFeatureConfigure.BLE_SCAN_BLOCK_ENABLE);
        }
        return enable;
    }

    public static String getCloudBleScanBlockParam(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        String param = null;
        if (bundle.containsKey(GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM)) {
            param = bundle.getString(GlobalFeatureConfigure.BLE_SCAN_BLOCK_PARAM);
        }
        return param;
    }

    public static int getCloudBgKillDelay(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        int def = -2;
        if (bundle.containsKey(GlobalFeatureConfigure.BG_KILL_DELAY)) {
            try {
                def = Integer.parseInt(bundle.getString(GlobalFeatureConfigure.BG_KILL_DELAY));
            } catch (Exception e) {
                Log.e(TAG, "getCloudBgKillDelay", e);
            }
        }
        return def;
    }

    public static int getCloudBgSDelay(Context ctx) {
        Bundle bundle = queryGlobalConfigure(ctx);
        int def = -2;
        if (bundle.containsKey(GlobalFeatureConfigure.SENSOR_DELAY)) {
            try {
                def = Integer.parseInt(bundle.getString(GlobalFeatureConfigure.SENSOR_DELAY));
            } catch (Exception e) {
                Log.e(TAG, "getCloudBgSDelay", e);
            }
        }
        return def;
    }

    public static boolean getConfigureStatusBoolean(Context ctx) {
        Uri uri = GlobalFeatureConfigure.CONTENT_URI;
        String selection = GlobalFeatureConfigure.Columns.CONFIGURE_NAME + " = ?";
        String[] selectionArgs = new String[] {
                GlobalFeatureConfigure.USER_CONFIGURE_STATUS
        };
        final ContentResolver cr = ctx.getContentResolver();
        Cursor cursor = cr.query(uri, null, selection, selectionArgs, null);
        boolean userConfigureEnable = false;
        if (cursor != null) {
            int userIdIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.USER_ID);
            int keyIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_NAME);
            int valueIndex = cursor.getColumnIndex(GlobalFeatureConfigure.Columns.CONFIGURE_PARAM);
            while (cursor.moveToNext()) {
                String value = cursor.getString(valueIndex);
                if (! value.equals(GlobalFeatureConfigure.USER_CONFIGURE_DISABLE)) {
                    userConfigureEnable = true;
                    break;
                }
            }
            cursor.close();
        }
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (userConfigureEnable) {
            if (bundle.containsKey(GlobalFeatureConfigure.FEATURE_STATUS) &&
                    bundle.getString(GlobalFeatureConfigure.FEATURE_STATUS).equals(GlobalFeatureConfigure.FEATURE_ENABLE)) {
                enable = true;
            } else if (bundle.containsKey(GlobalFeatureConfigure.BA_STATUS) &&
                    bundle.getString(GlobalFeatureConfigure.BA_STATUS).equals(GlobalFeatureConfigure.BA_ENABLE)) {
                enable = true;
            } else if (bundle.containsKey(GlobalFeatureConfigure.FROZEN_STATUS) &&
                    bundle.getString(GlobalFeatureConfigure.FROZEN_STATUS).equals(GlobalFeatureConfigure.FROZEN_ENABLE)) {
                enable = true;
            }
        }
        return enable;
    }

    public static Bundle queryGlobalConfigure(Context ctx) {
        int userId = 1;
        return queryGlobalConfigure(ctx, userId);
    }

    public static Bundle queryGlobalConfigure(Context ctx, int userId) {
        Bundle extras = new Bundle();
        extras.putInt(GlobalFeatureConfigure.Columns.USER_ID, userId);
        final ContentResolver cr = ctx.getContentResolver();
        Bundle bundle = cr.call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_QUERY, null, extras);
        return bundle;
    }

    public static void updateGlobalConfigure(Context ctx, Bundle bundle) {
        int userId = 1;
        updateGlobalConfigure(ctx, userId, bundle);
    }

    public static void updateGlobalConfigure(Context ctx, int userId, Bundle bundle) {
        bundle.putInt(GlobalFeatureConfigure.Columns.USER_ID, userId);
        final ContentResolver cr = ctx.getContentResolver();
        cr.call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_UPDATE, null, bundle);
    }

    public static void insertGlobalConfigure(Context ctx, Bundle bundle) {
        int userId = 1;
        insertGlobalConfigure(ctx, userId, bundle);
    }

    public static void insertGlobalConfigure(Context ctx, int userId, Bundle bundle) {
        bundle.putInt(GlobalFeatureConfigure.Columns.USER_ID, userId);
        final ContentResolver cr = ctx.getContentResolver();
        cr.call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_INSERT, null, bundle);
    }

    public static void deleteGlobalConfigure(Context ctx, Bundle bundle) {
        int userId = 1;
        deleteGlobalConfigure(ctx, userId, bundle);
    }

    public static void deleteGlobalConfigure(Context ctx, int userId, Bundle bundle) {
        bundle.putInt(GlobalFeatureConfigure.Columns.USER_ID, userId);
        final ContentResolver cr = ctx.getContentResolver();
        cr.call(GlobalFeatureConfigure.CONTENT_URI, GlobalFeatureConfigure.Method.METHOD_DELETE, null, bundle);
    }
}
