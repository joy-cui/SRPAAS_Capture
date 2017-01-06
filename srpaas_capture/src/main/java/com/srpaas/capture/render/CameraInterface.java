package com.srpaas.capture.render;

import android.graphics.Point;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * @authordingna
 * @date2016-12-23
 **/
public class CameraInterface {
    private static CameraInterface instance;
    private List<GLSurfaceView> glSurfaceViewList = new ArrayList<GLSurfaceView>();
    private Point dataSize;
    private int mCameraType = 1;
    private int rotation;
    private boolean isOpenCamera = false;
    private boolean isPreviewing = false;

    public static synchronized CameraInterface getInstance() {
        if (instance == null) {
            instance = new CameraInterface();
        }
        return instance;
    }

    /**
     * 保存共创建了多少个CameraGLSurfaceView
     *
     * @param glSurfaceView
     */
    public void haveGLSurfaceView(GLSurfaceView glSurfaceView) {
        if (glSurfaceViewList == null)
            glSurfaceViewList = new ArrayList<GLSurfaceView>();
        if (glSurfaceView != null)
            glSurfaceViewList.add(glSurfaceView);

    }

    /**
     * 获取创建的CameraGLSurfaceView列表
     *
     * @return
     */
    public List<GLSurfaceView> getCreateGLSurfaceView() {
        return glSurfaceViewList;
    }

    /**
     * 获取相机预览大小
     *
     * @return
     */
    public Point getDataSize() {
        return dataSize;
    }

    /**
     * 设置相机预览大小
     *
     * @param dataSize
     */
    public void setDataSize(Point dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * 获取相机类型
     *
     * @return
     */
    public int getCameraType() {
        return mCameraType;
    }

    /**
     * 设置相机类型
     *
     * @param mCameraType
     */
    public void setCameraType(int mCameraType) {
        this.mCameraType = mCameraType;
    }

    /**
     * 获取相机旋转角度
     *
     * @return
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * 设置相机旋转角度
     *
     * @param rotation
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    /**
     * 获取相机是否开启
     *
     * @return
     */
    public boolean isOpenCamera() {
        return isOpenCamera;
    }

    /**
     * 设置相机是否开启
     *
     * @param isOpenCamera
     */
    public void isOpenCamera(boolean isOpenCamera) {
        this.isOpenCamera = isOpenCamera;
    }

    /**
     * 获取相机是否开启预览
     *
     * @param isPreviewing
     */
    public void isPreviewing(boolean isPreviewing) {
        this.isPreviewing = isPreviewing;
    }

    /**
     * 设置相机是否开启预览
     *
     * @return
     */
    public boolean isPreviewing() {
        return isPreviewing;
    }
}
