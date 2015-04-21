package com.boha.ghostpractice.util;

import java.math.BigDecimal;

import android.content.Context;
import android.util.Log;
//import android.widget.Toast;

public class ElapsedTimeUtil {

	public static void showElapsed(long start, long end, Context ctx) {
		/*if (end - start < 1000) {
			return;
		}
		Toast.makeText(ctx, "Request took " + getElapsedSeconds(start, end) + " seconds to execute", Toast.LENGTH_SHORT).show();*/
		Log.i("ElapsedTimeUtil","Request took " + getElapsedSeconds(start, end) + " seconds to execute");
	}
	
	public static double getElapsedSeconds(long start, long end) {
		BigDecimal seconds = new BigDecimal(end - start).divide(new BigDecimal(1000));
		return seconds.doubleValue();
	}
}
