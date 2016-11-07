package com.shane.powersaver.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
import com.shane.powersaver.base.BaseFragment;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.NativeKernelWakelock;
import com.shane.powersaver.bean.base.StatElement;
import com.shane.powersaver.bean.base.Wakelock;
import com.shane.powersaver.bean.kernel.BatteryStatsHelper;
import com.shane.powersaver.bean.kernel.BatteryStatsHelperProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsTypes;
import com.shane.powersaver.bean.kernel.BatteryStatsTypesLolipop;
import com.shane.powersaver.util.FileUtil;
import com.shane.powersaver.util.LogUtil;
import com.shane.powersaver.util.TextUtils;

import java.util.ArrayList;
import java.util.Collections;


/**
 * 电池信息页面
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-22
 */
public class BatteryInfo extends BaseFragment {
    private static final String TAG = BatteryInfo.class.getSimpleName();
    private ScrollView mScrollView;
    private TextView mTextView;
    private Context mContext;
    private ArrayList<String> mResultStats;
    private static final int TOP = 6;

    public static final String NORMAL_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    public static final String HERMES_CURRENT_NOW = "/sys/class/power_supply/usb/device/FG_Battery_CurrentConsumption";

    String mCurrentNowPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
        requestData(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        mBackgroundHandler.removeMessages(MSG_GET_DATA);
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
        mTextView.setText("");
        if (mResultStats == null || mResultStats.size() < 1) return;
        for (String stat : mResultStats) {
            print(stat);
        }
    }

    @Override
    public void doInBackground(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_GET_DATA:
                try {
                    getData();
                    mUiHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                } catch (Exception e) {

                }

                break;
        }
    }

    @Override
    public void doInMainThread(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_UPDATE_DATA:
                fillUI();
                mBackgroundHandler.sendEmptyMessageDelayed(MSG_GET_DATA, 1000);
                break;
        }
    }

    private void getData() throws Exception {
        mResultStats.clear();

        String currentNow = FileUtil.getFileString(mCurrentNowPath);
        mResultStats.add("CurrentNow:" + currentNow + "\n");
    }

    private void requestData(boolean refresh) {
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        mResultStats = new ArrayList<String>();
        String res = FileUtil.getFileString(HERMES_CURRENT_NOW);
        if (!TextUtils.isEmpty(res)) {
            mCurrentNowPath = HERMES_CURRENT_NOW;
        } else {
            mCurrentNowPath = NORMAL_CURRENT_NOW;
        }
        LogUtil.d(TAG, "mCurrentNowPath:" + mCurrentNowPath);
    }

}
