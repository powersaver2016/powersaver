package com.shane.powersaver;

import android.content.Context;

import com.shane.powersaver.powerchecker.IPowerChecker;
import com.shane.powersaver.powerchecker.PowerCheckerService;

/**
 * Created by shane on 16-9-13.
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
