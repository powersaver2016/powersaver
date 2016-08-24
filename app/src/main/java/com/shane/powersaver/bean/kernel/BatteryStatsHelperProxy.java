package com.shane.powersaver.bean.kernel;

import android.content.Context;
import android.util.Log;

import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.util.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by shane on 16-8-24.
 */
public final class BatteryStatsHelperProxy {
    private static final String TAG = BatteryStatsHelperProxy.class.getSimpleName();

    private static BatteryStatsHelperProxy mProxy = null;
    private Class mClassDefinition;
    private Object mInstance;
    private boolean mEnvReady;
    private Context mContext;

    synchronized public static BatteryStatsHelperProxy getInstance(Context ctx) {
        if (mProxy == null) {
            mProxy = new BatteryStatsHelperProxy(ctx);
        }

        return mProxy;
    }

    private BatteryStatsHelperProxy(Context ctx) {
        try {
            ClassLoader cl = ctx.getClassLoader();

//            Class helper = ReflectionUtils.tryFindClass("com.android.internal.os.BatteryStatsHelper", null);
            Class helper = cl.loadClass("com.android.internal.os.BatteryStatsHelper");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = Context.class;
            Constructor ctor = helper.getConstructor(paramTypes);

            //Parameters
            Object[] params = new Object[1];
            params[0] = ctx;
            mInstance = ctor.newInstance(params);
            mClassDefinition = helper;
            mContext = ctx.getApplicationContext();

            mEnvReady = true;
        } catch (Exception e) {
            mEnvReady = false;
        }
    }


    public void create(Object stats) {
        try {
            ClassLoader cl = mContext.getClassLoader();
            Class cls = cl.loadClass("android.os.BatteryStats");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = cls;
            Method method = mClassDefinition.getDeclaredMethod("create", paramTypes);

            Object[] params = new Object[1];
            params[0] = stats;

            method.invoke(mInstance, params);
        } catch (Exception e) {
            Log.e(TAG, "An exception occured in create(). Message: " + e.getMessage() + ", cause: " + Log.getStackTraceString(e));
        }
    }

    public void refreshStats(int statsType, int asUser) {
        try {
            Class[] paramTypes = new Class[2];
            paramTypes[0] = int.class;
            paramTypes[1] = int.class;
            Method method = mClassDefinition.getDeclaredMethod("refreshStats", paramTypes);

            Object[] params = new Object[2];
            params[0] = statsType;
            params[1] = asUser;

            method.invoke(mInstance, params);
        } catch (Exception e) {
            LogUtil.e(TAG, "An exception occured in refreshStats(). Message: " + e.getMessage() + ", cause: " + Log.getStackTraceString(e));
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<BatterySipper> getUsageList() {
        ArrayList<BatterySipper> myStats = new ArrayList<BatterySipper>();

        try {
            ClassLoader cl = mContext.getClassLoader();
            Class batterySipper = cl.loadClass("com.android.internal.os.BatterySipper");
            Class drainType = cl.loadClass("com.android.internal.os.BatterySipper$DrainType");

            Method method = mClassDefinition.getDeclaredMethod("getUsageList", null);
            ArrayList<Object> sippers = (ArrayList<Object>)method.invoke(mInstance, null);

            Field totalPowerMah = batterySipper.getField("totalPowerMah");
            Field drainTypeField = batterySipper.getField("drainType");
            for(Object obj : sippers) {
                double tpm = (double)totalPowerMah.get(obj);
                String name = drainTypeField.get(obj).toString();
                BatterySipper bs = new BatterySipper(name, tpm);
                myStats.add(bs);
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "An exception occured in getUsageList(). Message: " + e.getMessage() + ", cause: " + Log.getStackTraceString(e));
        }


        return myStats;
    }
}

