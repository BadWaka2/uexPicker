package org.zywx.wbpalmstar.plugin.uexpicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPre1 {
	public static void setStrDate(String show, Context mContext, String dateStr) {
		// 将生成宫格个数添加到本地
		SharedPreferences.Editor token = mContext.getSharedPreferences(dateStr, 0).edit();
		token.putString(dateStr, show);
		token.commit();
	}

	public static String getStrDate(Context mContext, String dateStr) {
		// 将生成宫格个数添加到本地
		SharedPreferences preferences = mContext.getSharedPreferences(dateStr, 0);
		String shareDate = preferences.getString(dateStr, null);
		return shareDate;
	}

	public static void setStrDate(Context mContext, String DATE, String date) {
		// 将生成宫格个数添加到本地
		Log.i("date---", "date--" + date);
		SharedPreferences.Editor token = mContext.getSharedPreferences(DATE, 0).edit();
		token.putString(DATE, date);
		token.commit();
	}

	public static String getStrDat(Context mContext, String dateStr) {
		// 将生成宫格个数添加到本地
		SharedPreferences preferences = mContext.getSharedPreferences(dateStr, 0);
		String shareDate1 = preferences.getString(dateStr, null);
		Log.i("preview", "harvestData=" + shareDate1);
		return shareDate1;
	}

}
