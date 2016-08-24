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

    public double totalPowerMah;

    /**
     * the name of the object
     */
    public String name;
    public DrainType drainType;

    public enum DrainType {
        IDLE,
        CELL,
        PHONE,
        WIFI,
        BLUETOOTH,
        FLASHLIGHT,
        SCREEN,
        APP,
        USER,
        UNACCOUNTED,
        OVERCOUNTED,
        CAMERA
    }

    public static HashMap<String, DrainType> sDrainTypeMap = new HashMap<String, DrainType>();

    static {
        sDrainTypeMap.put("IDLE", DrainType.IDLE);
        sDrainTypeMap.put("CELL", DrainType.CELL);
        sDrainTypeMap.put("PHONE", DrainType.PHONE);
        sDrainTypeMap.put("WIFI", DrainType.WIFI);
        sDrainTypeMap.put("BLUETOOTH", DrainType.BLUETOOTH);
        sDrainTypeMap.put("FLASHLIGHT", DrainType.FLASHLIGHT);
        sDrainTypeMap.put("SCREEN", DrainType.SCREEN);
        sDrainTypeMap.put("UNACCOUNTED", DrainType.UNACCOUNTED);
        sDrainTypeMap.put("OVERCOUNTED", DrainType.OVERCOUNTED);
        sDrainTypeMap.put("CAMERA", DrainType.CAMERA);
    }

    public BatterySipper(String strName, double totalPower) {
        name = strName;
        totalPowerMah = totalPower;
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

        sb.append(this.formatRatio((long) totalPowerMah, (long) sBatteryCapacity));
        return sb.toString();
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
