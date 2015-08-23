package com.tiangongkaiwu.utility;

import android.util.Log;

public class MyLog {
	
	private static boolean switcher = true;
	
	public static void d(String tag, String msg){
		if(switcher) {
			Log.d(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if(switcher) {
			Log.e(tag, msg);
		}
	}
}
