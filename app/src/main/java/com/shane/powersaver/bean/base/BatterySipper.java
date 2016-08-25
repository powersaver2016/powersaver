package com.shane.powersaver.bean.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shane on 16-8-24.
 */
public class BatterySipper extends StatElement implements Comparable<BatterySipper>, Serializable {
    /**
     * the tag for logging
     */
    private static transient final String TAG = BatterySipper.class.getSimpleName();
    public static transient double sBatteryCapacity;
    public static transient final int IDLE = 0;
    public static transient final int CELL = 1;
    public static transient final int PHONE = 2;
    public static transient final int WIFI = 3;
    public static transient final int BLUETOOTH = 4;
    public static transient final int SCREEN = 5;
    public static transient final int APP = 6;
    public static transient final int FLASHLIGHT = 7;
    public static transient final int USER = 8;
    public static transient final int CAMERA = 9;
    public static transient final int OTHER = 10;
    public static transient final int UNACCOUNTED = 11;
    public static transient final int OVERCOUNTED = 12;

    public double totalPowerMah;

    /**
     * the name of the object
     */
    public String name;
    public int drainType;

    public static HashMap<String, Integer> sDrainTypeMap = new HashMap<String, Integer>();

    static {
        sDrainTypeMap.put("IDLE", IDLE);
        sDrainTypeMap.put("CELL", CELL);
        sDrainTypeMap.put("PHONE", PHONE);
        sDrainTypeMap.put("WIFI", WIFI);
        sDrainTypeMap.put("BLUETOOTH", BLUETOOTH);
        sDrainTypeMap.put("FLASHLIGHT", FLASHLIGHT);
        sDrainTypeMap.put("SCREEN", SCREEN);
        sDrainTypeMap.put("UNACCOUNTED", UNACCOUNTED);
        sDrainTypeMap.put("OVERCOUNTED", OVERCOUNTED);
        sDrainTypeMap.put("APP", APP);
        sDrainTypeMap.put("USER", USER);
        sDrainTypeMap.put("CAMERA", CAMERA);
    }

    public BatterySipper(String strName, double totalPower) {
        name = strName;
        totalPowerMah = totalPower;
        if (sDrainTypeMap.containsKey(name)) {
            drainType = sDrainTypeMap.get(name);
        } else {
            drainType = -1;
        }

    }

    public BatterySipper clone() {
        BatterySipper clone = new BatterySipper(name, totalPowerMah);

        return clone;
    }

    /**
     * Returns a speaking name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns data as displayable string
     *
     * @return the data
     */
    public String getData(long totalTime) {
        StringBuilder sb = new StringBuilder();
        if (this.getuid() > Constants.FIRST_APPLICATION_UID) {
            sb.append(this.getPackageName());
        } else {
            sb.append(name);
        }

        sb.append("\t\t");
        sb.append("uid(" + getuid() + ")\t\t");

        sb.append(formatRatio((long) totalPowerMah, (long) sBatteryCapacity));
        return sb.toString();
    }

    public double getRatio() {
        return 100*totalPowerMah/sBatteryCapacity;
    }

    public int compareTo(BatterySipper o) {
        // we want to sort in descending order
        return ((int) (o.totalPowerMah - this.totalPowerMah));
    }

    public String getPackageName() {
        if (m_uidInfo != null) {
            return m_uidInfo.getNamePackage();
        } else {
            return "";
        }
    }
}
