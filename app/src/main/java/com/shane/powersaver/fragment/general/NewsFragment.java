package com.shane.powersaver.fragment.general;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.shane.powersaver.adapter.base.BaseListAdapter;
import com.shane.powersaver.adapter.general.NewsAdapter;
import com.shane.powersaver.bean.Banner;
import com.shane.powersaver.bean.base.PageBean;
import com.shane.powersaver.bean.base.ResultBean;
import com.shane.powersaver.bean.news.News;
import com.shane.powersaver.cache.CacheManager;
import com.shane.powersaver.widget.ViewNewsHeader;

import java.lang.reflect.Type;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class NewsFragment extends GeneralListFragment<News> {

    public static final String HISTORY_NEWS = "history_news";
    private boolean isFirst = true;

    private static final String NEWS_BANNER = "news_banner";

    private ViewNewsHeader mHeaderView;
    private Handler handler = new Handler();

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mHeaderView = new ViewNewsHeader(getActivity());
        mExeService.execute(new Runnable() {
            @Override
            public void run() {
                final PageBean<Banner> pageBean = (PageBean<Banner>) CacheManager.readObject(getActivity(), NEWS_BANNER);
                if (pageBean != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderView.initData(getImgLoader(), pageBean.getItems());
                        }
                    });
                }
            }
        });

        mHeaderView.setRefreshLayout(mRefreshLayout);
        mListView.addHeaderView(mHeaderView);
        getBannerList();
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (!isFirst)
            getBannerList();
    }

    @Override
    protected void requestData() {
        super.requestData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        News news = mAdapter.getItem(position - 1);
        if (news != null) {


        }
    }

    @Override
    protected BaseListAdapter<News> getListAdapter() {
        return new NewsAdapter(this);
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
    protected void setListData(ResultBean<PageBean<News>> resultBean) {
        ((NewsAdapter)mAdapter).setSystemTime(resultBean.getTime());
        super.setListData(resultBean);
    }

    private void getBannerList() {

    }
}
