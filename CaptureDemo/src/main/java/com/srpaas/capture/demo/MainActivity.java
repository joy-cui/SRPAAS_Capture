package com.srpaas.capture.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.srpaas.capture.render.CameraGLSurfaceView;
import com.srpaas.capture.render.CameraInterface;
import com.srpaas.capture.render.VideoCapture;
import com.srpaas.capture.service.VideoService;
import com.srpaas.capture.service.VideoServiceImpl;
import com.srpaas.capture.service.VideoServiceListener;
import com.suirui.srpaas.base.util.CommonUtils;
import com.suirui.srpaas.base.util.log.SRLog;

import org.suirui.util.libyuv.jni.entry.YUV;
import org.suirui.util.libyuv.jni.util.CodeUtil;


public class MainActivity extends AppCompatActivity implements VideoServiceListener {
    SRLog log = new SRLog(VideoCapture.class.getName());
    private CameraGLSurfaceView cameraSurfaceView;
    //    CameraGLSurfaceView cameraSurfaceView2;
    private VideoService videoService;
    private int sw, sh;
    private Button btnClose, btnOpen;
    private boolean isChange = false;
    private boolean isOpen = false;
    private GLFrameSurface remoteGlFrameSurface;//yuv渲染
    private GLFrameRenderer remoteGlFrameRenderer;
    private CodeUtil yuvUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = CommonUtils.getDM(this);
        this.sw = metrics.widthPixels;
        this.sh = metrics.heightPixels;
        findview();
        initCamera();
    }

    private void findview() {
        remoteGlFrameSurface = new GLFrameSurface(this);
        remoteGlFrameSurface = (GLFrameSurface) this.findViewById(R.id.glrender_surface);
        remoteGlFrameRenderer = new GLFrameRenderer(remoteGlFrameSurface, this);
        remoteGlFrameSurface.setRenderer(remoteGlFrameRenderer);
        cameraSurfaceView = (CameraGLSurfaceView) findViewById(R.id.cameraSurfaceView);
//        cameraSurfaceView2 = (CameraGLSurfaceView) findViewById(R.id.cameraSurfaceView2);
        btnClose = (Button) findViewById(R.id.btnClose);
        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    videoService.stopCapture();
                    isOpen = true;
                    btnClose.setText("打开");
                } else {
                    videoService.startCapture(MainActivity.this, CameraInterface.getInstance().getCameraType());
                    isOpen = false;
                    btnClose.setText("关闭");
                }
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cameraType = CameraInterface.getInstance().getCameraType() == 1 ? 0 : 1;
                videoService.switchCamera(MainActivity.this, cameraType);
            }
        });
    }

    private void initCamera() {
        videoService = new VideoServiceImpl();
        videoService.addVideoServiceListener(this);
        videoService.startCapture(this, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoService.stopCapture();
        videoService.removeVideoServiceListener();
        super.onDestroy();
    }

    @Override
    public void onstartCaptureFailListener() {

    }

    @Override
    public void onstopCaptureFailListener() {

    }


    @Override
    public void onPreviewCallback(byte[] des, int width, int height, int rotation) {
//        log.E("onPreviewCallback..........des....");
//        ByteBuffer[] yuvPlanes = PreviewFrameUtil.bufferToByte(des, width, height);
//        byte[] y = new byte[yuvPlanes[0].remaining()];
//        yuvPlanes[0].get(y, 0, y.length);
//
//        byte[] u = new byte[yuvPlanes[1].remaining()];
//        yuvPlanes[1].get(u, 0, u.length);
//
//        byte[] v = new byte[yuvPlanes[2].remaining()];
//        yuvPlanes[2].get(v, 0, v.length);
//
//        byte[] toy = new byte[0];
//        byte[] tou = new byte[0];
//        byte[] tov = new byte[0];

        if (yuvUtil == null) {
            yuvUtil = new CodeUtil();
        }
        YUV yuv = yuvUtil.yuv420ToYUV(des, width, height);
        if (yuv != null) {
            byte[] y = yuv.getY();
            byte[] u = yuv.getU();
            byte[] v = yuv.getV();
            remoteGlFrameRenderer.update(width, height, false);
            remoteGlFrameRenderer.update(y, u, v);
        }

    }
}
