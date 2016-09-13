package com.shane.powersaver.cloudcontrol;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PowerKeeperCloudControlApp implements Parcelable {
    private static final String TAG = PowerKeeperCloudControlApp.class.getSimpleName();
    public static final String APP_LIST = "app_list";
    public static final String ADDED = "added";
    public static final String GROUP_ID = "group_id";
    public static final String APP_NAME = "app_name";
    public static final String ACTION_LIST = "action_list";
    public static final String ACTION_KEY = "action_key";
    public static final String ACTION_VALUE = "action_value";

    public boolean added;
    public int groupId;
    public String appName;
    public Bundle action;

    public PowerKeeperCloudControlApp(boolean added, int groupId, String appName, Bundle action) {
        this.added = added;
        this.groupId = groupId;
        this.appName = appName;
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(added? 1 : 0);
        dest.writeInt(groupId);
        dest.writeString(appName);
        dest.writeBundle(action);
    }

    public PowerKeeperCloudControlApp (Parcel in) {
        this.added = in.readInt() > 0? true: false;
        this.groupId = in.readInt();
        this.appName = in.readString();
        this.action = in.readBundle();
    }

    public JSONObject toJsonObject () {
        JSONObject json = new JSONObject();
        try {
            json.put(ADDED, String.valueOf(added));
            json.put(GROUP_ID, String.valueOf(groupId));
            json.put(APP_NAME, appName);
            JSONArray arr = new JSONArray();
            for (String key: action.keySet()) {
                JSONObject tmp = new JSONObject();
                tmp.put(ACTION_KEY, key);
                tmp.put(ACTION_VALUE, action.get(key));
                arr.put(tmp);
            }
            json.put(ACTION_LIST, arr);
        } catch (JSONException e) {
            json = null;
        }
        return json;
    }

    public static PowerKeeperCloudControlApp parseFromJson (final JSONObject obj) {
        PowerKeeperCloudControlApp app;
        try {
            boolean added = obj.getBoolean(ADDED);
            int groupId = obj.getInt(GROUP_ID);
            String appName = obj.getString(APP_NAME);
            Bundle action = new Bundle();
            JSONArray arr = obj.getJSONArray(ACTION_LIST);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject tmp = arr.optJSONObject(i);
                action.putString(tmp.getString(ACTION_KEY), tmp.getString(ACTION_VALUE));
            }
            app = new PowerKeeperCloudControlApp(added, groupId, appName, action);
        } catch (JSONException e) {
            Log.e(TAG, "parseFromJson", e);
            app = null;
        } catch (Exception e) {
            Log.e(TAG, "parseFromJson", e);
            app = null;
        }

        return app;
    }

    public static final Creator<PowerKeeperCloudControlApp> CREATOR =
            new Creator<PowerKeeperCloudControlApp>() {
                public PowerKeeperCloudControlApp createFromParcel(Parcel in) {
                    return new PowerKeeperCloudControlApp(in);
                }

                public PowerKeeperCloudControlApp[] newArray(int size) {
                    return new PowerKeeperCloudControlApp[size];
                }
            };

}
