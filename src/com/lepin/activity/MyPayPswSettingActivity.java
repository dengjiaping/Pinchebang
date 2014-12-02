package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

@Contextview(R.layout.my_pay_psw_setting)
public class MyPayPswSettingActivity extends Activity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mlTitleBack;// 返回

	@ViewInject(id = R.id.common_title_title)
	private TextView mlTitleText;// 标题

	@ViewInject(id = R.id.entry_pay_pass_edit)
	private EditText mpayPswEditText;// 支付密码 1

	@ViewInject(id = R.id.sure_pay_pass_edit)
	private EditText mpayPswEditText2;// 支付密码 2

	@ViewInject(id = R.id.entry_verification_code_edit)
	private EditText mVerificationCodeEditText;// 输入验证码

	@ViewInject(id = R.id.send_verification_code)
	private TextView mSendVerificationCodeButton;// 发送验证码

	@ViewInject(id = R.id.submitPass)
	private Button mSureButton;// 确定修改按钮

	private Util util = Util.getInstance();
	private TimeCount time;// 倒计时
	private boolean isReturn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			isReturn = bundle.getBoolean("return");
		}
		mlTitleText.setText(getString(R.string.pay_psw_setting_title));
		setOnclick();
	}

	private void setOnclick() {
		// TODO Auto-generated method stub
		this.time = new TimeCount(60000, 1000);// 构造CountDownTimer对象，为获取验o证码时间段
		mSendVerificationCodeButton.setOnClickListener(this);
		mSureButton.setOnClickListener(this);
		mlTitleBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mlTitleBack) {
			MyPayPswSettingActivity.this.finish();
		} else if (v == mSendVerificationCodeButton) {
			httpRequest();
		} else if (v == mSureButton) {
			if (checkFirstValue()) {
				settingPayPsw();
			}
		}
	}

	private boolean checkFirstValue() {
		// 验证码
		String strCode = this.mVerificationCodeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(strCode) || strCode.length() != 4) {
			Util.showToast(MyPayPswSettingActivity.this, getString(R.string.register_validate_code));
			return false;
		}

		String payPswCode1 = this.mpayPswEditText.getText().toString().trim();
		String payPswCode2 = this.mpayPswEditText2.getText().toString().trim();
		// 密码规则
		if (!payPswCode1.matches("^[0-9a-zA-Z]{6,18}$")) {
			Util.showToast(MyPayPswSettingActivity.this, getString(R.string.pay_pwd_error));
			return false;
		}

		if (!payPswCode1.equals(payPswCode2)) {
			Util.showToast(MyPayPswSettingActivity.this, getString(R.string.pay_pwd_error_1));
			return false;
		}
		return true;
	}

	/**
	 * 获得验证码
	 */
	private void httpRequest() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		util.doPostRequest(MyPayPswSettingActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					time.start();
					Util.showToast(MyPayPswSettingActivity.this, "获取验证码成功");
				} else {
					Util.showToast(MyPayPswSettingActivity.this, "获取验证码失败");
				}
			}
		}, params, Constant.URL_SENDTELCODE, getString(R.string.gets_the_verification_code), false);
	}

	/**
	 * 设置支付密码
	 */
	private void settingPayPsw() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("payPwd", mpayPswEditText.getText().toString()));
		params.add(new BasicNameValuePair("code", mVerificationCodeEditText.getText().toString()));
		util.doPostRequest(MyPayPswSettingActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Util.showToast(MyPayPswSettingActivity.this, "设置支付密码成功");
					if (isReturn) {
						setResult(RESULT_OK);
					}
					User user = util.getLoginUser(MyPayPswSettingActivity.this);
					user.setPayPwdSet(true);
					util.updateUser(MyPayPswSettingActivity.this, user);
					finish();
				} else {
					Util.showToast(MyPayPswSettingActivity.this, result);
				}
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				// TODO Auto-generated method stub
				Util.showToast(MyPayPswSettingActivity.this, errorMsg);
			}
		}, params, Constant.URL_SETTINGPAYPSW, getString(R.string.gets_the_verification_code), true);
	}

	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕触发
			setGetCodeBtn(true);
			mSendVerificationCodeButton.setText(getString(R.string.register_get_validate_code));
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			setGetCodeBtn(false);
			mSendVerificationCodeButton.setTextColor(getResources().getColor(
					R.color.btn_blue_normal));
			mSendVerificationCodeButton.setText(String.valueOf((millisUntilFinished / 1000)));
		}
	}

	private void setGetCodeBtn(boolean isEnabled) {
		this.mSendVerificationCodeButton.setEnabled(isEnabled);// 初始化时获取验证码按钮无效
		if (isEnabled) {
			this.mSendVerificationCodeButton.setTextColor(getResources().getColor(
					R.color.btn_blue_normal));
		} else {
			this.mSendVerificationCodeButton.setTextColor(getResources().getColor(
					android.R.color.black));
		}
	}

}
