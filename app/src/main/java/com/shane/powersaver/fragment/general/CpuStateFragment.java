package com.shane.powersaver.fragment.general;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.adapter.general.CpuStateAdapter;
import com.shane.powersaver.bean.base.PageBean;
import com.shane.powersaver.bean.base.ResultBean;
import com.shane.powersaver.bean.base.State;
import com.shane.powersaver.bean.kernel.CpuStates;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.ui.empty.EmptyLayout;
import com.shane.powersaver.widget.ViewNewsHeader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class CpuStateFragment extends GeneralListFragment<State> {
    private static final String TAG = PowerRankFragment.class.getSimpleName();

    public static final String HISTORY_NEWS = "history_news";
    private boolean isFirst = true;

    private static final String NEWS_BANNER = "news_banner";

    private ViewNewsHeader mHeaderView;
    private Handler handler = new Handler();
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
    public void doInBackground(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_GET_DATA:
                mStates = CpuStates.getTimesInStates();
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
                break;
        }
    }

    @Override
    protected void initData() {
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }
}

