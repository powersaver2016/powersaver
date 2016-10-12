package com.shane.powersaver.fragment.general;

import com.shane.powersaver.fragment.base.BaseListFragment;
import com.shane.powersaver.interf.OnTabReselectListener;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */

public abstract class GeneralListFragment<T> extends BaseListFragment<T> implements OnTabReselectListener {
    @Override
    public void onTabReselect() {
        mListView.setSelection(0);
        mRefreshLayout.setRefreshing(true);
        onRefreshing();
    }
}
