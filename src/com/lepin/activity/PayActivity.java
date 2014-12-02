package com.lepin.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lepin.entity.JsonResult;
import com.lepin.entity.MyPincheCoinAndBalance;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.pay.AlipayResult;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

@Contextview(R.layout.activity_pay)
public class PayActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_back)
	private ImageView mBack;

	@ViewInject(id = R.id.common_title_title)
	private TextView mTitle;

	// @ViewInject(id = R.id.pay_cost)
	// private TextView mPayCostTextView;// 所需人民币
	//
	// @ViewInject(id = R.id.pay_pinchebi_over)
	// private TextView mPinCheBiTextView;// 拼车币余额

	/**
	 * 本次拼车费用
	 */
	@ViewInject(id = R.id.pay_Carpool_costs_num)
	private TextView mCarpoolCostsNum;

	/**
	 * 单次乘客保险
	 */
	@ViewInject(id = R.id.pay_insurance_costs_num)
	private TextView mInsuranceCostsNum;

	/**
	 * 总计
	 */
	@ViewInject(id = R.id.pay_total_costs_num)
	private TextView mTotalCostsNum;

	/**
	 * 是否使用拼车币
	 */
	@ViewInject(id = R.id.pay_is_use_pinche_coin)
	private CheckedTextView mIsUsePincheCoin;

	/**
	 * 用户选择需要使用拼车币
	 */
	@ViewInject(id = R.id.pay_input_pinche_coin_layout)
	private RelativeLayout mInputPincheCoinLayout;

	/**
	 * 用户输入的需要使用拼车币
	 */
	@ViewInject(id = R.id.pay_input_pinche_coin_num)
	private EditText mInputPincheCoinNum;

	/**
	 * 提示用户有多少拼车币可用
	 */
	@ViewInject(id = R.id.pay_pinche_coin_show)
	private TextView mCanUsePincheCoinNum;

	/**
	 * 实际支付
	 */
	@ViewInject(id = R.id.pay_real_pay_num)
	private TextView mRealCosts;

	/**
	 * 是否填写参保人信息
	 */
	@ViewInject(id = R.id.pay_is_input_insurance_info)
	private CheckedTextView mIsInputInsuranceInfo;

	/**
	 * 填写参保人信息Layout
	 */
	@ViewInject(id = R.id.pay_insurance_info_layout)
	private RelativeLayout mInputInsuranceInfoLayout;

	/**
	 * 参保人姓名
	 */
	@ViewInject(id = R.id.pay_insurance_info_name_input)
	private EditText mInsuranceInfoName;

	/**
	 * 身份证号码
	 */
	@ViewInject(id = R.id.pay_insurance_info_id_input)
	private EditText mInsuranceInfoID;

	/**
	 * 支付密码
	 */
	@ViewInject(id = R.id.pay_password)
	private EditText mPayPassword;
	// // 支付成功页面
	// @ViewInject(id = R.id.pay_ok_use_pinchebi_num)
	// private TextView mPayOkUsePinCheBi;
	//
	// @ViewInject(id = R.id.pay_ok_use_alipay_num)
	// private TextView mPayOkUseAlipay;
	//
	// @ViewInject(id = R.id.pay_ok_note)
	// private TextView mPayOkNote;
	//
	@ViewInject(id = R.id.pay_start)
	private TextView mStart;

	@ViewInject(id = R.id.pay_end)
	private TextView mEnd;
	//
	// @ViewInject(id = R.id.pay_ok_back2_plaza)
	// private Button mPayOkBack2Plaza;
	//
	// @ViewInject(id = R.id.pay_ok_share)
	// private Button mPayOkShare;

	/**
	 * 确认支付
	 */
	@ViewInject(id = R.id.pay_pay_btn)
	private Button mPayButton;

	// @ViewInject(id = R.id.pay_pinche_bi_radio)
	// private ImageButton mPihCheBiButton;
	//
	// @ViewInject(id = R.id.pay_pinche_bi_layout)
	// private View mPinCheBiLayout;
	//
	// @ViewInject(id = R.id.pay_alipay_radio)
	// private ImageButton mAlipayButton;
	//
	// @ViewInject(id = R.id.pay_alipay_layout)
	// private View mAlipayLayout;

	/**
	 * 拼车花费费用
	 */
	private long mThisTimeCostRMB;

	/**
	 * 总计花销
	 */
	private long mTotalCostsNumRMB;

	/**
	 * 单次保险费用
	 */
	private long mInsuranceCostsRMB;

	/**
	 * 实际支付
	 */
	private long mRealCostsRMB;
	/**
	 * 我的拼车币
	 */
	private long mPinCheBiNum;
	/**
	 * 我使用的拼车币
	 */
	private long mUsePinCheBiNum;

	/**
	 * 我的余额
	 */
	private long mMyBalance;
	/**
	 * 使用支付宝要支付的费用
	 */
	private double mUserAlipayMoneyNum;
	/**
	 * 是否用支付宝支付
	 */
	private boolean isUserAliPay = false;

	/**
	 * 是否买保险
	 */
	private boolean isBuyInsurance = false;

	private LatLng end_L = null;
	private LatLng start_L = null;
	private Util util = Util.getInstance();
	private String mPayId;// 如果是订单就是bookid,如果是计划就是carpoolProgramPassengerId
	// private String idName;// 如果时订单就是"bookId"，计划就是"carpoolProgramPassengerId"
	private String url;
	/**
	 * 起点维度
	 */
	private int mStartLat;
	/**
	 * 起点经度
	 */
	private int mStartLon;
	/**
	 * 终点维度
	 */
	private int mEndLat;
	/**
	 * 终点经度
	 */
	private int mEndLon;
	/**
	 * 起点
	 */
	private String mStartName;
	/**
	 * 终点
	 */
	private String mEndName;
	/**
	 * 拼车方式 长途或者上下班
	 */
	private String mStyle;
	private TextWatcher mWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

			if (!mInputPincheCoinNum.getText().toString().equals("")) {
				long num = Long.parseLong(mInputPincheCoinNum.getText().toString());
				if (num <= ((mPinCheBiNum <= (mTotalCostsNumRMB - mInsuranceCostsRMB) ? mPinCheBiNum
						: (mTotalCostsNumRMB - mInsuranceCostsRMB)) - 1)) {
					mUsePinCheBiNum = num;
				} else {
					mUsePinCheBiNum = (mPinCheBiNum <= (mTotalCostsNumRMB - mInsuranceCostsRMB) ? mPinCheBiNum
							: (mTotalCostsNumRMB - mInsuranceCostsRMB)) - 1;
					mInputPincheCoinNum.removeTextChangedListener(mWatcher);
					mInputPincheCoinNum.setText(String.valueOf(mUsePinCheBiNum));
					mInputPincheCoinNum.addTextChangedListener(mWatcher);
					Util.showToast(PayActivity.this, "最多可用" + (mUsePinCheBiNum) + "拼车币");
				}
			} else {
				mUsePinCheBiNum = 0;
			}
			setCosts();
		}
	};

	private TextWatcher mPayPswWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			mPayPassword.removeTextChangedListener(mPayPswWatcher);
			mPayPassword.setText("");
			showSettingPswDialog(getString(R.string.pay_setting_psw_dialog_title));
			mPayPassword.addTextChangedListener(mPayPswWatcher);
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// mPayPassword.setText("");
		}
	};
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);

	private GeoCoder mSearch;

	/**
	 * 是否设置支付密码
	 */
	private Boolean isSettingPayPsw;

	/**
	 * 获取坐标是否成功
	 */
	private Boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		getUserCoins();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		mPayPassword.removeTextChangedListener(mPayPswWatcher);
	}

	/**
	 * 获取坐标的Listener
	 */
	private OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		}

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (!(result == null || result.error != SearchResult.ERRORNO.NO_ERROR)) {
				// 获取坐标成功
				LatLng location = result.getLocation();
				if (!flag) {
					end_L = location;
				}
				if (flag) {
					mSearch.geocode(new GeoCodeOption().city(mEndName).address(mEndName));
					flag = false;
					start_L = location;
				}
				if (end_L != null && start_L != null) {
					double mDistance = Util.getInstance().get2PointsDistances(start_L, end_L);
					mInsuranceCostsRMB = calcInsuranceResult(mDistance);
					setCosts();
				}
			}
		}
	};

	/**
	 * 获取用户金币
	 */

	public void getUserCoins() {
		util.doGetRequest(PayActivity.this, new OnHttpRequestDataCallback() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				JsonResult<MyPincheCoinAndBalance> cashResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<MyPincheCoinAndBalance>>() {
						});
				if (cashResult != null && cashResult.isSuccess()) {
					MyPincheCoinAndBalance coinAndBalance = cashResult.getData();
					mPinCheBiNum = Long.parseLong(coinAndBalance.getCoin());
					mMyBalance = Long.parseLong(coinAndBalance.getGold());
					IsUsePinCheCoin(mIsUsePincheCoin.isChecked());
					// isEnoughMoney();
				} else {
					Util.showToast(PayActivity.this, getString(R.string.request_error));
				}
				setCosts();
			}
		}, Constant.URL_GET_PINCHE_COIN_BALANCE, getString(R.string.get_pinche_coin_balance), false);
	}

	/**
	 * 设置 实际支付费用 和 总计花费
	 */
	public void setCosts() {
		if (!mIsInputInsuranceInfo.isChecked() && !mIsUsePincheCoin.isChecked()) { // 不买保险，不用拼车币
			mRealCostsRMB = mThisTimeCostRMB;
			mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
		}
		if (mIsInputInsuranceInfo.isChecked() && !mIsUsePincheCoin.isChecked()) { // 买保险，不用拼车币
			if (isOVer2015()) {
				mRealCostsRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
				mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
			} else {
				mRealCostsRMB = mThisTimeCostRMB;
				mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
			}
		}
		if (!mIsInputInsuranceInfo.isChecked() && mIsUsePincheCoin.isChecked()) {// 不买保险，用拼车币
			mRealCostsRMB = (mThisTimeCostRMB - mUsePinCheBiNum);
			mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
		}
		if (mIsInputInsuranceInfo.isChecked() && mIsUsePincheCoin.isChecked()) { // 买保险，用拼车币
			if (isOVer2015()) {
				mRealCostsRMB = mThisTimeCostRMB + mInsuranceCostsRMB - mUsePinCheBiNum;
				mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
			} else {
				mRealCostsRMB = mThisTimeCostRMB - mUsePinCheBiNum;
				mTotalCostsNumRMB = mThisTimeCostRMB + mInsuranceCostsRMB;
			}
		}
		mRealCosts.setText(switchToStringNum(mRealCostsRMB));
		mTotalCostsNum.setText(switchToStringNum(mTotalCostsNumRMB));
	}

	private void initView() {
		final Bundle mBundle = getIntent().getExtras();
		mThisTimeCostRMB = mBundle.getInt("cost") * Constant.EXCHANGERATE;
		mPayId = mBundle.getString(Constant.BOOK_ID);
		url = Constant.URL_ORDER_PAY_NOW;
		this.mStartLat = mBundle.getInt(Constant.START_LAT);
		this.mStartLon = mBundle.getInt(Constant.START_LON);
		this.mEndLat = mBundle.getInt(Constant.END_LAT);
		this.mEndLon = mBundle.getInt(Constant.END_LON);
		this.mStyle = mBundle.getString(Constant.TRIP_MODE);
		mStartName = mBundle.getString("start_name");
		mEndName = mBundle.getString("end_name");
		Util.printLog("支付book_id:" + mPayId);
		mStart.setText(getString(R.string.pay_start, mBundle.getString("start_name")));
		mEnd.setText(getString(R.string.pay_end, mBundle.getString("end_name")));
		mBack.setOnClickListener(this);
		mTitle.setText(getString(R.string.pay));
		// 使用支付宝时要显示的
		mPayButton.setOnClickListener(this);
		mCarpoolCostsNum.setText(switchToStringNum(mThisTimeCostRMB));
		mIsUsePincheCoin.setOnClickListener(this);
		mIsInputInsuranceInfo.setOnClickListener(this);
		mIsUsePincheCoin.setChecked(false); // 刚进入默认不适用拼车币
		mIsInputInsuranceInfo.setChecked(false); // 默认也不买保险
		IsBuyInsurance(mIsInputInsuranceInfo.isChecked());
		mInputPincheCoinNum.addTextChangedListener(mWatcher);
		isSettingPayPsw = util.getUser(PayActivity.this).isPayPwdSet();
		if (!isSettingPayPsw) {
			mPayPassword.addTextChangedListener(mPayPswWatcher);
		}
		if (mStyle.equals("LONG_TRIP")) {
			calcInsuranceCosts_L();
		} else if (mStyle.equals("ON_OFF_WORK")) {
			mInsuranceCostsRMB = calcInsuranceCosts_W();
		}
		setCosts();
	}

	/**
	 * 是否购买保险
	 * 
	 * @param checked
	 */
	private void IsBuyInsurance(boolean checked) {
		// TODO Auto-generated method stub
		if (mIsInputInsuranceInfo.isChecked()) { // 是否选中交纳保险
			if (isOVer2015()) {
				mInsuranceCostsNum.setText("RMB " + switchToStringNum(mInsuranceCostsRMB));
			} else {
				mInsuranceCostsNum.setText("RMB " + mThisTimeCostRMB);
			}
			mInputInsuranceInfoLayout.setVisibility(View.VISIBLE);
		} else {
			mInsuranceCostsNum.setText("RMB 0.00");
			mInputInsuranceInfoLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否使用拼车币
	 * 
	 * @param checked
	 */
	private void IsUsePinCheCoin(boolean checked) {
		// TODO Auto-generated method stub
		if (checked) { // 是否选中使用拼车币
			mInputPincheCoinLayout.setVisibility(View.VISIBLE);
			mCanUsePincheCoinNum
					.setText(((mPinCheBiNum <= (mTotalCostsNumRMB - mInsuranceCostsRMB) ? mPinCheBiNum
							: (mTotalCostsNumRMB - mInsuranceCostsRMB)) - 1)
							+ getResources().getString(R.string.pay_num_pinche_coin_can_use));
		} else {
			mInputPincheCoinLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 是否超过2015年，超过不送保险费 超过为true 没超过为false
	 */
	private boolean isOVer2015() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long now = System.currentTimeMillis();
		long time2015 = 0;
		try {
			java.util.Date date = sdf.parse("2015-01-02 00:00:00");
			time2015 = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (now - time2015) <= 0 ? false : true;
	}

	/**
	 * 计算 上下班保险费用 单位分
	 * 
	 * @return
	 */
	private long calcInsuranceCosts_W() {
		// TODO Auto-generated method stub
		Double mDistance = null;
		LatLng end = null;
		LatLng start = null;
		start = new LatLng((double) mStartLat / 1e6, (double) mStartLon / 1e6);
		end = new LatLng((double) mEndLat / 1e6, (double) mEndLon / 1e6);
		// 要获两个地址的距离
		mDistance = Util.getInstance().get2PointsDistances(start, end);
		util.printLog("相距距离" + mDistance);
		return calcInsuranceResult(mDistance);
	}

	/**
	 * 计算 长途保险费用 单位分
	 * 
	 * @return
	 */
	private void calcInsuranceCosts_L() {
		// TODO Auto-generated method stub
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
		LatLng end = null;
		LatLng start = null;
		// 要获取某个城市的坐标
		flag = true;
		mSearch.geocode(new GeoCodeOption().city(mStartName).address(mStartName));
	}

	private long calcInsuranceResult(double distance) {
		if (distance == 0 || distance > 500 * 1000) {
			mInsuranceCostsRMB = 0;
		} else if (distance < 100 * 1000) {
			mInsuranceCostsRMB = 100; // 单位 分
		} else if (distance < 200 * 1000) {
			mInsuranceCostsRMB = 200;
		} else if (distance <= 500 * 1000) {
			mInsuranceCostsRMB = 300;
		}
		mInsuranceCostsNum.setText(switchToStringNum(mInsuranceCostsRMB));
		return mInsuranceCostsRMB;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * @param num
	 * @return 将long 类型转成 String 用于几个金额显示
	 */
	private String switchToStringNum(long num) {
		double d = num;
		double i = d / Constant.EXCHANGERATE;
		String string = String.format("%.2f", i);
		return "RMB " + string;
	}

	protected void showPayDialog(String title) {
		util.showDialog(PayActivity.this, title, getString(R.string.pay),
				getString(R.string.my_info_btn_cancel), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							pay();
						}
					}
				});
	}

	protected void pay() {
		if (!util.isNetworkAvailable(PayActivity.this)) {
			Util.showToast(PayActivity.this, getString(R.string.network_unavaiable));
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("infoOrderId", mPayId));
		params.add(new BasicNameValuePair("coin", mInputPincheCoinNum.getText().toString()));
		params.add(new BasicNameValuePair("buyInsurance", String.valueOf(isBuyInsurance)));
		params.add(new BasicNameValuePair("payPwd", mPayPassword.getText().toString()));
		if (isBuyInsurance) {
			params.add(new BasicNameValuePair("trueNames", mInsuranceInfoName.getText().toString()));
			params.add(new BasicNameValuePair("idcards", mInsuranceInfoID.getText().toString()));
			params.add(new BasicNameValuePair("insurancePrice", String.valueOf(mInsuranceCostsRMB)));
		}

		if (isUserAliPay) { // 支付宝支付
			url = Constant.URL_ORDER_PAY_NOW;
			params.add(new BasicNameValuePair("amount", String.valueOf((int) mUserAlipayMoneyNum)));
			params.add(new BasicNameValuePair("serviceType", "INFO_ORDER"));
			params.add(new BasicNameValuePair("rechargeType", "ALIPAY"));
		} else { // 拼车币和余额支付
			url = Constant.URL_PAY;
			params.add(new BasicNameValuePair("payType", Constant.PAY_ONLINE));
		}

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
					String mALipayOrder = null;
					if (isUserAliPay) {
						mALipayOrder = jsonResult.getData();
					}
					showPayOkLayout(mALipayOrder);
				}
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				// TODO Auto-generated method stub
				super.onFail(errorType, errorMsg);
				Util.showToast(PayActivity.this, errorMsg);
			}
		}, params, url, getString(R.string.paying), true);
	}

	protected void showPayOkLayout(String mALipayOrder) {
		// Constant.s_PinCheBi -= (mThisTimeCostRMB * Constant.EXCHANGERATE);//
		// 支付成功，拼车币减少
		if (isUserAliPay) {
			initRecharge(mALipayOrder);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString(Constant.BOOK_ID, mPayId);
			util.go2OrderDetail(PayActivity.this, bundle);
			PayActivity.this.finish();
		}
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
						Bundle bundle = new Bundle();
						bundle.putString(Constant.BOOK_ID, mPayId);
						util.go2OrderDetail(PayActivity.this, bundle);
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

	protected void initRecharge(String AlipayOrderId) {
		// 拼车币
		Util.printLog("支付金额：" + mUserAlipayMoneyNum);
		final int money = (int) mUserAlipayMoneyNum;

		util.go2RechargeOrPay(PayActivity.this, String.valueOf((float) money / 100), AlipayOrderId,
				PayActivity.this.getString(R.string.pay_body),
				PayActivity.this.getString(R.string.pay_order), mHandler);

		// util.initRecharge(this, money, type, AlipayOrderId, mHandler);

		/*
		 * util.getOperateOrderNum(PayActivity.this, String.valueOf(money),
		 * type, mPayId, new OnHttpRequestDataCallback() {
		 * 
		 * @Override public void onSuccess(String result) { // TODO
		 * Auto-generated method stub Util.printLog("payactivity 支付初始化:" +
		 * result); JsonResult<String> mJsonResult =
		 * Util.getInstance().getObjFromJsonResult( result, new
		 * TypeToken<JsonResult<String>>() { }); if (mJsonResult != null &&
		 * mJsonResult.isSuccess()) { util.go2RechargeOrPay(PayActivity.this,
		 * String.valueOf(mThisTimeCost), mJsonResult.getData(),
		 * getString(R.string.pay_body), getString(R.string.pay_order),
		 * mHandler);
		 * 
		 * } else { Util.showToast(PayActivity.this, getString(R.string.init_f)
		 * + ":" + mJsonResult.getErrorMsg()); } } });
		 */

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 1) {

			}
		}
		/** 使用SSO授权必须添加如下代码 */
	}

	@Override
	public void onClick(View v) {
		if (v == mPayButton) {
			if (!util.isNetworkAvailable(PayActivity.this)) {
				Util.showToast(PayActivity.this, getString(R.string.network_unavaiable));
				return;
			}
			if (isBuyInsurance) {
				if (TextUtils.isEmpty(mInsuranceInfoName.getText().toString())) {
					Util.showToast(PayActivity.this,
							getString(R.string.pay_tip_input_personal_name));
					return;
				} else if (TextUtils.isEmpty(mInsuranceInfoID.getText().toString())) {
					Util.showToast(PayActivity.this, getString(R.string.pay_tip_input_personal_id));
					return;
				}
			}
			if (TextUtils.isEmpty(mPayPassword.getText().toString())) {
				Util.showToast(PayActivity.this, getString(R.string.pay_tip_input_psw));
				return;
			}
			String title = "";
			isUserAliPay = (mMyBalance + mUsePinCheBiNum) - mTotalCostsNumRMB >= 0 ? false : true;
			if (isUserAliPay) {
				mUserAlipayMoneyNum = mTotalCostsNumRMB - mUsePinCheBiNum - mMyBalance
						- mInsuranceCostsRMB;
				title = getString(R.string.pay_use_alipay_pay_yse_no)
						+ String.format("%.2f",
								(double) ((mUserAlipayMoneyNum) / Constant.EXCHANGERATE)) + "元";

			} else {// 使用拼车币和余额支付
				title = getString(R.string.this_time_use) + mUsePinCheBiNum
						+ getString(R.string.pinche_bi) + "和" + (double) (mRealCostsRMB)
						/ Constant.EXCHANGERATE + "元余额";
			}
			showPayDialog(title);
		} else if (v == mBack) {
			finish();
		} else if (v == mIsInputInsuranceInfo) {
			mIsInputInsuranceInfo.toggle();
			mInputInsuranceInfoLayout
					.setVisibility(mIsInputInsuranceInfo.isChecked() ? View.VISIBLE : View.GONE);

			setCosts();
			isBuyInsurance = mIsInputInsuranceInfo.isChecked();
		} else if (v == mIsUsePincheCoin) {
			mIsUsePincheCoin.toggle();
			mInputPincheCoinLayout.setVisibility(mIsUsePincheCoin.isChecked() ? View.VISIBLE
					: View.GONE);
			IsUsePinCheCoin(mIsUsePincheCoin.isChecked());
			setCosts();
		}
	}

	private void showSettingPswDialog(String title) {
		// TODO Auto-generated method stub
		util.showDialog(PayActivity.this, title, getString(R.string.pay_setting_psw_dialog_ok),
				getString(R.string.pay_setting_psw_dialog_no), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							startActivityForResult(new Intent(PayActivity.this,
									MyPayPswSettingActivity.class), 1);
						}
					}
				});

	}

	// private void setUsePinchebiPay(boolean isUserPinchebi) {
	// // mPihCheBiButton.setSelected(isUserPinchebi);
	// // mAlipayButton.setSelected(!isUserPinchebi);
	// isUserAliPay = !isUserPinchebi;
	// if (isUserPinchebi) {
	// // 如果拼车币足够
	// if ((mPinCheBiNum / 10) >= mThisTimeCostRMB)
	// mPayPassword.setVisibility(View.VISIBLE);
	// mPayButton.setText(getString(R.string.pay_with_pcb));
	// } else {
	// if (mPayButton.getVisibility() == View.VISIBLE)
	// mPayPassword.setVisibility(View.GONE);
	// mPayButton.setText(getString(R.string.pay_with_alipay));
	//
	// }
	// }
}
