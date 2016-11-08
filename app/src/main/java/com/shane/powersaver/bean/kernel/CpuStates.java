package com.shane.powersaver.bean.kernel;


/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;


import android.util.Log;
import android.os.SystemClock;

import com.shane.powersaver.bean.base.State;

public class CpuStates {
    private static final String TAG = "CpuStates";

    // path to sysfs
    public static final String TIME_IN_STATE_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state";
    public static final String VERSION_PATH = "/proc/version";


    public static ArrayList<State> getTimesInStates() {
        ArrayList<State> states = new ArrayList<State>();
        long totalTime = 0;
        try {
            // create a buffered reader to read in the time-in-states log
            InputStream is = new FileInputStream(TIME_IN_STATE_PATH);
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);

            String line;
            while ((line = br.readLine()) != null) {
                // split open line and convert to Integers
                String[] nums = line.split(" ");

                // duration x 10 to store ms
                State myState = new State(Integer.parseInt(nums[0]), Long.parseLong(nums[1]) * 10);
                totalTime += myState.m_duration;
                states.add(myState);
            }

            is.close();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        // add in sleep state
        long sleepTime = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis();
        states.add(new State(0, sleepTime));
        totalTime += sleepTime;

        // store the total time in order to be able to calculate ratio
        for (int i = 0; i < states.size(); i++) {
            states.get(i).setTotal(totalTime);
        }

        return states;
    }
}