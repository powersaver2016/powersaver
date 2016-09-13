package com.shane.powersaver.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class GlobalFeatureConfigure {


    public static final String TABLE = "GlobalFeatureTable";
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
         * 多用户的用户ID
         */
        public static final String USER_ID = "userId";
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
        public static final String METHOD_INSERT = TABLE + "insert";
        public static final String METHOD_QUERY = TABLE + "query";
        public static final String METHOD_DELETE = TABLE + "delete";
    }

    /*******************************************
     * 参数
     ******************************************/
    /**
     * 配置项目可选列表 CONFIGURE_NAME
     */
    public static final String FEATURE_STATUS = "featureStatus";
    public static final String DEVICEIDLE_STATUS = "deviceidleStatus";
    public static final String MIUI_IDLE_STATUS = "miui_idle";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String NO_CORE_SYSTEM_APPS = "noCoreSystemApps";
    public static final String BA_STATUS = "broadcastAlarmControlStatus";
    public static final String FROZEN_STATUS = "FrozenControlStatus";
    public static final String SENSORS_STATUS = "SensorControlStatus";
    public static final String MUSIC_APPS = "musicApps";
    public static final String BG_KILL_DELAY = "k_delay";
    public static final String SENSOR_DELAY = "s_delay";
    public static final String LEVEL_ULTIMATE_SPECIAL_APPS = "levelUtimateSpecialApps";
    public static final String BLE_SCAN_BLOCK_STATUS = "bleScanBlock";
    public static final String BLE_SCAN_BLOCK_PARAM = "bleScanParam";
    public static final String HOT_FEEDBACK_FEATURE = "hotFeedbackFeature";
    public static final String NETWORK_FEEDBACK_FEATURE = "networkFeedbackFeature";
    public static final String BG_LOCATION_DELAY_HOT = "l_delay_hot";
    public static final String BG_KILL_DELAY_HOT = "k_delay_hot";

    // 空间userId
    public static final String USER_ID = Columns.USER_ID;

    // 用户配置开关
    public static final String USER_CONFIGURE_STATUS = "userConfigureStatus";

    // 最后一次云端更新配置时间
    public static final String LAST_UPDATED = "lastUpdated";

    public static final String BG_DATA_DISABLE_SHORT_TIME = "bgDataDisableShortTime";
    public static final String BG_DATA_DISABLE_LONG_TIME = "bgDataDisableLongTime";

    public static final String BG_LOCATION_DISABLE_SHORT_TIME = "bgLocationDisableShortTime";

    // MIUI推荐配置后台数据相关
    public static final String BG_DATA = "bgData";
    public static final String BG_DATA_DELAY_TIME = "bgDataDelayTime";
    public static final String BG_DATA_DELAY_COUNT = "bgDataDelayCount";
    public static final String BG_DATA_MIN_DATA_KB = "bgDataMinDataKb";
    public static final String BG_DATA_MAX_INACTIVE_COUNT = "bgDataMaxInactiveCount";

    // MIUI推荐配置后台定位相关
    public static final String BG_LOCATION = "bgLocation";
    public static final String BG_LOCATION_DELAY_TIME = "bgLocationDelayTime";

    /**
     * 配置项目参数可选列表 CONFIGURE_PARAM
     */
    // Feature配置可选参数列表
    public static final String FEATURE_ENABLE = "true";
    public static final String FEATURE_DISABLE = "false";
    public static final String FEATURE_DEFAULT = FEATURE_DISABLE;
    // DeviceIdle配置可选参数列表
    public static final String DEVICEIDLE_ENABLE = "true";
    public static final String DEVICEIDLE_DISABLE = "false";
    public static final String DEVICEIDLE_DEFAULT = DEVICEIDLE_DISABLE;
    // QuickIdle配置可选参数列表
    public static final String QUICK_IDLE_ENABLE = "true";
    public static final String QUICK_IDLE_DISABLE = "false";
    public static final String QUICK_IDLE_DEFAULT = QUICK_IDLE_DISABLE;
    // AppIdle配置可选参数列表
    public static final String APP_IDLE_ENABLE = "true";
    public static final String APP_IDLE_DISABLE = "false";
    public static final String APP_IDLE_DEFAULT = APP_IDLE_ENABLE;
    // BroadCastAlarm配置可选参数列表
    public static final String BA_ENABLE = "true";
    public static final String BA_DISABLE = "false";
    public static final String BA_DEFAULT = BA_DISABLE;
    // FrozenControl配置可选参数列表
    public static final String FROZEN_ENABLE = "true";
    public static final String FROZEN_DISABLE = "false";
    public static final String FROZEN_DEFAULT = FROZEN_DISABLE;
    // SensorControl配置可选参数列表
    public static final String SENSOR_ENABLE = "true";
    public static final String SENSOR_DISABLE = "false";
    public static final String SENSOR_DEFAULT = SENSOR_DISABLE;
    // BleScanBlock配置可选参数列表
    public static final String BLE_SCAN_BLOCK_ENABLE = "true";
    public static final String BLE_SCAN_BLOCK_DISABLE = "false";
    public static final String BLE_SCAN_BLOCK_DEFAULT = BLE_SCAN_BLOCK_DISABLE;
    // User配置可选参数列表
    public static final String USER_CONFIGURE_DISABLE = "disable";
    public static final String USER_CONFIGURE_LEVEL_NORMAL = "normal";
    public static final String USER_CONFIGURE_LEVEL_ENHANCE = "enhance";
    public static final String USER_CONFIGURE_LEVEL_ULTIMATE = "ultimate";
    public static final String USER_CONFIGURE_DEFAULT = USER_CONFIGURE_LEVEL_ENHANCE;

    public static final String BG_DATA_DISABLE_SHORT_TIME_DEFAULT = "3";
    public static final String BG_DATA_DISABLE_LONG_TIME_DEFAULT = "10";
    public static final String BG_LOCATION_DISABLE_SHORT_TIME_DEFAULT = "3";

    // 后台data配置可选参数列表
    public static final String BG_DATA_ENABLE = "true";
    public static final String BG_DATA_DISABLE = "false";
    public static final String BG_DATA_DEFAULT = BG_DATA_ENABLE;
    public static final String BG_DATA_DELAY_TIME_DEFAULT = "3";
    public static final String BG_DATA_DELAY_COUNT_DEFAULT = "-1";
    public static final String BG_DATA_MIN_DATA_KB_DEFAULT = "-1";
    public static final String BG_DATA_MAX_INACTIVE_COUNT_DEFAULT = "-1";
    // 后台location配置可选参数列表
    public static final String BG_LOCATION_ENABLE = "true";
    public static final String BG_LOCATION_DISABLE = "false";
    public static final String BG_LOCATION_DEFAULT = BG_LOCATION_ENABLE;
    // 后台location delay配置可选参数列表 BG_LOCATION_DELAY_TIME
    public static final String BG_LOCATION_DELAY_WITH_NETWORK = "-1";
    public static final String BG_LOCATION_DELAY_TIME_DEFAULT = BG_LOCATION_DELAY_WITH_NETWORK;
    // Feedback配置可选参数列表 HOT_FEEDBACK_FEATURE NETWORK_FEEDBACK_FEATURE
    public static final String HOT_FEEDBACK_FEATURE_ENABLE = "true";
    public static final String HOT_FEEDBACK_FEATURE_DISABLE = "false";
    public static final String NETWORK_FEEDBACK_FEATURE_ENABLE = "true";
    public static final String NETWORK_FEEDBACK_FEATURE_DISABLE = "false";
}
