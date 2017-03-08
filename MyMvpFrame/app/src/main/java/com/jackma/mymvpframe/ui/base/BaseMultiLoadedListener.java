package com.jackma.mymvpframe.ui.base;

/**
 * BaseMultiLoadedListener
 * @author: mhj
 * @date: 16/5/26 下午3:44
 * 
 */
public interface BaseMultiLoadedListener<T> {

    /**
     * when data call back success
     *
     * @param data
     */
    void onSuccess(int event_tag, T data);

    /**
     * when data call back error
     *
     * @param msg
     */
    void onFailure(String msg);

    /**
     * when data call back occurred exception
     *
     * @param msg
     */
    void onException(String msg);
}
