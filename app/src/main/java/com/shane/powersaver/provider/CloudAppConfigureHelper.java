package com.shane.powersaver.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.shane.powersaver.AppContext;

import java.util.ArrayList;
import java.util.List;

public class CloudAppConfigureHelper {
    private static final String TAG = CloudAppConfigureHelper.class.getName();
    private static boolean DEBUG = AppContext.DEBUG;

    private long id;
    private String pkg;
    private String bgDataEnable;
    private int bgDataDelayTime;
    private int bgDataDelayCount;
    private int bgDataMinDataKb;
    private int bgDataMaxInactiveCount;
    private String bgLocationEnable;
    private int bgLocationDelayTime;
    private int bgKillDelayTime;
    private int bgSDelayTime;

    public CloudAppConfigureHelper() {
        id = -1;
        pkg = null;
        bgDataEnable = null;
        bgDataDelayTime = 0;
        bgDataDelayCount = -1;
        bgDataMinDataKb = -1;
        bgDataMaxInactiveCount = -1;
        bgLocationEnable = null;
        bgLocationDelayTime = 0;
        bgKillDelayTime = CloudAppConfigure.BG_KILL_DELAY_DEFAULT;
        bgSDelayTime = CloudAppConfigure.BG_SENSOR_DELAY_DEFAULT;
    }

    public String getBgLocationEnable() {
        return bgLocationEnable;
    }

    public int getBgLocationDelayTime() {
        return bgLocationDelayTime;
    }

    public int getBgKillDelayTime() {
        return bgKillDelayTime;
    }

    public int getBgSDelayTime() {
        return bgSDelayTime;
    }

    public static CloudAppConfigureHelper createFromPkg(Context ctx, String pkgName) {
        Uri uri = Uri.withAppendedPath(CloudAppConfigure.CONTENT_URI, pkgName);
        final ContentResolver cr = ctx.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        CloudAppConfigureHelper helper = null;
        if (cursor != null) {
            helper = createFirstFromTable(cursor, ctx);
            cursor.close();
        }
        return helper;
    }

    /**
     * 辅助函数, 直接将{@link CloudAppConfigure}表中查询的结果转化为CloudAppConfigureHelper列表
     *
     * @param cursor CloudAppConfigure表查询结果游标，调用者自行关闭
     * @return 对应的CloudAppConfigureHelper列表
     */
    private static List<CloudAppConfigureHelper> createFromTable(Cursor cursor, Context ctx) {
        List<CloudAppConfigureHelper> array = new ArrayList<CloudAppConfigureHelper>();
        try {
            int idIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.ID);
            int pkgIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.PACKAGE_NAME);
            int bgDataIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_DATA);
            int bgDataDelayTimeIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_DATA_DELAY_TIME);
            int bgDataDelayCountIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_DATA_DELAY_COUNT);
            int bgDataMinDataKbIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_DATA_MIN_DATA_KB);
            int bgDataMaxInactiveCountIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_DATA_MAX_INACTIVE_COUNT);
            int bgLocationIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_LOCATION);
            int bgLocationDelayTimeIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_LOCATION_DELAY_TIME);
            int bgKillDelayTimeIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_KILL_DELAY);
            int bgSDelayTimeIndex = cursor.getColumnIndex(CloudAppConfigure.Columns.BG_SENSOR_DELAY);
            while (cursor.moveToNext()) {
                CloudAppConfigureHelper result = new CloudAppConfigureHelper();
                result.id = cursor.getLong(idIndex);
                result.pkg = cursor.getString(pkgIndex);
                if (! cursor.isNull(bgDataIndex)) {
                    result.bgDataEnable = cursor.getString(bgDataIndex);
                }
                if (result.bgDataEnable != null && result.bgDataEnable.equals(CloudAppConfigure.BG_DATA_DISABLE)) {
                    if (! cursor.isNull(bgDataDelayTimeIndex)) {
                        result.bgDataDelayTime = cursor.getInt(bgDataDelayTimeIndex);
                    }
                    if (! cursor.isNull(bgDataDelayCountIndex)) {
                        result.bgDataDelayCount = cursor.getInt(bgDataDelayCountIndex);
                    }
                    if (! cursor.isNull(bgDataMinDataKbIndex)) {
                        result.bgDataMinDataKb = cursor.getInt(bgDataMinDataKbIndex);
                    }
                    if (! cursor.isNull(bgDataMaxInactiveCountIndex)) {
                        result.bgDataMaxInactiveCount = cursor.getInt(bgDataMaxInactiveCountIndex);
                    }
                }
                if (! cursor.isNull(bgLocationIndex)) {
                    result.bgLocationEnable = cursor.getString(bgLocationIndex);
                }
                if (result.bgLocationEnable != null && result.bgLocationEnable.equals(CloudAppConfigure.BG_LOCATION_DISABLE)) {
                    if (! cursor.isNull(bgLocationDelayTimeIndex)) {
                        result.bgLocationDelayTime = cursor.getInt(bgLocationDelayTimeIndex);
                    }
                    if (! cursor.isNull(bgSDelayTimeIndex)) {
                        result.bgSDelayTime = cursor.getInt(bgSDelayTimeIndex);
                    } else {
                        result.bgSDelayTime = GlobalFeatureConfigureHelper.getCloudBgSDelay(ctx);
                    }
                }
                if (cursor.isNull(bgKillDelayTimeIndex)) {
                    result.bgKillDelayTime = GlobalFeatureConfigureHelper.getCloudBgKillDelay(ctx);
                } else {
                    result.bgKillDelayTime = cursor.getInt(bgKillDelayTimeIndex);
                }
                array.add(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "createFromTable" + e);
        }
        return array;
    }

    /**
     * 辅助函数, 直接将{@link CloudAppConfigure}表中查询的结果中的首条记录转化为CloudAppConfigureHelper
     *
     * @param cursor CloudAppConfigure表查询结果游标，调用者自行关闭
     * @return 对应的CloudAppConfigureHelper
     */
    private static CloudAppConfigureHelper createFirstFromTable(Cursor cursor, Context ctx) {
        List<CloudAppConfigureHelper> array = createFromTable(cursor, ctx);
        if (array.size() > 0) {
            return array.get(0);
        } else {
            return null;
        }
    }
}
