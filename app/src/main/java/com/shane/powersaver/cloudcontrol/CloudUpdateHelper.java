package com.shane.powersaver.cloudcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shane on 16-9-13.
 */
public class CloudUpdateHelper {
    private static final String TAG = CloudUpdateHelper.class.getSimpleName();

    static boolean parseLocalResult(final Context context, final String result) {
        return parseResult(context, result);
    }

    public static void startUpdate(Context context) {

    }

    static boolean addOptGlobalFeature(final Context context, List<ContentValues> cvsList, JSONObject data, String key) {
        String featureValue = data.optString(key);
        Log.d(TAG, "key is " + key);
        Log.d(TAG, "featureValue is " + featureValue);
        if (!TextUtils.isEmpty(featureValue)) {
            ContentValues cv = CloudUpdateUtils.getGlobalCloudFeatureContentValues(context, key, featureValue);
            if (cv != null) {
                cvsList.add(cv);
            }
            return true;
        }
        return false;
    }

    public static boolean parseResult(final Context context, final String json) {
        boolean ret = false;
        try {
            JSONObject data = new JSONObject(json);
            JSONArray apps = data.getJSONArray(PowerSaverCloudControlApp.APP_LIST);
            JSONArray features =  data.getJSONArray(PowerSaverCloudControlFeature.FEATURE_LIST);
            List<ContentValues> cvsList = new ArrayList<ContentValues>();
            for (int i = 0; i < apps.length(); i++) {
                PowerSaverCloudControlApp app = PowerSaverCloudControlApp.parseFromJson(apps.optJSONObject(i));
                if (app != null) {
                    ContentValues cv = CloudUpdateUtils.getCloudAppContentValues(context, app);
                    if (cv != null) {
                        cvsList.add(cv);
                    }
                }
            }
            CloudUpdateUtils.setCloudAppRule(context, cvsList);

            cvsList.clear();
            for (int i = 0; i < features.length(); i++) {
                PowerSaverCloudControlFeature feature = PowerSaverCloudControlFeature.parseFromJson(features.optJSONObject(i));
                if (feature != null) {
                    ContentValues cv = CloudUpdateUtils.getCloudFeatureContentValues(context, feature);
                    if (cv != null) {
                        cvsList.add(cv);
                    }
                }
            }
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.DEVICEIDLE_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.MIUI_IDLE_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.MIUI_STANDBY_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.NO_CORE_SYSTEM_APPS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.LEVEL_ULTIMATE_SPECIAL_APPS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.MUSIC_APPS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.BLE_SCAN_BLOCK_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.BLE_SCAN_BLOCK_PARAM);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_BA_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_FROZEN_STATUS);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_HOT_FEEDBACK);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_NETWORK_FEEDBACK);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_BG_LOCATION_DELAY_HOT);
            addOptGlobalFeature(context, cvsList, data, CloudUpdateUtils.FEATURE_BG_KILL_DELAY_HOT);

            CloudUpdateUtils.setCloudFeatureRule(context, cvsList);

            ret = true;
        } catch (JSONException e) {
            Log.e(TAG, "parseResult", e);
        } catch (Exception e) {
            Log.e(TAG, "parseResult", e);
        }

        return ret;
    }
}

