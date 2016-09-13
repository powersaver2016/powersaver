package com.shane.powersaver.powerchecker;

interface IPowerChecker {
    //------------------------------------------------------------------------
    /** api used by system user only */
    //------------------------------------------------------------------------
    void startSchedulePowerChecker(boolean immediately);

}