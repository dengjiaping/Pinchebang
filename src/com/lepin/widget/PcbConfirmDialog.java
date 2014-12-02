package com.lepin.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.lepin.activity.R;
import com.lepin.util.Constant;

public class PcbConfirmDialog extends Dialog implements DialogInterface,
		android.view.View.OnClickListener {
	public final static int OK = 1;// 确定
	public final static int CANCEL = 0;// 取消
	private TextView tvContext;
	private TextView btnClose;
	private TextView btnOk;
	private OnOkOrCancelClickListener mOkClickListener;
	private int duration = 500;
	private boolean isCanCancelByBack = true;
	private View mDialogView;

	public interface OnOkOrCancelClickListener {
		void onOkClick(int type);
	}

	public PcbConfirmDialog(Context context) {
		super(context);
		init(context);
	}

	public PcbConfirmDialog(Context context, int theme, boolean isCanCancelByBack) {
		super(context, theme);
		this.isCanCancelByBack = isCanCancelByBack;
		init(context);
	}

	public static PcbConfirmDialog getInstance(Context context, int theme) {
		return new PcbConfirmDialog(context, theme, true);
	}

	public static PcbConfirmDialog getInstance(Context context, int theme, boolean isCanCancelByBack) {
		return new PcbConfirmDialog(context, theme, isCanCancelByBack);
	}

	protected PcbConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = ViewGroup.LayoutParams.MATCH_PARENT;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	}

	public void setCustomerListener(OnOkOrCancelClickListener clickListener) {
		this.mOkClickListener = clickListener;
	}

	public void setText(String title, String ok, String cancel) {
		tvContext.setText(title);
		btnClose.setText(cancel);
		btnOk.setText(ok);
	}

	public void setListener() {
		btnClose.setOnClickListener(this);
		btnOk.setOnClickListener(this);
	}

	protected void init(Context context) {
		mDialogView = View.inflate(context, R.layout.dialog, null);
		tvContext = (TextView) mDialogView.findViewById(R.id.dialog_context);
		btnClose = (TextView) mDialogView.findViewById(R.id.dialog_close);
		btnOk = (TextView) mDialogView.findViewById(R.id.dialog_ok);
		setContentView(mDialogView);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			this.setOnShowListener(new OnShowListener() {

				@SuppressLint("NewApi")
				@Override
				public void onShow(DialogInterface dialog) {
					AnimatorSet mShowAnimationSet = new AnimatorSet();
					mShowAnimationSet.playTogether(
							ObjectAnimator.ofFloat(mDialogView, "translationY", 300f, 0)
									.setDuration(duration),
							ObjectAnimator.ofFloat(mDialogView, "alpha", 0, 1).setDuration(
									duration * 3 / 2));
					mShowAnimationSet.start();

				}
			});
		}
		setCancelable(isCanCancelByBack);
		setListener();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		Constant.is_comfirm_dialog_show = true;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
		Constant.is_comfirm_dialog_show = false;
	}

	public void setContext(String text) {
		tvContext.setText(text.toString());
	}

	public void setBtnClose(String text) {
		btnClose.setText(text.toString());
	}

	public void setBtnOk(String text) {
		btnOk.setText(text.toString());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Constant.is_comfirm_dialog_show = false;
		if (v == btnOk) {
			mOkClickListener.onOkClick(OK);
		} else {
			mOkClickListener.onOkClick(CANCEL);
		}
		PcbConfirmDialog.this.dismiss();
	}

}
