package com.shane.powersaver.powerchecker;

import android.content.Context;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.String;

import com.shane.powersaver.AppContext;

public class PowerCheckerService extends IPowerChecker.Stub {
    static final String TAG = "PowerCheckerService";
    static final boolean DEBUG = AppContext.DEBUG;


    private static PowerCheckerService sInstance;

    private Context mContext;
    private boolean mBootCompleted;


    private static final int MSG_START_POWER_CHECK_CYCLE = 1;
    private static final int MSG_POWER_CHECKE_CYCLE_DONE = 2;

    private static final int WAKELOCK_INTERVAL = 5*1000; // 5s -> 10000 per 14h
    private static final int WAKEUP_INTERVAL = 5*60*1000; // 5min -> xmsf interval
    private static final int POWER_CHECK_CYCLE_INTERVAL = 30*60*1000; // 30min

    private final Object mLock = new Object();


    private PowerCheckerService(Context context) {
        mContext = context;

    }

    public static PowerCheckerService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PowerCheckerService(context);
        }
        return sInstance;
    }


    private void dumpService(FileDescriptor fd, PrintWriter pw, String[] args) {


    }

    @Override
    public void startSchedulePowerChecker(boolean immediately) {

    }
}
