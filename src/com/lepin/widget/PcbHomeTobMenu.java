package com.lepin.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.entity.User;
import com.lepin.util.Util;

public class PcbHomeTobMenu extends PopupWindow {
	private View mTopMenuView;
	private LinearLayout mPersonalLayout;

	private TextView mMenuName;
	private TextView mMenuPhoneNum;

	private TextView mNotLoginTitle;

	private ImageView mMsgImageView;
	private TextView mMsgNum;// 消息个数
	private TextView mMsgText;// 消息

	private ImageView mPulishImageView;
	private TextView mPublishNum;// 发布消息个数
	private TextView mPublishText;// 我的发布

	public PcbHomeTobMenu(final Activity mContext, OnClickListener onItemClickListener) {
		super(mContext);
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTopMenuView = inflater.inflate(R.layout.home_top_menu, null);

		int w = mContext.getWindowManager().getDefaultDisplay().getWidth();
		int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
		w = w / 5 * 3;
		h = h / 5 * 3;
		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mTopMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(h);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.my_popupwindow);
		// // 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(000000000);
		// // 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);

		mPersonalLayout = (LinearLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_personal_layout);

		mNotLoginTitle = (TextView) mTopMenuView.findViewById(R.id.home_top_menu_no_login_title);// 未登录时:请登录
		mNotLoginTitle.setOnClickListener(onItemClickListener);
		mPersonalLayout.setOnClickListener(onItemClickListener);
		// 用户名
		mMenuName = (TextView) mTopMenuView.findViewById(R.id.home_top_menu_name);
		// 电话号
		mMenuPhoneNum = (TextView) mTopMenuView.findViewById(R.id.home_top_menu_phone);
		// -----------------------------------消息中心-----------------------------
		RelativeLayout mMsgLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_msg_layout);

		mMsgLayout.setOnClickListener(onItemClickListener);
		mMsgImageView = (ImageView) mMsgLayout.findViewById(R.id.item_home_menu_img);
		mMsgImageView.setBackgroundResource(R.drawable.pcb_home_menu_mail);
		// 设置“消息中心”
		mMsgText = (TextView) mMsgLayout.findViewById(R.id.item_home_menu_title);
		mMsgText.setText(mContext.getString(R.string.message_center_title));
		// 消息个数
		mMsgNum = (TextView) mMsgLayout.findViewById(R.id.item_home_menu_num);

		// ---------------------------我的发布----------------------------－－－－－－－－－－－－－－－－－－
		RelativeLayout mPublishLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_publish_layout);
		mPublishLayout.setOnClickListener(onItemClickListener);
		// 设置我的发布图标
		mPulishImageView = (ImageView) mPublishLayout.findViewById(R.id.item_home_menu_img);
		mPulishImageView.setBackgroundResource(R.drawable.pcb_home_menu_publish);
		// 设置“我的发布”
		mPublishText = (TextView) mPublishLayout.findViewById(R.id.item_home_menu_title);
		mPublishText.setText(mContext.getString(R.string.my_publish));
		// 消息个数
		mPublishNum = (TextView) mPublishLayout.findViewById(R.id.item_home_menu_num);

		// -------------------------汽车服务-------------------------------－－－－－－－－－－－－－－－－－
		RelativeLayout mCarServiceLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_carservice_layout);
		mCarServiceLayout.setOnClickListener(onItemClickListener);
		// 设置汽车服务图标
		((ImageView) mCarServiceLayout.findViewById(R.id.item_home_menu_img))
				.setBackgroundResource(R.drawable.pcb_home_menu_car_service);
		// 设置“汽车服务”
		((TextView) mCarServiceLayout.findViewById(R.id.item_home_menu_title)).setText(mContext
				.getString(R.string.car_service));
		// ------------------------精彩活动----------------------------------
		RelativeLayout mActivitiesLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_activities_layout);
		mActivitiesLayout.setOnClickListener(onItemClickListener);
		// 设置精彩活动图标
		((ImageView) mActivitiesLayout.findViewById(R.id.item_home_menu_img))
				.setBackgroundResource(R.drawable.pcb_home_menu_activities);
		// 设置“ 精彩活动”
		((TextView) mActivitiesLayout.findViewById(R.id.item_home_menu_title)).setText(mContext
				.getString(R.string.activites));
		// ---------------------更多------------------------------------
		RelativeLayout mMoreLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_more_layout);
		mMoreLayout.setOnClickListener(onItemClickListener);
		// 设置更多图标
		((ImageView) mMoreLayout.findViewById(R.id.item_home_menu_img))
				.setBackgroundResource(R.drawable.pcb_home_menu_more);
		// 设置“更多”
		((TextView) mMoreLayout.findViewById(R.id.item_home_menu_title)).setText(mContext
				.getString(R.string.moreTitle));
		// ---------------------设置------------------------------------
		RelativeLayout mSettingLayout = (RelativeLayout) mTopMenuView
				.findViewById(R.id.home_top_menu_setting_layout);
		mSettingLayout.setOnClickListener(onItemClickListener);
		// 设置设置图标
		((ImageView) mSettingLayout.findViewById(R.id.item_home_menu_img))
				.setBackgroundResource(R.drawable.pcb_home_menu_setting);
		// 设置“设置”
		((TextView) mSettingLayout.findViewById(R.id.item_home_menu_title)).setText(mContext
				.getString(R.string.setting));
	}

	// 设置消息个数
	public void setmMsgNum(int num) {
		if (mMsgNum.getVisibility() != View.VISIBLE) mMsgNum.setVisibility(View.VISIBLE);
		mMsgNum.setText(String.valueOf(num));
	}

	public void hiddenMsg() {
		if (mMsgNum.getVisibility() == View.VISIBLE) mMsgNum.setVisibility(View.INVISIBLE);
	}

	// 设置发布消息个数
	public void setmPublishMsgNum(int num) {
		if (mPublishNum.getVisibility() != View.VISIBLE) mPublishNum.setVisibility(View.VISIBLE);
		mPublishNum.setText(String.valueOf(num));
	}

	public void isUserLogin(Context mContext) {
		if (Util.getInstance().isUserLoging(mContext)) {// 登录
			User user = Util.getInstance().getUser(mContext);
			if (mMenuName != null) mMenuName.setText(user.getUsername());
			if (mMenuPhoneNum != null) mMenuPhoneNum.setText(user.getTel());
			mNotLoginTitle.setVisibility(View.GONE);
			mPersonalLayout.setVisibility(View.VISIBLE);
			mMsgText.setTextColor(mContext.getResources().getColor(android.R.color.white));

			mMsgImageView.setBackgroundResource(R.drawable.pcb_home_menu_mail);
			mPublishText.setTextColor(mContext.getResources().getColor(android.R.color.white));
			mPulishImageView.setBackgroundResource(R.drawable.pcb_home_menu_publish);
		} else {// 未登录
			mNotLoginTitle.setVisibility(View.VISIBLE);
			mPersonalLayout.setVisibility(View.GONE);
			mMsgText.setTextColor(mContext.getResources().getColor(R.color.dialog_btn_tx));
			mMsgImageView.setBackgroundResource(R.drawable.pcb_home_menu_mail_pressed);
			mMsgNum.setText("");
			mPublishText.setTextColor(mContext.getResources().getColor(R.color.dialog_btn_tx));

			mPulishImageView.setBackgroundResource(R.drawable.pcb_home_menu_publish_pressed);
			mPublishNum.setVisibility(View.GONE);
		}
	}
}
