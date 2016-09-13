package com.shane.powersaver.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 云端配置, MIUI推荐配置
 */
public class CloudAppConfigure {


    public static final String TABLE = "cloudAppTable";
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
         * 软件包名
         */
        public static final String PACKAGE_NAME = "pkgName";
        /**
         * 后台data配置
         */
        public static final String BG_DATA = "bgData";
        /**
         * 后台data智能检测配置
         */
        public static final String BG_DATA_DELAY_TIME = "bgDataDelayTime";
        public static final String BG_DATA_DELAY_COUNT = "bgDataDelayCount";
        public static final String BG_DATA_MIN_DATA_KB = "bgDataMinDataKb";
        public static final String BG_DATA_MAX_INACTIVE_COUNT = "bgDataMaxInactiveCount";
        /**
         * 后台location配置
         */
        public static final String BG_LOCATION = "bgLocation";
        /**
         * 后台location delay配置
         */
        public static final String BG_LOCATION_DELAY_TIME = "bgLocationDelayTime";
        /**
         * 后台kill delay配置
         */
        public static final String BG_KILL_DELAY = "k_delay";
        /**
         * 后台s_delay配置
         */
        public static final String BG_SENSOR_DELAY = "s_delay";
        /**
         * 高温后台location delay配置
         */
        public static final String BG_LOCATION_DELAY_HOT = "l_delay_hot";
        /**
         * 高温后台kill delay配置
         */
        public static final String BG_KILL_DELAY_HOT = "k_delay_hot";
    }

    public interface Method {
        public static final String METHOD_OVERRIDE = TABLE + "override";
    }

    /*******************************************
     * 参数
     ******************************************/
    /**
     * 后台data配置可选参数列表 BG_DATA
     */
    public static final String BG_DATA_ENABLE = "true";
    public static final String BG_DATA_DISABLE = "false";
    /**
     * 后台location配置可选参数列表 BG_LOCATION
     */
    public static final String BG_LOCATION_ENABLE = "true";
    public static final String BG_LOCATION_DISABLE = "false";
    /**
     * 后台location delay配置可选参数列表 BG_LOCATION_DELAY_TIME
     */
    public static final int BG_LOCATION_DELAY_WITH_NETWORK = -1;
    /**
     * 后台kill_delay配置默认值: (-2, 不查杀) , (-1, 随活跃检测), (>=0, 后台一段时间查杀)
     */
    public static final int BG_KILL_DELAY_DEFAULT = -2;
    /**
     * 后台s_delay配置默认值: (-2, 不控制) , (-1, 随活跃检测), (>=0, 后台一段时间控制)
     */
    public static final int BG_SENSOR_DELAY_DEFAULT = -2;
}
