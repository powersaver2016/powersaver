package com.shane.java.utils;

public final class ObjectReference<T> {
    private final T mObject;

    public ObjectReference(T object) {
        mObject = object;
    }

    public final T get() {
        return mObject;
    }
}