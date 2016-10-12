package com.shane.powersaver.bean.kernel;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class BatteryStatsHelper {
    private static final String TAG = BatteryStatsHelper.class.getSimpleName();

    BatteryStatsProxy mStats;
    Context mContext;

    int mStatType;

    long mWhichRealtime;
    long mTimeScreenOn;
    long mTimeBatteryUp;
    long mScreenOffUptime;
    long mTimeScreenOff;
    int mScreenOnDischarged;
    int mScreenOffDischarged;
    double mScreenOnDischargedRate;
    double mScreenOffDischargedRate;
    long mMobileRxTotalBytes;
    long mMobileTxTotalBytes;
    long mWifiRxTotalBytes;
    long mWifiTxTotalBytes;

    long mMobileActiveTime;
    long mPhoneSignalScanningTime;

    public BatteryStatsHelper(Context context) {
        this(context, BatteryStatsTypes.STATS_SINCE_CHARGED);
    }

    public BatteryStatsHelper(Context context, int statsType) {
        mContext = context;
        mStats = BatteryStatsProxy.getInstance(mContext);
        mStatType = statsType;
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "An exception occured initing BatteryStatsHelper(). Message: " + e.getMessage());
            Log.e(TAG, "Exception: " + Log.getStackTraceString(e));
        }
    }

    private void init() throws Exception {
        long rawRealtime = SystemClock.elapsedRealtime() * 1000;
        long uptime = SystemClock.uptimeMillis();
        long elaspedRealtime = rawRealtime / 1000;
        long batteryRealtime = 0;
        try {
            batteryRealtime = mStats.getBatteryRealtime(rawRealtime);
        } catch (Exception e) {
            Log.e(TAG, "An exception occured processing battery realtime. Message: " + e.getMessage());
            Log.e(TAG, "Exception: " + Log.getStackTraceString(e));
        }

        mWhichRealtime = mStats.computeBatteryRealtime(rawRealtime, mStatType) / 1000;
        mTimeScreenOn = mStats.getScreenOnTime(batteryRealtime, mStatType) / 1000;
        mTimeBatteryUp = mStats.computeBatteryUptime(SystemClock.uptimeMillis() * 1000, mStatType) / 1000;
        mScreenOffUptime = mTimeBatteryUp - mTimeScreenOn;
        mTimeScreenOff = mWhichRealtime - mTimeScreenOn;
        mScreenOnDischarged = mStats.getDischargeAmountScreenOnSinceCharge();
        mScreenOffDischarged = mStats.getDischargeAmountScreenOffSinceCharge();
        mScreenOnDischargedRate = (mScreenOnDischarged * 1000 * 3600) / (double) mTimeScreenOn;
        mScreenOffDischargedRate = (mScreenOffDischarged * 1000 * 3600) / (double) mTimeScreenOff;

        mMobileRxTotalBytes = mStats.getNetworkActivityBytes(BatteryStatsTypes.NETWORK_MOBILE_RX_DATA, mStatType);
        mMobileTxTotalBytes = mStats.getNetworkActivityBytes(BatteryStatsTypes.NETWORK_MOBILE_TX_DATA, mStatType);
        mWifiRxTotalBytes = mStats.getNetworkActivityBytes(BatteryStatsTypes.NETWORK_WIFI_RX_DATA, mStatType);
        mWifiTxTotalBytes = mStats.getNetworkActivityBytes(BatteryStatsTypes.NETWORK_WIFI_TX_DATA, mStatType);

        mMobileActiveTime = mStats.getMobileRadioActiveTime(rawRealtime, mStatType);
        mPhoneSignalScanningTime = mStats.getPhoneSignalScanningTime(rawRealtime, mStatType);
    }

    public long getTimeScreenOn() {
        return mTimeScreenOn;
    }

    public long getTimeScreenOff() {
        return mTimeScreenOff;
    }

    public double getScreenOffDischargedRate() {

        return mScreenOffDischargedRate;
    }

    public int getScreenOffDischarged() {

        return mScreenOffDischarged;
    }

    public double getScreenOnDischargedRate() {

        return mScreenOnDischargedRate;
    }

    public int getScreenOnDischarged() {

        return mScreenOnDischarged;
    }

    public long getScreenOffUptime() {

        return mScreenOffUptime;
    }

    public long getTimeBatteryUp() {

        return mTimeBatteryUp;
    }

    public long getWhichRealtime() {
        return mWhichRealtime;
    }

    public long computeMobileTotalKB() {
        return (mMobileRxTotalBytes + mMobileTxTotalBytes + 512) / 1024;
    }

    public long computeWifiTotalKB() {
        return (mWifiRxTotalBytes + mWifiTxTotalBytes + 512) / 1024;
    }

    public long getPhoneSignalScanningTime() {
        return mPhoneSignalScanningTime;
    }

    public long getMobileActiveTime() {
        return mMobileActiveTime;
    }
}
