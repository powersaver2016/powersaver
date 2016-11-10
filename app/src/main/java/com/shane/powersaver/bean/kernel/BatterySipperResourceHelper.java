package com.shane.powersaver.bean.kernel;

import android.content.Context;
import android.text.TextUtils;

import com.shane.powersaver.AppContext;
import com.shane.powersaver.R;
import com.shane.powersaver.bean.base.BatterySipper;
import com.shane.powersaver.bean.base.UidNameResolver;

/**
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */
public class BatterySipperResourceHelper {
    private static final String TAG = BatterySipperResourceHelper.class.getSimpleName();

    public static final String UNKNOWN_TYPE = "Unknown";

    public static int getIconId(BatterySipper sipper) {
        switch (sipper.drainType) {
            case BatterySipper.IDLE:
                return R.drawable.power_consume_idle;
            case BatterySipper.CELL:
                return R.drawable.power_consume_signal;
            case BatterySipper.PHONE:
                return R.drawable.power_consume_call;
            case BatterySipper.WIFI:
                return R.drawable.power_consume_wifi;
            case BatterySipper.BLUETOOTH:
                return R.drawable.power_consume_bluetooth;
            case BatterySipper.SCREEN:
                return R.drawable.power_consume_screen;
            case BatterySipper.OTHER:
                return R.drawable.power_consume_other;
            default:
                break;
        }
        if (sipper.getuid() == 0) {
            return R.drawable.ic_power_system;
        }
        return 0;
    }

    public static String getDescription(Context context, int drainType) {
        switch (drainType) {
            case BatterySipper.IDLE:
                return context.getString(R.string.battery_desc_standby);
            case BatterySipper.CELL:
                return context.getString(R.string.battery_desc_radio);
            case BatterySipper.PHONE:
                return context.getString(R.string.battery_desc_voice);
            case BatterySipper.WIFI:
                return context.getString(R.string.battery_desc_wifi);
            case BatterySipper.BLUETOOTH:
                return context.getString(R.string.battery_desc_bluetooth);
            case BatterySipper.SCREEN:
                return context.getString(R.string.battery_desc_display);
            case BatterySipper.APP:
                return context.getString(R.string.battery_desc_apps);
            case BatterySipper.OTHER:
                return context.getString(R.string.battery_desc_other_apps);
            default:
                return "";
        }
    }

    public static String getDisplayName(Context context, BatterySipper sipper) {
        switch (sipper.drainType) {
            case BatterySipper.IDLE:
                return context.getString(R.string.power_idle);
            case BatterySipper.CELL:
                return context.getString(R.string.power_cell);
            case BatterySipper.PHONE:
                return context.getString(R.string.power_phone);
            case BatterySipper.WIFI:
                return context.getString(R.string.power_wifi);
            case BatterySipper.BLUETOOTH:
                return context.getString(R.string.power_bluetooth);
            case BatterySipper.SCREEN:
                return context.getString(R.string.power_screen);
            case BatterySipper.APP:
                return getAppDisplayName(context, sipper);
            case BatterySipper.OTHER:
                return context.getString(R.string.power_consume_other);
            default:
                break;
        }
        return UNKNOWN_TYPE;
    }

    private static String getAppDisplayName(Context context, BatterySipper sipper) {
        switch (sipper.getuid()) {
            case 0:
                return getString(R.string.process_kernel_label);
            case 1013:
                return getString(R.string.process_mediaserver_label);
            case 1000:
                return getString(R.string.process_system_label);
        }

        String label = UidNameResolver.getInstance(context).getLabel(sipper.getPackageName());
        if (!TextUtils.isEmpty(label)) {
            return label;
        }
        return "";
    }

    private static String getString(int resId) {
        return AppContext.getResString(resId);
    }
}
