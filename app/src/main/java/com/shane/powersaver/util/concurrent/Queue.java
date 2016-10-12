package com.shane.powersaver.util.concurrent;

/**
 * The interface of Queue.
 */
public interface Queue<T> {

    /**
     * A Predicate can determine a true or false value for any input of its
     * parameterized type.
     * <p/>
     * Implementors of Predicate which may cause side effects upon evaluation are
     * strongly encouraged to state this fact clearly in their API documentation.
     */
    public interface Predicate<T> {

        /**
         * @return true if the value match this predicate.
         */
        boolean apply(T value);
    }

    /**
     * Put a value into the Queue.
     * @param value the value to put into Queue.
     * @return true if the value is put into Queue successfully. false if the Queue is full or value is null.
     */
    boolean put(T value);

    /**
     * Get a value from the Queue.
     * @return null for Queue is empty or an object instance for value.
     */
    T get();

    /**
     * Removes a value from Queue.
     * @param value the value to be removed.
     * @return the count removed from Queue.
     */
    boolean remove(T value);

    /**
     * Removes the values from Queue.
     * @param predicate the predictor to match the values to be removed.
     * @return the count of the removed values.
     */
    int remove(Predicate<T> predicate);

    /**
     * Clears all values from Queue.
     * @return The count of the removed values.
     */
    int clear();

    /**
     * Checks the Queue is empty or not.
     * @return true if the value is empty or false indicates there is one or more values in Queue.
     */
    boolean isEmpty();

    /**
     * @return the capacity of this Queue, -1 means no capacity limitation.
     */
    int getCapacity();
}
