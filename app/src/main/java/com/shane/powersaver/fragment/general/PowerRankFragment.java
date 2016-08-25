package com.shane.powersaver.fragment.general;

import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import com.shane.powersaver.AppContext;
import com.shane.powersaver.R;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.adapter.general.NewsAdapter;
//import net.oschina.app.api.remote.OSChinaApi;
import com.shane.powersaver.adapter.general.PowerRankAdapter;
import com.shane.powersaver.bean.Banner;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.PageBean;
import com.shane.powersaver.bean.base.ResultBean;
import com.shane.powersaver.bean.kernel.BatteryStatsHelper;
import com.shane.powersaver.bean.kernel.BatteryStatsHelperProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsProxy;
import com.shane.powersaver.bean.kernel.BatteryStatsTypes;
import com.shane.powersaver.bean.kernel.BatteryStatsTypesLolipop;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.cache.CacheManager;
import com.shane.powersaver.ui.empty.EmptyLayout;
import com.shane.powersaver.util.LogUtil;
import com.shane.powersaver.util.UIHelper;
import com.shane.powersaver.widget.ViewNewsHeader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;

/**
 * 耗电排行
 */
public class PowerRankFragment extends GeneralListFragment<BatterySipper> {
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
    protected void initData() {
        mAdapter = getListAdapter();
        mListView.setAdapter(mAdapter);

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
        ArrayList<BatterySipper> items = new ArrayList<BatterySipper>();
        Collections.sort(sippers);


        for (int i = 0; i < sippers.size(); i++) {
            BatterySipper sipper = sippers.get(i);
            if (sipper.getRatio() < 1) {
                break;
            } else {
                if (sipper.getRatio() < 100) {
                    items.add(sipper);
                }
            }
        }

        mAdapter.addItem(items);

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRefreshLayout.setVisibility(View.VISIBLE);

        setFooterType(TYPE_NO_MORE);
        mRefreshLayout.setNoMoreData();
        mRefreshLayout.setOnRefreshListener(null);
    }
}
