/**
 *
 */
package com.shane.powersaver.bean.kernel;

/**
 *
 * @author shane（https://github.com/lxxgreat）
 * @version 1.0
 * @created 2016-08-07
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
