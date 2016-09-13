package com.shane.powersaver.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class BroadcastManageGlobalFeatureConfigure {

    public static final String TABLE = "BCGlobalFeatureTable";
    /**
     * Content URI
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(PowerSaverConfigure.CONTENT_URI, TABLE);

    /*******************************************
     * 列名
     ******************************************/
    public interface Columns {

        public static final String ID = BaseColumns._ID;
        /**
         * 配置项目
         */
        public static final String CONFIGURE_NAME = "configureName";
        /**
         * 配置参数
         */
        public static final String CONFIGURE_PARAM = "configureParam";
    }

    public interface Method {
        public static final String METHOD_UPDATE = TABLE + "update";
        public static final String METHOD_QUERY = TABLE + "query";
    }

    /*******************************************
     * 参数
     ******************************************/
    /**
     * 配置项目可选列表 CONFIGURE_NAME
     */
    public static final String BROADCAST_DELAY = "b_delay";
    public static final String BROADCAST_STATUS = "g_broadcast_status";

    // 最后一次云端更新配置时间
    public static final String LAST_UPDATED = "lastUpdated";

    /**
     * 配置项目参数可选列表 CONFIGURE_PARAM
     */
    // BroadCast配置可选参数列表
    public static final String BC_ENABLE = "true";
    public static final String BC_DISABLE = "false";
    public static final String BC_DEFAULT = BC_DISABLE;
}
