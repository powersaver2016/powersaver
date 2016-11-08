package com.shane.powersaver.bean.kernel;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class BatteryStatsTypes {
    /**
     * A constant indicating a partial wake lock timer.
     */
    public static final int WAKE_TYPE_PARTIAL = 0;

    /**
     * A constant indicating a full wake lock timer.
     */
    public static final int WAKE_TYPE_FULL = 1;

    /**
     * A constant indicating a window wake lock timer.
     */
    public static final int WAKE_TYPE_WINDOW = 2;

    /**
     * Constants for data connection type
     */
    public static final int DATA_CONNECTION_NONE = 0;

    /**
     * Constants for signal strength
     */
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;

    /**
     * Constants for screen brightness
     */

    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;

    /**
     * Constants for BT and Wifi state
     */

    public static final int CONTROLLER_IDLE_TIME = 0;
    public static final int CONTROLLER_RX_TIME = 1;
    public static final int CONTROLLER_TX_TIME = 2;
    public static final int CONTROLLER_ENERGY = 3;

    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = NETWORK_WIFI_TX_DATA + 1;

    /**
     * Enum of valid wakelock types
     */
    public static final boolean assertValidWakeType(int iWakeType) {
        boolean ret = false;
        switch (iWakeType) {
            case WAKE_TYPE_PARTIAL:
                ret = true;
                break;
            case WAKE_TYPE_FULL:
                ret = true;
                break;
            case WAKE_TYPE_WINDOW:
                ret = true;
                break;
            default:
                ret = false;
                break;

        }
        return ret;
    }

    /**
     * Include all of the data in the stats, including previously saved data.
     */
    public static final int STATS_SINCE_CHARGED = 0;

    /**
     * Include only the last run in the stats.
     */
    public static final int STATS_LAST = 1;

    /**
     * Include only the current run in the stats.
     */
    public static final int STATS_CURRENT = 2;

    /**
     * Include only the run since the last time the device was unplugged in the stats.
     */
    public static final int STATS_SINCE_UNPLUGGED = 3;

    /**
     * Enum of valid stat types
     */
    public static final boolean assertValidStatType(int iStatType) {
        boolean ret = false;
        switch (iStatType) {
            case STATS_SINCE_CHARGED:
                ret = true;
                break;
            case STATS_LAST:
                ret = true;
                break;
            case STATS_CURRENT:
                ret = true;
                break;
            case STATS_SINCE_UNPLUGGED:
                ret = true;
                break;

            default:
                ret = false;
                break;

        }
        return ret;
    }

    /**
     * Enum of valid stat types
     */
    public static final boolean assertValidWakelockPctRef(int iPctType) {
        boolean ret = false;
        switch (iPctType) {
            case 0:    // % of battery time
                ret = true;
                break;
            case 1:    // % of awake time
                ret = true;
                break;
            case 2:        // % of time awake - time with screen on
                ret = true;
                break;
            default:
                ret = false;
                break;

        }
        return ret;
    }
}
