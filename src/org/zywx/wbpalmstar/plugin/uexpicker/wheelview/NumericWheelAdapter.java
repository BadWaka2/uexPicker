package org.zywx.wbpalmstar.plugin.uexpicker.wheelview;

import android.text.TextUtils;
import android.util.Log;

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

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;

	// format
	@SuppressWarnings("unused")
	private String format;
	private String label;

	/**
	 * Default constructor
	 */
	public NumericWheelAdapter() {
		this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, null);
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericWheelAdapter(int minValue, int maxValue, String label) {
		this(minValue, maxValue, null, label);
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
	public NumericWheelAdapter(int minValue, int maxValue, String format,
			String label) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
		this.label = label;
	}

	@Override
	public String getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value = minValue + index;
			String valueStr=value + (TextUtils.isEmpty(label) ? "" : " " + label);
			Log.i("valueStr", "valueStr==="+valueStr);
			return valueStr;
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
		Log.i("NumericWheelAdapter", "maxLen=="+maxLen);
		return maxLen;
	}

	@Override
	public String getConfirmValue(int index) {
		// TODO Auto-generated method stub
		if (index >= 0 && index < getItemsCount()) {
			int value = minValue + index;
			Log.i("NumericWheelAdapter", "value=="+value);
			return "" + value;
		}
		return null;
	}
}
