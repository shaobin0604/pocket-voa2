package cn.yo2.aquarium.pocketvoa2.util;

import android.util.Log;

public class Logger {
	private static final String TAG = "PocketVOA2";
	
	public static void d(String msg) {
		Log.d(TAG, buildMsg(msg));
	}
	
	public static void e(String msg, Exception e) {
		Log.e(TAG, buildMsg(msg), e);
	}
	
	public static void e(String msg) {
		Log.e(TAG, buildMsg(msg));
	}

	private static String buildMsg(String msg) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[4];
		StringBuilder buffer = new StringBuilder("[");
		buffer.append(element.getFileName());
		buffer.append(':');
		buffer.append(element.getLineNumber());
		buffer.append("] ");
		buffer.append(element.getMethodName());
		buffer.append(" -> ");
		buffer.append(msg);
		return buffer.toString();
	}
}
