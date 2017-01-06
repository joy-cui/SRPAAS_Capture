package com.suirui.srpaas.base.util.net;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @authordingna
 * @date2016-11-11
 **/
public class SensorEventReceiver implements SensorEventListener {
    private final static String TAG = SensorEventReceiver.class.getSimpleName();
    private static List<SensorEventObserver> mSensorEventObserver = new ArrayList<SensorEventObserver>();
    private static SensorManager sensorManager;
    private static PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;
    private static boolean isonSensorChanged = false;
    private static Sensor mSensor;
    private static SensorEventListener mSensorEventListener;

    public static SensorEventListener getListener() {
        if (null == mSensorEventListener) {
            synchronized (SensorEventReceiver.class) {
                mSensorEventListener = new SensorEventReceiver();
            }
        }
        return mSensorEventListener;
    }

    private static void notifyObserver() {
        if (!mSensorEventObserver.isEmpty()) {
            int size = mSensorEventObserver.size();
            for (int i = 0; i < size; i++) {
                SensorEventObserver observer = mSensorEventObserver.get(i);
                if (observer != null) {
                    observer.onSensorEvent(isonSensorChanged);
                }
            }
        }
    }

    public static void registerObserver(SensorEventObserver observer) {
        if (mSensorEventObserver == null)
            mSensorEventObserver = new ArrayList<SensorEventObserver>();
        mSensorEventObserver.add(observer);
    }

    public static void registerSensorManager(Context context) {
        sensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);// 获取传感器管理器
        mSensor = (sensorManager.getSensorList(Sensor.TYPE_PROXIMITY)).get(0);
        powerManager = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);// 电源管理对象
        wakeLock = powerManager.newWakeLock(32,"MyPower");// 第一个参数为电源锁级别，第二个是日志tag
        sensorManager.registerListener(getListener(),mSensor,SensorManager.SENSOR_DELAY_NORMAL);  //注册监听器
    }

    public static void unRegisterSensorManager() {
        Log.i(TAG,"SensorEventReceiver....unRegisterSensorManager...");
        if (wakeLock != null) {
            if (wakeLock.isHeld())
                wakeLock.release();//释放电源锁
        }
        if (sensorManager != null)
            sensorManager.unregisterListener(mSensorEventListener);// 解除监听器注册
        mSensorEventListener = null;
        isonSensorChanged = false;
        powerManager = null;
        wakeLock = null;
        mSensor = null;
        sensorManager = null;
    }

    public static void removeRegisterObserver(SensorEventObserver observer) {
        Log.i(TAG,"SensorEventReceiver....removeRegisterObserver..");
        if (mSensorEventObserver != null) {
            if (mSensorEventObserver.contains(observer)) {
                mSensorEventObserver.remove(observer);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] its = event.values;
        Log.i(TAG,"SensorEventReceiver....onSensorChanged。。。its：" + its[0]);
        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // 经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
            try {
                if (its[0] == 0.0) {// 贴近手机
                    isonSensorChanged = true;
                    notifyObserver();
                    if (wakeLock.isHeld()) {
                        return;
                    } else {
                        wakeLock.acquire();//申请电源锁
                    }
                    Log.i(TAG,"SensorEventReceiver....贴近手机");
                } else {//远离手机
                    if (isonSensorChanged) {
                        isonSensorChanged = false;
                        notifyObserver();
                        if (wakeLock.isHeld()) {
                            return;
                        } else {
                            wakeLock.setReferenceCounted(false);
                            wakeLock.release();//释放电源锁
                        }
                        Log.i(TAG,"SensorEventReceiver....远离手机");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor,int accuracy) {
    }

}
