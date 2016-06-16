package org.zywx.wbpalmstar.plugin.uexpicker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PickerData {

	private static final String KEY_PICKER_CONFIG = "pickerConfig";
	private static final String KEY_FRAME_BG_COLOR = "frameBgColor";
	private static final String KEY_FRAME_BROUND_COLOR = "frameBroundColor";
	private static final String KEY_ANGLE = "angle";
	private static final String KEY_FRAME_BG_IMG = "frameBgImg";
	private static final String KEY_FONT_SIZE = "fontSize";
	private static final String KEY_FONT_COLOR = "fontColor";
	private static final String KEY_SELBG_COLOR = "selbgColor";
	private static final String KEY_SELFONT_COLOR = "selFontColor";
	private static final String KEY_SELBG_IMG = "selbgImg";
	private static final String KEY_BTNBG_IMG = "btnNBgImg";
	private static final String KEY_BTNFONT_SIZE = "btnFontSize";
	private static final String KEY_BTN_SIZE = "btnSize";
	private static final String KEY_BTN_TEXT_LEFT = "btnTextLeft";
	private static final String KEY_BTN_TEXT_RIGHT = "btnTextRight";
	private static final String KEY_DATE_LIMIT_START = "limitDateStart";
	private static final String KEY_DATE_LIMIT_END = "limitDateEnd";

	private static final String KEY_TYPEID = "typeId";
	private static final String KEY_TYPENAME = "typeName";

	public static PickerStyle parsePickerStyleJson(String pickerStyleStr) {
		// TODO Auto-generated method stub
		if (pickerStyleStr == null || pickerStyleStr.length() == 0) {
			return null;
		}
		PickerStyle pickerStyle = null;
		try {
			JSONObject objJson = new JSONObject(pickerStyleStr);
			if (objJson != null) {
				JSONObject pickerJson = objJson
						.optJSONObject(KEY_PICKER_CONFIG);
				if (pickerJson != null) {
					pickerStyle = new PickerStyle();
					pickerStyle.setFrameBgColor(pickerJson
							.optString(KEY_FRAME_BG_COLOR));
					pickerStyle.setFrameBroundColor(pickerJson
							.optString(KEY_FRAME_BROUND_COLOR));
					pickerStyle.setAngle(pickerJson.optString(KEY_ANGLE));
					pickerStyle.setFrameBgImg(pickerJson
							.optString(KEY_FRAME_BG_IMG));
					pickerStyle
							.setFontSize(pickerJson.optString(KEY_FONT_SIZE));
					pickerStyle.setFontColor(pickerJson
							.optString(KEY_FONT_COLOR));
					pickerStyle.setSelbgColor(pickerJson
							.optString(KEY_SELBG_COLOR));
					pickerStyle.setSelFontColor(pickerJson
							.optString(KEY_SELFONT_COLOR));
					pickerStyle
							.setSelbgImg(pickerJson.optString(KEY_SELBG_IMG));
					pickerStyle
							.setBtnBgImg(pickerJson.optString(KEY_BTNBG_IMG));
					pickerStyle.setBtnFontSize(pickerJson
							.optString(KEY_BTNFONT_SIZE));
					pickerStyle.setBtnSize(pickerJson.optString(KEY_BTN_SIZE));
					pickerStyle.setBtnTextLeft(pickerJson
							.optString(KEY_BTN_TEXT_LEFT));
					pickerStyle.setBtnTextRight(pickerJson
							.optString(KEY_BTN_TEXT_RIGHT));
					pickerStyle.setLimitDateStart(pickerJson
							.optString(KEY_DATE_LIMIT_START));
					pickerStyle.setLimitDateEnd(pickerJson
							.optString(KEY_DATE_LIMIT_END));
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pickerStyle;
	}

	public static ArrayList<PickerStr> parseBookJson(String msg) {
		// TODO Auto-generated method stub
		if (msg == null || msg.length() == 0) {
			return null;
		}
		try {
			JSONArray jsonArray = new JSONArray(msg);
			if (jsonArray != null) {
				ArrayList<PickerStr> pickerStrList = new ArrayList<PickerStr>();
				for (int i = 0, size = jsonArray.length(); i < size; i++) {
					JSONObject pickerObj = jsonArray.optJSONObject(i);
					if (pickerObj != null) {
						PickerStr pickerStr = new PickerStr();
						pickerStr.setId(pickerObj.optString(KEY_TYPEID));
						pickerStr.setValue(pickerObj.optString(KEY_TYPENAME));
						pickerStrList.add(pickerStr);
					}
				}
				return pickerStrList;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
