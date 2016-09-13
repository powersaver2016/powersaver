/**
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */

package com.shane.powersaver.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrent Ring Queue implements for Queue.
 */
public class ConcurrentRingQueue<T> implements Queue<T> {

    private static class Node<T> {

        T element;

        Node<T> next;
    }

    private int mCapacity;

    private final boolean mAllowExtendCapacity;

    private final boolean mAutoReleaseCapacity;

    private final AtomicInteger mReadLock;

    private volatile Node<T> mReadCursor;

    private final AtomicInteger mWriteLock;

    private volatile Node<T> mWriteCursor;

    private volatile int mAdditional;

    /**
     * Constructs a {@link ConcurrentRingQueue} object.
     *
     * @param capacity            the capacity of the Queue, if the capacity is less than 2, it will be set to 2.
     * @param allowExtendCapacity whether the Queue can extend the capacity.
     * @param autoReleaseCapacity whether the Queue release the extended capacity automatically.
     */
    public ConcurrentRingQueue(int capacity, boolean allowExtendCapacity, boolean autoReleaseCapacity) {
        mCapacity = capacity;
        mAllowExtendCapacity = allowExtendCapacity;
        mAutoReleaseCapacity = autoReleaseCapacity;
        mReadLock = new AtomicInteger(0);
        mWriteLock = new AtomicInteger(0);

        mReadCursor = new Node<T>();
        mWriteCursor = mReadCursor;
        Node<T> n = mReadCursor;
        for (int i = 0; i < capacity; ++i) {
            n.next = new Node<T>();
            n = n.next;
        }
        n.next = mReadCursor;
    }

    @Override
    public boolean put(T value) {
        if (value == null) {
            return false;
        }

        for (int lock = mWriteLock.get(); lock != 0 || !mWriteLock.compareAndSet(0, -1); lock = mWriteLock.get()) {
            Thread.yield();
        }

        boolean ret = false;
        Node<T> rc = mReadCursor;
        Node<T> wc = mWriteCursor;
        int additional = mAdditional;
        if (wc.next != rc) {
            wc.element = value;
            if (wc.next.next != rc && mAutoReleaseCapacity && additional > 0) {
                wc.next = wc.next.next;
                mAdditional = additional - 1;
            }
            mWriteCursor = wc.next;
            ret = true;
        } else if (mAllowExtendCapacity || additional < 0) {
            wc.next = new Node<T>();
            wc.next.next = rc;
            wc.element = value;
            mAdditional = additional + 1;
            mWriteCursor = wc.next;
            ret = true;
        }
        mWriteLock.set(0);
        return ret;
    }

    @Override
    public T get() {
        for (int lock = mReadLock.get(); lock != 0 || !mReadLock.compareAndSet(0, -1); lock = mReadLock.get()) {
            Thread.yield();
        }

        T ret = null;
        Node<T> rc = mReadCursor;
        for (Node<T> wc = mWriteCursor; ret == null && rc != wc; rc = rc.next, wc = mWriteCursor) {
            ret = rc.element;
            rc.element = null;
        }
        if (ret != null) {
            mReadCursor = rc;
        }
        mReadLock.set(0);
        return ret;
    }

    @Override
    public boolean remove(T value) {
        if (value == null) {
            return false;
        }

        for (int lock = mReadLock.get(); lock != 0 || !mReadLock.compareAndSet(0, -1); lock = mReadLock.get()) {
            Thread.yield();
        }

        boolean ret = false;
        for (Node<T> s = mReadCursor; s != mWriteCursor; s = s.next) {
            if (value.equals(s.element)) {
                s.element = null;
                ret = true;
                break;
            }
        }
        mReadLock.set(0);
        return ret;
    }

    @Override
    public int remove(Predicate<T> predicate) {
        if (predicate == null) {
            return 0;
        }

        for (int lock = mReadLock.get(); lock != 0 || !mReadLock.compareAndSet(0, -1); lock = mReadLock.get()) {
            Thread.yield();
        }

        int ret = 0;
        try {
            for (Node<T> s = mReadCursor; s != mWriteCursor; s = s.next) {
                if (predicate.apply(s.element)) {
                    s.element = null;
                    ret++;
                }
            }
        } finally {
            mReadLock.set(0);
        }
        return ret;
    }

    @Override
    public int clear() {
        for (int lock = mReadLock.get(); lock != 0 || !mReadLock.compareAndSet(0, -1); lock = mReadLock.get()) {
            Thread.yield();
        }

        int ret = 0;
        Node<T> s;
        for (s = mReadCursor; s != mWriteCursor; s = s.next) {
            s.element = null;
            ret++;
        }

        mReadCursor = s;

        mReadLock.set(0);
        return ret;
    }

    @Override
    public boolean isEmpty() {
        return mWriteCursor == mReadCursor;
    }

    @Override
    public int getCapacity() {
        int additional;
        return ((additional = mAdditional) > 0) ? mCapacity + additional : mCapacity;
    }

    /**
     * Increases the capacity of this queue.
     * <p/>
     * Please note the allowExtendCapacity in {@link #ConcurrentRingQueue(int, boolean, boolean)}  should be {@code false}.
     * @param size the capacity will to increased.
     */
    public void increaseCapacity(int size) {
        if (mAllowExtendCapacity || size <= 0) {
            return;
        }

        for (int lock = mWriteLock.get(); lock != 0 || !mWriteLock.compareAndSet(0, -1); lock = mWriteLock.get()) {
            Thread.yield();
        }

        mAdditional = -size;
        mCapacity += size;

        mWriteLock.set(0);
    }

    /**
     * Decreases the capacity of this queue.
     * <p/>
     * Please not the autoRleaseCapacity in {@link #ConcurrentRingQueue(int, boolean, boolean)} should be {@code true}.
     * @param size the capacity will to decreased.
     */
    public void decreaseCapacity(int size) {
        if (!mAutoReleaseCapacity || size <= 0) {
            return;
        }

        for (int lock = mWriteLock.get(); lock != 0 || !mWriteLock.compareAndSet(0, -1); lock = mWriteLock.get()) {
            Thread.yield();
        }

        mCapacity -= size;
        mAdditional = size;

        mWriteLock.set(0);
    }
}
