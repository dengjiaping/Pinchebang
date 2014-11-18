package com.lepin.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import com.lepin.util.TimeUtils;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

/**
 * 用户通过输入有效的手机号码获取服务器推送的验证进行注册，注册时需要输入两次密码，密码为6到16位任意字符。
 * 注册成功跳转到拼车广场并且保存用户注册信息，否则不跳转。在此界面用户还可选择已有账号去直接登录。
 * 
 */
@Contextview(R.layout.register)
public class RegisterActivity extends BaseActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题
	// -------------------------------第一步界面--------------------------------------
	@ViewInject(id = R.id.register_first_view)
	private LinearLayout mFirstLayout;// 第一步布局

	@ViewInject(id = R.id.register_et_mobile)
	private EditText mMobileNum;// 用户电话

	@ViewInject(id = R.id.register_btn_get_code)
	private TextView btnGetCode;// 获取验证码

	@ViewInject(id = R.id.register_et_validate_code)
	private EditText etValidateCode;// 获取到的手机验证码

	@ViewInject(id = R.id.register_et_pwd)
	private EditText etPassword;// 密码

	@ViewInject(id = R.id.register_et_rp_pwd)
	private EditText etRpPaw;// 确认密码

	@ViewInject(id = R.id.register_cb_ok)
	private CheckBox cbOk;// 注册协议

	@ViewInject(id = R.id.register_next)
	private TextView mNexText;// 注册

	@ViewInject(id = R.id.register_go_login)
	private TextView btnGoLogin;// 已有账号去登陆

	// -------------------------------第一步界面--------------------------------------
	@ViewInject(id = R.id.register_second_view)
	private LinearLayout mSecondLayout;// 第二步布局

	/** 个人信息 */
	@ViewInject(id = R.id.register_nickname)
	private EditText mNickname;// 昵称
	/*
	 * @ViewInject(id = R.id.my_data_mobile) private EditText mPhone;// 电话
	 */
	@ViewInject(id = R.id.register_birthday)
	private TextView mBirthday;// 出生日期

	@ViewInject(id = R.id.register_gender)
	private TextView mGender;// 性别

	@ViewInject(id = R.id.register_btn)
	private Button mRegister;// 注册

	private String user_tel;// 入力的电话
	private String user_pwd;// 入力的密码

	private TimeCount time;// 倒计时
	private boolean isCheck = true;// 使用条款
	private Util util = Util.getInstance();
	private String NOREGIST = "true";
	private static final int GET_VALIDATECODE = 1;// 获取验证码
	private static final int CHECK_USER = 2;// 检查用户是否存在

	private String gender = User.GENDER_MALE;// 性别 （ 0表示男 1表示女）
	private String[] genderStrings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		setViewsListener();// 设置文本框监听
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		try {
			mMobileNum.setText(getIntent().getExtras().getString(LoginActivity.PHONE));
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.cbOk.setChecked(true);// 初始化选择协议

		this.cbOk.setOnClickListener(this);
		this.mNexText.setOnClickListener(this);
		this.btnGoLogin.setOnClickListener(this);
		this.btnGetCode.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mGender.setOnClickListener(this);
		mBirthday.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		this.time = new TimeCount(60000, 1000);// 构造CountDownTimer对象，为获取验o证码时间段
		this.tvTitle.setText(getResources().getString(R.string.register));// 初始化title
		this.btnGoLogin.setVisibility(View.VISIBLE);
		genderStrings = getResources().getStringArray(R.array.gender);
		setGetCodeBtn(false);
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
	 * EditText事件监听
	 */
	private void setViewsListener() {
		this.mMobileNum.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				String str = mMobileNum.getText().toString();
				if (ValidateTool.validateMobileNum(str)) {// 校验输入的手机号码是否合法
					// 设置值获取验证码按钮的状态，防止频繁操作
					setGetCodeBtn(true);
				} else {
					setGetCodeBtn(false);
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

	@Override
	public void onClick(View v) {
		if (v == btnGetCode) {
			this.user_tel = mMobileNum.getText().toString().trim();
			isExistUser();// 获取验证码之前验证用户是否存在
			// 校验用户注册信息
		} else if (v == mNexText) {
			if (checkFirstValue()) {
				time.cancel();
				mFirstLayout.setVisibility(View.GONE);
				mSecondLayout.setVisibility(View.VISIBLE);
			}
		} else if (v == btnGoLogin) {// 已有账号去登陆
			Util.getInstance().go2Activity(RegisterActivity.this, LoginActivity.class);
			finish();
		} else if (v == cbOk) {
			if (!this.cbOk.isChecked()) {
				isCheck = false;
				cbOk.setChecked(false);
				mNexText.setEnabled(false);
				mNexText.setBackgroundResource(R.drawable.btn_gray_state);
			} else {
				isCheck = true;
				cbOk.setChecked(true);
				mNexText.setEnabled(true);
				mNexText.setBackgroundResource(R.drawable.btn_blue_selector);
				Util.getInstance().go2StaticHtmlPage(RegisterActivity.this, Constant.ARGEMENT,
						getString(R.string.use_clause));
			}

		} else if (v == mBack) {
			onBack();
		} else if (v == mRegister) {
			if (checkSecondValue()) {
				showAlertDialog();
			}
		} else if (v == mGender) {// 选择性别
			choiceGender();
		} else if (v == mBirthday) {
			Util.getInstance().showDateSelectorDialog(RegisterActivity.this, new Date(),
					dateSetListener);
		}
	}

	/**
	 * 日期监听
	 */
	private DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}
	};

	protected void choiceGender() {
		AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
		builder.setTitle(R.string.pick_gender).setItems(genderStrings,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mGender.setText(genderStrings[which]);
						setGender(genderStrings[which]);
					}
				});
		builder.create().show();
	}

	private void onBack() {
		if (mSecondLayout.getVisibility() == View.VISIBLE) {
			mSecondLayout.setVisibility(View.GONE);
			mFirstLayout.setVisibility(View.VISIBLE);
		} else {
			Util.getInstance().showDialog(this, "未注册完成，是否退出?", getString(R.string.register_exit),
					getString(R.string.register_continue), new OnOkOrCancelClickListener() {

						@Override
						public void onOkClick(int type) {
							if (type == PcbConfirmDialog.OK) {
								RegisterActivity.this.finish();
							}

						}
					});
		}
	}

	public void setGender(String mGenderString) {
		if (getString(R.string.my_data_gender_m).equals(mGenderString)) {
			this.gender = User.GENDER_MALE;
		} else {
			this.gender = User.GENDER_FEMALE;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 111:
			if (resultCode == 11) {
				Intent intent = new Intent();
				setResult(12, intent);
				finish();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		onBack();
	}

	/**
	 * 校验用户注册信息
	 * 
	 * @return 手机号码，验证码，密码校验合法后返回true,否则返回false
	 */
	private boolean checkFirstValue() {
		// 电话号码
		this.user_tel = this.mMobileNum.getText().toString().trim();
		if (!ValidateTool.validateMobileNum(user_tel)) {
			Util.showToast(RegisterActivity.this, getString(R.string.invalid_phone_num));
			return false;
		}

		// 验证码
		String strCode = this.etValidateCode.getText().toString().trim();
		if (TextUtils.isEmpty(strCode) || strCode.length() != 4) {
			Util.showToast(RegisterActivity.this, getString(R.string.register_validate_code));
			return false;
		}

		this.user_pwd = this.etPassword.getText().toString().trim();
		String strRpPwd = this.etRpPaw.getText().toString().trim();
		// 密码规则
		if (!this.user_pwd.matches("^[0-9a-zA-Z]{6,16}$")) {
			Util.showToast(RegisterActivity.this, "密码必须是数字，字母6－16位");
			return false;
		}

		if (!user_pwd.equals(strRpPwd)) {
			Util.showToast(RegisterActivity.this, getString(R.string.pwd_error));
			return false;
		}

		if (!isCheck) {
			Util.showToast(RegisterActivity.this, getString(R.string.not_agree_to_the_terms_of_use));
			return false;
		}
		return true;
	}

	private boolean checkSecondValue() {
		// 检查称呼
		final String nickString = mNickname.getText().toString();
		if (util.isNullOrEmpty(nickString)) {
			Util.showToast(this,
					getString(R.string.xx_can_not_null, getString(R.string.my_data_nickname_hint)));
			return false;
		} else if (!ValidateTool.isUserNameRight(nickString)) {
			Util.showToast(this, getString(R.string.my_data_nickname_hint) + "只能是数字，字母，中文");
			return false;
		}
		/*
		 * if (mBirthday.getText().toString().equals("")) {
		 * Util.showToast(MyDataActivity.this, "请选择生日"); return false; }
		 */
		return true;
	}

	/**
	 * 提示用户确认自己所填的信息正确新
	 */
	private void showAlertDialog() {
		if (util.isNetworkAvailable(this)) {
			util.showDialog(this, getString(R.string.msg_ack), getString(R.string.confirm),
					getString(R.string.msg_btn_text2), new OnOkOrCancelClickListener() {

						@Override
						public void onOkClick(int type) {
							if (type == PcbConfirmDialog.OK) {
								updateUserInfo();
							}
						}
					});

		} else {
			util.showTip(this);// 网络未连接
		}
	}

	/**
	 * 修改用户信息
	 */
	private void updateUserInfo() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("tel", mMobileNum.getText().toString().trim()));
		params.add(new BasicNameValuePair("pwd", user_pwd));
		params.add(new BasicNameValuePair("code", etValidateCode.getText().toString().trim()));

		params.add(new BasicNameValuePair("username", this.mNickname.getText().toString()));
		params.add(new BasicNameValuePair("gender", this.gender));
		String birthString = this.mBirthday.getText().toString();
		if (!TextUtils.isEmpty(birthString)) {
			String time = TimeUtils.formartTaskDate(this.mBirthday.getText().toString());
			params.add(new BasicNameValuePair("birthday", time));
		}
		Util.printLog("注册参数:" + params);

		util.doPostRequest(this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				JsonResult<User> jsonRegResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<User>>() {
						});
				if (jsonRegResult != null && jsonRegResult.isSuccess()) {
					User user = jsonRegResult.getData();// 获取用户信息对象
					if (user != null) {
						Util.getInstance().setUser(RegisterActivity.this, user);// 设置用户对象
						Util.getInstance().setPushEnable(RegisterActivity.this, user);
						Constant.reload_plan = true;
						Util.showToast(RegisterActivity.this, getString(R.string.register_su));
						Util.getInstance().go2Activity(RegisterActivity.this,
								CarSharingActivity.class);
						RegisterActivity.this.finish();
					}
				} else {
					Util.showToast(RegisterActivity.this, getString(R.string.register_fail));
				}

			}
		}, params, Constant.URL_ADDUSER, getString(R.string.register_ing), false);

	}

	/**
	 * 获取验证码
	 */
	public void getValidateCode() {
		httpRequest(GET_VALIDATECODE, Constant.URL_SENDTELCODE,
				getString(R.string.gets_the_verification_code), "tel", user_tel);

	}

	private void isExistUser() {
		httpRequest(CHECK_USER, Constant.URL_CHECKUSERACCOUNT, getString(R.string.check_user_info),
				"account", user_tel);

	}

	private void httpRequest(final int requestType, String url, String title, String paramKey,
			String paramValue) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(paramKey, paramValue));
		util.doPostRequest(RegisterActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					if (requestType == CHECK_USER) {
						if (jsonResult.getData() != null && !jsonResult.getData().equals(NOREGIST)) {// 已注册
							Util.showToast(RegisterActivity.this,
									getString(R.string.Log_in_directly));
						} else {// 未注册
							getValidateCode();// 获取验证码
						}
					} else if (requestType == GET_VALIDATECODE) {
						time.start();
						Util.showToast(RegisterActivity.this, "获取验证码成功");
					}
				} else {
					if (requestType == CHECK_USER) {
						Util.showToast(RegisterActivity.this, "请求失败");
					} else if (requestType == GET_VALIDATECODE) {
						Util.showToast(RegisterActivity.this, "获取验证码失败");
					}
				}
			}
		}, params, url, title, false);
	}

	/**
	 * 倒计时
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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
