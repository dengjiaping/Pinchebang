package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Book;
import com.lepin.entity.Car;
import com.lepin.entity.CarType;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Pinche;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.UMSharingMyOrder;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.CircleImageView;
import com.lepin.widget.PcbConfirmDialog;
import com.lepin.widget.PcbConfirmDialog.OnOkOrCancelClickListener;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 我的订单详情展示,当前版本之后确认订单操作（无取消订单操作）
 * 
 * 
 */
@Contextview(R.layout.my_order_details)
public class MyOrderDetailActivity extends BaseActivity implements OnClickListener {
	// title
	@ViewInject(id = R.id.common_title_title)
	private TextView tvTitle;// 标题

	@ViewInject(id = R.id.common_title_back)
	private ImageView btnBack;// 返回

	@ViewInject(id = R.id.common_title_operater)
	private TextView btnShare;// 分享

	// 头像
	@ViewInject(id = R.id.my_order_details_publisher_img)
	private CircleImageView mPhotoView;

	// ｖ图标
	@ViewInject(id = R.id.my_order_details_info_verified)
	private ImageView mVImageView;
	// 称呼
	@ViewInject(id = R.id.my_order_details_publisher_name)
	private TextView mNameText;

	// 联系电话
	@ViewInject(id = R.id.my_order_details_info_phone_btn)
	private Button mPhoneButton;// 打电话按钮

	// 车辆信息

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

	private String book_id;// 预约id

	@ViewInject(id = R.id.my_order_cancel_and_ok_layout)
	private LinearLayout mCancelAndOkLayout;

	/**
	 * 订单取消后 或者 司机端操作 取消layout
	 */
	@ViewInject(id = R.id.my_order_cancel_layout)
	private LinearLayout mCancelLayout;

	// 三个按钮
	/**
	 * 司机的取消按钮 （new） 乘客或司机的 订单完成 展示按钮 （complete）
	 */
	@ViewInject(id = R.id.btn_driver_cancle)
	private Button mCancleDriverBtn;// 司机取消按钮

	// 两个按钮的取消按钮 新建订单的取消按钮 （乘客）
	@ViewInject(id = R.id.my_order_btn_cancel)
	private Button mTwoCancelBtn;

	// 新建订单的立即支付按钮 （乘客）
	/**
	 * 立即支付 （operateType = 6） 或 确认上车（operateType = 1） 按钮
	 */
	@ViewInject(id = R.id.my_order_btn_pay)
	private Button mTwoPayOrInBtn;// 两个按钮的立即支付按钮

	@ViewInject(id = R.id.my_order_detail_calendar_layout)
	private View mCalendarLayout;
	// 操作类型
	/**
	 * 确认上车
	 */
	private static final int COMFIRM_IN = 1;// 确认上车
	/**
	 * 线下支付
	 */
	private static final int COMPLETE_PAY_CASH_BOOK = 2;// 线下支付
	/**
	 * 订单加载
	 */
	private static final int GET_ORDER_MSG = 4;// 加载订单
	/**
	 * 司机取消订单操作
	 */
	private static final int DRIVER_CANCLE_ORDER = 7;// 司机取消订单操作
	/**
	 * 乘客取消操作
	 */
	private static final int CANCEL_ORDER = 5;// 乘客取消订单操作
	/**
	 * 乘客立即支付
	 */
	private static final int PAY = 6; // 乘客立即支付操作
	private int operateType; // 操作类型

	// private int state;// 状态
	private String mBookState;// 订单状态
	private Book book;// 预约对象
	private Pinche mPinche;// 拼车对象
	private User mUser;

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
	 * true 车主 false 乘客
	 */
	private boolean isDriverOfMe;// 我在这个订单中的身份
	/**
	 * true 自己发的 false 别人发的
	 */
	// private boolean isPublisher;//
	// private boolean isPassgerCanPay = false;// 乘客是否可以付款
	private boolean isCancelOrder = false;// 是否取消了
	private boolean isShowCalendar = false;
	private Util util = Util.getInstance();
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
			RequestType.SOCIAL);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		setTitleText();
		initView();
	}

	private void setTitleText() {
		mStartDateTitleText.setText(getString(R.string.pick_details_start_date));
		mBackDateTitleText.setText(getString(R.string.pick_details_back_date));
		mCostTitleText.setText(getString(R.string.pick_details_cost_note));
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		final Bundle bundle = getIntent().getExtras();
		this.book_id = bundle.getString(Constant.BOOK_ID);
		this.mStartLat = bundle.getInt(Constant.START_LAT);
		this.mStartLon = bundle.getInt(Constant.START_LON);
		this.mEndLat = bundle.getInt(Constant.END_LAT);
		this.mEndLon = bundle.getInt(Constant.END_LON);
		isShowCalendar = bundle.getBoolean(Constant.SHOW_CALENDAR);
		Util.printLog("bookId:" + book_id);
		this.tvTitle.setText(getResources().getString(R.string.order_details_title));// 初始化title
		this.btnBack.setOnClickListener(this);
		this.btnShare.setOnClickListener(this);
		this.mPhoneButton.setOnClickListener(this);
		this.mCancleDriverBtn.setOnClickListener(this);
		mTwoCancelBtn.setOnClickListener(this);
		mTwoPayOrInBtn.setOnClickListener(this);
		mapImageView.setOnClickListener(this);
		mPhotoView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (this.btnBack == view) {// 返回
			finish();
		} else if (this.mCancleDriverBtn == view) {// 司机取消按钮
			operateType = DRIVER_CANCLE_ORDER;
			doBtnPress();
		} else if (this.mPhoneButton == view) {
			contactEachOther();
		} else if (view == this.mTwoPayOrInBtn) { // 支付
			if (mBookState.equals(Book.STATE_NEW)) {
				operateType = PAY;
			} else if (mBookState.equals(Book.STATE_PAYMENT)) {
				operateType = COMFIRM_IN;
			}
			doBtnPress();
		} else if (view == this.mTwoCancelBtn) {
			operateType = CANCEL_ORDER;
			isCancelOrder = isCanCancleOrder(mBookState.equals(Book.STATE_PAYMENT)); // 状态要是乘客付款
																						// 离发车前两小时外才能取消订单
			doBtnPress();
		} else if (view == this.btnShare) {
			util.share(MyOrderDetailActivity.this, book_id, mController,
					UMSharingMyOrder.SHARE_TYPE_ORDER, Constant.URL_SHARE_ORDER,
					getString(R.string.share_order_content), getString(R.string.share_order_title));
		} else if (view == mapImageView) {
			Util.getInstance().showStartAndEndOnMap(this, mPinche);
		} else if (view == mPhotoView) {// 头像
			User user = null;
			String role = "";
			if (book.getDriver() != null) {
				user = book.getDriver();
				role = Constant.DRIVER;
			} else if (book.getPassenger() != null) {
				user = book.getPassenger();
				role = Constant.PASSENGER;
			}

			if (user == null) return;

			Bundle dataBundle = new Bundle();
			dataBundle.putInt("userId", user.getUserId());
			dataBundle.putString("role", role);
			util.go2ActivityWithBundle(MyOrderDetailActivity.this, PersonalInfoTrailActivity.class,
					dataBundle);
		}
	}

	private void contactEachOther() {
		String number = "";
		if (book.getPassenger() != null) number = book.getPassenger().getTel();
		if (book.getDriver() != null) number = book.getDriver().getTel();
		Util.getInstance().call(this, number);
	}

	protected void doBtnPress() {
		if (util.isNetworkAvailable(this)) {// 判断网络是否连接
			showDialog();
		} else {
			Util.showToast(MyOrderDetailActivity.this, getString(R.string.network_unavaiable));
		}
	}

	protected void showDialog() {

		String title = "";
		String ok = "";
		String cancel = "";
		if ((isCancelOrder && operateType == CANCEL_ORDER) || (operateType == DRIVER_CANCLE_ORDER)) {// 乘客自己取消订单或者司机取消订单
			title = getString(R.string.is_cancel_order);
			ok = getString(R.string.dialog_confirm);
			cancel = getString(R.string.my_info_btn_cancel);
		} else if (mBookState.equals(Book.STATE_NEW) && operateType == PAY) { // 乘客支付订单
			title = getString(R.string.complete_carpool_dialog_title);
			ok = getString(R.string.complete_carpool_online);
			cancel = getString(R.string.complete_carpool_cash);
		} else if (mBookState.equals(Book.STATE_PAYMENT) && operateType == COMFIRM_IN) { // 乘客确认上车
			title = getString(R.string.order_dialog_comfirm_in_title);
			ok = getString(R.string.dialog_confirm);
			cancel = getString(R.string.my_info_btn_cancel);
		} else if (mBookState.equals(Book.STATE_CONFIRM)) {
			title = getString(R.string.complete_carpool_dialog_title);
			ok = getString(R.string.complete_carpool_online);
			cancel = getString(R.string.complete_carpool_cash);
		}

		Util.getInstance().showDialog(MyOrderDetailActivity.this, title, ok, cancel,
				new OnOkOrCancelClickListener() {
					@Override
					public void onOkClick(int type) {
						if (type == PcbConfirmDialog.OK) {
							if ((isCancelOrder && operateType == CANCEL_ORDER)
									|| (operateType == DRIVER_CANCLE_ORDER)) {
								operateBook(CANCEL_ORDER, Constant.URL_CANCLEBOOK_NEW);
							} else if (operateType == PAY) {// 乘客立即支付
								go2OnLinePay();
								// 跳到支付頁面
							} else if (mBookState.equals(Book.STATE_PAYMENT)) {// 乘客确认上车
								operateBook(COMFIRM_IN, Constant.URL_ORDER_IN);
							} else if (mBookState.equals(Book.STATE_CONFIRM)) {// 乘客支付
								if (!isDriverOfMe) {
									go2OnLinePay();
								}
							}
						} else {
							if (mBookState.equals(Book.STATE_NEW) && !isDriverOfMe
									&& operateType == PAY) {// 乘客支付，现金
								operateType = COMPLETE_PAY_CASH_BOOK;
								operateBook(COMPLETE_PAY_CASH_BOOK, Constant.URL_PAY_NEW);
							}
						}
					}
				});
	}

	/**
	 * 加载我的预约详情
	 */
	private void loadSingleOrder() {
		operateBook(GET_ORDER_MSG, Constant.URL_GET_ORDER_BY_ID);
	}

	/**
	 * 设置界面view的值
	 */
	private void setBookData2View() {
		if (book != null) {
			this.mStarText.setText(this.mPinche.getStart_name(this));
			this.mEndText.setText(this.mPinche.getEnd_name(this));
			showDriverOrPassengerInfo();
			showOrderInfo();
			if (isShowCalendar) mCalendarLayout.setVisibility(View.VISIBLE);
		}

	}

	private void showOrderInfo() {
		if (mPinche.getCarpoolType().equals(Pinche.CARPOOLTYPE_LONG_TRIP)) {// 长途
			this.mStartDateText.setText(mPinche.getDepartureTime());// 出发时间（长途）
			((View) findViewById(R.id.my_order_details_back_time_layout)).setVisibility(View.GONE);// 没有返程信息，隐藏
		} else {// 上下班
			if ("".equals(mPinche.getBackTime())) {
				this.mStartDateText.setText(mPinche.getCycle().getTxt() + "  "
						+ getString(R.string.moring) + ":" + mPinche.getDepartureTime());// 出发时间单程（上下班）
			} else {
				this.mStartDateText.setText(mPinche.getCycle().getTxt() + "  "
						+ mPinche.getDepartureTime());// 出发时间（上下班）
				if (mPinche.getBackTime() == null || mPinche.getBackTime().equals("")) {
					((LinearLayout) findViewById(R.id.my_order_details_back_time_layout))
							.setVisibility(View.GONE);// 没有返程信息，隐藏
				} else {
					this.mBackDateText.setText(mPinche.getCycle().getTxt() + "  "
							+ mPinche.getBackTime());// 返回时间（上下班）
				}
			}
		}
		mCostText.setText(String.valueOf(mPinche.getCharge()));// 费用
		if (book.getBookType().equals(Pinche.DRIVER)) {// 司机发的信息 车找人
			this.mPeopleNumTitle.setText(getString(R.string.avaiable_passenger_num));
		} else {// 乘客发的 人找车 显示 乘客人数
			this.mPeopleNumTitle.setText(getString(R.string.passenger_number));
		}
		if (!TextUtils.isEmpty(String.valueOf(mPinche.getNum()))) {
			this.mPeopleNum.setText(String.valueOf(mPinche.getNum()));
		} else {
			this.mPeopleNum.setText("0" + getString(R.string.human));
		}
		if (!TextUtils.isEmpty(mPinche.getNote())) {
			this.mNoteText.setText(mPinche.getNote());
		}
	}

	private void showDriverOrPassengerInfo() {
		if (!isDriverOfMe) {// 头像显示司机信息
			final User driver = book.getDriver();
			if (driver != null) {
				this.mNameText.setText(driver.getUsername(this));
				mPhotoView.displayWithUrl(Util.getInstance().getPhotoURL(driver.getUserId()),
						false, false);
			}
			final Car car = driver.getCar();
			if (car != null) {
				mCarLicenseText.setText(car.getLicence(this));// 车牌
				mCarLicenseText.setVisibility(View.VISIBLE);
				CarType carType = driver.getCar().getCarType();
				if (carType != null) {
					mCarBrandText.setText(carType.getCarTypeName());// 车型
				} else {
					mCarBrandText.setText(getString(R.string.unknow));
				}
				mCarBrandText.setVisibility(View.VISIBLE);
				// 是否验证
				if (driver.isUserStateVerify()) {
					mVImageView.setVisibility(View.VISIBLE);
				}
				// 驾龄
				mDrivingYears.setText(getString(R.string.driving_years, TextUtils.isEmpty(driver
						.getDriveAge()) ? getString(R.string.unknow) : driver.getDriveAge()));
				mDrivingYears.setVisibility(View.VISIBLE);
			} else {
				mCarLicenseText.setText(getString(R.string.unknow));
				mCarLicenseText.setVisibility(View.VISIBLE);
				mCarBrandText.setText(getString(R.string.unknow));
				mCarBrandText.setVisibility(View.VISIBLE);
			}

			// 显示车辆品牌，车牌号
		} else {// 头像显示乘客信息
			final User passager = book.getPassenger();
			if (passager != null) {
				mPhotoView.displayWithUrl(Util.getInstance().getPhotoURL(passager.getUserId()),
						false, false);
				this.mNameText.setText(passager.getUsername(this));
			}
			if (passager.isUserStateVerify()) {
				mVImageView.setVisibility(View.VISIBLE);
			}
		}
	}

	protected void operateBook(final int type, String url) {
		// 1 代表确认订单 2 代表线下支付完成 4加載订单信息
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("infoOrderId", this.book_id));
		if (type == COMPLETE_PAY_CASH_BOOK) {
			params.add(new BasicNameValuePair("payType", Constant.PAY_OFFLINE));// OFFLINE线下支付
			// ONLINE线上支付
		}
		String loadingMsg = "";
		if (type == COMFIRM_IN) {
			loadingMsg = getString(R.string.comfirm_order_in);
		} else if (type == COMPLETE_PAY_CASH_BOOK) {
			loadingMsg = getString(R.string.complete_order_ing);
		} else if (type == GET_ORDER_MSG) {
			loadingMsg = getString(R.string.order_detail_loading);
		} else if (type == CANCEL_ORDER) {
			loadingMsg = getString(R.string.order_cancel_ing);
		}

		util.doPostRequest(MyOrderDetailActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				if (type == GET_ORDER_MSG) {// 获取订单信息
					setViewValueAndBtnState(result);
				} else {
					JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
							new TypeToken<JsonResult<String>>() {
							});
					final boolean isSuccess = jsonResult.isSuccess();
					if (type == COMFIRM_IN) {
						if (isSuccess) {// 乘客确定上车后 乘客端 司机端展示
							Util.showLongToast(MyOrderDetailActivity.this, jsonResult.getData());
							mCancelAndOkLayout.setVisibility(View.GONE);
							mCancelLayout.setVisibility(View.VISIBLE);
							setBtnTextAndColor(R.string.order_state_complete, Color.GRAY, false,
									View.VISIBLE);
						} else {
							mCancelLayout.setVisibility(View.VISIBLE);
							mCancleDriverBtn.setVisibility(View.VISIBLE);
							Util.showLongToast(MyOrderDetailActivity.this, jsonResult.getData());
						}
					} else if (type == COMPLETE_PAY_CASH_BOOK) {
						if (isSuccess) {
							mCancelAndOkLayout.setVisibility(View.GONE);
							mCancleDriverBtn.setVisibility(View.VISIBLE);
							mCancelLayout.setVisibility(View.VISIBLE);
							setBtnTextAndColor(R.string.order_state_complete, Color.WHITE, false,
									View.VISIBLE);
						}
						Util.showLongToast(MyOrderDetailActivity.this, jsonResult.getData());
					} else if (type == CANCEL_ORDER) {
						if (isSuccess) {// 订单取消成功
							Util.showLongToast(MyOrderDetailActivity.this, jsonResult.getData());
							mCancelAndOkLayout.setVisibility(View.GONE);
							mCancelLayout.setVisibility(View.VISIBLE);
							setBtnTextAndColor(R.string.has_been_cancel, Color.WHITE, false,
									View.VISIBLE);
						} else {// 订单取消失败
							Util.showLongToast(MyOrderDetailActivity.this, jsonResult.getData());
						}
					}
				}
			}

			@Override
			public void onFail(String errorType, String errorMsg) {
				// TODO Auto-generated method stub
				super.onFail(errorType, errorMsg);
				Util.showToast(MyOrderDetailActivity.this, errorMsg);
			}
		}, params, url, loadingMsg, true);
	}

	protected void setBtnTextAndColor(int textId, int colorId, boolean isCanPressed, int visibility) {
		mCancleDriverBtn.setText(getString(textId));
		mCancleDriverBtn.setTextColor(colorId);
		mCancleDriverBtn.setClickable(isCanPressed);
		mCancleDriverBtn.setVisibility(visibility);
		mCancelLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mUser = util.getLoginUser(MyOrderDetailActivity.this);
		loadSingleOrder();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void go2OnLinePay() {
		Util.getInstance().go2OnLinePay(this, book.getPrice(), this.book_id,
				mPinche.getStart_name(this), mPinche.getEnd_name(), mStartLat, mStartLon, mEndLat,
				mEndLon, mPinche.getCarpoolType());
	}

	/**
	 * @param result
	 */
	private void setViewValueAndBtnState(String result) {
		JsonResult<Book> jsonResult = util.getObjFromJsonResult(result,
				new TypeToken<JsonResult<Book>>() {
				});
		if (jsonResult != null && jsonResult.isSuccess()) {
			if (!util.isDataExist(jsonResult)) {
				Util.showToast(MyOrderDetailActivity.this, getString(R.string.common_no_data));
				return;
			}
			book = jsonResult.getData();// 获取拼车信息对象
			if (book == null) {
				Util.showToast(MyOrderDetailActivity.this, getString(R.string.common_no_data));
				return;
			}

			mBookState = book.getState();// 订单状态
			this.mPinche = util.string2Bean(this.book.getSnapshot(), Pinche.class);
			isDriverOfMe = mUser.getUserId() == book.getDriverId();

			setBookData2View();// 初始化界面预约详情
			showBtnState();
		} else {
			Util.showToast(MyOrderDetailActivity.this, jsonResult.getErrorMsg());
		}
	}

	private void showBtnState() {
		/* 展示订单操作按钮 */
		if (mBookState.equals(Book.STATE_CANCEL)) {
			setBtnTextAndColor(R.string.has_been_cancel, Color.WHITE, false, View.VISIBLE);
		} else if (mBookState.equals(Book.STATE_NEW)) {// 新订单，可以由发布信息者操作（确认订单）
			if (!isDriverOfMe) {// 乘客 可以取消，支付
				mCancelAndOkLayout.setVisibility(View.VISIBLE);
				operateType = CANCEL_ORDER;
			} else { // 司机取消
				setBtnTextAndColor(R.string.order_operate_cancle, Color.WHITE, true, View.VISIBLE);
			}
		} else if (mBookState.equals(Book.STATE_PAYMENT)) {// 订单状态时已支付
			if (!isDriverOfMe) { // 乘客确认上车 或者 取消
				mCancelAndOkLayout.setVisibility(View.VISIBLE);
				mTwoPayOrInBtn.setText(getResources().getString(R.string.order_operate_in));
				isCanCancleOrder(true);
				operateType = COMFIRM_IN;
			} else {
				// 司机取消
				setBtnTextAndColor(R.string.order_operate_driver_cancle, Color.RED, true,
						View.VISIBLE);
			}
		} else if (mBookState.equals(Book.STATE_COMPLETE)) {// 已完成订单
			setBtnTextAndColor(R.string.order_state_complete, Color.WHITE, false, View.VISIBLE);
		}
	}

	/**
	 * @return 检查出发前两小时是否可以取消订单
	 */
	private Boolean isCanCancleOrder(boolean isPaymentState) {
		// TODO Auto-generated method stub
		long currentTime = System.currentTimeMillis();
		long bookRideTime = book.getRideTime() * 1000L;
		Boolean isCanCancleOrder = (bookRideTime * 1L - currentTime * 1L) >= 2 * 60 * 60 * 1000L ? true
				: false;
		if (!isCanCancleOrder && isPaymentState) {
			mTwoCancelBtn.setClickable(false);
			mTwoCancelBtn.setText("不能取消订单");
		}
		return isCanCancleOrder;
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
	public void onBackPressed() {
		finish();
	}

}
