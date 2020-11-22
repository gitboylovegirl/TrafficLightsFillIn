package com.fred.trafficlightsfillin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.util.Locale;

/**
 * 一个日志的工具类 可以开启和关闭打印日志 最好不要用System打印，消耗内存。
 * 
 */
public class LogUtils {

	/**
	 * 默认开启
	 */
	public static boolean isLogEnabled = false;
	/**
	 * log默认的 tag
	 */
	private static final String defaultTag = "prolog ";
	private static final String TAG_CONTENT_PRINT = "%s.%s:%d";

	/**
	 * 获得当前的 堆栈
	 * 
	 * @return
	 */
	private static StackTraceElement getCurrentStackTraceElement() {
		return Thread.currentThread().getStackTrace()[4];

	}

	/**
	 * 
	 * 设置 debug是否启用 根据 判断 是否 为上线模式 android:debuggable 打包后变为false，没打包前为true
	 * 
	 * 要在application中 首先进行调用此方法 对 isLogEnabled 进行赋值
	 * 
	 * @param context
	 * @return
	 */
	public static void setDebugable(Context context) {
		try {
			ApplicationInfo info = context.getApplicationInfo();
			isLogEnabled = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取是否DEBUG模式
	 * 
	 * @return
	 */
	public static boolean isDebugable() {
		return isLogEnabled;
	}

	/**
	 * 打印的log信息 类名.方法名:行数--->msg
	 * 
	 * @param trace
	 * @return
	 */
	private static String getContent(StackTraceElement trace) {
		return String.format(Locale.CHINA, TAG_CONTENT_PRINT, trace.getClassName(), trace.getMethodName(), trace.getLineNumber());
	}

	/**
	 * debug
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg, Throwable tr) {
		if (isLogEnabled) {
			Log.d(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg, tr);
		}
	}

	/**
	 * debug
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (isLogEnabled) {
//			getContent(getCurrentStackTraceElement())
			Log.d(defaultTag + tag,"--->" + msg);
		}
	}

	/**
	 * debug
	 * 
	 * @param msg
	 */
	public static void d(String msg) {
		if (isLogEnabled) {
			Log.d(defaultTag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	public static void log(String msg) {
		if (isLogEnabled) {
			Log.d(defaultTag, "--->" + msg);
		}
	}

	/**
	 * error
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (isLogEnabled) {
			Log.e(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg, tr);
		}
	}

	/**
	 * error
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		if (isLogEnabled) {
			Log.e(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * error
	 * 
	 * @param msg
	 */
	public static void e(String msg) {
		if (isLogEnabled) {
			Log.e(defaultTag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * info
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag, String msg, Throwable tr) {
		if (isLogEnabled) {
			Log.i(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg, tr);
		}
	}

	/**
	 * info
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag, String msg) {
		if (isLogEnabled) {
			Log.i(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * info
	 * 
	 * @param msg
	 */
	public static void i(String msg) {
		if (isLogEnabled) {
			Log.i(defaultTag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * verbose
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg, Throwable tr) {
		if (isLogEnabled) {
			Log.v(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg, tr);
		}
	}

	/**
	 * verbose
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg) {
		if (isLogEnabled) {
			Log.v(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * verbose
	 * 
	 * @param msg
	 */
	public static void v(String msg) {
		if (isLogEnabled) {
			Log.v(defaultTag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * warn
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag, String msg, Throwable tr) {
		if (isLogEnabled) {
			Log.w(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg, tr);
		}
	}

	/**
	 * warn
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void w(String tag, String msg) {
		if (isLogEnabled) {
			Log.w(defaultTag + tag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	/**
	 * warn
	 * 
	 * @param msg
	 */
	public static void w(String msg) {
		if (isLogEnabled) {
			Log.w(defaultTag, getContent(getCurrentStackTraceElement()) + "--->" + msg);
		}
	}

	public static void e(Throwable throwable) {
		Log.e(defaultTag, throwable.getMessage(), throwable);
	}

}
