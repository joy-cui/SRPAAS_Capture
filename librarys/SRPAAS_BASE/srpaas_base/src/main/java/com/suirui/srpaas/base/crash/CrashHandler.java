package com.suirui.srpaas.base.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 *
 * @author way
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "org.suirui.remote.project.crash.CrashHandler";
	private UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
	private Context mContext;// 程序的Context对象
	private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
//	public void init(Context context) {
//		mContext = context;
//		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
//		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
//	}

	//这里主要完成初始化工作
	public void init(Context context) {
		//获取系统默认的异常处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//将当前实例设为系统默认的异常处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		//获取Context，方便内部使用
		mContext = context.getApplicationContext();
	}
	/**
	 * 当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
	 * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		//如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
		if (mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}
//	/**
//	 * 当UncaughtException发生时会转入该重写的方法来处理
//	 */
//	public void uncaughtException(Thread thread, Throwable ex) {
//		Log.e(TAG,
//				"uncaughtException..........RemoteServer....."
//						+ ex.getMessage());
//		if (!handleException(ex) && mDefaultHandler != null) {
//			// 如果自定义的没有处理则让系统默认的异常处理器来处理
//			mDefaultHandler.uncaughtException(thread, ex);
//		} else {
//			try {
//				thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			// 退出程序
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(1);
//		}
//	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 *
	 * @param ex
	 *            异常信息
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		if (ex == null)
			return false;
		new Thread() {
			public void run() {
				Looper.prepare();
//				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", 0).show();
				Looper.loop();
			}
		}.start();
		Log.e("TAG", "handleException-------ex==" + ex);
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 *
	 * @param context
	 */
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();// 反射机制
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("LongLogTag")
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".log";

		Log.d(TAG, "fileName=" + fileName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				String path = "/sdcard/crash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				// Log.d(TAG, "toString=" + sb.toString().getBytes());
				fos.close();
				return fileName;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取App安装包信息
	 *
	 * @return
	 */
	private PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace(System.err);
			// L.i("getPackageInfo err = " + e.getMessage());
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}
}