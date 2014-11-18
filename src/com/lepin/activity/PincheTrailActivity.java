package com.lepin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lepin.entity.Car;
import com.lepin.entity.JsonResult;
import com.lepin.entity.Pinche;
import com.lepin.entity.Point;
import com.lepin.entity.User;
import com.lepin.inject.Contextview;
import com.lepin.inject.ViewInject;
import com.lepin.inject.ViewInjectUtil;
import com.lepin.util.Constant;
import com.lepin.util.Util;
import com.lepin.util.Util.OnHttpRequestDataCallback;
import com.lepin.widget.CircleImageView;

/**
 * 查看拼车信息详情，并可以预约拼车，如果已经预约了当前信息，就不能在预约，预约按钮隐藏
 * 
 */
@Contextview(R.layout.pinche_trail_details)
public class PincheTrailActivity extends BaseActivity implements OnClickListener {
	@ViewInject(id = R.id.common_title_title, parentId = R.id.pinche_details_title)
	private TextView mTitle;// 标题

	@ViewInject(id = R.id.common_title_back, parentId = R.id.pinche_details_title)
	private ImageView mTitleBack;// 返回

	// 头像
	@ViewInject(id = R.id.pinche_trail_details_publisher_img)
	private CircleImageView mPhotoView;
	// 称呼
	@ViewInject(id = R.id.pinche_trail_details_publisher_name)
	private TextView mNameText;

	// 立即预约
	@ViewInject(id = R.id.pinche_trail_details_info_appointment)
	private Button mappointment;//

	// 车辆信息

	// 车辆型号// 性别
	@ViewInject(id = R.id.pinche_trail_details_publisher_car_brand)
	private TextView mCarBrandText;

	// 地图
	@ViewInject(id = R.id.pinche_trail_details_map)
	private ImageView mapImageView;
	// 车牌
	@ViewInject(id = R.id.pinche_trail_details_publisher_car_license)
	private TextView mCarLicenseText;
	// 验证标示
	@ViewInject(id = R.id.pinche_trail_details_info_verified)
	private ImageView mUserISVerified;

	// 性别
	@ViewInject(id = R.id.pinche_trail_details_publisher_car_license)
	private TextView mGenderText;

	// 起点
	@ViewInject(id = R.id.pinche_trail_details_start)
	private TextView mStarText;

	// 终点
	@ViewInject(id = R.id.pinche_trail_details_end)
	private TextView mEndText;

	@ViewInject(id = R.id.point_layout)
	private RelativeLayout pointLayout;

	// 两个途经点上面的线
	@ViewInject(id = R.id.line)
	private View line1;

	// 途径点1
	@ViewInject(id = R.id.point1)
	private TextView point1;

	// 两个途经点中间线
	@ViewInject(id = R.id.line1)
	private View line2;

	// 途经点2
	@ViewInject(id = R.id.point2)
	private TextView point2;
	// 驾龄
	@ViewInject(id = R.id.pinche_trail_details_publisher_driving_years)
	private TextView mDrivingYears;
	// 出发
	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.pinche_trail_details_start_time_layout)
	private TextView mStartDateTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.pinche_trail_details_start_time_layout)
	private TextView mStartDateText;

	// 返程
	@ViewInject(id = R.id.pinche_trail_details_back_time_layout)
	private View mBackDateLayout;

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.pinche_trail_details_back_time_layout)
	private TextView mBackDateTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.pinche_trail_details_back_time_layout)
	private TextView mBackDateText;

	// 费用

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.pinche_trail_details_cost_layout)
	private TextView mCostTitleText;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.pinche_trail_details_cost_layout)
	private TextView mCostText;

	// 可载人数
	@ViewInject(id = R.id.pinche_trail_details_people_num_layout)
	private View mPeopleNumLayout;

	@ViewInject(id = R.id.item_text_text_title, parentId = R.id.pinche_trail_details_people_num_layout)
	private TextView mPeopleNumTitle;

	@ViewInject(id = R.id.item_text_text_value, parentId = R.id.pinche_trail_details_people_num_layout)
	private TextView mPeopleNum;

	// 备注
	@ViewInject(id = R.id.pinche_trail_details_note)
	private EditText mNoteText;

	private int pinche_id;// 获取拼车信息的id
	private Pinche mPincheDetails;// 拼车信息对象

	private Util util = Util.getInstance();
	/**
	 * 身份 ： 乘客 /司机
	 */
	private String role = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewInjectUtil.inject(this);
		initView();
		loadPinche();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		final Bundle bundle = getIntent().getExtras();
		pinche_id = bundle.getInt("PincheId");
		this.mTitleBack.setOnClickListener(this);
		this.mPhotoView.setOnClickListener(this);
		this.mapImageView.setOnClickListener(this);
		// this.mappointment.setOnClickListener(this);
		this.mTitle.setText(getResources().getString(R.string.pick_trail_Title));// 初始化title
	}

	/**
	 * 设置拼车详情
	 */
	public void setPincheDetails() {
		final Car car = mPincheDetails.getCar();
		final User user = mPincheDetails.getUser();

		if (this.mPincheDetails != null) {
			this.mStarText.setText(mPincheDetails.getStart_name());
			this.mEndText.setText(mPincheDetails.getEnd_name());
			String urlString = util.getPhotoURL(user.getUserId());
			this.mPhotoView.displayWithUrl(urlString, false, false);
			mNameText.setText(user.getUsername());
			// 出发费用备注都有
			this.mStartDateTitleText.setText(getResources().getString(
					R.string.pick_details_start_date));
			this.mStartDateText.setText(mPincheDetails.getDepartureTime());
			mBackDateLayout.setVisibility(View.GONE);

			if (user != null && user.isUserStateVerify()) {
				this.mUserISVerified.setVisibility(View.VISIBLE);
			}
			if (mPincheDetails.getInfoType().equals(Pinche.DRIVER)) {
				// (看司机信息)
				role = Constant.DRIVER;
				this.mGenderText.setVisibility(View.GONE);
				this.mCarBrandText.setText(car.getCarType().getCarTypeName());
				mDrivingYears.setVisibility(View.VISIBLE);
				mCarLicenseText.setVisibility(View.VISIBLE);
				this.mDrivingYears.setText(user.getDriveAge());
				this.mCarLicenseText.setText(car.getLicence());
				// 上下班设置返回
				if (mPincheDetails.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
					this.mBackDateTitleText.setText(getResources().getString(
							R.string.pick_details_back_date));
					this.mBackDateText.setText(mPincheDetails.getBackTime());
				}
				this.mCostTitleText.setText(getResources().getString(
						R.string.pick_details_cost_note));
				this.mCostText.setText(mPincheDetails.getCharge() + "");
				this.mPeopleNumTitle.setText(getResources().getString(
						R.string.pick_details_total_people));
				this.mPeopleNum.setText(mPincheDetails.getNum() + "");
				// 途径点
				Point[] points = mPincheDetails.getPoints();
				if (null != points) {
					TextView[] pointsViews = { point1, point2 };
					for (int i = 0; i < points.length; i++) {
						pointLayout.setVisibility(View.VISIBLE);
						line1.setVisibility(View.VISIBLE);
						pointsViews[i].setVisibility(View.VISIBLE);
						pointsViews[i].setText(points.length == 1 ? "途经：" + points[i].getName()
								: "途径" + (i + 1) + ":" + points[i].getName());
						if (i == 1) {
							line2.setVisibility(View.VISIBLE);
						}
					}
				}
			} else {// （看乘客信息）
				role = Constant.PASSENGER;
				mPeopleNumLayout.setVisibility(View.GONE);
				mCarBrandText.setText(user.getGender(this));
				if (mPincheDetails.getCarpoolType().equals(Pinche.CARPOOLTYPE_ON_OFF_WORK)) {
					mBackDateLayout.setVisibility(View.VISIBLE);
					this.mBackDateTitleText.setText(getResources().getString(
							R.string.pick_details_back_date));
					this.mBackDateText.setText(mPincheDetails.getBackTime());
				}
				this.mCostTitleText.setText(getResources().getString(R.string.pick_details_cost));
				this.mCostText.setText(mPincheDetails.getCharge() + "");
				// this.mPeopleNumTitle.setText(getResources().getString(
				// R.string.pick_details_total_people));
				// this.mPeopleNum.setText(car.getNum());
			}
			String note = mPincheDetails.getNote();
			if (!TextUtils.isEmpty(note)) mNoteText.setText(note);
			if (util.getLoginUser(PincheTrailActivity.this) != null
					&& mPincheDetails.getUser_id() == util.getLoginUser(PincheTrailActivity.this)
							.getUserId()) {// 如果此信息是自己发的
				mappointment.setVisibility(View.GONE);
			} else {
				// 等待处理
				if (mPincheDetails.isBooking()) {// 已经预约
					mappointment.setFocusable(false);
					mappointment.setText("已经预约");
				} else {
					mappointment.setOnClickListener(this);
				}
			}
		}
	}

	/**
	 * 初始时加载拼车详情
	 */
	private void loadPinche() {
		final String mUrl = "infoId=" + pinche_id;// 请求拼车详情参数

		util.doGetRequest(PincheTrailActivity.this, new OnHttpRequestDataCallback() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				JsonResult<Pinche> jsonResult = util.getObjFromJsonResult(result,
						new TypeToken<JsonResult<Pinche>>() {
						});
				if (jsonResult != null && jsonResult.isSuccess()) {
					mPincheDetails = jsonResult.getData();
					setPincheDetails();// 设置界面初始值
				} else {
					Util.showToast(PincheTrailActivity.this, getString(R.string.request_error));
				}
			}
		}, Constant.URL_GET_INFO_BY_ID + mUrl, getString(R.string.lepin_dialog_loading), false);

	}

	@Override
	public void onClick(View view) {
		if (this.mTitleBack == view) {// 返回
			PincheTrailActivity.this.finish();
		} else if (this.mappointment == view) {// 立即预约
			if (util.isUserLoging(PincheTrailActivity.this)) {
				// 跳转到订单页面
				getBook();

			} else {// 没有登陆跳转到注册界面
				util.go2Activity(PincheTrailActivity.this, LoginActivity.class);
			}
		} else if (this.mPhotoView == view) {
			// 如果自己的界面是需要登录的，不是自己的无需登录
			// 自己界面
			// 先判断登陆
			if (util.isUserLoging(PincheTrailActivity.this)) {
				// 判断是否是自己的
				if (util.getLoginUser(PincheTrailActivity.this) != null
						&& mPincheDetails.getUser_id() == util.getLoginUser(
								PincheTrailActivity.this).getUserId()) {
					// 跳转到个人主页
					util.go2Activity(PincheTrailActivity.this, PersonalInfoActivity.class);
				} else {
					int userId = mPincheDetails.getUser().getUserId();
					Bundle dataBundle = new Bundle();
					dataBundle.putInt("userId", userId);
					dataBundle.putString("role", role);
					util.go2ActivityWithBundle(PincheTrailActivity.this,
							PersonalInfoTrailActivity.class, dataBundle);

				}
			} else {
				// 没有登陆跳转到注册界面
				Util.getInstance().go2Activity(PincheTrailActivity.this, LoginActivity.class);
			}

		} else if (view == mapImageView) {
			if (null == mPincheDetails.getStartLat() || null == mPincheDetails.getStartLon()
					|| null == mPincheDetails.getEndLat() || null == mPincheDetails.getEndLon()) {
				Util.showToast(PincheTrailActivity.this,
						getResources().getString(R.string.pinche_trail_no_location));
			} else {
				Util.getInstance().showStartAndEndOnMap(PincheTrailActivity.this, mPincheDetails);
			}
		}
	}

	/**
	 * 预约当前拼车
	 */
	private void getBook() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("infoId", String.valueOf(pinche_id)));

		util.doPostRequest(PincheTrailActivity.this, new OnHttpRequestDataCallback() {

			public void onSuccess(String result) {
				String bookId = doBooking(result);
				if (null != bookId) {
					Bundle bundle = new Bundle();
					bundle.putString(Constant.BOOK_ID, bookId);
					util.go2OrderDetail(PincheTrailActivity.this, bundle);
				}
			}
		}, params, Constant.URL_ADDBOOK, getString(R.string.my_book_ing) + "...", false);

		// util.doPostRequest(PincheTrailActivity.this, new
		// OnDataLoadingCallBack() {
		// @Override
		// public void onLoadingBack(String result) {
		// if (!TextUtils.isEmpty(result)) {
		// String bookId = doBooking(result);
		// if (null != bookId) {
		// Bundle bundle = new Bundle();
		// bundle.putString(Constant.BOOK_ID, bookId);
		// util.go2OrderDetail(PincheTrailActivity.this, bundle);
		// }
		// }
		// }
		//
		// }, params, Constant.URL_ADDBOOK, getString(R.string.my_book_ing) +
		// "...");
	}

	private String doBooking(String result) {
		JsonResult<String> jsonResult = util.getObjFromJsonResult(result,
				new TypeToken<JsonResult<String>>() {
				});
		String bookIdString = null;
		if (jsonResult != null) {
			if (jsonResult.isSuccess()) {
				// 初始化底部提示信息
				bookIdString = jsonResult.getData();
			} else {
				Util.showToast(PincheTrailActivity.this, jsonResult.getErrorMsg());
			}
		}
		return bookIdString;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
}
