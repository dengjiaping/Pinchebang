package com.lepin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lepin.activity.R;

public class TopTabIndicator extends RelativeLayout {
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	private int mCurrent = LEFT;
	private TabClick mTabClick;
	TextView mLeftButton = null;
	TextView mRightButton = null;

	public void setmTabClick(TabClick mTabClick) {
		this.mTabClick = mTabClick;
	}

	public TopTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public TopTabIndicator(Context context) {
		super(context);
	}

	private void init(Context mContext, AttributeSet attrs) {

		TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.top_tab);
		String leftString = "";
		String rightString = "";
		try {
			leftString = array.getString(R.styleable.top_tab_top_left_text);
			rightString = array.getString(R.styleable.top_tab_top_right_text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			array.recycle();
		}
		LayoutInflater inflater = LayoutInflater.from(mContext);
		setBackgroundColor(getResources().getColor(android.R.color.white));
		// 左边
        View leftView = inflater.inflate(R.layout.top_left, null);
		mLeftButton = (TextView)leftView.findViewById(R.id.top_left_btn);
		mLeftButton.setText(leftString);
		mLeftButton.setTextColor(getResources().getColor(android.R.color.white));
		// 右边
		View rightView = inflater.inflate(R.layout.top_right,null);
		mRightButton = (TextView)rightView.findViewById(R.id.top_right_btn);
		mRightButton.setText(rightString);
		mRightButton.setTextColor(getResources().getColor(R.color.btn_blue_normal));

		LinearLayout.LayoutParams mleftParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		LinearLayout layout = new LinearLayout(mContext);

		mLeftButton.setOnClickListener(onClickListener);
		mRightButton.setOnClickListener(onClickListener);
		layout.addView(mLeftButton, mleftParams);
		layout.addView(mRightButton, mleftParams);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(layout, layoutParams);

		 ImageView view = new ImageView(mContext);
		 view.setBackgroundResource(R.color.item_devider);
		 RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		 LayoutParams.MATCH_PARENT, 1);
		 params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 this.addView(view, params);
	}

	public void setTabClick(TabClick mTabClick) {
		this.mTabClick = mTabClick;
	}

	private void setRightTab() {
		if (mCurrent != RIGHT) {
			mCurrent = RIGHT;
			mRightButton.setBackgroundResource(R.drawable.pcb_toptab_right_pressed);
			mRightButton.setTextColor(getResources().getColor(android.R.color.white));
			mLeftButton.setTextColor(getResources().getColor(R.color.btn_blue_normal));
			mLeftButton.setBackgroundResource(R.drawable.pcb_toptab_left_normal);
			if (mTabClick != null) mTabClick.onTabClick(RIGHT);
		}
	}

	private void setLeftTab() {
		if (mCurrent != LEFT) {
			mCurrent = LEFT;
			mLeftButton.setBackgroundResource(R.drawable.pcb_toptab_left_pressed);
			mLeftButton.setTextColor(getResources().getColor(android.R.color.white));
			mRightButton.setBackgroundResource(R.drawable.pcb_toptab_right_normal);
			mRightButton.setTextColor(getResources().getColor(R.color.btn_blue_normal));
			if (mTabClick != null) mTabClick.onTabClick(LEFT);
		}
	}

	public void setTab2Click(int index) {
		if (index != mCurrent) {
			if (index == LEFT) {
				setLeftTab();
			} else {
				setRightTab();
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mLeftButton) {
				setLeftTab();
			} else {
				setRightTab();
			}
		}
	};

	public interface TabClick {
		void onTabClick(int index);
	}
}