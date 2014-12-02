package com.lepin.activity;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.CarSharingApplication;
import com.lepin.entity.JsonResult;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.util.ValidateTool;

/**
 * 登陆界面,已经注册的用户通过注册的手机号码和密码进行登录，登录成功后保存用户名和密码同时进入拼车广场，失败则不跳转。
 * 当用户要进行发布、查看个人中心等功能是如果没有登陆，也将跳转到此界面进行登录。
 */
@Contextview(R.layout.login)
public class LoginActivity extends BaseActivity implements OnClickListener {
	// private static final String TAG = "LoginActivity";
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题

	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回

	@ViewInject(id = R.id.login_et_mobile)
	private EditText etMobile;// 手机号码

	@ViewInject(id = R.id.login_et_pwd)
	private EditText etPwd;// 密码

	@ViewInject(id = R.id.login_btn)
	private TextView btnLogin;// 登陆按钮

	@ViewInject(id = R.id.reg_btn)
	private TextView btnReg;// 注册按钮

	@ViewInject(id = R.id.forget_pwd)
	private TextView btnForget;// 忘记密码

	private String usrname;// 用户名
	private String password;// 密码
	private SharedPreferences sharedPreferences;// 文件存储
	private String action = "";
	private Util util = Util.getInstance();
	public final static String LOGIN_2_RETURN_AS_RESULT = "retrun_result";
	public final static String PHONE = "phone";
	private boolean isLoginAgain = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		getUser();// 获取保存的用户信息
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		String phone = "";
		final Bundle bundle = getIntent().getExtras();
		try {// 可能拿不到
			phone = bundle.getString(PHONE);
			isLoginAgain = bundle.getBoolean("login_again");
		} catch (Exception e) {
			e.printStackTrace();
		}
		etMobile.setText((phone == null || phone.equals("")) ? util
				.getPhoneNumber(LoginActivity.this) : phone);
		this.btnBack.setOnClickListener(this);
		this.btnReg.setOnClickListener(this);
		this.btnLogin.setOnClickListener(this);
		this.btnForget.setOnClickListener(this);
		this.tvTitle.setText(getResources().getString(R.string.loginTitle));// 初始化title
		this.btnReg.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View paramView) {
		switch (paramView.getId()) {
		case R.id.common_title_back:// 返回
			if (!isLoginAgain) finish();
			break;
		case R.id.login_btn:// 登陆
			if (validateForm()) {// 校验账号，密码合法性
				doLogin();

			}
			break;
		case R.id.reg_btn:// 注册
			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setAction(this.action);
			startActivity(intent);
			finish();
			break;
		case R.id.forget_pwd:// 忘记密码
			Intent forgetIntent = new Intent(this, FindPwdActivity.class);
			startActivity(forgetIntent);
			break;
		}
	}

	private void doLogin() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", this.usrname));
		params.add(new BasicNameValuePair("pwd", this.password));

		util.doPostRequest(LoginActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				doLoginResult(result);
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				Util.showToast(LoginActivity.this, errorMsg);
			}
		}, params, Constant.URL_LOGIN, getString(R.string.login_ing), true);
	}

	/**
	 * 获取用户注册时的账号
	 */
	private void getUser() {
		this.sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
		String tel = this.sharedPreferences.getString("userPhone", "");
		String pwd = this.sharedPreferences.getString("pwd", "");
		if ("" != tel) {
			this.etMobile.setText(tel);
		}
		if (!"".equals(pwd)) {
			this.etPwd.setText(pwd);
		}
	}

	/**
	 * 登陆信息校验
	 * 
	 * @return 手机号码，密码校验合法返回true，否则返回false
	 */
	private boolean validateForm() {
		boolean isCheck = false;
		this.usrname = this.etMobile.getText().toString();
		this.password = this.etPwd.getText().toString();
		if (!util.isNullOrEmpty(this.usrname)) {
			// 校验用户名为邮箱格式为后期Web版注册账号联合使用实现登录
			if (ValidateTool.validateEmailFormat(this.usrname)
					|| ValidateTool.validateMobileNum(this.usrname)) {
				if (!util.isNullOrEmpty(this.password)
						&& ((this.password.length() <= 16) && (this.password.length() >= 6))) {
					isCheck = true;
				} else {
					Util.showToast(this, getString(R.string.pwd_error));
				}
			} else {
				Util.showToast(this, getString(R.string.account_error));
			}
		} else {
			Util.showToast(this, getString(R.string.account_null));
		}
		return isCheck;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void doLoginResult(String result) {
		JsonResult<User> jresult = util.getObjFromJsonResult(result,
				new TypeToken<JsonResult<User>>() {
				});

		if (jresult != null) {
			if (jresult.isSuccess()) {// 登录成功
				Constant.is_refresh_orders = true;
				User user = jresult.getData();
				if (user != null) {
					util.setUser(LoginActivity.this, user);
					util.setPushEnable(LoginActivity.this, user);
				}
				finish();
			} else {
				Util.showToast(LoginActivity.this, jresult.getErrorMsg());
			}
		} else {
			Util.showToast(LoginActivity.this, getString(R.string.request_error));
		}
	}

	@Override
	public void onBackPressed() {
		if (isLoginAgain) {
			CarSharingApplication.Instance().exit(LoginActivity.this);
		} else {
			finish();
		}
	}
}
