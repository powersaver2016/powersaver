package com.shane.powersaver.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

//import com.android.internal.os.PowerProfile;

public class BatteryInfoHelper {
    private static final String TAG = BatteryInfoHelper.class.getSimpleName();
    private static final String BATTERY_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    private static final String REDMI_BATTERY_CURRENT_NOW = "/sys/class/power_supply/usb/device/FG_Battery_CurrentConsumption";
    private static final String BATTERY_INFO = "battery_info_settings";
    private static final String KEY_CURRENT_NOW_PATH = "key_current_now_path";

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private static BatteryInfoHelper mInstance;
    private String mCurrentNowPath;
    private int mDivisor;
    private IntentFilter mIntentFilter;

    private BatteryInfoHelper(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(BATTERY_INFO, 0);
        mCurrentNowPath = getCurrentNow();
        if  (TextUtils.isEmpty(mCurrentNowPath)) {
            String res = readSystemFile(REDMI_BATTERY_CURRENT_NOW);
            if (!TextUtils.isEmpty(res)) {
                mCurrentNowPath = REDMI_BATTERY_CURRENT_NOW;
            } else {
                mCurrentNowPath = BATTERY_CURRENT_NOW;
            }
            putCurrentNow(mCurrentNowPath);
        }

        if (mCurrentNowPath.equals(BATTERY_CURRENT_NOW)) {
            mDivisor = 1000;
        } else {
            mDivisor = 10;
        }
        mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    }

    public static BatteryInfoHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BatteryInfoHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    public int getBatteryPercent() {
        Intent batteryStatus = mContext.registerReceiver(null, mIntentFilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) (level * 100 / (float) scale);
        return batteryPct;
    }

    public boolean isBatteryCharging() {
        Intent batteryStatus = mContext.registerReceiver(null, mIntentFilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }

    public long getBatteryStandbyTime() {
        int bc = getBatteryCapacity()/100;
        int cn = getBatteryCurrentNow();
        if (cn < 0) cn = -cn;
        int remain = bc * getBatteryPercent();
        int seconds = (cn == 0 ? 0: remain/cn);
        Log.d(TAG, "standby, BatteryCapacity:"+bc+"\tremain:"+remain+"\tseconds:"+seconds);
        return seconds;
    }

    public long getBatteryChargeTime() {
        int bc = getBatteryCapacity()/100;
        int cn = getBatteryCurrentNow();
        if (cn < 0) cn = -cn;
        int remain = bc * (100 - getBatteryPercent());
        int seconds = (cn == 0 ? 0: remain/cn);
        Log.d(TAG, "charging, BatteryCapacity:"+bc+"\tremain:"+remain+"\tseconds:"+seconds);
        return seconds;
    }

    public int getBatteryCurrentNow() {
        String res = readSystemFile(mCurrentNowPath);
        int ret;
        try {
            ret = Integer.parseInt(res)/mDivisor;
        } catch (NumberFormatException e) {
            ret = 0;
        }
        if (isBatteryCharging()) {
            if (ret > 0) ret = -ret;
        } else {
             if (ret < 0) ret = -ret;
        }
        return ret;
    }

    public int getBatteryCapacity() {
//        PowerProfile profile = new PowerProfile(mContext);
//        double d = profile.getBatteryCapacity();
        double d = 4100;
        int ret = (int)(d * 3600);
        return ret;
    }

    static String readSystemFile(String file) {
        BufferedReader buffered_reader = null;
        String result = "";
        try {
            buffered_reader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = buffered_reader.readLine()) != null) {
                result += line;
            }
        } catch (FileNotFoundException e) {
            // ignore
        } catch (Exception e) {
            // ignore
        } finally {
            try {
                if (buffered_reader != null)
                    buffered_reader.close();
            } catch (Exception ex) {
                // ignore
            }
        }
        return result;
    }

    void putCurrentNow(String currentNow) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_CURRENT_NOW_PATH, currentNow);
        editor.commit();
    }

    String getCurrentNow() {
        return mSharedPreferences.getString(KEY_CURRENT_NOW_PATH, "");
    }


    public int getBatteryState() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status;
    }

    public int getTemperature() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
    }

    public float getVoltage() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000;
    }

    public int getHealth() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
    }

    public String getTechnology() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, filter);
        return batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }
}
