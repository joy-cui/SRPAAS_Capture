package com.suirui.srpaas.base.crash;

import java.util.Observable;

/**
 * Created by cui.li on 2016/11/7.
 */

public class CrashEvent extends Observable {
    private volatile static CrashEvent instance;
    private final String TAG = CrashEvent.class.getSimpleName();

    public static CrashEvent getInstance() {
        if (instance == null) {
            synchronized (CrashEvent.class) {
                if (instance == null) {
                    instance = new CrashEvent();
                }
            }
        }
        return instance;
    }

    public void crash() {
        setChanged();
        notifyObservers();
    }
}
