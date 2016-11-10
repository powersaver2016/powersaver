package com.shane.powersaver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shane.powersaver.base.BaseFragment;
import com.shane.powersaver.util.BatteryInfoHelper;
import com.shane.powersaver.util.LogUtil;
import com.shane.powersaver.util.RootShell;

import java.util.ArrayList;


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

    private BatteryInfoHelper mBatteryInfoHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
//        requestData(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeMessages(MSG_GET_DATA);
            if (isVisibleToUser) {
                mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeMessages(MSG_GET_DATA);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RootShell.getInstance().run("setenforce 1");
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

        LogUtil.i(TAG, "CurrentNow");
        mResultStats.add("CurrentNow:" + mBatteryInfoHelper.getBatteryCurrentNow() + "\n");
        mResultStats.add("Capacity:" + mBatteryInfoHelper.getBatteryCapacity() + "\n");
        mResultStats.add("Percent:" + mBatteryInfoHelper.getBatteryPercent() + "\n");
        mResultStats.add("ChargeTime:" + mBatteryInfoHelper.getBatteryChargeTime() + "\n");
        mResultStats.add("StandbyTime:" + mBatteryInfoHelper.getBatteryStandbyTime() + "\n");
        mResultStats.add("Technology:" + mBatteryInfoHelper.getTechnology() + "\n");
        mResultStats.add("BatteryState:" + mBatteryInfoHelper.getBatteryState() + "\n");
        mResultStats.add("Health:" + mBatteryInfoHelper.getHealth() + "\n");
        mResultStats.add("Temperature:" + mBatteryInfoHelper.getTemperature() + "\n");
        mResultStats.add("Voltage:" + mBatteryInfoHelper.getVoltage() + "\n");

    }

    private void requestData(boolean refresh) {
        mBackgroundHandler.removeMessages(MSG_GET_DATA);
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        mResultStats = new ArrayList<String>();
        // read CurrentNow
        RootShell.getInstance().run("setenforce 0");
        mBatteryInfoHelper = BatteryInfoHelper.getInstance(mContext);
    }

}
