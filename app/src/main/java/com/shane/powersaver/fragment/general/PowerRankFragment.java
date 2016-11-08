package com.shane.powersaver.fragment.general;

import android.os.Build;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.adapter.general.PowerRankAdapter;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.PageBean;
import com.shane.powersaver.bean.base.ResultBean;
import com.shane.powersaver.bean.kernel.BatteryStatsHelperProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsTypes;
import com.shane.powersaver.bean.kernel.BatteryStatsTypesLolipop;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.ui.empty.EmptyLayout;
import com.shane.powersaver.util.LogUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 耗电排行
 */
public class PowerRankFragment extends GeneralListFragment<BatterySipper> {
    private static final String TAG = PowerRankFragment.class.getSimpleName();

    private boolean isFirst = true;
    ArrayList<BatterySipper> mItems = new ArrayList<BatterySipper>();

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
        BatterySipper bs = mAdapter.getItem(position - 1);
        if (bs != null) {


        }
    }

    @Override
    protected BaseListAdapter<BatterySipper> getListAdapter() {
        return new PowerRankAdapter(this);
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
    protected void setListData(ResultBean<PageBean<BatterySipper>> resultBean) {
        ((PowerRankAdapter)mAdapter).setSystemTime(resultBean.getTime());
        super.setListData(resultBean);
    }



    @Override
    public void doInBackground(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_GET_DATA:
                getData();
                mUiHandler.sendEmptyMessage(MSG_UPDATE_DATA);
                break;
        }
    }

    @Override
    public void doInMainThread(Message msg) {
        super.doInBackground(msg);
        switch (msg.what) {
            case MSG_UPDATE_DATA:
                updateUI();
                break;
        }
    }
    @Override
    protected void initData() {
        mBackgroundHandler.sendEmptyMessage(MSG_GET_DATA);
    }


    protected void getData() {
        BatteryStatsProxy.getInstance(mContext).invalidate();
        BatteryStatsProxy mStats = BatteryStatsProxy.getInstance(mContext);
        BatteryStatsHelperProxy bshp = BatteryStatsHelperProxy.getInstance(mContext);
        int statsType = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statsType = BatteryStatsTypesLolipop.STATS_CURRENT;
        } else {
            statsType = BatteryStatsTypes.STATS_CURRENT;
        }
        statsType = BatteryStatsTypes.STATS_SINCE_CHARGED;
        bshp.create(mStats.getBatteryStatsInstance());
        bshp.refreshStats(statsType, -1);
        ArrayList<BatterySipper> sippers = bshp.getUsageList();
        mItems = new ArrayList<BatterySipper>();
        Collections.sort(sippers);


        for (int i = 0; i < sippers.size(); i++) {
            BatterySipper sipper = sippers.get(i);
            if (sipper.getRatio() < 1) {
                break;
            } else {
                if (sipper.getRatio() < 100) {
                    mItems.add(sipper);
                }
            }
        }
        LogUtil.i(TAG, "size:====" + mItems.size());
    }

    protected void updateUI() {
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.addItem(mItems);

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRefreshLayout.setVisibility(View.VISIBLE);

        setFooterType(TYPE_NO_MORE);
        mRefreshLayout.setNoMoreData();
        mRefreshLayout.setOnRefreshListener(null);
        mRefreshLayout.setEnabled(false);
    }
}
