package com.shane.powersaver.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.shane.powersaver.AppContext;

import java.util.ArrayList;
import java.util.List;

public class BroadcastManageCloudAppConfigureHelper {
    private static final String TAG = BroadcastManageCloudAppConfigureHelper.class.getName();
    private static boolean DEBUG = AppContext.DEBUG;

    private long id;
    private String pkg;
    private String broadcast_ids;
    private String exceptPkgs;
    private int bgBroadcastDelayTime;

    public BroadcastManageCloudAppConfigureHelper() {
        id = -1;
        pkg = null;
        broadcast_ids = null;
        exceptPkgs = null;
        bgBroadcastDelayTime = BroadcastManageCloudAppConfigure.BG_DELAY_DEFAULT;
    }

//    public BroadcastManageRule toBroadcastManageRule() {
//        return new BroadcastManageRule(this.pkg, this.broadcast_ids, this.bgBroadcastDelayTime, this.exceptPkgs);
//    }
    /**
     * 辅助函数, 直接将{@link BroadcastManageCloudAppConfigure}表中查询的结果转化为BroadcastManageCloudAppConfigureHelper列表
     *
     * @param cursor BroadcastManageCloudAppConfigure表查询结果游标，调用者自行关闭
     * @return 对应的BroadcastManageCloudAppConfigureHelper列表
     */
    private static List<BroadcastManageCloudAppConfigureHelper> createFromTable(Cursor cursor, Context ctx) {
        List<BroadcastManageCloudAppConfigureHelper> array = new ArrayList<BroadcastManageCloudAppConfigureHelper>();
        try {
            int idIndex = cursor.getColumnIndex(BroadcastManageCloudAppConfigure.Columns.ID);
            int pkgIndex = cursor.getColumnIndex(BroadcastManageCloudAppConfigure.Columns.PACKAGE_NAME);
            int broadcast_ids_Index = cursor.getColumnIndex(BroadcastManageCloudAppConfigure.Columns.BG_BROADCAST_IDS);
            int bc_except_list_Index = cursor.getColumnIndex(BroadcastManageCloudAppConfigure.Columns.BG_EXCEPT_PKGS);
            int bc_DelayTimeIndex = cursor.getColumnIndex(BroadcastManageCloudAppConfigure.Columns.BG_DELAY);
            while (cursor.moveToNext()) {
                BroadcastManageCloudAppConfigureHelper result = new BroadcastManageCloudAppConfigureHelper();
                result.id = cursor.getLong(idIndex);
                result.pkg = cursor.getString(pkgIndex);
                result.broadcast_ids = cursor.getString(broadcast_ids_Index);
                if (! cursor.isNull(bc_except_list_Index)) {
                    result.exceptPkgs = cursor.getString(bc_except_list_Index);
                }
                if (! cursor.isNull(bc_DelayTimeIndex)) {
                    result.bgBroadcastDelayTime = cursor.getInt(bc_DelayTimeIndex);
                }
                array.add(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "createFromTable" + e);
        }
        return array;
    }
    public static List<BroadcastManageCloudAppConfigureHelper> createFromTable(Context ctx) {
        Cursor cursor = ctx.getContentResolver().query(BroadcastManageCloudAppConfigure.CONTENT_URI, null, null, null, null);
        List<BroadcastManageCloudAppConfigureHelper> array = createFromTable(cursor, ctx);
        cursor.close();
        return array;
    }
}
