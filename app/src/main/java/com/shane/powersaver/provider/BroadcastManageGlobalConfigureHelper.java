package com.shane.powersaver.provider;

//import com.miui.powerkeeper.ContextHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;

public class BroadcastManageGlobalConfigureHelper {
    private static final String TAG = BroadcastManageGlobalConfigureHelper.class.getSimpleName();

    public static void setBroadcastStatus(Context ctx, boolean enable) {
//        int userId = ctx.getUserId();
        setBroadcastStatus(ctx, 1, enable);
    }

    private static void setBroadcastStatus(Context ctx, int userId, boolean enable) {
        Bundle bundle = new Bundle();
        if (enable) {
            bundle.putString(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS, BroadcastManageGlobalFeatureConfigure.BC_ENABLE);
        } else {
            bundle.putString(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS, BroadcastManageGlobalFeatureConfigure.BC_DISABLE);
        }
        updateGlobalConfigure(ctx, userId, bundle);
    }

    public static void setBroadcastDelay(Context ctx, int delay) {
//        int userId = ctx.getUserId();
        setBroadcastDelay(ctx, 1, delay);
    }

    private static void setBroadcastDelay(Context ctx, int userId, int delay) {
        Bundle bundle = new Bundle();
        bundle.putString(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY, Integer.toString(delay));
        updateGlobalConfigure(ctx, userId, bundle);
    }

    public static boolean getBroadcastStatus(Context ctx) {
//        int userId = ctx.getUserId();
        return getBroadcastStatus(ctx, 1);
    }

    private static boolean getBroadcastStatus(Context ctx, int userId) {
        Bundle bundle = queryGlobalConfigure(ctx);
        boolean enable = false;
        if (bundle.containsKey(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS)) {
            enable = bundle.getString(BroadcastManageGlobalFeatureConfigure.BROADCAST_STATUS).equals(BroadcastManageGlobalFeatureConfigure.BC_ENABLE);
        }
        return enable;
    }

    public static int getBroadcastDelay(Context ctx) {
//        int userId = ctx.getUserId();
        return getBroadcastDelay(ctx, 1);
    }

    private static int getBroadcastDelay(Context ctx, int userId) {
        Bundle bundle = queryGlobalConfigure(ctx);
        int delay = -2;
        if (bundle.containsKey(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY)) {
            delay = bundle.getInt(BroadcastManageGlobalFeatureConfigure.BROADCAST_DELAY);
        }
        return delay;
    }
    public static Bundle queryGlobalConfigure(Context ctx) {
//        int userId = ctx.getUserId();
        return queryGlobalConfigure(ctx, 1);
    }
    public static Bundle queryGlobalConfigure(Context ctx, int userId) {
        Bundle extras = new Bundle();
//        final ContentResolver cr = ContextHelper.getContentResolverForUser(ctx, UserHandle.OWNER);
//        Bundle bundle = cr.call(BroadcastManageGlobalFeatureConfigure.CONTENT_URI, BroadcastManageGlobalFeatureConfigure.Method.METHOD_QUERY, null, extras);
//        return bundle;
        return extras;
    }

    public static void updateGlobalConfigure(Context ctx, int userId, Bundle bundle) {
//        final ContentResolver cr = ContextHelper.getContentResolverForUser(ctx, UserHandle.OWNER);
//        cr.call(BroadcastManageGlobalFeatureConfigure.CONTENT_URI, BroadcastManageGlobalFeatureConfigure.Method.METHOD_UPDATE, null, bundle);
    }
}
