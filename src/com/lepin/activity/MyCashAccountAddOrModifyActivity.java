package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;

@Contextview(R.layout.my_cash_account_add_modify)
public class MyCashAccountAddOrModifyActivity extends Activity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitleTextView;

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBackBtn;

	@ViewInject(id = R.id.setting_cash_account_layout)
	private LinearLayout mAddCashAccountLayout; // 增加提现账户layout

	@ViewInject(id = R.id.add_modify_cash_account_tv)
	private TextView mAddModifyCashAccountTv; // 增加支付宝账户的编辑框

	@ViewInject(id = R.id.add_alipay_cash_account_edit)
	private EditText mAddAlipayCashAccountEditText; // 增加支付宝账户的编辑框

	@ViewInject(id = R.id.my_cash_account_name)
	private EditText mAddAlipayCashAccountNameEditText; // 增加支付宝名称编辑框

	@ViewInject(id = R.id.add_cash_account_sure)
	private Button mAddCashAccountBtn; // 确定添加

	private Util util = Util.getInstance();

	private String account;
	private String name;
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		account = getIntent().getStringExtra("account");
		name = getIntent().getStringExtra("name");
		id = getIntent().getStringExtra("id");
		mTitleTextView.setText(getResources().getString(R.string.cash_account_title));
		if (null != account || null != name) {
			mAddAlipayCashAccountEditText.setText(account);
			mAddAlipayCashAccountNameEditText.setText(name);
			mAddModifyCashAccountTv.setText(getResources().getString(
					R.string.modify_cash_account_text));
		} else {
			mAddModifyCashAccountTv.setText(getResources()
					.getString(R.string.add_cash_account_text));
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setOnclick();
	}

	private void setOnclick() {
		// TODO Auto-generated method stub
		mBackBtn.setOnClickListener(this);
		mAddCashAccountBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBackBtn) {
			MyCashAccountAddOrModifyActivity.this.finish();
		} else if (v == mAddCashAccountBtn) {
			addCashAccount();
		}
	}

	private void addCashAccount() {
		// TODO Auto-generated method stub
		String alipayAccountString = mAddAlipayCashAccountEditText.getText().toString();
		String alipayTrueNameString = mAddAlipayCashAccountNameEditText.getText().toString();
		addCashAccount(alipayAccountString, alipayTrueNameString);

	}

	private void addCashAccount(String account, String trueName) {
		// TODO Auto-generated method stub
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("trueName", trueName));
		if (id != null) {
			params.add(new BasicNameValuePair("goldBankId", id));
		}
		util.doPostRequest(MyCashAccountAddOrModifyActivity.this, new OnHttpRequestDataCallback() {
			@Override
			public void onSuccess(String result) {
				JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					Util.showToast(
							MyCashAccountAddOrModifyActivity.this,
							getResources().getString(
									R.string.add_or_modify_cash_account_success_toast));
					setResult(RESULT_OK);
					finish();
				}
			}

			public void onFail(String errorType, String errorMsg) {
				Util.showToast(MyCashAccountAddOrModifyActivity.this, errorMsg);
			};
		}, params, Constant.ADD_DELETE_ACCOUNT, getString(R.string.adding_cash_account_tip), true);
	}

}
