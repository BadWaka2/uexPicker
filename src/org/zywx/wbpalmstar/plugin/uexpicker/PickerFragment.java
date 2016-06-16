package org.zywx.wbpalmstar.plugin.uexpicker;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.zywx.wbpalmstar.base.view.BaseFragment;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexpicker.EUExPicker.OnCloseListener;
import org.zywx.wbpalmstar.plugin.uexpicker.wheelview.NumericWheelAdapter;
import org.zywx.wbpalmstar.plugin.uexpicker.wheelview.NumericWheelAdapter1;
import org.zywx.wbpalmstar.plugin.uexpicker.wheelview.OnWheelChangedListener;
import org.zywx.wbpalmstar.plugin.uexpicker.wheelview.SHNumericWheelAdapter;
import org.zywx.wbpalmstar.plugin.uexpicker.wheelview.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PickerFragment extends BaseFragment {

	public static final int DATE_TYPE = 0; // 日期选择器
	public static final int HOUR_MIN_TYPE = 1; // 时间选择器
	public static final int DATA_TYPE = 2; // 事件选择器
	private int showType = 0;
	/*** picker */
	private GregorianCalendar[] allDates;
	private int nowDateIndex;
	private WheelView wv_day;
	private WheelView wv_str;
	private Calendar calendar = Calendar.getInstance(); // 系统当前日期
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private int START_YEAR = 1900, END_YEAR = 2100;
	private int START_MONTH = 1, END_MONTH = 12;
	private int START_DAY = 1, END_DAY = 31;
	private int pickerYear = 2013;
	private String comfirmDate = "";
	private PickerStyle pickerStyle;
	// private boolean isYear4 = false; // 判断是否是闰年操作
	private OnCloseListener onCloseListener;
	private String upDown = "";
	private Dialog showDialog;

	int count;

	private List<Integer> listInt = null;
	private List<Integer> listIntHM = null;
	private List<Integer> listIntYMD = null;
	private List<Integer> listIntYM = null;

	int hg;

	/**
	 * 构造方法
	 */
	public PickerFragment() {

	}

	/**
	 * newInstance(推荐使用)
	 * 
	 * @param bundle
	 * @return
	 */
	public static PickerFragment newInstance(Bundle bundle) {
		PickerFragment fragment = new PickerFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * onCreate
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获得新建时传入的数据bundle
		Bundle bundle = this.getArguments();

		showType = bundle.getInt("showType", 0);
		pickerStyle = (PickerStyle) bundle.getSerializable("pickerStyle");
		initDate();
		showDialog = new Dialog(this.getActivity(), EUExUtil.getResStyleID("Style_platform_dialog"));
		showDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		showDialog.setCanceledOnTouchOutside(true);
		Window dialogWindow = showDialog.getWindow();
		WindowManager.LayoutParams lps = dialogWindow.getAttributes();

		listInt = new ArrayList<Integer>();
		listIntHM = new ArrayList<Integer>();
		listIntYMD = new ArrayList<Integer>();
		listIntYM = new ArrayList<Integer>();

		String str1 = SharedPre1.getStrDate(this.getActivity(), Contains.dateStr);
		count = Integer.parseInt(str1);
		if (count == 1) {
			getTime();
			getHM();
			getYMD2();
			getYM();
		}

		lps.gravity = Gravity.FILL_HORIZONTAL;
		int wd = bundle.getInt("w", 0);
		hg = bundle.getInt("h", 0);

		lps.x = bundle.getInt("x", 0);
		lps.y = bundle.getInt("y", 0);
		lps.width = wd;
		lps.height = hg;

		Log.i("WheelView", hg / 5 + "====" + hg);

		// lps.height = (int) (wd * 0.6); // 高度设置为屏幕的0.6
		// lps.width = (int) (hg * 0.65); // 宽度设置为屏幕的0.95

		Log.i("getIntExtra", "wd===" + wd + "==hg==" + hg);

		// lps.windowAnimations =
		// EUExUtil.getResStyleID("Anim_platform_window_actionsheet_dialog");
		lps.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lps.dimAmount = 0.0f;
		// 模糊度
		// showDialog.getWindow().setAttributes(lps);
		dialogWindow.setAttributes(lps);

		if (showType == 0 || showType == 1 || showType == 5) {
			int nowYear = calendar.get(Calendar.YEAR);
			String start_year;
			String end_year;
			if (!("[]".equals(listInt.toString()))) {
				nowYear = listInt.get(0);
				pickerYear = nowYear;
				start_year = nowYear + "-1-1";
				end_year = nowYear + "-12-31";
			} else {
				pickerYear = nowYear;
				start_year = nowYear + "-1-1";
				end_year = nowYear + "-12-31";
			}
			allDates = getBetweenDate(start_year, end_year);
			// LinearLayout.LayoutParams lay=new LinearLayout.LayoutParams(800,
			// 800);
			try {
				showDialog.setContentView(showDateTimePicker());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (showType == 2) {
			showDialog.setContentView(showPickerStr());
		} else if (showType == 3) {
			showDialog.setContentView(showYMD(3));
		} else if (showType == 4) {
			showDialog.setContentView(showYMD(4));
		}
		showDialog.show();
	}

	/**
	 * limit date init
	 */
	private void initDate() {
		if (pickerStyle != null) {
			String limitDateStart = pickerStyle.getLimitDateStart();
			String limitDateEnd = pickerStyle.getLimitDateEnd();
			try {
				if (!TextUtils.isEmpty(limitDateStart)) {
					String[] startDate = limitDateStart.split("-");
					START_YEAR = Integer.valueOf(startDate[0]);
					START_MONTH = Integer.valueOf(startDate[1]);
					START_DAY = Integer.valueOf(startDate[2]);
				}
				if (!TextUtils.isEmpty(limitDateEnd)) {
					String[] endDate = limitDateEnd.split("-");
					END_YEAR = Integer.valueOf(endDate[0]);
					END_MONTH = Integer.valueOf(endDate[1]);
					END_DAY = Integer.valueOf(endDate[2]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @Description: TODO 弹出日期、时间选择器
	 */
	@SuppressWarnings("deprecation")
	private View showDateTimePicker() {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		// 找到dialog的布局文件
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = null;
		if (showType == 0) {
			view = inflater.inflate(EUExUtil.getResLayoutID("plugin_uexpicker_date_layout"), null);
		} else if (showType == 1 || showType == 5) {
			view = inflater.inflate(EUExUtil.getResLayoutID("plugin_uexpicker_time_layout"), null);
		}
		if (pickerStyle != null && view != null) {
			RelativeLayout frame_relative = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("frame_relative"));
			final RelativeLayout pickerLayout = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("pickerLayout"));
			View viewline = view.findViewById(EUExUtil.getResIdID("viewline"));
			int frameBoundColor = ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor());
			int radius = 0;
			if ("Y".equals(pickerStyle.getAngle())) {
				radius = 10;
			}
			GradientDrawable gradientFrameBound = new GradientDrawable();
			gradientFrameBound.setColor(frameBoundColor);
			gradientFrameBound.setCornerRadius(radius);
			frame_relative.setBackgroundDrawable(gradientFrameBound);
			viewline.setBackgroundColor(frameBoundColor);
			if (TextUtils.isEmpty(pickerStyle.getFrameBgImg())) {
				GradientDrawable gradientFrameBg = new GradientDrawable();
				gradientFrameBg.setColor(ImageColorUtils.parseColor(pickerStyle.getFrameBgColor()));
				gradientFrameBg.setCornerRadius(radius);
				pickerLayout.setBackgroundDrawable(gradientFrameBg);
			} else {
				Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getFrameBgImg());
				if (bitmap != null) {
					BitmapDrawable bd = new BitmapDrawable(bitmap);
					pickerLayout.setBackgroundDrawable(bd);
				}
			}

		}
		wv_day = (WheelView) view.findViewById(EUExUtil.getResIdID("day"));

		if (showType == 0) {
			// 年月日
			wv_day.setVisibility(View.VISIBLE);
			if (pickerStyle != null) {
				wv_day.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
				wv_day.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
				if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
					int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
					GradientDrawable gradient = new GradientDrawable();
					gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
					gradient.setColor(centerDrawable);
					wv_day.setCenterDrawable(gradient);
				} else {
					setSelPickerImg(wv_day);
				}
			}
			wv_day.setCyclic(true);
			wv_day.setAdapter(new NumericWheelAdapter1(1, allDates.length, allDates, null, PickerFragment.this.getActivity()));

			wv_day.setCurrentItem(nowDateIndex);

			comfirmDate = wv_day.getAdapter().getConfirmValue(nowDateIndex);
			final Calendar newCaledar = Calendar.getInstance();
			final Calendar oldCaledar = Calendar.getInstance();
			OnWheelChangedListener wheelChangedListener_day = new OnWheelChangedListener() {

				@Override
				public void onChanged(WheelView wheel, int oldValue, int newValue) {
					// TODO Auto-generated method stub
					try {
						Date oldDate = sdf.parse(wv_day.getAdapter().getConfirmValue(oldValue));
						Date newDate = sdf.parse(wv_day.getAdapter().getConfirmValue(newValue));
						newCaledar.setTime(newDate);
						oldCaledar.setTime(oldDate);
						int newmonth = newCaledar.get(Calendar.MONTH) + 1;
						int newday = newCaledar.get(Calendar.DAY_OF_MONTH);
						int oldmonth = oldCaledar.get(Calendar.MONTH) + 1;
						if (oldDate.before(newDate)) { // 滑轮向下滚动(日期增大)
							if (newmonth == 12 && oldmonth == 1) {
								pickerYear--;
								upDown = "DOWN-NEXT";
								// if (isYear4
								// || ((pickerYear % 4 == 0 && pickerYear % 100
								// != 0) || pickerYear % 400 == 0)) {
								String start_year = pickerYear + "-1-1";
								String end_year = pickerYear + "-12-31";
								allDates = getBetweenDate(start_year, end_year);
								// isYear4 = !isYear4;
								// }
								wv_day.setAdapter(new NumericWheelAdapter1(1, allDates.length, allDates, null, PickerFragment.this.getActivity()));
							} else {
								upDown = "DOWN";
							}
						} else if (oldDate.after(newDate)) {// 滑轮向上滚动(日期减小)
							if (newmonth == 1 && oldmonth == 12) {
								upDown = "UP-PRE";
								pickerYear++;
								// if (isYear4
								// || ((pickerYear % 4 == 0 && pickerYear % 100
								// != 0) || pickerYear % 400 == 0)) {
								String start_year = pickerYear + "-1-1";
								String end_year = pickerYear + "-12-31";
								allDates = getBetweenDate(start_year, end_year);
								// isYear4 = !isYear4;
								// }
								wv_day.setAdapter(new NumericWheelAdapter1(1, allDates.length, allDates, null, PickerFragment.this.getActivity()));
							} else {
								upDown = "UP";
							}
						}
						// 闰年
						comfirmDate = pickerYear + "-" + String.format("%02d", newmonth) + "-" + String.format("%02d", newday);
						// showdateTextView.setText(pickerYear+"-"+newmonth+"-"+newday);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};
			wv_day.addChangingListener(wheelChangedListener_day);
		}
		// 时
		final WheelView wv_hours = (WheelView) view.findViewById(EUExUtil.getResIdID("hour"));
		wv_hours.ADDITIONAL_ITEM_HEIGHT = hg / 5;

		if (pickerStyle != null) {
			wv_hours.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
			wv_hours.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
			if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
				int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
				GradientDrawable gradient = new GradientDrawable();
				gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
				gradient.setColor(centerDrawable);
				wv_hours.setCenterDrawable(gradient);
			} else {
				setSelPickerImg(wv_hours);
			}
		}
		wv_hours.setAdapter(new NumericWheelAdapter1(0, 23, null, null, PickerFragment.this.getActivity()));
		wv_hours.setCyclic(true);

		if (showType == 0) {
			if (count == 1) {
				// wv_day.setCurrentItem(listInt.get(2)-1);
				if ("[]".equals(listInt.toString())) {
					wv_hours.setCurrentItem(hour);
				} else {
					wv_hours.setCurrentItem(listInt.get(3));
				}
			} else {
				wv_hours.setCurrentItem(hour);
			}
		} else if (showType == 1) {
			if (count == 1) {
				// wv_day.setCurrentItem(listInt.get(2)-1);
				if ("[]".equals(listIntHM.toString())) {
					wv_hours.setCurrentItem(hour);
				} else {
					wv_hours.setCurrentItem(listIntHM.get(0));
				}
			} else {
				wv_hours.setCurrentItem(hour);
			}
		} else if (showType == 5) {
			// minute = (minute <= 30) ? 1 : 0;
			int currentMinute = minute;
			if (currentMinute > 30) {
				hour += 1;
			}
			if (count == 1) {
				// wv_day.setCurrentItem(listInt.get(2)-1);
				if ("[]".equals(listIntHM.toString())) {
					wv_hours.setCurrentItem(hour);
				} else {
					wv_hours.setCurrentItem(listIntHM.get(0));
				}
			} else {
				wv_hours.setCurrentItem(hour);
			}

		}
		// wv_hours.setCurrentItem(hour);
		// 分
		final WheelView wv_mins = (WheelView) view.findViewById(EUExUtil.getResIdID("mins"));
		wv_mins.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		if (pickerStyle != null) {
			wv_mins.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
			wv_mins.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
			if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
				int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
				GradientDrawable gradient = new GradientDrawable();
				gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
				gradient.setColor(centerDrawable);
				wv_mins.setCenterDrawable(gradient);
			} else {
				setSelPickerImg(wv_mins);
			}
		}
		if (showType == 5) {// 分钟数只显示00和30

			wv_mins.setAdapter(new SHNumericWheelAdapter(0, 1, "%02d", null, null, PickerFragment.this.getActivity()));
			wv_mins.setCyclic(false);
		} else {
			wv_mins.setAdapter(new NumericWheelAdapter1(0, 59, "%02d", null, null, PickerFragment.this.getActivity()));
			wv_mins.setCyclic(false);
		}

		if (showType == 0) {
			if (count == 1) {
				if ("[]".equals(listInt.toString())) {
					wv_mins.setCurrentItem(minute);
				} else {
					wv_mins.setCurrentItem(listInt.get(4));
				}
			} else {
				wv_mins.setCurrentItem(minute);
			}
		} else if (showType == 1) {
			if (count == 1) {
				// wv_day.setCurrentItem(listInt.get(2)-1);
				if ("[]".equals(listIntHM.toString())) {
					wv_mins.setCurrentItem(minute);
				} else {
					wv_mins.setCurrentItem(listIntHM.get(1));
				}
			} else {
				wv_mins.setCurrentItem(minute);
			}
		} else if (showType == 5) {
			minute = (minute > 30) ? 0 : 1;
			if (count == 1) {
				// wv_day.setCurrentItem(listInt.get(2)-1);
				if ("[]".equals(listIntHM.toString())) {
					wv_mins.setCurrentItem(minute);
				} else {
					wv_mins.setCurrentItem(listIntHM.get(1) / 30);
				}
			} else {
				wv_mins.setCurrentItem(minute);
			}
		}
		// wv_mins.setCurrentItem(minute);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 15;
		if (!TextUtils.isEmpty(pickerStyle.getFontSize())) {
			try {
				// textSize = Integer.parseInt(pickerStyle.getFontSize());
				// textSize =
				// (int)(Integer.parseInt(pickerStyle.getFontSize())/1.5);
				textSize = px2dip(PickerFragment.this.getActivity(), Integer.parseInt(pickerStyle.getFontSize()));
				Log.i("textSize1", "textSize===" + textSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (showType == 0)
			wv_day.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

		final Button btn_sure = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_sure"));
		final Button btn_cancel = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_cancel"));
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextRight())) {
			btn_sure.setText(pickerStyle.getBtnTextRight());
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextLeft())) {
			btn_cancel.setText(pickerStyle.getBtnTextLeft());
		}
		try {
			int btnSize = Integer.parseInt(pickerStyle.getBtnFontSize());
			btn_sure.setTextSize(btnSize);
			btn_cancel.setTextSize(btnSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnBgImg())) {
			Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getBtnBgImg());
			if (bitmap != null) {
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				btn_sure.setBackgroundDrawable(bd);
				btn_cancel.setBackgroundDrawable(bd);
			}
		}
		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 如果是个数,则显示为"02"的样式
				// 设置日期的显示
				String selectDate = "";
				if (showType == 0) {
					selectDate = comfirmDate + " " + String.format("%02d", wv_hours.getCurrentItem()) + ":" + String.format("%02d", wv_mins.getCurrentItem());
				} else if (showType == 1) {
					selectDate = String.format("%02d", wv_hours.getCurrentItem()) + ":" + String.format("%02d", wv_mins.getCurrentItem());
				} else if (showType == 5) {
					selectDate = String.format("%02d", wv_hours.getCurrentItem()) + ":" + String.format("%02d", wv_mins.getCurrentItem() * 30);
				}
				if (showDialog != null)
					showDialog.dismiss();
				if (onCloseListener != null)
					if (showType == 0) {
						SharedPre1.setStrDate(PickerFragment.this.getActivity(), Contains.harvestDate, selectDate);

					} else if (showType == 1) {
						SharedPre1.setStrDate(PickerFragment.this.getActivity(), Contains.harvestDate1, selectDate);

					} else if (showType == 5) {
						SharedPre1.setStrDate(PickerFragment.this.getActivity(), Contains.harvestDate5, selectDate);
					}
				Log.i("selectDateselectDate", "selectDate=0=0=" + selectDate);
				onCloseListener.onClose(selectDate);
			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (showDialog != null)
					showDialog.dismiss();
				if (onCloseListener != null)
					onCloseListener.onClose(null);
			}
		});

		// int w=view.getWidth();
		// int h=view.getHeight();
		// Log.i("getWidth", "W==="+w+"===h==="+h);
		return view;
	}

	/**
	 * 获取文字选择器
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private View showPickerStr() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(EUExUtil.getResLayoutID("plugin_uexpicker_str_layout"), null);
		if (pickerStyle != null && view != null) {
			RelativeLayout frame_relative = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("frame_relative"));
			final RelativeLayout pickerLayout = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("pickerLayout"));
			View viewline = view.findViewById(EUExUtil.getResIdID("viewline"));
			int frameBoundColor = ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor());
			int radius = 0;
			if ("Y".equals(pickerStyle.getAngle())) {
				radius = 10;
			}
			GradientDrawable gradientFrameBound = new GradientDrawable();
			gradientFrameBound.setColor(frameBoundColor);
			gradientFrameBound.setCornerRadius(radius);
			frame_relative.setBackgroundDrawable(gradientFrameBound);
			viewline.setBackgroundColor(frameBoundColor);
			if (TextUtils.isEmpty(pickerStyle.getFrameBgImg())) {
				GradientDrawable gradientFrameBg = new GradientDrawable();
				gradientFrameBg.setColor(ImageColorUtils.parseColor(pickerStyle.getFrameBgColor()));
				gradientFrameBg.setCornerRadius(radius);
				pickerLayout.setBackgroundDrawable(gradientFrameBg);
			} else {
				Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getFrameBgImg());
				if (bitmap != null) {
					BitmapDrawable bd = new BitmapDrawable(bitmap);
					pickerLayout.setBackgroundDrawable(bd);
				}
			}

		}
		wv_str = (WheelView) view.findViewById(EUExUtil.getResIdID("str_wheel"));
		wv_str.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		if (pickerStyle != null) {
			wv_str.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
			wv_str.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
			if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
				int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
				GradientDrawable gradient = new GradientDrawable();
				gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
				gradient.setColor(centerDrawable);
				wv_str.setCenterDrawable(gradient);
			} else {
				setSelPickerImg(wv_str);
			}
		}
		wv_str.setCyclic(true);
		int textSize = 15;
		if (!TextUtils.isEmpty(pickerStyle.getFontSize())) {
			try {
				// textSize = Integer.parseInt(pickerStyle.getFontSize());
				// textSize =
				// (int)(Integer.parseInt(pickerStyle.getFontSize())/1.5);
				textSize = px2dip(PickerFragment.this.getActivity(), Integer.parseInt(pickerStyle.getFontSize()));
				Log.i("textSize", "textSize=2==" + textSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		wv_str.TEXT_SIZE = textSize;
		wv_str.setAdapter(new NumericWheelAdapter1(1, 0, null, null, PickerFragment.this.getActivity()));
		final Button btn_sure = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_sure"));
		try {
			btn_sure.setTextSize(Integer.parseInt(pickerStyle.getBtnFontSize()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Button btn_cancel = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_cancel"));
		try {
			btn_cancel.setTextSize(Integer.parseInt(pickerStyle.getBtnFontSize()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextRight())) {
			btn_sure.setText(pickerStyle.getBtnTextRight());
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextLeft())) {
			btn_cancel.setText(pickerStyle.getBtnTextLeft());
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnBgImg())) {
			Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getBtnBgImg());
			if (bitmap != null) {
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				btn_sure.setBackgroundDrawable(bd);
				btn_cancel.setBackgroundDrawable(bd);
			}
		}

		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (showDialog != null)
					showDialog.dismiss();
				// 如果是个数,则显示为"02"的样式
				// 设置日期的显示
				if (onCloseListener != null) {
					String date11 = wv_str.getAdapter().getItem(wv_str.getCurrentItem());
					Log.i("date11", "date11-----" + date11);
					onCloseListener.onClose(date11);
				}

			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (showDialog != null)
					showDialog.dismiss();
				if (onCloseListener != null)
					onCloseListener.onClose(null);
			}
		});
		return view;
	}

	// 处理年月日选择器
	@SuppressWarnings("deprecation")
	private View showYMD(final int type) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DATE);
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 找到dialog的布局文件s
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(EUExUtil.getResLayoutID("plugin_uexpicker_ymd_layout"), null);
		if (pickerStyle != null && view != null) {
			RelativeLayout frame_relative = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("frame_relative"));
			final RelativeLayout pickerLayout = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("pickerLayout"));
			View viewline = view.findViewById(EUExUtil.getResIdID("viewline"));
			int frameBoundColor = ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor());
			int radius = 0;
			if ("Y".equals(pickerStyle.getAngle())) {
				radius = 10;
			}
			GradientDrawable gradientFrameBound = new GradientDrawable();
			gradientFrameBound.setColor(frameBoundColor);
			gradientFrameBound.setCornerRadius(radius);
			frame_relative.setBackgroundDrawable(gradientFrameBound);
			viewline.setBackgroundColor(frameBoundColor);
			if (TextUtils.isEmpty(pickerStyle.getFrameBgImg())) {
				GradientDrawable gradientFrameBg = new GradientDrawable();
				gradientFrameBg.setColor(ImageColorUtils.parseColor(pickerStyle.getFrameBgColor()));
				gradientFrameBg.setCornerRadius(radius);
				pickerLayout.setBackgroundDrawable(gradientFrameBg);
			} else {
				Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getFrameBgImg());
				if (bitmap != null) {
					BitmapDrawable bd = new BitmapDrawable(bitmap);
					pickerLayout.setBackgroundDrawable(bd);
				}
			}
		}
		// 年
		final WheelView wv_year = (WheelView) view.findViewById(EUExUtil.getResIdID("year"));
		wv_year.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR, "年"));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动

		if (showType == 3) {
			if (count == 1) {
				if ("[]".equals(listIntYMD.toString())) {
					wv_year.setCurrentItem(year - START_YEAR);
				} else {
					wv_year.setCurrentItem(listIntYMD.get(0) - START_YEAR);
				}
			} else {
				wv_year.setCurrentItem(year - START_YEAR);
			}
		} else if (showType == 4) {
			if (count == 1) {
				if ("[]".equals(listIntYM.toString())) {
					wv_year.setCurrentItem(year - START_YEAR);
				} else {
					wv_year.setCurrentItem(listIntYM.get(0) - START_YEAR);
				}
			} else {
				wv_year.setCurrentItem(year - START_YEAR);
			}
		}
		// wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		if (pickerStyle != null) {
			wv_year.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
			wv_year.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
			if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
				int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
				GradientDrawable gradient = new GradientDrawable();
				gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
				gradient.setColor(centerDrawable);
				wv_year.setCenterDrawable(gradient);
			} else {
				setSelPickerImg(wv_year);
			}
		}

		// 月
		final WheelView wv_month = (WheelView) view.findViewById(EUExUtil.getResIdID("month"));
		wv_month.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		if (!checkLimitMonth(wv_month, year)) {
			wv_month.setAdapter(new NumericWheelAdapter(1, 12, "%02d", "月"));
		}
		wv_month.setCyclic(true);

		if (showType == 3) {
			if (count == 1) {
				if ("[]".equals(listIntYMD.toString())) {
					wv_month.setCurrentItem(month);
				} else {
					Log.i("listInt", listIntYMD.get(1) + "");
					wv_month.setCurrentItem(listIntYMD.get(1) - 1);
				}
			} else {
				wv_month.setCurrentItem(month);
			}
		} else if (showType == 4) {
			if (count == 1) {
				if ("[]".equals(listIntYM.toString())) {
					wv_month.setCurrentItem(month);
				} else {
					wv_month.setCurrentItem(listIntYM.get(1) - 1);
				}
			} else {
				wv_month.setCurrentItem(month);
			}
		}

		if (pickerStyle != null) {
			wv_month.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
			wv_month.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
			if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
				int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
				GradientDrawable gradient = new GradientDrawable();
				gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
				gradient.setColor(centerDrawable);
				wv_month.setCenterDrawable(gradient);
			} else {
				setSelPickerImg(wv_month);
			}
		}
		// 日
		final WheelView wv_day = (WheelView) view.findViewById(EUExUtil.getResIdID("day"));
		// TODO by waka
		wv_day.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		if (type == 3) {
			wv_day.setCyclic(true);
			wv_day.setVisibility(View.VISIBLE);
			checkLimitDay(wv_day, year, month + 1, list_big, list_little);
			// 判断大小月及是否闰年,用来确定"日"的数据
			/*
			 * if (list_big.contains(String.valueOf(month + 1))) {
			 * wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d", "日")); }
			 * else if (list_little.contains(String.valueOf(month + 1))) {
			 * wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d", "日")); }
			 * else { // 闰年 if ((year % 4 == 0 && year % 100 != 0) || year % 400
			 * == 0) wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d",
			 * "日")); else wv_day.setAdapter(new NumericWheelAdapter(1, 28,
			 * "%02d", "日")); }
			 */

			if (showType == 3) {
				if (count == 1) {
					if ("[]".equals(listIntYMD.toString())) {
						wv_day.setCurrentItem(day - 1);
					} else {
						wv_day.setCurrentItem(listIntYMD.get(2) - 1);
					}
				} else {
					wv_day.setCurrentItem(day - 1);
				}
			}
			// wv_day.setCurrentItem(day-1);
			if (pickerStyle != null) {
				wv_day.setITEMS_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getFontColor()));
				wv_day.setVALUE_TEXT_COLOR(ImageColorUtils.parseColor(pickerStyle.getSelFontColor()));
				if (TextUtils.isEmpty(pickerStyle.getSelbgImg())) {
					int centerDrawable = ImageColorUtils.parseColor(pickerStyle.getSelbgColor());
					GradientDrawable gradient = new GradientDrawable();
					gradient.setStroke(1, ImageColorUtils.parseColor(pickerStyle.getFrameBroundColor()));
					gradient.setColor(centerDrawable);
					wv_day.setCenterDrawable(gradient);
				} else {
					setSelPickerImg(wv_day);
				}
			}
		} else if (type == 4) {
			wv_day.setVisibility(View.GONE);
		}
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.ADDITIONAL_ITEM_HEIGHT = hg / 5;
				int year_num = newValue + START_YEAR;
				checkLimitMonth(wv_month, year_num);
				String monthValue = ((NumericWheelAdapter) wv_month.getAdapter()).getItem(wv_month.getCurrentItem());
				int currentMonth = Integer.valueOf(monthValue.split(" ")[0]);
				checkLimitDay(wv_day, year_num, currentMonth, list_big, list_little);
				/*
				 * // 判断大小月及是否闰年,用来确定"日"的数据 if (list_big
				 * .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
				 * wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d",
				 * "日")); } else if
				 * (list_little.contains(String.valueOf(wv_month
				 * .getCurrentItem() + 1))) { wv_day.setAdapter(new
				 * NumericWheelAdapter(1, 30, "%02d", "日")); } else { if
				 * ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400
				 * == 0) wv_day.setAdapter(new NumericWheelAdapter(1, 29,
				 * "%02d", "日")); else wv_day.setAdapter(new
				 * NumericWheelAdapter(1, 28, "%02d", "日")); }
				 */
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.ADDITIONAL_ITEM_HEIGHT = hg / 5;
				int currentYear = wv_year.getCurrentItem() + START_YEAR;
				String monthValue = ((NumericWheelAdapter) wheel.getAdapter()).getItem(newValue);
				int currentMonth = Integer.valueOf(monthValue.split(" ")[0]);
				checkLimitDay(wv_day, currentYear, currentMonth, list_big, list_little);
				/*
				 * int month_num = newValue + 1; int datItem =
				 * wv_day.getCurrentItem() + 1; Log.i("datItem", "datItem==" +
				 * datItem); // 判断大小月及是否闰年,用来确定"日"的数据 if
				 * (list_big.contains(String.valueOf(month_num))) {
				 * wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d",
				 * "日")); } else if
				 * (list_little.contains(String.valueOf(month_num))) {
				 * wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d",
				 * "日")); if (datItem == 31) { wv_day.setCurrentItem(0); } }
				 * else { if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0
				 * && (wv_year .getCurrentItem() + START_YEAR) % 100 != 0) ||
				 * (wv_year.getCurrentItem() + START_YEAR) % 400 == 0) {
				 * wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d",
				 * "日")); if (datItem == 30 || datItem == 31) {
				 * wv_day.setCurrentItem(0); } } else { wv_day.setAdapter(new
				 * NumericWheelAdapter(1, 28, "%02d", "日")); if (datItem == 29
				 * || datItem == 30 || datItem == 31) {
				 * wv_day.setCurrentItem(0); } } }
				 */
				// if(day)

			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 15;
		if (!TextUtils.isEmpty(pickerStyle.getFontSize())) {
			try {
				// textSize = Integer.parseInt(pickerStyle.getFontSize());
				// textSize =
				// (int)(Integer.parseInt(pickerStyle.getFontSize())/1.5);
				textSize = px2dip(PickerFragment.this.getActivity(), Integer.parseInt(pickerStyle.getFontSize()));
				Log.i("textSize", "textSize==3=" + textSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		final Button btn_sure = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_sure"));
		try {
			btn_sure.setTextSize(Integer.parseInt(pickerStyle.getBtnFontSize()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Button btn_cancel = (Button) view.findViewById(EUExUtil.getResIdID("btn_datetime_cancel"));
		try {
			btn_cancel.setTextSize(Integer.parseInt(pickerStyle.getBtnFontSize()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextRight())) {
			btn_sure.setText(pickerStyle.getBtnTextRight());
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnTextLeft())) {
			btn_cancel.setText(pickerStyle.getBtnTextLeft());
		}
		if (!TextUtils.isEmpty(pickerStyle.getBtnBgImg())) {
			Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getBtnBgImg());
			if (bitmap != null) {
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				btn_sure.setBackgroundDrawable(bd);
				btn_cancel.setBackgroundDrawable(bd);
			}
		}

		// 确定
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (showDialog != null)
					showDialog.dismiss();
				// 如果是个数,则显示为"02"的样式
				// 设置日期的显示
				if (onCloseListener != null) {
					String parten = "00";
					DecimalFormat decimal = new DecimalFormat(parten);
					if (type == 3) {
						String result = (wv_year.getCurrentItem() + START_YEAR) + "-";
						String monthCurrentValue = ((NumericWheelAdapter) wv_month.getAdapter()).getItem(wv_month.getCurrentItem()).split(" ")[0];
						result = result + decimal.format(Integer.valueOf(monthCurrentValue)) + "-";
						String dayCurrentValue = ((NumericWheelAdapter) wv_day.getAdapter()).getItem(wv_day.getCurrentItem()).split(" ")[0];
						result = result + decimal.format(Integer.valueOf(dayCurrentValue));
						Log.i("date11", "result===" + result);
						SharedPre1.setStrDate(PickerFragment.this.getActivity(), Contains.harvestDate3, result);
						Log.i("selectDateselectDate", "result=3==" + result);
						onCloseListener.onClose(result);
					} else if (type == 4) {
						String result = (wv_year.getCurrentItem() + START_YEAR) + "-" + decimal.format((wv_month.getCurrentItem() + 1));

						SharedPre1.setStrDate(PickerFragment.this.getActivity(), Contains.harvestDate4, result);
						Log.i("selectDateselectDate", "result=4==" + result);
						onCloseListener.onClose(result);
					}
				}
			}
		});
		// 取消
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (showDialog != null)
					showDialog.dismiss();
				if (onCloseListener != null)
					onCloseListener.onClose(null);
			}
		});
		return view;
	}

	/**
	 * 显示时间内容
	 * 
	 * @param pickerStrList
	 * @param pickerStyle
	 */
	public void setData(ArrayList<PickerStr> pickerStrList) {
		wv_str.setAdapter(new NumericWheelAdapter1(1, pickerStrList.size(), null, pickerStrList, PickerFragment.this.getActivity()));
		wv_str.setCurrentItem(0);

	}

	/**
	 * 获取指定时间内所有日期
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public GregorianCalendar[] getBetweenDate(String startDate, String endDate) {
		Vector<GregorianCalendar> v = new Vector<GregorianCalendar>();
		GregorianCalendar gc1 = new GregorianCalendar(), gc2 = new GregorianCalendar();
		try {
			gc1.setTime(sdf.parse(startDate));
			gc2.setTime(sdf.parse(endDate));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		int nowday;
		int nowyear;
		int nowmonth;
		if (count == 1) {
			if (listInt.toString().equals("[]")) {
				nowyear = calendar.get(Calendar.YEAR);
				nowmonth = calendar.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
				nowday = calendar.get(Calendar.DAY_OF_MONTH);
			} else {
				nowyear = listInt.get(0);
				nowmonth = listInt.get(1);// 系统日期从0开始算起
				nowday = listInt.get(2);
			}
		} else {
			nowyear = calendar.get(Calendar.YEAR);
			nowmonth = calendar.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
			nowday = calendar.get(Calendar.DAY_OF_MONTH);
		}

		int index = 0;
		do {
			GregorianCalendar gc3 = (GregorianCalendar) gc1.clone();
			int itemYear = gc3.get(Calendar.YEAR);
			int itemMonth = gc3.get(Calendar.MONTH) + 1;// 系统日期从0开始算起
			int itemDay = gc3.get(Calendar.DAY_OF_MONTH);
			if (itemYear == nowyear && itemMonth == nowmonth && itemDay == nowday) {
				nowDateIndex = index;
			}
			v.add(gc3);
			gc1.add(Calendar.DAY_OF_MONTH, 1);
			index++;
		} while (!gc1.after(gc2));
		// GregorianCalendar[] ga = v.toArray(new GregorianCalendar[v.size()]);
		// for(GregorianCalendar e:ga)
		// {
		// System.out.println(e.get(Calendar.YEAR)+"年 "+
		// +(e.get(Calendar.MONTH)+1)+"月 "+
		// e.get(Calendar.DAY_OF_MONTH)+"号"+
		// e.get(Calendar.DAY_OF_WEEK)+"周");
		// }
		return v.toArray(new GregorianCalendar[v.size()]);
	}

	// 设置点击监听
	public void setOnCloseListener(OnCloseListener onCloseListener) {
		this.onCloseListener = onCloseListener;
	}

	private void setSelPickerImg(final WheelView wheelView) {
		wheelView.ADDITIONAL_ITEM_HEIGHT = hg / 5;
		Bitmap bitmap = ImageColorUtils.getImage(PickerFragment.this.getActivity(), pickerStyle.getSelbgImg());
		if (bitmap != null) {
			bitmap = ImageColorUtils.setAlpha(bitmap, 50);
			@SuppressWarnings("deprecation")
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			wheelView.setCenterDrawable(bd);
		}
	}

	/**
	 * 监测当前是否有月份限制
	 * 
	 * @Date 2015-11-12
	 * @param wv_month
	 * @param currentYear
	 * @return isLimit
	 */
	private boolean checkLimitMonth(WheelView wv_month, int currentYear) {
		int currentMonthIndex = wv_month.getCurrentItem();
		if (START_MONTH != 1 || END_MONTH != 12) {
			if (START_YEAR == currentYear && END_YEAR == currentYear) {
				changeMonthWheelData(wv_month, START_MONTH, END_MONTH, currentMonthIndex);
				return true;
			} else if (START_YEAR == currentYear) {
				changeMonthWheelData(wv_month, START_MONTH, 12, currentMonthIndex);
				return true;
			} else if (END_YEAR == currentYear) {
				changeMonthWheelData(wv_month, 1, END_MONTH, currentMonthIndex);
				return true;
			}
		}
		return false;
	}

	/**
	 * check limit day
	 * 
	 * @Date 2015-11-12
	 * @param wv_day
	 * @param currentYear
	 * @param currentMonth
	 * @param list_big
	 * @param list_little
	 */
	private void checkLimitDay(WheelView wv_day, int currentYear, int currentMonth, List<String> list_big, List<String> list_little) {
		int currentDayIndex = wv_day.getCurrentItem();
		if (list_big.contains(String.valueOf(currentMonth))) {
			if ((currentYear == START_YEAR && currentYear == END_YEAR) && (currentMonth == START_MONTH && currentMonth == END_MONTH) && (START_DAY > 1 || END_DAY < 31)) {
				changeDayWheelData(wv_day, START_DAY, END_DAY, currentDayIndex);
			} else if (currentYear == START_YEAR && currentMonth == START_MONTH && START_DAY != 1) {
				changeDayWheelData(wv_day, START_DAY, 31, currentDayIndex);
			} else if (currentYear == END_YEAR && currentMonth == END_MONTH && END_DAY < 31) {
				changeDayWheelData(wv_day, 1, END_DAY, currentDayIndex);
			} else {
				changeDayWheelData(wv_day, 1, 31, currentDayIndex);
			}
		} else if (list_little.contains(String.valueOf(currentMonth))) {
			if ((currentYear == START_YEAR && currentYear == END_YEAR) && (currentMonth == START_MONTH && currentMonth == END_MONTH) && (START_DAY > 1 || END_DAY < 30)) {
				changeDayWheelData(wv_day, START_DAY, END_DAY, currentDayIndex);
			} else if (currentYear == START_YEAR && currentMonth == START_MONTH && START_DAY != 1) {
				changeDayWheelData(wv_day, START_DAY, 30, currentDayIndex);
			} else if (currentYear == END_YEAR && currentMonth == END_MONTH && END_DAY < 30) {
				changeDayWheelData(wv_day, 1, END_DAY, currentDayIndex);
			} else {
				changeDayWheelData(wv_day, 1, 30, currentDayIndex);
			}
		} else {
			if ((currentYear % 4 == 0 && currentYear % 100 != 0) || currentYear % 400 == 0) {
				if ((currentYear == START_YEAR && currentYear == END_YEAR) && (currentMonth == START_MONTH && currentMonth == END_MONTH) && (START_DAY > 1 || END_DAY < 29)) {
					changeDayWheelData(wv_day, START_DAY, END_DAY, currentDayIndex);
				} else if (currentYear == START_YEAR && currentMonth == START_MONTH && START_DAY != 1) {
					changeDayWheelData(wv_day, START_DAY, 29, currentDayIndex);
				} else if (currentYear == END_YEAR && currentMonth == END_MONTH && END_DAY < 29) {
					changeDayWheelData(wv_day, 1, END_DAY, currentDayIndex);
				} else {
					changeDayWheelData(wv_day, 1, 29, currentDayIndex);
				}
			} else {
				if ((currentYear == START_YEAR && currentYear == END_YEAR) && (currentMonth == START_MONTH && currentMonth == END_MONTH) && (START_DAY > 1 || END_DAY < 28)) {
					changeDayWheelData(wv_day, START_DAY, END_DAY, currentDayIndex);
				} else if (currentYear == START_YEAR && currentMonth == START_MONTH && START_DAY != 1) {
					changeDayWheelData(wv_day, START_DAY, 28, currentDayIndex);
				} else if (currentYear == END_YEAR && currentMonth == END_MONTH && END_DAY < 28) {
					changeDayWheelData(wv_day, 1, END_DAY, currentDayIndex);
				} else {
					changeDayWheelData(wv_day, 1, 28, currentDayIndex);
				}
			}
		}
	}

	/**
	 * change DayWheel Data
	 * 
	 * @Date 2015-11-12
	 * @param wv_day
	 * @param startDay
	 * @param endDay
	 * @param currentDay
	 */
	private void changeDayWheelData(WheelView wv_day, int startDay, int endDay, int currentDay) {
		wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay, "%02d", "日"));
		if (currentDay > endDay - startDay) {
			wv_day.setCurrentItem(0);
		}
	}

	/**
	 * change MonthWheel Data
	 * 
	 * @Date 2015-11-12
	 * @param wv_month
	 * @param startMonth
	 * @param endMonth
	 * @param currentMonth
	 */
	private void changeMonthWheelData(WheelView wv_month, int startMonth, int endMonth, int currentMonth) {
		wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth, "%02d", "月"));
		if (currentMonth > endMonth - startMonth) {
			wv_month.setCurrentItem(0);
		}
	}

	// private class PickerImageTask extends ImageLoadTask {
	//
	// private static final long serialVersionUID = 1L;
	// private Context mContext;
	// private String mImgUrl;
	//
	// public PickerImageTask(Context context, String imgUrl) {
	// // TODO Auto-generated constructor stub
	// super(imgUrl);
	// this.mContext = context;
	// this.mImgUrl = imgUrl;
	// }
	//
	// @Override
	// protected Bitmap doInBackground() {
	// // TODO Auto-generated method stub
	//
	// return myoriginalImage;
	// }
	//
	// @Override
	// protected BytesArrayFactory$BytesArray transBitmapToBytesArray(
	// Bitmap originalImage) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// }

	public String getUpDown() {
		return upDown;
	}

	public int getPickerYear() {
		return pickerYear;
	}

	public void getTime() {
		try {
			String s = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate);
			if (s != null) {
				String st[] = s.split(" ");
				String nyr = st[0];
				String sf = st[1];
				String ymr[] = nyr.split("-");
				String hm[] = sf.split(":");
				listInt.add(Integer.parseInt(ymr[0]));
				listInt.add(Integer.parseInt(ymr[1]));
				listInt.add(Integer.parseInt(ymr[2]));
				listInt.add(Integer.parseInt(hm[0]));
				listInt.add(Integer.parseInt(hm[1]));
			}
			Log.i("listInt", listInt.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();

		}
	}

	public void getHM() {
		try {
			String hm = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate1);
			if (showType == 5) {
				hm = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate5);
			}
			if (hm != null) {
				String hm1[] = hm.split(":");
				listIntHM.add(Integer.parseInt(hm1[0]));
				listIntHM.add(Integer.parseInt(hm1[1]));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void getYMD() {
		try {
			String hm = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate1);
			if (hm != null) {
				String hm1[] = hm.split(":");
				listIntHM.add(Integer.parseInt(hm1[0]));
				listIntHM.add(Integer.parseInt(hm1[1]));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void getYMD2() {
		try {
			String hm = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate3);
			if (hm != null) {
				String hm1[] = hm.split("-");
				listIntYMD.add(Integer.parseInt(hm1[0]));
				listIntYMD.add(Integer.parseInt(hm1[1]));
				listIntYMD.add(Integer.parseInt(hm1[2]));
				Log.i("date11", listIntYMD.toString());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void getYM() {
		try {
			String hm = SharedPre1.getStrDat(PickerFragment.this.getActivity(), Contains.harvestDate4);
			if (hm != null) {
				String hm1[] = hm.split("-");
				listIntYM.add(Integer.parseInt(hm1[0]));
				listIntYM.add(Integer.parseInt(hm1[1]));
				Log.i("date11", listIntYM.toString());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
	public static int px2dip(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
