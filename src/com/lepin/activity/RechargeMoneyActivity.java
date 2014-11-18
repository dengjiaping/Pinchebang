package com.lepin.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.pay.AlipayResult;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.SERVICE_TYPE;

/**
 * 充值余额
 * 
 * @author zhiqiang
 * 
 */
@Contextview(R.layout.recharge_money_activity)
public class RechargeMoneyActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.recharge_money_edittext)
	private EditText mInputNumEditText;

	@ViewInject(id = R.id.recharge_money_recharge_btn)
	private Button mRechargeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		mBack.setOnClickListener(this);
		mTitle.setText(getString(R.string.recharge_title));
		mRechargeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mBack) {
			this.finish();
		} else if (v == mRechargeButton) {
			String money = mInputNumEditText.getText().toString();
			if (TextUtils.isEmpty(money)) {
				Util.showToast(this, getString(R.string.input_recharge_num));
			} else {
				try {
					if(money.startsWith("0")){
						Util.showToast(RechargeMoneyActivity.this, "充值金额必须是1-999元");
						return;
					}
					int rechargeMoney = Integer.parseInt(money);
					if (rechargeMoney <= 0 || rechargeMoney > 999) {
						Util.showToast(RechargeMoneyActivity.this, "充值金额必须是1-999元");
					} else {
						recharge(rechargeMoney);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Util.printLog("RechargeMoneyActivity金额转换错误");
				}
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				AlipayResult mAlipayResult = new AlipayResult((String) msg.obj);
				final String result = mAlipayResult.getResult("resultStatus=", ";memo");
				if (Constant.sResultStatus.containsKey(result)) {
					if (result.equals("9000")) {
						Util.showToast(RechargeMoneyActivity.this,
								Constant.sResultStatus.get(result));
						RechargeMoneyActivity.this.finish();
					} else {
						Util.showToast(RechargeMoneyActivity.this,
								Constant.sResultStatus.get(result));
					}
				}
			}
		};
	};

	private void recharge(int money) {
		Util.getInstance().initRecharge(this, money, SERVICE_TYPE.GOLD_RECHARGE.name(), null,
				mHandler);
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
