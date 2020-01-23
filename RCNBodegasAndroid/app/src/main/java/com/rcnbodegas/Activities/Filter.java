package com.rcnbodegas.Activities;

/**
 * Created by windows 8 on 17/03/14.
 */
public interface Filter<T,E> {
    public boolean isMatched(T object, E text);
}
