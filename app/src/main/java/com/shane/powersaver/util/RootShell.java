package com.shane.powersaver.util;

import java.util.ArrayList;
import java.util.List;


import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

/**
 * RootShell
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-11 18:59
 *
 */
public class RootShell {
    static RootShell sInstance = null;
    static Shell sShell = null;

    private RootShell() {
    }

    public static RootShell getInstance() {
        if (sInstance == null) {
            sInstance = new RootShell();
            try {
                sShell = RootTools.getShell(true);
            } catch (Exception e) {
                sShell = null;
            }
        }

        return sInstance;
    }


    public synchronized List<String> run(String command) {
        final List<String> res = new ArrayList<String>();

        if (!RootTools.isRootAvailable()) {
            return res;
        }

        if (sShell == null) {
            // reopen if for whatever reason the shell got closed
            RootShell.getInstance();
        }

        CommandCapture shellCommand = new CommandCapture(0, command) {
            @Override
            public void output(int id, String line) {
                res.add(line);
            }
        };
        try {
            RootTools.getShell(true).add(shellCommand);

            // we need to make this synchronous
            while (!shellCommand.isFinished()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {

        }

        return res;

    }

    public boolean phoneRooted() {
        return RootTools.isRootAvailable();
    }

    public boolean hasRootPermissions() {
        return ((sShell != null) && (RootTools.isRootAvailable()));
    }
}
