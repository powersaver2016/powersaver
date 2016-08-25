package com.shane.powersaver.fragment.general;

import android.os.Handler;
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
 * Created by shane on 16-8-25.
 */
public class CpuStateFragment extends GeneralListFragment<State> {
    private static final String TAG = PowerRankFragment.class.getSimpleName();

    public static final String HISTORY_NEWS = "history_news";
    private boolean isFirst = true;

    private static final String NEWS_BANNER = "news_banner";

    private ViewNewsHeader mHeaderView;
    private Handler handler = new Handler();


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
    protected void initData() {
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);

        ArrayList<State> states = CpuStates.getTimesInStates();
        Collections.sort(states);

        mAdapter.addItem(states);

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRefreshLayout.setVisibility(View.VISIBLE);

        setFooterType(TYPE_NO_MORE);
        mRefreshLayout.setNoMoreData();
        mRefreshLayout.setOnRefreshListener(null);
    }
}

