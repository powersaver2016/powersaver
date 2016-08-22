/**
 *
 */
package com.shane.powersaver.bean.kernel;

/**
 * @author sven
 */
public class BatteryInfoUnavailableException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BatteryInfoUnavailableException() {

    }

    public BatteryInfoUnavailableException(String msg) {
        super(msg);
    }
}
