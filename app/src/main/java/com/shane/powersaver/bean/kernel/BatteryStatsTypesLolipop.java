package com.shane.powersaver.bean.kernel;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class BatteryStatsTypesLolipop extends BatteryStatsTypes {

    /**
     * Include all of the data in the stats, including previously saved data
     */
    public static final int STATS_SINCE_CHARGED = 0;

    /**
     * Include only the current run in the stats
     */
    public static final int STATS_CURRENT = 1;

    /**
     * Include only the run since the last time the device was unplugged in the stats
     */
    public static final int STATS_SINCE_UNPLUGGED = 2;

}
