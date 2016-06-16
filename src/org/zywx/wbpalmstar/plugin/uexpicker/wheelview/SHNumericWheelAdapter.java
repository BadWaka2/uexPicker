package org.zywx.wbpalmstar.plugin.uexpicker.wheelview;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.zywx.wbpalmstar.plugin.uexpicker.PickerStr;

import android.content.Context;

public class SHNumericWheelAdapter extends NumericWheelAdapter1 {

	public SHNumericWheelAdapter() {
		super();
	}

	public SHNumericWheelAdapter(int minValue, int maxValue, GregorianCalendar[] allDates, ArrayList<PickerStr> pickerStrList, Context context) {
		super(minValue, maxValue, allDates, pickerStrList, context);
	}

	public SHNumericWheelAdapter(int minValue, int maxValue, String format, GregorianCalendar[] allDates, ArrayList<PickerStr> pickerStrList, Context context) {
		super(minValue, maxValue, format, allDates, pickerStrList, context);
	}

	@Override
	public String getItem(int index) {
		int i = index % 2;
		int value = i * 30;
		return format != null ? String.format(format, value) : Integer.toString(value);
	}

}
