package com.lepin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lepin.activity.R;

/**
 * ListView页脚布局
 *
 */
public class PcbListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;// 初始状态
	public final static int STATE_READY = 1;// 上拉加载更多（此时松开会加载更多）
	public final static int STATE_LOADING = 2;// 加载更多（加载中）

	private Context mContext;
	private View mContentView;// 查看更多布局
	private View mProgressBar;// 进度条
	private TextView mHintView;// 查看更多
	
	public PcbListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	
	public PcbListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {// 松开加载更多
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.xlistview_footer_hint_ready);
		} else if (state == STATE_LOADING) {// 更多数据加载中
			mProgressBar.setVisibility(View.VISIBLE);
		} else {// 查看更多
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.xlistview_footer_hint_normal);
		}
	}
	
	public void setBottomMargin(int height) {
		if (height < 0){
			return;
		}
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	
	/**
	 * 初始状态（查看更多）
	 */
//	public void normal() {
//		mHintView.setVisibility(View.VISIBLE);
//		mProgressBar.setVisibility(View.GONE);// 进度条隐藏
//	}
	
	
	/**
	 * 加载更多数据加载中
	 */
//	public void loading() {
//		mHintView.setVisibility(View.GONE);// 查看更多隐藏
//		mProgressBar.setVisibility(View.VISIBLE);
//	}
	
	/**
	 * 隐藏页脚，当没有更多数据供加载时
	 */
	public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
		mContentView.setVisibility(View.GONE);
	}
	
	/**
	 * 显示页脚
	 */
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
		mContentView.setVisibility(View.VISIBLE);
	}
	
	private void initView(Context context) {
		mContext = context;
		// 加载页脚布局
		LinearLayout moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		
		mContentView = moreView.findViewById(R.id.xlistview_footer_content);// 查看更多布局
		mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);// 进度条
		mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);// 查看更多
	}
}
