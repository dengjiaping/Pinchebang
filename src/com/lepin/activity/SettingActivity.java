package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.HttpRequestOnBackgrount;
import com.lepin.util.HttpUtil;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

@Contextview(R.layout.setting)
public class SettingActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;// 标题

	@ViewInject(id = R.id.text_version_code)
	private TextView tvVersionCode;// 版本

	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;

	@ViewInject(id = R.id.setting_check_update)
	private RelativeLayout checkUpdate;

	@ViewInject(id = R.id.setting_push)
	private ImageButton mPushSettingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		mTitle.setText(R.string.setting);
		btnBack.setOnClickListener(this);
		checkUpdate.setOnClickListener(this);
		tvVersionCode.setText(Constant.sLocalVersionName);
		boolean isPushEnable = false;
		if (Util.getInstance().isUserLoging(SettingActivity.this)) {
			Util.printLog("user:" + Util.getInstance().getUser(SettingActivity.this).toString());
			if (Util.getInstance().getUser(SettingActivity.this).getPushSwitch()
					.equals(User.PUSH_OPEN)) {
				isPushEnable = true;
			}
		}

		Util.printLog("推送是否可用:" + isPushEnable);
		if (isPushEnable) {
			mPushSettingButton.setBackgroundResource(R.drawable.pcb_sitting_on);
		} else {
			mPushSettingButton.setBackgroundResource(R.drawable.pcb_sitting_off);
		}
		mPushSettingButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.common_title_back:
			SettingActivity.this.finish();
			break;
		case R.id.setting_check_update:
			Util.getInstance().checkUpdate(SettingActivity.this, true);
			break;
		case R.id.setting_push:
			if (Util.getInstance().isUserLoging(SettingActivity.this)) {
				final boolean isPushOpen = Util.getInstance().getUser(SettingActivity.this)
						.getPushSwitch().equals(User.PUSH_OPEN) ? true : false;
				Util.printLog("推送是否可用:" + isPushOpen);
				if (isPushOpen) {// 关闭推送功能
					openPush(false);
				} else {// 开启推送
					openPush(true);
				}
			} else {
				Util.showToast(SettingActivity.this, getString(R.string.not_logged_in));
			}
			break;
		}
	}

	protected void openPush(final boolean isOpen) {
		List<NameValuePair> paramArrayList = new ArrayList<NameValuePair>();
		if (isOpen) {
			paramArrayList.add(new BasicNameValuePair("pushSwitch", User.PUSH_OPEN));
		} else {
			paramArrayList.add(new BasicNameValuePair("pushSwitch", User.PUSH_CLOSED));
		}
		HttpRequestOnBackgrount publishBackgrount = new HttpRequestOnBackgrount(
				HttpRequestOnBackgrount.POST, new OnHttpRequestDataCallback() {

					public void onSuccess(String result) {
						Util.printLog("修改推送:" + result);
						if (!TextUtils.isEmpty(result)) {
							TypeToken<JsonResult<String>> jToken = new TypeToken<JsonResult<String>>() {
							};
							JsonResult<String> jsonResult = Util.getInstance()
									.getObjFromJsonResult(result, jToken);
							if (jsonResult != null && jsonResult.isSuccess()) {
								User user = Util.getInstance().getUser(SettingActivity.this);
								if (isOpen) {// 开启
									user.setPushSwitch(User.PUSH_OPEN);
									mPushSettingButton
											.setBackgroundResource(R.drawable.pcb_sitting_on);
									Util.showToast(SettingActivity.this,
											getString(R.string.push_has_been_open));
								} else {
									user.setPushSwitch(User.PUSH_CLOSED);
									mPushSettingButton
											.setBackgroundResource(R.drawable.pcb_sitting_off);
									Util.showToast(SettingActivity.this,
											getString(R.string.push_has_been_closed));
								}
								Util.getInstance().setUser(SettingActivity.this, user);
							}
						} else {
							Util.showToast(SettingActivity.this, "推送修改失败");
						}

					}
				}, paramArrayList, this, false);
		publishBackgrount.execute(Constant.URL_UPDATE_USER_INFO);

	}

}
