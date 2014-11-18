package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.pay.AlipayResult;
import com.lepin.util.Constant;
import com.lepin.util.UMSharingMyOrder;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

@Contextview(R.layout.activity_pay)
public class PayActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	@ViewInject(id = R.id.pay_cost)
	private TextView mPayCostTextView;// 所需人民币

	@ViewInject(id = R.id.pay_pinchebi_over)
	private TextView mPinCheBiTextView;// 拼车币余额

	@ViewInject(id = R.id.pay_password)
	private EditText mLoginPassword;
	// 支付成功页面
	@ViewInject(id = R.id.pay_ok_use_pinchebi_num)
	private TextView mPayOkUsePinCheBi;

	@ViewInject(id = R.id.pay_ok_use_alipay_num)
	private TextView mPayOkUseAlipay;

	@ViewInject(id = R.id.pay_ok_note)
	private TextView mPayOkNote;

	@ViewInject(id = R.id.pay_start)
	private TextView mStart;

	@ViewInject(id = R.id.pay_end)
	private TextView mEnd;

	@ViewInject(id = R.id.pay_ok_back2_plaza)
	private Button mPayOkBack2Plaza;

	@ViewInject(id = R.id.pay_ok_share)
	private Button mPayOkShare;

	@ViewInject(id = R.id.pay_pay_btn)
	private Button mPayButton;

	@ViewInject(id = R.id.pay_pinche_bi_radio)
	private ImageButton mPihCheBiButton;

	@ViewInject(id = R.id.pay_pinche_bi_layout)
	private View mPinCheBiLayout;

	@ViewInject(id = R.id.pay_alipay_radio)
	private ImageButton mAlipayButton;

	@ViewInject(id = R.id.pay_alipay_layout)
	private View mAlipayLayout;

	private int mThisTimeCost;// 本次所花的钱
	private long mPinCheBiNum;// 我的拼车币余额
	private double mUserAlipayMoneyNum;// 使用支付宝要支付的费用
	private boolean isUserAliPay = false;// 是否使用支付宝支付
	private Util util = Util.getInstance();
	private String mPayId;// 如果是订单就是bookid,如果是计划就是carpoolProgramPassengerId
	private String idName;// 如果时订单就是"bookId"，计划就是"carpoolProgramPassengerId"
	private String url;
	private String type;
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		getUserCoins();
	}

	/**
	 * 获取用户金币
	 */

	public void getUserCoins() {
		
		util.doGetRequest(PayActivity.this, new OnHttpRequestDataCallback() {
			
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				JsonResult<String> cashResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if(cashResult != null && cashResult.isSuccess()){
				mPinCheBiNum = Long.parseLong(cashResult.getData());
				isEnoughMoney();
				}else{
					Util.showToast(PayActivity.this, getString(R.string.request_error));
				}
			}
		}, Constant.URL_GET_PINCHE_BI, getString(R.string.get_pinche_money), false);
		
	}

	private void isEnoughMoney() {
		mPayCostTextView.setText(Html.fromHtml(getString(R.string.pay_this_time)
				+ "<font color=\"#fe6b20\">" + "RMB" + mThisTimeCost + ".00" + "</font>"));
		mPinCheBiTextView.setText(getString(R.string.pcb_balance, mPinCheBiNum));
		isUserAliPay = mThisTimeCost * Constant.EXCHANGERATE > mPinCheBiNum;
		if (isUserAliPay) {// 使用支付宝
			setUsePinchebiPay(false);
			setUseAliPay();
		} else {
			setUsePinchebiPay(true);
		}

	}

	private void setUseAliPay() {
		mLoginPassword.setVisibility(View.GONE);
		mUserAlipayMoneyNum = (mThisTimeCost * Constant.EXCHANGERATE - mPinCheBiNum) / 10.0;
		mPayButton.setText(getString(R.string.pay_use_alipay));
		mPayButton.setVisibility(View.VISIBLE);
	}

	private void initView() {
		final Bundle mBundle = getIntent().getExtras();
		mThisTimeCost = mBundle.getInt("cost");
		mPayId = mBundle.getString(Constant.BOOK_ID);
		idName = mBundle.getString("id_name");
		url = mBundle.getString("url");
		type = mBundle.getString("type");
		Util.printLog("支付book_id:" + mPayId);
		mStart.setText(getString(R.string.pay_start, mBundle.getString("start_name")));
		mEnd.setText(getString(R.string.pay_end, mBundle.getString("end_name")));
		mBack.setOnClickListener(this);
		mTitle.setText(getString(R.string.pay));
		// 使用支付宝时要显示的
		mPayButton.setOnClickListener(this);
		// 支付成功页面
		mPayOkUseAlipay.setVisibility(View.VISIBLE);
		mPayOkNote.setText(getString(R.string.pay_ok));
		mPayOkBack2Plaza.setOnClickListener(this);
		mPayOkShare.setOnClickListener(this);

		mPinCheBiLayout.setOnClickListener(this);
		mAlipayLayout.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	protected void showPayDialog(String title) {
		util.showDialog(PayActivity.this, title, getString(R.string.pay),
				getString(R.string.my_info_btn_cancel), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							if (!isUserAliPay) {// 拼车币
								payUsePinCheBi();
							} else {// 使用支付宝
								initRecharge();
							}
						} else if (type == PcbConfirmDialog.CANCEL) {

						}
					}
				});
	}

	protected void payUsePinCheBi() {
		if (!util.isNetworkAvailable(PayActivity.this)) {
			Util.showToast(PayActivity.this, getString(R.string.network_unavaiable));
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(idName, mPayId));
		params.add(new BasicNameValuePair("pwd", mLoginPassword.getText().toString()));
		params.add(new BasicNameValuePair("payType", Constant.PAY_ONLINE));

		util.doPostRequest(PayActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				Gson gson = new GsonBuilder().create();
				TypeToken<JsonResult<String>> token = new TypeToken<JsonResult<String>>() {
				};
				JsonResult<String> jsonResult = gson.fromJson(result, token.getType());
				if (!jsonResult.isSuccess()) {// 支付不成功
					Util.showToast(PayActivity.this, jsonResult.getErrorMsg());
				} else {
					// 支付成功，可以分享
					showPayOkLayout();
				}
			}
		}, params, url, getString(R.string.paying), false);

		// util.doPostRequest(PayActivity.this, new OnDataLoadingCallBack() {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// Gson gson = new GsonBuilder().create();
		// TypeToken<JsonResult<String>> token = new
		// TypeToken<JsonResult<String>>() {
		// };
		// JsonResult<String> jsonResult = gson.fromJson(result,
		// token.getType());
		// if (!jsonResult.isSuccess()) {// 支付不成功
		// Util.showToast(PayActivity.this, jsonResult.getErrorMsg());
		// } else {
		// // 支付成功，可以分享
		// showPayOkLayout();
		// }
		// }
		//
		// }, params, url, getString(R.string.paying));
	}

	protected void showPayOkLayout() {
		Constant.s_PinCheBi -= (mThisTimeCost * Constant.EXCHANGERATE);// 支付成功，拼车币减少
		if (isUserAliPay) {
			mPayOkUsePinCheBi.setVisibility(View.GONE);
			mPayOkUseAlipay.setText(Html.fromHtml(getString(R.string.pay_use_alipay_pay)
					+ "<font color=\"#fe6b20\">" + mThisTimeCost + ".00" + "</font>"
					+ getString(R.string.unit_yuan)));
		} else {
			mPayOkUsePinCheBi.setText(Html.fromHtml(getString(R.string.pay_ok_use_pinchebi_num)
					+ "<font color=\"#fe6b20\">" + (mThisTimeCost * Constant.EXCHANGERATE)
					+ "</font>" + getString(R.string.unit_one)));
			mPayOkUseAlipay.setVisibility(View.GONE);
		}
		((LinearLayout) findViewById(R.id.pay_ok_layout)).setVisibility(View.VISIBLE);
		((LinearLayout) findViewById(R.id.pay_choice_layout)).setVisibility(View.INVISIBLE);
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				AlipayResult mAlipayResult = new AlipayResult((String) msg.obj);
				final String result = mAlipayResult.getResult("resultStatus=", ";memo");
				if (Constant.sResultStatus.containsKey(result)) {
					if (result.equals("9000")) {
						showPayOkLayout();
					} else {
						Util.showToast(PayActivity.this, Constant.sResultStatus.get(result));
					}
				}
				break;
			default:
				break;
			}
		};
	};

	protected void initRecharge() {
		// 拼车币
		Util.printLog("支付金额：" + mUserAlipayMoneyNum);
		final int money = (int) mThisTimeCost * 100;
		
		util.initRecharge(this, money, type, mPayId, mHandler);
		
	/*	util.getOperateOrderNum(PayActivity.this, String.valueOf(money), type, mPayId, new OnHttpRequestDataCallback() {
			
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				Util.printLog("payactivity 支付初始化:" + result);
				JsonResult<String> mJsonResult = Util.getInstance().getObjFromJsonResult(
						result, new TypeToken<JsonResult<String>>() {
						});
				if (mJsonResult != null && mJsonResult.isSuccess()) {
					util.go2RechargeOrPay(PayActivity.this, String.valueOf(mThisTimeCost),
							mJsonResult.getData(), getString(R.string.pay_body),
							getString(R.string.pay_order), mHandler);

				} else {
					Util.showToast(PayActivity.this, getString(R.string.init_f) + ":"
							+ mJsonResult.getErrorMsg());
				}
			}
		});*/

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mPayButton) {
			if (!util.isNetworkAvailable(PayActivity.this)) {
				Util.showToast(PayActivity.this, getString(R.string.network_unavaiable));
				return;
			}
			String title = "";
			if (isUserAliPay) {
				title = getString(R.string.pay_use_alipay_pay_yse_no, mThisTimeCost);
			} else {// 使用拼车币
				if (mThisTimeCost * Constant.EXCHANGERATE > mPinCheBiNum) {
					Util.showToast(PayActivity.this, getString(R.string.pin_che_bi_not_enough));
					return;
				}
				title = getString(R.string.this_time_use) + (mThisTimeCost * Constant.EXCHANGERATE)
						+ getString(R.string.pinche_bi);
			}
			showPayDialog(title);
		} else if (v == mBack) {
			finish();
		} else if (v == mPayOkBack2Plaza) {
			finish();
			// util.go2Activity(PayActivity.this, CarSharingActivity.class);
		} else if (v == mPayOkShare) {
			// util.shareOrder(PayActivity.this, mPayId, mController);
			util.share(PayActivity.this, mPayId, mController, UMSharingMyOrder.SHARE_TYPE_ORDER,
					Constant.URL_SHARE_ORDER, getString(R.string.share_order_content),
					getString(R.string.share_order_title));
		} else if (v == mPinCheBiLayout) {
			if (!mPihCheBiButton.isSelected()) setUsePinchebiPay(true);
		} else if (v == mAlipayLayout) {
			if (!mAlipayButton.isSelected()) setUsePinchebiPay(false);
		}

	}

	private void setUsePinchebiPay(boolean isUserPinchebi) {
		mPihCheBiButton.setSelected(isUserPinchebi);
		mAlipayButton.setSelected(!isUserPinchebi);
		isUserAliPay = !isUserPinchebi;
		if (isUserPinchebi) {
			// 如果拼车币足够
			if ((mPinCheBiNum / 10) >= mThisTimeCost) mLoginPassword.setVisibility(View.VISIBLE);
			mPayButton.setText(getString(R.string.pay_with_pcb));
		} else {
			if (mPayButton.getVisibility() == View.VISIBLE)
				mLoginPassword.setVisibility(View.GONE);
			mPayButton.setText(getString(R.string.pay_with_alipay));

		}
	}
}
