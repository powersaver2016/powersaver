package com.shane.powersaver.cloudcontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shane.powersaver.PowerSaverManager;

/**
 * Created by shane on 16-9-13.
 */
public class CloudUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return PowerSaverManager.getInstance(this).asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
