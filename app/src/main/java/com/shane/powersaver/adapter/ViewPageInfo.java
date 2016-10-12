package com.shane.powersaver.adapter;

import android.os.Bundle;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public final class ViewPageInfo {

	public final String tag;
    public final Class<?> clss;
    public final Bundle args;
    public final String title;

    public ViewPageInfo(String _title, String _tag, Class<?> _class, Bundle _args) {
    	title = _title;
        tag = _tag;
        clss = _class;
        args = _args;
    }
}