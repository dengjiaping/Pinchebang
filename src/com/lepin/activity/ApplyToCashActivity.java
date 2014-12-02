package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.GoldBank;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

/**
 * 申请提现
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.apply_to_cash_activity)
public class ApplyToCashActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.apply_to_cash_money_edittext)
	private EditText mApplyBalanceEditText;// 提现金额

	@ViewInject(id = R.id.apply_to_cash_title)
	private TextView mApplyNoteTitleTextView;// 提示

	@ViewInject(id = R.id.apply_to_cash_no_account_layout)
	private RelativeLayout mApplyNoAccountLayout;// 没有支付宝账户的布局

	@ViewInject(id = R.id.apply_to_cash_has_account_layout)
	private LinearLayout mApplyHasAccountLayout;// 有支付宝账户的布局

	@ViewInject(id = R.id.apply_to_cash_add_account_btn)
	private Button mApplyAddAccountButton;// 添加提现账户按钮

	@ViewInject(id = R.id.apply_to_cash_alipay_account_edittext)
	private EditText mApplyAlipayAccountEditText;// 输入的支付宝账户

	@ViewInject(id = R.id.apply_to_cash_name_edittext)
	private EditText mApplyAlipayNameEditText;// 输入的支付宝账户名称

	@ViewInject(id = R.id.apply_to_cash_pwd_edittext)
	private EditText mApplyAlipayPwdEditText;// 输入的支付密码

	@ViewInject(id = R.id.apply_to_cash_apply_btn)
	private Button mApplySubmit;// 提现按钮

	ArrayList<CheckedTextView> accountLayouts = new ArrayList<CheckedTextView>();
	List<GoldBank> allGoldBanks = new ArrayList<GoldBank>();// 已经有的账户

	// private boolean hasAccount = false;// 是否已经添加了账户
	private GoldBank choiceGoldBank = null;

	private int mRequestCode = 1;
	private LayoutInflater inflater;
	private LayoutParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		setListener();
		mTitle.setText(getString(R.string.apply_to_cash));
		inflater = LayoutInflater.from(this);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(
				R.dimen.dp_35));
		getCashAccount();
	}

	private void getCashAccount() {
		Util.getInstance().doGetRequest(this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				// Util.printLog("获取现金账户:" + result);
				if (!TextUtils.isEmpty(result)) {
					JsonResult<List<GoldBank>> jsonResult = Util.getInstance()
							.getObjFromJsonResult(result,
									new TypeToken<JsonResult<List<GoldBank>>>() {
									});
					if (jsonResult != null && jsonResult.isSuccess()) {
						List<GoldBank> goldBanks = jsonResult.getData();
						if (goldBanks != null) {
							if (goldBanks.size() == 0) {// 还没有添加账户
								mApplyNoAccountLayout.setVisibility(View.VISIBLE);
								mApplyNoteTitleTextView
										.setText(getString(R.string.input_account_and_name));
								mApplyNoteTitleTextView.setVisibility(View.VISIBLE);
							} else {
								if (allGoldBanks.size() == 0) {// 还没有添加账户
									allGoldBanks.addAll(goldBanks);
									addAccountLayout(goldBanks);
								} else {// 又增加了账户
									if (goldBanks.size() > allGoldBanks.size()) {
										allGoldBanks.clear();
										for (CheckedTextView checkedTextView : accountLayouts) {
											mApplyHasAccountLayout.removeView(checkedTextView);
										}
										allGoldBanks.addAll(goldBanks);
										addAccountLayout(goldBanks);
									}

								}
							}
						} else {
							mApplyNoAccountLayout.setVisibility(View.VISIBLE);
						}
					} else {

					}
				} else {// 获取失败
				}

			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				if (errorType.equals("PAYPWD_NOT_EXIST")) {
					showNoPayPwdNotice();
				} else {
					Util.showToast(ApplyToCashActivity.this, errorMsg);
				}
			}
		}, Constant.GET_CASH_ACCOUNT, getString(R.string.get_cash_accounts), true);

	}

	protected void showNoPayPwdNotice() {
		mApplyNoAccountLayout.setVisibility(View.VISIBLE);
		Util.getInstance().showDialogWithCanCancel(this, getString(R.string.not_pay_pwd_notice),
				getString(R.string.setting_now), getString(R.string.setting_later),
				new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							Intent intent = new Intent(ApplyToCashActivity.this,
									MyPayPswSettingActivity.class);
							intent.putExtra("return", true);
							startActivityForResult(intent, mRequestCode);
						} else {
							finish();
						}

					}
				}, false);

	}

	private void setListener() {
		mBack.setOnClickListener(this);
		mApplyAddAccountButton.setOnClickListener(this);
		mApplySubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBack) {
			this.finish();
		} else if (v == mApplyAddAccountButton) {// 添加提现账户
			go2AddAccount();
		} else if (v == mApplySubmit) {// 提现按钮
			checkAndApply();
		}
	}

	private void go2AddAccount() {
		startActivityForResult(new Intent(ApplyToCashActivity.this,
				MyCashAccountAddOrModifyActivity.class), 1);
	}

	private void checkAndApply() {
		if (checkValue()) {
			applyToCash();
		}
	}

	private void applyToCash() {
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		final String cashValue = mApplyBalanceEditText.getText().toString();
		int money = Integer.parseInt(cashValue) * 100;
		paramsList.add(new BasicNameValuePair("amount", String.valueOf(money)));// 提现金额:分
		if (allGoldBanks.size() > 0 && choiceGoldBank != null) {
			paramsList.add(new BasicNameValuePair("goldBankId", String.valueOf(choiceGoldBank
					.getGoldBankId())));// 提现账户ID
			paramsList.add(new BasicNameValuePair("account", choiceGoldBank.getAccount()));// 提现账户名
			paramsList.add(new BasicNameValuePair("trueName", choiceGoldBank.getTrueName()));// 姓名
		} else {
			paramsList.add(new BasicNameValuePair("account", mApplyAlipayAccountEditText.getText()
					.toString()));// 提现账户名
			paramsList.add(new BasicNameValuePair("trueName", mApplyAlipayNameEditText.getText()
					.toString()));// 姓名
		}
		paramsList.add(new BasicNameValuePair("payPwd", mApplyAlipayPwdEditText.getText()
				.toString()));// 支付密码
		Util.printLog("申请提现参数：" + paramsList);
		Util.getInstance().doPostRequest(this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					showNoticeDialog();
				}
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				Util.showToast(ApplyToCashActivity.this, errorMsg);
			}
		}, paramsList, Constant.APPLY_TO_CASH, "申请提现中...", true);

	}

	private void showNoticeDialog() {
		AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
		dBuilder.setTitle(getString(R.string.prompt))
				.setMessage("你的提现申请已经提交成功，我们将在一个工作日内处理你的提现申请，请注意查收相应账户").setCancelable(false);
		dBuilder.setPositiveButton(getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				});
		dBuilder.show();
	}

	private boolean checkValue() {
		final String cashValue = mApplyBalanceEditText.getText().toString();

		if (TextUtils.isEmpty(cashValue) || cashValue.equals("0")) {
			Util.printLog("提现金额有错误!");
			return false;
		}
		if (allGoldBanks.size() > 0) {
			if (choiceGoldBank == null) {
				Util.showToast(ApplyToCashActivity.this, "请选择账户");
				return false;
			}
		} else {

			final String alipayAccount = mApplyAlipayAccountEditText.getText().toString();
			if (TextUtils.isEmpty(alipayAccount)) {
				Util.showToast(ApplyToCashActivity.this, "请输入支付宝账户");
				return false;
			}

			final String name = mApplyAlipayNameEditText.getText().toString();
			if (TextUtils.isEmpty(name)) {
				Util.showToast(ApplyToCashActivity.this, "请输入姓名");
				return false;
			}
		}

		final String pwd = mApplyAlipayPwdEditText.getText().toString();
		if (TextUtils.isEmpty(pwd)) {
			Util.showToast(ApplyToCashActivity.this, "请输入支付密码");
			return false;
		}
		return true;

	}

	/**
	 * 动态添加账户
	 * 
	 * @param goldBanks
	 */
	private void addAccountLayout(List<GoldBank> goldBanks) {
		mApplyNoteTitleTextView.setText(getString(R.string.choice_account));
		mApplyNoteTitleTextView.setVisibility(View.VISIBLE);
		params.gravity = Gravity.CENTER_VERTICAL;
		for (GoldBank g : goldBanks) {
			addAccount2View(g);
		}
		mApplyHasAccountLayout.setVisibility(View.VISIBLE);
	}

	private void addAccount2View(GoldBank g) {
		final CheckedTextView checkedTextView = (CheckedTextView) inflater.inflate(
				R.layout.item_radio_text, null);
		checkedTextView.setTag(g);// 设置标识
		accountLayouts.add(checkedTextView);
		checkedTextView.setText(g.getTrueName());
		checkedTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (accountLayouts.size() > 1) {
					for (CheckedTextView l : accountLayouts) {
						l.setSelected(false);
					}
				}

				if (v.isSelected()) {
					v.setSelected(false);
				} else {
					v.setSelected(true);
				}
				choiceGoldBank = (GoldBank) v.getTag();
			}
		});
		mApplyHasAccountLayout.addView(checkedTextView, 0, params);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Util.printLog("添加成功返回:requestCode:" + requestCode + "--resultCode:" + resultCode);
		if (requestCode == mRequestCode && resultCode == RESULT_OK) {
			getCashAccount();
		}
	}
}
