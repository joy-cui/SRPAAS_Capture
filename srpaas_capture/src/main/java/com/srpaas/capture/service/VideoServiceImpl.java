package com.srpaas.capture.service;

import android.content.Context;

import com.srpaas.capture.constant.CameraEntry;
import com.srpaas.capture.render.CameraCaptureListener;
import com.srpaas.capture.render.VideoCapture;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.util.libyuv.jni.util.CodeUtil;

import java.nio.ByteBuffer;

public class VideoServiceImpl implements VideoService, CameraCaptureListener.CameraVideoListener {
    SRLog log = new SRLog(VideoServiceImpl.class.getName());
    private VideoServiceListener mListener;
    private VideoCapture videoCapture;

    public VideoServiceImpl() {
        if (videoCapture == null)
            videoCapture = new VideoCapture();
        CameraCaptureListener.getInstance().addCameraVideoListener(this);
        CodeUtil.getInstance();
    }

    @Override
    public void addVideoServiceListener(VideoServiceListener listener) {
        this.mListener = listener;
    }

    @Override
    public void removeVideoServiceListener() {
        this.mListener = null;
    }

    @Override
    public boolean startCapture(Context context, int mCameraType) {
        boolean openStatus = false;
        if (videoCapture != null)
            openStatus = videoCapture.startCapture(context, mCameraType);
        if (!openStatus && mListener != null) {
            mListener.onstartCaptureFailListener();
        }
        return true;
    }

    @Override
    public boolean stopCapture() {
        boolean closeStatus = false;
        if (videoCapture != null)
            closeStatus = videoCapture.stopCapture();
        if (!closeStatus && mListener != null) {
            mListener.onstopCaptureFailListener();
        }
        return true;
    }

    @Override
    public void switchCamera(Context context, int cameraType) {
        if (videoCapture != null)
            videoCapture.switchCamera(context, cameraType);
    }


    @Override
    public void onPreviewCallback(final byte[] data, final int width, final int height, int cameraType, int rotation) {
        if (data == null)
            return;
        ByteBuffer[] yuv = null;
        final byte[] des = new byte[width * height * 3 / 2];
//        PreviewFrameUtil.YUV420SPToYUV420P(data, des, width, height);
//        log.E("VideoServiceImpl..onPreviewCallback....width:" + width + " height:" + height);
        switch (rotation) {
            case CameraEntry.Rotation.ROTATE_0:// 旋转270度
                if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
//                    log.E("onPreviewCallback...front..旋转270度 ...");
//                    PreviewFrameUtil.rotateYUV270(data, des, width, height);
//                    yuv = PreviewFrameUtil.bufferToByte(des, width, height);
//                    render(des, yuv, height, width, rotation);

//                    render(data, yuv, width, height, rotation);

                        byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 270, false, true);
                        render(rotaeData, yuv, height, width, rotation);

                } else {// 后
                    log.E("onPreviewCallback...back...旋转90度...");
//                    PreviewFrameUtil.rotateYUV90(data, des, width, height);
//                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 90, false, true);
                    render(rotaeData, yuv, height, width, rotation);

//                    render(des, yuv, height, width, rotation);
                }
                break;
            case CameraEntry.Rotation.ROTATE_90:
                if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
////                    log.E("onPreviewCallback...front...不旋转...");
//                    PreviewFrameUtil.Mirror(des, width, height);
////                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, width, height, rotation);

                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 0, false, false);
                    render(rotaeData, yuv, width, height, rotation);
                } else {
//                    log.E("onPreviewCallback...back...不旋转...");
//                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, width, height, rotation);

                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 0, false, false);
                    render(rotaeData, yuv, width, height, rotation);
                }
                break;
            case CameraEntry.Rotation.ROTATE_180:// 旋转90度
                if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
////                    log.E("onPreviewCallback..front....旋转90度...");
//                    PreviewFrameUtil.rotateYUV90(data, des, width, height);
//                    PreviewFrameUtil.Mirror(des, height, width);
////                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, height, width, rotation);
                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 90, true, true);
                    render(rotaeData, yuv, height, width, rotation);

                } else {
//                    log.E("onPreviewCallback..back...旋转270度 ...");
//                    PreviewFrameUtil.rotateYUV270(data, des, width, height);
//                    PreviewFrameUtil.Mirror(des, height, width);
////                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, height, width, rotation);

                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 270, true, true);
                    render(rotaeData, yuv, height, width, rotation);
                }
                break;
            case CameraEntry.Rotation.ROTATE_270:// 旋转180度
                if (cameraType == CameraEntry.Type.FRONT_CAMERA.getValue()) {
////                    log.E("onPreviewCallback...front... 旋转180度...");
//                    PreviewFrameUtil.rotateYUV180(data, des, width, height);
//                    PreviewFrameUtil.Mirror(des, width, height);
////                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, width, height, rotation);

                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 180, true, true);
                    render(rotaeData, yuv, width, height, rotation);

                } else {
////                    log.E("onPreviewCallback...back... 旋转180度...");
//                    PreviewFrameUtil.rotateYUV180(data, des, width, height);
////                    yuv = PreviewFrameUtil.bufferToByte(yuv420, width, height);
//                    render(des, yuv, width, height, rotation);
                    byte[] rotaeData = CodeUtil.getInstance().nv21To420(data, width, height, 180, false, true);
                    render(rotaeData, yuv, width, height, rotation);
                }
                break;

            default:
                break;
        }
    }

    private void render(byte[] des, ByteBuffer[] yuvPlanes, int width, int height, int rotation) {
        if (yuvPlanes != null) {
            byte[] y = new byte[yuvPlanes[0].remaining()];
            yuvPlanes[0].get(y, 0, y.length);

            byte[] u = new byte[yuvPlanes[1].remaining()];
            yuvPlanes[1].get(u, 0, u.length);

            byte[] v = new byte[yuvPlanes[2].remaining()];
            yuvPlanes[2].get(v, 0, v.length);
//            if (mListener != null)
//                mListener.onPreviewCallback(des, y, u, v, width, height, rotation);
        }
        if (mListener != null)
            mListener.onPreviewCallback(des, width, height, rotation);
    }
}
