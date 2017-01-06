package com.srpaas.capture.render;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.listener.MyOrientationDetector;
import com.srpaas.capture.util.AFilter;
import com.srpaas.capture.util.Gl2Utils;
import com.srpaas.capture.util.OesFilter;
import com.srpaas.capture.util.TextureUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 相机采集的render
 *
 * @authordingna
 * @date2016-12-23
 **/
public class CameraRender implements GLSurfaceView.Renderer, MyOrientationDetector.onOrientationChanged {
    SRLog log = new SRLog(VideoCapture.class.getName());
    int mTextureID = -1;
    private AFilter mOesFilter;
    private int width, height;
    private int dataWidth, dataHeight;
    private float[] matrix = new float[16];


    public CameraRender(Context context) {
        mOesFilter = new OesFilter(context.getResources());
        MyOrientationDetector.addOnOrientationChanged(this);
//        log.E("VideoCapture。。。。。CameraRender...OesFilter");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        log.E("VideoCapture。。。。。CameraRender...onSurfaceCreated..:");
        mTextureID = createTextureID();
        mOesFilter.create();
        mOesFilter.setTextureId(mTextureID);
        calculateMatrix();
        Point point = CameraInterface.getInstance().getDataSize();
        if (point != null)
            setDataSize(point.x, point.y);
    }

    public void setDataSize(int dataWidth, int dataHeight) {
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        log.E("VideoCapture。。。。。CameraRender...onSurfaceChanged");
        setViewSize(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    private void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    private void calculateMatrix() {
        log.E("VideoCapture。。。。。CameraRender...calculateMatrix...:" + CameraEntry.isRotate);
        if (CameraInterface.getInstance().getCameraType() == CameraEntry.Type.FRONT_CAMERA.getValue()) {
            switch (CameraInterface.getInstance().getRotation()) {
                case CameraEntry.Rotation.ROTATE_0:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
                        Gl2Utils.flip(matrix, true, false);
                        Gl2Utils.rotate(matrix, 90);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_90:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataHeight, this.dataWidth, this.width, this.height);
                        Gl2Utils.flip(matrix, true, false);
                        Gl2Utils.rotate(matrix, 0);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_180://反向竖屏不做旋转处理
//                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate){
//                        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
//                        Gl2Utils.flip(matrix, true, false);
//                        Gl2Utils.rotate(matrix, 270);
//                    }
                    break;
                case CameraEntry.Rotation.ROTATE_270:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataHeight, this.dataWidth, this.width, this.height);
                        Gl2Utils.flip(matrix, true, false);
                        Gl2Utils.rotate(matrix, 180);
                    }
                    break;
            }
        } else {
            switch (CameraInterface.getInstance().getRotation()) {
                case CameraEntry.Rotation.ROTATE_0:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
                        Gl2Utils.rotate(matrix, 270);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_90:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataHeight, this.dataWidth, this.width, this.height);
                        Gl2Utils.rotate(matrix, 0);
                    }
                    break;
                case CameraEntry.Rotation.ROTATE_180://反向竖屏不做旋转处理
//                    if (!CameraEntry.isSwitch&& !CameraEntry.isRotate) {
//                        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
//                        Gl2Utils.rotate(matrix, 90);
//                    }
                    break;
                case CameraEntry.Rotation.ROTATE_270:
                    if (!CameraEntry.isSwitch && !CameraEntry.isRotate) {
                        Gl2Utils.getShowMatrix(matrix, this.dataHeight, this.dataWidth, this.width, this.height);
                        Gl2Utils.rotate(matrix, 180);
                    }
                    break;
            }
        }
        mOesFilter.setMatrix(matrix);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        log.E("VideoCapture。。。。。CameraRender...onDrawFrame.." + CameraEntry.isRotate);
        TextureUtil.draw(mOesFilter, mTextureID);
    }

    public int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    @Override
    public void onScreenChange(int rotation) {
        try {
            CameraEntry.isRotate = false;
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rotation == CameraEntry.Rotation.ROTATE_0) {
            if (width > height) {
                setViewSize(height, width);
            } else {
                setViewSize(width, height);
            }
        } else if (rotation == CameraEntry.Rotation.ROTATE_90 || rotation == CameraEntry.Rotation.ROTATE_270) {
            if (width > height) {
                setViewSize(width, height);
            } else {
                setViewSize(height, width);
            }
        }
    }
}
