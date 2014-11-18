package com.lepin.widget;

import java.util.Calendar;
import java.util.LinkedHashMap;

import com.lepin.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PcbCalender extends LinearLayout {

	private Calendar mCalendar;
	private MonthDisplayHelper monthDisplayHelper;
	private Context mContext;
	private String[] week = new String[] { "SU", "MO", "TU", "WE", "TH", "FR", "SA" };
	private LinkedHashMap<String, Boolean> linkedHashMap = new LinkedHashMap<String, Boolean>();

	public PcbCalender(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public PcbCalender(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PcbCalender(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);
		init(context);
		addCellView();
	}

	private void addCellView() {

		addTopView();
		addWeek();
		addDays();

	}

	@SuppressWarnings("deprecation")
	private void addDays() {
		for (int i = 0; i <= 5; i++) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams itemParams = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			itemParams.weight = 1;
			int line[] = monthDisplayHelper.getDigitsForRow(i);
			for (int j = 0; j < line.length; j++) {
				TextView cellTextView = new TextView(mContext);

				if (monthDisplayHelper.isWithinCurrentMonth(i, j)) {

					if (mCalendar.get(Calendar.MONTH) == monthDisplayHelper.getMonth()
							&& line[j] == mCalendar.get(Calendar.DAY_OF_MONTH)) {// 判断是否时今天
						cellTextView.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.blue_circle));
						cellTextView.setTextColor(getResources().getColor(android.R.color.white));
						setTextColor(line[j], j, cellTextView);
						setParams(layout, itemParams, line, j, cellTextView);
						continue;
					}

					if (linkedHashMap.size() > 0) {
						setTextColor(line[j], j, cellTextView);
					}
				}
				setParams(layout, itemParams, line, j, cellTextView);
			}

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			addView(layout, params);
		}
	}

	private void setTextColor(int day, int j, TextView cellTextView) {
		int month = monthDisplayHelper.getMonth() + 1;
		String date = String.valueOf(monthDisplayHelper.getYear())
				+ (month > 9 ? month : "0" + month) + (day > 9 ? day : "0" + day);
		if (linkedHashMap.get(date) != null && linkedHashMap.get(date)) {
			// cellTextView.setBackgroundDrawable(getResources().getDrawable(
			// R.drawable.red_circle));
			cellTextView.setTextColor(getResources().getColor(R.color.red));
		}
	}

	private void setParams(LinearLayout layout, LayoutParams itemParams, int[] line, int j,
			TextView cellTextView) {
		cellTextView.setTextSize(13f);
		cellTextView.setGravity(Gravity.CENTER);
		cellTextView.setText(String.valueOf(line[j]));
		layout.addView(cellTextView, itemParams);
	}

	private void addWeek() {
		LinearLayout weekLayout = new LinearLayout(mContext);
		LayoutParams weekItemParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		weekItemParams.weight = 1;
		for (int i = 0; i < week.length; i++) {
			TextView weekTextView = new TextView(mContext);
			weekTextView.setTextSize(15f);
			weekTextView.setTextColor(getResources().getColor(R.color.blue_title));
			weekTextView.setGravity(Gravity.CENTER);
			weekTextView.setText(String.valueOf(week[i]));
			weekLayout.addView(weekTextView, weekItemParams);
		}

		this.addView(weekLayout, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	private void addTopView() {
		RelativeLayout top = new RelativeLayout(mContext);

		ImageView leftImageView = new ImageView(mContext);
		leftImageView.setId(0);
		leftImageView.setImageResource(R.drawable.pcb_order_arrow_left);
		RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		leftLayoutParams.leftMargin = 15;
		leftImageView.setOnClickListener(onClickListener);
		top.addView(leftImageView, leftLayoutParams);

		TextView middleTextView = new TextView(mContext);
		middleTextView.setText(monthDisplayHelper.getYear() + "-"
				+ (monthDisplayHelper.getMonth() + 1));
		middleTextView.setTextColor(getResources().getColor(R.color.blue_title));
		RelativeLayout.LayoutParams middleLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		middleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		top.addView(middleTextView, middleLayoutParams);

		ImageView rightImageView = new ImageView(mContext);
		rightImageView.setId(1);
		rightImageView.setImageResource(R.drawable.pcb_order_arrow_right);
		RelativeLayout.LayoutParams rightLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rightLayoutParams.rightMargin = 15;
		rightImageView.setOnClickListener(onClickListener);
		top.addView(rightImageView, rightLayoutParams);

		addView(top, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	private void init(Context context) {
		this.setBackgroundColor(getResources().getColor(android.R.color.white));
		mCalendar = Calendar.getInstance();
		monthDisplayHelper = new MonthDisplayHelper(mCalendar.get(Calendar.YEAR),
				mCalendar.get(Calendar.MONTH));

	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == 0) {
				if (monthDisplayHelper.getMonth() == mCalendar.get(Calendar.MONTH))
					monthDisplayHelper.previousMonth();
			} else if (v.getId() == 1) {
				if (monthDisplayHelper.getMonth() < mCalendar.get(Calendar.MONTH))
					monthDisplayHelper.nextMonth();
			}
			refresh();
		}
	};

	public void setCalendarDate(int[] date) {
		if (linkedHashMap.size() > 0) linkedHashMap.clear();
		if(date == null || date.length == 0) return;
		for (int d : date) {
			linkedHashMap.put(String.valueOf(d), true);
		}
		refresh();
	}

	protected void refresh() {
		this.removeAllViews();
		addCellView();
	}
}
