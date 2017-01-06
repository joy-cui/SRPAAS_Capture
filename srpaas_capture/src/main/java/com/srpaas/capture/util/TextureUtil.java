package com.srpaas.capture.util;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

public class TextureUtil {
    static SurfaceTexture mSurfaceTexture;
    static int[] mTexture;

    public static SurfaceTexture getInstance() {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = new SurfaceTexture(createTextureID());
            try {
                mSurfaceTexture.detachFromGLContext();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mSurfaceTexture;
    }

    public synchronized static void draw(AFilter mOesFilter, int mTextureID) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        getInstance().attachToGLContext(mTextureID);
        getInstance().updateTexImage();
        mOesFilter.draw();
        getInstance().detachFromGLContext();
    }

    private static int createTextureID() {
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
}
