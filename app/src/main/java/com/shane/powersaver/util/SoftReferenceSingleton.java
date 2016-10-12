
package com.shane.powersaver.util;

import java.lang.ref.SoftReference;

/**
 * A helper class to enable a soft referenced singleton.
 */
public abstract class SoftReferenceSingleton<T> {
    private SoftReference<T> mInstance = null;

    protected abstract T createInstance();

    /**
     * Gets or create the soft referenced singleton instance.
     * @return The instance.
     */
    public final T get() {
        synchronized (this) {
            T t;
            if (mInstance == null || (t = mInstance.get()) == null) {
                t = createInstance();
                mInstance = new SoftReference<T>(t);
            }
            return t;
        }
    }
}
