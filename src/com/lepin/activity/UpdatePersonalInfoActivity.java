package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

@Contextview(R.layout.update_personal_info)
public class UpdatePersonalInfoActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView upBack;// 返回
	@ViewInject(id = R.id.common_title_title)
	private TextView upTitle;// 标题
	@ViewInject(id = R.id.update_name_layout)
	private LinearLayout upnameLayout;// 更新昵称
	@ViewInject(id = R.id.update_pass_layout)
	private LinearLayout uppassLayout;// 更新昵称
	@ViewInject(id = R.id.uedate_name)
	private EditText upName;// 更新昵称
	@ViewInject(id = R.id.submit_update_name)
	private TextView upSubmitName;// 提交昵称

	@ViewInject(id = R.id.update_old_pass)
	private EditText upOldPass;// 旧密码
	@ViewInject(id = R.id.update_new_pass)
	private EditText upNewPass;// 新密码
	@ViewInject(id = R.id.update_repeat_pass)
	private EditText upReNewPass;// 重复新密码
	@ViewInject(id = R.id.submitPass)
	private TextView upSubmitPass;// 提交密码

	private String updateType;// name:更新姓名 pass：更新密码
	private Util util = Util.getInstance();

	private String oldPass;
	private String newPass;
	private String reNewPass;
	private User user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		user = util.getLoginUser(UpdatePersonalInfoActivity.this);
		Intent intent = getIntent();
		updateType = intent.getStringExtra("updateType");
		initView(updateType);
	}

	private void initView(String type) {
		if (type.equals("name")) {
			upnameLayout.setVisibility(View.VISIBLE);
			upTitle.setText(getString(R.string.update_name));
			upName.setText(user.getUsername());
			upName.setSelection(upName.getText().length());
			upSubmitName.setOnClickListener(this);
		} else if (type.equals("pass")) {
			uppassLayout.setVisibility(View.VISIBLE);
			upTitle.setText(getString(R.string.update_pass));
			upSubmitPass.setOnClickListener(this);
		}
		upBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == upSubmitName) {
			String userName = upName.getText().toString();
			if (TextUtils.isEmpty(userName)) {
				Util.showToast(UpdatePersonalInfoActivity.this,
						getString(R.string.update_input_name));
				return;
			}
			if (!ValidateTool.isUserNameRight(userName)) {
				Util.showToast(this, getString(R.string.my_data_nickname_hint) + "只能是数字，字母，中文");
			} else {
				updateUserName();
			}
		} else if (v == upSubmitPass) {
			if (checkData()) {
				updatePwd();
			}
		} else if (v == upBack) {
			this.finish();
		}

	}

	/**
	 * 更新用户名
	 */
	public void updateUserName() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", upName.getText().toString().trim()));

		util.doPostRequest(UpdatePersonalInfoActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					user.setUsername(upName.getText().toString().trim());
					Util.showToast(UpdatePersonalInfoActivity.this,
							getString(R.string.update_name_success));
					UpdatePersonalInfoActivity.this.finish();
				} else {
					Util.showToast(UpdatePersonalInfoActivity.this,
							getString(R.string.request_error));
				}
			}
		}, params, Constant.URL_UPDATE_USER_INFO, getString(R.string.update_name_ing), false);
	}

	/**
	 * 更新密码
	 */
	public void updatePwd() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("oldPwd", oldPass));
		params.add(new BasicNameValuePair("pwd", newPass));

		util.doPostRequest(UpdatePersonalInfoActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResultpwd = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResultpwd != null && jsonResultpwd.isSuccess()) {
					Constant.reload_plan = true;
					Util.showToast(UpdatePersonalInfoActivity.this,
							getString(R.string.update_pass_success));
					logout();

					Util.getInstance().go2Activity(UpdatePersonalInfoActivity.this,
							LoginActivity.class);
				} else {
					Util.showToast(UpdatePersonalInfoActivity.this, jsonResultpwd.getErrorMsg()
							.toString());
				}
			}
		}, params, Constant.URL_MODIFYPWD, getString(R.string.update_pass_ing), false);

	}

	/**
	 * 注销登录
	 */

	private void logout() {
		if (Util.getInstance().isNetworkAvailable(this)) {

			Util.getInstance().doGetRequest(UpdatePersonalInfoActivity.this,
					new OnHttpRequestDataCallback() {

						@Override
						public void onSuccess(String result) {
							Util.getInstance().logout(UpdatePersonalInfoActivity.this);
							UpdatePersonalInfoActivity.this.finish();
						}
					}, Constant.URL_LOGOUT, getString(R.string.logout_int), false);
		} else {
			Util.showToast(UpdatePersonalInfoActivity.this, getString(R.string.network_unavaiable));
		}
	}

	public boolean checkData() {
		oldPass = upOldPass.getText().toString().trim();
		newPass = upNewPass.getText().toString().trim();
		reNewPass = upReNewPass.getText().toString().trim();

		if (oldPass.length() < 6) {
			Util.showToast(UpdatePersonalInfoActivity.this,
					getString(R.string.update_oldpass_short));
			upOldPass.requestFocus();
			return false;
		} else if (newPass.length() < 6) {
			Util.showToast(UpdatePersonalInfoActivity.this,
					getString(R.string.update_newpass_short));
			upNewPass.requestFocus();
			return false;
		}
		if (!newPass.equals(reNewPass)) {
			Util.showToast(UpdatePersonalInfoActivity.this,
					getString(R.string.update_pass_not_equal));
			upReNewPass.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UpdatePersonalInfoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
