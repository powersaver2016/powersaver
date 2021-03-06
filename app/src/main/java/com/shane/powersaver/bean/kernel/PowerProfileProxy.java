package com.shane.powersaver.bean.kernel;

import android.content.Context;
import android.util.Log;

import com.shane.powersaver.util.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class PowerProfileProxy {
    private static final String TAG = PowerProfileProxy.class.getSimpleName();

    private static PowerProfileProxy mProxy = null;
    private Class mClassDefinition;
    private Object mInstance;
    private boolean mEnvReady;
    private Context mContext;

    private double mCapacity;

    public static synchronized PowerProfileProxy getInstance(Context ctx) {
        if (mProxy == null) {
            mProxy = new PowerProfileProxy(ctx);
        }

        return mProxy;
    }

    private PowerProfileProxy(Context ctx) {
        try {
            ClassLoader cl = ctx.getClassLoader();

            Class helper = cl.loadClass("com.android.internal.os.PowerProfile");
            Class[] paramTypes = new Class[1];
            paramTypes[0] = Context.class;
            Constructor ctor = helper.getConstructor(paramTypes);

            //Parameters
            Object[] params = new Object[1];
            params[0] = ctx;
            mInstance = ctor.newInstance(params);
            mClassDefinition = helper;
            mContext = ctx.getApplicationContext();

            mCapacity = getBC();
            mEnvReady = true;
        } catch (Exception e) {
            LogUtil.e(TAG, "An exception occured in PowerProfileProxy(): ", e);
            mEnvReady = false;
        }
    }

    // Mah
    public double getBatteryCapacity() {
        return mCapacity;
    }

    // Mah
    private double getBC() {
        try {
            Method method = mClassDefinition.getMethod("getBatteryCapacity");
            return (Double)method.invoke(mInstance, (Object[])null);
        } catch (Exception e) {
            LogUtil.e(TAG, "An exception occured in getBatteryCapacity():", e);
            return 3000;
        }
    }
}
