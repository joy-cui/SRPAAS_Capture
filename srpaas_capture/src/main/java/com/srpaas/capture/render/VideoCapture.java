package com.srpaas.capture.render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.listener.MyOrientationDetector;
import com.srpaas.capture.util.PreviewFrameUtil;
import com.srpaas.capture.util.TextureUtil;
import com.suirui.srpaas.base.util.log.SRLog;

import java.io.IOException;
import java.util.List;

@SuppressLint("NewApi")
public class VideoCapture implements Camera.PreviewCallback, SurfaceTexture.OnFrameAvailableListener {

    SRLog log = new SRLog(VideoCapture.class.getName());
    float rate = 1.778f; //宽高比
    int minPreviewWidth = 720;
    private MyOrientationDetector myOrientationDetector;
    private int cameraType = 1;
    private Camera mCamera;
    private int mCaptureWidth = 0;
    private int mCaptureHeight = 0;
    private int numCaptureBuffers = 3;
    private Camera.Size preSize;
    private Point mPreSize;
    private Camera.Parameters param;

    public VideoCapture() {
        TextureUtil.getInstance().setOnFrameAvailableListener(this);
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera callbackCamera) {
//        log.E("VideoCapture。。。。。onPreviewFrame：");
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera() || mCamera != callbackCamera) {
            return;
        }
        try {
            int rotation = 0;
            if (myOrientationDetector != null)
                rotation = myOrientationDetector.getRotation();
            cameraType = CameraInterface.getInstance().getCameraType();
            CameraCaptureListener.getInstance().onPreviewCallback(data, mCaptureWidth, mCaptureHeight, cameraType, rotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.addCallbackBuffer(data);
    }

    public boolean startCapture(Context context, int mCameraType) {
        CameraInterface.getInstance().setCameraType(mCameraType);
        registerOrientationDetector(context);
        try {
            if (mCamera == null || !CameraInterface.getInstance().isOpenCamera()) {
                int cameraCount = Camera.getNumberOfCameras();
                if (cameraCount <= 0)
                    return false;
                mCamera = Camera.open(mCameraType);
//                log.E("VideoCapture..startCapture.....mCamera:" + mCamera);
                CameraInterface.getInstance().isOpenCamera(true);
            }
            if (mCamera != null) {
                param = mCamera.getParameters();
                preSize = PreviewFrameUtil.getPropPreviewSize(param.getSupportedPreviewSizes(), rate, minPreviewWidth);
                param.setPreviewSize(preSize.width, preSize.height);
//                param.setPreviewSize(1280, 720);
                param.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(param);
                param = mCamera.getParameters();
                int mformat = param.getPreviewFormat();
                this.mCaptureWidth = param.getPreviewSize().width;
                this.mCaptureHeight = param.getPreviewSize().height;
                int bufSize = mCaptureWidth * mCaptureHeight * ImageFormat.getBitsPerPixel(mformat) / 8;
                for (int i = 0; i < numCaptureBuffers; i++) {
                    mCamera.addCallbackBuffer(new byte[bufSize]);
                }
                mCamera.setPreviewCallbackWithBuffer(this);
                Camera.Size pre = param.getPreviewSize();
                mPreSize = new Point(pre.height, pre.width);
                CameraInterface.getInstance().setDataSize(mPreSize);
            }
            log.E("VideoCapture..startCapture.....setOnFrameAvailableListener:");
            return setPreviewTexture(TextureUtil.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setPreviewTexture(SurfaceTexture surface) {
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera())
            return false;
        try {
            mCamera.setPreviewTexture(surface);
            startPreview();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startPreview() {
        if (CameraInterface.getInstance().isPreviewing())
            return;
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera())
            return;
        mCamera.startPreview();
        try {
            CameraEntry.isSwitch = false;
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.E("VideoCapture。。。。。startPreview");
        CameraInterface.getInstance().isPreviewing(true);
    }

    public boolean stopCapture() {
        unRegisterOrientationDetector();
        if (mCamera == null || !CameraInterface.getInstance().isOpenCamera()) {
            return true;
        }
        try {
            CameraInterface.getInstance().isOpenCamera(false);
            CameraInterface.getInstance().isPreviewing(false);
            mCamera.addCallbackBuffer(null);
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            log.E("VideoCapture。。。。。stopPreview");
            mCamera.setPreviewTexture(null);
            mCamera.release();
            mCamera = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void registerOrientationDetector(Context context) {
        if (myOrientationDetector == null)
            myOrientationDetector = new MyOrientationDetector(context);
        myOrientationDetector.enable();
    }

    private void unRegisterOrientationDetector() {
        if (myOrientationDetector != null) {
            myOrientationDetector.disable();
            myOrientationDetector = null;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        log.E("VideoCapture。。。。。onFrameAvailable..." + CameraInterface.getInstance().isPreviewing());
        if (!CameraInterface.getInstance().isPreviewing()) {
            setPreviewTexture(surfaceTexture);
        } else {
            List<GLSurfaceView> glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
            if (glSurfaceViewList == null)
                return;
            for (GLSurfaceView glSurfaceView : glSurfaceViewList) {
                if (glSurfaceView != null)
                    glSurfaceView.requestRender();
            }
        }
    }

    private void onPause() {
        List<GLSurfaceView> glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null)
            return;
        for (GLSurfaceView glSurfaceView : glSurfaceViewList) {
            if (glSurfaceView != null)
                glSurfaceView.onPause();
        }

    }

    private void onResume() {
        List<GLSurfaceView> glSurfaceViewList = CameraInterface.getInstance().getCreateGLSurfaceView();
        if (glSurfaceViewList == null)
            return;
        for (GLSurfaceView glSurfaceView : glSurfaceViewList) {
            if (glSurfaceView != null)
                glSurfaceView.onResume();
        }
    }

    public void switchCamera(Context context, int cameraType) {
        CameraEntry.isSwitch = true;
        onPause();
        stopCapture();
        startCapture(context, cameraType);
        onResume();
    }

}
