package com.srpaas.capture.listener;

import android.content.Context;
import android.os.Build;
import android.view.OrientationEventListener;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.render.CameraInterface;
import com.suirui.srpaas.base.util.log.SRLog;

public class MyOrientationDetector extends OrientationEventListener {
    private static onOrientationChanged mListener;
    public final int SCREEN_0 = 0;// 竖屏
    public final int SCREEN_90 = 90;// 横屏
    public final int SCREEN180 = 180;
    public final int SCREEN_270 = 270;
    SRLog log = new SRLog(MyOrientationDetector.class.getName());
    private int mCamera;
    private int rotation;


    public MyOrientationDetector(Context context) {
        super(context);
        init();
    }

    public static void addOnOrientationChanged(onOrientationChanged listener) {
        mListener = listener;
    }

    private void init() {
        mCamera = CameraInterface.getInstance().getCameraType();
    }

    public int getRotation() {
        return rotation;
    }

    private void setRotation(int orientation) {
        switch (orientation) {
            case SCREEN_0:
                rotation = CameraEntry.Rotation.ROTATE_0;
                break;
            case SCREEN_270:
                rotation = CameraEntry.Rotation.ROTATE_90;
                break;
            case SCREEN180:
                rotation = CameraEntry.Rotation.ROTATE_180;
                break;
            case SCREEN_90:
                rotation = CameraEntry.Rotation.ROTATE_270;
                break;
            default:
                break;
        }
        if (CameraInterface.getInstance().getRotation() != rotation) {
            CameraEntry.isRotate = true;
            CameraInterface.getInstance().setRotation(rotation);
            log.E("CameraRender....相机旋转了....");
            if (mListener != null)
                mListener.onScreenChange(rotation);
        }
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return; // 手机平放时，检测不到有效的角度
        }
        // 只检测是否有四个角度的改变
        if (orientation > 350 || orientation < 30) { // 0度(竖屏幕)
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                setRotation(0);
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                setRotation(0);
            }

        } else if (orientation > 80 && orientation < 100) { // 90度
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                setRotation(90);
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                setRotation(90);
            }

        } else if (orientation > 170 && orientation < 190) { // 180度
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                setRotation(180);
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                setRotation(180);
            }

        } else if (orientation > 260 && orientation < 280) { // 270度
            if (mCamera == CameraEntry.Type.FRONT_CAMERA.getValue()) {
                if (Build.MODEL != null && Build.MODEL.equals("U9180")) {
                    setRotation(180);// 270 针对中兴U9180此款手机横屏时相反
                } else {
                    setRotation(270);
                }
            } else if (mCamera == CameraEntry.Type.BACK_CAMERA.getValue()) {
                setRotation(270);
            }

        } else {
            return;
        }
    }

    public interface onOrientationChanged {
        void onScreenChange(int rotation);
    }
}
