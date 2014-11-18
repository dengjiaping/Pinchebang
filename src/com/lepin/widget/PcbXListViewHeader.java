package com.lepin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lepin.activity.R;

/**
 * ListView头部布局
 *
 */
public class PcbXListViewHeader extends LinearLayout {
	private LinearLayout mContainer;// 头部布局
	private ImageView mArrowImageView;// 下拉或上弹图片
	private ProgressBar mProgressBar;// 进度条
	private TextView mHintTextView;// 下拉刷新
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;// 旋转动画（用于图片方向改变）
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	
	public final static int STATE_NORMAL = 0;// 初始状态（此状态会显示下拉刷新）
	public final static int STATE_READY = 1;// 松开可以刷新（此状态位刷新的准备状态）
	public final static int STATE_REFRESHING = 2;// 正在刷新（此状态会显示进度条）

	public PcbXListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public PcbXListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);// 下拉图片
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);// 下拉刷新
		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);// 进度条
		
		// 设置旋转动画（松开刷新数据）
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		// 设置旋转动画（下拉刷新数据）
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState){
			return;
		}
		
		if (state == STATE_REFRESHING) {// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		
		switch(state){
		case STATE_NORMAL:// 下拉刷新
			if (mState == STATE_READY) {
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(R.string.xlistview_header_hint_normal);// 下拉刷新
			break;
		case STATE_READY:// 松开可以刷新
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(R.string.xlistview_header_hint_ready);// 松开可以刷新
			}
			break;
		case STATE_REFRESHING:// 正在刷新
			mHintTextView.setText(R.string.xlistview_header_hint_loading);// 正在加载...
			break;
		}
		
		mState = state;
	}
	
	public void setVisiableHeight(int height) {
		if (height < 0){
			height = 0;
		}
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
