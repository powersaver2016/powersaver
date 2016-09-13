package com.shane.powersaver;

import com.shane.powersaver.powerchecker.IPowerChecker;

interface IPowerSaver {
    /* get PowerCheckerService interface */
    IPowerChecker getPowerCheckerService();

}
