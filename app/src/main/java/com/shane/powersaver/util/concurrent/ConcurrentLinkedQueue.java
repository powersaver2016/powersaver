package com.shane.powersaver.util.concurrent;

import java.util.Iterator;

/**
 * Concurrent Linked Queue implements for Queue.
 */
public class ConcurrentLinkedQueue<T> implements Queue<T> {

    private final java.util.concurrent.ConcurrentLinkedQueue<T> mQueue;

    /**
     * Constructs a new ConcurrentLinkedQueue instance.
     */
    public ConcurrentLinkedQueue() {
        mQueue = new java.util.concurrent.ConcurrentLinkedQueue<T>();
    }

    @Override
    public boolean put(T value) {
        return mQueue.offer(value);
    }

    @Override
    public T get() {
        return mQueue.poll();
    }

    @Override
    public boolean remove(T value) {
        return mQueue.remove(value);
    }

    @Override
    public int remove(Predicate<T> predicate) {
        int ret = 0;
        for (Iterator<T> iterator = mQueue.iterator(); iterator.hasNext();) {
            if (predicate.apply(iterator.next())) {
                iterator.remove();
                ++ret;
            }
        }
        return ret;
    }

    @Override
    public int clear() {
        int ret = mQueue.size();
        mQueue.clear();
        return ret;
    }

    @Override
    public boolean isEmpty() {
        return mQueue.isEmpty();
    }

    @Override
    public int getCapacity() {
        return -1;
    }
}
