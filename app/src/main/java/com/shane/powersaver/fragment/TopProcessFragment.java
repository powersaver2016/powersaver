package com.shane.powersaver.fragment;

import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.adapter.general.CpuStateAdapter;
import com.shane.powersaver.bean.base.PageBean;
import com.shane.powersaver.bean.base.ResultBean;
import com.shane.powersaver.bean.base.State;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.fragment.general.GeneralListFragment;
import com.shane.powersaver.ui.empty.EmptyLayout;
import com.shane.powersaver.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class TopProcessFragment extends GeneralListFragment<State> {
    private static final String TAG = TopProcessFragment.class.getSimpleName();

    private boolean isFirst = true;

    ArrayList<State> mStates;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
    }

    @Override
    protected void requestData() {
        super.requestData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    protected BaseListAdapter<State> getListAdapter() {
        return new CpuStateAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<News>>>() {
        }.getType();
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        isFirst = false;
    }

    @Override
    protected void setListData(ResultBean<PageBean<State>> resultBean) {
        super.setListData(resultBean);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
        mBackgroundHandler.removeMessages(MSG_GET_DATA);
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        mBackgroundHandler.removeMessages(MSG_GET_DATA);
    }

    @Override
    public void doInBackground(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_GET_DATA:
                mStates = getTopProcesses(10);
                Collections.sort(mStates);
                mUiHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                break;
        }
    }

    @Override
    public void doInMainThread(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_UPDATE_DATA:
                mAdapter = getListAdapter();
                mListView.setAdapter(mAdapter);
                mAdapter.addItem(mStates);
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mRefreshLayout.setVisibility(View.VISIBLE);

                setFooterType(TYPE_NO_MORE);
                mRefreshLayout.setNoMoreData();
                mRefreshLayout.setOnRefreshListener(null);
                mRefreshLayout.setEnabled(false);
                mBackgroundHandler.sendEmptyMessageDelayed(MSG_GET_DATA, 2000);
                break;
        }
    }

    @Override
    protected void initData() {
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }


    private ArrayList<State> getTopProcesses(int num) {
        ArrayList<State> states = new ArrayList<State>();

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
            String[] lineComponents = line.trim().split("\\s+");
            if (lineComponents == null || lineComponents.length < 10) {
                continue;
            }

            String CPU = lineComponents[2]; // eg: 3%
            int rank;
            try {
                rank = Integer.parseInt(CPU.substring(0, CPU.length()-1));
            } catch (Exception e) {
                rank = 0;
            }
            State myState = new State(1, rank);
            myState.mName = lineComponents[9];
            states.add(myState);
        }

        // store the total time in order to be able to calculate ratio
        for (int i = 0; i < states.size(); i++) {
            states.get(i).setTotal(100);
        }
        return states;
    }
}

