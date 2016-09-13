package com.shane.powersaver.cloudcontrol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;


import com.shane.powersaver.AppContext;
import com.shane.powersaver.bean.base.Constants;
import com.shane.powersaver.util.AssetUtils;
import com.shane.powersaver.util.Device;

import java.util.List;

public class CloudUpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_UPDATE_DATA = "com.shane.powersaver.action.UPDATE_CLOUD_DATA";

    private static final String TAG = CloudUpdateReceiver.class.getSimpleName();

    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String LOCAL_CONFIG_FILE = "local.config";
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        saveFirstPowerOnTime(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            resetAlarm(context);
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            if (tryUpdate(context)) {
                setAlarmFunction(context, Constants.QUERY_PERIOD);
            }
        } else if (Constants.ACTION_ALARM.equals(action)) {
            tryUpdate(context);
            setAlarmFunction(context, Constants.QUERY_PERIOD);
        }
    }

    public static void sendUpdateBroadcast(Context ctx) {
        Intent intent = new Intent(ACTION_UPDATE_DATA)
                .setPackage(Constants.PACKAGE_NAME);
//                .setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, intent, 0);
    }

    private PendingIntent getAlarmPendingIntent(Context context) {
        Intent intent = new Intent(context, CloudUpdateReceiver.class);
        intent.setAction(Constants.ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void setAlarmFunction(Context context, long delay) {
        cancelAlarmFunction(context);
        if(DEBUG) Log.i(TAG, "setAlarm, delay is " + delay);
        PendingIntent pendingIntent = getAlarmPendingIntent(context);
        setAlarm(context, System.currentTimeMillis() + delay, pendingIntent);
    }

    private void cancelAlarmFunction(Context context) {
        PendingIntent pendingIntent = getAlarmPendingIntent(context);
        cancelAlarm(context, pendingIntent);
    }

    // Try to update only under WIFI, if succeed, update LAST_UPDATE_TIME
    private boolean tryUpdate(Context context) {
        if (!Device.isWifiOpen()) {
            return false;
        }
        if (isUpdateTimeOverPeriod(context)) {
            update(context);
            saveLastUpdateTime(context, System.currentTimeMillis());
            return true;
        } else {
            if(DEBUG) Log.i(TAG, "isUpdateTimeOverPeriod: false");
        }
        return false;
    }

    private void update(Context context) {

        sendUpdateIntentToService(context);
    }

    private void sendUpdateIntentToService(Context context) {
        Intent intent = new Intent(context, CloudUpdateService.class);
        intent.setAction(Constants.CLOUD_UPDATE);
        context.startService(intent);
    }

    private void saveLastUpdateTime(Context context, long lastUpdateTime) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putLong(Constants.LAST_UPDATE_TIME, lastUpdateTime);
        editor.commit();
    }

    private void setAlarm(Context context, long triggerAtMillis, PendingIntent pendingIntent) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, triggerAtMillis, pendingIntent);
    }

    private void cancelAlarm(Context context, PendingIntent pendingIntent){
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    // 距离上次查询是否超过Globals.QUERY_PERIOD
    private boolean isUpdateTimeOverPeriod(Context context) {
        boolean ret = false;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final long currentTime = System.currentTimeMillis();
        final long lastTime = prefs.getLong(Constants.LAST_UPDATE_TIME, 0);
        if(DEBUG) Log.i(TAG, "currentTime is " + currentTime + ", lastTime is " + lastTime);
        if (currentTime < lastTime) {
            return true;
        }
        ret = (currentTime - lastTime) > Constants.QUERY_PERIOD;
        return ret;
    }

    // 重启后重新设定alarm确保系统有个alarm负责更新数据
    private long resetAlarm(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final long currentTime = System.currentTimeMillis();
        final long lastTime = prefs.getLong(Constants.LAST_UPDATE_TIME, 0);
        long triggerAtMillis = 0;
        long pastTime = currentTime - lastTime;
        if (pastTime > 0) {
            if (pastTime < Constants.QUERY_PERIOD) {
                triggerAtMillis = Constants.QUERY_PERIOD - pastTime;
                setAlarmFunction(context, triggerAtMillis);
            } else {
                if(DEBUG) Log.d(TAG, "Connectivity Changed event will trigger geting config");
            }
        } else {
            if(DEBUG) Log.w(TAG, "date error, nothing to do!");
        }

        return triggerAtMillis;
    }


    private void saveFirstPowerOnTime(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long firstPowerOn = prefs.getLong(Constants.FIRST_POWER_ON, 0);
        if (firstPowerOn == 0 || firstPowerOn > System.currentTimeMillis()) {
            if (firstPowerOn == 0) {
                String localConfig = new String(
                        Base64.decode(AssetUtils.getStrFromAssetFile(context, LOCAL_CONFIG_FILE), Base64.URL_SAFE));

                CloudUpdateHelper.parseLocalResult(context, localConfig);
            }

            Editor editor = prefs.edit();
            editor.putLong(Constants.FIRST_POWER_ON, System.currentTimeMillis());
            editor.commit();
        }
    }

    private long getFirstPowerOnTime(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(Constants.FIRST_POWER_ON, 0);
    }

    private boolean isFirstPowerOn(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(Constants.IS_FIRST_POWER_ON, true);
    }

}
