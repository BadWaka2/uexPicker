/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.zywx.wbpalmstar.plugin.uexpicker.wheelview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.zywx.wbpalmstar.plugin.uexpicker.Contains;
import org.zywx.wbpalmstar.plugin.uexpicker.PickerStr;
import org.zywx.wbpalmstar.plugin.uexpicker.SharedPre1;

import android.content.Context;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter1 implements WheelAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;
	/** The default max value */
	@SuppressWarnings("unused")
	private static final int DEFAULT_MIN_VALUE = 0;
	@SuppressWarnings("unused")
	private int lastMonth;
	// Values
	private int minValue;
	private int maxValue;
	private GregorianCalendar[] mAllDates;
	private ArrayList<PickerStr> pickerStrList;
	// format
	protected String format;
	private int nowIndex;
	private boolean isUp;
	private Context mContext;
	@SuppressWarnings("unused")
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Default constructor
	 */
	public NumericWheelAdapter1() {

	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericWheelAdapter1(int minValue, int maxValue, GregorianCalendar[] allDates, ArrayList<PickerStr> pickerStrList, Context context) {
		this(minValue, maxValue, null, allDates, pickerStrList, context);
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public NumericWheelAdapter1(int minValue, int maxValue, String format, GregorianCalendar[] allDates, ArrayList<PickerStr> pickerStrList, Context context) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
		this.mAllDates = allDates;
		this.pickerStrList = pickerStrList;
		this.mContext = context;
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			if (mAllDates == null && pickerStrList == null) {
				int value = minValue + index;
				return format != null ? String.format(format, value) : Integer.toString(value);
			} else if (mAllDates != null && pickerStrList == null) {
				GregorianCalendar e = mAllDates[index];
				return showItemValue(e, index);
			} else if (mAllDates == null && pickerStrList != null) {
				PickerStr pickerStr = pickerStrList.get(index);
				return pickerStr.getValue();
			}

		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 1;
	}

	@Override
	public int getMaximumLength() {
		int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
		int maxLen = Integer.toString(max).length();
		if (minValue < 0) {
			maxLen++;
		}
		return maxLen;
	}

	private String getWeekDayByBig(int weekDay) {
		switch (weekDay) {
		case 1:
			return "日";
		case 2:
			return "一";
		case 3:
			return "二";
		case 4:
			return "三";
		case 5:
			return "四";
		case 6:
			return "五";
		case 7:
			return "六";
		}
		return "日";
	}

	private String showItemValue(GregorianCalendar e, int index) {
		if (e == null)
			return "";
		int itemYear = e.get(Calendar.YEAR);
		int itemMonth = e.get(Calendar.MONTH) + 1;
		int itemDay = e.get(Calendar.DAY_OF_MONTH);
		Calendar c = Calendar.getInstance();// 获得系统当前日期
		int nowyear = c.get(Calendar.YEAR);
		int nowmonth = c.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
		int nowday = c.get(Calendar.DAY_OF_MONTH);

		String str1 = SharedPre1.getStrDate(mContext, Contains.dateStr);
		int count = Integer.parseInt(str1);

		if (count == 1) {
			return itemMonth + "月 " + itemDay + "日    ";
		} else {

			if (itemYear == nowyear && itemMonth == nowmonth && itemDay == nowday) {
				nowIndex = index;
				return "今天";
			} else {
				// String upDown = ((PickerActivity)mContext).getUpDown();
				// int pickerNowYear =
				// ((PickerActivity)mContext).getPickerYear();
				// if("UP-PRE".equals(upDown)){
				// System.out.println("------UP-PRE--------");
				// if(itemMonth == 12 && itemDay >= 10){
				// Calendar caledar = Calendar.getInstance();
				// try {
				// Date date =
				// sdf.parse(pickerNowYear-1+"-"+itemMonth+"-"+itemDay);
				// caledar.setTime(date);
				// } catch (Exception e2) {
				// // TODO: handle exception
				// e2.printStackTrace();
				// }
				// return itemMonth + "月 " + itemDay + "日 " + "周"
				// + getWeekDayByBig(caledar.get(Calendar.DAY_OF_WEEK));
				// }
				// }else if("DOWN-NEXT".equals(upDown)){
				// System.out.println("------DOWN-NEXT--------");
				// if(itemMonth == 1 && itemDay < 10){
				// Calendar caledar = Calendar.getInstance();
				// try {
				// Date date =
				// sdf.parse(pickerNowYear+1+"-"+itemMonth+"-"+itemDay);
				// caledar.setTime(date);
				// } catch (Exception e2) {
				// // TODO: handle exception
				// e2.printStackTrace();
				// }
				// return itemMonth + "月 " + itemDay + "日 " + "周"
				// + getWeekDayByBig(caledar.get(Calendar.DAY_OF_WEEK));
				// }
				// }else if("DOWN".equals(upDown)){
				// System.out.println("------DOWN--------");
				// if(itemMonth == 1 && itemDay < 10){
				// Calendar caledar = Calendar.getInstance();
				// try {
				// Date date =
				// sdf.parse(pickerNowYear+1+"-"+itemMonth+"-"+itemDay);
				// caledar.setTime(date);
				// } catch (Exception e2) {
				// // TODO: handle exception
				// e2.printStackTrace();
				// }
				// return itemMonth + "月 " + itemDay + "日 " + "周"
				// + getWeekDayByBig(caledar.get(Calendar.DAY_OF_WEEK));
				// }
				// }else if("UP".equals(upDown)){
				// System.out.println("------DOWN--------");
				// if(itemMonth == 12 && itemDay < 10){
				// Calendar caledar = Calendar.getInstance();
				// try {
				// Date date =
				// sdf.parse(pickerNowYear-1+"-"+itemMonth+"-"+itemDay);
				// caledar.setTime(date);
				// } catch (Exception e2) {
				// // TODO: handle exception
				// e2.printStackTrace();
				// }
				// return itemMonth + "月 " + itemDay + "日 " + "周"
				// + getWeekDayByBig(caledar.get(Calendar.DAY_OF_WEEK));
				// }
				// }
				return itemMonth + "月 " + itemDay + "日    " + "周" + getWeekDayByBig(e.get(Calendar.DAY_OF_WEEK));
			}
		}
	}

	public int getNowIndex() {
		return nowIndex;
	}

	public String getConfirmValue(int index) {
		if (index >= 0 && index < getItemsCount()) {
			if (mAllDates == null) {
				int value = minValue + index;
				return format != null ? String.format(format, value) : Integer.toString(value);
			} else {
				GregorianCalendar e = mAllDates[index];
				int itemYear = e.get(Calendar.YEAR);
				int itemMonth = e.get(Calendar.MONTH) + 1;
				int itemDay = e.get(Calendar.DAY_OF_MONTH);
				return itemYear + "-" + String.format("%02d", itemMonth) + "-" + String.format("%02d", itemDay);
			}

		}
		return "";
	}

	public boolean isUp() {
		return isUp;
	}

	public void setUp(boolean isUp) {
		this.isUp = isUp;
	}

}
