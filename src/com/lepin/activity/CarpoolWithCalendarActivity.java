package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Car;
import com.lepin.entity.CarpoolProgramPassenger;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Pinche;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.CircleImageView;
import com.lepin.widget.PcbCalender;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;

@Contextview(R.layout.my_order_details)
public class CarpoolWithCalendarActivity extends BaseActivity implements OnClickListener {

	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题

	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回

	// 头像
	@ViewInject(id = R.id.my_order_details_publisher_img)
	private CircleImageView mPhotoView;
	// 称呼
	@ViewInject(id = R.id.my_order_details_publisher_name)
	private TextView mNameText;

	// 联系电话
	@ViewInject(id = R.id.my_order_details_info_phone_btn)
	private Button mPhoneButton;// 打电话按钮

	// 车辆型号
	@ViewInject(id = R.id.my_order_details_publisher_car_brand)
	private TextView mCarBrandText;

	// 地图
	@ViewInject(id = R.id.my_order_details_map)
	private ImageView mapImageView;
	// 车牌

	@ViewInject(id = R.id.my_order_details_publisher_car_license)
	private TextView mCarLicenseText;

	// 起点
	@ViewInject(id = R.id.my_order_details_start)
	private TextView mStarText;

	// 终点
	@ViewInject(id = R.id.my_order_details_end)
	private TextView mEndText;
	// 驾龄
	@ViewInject(id = R.id.my_order_details_publisher_driving_years)
	private TextView mDrivingYears;
	// 出发
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.my_order_details_start_time_layout)
	private TextView mStartDateTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.my_order_details_start_time_layout)
	private TextView mStartDateText;

	// 返程
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.my_order_details_back_time_layout)
	private TextView mBackDateTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.my_order_details_back_time_layout)
	private TextView mBackDateText;

	// 费用

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.my_order_details_cost_layout)
	private TextView mCostTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.my_order_details_cost_layout)
	private TextView mCostText;

	// 可载人数
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.my_order_details_people_num_layout)
	private TextView mPeopleNumTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.my_order_details_people_num_layout)
	private TextView mPeopleNum;

	// 备注
	@ViewInject(id = R.id.my_order_details_note)
	private TextView mNoteText;
	// 日历布局
	@ViewInject(id = R.id.my_order_detail_calendar_layout)
	private View mCalendarLayout;
	// 日历
	@ViewInject(id = R.id.my_order_detail_calendar)
	private PcbCalender mCalendarView;

	@ViewInject(id = R.id.calendar_pay_layout)
	private LinearLayout mPayLayout;

	@ViewInject(id = R.id.calendar_pay_btn)
	private Button mPayButton;

	@ViewInject(id = R.id.calendar_pay_btn_money)
	private TextView mPayMoneyTextView;

	@ViewInject(id = R.id.calendar_pay_money)
	private TextView mShowMoneyTextView;

	// ｖ图标
	@ViewInject(id = R.id.my_order_details_info_verified)
	private ImageView mVImageView;
	private String carpoolProgramPassengerId;

	private Pinche mPincheInfo;
	private CarpoolProgramPassenger carpoolProgramPassenger;
	private boolean isDriver = false;// 我是不是司机

	private String telString;
	private int[] carpoolCalendar;
	private int money;

	private User user;

	private Util util = Util.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(CarpoolWithCalendarActivity.this);
		Bundle bundle = getIntent().getExtras();
		carpoolProgramPassengerId = bundle.getString(Constant.CARPOOLPROGRAMPASSENGERID);
		isDriver = bundle.getBoolean(Constant.IS_DRIVER);
		setTitleText();
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		getPlanInfo();
	}

	private void getPlanInfo() {
		String url = Constant.URL_GET_ORDER_CALENDAR + "?carpoolProgramPassengerId="
				+ carpoolProgramPassengerId;
		Util.getInstance().doGetRequest(CarpoolWithCalendarActivity.this,
				new OnHttpRequestDataCallback() {

					@Override
					public void onSuccess(String result) {
						JsonResult<CarpoolProgramPassenger> carpooResult = Util.getInstance()
								.getObjFromJsonResult(result,
										new TypeToken<JsonResult<CarpoolProgramPassenger>>() {
										});
						if (carpooResult != null && carpooResult.isSuccess()) {
							carpoolProgramPassenger = carpooResult.getData();
							mPincheInfo = carpoolProgramPassenger.getCarpoolProgram().getInfo();
							setData();
						} else {
							Util.showToast(CarpoolWithCalendarActivity.this,
									getString(R.string.request_error));
						}
					}
				}, url, "获取拼车详情...", false);
	}

	protected void setData() {
		carpoolCalendar = carpoolProgramPassenger.getCarpoolCalendar();
		money = carpoolCalendar.length * carpoolProgramPassenger.getCarpoolProgram().getCharge();
		if (isDriver) {
			user = carpoolProgramPassenger.getPassenger();
		} else {
			user = mPincheInfo.getUser();
		}
		mNameText.setText(user.getUsername(this));
		telString = user.getTel();
		// 显示用户是否已经验证
		if (user != null && user.isUserStateVerify()) {
			mVImageView.setVisibility(View.VISIBLE);
		}
		if (isDriver) {
			if (carpoolCalendar.length > 0) {

				mShowMoneyTextView.setText(Html.fromHtml("对方还有" + "<font color=\"#ff00ff\">"
						+ carpoolCalendar.length + "</font>" + "次,共" + "<font color=\"#ff00ff\">"
						+ money + "</font>" + "元未付款"));
			} else {
				mShowMoneyTextView.setText(getString(R.string.passenger_pay_all));
			}

			mShowMoneyTextView.setVisibility(View.VISIBLE);
		} else {
			if (carpoolCalendar.length > 0) {
				mPayMoneyTextView.setText(Html.fromHtml(getString(R.string.money_num)
						+ "<font color=\"#ff00ff\">" + money + "</font>"
						+ getString(R.string.unit_yuan)));
				mPayButton.setOnClickListener(this);
			} else {
				mPayMoneyTextView.setText(getString(R.string.no_need_to_pay));
				mPayButton.setVisibility(View.GONE);
			}
			mDrivingYears.setText(getString(R.string.driving_years, user.getDriveAge()));
			mDrivingYears.setVisibility(View.VISIBLE);
			if (mPincheInfo.getCar() != null) {// 车牌
				Car car = mPincheInfo.getCar();
				mCarLicenseText
						.setText((car.getLicence() == null || car.getLicence().equals("")) ? getString(R.string.unknow)
								: car.getLicence());
				mCarBrandText.setText(car.getCarType().getCarTypeName());
			}
			mPayLayout.setVisibility(View.VISIBLE);
		}

		mStarText.setText(mPincheInfo.getStart_name());
		mEndText.setText(mPincheInfo.getEnd_name());
		mPeopleNum.setText(String.valueOf(mPincheInfo.getNum()));
		mCostText.setText(String.valueOf(mPincheInfo.getCharge()));
		this.mStartDateText.setText(mPincheInfo.getCycle().getTxt() + "  "
				+ getString(R.string.moring) + ":" + mPincheInfo.getDepartureTime());// 出发时间单程（上下班）
		if (!TextUtils.isEmpty(mPincheInfo.getBackTime())) {
			this.mBackDateText.setText(mPincheInfo.getCycle().getTxt() + "  "
					+ getString(R.string.night) + ":" + mPincheInfo.getBackTime());// 返回时间（上下班）
		}
		if (!TextUtils.isEmpty(mPincheInfo.getNote())) {
			this.mNoteText.setText(mPincheInfo.getNote());
		}

		// 显示日历
		mCalendarView.setCalendarDate(carpoolCalendar);
		if (mCalendarLayout.getVisibility() != View.VISIBLE) {
			mCalendarLayout.setVisibility(View.VISIBLE);
			((View) findViewById(R.id.my_order_detail_calendar_divider)).setVisibility(View.GONE);
		}
		((View) findViewById(R.id.my_order_root_view)).setVisibility(View.VISIBLE);

	}

	private void setTitleText() {
		mStartDateTitleText.setText(getString(R.string.pick_details_start_date));
		mBackDateTitleText.setText(getString(R.string.pick_details_back_date));
		mCostTitleText.setText(getString(R.string.pick_details_cost_note));
		tvTitle.setText(getResources().getString(R.string.pin_che_plan_detail));// 初始化title
		mPeopleNumTitle.setText(getString(R.string.my_car_total_people));
	}

	private void initView() {
		btnBack.setOnClickListener(this);
		mapImageView.setOnClickListener(this);
		mPhoneButton.setOnClickListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		// String url = Constant.URL_RESOURCE + "/userImg/" +
		// mPincheInfo.getUser().getUserId()
		// + ".jpg";
		// Util.printLog("拼车详情用户头像:" + url);
		// mPhotoView.displayWithUrl(url);
	}

	protected void showPayDialog() {
		Util.getInstance().showDialog(this, getString(R.string.complete_carpool_dialog_title),
				getString(R.string.complete_carpool_online),
				getString(R.string.complete_carpool_cash), new OnOkOrCancelClickListener() {

					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							// Util.getInstance().go2OnLinePay(CarpoolWithCalendarActivity.this,
							// money, "carpoolProgramPassengerId",
							// carpoolProgramPassengerId,
							// mPincheInfo.getStart_name(CarpoolWithCalendarActivity.this),
							// mPincheInfo.getEnd_name(CarpoolWithCalendarActivity.this),
							// Constant.URL_CARPOOL_PAY,
							// SERVICE_TYPE.CARPOOL_PROGRAM);
						} else {
							pay();
						}
					}
				});
	}

	// 线下支付
	protected void pay() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("carpoolProgramPassengerId", carpoolProgramPassengerId));
		params.add(new BasicNameValuePair("payType", Constant.PAY_OFFLINE));
		Util.printLog("计划线下支付参数:" + params.toString());
		util.doPostRequest(CarpoolWithCalendarActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				Util.printLog("计划线下支付:" + result);
				JsonResult<String> jsonResult = Util.getInstance().getObjFromJsonResult(result,
						new TypeToken<JsonResult<String>>() {
						});
				if (jsonResult.isSuccess()) {
					Util.showToast(CarpoolWithCalendarActivity.this, jsonResult.getData());
					getPlanInfo();
				} else {
					Util.showToast(CarpoolWithCalendarActivity.this, getString(R.string.pay_error));
				}
			}

		}, params, Constant.URL_CARPOOL_PAY, getString(R.string.complete_order_ing), false);

		// Util.getInstance().doPostRequest(this, new OnDataLoadingCallBack() {
		//
		// @Override
		// public void onLoadingBack(String result) {
		// Util.printLog("计划线下支付:" + result);
		// if (!TextUtils.isEmpty(result)) {
		// JsonResult<String> jsonResult =
		// Util.getInstance().getObjFromJsonResult(result,
		// new TypeToken<JsonResult<String>>() {
		// });
		// if (jsonResult.isSuccess()) {
		// Util.showToast(CarpoolWithCalendarActivity.this,
		// jsonResult.getData());
		// getPlanInfo();
		// } else {
		// Util.showToast(CarpoolWithCalendarActivity.this,
		// getString(R.string.pay_error));
		// }
		// }
		// }
		// }, params, Constant.URL_CARPOOL_PAY,
		// getString(R.string.complete_order_ing));
	}

	@Override
	public void onClick(View v) {
		if (v == btnBack) {
			finish();
		} else if (v == mapImageView) {// 地图
			Util.getInstance().showStartAndEndOnMap(this, mPincheInfo);
		} else if (v == mPhoneButton) {// 电话
			if (!TextUtils.isEmpty(telString)) {
				Util.getInstance().call(this, telString);
			}
		} else if (v == mPayButton) {// 付款
			showPayDialog();
		}

	}
}
