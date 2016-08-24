package com.shane.powersaver.bean.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by shane on 16-8-24.
 */
public class BatterySipper extends StatElement implements Comparable<BatterySipper>, Serializable {
    /**
     * the tag for logging
     */
    private static transient final String TAG = BatterySipper.class.getSimpleName();

    public double totalPowerMah;

    /**
     * the name of the object
     */
    public String name;

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
        return String.valueOf(totalPowerMah);
    }

    public int compareTo(BatterySipper o) {
        // we want to sort in descending order
        return ((int) (o.totalPowerMah - this.totalPowerMah));
    }
}
