package com.suirui.srpaas.base.ui.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.suirui.srpaas.base.ui.BaseAppManager;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.net.HeadSetObserver;
import com.suirui.srpaas.base.util.net.HeadStatusReceiver;
import com.suirui.srpaas.base.util.net.NetChangeObserver;
import com.suirui.srpaas.base.util.net.NetStateReceiver;
import com.suirui.srpaas.base.util.net.NetUtils;
import com.suirui.srpaas.base.util.net.SensorEventObserver;
import com.suirui.srpaas.base.util.net.SensorEventReceiver;

/**
 * Author:cui.li
 * Date: by 2016.1.26
 * Description:基础activity类
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    /**
     * Log tag
     */
    protected static String TAG_LOG = "BaseAppCompatActivity";
    /**
     * 屏幕参数
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;
    /**
     * 上下文
     */
    protected Context mContext = null;
    /**
     * 联网状态
     */
    protected NetChangeObserver mNetChangeObserver = null;

    /**
     * 插拔耳机
     */
    protected HeadSetObserver mHeadSetObserver = null;
    SRLog log = new SRLog(TAG_LOG);
    /**
     * 传感器
     */
    private SensorEventObserver mSensorEventObserver = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (toggleOverridePendingTransition()) {
//            switch (getOverridePendingTransitionMode()) {
//                case LEFT:
//                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                    break;
//                case RIGHT:
//                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                    break;
//                case TOP:
//                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
//                    break;
//                case BOTTOM:
//                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
//                    break;
//                case SCALE:
//                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
//                    break;
//                case FADE:
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    break;
//            }
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG_LOG = this.getClass().getSimpleName();
//        SmartBarUtils.hide(getWindow().getDecorView());
//        setTranslucentStatus(true);

        if (isSupportActionBar()) {
            getSupportActionBar().hide();
        }
        BaseAppManager.getInstance().addActivity(this);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
//        if (getContentViewLayoutID() != 0) {
//            setContentView(getContentViewLayoutID());
//        } else {
//            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
//        }
        NetChangeRealization();
        HeadChangeRealization();
        SensorEventRealization();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void NetChangeRealization() {
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                super.onNetConnected(type);
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                onNetworkDisConnected();
            }
        };
        NetStateReceiver.registerObserver(mNetChangeObserver);
    }


    protected void HeadChangeRealization() {
        mHeadSetObserver = new HeadSetObserver() {

            @Override
            public void onHeadStatus(boolean isHead) {
                super.onHeadStatus(isHead);
                onHeadsetStatus(isHead);
            }
        };
        HeadStatusReceiver.registerObserver(mHeadSetObserver);
    }

    protected void SensorEventRealization() {
        mSensorEventObserver = new SensorEventObserver() {
            @Override
            public void onSensorEvent(boolean isonSensorChanged) {
                super.onSensorEvent(isonSensorChanged);
                onSensorEventChange(isonSensorChanged);
            }
        };
        SensorEventReceiver.registerObserver(mSensorEventObserver);
    }

    protected abstract void onSensorEventChange(boolean isonSensorChanged);

    protected abstract void onHeadsetStatus(boolean isHead);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        HeadStatusReceiver.removeRegisterObserver(mHeadSetObserver);
        SensorEventReceiver.removeRegisterObserver(mSensorEventObserver);
        BaseAppManager.getInstance().removeActivity(this);
//        if (toggleOverridePendingTransition()) {
//            switch (getOverridePendingTransitionMode()) {
//                case LEFT:
//                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                    break;
//                case RIGHT:
//                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                    break;
//                case TOP:
//                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
//                    break;
//                case BOTTOM:
//                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
//                    break;
//                case SCALE:
//                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
//                    break;
//                case FADE:
//                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    break;
//            }
//        }
    }

//    @Override
//    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNetChangeObserver = null;
        mHeadSetObserver = null;
        mSensorEventObserver = null;
    }

    /**
     * network connected
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);

    /**
     * network disconnected
     */
    protected abstract void onNetworkDisConnected();

    //    protected abstract int getContentViewLayoutID();
    protected abstract boolean isSupportActionBar();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("BaseAppCompat Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client,getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client,getIndexApiAction());
        client.disconnect();
    }


//    /**
//     *
//     * toggle overridePendingTransition
//     *
//     * @return
//     */
//    protected abstract boolean toggleOverridePendingTransition();
//
//    /**
//     * 获取overridePendingTransition 模式
//     * get the overridePendingTransition mode
//     */
//    protected abstract TransitionMode getOverridePendingTransitionMode();

    /**
     * overridePendingTransition mode
     */
    public enum TransitionMode {
        LEFT,RIGHT,TOP,BOTTOM,SCALE,FADE
    }

    /**
     * set status bar translucency
     *
     * @param on
     */
//    protected void setTranslucentStatus(boolean on) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window win = getWindow();
//            WindowManager.LayoutParams winParams = win.getAttributes();
//            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            if (on) {
//                winParams.flags |= bits;
//            } else {
//                winParams.flags &= ~bits;
//            }
//            win.setAttributes(winParams);
//        }
//    }

}
