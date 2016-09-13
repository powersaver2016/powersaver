package com.shane.powersaver.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.shane.powersaver.bean.base.Constants;


/**
 * Created by shane on 16-9-13.
 */
public class AlarmHelper {
    public static final int TYPE_BROADCAST = 0x01;
    public static final int TYPE_SERVICE = 0x02;
    public static final int TYPE_ACTIVITY = 0x04;
    public static final int TYPE_ACTIVITIES = 0x08;


    private static final String TAG = AlarmHelper.class.getSimpleName();

    private static AlarmHelper sInstance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    private AlarmHelper(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public static synchronized AlarmHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void scheduleElapsedAlarm(PendingIntent alarmIntent, long delay, boolean cancelOldAlarm) {
        if (cancelOldAlarm) {
            cancelAlarm(alarmIntent);
        }

        long nextAlarmTime = SystemClock.elapsedRealtime() + delay;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                nextAlarmTime, alarmIntent);
    }

    public PendingIntent getPendingIntent(String action, int type) {
        Intent intent = new Intent(action).setPackage(Constants.PACKAGE_NAME);
        PendingIntent pi = null;

        switch (type) {
            case TYPE_BROADCAST:
                pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                break;
            default:break;
        }

        return pi;
    }

    private void cancelAlarm(PendingIntent alarmIntent) {
        if (alarmIntent != null) {
            mAlarmManager.cancel(alarmIntent);
        }
    }
}
