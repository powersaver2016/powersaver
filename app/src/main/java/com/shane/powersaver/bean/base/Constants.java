package com.shane.powersaver.bean.base;

import android.text.format.DateUtils;

import java.text.DecimalFormat;

/**
 * Created by shane on 16-8-23.
 */
public class Constants {
    public static final DecimalFormat DOUBLE_FORMAT_2 = new DecimalFormat("######0.00");
    public static final int PER_USER_RANGE = 100000;
    public static final int FIRST_APPLICATION_UID = 10000;

    public static final String LAST_UPDATE_TIME = "powersaver_cloudcontrol_last_update_time";
    public static final String ENHANCE_HITS = "powersaver_hide_mode_enhance_hits";
    public static final String DAY_UPDATE_TIMES = "powersaver_cloudcontrol_day_update_times";
    public static final String LAST_DATA_MD5 = "powersaver_cloudcontrol_last_data_md5";
    public static final String HIDEMODE_UPDATE_TIME = "powersaver_update_time";
    public static final String ACTION_ALARM = "action_alarm";
    public static final long CTA_QUERY_PERIOD = 3 * DateUtils.DAY_IN_MILLIS; //3days
    public static final long QUERY_PERIOD = DateUtils.DAY_IN_MILLIS; //1day
    public static final long VERSION_CHECK_PERIOD = 4 * DateUtils.HOUR_IN_MILLIS; //4 hour

    public static final String CLOUD_UPDATE = "cloud_update";
    public static final String FIRST_POWER_ON = "first_power_on";
    public static final String IS_FIRST_POWER_ON = "is_first_power_on";

    public static final String PACKAGE_NAME = "com.shane.powersaver";

    public static final String INTENT_ACTION_USER_CHANGE = PACKAGE_NAME + ".action.USER_CHANGE";
    public static final String INTENT_ACTION_COMMENT_CHANGED = PACKAGE_NAME + ".action.COMMENT_CHANGED";
    public static final String INTENT_ACTION_NOTICE = PACKAGE_NAME + ".action.APPWIDGET_UPDATE";
    public static final String INTENT_ACTION_LOGOUT = PACKAGE_NAME + ".action.LOGOUT";
    public static final String WEICHAT_APPID = "wxa8213dc827399101";
    public static final String WEICHAT_SECRET = "5c716417ce72ff69d8cf0c43572c9284";

    public static final String QQ_APPID = "100942993";
    public static final String QQ_APPKEY = "8edd3cc7ca8dcc15082d6fe75969601b";
}
