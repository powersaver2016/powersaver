/**
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */

package com.shane.powersaver.util;

import com.shane.powersaver.util.concurrent.ConcurrentRingQueue;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Pools for cache.<p>
 * 
 * <h3>Usage</h3>
 * <pre>
 *   Pools.SimplePool&lt;Object&gt; objectPool = Pools.createSimplePool(new Pools.Manager&lt;Object&gt;() {
 *       &#64;Override
 *       public Object createInstance() {
 *           return new Object();
 *       };
 *   }, 4);
 * </pre>
 * Then,
 * <pre>Object o = objectPool.acquire();</pre>
 * <pre>objectPool.release(o);</pre>
 */
public final class Pools {

    /**
     * The interface of Pool to store objects.
     */
    public interface Pool<T> {

        /**
         * Acquires an instance of object.
         * <p/>
         * If there is no instance in pool, it will be create one by calling {@link Pools.Manager#createInstance()}.
         * @return The instance to acquire.
         */
        public T acquire();

        /**
         * Releases an instance to pool.
         * @param element The instance to release.
         */
        public void release(T element);

        /**
         * Closes this pool.
         */
        public void close();

        /**
         * Gets the size of this pool.
         * @return The size of this pool.
         */
        public int getSize();
    }

    /**
     * Manager class to create object instance.
     */
    public static abstract class Manager<T> {
        /** Creates new instance of the pool element. */
        abstract public T createInstance();

        /** Calls when a new instance is acquired. */
        public void onAcquire(T element) {
        }

        /** Calls when a new instance is release. */
        public void onRelease(T element) {
        }

        /** Calls when released instance is to be destroied. */
        public void onDestroy(T element) {
        }
    }

    /**
     * The interface of instance holder.
     */
    private interface IInstanceHolder<T> {

        /**
         * The {@link Class} of instance.
         * @return The instance class in this pool.
         */
        Class<T> getElementClass();

        /**
         * @return The total size of this instance holder.
         */
        int getSize();

        /**
         * Resizes the pool when a new {@link miui.util.Pools.Pool} is created or a {@link miui.util.Pools.Pool} is closed.
         * @param size the size of created/closed {@link miui.util.Pools.Pool}.
         *             Negative value means closed and positive value means created.
         */
        void resize(int size);

        /**
         * Gets a new instance from holder.
         * @return a new instance in holder or null means no instance in holder.
         */
        T get();

        /**
         * Puts a instance into holder.
         * @param element The instance to put in holder.
         * @return {@code True} means the instance is put into holder successfully. {@code False} means the holder is full.
         */
        boolean put(T element);
    }

    /**
     * Simple instance holder.
     */
    private static class InstanceHolder<T> implements IInstanceHolder<T> {
        private final Class<T> mClazz;
        private final ConcurrentRingQueue<T> mQueue;

        /*package*/ InstanceHolder(Class<T> clazz, int size) {
            mClazz = clazz;
            mQueue = new ConcurrentRingQueue<T>(size, false, true);
        }

        @Override
        public Class<T> getElementClass() {
            return mClazz;
        }

        @Override
        public int getSize() {
            return mQueue.getCapacity();
        }

        @Override
        public synchronized void resize(int size) {
            size += mQueue.getCapacity();
            if (size <= 0) {
                synchronized (mInstanceHolderMap) {
                    mInstanceHolderMap.remove(getElementClass());
                }
                return;
            }

            if (size > 0) {
                mQueue.increaseCapacity(size);
            } else {
                mQueue.decreaseCapacity(-size);
            }
        }

        @Override
        public T get() {
            return mQueue.get();
        }

        @Override
        public boolean put(T element) {
            return mQueue.put(element);
        }
    }

    /**
     * Soft referenced instance holder.
     */
    private static class SoftReferenceInstanceHolder<T> implements IInstanceHolder<T> {
        private final Class<T> mClazz;
        private volatile SoftReference<T>[] mElements;
        private volatile int mIndex;
        private volatile int mSize;

        /*package*/ SoftReferenceInstanceHolder(Class<T> clazz, int size) {
            mClazz = clazz;
            mSize = size;
            @SuppressWarnings("unchecked") SoftReference<T>[] array
                    = (SoftReference<T>[]) new SoftReference[size];
            mElements = array;
            mIndex = 0;
        }

        @Override
        public Class<T> getElementClass() {
            return mClazz;
        }

        @Override
        public int getSize() {
            return mSize;
        }

        @Override
        public synchronized void resize(int size) {
            size += mSize;
            if (size <= 0) {
                synchronized (mSoftReferenceInstanceHolderMap) {
                    mSoftReferenceInstanceHolderMap.remove(getElementClass());
                }
                return;
            }

            mSize = size;
            SoftReference<T>[] elements = mElements;
            int index = mIndex;
            if (size > elements.length) {
                @SuppressWarnings("unchecked") SoftReference<T>[] newElements
                        = (SoftReference<T>[]) new SoftReference[size];
                System.arraycopy(elements, 0, newElements, 0, index);
                mElements = newElements;
            }
        }

        @Override
        public synchronized T get() {
            int index = mIndex;
            SoftReference<T>[] elements = mElements;
            while (index != 0) {
                index--;
                if (elements[index] != null) {
                    T element = elements[index].get();
                    elements[index] = null;

                    if (element != null) {
                        mIndex = index;
                        return element;
                    }
                }
            }

            return null;
        }

        @Override
        public synchronized boolean put(T element) {
            int index = mIndex;
            SoftReference<T>[] elements = mElements;

            if (index >= mSize) {
                for (int i = 0; i < index; ++i) {
                    if (elements[i] == null || elements[i].get() == null) {
                        elements[i] = new SoftReference<T>(element);
                        return true;
                    }
                }
                return false;
            }

            elements[index] = new SoftReference<T>(element);
            mIndex = index + 1;
            return true;
        }
    }

    /** Simple instance holder map. */
    private final static HashMap<Class<?>, InstanceHolder<?>> mInstanceHolderMap = new HashMap<Class<?>, InstanceHolder<?>>();

    /** Soft referenced instance holder map. */
    private final static HashMap<Class<?>, SoftReferenceInstanceHolder<?>> mSoftReferenceInstanceHolderMap =
            new HashMap<Class<?>, SoftReferenceInstanceHolder<?>>();

    /** Soft referenced string builder pool. */
    private final static Pool<StringBuilder> mStringBuilderPool = createSoftReferencePool(
            new Manager<StringBuilder>() {
                @Override
                public StringBuilder createInstance() {
                    return new StringBuilder();
                }

                @Override
                public void onRelease(StringBuilder element) {
                    element.setLength(0);
                }
            }, 4);

    /** Gets the static string builder pool. */
    public static Pool<StringBuilder> getStringBuilderPool() {
        return mStringBuilderPool;
    }

    /**
     * Called when a simple pool created.
     * @param clazz The {@link Class} of the elements.
     * @param size The size of the simple pool.
     * @return The instance holder to store the element in pool.
     */
    /*package*/ static <T> InstanceHolder<T> onPoolCreate(Class<T> clazz, int size) {
        synchronized (mInstanceHolderMap) {
            @SuppressWarnings("unchecked") InstanceHolder<T> holder
                    = (InstanceHolder<T>) mInstanceHolderMap.get(clazz);
            if (holder == null) {
                holder = new InstanceHolder<T>(clazz, size);
                mInstanceHolderMap.put(clazz, holder);
            } else {
                holder.resize(size);
            }
            return holder;
        }
    }

    /**
     * Called when a simple pool closed.
     * @param holder The instance holder to store the element in pool.
     * @param size The size of the closed simple pool.
     */
    /*package*/ static <T> void onPoolClose(InstanceHolder<T> holder, int size) {
        synchronized (mInstanceHolderMap) {
            holder.resize(-size);
        }
    }

    /**
     * Called when a soft referenced pool created.
     * @param clazz The {@link Class} of the elements.
     * @param size The size of the simple pool.
     * @return The instance holder to store the element in pool.
     */
    /*package*/ static <T> SoftReferenceInstanceHolder<T> onSoftReferencePoolCreate(Class<T> clazz, int size) {
        synchronized (mSoftReferenceInstanceHolderMap) {
            @SuppressWarnings("unchecked") SoftReferenceInstanceHolder<T> holder
                    = (SoftReferenceInstanceHolder<T>) mSoftReferenceInstanceHolderMap.get(clazz);
            if (holder == null) {
                holder = new SoftReferenceInstanceHolder<T>(clazz, size);
                mSoftReferenceInstanceHolderMap.put(clazz, holder);
            } else {
                holder.resize(size);
            }
            return holder;
        }
    }

    /**
     * Called when a soft referenced pool closed.
     * @param holder The instance holder to store the element in pool.
     * @param size The size of the closed simple pool.
     */
    /*package*/ static <T> void onSoftReferencePoolClose(SoftReferenceInstanceHolder<T> holder, int size) {
        synchronized (mSoftReferenceInstanceHolderMap) {
            holder.resize(-size);
        }
    }

    /** Abstract class to implement {@link miui.util.Pools.Pool}. */
    /*package*/ static abstract class BasePool<T> implements Pool<T> {
        private final Manager<T> mManager;
        private final int mSize;
        private IInstanceHolder<T> mInstanceHolder;
        private final Object mFinalizeGuardian = new Object() {
            @Override
            protected void finalize() throws Throwable {
                try {
                    close();
                } finally {
                    super.finalize();
                }
            }
        };

        public BasePool(Manager<T> manager, int size) {
            if (manager == null || size < 1) {
                // Remove usage warning of mFinalzeGuardian
                mSize = mFinalizeGuardian.hashCode();
                throw new IllegalArgumentException("manager cannot be null and size cannot less then 1");
            }

            mManager = manager;
            mSize = size;
            T element = mManager.createInstance();
            if (element == null) {
                throw new IllegalStateException("manager create instance cannot return null");
            }

            @SuppressWarnings("unchecked") Class<T> clazz = (Class<T>) element.getClass();
            mInstanceHolder = createInstanceHolder(clazz, size);
            doRelease(element);
        }

        /*package*/ abstract IInstanceHolder<T> createInstanceHolder(Class<T> clazz, int size);
        /*package*/ abstract void destroyInstanceHolder(IInstanceHolder<T> instanceHolder, int size);

        protected final T doAcquire() {
            if (mInstanceHolder == null) {
                throw new IllegalStateException("Cannot acquire object after close()");
            }

            T element = mInstanceHolder.get();
            if (element == null) {
                element = mManager.createInstance();
                if (element == null) {
                    throw new IllegalStateException("manager create instance cannot return null");
                }
            }

            mManager.onAcquire(element);

            return element;
        }

        protected final void doRelease(T element) {
            if (mInstanceHolder == null) {
                throw new IllegalStateException("Cannot release object after close()");
            }

            if (element == null) {
                return;
            }

            mManager.onRelease(element);

            if (!mInstanceHolder.put(element)) {
                mManager.onDestroy(element);
            }
        }

        @Override
        public T acquire() {
            return doAcquire();
        }

        @Override
        public void release(T element) {
            doRelease(element);
        }

        @Override
        public void close() {
            if (mInstanceHolder != null) {
                destroyInstanceHolder(mInstanceHolder, mSize);
                mInstanceHolder = null;
            }
        }

        @Override
        public int getSize() {
            return (mInstanceHolder == null) ? 0 : mSize;
        }
    }

    /**
     * Creates a simple pool instance.<p>
     * <h3>Notes</h3>
     * Simple pool has synchronize mechanism. So, it is thread safe.
     * @param manager The manager of the instance.
     * @param size The maximum size of the pool.
     * @return A new simple pool instance.
     *
     * @see #createSoftReferencePool(miui.util.Pools.Manager, int)
     */
    public static <T> SimplePool<T> createSimplePool(Manager<T> manager, int size) {
        return new SimplePool<T>(manager, size);
    }

    /**
     * Creates a simple soft referenced pool instance.<p>
     * <h3>Notes</h3>
     * Simple pool has synchronize mechanism. So, it is thread safe.
     * @param manager The manager of the instance.
     * @param size The maximum size of the pool.
     * @return A new simple soft referenced pool instance.
     *
     * @see #createSoftReferencePool(miui.util.Pools.Manager, int)
     */
    public static <T> SoftReferencePool<T> createSoftReferencePool(Manager<T> manager, int size) {
        return new SoftReferencePool<T>(manager, size);
    }

    /**
     * A simple pool implementation.
     * @see miui.util.Pools.Pool
     */
    public static class SimplePool<T> extends BasePool<T> {

        /** Constructs a new pool instance. */
        /*package*/ SimplePool(Manager<T> manager, int size) {
            super(manager, size);
        }

        @Override
        /*package*/ final IInstanceHolder<T> createInstanceHolder(Class<T> clazz, int size) {
            return onPoolCreate(clazz, size);
        }

        @Override
        /*package*/ final void destroyInstanceHolder(IInstanceHolder<T> instanceHolder, int size) {
            onPoolClose((InstanceHolder<T>) instanceHolder, size);
        }
    }

    /**
     * A simple soft referenced pool implementation.
     * @see miui.util.Pools.Pool
     */
    public static class SoftReferencePool<T> extends BasePool<T> {

        /** Constructs a new pool instance. */
        /*package*/ SoftReferencePool(Manager<T> manager, int size) {
            super(manager, size);
        }

        @Override
        /*package*/ final IInstanceHolder<T> createInstanceHolder(Class<T> clazz, int size) {
            return onSoftReferencePoolCreate(clazz, size);
        }

        @Override
        /*package*/ final void destroyInstanceHolder(IInstanceHolder<T> instanceHolder, int size) {
            onSoftReferencePoolClose(
                    (SoftReferenceInstanceHolder<T>) instanceHolder, size);
        }
    }
}
