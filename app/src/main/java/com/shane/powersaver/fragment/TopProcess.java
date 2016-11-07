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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * TOP命令页面
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-22
 */
public class TopProcess extends BaseFragment {
    private static final String TAG = TopProcess.class.getSimpleName();
    private ScrollView mScrollView;
    private TextView mTextView;
    private Context mContext;
    private ArrayList<String> mResultStats;
    private static final int TOP = 6;

    public static final String NORMAL_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    public static final String HERMES_CURRENT_NOW = "/sys/class/power_supply/usb/device/FG_Battery_CurrentConsumption";

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
                mBackgroundHandler.sendEmptyMessageDelayed(MSG_GET_DATA, 3000);
                break;
        }
    }

    private void getData() throws Exception {
        mResultStats.clear();

        mResultStats.add(" " + getTopProcesses(10));

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
        mBatteryInfoHelper = BatteryInfoHelper.getInstance(mContext);
    }

    private String getTopProcesses(int num) {
        StringBuilder sb = new StringBuilder();
        String cmd = "top -n 1 -d 1 -m " + num;
        ArrayList<String> lines = new ArrayList<String>();
        String line = null;
        try {
            InputStreamReader isr = new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream());
            BufferedReader br = new BufferedReader(isr);
            while((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int begin = lines.size() - num;
        if(begin < 0){
            return null;
        }
        for (int i = begin; i < lines.size(); i++) {
            line = lines.get(i);
            if (line == null || line.length() == 0) {
                continue;
            }
            // split 后是 正则表达式, 用于匹配一个以上的空格符号
            String[] lineComponents = line.split(" +");
            if (lineComponents == null || lineComponents.length < 10) {
                continue;
            }
            sb.append("(");
            sb.append(lineComponents[9]);
            sb.append(":");
            sb.append(lineComponents[2]);
            sb.append(")");
            sb.append(line);
        }
        return sb.toString();
    }
}
