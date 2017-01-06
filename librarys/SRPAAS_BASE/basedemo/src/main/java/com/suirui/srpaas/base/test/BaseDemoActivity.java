package com.suirui.srpaas.base.test;

import android.os.Bundle;

import com.suirui.srpaas.base.ui.activity.BaseAppCompatActivity;
import com.suirui.srpaas.base.util.log.SRLog;
import com.suirui.srpaas.base.util.net.NetStateReceiver;
import com.suirui.srpaas.base.util.net.NetUtils;

public class BaseDemoActivity extends BaseAppCompatActivity {
    private SRLog log = new SRLog(BaseDemoActivity.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);
        log.E("import srpaas_base_v0.1.0.jar success");
        NetStateReceiver.registerNetworkStateReceiver(this);//注册网络状态
    }

    @Override
    protected void onSensorEventChange(boolean b) {

    }

    @Override
    protected void onHeadsetStatus(boolean b) {

    }

    @Override
    protected void onNetworkConnected(NetUtils.NetType netType) {
        log.E("Net Connect");
    }

    @Override
    protected void onNetworkDisConnected() {
        log.E("Net disConnect ");
    }

    @Override
    protected boolean isSupportActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
    }
}
