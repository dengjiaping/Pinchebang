package com.lepin.widget;

import com.lepin.activity.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckedTextView;
import android.widget.PopupWindow;

public class PcbHomeBottomMenu extends PopupWindow {
	private View mTopMenuView;

	public PcbHomeBottomMenu(final Activity mContext, OnClickListener clickListener) {
		super(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTopMenuView = inflater.inflate(R.layout.home_bottom_publish_menu, null);
		CheckedTextView mLong = (CheckedTextView) mTopMenuView
				.findViewById(R.id.home_bottom_publish_menu_long);
		mLong.setOnClickListener(clickListener);
		CheckedTextView mWork = (CheckedTextView) mTopMenuView
				.findViewById(R.id.home_bottom_publish_menu_work);
		mWork.setOnClickListener(clickListener);
		int w = mContext.getWindowManager().getDefaultDisplay().getWidth();

		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mTopMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth((w / 3) * 2);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.my_bottom_popupwindow);
		// // 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(000000000);
		// // 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(true);
	}
}
