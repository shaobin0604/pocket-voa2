package cn.yo2.aquarium.pocketvoa2.util;

import android.util.Log;

public class Logger {
	private static final String TAG = "PracticeVOA";
	
	public static void d(String msg) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[3];
		StringBuilder buffer = new StringBuilder("[");
		buffer.append(element.getFileName());
		buffer.append('|');
		buffer.append(element.getLineNumber());
		buffer.append(']');
		buffer.append(element.getMethodName());
		buffer.append(" -> ");
		buffer.append(msg);
		Log.d(TAG, buffer.toString());
	}
}
