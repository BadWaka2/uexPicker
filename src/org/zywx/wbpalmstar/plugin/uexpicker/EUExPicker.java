package org.zywx.wbpalmstar.plugin.uexpicker;

import java.util.ArrayList;
import java.util.HashSet;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexpicker.utils.MLog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;

public class EUExPicker extends EUExBase {

	// 神华国能定制版
	public static final String CALLBACK_LOAD_DATA = "uexPicker.loadData";
	public static final String CALLBACK_RESULT_DATA = "uexPicker.resultData";
	private PickerStyle pickerStyle;
	private HashSet<String> tmIdSet = new HashSet<String>();
	private static String TAG = "EUExPicker";
	private String etmId;

	/**
	 * 选择器Fragment
	 */
	private PickerFragment mPickerFragment;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param inParent
	 */
	public EUExPicker(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	/**
	 * clean
	 */
	@Override
	protected boolean clean() {
		for (String tmId : tmIdSet) {
			if (!TextUtils.isEmpty(tmId)) {
				closeUexPickerList(tmId);
			}
		}
		tmIdSet.clear();
		return false;
	}

	/**
	 * 打开Picker
	 * 
	 * @param params
	 */
	public void open(String[] params) {

		MLog.getIns().d("params.length = " + params.length);

		try {
			final String tmId = params[0];
			tmIdSet.add(tmId);
			final float x = Float.parseFloat(params[1]);
			final float y = Float.parseFloat(params[2]);
			final float w = Float.parseFloat(params[3]);
			final float h = Float.parseFloat(params[4]);
			final int type = Integer.parseInt(params[5]);
			final String listStyle = params[6];
			if (params.length == 8) {
				final String countCode = params[7];
				SharedPre1.setStrDate(countCode, mContext, Contains.dateStr);
			} else {
				SharedPre1.setStrDate("0", mContext, Contains.dateStr);
			}

			pickerStyle = PickerData.parsePickerStyleJson(listStyle);
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (mPickerFragment != null) {
						removeFragmentFromWindow(mPickerFragment);
						mPickerFragment = null;
					}

					etmId = tmId;
					Bundle bundle = new Bundle();
					bundle.putInt("showType", type);
					bundle.putSerializable("pickerStyle", pickerStyle);
					bundle.putInt("x", (int) x);
					bundle.putInt("y", (int) y);
					bundle.putInt("w", (int) w);
					bundle.putInt("h", (int) h);

					mPickerFragment = PickerFragment.newInstance(bundle);

					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) w, (int) h);
					lp.leftMargin = (int) x;
					lp.topMargin = (int) y;
					addFragmentToCurrentWindow(mPickerFragment, lp, TAG);

					String js = SCRIPT_HEADER + "if(" + CALLBACK_LOAD_DATA + "){" + CALLBACK_LOAD_DATA + "('" + tmId + "','" + type + "');}";
					onCallback(js);

					if (mPickerFragment != null)
						mPickerFragment.setOnCloseListener(new OnCloseListener() {

							@Override
							public void onClose(String str) {
								closeUexPickerList(etmId);
								if (str != null && str.length() > 0) {
									String js = SCRIPT_HEADER + "if(" + CALLBACK_RESULT_DATA + "){" + CALLBACK_RESULT_DATA + "('" + str + "');}";
									onCallback(js);
								}
							}
						});
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setJsonData(final String[] params) {
		if (params.length != 2)
			return;
		final int type = Integer.parseInt(params[0]);
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mPickerFragment != null) {
					if (type == 2) {
						final ArrayList<PickerStr> pickerStrList = PickerData.parseBookJson(params[1]);
						if (pickerStrList == null) {
							return;
						}
						mPickerFragment.setData(pickerStrList);
					}
					mPickerFragment.setOnCloseListener(new OnCloseListener() {

						@Override
						public void onClose(String str) {
							// TODO Auto-generated method stub
							closeUexPickerList(etmId);
							if (str != null && str.length() > 0) {
								String js = SCRIPT_HEADER + "if(" + CALLBACK_RESULT_DATA + "){" + CALLBACK_RESULT_DATA + "('" + str + "');}";
								onCallback(js);
							}
						}
					});
				}
			}
		});

	}

	public void close(String[] params) {
		if (params.length != 1) {
			return;
		}
		String[] paramsArray = params[0].split(",");
		if (paramsArray != null) {
			for (int i = 0, length = paramsArray.length; i < length; i++) {
				final String tmId = paramsArray[i];
				if (TextUtils.isEmpty(tmId)) {
					return;
				}
				closeUexPickerList(tmId);
				tmIdSet.remove(tmId);
			}
		}

	}

	private void closeUexPickerList(final String tmId) {
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mPickerFragment != null) {
					removeFragmentFromWindow(mPickerFragment);
				}
			}
		});
	}

	public interface OnCloseListener {
		public void onClose(String str);
	}
}
