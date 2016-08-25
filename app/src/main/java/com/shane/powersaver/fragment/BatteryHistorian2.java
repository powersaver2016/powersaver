package com.shane.powersaver.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shane.android.common.utils.DateUtils;
import com.shane.android.system.Device;
import com.shane.powersaver.AppContext;
import com.shane.powersaver.base.BaseFragment;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.Constants;
import com.shane.powersaver.bean.base.NativeKernelWakelock;
import com.shane.powersaver.bean.base.StatElement;
import com.shane.powersaver.bean.base.Wakelock;
import com.shane.powersaver.bean.kernel.BatteryStatsHelper;
import com.shane.powersaver.bean.kernel.BatteryStatsHelperProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsTypes;
import com.shane.powersaver.bean.kernel.BatteryStatsTypesLolipop;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by shane on 16-8-22.
 */
public class BatteryHistorian2 extends BaseFragment {
    private static final String TAG = "BatteryHistorian2";
    private ScrollView mScrollView;
    private TextView mTextView;
    private Context mContext;
    private ArrayList<String> mResultStats;
    private static final int TOP = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = this.getContext();
        mTextView = new TextView(mContext);
        mTextView.setText("");
        View view = mScrollView = new ScrollView(mContext);
        mScrollView.addView(mTextView);
//        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextView.setText("");
        try {
            requestData(true);
            fillUI();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + Log.getStackTraceString(e));
        }
//        print("Hello, World");
    }

    protected void print(CharSequence text) {
        mTextView.append(text);
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void fillUI() {
        if (mResultStats == null || mResultStats.size() < 1) return;
        for (String stat : mResultStats) {
            print(stat);
            Log.i(TAG, "stat: " + stat);
        }
    }

    private void requestData(boolean refresh) throws Exception {
        ArrayList<StatElement> myStats = new ArrayList<StatElement>();
        // List to store the other usages to
        ArrayList<StatElement> myUsages = new ArrayList<StatElement>();
        BatteryStatsProxy mStats = BatteryStatsProxy.getInstance(mContext);

        mResultStats.add("Aggregated Stats:\n");
        mResultStats.add("Device:" + Device.getInstance(mContext).getDeviceModel() + ", version:" + Device.getDeviceVersion() + "\n");
        mResultStats.add("Build:" + Device.getDeviceBuild() + "\n");

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

        int statsType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statsType = BatteryStatsTypesLolipop.STATS_CURRENT;
        } else {
            statsType = BatteryStatsTypes.STATS_CURRENT;
        }

        long whichRealtime = mStats.computeBatteryRealtime(rawRealtime, statsType) / 1000;
        long timeScreenOn = mStats.getScreenOnTime(batteryRealtime, statsType) / 1000;
        long timeBatteryUp = mStats.computeBatteryUptime(SystemClock.uptimeMillis() * 1000, statsType) / 1000;
        long screenOffUptime = timeBatteryUp - timeScreenOn;
        long timeScreenOff = whichRealtime - timeScreenOn;
        int screenOnDischarged = mStats.getDischargeAmountScreenOnSinceCharge();
        int screenOffDischarged = mStats.getDischargeAmountScreenOffSinceCharge();
        double screenOnDischargedRate = (screenOnDischarged * 1000 * 3600) / (double)timeScreenOn;
        double screenOffDischargedRate = (screenOffDischarged * 1000 * 3600) / (double)timeScreenOff;
        String screenOnDischargedStr = String.format("%.2f (Discharged: %d%%)", screenOnDischargedRate, screenOnDischarged);
        String screenOffDischargedStr = String.format("%.2f (Discharged: %d%%)", screenOffDischargedRate, screenOffDischarged);

        mResultStats.add("Duration / Realtime:" + DateUtils.formatDuration(whichRealtime) + "\n");
        mResultStats.add("Screen On Time:" + DateUtils.formatDuration(timeScreenOn) + "\n");
        mResultStats.add("Screen Off Uptime:" + DateUtils.formatDuration(screenOffUptime) + "\n");
        mResultStats.add("Screen On Discharge Rate (%/hr):" + screenOnDischargedStr + "\n");
        mResultStats.add("Screen Off Discharge Rate (%/hr):" + screenOffDischargedStr + "\n");

        ArrayList<StatElement> kernelWakelocks = mStats.getKernelWakelockStats(mContext, statsType, true);
        ArrayList<StatElement> partialWakelocks = mStats.getWakelockStats(mContext, BatteryStatsTypes.WAKE_TYPE_PARTIAL, statsType, 0);
        ArrayList<Wakelock> partialWakelocks2 = new ArrayList<Wakelock>();
        ArrayList<NativeKernelWakelock> kernelWakelocks2 = new ArrayList<NativeKernelWakelock>();
        long totalPartitalWakelockTime = 0;
        for (StatElement se : partialWakelocks) {
            Wakelock wl = (Wakelock)se;
            totalPartitalWakelockTime += wl.getDuration();
            partialWakelocks2.add(wl);
        }
        for (StatElement se : kernelWakelocks) {
            NativeKernelWakelock wl = (NativeKernelWakelock)se;
            kernelWakelocks2.add(wl);
        }

        long kernelOverheadTime = screenOffUptime - totalPartitalWakelockTime;
        mResultStats.add("Userspace Wakelock Time:" + DateUtils.formatDuration(totalPartitalWakelockTime) + "\n");
        mResultStats.add("Kernel Overhead Time:" + DateUtils.formatDuration(kernelOverheadTime) + "\n");

        BatteryStatsHelper bsh = new BatteryStatsHelper(mContext, statsType);
        mResultStats.add("Mobile Total KBs:" + bsh.computeMobileTotalKB() + "\n");
        mResultStats.add("WiFi Total KBs:" + bsh.computeWifiTotalKB() + "\n");
        mResultStats.add("Mobile Active Time:" + DateUtils.formatDuration(bsh.getMobileActiveTime()) + "\n");
        mResultStats.add("Signal Scanning Time:" + DateUtils.formatDuration(bsh.getPhoneSignalScanningTime()) + "\n");

        BatteryStatsHelperProxy bshp = BatteryStatsHelperProxy.getInstance(mContext);
        bshp.create(mStats.getBatteryStatsInstance());
        bshp.refreshStats(statsType, -1);
        ArrayList<BatterySipper> sippers = bshp.getUsageList();
        Collections.sort(sippers);

        mResultStats.add("========================================\n");
        mResultStats.add("Top power consuming entities:\n");
        for (int i = 0; i < TOP && i < sippers.size(); i++) {
            BatterySipper sipper = sippers.get(i);
            mResultStats.add(sipper.getData(0) + "\n");
        }

        mResultStats.add("========================================\n");
        mResultStats.add("Kernel wakesources:\n");
        Collections.sort(kernelWakelocks2);
        for (int i = 0; i < TOP && i < kernelWakelocks2.size(); i++) {
            NativeKernelWakelock wl = kernelWakelocks2.get(i);
            mResultStats.add(wl.getName() + "\t " + DateUtils.formatDuration(wl.getDuration()) + " \t" + wl.getCount() + "\n");
        }

        mResultStats.add("========================================\n");
        mResultStats.add("Userspace partial wakelocks:\n");
        Collections.sort(partialWakelocks2);
        for (int i = 0; i < TOP && i < partialWakelocks2.size(); i++) {
            Wakelock wl = partialWakelocks2.get(i);
            mResultStats.add(wl.getName() + "\t uid(" + wl.getuid() + ")\t" + DateUtils.formatDuration(wl.getDuration()) + "\t" + wl.getCount() + "\n");
        }





    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        mResultStats = new ArrayList<String>();
    }
}
