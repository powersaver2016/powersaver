package com.shane.powersaver.widget.indicator;

import android.support.v4.view.ViewPager;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
@SuppressWarnings("unused")
public interface PagerIndicator extends ViewPager.OnPageChangeListener {

    /**
     * bind the viewPager into indicator
     *
     * @param viewPager the ViewPager
     */
    void bindViewPager(ViewPager viewPager);


    /**
     * bind the viewPager into indicator
     *
     * @param viewPager       the ViewPager
     * @param initialPosition initialPosition
     */
    void bindViewPager(ViewPager viewPager, int initialPosition);


    /**
     * the ViewPager Current Item
     *
     * @param currentItem currentItem
     */
    void setCurrentItem(int currentItem);

    /**
     * the ViewPager ChangeListener
     *
     * @param listener listener
     */
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    /**
     * update the DataSet,invalidate
     */
    void notifyDataSetChanged();
}
