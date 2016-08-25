package com.shane.powersaver.bean.base;

import java.text.DecimalFormat;

/**
 * Created by shane on 16-8-23.
 */
public class Constants {
    public static final String LAST_UPDATE_TIME = "powerkeeper_cloudcontrol_last_update_time";
    public static final DecimalFormat DOUBLE_FORMAT_2 = new DecimalFormat("######0.00");
    public static final int PER_USER_RANGE = 100000;
    public static final int FIRST_APPLICATION_UID = 10000;

    public static final String INTENT_ACTION_USER_CHANGE = "net.oschina.action.USER_CHANGE";

    public static final String INTENT_ACTION_COMMENT_CHANGED = "net.oschina.action.COMMENT_CHANGED";

    public static final String INTENT_ACTION_NOTICE = "net.oschina.action.APPWIDGET_UPDATE";

    public static final String INTENT_ACTION_LOGOUT = "net.oschina.action.LOGOUT";

    public static final String WEICHAT_APPID = "wxa8213dc827399101";
    public static final String WEICHAT_SECRET = "5c716417ce72ff69d8cf0c43572c9284";

    public static final String QQ_APPID = "100942993";
    public static final String QQ_APPKEY = "8edd3cc7ca8dcc15082d6fe75969601b";
}
