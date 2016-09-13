package com.shane.powersaver.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 云端配置, MIUI推荐配置
 */
public class BroadcastManageCloudAppConfigure {


    public static final String TABLE = "BcCloudAppTable";
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
         * broadcast 该应用阻止的广播列表
         */
        public static final String BG_BROADCAST_IDS = "bgBroadcastIds";
        /**
         * 例外包名
         */
        public static final String BG_EXCEPT_PKGS = "except_pkg";
        /**
         * 后台延迟时间参数
         */
        public static final String BG_DELAY = BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY;
    }
    public interface Method {
        public static final String METHOD_OVERRIDE = TABLE + "override";
        public static final String METHOD_UPDATE = TABLE + "update";
        public static final String METHOD_INSERT = TABLE + "insert";
        public static final String METHOD_QUERY = TABLE + "query";
        public static final String METHOD_DELETE = TABLE + "delete";
    }

    /*******************************************
     * 参数
     ******************************************/

    /**
     * 后台b_delay配置默认值: (-2, 不控制) , (-1, 随活跃检测), (>=0, 后台一段时间控制)
     */
    public static final int BG_DELAY_DEFAULT = -2;
}
