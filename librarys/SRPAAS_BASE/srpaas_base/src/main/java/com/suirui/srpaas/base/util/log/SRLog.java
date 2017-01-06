package com.suirui.srpaas.base.util.log;

import android.util.Log;

/**
 * 控制整个应用的LOG打印
 *
 * @author cui.li
 *
 */
public final class SRLog {

	private int debugType = DebugType.V;

	public static final class DebugType {
		public static final int V = 0;
		public static final int D = 1;
		public static final int I = 2;
		public static final int W = 3;
		public static final int E = 4;
		public static final int N = 5; // close all debug info
	}

	private String TAG = "";

	/**
	 * 设置要打印LOG的类的TAG信息
	 *
	 * @param TAG
	 *            应包含这个类的包名和类名
	 */
	public SRLog(String TAG) {
		this.TAG = TAG;
	}

	/**
	 * 设置要打印LOG的类的TAG信息
	 *
	 * @param TAG
	 *            应包含这个类的包名和类名
	 * @param debugType
	 *            设置LOG的最高级别
	 */
	public SRLog(String TAG, int debugType) {
		this.TAG = TAG;
		this.debugType = debugType;
	}

	public void D(String s) {
		try {
			if (debugType <= DebugType.D) {
				Log.d(TAG, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void E(String s) {
		try {
			if (debugType <= DebugType.E) {
				Log.e(TAG, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void I(String s) {
		try {
			if (debugType <= DebugType.I) {
				Log.i(TAG, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void W(String s) {
		try {
			if (debugType <= DebugType.W) {
				Log.w(TAG, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void V(String s) {
		try {
			if (debugType <= DebugType.V) {
				Log.v(TAG, s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
