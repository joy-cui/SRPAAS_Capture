package com.srpaas.capture.service;

public interface VideoServiceListener {

    /**
     * 相机失败
     */
    void onstartCaptureFailListener();

    /**
     * 关闭相机失败
     */
    void onstopCaptureFailListener();

//    /**
//     * 采集回来的yuv数据
//     *
//     * @param des
//     * @param height
//     * @param width
//     */
//    void onPreviewCallback(byte[] des, byte[] y, byte[] u, byte[] v, int height, int width, int rotation);

    /**
     * 相机采集回来的yuv420数据
     *
     * @param des
     * @param width
     * @param height
     * @param rotation
     */
    void onPreviewCallback(byte[] des, int width, int height, int rotation);
}
