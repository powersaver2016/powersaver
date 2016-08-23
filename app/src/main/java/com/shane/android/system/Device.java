/*
 * Copyright (C) 2012 asksven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shane.android.system;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import com.shane.android.common.utils.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author sven
 */
public class Device {
    private static final String TAG = Device.class.getSimpleName();
    private static final String NONE = "Null";

    private static Device sInstance;
    private Class mSystemProperties;
    private Method mGetMethod;
    private boolean mEnvReady;

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }




    private Device(Context ctx) {
        try {
            mSystemProperties = ReflectionUtils.tryFindClass("android.os.SystemProperties", null);
            mGetMethod = ReflectionUtils.tryFindMethodBestMatch(mSystemProperties, "get", String.class, String.class);
        } catch (Exception e) {
            Log.e(TAG, "Device()", e);
        }

        if (mGetMethod == null) {
            mEnvReady = false;
        } else {
            mEnvReady = true;
        }
    }

    synchronized public static Device getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new Device(ctx);
        }

        return sInstance;
    }

    /**
     * Device model
     *
     * @return Device model(eg:Mi4W)
     */
    public String getDeviceModel() {
        if (mEnvReady) {
            try {
                return (String)mGetMethod.invoke(mSystemProperties, "ro.product.model", NONE);
            } catch (Exception e) {
                Log.e(TAG, "getDeviceModel()", e);
            }
        }
        return NONE;
    }

    public static String getDeviceVersion() {
        return Build.VERSION.INCREMENTAL;
    }

    public static String getDeviceBuild() {
        return Build.ID;
    }
}
