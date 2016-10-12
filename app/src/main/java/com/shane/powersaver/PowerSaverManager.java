package com.shane.powersaver;

import android.content.Context;

import com.shane.powersaver.powerchecker.IPowerChecker;
import com.shane.powersaver.powerchecker.PowerCheckerService;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-09-13
 */
public class PowerSaverManager extends IPowerSaver.Stub {
    private static PowerSaverManager sInstance;
    private Context mContext;
    private IPowerChecker mPowerChecker;

    private PowerSaverManager(Context context) {
        mContext = context;
        //instantiate PowerCheckerService
        mPowerChecker = PowerCheckerService.asInterface(PowerCheckerService.getInstance(mContext).asBinder());
    }

    public static synchronized PowerSaverManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PowerSaverManager(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public IPowerChecker getPowerCheckerService() {
        return mPowerChecker;
    }
}
