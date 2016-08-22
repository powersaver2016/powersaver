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

import com.shane.powersaver.AppContext;
import com.shane.powersaver.base.BaseFragment;
import com.shane.powersaver.bean.base.StatElement;
import com.shane.powersaver.bean.kernel.BatteryStatsProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsTypes;
import com.shane.powersaver.bean.kernel.BatteryStatsTypesLolipop;

import java.util.ArrayList;

/**
 * Created by shane on 16-8-22.
 */
public class BatteryHistorian2 extends BaseFragment {
    private static final String TAG = "BatteryHistorian2";
    private ScrollView mScrollView;
    private TextView mTextView;
    private Context mContext;
    private ArrayList<String> mResultStats;

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

        long timeBatteryUp = mStats.computeBatteryUptime(
                SystemClock.uptimeMillis() * 1000, statsType) / 1000;
        long timeScreenOn = mStats.getScreenOnTime(batteryRealtime, statsType) / 1000;
        long timePhoneOn = mStats.getPhoneOnTime(batteryRealtime, statsType) / 1000;
        if (AppContext.DEBUG) {
            mResultStats.add("whichRealtime = " + whichRealtime + " batteryRealtime = " + batteryRealtime + " timeBatteryUp=" + timeBatteryUp);
            mResultStats.add("\ttimeScreenOn = " + timeScreenOn + " timePhoneOn = " + timePhoneOn);
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
