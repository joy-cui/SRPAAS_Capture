package com.suirui.srpaas.base.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

/**
 * 插拔耳机
 *
 * @authordingna
 * @date2016-11-11
 **/
public class HeadStatusReceiver extends BroadcastReceiver {
    public static final String HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String STATUS = "state";
    private final static String TAG = HeadStatusReceiver.class.getSimpleName();
    private static ArrayList<HeadSetObserver> mHeadSetObservers = new ArrayList<HeadSetObserver>();
    private static BroadcastReceiver mBroadcastReceiver;
    private static int on = 0;//拔出耳机
    private static int off = 1;//插入耳机
    private static boolean isHead = false;

    public static void registerHeadSetReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(HEADSET_PLUG);
        context.getApplicationContext().registerReceiver(getReceiver(), filter);
    }

    public static void unRegisterHeadSetReceiver(Context context) {
        if (mBroadcastReceiver != null) {
            try {
                context.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerObserver(HeadSetObserver observer) {
        if (mHeadSetObservers == null)
            mHeadSetObservers = new ArrayList<HeadSetObserver>();
        mHeadSetObservers.add(observer);
    }

    public static void removeRegisterObserver(HeadSetObserver observer) {
        if (mHeadSetObservers != null) {
            if (mHeadSetObservers.contains(observer)) {
                mHeadSetObservers.remove(observer);
            }
        }
    }

    public static BroadcastReceiver getReceiver() {
        if (null == mBroadcastReceiver) {
            synchronized (HeadStatusReceiver.class) {
                mBroadcastReceiver = new HeadStatusReceiver();
            }
        }
        return mBroadcastReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mBroadcastReceiver = HeadStatusReceiver.this;
        if (intent.getAction().equalsIgnoreCase(HEADSET_PLUG)) {
            if (intent.hasExtra(STATUS)) {
                if (intent.getIntExtra(STATUS, 0) == on) {
                    isHead = false;
                } else if (intent.getIntExtra(STATUS, 0) == off) {
                    isHead = true;
                }
            }
            notifyObserver();
        }
    }

    private void notifyObserver() {
        if (!mHeadSetObservers.isEmpty()) {
            int size = mHeadSetObservers.size();
            for (int i = 0; i < size; i++) {
                HeadSetObserver observer = mHeadSetObservers.get(i);
                if (observer != null) {
                    observer.onHeadStatus(isHead);
                }
            }
        }
    }
}
