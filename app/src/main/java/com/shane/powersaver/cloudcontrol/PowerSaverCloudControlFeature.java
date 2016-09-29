package com.shane.powersaver.cloudcontrol;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PowerSaverCloudControlFeature implements Parcelable {
    private static final String TAG = PowerSaverCloudControlFeature.class.getSimpleName();
    public static final String FEATURE_STATUS = "hide_mode";
    public static final String DEVICEIDLE_STATUS = "idle_mode";
    public static final String MIUI_IDLE_STATUS = "miui_idle";
    public static final String MIUI_STANDBY_STATUS = "miui_standby";
    public static final String NO_CORE_SYSTEM_APPS = "no_core_system_apps";
    public static final String BROADCAST_ALARM_STATUS = "broadcast_alarm";
    public static final String FROZEN_STATUS = "frozen";
    public static final String FEATURE_LIST = "feature_list";
    public static final String ADDED = PowerSaverCloudControlApp.ADDED;
    public static final String GROUP_ID = PowerSaverCloudControlApp.GROUP_ID;
    public static final String ACTION_KEY = PowerSaverCloudControlApp.ACTION_KEY;
    public static final String ACTION_VALUE = PowerSaverCloudControlApp.ACTION_VALUE;

    public boolean added;
    public int groupId;
    public String featureName;
    public String value;

    public PowerSaverCloudControlFeature(boolean added, int groupId, String featureName, String value) {
        this.added = added;
        this.groupId = groupId;
        this.featureName = featureName;
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(added? 1 : 0);
        dest.writeInt(groupId);
        dest.writeString(featureName);
        dest.writeString(value);
    }

    public PowerSaverCloudControlFeature(Parcel in) {
        this.added = in.readInt() > 0? true: false;
        this.groupId = in.readInt();
        this.featureName = in.readString();
        this.value = in.readString();
    }

    public JSONObject toJsonObject () {
        JSONObject json = new JSONObject();
        try {
            json.put(ADDED, String.valueOf(added));
            json.put(GROUP_ID, String.valueOf(groupId));
            json.put(ACTION_KEY, featureName);
            json.put(ACTION_VALUE, value);
        } catch (JSONException e) {
            json = null;
        }
        return json;
    }

    public static PowerSaverCloudControlFeature parseFromJson (JSONObject obj) {
        PowerSaverCloudControlFeature feature;
        try {
            boolean added = obj.getBoolean(ADDED);
            int groupId = obj.getInt(GROUP_ID);
            String featureName = obj.getString(ACTION_KEY);
            String value = obj.getString(ACTION_VALUE);
            feature = new PowerSaverCloudControlFeature(added, groupId, featureName, value);
        } catch (JSONException e) {
            Log.e(TAG, "parseFromJson", e);
            feature = null;
        } catch (Exception e) {
            Log.e(TAG, "parseFromJson", e);
            feature = null;
        }

        return feature;
    }

    public static final Creator<PowerSaverCloudControlFeature> CREATOR
        = new Creator<PowerSaverCloudControlFeature>() {
                public PowerSaverCloudControlFeature createFromParcel(Parcel in) {
                    return new PowerSaverCloudControlFeature(in);
                }

                public PowerSaverCloudControlFeature[] newArray(int size) {
                    return new PowerSaverCloudControlFeature[size];
                }
            };

}
