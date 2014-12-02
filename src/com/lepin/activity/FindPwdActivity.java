package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.CarSharingApplication;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

/**
 * 忘记密码，通过短信认证找回密码，在由用户自行重新输入6~16任意字符作为新密码
 * 
 * 
 */
@Contextview(R.layout.forget_pwd)
public class FindPwdActivity extends BaseActivity {
	// private final static String TAG="FindPwdActivity";
	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回按钮
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题
	@ViewInject(id = R.id.forget_pwd_et_mobile)
	private EditText etMobile;// 注册过的手机号码
	@ViewInject(id = R.id.forget_pwd_et_validate_code)
	private EditText etValidateCode;// 获取到的验证
	@ViewInject(id = R.id.forget_pwd_et_newpwd)
	private EditText etNewPwd;// 重置后的密码
	@ViewInject(id = R.id.forget_pwd_btn_get_code)
	private TextView btnGetCode;// 获取验证码按钮

	@ViewInject(id = R.id.forget_pwd_btn)
	private TextView btnForget;// 提交

	private String user_tel;// 用户输入的手机号码
	private TimeCount time;// 倒计时
	// private boolean isExist = false;// 是否存在
	private String NOREGIST = "true";
	private Util util = Util.getInstance();
	/**
	 * 按钮事件监听
	 */
	private View.OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View paramView) {
			if (btnBack == paramView) {// 返回
				finishMe();
			} else if (btnGetCode == paramView) {// 获取验证码
				isExistUser();
			} else if (btnForget == paramView) {// 提交
				if (validateForm()) {// 验证用户提交的信息
					findPassword();// 找回验证码
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		this.tvTitle.setText(getResources().getString(R.string.forget_pwd_title));// 初始化title
		this.btnBack.setOnClickListener(this.myListener);
		this.btnGetCode.setOnClickListener(this.myListener);
		this.btnForget.setOnClickListener(this.myListener);
		this.time = new TimeCount(60000, 1000);// 构造CountDownTimer对象，为获取验证码时间段
		this.etMobile.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				user_tel = etMobile.getText().toString();
				if (ValidateTool.validateMobileNum(user_tel)) {
					btnGetCode.setEnabled(true);
					btnGetCode.setTextColor(getResources().getColor(R.color.btn_blue_normal));
				} else {
					btnGetCode.setEnabled(false);
					btnGetCode.setTextColor(getResources().getColor(R.color.btn_grey_normal));
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

		});
	}

	/**
	 * 校验用户输入的信息合法性
	 * 
	 * @return
	 */
	private boolean validateForm() {
		boolean isOk = false;
		String mobile = this.etMobile.getText().toString();
		String code = this.etValidateCode.getText().toString();
		String pwd = this.etNewPwd.getText().toString();
		if (!util.isNullOrEmpty(mobile)) {
			if (ValidateTool.validateMobileNum(mobile)) {
				if (!util.isNullOrEmpty(code) && code.length() == 4) {
					if (!util.isNullOrEmpty(pwd) && (pwd.length() >= 6 && pwd.length() <= 16)) {
						isOk = true;
					} else {
						Util.showToast(this, getString(R.string.pwd_rule));
					}
				} else {
					Util.showToast(this, getString(R.string.register_validate_code));
				}
			} else {
				Util.showToast(this, getString(R.string.invalid_phone_num));
			}
		} else {
			Util.showToast(this, getString(R.string.input_regist_phone_num));
		}
		return isOk;
	}

	private void finishMe() {
		CarSharingApplication.Instance().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishMe();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 通过注册的手机号码获取验证码
	 */
	private void getValidateCode() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tel", user_tel));

		util.doPostRequest(FindPwdActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					setGetCodeBtn(false);
					Util.showToast(FindPwdActivity.this, jsonResult.getData().toString());
					time.start();
				} else {
					Util.showToast(FindPwdActivity.this, jsonResult.getErrorMsg().toString());
				}
			}
		}, params, Constant.URL_SENDTELCODE, getString(R.string.gets_the_verification_code), false);

		// util.doPostRequest(FindPwdActivity.this, new OnDataLoadingCallBack()
		// {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// if (!util.isNullOrEmpty(result)) {
		// JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
		// new TypeToken<JsonResult<String>>() {
		// });
		// if (jsonResult != null && jsonResult.isSuccess()) {
		// setGetCodeBtn(false);
		// Util.showToast(FindPwdActivity.this,
		// jsonResult.getData().toString());
		// time.start();
		// } else {
		// Util.showToast(FindPwdActivity.this,
		// jsonResult.getErrorMsg().toString());
		// }
		//
		// }
		// }
		// }, params, Constant.URL_SENDTELCODE,
		// getString(R.string.gets_the_verification_code));

	}

	/**
	 * 找回密码
	 */
	private void findPassword() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tel", user_tel));
		params.add(new BasicNameValuePair("code", etValidateCode.getText().toString()));
		params.add(new BasicNameValuePair("pwd", etNewPwd.getText().toString()));

		util.doPostRequest(FindPwdActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				JsonResult<String> findPwdResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (findPwdResult.isSuccess()) {
					Util.showToast(FindPwdActivity.this, findPwdResult.getData().toString());
					Util.getInstance().clearUser(FindPwdActivity.this);
					finishMe();// 关闭自身
				} else {
					Util.showToast(FindPwdActivity.this, findPwdResult.getErrorMsg().toString());
				}

			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				// TODO Auto-generated method stub
				super.onFail(errorType, errorMsg);
				Util.showToast(FindPwdActivity.this, errorMsg);
			}
		}, params, Constant.URL_FINDPWDBYTEL, getString(R.string.find_back_pwd), true);

		// util.doPostRequest(FindPwdActivity.this, new OnDataLoadingCallBack()
		// {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// if (!util.isNullOrEmpty(result)) {
		// JsonResult<String> findPwdResult = util.getObjFromJsonResult(result,
		// new TypeToken<JsonResult<String>>() {
		// });
		// if (findPwdResult.isSuccess()) {
		// Util.showToast(FindPwdActivity.this,
		// findPwdResult.getData().toString());
		// Util.getInstance().clearUser(FindPwdActivity.this);
		// finishMe();// 关闭自身
		// } else {
		// Util.showToast(FindPwdActivity.this,
		// findPwdResult.getErrorMsg().toString());
		// }
		//
		// }
		// }
		// }, params, Constant.URL_FINDPWDBYTEL,
		// getString(R.string.find_back_pwd));

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 计时
	 * 
	 * @author Administrator
	 * 
	 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕触发
			setGetCodeBtn(true);
			btnGetCode.setText(getString(R.string.register_get_validate_code));
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			setGetCodeBtn(false);
			btnGetCode.setTextColor(getResources().getColor(R.color.btn_blue_normal));
			btnGetCode.setText(String.valueOf((millisUntilFinished / 1000)));
		}
	}

	private void setGetCodeBtn(boolean isEnabled) {
		this.btnGetCode.setEnabled(isEnabled);// 初始化时获取验证码按钮无效
		if (isEnabled) {
			this.btnGetCode.setTextColor(getResources().getColor(R.color.btn_blue_normal));
		} else {
			this.btnGetCode.setTextColor(getResources().getColor(android.R.color.black));
		}
	}

	/**
	 * 检查号码是否注册过
	 */
	private void isExistUser() {
		if (util.isNetworkAvailable(this)) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("account", user_tel));

			util.doPostRequest(FindPwdActivity.this, new OnHttpRequestDataCallback() {

				public void onSuccess(String result) {
					JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
							new TypeToken<JsonResult<String>>() {
							});
					if (jsonResult != null && jsonResult.isSuccess()) {
						if (!jsonResult.getData().equals(NOREGIST)) {// 未注册
							getValidateCode();// 获取找回密码验证码
						} else {// 未注册
							Util.showToast(FindPwdActivity.this, getString(R.string.not_registered));
						}
					}
				}
			}, params, Constant.URL_CHECKUSERACCOUNT, getString(R.string.check_user_info), false);

			// util.doPostRequest(FindPwdActivity.this, new
			// OnDataLoadingCallBack() {
			//
			// @Override
			// public void onLoadingBack(String result) {
			// JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
			// new TypeToken<JsonResult<String>>() {
			// });
			// if (jsonResult != null && jsonResult.isSuccess()) {
			// if (!jsonResult.getData().equals(NOREGIST)) {// 未注册
			// getValidateCode();// 获取找回密码验证码
			// } else {// 未注册
			// Util.showToast(FindPwdActivity.this,
			// getString(R.string.not_registered));
			// }
			// }
			// }
			// }, params, Constant.URL_CHECKUSERACCOUNT,
			// getString(R.string.check_user_info));

		} else {
			util.showTip(FindPwdActivity.this);
		}
	}

}
